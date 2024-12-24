package com.pas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pas.model.Billing;
import com.pas.service.BillingService;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@RestController
@RequestMapping("/billings")
public class BillingController {

	private static final Logger logger = LoggerFactory.getLogger(BillingController.class);
    @Autowired
    private BillingService billingService;

    @GetMapping("/getAllBillings")
    public List<Billing> getAllBillings() {
        return billingService.getAllBillings();
    }

    @GetMapping("/getBilling/{billingId}")
    public ResponseEntity<Billing> getBillingById(@PathVariable int billingId) {
        Billing billing = billingService.getBillingById(billingId);
        return ResponseEntity.ok(billing);
    }
    @PostMapping("/generateBill/{prescriptionId}")
    public ResponseEntity<?> generateBill(@PathVariable int prescriptionId) {
        try {
            logger.info("Received request to generate bill for prescription ID: {}", prescriptionId);
            Billing generatedBill = billingService.generateBill(prescriptionId);
            return ResponseEntity.ok(generatedBill);
        } catch (Exception e) {
            logger.error("Error generating bill for prescription ID: {}", prescriptionId, e);
            return ResponseEntity.internalServerError().body("Failed to generate bill: " + e.getMessage());
        }
    }

    @DeleteMapping("/deleteBilling/{billingId}")
    public ResponseEntity<Void> deleteBilling(@PathVariable int billingId) {
        billingService.deleteBilling(billingId);
        return ResponseEntity.noContent().build();
    }
}