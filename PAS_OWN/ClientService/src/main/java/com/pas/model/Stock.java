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
    private double unitPrice;
    private int thresholdQuantity;
    
    public int getThresholdQuantity() {
        return thresholdQuantity;
    }

    public void setThresholdQuantity(int thresholdQuantity) {
        this.thresholdQuantity = thresholdQuantity;
    }

    public Stock() {}

    public Stock(Drug drug, Supplier supplier, int receivedQuantity, LocalDate receivedDate, 
                 LocalDate manufacturingDate, LocalDate expiryDate, double unitPrice) {
        this.drug = drug;
        this.supplier = supplier;
        this.receivedQuantity = receivedQuantity;
        this.receivedDate = receivedDate;
        this.manufacturingDate = manufacturingDate;
        this.expiryDate = expiryDate;
        this.unitPrice = unitPrice;
        updateDrugQuantity(receivedQuantity);
    }

    // Helper method to update drug quantity
    private void updateDrugQuantity(int quantity) {
        if (this.drug != null) {
            this.drug.setTotalQuantity(this.drug.getTotalQuantity() + quantity);
        }
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
            updateDrugQuantity(this.receivedQuantity); // Ensure drug quantity is updated
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
        updateDrugQuantity(receivedQuantity); // Update drug quantity on change
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
}
