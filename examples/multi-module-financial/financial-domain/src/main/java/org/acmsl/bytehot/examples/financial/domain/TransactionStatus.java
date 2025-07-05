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
 * Filename: TransactionStatus.java
 *
 * Author: Claude Code
 *
 * Class name: TransactionStatus
 *
 * Responsibilities:
 *   - Define transaction lifecycle states
 *   - Support status-based transition rules
 *   - Enable hot-swappable status behavior
 *
 * Collaborators:
 *   - Transaction: Uses this enum for status tracking
 */
package org.acmsl.bytehot.examples.financial.domain;

/**
 * Enumeration of transaction status values.
 * @author Claude Code
 * @since 2025-07-04
 */
public enum TransactionStatus {
    
    /**
     * Transaction is pending processing.
     */
    PENDING("Pending", "Transaction is awaiting processing"),
    
    /**
     * Transaction is currently being processed.
     */
    PROCESSING("Processing", "Transaction is currently being processed"),
    
    /**
     * Transaction has been completed successfully.
     */
    COMPLETED("Completed", "Transaction has been successfully completed"),
    
    /**
     * Transaction has been rejected.
     */
    REJECTED("Rejected", "Transaction has been rejected"),
    
    /**
     * Transaction processing failed.
     */
    FAILED("Failed", "Transaction processing failed"),
    
    /**
     * Transaction was cancelled.
     */
    CANCELLED("Cancelled", "Transaction was cancelled");

    private final String displayName;
    private final String description;

    TransactionStatus(final String displayName, final String description) {
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
     * Determines if this is a final status.
     * This method can be hot-swapped to change finality rules.
     * @return true if final, false otherwise
     */
    public boolean isFinal() {
        switch (this) {
            case COMPLETED:
            case REJECTED:
            case FAILED:
            case CANCELLED:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determines if status indicates success.
     * This method can be hot-swapped to change success criteria.
     * @return true if successful, false otherwise
     */
    public boolean isSuccessful() {
        return this == COMPLETED;
    }

    /**
     * Determines if status indicates failure.
     * This method can be hot-swapped to change failure criteria.
     * @return true if failed, false otherwise
     */
    public boolean isFailure() {
        switch (this) {
            case REJECTED:
            case FAILED:
            case CANCELLED:
                return true;
            default:
                return false;
        }
    }
}