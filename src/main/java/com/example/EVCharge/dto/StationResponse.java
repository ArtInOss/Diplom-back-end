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
    private Double distanceKm;
    private Integer travelTimeSeconds; // 🆕 добавлено поле

    // ✅ Пустой конструктор
    public StationResponse() {}

    // ✅ Конструктор со всеми параметрами (travelTime опционально добавляется отдельно)
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

    // ✅ Геттеры
    public Long getId() { return id; }
    public String getLocationName() { return locationName; }
    public String getAddress() { return address; }
    public int getPowerKw() { return powerKw; }
    public String getConnectors() { return connectors; }
    public String getManufacturer() { return manufacturer; }
    public double getPricePerKwh() { return pricePerKwh; }
    public StationStatus getStatus() { return status; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public Double getDistanceKm() { return distanceKm; }
    public Integer getTravelTimeSeconds() { return travelTimeSeconds; }

    // ✅ Сеттеры
    public void setId(Long id) { this.id = id; }
    public void setLocationName(String locationName) { this.locationName = locationName; }
    public void setAddress(String address) { this.address = address; }
    public void setPowerKw(int powerKw) { this.powerKw = powerKw; }
    public void setConnectors(String connectors) { this.connectors = connectors; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
    public void setPricePerKwh(double pricePerKwh) { this.pricePerKwh = pricePerKwh; }
    public void setStatus(StationStatus status) { this.status = status; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }
    public void setTravelTimeSeconds(Integer travelTimeSeconds) { this.travelTimeSeconds = travelTimeSeconds; }
}
