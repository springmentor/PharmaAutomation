package com.pas.controller;

<<<<<<< HEAD
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pas.model.Prescription;
import com.pas.service.PrescriptionService;

import java.util.List;
import java.util.Map;
=======
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.pas.model.Prescription;
import com.pas.service.PrescriptionService;

import jakarta.validation.Valid;
>>>>>>> 0b44b1b699e7b5ab20af7f380b908f76be024f7b

@RestController
@RequestMapping("/prescriptions")
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;

<<<<<<< HEAD

=======
    // Get all prescriptions
>>>>>>> 0b44b1b699e7b5ab20af7f380b908f76be024f7b
    @GetMapping("/getAllPrescriptions")
    public List<Prescription> getAllPrescriptions() {
        return prescriptionService.getAllPrescriptions();
    }

<<<<<<< HEAD
    @GetMapping("/getPrescription/{prescriptionId}")
    public ResponseEntity<Prescription> getPrescriptionById(@PathVariable int prescriptionId) {
        Prescription prescription = prescriptionService.getPrescriptionById(prescriptionId);
        return ResponseEntity.ok(prescription);
    }

    @PostMapping("/addPrescription")
    public ResponseEntity<Prescription> addPrescription(@RequestBody Prescription prescription) {
        Prescription addedPrescription = prescriptionService.addPrescription(prescription);
        return ResponseEntity.ok(addedPrescription);
    }

    @PutMapping("/updatePrescription/{prescriptionId}")
    public ResponseEntity<Prescription> updatePrescription(@PathVariable int prescriptionId, @RequestBody Map<Integer, Integer> updatedDrugs) {
        try {
            Prescription updatedPrescription = prescriptionService.updatePrescription(prescriptionId, updatedDrugs);
            return ResponseEntity.ok(updatedPrescription);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/deletePrescription/{prescriptionId}")
    public ResponseEntity<Void> deletePrescription(@PathVariable int prescriptionId) {
        prescriptionService.deletePrescription(prescriptionId);
        return ResponseEntity.noContent().build();
    }
}
=======
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
>>>>>>> 0b44b1b699e7b5ab20af7f380b908f76be024f7b
