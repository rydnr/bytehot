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
 * Filename: ErrorHandlingOutcome.java
 *
 * Author: Claude Code
 *
 * Class name: ErrorHandlingOutcome
 *
 * Responsibilities:
 *   - Define possible outcomes of error handling operations
 *   - Support error handling result classification
 *   - Enable outcome-based decision making
 *
 * Collaborators:
 *   - ErrorHandlingResult: Uses this enum for outcome classification
 *   - ProductionErrorHandler: References outcomes for handling logic
 */
package org.acmsl.bytehot.infrastructure.production;

/**
 * Enumeration of error handling outcomes.
 * @author Claude Code
 * @since 2025-07-04
 */
public enum ErrorHandlingOutcome {
    
    /**
     * Error was successfully recovered and operation can continue normally.
     */
    RECOVERED("Error was successfully recovered"),
    
    /**
     * Error was handled by graceful degradation with reduced functionality.
     */
    GRACEFULLY_DEGRADED("Error handled with graceful degradation"),
    
    /**
     * Error was contained by circuit breaker to prevent cascading failures.
     */
    CIRCUIT_BREAKER_TRIGGERED("Circuit breaker triggered to contain error"),
    
    /**
     * Error was reported as an incident for investigation.
     */
    INCIDENT_REPORTED("Error reported as incident for investigation"),
    
    /**
     * Error handling failed and the error could not be resolved.
     */
    FAILED("Error handling failed to resolve the error"),
    
    /**
     * Error was classified but no recovery action was taken.
     */
    CLASSIFIED_ONLY("Error was classified but no recovery attempted"),
    
    /**
     * Error was suppressed or ignored based on classification.
     */
    SUPPRESSED("Error was suppressed based on classification rules");
    
    /**
     * Human-readable description of the outcome.
     */
    private final String description;
    
    /**
     * Creates a new ErrorHandlingOutcome with the specified description.
     * @param description The outcome description
     */
    ErrorHandlingOutcome(final String description) {
        this.description = description;
    }
    
    /**
     * Gets the human-readable description of this outcome.
     * @return The outcome description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Determines if this outcome represents a successful handling.
     * @return true if the outcome is considered successful, false otherwise
     */
    public boolean isSuccessful() {
        switch (this) {
            case RECOVERED:
            case GRACEFULLY_DEGRADED:
            case CIRCUIT_BREAKER_TRIGGERED:
            case SUPPRESSED:
                return true;
            case INCIDENT_REPORTED:
            case FAILED:
            case CLASSIFIED_ONLY:
                return false;
            default:
                return false;
        }
    }
    
    /**
     * Determines if this outcome requires follow-up action.
     * @return true if follow-up action is required, false otherwise
     */
    public boolean requiresFollowUp() {
        switch (this) {
            case INCIDENT_REPORTED:
            case FAILED:
            case CIRCUIT_BREAKER_TRIGGERED:
                return true;
            case RECOVERED:
            case GRACEFULLY_DEGRADED:
            case CLASSIFIED_ONLY:
            case SUPPRESSED:
                return false;
            default:
                return false;
        }
    }
}