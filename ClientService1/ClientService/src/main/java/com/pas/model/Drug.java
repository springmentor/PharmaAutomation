package com.pas.model;

import java.util.List;

//import jakarta.persistence.Id;


public class Drug {

   private int drugId;
   private String drugName;
   private String supplierId;
   private String supplierName;
   private Float unitPrice;
   private String status;
   private Integer totalQuantity;
   
   private List<Stock> stocks;
   
public String getSupplierId() {
	return supplierId;
}
public void setSupplierId(String supplierId) {
	this.supplierId = supplierId;
}
public int getDrugId() {
	return drugId;
}
public void setDrugId(int drugId) {
	this.drugId = drugId;
}
public String getDrugName() {
	return drugName;
}
public void setDrugName(String drugName) {
	this.drugName = drugName;
}
public String getSupplierName() {
	return supplierName;
}
public void setSupplierName(String supplierName) {
	this.supplierName = supplierName;
}
public Float getUnitPrice() {
	return unitPrice;
}
public void setUnitPrice(Float unitPrice) {
	this.unitPrice = unitPrice;
}
public String getStatus() {
	return status;
}
public void setStatus(String status) {
	this.status = status;
}
public Integer getTotalQuantity() {
	return totalQuantity;
}
public void setTotalQuantity(Integer totalQuantity) {
	this.totalQuantity = totalQuantity;
}
public List<Stock> getStocks() {
	return stocks;
}
public void setStocks(List<Stock> stocks) {
	this.stocks = stocks;
}
    
}
