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
 * Filename: PricingService.java
 *
 * Author: Claude Code
 *
 * Class name: PricingService
 *
 * Responsibilities:
 *   - Apply complex pricing rules and strategies
 *   - Calculate dynamic pricing based on market conditions
 *   - Provide hot-swappable pricing algorithms
 *
 * Collaborators:
 *   - Product: Uses product information for pricing
 *   - ProductService: Coordinates with product operations
 */
package org.acmsl.bytehot.examples.ecommerce.service;

import org.acmsl.bytehot.examples.ecommerce.model.Product;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.logging.Logger;

/**
 * Service for advanced pricing logic that can be hot-swapped at runtime.
 * Demonstrates ByteHot's capability to change complex business algorithms without downtime.
 * @author Claude Code
 * @since 2025-07-04
 */
@Service
public class PricingService {

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(PricingService.class.getName());

    /**
     * Applies advanced pricing rules to a product.
     * This method can be hot-swapped to implement different pricing strategies.
     * @param product The product being priced
     * @param basePrice The base price after initial discounts
     * @param quantity The purchase quantity
     * @return The final price after all rules
     */
    public BigDecimal applyPricingRules(final Product product, 
                                       final BigDecimal basePrice, 
                                       final int quantity) {
        BigDecimal finalPrice = basePrice;
        
        // Apply time-based pricing (can be hot-swapped)
        finalPrice = applyTimeBasedPricing(finalPrice);
        
        // Apply category-specific pricing (can be hot-swapped)
        finalPrice = applyCategoryPricing(product, finalPrice);
        
        // Apply inventory-based pricing (can be hot-swapped)
        finalPrice = applyInventoryPricing(product, finalPrice);
        
        // Apply bulk discount rules (can be hot-swapped)
        finalPrice = applyBulkDiscounts(finalPrice, quantity);
        
        LOGGER.info(String.format("Applied pricing rules: %s -> %s for product %s",
                                basePrice, finalPrice, product.getSku()));
        
        return finalPrice;
    }

    /**
     * Applies time-based pricing adjustments.
     * This method can be hot-swapped to change time-sensitive pricing.
     * @param price The current price
     * @return The price after time-based adjustments
     */
    protected BigDecimal applyTimeBasedPricing(final BigDecimal price) {
        LocalTime now = LocalTime.now();
        
        // Example: Happy hour pricing (can be hot-swapped)
        if (now.isAfter(LocalTime.of(14, 0)) && now.isBefore(LocalTime.of(16, 0))) {
            // 5% discount during happy hours (2-4 PM)
            return price.multiply(BigDecimal.valueOf(0.95));
        }
        
        return price;
    }

    /**
     * Applies category-specific pricing rules.
     * This method can be hot-swapped to change category pricing strategies.
     * @param product The product
     * @param price The current price
     * @return The price after category adjustments
     */
    protected BigDecimal applyCategoryPricing(final Product product, final BigDecimal price) {
        switch (product.getCategory()) {
            case ELECTRONICS:
                // Electronics get a small premium for warranty
                return price.multiply(BigDecimal.valueOf(1.02));
            case CLOTHING:
                // Clothing gets seasonal adjustments (simplified)
                return price.multiply(BigDecimal.valueOf(0.98));
            case BOOKS:
                // Books maintain stable pricing
                return price;
            default:
                return price;
        }
    }

    /**
     * Applies inventory-based pricing adjustments.
     * This method can be hot-swapped to change inventory pricing logic.
     * @param product The product
     * @param price The current price
     * @return The price after inventory adjustments
     */
    protected BigDecimal applyInventoryPricing(final Product product, final BigDecimal price) {
        int stockQuantity = product.getStockQuantity();
        
        // Low stock premium (can be hot-swapped)
        if (stockQuantity < 5) {
            // 3% premium for very low stock
            return price.multiply(BigDecimal.valueOf(1.03));
        } else if (stockQuantity < 20) {
            // 1% premium for low stock
            return price.multiply(BigDecimal.valueOf(1.01));
        }
        
        // High stock discount (can be hot-swapped)
        if (stockQuantity > 100) {
            // 2% discount for high stock
            return price.multiply(BigDecimal.valueOf(0.98));
        }
        
        return price;
    }

    /**
     * Applies bulk discount rules.
     * This method can be hot-swapped to change bulk pricing strategies.
     * @param price The current price
     * @param quantity The purchase quantity
     * @return The price after bulk discounts
     */
    protected BigDecimal applyBulkDiscounts(final BigDecimal price, final int quantity) {
        // Progressive bulk discounts (can be hot-swapped)
        if (quantity >= 50) {
            // 8% discount for orders of 50+
            return price.multiply(BigDecimal.valueOf(0.92));
        } else if (quantity >= 25) {
            // 5% discount for orders of 25+
            return price.multiply(BigDecimal.valueOf(0.95));
        } else if (quantity >= 15) {
            // 3% discount for orders of 15+
            return price.multiply(BigDecimal.valueOf(0.97));
        }
        
        return price;
    }

    /**
     * Calculates dynamic pricing based on market conditions.
     * This method can be hot-swapped to implement different market pricing algorithms.
     * @param product The product
     * @return The market-adjusted price multiplier
     */
    public BigDecimal calculateMarketPriceMultiplier(final Product product) {
        // Simulate market conditions (can be hot-swapped)
        // In real implementation, this would use external market data
        
        switch (product.getCategory()) {
            case ELECTRONICS:
                // Tech products have volatile pricing
                return BigDecimal.valueOf(1.0 + (Math.random() * 0.1 - 0.05)); // ±5%
            case CLOTHING:
                // Fashion items have seasonal variations
                return BigDecimal.valueOf(1.0 + (Math.random() * 0.2 - 0.1)); // ±10%
            default:
                // Other categories have stable pricing
                return BigDecimal.valueOf(1.0 + (Math.random() * 0.04 - 0.02)); // ±2%
        }
    }

    /**
     * Validates pricing constraints.
     * This method can be hot-swapped to change pricing validation rules.
     * @param originalPrice The original price
     * @param calculatedPrice The calculated price
     * @return The validated price
     */
    public BigDecimal validatePricingConstraints(final BigDecimal originalPrice, 
                                                final BigDecimal calculatedPrice) {
        // Ensure price doesn't deviate too much from original (can be hot-swapped)
        BigDecimal maxIncrease = originalPrice.multiply(BigDecimal.valueOf(1.20)); // Max 20% increase
        BigDecimal maxDecrease = originalPrice.multiply(BigDecimal.valueOf(0.70)); // Max 30% decrease
        
        if (calculatedPrice.compareTo(maxIncrease) > 0) {
            LOGGER.warning("Calculated price exceeds maximum increase limit, capping at 20%");
            return maxIncrease;
        }
        
        if (calculatedPrice.compareTo(maxDecrease) < 0) {
            LOGGER.warning("Calculated price below minimum threshold, setting to 30% discount");
            return maxDecrease;
        }
        
        return calculatedPrice;
    }
}