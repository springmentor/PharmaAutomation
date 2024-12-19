package com.pas.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Entity
public class Prescription {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private int prescriptionId;
private String doctorName;
private String patientName;
private LocalDate prescriptionDate;
private String drugName;
private int drugId;
private int totalAmount;

}
