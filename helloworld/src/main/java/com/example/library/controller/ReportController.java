package com.example.library.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.library.model.Category;
import com.example.library.service.CategoryService;
import com.example.library.service.TransactionService;

@Controller
@RequestMapping("/reports")
public class ReportController {
    private final TransactionService transactionService;
    private final CategoryService categoryService;
    
    @Autowired
    public ReportController(TransactionService transactionService, CategoryService categoryService) {
        this.transactionService = transactionService;
        this.categoryService = categoryService;
    }
    
    @GetMapping("/transactions")
    public String transactionReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String status,
            Model model) {
        
        
        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(3);
        }
        
        if (endDate == null) {
            endDate = LocalDate.now().plusMonths(3); 
        }
        
        System.out.println("Report date range: " + startDate + " to " + endDate);
        System.out.println("Status filter: " + status);
  
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        
        
        List<Map<String, Object>> reportData = transactionService.getTransactionReport(startDate, endDate, status);
        model.addAttribute("reportData", reportData);
        
       
        List<Map<String, Object>> detailedReportData = transactionService.getDetailedTransactionReport(startDate, endDate, status);
        System.out.println("Detailed report size: " + (detailedReportData != null ? detailedReportData.size() : "null"));
        model.addAttribute("detailedReportData", detailedReportData);
        
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("status", status);
        
        return "reports/transaction-report";
    }
}