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
 *   - Represent a product in the e-commerce system
 *   - Provide product information and business logic
 *   - Support hot-swapping of business rules and validation
 *
 * Collaborators:
 *   - JPA: For persistence mapping
 *   - ProductCategory: Product categorization
 *   - Order: Order line items
 */
package org.acmsl.bytehot.examples.ecommerce.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * Product entity representing items available for purchase in the e-commerce system.
 * Demonstrates ByteHot capabilities by allowing hot-swapping of business logic.
 * @author Claude Code
 * @since 2025-07-04
 */
@Entity
@Table(name = "products")
public class Product {

    /**
     * Unique identifier for the product.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Product name.
     */
    @NotBlank(message = "Product name is required")
    @Column(nullable = false, length = 255)
    private String name;

    /**
     * Product description.
     */
    @Column(length = 1000)
    private String description;

    /**
     * Product price.
     */
    @NotNull(message = "Product price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Product price must be positive")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * Available stock quantity.
     */
    @PositiveOrZero(message = "Stock quantity cannot be negative")
    @Column(nullable = false)
    private Integer stockQuantity;

    /**
     * Product category.
     */
    @NotNull(message = "Product category is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductCategory category;

    /**
     * Product status.
     */
    @NotNull(message = "Product status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;

    /**
     * SKU (Stock Keeping Unit).
     */
    @NotBlank(message = "Product SKU is required")
    @Column(nullable = false, unique = true, length = 50)
    private String sku;

    /**
     * Product weight in grams.
     */
    @PositiveOrZero(message = "Product weight cannot be negative")
    @Column
    private Integer weightGrams;

    /**
     * When the product was created.
     */
    @Column(nullable = false)
    private Instant createdAt;

    /**
     * When the product was last updated.
     */
    @Column(nullable = false)
    private Instant updatedAt;

    /**
     * Version for optimistic locking.
     */
    @Version
    private Long version;

    /**
     * Default constructor for JPA.
     */
    protected Product() {
        // JPA requires default constructor
    }

    /**
     * Creates a new Product.
     * @param name The product name
     * @param description The product description
     * @param price The product price
     * @param stockQuantity The initial stock quantity
     * @param category The product category
     * @param sku The product SKU
     */
    public Product(final String name,
                   final String description,
                   final BigDecimal price,
                   final Integer stockQuantity,
                   final ProductCategory category,
                   final String sku) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.category = category;
        this.sku = sku;
        this.status = ProductStatus.ACTIVE;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    /**
     * Checks if the product is available for purchase.
     * This method can be hot-swapped to change availability logic.
     * @return true if available, false otherwise
     */
    public boolean isAvailable() {
        return status == ProductStatus.ACTIVE && stockQuantity > 0;
    }

    /**
     * Calculates the discounted price based on quantity.
     * This method demonstrates hot-swappable business logic.
     * @param quantity The quantity being purchased
     * @return The discounted price per unit
     */
    public BigDecimal calculateDiscountedPrice(final int quantity) {
        // Basic quantity discount logic (can be hot-swapped)
        if (quantity >= 10) {
            return price.multiply(BigDecimal.valueOf(0.9)); // 10% discount
        } else if (quantity >= 5) {
            return price.multiply(BigDecimal.valueOf(0.95)); // 5% discount
        }
        return price;
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
     */
    public void reserveStock(final int quantity) {
        if (!hasStock(quantity)) {
            throw new IllegalArgumentException(
                String.format("Insufficient stock. Available: %d, Requested: %d", 
                              stockQuantity, quantity)
            );
        }
        this.stockQuantity -= quantity;
        this.updatedAt = Instant.now();
    }

    /**
     * Releases reserved stock (e.g., when order is cancelled).
     * @param quantity The quantity to release
     */
    public void releaseStock(final int quantity) {
        this.stockQuantity += quantity;
        this.updatedAt = Instant.now();
    }

    /**
     * Updates product information.
     * @param name The new name
     * @param description The new description
     * @param price The new price
     */
    public void updateInfo(final String name, final String description, final BigDecimal price) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.updatedAt = Instant.now();
    }

    /**
     * Deactivates the product.
     */
    public void deactivate() {
        this.status = ProductStatus.INACTIVE;
        this.updatedAt = Instant.now();
    }

    /**
     * Activates the product.
     */
    public void activate() {
        this.status = ProductStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public String getSku() {
        return sku;
    }

    public Integer getWeightGrams() {
        return weightGrams;
    }

    public void setWeightGrams(final Integer weightGrams) {
        this.weightGrams = weightGrams;
        this.updatedAt = Instant.now();
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Long getVersion() {
        return version;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(sku, product.sku);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sku);
    }

    @Override
    public String toString() {
        return String.format("Product{id=%d, name='%s', sku='%s', price=%s, stock=%d}",
                           id, name, sku, price, stockQuantity);
    }
}