package com.pas.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pas.model.Drug;
import com.pas.repository.DrugRepository;


@Service
public class DrugService {
	@Autowired
	private DrugRepository drugRepository;

	public Drug addDrug(Drug drug) {
        return drugRepository.save(drug);
    }
	public List<Drug> findDrugByStatus(String state) {
        return drugRepository.findByStatus(state);
    }
	public boolean deleteDrug(int id) {
	    if (drugRepository.existsById(id)) {
	        drugRepository.deleteById(id);
	        return true;
	    }
	    return false;
	}
	public Drug updateDrugStatus(int id, String status) {
	    Optional<Drug> optionalDrug = drugRepository.findById(id);
	    if (optionalDrug.isPresent()) {
	        Drug drug = optionalDrug.get();
	        drug.setStatus(status);
	        return drugRepository.save(drug); 
	    }
	    return null;
	}
	public List<Drug> findDrugsBelowThreshold(int threshold) {
	    return drugRepository.findByTotalQuantityLessThan(threshold);
	}

}
