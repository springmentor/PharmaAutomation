package com.pas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.pas.model.Billing;
<<<<<<< HEAD
import com.pas.model.Prescription;

import java.util.Optional;

public interface BillingRepository extends JpaRepository<Billing, Integer> {
    Optional<Billing> findByPrescription(Prescription prescription);
}
=======

public interface BillingRepository extends JpaRepository<Billing, Integer> {
    // Add custom query methods if needed
}
>>>>>>> 0b44b1b699e7b5ab20af7f380b908f76be024f7b
