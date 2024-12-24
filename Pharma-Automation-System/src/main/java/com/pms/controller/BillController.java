package com.pms.controller;

import com.pms.exception.InvalidEntityException;
import com.pms.model.Bill;
import com.pms.model.Prescription;
import com.pms.service.BillService;
import com.pms.service.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bills")
public class BillController {

    @Autowired
    private BillService billService;

    @Autowired
    private PrescriptionService prescriptionService;

    @GetMapping
    public ResponseEntity<List<Bill>> getAllBills() {
        List<Bill> bills = billService.getAllBills();
        return ResponseEntity.ok(bills);
    }

    @GetMapping("/{prescriptionId}")
    public ResponseEntity<Bill> getBillByPrescriptionId(@PathVariable Long prescriptionId) {
            Bill bill = billService.getBillByPrescriptionId(prescriptionId);
            return ResponseEntity.ok(bill);

    }


    @PostMapping("/generate/{prescriptionId}")
    public ResponseEntity<Bill> generateBill(@PathVariable Long prescriptionId,
                                             @RequestParam(value = "discountPercentage", defaultValue = "10.0") double discountPercentage)throws InvalidEntityException {
        // Generate the bill
        Bill generatedBill = billService.generateBill(prescriptionId, discountPercentage);

        // Update the prescription's bill generated status
        Prescription prescription = prescriptionService.getPrescriptionById(prescriptionId);

        // Save the updated prescription
        prescriptionService.addPrescription(prescription);


        return ResponseEntity.ok(generatedBill);
    }


}
