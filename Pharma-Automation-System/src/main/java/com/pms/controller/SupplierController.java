package com.pms.controller;

import com.pms.exception.InvalidEntityException;
import com.pms.model.Drug;
import com.pms.model.Supplier;
import com.pms.repository.SupplierRepository;
import com.pms.service.EmailService;
import com.pms.service.SupplierService;
import com.pms.repository.DrugRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private DrugRepository drugRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private EmailService emailService;

    @GetMapping
    public List<Supplier> getAllSuppliers() {
        return supplierService.getAllSuppliers();
    }

    @PostMapping("/add")
    public Supplier addSupplier(@RequestBody Supplier supplier)throws InvalidEntityException {
        return supplierService.addSupplier(supplier);
    }

    @PutMapping("/update/{id}")
    public Supplier updateSupplier(@PathVariable Long id, @RequestBody Supplier supplier)throws InvalidEntityException {
        return supplierService.updateSupplier(id, supplier);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteSupplier(@PathVariable Long id)throws InvalidEntityException {
        supplierService.deleteSupplier(id);
    }

    @GetMapping("/drugs")
    public List<Drug> getAllDrugs()throws InvalidEntityException {
        return drugRepository.findAll();
    }
    @PostMapping("/send-email/{supplierId}")
    public String sendEmailToSupplier(@PathVariable Long supplierId) {
        Optional<Supplier> supplierOptional = supplierRepository.findById(supplierId);

        if (supplierOptional.isPresent()) {
            Supplier supplier = supplierOptional.get();
            Drug drug = supplier.getDrug(); // Get the associated drug

            if (drug == null) {
                return "Drug not found for the supplier.";
            }

            // Retrieve details
            String supplierName = drug.getSupplierName(); // Assuming 'address' holds the name; adjust if incorrect
            String supplierEmail = supplier.getEmail();
            String drugName = drug.getName();

            // Email details
            String subject = "Reorder Notification for Drug: " + drugName;
            String body = "Dear " + supplierName + ",\n\n"
                    + "This is a notification regarding a reorder request for the drug \"" + drugName + "\". "
                    + "Please ensure timely delivery to avoid stock shortages.\n\n"
                    + "Regards,\nPharma Management Team";

            // Send email
            emailService.sendEmail(supplierEmail, subject, body);

            return "Email sent to supplier: " + supplierName + " for drug: " + drugName;
        } else {
            return "Supplier not found with ID: " + supplierId;
        }
    }



}
