package com.example.EVCharge.service;

import com.example.EVCharge.models.Station;
import com.example.EVCharge.repository.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
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
        List<String> list = List.of(connectors.split(",")).stream()
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
}
