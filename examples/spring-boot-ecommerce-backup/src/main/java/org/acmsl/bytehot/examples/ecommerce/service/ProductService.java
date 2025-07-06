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
 * Filename: ProductService.java
 *
 * Author: Claude Code
 *
 * Class name: ProductService
 *
 * Responsibilities:
 *   - Implement business logic for product management
 *   - Provide hot-swappable pricing and inventory rules
 *   - Coordinate product operations with repository layer
 *
 * Collaborators:
 *   - ProductRepository: Data access layer
 *   - Product: Domain model
 *   - PricingService: Pricing calculations
 */
package org.acmsl.bytehot.examples.ecommerce.service;

import org.acmsl.bytehot.examples.ecommerce.model.Product;
import org.acmsl.bytehot.examples.ecommerce.model.ProductCategory;
import org.acmsl.bytehot.examples.ecommerce.model.ProductStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Service class for product management with hot-swappable business logic.
 * Demonstrates ByteHot capabilities by allowing runtime changes to business rules.
 * @author Claude Code
 * @since 2025-07-04
 */
@Service
@Transactional
public class ProductService {

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(ProductService.class.getName());

    /**
     * Product repository for data access.
     */
    private final ProductRepository productRepository;

    /**
     * Pricing service for price calculations.
     */
    private final PricingService pricingService;

    /**
     * Creates a new ProductService.
     * @param productRepository The product repository
     * @param pricingService The pricing service
     */
    @Autowired
    public ProductService(final ProductRepository productRepository,
                         final PricingService pricingService) {
        this.productRepository = productRepository;
        this.pricingService = pricingService;
    }

    /**
     * Creates a new product.
     * @param product The product to create
     * @return The created product
     */
    public Product createProduct(final Product product) {
        LOGGER.info("Creating new product: " + product.getName());
        
        // Apply business rules (can be hot-swapped)
        validateProductForCreation(product);
        applyCreationDefaults(product);
        
        Product savedProduct = productRepository.save(product);
        LOGGER.info("Product created successfully with ID: " + savedProduct.getId());
        
        return savedProduct;
    }

