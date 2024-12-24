package com.pas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pas.model.Prescription;
import com.pas.service.PrescriptionService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/prescriptions")
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;


    @GetMapping("/getAllPrescriptions")
    public List<Prescription> getAllPrescriptions() {
        return prescriptionService.getAllPrescriptions();
    }

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