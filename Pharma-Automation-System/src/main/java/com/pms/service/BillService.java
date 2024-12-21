package com.pms.service;

import com.pms.model.Bill;
import com.pms.model.Prescription;
import com.pms.model.Stock;
import com.pms.repository.BillRepository;
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

    public List<Bill> getAllBills() {
        return billRepository.findAll();
    }

    public Bill getBillByPrescriptionId(Long prescriptionId) {
        return billRepository.findByPrescriptionId(prescriptionId)
                .orElseThrow(() -> new EntityNotFoundException("Bill not found with prescription ID: " + prescriptionId));
    }

    @Transactional
    public Bill generateBill(Long prescriptionId, double discountPercentage) {
        Prescription prescription = prescriptionService.getPrescriptionById(prescriptionId);

        double totalAmount = prescription.getItems().stream()
                .mapToDouble(item -> item.getDrug().getPrice() * item.getQuantity())
                .sum();

        double discountedAmount = totalAmount * (1 - discountPercentage / 100);

        Bill bill = new Bill();
        bill.setPrescription(prescription);
        bill.setAmount(totalAmount);
        bill.setBillDate(LocalDate.now());
        bill.setDiscountPercentage(discountPercentage);
        bill.setDiscountedAmount(discountedAmount);

        return billRepository.save(bill);
    }

}
