package com.example.EVCharge.mapper;

import com.example.EVCharge.dto.StationRequest;
import com.example.EVCharge.dto.StationResponse;
import com.example.EVCharge.models.Station;
import org.springframework.stereotype.Component;

@Component
public class StationMapper {

    public StationResponse toResponse(Station station) {
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

    public Station toEntity(StationRequest request) {
        Station station = new Station();
        station.setLocationName(request.getLocationName());
        station.setAddress(request.getAddress());
        station.setPowerKw(request.getPowerKw());
        station.setConnectors(request.getConnectors());
        station.setManufacturer(request.getManufacturer());
        station.setPricePerKwh(request.getPricePerKwh());
        station.setStatus(request.getStatus());
        station.setLatitude(request.getLatitude());
        station.setLongitude(request.getLongitude());
        return station;
    }
}