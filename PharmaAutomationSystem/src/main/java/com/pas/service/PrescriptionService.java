package com.pas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pas.model.Prescription;

import com.pas.repository.PrescriptionRepository;

@Service
public class PrescriptionService {
	@Autowired
	private PrescriptionRepository prescriptionRepository;
	
	public Prescription addPrescription(Prescription prescription) {
		// TODO Auto-generated method stub
		return prescriptionRepository.save(prescription);
	}

}