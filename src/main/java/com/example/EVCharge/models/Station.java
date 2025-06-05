package com.example.EVCharge.models;

import jakarta.persistence.*;

@Entity
@Table(name = "stations")
public class Station {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String locationName;
    private String address;
    private int powerKw;
    private String connectors;
    private String manufacturer;
    private double pricePerKwh;

    @Enumerated(EnumType.STRING)
    private StationStatus status;

    private double latitude;
    private double longitude;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPowerKw() {
        return powerKw;
    }

    public void setPowerKw(int powerKw) {
        this.powerKw = powerKw;
    }

    public String getConnectors() {
        return connectors;
    }

    public void setConnectors(String connectors) {
        this.connectors = connectors;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public double getPricePerKwh() {
        return pricePerKwh;
    }

    public void setPricePerKwh(double pricePerKwh) {
        this.pricePerKwh = pricePerKwh;
    }

    public StationStatus getStatus() {
        return status;
    }

    public void setStatus(StationStatus status) {
        this.status = status;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
