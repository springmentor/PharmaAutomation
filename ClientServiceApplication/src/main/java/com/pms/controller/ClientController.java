package com.pms.controller;

import com.pms.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ClientController {
    private static final Logger logger = LoggerFactory.getLogger(ClientController.class);
    private final RestTemplate restTemplate;
    private final String backendBaseUrl;

    @Autowired
    public ClientController(RestTemplate restTemplate, @Value("${backend.base-url}") String backendBaseUrl) {
        this.restTemplate = restTemplate;
        this.backendBaseUrl = backendBaseUrl;
    }

    @GetMapping("/pharma")
    public String home() {
        return "landing-page";
    }

    @GetMapping("/drugs")
    public String getDrugManagement(@RequestParam(value = "query", required = false) String query, Model model) {
        try {
            if (query != null && !query.trim().isEmpty()) {
                logger.info("Searching for drugs with query: {}", query);
                ResponseEntity<List<Drug>> response = restTemplate.exchange(
                        backendBaseUrl + "/api/drugs/search?query=" + query,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<Drug>>() {
                        }
                );
                model.addAttribute("drugs", response.getBody());
            } else {
                logger.info("Fetching all drugs from {}", backendBaseUrl + "/api/drugs");
                ResponseEntity<List<Drug>> response = restTemplate.exchange(
                        backendBaseUrl + "/api/drugs",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<Drug>>() {
                        }
                );
                model.addAttribute("drugs", response.getBody());
            }
            return "drug-management";
        } catch (Exception e) {
            logger.error("Error fetching drugs", e);
            model.addAttribute("error", "Error fetching drugs: " + e.getMessage());
            model.addAttribute("drugs", Collections.emptyList());
            return "drug-management";
        }
    }

    @GetMapping("/drugs/add")
    public String addDrugForm(Model model) {
        model.addAttribute("drug", new Drug());
        return "add-drug";
    }

    @PostMapping("/drugs/add")
    public String addDrug(@ModelAttribute Drug drug, RedirectAttributes redirectAttributes) {
        try {
            logger.info("Adding new drug: {}", drug);
            HttpEntity<Drug> request = new HttpEntity<>(drug);
            ResponseEntity<Drug> response = restTemplate.exchange(
                    backendBaseUrl + "/api/drugs/add",
                    HttpMethod.POST,
                    request,
                    Drug.class
            );
            logger.info("Drug added successfully: {}", response.getBody());
            redirectAttributes.addFlashAttribute("success", "Drug added successfully");
            return "redirect:/drugs";
        } catch (Exception e) {
            logger.error("Error adding drug", e);
            redirectAttributes.addFlashAttribute("error", "Error adding drug: " + e.getMessage());
            return "redirect:/drugs";
        }
    }

    @GetMapping("/drugs/update/{id}")
    public String updateDrugForm(@PathVariable Long id, Model model) {
        try {
            ResponseEntity<Drug> response = restTemplate.getForEntity(
                    backendBaseUrl + "/api/drugs/" + id,
                    Drug.class
            );
            model.addAttribute("drug", response.getBody());
            return "edit-drug";
        } catch (Exception e) {
            logger.error("Error fetching drug for editing", e);
            model.addAttribute("error", "Error fetching drug: " + e.getMessage());
            return "redirect:/drugs";
        }
    }

    @PostMapping("/drugs/update")
    public String updateDrug(@ModelAttribute Drug drug, RedirectAttributes redirectAttributes) {
        try {
            HttpEntity<Drug> request = new HttpEntity<>(drug);
            ResponseEntity<Drug> response = restTemplate.exchange(
                    backendBaseUrl + "/api/drugs/update",

                    HttpMethod.PUT,
                    request,
                    Drug.class
            );
            if (response.getStatusCode().is2xxSuccessful()) {
                redirectAttributes.addFlashAttribute("success", "Drug updated successfully");
            } else {
                redirectAttributes.addFlashAttribute("error", "Error updating drug: " + response.getStatusCode());
            }
            return "redirect:/drugs";
        } catch (Exception e) {
            logger.error("Error updating drug", e);
            redirectAttributes.addFlashAttribute("error", "Error updating drug: " + e.getMessage());
            return "redirect:/drugs";
        }
    }

    @PostMapping("/drugs/delete")
    public String deleteDrug(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            restTemplate.delete(backendBaseUrl + "/api/drugs/delete?id=" + id);
            redirectAttributes.addFlashAttribute("success", "Drug deleted successfully");
            return "redirect:/drugs";
        } catch (Exception e) {
            logger.error("Error deleting drug", e);
            redirectAttributes.addFlashAttribute("error", "Error deleting drug: " + e.getMessage());
            return "redirect:/drugs";
        }
    }

    @PostMapping("/drugs/deactivate")
    public String deactivateDrug(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            restTemplate.postForObject(backendBaseUrl + "/api/drugs/deactivate?id=" + id, null, Void.class);
            redirectAttributes.addFlashAttribute("success", "Drug deactivated successfully");
            return "redirect:/drugs";
        } catch (Exception e) {
            logger.error("Error deactivating drug", e);
            redirectAttributes.addFlashAttribute("error", "Error deactivating drug: " + e.getMessage());
            return "redirect:/drugs";
        }
    }

    @GetMapping("/drugs/filter")
    public String getDrugsWithFilter(@RequestParam(defaultValue = "none") String filter, Model model) {
        try {
            String apiUrl = backendBaseUrl + "/api/drugs/filter?";
            if ("deactivated".equals(filter)) {
                apiUrl += "active=false";
            } else if ("banned".equals(filter)) {
                apiUrl += "banned=true";
            } else {
                apiUrl += "active=true&banned=false";
            }
            ResponseEntity<List<Drug>> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Drug>>() {
                    }
            );
            List<Drug> filteredDrugs = response.getBody();
            model.addAttribute("drugs", filteredDrugs);
            model.addAttribute("currentFilter", filter);
            logger.debug("Filter applied: " + filter);
            logger.debug("Filtered Drugs: " + filteredDrugs);
        } catch (Exception e) {
            logger.error("Error fetching drugs with filter: " + filter, e);
            model.addAttribute("error", "Error fetching drugs: " + e.getMessage());
            model.addAttribute("drugs", Collections.emptyList());
        }
        return "drug-management";
    }

    @GetMapping("/stocks")
    public String getStockManagement(@RequestParam(value = "query", required = false) String query, Model model) {
        try {
            ResponseEntity<List<Stock>> stockResponse = restTemplate.exchange(
                    backendBaseUrl + "/api/stocks",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Stock>>() {
                    }
            );
            List<Stock> allStocks = stockResponse.getBody();
            List<Drug> filteredDrugs = new ArrayList<>();
            if (query != null && !query.trim().isEmpty()) {
                ResponseEntity<List<Drug>> drugResponse = restTemplate.exchange(
                        backendBaseUrl + "/api/drugs/search?query=" + query,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<Drug>>() {
                        }
                );
                filteredDrugs = drugResponse.getBody();
                List<Long> filteredDrugIds = filteredDrugs.stream()
                        .map(Drug::getId)
                        .collect(Collectors.toList());
                allStocks = allStocks.stream()
                        .filter(stock -> filteredDrugIds.contains(stock.getDrug().getId()))
                        .collect(Collectors.toList());
            }
            ResponseEntity<List<Drug>> allDrugResponse = restTemplate.exchange(
                    backendBaseUrl + "/api/drugs",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Drug>>() {
                    }
            );
            List<Drug> allDrugs = allDrugResponse.getBody();
            model.addAttribute("stocks", allStocks);
            model.addAttribute("drugs", allDrugs);
            return "stock-management";
        } catch (Exception e) {
            logger.error("Error fetching stocks or drugs", e);
            model.addAttribute("error", "Error fetching data: " + e.getMessage());
            model.addAttribute("stocks", Collections.emptyList());
            model.addAttribute("drugs", Collections.emptyList());
            return "stock-management";
        }
    }

    @PostMapping("/stocks/add")
    public String addStock(@ModelAttribute Stock stock, RedirectAttributes redirectAttributes) {
        try {
            logger.info("Adding new stock: {}", stock);
            HttpEntity<Stock> request = new HttpEntity<>(stock);
            ResponseEntity<Stock> response = restTemplate.exchange(
                    backendBaseUrl + "/api/stocks/add",
                    HttpMethod.POST,
                    request,
                    Stock.class
            );
            logger.info("Stock added successfully: {}", response.getBody());
            redirectAttributes.addFlashAttribute("success", "Stock added successfully");
            return "redirect:/stocks";
        } catch (Exception e) {
            logger.error("Error adding stock", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage()); // Only the error message
            return "redirect:/stocks";
        }
    }

    @PostMapping("/stocks/update")
    public String updateStock(@RequestParam Long id,
                              @RequestParam Integer quantity,
                              @RequestParam Integer threshold,
                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expiryDate,
                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate manufacturingDate,
                              RedirectAttributes redirectAttributes) {
        try {
            String url = backendBaseUrl + "/api/stocks/update/" + id;
            Stock stockUpdate = new Stock();
            stockUpdate.setId(id);
            stockUpdate.setQuantity(quantity);
            stockUpdate.setThreshold(threshold);
            stockUpdate.setExpiryDate(expiryDate);
            stockUpdate.setManufacturingDate(manufacturingDate);
            logger.info("Updating stock with ID: {}, New Quantity: {}, New Threshold: {}, New Expiry Date: {}, New Manufacturing Date: {}",
                    id, quantity, threshold, expiryDate, manufacturingDate);
            HttpEntity<Stock> request = new HttpEntity<>(stockUpdate);
            ResponseEntity<Stock> response = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    request,
                    Stock.class
            );
            logger.info("Update stock response status: {}", response.getStatusCode());
            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Stock updated successfully: {}", response.getBody());
                redirectAttributes.addFlashAttribute("success", "Stock updated successfully");
            } else {
                logger.error("Error updating stock: {}", response.getStatusCode());
                redirectAttributes.addFlashAttribute("error", "Error updating stock: " + response.getStatusCode());
            }
            return "redirect:/stocks";
        } catch (Exception e) {
            logger.error("Exception occurred while updating stock", e);
            redirectAttributes.addFlashAttribute("error", "Error updating stock: " + e.getMessage());
            return "redirect:/stocks";
        }
    }

    @PostMapping("/stocks/delete")
    public String deleteStock(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            restTemplate.delete(backendBaseUrl + "/api/stocks/delete?id=" + id);
            redirectAttributes.addFlashAttribute("success", "Stock deleted successfully");
            return "redirect:/stocks";
        } catch (Exception e) {
            logger.error("Error deleting stock", e);
            redirectAttributes.addFlashAttribute("error", "Error deleting stock: " + e.getMessage());
            return "redirect:/stocks";
        }
    }

    @GetMapping("/stocks/filter")
    public String getStocksWithFilter(@RequestParam(defaultValue = "none") String filter, Model model) {
        try {
            String apiUrl = backendBaseUrl + "/api/stocks/filter?filter=" + filter;
            ResponseEntity<List<Stock>> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Stock>>() {
                    }
            );
            ResponseEntity<List<Drug>> drugResponse = restTemplate.exchange(
                    backendBaseUrl + "/api/drugs",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Drug>>() {
                    }
            );
            model.addAttribute("drugs", drugResponse.getBody());

            List<Stock> filteredStocks = response.getBody();
            model.addAttribute("stocks", filteredStocks);
            model.addAttribute("currentFilter", filter);

            // Debug log
            logger.debug("Filter: " + filter);
            logger.debug("Stocks: " + filteredStocks);
        } catch (Exception e) {
            logger.error("Error fetching stocks with filter: " + filter, e);
            model.addAttribute("error", "Error fetching stocks: " + e.getMessage());
            model.addAttribute("stocks", Collections.emptyList());
        }
        return "stock-management";
    }

    @GetMapping("/prescriptions")
    public String getAllPrescriptions(@RequestParam(value = "query", required = false) String query, Model model) {
        try {
            List<Prescription> prescriptions;
            if (query != null && !query.trim().isEmpty()) {
                ResponseEntity<List<Prescription>> response = restTemplate.exchange(
                        backendBaseUrl + "/api/prescriptions/search?patientName=" + query,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<Prescription>>() {
                        }
                );
                prescriptions = response.getBody();
            } else {
                ResponseEntity<List<Prescription>> response = restTemplate.exchange(
                        backendBaseUrl + "/api/prescriptions",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<Prescription>>() {
                        }
                );
                prescriptions = response.getBody();
            }
            ResponseEntity<List<Drug>> drugResponse = restTemplate.exchange(
                    backendBaseUrl + "/api/drugs",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Drug>>() {}
            );
            List<Drug> drugs = drugResponse.getBody();
            model.addAttribute("drugs", drugs);
            Map<Long, Bill> bills = new HashMap<>();
            for (Prescription prescription : prescriptions) {
                try {
                    ResponseEntity<Bill> billResponse = restTemplate.exchange(
                            backendBaseUrl + "/api/bills/{prescriptionId}",
                            HttpMethod.GET,
                            null,
                            Bill.class,
                            prescription.getId()
                    );
                    Bill bill = billResponse.getBody();
                    bills.put(prescription.getId(), bill);
                } catch (Exception e) {
                    bills.put(prescription.getId(), null);
                }
            }
            model.addAttribute("prescriptions", prescriptions);
            model.addAttribute("bills", bills);
            return "prescription";
        } catch (Exception e) {
            model.addAttribute("error", "Error fetching prescriptions or drugs: " + e.getMessage());
            return "prescription";
        }
    }

    @PostMapping("/prescriptions/add")
    public String addPrescription(@ModelAttribute Prescription prescription, RedirectAttributes redirectAttributes) {
        try {
            prescription.setPrescriptionDate(java.time.LocalDate.now());
            logger.info("Adding new prescription: {}", prescription);
            HttpEntity<Prescription> request = new HttpEntity<>(prescription);
            ResponseEntity<Prescription> response = restTemplate.exchange(
                    backendBaseUrl + "/api/prescriptions/add",
                    HttpMethod.POST,
                    request,
                    Prescription.class
            );
            ResponseEntity<List<Drug>> drugResponse = restTemplate.exchange(
                    backendBaseUrl + "/api/drugs",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Drug>>() {}
            );
            List<Drug> drugs = drugResponse.getBody();
            logger.info("Prescription added successfully: {}", response.getBody());
            redirectAttributes.addFlashAttribute("success", "Prescription added successfully");
            return "redirect:/prescriptions";
        } catch (Exception e) {
            logger.error("Error adding prescription", e);
            redirectAttributes.addFlashAttribute("error", "Error adding prescription: " + e.getMessage());
            return "redirect:/prescriptions";
        }
    }

    @PostMapping("/bills/generate/{prescriptionId}")
    public String generateBill(@PathVariable Long prescriptionId,
                               @RequestParam("discountPercentage") double discountPercentage,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        try {
            logger.info("Received request with prescriptionId: " + prescriptionId + " and discountPercentage: " + discountPercentage);
            ResponseEntity<Bill> response = restTemplate.exchange(
                    backendBaseUrl + "/api/bills/generate/" + prescriptionId + "?discountPercentage=" + discountPercentage,
                    HttpMethod.POST,
                    null,
                    Bill.class
            );
            Bill bill = response.getBody();
            boolean isBillGenerated = bill != null;
            redirectAttributes.addFlashAttribute("bill", bill);
            redirectAttributes.addFlashAttribute("success", "Bill generated successfully!");
            redirectAttributes.addFlashAttribute("isBillGenerated", isBillGenerated);
            return "redirect:/prescriptions";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error generating bill: " + e.getMessage());
            return "redirect:/prescriptions";
        }
    }

    @PostMapping("/prescriptions/delete")
    public String deletePrescription(@RequestParam Long prescriptionId, RedirectAttributes redirectAttributes) {
        try {
            restTemplate.delete(backendBaseUrl + "/api/prescriptions/delete/" + prescriptionId);
            redirectAttributes.addFlashAttribute("success", "Prescription deleted successfully");
            return "redirect:/prescriptions";
        } catch (Exception e) {
            // Log and handle error
            logger.error("Error deleting prescription", e);
            redirectAttributes.addFlashAttribute("error", "Error deleting prescription: " + e.getMessage());
            return "redirect:/prescriptions";
        }
    }
    @GetMapping("/prescriptions/filter")
    public String getPrescriptionsWithFilter(
            @RequestParam(required = false) String isBillGenerate,
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(required = false) String filterType,
            @RequestParam(required = false) Integer filterValue,
            Model model) {
        try {
            ResponseEntity<List<Drug>> drugResponse = restTemplate.exchange(
                    backendBaseUrl + "/api/drugs",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Drug>>() {}
            );
            List<Drug> drugs = drugResponse.getBody();
            model.addAttribute("drugs", drugs);
            Boolean isBillGenerated = null;
            if ("true".equalsIgnoreCase(isBillGenerate)) {
                isBillGenerated = true;
            } else if ("false".equalsIgnoreCase(isBillGenerate)) {
                isBillGenerated = false;
            }
            StringBuilder filterUrl = new StringBuilder(backendBaseUrl + "/api/prescriptions/filter");
            boolean hasQueryParam = false;
            if (isBillGenerated != null) {
                filterUrl.append("?isBillGenerate=").append(isBillGenerated);
                hasQueryParam = true;
            }
            if (query != null && !query.trim().isEmpty()) {
                filterUrl.append(hasQueryParam ? "&" : "?").append("query=").append(query);
            }
            if ("maxPrescribed".equals(filterType) && filterValue != null) {
                return getMaxPrescribedDrugs(filterValue, model);
            } else if ("unprescribed".equals(filterType) && filterValue != null) {
                return getUnprescribedDrugs(filterValue, model);
            }

            ResponseEntity<List<Prescription>> response = restTemplate.exchange(
                    filterUrl.toString(),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Prescription>>() {}
            );
            List<Prescription> prescriptions = response.getBody();
            Map<Long, Bill> bills = new HashMap<>();
            for (Prescription prescription : prescriptions) {
                try {
                    ResponseEntity<Bill> billResponse = restTemplate.exchange(
                            backendBaseUrl + "/api/bills/{prescriptionId}",
                            HttpMethod.GET,
                            null,
                            Bill.class,
                            prescription.getId()
                    );
                    Bill bill = billResponse.getBody();
                    bills.put(prescription.getId(), bill);
                } catch (Exception e) {
                    bills.put(prescription.getId(), null);
                }
            }
            model.addAttribute("prescriptions", prescriptions);
            model.addAttribute("bills", bills);
            return "prescription";
        } catch (Exception e) {
            logger.error("Error fetching prescriptions with filter", e);
            model.addAttribute("error", "Error fetching prescriptions with filter: " + e.getMessage());
            return "prescription";
        }
    }


    @GetMapping("/suppliers")
    public String getSupplierManagement(@RequestParam(value = "query", required = false) String query, Model model) {
        try {
            ResponseEntity<List<Supplier>> supplierResponse = restTemplate.exchange(
                    backendBaseUrl + "/api/suppliers",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Supplier>>() {}
            );
            List<Supplier> allSuppliers = supplierResponse.getBody();
            ResponseEntity<List<Drug>> drugResponse = restTemplate.exchange(
                    backendBaseUrl + "/api/drugs",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Drug>>() {}
            );
            List<Drug> drugs = drugResponse.getBody();
            List<Supplier> filteredSuppliers = new ArrayList<>();
            if (query != null && !query.trim().isEmpty()) {
                ResponseEntity<List<Drug>> filteredDrugsResponse = restTemplate.exchange(
                        backendBaseUrl + "/api/drugs/search?query=" + query,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<Drug>>() {}
                );
                List<Drug> filteredDrugs = filteredDrugsResponse.getBody();
                List<Long> filteredDrugIds = filteredDrugs.stream()
                        .map(Drug::getId)
                        .collect(Collectors.toList());
                filteredSuppliers = allSuppliers.stream()
                        .filter(supplier -> supplier.getDrug() != null && filteredDrugIds.contains(supplier.getDrug().getId()))
                        .collect(Collectors.toList());
            } else {
                filteredSuppliers = allSuppliers;
            }
            model.addAttribute("suppliers", filteredSuppliers);
            model.addAttribute("drugs", drugs);
            model.addAttribute("query", query);
            return "supplier-management";
        } catch (Exception e) {
            model.addAttribute("error", "Error fetching data: " + e.getMessage());
            model.addAttribute("suppliers", Collections.emptyList());
            return "supplier-management";
        }
    }

    @PostMapping("/suppliers/add")
    public String addSupplier(@ModelAttribute Supplier supplier, RedirectAttributes redirectAttributes) {
        try {
            // Sending POST request to add the supplier
            ResponseEntity<Supplier> response = restTemplate.exchange(
                    backendBaseUrl + "/api/suppliers/add",
                    HttpMethod.POST,
                    new HttpEntity<>(supplier),
                    Supplier.class
            );

            // Optionally fetching the list of drugs (if needed elsewhere)
            ResponseEntity<List<Drug>> drugResponse = restTemplate.exchange(
                    backendBaseUrl + "/api/drugs",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Drug>>() {}
            );

            // Add success message as a redirect attribute
            redirectAttributes.addFlashAttribute("success", "Supplier added successfully!");

            return "redirect:/suppliers";
        } catch (Exception e) {
            // Add error message as a redirect attribute
            redirectAttributes.addFlashAttribute("error", "Error adding supplier: " + e.getMessage());
            return "redirect:/suppliers";
        }
    }

    @PostMapping("/suppliers/delete")
    public String deleteSupplier(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            restTemplate.delete(backendBaseUrl + "/api/suppliers/delete/" + id);
            redirectAttributes.addFlashAttribute("success", "Supplier deleted successfully.");
            return "redirect:/suppliers";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting supplier: " + e.getMessage());
            return "redirect:/suppliers";
        }
    }
    @PostMapping("/suppliers/reorder/{supplierId}")
    public String reorder(@PathVariable Long supplierId, RedirectAttributes redirectAttributes) {
        try {
            logger.info("Initiating reorder for supplier ID: {}", supplierId);
            HttpEntity<Void> request = new HttpEntity<>(null);
            ResponseEntity<String> response = restTemplate.exchange(
                    backendBaseUrl + "/api/suppliers/send-email/" + supplierId,
                    HttpMethod.POST,
                    request,
                    String.class
            );
            logger.info("Reorder processed successfully for supplier ID: {}", supplierId);
            redirectAttributes.addFlashAttribute("success", "Reorder sent successfully for"+supplierId);
            return "redirect:/suppliers";
        } catch (Exception e) {
            logger.error("Error during reorder process", e);
            redirectAttributes.addFlashAttribute("error", "Error initiating reorder: " + e.getMessage());
            return "redirect:/suppliers";
        }
    }
    @PostMapping("/suppliers/update")
    public String updateSupplier(@RequestParam Long id,
                                 @RequestParam String email,
                                 @RequestParam String contactNumber,
                                 @RequestParam String address,
                                 RedirectAttributes redirectAttributes) {
        try {
            String url = backendBaseUrl + "/api/suppliers/update/" + id;
            Supplier supplierUpdate = new Supplier();
            supplierUpdate.setId(id);
            supplierUpdate.setEmail(email);
            supplierUpdate.setContactNumber(contactNumber);
            supplierUpdate.setAddress(address);
            logger.info("Updating supplier with ID: {}, Email: {}, Contact: {}, Address: {}",
                    id, email, contactNumber, address);

            HttpEntity<Supplier> request = new HttpEntity<>(supplierUpdate);
            ResponseEntity<Supplier> response = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    request,
                    Supplier.class
            );
            logger.info("Update supplier response status: {}", response.getStatusCode());
            redirectAttributes.addFlashAttribute("success", "Supplier updated successfully");
            return "redirect:/suppliers";
        } catch (Exception e) {
            logger.error("Exception occurred while updating supplier", e);
            redirectAttributes.addFlashAttribute("error", "Error updating supplier: " + e.getMessage());
            return "redirect:/suppliers";
        }
    }

    @GetMapping("/reports")
    public String getReports(Model model) {
        try {
            // Fetch max prescribed drugs (top 5)
            ResponseEntity<List<Map<String, Object>>> maxPrescribedResponse = restTemplate.exchange(
                    backendBaseUrl + "/api/prescriptions/max-prescribed/5",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            List<Map<String, Object>> maxPrescribedDrugs = maxPrescribedResponse.getBody();

            // Fetch unprescribed drugs for the last 30 days
            ResponseEntity<List<Drug>> unprescribedResponse = restTemplate.exchange(
                    backendBaseUrl + "/api/prescriptions/unprescribed/30",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Drug>>() {}
            );
            List<Drug> unprescribedDrugs = unprescribedResponse.getBody();

            model.addAttribute("maxPrescribedDrugs", maxPrescribedDrugs);
            model.addAttribute("unprescribedDrugs", unprescribedDrugs);

            return "reports";
        } catch (Exception e) {
            logger.error("Error fetching report data", e);
            model.addAttribute("error", "Error fetching report data: " + e.getMessage());
            return "reports";
        }
    }

    @GetMapping("/reports/max-prescribed")
    public String getMaxPrescribedDrugs(@RequestParam(defaultValue = "5") int limit, Model model) {
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    backendBaseUrl + "/api/prescriptions/max-prescribed/" + limit,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            List<Map<String, Object>> maxPrescribedDrugs = response.getBody();
            model.addAttribute("maxPrescribedDrugs", maxPrescribedDrugs);
            return "reports";
        } catch (Exception e) {
            logger.error("Error fetching max prescribed drugs", e);
            model.addAttribute("error", "Error fetching max prescribed drugs: " + e.getMessage());
            return "reports";
        }
    }

    @GetMapping("/reports/unprescribed")
    public String getUnprescribedDrugs(@RequestParam(defaultValue = "30") int days, Model model) {
        try {
            ResponseEntity<List<Drug>> response = restTemplate.exchange(
                    backendBaseUrl + "/api/prescriptions/unprescribed/" + days,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Drug>>() {}
            );
            List<Drug> unprescribedDrugs = response.getBody();
            model.addAttribute("unprescribedDrugs", unprescribedDrugs);
            return "reports";
        } catch (Exception e) {
            logger.error("Error fetching unprescribed drugs", e);
            model.addAttribute("error", "Error fetching unprescribed drugs: " + e.getMessage());
            return "reports";
        }
    }
}

