package com.pms.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

import org.springframework.stereotype.Component;
@Component
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
    @JsonIgnore
    private List<Stock> stock;

    @OneToMany(mappedBy = "drug", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Supplier> suppliers;

    @Override
    public String toString() {
        return "Drug{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", supplierName='" + supplierName + '\'' +
                ", totalQuantity=" + totalQuantity +
                ", active=" + active +
                ", banned=" + banned +
                ", bannedReason='" + bannedReason + '\'' +
                ", stockCount=" + (stock != null ? stock.size() : 0) +
                ", suppliersCount=" + (suppliers != null ? suppliers.size() : 0) +
                '}';
    }
}

