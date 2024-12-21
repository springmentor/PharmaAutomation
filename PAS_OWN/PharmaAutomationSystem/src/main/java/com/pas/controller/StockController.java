package com.pas.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.pas.model.Stock;
import com.pas.service.StockService;

import jakarta.validation.Valid;

import java.time.LocalDate;

@RestController
public class StockController {

    @Autowired
    private StockService stockService;

    // Get all stocks
    @GetMapping("/getAllStocks")
    public List<Stock> getAllStocks() {
        return stockService.getAllStocks();
    }

    // Get a specific stock by stockId
    @GetMapping("/getStock/{stockId}")
    public ResponseEntity<Stock> getStockById(@PathVariable int stockId) {
        Stock stock = stockService.getStockById(stockId);
        return stock != null ? ResponseEntity.ok(stock) : ResponseEntity.notFound().build();
    }

    // Add a new stock and update the drug quantity
    @PostMapping("/addStock")
    public ResponseEntity<String> addStock(@Valid @RequestBody Stock stock, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Validation failed: " + result.getAllErrors());
        }

        try {
            Stock addedStock = stockService.addStock(stock);
            if (addedStock != null) {
                return ResponseEntity.ok("Stock added successfully.");
            } else {
                return ResponseEntity.badRequest().body("Failed to add stock.");
            }
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("Internal server error.");
        }
    }

    // Update an existing stock by stockId
    @PutMapping("/updateStock/{stockId}")
    public ResponseEntity<Stock> updateStock(@PathVariable int stockId, @RequestBody Stock stockDetails) {
        // Validate expiryDate
        if (stockDetails.getExpiryDate() != null && stockDetails.getExpiryDate().isBefore(LocalDate.now())) {
            return ResponseEntity.badRequest().body(null);  // Expiry date is not in the future
        }

        Stock updatedStock = stockService.updateStock(stockId, stockDetails); // Use stockId here
        return updatedStock != null ? ResponseEntity.ok(updatedStock) : ResponseEntity.notFound().build();
    }

    // Delete a stock by stockId and update the drug quantity
    @DeleteMapping("/deleteStock/{stockId}")
    public ResponseEntity<Void> deleteStock(@PathVariable int stockId) {
        try {
            Stock stock = stockService.getStockById(stockId);
            if (stock != null) {
                stockService.deleteStock(stockId);
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            return ResponseEntity.status(500).build();
        }
    }

    // Endpoint to trigger reorder notifications manually
    @GetMapping("/sendReorderNotification")
    public ResponseEntity<String> sendReorderNotification() {
        return ResponseEntity.ok(stockService.sendReorderNotification());
    }
}