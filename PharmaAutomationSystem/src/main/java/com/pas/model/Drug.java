package com.pas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Drug {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int drugId;

    @NotBlank(message = "Drug name cannot be blank")
    private String drugName;

    private String status;

    @Min(value = 0, message = "Total quantity cannot be negative")
    private int totalQuantity;

    @Min(value = 0, message = "Unit price cannot be negative")
    private double unitPrice;

    // Constructors
    public Drug() {}

    public Drug(String drugName, String status, int totalQuantity, double unitPrice) {
        this.drugName = drugName;
        this.status = status;
        this.totalQuantity = totalQuantity;
        this.unitPrice = unitPrice;
    }

    // Getters and Setters
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

