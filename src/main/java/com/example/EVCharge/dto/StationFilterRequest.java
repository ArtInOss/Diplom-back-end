package com.example.EVCharge.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Digits;
import java.util.List;

public class StationFilterRequest {

    private List<String> connectors;       // CCS2, CHAdeMO, GB/T
    private List<String> manufacturers;    // ND, UGV, ABB и т.д.

    @NotNull(message = "Потужність повинна бути вказана")
    @Min(value = 1, message = "Потужність повинна бути не менше 1 кВт")
    @Digits(integer = 4, fraction = 0, message = "Потужність повинна бути цілим числом")
    private Integer minPower;

    @NotNull(message = "Ціна повинна бути вказана")
    @DecimalMin(value = "0.1", message = "Ціна повинна бути більше 0")
    @Digits(integer = 4, fraction = 1, message = "Ціна повинна мати не більше 1 знаку після коми")
    private Double maxPricePerKwh;

    @NotNull(message = "Запас ходу повинен бути вказаний")
    @Min(value = 1, message = "Запас ходу повинен бути більше 0 км")
    @Digits(integer = 4, fraction = 0, message = "Запас ходу повинен бути цілим числом")
    private Integer rangeKm;

    private Double userLat;
    private Double userLng;



    // Геттеры и сеттеры
    public List<String> getConnectors() {
        return connectors;
    }

    public void setConnectors(List<String> connectors) {
        this.connectors = connectors;
    }

    public List<String> getManufacturers() {
        return manufacturers;
    }

    public void setManufacturers(List<String> manufacturers) {
        this.manufacturers = manufacturers;
    }

    public Integer getMinPower() {
        return minPower;
    }

    public void setMinPower(Integer minPower) {
        this.minPower = minPower;
    }

    public Double getMaxPricePerKwh() {
        return maxPricePerKwh;
    }

    public void setMaxPricePerKwh(Double  maxPricePerKwh) {
        this.maxPricePerKwh = maxPricePerKwh;
    }

    public Double getUserLat() {
        return userLat;
    }

    public void setUserLat(Double userLat) {
        this.userLat = userLat;
    }

    public Double getUserLng() {
        return userLng;
    }

    public void setUserLng(Double userLng) {
        this.userLng = userLng;
    }

    public Integer getRangeKm() {
        return rangeKm;
    }

    public void setRangeKm(Integer rangeKm) {
        this.rangeKm = rangeKm;
    }
}
