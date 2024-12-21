package com.pms.repository;

import com.pms.model.Bill;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
        @Query("SELECT b FROM Bill b WHERE b.prescription.id = :prescriptionId")
        Optional<Bill> findByPrescriptionId(@Param("prescriptionId") Long prescriptionId);
    @Transactional
    void deleteByPrescriptionId(Long prescriptionId);
}
