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
 * Filename: Product.java
 *
 * Author: Claude Code
 *
 * Class name: Product
 *
 * Responsibilities:
 *   - Act as aggregate root for product management
 *   - Enforce business invariants and rules for products
 *   - Process product-related domain events with hot-swappable logic
 *
 * Collaborators:
 *   - ProductCreatedEvent: Domain event for product creation
 *   - ProductUpdatedEvent: Domain event for product updates
 *   - ProductStatus: Value object for product status
 *   - ProductCategory: Value object for product categorization
 *   - Money: Value object for monetary amounts
 */
package org.acmsl.bytehot.examples.ecommerce.domain;

import org.acmsl.commons.patterns.DomainEvent;
import org.acmsl.commons.patterns.DomainResponseEvent;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Product aggregate root representing items in the e-commerce catalog.
 * Demonstrates ByteHot hot-swapping capabilities in business logic.
 * @author Claude Code
 * @since 2025-07-04
 */
public class Product {

    /**
     * Unique product identifier.
     */
    @NotNull
    private final String productId;

    /**
     * Product name.
     */
    @NotBlank
    private String name;

    /**
     * Product description.
     */
    private String description;

    /**
     * Product price.
     */
    @NotNull
    private Money price;

    /**
     * Available stock quantity.
     */
    @NotNull
    private Integer stockQuantity;

    /**
     * Product category.
     */
    @NotNull
    private final ProductCategory category;

    /**
     * Product status.
     */
    @NotNull
    private ProductStatus status;

    /**
     * SKU (Stock Keeping Unit).
     */
    @NotBlank
    private final String sku;

    /**
     * Product weight in grams.
     */
    private Integer weightGrams;

    /**
     * When the product was created.
     */
    @NotNull
    private final Instant createdAt;

    /**
     * When the product was last updated.
     */
    @NotNull
    private Instant updatedAt;

    /**
     * Version for optimistic locking.
     */
    private Long version;

    /**
     * Creates a new Product aggregate.
     * @param name The product name
     * @param description The product description
     * @param price The product price
     * @param stockQuantity The initial stock quantity
     * @param category The product category
     * @param sku The product SKU
     */
    public Product(final String name,
                   final String description,
                   final Money price,
                   final Integer stockQuantity,
                   final ProductCategory category,
                   final String sku) {
        this.productId = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.category = category;
        this.sku = sku;
        this.status = ProductStatus.PENDING;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.version = 1L;
    }

    /**
     * Primary port: accepts ProductCreationRequestedEvent and processes it.
     * This method can be hot-swapped to change product creation logic.
     * @param event The incoming product creation request event
     * @return List of resulting domain events
     */
    public static List<DomainResponseEvent<ProductCreationRequestedEvent>> accept(
            final ProductCreationRequestedEvent event) {
        List<DomainResponseEvent<ProductCreationRequestedEvent>> results = new ArrayList<>();
        
        try {
            // Create product from event (can be hot-swapped)
            Product product = createFromEvent(event);
            
            // Validate product (can be hot-swapped)
            if (!product.isValid()) {
                results.add(new ProductCreationRejectedEvent(event, "Product validation failed"));
                return results;
            }
            
            // Apply business rules (can be hot-swapped)
            if (!product.meetsBusinessRules()) {
                results.add(new ProductCreationRejectedEvent(event, "Business rules violation"));
                return results;
            }
            
            // Activate if meets criteria (can be hot-swapped)
            if (product.shouldAutoActivate()) {
                product.activate();
            }
            
            results.add(new ProductCreatedEvent(event, product));
            
        } catch (Exception e) {
            results.add(new ProductCreationRejectedEvent(event, "Creation error: " + e.getMessage()));
        }
        
        return results;
    }

    /**
     * Creates product from domain event.
     * This method can be hot-swapped to change event mapping.
     * @param event The product creation requested event
     * @return New product instance
     */
    protected static Product createFromEvent(final ProductCreationRequestedEvent event) {
        return new Product(
            event.getName(),
            event.getDescription(),
            event.getPrice(),
            event.getStockQuantity(),
            event.getCategory(),
            event.getSku()
        );
    }

