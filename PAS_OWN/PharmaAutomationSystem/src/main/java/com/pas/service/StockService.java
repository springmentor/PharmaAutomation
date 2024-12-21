package com.pas.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
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

    @Autowired
    private JavaMailSender mailSender;

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

    // Method to send an email
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
        System.out.println("Email sent successfully to " + to);
    }

    // Method to send reorder notifications
    public String sendReorderNotification() {
        List<Stock> stocks = stockRepository.findStocksBelowThreshold();

        System.out.println("Found " + stocks.size() + " stocks below threshold.");

        for (Stock stock : stocks) {
            Drug drug = stock.getDrug();
            String subject = "Reorder Notification for Drug: " + drug.getDrugName();
            String body = "The stock for drug " + drug.getDrugName() + 
                          " (Batch ID: " + stock.getStockId() + 
                          ") is below the threshold. Please reorder.";

            System.out.println("Sending email for drug: " + drug.getDrugName());
            sendEmail("deep.pal0246@gmail.com", subject, body);
        }

        return "Mail sent";
    }

    // Scheduled task to send reorder notifications daily at noon
    @Scheduled(cron = "0 * * * * ?")
    public void scheduledReorderNotification() {
        sendReorderNotification();
        System.out.println("Reorder notifications sent.");
    }

}