package com.pas.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pas.model.Billing;
import com.pas.model.Prescription;
import com.pas.model.Drug;
import com.pas.repository.BillingRepository;
import com.pas.repository.PrescriptionRepository;
import com.pas.repository.DrugRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class BillingService {

	private static final Logger logger = LoggerFactory.getLogger(BillingService.class);
	
    @Autowired
    private BillingRepository billingRepository;

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private DrugRepository drugRepository;

    public List<Billing> getAllBillings() {
        return billingRepository.findAll();
    }

    public Billing getBillingById(int billingId) {
        return billingRepository.findById(billingId)
            .orElseThrow(() -> new RuntimeException("Billing not found"));
    }

    @Transactional
    public Billing generateBill(int prescriptionId) {
        logger.info("Generating bill for prescription ID: {}", prescriptionId);
        try {
            Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new RuntimeException("Prescription not found with ID: " + prescriptionId));

            // Check if billing already exists
            if (billingRepository.findByPrescription(prescription).isPresent()) {
                logger.warn("Billing already exists for prescription ID: {}", prescriptionId);
                throw new RuntimeException("Billing already exists for this prescription");
            }

            double totalAmount = calculateTotalAmount(prescription);
            double discount = calculateDiscount(totalAmount);
            double finalAmount = totalAmount - discount;

            Billing billing = new Billing();
            billing.setPrescription(prescription);
            billing.setBillingDate(LocalDate.now());
            billing.setTotalAmount(totalAmount);
            billing.setDiscount(discount);
            billing.setFinalAmount(finalAmount);

            Billing savedBilling = billingRepository.save(billing);
            logger.info("Bill generated successfully for prescription ID: {}", prescriptionId);
            return savedBilling;
        } catch (Exception e) {
            logger.error("Error generating bill for prescription ID: {}", prescriptionId, e);
            throw new RuntimeException("Failed to generate bill: " + e.getMessage(), e);
        }
    }

    private double calculateTotalAmount(Prescription prescription) {
        return prescription.getPrescribedDrugs().entrySet().stream()
            .mapToDouble(entry -> {
                Drug drug = drugRepository.findById(entry.getKey())
                    .orElseThrow(() -> new RuntimeException("Drug not found with ID: " + entry.getKey()));
                return drug.getUnitPrice() * entry.getValue();
            })
            .sum();
    }

    @Transactional
    public Billing updateBillingForPrescription(Prescription prescription) {
        Billing billing = billingRepository.findByPrescription(prescription)
            .orElseThrow(() -> new RuntimeException("Billing not found for prescription"));

        double totalAmount = calculateTotalAmount(prescription);
        double discount = calculateDiscount(totalAmount);
        double finalAmount = totalAmount - discount;

        billing.setTotalAmount(totalAmount);
        billing.setDiscount(discount);
        billing.setFinalAmount(finalAmount);

        return billingRepository.save(billing);
    }

//    private double calculateTotalAmount(Prescription prescription) {
//        return prescription.getPrescribedDrugs().entrySet().stream()
//            .mapToDouble(entry -> {
//                Drug drug = drugRepository.findById(entry.getKey())
//                    .orElseThrow(() -> new RuntimeException("Drug not found"));
//                return drug.getUnitPrice() * entry.getValue();
//            })
//            .sum();
//    }

    private double calculateDiscount(double totalAmount) {
        return totalAmount > 500 ? totalAmount * 0.1 : 0;
    }

    @Transactional
    public void deleteBilling(int billingId) {
        billingRepository.deleteById(billingId);
    }
}

