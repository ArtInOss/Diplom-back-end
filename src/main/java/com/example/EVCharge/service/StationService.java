package com.example.EVCharge.service;

import com.example.EVCharge.dto.StationFilterRequest;
import com.example.EVCharge.dto.StationResponse;
import com.example.EVCharge.mapper.StationMapper;
import com.example.EVCharge.models.Station;
import com.example.EVCharge.repository.StationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StationService {

    private final StationRepository stationRepository;
    private final StationMapper stationMapper;
    private final StationUpdateBroadcaster updateBroadcaster;
    private final GoogleMapsService googleMapsService;
    private final StationRankingService stationRankingService;

    private static final Logger logger = LoggerFactory.getLogger(StationService.class);
    private static final Set<String> ALLOWED_CONNECTORS = Set.of("CCS2", "CHADEMO", "GB/T");

    public StationService(StationRepository stationRepository,
                          StationMapper stationMapper,
                          StationUpdateBroadcaster updateBroadcaster,
                          GoogleMapsService googleMapsService,
                          StationRankingService stationRankingService) {
        this.stationRepository = stationRepository;
        this.stationMapper = stationMapper;
        this.updateBroadcaster = updateBroadcaster;
        this.googleMapsService = googleMapsService;
        this.stationRankingService = stationRankingService;
    }

    public List<Station> getAllStations() {
        return stationRepository.findAll();
    }

    public Optional<Station> getStationById(Long id) {
        return stationRepository.findById(id);
    }

    public Station addStation(Station station) {
        validateNewStation(station);
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
                .map(existing -> {
                    validateUpdate(existing, updatedStation);
                    updateFields(existing, updatedStation);
                    return stationRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Станцію не знайдено"));

        updateBroadcaster.broadcastUpdate();
        return result;
    }

    public Map<String, List<StationResponse>> filterStationsWithTop(StationFilterRequest filter) {
        List<Station> filtered = filterByAttributes(filter);
        List<Station> reachable = filterByRange(filtered, filter);
        List<StationResponse> allFilteredResponses = mapToResponses(reachable);

        List<StationResponse> top10 = stationRankingService.rankTop10ByVSM(reachable, filter.getUserLat(), filter.getUserLng());
        List<ScoredStation> enriched = googleMapsService.enrichWithTravelTime(top10, filter.getUserLat(), filter.getUserLng(), filter.getRangeKm());
        List<StationResponse> top5 = stationRankingService.rankTop5ByVSMWithTravelTime(enriched);

        logger.info("📌 Всього після фільтра: {}, у межах запасу ходу: {}, у фінальному топі: {}",
                filtered.size(), reachable.size(), top5.size());

        Map<String, List<StationResponse>> result = new HashMap<>();
        result.put("allStations", allFilteredResponses);
        result.put("topStations", top5);
        return result;
    }

    public List<StationResponse> getAllStationResponses() {
        return stationRepository.findAll()
                .stream()
                .map(stationMapper::toResponse)
                .collect(Collectors.toList());
    }

    // ========== PRIVATE METHODS ==========

    private List<Station> filterByAttributes(StationFilterRequest filter) {
        return stationRepository.findAll().stream()
                .filter(station -> {
                    if (filter.getConnectors() != null && !filter.getConnectors().isEmpty()) {
                        boolean matches = filter.getConnectors().stream()
                                .anyMatch(conn -> station.getConnectors().toUpperCase().contains(conn.toUpperCase()));
                        if (!matches) return false;
                    }
                    if (filter.getManufacturers() != null && !filter.getManufacturers().isEmpty()
                            && !filter.getManufacturers().contains(station.getManufacturer())) return false;
                    if (filter.getMinPower() != null && station.getPowerKw() < filter.getMinPower()) return false;
                    if (filter.getMaxPricePerKwh() != null && station.getPricePerKwh() > filter.getMaxPricePerKwh()) return false;
                    return true;
                }).collect(Collectors.toList());
    }

    private List<Station> filterByRange(List<Station> stations, StationFilterRequest filter) {
        if (filter.getUserLat() == null || filter.getUserLng() == null || filter.getRangeKm() == null) {
            return stations;
        }

        double correctionFactor = 1.2;
        return stations.stream()
                .filter(station -> {
                    double distance = DistanceCalculator.calculateDistanceKm(
                            filter.getUserLat(), filter.getUserLng(),
                            station.getLatitude(), station.getLongitude());
                    return distance * correctionFactor <= filter.getRangeKm();
                }).collect(Collectors.toList());
    }

    private List<StationResponse> mapToResponses(List<Station> stations) {
        return stations.stream()
                .map(stationMapper::toResponse)
                .collect(Collectors.toList());
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

    private void validateNewStation(Station station) {
        if (stationRepository.existsByLocationName(station.getLocationName())) {
            throw new RuntimeException("Станція з такою назвою вже існує!");
        }
        if (stationRepository.existsByLatitudeAndLongitude(station.getLatitude(), station.getLongitude())) {
            throw new RuntimeException("Станція з такими координатами вже існує!");
        }
        validateConnectors(station.getConnectors());
    }

    private void validateUpdate(Station existing, Station updated) {
        if (!existing.getLocationName().equalsIgnoreCase(updated.getLocationName()) &&
                stationRepository.existsByLocationName(updated.getLocationName())) {
            throw new RuntimeException("Станція з такою назвою вже існує!");
        }

        if ((updated.getLatitude() != existing.getLatitude() ||
                updated.getLongitude() != existing.getLongitude()) &&
                stationRepository.existsByLatitudeAndLongitude(updated.getLatitude(), updated.getLongitude())) {
            throw new RuntimeException("Станція з такими координатами вже існує!");
        }

        validateConnectors(updated.getConnectors());
    }

    private void updateFields(Station existing, Station updated) {
        existing.setLocationName(updated.getLocationName());
        existing.setAddress(updated.getAddress());
        existing.setPowerKw(updated.getPowerKw());
        existing.setConnectors(updated.getConnectors());
        existing.setManufacturer(updated.getManufacturer());
        existing.setPricePerKwh(updated.getPricePerKwh());
        existing.setStatus(updated.getStatus());
        existing.setLatitude(updated.getLatitude());
        existing.setLongitude(updated.getLongitude());
    }
}
