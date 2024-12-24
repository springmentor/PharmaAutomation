package com.pms.repository;

import com.pms.model.Stock;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StockRepository extends JpaRepository<Stock, Long> {

    @Query("SELECT s FROM Stock s WHERE s.expiryDate < :currentDate")
    List<Stock> findStocksPastExpiryDate(@Param("currentDate") LocalDate currentDate);
    @Query("SELECT s FROM Stock s WHERE s.quantity < s.threshold")
    List<Stock> findStocksBelowThreshold();
    List<Stock> findByDrugIdOrderByExpiryDateAsc(Long id);
    @Query("SELECT SUM(s.quantity) FROM Stock s WHERE s.drug.id = :drugId")
    Integer getTotalQuantityByDrugId(@Param("drugId") Long drugId);
}