    /**
     * Updates an existing product.
     * @param id The product ID
     * @param updatedProduct The updated product data
     * @return The updated product
     */
    @CacheEvict(value = "products", key = "#id")
    public Product updateProduct(final Long id, final Product updatedProduct) {
        LOGGER.info("Updating product with ID: " + id);
        
        Product existingProduct = getProductById(id)
            .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));
        
        // Apply business rules (can be hot-swapped)
        validateProductForUpdate(existingProduct, updatedProduct);
        
        existingProduct.updateInfo(
            updatedProduct.getName(),
            updatedProduct.getDescription(),
            updatedProduct.getPrice()
        );
        
        Product savedProduct = productRepository.save(existingProduct);
        LOGGER.info("Product updated successfully");
        
        return savedProduct;
    }

    /**
     * Gets a product by ID with caching.
     * @param id The product ID
     * @return The product if found
     */
    @Cacheable(value = "products", key = "#id")
    @Transactional(readOnly = true)
    public Optional<Product> getProductById(final Long id) {
        return productRepository.findById(id);
    }

    /**
     * Gets all active products by category.
     * @param category The product category
     * @param pageable Pagination information
     * @return Page of products
     */
    @Transactional(readOnly = true)
    public Page<Product> getActiveProductsByCategory(final ProductCategory category, 
                                                   final Pageable pageable) {
        return productRepository.findByCategoryAndStatus(category, ProductStatus.ACTIVE, pageable);
    }

    /**
     * Searches products by name or description.
     * @param searchTerm The search term
     * @param pageable Pagination information
     * @return Page of matching products
     */
    @Transactional(readOnly = true)
    public Page<Product> searchProducts(final String searchTerm, final Pageable pageable) {
        return productRepository.findByNameContainingOrDescriptionContaining(
            searchTerm, searchTerm, pageable);
    }

    /**
     * Gets products with low stock.
     * This method can be hot-swapped to change the low stock threshold.
     * @return List of products with low stock
     */
    @Transactional(readOnly = true)
    public List<Product> getProductsWithLowStock() {
        int lowStockThreshold = calculateLowStockThreshold();
        return productRepository.findByStockQuantityLessThan(lowStockThreshold);
    }

    /**
     * Calculates the recommended price for a product.
     * This method demonstrates hot-swappable pricing logic.
     * @param product The product
     * @param quantity The purchase quantity
     * @return The recommended price
     */
    public BigDecimal calculateRecommendedPrice(final Product product, final int quantity) {
        // Base price from product
        BigDecimal basePrice = product.getPrice();
        
        // Apply quantity discounts (can be hot-swapped)
        BigDecimal discountedPrice = product.calculateDiscountedPrice(quantity);
        
        // Apply additional pricing rules (can be hot-swapped)
        BigDecimal finalPrice = pricingService.applyPricingRules(product, discountedPrice, quantity);
        
        LOGGER.info(String.format("Calculated price for product %s (qty %d): %s -> %s", 
                                product.getSku(), quantity, basePrice, finalPrice));
        
        return finalPrice;
    }

    /**
     * Reserves stock for a product.
     * @param productId The product ID
     * @param quantity The quantity to reserve
     * @return The updated product
     */
    @CacheEvict(value = "products", key = "#productId")
    public Product reserveStock(final Long productId, final int quantity) {
        LOGGER.info(String.format("Reserving %d units of product %d", quantity, productId));
        
        Product product = getProductById(productId)
            .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));
        
        if (!product.isAvailable()) {
            throw new ProductUnavailableException("Product is not available for purchase");
        }
        
        product.reserveStock(quantity);
        Product savedProduct = productRepository.save(product);
        
        LOGGER.info("Stock reserved successfully");
        return savedProduct;
    }

    /**
     * Releases reserved stock.
     * @param productId The product ID
     * @param quantity The quantity to release
     * @return The updated product
     */
    @CacheEvict(value = "products", key = "#productId")
    public Product releaseStock(final Long productId, final int quantity) {
        LOGGER.info(String.format("Releasing %d units of product %d", quantity, productId));
        
        Product product = getProductById(productId)
            .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));
        
        product.releaseStock(quantity);
        Product savedProduct = productRepository.save(product);
        
        LOGGER.info("Stock released successfully");
        return savedProduct;
    }

    /**
     * Deactivates a product.
     * @param productId The product ID
     * @return The deactivated product
     */
    @CacheEvict(value = "products", key = "#productId")
    public Product deactivateProduct(final Long productId) {
        LOGGER.info("Deactivating product with ID: " + productId);
        
        Product product = getProductById(productId)
            .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));
        
        product.deactivate();
        Product savedProduct = productRepository.save(product);
        
        LOGGER.info("Product deactivated successfully");
        return savedProduct;
    }

    /**
     * Validates a product for creation.
     * This method can be hot-swapped to change validation rules.
     * @param product The product to validate
     */
    protected void validateProductForCreation(final Product product) {
        // Check for duplicate SKU
        if (productRepository.existsBySku(product.getSku())) {
            throw new DuplicateSkuException("Product with SKU " + product.getSku() + " already exists");
        }
        
        // Apply category-specific validation
        validateByCategory(product);
    }

    /**
     * Validates a product for update.
     * This method can be hot-swapped to change validation rules.
     * @param existingProduct The existing product
     * @param updatedProduct The updated product data
     */
    protected void validateProductForUpdate(final Product existingProduct, final Product updatedProduct) {
        // Price change validation (can be hot-swapped)
        if (updatedProduct.getPrice().compareTo(existingProduct.getPrice()) != 0) {
            validatePriceChange(existingProduct, updatedProduct.getPrice());
        }
    }

    /**
     * Applies creation defaults to a new product.
     * This method can be hot-swapped to change default values.
     * @param product The product
     */
    protected void applyCreationDefaults(final Product product) {
        // Set default weight if not provided
        if (product.getWeightGrams() == null) {
            product.setWeightGrams(calculateDefaultWeight(product));
        }
    }

    /**
     * Validates product by category.
     * This method can be hot-swapped to change category-specific rules.
     * @param product The product to validate
     */
    protected void validateByCategory(final Product product) {
        switch (product.getCategory()) {
            case FOOD_BEVERAGES:
                // Food products require special handling
                if (product.getWeightGrams() == null) {
                    throw new IllegalArgumentException("Food products must have weight specified");
                }
                break;
            case ELECTRONICS:
                // Electronics typically have higher minimum prices
                if (product.getPrice().compareTo(BigDecimal.valueOf(10.0)) < 0) {
                    throw new IllegalArgumentException("Electronics must have minimum price of $10");
                }
                break;
            default:
                // No special validation for other categories
                break;
        }
    }

    /**
     * Validates price changes.
     * This method can be hot-swapped to change pricing rules.
     * @param product The existing product
     * @param newPrice The new price
     */
    protected void validatePriceChange(final Product product, final BigDecimal newPrice) {
        BigDecimal currentPrice = product.getPrice();
        BigDecimal percentageChange = newPrice.subtract(currentPrice)
            .divide(currentPrice, 4, BigDecimal.ROUND_HALF_UP);
        
        // Don't allow price increases of more than 20% without approval
        if (percentageChange.compareTo(BigDecimal.valueOf(0.2)) > 0) {
            throw new IllegalArgumentException("Price increases of more than 20% require approval");
        }
    }

    /**
     * Calculates default weight for a product.
     * This method can be hot-swapped to change weight calculation logic.
     * @param product The product
     * @return The default weight in grams
     */
    protected int calculateDefaultWeight(final Product product) {
        switch (product.getCategory()) {
            case ELECTRONICS:
                return 500; // 500g default for electronics
            case CLOTHING:
                return 200; // 200g default for clothing
            case BOOKS:
                return 300; // 300g default for books
            case HOME_GARDEN:
                return 1000; // 1kg default for home & garden
            case SPORTS:
                return 800; // 800g default for sports equipment
            case HEALTH_BEAUTY:
                return 150; // 150g default for health & beauty
            case AUTOMOTIVE:
                return 2000; // 2kg default for automotive parts
            case TOYS_GAMES:
                return 400; // 400g default for toys & games
            case FOOD_BEVERAGES:
                return 250; // 250g default for food & beverages
            case GENERAL:
                return 300; // 300g default for general items
            default:
                return 300;
        }
    }

    /**
     * Calculates the low stock threshold.
     * This method can be hot-swapped to change the threshold logic.
     * @return The low stock threshold
     */
    protected int calculateLowStockThreshold() {
        // This could be configurable or based on sales velocity
        return 10;
    }

    /**
     * Exception thrown when a product is not found.
     */
    public static class ProductNotFoundException extends RuntimeException {
        public ProductNotFoundException(final String message) {
            super(message);
        }
    }

    /**
     * Exception thrown when a product is unavailable.
     */
    public static class ProductUnavailableException extends RuntimeException {
        public ProductUnavailableException(final String message) {
            super(message);
        }
    }

    /**
     * Exception thrown when a duplicate SKU is detected.
     */
    public static class DuplicateSkuException extends RuntimeException {
        public DuplicateSkuException(final String message) {
            super(message);
        }
    }
}