package com.pas.controller;

import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.pas.model.Billing;
import com.pas.model.Drug;
import com.pas.model.Prescription;
import com.pas.model.Stock;
import com.pas.model.Supplier;

@Controller
public class ClientController {

	@InitBinder
    public void initBinder(WebDataBinder binder) {
       
    }
	
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    private static final String SERVICE_URL = "http://localhost:7189";

    // Main Navigation

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/drugs")
    public String drugsHome() {
        return "drugindex";
    }

    @GetMapping("/stocks")
    public String stocksHome() {
        return "stockindex";
    }
    // Drug Management

    @GetMapping("/addDrug")
    public String addDrugPage(Model model) {
        model.addAttribute("drug", new Drug());
        return "addDrug"; // Render the add drug form
    }

    @PostMapping("/addDrug")
    public String submitAddDrugForm(@ModelAttribute("drug") Drug drug, Model model) {
        String url = SERVICE_URL + "/addDrug";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<Drug> request = new HttpEntity<>(drug, headers);
        ResponseEntity<Drug> response = restTemplate().exchange(url, HttpMethod.POST, request, Drug.class);
        Drug addedDrug = response.getBody();
        
        if (addedDrug != null) {
            model.addAttribute("successMessage", "Drug added successfully");
        } else {
            model.addAttribute("errorMessage", "Failed to add Drug");
        }
        return "redirect:/viewDrugs"; // Redirect to the drugs list page
    }

    @GetMapping("/viewDrugs")
    public String viewAllDrugs(Model model) {
        String url = SERVICE_URL + "/getAllDrugs";
        ResponseEntity<List<Drug>> response = restTemplate().exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Drug>>() {});
        List<Drug> drugs = response.getBody();
        
