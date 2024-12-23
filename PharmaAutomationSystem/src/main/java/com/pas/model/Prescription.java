package com.pas.model;

import java.time.LocalDate;
import java.util.List;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int prescriptionId;

    @NotNull(message = "Patient name cannot be null.")
    private String patientName;

    @NotNull(message = "Doctor name cannot be null.")
    private String doctorName;

    @NotNull(message = "Prescribed date cannot be null.")
    private LocalDate prescribedDate;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id")
    private List<Stock> stocks;

    // Default constructor
    public Prescription() {}

    // Parameterized constructor
    public Prescription(String patientName, String doctorName, LocalDate prescribedDate, List<Stock> stocks) {
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.prescribedDate = prescribedDate;
        this.stocks = stocks;
    }

    // Getters and Setters
    public int getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(int prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public LocalDate getPrescribedDate() {
        return prescribedDate;
    }

    public void setPrescribedDate(LocalDate prescribedDate) {
        this.prescribedDate = prescribedDate;
    }

    public List<Stock> getStocks() {
        return stocks;
    }

    public void setStocks(List<Stock> stocks) {
        this.stocks = stocks;
    }
}
