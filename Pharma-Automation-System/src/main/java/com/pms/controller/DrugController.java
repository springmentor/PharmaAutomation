package com.pms.controller;

import com.pms.exception.InvalidEntityException;
import com.pms.model.Drug;
import com.pms.service.DrugService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drugs")
public class DrugController {

    @Autowired
    private DrugService drugService;

    @GetMapping
    public ResponseEntity<List<Drug>> getAllDrugs() {
        List<Drug> drugs = drugService.getAllDrugs();
        return ResponseEntity.ok(drugs);
    }

    @PostMapping("/add")
    public ResponseEntity<Drug> addDrug(@RequestBody Drug drug)throws InvalidEntityException {
        Drug savedDrug = drugService.saveDrug(drug);
        return ResponseEntity.ok(savedDrug);
    }

    @PutMapping("/update")
    public ResponseEntity<Drug> updateDrug(@RequestBody Drug drug)throws InvalidEntityException {
        Drug updatedDrug = drugService.updateDrug(drug);
        return ResponseEntity.ok(updatedDrug);
    }

    @PostMapping("/deactivate")
    public ResponseEntity<Void> deactivateDrug(@RequestParam Long id)throws InvalidEntityException {
        drugService.deactivateDrug(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> softDeleteDrug(@RequestParam Long id) throws InvalidEntityException {
        drugService.softDeleteDrug(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Drug>> filterDrugs(@RequestParam(required = false) Boolean banned, @RequestParam(required = false) Boolean active) {
        List<Drug> drugs;
        if (banned != null && banned) {
            drugs = drugService.getBannedDrugs();
        } else if (active != null && !active) {
            drugs = drugService.getDeactivatedDrugs();
        } else {
            drugs = drugService.getAllDrugs();
        }
        return ResponseEntity.ok(drugs);
    }
    @GetMapping("/search")
    public ResponseEntity<List<Drug>> searchDrugs(@RequestParam(value = "query", required = false) String query) {
        if (query != null && !query.trim().isEmpty()) {
            List<Drug> drugs = drugService.searchDrugs(query);
            return ResponseEntity.ok(drugs);
        } else {
            return ResponseEntity.noContent().build(); // Return no content if query is empty
        }
    }
}

