package com.pas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.pas.model.Drug;

import java.util.List;

public interface DrugRepository extends JpaRepository<Drug, Integer> {

    // Find drugs by status
    List<Drug> findByStatus(String status);
}
