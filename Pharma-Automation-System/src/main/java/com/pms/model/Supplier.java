package com.pms.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "suppliers")
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Supplier address cannot be empty")
    private String address;

    @NotBlank(message = "Contact number cannot be empty")
    @Pattern(regexp = "^\\d{10}$", message = "Contact number must be a 10-digit number")
    private String contactNumber;

    @NotBlank(message = "Email ID cannot be empty")
    @Email(message = "Email should be valid")
    private String email;

    @ManyToOne
    @JoinColumn(name = "drug_id", nullable = false)
    @JsonIgnoreProperties("supplier")
    private Drug drug;


}
