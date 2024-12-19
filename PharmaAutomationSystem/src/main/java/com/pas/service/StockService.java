
package com.pas.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.pas.exception.InvalidEntityException;
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
    
    @Autowired
    private EmailService emailService;

   

  
    public Stock addStock(Stock stock,int drugid)throws InvalidEntityException  {
    	Optional<Drug> d1 =drugRepository.findById(drugid);
    	 if (!d1.isPresent()) {
 	        throw new InvalidEntityException("Drug with ID " + drugid + " does not exist.");
 	    }
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

    

    public List<Stock> getStocksBelowThreshold() {
        return stockRepository.findByTotalQuantityLessThanThresholdLevel();
    }
  

    public String sendReorderNotification() {
        List<Stock> stocks = stockRepository.findAll(); 
        for (Stock stock : stocks) {
            if (stock.getTotalQuantity() < stock.getThresholdLevel()) {
            	Drug drug = stock.getDrug();
                String subject = "Reorder Notification for Drug: " + drug.getDrugName();
                String body ="The stock for drug '" + drug.getDrugName() + "' has fallen below the threshold level. "
                		     + "threshold level: " + stock.getThresholdLevel() +  ", Current Quantity: " + stock.getTotalQuantity() + ". Please reorder.";
                String supplierEmail = "muthukumaran000027@gmail.com"; 
                emailService.sendEmail(supplierEmail, subject , body);
            }
        }
        return "Mail sent";
    }
    
    @Scheduled(cron = "0 0 0/1 * * ?")
     public void scheduledRecorderNotifications() {
        String result = sendReorderNotification();
        System.out.println(result);
            
        }
    

}
