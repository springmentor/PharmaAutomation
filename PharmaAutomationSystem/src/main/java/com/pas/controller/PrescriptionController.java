package com.pas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.pas.model.Prescription;
import com.pas.service.PrescriptionService;

@RestController
public class PrescriptionController {
	
    @Autowired
    private PrescriptionService prescriptionService;
    
    @PostMapping("/addPrescription")
    public ResponseEntity<Prescription>addPrescription(@RequestBody Prescription prescription) {
        
        return new ResponseEntity<>(prescriptionService.addPrescription(prescription), HttpStatus.OK);
    }


}
