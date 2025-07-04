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
 * Filename: AlertSeverity.java
 *
 * Author: Claude Code
 *
 * Class name: AlertSeverity
 *
 * Responsibilities:
 *   - Define alert severity levels
 *   - Support alert prioritization and handling
 *   - Enable severity-based alert routing
 *
 * Collaborators:
 *   - PerformanceAlert: Uses this enum for severity classification
 */
package org.acmsl.bytehot.infrastructure.production;

/**
 * Enumeration of alert severity levels.
 * @author Claude Code
 * @since 2025-07-04
 */
public enum AlertSeverity {
    
    /**
     * Informational alerts that don't require immediate action.
     */
    INFO("Info", "Informational notification"),
    
    /**
     * Warning alerts that should be monitored but aren't critical.
     */
    WARNING("Warning", "Warning condition detected"),
    
    /**
     * Critical alerts that require immediate attention.
     */
    CRITICAL("Critical", "Critical condition requiring immediate attention");
    
    /**
     * Human-readable name of the severity level.
     */
    private final String displayName;
    
    /**
     * Description of the severity level.
     */
    private final String description;
    
    /**
     * Creates a new AlertSeverity.
     * @param displayName The display name
     * @param description The description
     */
    AlertSeverity(final String displayName, final String description) {
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
     * Gets the numeric priority level (higher number = higher priority).
     * @return The priority level
     */
    public int getPriority() {
        switch (this) {
            case INFO:
                return 1;
            case WARNING:
                return 2;
            case CRITICAL:
                return 3;
            default:
                return 0;
        }
    }
    
    /**
     * Determines if this severity requires immediate attention.
     * @return true if requires immediate attention, false otherwise
     */
    public boolean requiresImmediateAttention() {
        return this == CRITICAL;
    }
    
    /**
     * Determines if this severity should trigger notifications.
     * @return true if should trigger notifications, false otherwise
     */
    public boolean shouldTriggerNotifications() {
        return this == WARNING || this == CRITICAL;
    }
    
    /**
     * Gets the recommended response time for this severity.
     * @return The recommended response time description
     */
    public String getRecommendedResponseTime() {
        switch (this) {
            case INFO:
                return "No immediate response required";
            case WARNING:
                return "Respond within business hours";
            case CRITICAL:
                return "Respond immediately";
            default:
                return "Follow standard procedures";
        }
    }
}