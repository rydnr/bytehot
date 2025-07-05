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
 * Filename: TransactionType.java
 *
 * Author: Claude Code
 *
 * Class name: TransactionType
 *
 * Responsibilities:
 *   - Define transaction classification types
 *   - Support type-specific processing rules
 *   - Enable hot-swappable type behavior
 *
 * Collaborators:
 *   - Transaction: Uses this enum for transaction classification
 */
package org.acmsl.bytehot.examples.financial.domain;

/**
 * Enumeration of transaction types for financial processing.
 * @author Claude Code
 * @since 2025-07-04
 */
public enum TransactionType {
    
    /**
     * Wire transfer transaction.
     */
    WIRE_TRANSFER("Wire Transfer", "Electronic transfer between banks"),
    
    /**
     * International transfer transaction.
     */
    INTERNATIONAL_TRANSFER("International Transfer", "Cross-border transfer"),
    
    /**
     * Internal transfer transaction.
     */
    INTERNAL_TRANSFER("Internal Transfer", "Transfer within same institution"),
    
    /**
     * Payment transaction.
     */
    PAYMENT("Payment", "General payment transaction");

    private final String displayName;
    private final String description;

    TransactionType(final String displayName, final String description) {
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
     * Checks if this transaction type requires reference information.
     * This method can be hot-swapped to change reference requirements.
     * @return true if reference required, false otherwise
     */
    public boolean requiresReference() {
        switch (this) {
            case WIRE_TRANSFER:
            case INTERNATIONAL_TRANSFER:
                return true;
            default:
                return false;
        }
    }

    /**
     * Gets the default processing time for this transaction type.
     * This method can be hot-swapped to change processing times.
     * @return Processing time in milliseconds
     */
    public long getDefaultProcessingTime() {
        switch (this) {
            case INTERNAL_TRANSFER:
                return 100;
            case WIRE_TRANSFER:
                return 500;
            case INTERNATIONAL_TRANSFER:
                return 1000;
            case PAYMENT:
                return 200;
            default:
                return 300;
        }
    }
}