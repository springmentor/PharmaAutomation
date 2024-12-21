package com.pms.service;

import com.pms.exception.InvalidEntityException;
import com.pms.model.Drug;
import com.pms.repository.DrugRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DrugService {

    @Autowired
    private DrugRepository drugRepository;

    public List<Drug> getAllDrugs() {
        return drugRepository.findAll();
    }

    public Drug getDrugById(Long id) throws InvalidEntityException {
        Drug drug = drugRepository.findById(id).orElse(null);
        if (drug != null) {
            return drug;
        } else {
            throw new InvalidEntityException("Drug with ID " + id + " not found");
        }
    }
    @Transactional
    public Drug saveDrug(Drug drug)throws InvalidEntityException {
        validateDrug(drug);
        return drugRepository.save(drug);
    }

    private void validateDrug(Drug drug) throws InvalidEntityException {
    	if (drug == null || drug.getName() == null || drug.getName().trim().isEmpty()) {
            throw new InvalidEntityException("Drug name cannot be empty");
        }
        if (drug.getPrice() == null || drug.getPrice() <= 0) {
            throw new InvalidEntityException("Drug price must be greater than zero");
        }
        if (drug.getTotalQuantity() == null || drug.getTotalQuantity() <=0) {
            throw new InvalidEntityException("Total quantity cannot be negative");
        }
    }

    @Transactional
    public Drug updateDrug(Drug drug) throws InvalidEntityException {
        // Validate input
        if (drug.getId() == null) {
            throw new InvalidEntityException("Drug ID cannot be null for update operation");
        }

        // Fetch existing drug
        Drug existingDrug = drugRepository.findById(drug.getId())
                .orElseThrow(() -> new InvalidEntityException("Drug with ID " + drug.getId() + " not found"));

        // Validate banned reason if drug is marked as banned
        if (drug.isBanned() && (drug.getBannedReason() == null)) {
            throw new InvalidEntityException("Banned reason must be provided if the drug is marked as banned.");
        }

        // Update the fields
        existingDrug.setName(drug.getName());
        existingDrug.setDescription(drug.getDescription());
        existingDrug.setPrice(drug.getPrice());
        existingDrug.setSupplierName(drug.getSupplierName());
        existingDrug.setTotalQuantity(drug.getTotalQuantity());
        existingDrug.setActive(drug.isActive());
        existingDrug.setBanned(drug.isBanned());
        existingDrug.setBannedReason(drug.getBannedReason());

        // Save the updated drug
        return drugRepository.save(existingDrug);
    }

    @Transactional
    public void deactivateDrug(Long id)throws InvalidEntityException {
        if (!drugRepository.existsById(id)) {
            throw new InvalidEntityException("Drug with ID " + id + " not found");
        }
        Drug drug = drugRepository.findById(id).get();
        drug.setActive(false);
        drugRepository.save(drug);
    }
    @Transactional
    public void deleteDrug(Long id)throws InvalidEntityException {
        if (!drugRepository.existsById(id)) {
            throw new InvalidEntityException("Drug with ID " + id + " not found");
        }
        drugRepository.deleteById(id);
    }
    public List<Drug> searchDrugs(String query) {
        return drugRepository.findByNameStartingWithIgnoreCase(query);
    }
    public List<Drug> getBannedDrugs() {
        return drugRepository.findByBannedTrue();
    }

    public List<Drug> getDeactivatedDrugs() {
        return drugRepository.findByActiveFalse();
    }



}