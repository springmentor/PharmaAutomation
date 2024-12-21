package com.pms.repository;

import com.pms.model.Drug;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DrugRepository extends JpaRepository<Drug, Long> {
    List<Drug> findByNameStartingWithIgnoreCase(String prefix);
    List<Drug> findByBannedTrue();
    List<Drug> findByActiveFalse();
    @Query("SELECT d FROM Drug d WHERE d NOT IN :prescribedDrugs")
    List<Drug> findUnprescribedDrugs(@Param("prescribedDrugs") List<Drug> prescribedDrugs);
}