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
 * Filename: CircuitBreakerException.java
 *
 * Author: Claude Code
 *
 * Class name: CircuitBreakerException
 *
 * Responsibilities:
 *   - Represent circuit breaker open state rejection
 *   - Provide context about circuit breaker state
 *   - Enable circuit breaker exception handling
 *
 * Collaborators:
 *   - CircuitBreaker: Throws this exception when circuit is open
 *   - CircuitBreakerState: Provides state context for the exception
 */
package org.acmsl.bytehot.infrastructure.production;

import java.time.Duration;
import java.time.Instant;

/**
 * Exception thrown when circuit breaker is open and rejects requests.
 * @author Claude Code
 * @since 2025-07-04
 */
public class CircuitBreakerException
    extends RuntimeException {
    
    /**
     * Serial version UID for serialization.
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * The circuit breaker name that threw this exception.
     */
    private final String circuitBreakerName;
    
    /**
     * The current state of the circuit breaker.
     */
    private final CircuitBreakerState state;
    
    /**
     * The time when the circuit breaker opened.
     */
    private final Instant openedAt;
    
    /**
     * The duration until the circuit breaker can transition to half-open.
     */
    private final Duration timeUntilHalfOpen;
    
    /**
     * The number of recent failures that triggered the circuit opening.
     */
    private final int recentFailures;
    
    /**
     * Creates a new CircuitBreakerException.
     * @param message The exception message
     * @param circuitBreakerName The circuit breaker name
     * @param state The current circuit breaker state
     * @param openedAt The time when circuit opened
     * @param timeUntilHalfOpen Time until half-open transition
     * @param recentFailures Number of recent failures
     */
    public CircuitBreakerException(final String message,
                                 final String circuitBreakerName,
                                 final CircuitBreakerState state,
                                 final Instant openedAt,
                                 final Duration timeUntilHalfOpen,
                                 final int recentFailures) {
        super(message);
        this.circuitBreakerName = circuitBreakerName;
        this.state = state;
        this.openedAt = openedAt;
        this.timeUntilHalfOpen = timeUntilHalfOpen;
        this.recentFailures = recentFailures;
    }
    
    /**
     * Creates a new CircuitBreakerException with cause.
     * @param message The exception message
     * @param cause The underlying cause
     * @param circuitBreakerName The circuit breaker name
     * @param state The current circuit breaker state
     * @param openedAt The time when circuit opened
     * @param timeUntilHalfOpen Time until half-open transition
     * @param recentFailures Number of recent failures
     */
    public CircuitBreakerException(final String message,
                                 final Throwable cause,
                                 final String circuitBreakerName,
                                 final CircuitBreakerState state,
                                 final Instant openedAt,
                                 final Duration timeUntilHalfOpen,
                                 final int recentFailures) {
        super(message, cause);
        this.circuitBreakerName = circuitBreakerName;
        this.state = state;
        this.openedAt = openedAt;
        this.timeUntilHalfOpen = timeUntilHalfOpen;
        this.recentFailures = recentFailures;
    }
    
    /**
     * Creates a circuit breaker exception for an open circuit.
     * @param circuitBreakerName The circuit breaker name
     * @param openedAt The time when circuit opened
     * @param timeUntilHalfOpen Time until half-open transition
     * @param recentFailures Number of recent failures
     * @return A new CircuitBreakerException
     */
    public static CircuitBreakerException circuitOpen(final String circuitBreakerName,
                                                    final Instant openedAt,
                                                    final Duration timeUntilHalfOpen,
                                                    final int recentFailures) {
        String message = String.format(
            "Circuit breaker '%s' is OPEN. Request rejected. " +
            "Opened at: %s, Time until half-open: %s seconds, Recent failures: %d",
            circuitBreakerName,
            openedAt,
            timeUntilHalfOpen.getSeconds(),
            recentFailures
        );
        
        return new CircuitBreakerException(
            message,
            circuitBreakerName,
            CircuitBreakerState.OPEN,
            openedAt,
            timeUntilHalfOpen,
            recentFailures
        );
    }
    
    /**
     * Creates a circuit breaker exception for half-open circuit at capacity.
     * @param circuitBreakerName The circuit breaker name
     * @return A new CircuitBreakerException
     */
    public static CircuitBreakerException halfOpenAtCapacity(final String circuitBreakerName) {
        String message = String.format(
            "Circuit breaker '%s' is HALF_OPEN and at capacity. Request rejected.",
            circuitBreakerName
        );
        
        return new CircuitBreakerException(
            message,
            circuitBreakerName,
            CircuitBreakerState.HALF_OPEN,
            null,
            Duration.ZERO,
            0
        );
    }
    
    /**
     * Gets the circuit breaker name.
     * @return The circuit breaker name
     */
    public String getCircuitBreakerName() {
        return circuitBreakerName;
    }
    
    /**
     * Gets the circuit breaker state.
     * @return The circuit breaker state
     */
    public CircuitBreakerState getState() {
        return state;
    }
    
    /**
     * Gets the time when the circuit opened.
     * @return The time when circuit opened, or null if not applicable
     */
    public Instant getOpenedAt() {
        return openedAt;
    }
    
    /**
     * Gets the time until half-open transition.
     * @return The time until half-open transition
     */
    public Duration getTimeUntilHalfOpen() {
        return timeUntilHalfOpen;
    }
    
    /**
     * Gets the number of recent failures.
     * @return The number of recent failures
     */
    public int getRecentFailures() {
        return recentFailures;
    }
    
    /**
     * Determines if the circuit breaker should be retried later.
     * @return true if retry is possible later, false otherwise
     */
    public boolean isRetryPossible() {
        return state == CircuitBreakerState.OPEN && timeUntilHalfOpen != null && 
               !timeUntilHalfOpen.isZero() && !timeUntilHalfOpen.isNegative();
    }
    
    /**
     * Gets a human-readable description of when retry might be possible.
     * @return A retry suggestion message
     */
    public String getRetryAdvice() {
        if (!isRetryPossible()) {
            return "Circuit breaker state does not allow retries at this time.";
        }
        
        return String.format(
            "Circuit breaker will transition to HALF_OPEN in %d seconds. Retry after that time.",
            timeUntilHalfOpen.getSeconds()
        );
    }
}