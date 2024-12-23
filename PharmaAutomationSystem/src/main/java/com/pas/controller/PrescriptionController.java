package com.pas.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.pas.model.Prescription;
import com.pas.service.PrescriptionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/prescriptions")
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;

    // Get all prescriptions
    @GetMapping("/getAllPrescriptions")
    public List<Prescription> getAllPrescriptions() {
        return prescriptionService.getAllPrescriptions();
    }

    // Get a specific prescription by ID
    @GetMapping("/getPrescription/{prescriptionId}")
    public ResponseEntity<Prescription> getPrescriptionById(@PathVariable int prescriptionId) {
        Prescription prescription = prescriptionService.getPrescriptionById(prescriptionId);
        return prescription != null ? ResponseEntity.ok(prescription) : ResponseEntity.notFound().build();
    }

    // Add a new prescription
    @PostMapping("/addPrescription")
    public ResponseEntity<String> addPrescription(@Valid @RequestBody Prescription prescription, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Validation failed: " + result.getAllErrors());
        }

        try {
            Prescription addedPrescription = prescriptionService.addPrescription(prescription);
            if (addedPrescription != null) {
                return ResponseEntity.ok("Prescription added successfully.");
            } else {
                return ResponseEntity.badRequest().body("Failed to add prescription.");
            }
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("Internal server error.");
        }
    }

    // Update an existing prescription by ID
    @PutMapping("/updatePrescription/{prescriptionId}")
    public ResponseEntity<Prescription> updatePrescription(@PathVariable int prescriptionId, @RequestBody Prescription prescriptionDetails) {
        Prescription updatedPrescription = prescriptionService.updatePrescription(prescriptionId, prescriptionDetails);
        return updatedPrescription != null ? ResponseEntity.ok(updatedPrescription) : ResponseEntity.notFound().build();
    }

    // Delete a prescription by ID
    @DeleteMapping("/deletePrescription/{prescriptionId}")
    public ResponseEntity<Void> deletePrescription(@PathVariable int prescriptionId) {
        try {
            Prescription prescription = prescriptionService.getPrescriptionById(prescriptionId);
            if (prescription != null) {
                prescriptionService.deletePrescription(prescriptionId);
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            return ResponseEntity.status(500).build();
        }
    }
}
