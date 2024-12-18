package com.pas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.pas.service.StockService;

@RestController
public class PrescriptionController {
	   @Autowired
	    private PrescriptionService presService;

	
}
