package com.pms.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "drugs")
public class Drug {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Drug name cannot be empty")
    private String name;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    @NotNull(message = "Price cannot be null")
    @Positive(message = "Price must be greater than zero")
    @Column(nullable = false)
    private Double price;

    @NotBlank(message = "Name cannot be blank")
    private String supplierName;

    @NotNull(message = "Total quantity cannot be null")
    @Positive(message = "Total quantity must be greater than zero")
    @Column(name = "total_quantity", nullable = false)
    private Integer totalQuantity = 0;

    private boolean active = true;
    private boolean banned = false;
    private String bannedReason;

    @OneToMany(mappedBy = "drug", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("drug")
    private List<Stock> stock;

    @OneToMany(mappedBy = "drug", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("drug")
    private List<Supplier> suppliers;
}