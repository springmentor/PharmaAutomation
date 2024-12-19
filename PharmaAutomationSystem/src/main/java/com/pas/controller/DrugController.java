package com.pas.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.pas.model.Drug;
import com.pas.service.DrugService;


@RestController
public class DrugController {
    @Autowired
	private DrugService drugService;
    
    
    @PostMapping("/addDrug")
    public ResponseEntity<Drug> addDrug(@RequestBody Drug drug) {
        
        return new ResponseEntity<>(drugService.addDrug(drug), HttpStatus.OK);
    }
    @GetMapping("/viewDrugsByStatus/{state}")
    public  ResponseEntity<List<Drug>>findDrugByStatus(@PathVariable String state) {
        List<Drug> drugs = drugService.findDrugByStatus(state);
        if (drugs.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(drugs, HttpStatus.OK);
    }
    @DeleteMapping("/deleteDrug/{id}")
    public ResponseEntity<Void> deleteDrug(@PathVariable int id) {
        boolean deleted = drugService.deleteDrug(id);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); 
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND); 
    }
    @PutMapping("/updateDrugStatus/{id}/{status}")
    public ResponseEntity<Drug> updateDrugStatus(@PathVariable int id, @PathVariable String status) {
        Drug updatedstatus = drugService.updateDrugStatus(id, status);
        if (updatedstatus != null) {
            return new ResponseEntity<>(updatedstatus, HttpStatus.OK); 
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND); 
    }
//    @GetMapping("/viewDrugsBelowThreshold/{threshold}")
//    public ResponseEntity<List<Drug>> findDrugsBelowThreshold(@PathVariable int threshold) {
//        List<Drug> drugs = drugService.findDrugsBelowThreshold(threshold);
//        if (drugs.isEmpty()) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//        return new ResponseEntity<>(drugs, HttpStatus.OK);
//    }

}
