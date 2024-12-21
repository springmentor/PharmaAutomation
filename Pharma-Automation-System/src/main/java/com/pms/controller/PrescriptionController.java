package com.pms.controller;

import com.pms.model.Drug;
import com.pms.model.Prescription;
import com.pms.service.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;

    @GetMapping
    public ResponseEntity<List<Prescription>> getAllPrescriptions() {
        List<Prescription> prescriptions = prescriptionService.getAllPrescriptions();
        return ResponseEntity.ok(prescriptions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Prescription> getPrescriptionById(@PathVariable Long id) {
        Prescription prescription = prescriptionService.getPrescriptionById(id);
        return ResponseEntity.ok(prescription);
    }

    @PostMapping("/add") 
    public ResponseEntity<Prescription> addPrescription(@RequestBody Prescription prescription) {
        Prescription savedPrescription = prescriptionService.addPrescription(prescription);
        return ResponseEntity.ok(savedPrescription);
    }
    @DeleteMapping("/delete/{prescriptionId}")
    public ResponseEntity<Void> deletePrescription(@PathVariable Long prescriptionId) {
            prescriptionService.deletePrescriptionById(prescriptionId);
            return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Prescription>> searchPrescriptionsByPatientName(@RequestParam("patientName") String patientName) {
        List<Prescription> prescriptions = prescriptionService.findByPatientName(patientName);
        if (prescriptions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(prescriptions);
    }
    @GetMapping("/max-prescribed")
    public ResponseEntity<List<Map.Entry<Drug, Long>>> getMaxPrescribedDrugsForPastYear() {
        List<Map.Entry<Drug, Long>> maxPrescribedDrugs = prescriptionService.getMaxPrescribedDrugsForPastYear();
        return ResponseEntity.ok(maxPrescribedDrugs);
    }

    @GetMapping("/unprescribed")
    public ResponseEntity<List<Drug>> getUnprescribedDrugsForPastYear() {
        List<Drug> unprescribedDrugs = prescriptionService.getUnprescribedDrugsForPastYear();
        return ResponseEntity.ok(unprescribedDrugs);
    }

    @GetMapping("/max-prescribed/{limit}")
    public ResponseEntity<List<Map.Entry<Drug, Long>>> getMaxPrescribedDrugs(@PathVariable int limit) {
        List<Map.Entry<Drug, Long>> maxPrescribedDrugs = prescriptionService.getMaxPrescribedDrugs(limit);
        return ResponseEntity.ok(maxPrescribedDrugs);
    }

    @GetMapping("/unprescribed/{days}")
    public ResponseEntity<List<Drug>> getUnprescribedDrugsForPast(@PathVariable int days) {
        List<Drug> unprescribedDrugs = prescriptionService.getUnprescribedDrugsForPast(days);
        return ResponseEntity.ok(unprescribedDrugs);
    }

}
