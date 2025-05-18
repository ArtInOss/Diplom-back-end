package com.example.EVCharge.repository;

import com.example.EVCharge.models.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StationRepository extends JpaRepository<Station, Long> {
    // Додаткові методи при потребі, наприклад:
    // List<Station> findByStatus(StationStatus status);
    boolean existsByLocationName(String locationName);
}
