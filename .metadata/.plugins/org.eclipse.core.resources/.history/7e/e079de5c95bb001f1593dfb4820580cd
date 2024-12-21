package com.pas.model;

import java.time.LocalDate;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;

@Entity
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int stockId;

    @ManyToOne
    @JoinColumn(name = "drug_id", nullable = false)
    private Drug drug;

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    private int receivedQuantity;

    private LocalDate receivedDate;

    private LocalDate manufacturingDate;

    @jakarta.validation.constraints.NotNull(message = "Expiry date cannot be null.")
    @FutureOrPresent(message = "Expiry date must be in the future or today.")
    private LocalDate expiryDate;

    private double unitPrice;

    private int thresholdQuantity;

    // Default constructor
    public Stock() {}

    // Parameterized constructor
    public Stock(Drug drug, Supplier supplier, int receivedQuantity, LocalDate receivedDate, 
                 LocalDate manufacturingDate, LocalDate expiryDate, double unitPrice) {
        this.drug = drug;
        this.supplier = supplier;
        this.receivedQuantity = receivedQuantity;
        this.receivedDate = receivedDate;
        this.manufacturingDate = manufacturingDate;
        this.expiryDate = expiryDate;
        this.unitPrice = unitPrice;
    }

    // Getters and Setters
    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    public Drug getDrug() {
        return drug;
    }

    public void setDrug(Drug drug) {
        this.drug = drug;
        if (drug != null) {
            drug.setTotalQuantity(drug.getTotalQuantity() + this.receivedQuantity);
        }
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public int getReceivedQuantity() {
        return receivedQuantity;
    }

    public void setReceivedQuantity(int receivedQuantity) {
        this.receivedQuantity = receivedQuantity;
        if (this.drug != null) {
            this.drug.setTotalQuantity(this.drug.getTotalQuantity() + receivedQuantity);
        }
    }

    public LocalDate getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(LocalDate receivedDate) {
        this.receivedDate = receivedDate;
    }

    public LocalDate getManufacturingDate() {
        return manufacturingDate;
    }

    public void setManufacturingDate(LocalDate manufacturingDate) {
        this.manufacturingDate = manufacturingDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getThresholdQuantity() {
        return thresholdQuantity;
    }

    public void setThresholdQuantity(int thresholdQuantity) {
        this.thresholdQuantity = thresholdQuantity;
    }
}
