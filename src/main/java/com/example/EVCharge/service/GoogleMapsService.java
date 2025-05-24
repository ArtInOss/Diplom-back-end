package com.example.EVCharge.service;

import com.example.EVCharge.dto.StationResponse;
import com.example.EVCharge.models.Station;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoogleMapsService {

    private static final Logger logger = LoggerFactory.getLogger(GoogleMapsService.class);

    @Value("${google.api.key}")
    private String googleApiKey;

    public List<ScoredStation> enrichWithTravelTime(List<StationResponse> top10, double userLat, double userLng, Integer rangeKm) {
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

                int distanceMeters = el.get("distance").get("value").asInt();
                double distanceKm = distanceMeters / 1000.0;
                int durationSec = el.get("duration_in_traffic").get("value").asInt();

                if (rangeKm != null && distanceKm > rangeKm) continue;

                Station station = new Station();
                station.setLocationName(r.getLocationName());
                station.setLatitude(r.getLatitude());
                station.setLongitude(r.getLongitude());
                station.setPowerKw(r.getPowerKw());
                station.setPricePerKwh(r.getPricePerKwh());
                station.setManufacturer(r.getManufacturer());
                station.setAddress(r.getAddress());
                station.setConnectors(r.getConnectors());

                scoredStations.add(new ScoredStation(station, distanceKm, durationSec));
            }

            logger.info("📌 Станцій після Google API і запасу ходу: {}", scoredStations.size());
            return scoredStations;

        } catch (Exception e) {
            throw new RuntimeException("Помилка при запиті до Google API: " + e.getMessage(), e);
        }
    }
}
