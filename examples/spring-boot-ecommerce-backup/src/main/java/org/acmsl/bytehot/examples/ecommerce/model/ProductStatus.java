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
 * Filename: ProductStatus.java
 *
 * Author: Claude Code
 *
 * Class name: ProductStatus
 *
 * Responsibilities:
 *   - Define product lifecycle status values
 *   - Support status-based business logic
 *   - Enable status transition validation
 *
 * Collaborators:
 *   - Product: Uses this enum for status tracking
 */
package org.acmsl.bytehot.examples.ecommerce.model;

/**
 * Enumeration of product status values in the e-commerce system.
 * @author Claude Code
 * @since 2025-07-04
 */
public enum ProductStatus {

    /**
     * Product is active and available for sale.
     */
    ACTIVE("Active", "Product is available for purchase"),

    /**
     * Product is temporarily inactive.
     */
    INACTIVE("Inactive", "Product is temporarily unavailable"),

    /**
     * Product is discontinued and no longer sold.
     */
    DISCONTINUED("Discontinued", "Product is no longer available for sale"),

    /**
     * Product is pending approval or review.
     */
    PENDING("Pending", "Product is awaiting approval"),

    /**
     * Product is out of stock.
     */
    OUT_OF_STOCK("Out of Stock", "Product is temporarily out of stock");

    /**
     * Human-readable name of the status.
     */
    private final String displayName;

    /**
     * Description of the status.
     */
    private final String description;

    /**
     * Creates a new ProductStatus.
     * @param displayName The display name
     * @param description The status description
     */
    ProductStatus(final String displayName, final String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * Gets the display name.
     * @return The display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the description.
     * @return The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Determines if products with this status can be purchased.
     * This method can be hot-swapped to change purchase eligibility logic.
     * @return true if purchasable, false otherwise
     */
    public boolean isPurchasable() {
        switch (this) {
            case ACTIVE:
                return true;
            case INACTIVE:
            case DISCONTINUED:
            case PENDING:
            case OUT_OF_STOCK:
                return false;
            default:
                return false;
        }
    }

    /**
     * Determines if this status allows stock updates.
     * @return true if stock updates are allowed, false otherwise
     */
    public boolean allowsStockUpdates() {
        switch (this) {
            case ACTIVE:
            case INACTIVE:
            case OUT_OF_STOCK:
                return true;
            case DISCONTINUED:
            case PENDING:
                return false;
            default:
                return false;
        }
    }

    /**
     * Determines if this status is a final state.
     * @return true if this is a final state, false otherwise
     */
    public boolean isFinalState() {
        return this == DISCONTINUED;
    }
}