        model.addAttribute("drugs", drugs); // Pass list of drugs to the view
        return "viewDrugs"; // Display the drugs list
    }


    @GetMapping("/updateDrug/{drugId}")
    public String updateDrugPage(@PathVariable int drugId, Model model) {
        String url = SERVICE_URL + "/getDrug/" + drugId;
        ResponseEntity<Drug> response = restTemplate().exchange(url, HttpMethod.GET, null, Drug.class);
        Drug drug = response.getBody();
        model.addAttribute("drug", drug);
        return "updateDrugs"; // Render the update drug form
    }

    @PostMapping("/updateDrug/{drugId}")
    public String updateDrug(@PathVariable int drugId, @RequestParam String status, Model model) {
        // Fetch the existing drug from the service or database to get the other details
        String url = SERVICE_URL + "/getDrug/" + drugId;
        ResponseEntity<Drug> response = restTemplate().exchange(url, HttpMethod.GET, null, Drug.class);
        Drug drug = response.getBody();
        
        if (drug == null) {
            model.addAttribute("errorMessage", "Drug not found");
            return "errorPage";  // Redirect to an error page or show an error message
        }
        
        // Set the status from the form
        drug.setStatus(status);
        
        // Now only update the status field
        String updateUrl = SERVICE_URL + "/updateDrug/" + drugId;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<Drug> request = new HttpEntity<>(drug, headers);
        
        // Send the update request to the backend to update only the status
        ResponseEntity<Drug> updateResponse = restTemplate().exchange(updateUrl, HttpMethod.PUT, request, Drug.class);
        Drug updatedDrug = updateResponse.getBody();
        
        if (updatedDrug != null) {
            model.addAttribute("successMessage", "Drug status updated successfully");
        } else {
            model.addAttribute("errorMessage", "Failed to update Drug status");
        }

        return "statuspage"; // Show a success or error message on the status page
    }



    @PostMapping("/deleteDrug/{drugId}")
    public String deleteDrug(@PathVariable int drugId, Model model) {
        String url = SERVICE_URL + "/deleteDrug/" + drugId;
        ResponseEntity<Void> response = restTemplate().exchange(url, HttpMethod.DELETE, null, Void.class);
        
        if (response.getStatusCode().is2xxSuccessful()) {
            model.addAttribute("successMessage", "Drug deleted successfully");
        } else {
            model.addAttribute("errorMessage", "Failed to delete Drug");
        }
        return "statuspage"; // Redirect to a status page
    }


 
    // Stock Management

    @GetMapping("/addStock")
    public String addStockPage(Model model) {
        model.addAttribute("stock", new Stock());

        // Fetch drugs for dropdown
        String drugUrl = SERVICE_URL + "/getAllDrugs";
        ResponseEntity<List<Drug>> drugResponse = restTemplate().exchange(drugUrl, HttpMethod.GET, null, new ParameterizedTypeReference<List<Drug>>() {});
        List<Drug> drugs = drugResponse.getBody();
        model.addAttribute("drugs", drugs); // Add drugs to the model

        // Fetch suppliers for dropdown
        String supplierUrl = SERVICE_URL + "/getAllSuppliers";
        ResponseEntity<List<Supplier>> supplierResponse = restTemplate().exchange(supplierUrl, HttpMethod.GET, null, new ParameterizedTypeReference<List<Supplier>>() {});
        List<Supplier> suppliers = supplierResponse.getBody();
        model.addAttribute("suppliers", suppliers); // Add suppliers to the model

        return "addstock"; // Return the add stock form view
    }


    @PostMapping("/addStock")
    public String submitAddStockForm(@ModelAttribute("stock") Stock stock,
                                     @RequestParam("drugId") int drugId,
                                     @RequestParam("supplierId") int supplierId,
                                     Model model) {
        try {
            // Validate expiry date
            if (stock.getExpiryDate() != null && stock.getExpiryDate().isBefore(LocalDate.now())) {
                model.addAttribute("errorMessage", "Please enter a future date for the expiry.");
                return "statuspage"; // Return to the form with error message
            }

            // Map the drugId to a Drug object
            Drug drug = new Drug();
            drug.setDrugId(drugId);
            stock.setDrug(drug);

            // Map the supplierId to a Supplier object
            Supplier supplier = new Supplier();
            supplier.setSupplierId(supplierId);
            stock.setSupplier(supplier);

            // Call the REST service to add stock
            String url = SERVICE_URL + "/addStock";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            HttpEntity<Stock> request = new HttpEntity<>(stock, headers);
            ResponseEntity<Stock> response = restTemplate().exchange(url, HttpMethod.POST, request, Stock.class);

            // Handle successful response
            if (response.getStatusCode().is2xxSuccessful()) {
                // Fetch the drug details to update its quantity
                String drugUrl = SERVICE_URL + "/getDrug/" + drugId;
                ResponseEntity<Drug> drugResponse = restTemplate().exchange(drugUrl, HttpMethod.GET, null, Drug.class);
                Drug drugToUpdate = drugResponse.getBody();

                if (drugToUpdate != null) {
                    // Update the drug's quantity with the new stock quantity
                    int newQuantity = drugToUpdate.getTotalQuantity() + stock.getReceivedQuantity();
                    drugToUpdate.setTotalQuantity(newQuantity);

                    // Update the drug with the new quantity
                    String updateDrugUrl = SERVICE_URL + "/updateDrug/" + drugId;
                    HttpEntity<Drug> updateRequest = new HttpEntity<>(drugToUpdate, headers);
                    ResponseEntity<Drug> updateResponse = restTemplate().exchange(updateDrugUrl, HttpMethod.PUT, updateRequest, Drug.class);

                    if (updateResponse.getStatusCode().is2xxSuccessful()) {
                        model.addAttribute("successMessage", "Stock added and Drug quantity updated successfully!");
                    } else {
                        model.addAttribute("errorMessage", "Failed to update Drug quantity.");
                    }
                } else {
                    model.addAttribute("errorMessage", "Failed to find the Drug for update.");
                }
            } else {
                model.addAttribute("errorMessage", "Failed to add stock. Please try again.");
            }

        } catch (Exception ex) {
            // Log and handle any exceptions
            ex.printStackTrace();
            model.addAttribute("errorMessage", "An error occurred while adding stock: " + ex.getMessage());
        }

        // Redirect to the stock view page
        return "redirect:/viewStock";
    }






    @GetMapping("/viewStock")
    public String viewAllStocks(Model model) {
        String url = SERVICE_URL + "/getAllStocks";
        ResponseEntity<List<Stock>> response = restTemplate().exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Stock>>() {});
        List<Stock> stocks = response.getBody();
        
        // Add null check for drug object
        stocks.forEach(stock -> {
            if (stock.getDrug() == null) {
                stock.setDrug(new Drug());
                stock.getDrug().setDrugId(0); // or any default value
            }
        });
        
        model.addAttribute("stocks", stocks);
        return "viewstock";
    }

    @GetMapping("/updateStock/{stockId}")
    public String updateStockPage(@PathVariable("stockId") int stockId, Model model) {
        // Fetch the stock by ID
        String stockUrl = SERVICE_URL + "/getStock/" + stockId;
        ResponseEntity<Stock> stockResponse = restTemplate().exchange(stockUrl, HttpMethod.GET, null, Stock.class);
        Stock stock = stockResponse.getBody();

        // Fetch drugs and suppliers for dropdown
        String drugUrl = SERVICE_URL + "/getAllDrugs";
        ResponseEntity<List<Drug>> drugResponse = restTemplate().exchange(drugUrl, HttpMethod.GET, null, new ParameterizedTypeReference<List<Drug>>() {});
        List<Drug> drugs = drugResponse.getBody();

        String supplierUrl = SERVICE_URL + "/getAllSuppliers";
        ResponseEntity<List<Supplier>> supplierResponse = restTemplate().exchange(supplierUrl, HttpMethod.GET, null, new ParameterizedTypeReference<List<Supplier>>() {});
        List<Supplier> suppliers = supplierResponse.getBody();

        // Add attributes to the model
        model.addAttribute("stock", stock);
        model.addAttribute("drugs", drugs);
        model.addAttribute("suppliers", suppliers);

        return "updateStock"; // Return the update stock form view
    }


    @PostMapping("/updateStock/{stockId}")
    public String submitUpdateStockForm(@ModelAttribute("stock") Stock stock,
                                        @RequestParam("threshold") int thresholdQuantity, // Only update threshold
                                        Model model) {
        // Update the threshold, leave other fields unchanged
        stock.setThresholdQuantity(thresholdQuantity);

        // Send the updated stock to the backend service (use PUT instead of POST to update existing resource)
        String url = SERVICE_URL + "/updateStock/" + stock.getStockId(); // Assuming stockId is available
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<Stock> request = new HttpEntity<>(stock, headers);
        ResponseEntity<Stock> response = restTemplate().exchange(url, HttpMethod.PUT, request, Stock.class);

        if (response.getBody() != null) {
            model.addAttribute("successMessage", "Stock updated successfully.");
        } else {
            model.addAttribute("errorMessage", "Failed to update stock.");
        }

        return "redirect:/viewStock"; // Redirect to the stock list page
    }


    @PostMapping("/deleteStock/{stockId}")
    public String deleteStock(@PathVariable int stockId, Model model) {
        // Step 1: Get the stock details first from the service or backend
        String stockUrl = SERVICE_URL + "/getStock/" + stockId;
        ResponseEntity<Stock> stockResponse = restTemplate().exchange(stockUrl, HttpMethod.GET, null, Stock.class);
        Stock stock = stockResponse.getBody();

        if (stock == null) {
            model.addAttribute("errorMessage", "Stock not found");
            return "statuspage"; // Return an error page if the stock doesn't exist
        }

        // Step 2: Get the associated drug and update its total quantity by subtracting the stock quantity
        Drug drug = stock.getDrug();
        if (drug != null) {
            int newQuantity = drug.getTotalQuantity() - stock.getReceivedQuantity();
            
            if (newQuantity < 0) {
                model.addAttribute("errorMessage", "Not enough drug quantity to delete stock.");
                return "statuspage"; // Ensure stock deletion does not happen if the quantity goes negative
            }

            drug.setTotalQuantity(newQuantity);  // Update drug quantity
            
            // Step 3: Send the updated drug back to the backend
            String updateDrugUrl = SERVICE_URL + "/updateDrug/" + drug.getDrugId();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            HttpEntity<Drug> updateRequest = new HttpEntity<>(drug, headers);
            ResponseEntity<Drug> updateResponse = restTemplate().exchange(updateDrugUrl, HttpMethod.PUT, updateRequest, Drug.class);

            // Check if drug update was successful
            if (!updateResponse.getStatusCode().is2xxSuccessful()) {
                model.addAttribute("errorMessage", "Failed to update Drug quantity. Status: " + updateResponse.getStatusCode());
                return "statuspage"; // Return if drug update fails
            } else {
                model.addAttribute("successMessage", "Drug quantity updated successfully.");
            }

            // Step 4: Proceed with deleting the stock
            String deleteStockUrl = SERVICE_URL + "/deleteStock/" + stockId;
            ResponseEntity<Void> deleteResponse = restTemplate().exchange(deleteStockUrl, HttpMethod.DELETE, null, Void.class);

            // Check the stock deletion status
            if (deleteResponse.getStatusCode().is2xxSuccessful()) {
                model.addAttribute("successMessage", "Stock deleted successfully.");
            } else {
                model.addAttribute("errorMessage", "Failed to delete stock. Status: " + deleteResponse.getStatusCode());
            }
        } else {
            model.addAttribute("errorMessage", "Associated drug not found.");
            return "statuspage"; // If no drug is associated, show error message
        }

        return "statuspage";
    }




    
 // Supplier Management

    @GetMapping("/addSupplier")
    public String addSupplierPage(Model model) {
        model.addAttribute("supplier", new Supplier());
        return "addsupplier";
    }

    @PostMapping("/addSupplier")
    public String submitAddSupplierForm(@ModelAttribute("supplier") Supplier supplier, Model model) {
        String url = SERVICE_URL + "/addSupplier";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<Supplier> request = new HttpEntity<>(supplier, headers);
        ResponseEntity<Supplier> response = restTemplate().exchange(url, HttpMethod.POST, request, Supplier.class);
        Supplier addedSupplier = response.getBody();
        if (addedSupplier != null) {
            model.addAttribute("successMessage", "Supplier added successfully");
        } else {
            model.addAttribute("errorMessage", "Failed to add Supplier");
        }
        return "statuspage";
    }

    @GetMapping("/viewSuppliers")
    public String viewSuppliers(Model model) {
        String url = SERVICE_URL + "/getAllSuppliers";
        ResponseEntity<List<Supplier>> response = restTemplate().exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Supplier>>() {});
        List<Supplier> suppliers = response.getBody();
        model.addAttribute("suppliers", suppliers);
        return "viewSuppliers";  // Ensure the template name matches exactly
    }
    

    @GetMapping("/updateSupplier/{supplierId}")
    public String updateSupplierPage(@PathVariable int supplierId, Model model) {
        // Fetch supplier details from the external service or database
        String url = SERVICE_URL + "/getSupplier/" + supplierId;
        ResponseEntity<Supplier> response = restTemplate().exchange(url, HttpMethod.GET, null, Supplier.class);
        Supplier supplier = response.getBody();
        
        if (supplier == null) {
            model.addAttribute("errorMessage", "Supplier not found");
            return "errorPage";  // Redirect to an error page or show an error message
        }

        // Add supplier to the model to be used in the Thymeleaf template
        model.addAttribute("supplier", supplier);
        
        return "updateSupplier";  // Make sure this matches your Thymeleaf template name
    }

    @PostMapping("/updateSupplier/{supplierId}")
    public String updateSupplier(@PathVariable int supplierId, @ModelAttribute Supplier supplier, Model model) {
        // Ensure the supplierId is correctly set if not already set in the form
        if (supplier.getSupplierId() == 0) {
            supplier.setSupplierId(supplierId);  // Use the supplierId from the URL
        }

        // Update the supplier via the service
        String url = SERVICE_URL + "/updateSupplier/" + supplier.getSupplierId();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<Supplier> request = new HttpEntity<>(supplier, headers);
        
        ResponseEntity<Supplier> response = restTemplate().exchange(url, HttpMethod.PUT, request, Supplier.class);
        Supplier updatedSupplier = response.getBody();
        
        // Check if the update was successful
        if (response.getStatusCode() == HttpStatus.OK && updatedSupplier != null) {
            model.addAttribute("successMessage", "Supplier updated successfully");
        } else {
            model.addAttribute("errorMessage", "Failed to update Supplier");
        }
        
        // Redirect to a status page or supplier details page after the update
        return "statuspage";  // Adjust this path as needed
    }

    @PostMapping("/deleteSupplier/{supplierId}")
    public String deleteSupplier(@PathVariable int supplierId, Model model) {
        String url = SERVICE_URL + "/deleteSupplier/" + supplierId;
        ResponseEntity<Void> response = restTemplate().exchange(url, HttpMethod.DELETE, null, Void.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            model.addAttribute("successMessage", "Supplier deleted successfully");
        } else {
            model.addAttribute("errorMessage", "Failed to delete Supplier");
        }
        return "statuspage";
    }
