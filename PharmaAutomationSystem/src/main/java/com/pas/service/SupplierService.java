package com.pas.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pas.model.Supplier;
import com.pas.repository.SupplierRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private SupplierRepository supplierService;
    
    // Get all suppliers
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    // Get a specific supplier by ID
    public Supplier getSupplierById(int supplierId) {
        return supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Supplier not found"));
    }

    // Add a new supplier
    public Supplier addSupplier(Supplier supplier) {
        // Perform any additional validations if necessary
        return supplierRepository.save(supplier);
    }

    // Update an existing supplier by ID
    public Supplier updateSupplier(int supplierId, Supplier updatedSupplier) {
        Supplier existingSupplier = supplierRepository.findById(supplierId).orElse(null);
        if (existingSupplier != null) {
            // Perform the update
            existingSupplier.setName(updatedSupplier.getName());
            existingSupplier.setEmail(updatedSupplier.getEmail());
            existingSupplier.setPhone(updatedSupplier.getPhone());
            existingSupplier.setAddress(updatedSupplier.getAddress());
            return supplierRepository.save(existingSupplier);  // Save the updated supplier
        }
        return null;
    }


    // Delete a supplier by ID
    public void deleteSupplier(int supplierId) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Supplier not found"));

        supplierRepository.delete(supplier);
    }
}
