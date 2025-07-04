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
 * Filename: CircuitBreakerState.java
 *
 * Author: Claude Code
 *
 * Class name: CircuitBreakerState
 *
 * Responsibilities:
 *   - Define circuit breaker operational states
 *   - Support state-based decision making
 *   - Enable circuit breaker state transitions
 *
 * Collaborators:
 *   - CircuitBreaker: Uses this enum for state management
 *   - CircuitBreakerStatistics: References states for metrics
 */
package org.acmsl.bytehot.infrastructure.production;

/**
 * Enumeration of circuit breaker states.
 * @author Claude Code
 * @since 2025-07-04
 */
public enum CircuitBreakerState {
    
    /**
     * Circuit breaker is closed and allows all requests to pass through.
     * Failures are monitored and counted.
     */
    CLOSED("Circuit is closed, requests pass through normally"),
    
    /**
     * Circuit breaker is open and rejects all requests immediately.
     * No requests are passed to the protected resource.
     */
    OPEN("Circuit is open, requests are rejected immediately"),
    
    /**
     * Circuit breaker is half-open and allows limited requests to test recovery.
     * If requests succeed, circuit transitions to CLOSED; if they fail, back to OPEN.
     */
    HALF_OPEN("Circuit is half-open, testing recovery with limited requests");
    
    /**
     * Human-readable description of the state.
     */
    private final String description;
    
    /**
     * Creates a new CircuitBreakerState with the specified description.
     * @param description The state description
     */
    CircuitBreakerState(final String description) {
        this.description = description;
    }
    
    /**
     * Gets the human-readable description of this state.
     * @return The state description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Determines if requests should be allowed in this state.
     * @return true if requests are allowed, false otherwise
     */
    public boolean allowsRequests() {
        switch (this) {
            case CLOSED:
            case HALF_OPEN:
                return true;
            case OPEN:
                return false;
            default:
                return false;
        }
    }
    
    /**
     * Determines if this state monitors failures.
     * @return true if failures are monitored, false otherwise
     */
    public boolean monitorsFailures() {
        switch (this) {
            case CLOSED:
            case HALF_OPEN:
                return true;
            case OPEN:
                return false;
            default:
                return false;
        }
    }
    
    /**
     * Determines if this state can transition to another state.
     * @param targetState The target state
     * @return true if transition is allowed, false otherwise
     */
    public boolean canTransitionTo(final CircuitBreakerState targetState) {
        switch (this) {
            case CLOSED:
                return targetState == OPEN;
            case OPEN:
                return targetState == HALF_OPEN;
            case HALF_OPEN:
                return targetState == CLOSED || targetState == OPEN;
            default:
                return false;
        }
    }
}