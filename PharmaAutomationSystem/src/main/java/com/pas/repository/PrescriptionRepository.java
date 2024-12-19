package com.pas.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pas.model.Prescription;


@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Integer> {
	
}