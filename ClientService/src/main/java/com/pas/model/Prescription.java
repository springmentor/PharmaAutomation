package com.pas.model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Prescription {
    private int prescriptionId;
    private String patientName;
    private String doctorName;
    private LocalDate prescribedDate;
    private Map<Integer, Integer> prescribedDrugs;
    private Billing billing;

    public Prescription() {
        this.prescribedDrugs = new HashMap<>();
    }

    public Prescription(String patientName, String doctorName, LocalDate prescribedDate,
                        Map<Integer, Integer> prescribedDrugs, Billing billing) {
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.prescribedDate = prescribedDate;
        this.prescribedDrugs = prescribedDrugs;
        this.billing = billing;
    }

    public void addOrUpdatePrescribedDrug(Integer drugId, Integer quantity) {
        this.prescribedDrugs.put(drugId, quantity);
    }

    public void removePrescribedDrug(Integer drugId) {
        this.prescribedDrugs.remove(drugId);
    }

    // Getters and setters
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

    public Map<Integer, Integer> getPrescribedDrugs() {
        return prescribedDrugs;
    }

    public void setPrescribedDrugs(Map<Integer, Integer> prescribedDrugs) {
        this.prescribedDrugs = prescribedDrugs;
    }

    public Billing getBilling() {
        return billing;
    }

    public void setBilling(Billing billing) {
        this.billing = billing;
    }
}

