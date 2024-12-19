package com.pas.model;

import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Drug {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int drugId; 
    @NotBlank(message = "Drug name cannot be blank")
    @Size(min = 2,max = 100, message = "Drug name must not exceed 100 characters")
    private String drugName;

    @NotBlank(message = "Supplier ID cannot be blank")
    @Size(min = 2,max = 50, message = "Supplier ID must not exceed 50 characters")
    private String supplierId;

    @NotBlank(message = "Supplier name cannot be blank")
    @Size(min = 2,max = 100, message = "Supplier name must not exceed 100 characters")
    private String supplierName;

    @Positive(message = "Unit price must be greater than 0")
    private float unitPrice;

    @NotBlank(message = "Status cannot be blank")
    @Pattern(regexp = "^(Active|Banned)$", message = "Status must be Active or Banned")
    private String status;

    @Min(value = 0, message = "Total quantity cannot be negative")
    private int totalQuantity;

    @OneToMany(mappedBy = "drug", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("drug")
    private List<Stock> stocks;
}
