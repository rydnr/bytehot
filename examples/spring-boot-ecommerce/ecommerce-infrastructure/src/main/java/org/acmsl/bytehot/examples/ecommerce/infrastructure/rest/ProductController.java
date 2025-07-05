/*
                        ByteHot

    Copyright (C) 2025-today  rydnr@acm-sl.org

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 3 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public v3
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    Thanks to ACM S.L. for distributing this library under the GPLv3 license.
    Contact info: jose.sanleandro@acm-sl.com

 ******************************************************************************
 *
 * Filename: ProductController.java
 *
 * Author: Claude Code
 *
 * Class name: ProductController
 *
 * Responsibilities:
 *   - Handle HTTP requests for product operations
 *   - Transform HTTP requests to domain events
 *   - Provide REST API for e-commerce product management
 *
 * Collaborators:
 *   - ECommerceApplication: Processes domain events
 *   - ProductCreationRequestedEvent: Domain event for product creation
 *   - Spring Boot: REST framework
 */
package org.acmsl.bytehot.examples.ecommerce.infrastructure.rest;

import org.acmsl.bytehot.examples.ecommerce.application.ECommerceApplication;
import org.acmsl.bytehot.examples.ecommerce.domain.Money;
import org.acmsl.bytehot.examples.ecommerce.domain.ProductCategory;
import org.acmsl.bytehot.examples.ecommerce.domain.ProductCreationRequestedEvent;
import org.acmsl.commons.patterns.DomainResponseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Logger;

