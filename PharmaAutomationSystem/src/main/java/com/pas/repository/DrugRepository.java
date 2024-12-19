package com.pas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pas.model.Drug;


@Repository
public interface DrugRepository extends JpaRepository<Drug, Integer> {
	
	List<Drug> findByStatus(String state);
	List<Drug> findByTotalQuantityLessThan(int totalQuantity);

}
