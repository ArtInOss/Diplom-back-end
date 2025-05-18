package com.example.EVCharge.service;

import com.example.EVCharge.models.Station;
import com.example.EVCharge.repository.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StationService {

    @Autowired
    private StationRepository stationRepository;

    public List<Station> getAllStations() {
        return stationRepository.findAll();
    }

    public Optional<Station> getStationById(Long id) {
        return stationRepository.findById(id);
    }

    public Station addStation(Station station) {
        // 🔒 Перевірка унікальності назви локації
        if (stationRepository.existsByLocationName(station.getLocationName())) {
            throw new RuntimeException("Станція з такою назвою вже існує!");
        }

        // ⚠️ Перевірка: connectors не може бути порожнім
        if (station.getConnectors() == null || station.getConnectors().trim().isEmpty()) {
            throw new RuntimeException("Потрібно вказати хоча б один конектор.");
        }

        // ⚠️ Перевірка: чи є хоча б один непорожній конектор у списку
        boolean hasValidConnector = List.of(station.getConnectors().split(","))
                .stream()
                .anyMatch(conn -> !conn.trim().isEmpty());

        if (!hasValidConnector) {
            throw new RuntimeException("Список конекторів не може бути повністю порожнім.");
        }

        return stationRepository.save(station);
    }

    public void deleteStation(Long id) {
        stationRepository.deleteById(id);
    }

    public Station updateStation(Long id, Station updatedStation) {
        return stationRepository.findById(id)
                .map(existingStation -> {
                    existingStation.setLocationName(updatedStation.getLocationName());
                    existingStation.setAddress(updatedStation.getAddress());
                    existingStation.setPowerKw(updatedStation.getPowerKw());
                    existingStation.setConnectors(updatedStation.getConnectors());
                    existingStation.setManufacturer(updatedStation.getManufacturer());
                    existingStation.setPricePerKwh(updatedStation.getPricePerKwh());
                    existingStation.setStatus(updatedStation.getStatus());
                    return stationRepository.save(existingStation);
                })
                .orElseThrow(() -> new RuntimeException("Станцію не знайдено"));
    }
}
