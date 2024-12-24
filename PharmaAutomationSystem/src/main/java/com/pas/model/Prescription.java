package com.pas.model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonManagedReference;

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

    @ElementCollection
    @CollectionTable(name = "prescription_drugs",
        joinColumns = @JoinColumn(name = "prescription_id"))
    @MapKeyColumn(name = "drug_id")
    @Column(name = "prescribed_quantity")
    private Map<Integer, Integer> prescribedDrugs = new HashMap<>();

    @OneToOne(mappedBy = "prescription", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Billing billing;

    // Constructors, getters, and setters

    public Prescription(){}
    public Prescription(int prescriptionId, @NotNull(message = "Patient name cannot be null.") String patientName,
			@NotNull(message = "Doctor name cannot be null.") String doctorName,
			@NotNull(message = "Prescribed date cannot be null.") LocalDate prescribedDate,
			Map<Integer, Integer> prescribedDrugs, Billing billing) {
		super();
		this.prescriptionId = prescriptionId;
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
	        if (billing != null) {
	            billing.setPrescription(this);
	        }
	}
}