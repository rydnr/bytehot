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
 * Filename: ResourceOptimizationType.java
 *
 * Author: Claude Code
 *
 * Class name: ResourceOptimizationType
 *
 * Responsibilities:
 *   - Define types of resource optimizations
 *   - Support optimization categorization
 *   - Enable type-specific optimization handling
 *
 * Collaborators:
 *   - ResourceOptimization: Uses this enum for optimization classification
 */
package org.acmsl.bytehot.infrastructure.production;

/**
 * Enumeration of resource optimization types.
 * @author Claude Code
 * @since 2025-07-04
 */
public enum ResourceOptimizationType {
    
    /**
     * Memory-focused optimizations.
     */
    MEMORY("Memory Optimization", "Optimizations focused on memory usage and allocation"),
    
    /**
     * CPU and performance-focused optimizations.
     */
    PERFORMANCE("Performance Optimization", "Optimizations focused on CPU usage and performance"),
    
    /**
     * Hot-swap operation optimizations.
     */
    HOTSWAP("Hot-Swap Optimization", "Optimizations focused on hot-swap operation performance"),
    
    /**
     * I/O and disk-focused optimizations.
     */
    IO("I/O Optimization", "Optimizations focused on I/O operations and disk usage"),
    
    /**
     * Network-focused optimizations.
     */
    NETWORK("Network Optimization", "Optimizations focused on network usage and latency"),
    
    /**
     * Threading and concurrency optimizations.
     */
    THREADING("Threading Optimization", "Optimizations focused on thread management and concurrency"),
    
    /**
     * General system resource optimizations.
     */
    GENERAL("General Optimization", "General system resource optimizations");
    
    /**
     * Human-readable name of the optimization type.
     */
    private final String displayName;
    
    /**
     * Description of the optimization type.
     */
    private final String description;
    
    /**
     * Creates a new ResourceOptimizationType.
     * @param displayName The display name
     * @param description The description
     */
    ResourceOptimizationType(final String displayName, final String description) {
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
     * Determines if this optimization type typically requires immediate action.
     * @return true if typically requires immediate action, false otherwise
     */
    public boolean requiresImmediateAction() {
        switch (this) {
            case MEMORY:
            case PERFORMANCE:
                return true;
            case HOTSWAP:
            case IO:
            case NETWORK:
            case THREADING:
            case GENERAL:
                return false;
            default:
                return false;
        }
    }
    
    /**
     * Gets the typical impact level of this optimization type.
     * @return The impact level (1-5, where 5 is highest impact)
     */
    public int getTypicalImpactLevel() {
        switch (this) {
            case MEMORY:
                return 5;
            case PERFORMANCE:
                return 4;
            case HOTSWAP:
                return 3;
            case THREADING:
                return 3;
            case IO:
                return 2;
            case NETWORK:
                return 2;
            case GENERAL:
                return 1;
            default:
                return 1;
        }
    }
}