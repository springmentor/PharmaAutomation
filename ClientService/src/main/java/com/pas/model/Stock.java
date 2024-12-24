package com.pas.model;

import java.time.LocalDate;

public class Stock {
    private int stockId;
    private Drug drug;
    private Supplier supplier;
    private int receivedQuantity;
    private LocalDate receivedDate;
    private LocalDate manufacturingDate;
    private LocalDate expiryDate;
    private int thresholdQuantity;
    
    // Constructors
    public Stock() {}
    
    public Stock(Drug drug, Supplier supplier, int receivedQuantity, LocalDate receivedDate, 
                 LocalDate manufacturingDate, LocalDate expiryDate, int thresholdQuantity) {
        this.drug = drug;
        this.supplier = supplier;
        this.receivedQuantity = receivedQuantity;
        this.receivedDate = receivedDate;
        this.manufacturingDate = manufacturingDate;
        this.expiryDate = expiryDate;
        this.thresholdQuantity = thresholdQuantity;
    }
    
    // Getters and setters
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
    
    public int getThresholdQuantity() {
        return thresholdQuantity;
    }
    
    public void setThresholdQuantity(int thresholdQuantity) {
        this.thresholdQuantity = thresholdQuantity;
    }
}

