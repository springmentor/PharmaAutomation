package com.pms.repository;

import com.pms.model.Drug;
import com.pms.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    List<Prescription> findByIsBillGenerated(Boolean isBillGenerated);
    List<Prescription> findByPatientNameContainingIgnoreCase(String patientName);
    
    List<Prescription> findByPrescriptionDateAfter(LocalDate date);
  
    @Query("SELECT pi.drug, SUM(pi.quantity) as totalQuantity " +
            "FROM Prescription p JOIN p.items pi " +
            "WHERE p.prescriptionDate >= :startDate " +
            "GROUP BY pi.drug " +
            "ORDER BY totalQuantity DESC")
     List<Object[]> findMostPrescribedDrugsSince(@Param("startDate") LocalDate startDate);

     @Query("SELECT DISTINCT pi.drug FROM Prescription p JOIN p.items pi WHERE p.prescriptionDate >= :startDate")
     List<Drug> findPrescribedDrugsSince(@Param("startDate") LocalDate startDate);
     @Modifying
     @Query("UPDATE Prescription p SET p.isBillGenerated = :status WHERE p.id = :prescriptionId")
     int updateBillGeneratedStatus(@Param("prescriptionId") Long prescriptionId, @Param("status") boolean status);
 }

  





