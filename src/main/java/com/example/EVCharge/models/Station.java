package com.example.EVCharge.models;

import jakarta.persistence.*;

@Entity
@Table(name = "stations")
public class Station {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "location_name", nullable = false)
    private String locationName;

    @Column(nullable = false)
    private String address;

    @Column(name = "power_kw", nullable = false)
    private int powerKw;

    @Column(nullable = false)
    private String connectors; // CSV string like "CCS2,CHAdeMO"

    @Column(nullable = false)
    private String manufacturer;

    @Column(name = "price_per_kwh", nullable = false)
    private double pricePerKwh;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StationStatus status;
    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    // Геттери і сеттери
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
    // + геттери та сеттери
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
