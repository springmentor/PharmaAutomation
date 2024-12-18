package com.pas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pas.model.Drug;
import com.pas.repository.DrugRepository;

import java.util.List;
import java.util.Optional;
@Service
public class DrugService {

    @Autowired
    private DrugRepository drugRepository;

    // Get all drugs
    public List<Drug> getAllDrugs() {
        return drugRepository.findAll();
    }

    // Get a drug by its ID
    public Drug getDrugById(int id) {
        Optional<Drug> drug = drugRepository.findById(id);
        return drug.orElse(null);
    }

    // Add a new drug
    public Drug addDrug(Drug drug) {
        return drugRepository.save(drug);
    }

    // Update drug details
    public Drug updateDrug(Drug drug) {
        return drugRepository.save(drug);  // Save the updated drug object
    }

    // Delete a drug by ID
    public void deleteDrug(int id) {
        drugRepository.deleteById(id);
    }

    // Get drugs by status
    public List<Drug> getDrugsByStatus(String status) {
        return drugRepository.findByStatus(status);
    }
}
