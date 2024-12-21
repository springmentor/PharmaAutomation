package com.pas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pas.model.Stock;

public interface StockRepository extends JpaRepository<Stock, Integer> {

    @Query("SELECT s FROM Stock s WHERE s.receivedQuantity < s.thresholdQuantity")
    List<Stock> findStocksBelowThreshold();
}
