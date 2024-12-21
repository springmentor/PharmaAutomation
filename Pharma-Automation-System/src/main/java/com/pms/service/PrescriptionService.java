package com.pms.service;

import com.pms.model.Drug;
import com.pms.model.Prescription;
import com.pms.model.PrescriptionItem;
import com.pms.model.Stock;
import com.pms.repository.BillRepository;
import com.pms.repository.DrugRepository;
import com.pms.repository.PrescriptionRepository;
import com.pms.repository.StockRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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


    public List<Prescription> getAllPrescriptions() {
        return prescriptionRepository.findAll();
    }

    public Prescription getPrescriptionById(Long id) {
        return prescriptionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Prescription not found with id: " + id));
    }

    @Transactional
    public Prescription addPrescription(Prescription prescription) {
        prescription.getItems().forEach(item -> {
            Drug drug = drugRepository.findById(item.getDrug().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Drug not found with id: " + item.getDrug().getId()));

            // Prevent deactivated drugs from being prescribed
            if (!drug.isActive()) {
                throw new IllegalStateException("Cannot prescribe deactivated drug: " + drug.getName());
            }

            int totalAvailableQuantity = stockRepository.getTotalQuantityByDrugId(drug.getId());
            if (totalAvailableQuantity < item.getQuantity()) {
                throw new IllegalStateException("Insufficient stock for drug: " + drug.getName());
            }

            updateStockQuantities(drug.getId(), item.getQuantity());
        });

        prescription.setIsBillGenerated(false);
        Prescription savedPrescription = prescriptionRepository.save(prescription);
        
        // Explicitly save items
        prescription.getItems().forEach(item -> item.setPrescription(savedPrescription));
        return savedPrescription;
    }



    @Transactional
    public void markPrescriptionAsBilled(Long prescriptionId) {
        Prescription prescription = getPrescriptionById(prescriptionId);
        prescription.setIsBillGenerated(true);
        prescriptionRepository.save(prescription);  // Direct repository call (no cascade)
    }


    private void updateStockQuantities(Long drugId, int requiredQuantity) {
    	 System.out.println("Updating stock for drugId: " + drugId + " Quantity: " + requiredQuantity);
    	   
        List<Stock> stocks = stockRepository.findByDrugIdOrderByExpiryDateAsc(drugId);
        int remainingQuantity = requiredQuantity;

        for (Stock stock : stocks) {
            if (remainingQuantity <= 0) break;

            if (stock.getAvailableQuantity() <= remainingQuantity) {
                remainingQuantity -= stock.getAvailableQuantity();
                stock.setAvailableQuantity(0);
            } else {
                stock.setAvailableQuantity(stock.getAvailableQuantity() - remainingQuantity);
                remainingQuantity = 0;
            }

            stockRepository.save(stock);
        }
    }

    public List<Map.Entry<Drug, Long>> getMaxPrescribedDrugs(int limit) {
        List<Prescription> allPrescriptions = prescriptionRepository.findAll();
        Map<Drug, Long> drugQuantityMap = allPrescriptions.stream()
                .flatMap(p -> p.getItems().stream())
                .filter(item -> item.getDrug().isActive())
                .collect(Collectors.groupingBy(
                        PrescriptionItem::getDrug,
                        Collectors.summingLong(PrescriptionItem::getQuantity)
                ));

        return drugQuantityMap.entrySet().stream()
                .sorted(Map.Entry.<Drug, Long>comparingByValue().reversed())
                .limit(limit)
                .collect(Collectors.toList());
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
    public List<Map.Entry<Drug, Long>> getMaxPrescribedDrugsForPastYear() {
        LocalDate oneYearAgo = LocalDate.now().minusYears(1);
        List<Prescription> prescriptions = prescriptionRepository.findByPrescriptionDateAfter(oneYearAgo);
        Map<Drug, Long> drugQuantityMap = prescriptions.stream()
                .flatMap(p -> p.getItems().stream())
                .filter(item -> item.getDrug().isActive())
                .collect(Collectors.groupingBy(
                        PrescriptionItem::getDrug,
                        Collectors.summingLong(PrescriptionItem::getQuantity)
                ));

        return drugQuantityMap.entrySet().stream()
                .sorted(Map.Entry.<Drug, Long>comparingByValue().reversed())
                .collect(Collectors.toList());
    }

    public List<Drug> getUnprescribedDrugsForPastYear() {
        LocalDate oneYearAgo = LocalDate.now().minusYears(1);
        List<Prescription> prescriptions = prescriptionRepository.findByPrescriptionDateAfter(oneYearAgo);
        Set<Long> prescribedDrugIds = prescriptions.stream()
                .flatMap(p -> p.getItems().stream())
                .map(item -> item.getDrug().getId())
                .collect(Collectors.toSet());

        List<Drug> allDrugs = drugRepository.findAll();
        return allDrugs.stream()
                .filter(drug -> !prescribedDrugIds.contains(drug.getId()) && drug.isActive())
                .collect(Collectors.toList());
    }
    public List<Drug> getUnprescribedDrugsForPast(int days) {
        LocalDate startDate = LocalDate.now().minusDays(days);
        List<Prescription> prescriptions = prescriptionRepository.findByPrescriptionDateAfter(startDate);
        Set<Long> prescribedDrugIds = prescriptions.stream()
                .flatMap(p -> p.getItems().stream())
                .map(item -> item.getDrug().getId())
                .collect(Collectors.toSet());

        List<Drug> allDrugs = drugRepository.findAll();
        return allDrugs.stream()
                .filter(drug -> !prescribedDrugIds.contains(drug.getId()) && drug.isActive())
                .collect(Collectors.toList());
    }
}

