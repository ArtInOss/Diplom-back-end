package com.example.EVCharge.service;

import com.example.EVCharge.dto.StationFilterRequest;
import com.example.EVCharge.dto.StationResponse;
import com.example.EVCharge.mapper.StationMapper;
import com.example.EVCharge.models.Station;
import com.example.EVCharge.repository.StationRepository;
import com.example.EVCharge.service.StationUpdateBroadcaster;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;





@Service
public class StationService {
    @Autowired
    private StationMapper stationMapper;
    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private StationUpdateBroadcaster updateBroadcaster;

    @Value("${google.api.key}")
    private String googleApiKey;
    private static final Logger logger = LoggerFactory.getLogger(StationService.class);
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
        Station saved = stationRepository.save(station);
        updateBroadcaster.broadcastUpdate();
        return saved;
    }

    public void deleteStation(Long id) {
        stationRepository.deleteById(id);
        updateBroadcaster.broadcastUpdate();

    }

    public Station updateStation(Long id, Station updatedStation) {
        Station result = stationRepository.findById(id)
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

        updateBroadcaster.broadcastUpdate();
        return result;
    }

    private void validateConnectors(String connectors) {
        List<String> list = Arrays.stream(connectors.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        if (list.isEmpty()) {
            throw new RuntimeException("Потрібно вказати хоча б один валідний конектор.");
        }
        for (String connector : list) {
            if (!ALLOWED_CONNECTORS.contains(connector.toUpperCase())) {
                throw new RuntimeException("Недопустимий тип конектора: " + connector);
            }
        }
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



    private static class ScoredStation {
        Station station;
        double distanceKm;
        Double travelTimeSeconds;
        double score;

        public ScoredStation(Station station, double distanceKm) {
            this.station = station;
            this.distanceKm = distanceKm;
        }

        public ScoredStation(Station station, double distanceKm, double travelTimeSeconds) {
            this.station = station;
            this.distanceKm = distanceKm;
            this.travelTimeSeconds = travelTimeSeconds;
        }
    }

    public Map<String, List<StationResponse>> filterStationsWithTop(StationFilterRequest filter) {
        List<Station> filtered = filterByAttributes(filter);
        List<Station> reachable = filterByRange(filtered, filter);
        List<StationResponse> allFilteredResponses = buildAllResponses(reachable);
        List<StationResponse> topStations = runTopStationSelection(reachable, filter);

        Map<String, List<StationResponse>> result = new HashMap<>();
        result.put("allStations", allFilteredResponses);
        result.put("topStations", topStations);
        return result;
    }
    private List<Station> filterByAttributes(StationFilterRequest filter) {
        return stationRepository.findAll().stream()
                .filter(station -> {
                    if (filter.getConnectors() != null && !filter.getConnectors().isEmpty()) {
                        boolean matches = filter.getConnectors().stream()
                                .anyMatch(conn -> station.getConnectors().toUpperCase().contains(conn.toUpperCase()));
                        if (!matches) return false;
                    }
                    if (filter.getManufacturers() != null && !filter.getManufacturers().isEmpty()) {
                        if (!filter.getManufacturers().contains(station.getManufacturer())) return false;
                    }
                    if (filter.getMinPower() != null && station.getPowerKw() < filter.getMinPower()) return false;
                    if (filter.getMaxPricePerKwh() != null && station.getPricePerKwh() > filter.getMaxPricePerKwh()) return false;
                    return true;
                })
                .collect(Collectors.toList());
    }
    private List<Station> filterByRange(List<Station> stations, StationFilterRequest filter) {
        if (filter.getUserLat() == null || filter.getUserLng() == null || filter.getRangeKm() == null) {
            return stations;
        }
        double lat = filter.getUserLat();
        double lng = filter.getUserLng();
        int rangeKm = filter.getRangeKm();
        double correctionFactor = 1.2;

        return stations.stream()
                .filter(station -> {
                    double distance = calculateDistanceKm(lat, lng, station.getLatitude(), station.getLongitude());
                    return distance * correctionFactor <= rangeKm;
                })
                .collect(Collectors.toList());
    }
    private List<StationResponse> buildAllResponses(List<Station> reachable) {
        return reachable.stream()
                .map(stationMapper::toResponse)
                .collect(Collectors.toList());
    }
    private List<StationResponse> runTopStationSelection(List<Station> reachable, StationFilterRequest filter) {
        if (filter.getUserLat() == null || filter.getUserLng() == null || filter.getRangeKm() == null) {
            return new ArrayList<>();
        }

        List<StationResponse> top10 = findTopStationsByVSM(reachable, filter.getUserLat(), filter.getUserLng());
        if (top10.isEmpty()) return new ArrayList<>();

        return findTopStationsByVSMWithTravelTime(top10, filter.getUserLat(), filter.getUserLng(), filter.getRangeKm());
    }


    public List<StationResponse> findTopStationsByVSM(List<Station> stations, double userLat, double userLng) {
        List<ScoredStation> scoredList = new ArrayList<>();
        for (Station station : stations) {
            double distance = calculateDistanceKm(userLat, userLng, station.getLatitude(), station.getLongitude());
            scoredList.add(new ScoredStation(station, distance));
        }
        double maxPower = scoredList.stream().mapToDouble(s -> s.station.getPowerKw()).max().orElse(1);
        double minPower = scoredList.stream().mapToDouble(s -> s.station.getPowerKw()).min().orElse(0);
        double maxPrice = scoredList.stream().mapToDouble(s -> s.station.getPricePerKwh()).max().orElse(1);
        double minPrice = scoredList.stream().mapToDouble(s -> s.station.getPricePerKwh()).min().orElse(0);
        double maxDist = scoredList.stream().mapToDouble(s -> s.distanceKm).max().orElse(1);
        double minDist = scoredList.stream().mapToDouble(s -> s.distanceKm).min().orElse(0);

        double wPower = 0.33, wPrice = 0.33, wDistance = 0.34;

        for (ScoredStation s : scoredList) {
            double normPower = (s.station.getPowerKw() - minPower) / (maxPower - minPower + 0.001);
            double normPrice = (maxPrice - s.station.getPricePerKwh()) / (maxPrice - minPrice + 0.001);
            double normDist = (maxDist - s.distanceKm) / (maxDist - minDist + 0.001);
            s.score = wPower * normPower + wPrice * normPrice + wDistance * normDist;
        }

        return scoredList.stream()
                .sorted((a, b) -> Double.compare(b.score, a.score))
                .limit(10)
                .map(s -> {
                    StationResponse r = stationMapper.toResponse(s.station);
                    r.setDistanceKm(s.distanceKm);
                    return r;
                }).collect(Collectors.toList());
    }

    public List<StationResponse> findTopStationsByVSMWithTravelTime(List<StationResponse> top10, double userLat, double userLng, Integer rangeKm) {
        try {
            String destinations = top10.stream()
                    .map(s -> s.getLatitude() + "," + s.getLongitude())
                    .collect(Collectors.joining("|"));
            String origin = userLat + "," + userLng;

            String urlStr = "https://maps.googleapis.com/maps/api/distancematrix/json" +
                    "?origins=" + origin +
                    "&destinations=" + URLEncoder.encode(destinations, StandardCharsets.UTF_8) +
                    "&departure_time=now" +
                    "&key=" + googleApiKey;

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(conn.getInputStream());

            JsonNode elements = root.get("rows").get(0).get("elements");

            List<ScoredStation> scoredStations = new ArrayList<>();

            for (int i = 0; i < top10.size(); i++) {
                StationResponse r = top10.get(i);
                JsonNode el = elements.get(i);
                if (el == null || el.get("status") == null || !"OK".equals(el.get("status").asText()) ||
                        el.get("distance") == null || el.get("duration_in_traffic") == null) {
                    logger.warn("❌ Google API не вернул расстояние или время в пути для станции: {}", r.getLocationName());
                    continue;
                }

                Station station = new Station();
                station.setLocationName(r.getLocationName());
                station.setLatitude(r.getLatitude());
                station.setLongitude(r.getLongitude());
                station.setPowerKw(r.getPowerKw());
                station.setPricePerKwh(r.getPricePerKwh());
                station.setManufacturer(r.getManufacturer());
                station.setAddress(r.getAddress());
                station.setConnectors(r.getConnectors());

                int distanceMeters = el.get("distance").get("value").asInt();
                double distanceKm = distanceMeters / 1000.0;
                int durationSec = el.get("duration_in_traffic").get("value").asInt();

                // ❗ Исключаем станцию, если превышает запас хода по маршруту
                if (rangeKm != null && distanceKm > rangeKm) continue;

                scoredStations.add(new ScoredStation(station, distanceKm, durationSec));
            }

            List<Station> top5 = runFinalVSM(scoredStations);

            return scoredStations.stream()
                    .filter(s -> top5.contains(s.station))
                    .sorted(Comparator.comparingInt(s -> top5.indexOf(s.station)))
                    .map(s -> {
                        StationResponse r = stationMapper.toResponse(s.station);
                        r.setDistanceKm(s.distanceKm);
                        r.setTravelTimeSeconds(s.travelTimeSeconds != null ? s.travelTimeSeconds.intValue() : null);
                        return r;
                    })
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("Помилка при запиті до Google API: " + e.getMessage(), e);
        }
    }

    private List<Station> runFinalVSM(List<ScoredStation> scoredStations) {
        if (scoredStations == null || scoredStations.isEmpty()) return Collections.emptyList();

        double minPower = scoredStations.stream().mapToDouble(s -> s.station.getPowerKw()).min().orElse(1);
        double maxPower = scoredStations.stream().mapToDouble(s -> s.station.getPowerKw()).max().orElse(1);
        double minPrice = scoredStations.stream().mapToDouble(s -> s.station.getPricePerKwh()).min().orElse(1);
        double maxPrice = scoredStations.stream().mapToDouble(s -> s.station.getPricePerKwh()).max().orElse(1);
        double minDistance = scoredStations.stream().mapToDouble(s -> s.distanceKm).min().orElse(1);
        double maxDistance = scoredStations.stream().mapToDouble(s -> s.distanceKm).max().orElse(1);
        double minTime = scoredStations.stream().mapToDouble(s -> s.travelTimeSeconds != null ? s.travelTimeSeconds : 0).min().orElse(1);
        double maxTime = scoredStations.stream().mapToDouble(s -> s.travelTimeSeconds != null ? s.travelTimeSeconds : 0).max().orElse(1);

        double wPower = 0.2, wPrice = 0.2, wDistance = 0.2, wTime = 0.4;

        for (ScoredStation s : scoredStations) {
            double normPower = (s.station.getPowerKw() - minPower) / (maxPower - minPower + 1e-6);
            double normPrice = 1 - ((s.station.getPricePerKwh() - minPrice) / (maxPrice - minPrice + 1e-6));
            double normDistance = 1 - ((s.distanceKm - minDistance) / (maxDistance - minDistance + 1e-6));
            double normTime = 1 - (((s.travelTimeSeconds != null ? s.travelTimeSeconds : maxTime) - minTime) / (maxTime - minTime + 1e-6));
            s.score = wPower * normPower + wPrice * normPrice + wDistance * normDistance + wTime * normTime;
        }

        return scoredStations.stream()
                .sorted(Comparator.comparingDouble(s -> -s.score))
                .limit(5)
                .map(s -> s.station)
                .collect(Collectors.toList());
    }
    public List<StationResponse> getAllStationResponses() {
        return stationRepository.findAll()
                .stream()
                .map(stationMapper::toResponse)
                .collect(Collectors.toList());
    }

    }

