package com.pms.controller;

import com.pms.exception.InvalidEntityException;
import com.pms.model.Drug;
import com.pms.model.Stock;
import com.pms.service.StockService;
import com.pms.service.DrugService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/stocks")
public class StockController {

    @Autowired
    private StockService stockService;

    @Autowired
    private DrugService drugService;

    @GetMapping
    public ResponseEntity<List<Stock>> getAllStocks() {
        List<Stock> stocks = stockService.getAllStocks();
        return ResponseEntity.ok(stocks);
    }

    @PostMapping("/add")
    public ResponseEntity<Stock> addStock(@RequestBody Stock stock) {
        Stock savedStock = stockService.addStock(stock);
        return ResponseEntity.ok(savedStock);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Stock> updateStock(@PathVariable Long id, @RequestBody Stock stockDetails) {
        Stock updatedStock = stockService.updateStock(id, stockDetails);
        return ResponseEntity.ok(updatedStock);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> removeStock(@RequestParam Long id) throws InvalidEntityException {
        stockService.deleteStock(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @GetMapping("/filter")
    public ResponseEntity<List<Stock>> filterStocks(@RequestParam(defaultValue = "none") String filter, @RequestParam(defaultValue = "") String expiryDate)throws InvalidEntityException {
        List<Stock> stockList;
        if ("below_threshold".equals(filter)) {
            stockList = stockService.getStocksBelowThreshold();
        } else if ("expiry_date".equals(filter)) {
            stockList = stockService.getStocksPastExpiryDate(LocalDate.now());
        } else {
            stockList = stockService.getAllStocks();
        }
        return new ResponseEntity<>(stockList, HttpStatus.OK);
    }


    @GetMapping("/drugs")
    public ResponseEntity<List<Drug>> getAllDrugs() {
        List<Drug> drugs = drugService.getAllDrugs();
        return ResponseEntity.ok(drugs);
    }


}
