package com.pas.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pas.model.Supplier;
import com.pas.service.SupplierService;

@RestController
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    // Get all suppliers
    @GetMapping("/getAllSuppliers")
    public List<Supplier> getAllSuppliers() {
        return supplierService.getAllSuppliers();
    }

    // Get a specific supplier by ID
    @GetMapping("/getSupplier/{supplierId}")
    public ResponseEntity<Supplier> getSupplierById(@PathVariable int supplierId) {
        Supplier supplier = supplierService.getSupplierById(supplierId);
        return supplier != null ? ResponseEntity.ok(supplier) : ResponseEntity.notFound().build();
    }

    // Add a new supplier
    @PostMapping("/addSupplier")
    public Supplier addSupplier(@RequestBody Supplier supplier) {
        return supplierService.addSupplier(supplier);
    }

    @PutMapping("/updateSupplier/{supplierId}")
    public ResponseEntity<Supplier> updateSupplier(@PathVariable int supplierId, @RequestBody Supplier supplierDetails) {
        // Fetch the existing supplier from the database
        Supplier existingSupplier = supplierService.getSupplierById(supplierId);
        
        if (existingSupplier != null) {
            // Update the supplier fields
            existingSupplier.setName(supplierDetails.getName());
            existingSupplier.setEmail(supplierDetails.getEmail());
            existingSupplier.setPhone(supplierDetails.getPhone());
            existingSupplier.setAddress(supplierDetails.getAddress());
            
            // Save the updated supplier
            Supplier updatedSupplier = supplierService.updateSupplier(supplierId, existingSupplier);
            return ResponseEntity.ok(updatedSupplier);
        }
        return ResponseEntity.notFound().build();
    }


    // Delete a supplier by ID
    @DeleteMapping("/deleteSupplier/{supplierId}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable int supplierId) {
        Supplier supplier = supplierService.getSupplierById(supplierId);
        if (supplier != null) {
            supplierService.deleteSupplier(supplierId);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
