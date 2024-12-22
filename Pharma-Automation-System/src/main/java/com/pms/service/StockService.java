package com.pms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pms.model.Drug;
import com.pms.model.Stock;
import com.pms.repository.DrugRepository;
import com.pms.repository.StockRepository;

import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StockService {

    private final StockRepository stockRepository;
    private final DrugRepository drugRepository;
    private final SmsService smsService;
    private final EmailService emailService;
    
    @Autowired
    private Drug drug;

    @Autowired
    public StockService(StockRepository stockRepository, DrugRepository drugRepository, SmsService smsService, EmailService emailService) {
        this.stockRepository = stockRepository;
        this.drugRepository = drugRepository;
        this.smsService = smsService;
        this.emailService = emailService;
    }

    @Transactional
    public Stock addStock(Stock stock) {
        if (stock.getDrug() == null || stock.getDrug().getId() == null) {
            throw new IllegalArgumentException("Drug ID must be provided");
        }

        Drug drug = drugRepository.findById(stock.getDrug().getId())
            .orElseThrow(() -> new EntityNotFoundException("Drug not found with id: " + stock.getDrug().getId()));

        if (!drug.isActive()) {
            throw new IllegalStateException("Cannot add stock for inactive drug: " + drug.getName());
        }

        stock.setDrug(drug);
        stock.setAvailableQuantity(stock.getQuantity()); // Set initial available quantity
        Stock savedStock = stockRepository.save(stock);

        drug.setTotalQuantity(drug.getTotalQuantity() + stock.getQuantity());
        drugRepository.save(drug);

        return savedStock;
    }

    @Transactional
    public Stock updateStock(Long id, Stock stockDetails) {
        Stock stock = stockRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Stock not found with id: " + id));

        if (!stock.getDrug().isActive()) {
            throw new IllegalStateException("Cannot update stock for inactive drug: " + stock.getDrug().getName());
        }
        if (!drug.isActive()) {
            throw new IllegalStateException("Cannot add stock for deactivated drug: " + drug.getName());
        }

        int quantityDifference = stockDetails.getQuantity() - stock.getQuantity();

        stock.setQuantity(stockDetails.getQuantity());
        stock.setAvailableQuantity(stock.getAvailableQuantity() + quantityDifference);
        stock.setExpiryDate(stockDetails.getExpiryDate());
        stock.setManufacturingDate(stockDetails.getManufacturingDate());
        stock.setThreshold(stockDetails.getThreshold());

        Drug drug = stock.getDrug();
        drug.setTotalQuantity(drug.getTotalQuantity() + quantityDifference);
        drugRepository.save(drug);

        return stockRepository.save(stock);
    }
      public List<Stock> getAllStocks() {
        return stockRepository.findAll().stream()
            .filter(stock -> stock.getDrug().isActive())
            .collect(Collectors.toList());
    }
    @Transactional
    public void deleteStock(Long id) {
        Stock stock = stockRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Stock not found with id: " + id));
        
        Drug drug = stock.getDrug();
  
        drugRepository.save(drug);
        
        stockRepository.delete(stock);
    }
    @Transactional
    public void updateStockQuantities(Long drugId, int quantityToReduce) {
        List<Stock> stocks = stockRepository.findByDrugIdOrderByExpiryDateAsc(drugId);
        int remainingQuantity = quantityToReduce;

        for (Stock stock : stocks) {
            if (remainingQuantity <= 0) {
                break;
            }

            int availableQuantity = stock.getAvailableQuantity();
            int quantityToReduceFromStock = Math.min(availableQuantity, remainingQuantity);

            stock.setAvailableQuantity(availableQuantity - quantityToReduceFromStock);
            stockRepository.save(stock);

            remainingQuantity -= quantityToReduceFromStock;
        }

        if (remainingQuantity > 0) {
            throw new IllegalStateException("Insufficient stock to fulfill the prescription");
        }

//        // Update the total quantity for the drug
//        Drug drug = drugRepository.findById(drugId)
//                .orElseThrow(() -> new EntityNotFoundException("Drug not found with id: " + drugId));
//        drug.setTotalQuantity(drug.getTotalQuantity() - quantityToReduce);
//        drugRepository.save(drug);
    }


    public List<Stock> getStocksBelowThreshold() {
        return stockRepository.findStocksBelowThreshold();
    }

    public String sendReorderNotification() {
        List<Stock> stocks = stockRepository.findAll();

        for (Stock stock : stocks) {
            if (stock.getQuantity() < stock.getThreshold()) {
                Drug drug = stock.getDrug();
                String message = "Reorder Notification: The stock for drug " 
                                 + drug.getName() + " (Batch ID: " + stock.getBatchNo()
                                 + ") is below the threshold. Please reorder.";

                // Send email
                emailService.sendEmail("rajapriyanka11.01.2004@gmail.com", 
                                       "Reorder Notification for Drug: " + drug.getName(), 
                                       message);

                // Send SMS (example recipient number)
                smsService.sendSms("+919384145111", message);
            }
        }
        return "Notifications sent.";
    }


//     @Scheduled(cron = "*/5 * * * * ?") 
//	 @Scheduled(cron = "0 0 * * * * ?") //runs every one hr
	 public void scheduledReorderNotifications() {
	     // Automatically process reorder notifications for all stocks
	     String result = sendReorderNotification();
	     
	     System.out.println(result);
	 }

	public List<Stock> getStocksPastExpiryDate(LocalDate now) {
		// TODO Auto-generated method stub
		return null;
	}

   

    
}

