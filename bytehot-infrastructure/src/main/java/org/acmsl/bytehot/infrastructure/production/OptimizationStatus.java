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
 * Filename: OptimizationStatus.java
 *
 * Author: Claude Code
 *
 * Class name: OptimizationStatus
 *
 * Responsibilities:
 *   - Define optimization operation status states
 *   - Support optimization lifecycle management
 *   - Enable status-based optimization handling
 *
 * Collaborators:
 *   - ResourceOptimization: Uses this enum for status tracking
 */
package org.acmsl.bytehot.infrastructure.production;

/**
 * Enumeration of optimization operation status values.
 * @author Claude Code
 * @since 2025-07-04
 */
public enum OptimizationStatus {
    
    /**
     * Optimization is currently running.
     */
    RUNNING("Running", "Optimization is currently in progress"),
    
    /**
     * Optimization completed successfully.
     */
    COMPLETED("Completed", "Optimization completed successfully"),
    
    /**
     * Optimization failed with an error.
     */
    FAILED("Failed", "Optimization failed to complete"),
    
    /**
     * Optimization was stopped before completion.
     */
    STOPPED("Stopped", "Optimization was stopped before completion"),
    
    /**
     * Optimization is pending execution.
     */
    PENDING("Pending", "Optimization is waiting to be executed");
    
    /**
     * Human-readable name of the status.
     */
    private final String displayName;
    
    /**
     * Description of the status.
     */
    private final String description;
    
    /**
     * Creates a new OptimizationStatus.
     * @param displayName The display name
     * @param description The description
     */
    OptimizationStatus(final String displayName, final String description) {
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
     * Determines if this status represents a final state.
     * @return true if this is a final state, false otherwise
     */
    public boolean isFinalState() {
        switch (this) {
            case COMPLETED:
            case FAILED:
            case STOPPED:
                return true;
            case RUNNING:
            case PENDING:
                return false;
            default:
                return false;
        }
    }
    
    /**
     * Determines if this status represents a successful outcome.
     * @return true if successful, false otherwise
     */
    public boolean isSuccessful() {
        return this == COMPLETED;
    }
    
    /**
     * Determines if this status represents an active state.
     * @return true if active, false otherwise
     */
    public boolean isActive() {
        return this == RUNNING || this == PENDING;
    }
}