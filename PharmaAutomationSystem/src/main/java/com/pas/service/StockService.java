
package com.pas.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pas.model.Drug;
import com.pas.model.Stock;
import com.pas.repository.DrugRepository;
import com.pas.repository.StockRepository;

@Service
public class StockService {

    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private DrugRepository drugRepository;

   

  
    public Stock addStock(Stock stock,int drugid) {
    	Optional<Drug> d1 =drugRepository.findById(drugid);
		Drug drug = d1.get();
		stock.setDrug(drug);
        return stockRepository.save(stock);
    }

   
    public boolean deleteStock(int batchId) {
        if (stockRepository.existsById(batchId)) {
            stockRepository.deleteById(batchId);
            return true;
        }
        return false;
    }

    
    public List<Stock> viewDrugsBelowThreshold(int threshold) {
        return stockRepository.findByTotalQuantityLessThan(threshold);
    }
}
