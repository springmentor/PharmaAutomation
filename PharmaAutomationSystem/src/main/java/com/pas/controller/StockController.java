package com.pas.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.pas.model.Stock;
import com.pas.service.StockService;

@RestController
public class StockController {

    @Autowired
    private StockService stockService;

   
    @PostMapping("/addStock/{drugid}")
    public ResponseEntity<Stock> addStock(@RequestBody Stock stock,@PathVariable int drugid) {
        Stock addedStock = stockService.addStock(stock,drugid);
        return new ResponseEntity<>(addedStock, HttpStatus.CREATED);
    }

    @DeleteMapping("/deleteStock/{batchId}")
    public ResponseEntity<Void> deleteStock(@PathVariable int batchId) {
        boolean isDeleted = stockService.deleteStock(batchId);
        if (isDeleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); 
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND); 
    }

   
    @GetMapping("/viewDrugsBelowThreshold/{threshold}")
    public ResponseEntity<List<Stock>> viewDrugsBelowThreshold(@PathVariable int threshold) {
        List<Stock> stocks = stockService.viewDrugsBelowThreshold(threshold);
        if (stocks.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(stocks, HttpStatus.OK);
    }
}
