package com.pas.controller;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.client.RestTemplate;



public class ClientController {
	
	@InitBinder
    public void initBinder(WebDataBinder binder) {
       
    }

	
	@Bean
	public RestTemplate restTemplate() {
	    return new RestTemplate();
	}
	//Done by hema
	
}