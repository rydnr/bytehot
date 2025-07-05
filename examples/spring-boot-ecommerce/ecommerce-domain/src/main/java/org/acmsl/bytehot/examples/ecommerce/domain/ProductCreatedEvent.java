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
 * Filename: ProductCreatedEvent.java
 *
 * Author: Claude Code
 *
 * Class name: ProductCreatedEvent
 *
 * Responsibilities:
 *   - Represent successful product creation as domain response event
 *   - Carry created product data for downstream systems
 *   - Enable hot-swappable success event structure evolution
 *
 * Collaborators:
 *   - Product: Aggregate that generates this event
 *   - ProductCreationRequestedEvent: Original event being responded to
 */
package org.acmsl.bytehot.examples.ecommerce.domain;

import org.acmsl.commons.patterns.DomainEvent;
import org.acmsl.commons.patterns.DomainResponseEvent;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Domain response event representing successful product creation.
 * @author Claude Code
 * @since 2025-07-04
 */
public class ProductCreatedEvent implements DomainResponseEvent<ProductCreationRequestedEvent> {

    /**
     * Original product creation request event.
     */
    @NotNull
    private final ProductCreationRequestedEvent originalEvent;

    /**
     * Created product.
     */
    @NotNull
    private final Product product;

    /**
     * Event timestamp.
     */
    @NotNull
    private final LocalDateTime timestamp;

    /**
     * Creates a new ProductCreatedEvent.
     * @param originalEvent The original product creation request event
     * @param product The created product
     */
    public ProductCreatedEvent(final ProductCreationRequestedEvent originalEvent,
                              final Product product) {
        this.originalEvent = originalEvent;
        this.product = product;
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public ProductCreationRequestedEvent getOriginalEvent() {
        return originalEvent;
    }

    @Override
    public DomainEvent getPreviousEvent() {
        return originalEvent;
    }

    public Product getProduct() {
        return product;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "ProductCreatedEvent{" +
                "productId='" + product.getProductId() + '\'' +
                ", name='" + product.getName() + '\'' +
                ", sku='" + product.getSku() + '\'' +
                ", status=" + product.getStatus() +
                ", timestamp=" + timestamp +
                '}';
    }
}