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
 * Filename: PerformanceAlertType.java
 *
 * Author: Claude Code
 *
 * Class name: PerformanceAlertType
 *
 * Responsibilities:
 *   - Define types of performance alerts
 *   - Support alert categorization and handling
 *   - Enable alert type-specific behavior
 *
 * Collaborators:
 *   - PerformanceAlert: Uses this enum for alert classification
 */
package org.acmsl.bytehot.infrastructure.production;

/**
 * Enumeration of performance alert types.
 * @author Claude Code
 * @since 2025-07-04
 */
public enum PerformanceAlertType {
    
    /**
     * Memory usage threshold exceeded.
     */
    MEMORY_USAGE("Memory Usage", "High memory usage detected"),
    
    /**
     * CPU usage threshold exceeded.
     */
    CPU_USAGE("CPU Usage", "High CPU usage detected"),
    
    /**
     * Hot-swap operation performance degraded.
     */
    HOTSWAP_PERFORMANCE("Hot-Swap Performance", "Hot-swap operations are taking too long"),
    
    /**
     * Garbage collection activity is excessive.
     */
    GARBAGE_COLLECTION("Garbage Collection", "Excessive garbage collection activity"),
    
    /**
     * Thread count threshold exceeded.
     */
    THREAD_COUNT("Thread Count", "High number of threads detected"),
    
    /**
     * Application response time degraded.
     */
    RESPONSE_TIME("Response Time", "Application response time degraded"),
    
    /**
     * System health check failed.
     */
    HEALTH_CHECK("Health Check", "System health check failed"),
    
    /**
     * Resource exhaustion detected.
     */
    RESOURCE_EXHAUSTION("Resource Exhaustion", "System resources are exhausted"),
    
    /**
     * Performance trend indicates degradation.
     */
    PERFORMANCE_TREND("Performance Trend", "Performance trend indicates degradation");
    
    /**
     * Human-readable name of the alert type.
     */
    private final String displayName;
    
    /**
     * Default description for this alert type.
     */
    private final String defaultDescription;
    
    /**
     * Creates a new PerformanceAlertType.
     * @param displayName The display name
     * @param defaultDescription The default description
     */
    PerformanceAlertType(final String displayName, final String defaultDescription) {
        this.displayName = displayName;
        this.defaultDescription = defaultDescription;
    }
    
    /**
     * Gets the display name.
     * @return The display name
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Gets the default description.
     * @return The default description
     */
    public String getDefaultDescription() {
        return defaultDescription;
    }
    
    /**
     * Determines if this alert type is critical by nature.
     * @return true if typically critical, false otherwise
     */
    public boolean isTypicallyCritical() {
        switch (this) {
            case RESOURCE_EXHAUSTION:
            case HEALTH_CHECK:
                return true;
            case MEMORY_USAGE:
            case CPU_USAGE:
            case GARBAGE_COLLECTION:
                return false;
            default:
                return false;
        }
    }
    
    /**
     * Determines if this alert type requires immediate attention.
     * @return true if requires immediate attention, false otherwise
     */
    public boolean requiresImmediateAttention() {
        switch (this) {
            case RESOURCE_EXHAUSTION:
            case HEALTH_CHECK:
            case MEMORY_USAGE:
                return true;
            case CPU_USAGE:
            case HOTSWAP_PERFORMANCE:
            case GARBAGE_COLLECTION:
            case THREAD_COUNT:
            case RESPONSE_TIME:
            case PERFORMANCE_TREND:
                return false;
            default:
                return false;
        }
    }
    
    /**
     * Gets the recommended action for this alert type.
     * @return The recommended action
     */
    public String getRecommendedAction() {
        switch (this) {
            case MEMORY_USAGE:
                return "Consider increasing heap size or investigating memory leaks";
            case CPU_USAGE:
                return "Investigate high CPU usage patterns and optimize performance";
            case HOTSWAP_PERFORMANCE:
                return "Review hot-swap operations and consider optimization";
            case GARBAGE_COLLECTION:
                return "Tune garbage collection settings or investigate memory allocation patterns";
            case THREAD_COUNT:
                return "Investigate thread usage and potential thread leaks";
            case RESPONSE_TIME:
                return "Investigate response time degradation and optimize performance";
            case HEALTH_CHECK:
                return "Investigate system health issues and restore service";
            case RESOURCE_EXHAUSTION:
                return "Free up system resources or scale resources";
            case PERFORMANCE_TREND:
                return "Investigate performance trends and plan optimization";
            default:
                return "Investigate and take appropriate action";
        }
    }
}