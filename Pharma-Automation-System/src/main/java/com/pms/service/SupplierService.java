package com.pms.service;

import com.pms.model.Supplier;
import com.pms.model.Drug;
import com.pms.repository.SupplierRepository;
import com.pms.repository.DrugRepository;
import com.pms.exception.InvalidEntityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private DrugRepository drugRepository;

    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    @Transactional
    public Supplier addSupplier(Supplier supplier)throws InvalidEntityException {
        if (supplier.getDrug() == null || supplier.getDrug().getId() == null) {
            throw new InvalidEntityException("Drug ID must be provided.");
        }

        Drug drug = drugRepository.findById(supplier.getDrug().getId())
                .orElseThrow(() -> new InvalidEntityException("Drug not found with id: " + supplier.getDrug().getId()));

        supplier.setDrug(drug);
        return supplierRepository.save(supplier);
    }

    @Transactional
    public Supplier updateSupplier(Long id, Supplier supplierDetails)throws InvalidEntityException {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new InvalidEntityException("Supplier not found with id: " + id));

        supplier.setAddress(supplierDetails.getAddress());
        supplier.setContactNumber(supplierDetails.getContactNumber());
        supplier.setEmail(supplierDetails.getEmail());

        return supplierRepository.save(supplier);
    }

    public void deleteSupplier(Long id)throws InvalidEntityException {
        if (!supplierRepository.existsById(id)) {
            throw new InvalidEntityException("Supplier not found with id: " + id);
        }
        supplierRepository.deleteById(id);
    }
}
