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
 * Filename: CustomerSegmentation.java
 *
 * Author: Claude Code
 *
 * Class name: CustomerSegmentation
 *
 * Responsibilities:
 *   - Define customer market segmentation categories
 *   - Support segment-based business logic and marketing strategies
 *   - Enable hot-swappable segmentation criteria and behaviors
 *
 * Collaborators:
 *   - Customer: Uses this enum for market segmentation
 *   - LifetimeValueCalculator: Uses segmentation for CLV modeling
 */
package org.acmsl.bytehot.examples.customer.domain;

import java.math.BigDecimal;

/**
 * Enumeration of customer market segmentation categories.
 * @author Claude Code
 * @since 2025-07-05
 */
public enum CustomerSegmentation {

    /**
     * Premium customers with highest value and service levels.
     */
    PREMIUM("Premium", "High-value customers with premium service offerings"),

    /**
     * High-value customers with enhanced service levels.
     */
    HIGH_VALUE("High Value", "Above-average value customers with enhanced services"),

    /**
     * Standard customers with regular service levels.
     */
    STANDARD("Standard", "Regular customers with standard service offerings"),

    /**
     * Subprime customers requiring special handling.
     */
    SUBPRIME("Subprime", "Higher-risk customers with enhanced monitoring"),

    /**
     * Premium prospects during onboarding process.
     */
    PREMIUM_PROSPECT("Premium Prospect", "High-potential prospects for premium segment"),

    /**
     * Standard prospects during onboarding process.
     */
    STANDARD_PROSPECT("Standard Prospect", "Regular prospects for standard segment");

    private final String displayName;
    private final String description;

