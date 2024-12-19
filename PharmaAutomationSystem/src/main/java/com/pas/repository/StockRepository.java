
package com.pas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pas.model.Stock;

@Repository
public interface StockRepository extends JpaRepository<Stock, Integer> {
    
    List<Stock> findByTotalQuantityLessThan(int thresholdLevel);
}
