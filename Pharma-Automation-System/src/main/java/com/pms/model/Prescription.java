package com.pms.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
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

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "prescription_id")
    private List<PrescriptionItem> items;
}