    CustomerSegmentation(final String displayName, final String description) {
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
     * Gets the service level priority for this segment.
     * This method can be hot-swapped to change service prioritization.
     * @return Priority level (1 = highest, 5 = lowest)
     */
    public int getServicePriority() {
        switch (this) {
            case PREMIUM:
            case PREMIUM_PROSPECT:
                return 1;
            case HIGH_VALUE:
                return 2;
            case STANDARD:
            case STANDARD_PROSPECT:
                return 3;
            case SUBPRIME:
                return 4;
            default:
                return 5;
        }
    }

    /**
     * Gets the default credit limit for this segment.
     * This method can be hot-swapped to change credit policies.
     * @return Default credit limit
     */
    public BigDecimal getDefaultCreditLimit() {
        switch (this) {
            case PREMIUM:
                return BigDecimal.valueOf(100000);
            case HIGH_VALUE:
                return BigDecimal.valueOf(50000);
            case STANDARD:
                return BigDecimal.valueOf(15000);
            case SUBPRIME:
                return BigDecimal.valueOf(2500);
            case PREMIUM_PROSPECT:
                return BigDecimal.valueOf(25000);
            case STANDARD_PROSPECT:
                return BigDecimal.valueOf(5000);
            default:
                return BigDecimal.valueOf(1000);
        }
    }

    /**
     * Gets the marketing campaign eligibility for this segment.
     * This method can be hot-swapped to change marketing targeting.
     * @param campaignType The type of marketing campaign
     * @return true if eligible, false otherwise
     */
    public boolean isEligibleForCampaign(final CampaignType campaignType) {
        switch (campaignType) {
            case PREMIUM_OFFERS:
                return this == PREMIUM || this == PREMIUM_PROSPECT;
            case LOYALTY_REWARDS:
                return this == PREMIUM || this == HIGH_VALUE;
            case RETENTION_CAMPAIGNS:
                return this == HIGH_VALUE || this == STANDARD;
            case ACQUISITION_CAMPAIGNS:
                return this == STANDARD_PROSPECT || this == PREMIUM_PROSPECT;
            case RISK_MITIGATION:
                return this == SUBPRIME;
            default:
                return false;
        }
    }

    /**
     * Gets the relationship manager assignment requirement.
     * This method can be hot-swapped to change RM assignment logic.
     * @return true if dedicated RM required, false otherwise
     */
    public boolean requiresDedicatedRelationshipManager() {
        switch (this) {
            case PREMIUM:
            case HIGH_VALUE:
                return true;
            case STANDARD:
            case SUBPRIME:
            case PREMIUM_PROSPECT:
            case STANDARD_PROSPECT:
                return false;
            default:
                return false;
        }
    }

    /**
     * Gets the annual review frequency for this segment.
     * This method can be hot-swapped to change review schedules.
     * @return Number of reviews per year
     */
    public int getAnnualReviewFrequency() {
        switch (this) {
            case PREMIUM:
                return 6; // Bi-monthly
            case HIGH_VALUE:
                return 4; // Quarterly
            case STANDARD:
                return 2; // Semi-annually
            case SUBPRIME:
                return 12; // Monthly
            case PREMIUM_PROSPECT:
            case STANDARD_PROSPECT:
                return 1; // Annual
            default:
                return 1;
        }
    }

    /**
     * Gets the fee waiver eligibility for this segment.
     * This method can be hot-swapped to change fee policies.
     * @param feeType The type of fee
     * @return true if fee waiver eligible, false otherwise
     */
    public boolean isEligibleForFeeWaiver(final FeeType feeType) {
        switch (this) {
            case PREMIUM:
                return true; // All fees waived
            case HIGH_VALUE:
                return feeType == FeeType.MAINTENANCE || feeType == FeeType.TRANSACTION;
            case STANDARD:
                return feeType == FeeType.MAINTENANCE && 
                       getCurrentMonth() % 6 == 0; // Maintenance fee waived every 6 months
            case SUBPRIME:
            case PREMIUM_PROSPECT:
            case STANDARD_PROSPECT:
                return false;
            default:
                return false;
        }
    }

    /**
     * Upgrades the segmentation to the next higher tier.
     * This method can be hot-swapped to change upgrade paths.
     * @return Upgraded segmentation
     */
    public CustomerSegmentation upgrade() {
        switch (this) {
            case SUBPRIME:
                return STANDARD;
            case STANDARD:
                return HIGH_VALUE;
            case HIGH_VALUE:
                return PREMIUM;
            case STANDARD_PROSPECT:
                return PREMIUM_PROSPECT;
            case PREMIUM_PROSPECT:
                return PREMIUM;
            case PREMIUM:
                return PREMIUM; // Already at top
            default:
                return this;
        }
    }

    /**
     * Downgrades the segmentation to the next lower tier.
     * This method can be hot-swapped to change downgrade paths.
     * @return Downgraded segmentation
     */
    public CustomerSegmentation downgrade() {
        switch (this) {
            case PREMIUM:
                return HIGH_VALUE;
            case HIGH_VALUE:
                return STANDARD;
            case STANDARD:
                return SUBPRIME;
            case PREMIUM_PROSPECT:
                return STANDARD_PROSPECT;
            case STANDARD_PROSPECT:
                return SUBPRIME;
            case SUBPRIME:
                return SUBPRIME; // Already at bottom
            default:
                return this;
        }
    }

    /**
     * Gets the current month for date-based calculations.
     * This method can be hot-swapped for testing or different calendar systems.
     * @return Current month (1-12)
     */
    protected int getCurrentMonth() {
        return java.time.LocalDate.now().getMonthValue();
    }

    /**
     * Enumeration of campaign types for marketing eligibility.
     */
    public enum CampaignType {
        PREMIUM_OFFERS,
        LOYALTY_REWARDS,
        RETENTION_CAMPAIGNS,
        ACQUISITION_CAMPAIGNS,
        RISK_MITIGATION
    }

    /**
     * Enumeration of fee types for waiver eligibility.
     */
    public enum FeeType {
        MAINTENANCE,
        TRANSACTION,
        OVERDRAFT,
        WIRE_TRANSFER,
        FOREIGN_EXCHANGE
    }
}