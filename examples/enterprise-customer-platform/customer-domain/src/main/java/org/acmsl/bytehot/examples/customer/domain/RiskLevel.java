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
 * Filename: RiskLevel.java
 *
 * Author: Claude Code
 *
 * Class name: RiskLevel
 *
 * Responsibilities:
 *   - Define risk assessment levels for customers
 *   - Support risk-based decision making and monitoring
 *   - Enable hot-swappable risk level behaviors and thresholds
 *
 * Collaborators:
 *   - Customer: Uses this enum for risk assessment
 *   - RiskProfile: Contains risk level information
 */
package org.acmsl.bytehot.examples.customer.domain;

/**
 * Enumeration of risk levels for customer assessment.
 * @author Claude Code
 * @since 2025-07-05
 */
public enum RiskLevel {

    /**
     * Low risk customers requiring minimal monitoring.
     */
    LOW("Low", "Minimal risk with standard monitoring"),

    /**
     * Medium risk customers requiring regular monitoring.
     */
    MEDIUM("Medium", "Moderate risk with enhanced monitoring"),

    /**
     * High risk customers requiring intensive monitoring.
     */
    HIGH("High", "Elevated risk requiring intensive monitoring"),

    /**
     * Very high risk customers requiring special handling.
     */
    VERY_HIGH("Very High", "Critical risk requiring special procedures");

    private final String displayName;
    private final String description;

    RiskLevel(final String displayName, final String description) {
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
     * Increases the risk level by one tier.
     * This method can be hot-swapped to change risk escalation logic.
     * @return Higher risk level
     */
    public RiskLevel increase() {
        switch (this) {
            case LOW:
                return MEDIUM;
            case MEDIUM:
                return HIGH;
            case HIGH:
                return VERY_HIGH;
            case VERY_HIGH:
                return VERY_HIGH; // Already at maximum
            default:
                return this;
        }
    }

    /**
     * Decreases the risk level by one tier.
     * This method can be hot-swapped to change risk de-escalation logic.
     * @return Lower risk level
     */
    public RiskLevel decrease() {
        switch (this) {
            case VERY_HIGH:
                return HIGH;
            case HIGH:
                return MEDIUM;
            case MEDIUM:
                return LOW;
            case LOW:
                return LOW; // Already at minimum
            default:
                return this;
        }
    }

    /**
     * Gets the monitoring frequency for this risk level.
     * This method can be hot-swapped to change monitoring schedules.
     * @return Monitoring frequency in days
     */
    public int getMonitoringFrequencyDays() {
        switch (this) {
            case LOW:
                return 90; // Quarterly
            case MEDIUM:
                return 30; // Monthly
            case HIGH:
                return 7;  // Weekly
            case VERY_HIGH:
                return 1;  // Daily
            default:
                return 30;
        }
    }

    /**
     * Determines if manual approval is required for this risk level.
     * This method can be hot-swapped to change approval requirements.
     * @return true if manual approval required, false otherwise
     */
    public boolean requiresManualApproval() {
        switch (this) {
            case LOW:
            case MEDIUM:
                return false;
            case HIGH:
            case VERY_HIGH:
                return true;
            default:
                return true;
        }
    }

    /**
     * Gets the transaction limit multiplier for this risk level.
     * This method can be hot-swapped to change transaction limits.
     * @return Multiplier for transaction limits (1.0 = no change)
     */
    public double getTransactionLimitMultiplier() {
        switch (this) {
            case LOW:
                return 1.2; // 20% increase
            case MEDIUM:
                return 1.0; // No change
            case HIGH:
                return 0.5; // 50% reduction
            case VERY_HIGH:
                return 0.1; // 90% reduction
            default:
                return 0.5;
        }
    }
}