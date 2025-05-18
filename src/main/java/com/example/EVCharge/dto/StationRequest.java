package com.example.EVCharge.dto;

import com.example.EVCharge.models.StationStatus;
import jakarta.validation.constraints.*;

public class StationRequest {

    @NotBlank(message = "Назва локації обов'язкова")
    private String locationName;

    @NotBlank(message = "Адреса обов'язкова")
    private String address;

    @Min(value = 1, message = "Потужність повинна бути більше 0")
    private int powerKw;

    @NotBlank(message = "Список конекторів обов'язковий")
    private String connectors; // CSV string

    @NotBlank(message = "Виробник обов'язковий")
    private String manufacturer;

    @DecimalMin(value = "0.01", message = "Тариф має бути більший за 0")
    private double pricePerKwh;

    @NotNull(message = "Статус обов'язковий")
    private StationStatus status;
    @DecimalMin(value = "-90.0", message = "Широта має бути в межах -90 до 90")
    @DecimalMax(value = "90.0", message = "Широта має бути в межах -90 до 90")
    private double latitude;

    @DecimalMin(value = "-180.0", message = "Довгота має бути в межах -180 до 180")
    @DecimalMax(value = "180.0", message = "Довгота має бути в межах -180 до 180")
    private double longitude;

    // геттери + сеттери

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
