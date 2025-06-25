/*
                        ACM-SL Commons

    Copyright (C) 2002-today  Jose San Leandro Armendariz
                              chous@acm-sl.org

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the License, or any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-307  USA

    Thanks to ACM S.L. for distributing this library under the GPL license.
    Contact info: jose.sanleandro@acm-sl.com

 ******************************************************************************
 *
 * Filename: RecoveryStrategy.java
 *
 * Author: Claude (Anthropic AI)
 *
 * Class name: RecoveryStrategy
 *
 * Responsibilities:
 *   - Define recovery strategies for different error scenarios
 *   - Guide error handlers on how to recover from failures
 *   - Provide standard recovery patterns for application resilience
 *
 * Collaborators:
 *   - ErrorHandler: Uses strategies for error recovery
 *   - ErrorCategory: Mapped from error types to determine strategy
 */
package org.acmsl.commons.patterns;


/**
 * Enumeration of recovery strategies for error handling.
 * Defines standard approaches for recovering from various types of application failures.
 * @author Claude (Anthropic AI)
 * @since 2025-06-19
 */
public enum RecoveryStrategy {
    
    /**
     * Reject the change and continue with current state.
     * Suitable for validation errors or unsafe operations.
     */
    REJECT_CHANGE("Reject Change", "Refuse the operation and maintain current state"),
    
    /**
     * Rollback to previous known good state.
     * Used when the current state has been corrupted or is inconsistent.
     */
    ROLLBACK_CHANGES("Rollback", "Restore to previous known good state"),
    
    /**
     * Preserve current state and skip update.
     * Continue operation without applying the problematic change.
     */
    PRESERVE_CURRENT_STATE("Preserve State", "Keep current state and skip update"),
    
    /**
     * Retry the operation with modified parameters.
     * Attempt the operation again with adjusted or corrected parameters.
     */
    RETRY_OPERATION("Retry", "Attempt operation again with modifications"),
    
    /**
     * Restart the affected component or service.
     * Reset the component to a clean state.
     */
    RESTART_COMPONENT("Restart", "Restart the affected component"),
    
    /**
     * Emergency shutdown to prevent further damage.
     * Immediately stop operations to prevent cascading failures.
     */
    EMERGENCY_SHUTDOWN("Emergency Shutdown", "Immediate shutdown to prevent damage"),
    
    /**
     * Notify administrators and wait for manual intervention.
     * Escalate the issue to human operators for resolution.
     */
    MANUAL_INTERVENTION("Manual Intervention", "Escalate to human operators"),
    
    /**
     * Use fallback mechanisms or degraded mode.
     * Continue with reduced functionality using alternative methods.
     */
    FALLBACK_MODE("Fallback", "Continue with degraded functionality"),
    
    /**
     * Ignore the error and continue (for non-critical errors).
     * Log the error but proceed with normal operation.
     */
    IGNORE_ERROR("Ignore", "Log error but continue normal operation"),
    
    /**
     * No specific recovery action needed.
     * The error is informational or has already been handled.
     */
    NO_ACTION("No Action", "No recovery action required");

    /**
     * Short name for the strategy.
     */
    
    private final String name;

    /**
     * Detailed description of the strategy.
     */
    
    private final String description;

    /**
     * Constructor for recovery strategies.
     * @param name Short name for the strategy
     * @param description Detailed description of the strategy
     */
    RecoveryStrategy(final String name, final String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Gets the short name of the strategy.
     * @return The strategy name
     */
    
    public String getName() {
        return name;
    }

    /**
     * Gets the detailed description of the strategy.
     * @return The strategy description
     */
    
    public String getDescription() {
        return description;
    }

    /**
     * Checks if this strategy involves stopping or shutting down operations.
     * @return true if this is a shutdown-type strategy
     */
    public boolean isShutdownStrategy() {
        return this == EMERGENCY_SHUTDOWN || this == RESTART_COMPONENT;
    }

    /**
     * Checks if this strategy involves human intervention.
     * @return true if human intervention is required
     */
    public boolean requiresHumanIntervention() {
        return this == MANUAL_INTERVENTION;
    }

    /**
     * Checks if this strategy allows operations to continue.
     * @return true if operations can continue after applying this strategy
     */
    public boolean allowsContinuation() {
        return this != EMERGENCY_SHUTDOWN && this != MANUAL_INTERVENTION;
    }
}