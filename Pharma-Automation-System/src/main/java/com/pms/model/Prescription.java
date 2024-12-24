package com.pms.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "prescriptions")
public class Prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate prescriptionDate;

    @Column(nullable = false)
    private String doctorName;

    @Column(nullable = false)
    private String patientName;

    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnoreProperties("prescription")
    private List<PrescriptionItem> items = new ArrayList<>();
    @Column(name = "is_bill_generated", nullable = false)
    private boolean isBillGenerated = false;

    // Other fields and methods

    public boolean isBillGenerated() {
        return isBillGenerated;
    }

    public void setBillGenerated(boolean billGenerated) {
        this.isBillGenerated = billGenerated;
    }

    public void addItem(PrescriptionItem item) {
        items.add(item);
        item.setPrescription(this);
    }

    public void removeItem(PrescriptionItem item) {
        items.remove(item);
        item.setPrescription(null);
    }
    @Override
    @Transient
    public String toString() {
        return "Prescription{" +
                "id=" + id +
                ", prescriptionDate=" + prescriptionDate +
                ", doctorName='" + doctorName + '\'' +
                ", patientName='" + patientName + '\'' +
                ", isBillGenerated=" + isBillGenerated +
                ", itemsCount=" + (items != null ? items.size() : 0) +
                '}';
    }

}

