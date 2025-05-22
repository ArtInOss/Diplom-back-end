package com.example.EVCharge.controllers;

import com.example.EVCharge.dto.StationFilterRequest;
import com.example.EVCharge.dto.StationRequest;
import com.example.EVCharge.dto.StationResponse;
import com.example.EVCharge.mapper.StationMapper;
import com.example.EVCharge.models.Station;
import com.example.EVCharge.service.StationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stations")
public class StationController {

    @Autowired
    private StationService stationService;

    @Autowired
    private StationMapper stationMapper;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping
    public ResponseEntity<List<StationResponse>> getAllStations() {
        List<StationResponse> stations = stationService.getAllStationResponses();
        return ResponseEntity.ok(stations);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getStationById(@PathVariable Long id) {
        return stationService.getStationById(id)
                .map(stationMapper::toResponse)
                .map(ResponseEntity::ok)
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
                            (e1, e2) -> e1
                    ));
            return ResponseEntity.badRequest().body(Map.of("validationErrors", errors));
        }

        try {
            Station station = stationMapper.toEntity(request);
            Station saved = stationService.addStation(station);
            return ResponseEntity.ok(stationMapper.toResponse(saved));
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
            Station updated = stationService.updateStation(id, stationMapper.toEntity(request));
            return ResponseEntity.ok(stationMapper.toResponse(updated));
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

    @PostMapping("/filter")
    public ResponseEntity<?> filterStations(@Valid @RequestBody StationFilterRequest filterRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(err -> {
                errors.put(err.getField(), err.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body(errors);
        }

        Map<String, List<StationResponse>> result = stationService.filterStationsWithTop(filterRequest);
        return ResponseEntity.ok(result);
    }
}
