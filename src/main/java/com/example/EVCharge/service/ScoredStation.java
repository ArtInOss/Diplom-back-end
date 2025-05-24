

package com.example.EVCharge.service;

import com.example.EVCharge.models.Station;

public class ScoredStation {
    public Station station;
    public double distanceKm;
    public Double travelTimeSeconds; // может быть null
    public double score;

    public ScoredStation(Station station, double distanceKm) {
        this.station = station;
        this.distanceKm = distanceKm;
    }

    public ScoredStation(Station station, double distanceKm, double travelTimeSeconds) {
        this.station = station;
        this.distanceKm = distanceKm;
        this.travelTimeSeconds = travelTimeSeconds;
    }
}