/**
 * REST controller for product management operations.
 * Demonstrates ByteHot hot-swapping in Spring Boot REST APIs.
 * @author Claude Code
 * @since 2025-07-04
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    /**
     * Logger for controller operations.
     */
    private static final Logger LOGGER = Logger.getLogger(ProductController.class.getName());

    /**
     * E-commerce application for processing domain events.
     */
    @Autowired
    private ECommerceApplication ecommerceApplication;

    /**
     * Health check endpoint.
     * @return Simple health status
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("E-Commerce Product API is running with ByteHot hot-swapping enabled");
    }

    /**
     * Creates a new product.
     * This method can be hot-swapped to change API behavior.
     * @param request The product creation request
     * @return Response with created product or error
     */
    @PostMapping
    public ResponseEntity<?> createProduct(@Valid @RequestBody final ProductCreationRequest request) {
        LOGGER.info("Creating product: " + request.getName());
        
        try {
            // Transform REST request to domain event (can be hot-swapped)
            ProductCreationRequestedEvent event = transformToEvent(request);
            
            // Process through domain layer
            List<DomainResponseEvent<ProductCreationRequestedEvent>> results = 
                ecommerceApplication.processProductCreationRequest(event);
            
            // Transform domain response to REST response (can be hot-swapped)
            return transformToResponse(results);
            
        } catch (Exception e) {
            LOGGER.severe("Error creating product: " + e.getMessage());
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("Failed to create product: " + e.getMessage()));
        }
    }

    /**
     * Transforms REST request to domain event.
     * This method can be hot-swapped to change request mapping.
     * @param request The REST request
     * @return Domain event
     */
    protected ProductCreationRequestedEvent transformToEvent(final ProductCreationRequest request) {
        Money price = new Money(new BigDecimal(request.getPrice()), request.getCurrency());
        ProductCategory category = ProductCategory.valueOf(request.getCategory().toUpperCase());
        
        return new ProductCreationRequestedEvent(
            request.getName(),
            request.getDescription(),
            price,
            request.getStockQuantity(),
            category,
            request.getSku(),
            request.getWeightGrams(),
            "rest-api"
        );
    }

    /**
     * Transforms domain response to REST response.
     * This method can be hot-swapped to change response mapping.
     * @param results Domain response events
     * @return HTTP response entity
     */
    protected ResponseEntity<?> transformToResponse(final List<DomainResponseEvent<ProductCreationRequestedEvent>> results) {
        if (results.isEmpty()) {
            return ResponseEntity.internalServerError()
                .body(new ErrorResponse("No response from domain layer"));
        }
        
        DomainResponseEvent<ProductCreationRequestedEvent> result = results.get(0);
        String resultType = result.getClass().getSimpleName();
        
        // Handle different response types (can be hot-swapped)
        if (resultType.contains("Created")) {
            return ResponseEntity.ok(new ProductCreationResponse("Product created successfully", result.toString()));
        } else if (resultType.contains("Rejected")) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("Product creation rejected: " + result.toString()));
        } else {
            return ResponseEntity.internalServerError()
                .body(new ErrorResponse("Unknown response type: " + resultType));
        }
    }

    /**
     * Gets pricing information for a product category.
     * This method demonstrates hot-swappable pricing logic.
     * @param category The product category
     * @param quantity The quantity
     * @return Pricing information
     */
    @GetMapping("/pricing")
    public ResponseEntity<PricingResponse> getPricing(final String category, final int quantity) {
        try {
            ProductCategory productCategory = ProductCategory.valueOf(category.toUpperCase());
            
            // Calculate pricing (can be hot-swapped)
            double basePrice = getBasePriceForCategory(productCategory);
            double discountMultiplier = getDiscountMultiplier(productCategory, quantity);
            double finalPrice = basePrice * discountMultiplier;
            double taxRate = productCategory.getTaxRate();
            double shippingMultiplier = productCategory.getShippingWeightMultiplier();
            
            PricingResponse response = new PricingResponse(
                basePrice, finalPrice, taxRate, shippingMultiplier, 
                productCategory.supportsExpeditedShipping()
            );
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Gets base price for a category.
     * This method can be hot-swapped to change pricing policies.
     * @param category The product category
     * @return Base price
     */
    protected double getBasePriceForCategory(final ProductCategory category) {
        switch (category) {
            case ELECTRONICS: return 99.99;
            case CLOTHING: return 29.99;
            case BOOKS: return 14.99;
            case HOME_GARDEN: return 49.99;
            case SPORTS: return 39.99;
            case HEALTH_BEAUTY: return 19.99;
            case AUTOMOTIVE: return 79.99;
            case TOYS_GAMES: return 24.99;
            case FOOD_BEVERAGES: return 9.99;
            case GENERAL: return 19.99;
            default: return 19.99;
        }
    }

    /**
     * Gets discount multiplier based on category and quantity.
     * This method can be hot-swapped to change discount policies.
     * @param category The product category
     * @param quantity The quantity
     * @return Discount multiplier
     */
    protected double getDiscountMultiplier(final ProductCategory category, final int quantity) {
        switch (category) {
            case BOOKS:
                if (quantity >= 5) return 0.85; // 15% discount for 5+ books
                break;
            case CLOTHING:
                if (quantity >= 3) return 0.90; // 10% discount for 3+ clothing items
                break;
            case ELECTRONICS:
                if (quantity >= 2) return 0.95; // 5% discount for 2+ electronics
                break;
            default:
                if (quantity >= 10) return 0.90; // 10% discount for 10+ items
                if (quantity >= 5) return 0.95;  // 5% discount for 5+ items
        }
        
        return 1.0; // No discount
    }

    // Inner classes for request/response DTOs

    /**
     * DTO for product creation requests.
     */
    public static class ProductCreationRequest {
        private String name;
        private String description;
        private String price;
        private String currency = "USD";
        private Integer stockQuantity;
        private String category;
        private String sku;
        private Integer weightGrams;

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getPrice() { return price; }
        public void setPrice(String price) { this.price = price; }
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
        public Integer getStockQuantity() { return stockQuantity; }
        public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getSku() { return sku; }
        public void setSku(String sku) { this.sku = sku; }
        public Integer getWeightGrams() { return weightGrams; }
        public void setWeightGrams(Integer weightGrams) { this.weightGrams = weightGrams; }
    }

    /**
     * DTO for successful product creation responses.
     */
    public static class ProductCreationResponse {
        private final String message;
        private final String details;

        public ProductCreationResponse(String message, String details) {
            this.message = message;
            this.details = details;
        }

        public String getMessage() { return message; }
        public String getDetails() { return details; }
    }

    /**
     * DTO for error responses.
     */
    public static class ErrorResponse {
        private final String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() { return error; }
    }

    /**
     * DTO for pricing responses.
     */
    public static class PricingResponse {
        private final double basePrice;
        private final double finalPrice;
        private final double taxRate;
        private final double shippingMultiplier;
        private final boolean expeditedShippingAvailable;

        public PricingResponse(double basePrice, double finalPrice, double taxRate, 
                             double shippingMultiplier, boolean expeditedShippingAvailable) {
            this.basePrice = basePrice;
            this.finalPrice = finalPrice;
            this.taxRate = taxRate;
            this.shippingMultiplier = shippingMultiplier;
            this.expeditedShippingAvailable = expeditedShippingAvailable;
        }

        public double getBasePrice() { return basePrice; }
        public double getFinalPrice() { return finalPrice; }
        public double getTaxRate() { return taxRate; }
        public double getShippingMultiplier() { return shippingMultiplier; }
        public boolean isExpeditedShippingAvailable() { return expeditedShippingAvailable; }
    }
}