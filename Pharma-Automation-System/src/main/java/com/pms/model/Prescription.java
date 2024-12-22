package com.pms.model;

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

    private boolean isBillGenerated;

    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<PrescriptionItem> items = new ArrayList<>();

    public void setIsBillGenerated(boolean isBillGenerated) {
        this.isBillGenerated = isBillGenerated;
    }

    public boolean getIsBillGenerated() {
        return this.isBillGenerated;
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
