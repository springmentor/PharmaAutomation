package com.pas.model;

import java.time.LocalDate;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class Billing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int billingId;

    @NotNull(message = "Billing date cannot be null.")
    private LocalDate billingDate;

    @NotNull(message = "Total amount cannot be null.")
    private double totalAmount;

    @NotNull(message = "Discount cannot be null.")
    private double discount;

    @NotNull(message = "Final amount cannot be null.")
    private double finalAmount;

    @OneToOne
    @JoinColumn(name = "prescription_id")
    private Prescription prescription;

    // Default constructor
    public Billing() {}

    // Parameterized constructor
    public Billing(LocalDate billingDate, double totalAmount, double discount, double finalAmount, Prescription prescription) {
        this.billingDate = billingDate;
        this.totalAmount = totalAmount;
        this.discount = discount;
        this.finalAmount = finalAmount;
        this.prescription = prescription;
    }

    // Getters and Setters
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
