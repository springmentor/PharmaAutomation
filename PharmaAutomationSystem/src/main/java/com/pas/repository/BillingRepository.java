package com.pas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.pas.model.Billing;

public interface BillingRepository extends JpaRepository<Billing, Integer> {
    // Add custom query methods if needed
}
