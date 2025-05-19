package com.example.EVCharge.controllers;

import com.example.EVCharge.dto.StationRequest;
import com.example.EVCharge.dto.StationResponse;
import com.example.EVCharge.models.Station;
import com.example.EVCharge.service.StationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stations")
public class StationController {

    @Autowired
    private StationService stationService;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping
    public ResponseEntity<List<StationResponse>> getAllStations() {
        List<StationResponse> stations = stationService.getAllStations()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(stations);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getStationById(@PathVariable Long id) {
        return stationService.getStationById(id)
                .map(station -> ResponseEntity.ok(toResponse(station)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> addStation(@Valid @RequestBody StationRequest request, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = result.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            FieldError::getField,
                            FieldError::getDefaultMessage,
                            (e1, e2) -> e1 // если дубликаты полей — берём первый
                    ));
            return ResponseEntity.badRequest().body(Map.of("validationErrors", errors));
        }

        try {
            Station station = new Station();
            applyRequestToStation(station, request);
            Station saved = stationService.addStation(station);
            return ResponseEntity.ok(toResponse(saved));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStation(@PathVariable Long id, @Valid @RequestBody StationRequest request, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = result.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            FieldError::getField,
                            FieldError::getDefaultMessage,
                            (e1, e2) -> e1
                    ));
            return ResponseEntity.badRequest().body(Map.of("validationErrors", errors));
        }

        try {
            Station updated = stationService.updateStation(id, toEntity(request));
            return ResponseEntity.ok(toResponse(updated));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStation(@PathVariable Long id) {
        stationService.deleteStation(id);
        return ResponseEntity.ok().build();
    }

    // 🔄 Mapping: Request DTO → Entity
    private Station toEntity(StationRequest request) {
        Station station = new Station();
        applyRequestToStation(station, request);
        return station;
    }

    // 🔄 Mapping: Entity → Response DTO
    private StationResponse toResponse(Station station) {
        return new StationResponse(
                station.getId(),
                station.getLocationName(),
                station.getAddress(),
                station.getPowerKw(),
                station.getConnectors(),
                station.getManufacturer(),
                station.getPricePerKwh(),
                station.getStatus(),
                station.getLatitude(),
                station.getLongitude()
        );
    }

    // 🔁 Записати DTO в сутність
    private void applyRequestToStation(Station station, StationRequest request) {
        station.setLocationName(request.getLocationName());
        station.setAddress(request.getAddress());
        station.setPowerKw(request.getPowerKw());
        station.setConnectors(request.getConnectors());
        station.setManufacturer(request.getManufacturer());
        station.setPricePerKwh(request.getPricePerKwh());
        station.setStatus(request.getStatus());
        station.setLatitude(request.getLatitude());
        station.setLongitude(request.getLongitude());
    }
}
