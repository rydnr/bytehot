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
 *   - Expose REST API endpoints for product management
 *   - Handle HTTP requests and responses for product operations
 *   - Demonstrate hot-swappable API behavior and business logic
 *
 * Collaborators:
 *   - ProductService: Business logic for product operations
 *   - Product: Domain model for product data
 */
package org.acmsl.bytehot.examples.ecommerce.controller;

import org.acmsl.bytehot.examples.ecommerce.model.Product;
import org.acmsl.bytehot.examples.ecommerce.model.ProductCategory;
import org.acmsl.bytehot.examples.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * REST controller for product management operations.
 * Demonstrates ByteHot capabilities by allowing hot-swapping of API behavior.
 * @author Claude Code
 * @since 2025-07-04
 */
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(ProductController.class.getName());

    /**
     * Product service for business operations.
     */
    private final ProductService productService;

    /**
     * Creates a new ProductController.
     * @param productService The product service
     */
    @Autowired
    public ProductController(final ProductService productService) {
        this.productService = productService;
    }

    /**
     * Creates a new product.
     * @param product The product to create
     * @return The created product
     */
    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody final Product product) {
        LOGGER.info("Creating product via API: " + product.getName());
        
        try {
            Product createdProduct = productService.createProduct(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
        } catch (ProductService.DuplicateSkuException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Gets a product by ID.
     * @param id The product ID
     * @return The product if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable final Long id) {
        LOGGER.info("Getting product by ID: " + id);
        
        Optional<Product> product = productService.getProductById(id);
        return product.map(p -> ResponseEntity.ok(p))
                     .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Updates an existing product.
     * @param id The product ID
     * @param product The updated product data
     * @return The updated product
     */
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable final Long id,
                                               @Valid @RequestBody final Product product) {
        LOGGER.info("Updating product via API: " + id);
        
        try {
            Product updatedProduct = productService.updateProduct(id, product);
            return ResponseEntity.ok(updatedProduct);
        } catch (ProductService.ProductNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Deactivates a product.
     * @param id The product ID
     * @return No content response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateProduct(@PathVariable final Long id) {
        LOGGER.info("Deactivating product via API: " + id);
        
        try {
            productService.deactivateProduct(id);
            return ResponseEntity.noContent().build();
        } catch (ProductService.ProductNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Gets products by category with pagination.
     * @param category The product category
     * @param page The page number (default 0)
     * @param size The page size (default 20)
     * @param sort The sort field (default "name")
     * @return Page of products
     */
    @GetMapping
    public ResponseEntity<Page<Product>> getProductsByCategory(
            @RequestParam(required = false) final ProductCategory category,
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "20") final int size,
            @RequestParam(defaultValue = "name") final String sort) {
        
        LOGGER.info(String.format("Getting products by category: %s (page %d, size %d)", 
                                category, page, size));
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        
        Page<Product> products;
        if (category != null) {
            products = productService.getActiveProductsByCategory(category, pageable);
        } else {
            // If no category specified, return all active products
            // This would require an additional method in ProductService
            products = Page.empty(pageable);
        }
        
        return ResponseEntity.ok(products);
    }

    /**
     * Searches products by name or description.
     * @param q The search query
     * @param page The page number (default 0)
     * @param size The page size (default 20)
     * @return Page of matching products
     */
    @GetMapping("/search")
    public ResponseEntity<Page<Product>> searchProducts(
            @RequestParam final String q,
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "20") final int size) {
        
        LOGGER.info(String.format("Searching products with query: '%s'", q));
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("name"));
        Page<Product> products = productService.searchProducts(q, pageable);
        
        return ResponseEntity.ok(products);
    }

    /**
     * Gets products with low stock.
     * @return List of products with low stock
     */
    @GetMapping("/low-stock")
    public ResponseEntity<List<Product>> getProductsWithLowStock() {
        LOGGER.info("Getting products with low stock");
        
        List<Product> products = productService.getProductsWithLowStock();
        return ResponseEntity.ok(products);
    }

    /**
     * Calculates recommended price for a product.
     * This endpoint demonstrates hot-swappable pricing logic.
     * @param id The product ID
     * @param quantity The purchase quantity (default 1)
     * @return The recommended price
     */
    @GetMapping("/{id}/price")
    public ResponseEntity<Map<String, Object>> calculatePrice(
            @PathVariable final Long id,
            @RequestParam(defaultValue = "1") final int quantity) {
        
        LOGGER.info(String.format("Calculating price for product %d with quantity %d", id, quantity));
        
        try {
            Optional<Product> productOpt = productService.getProductById(id);
            if (productOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Product product = productOpt.get();
            BigDecimal recommendedPrice = productService.calculateRecommendedPrice(product, quantity);
            
            Map<String, Object> response = Map.of(
                "productId", id,
                "originalPrice", product.getPrice(),
                "recommendedPrice", recommendedPrice,
                "quantity", quantity,
                "totalPrice", recommendedPrice.multiply(BigDecimal.valueOf(quantity))
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LOGGER.severe("Error calculating price: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Reserves stock for a product.
     * @param id The product ID
     * @param quantity The quantity to reserve
     * @return The updated product
     */
    @PostMapping("/{id}/reserve")
    public ResponseEntity<Product> reserveStock(@PathVariable final Long id,
                                              @RequestParam final int quantity) {
        LOGGER.info(String.format("Reserving %d units of product %d", quantity, id));
        
        try {
            Product updatedProduct = productService.reserveStock(id, quantity);
            return ResponseEntity.ok(updatedProduct);
        } catch (ProductService.ProductNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (ProductService.ProductUnavailableException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Releases reserved stock for a product.
     * @param id The product ID
     * @param quantity The quantity to release
     * @return The updated product
     */
    @PostMapping("/{id}/release")
    public ResponseEntity<Product> releaseStock(@PathVariable final Long id,
                                              @RequestParam final int quantity) {
        LOGGER.info(String.format("Releasing %d units of product %d", quantity, id));
        
        try {
            Product updatedProduct = productService.releaseStock(id, quantity);
            return ResponseEntity.ok(updatedProduct);
        } catch (ProductService.ProductNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Gets API information and demonstrates hot-swappable responses.
     * This method can be modified at runtime to change API behavior.
     * @return API information
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getApiInfo() {
        LOGGER.info("Getting API information");
        
        Map<String, Object> info = Map.of(
            "service", "ByteHot E-Commerce Product API",
            "version", "1.0.0",
            "hotSwapEnabled", true,
            "supportedOperations", List.of(
                "create", "read", "update", "deactivate", 
                "search", "pricing", "inventory"
            ),
            "message", "This API demonstrates ByteHot's hot-swapping capabilities!"
        );
        
        return ResponseEntity.ok(info);
    }
}