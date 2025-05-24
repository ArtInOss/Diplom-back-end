package com.example.EVCharge.service;

import com.example.EVCharge.dto.StationResponse;
import com.example.EVCharge.mapper.StationMapper;
import com.example.EVCharge.models.Station;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StationRankingService {

    private final StationMapper stationMapper;

    public StationRankingService(StationMapper stationMapper) {
        this.stationMapper = stationMapper;
    }

    public List<StationResponse> rankTop10ByVSM(List<Station> stations, double userLat, double userLng) {
        List<ScoredStation> scoredList = new ArrayList<>();
        for (Station station : stations) {
            double distance = DistanceCalculator.calculateDistanceKm(userLat, userLng, station.getLatitude(), station.getLongitude());
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
                .sorted(Comparator.comparingDouble((ScoredStation s) -> s.score).reversed())
                .limit(10)
                .map(s -> {
                    StationResponse r = stationMapper.toResponse(s.station);
                    r.setDistanceKm(s.distanceKm);
                    return r;
                })
                .collect(Collectors.toList());
    }

    public List<StationResponse> rankTop5ByVSMWithTravelTime(List<ScoredStation> scoredStations) {
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
                .map(s -> {
                    StationResponse r = stationMapper.toResponse(s.station);
                    r.setDistanceKm(s.distanceKm);
                    r.setTravelTimeSeconds(s.travelTimeSeconds != null ? s.travelTimeSeconds.intValue() : null);
                    return r;
                })
                .collect(Collectors.toList());
    }
}

