package com.example.application.repositories;

import com.example.application.data.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;

public interface MeasurementRepository extends JpaRepository<Measurement, Long> {

    @Query("SELECT m FROM Measurement m WHERE m.person.id = :personId")
    List<Measurement> findByPersonId(Long personId);
}