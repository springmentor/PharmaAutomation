package com.pas.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pas.model.Drug;
import com.pas.service.DrugService;

@RestController
public class DrugController {

    @Autowired
    private DrugService drugService;

    // Get all drugs
    @GetMapping("/getAllDrugs")
    public List<Drug> getAllDrugs() {
        return drugService.getAllDrugs();
    }

    // Get a specific drug by drugId
    @GetMapping("/getDrug/{drugId}")
    public ResponseEntity<Drug> getDrugById(@PathVariable int drugId) {
        Drug drug = drugService.getDrugById(drugId);
        return drug != null ? ResponseEntity.ok(drug) : ResponseEntity.notFound().build();
    }

    // Add a new drug
    @PostMapping("/addDrug")
    public Drug addDrug(@RequestBody Drug drug) {
        return drugService.addDrug(drug);
    }

    @PutMapping("/updateDrug/{drugId}")
    public ResponseEntity<Drug> updateDrug(@PathVariable int drugId, @RequestBody Drug drug) {
        if (drug.getDrugId() != drugId) {
            return ResponseEntity.badRequest().build();  // Ensure the IDs match
        }

        Drug existingDrug = drugService.getDrugById(drugId);
        if (existingDrug != null) {
            // Update fields of the existing drug
            existingDrug.setDrugName(drug.getDrugName());  // Update drug name
            existingDrug.setStatus(drug.getStatus());      // Update status
            existingDrug.setTotalQuantity(drug.getTotalQuantity());  // Update total quantity

            // Save the updated drug
            Drug updatedDrug = drugService.updateDrug(existingDrug);
            return ResponseEntity.ok(updatedDrug);
        } else {
            return ResponseEntity.notFound().build();  // Drug not found
        }
    }

    // Delete a drug by drugId
    @DeleteMapping("/deleteDrug/{drugId}")
    public ResponseEntity<Void> deleteDrug(@PathVariable int drugId) {
        Drug drug = drugService.getDrugById(drugId);
        if (drug != null) {
            drugService.deleteDrug(drugId);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
