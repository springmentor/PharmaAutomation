package com.pas.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.pas.model.Drug;
import com.pas.model.Stock;
import com.pas.model.Supplier;
import com.pas.repository.DrugRepository;
import com.pas.repository.StockRepository;

@Service
public class StockService {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private DrugRepository drugRepository;

    @Autowired
    private EmailService mailSender;

    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    public Stock getStockById(int stockId) {
        return stockRepository.findById(stockId).orElse(null);
    }

    public Stock addStock(Stock stock) {
        // Fetch the drug from the database using the drugId from stock
        Drug drug = drugRepository.findById(stock.getDrug().getDrugId()).orElse(null);

        if (drug == null) {
            return null;  // Return null if drug is not found
        }

        // Update the drug's quantity
        int newQuantity = drug.getTotalQuantity() + stock.getReceivedQuantity();
        drug.setTotalQuantity(newQuantity);

        // Save the updated drug
        drugRepository.save(drug);

        // Save the stock
        return stockRepository.save(stock);
    }

    public Stock updateStock(int stockId, Stock stockDetails) {
        Stock existingStock = stockRepository.findById(stockId).orElse(null);

        if (existingStock != null) {
            // Update stock details
            existingStock.setThresholdQuantity(stockDetails.getThresholdQuantity());
            return stockRepository.save(existingStock);
        }

        return null;
    }

    public void deleteStock(int stockId) {
        Stock stock = stockRepository.findById(stockId).orElse(null);

        if (stock != null) {
            // Fetch the associated drug
            Drug drug = stock.getDrug();

            if (drug != null) {
                // Reduce the drug quantity
                int newQuantity = drug.getTotalQuantity() - stock.getReceivedQuantity();

                // Check if the new quantity would be negative
                if (newQuantity < 0) {
                    throw new IllegalArgumentException("Not enough drug quantity to delete stock.");
                }

                // Update the drug quantity
                drug.setTotalQuantity(newQuantity);
                drugRepository.save(drug);
            }

            // Finally, delete the stock
            stockRepository.deleteById(stockId);
        }
    }

    public void reduceStock(int drugId, int quantity) {
        Drug drug = drugRepository.findById(drugId).orElse(null);

        if (drug != null) {
            int newQuantity = drug.getTotalQuantity() - quantity;

            if (newQuantity < 0) {
                throw new IllegalArgumentException("Not enough stock for drug ID: " + drugId);
            }

            drug.setTotalQuantity(newQuantity);
            drugRepository.save(drug);
        } else {
            throw new IllegalArgumentException("Drug ID: " + drugId + " not found.");
        }
    }

    public void increaseStock(int drugId, int quantity) {
        Drug drug = drugRepository.findById(drugId).orElse(null);

        if (drug != null) {
            int newQuantity = drug.getTotalQuantity() + quantity;
            drug.setTotalQuantity(newQuantity);
            drugRepository.save(drug);
        } else {
            throw new IllegalArgumentException("Drug ID: " + drugId + " not found.");
        }
    }

    public String sendReorderNotification() {
        // Retrieve all stocks below the threshold
        List<Stock> stocks = stockRepository.findStocksBelowThreshold();

        System.out.println("Found " + stocks.size() + " stocks below threshold.");

        for (Stock stock : stocks) {
            Drug drug = stock.getDrug();
            Supplier supplier = stock.getSupplier(); // Directly retrieve the Supplier entity
            String supplierEmail = supplier.getEmail(); // Assuming the Supplier entity has an `email` field

            // Prepare email subject and body
            String subject = "Reorder Notification for Drug: " + drug.getDrugName();
            String body = "The stock for drug " + drug.getDrugName() +
                          " (Batch ID: " + stock.getStockId() +
                          ") is below the threshold. Please reorder.";

            System.out.println("Sending email to supplier: " + supplierEmail + " for drug: " + drug.getDrugName());
            
            // Send email to the supplier
            mailSender.sendEmail(supplierEmail, subject, body);
        }

        return "Mail sent";
    }

    // Scheduled task to send reorder notifications every 5 seconds
    @Scheduled(cron = "*/700000000 * * *  * ?")
    public void scheduledReorderNotification() {
        sendReorderNotification();
        System.out.println("Reorder notifications sent.");
    }
}
