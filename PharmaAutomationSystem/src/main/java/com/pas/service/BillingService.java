package com.pas.service;
<<<<<<< HEAD
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
=======
>>>>>>> 0b44b1b699e7b5ab20af7f380b908f76be024f7b

import java.time.LocalDate;
import java.util.List;

<<<<<<< HEAD
@Service
public class BillingService {

	private static final Logger logger = LoggerFactory.getLogger(BillingService.class);
	
=======
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pas.model.Billing;
import com.pas.model.Prescription;
import com.pas.repository.BillingRepository;
import com.pas.repository.PrescriptionRepository;

@Service
public class BillingService {

>>>>>>> 0b44b1b699e7b5ab20af7f380b908f76be024f7b
    @Autowired
    private BillingRepository billingRepository;

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
<<<<<<< HEAD
    private DrugRepository drugRepository;
=======
    private StockService stockService;
>>>>>>> 0b44b1b699e7b5ab20af7f380b908f76be024f7b

    public List<Billing> getAllBillings() {
        return billingRepository.findAll();
    }

    public Billing getBillingById(int billingId) {
<<<<<<< HEAD
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
=======
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
>>>>>>> 0b44b1b699e7b5ab20af7f380b908f76be024f7b

        return billingRepository.save(billing);
    }

<<<<<<< HEAD
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
=======
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

>>>>>>> 0b44b1b699e7b5ab20af7f380b908f76be024f7b
    public void deleteBilling(int billingId) {
        billingRepository.deleteById(billingId);
    }
}
<<<<<<< HEAD

=======
>>>>>>> 0b44b1b699e7b5ab20af7f380b908f76be024f7b
