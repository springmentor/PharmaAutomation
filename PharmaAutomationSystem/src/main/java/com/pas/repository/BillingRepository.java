package com.pas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.pas.model.Billing;
import com.pas.model.Prescription;

import java.util.Optional;

public interface BillingRepository extends JpaRepository<Billing, Integer> {
    Optional<Billing> findByPrescription(Prescription prescription);
}