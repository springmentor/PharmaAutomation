package com.pms.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "prescription_items")
public class PrescriptionItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "drug_id", nullable = false)
    private Drug drug;

    @Column(nullable = false)
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;
    
    @Override
    public String toString() {
        return "PrescriptionItem{" +
                "id=" + id +
                ", drugId=" + (drug != null ? drug.getId() : null) +
                ", quantity=" + quantity +
                ", prescriptionId=" + (prescription != null ? prescription.getId() : null) +
                '}';
    }

    public void setPrescription(Prescription prescription) {
        this.prescription = prescription;
    }

    public Prescription getPrescription() {
        return this.prescription;
    }
}
