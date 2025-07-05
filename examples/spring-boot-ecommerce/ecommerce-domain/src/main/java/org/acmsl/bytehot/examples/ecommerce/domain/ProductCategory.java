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
 * Filename: ProductCategory.java
 *
 * Author: Claude Code
 *
 * Class name: ProductCategory
 *
 * Responsibilities:
 *   - Define product categories for the e-commerce system
 *   - Support category-based business logic and rules
 *   - Enable hot-swappable category-specific behaviors
 *
 * Collaborators:
 *   - Product: Uses this enum for categorization
 *   - PricingPolicy: May use category for pricing rules
 */
package org.acmsl.bytehot.examples.ecommerce.domain;

/**
 * Enumeration of product categories in the e-commerce system.
 * @author Claude Code
 * @since 2025-07-04
 */
public enum ProductCategory {

    /**
     * Electronics and technology products.
     */
    ELECTRONICS("Electronics", "Electronic devices and technology products"),

    /**
     * Clothing and fashion items.
     */
    CLOTHING("Clothing", "Apparel, shoes, and fashion accessories"),

    /**
     * Books and educational materials.
     */
    BOOKS("Books", "Books, magazines, and educational materials"),

    /**
     * Home and garden products.
     */
    HOME_GARDEN("Home & Garden", "Home improvement and gardening products"),

    /**
     * Sports and outdoor equipment.
     */
    SPORTS("Sports & Outdoors", "Sports equipment and outdoor gear"),

    /**
     * Health and beauty products.
     */
    HEALTH_BEAUTY("Health & Beauty", "Health, wellness, and beauty products"),

    /**
     * Automotive products and accessories.
     */
    AUTOMOTIVE("Automotive", "Car parts, accessories, and automotive tools"),

    /**
     * Toys and games.
     */
    TOYS_GAMES("Toys & Games", "Children's toys, board games, and entertainment"),

    /**
     * Food and beverages.
     */
    FOOD_BEVERAGES("Food & Beverages", "Food items and beverages"),

    /**
     * General merchandise not fitting other categories.
     */
    GENERAL("General", "General merchandise and miscellaneous items");

    private final String displayName;
    private final String description;

    ProductCategory(final String displayName, final String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Determines if this category requires special handling.
     * This method can be hot-swapped to change category behavior.
     * @return true if special handling required, false otherwise
     */
    public boolean requiresSpecialHandling() {
        switch (this) {
            case ELECTRONICS:
            case AUTOMOTIVE:
                return true; // Electronics and automotive require warranty tracking
            case FOOD_BEVERAGES:
                return true; // Food requires expiration date tracking
            default:
                return false;
        }
    }

    /**
     * Gets the default shipping weight multiplier for this category.
     * This method demonstrates hot-swappable business logic.
     * @return The shipping weight multiplier
     */
    public double getShippingWeightMultiplier() {
        switch (this) {
            case ELECTRONICS:
                return 1.2; // Electronics are fragile, need extra packaging
            case CLOTHING:
                return 0.8; // Clothing is lightweight
            case BOOKS:
                return 1.0; // Standard weight
            case HOME_GARDEN:
                return 1.5; // Heavy items, bulky packaging
            case SPORTS:
                return 1.3; // Sports equipment is often bulky
            case HEALTH_BEAUTY:
                return 0.9; // Generally lightweight
            case AUTOMOTIVE:
                return 2.0; // Heavy automotive parts
            case TOYS_GAMES:
                return 1.1; // Slightly heavier due to packaging
            case FOOD_BEVERAGES:
                return 1.0; // Standard weight
            case GENERAL:
                return 1.0; // Default weight
            default:
                return 1.0;
        }
    }

    /**
     * Determines if this category supports expedited shipping.
     * This method can be hot-swapped to change shipping policies.
     * @return true if expedited shipping is supported, false otherwise
     */
    public boolean supportsExpeditedShipping() {
        switch (this) {
            case FOOD_BEVERAGES:
                return false; // Perishable items may not support expedited shipping
            case AUTOMOTIVE:
                return false; // Heavy items may have shipping restrictions
            default:
                return true;
        }
    }

    /**
     * Gets the tax rate for this category.
     * This method can be hot-swapped to adjust tax policies.
     * @return Tax rate as a decimal (e.g., 0.08 for 8%)
     */
    public double getTaxRate() {
        switch (this) {
            case FOOD_BEVERAGES:
                return 0.03; // Lower tax rate for food
            case BOOKS:
                return 0.0; // No tax on books in many jurisdictions
            case HEALTH_BEAUTY:
                return 0.05; // Reduced rate for health products
            default:
                return 0.08; // Standard tax rate
        }
    }
}