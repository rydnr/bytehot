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
 * Filename: ProductCreationRequestedEvent.java
 *
 * Author: Claude Code
 *
 * Class name: ProductCreationRequestedEvent
 *
 * Responsibilities:
 *   - Represent incoming product creation request as domain event
 *   - Carry product creation data from external sources
 *   - Enable hot-swappable event structure evolution
 *
 * Collaborators:
 *   - Product: Aggregate that processes this event
 *   - ProductCategory: Value object for categorization
 *   - Money: Value object for pricing
 */
package org.acmsl.bytehot.examples.ecommerce.domain;

import org.acmsl.commons.patterns.DomainEvent;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Domain event representing a request to create a new product.
 * @author Claude Code
 * @since 2025-07-04
 */
public class ProductCreationRequestedEvent implements DomainEvent {

    /**
     * Product name.
     */
    @NotBlank
    private final String name;

    /**
     * Product description.
     */
    private final String description;

    /**
     * Product price.
     */
    @NotNull
    private final Money price;

    /**
     * Initial stock quantity.
     */
    @NotNull
    private final Integer stockQuantity;

    /**
     * Product category.
     */
    @NotNull
    private final ProductCategory category;

    /**
     * Product SKU.
     */
    @NotBlank
    private final String sku;

    /**
     * Product weight in grams.
     */
    private final Integer weightGrams;

    /**
     * Event timestamp.
     */
    @NotNull
    private final LocalDateTime timestamp;

    /**
     * Request originator.
     */
    private final String requestedBy;

    /**
     * Creates a new ProductCreationRequestedEvent.
     * @param name The product name
     * @param description The product description
     * @param price The product price
     * @param stockQuantity The initial stock quantity
     * @param category The product category
     * @param sku The product SKU
     * @param weightGrams The product weight
     * @param requestedBy Who requested the creation
     */
    public ProductCreationRequestedEvent(final String name,
                                        final String description,
                                        final Money price,
                                        final Integer stockQuantity,
                                        final ProductCategory category,
                                        final String sku,
                                        final Integer weightGrams,
                                        final String requestedBy) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.category = category;
        this.sku = sku;
        this.weightGrams = weightGrams;
        this.requestedBy = requestedBy;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Simplified constructor without weight and requester.
     * @param name The product name
     * @param description The product description
     * @param price The product price
     * @param stockQuantity The initial stock quantity
     * @param category The product category
     * @param sku The product SKU
     */
    public ProductCreationRequestedEvent(final String name,
                                        final String description,
                                        final Money price,
                                        final Integer stockQuantity,
                                        final ProductCategory category,
                                        final String sku) {
        this(name, description, price, stockQuantity, category, sku, null, "system");
    }

    @Override
    public DomainEvent getPreviousEvent() {
        return null; // This is typically the first event in the chain
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public Money getPrice() { return price; }
    public Integer getStockQuantity() { return stockQuantity; }
    public ProductCategory getCategory() { return category; }
    public String getSku() { return sku; }
    public Integer getWeightGrams() { return weightGrams; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getRequestedBy() { return requestedBy; }

    @Override
    public String toString() {
        return "ProductCreationRequestedEvent{" +
                "name='" + name + '\'' +
                ", sku='" + sku + '\'' +
                ", price=" + price +
                ", category=" + category +
                ", stockQuantity=" + stockQuantity +
                ", requestedBy='" + requestedBy + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}