    /**
     * Validates product according to domain rules.
     * This method can be hot-swapped to change validation logic.
     * @return true if valid, false otherwise
     */
    protected boolean isValid() {
        // Basic validation (can be hot-swapped)
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        
        if (price == null || !price.isPositive()) {
            return false;
        }
        
        if (stockQuantity == null || stockQuantity < 0) {
            return false;
        }
        
        if (sku == null || sku.trim().isEmpty()) {
            return false;
        }
        
        // Category-specific validation (can be hot-swapped)
        if (category.requiresSpecialHandling() && (description == null || description.trim().isEmpty())) {
            return false; // Special handling categories require description
        }
        
        return true;
    }

    /**
     * Checks if product meets business rules.
     * This method can be hot-swapped to change business rule evaluation.
     * @return true if rules are met, false otherwise
     */
    protected boolean meetsBusinessRules() {
        // SKU format validation (can be hot-swapped)
        if (!isValidSkuFormat(sku)) {
            return false;
        }
        
        // Price validation based on category (can be hot-swapped)
        if (!isValidPriceForCategory()) {
            return false;
        }
        
        // Weight validation (can be hot-swapped)
        if (weightGrams != null && weightGrams <= 0) {
            return false;
        }
        
        return true;
    }

    /**
     * Determines if product should be auto-activated.
     * This method can be hot-swapped to change activation criteria.
     * @return true if should auto-activate, false otherwise
     */
    protected boolean shouldAutoActivate() {
        // Auto-activate if all conditions are met (can be hot-swapped)
        return stockQuantity > 0 && 
               price.isPositive() && 
               !category.requiresSpecialHandling();
    }

    /**
     * Validates SKU format.
     * This method can be hot-swapped to change SKU validation rules.
     * @param sku The SKU to validate
     * @return true if valid format, false otherwise
     */
    protected boolean isValidSkuFormat(final String sku) {
        // Simple SKU format: 3-20 alphanumeric characters (can be hot-swapped)
        return sku.matches("^[A-Z0-9]{3,20}$");
    }

    /**
     * Validates price for the product category.
     * This method can be hot-swapped to change category pricing rules.
     * @return true if valid price, false otherwise
     */
    protected boolean isValidPriceForCategory() {
        BigDecimal amount = price.getAmount();
        
        switch (category) {
            case ELECTRONICS:
                return amount.compareTo(BigDecimal.valueOf(10.00)) >= 0; // Min $10 for electronics
            case AUTOMOTIVE:
                return amount.compareTo(BigDecimal.valueOf(5.00)) >= 0; // Min $5 for automotive
            case BOOKS:
                return amount.compareTo(BigDecimal.valueOf(1.00)) >= 0; // Min $1 for books
            default:
                return amount.compareTo(BigDecimal.valueOf(0.50)) >= 0; // Min $0.50 for others
        }
    }

    /**
     * Checks if the product is available for purchase.
     * This method can be hot-swapped to change availability logic.
     * @return true if available, false otherwise
     */
    public boolean isAvailable() {
        return status.isPurchasable() && stockQuantity > 0;
    }

    /**
     * Calculates the discounted price based on quantity.
     * This method demonstrates hot-swappable pricing logic.
     * @param quantity The quantity being purchased
     * @return The discounted price per unit
     */
    public Money calculateDiscountedPrice(final int quantity) {
        BigDecimal baseAmount = price.getAmount();
        BigDecimal discountMultiplier = getQuantityDiscountMultiplier(quantity);
        
        return new Money(baseAmount.multiply(discountMultiplier), price.getCurrency());
    }

    /**
     * Gets quantity discount multiplier.
     * This method can be hot-swapped to change discount policies.
     * @param quantity The purchase quantity
     * @return Discount multiplier
     */
    protected BigDecimal getQuantityDiscountMultiplier(final int quantity) {
        // Category-specific discount logic (can be hot-swapped)
        switch (category) {
            case BOOKS:
                if (quantity >= 5) return BigDecimal.valueOf(0.85); // 15% discount for 5+ books
                break;
            case CLOTHING:
                if (quantity >= 3) return BigDecimal.valueOf(0.90); // 10% discount for 3+ clothing items
                break;
            case ELECTRONICS:
                if (quantity >= 2) return BigDecimal.valueOf(0.95); // 5% discount for 2+ electronics
                break;
            default:
                // Standard quantity discount (can be hot-swapped)
                if (quantity >= 10) return BigDecimal.valueOf(0.90); // 10% discount
                if (quantity >= 5) return BigDecimal.valueOf(0.95);  // 5% discount
        }
        
        return BigDecimal.ONE; // No discount
    }

