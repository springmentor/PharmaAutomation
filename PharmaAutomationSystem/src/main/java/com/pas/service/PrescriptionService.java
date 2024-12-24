package com.pas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pas.model.Prescription;
import com.pas.model.Drug;
import com.pas.repository.PrescriptionRepository;
import com.pas.repository.DrugRepository;

import java.util.List;
import java.util.Map;

@Service
public class PrescriptionService {

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private DrugRepository drugRepository;

    @Autowired
    private StockService stockService;

    @Autowired
    private BillingService billingService;

    public List<Prescription> getAllPrescriptions() {
        return prescriptionRepository.findAll();
    }

    public Prescription getPrescriptionById(int prescriptionId) {
        return prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new RuntimeException("Prescription not found"));
    }

    @Transactional
    public Prescription addPrescription(Prescription prescription) {
        // Validate prescribed drugs
        for (Map.Entry<Integer, Integer> entry : prescription.getPrescribedDrugs().entrySet()) {
            Drug drug = drugRepository.findById(entry.getKey())
                    .orElseThrow(() -> new RuntimeException("Drug not found with ID: " + entry.getKey()));
            
            if (drug.getTotalQuantity() < entry.getValue()) {
                throw new RuntimeException("Insufficient stock for drug: " + drug.getDrugName());
            }
        }

        // Reduce stock
        for (Map.Entry<Integer, Integer> entry : prescription.getPrescribedDrugs().entrySet()) {
            stockService.reduceStock(entry.getKey(), entry.getValue());
        }

        // Save prescription
        Prescription savedPrescription = prescriptionRepository.save(prescription);

        // Generate billing
        billingService.generateBill(savedPrescription.getPrescriptionId());

        return savedPrescription;
    }

    @Transactional
    public Prescription updatePrescription(int prescriptionId, Map<Integer, Integer> updatedDrugs) {
        Prescription prescription = getPrescriptionById(prescriptionId);
        
        // Restore previous stock quantities
        for (Map.Entry<Integer, Integer> entry : prescription.getPrescribedDrugs().entrySet()) {
            stockService.increaseStock(entry.getKey(), entry.getValue());
        }

        // Update prescribed drugs
        prescription.setPrescribedDrugs(updatedDrugs);

        // Reduce new stock quantities
        for (Map.Entry<Integer, Integer> entry : updatedDrugs.entrySet()) {
            Drug drug = drugRepository.findById(entry.getKey())
                .orElseThrow(() -> new RuntimeException("Drug not found"));
            stockService.reduceStock(drug.getDrugId(), entry.getValue());
        }

        Prescription updatedPrescription = prescriptionRepository.save(prescription);
        billingService.updateBillingForPrescription(updatedPrescription);

        return updatedPrescription;
    }

    @Transactional
    public void deletePrescription(int prescriptionId) {
        Prescription prescription = getPrescriptionById(prescriptionId);
        for (Map.Entry<Integer, Integer> entry : prescription.getPrescribedDrugs().entrySet()) {
            stockService.increaseStock(entry.getKey(), entry.getValue());
        }
        prescriptionRepository.deleteById(prescriptionId);
    }
}