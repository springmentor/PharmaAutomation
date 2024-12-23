package com.pas.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pas.model.Billing;
import com.pas.model.Prescription;
import com.pas.repository.BillingRepository;
import com.pas.repository.PrescriptionRepository;

@Service
public class BillingService {

    @Autowired
    private BillingRepository billingRepository;

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private StockService stockService;

    public List<Billing> getAllBillings() {
        return billingRepository.findAll();
    }

    public Billing getBillingById(int billingId) {
        return billingRepository.findById(billingId).orElse(null);
    }

    public Billing addBilling(Billing billing) {
        // Reduce stock based on the prescription
        Prescription prescription = billing.getPrescription();
        prescription.getStocks().forEach(stock -> {
            stockService.reduceStock(stock.getDrug().getDrugId(), stock.getReceivedQuantity());
        });

        // Calculate the total amount
        double totalAmount = prescription.getStocks().stream()
            .mapToDouble(stock -> stock.getReceivedQuantity() * stock.getUnitPrice())
            .sum();

        // Apply discount
        double discount = billing.getDiscount();
        double finalAmount = totalAmount - (totalAmount * discount / 100);

        billing.setTotalAmount(totalAmount);
        billing.setFinalAmount(finalAmount);
        billing.setBillingDate(LocalDate.now());

        return billingRepository.save(billing);
    }

    public Billing updateBilling(int billingId, Billing billingDetails) {
        Billing existingBilling = billingRepository.findById(billingId).orElse(null);

        if (existingBilling != null) {
            existingBilling.setBillingDate(billingDetails.getBillingDate());
            existingBilling.setTotalAmount(billingDetails.getTotalAmount());
            existingBilling.setDiscount(billingDetails.getDiscount());
            existingBilling.setFinalAmount(billingDetails.getFinalAmount());
            existingBilling.setPrescription(billingDetails.getPrescription());

            return billingRepository.save(existingBilling);
        }

        return null;
    }

    public void deleteBilling(int billingId) {
        billingRepository.deleteById(billingId);
    }
}