    /**
     * Checks if sufficient stock is available.
     * @param requestedQuantity The requested quantity
     * @return true if sufficient stock, false otherwise
     */
    public boolean hasStock(final int requestedQuantity) {
        return stockQuantity >= requestedQuantity;
    }

    /**
     * Reserves stock for an order.
     * @param quantity The quantity to reserve
     * @throws IllegalArgumentException if insufficient stock
     * @throws IllegalStateException if status doesn't allow stock updates
     */
    public void reserveStock(final int quantity) {
        if (!status.allowsStockUpdates()) {
            throw new IllegalStateException("Cannot reserve stock. Product status: " + status);
        }
        
        if (!hasStock(quantity)) {
            throw new IllegalArgumentException(
                String.format("Insufficient stock. Available: %d, Requested: %d", 
                              stockQuantity, quantity)
            );
        }
        
        this.stockQuantity -= quantity;
        this.updatedAt = Instant.now();
        this.version++;
        
        // Auto-update status if needed (can be hot-swapped)
        if (stockQuantity == 0 && status == ProductStatus.ACTIVE) {
            this.status = ProductStatus.OUT_OF_STOCK;
        }
    }

    /**
     * Releases reserved stock (e.g., when order is cancelled).
     * @param quantity The quantity to release
     */
    public void releaseStock(final int quantity) {
        if (!status.allowsStockUpdates()) {
            throw new IllegalStateException("Cannot release stock. Product status: " + status);
        }
        
        this.stockQuantity += quantity;
        this.updatedAt = Instant.now();
        this.version++;
        
        // Auto-update status if needed (can be hot-swapped)
        if (stockQuantity > 0 && status == ProductStatus.OUT_OF_STOCK) {
            this.status = ProductStatus.ACTIVE;
        }
    }

    /**
     * Updates product information.
     * @param name The new name
     * @param description The new description
     * @param price The new price
     */
    public void updateInfo(final String name, final String description, final Money price) {
        if (!status.allowsPriceChanges()) {
            throw new IllegalStateException("Cannot update price. Product status: " + status);
        }
        
        this.name = name;
        this.description = description;
        this.price = price;
        this.updatedAt = Instant.now();
        this.version++;
    }

    /**
     * Changes product status.
     * @param newStatus The new status
     * @throws IllegalStateException if transition is not allowed
     */
    public void changeStatus(final ProductStatus newStatus) {
        if (!status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                String.format("Invalid status transition from %s to %s", status, newStatus)
            );
        }
        
        this.status = newStatus;
        this.updatedAt = Instant.now();
        this.version++;
    }

    /**
     * Activates the product.
     */
    public void activate() {
        changeStatus(ProductStatus.ACTIVE);
    }

    /**
     * Deactivates the product.
     */
    public void deactivate() {
        changeStatus(ProductStatus.INACTIVE);
    }

    /**
     * Discontinues the product.
     */
    public void discontinue() {
        changeStatus(ProductStatus.DISCONTINUED);
    }

    // Getters
    public String getProductId() { return productId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Money getPrice() { return price; }
    public Integer getStockQuantity() { return stockQuantity; }
    public ProductCategory getCategory() { return category; }
    public ProductStatus getStatus() { return status; }
    public String getSku() { return sku; }
    public Integer getWeightGrams() { return weightGrams; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Long getVersion() { return version; }

    public void setWeightGrams(final Integer weightGrams) {
        this.weightGrams = weightGrams;
        this.updatedAt = Instant.now();
        this.version++;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(productId, product.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }

    @Override
    public String toString() {
        return String.format("Product{id='%s', name='%s', sku='%s', price=%s, stock=%d, status=%s}",
                           productId, name, sku, price, stockQuantity, status);
    }
}