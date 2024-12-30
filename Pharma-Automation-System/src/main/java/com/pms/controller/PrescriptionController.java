package com.pms.controller;

import com.pms.exception.InvalidEntityException;
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
    public ResponseEntity<Prescription> addPrescription(@RequestBody Prescription prescription) throws InvalidEntityException {
        Prescription savedPrescription = prescriptionService.addPrescription(prescription);
        return ResponseEntity.ok(savedPrescription);
    }

    @DeleteMapping("/delete/{prescriptionId}")
    public ResponseEntity<Void> deletePrescription(@PathVariable Long prescriptionId) {
        prescriptionService.deletePrescriptionById(prescriptionId);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/filter")
    public ResponseEntity<List<Prescription>> filterPrescriptions(@RequestParam(required = false) Boolean isBillGenerate) {
        List<Prescription> prescriptions;
        if (isBillGenerate != null) {
            if (isBillGenerate) {
                prescriptions = prescriptionService.getPrescriptionsWithGeneratedBills();
            } else {
                prescriptions = prescriptionService.getPrescriptionsWithoutGeneratedBills();
            }
        } else {
            prescriptions = prescriptionService.getAllPrescriptions();
        }
        return ResponseEntity.ok(prescriptions);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Prescription>> searchPrescriptionsByPatientName(@RequestParam("patientName") String patientName) {
        List<Prescription> prescriptions = prescriptionService.findByPatientName(patientName);
        if (prescriptions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(prescriptions);
    }

    @GetMapping("/max-prescribed/{limit}")
    public ResponseEntity<List<Map<String, Object>>> getMaxPrescribedDrugs(@PathVariable int limit) {
        List<Map.Entry<Drug, Long>> maxPrescribedDrugs = prescriptionService.getMaxPrescribedDrugs(limit);
        
        List<Map<String, Object>> response = maxPrescribedDrugs.stream()
            .map(entry -> Map.of(
                "drug", entry.getKey(),
                "quantity", entry.getValue()
            ))
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/unprescribed/{days}")
    public ResponseEntity<List<Drug>> getUnprescribedDrugs(@PathVariable int days) {
        List<Drug> unprescribedDrugs = prescriptionService.getUnprescribedDrugs(days);
        return ResponseEntity.ok(unprescribedDrugs);
    }

    @GetMapping("/with-bills")
    public ResponseEntity<List<Prescription>> getPrescriptionsWithGeneratedBills() {
        List<Prescription> prescriptions = prescriptionService.getPrescriptionsWithGeneratedBills();
        return ResponseEntity.ok(prescriptions);
    }

    @GetMapping("/without-bills")
    public ResponseEntity<List<Prescription>> getPrescriptionsWithoutGeneratedBills() {
        List<Prescription> prescriptions = prescriptionService.getPrescriptionsWithoutGeneratedBills();
        return ResponseEntity.ok(prescriptions);
    }

    @PostMapping("/mark-billed/{prescriptionId}")
    public ResponseEntity<Void> markPrescriptionAsBilled(@PathVariable Long prescriptionId) {
        prescriptionService.markPrescriptionAsBilled(prescriptionId);
        return ResponseEntity.ok().build();
    }
}

