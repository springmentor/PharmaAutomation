package com.pas.model;

import java.time.LocalDate;

public class Billing {
    private int billingId;
    private LocalDate billingDate;
    private double totalAmount;
    private double discount;
    private double finalAmount;
    private Prescription prescription;

    // Constructors
    public Billing() {}

    public Billing(LocalDate billingDate, double totalAmount, double discount, double finalAmount, Prescription prescription) {
        this.billingDate = billingDate;
        this.totalAmount = totalAmount;
        this.discount = discount;
        this.finalAmount = finalAmount;
        this.prescription = prescription;
    }

    // Getters and setters
    public int getBillingId() {
        return billingId;
    }

    public void setBillingId(int billingId) {
        this.billingId = billingId;
    }

    public LocalDate getBillingDate() {
        return billingDate;
    }

    public void setBillingDate(LocalDate billingDate) {
        this.billingDate = billingDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(double finalAmount) {
        this.finalAmount = finalAmount;
    }

    public Prescription getPrescription() {
        return prescription;
    }

    public void setPrescription(Prescription prescription) {
        this.prescription = prescription;
    }
}