//    
// // Prescription Management
//
//    @GetMapping("/prescriptions")
//    public String prescriptionsHome() {
//        return "prescriptionindex";
//    }
//
//    @GetMapping("/addPrescription")
//    public String addPrescriptionPage(Model model) {
//        // Create new prescription object
//        model.addAttribute("prescription", new Prescription());
//        
//        // Fetch available stocks from the service
//        String url = SERVICE_URL + "/getAllStocks";
//        ResponseEntity<List<Stock>> response = restTemplate().exchange(
//            url, 
//            HttpMethod.GET, 
//            null, 
//            new ParameterizedTypeReference<List<Stock>>() {}
//        );
//        List<Stock> availableStocks = response.getBody();
//        
//        // Add stocks to the model
//        model.addAttribute("availableStocks", availableStocks);
//        
//        return "addPrescription";
//    }
//
//    @PostMapping("/addPrescription")
//    public String submitAddPrescriptionForm(
//        @RequestParam String patientName,
//        @RequestParam String doctorName,
//        @RequestParam LocalDate prescribedDate,
//        @RequestParam(required = false) List<Integer> selectedStocks,
//        @RequestParam Map<String, String> quantities,
//        Model model) {
//        
//        try {
//            if (selectedStocks == null || selectedStocks.isEmpty()) {
//                model.addAttribute("errorMessage", "Please select at least one drug");
//                return "addPrescription";
//            }
//
//            // Create new prescription
//            Prescription prescription = new Prescription();
//            prescription.setPatientName(patientName);
//            prescription.setDoctorName(doctorName);
//            prescription.setPrescribedDate(prescribedDate);
//
//            List<Stock> prescribedStocks = new ArrayList<>();
//            Map<Integer, Integer> prescribedQuantities = new HashMap<>();
//
//            // Process each selected stock and its quantity
//            for (Integer stockId : selectedStocks) {
//                // Get the quantity for this stock
//                String quantityKey = "quantities[" + stockId + "]";
//                if (!quantities.containsKey(quantityKey)) {
//                    continue;
//                }
//
//                int prescribedQuantity = Integer.parseInt(quantities.get(quantityKey));
//
//                // Fetch the stock
//                String stockUrl = SERVICE_URL + "/getStock/" + stockId;
//                ResponseEntity<Stock> stockResponse = restTemplate().exchange(
//                    stockUrl,
//                    HttpMethod.GET,
//                    null,
//                    Stock.class
//                );
//                Stock stock = stockResponse.getBody();
//
//                if (stock == null) {
//                    model.addAttribute("errorMessage", "Stock not found: " + stockId);
//                    return "addPrescription";
//                }
//
//                // Validate quantity
//                if (prescribedQuantity > stock.getReceivedQuantity()) {
//                    model.addAttribute("errorMessage", 
//                        "Prescribed quantity exceeds available stock for " + stock.getDrug().getDrugName());
//                    return "addPrescription";
//                }
//
//                // Update stock quantity
//                int newQuantity = stock.getReceivedQuantity() - prescribedQuantity;
//                stock.setReceivedQuantity(newQuantity);
//
//                // Update the stock in the backend
//                String updateStockUrl = SERVICE_URL + "/updateStock/" + stockId;
//                HttpHeaders headers = new HttpHeaders();
//                headers.setContentType(MediaType.APPLICATION_JSON);
//                HttpEntity<Stock> updateStockRequest = new HttpEntity<>(stock, headers);
//                restTemplate().exchange(
//                    updateStockUrl,
//                    HttpMethod.PUT,
//                    updateStockRequest,
//                    Stock.class
//                );
//
//                // Add to prescription
//                prescribedStocks.add(stock);
//                prescribedQuantities.put(stockId, prescribedQuantity);
//            }
//
//            // Set the stocks and quantities
//            prescription.setStocks(prescribedStocks);
//            prescription.setPrescribedQuantities(prescribedQuantities);
//
//            // Save the prescription
//            String prescriptionUrl = SERVICE_URL + "/prescriptions/addPrescription";
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            HttpEntity<Prescription> prescriptionRequest = new HttpEntity<>(prescription, headers);
//            
//            ResponseEntity<String> response = restTemplate().exchange(
//                prescriptionUrl,
//                HttpMethod.POST,
//                prescriptionRequest,
//                String.class
//            );
//
//            if (response.getStatusCode().is2xxSuccessful()) {
//                return "redirect:/viewPrescriptions";
//            } else {
//                model.addAttribute("errorMessage", "Failed to add prescription");
//                return "addPrescription";
//            }
//
//        } catch (Exception ex) {
//            model.addAttribute("errorMessage", "Error adding prescription: " + ex.getMessage());
//            return "addPrescription";
//        }
//    }
//
//
//
//    @GetMapping("/viewPrescriptions")
//    public String viewAllPrescriptions(Model model) {
//        String url = SERVICE_URL + "/prescriptions/getAllPrescriptions";
//        ResponseEntity<List<Prescription>> response = restTemplate().exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Prescription>>() {});
//        List<Prescription> prescriptions = response.getBody();
//        
//        model.addAttribute("prescriptions", prescriptions);
//        return "viewPrescriptions";
//    }
//    @GetMapping("/viewPrescription/{prescriptionId}")
//    public String viewPrescriptionDetails(@PathVariable int prescriptionId, Model model) {
//        String url = SERVICE_URL + "/prescriptions/getPrescription/" + prescriptionId;
//        ResponseEntity<Prescription> response = restTemplate().getForEntity(url, Prescription.class);
//        Prescription prescription = response.getBody();
//        
//        if (prescription != null) {
//            model.addAttribute("prescription", prescription);
//            return "viewPrescriptionDetails";
//        } else {
//            return "redirect:/viewPrescriptions";
//        }
//    }
//
//
//@GetMapping("/updatePrescription/{prescriptionId}")
//public String updatePrescriptionPage(@PathVariable int prescriptionId, Model model) {
//    String prescriptionUrl = SERVICE_URL + "/prescriptions/getPrescription/" + prescriptionId;
//    ResponseEntity<Prescription> prescriptionResponse = restTemplate().getForEntity(prescriptionUrl, Prescription.class);
//    Prescription prescription = prescriptionResponse.getBody();
//    
//    String stocksUrl = SERVICE_URL + "/getAllStocks";
//    ResponseEntity<List<Stock>> stocksResponse = restTemplate().exchange(
//        stocksUrl, HttpMethod.GET, null, new ParameterizedTypeReference<List<Stock>>() {}
//    );
//    List<Stock> availableStocks = stocksResponse.getBody();
//    
//    model.addAttribute("prescription", prescription);
//    model.addAttribute("availableStocks", availableStocks);
//    return "updatePrescription";
//} 
//
//@PostMapping("/updatePrescription/{prescriptionId}")
//public String updatePrescription(@PathVariable int prescriptionId, 
//                                 @RequestParam Map<String, String> allParams,
//                                 @RequestParam(required = false) String action,
//                                 Model model) {
//    String prescriptionUrl = SERVICE_URL + "/prescriptions/getPrescription/" + prescriptionId;
//    ResponseEntity<Prescription> prescriptionResponse = restTemplate().getForEntity(prescriptionUrl, Prescription.class);
//    Prescription prescription = prescriptionResponse.getBody();
//    
//    if (prescription == null) {
//        return "redirect:/viewPrescriptions";
//    }
//    
//    if (action != null && action.startsWith("remove_")) {
//        int stockIdToRemove = Integer.parseInt(action.split("_")[1]);
//        prescription.getPrescribedQuantities().remove(stockIdToRemove);
//    } else if ("update".equals(action)) {
//        Map<Integer, Integer> updatedQuantities = new HashMap<>();
//        for (Map.Entry<String, String> entry : allParams.entrySet()) {
//            if (entry.getKey().startsWith("quantities[") && entry.getKey().endsWith("]")) {
//                int stockId = Integer.parseInt(entry.getKey().substring(11, entry.getKey().length() - 1));
//                int quantity = Integer.parseInt(entry.getValue());
//                updatedQuantities.put(stockId, quantity);
//            }
//        }
//        prescription.setPrescribedQuantities(updatedQuantities);
//        
//        // Add new drug if selected
//        String newDrugIdStr = allParams.get("newDrugId");
//        String newQuantityStr = allParams.get("newQuantity");
//        if (newDrugIdStr != null && !newDrugIdStr.isEmpty() && newQuantityStr != null && !newQuantityStr.isEmpty()) {
//            int newDrugId = Integer.parseInt(newDrugIdStr);
//            int newQuantity = Integer.parseInt(newQuantityStr);
//            prescription.getPrescribedQuantities().put(newDrugId, newQuantity);
//        }
//    }
//    
//    // Update prescription in the backend
//    String updateUrl = SERVICE_URL + "/prescriptions/updatePrescription/" + prescriptionId;
//    HttpHeaders headers = new HttpHeaders();
//    headers.setContentType(MediaType.APPLICATION_JSON);
//    HttpEntity<Prescription> request = new HttpEntity<>(prescription, headers);
//    restTemplate().exchange(updateUrl, HttpMethod.PUT, request, Prescription.class);
//    
//    return "redirect:/viewPrescription/" + prescriptionId;
//}
//
//
//
//    @PostMapping("/deletePrescription/{prescriptionId}")
//    public String deletePrescription(@PathVariable int prescriptionId, Model model) {
//        String url = SERVICE_URL + "/prescriptions/deletePrescription/" + prescriptionId;
//        ResponseEntity<Void> response = restTemplate().exchange(url, HttpMethod.DELETE, null, Void.class);
//        
//        if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
//            model.addAttribute("successMessage", "Prescription deleted successfully");
//        } else {
//            model.addAttribute("errorMessage", "Failed to delete Prescription");
//        }
//        return "redirect:/viewPrescriptions";
//    }
//
//    

    // Prescription Management
    @GetMapping("/addPrescription")
    public String addPrescriptionPage(Model model) {
        model.addAttribute("prescription", new Prescription());
        
        String url = SERVICE_URL + "/getAllDrugs";
        ResponseEntity<List<Drug>> response = restTemplate().exchange(
            url, 
            HttpMethod.GET, 
            null, 
            new ParameterizedTypeReference<List<Drug>>() {}
        );
        List<Drug> drugs = response.getBody();
        
        model.addAttribute("drugs", drugs);
        
        return "addPrescription";
    }

    @PostMapping("/addPrescription")
    public String submitAddPrescriptionForm(
        @RequestParam String patientName,
        @RequestParam String doctorName,
        @RequestParam LocalDate prescribedDate,
        @RequestParam List<Integer> drugIds,
        @RequestParam List<Integer> quantities,
        Model model) {
        
        try {
            Prescription prescription = new Prescription();
            prescription.setPatientName(patientName);
            prescription.setDoctorName(doctorName);
            prescription.setPrescribedDate(prescribedDate);

            Map<Integer, Integer> prescribedDrugs = new HashMap<>();
            for (int i = 0; i < drugIds.size(); i++) {
                prescribedDrugs.put(drugIds.get(i), quantities.get(i));
            }
            prescription.setPrescribedDrugs(prescribedDrugs);

            String prescriptionUrl = SERVICE_URL + "/prescriptions/addPrescription";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Prescription> prescriptionRequest = new HttpEntity<>(prescription, headers);
            
            ResponseEntity<Prescription> response = restTemplate().exchange(
                prescriptionUrl,
                HttpMethod.POST,
                prescriptionRequest,
                Prescription.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                return "redirect:/viewPrescriptions";
            } else {
                model.addAttribute("errorMessage", "Failed to add prescription");
                return "addPrescription";
            }

        } catch (Exception ex) {
            model.addAttribute("errorMessage", "Error adding prescription: " + ex.getMessage());
            return "addPrescription";
        }
    }

    @GetMapping("/viewPrescriptions")
    public String viewAllPrescriptions(Model model) {
        String url = SERVICE_URL + "/prescriptions/getAllPrescriptions";
        ResponseEntity<List<Prescription>> response = restTemplate().exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Prescription>>() {});
        List<Prescription> prescriptions = response.getBody();
        
        model.addAttribute("prescriptions", prescriptions);
        return "viewPrescriptions";
    }

    @GetMapping("/viewPrescription/{prescriptionId}")
    public String viewPrescriptionDetails(@PathVariable int prescriptionId, Model model) {
        String url = SERVICE_URL + "/prescriptions/getPrescription/" + prescriptionId;
        ResponseEntity<Prescription> response = restTemplate().getForEntity(url, Prescription.class);
        Prescription prescription = response.getBody();
        
        if (prescription != null) {
            Map<Integer, Drug> drugDetails = new HashMap<>();
            for (Integer drugId : prescription.getPrescribedDrugs().keySet()) {
                String drugUrl = SERVICE_URL + "/getDrug/" + drugId;
                ResponseEntity<Drug> drugResponse = restTemplate().getForEntity(drugUrl, Drug.class);
                Drug drug = drugResponse.getBody();
                if (drug != null) {
                    drugDetails.put(drugId, drug);
                }
            }
            
            model.addAttribute("prescription", prescription);
            model.addAttribute("drugDetails", drugDetails);
            return "viewPrescriptionDetails";
        } else {
            return "redirect:/viewPrescriptions";
        }
    }

    @GetMapping("/updatePrescription/{prescriptionId}")
    public String updatePrescriptionPage(@PathVariable int prescriptionId, Model model) {
        String prescriptionUrl = SERVICE_URL + "/prescriptions/getPrescription/" + prescriptionId;
        ResponseEntity<Prescription> prescriptionResponse = restTemplate().getForEntity(prescriptionUrl, Prescription.class);
        Prescription prescription = prescriptionResponse.getBody();
        
        String stocksUrl = SERVICE_URL + "/getAllStocks";
        ResponseEntity<List<Stock>> stocksResponse = restTemplate().exchange(
            stocksUrl, HttpMethod.GET, null, new ParameterizedTypeReference<List<Stock>>() {}
        );
        List<Stock> availableStocks = stocksResponse.getBody();
        
        model.addAttribute("prescription", prescription);
        model.addAttribute("availableStocks", availableStocks);
        return "updatePrescription";
    }

    @PostMapping("/updatePrescription/{prescriptionId}")
    public String updatePrescription(@PathVariable int prescriptionId, 
                                     @RequestParam Map<String, String> allParams,
                                     @RequestParam(required = false) String action,
                                     Model model) {
        try {
            // First, get the current prescription
            String getPrescriptionUrl = SERVICE_URL + "/prescriptions/getPrescription/" + prescriptionId;
            ResponseEntity<Prescription> getPrescriptionResponse = restTemplate().getForEntity(getPrescriptionUrl, Prescription.class);
            Prescription prescription = getPrescriptionResponse.getBody();

            if (prescription == null) {
                model.addAttribute("errorMessage", "Prescription not found");
                return "updatePrescription";
            }

            Map<Integer, Integer> updatedDrugs = new HashMap<>(prescription.getPrescribedDrugs());

            if ("update".equals(action)) {
                // Update existing drug quantities
                for (Map.Entry<String, String> entry : allParams.entrySet()) {
                    if (entry.getKey().startsWith("quantities[") && entry.getKey().endsWith("]")) {
                        int drugId = Integer.parseInt(entry.getKey().substring(11, entry.getKey().length() - 1));
                        int quantity = Integer.parseInt(entry.getValue());
                        updatedDrugs.put(drugId, quantity);
                    }
                }

                // Add new drug if provided
                String newDrugIdStr = allParams.get("newDrugId");
                String newQuantityStr = allParams.get("newQuantity");
                if (newDrugIdStr != null && !newDrugIdStr.isEmpty() && newQuantityStr != null && !newQuantityStr.isEmpty()) {
                    int newDrugId = Integer.parseInt(newDrugIdStr);
                    int newQuantity = Integer.parseInt(newQuantityStr);
                    updatedDrugs.put(newDrugId, newQuantity);
                }
            } else if (action != null && action.startsWith("remove_")) {
                int drugIdToRemove = Integer.parseInt(action.split("_")[1]);
                updatedDrugs.remove(drugIdToRemove);
            }

            // Send update request to backend
            String updateUrl = SERVICE_URL + "/prescriptions/updatePrescription/" + prescriptionId;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<Integer, Integer>> request = new HttpEntity<>(updatedDrugs, headers);
            ResponseEntity<Prescription> response = restTemplate().exchange(updateUrl, HttpMethod.PUT, request, Prescription.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return "redirect:/viewPrescription/" + prescriptionId;
            } else {
                model.addAttribute("errorMessage", "Failed to update prescription");
                return "updatePrescription";
            }
        } catch (Exception ex) {
            model.addAttribute("errorMessage", "Error updating prescription: " + ex.getMessage());
            return "updatePrescription";
        }
    }



    @PostMapping("/deletePrescription/{prescriptionId}")
    public String deletePrescription(@PathVariable int prescriptionId, Model model) {
        String url = SERVICE_URL + "/prescriptions/deletePrescription/" + prescriptionId;
        ResponseEntity<Void> response = restTemplate().exchange(url, HttpMethod.DELETE, null, Void.class);
        
        if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
            model.addAttribute("successMessage", "Prescription deleted successfully");
        } else {
            model.addAttribute("errorMessage", "Failed to delete Prescription");
        }
        return "redirect:/viewPrescriptions";
    }

    // Billing Management
    
    @PostMapping("/generateBill/{prescriptionId}")
    public String generateBill(@PathVariable int prescriptionId, Model model) {
        String url = SERVICE_URL + "/billings/generateBill/" + prescriptionId;
        try {
            ResponseEntity<Billing> response = restTemplate().postForEntity(url, null, Billing.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return "redirect:/viewBillings";
            } else {
                model.addAttribute("errorMessage", "Failed to generate bill");
                return "redirect:/viewPrescriptions";
            }
        } catch (Exception ex) {
            model.addAttribute("errorMessage", "Error generating bill: " + ex.getMessage());
            return "redirect:/viewPrescriptions";
        }
    }

    @GetMapping("/viewBillings")
    public String viewAllBillings(Model model) {
        String url = SERVICE_URL + "/billings/getAllBillings";
        ResponseEntity<List<Billing>> response = restTemplate().exchange(
            url, 
            HttpMethod.GET, 
            null, 
            new ParameterizedTypeReference<List<Billing>>() {}
        );
        List<Billing> billings = response.getBody();
        
        model.addAttribute("billings", billings);
        return "viewBillings";
    }

    @GetMapping("/printBill/{billingId}")
    public String printBill(@PathVariable int billingId, Model model) {
        String url = SERVICE_URL + "/billings/getBilling/" + billingId;
        ResponseEntity<Billing> response = restTemplate().getForEntity(url, Billing.class);
        Billing billing = response.getBody();
        
        if (billing != null) {
            model.addAttribute("billing", billing);
            return "printBill";
        } else {
            return "redirect:/viewBillings";
        }
    }
}