package com.pas.model;

public class Drug {
    private int drugId;
    private String drugName;
    private String status;
    private int totalQuantity;
    private double unitPrice;
    
    // Getters and setters
    public int getDrugId() {
        return drugId;
    }
    
    public void setDrugId(int drugId) {
        this.drugId = drugId;
    }
    
    public String getDrugName() {
        return drugName;
    }
    
    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public int getTotalQuantity() {
        return totalQuantity;
    }
    
    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }
    
    public double getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
}

