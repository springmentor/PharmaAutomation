package com.pas.controller;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import com.pas.model.Drug;
import com.pas.model.Stock;
@Controller
public class ClientController {
	
	@InitBinder
    public void initBinder(WebDataBinder binder) {
       
    }

	@RequestMapping(value="/")
	public String registrationPage(Model model) {
	
		model.addAttribute("drug", new Drug());
		return "index";
	
	}
	
	@Bean
	public RestTemplate restTemplate() {
	    return new RestTemplate();
	}
	
	@RequestMapping(value="/updateStatus")
	public String updateDrugPage(Model model) {
		model.addAttribute("drug", new Drug());
		return "updatedrug";
	
	}
	
	@RequestMapping(value = "/updateByStatus", method = RequestMethod.GET)
	public String findDrugByStatus(@RequestParam("id") int drugid,@RequestParam("status") String drugstatus, Model model) {
	    Drug drug = new Drug();
	    String url = "http://localhost:7189/updateDrugStatus/" + drugid +"/"+ drugstatus;
	    HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");


	        ResponseEntity<Drug> response = restTemplate().exchange(
	                url,
	                HttpMethod.PUT,
	                null,
	                new ParameterizedTypeReference<Drug>() {}
	        );
	        drug = response.getBody();


	    if (drug!=null) {
	        model.addAttribute("drugs", drug);
	        return "druglist";
	    } else {
	        model.addAttribute("errorMessage", "No drug found with the given status.");
	        return "statuspage";
	    }
	}
	
	@RequestMapping(value="/addDrug")
	public String addDrugPage(Model model) {
		model.addAttribute("drug", new Drug());
		return "adddrug";
	
	}
	
	@RequestMapping(value="/add",method=RequestMethod.POST)
	public String submitAddDrugFormPage(@ModelAttribute("drug") Drug d,Model model) 
	{
			Drug drugobj=null;
			String url = "http://localhost:7189/addDrug";
			
			HttpHeaders headers = new HttpHeaders();
	        headers.set("Content-Type", "application/json");

	        HttpEntity<Drug> request = new HttpEntity<>(d, headers);

	      
	        ResponseEntity<Drug> response = restTemplate().exchange(
	                url,
	                HttpMethod.POST,
	                request,
	                new ParameterizedTypeReference<Drug>() {}
	        );
	       
	               
	
	        drugobj=response.getBody();
	       
		if(drugobj !=null)
			return "statuspage";
		else
		{
			model.addAttribute("errorMessage", "Failed to add Drug details !");
			return "statuspage";
		}
			

	}
	@RequestMapping(value="/addStock")
	public String addStockPage(Model model) {
		model.addAttribute("stock", new Stock());
		return "addstock";
	
	}
	@RequestMapping(value = "/addByStock", method = RequestMethod.POST)
	public String submitAddStockFormPage(@RequestParam("drugId") int drugId  , @ModelAttribute("stock") Stock stock, Model model) {
		
	    String url = "http://localhost:7189/addStock/" + drugId; 

	    HttpHeaders headers = new HttpHeaders();
	    headers.set("Content-Type", "application/json");

	    HttpEntity<Stock> request = new HttpEntity<>(stock, headers);

	        
	        ResponseEntity<Stock> response = restTemplate().exchange(
	            url,
	            HttpMethod.POST,
	            request,
	            new ParameterizedTypeReference<Stock>() {}
	        );

	        Stock addedStock = response.getBody();

	        if (addedStock != null) {
	        	model.addAttribute("successMessage", "The Stock added successfully!");
	        } else {
	            model.addAttribute("errorMessage", "Failed to add stock details!");
	        }
	    
	    
	    return "status"; 
	    
	}

	
	@RequestMapping(value="/viewDrugStatus")
	public String viewByDrugStatus(Model model) {
	
		return "findbydrugstatus";
	
	}
	@RequestMapping(value = "/viewLowThreshold", method = RequestMethod.GET)
	public String findstockthreshold( Model model) {
	    Stock stock = new Stock();
	    String url = "http://localhost:7189/stocksBelowThreshold" ;
	    HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");


        ResponseEntity<List<Stock>> response = restTemplate().exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Stock>>() {}
        );
        

        List<Stock> stocks = response.getBody();

