package com.example.EVCharge.service;

import com.example.EVCharge.dto.StationFilterRequest;
import com.example.EVCharge.dto.StationResponse;
import com.example.EVCharge.models.Station;
import com.example.EVCharge.repository.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StationService {

    @Autowired
    private StationRepository stationRepository;

    // ✅ Допустимі типи конекторів
    private static final Set<String> ALLOWED_CONNECTORS = Set.of("CCS2", "CHADEMO", "GB/T");

    public List<Station> getAllStations() {
        return stationRepository.findAll();
    }

    public Optional<Station> getStationById(Long id) {
        return stationRepository.findById(id);
    }

    public Station addStation(Station station) {
        if (stationRepository.existsByLocationName(station.getLocationName())) {
            throw new RuntimeException("Станція з такою назвою вже існує!");
        }

        if (station.getConnectors() == null || station.getConnectors().trim().isEmpty()) {
            throw new RuntimeException("Потрібно вказати хоча б один конектор.");
        }

        validateConnectors(station.getConnectors());

        if (stationRepository.existsByLatitudeAndLongitude(station.getLatitude(), station.getLongitude())) {
            throw new RuntimeException("Станція з такими координатами вже існує!");
        }

        return stationRepository.save(station);
    }

    public void deleteStation(Long id) {
        stationRepository.deleteById(id);
    }

    public Station updateStation(Long id, Station updatedStation) {
        return stationRepository.findById(id)
                .map(existingStation -> {
                    if (!updatedStation.getLocationName().equalsIgnoreCase(existingStation.getLocationName()) &&
                            stationRepository.existsByLocationName(updatedStation.getLocationName())) {
                        throw new RuntimeException("Станція з такою назвою вже існує!");
                    }

                    if ((updatedStation.getLatitude() != existingStation.getLatitude() ||
                            updatedStation.getLongitude() != existingStation.getLongitude()) &&
                            stationRepository.existsByLatitudeAndLongitude(updatedStation.getLatitude(), updatedStation.getLongitude())) {
                        throw new RuntimeException("Станція з такими координатами вже існує!");
                    }

                    // ✅ Валідація конекторів
                    validateConnectors(updatedStation.getConnectors());

                    existingStation.setLocationName(updatedStation.getLocationName());
                    existingStation.setAddress(updatedStation.getAddress());
                    existingStation.setPowerKw(updatedStation.getPowerKw());
                    existingStation.setConnectors(updatedStation.getConnectors());
                    existingStation.setManufacturer(updatedStation.getManufacturer());
                    existingStation.setPricePerKwh(updatedStation.getPricePerKwh());
                    existingStation.setStatus(updatedStation.getStatus());
                    existingStation.setLatitude(updatedStation.getLatitude());
                    existingStation.setLongitude(updatedStation.getLongitude());

                    return stationRepository.save(existingStation);
                })
                .orElseThrow(() -> new RuntimeException("Станцію не знайдено"));
    }

    // 🔍 Перевірка валідності всіх конекторів
    private void validateConnectors(String connectors) {
        List<String> list = Arrays.stream(connectors.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        if (list.isEmpty()) {
            throw new RuntimeException("Потрібно вказати хоча б один валідний конектор.");
        }

        for (String connector : list) {
            String upper = connector.toUpperCase();
            if (!ALLOWED_CONNECTORS.contains(upper)) {
                throw new RuntimeException("Недопустимий тип конектора: " + connector);
            }
        }
    }

    // ✅ Новый метод: фильтрация по фильтрам с формы
    public List<StationResponse> filterStations(StationFilterRequest filter) {
        List<Station> filtered = stationRepository.findAll().stream()
                .filter(station -> {
                    // фильтрация по коннекторам
                    if (filter.getConnectors() != null && !filter.getConnectors().isEmpty()) {
                        boolean matches = filter.getConnectors().stream()
                                .anyMatch(conn -> station.getConnectors().toUpperCase().contains(conn.toUpperCase()));
                        if (!matches) return false;
                    }

                    // фильтрация по производителям
                    if (filter.getManufacturers() != null && !filter.getManufacturers().isEmpty()) {
                        if (!filter.getManufacturers().contains(station.getManufacturer())) return false;
                    }

                    // фильтрация по мощности
                    if (filter.getMinPower() != null && station.getPowerKw() < filter.getMinPower()) return false;

                    // фильтрация по цене
                    if (filter.getMaxPricePerKwh() != null && station.getPricePerKwh() > filter.getMaxPricePerKwh()) return false;

                    return true;
                })
                .collect(Collectors.toList());

        // Фильтрация по запасу ходу с учётом расстояния
        if (filter.getUserLat() != null && filter.getUserLng() != null && filter.getRangeKm() != null) {
            final double lat = filter.getUserLat();
            final double lng = filter.getUserLng();
            final int rangeKm = filter.getRangeKm();
            final double correctionFactor = 1.2; // коэффициент погрешности маршрута

            filtered = filtered.stream()
                    .filter(station -> {
                        double distance = calculateDistanceKm(lat, lng, station.getLatitude(), station.getLongitude());
                        return distance * correctionFactor <= rangeKm;
                    })
                    .collect(Collectors.toList());
        }

        return filtered.stream()
                .map(this::mapToStationResponse)
                .collect(Collectors.toList());
    }

    private double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS_KM = 6371;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double rLat1 = Math.toRadians(lat1);
        double rLat2 = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(rLat1) * Math.cos(rLat2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }


    // ✅ Преобразование Station → StationResponse
    private StationResponse mapToStationResponse(Station station) {
        StationResponse response = new StationResponse();
        response.setId(station.getId());
        response.setLocationName(station.getLocationName());
        response.setLatitude(station.getLatitude());
        response.setLongitude(station.getLongitude());
        response.setConnectors(station.getConnectors());
        response.setPowerKw(station.getPowerKw());
        response.setPricePerKwh(station.getPricePerKwh());
        response.setManufacturer(station.getManufacturer());
        response.setStatus(station.getStatus());
        response.setAddress(station.getAddress());
        return response;
    }

}
