package com.pas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.pas.model.Supplier;

public interface SupplierRepository extends JpaRepository<Supplier, Integer> {
}
