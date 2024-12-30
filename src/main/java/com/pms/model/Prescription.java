package com.pms.model;


import java.time.LocalDate;
import java.util.List;

public class Prescription {
    private Long id;
    private LocalDate prescriptionDate;
    private String doctorName;
    private String patientName;
    private boolean isBillGenerated;  // Add this field

    // Getter and Setter for isBillGenerated
    public boolean isBillGenerated() {
        return isBillGenerated;
    }

    public void setBillGenerated(boolean billGenerated) {
        isBillGenerated = billGenerated;
    }

    public List<PrescriptionItem> getItems() {
        return items;
    }

    public void setItems(List<PrescriptionItem> items) {
        this.items = items;
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

    public LocalDate getPrescriptionDate() {
        return prescriptionDate;
    }

    public void setPrescriptionDate(LocalDate prescriptionDate) {
        this.prescriptionDate = prescriptionDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private List<PrescriptionItem> items;
}

