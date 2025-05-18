package com.example.EVCharge.dto;

import com.example.EVCharge.models.StationStatus;

public class StationResponse {

    private Long id;
    private String locationName;
    private String address;
    private int powerKw;
    private String connectors;
    private String manufacturer;
    private double pricePerKwh;
    private StationStatus status;
    private double latitude;
    private double longitude;

    // Конструктор

    public StationResponse(Long id, String locationName, String address, int powerKw,
                           String connectors, String manufacturer, double pricePerKwh,
                           StationStatus status, double latitude, double longitude) {
        this.id = id;
        this.locationName = locationName;
        this.address = address;
        this.powerKw = powerKw;
        this.connectors = connectors;
        this.manufacturer = manufacturer;
        this.pricePerKwh = pricePerKwh;
        this.status = status;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Геттери

    public Long getId() {
        return id;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getAddress() {
        return address;
    }

    public int getPowerKw() {
        return powerKw;
    }

    public String getConnectors() {
        return connectors;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public double getPricePerKwh() {
        return pricePerKwh;
    }

    public StationStatus getStatus() {
        return status;
    }
    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }
}
