package com.pms.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "bills")
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "prescription_id", nullable = false)
    @JsonIgnoreProperties("prescription")
    private Prescription prescription;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private LocalDate billDate;

    @Column(nullable = false)
    private double discountPercentage;

    @Column(nullable = false)
    private Double discountedAmount;

}
