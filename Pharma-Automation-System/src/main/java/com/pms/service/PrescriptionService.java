package com.pms.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pms.exception.InvalidEntityException;
import com.pms.model.Drug;
import com.pms.model.Prescription;
import com.pms.model.PrescriptionItem;
import com.pms.repository.BillRepository;
import com.pms.repository.DrugRepository;
import com.pms.repository.PrescriptionRepository;
import com.pms.repository.StockRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class PrescriptionService {

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private DrugRepository drugRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private BillRepository billRepository;
    
    @Autowired
    private StockService stockService;

    public List<Prescription> getAllPrescriptions() {
        return prescriptionRepository.findAll();
    }

    @Transactional
    public Prescription addPrescription(Prescription prescription)throws InvalidEntityException {
        prescription.getItems().forEach(item -> {
            Drug drug = null;
            try {
                drug = drugRepository.findById(item.getDrug().getId())
                        .orElseThrow(() -> new InvalidEntityException("Drug not found with id: " + item.getDrug().getId()));
            } catch (InvalidEntityException e) {
                throw new RuntimeException(e);
            }

            if (!drug.isActive()||drug.isBanned()) {
                try {
                    throw new InvalidEntityException("Cannot prescribe deactivated drug: " + drug.getName());
                } catch (InvalidEntityException e) {
                    throw new RuntimeException(e);
                }
            }

            int totalAvailableQuantity = stockRepository.getTotalQuantityByDrugId(drug.getId());
            if (totalAvailableQuantity < item.getQuantity()) {
                try {
                    throw new InvalidEntityException("Insufficient stock for drug: " + drug.getName());
                } catch (InvalidEntityException e) {
                    throw new RuntimeException(e);
                }
            }

            item.setPrescription(prescription);
        });

        prescription.setBillGenerated(false);
        return prescriptionRepository.save(prescription);
    }

    public Prescription getPrescriptionById(Long id) {
        try {
            return prescriptionRepository.findById(id)
                    .orElseThrow(() -> new InvalidEntityException("Prescription not found with id: " + id));
        } catch (InvalidEntityException e) {
            throw new RuntimeException(e);
        }
    }
    @Transactional
    public void markPrescriptionAsBilled(Long prescriptionId) {
        int updatedRows = prescriptionRepository.updateBillGeneratedStatus(prescriptionId, true);
        if (updatedRows != 1) {
            throw new RuntimeException("Failed to update prescription status. Expected 1 row updated, got " + updatedRows);
        }

        boolean verified = verifyBillGeneratedStatus(prescriptionId);
        if (!verified) {
            throw new RuntimeException("Failed to verify prescription status update for prescription: " + prescriptionId);
        }
    }

    @Transactional
    public Prescription savePrescription(Prescription prescription) {
        return prescriptionRepository.save(prescription);
    }

    @Transactional
    public void deletePrescriptionById(Long prescriptionId) {
        billRepository.deleteByPrescriptionId(prescriptionId);
        prescriptionRepository.deleteById(prescriptionId);
    }

    public List<Prescription> getPrescriptionsWithGeneratedBills() {
        return prescriptionRepository.findByIsBillGenerated(true);
    }

    public List<Prescription> getPrescriptionsWithoutGeneratedBills() {
        return prescriptionRepository.findByIsBillGenerated(false);
    }

    public List<Prescription> findByPatientName(String patientName) {
        return prescriptionRepository.findByPatientNameContainingIgnoreCase(patientName);
    }

    public List<Map.Entry<Drug, Long>> getMaxPrescribedDrugs(int limit) {
        List<Prescription> allPrescriptions = prescriptionRepository.findAll();
        Map<Drug, Long> drugPrescriptionCount = allPrescriptions.stream()
                .flatMap(prescription -> prescription.getItems().stream())
                .collect(Collectors.groupingBy(PrescriptionItem::getDrug, Collectors.counting()));

        return drugPrescriptionCount.entrySet().stream()
                .sorted(Map.Entry.<Drug, Long>comparingByValue().reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    private Set<Drug> getMaxPrescribedDrugsSet(int limit) {
        return getMaxPrescribedDrugs(limit).stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    public List<Drug> getUnprescribedDrugs(int days) {
        LocalDate cutoffDate = LocalDate.now().minusDays(days);
        List<Prescription> recentPrescriptions = prescriptionRepository.findByPrescriptionDateAfter(cutoffDate);
        
        Set<Drug> prescribedDrugs = recentPrescriptions.stream()
                .flatMap(prescription -> prescription.getItems().stream())
                .map(PrescriptionItem::getDrug)
                .collect(Collectors.toSet());

        List<Drug> allDrugs = drugRepository.findAll();
        
        // Get max prescribed drugs to exclude them from unprescribed list
        Set<Drug> maxPrescribedDrugs = getMaxPrescribedDrugsSet(Integer.MAX_VALUE);

        return allDrugs.stream()
                .filter(drug -> !prescribedDrugs.contains(drug) && !maxPrescribedDrugs.contains(drug))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean verifyBillGeneratedStatus(Long prescriptionId) {
        return prescriptionRepository.findById(prescriptionId)
                .map(Prescription::isBillGenerated)
                .orElseThrow(() -> new EntityNotFoundException("Prescription not found with id: " + prescriptionId));
    }
}