        if (stocks != null && !stocks.isEmpty()) {
            model.addAttribute("stocks", stocks);
            return "stocklist"; 
        } else {
	        model.addAttribute("errorMessage", "No stocks found with low threshold");
	        return "status";
	    }
	}
	@RequestMapping(value="/Home")
	public String Home(Model model) {
	
		return "home";
	
	}
	
	
	@RequestMapping(value = "/viewByDrugStatus", method = RequestMethod.GET)
	public String findbydrugstatus(@RequestParam("status") String drugstatus, Model model) {
	    Drug drug = new Drug();
	    String url = "http://localhost:7189/viewDrugsByStatus/" + drugstatus;
	    HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");


        ResponseEntity<List<Drug>> response = restTemplate().exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Drug>>() {}
        );
        

        List<Drug> drugs = response.getBody();

        if (drugs != null && !drugs.isEmpty()) {
            model.addAttribute("drugs", drugs);
            return "druglist"; 
        } else {
	        model.addAttribute("errorMessage", "No drugs found with the given drug status.");
	        return "statuspage";
	    }
	}
	
	@RequestMapping(value="/deleteDrug")
	public String deleteDrugPage(Model model) {
		model.addAttribute("drug", new Drug());
		return "deletedrug";
	}
	@RequestMapping(value="/stockIndex")
	public String StockIndexPage(Model model) {
		
		return "stockindex";
	}
	@RequestMapping(value="/deleteStock")
	public String deleteStockPage(Model model) {
		model.addAttribute("stock", new Stock());
		return "deletestock";
	}
	@RequestMapping(value = "/deleteStockById", method = RequestMethod.POST)
	public String deleteStock(@RequestParam("batchId") int id, Model model) {
	    String url = "http://localhost:7189/deleteStock/" + id;
	    
	    try {
	        ResponseEntity<Void> response = restTemplate().exchange(
	                url,
	                HttpMethod.DELETE,
	                null,
	                Void.class
	        );

	        if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
	            model.addAttribute("successMessage", "The Stock deleted successfully!");
	        } else {
	            model.addAttribute("errorMessage", "Failed to delete Drug details!");
	        }
	    } catch (HttpClientErrorException.NotFound e) {
	        model.addAttribute("errorMessage", "The Stock not found!");
	    } catch (Exception e) {
	        model.addAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
	    }
	    
	    return "status"; 
	}

	
	@RequestMapping(value = "/deleteById", method = RequestMethod.POST)
	public String deleteDrug(@RequestParam("id") int id, Model model) {
	    String url = "http://localhost:7189/deleteDrug/" + id;
	    
	    try {
	        ResponseEntity<Void> response = restTemplate().exchange(
	                url,
	                HttpMethod.DELETE,
	                null,
	                Void.class
	        );
	        
	        if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
	        	return "deletestautus";}
	    	else
	    	{
	    		model.addAttribute("errorMessage", "Failed to delete Drug details !");
	    		return "deletestautus";
	    	}
	    } catch (HttpClientErrorException.NotFound e) {
                model.addAttribute("errorMessage", "Drug not found!");
	        } catch (Exception e) {
                model.addAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
          	 }

	    
	  
	    return "deletestatus"; 
	}
//	 if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
//         model.addAttribute("successMessage", "Drug deleted successfully!");
//         return "deletestatus";
//     } else {
//         model.addAttribute("errorMessage", "Failed to delete the drug. Please try again.");
//     }
// } catch (HttpClientErrorException.NotFound e) {
//     model.addAttribute("errorMessage", "Drug not found!");
// } catch (Exception e) {
//     model.addAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
// }

	
//	@RequestMapping(value = "/viewAllDrug", method = RequestMethod.GET)
//	public String viewAllDrugs(Model model) {
//		List<Drug> emp=new ArrayList<Drug>();
//	    String url = "http://localhost:8090/viewAll";
//	    HttpHeaders headers = new HttpHeaders();
//        headers.set("Content-Type", "application/json");
//
//        HttpEntity<Drug> request = new HttpEntity<>(headers);
//	    
//	 
//	        ResponseEntity<List<Drug>> response = restTemplate().exchange(
//	                url,
//	                HttpMethod.GET,
//	                null,
//	                new ParameterizedTypeReference<List<Drug>>() {}
//	        );
//	       emp = response.getBody();
//	        
//
//	    model.addAttribute("employees", emp);
//	    if (emp == null || emp.isEmpty()) {
//	        model.addAttribute("errorMessage", "No employees found!");
//	        return "statuspage";
//	    }
//		else
//		{
//			model.addAttribute("errorMessage", "No record found!!!");
//			return "employeelist";
//		}
//	}
////
//	@RequestMapping(value="/view")
//	public String viewByEmployeeIDPage(Model model) {
//	
//		return "findbyid";
//	
//	}
//	
//	
//	@RequestMapping(value = "/viewById", method = RequestMethod.GET)
//	public String findEmployeeByName(@RequestParam("id") int id, Model model) {
//	    Drug emp = new Drug();
//	    String url = "http://localhost:8090/viewById/" + id;
//	    HttpHeaders headers = new HttpHeaders();
//        headers.set("Content-Type", "application/json");
//
//
//	        ResponseEntity<Drug> response = restTemplate().exchange(
//	                url,
//	                HttpMethod.GET,
//	                null,
//	                Drug.class
//	        );
//	        emp = response.getBody();
//
//
//	    if (emp!=null) {
//	        model.addAttribute("employees", emp);
//	        return "employeelist";
//	    } else {
//	        model.addAttribute("errorMessage", "No employees found with the given employee id.");
//	        return "statuspage";
//	    }
//	}
//	
//	@RequestMapping(value="/viewByDesg")
//	public String viewByEmployeeDesigPage(Model model) {
//	
//		return "findbydesg";
//	
//	}
//	
//	@RequestMapping(value = "/viewByDesignation", method = RequestMethod.GET)
//	public String findEmployeeByDesignation(@RequestParam("designation") String designation, Model model) {
//	    List<Drug> emp = new ArrayList<>();
//	    String url = "http://localhost:8090/viewByDesignation/" + designation;
//	    HttpHeaders headers = new HttpHeaders();
//        headers.set("Content-Type", "application/json");
//
//
//	        ResponseEntity<List<Drug>> response = restTemplate().exchange(
//	                url,
//	                HttpMethod.GET,
//	                null,
//	                new ParameterizedTypeReference<List<Drug>>() {}
//	        );
//	        emp = response.getBody();
//
//
//	    if (emp!=null && emp.size()!=0) {
//	        model.addAttribute("employees", emp);
//	        return "employeelist";
//	    } else {
//	        model.addAttribute("errorMessage", "No employees found with the given designation.");
//	        return "statuspage";
//	    }
//	}

}
