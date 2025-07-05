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
 *   - Define risk assessment levels for transactions
 *   - Support risk-based processing rules
 *   - Enable hot-swappable risk level behavior
 *
 * Collaborators:
 *   - Transaction: Uses this enum for risk assessment
 */
package org.acmsl.bytehot.examples.financial.domain;

/**
 * Enumeration of risk levels for transaction assessment.
 * @author Claude Code
 * @since 2025-07-04
 */
public enum RiskLevel {
    
    /**
     * Low risk transaction.
     */
    LOW("Low", "Transaction poses minimal risk"),
    
    /**
     * Medium risk transaction.
     */
    MEDIUM("Medium", "Transaction poses moderate risk"),
    
    /**
     * High risk transaction.
     */
    HIGH("High", "Transaction poses high risk and requires additional scrutiny");

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
     * Checks if this risk level requires manual approval.
     * This method can be hot-swapped to change approval requirements.
     * @return true if manual approval required, false otherwise
     */
    public boolean requiresManualApproval() {
        return this == HIGH;
    }

    /**
     * Checks if this risk level allows automated processing.
     * This method can be hot-swapped to change automation rules.
     * @return true if automated processing allowed, false otherwise
     */
    public boolean allowsAutomatedProcessing() {
        return this != HIGH;
    }

    /**
     * Gets the risk score for this level.
     * This method can be hot-swapped to change scoring algorithms.
     * @return Risk score value
     */
    public int getRiskScore() {
        switch (this) {
            case LOW:
                return 1;
            case MEDIUM:
                return 5;
            case HIGH:
                return 10;
            default:
                return 0;
        }
    }
}