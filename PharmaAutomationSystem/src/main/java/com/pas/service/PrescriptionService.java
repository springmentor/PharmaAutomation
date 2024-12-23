package com.pas.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pas.model.Prescription;
import com.pas.repository.PrescriptionRepository;

@Service
public class PrescriptionService {

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private StockService stockService;

    public List<Prescription> getAllPrescriptions() {
        return prescriptionRepository.findAll();
    }

    public Prescription getPrescriptionById(int prescriptionId) {
        return prescriptionRepository.findById(prescriptionId).orElse(null);
    }

    public Prescription addPrescription(Prescription prescription) {
        // Add prescription to the repository
        Prescription addedPrescription = prescriptionRepository.save(prescription);

        // Deduct stock based on the prescription
        prescription.getStocks().forEach(stock -> {
            stockService.reduceStock(stock.getDrug().getDrugId(), stock.getReceivedQuantity());
        });

        return addedPrescription;
    }

    public Prescription updatePrescription(int prescriptionId, Prescription prescriptionDetails) {
        Prescription existingPrescription = prescriptionRepository.findById(prescriptionId).orElse(null);

        if (existingPrescription != null) {
            existingPrescription.setPatientName(prescriptionDetails.getPatientName());
            existingPrescription.setDoctorName(prescriptionDetails.getDoctorName());
            existingPrescription.setPrescribedDate(prescriptionDetails.getPrescribedDate());
            existingPrescription.setStocks(prescriptionDetails.getStocks());

            // Deduct stock based on the updated prescription
            prescriptionDetails.getStocks().forEach(stock -> {
                stockService.reduceStock(stock.getDrug().getDrugId(), stock.getReceivedQuantity());
            });

            return prescriptionRepository.save(existingPrescription);
        }

        return null;
    }

    public void deletePrescription(int prescriptionId) {
        prescriptionRepository.deleteById(prescriptionId);
    }
}
