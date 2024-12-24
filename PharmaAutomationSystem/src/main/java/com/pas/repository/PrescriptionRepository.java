package com.pas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.pas.model.Prescription;

public interface PrescriptionRepository extends JpaRepository<Prescription, Integer> {
    // Add custom query methods if needed
}
