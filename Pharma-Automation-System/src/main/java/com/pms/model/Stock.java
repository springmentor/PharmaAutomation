package com.pms.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Entity
@Table(name="stocks")
@Data
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "drug_id", nullable = false)
    @JsonIgnoreProperties("stock")
    private Drug drug;

    @NotNull(message = "Total quantity cannot be null")
    @Positive(message = "Total quantity must be greater than zero")
    private int quantity;
    
    @NotNull(message = "Total quantity cannot be null")
    @Positive(message = "Total quantity must be greater than zero")
    private int availableQuantity;
    
    @NotNull(message = "Expiry date cannot be null")
    @FutureOrPresent(message = "Expiry date must be in the Future or Present.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;

    @NotNull(message = "Manufacture date cannot be null")
    @PastOrPresent(message = "Manufacture date must be in the Past or Present.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate manufacturingDate;

    @NotNull(message = "Threshold value cannot be null")
    @Positive(message = "Threshold value must be greater than zero")
    private int threshold;
    
    @Override
    public String toString() {
        return "Stock{" +
                "id=" + id +
                ", drugId=" + (drug != null ? drug.getId() : null) +
                ", availableQuantity=" + availableQuantity +
                ", expiryDate=" + expiryDate +
                '}';
    }

	public String getBatchNo() {
		// TODO Auto-generated method stub
		return null;
	}


}
