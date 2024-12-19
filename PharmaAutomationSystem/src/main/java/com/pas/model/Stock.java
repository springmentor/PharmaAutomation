package com.pas.model;

import java.time.LocalDate;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.persistence.ManyToOne;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@Data
@Entity
public class Stock {
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private int batchId;
	    
	    @NotNull(message = "Manufacturing date cannot be null")
	    @PastOrPresent(message = "Manufacturing date must be today or in the past")
	    private LocalDate manDate;
	    
	    @NotNull(message = "Expiry date cannot be null")
	    @FutureOrPresent(message = "Expiry date must be today or in the future")
	    private LocalDate expDate;
	    
	    @Min(value = 1, message = "Total quantity must be at least 1")
	    private int totalQuantity;
	    
	    @Positive(message = "Threshold level must be greater than 0")
	    private int thresholdLevel;
	    @ManyToOne
	    @JoinColumn(name="drugId")
	    @JsonIgnoreProperties("stocks")
	    private Drug drug;
		
	}


