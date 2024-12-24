package com.pms.service;

import com.pms.exception.InvalidEntityException;
import com.pms.model.Bill;
import com.pms.model.Prescription;
import com.pms.model.PrescriptionItem;
import com.pms.repository.BillRepository;
import com.pms.repository.PrescriptionRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class BillService {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private StockService stockService;

    public List<Bill> getAllBills() {
        return billRepository.findAll();
    }

    public Bill getBillByPrescriptionId(Long prescriptionId) {
        return billRepository.findByPrescriptionId(prescriptionId)
                .orElseThrow(() -> new EntityNotFoundException("Bill not found with prescription ID: " + prescriptionId));
    }

    @Transactional
    public Bill generateBill(Long prescriptionId, double discountPercentage) throws InvalidEntityException {
        Prescription prescription = prescriptionService.getPrescriptionById(prescriptionId);

        if (prescription == null) {
            throw new EntityNotFoundException("Prescription not found with id: " + prescriptionId);
        }

        if (prescription.isBillGenerated()) {
            throw new IllegalStateException("Bill has already been generated for this prescription");
        }

        if (prescription.getItems() == null || prescription.getItems().isEmpty()) {
            throw new IllegalStateException("Prescription has no items");
        }

        double totalAmount = calculateTotalAmount(prescription);
        double discountedAmount = applyDiscount(totalAmount, discountPercentage);

        // Create and save the bill first
        Bill bill = createBill(prescription, totalAmount, discountPercentage, discountedAmount);
        Bill savedBill = billRepository.save(bill);
        
        // Update stock quantities
        for (PrescriptionItem item : prescription.getItems()) {
            stockService.updateStockQuantities(item.getDrug().getId(), item.getQuantity());
        }

        // Use direct JPQL update
        int updatedRows = prescriptionRepository.updateBillGeneratedStatus(prescriptionId, true);
        if (updatedRows != 1) {
            throw new RuntimeException("Failed to update prescription status. Expected 1 row updated, got " + updatedRows);
        }

        return savedBill;
    }

    private double calculateTotalAmount(Prescription prescription) {
        double totalAmount = 0;
        for (PrescriptionItem item : prescription.getItems()) {
            double itemPrice = item.getDrug().getPrice();
            int quantity = item.getQuantity();
            double itemTotal = itemPrice * quantity;
            totalAmount += itemTotal;
        }
        return totalAmount;
    }

    private double applyDiscount(double totalAmount, double discountPercentage) {
        return totalAmount * (1 - discountPercentage / 100);
    }

    private Bill createBill(Prescription prescription, double totalAmount, double discountPercentage, double discountedAmount) {
        Bill bill = new Bill();
        bill.setPrescription(prescription);
        bill.setAmount(totalAmount);
        bill.setBillDate(LocalDate.now());
        bill.setDiscountPercentage(discountPercentage);
        bill.setDiscountedAmount(discountedAmount);
        return bill;
    }
}

