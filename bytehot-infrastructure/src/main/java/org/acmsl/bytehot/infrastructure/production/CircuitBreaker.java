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
 * Filename: CircuitBreaker.java
 *
 * Author: Claude Code
 *
 * Class name: CircuitBreaker
 *
 * Responsibilities:
 *   - Prevent cascade failures by monitoring operation success/failure rates
 *   - Implement circuit breaker pattern with CLOSED, OPEN, and HALF_OPEN states
 *   - Provide fail-fast behavior when external dependencies are unavailable
 *   - Track failure statistics and recovery attempts
 *
 * Collaborators:
 *   - CircuitBreakerState: Enumeration of circuit breaker states
 *   - CircuitBreakerConfiguration: Configuration parameters for the circuit breaker
 *   - CircuitBreakerException: Exception thrown when circuit is open
 */
package org.acmsl.bytehot.infrastructure.production;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * Implementation of the Circuit Breaker pattern to prevent cascade failures
 * and provide fail-fast behavior for ByteHot operations.
 * @author Claude Code
 * @since 2025-07-04
 */
public class CircuitBreaker {
    
    /**
     * Current state of the circuit breaker.
     */
    private volatile CircuitBreakerState state = CircuitBreakerState.CLOSED;
    
    /**
     * Number of consecutive failures.
     */
    private final AtomicInteger failureCount = new AtomicInteger(0);
    
    /**
     * Timestamp of the last failure.
     */
    private final AtomicLong lastFailureTime = new AtomicLong(0);
    
    /**
     * Timestamp when the circuit was opened.
     */
    private final AtomicLong circuitOpenTime = new AtomicLong(0);
    
    /**
     * Number of successful operations since last reset.
     */
    private final AtomicInteger successCount = new AtomicInteger(0);
    
    /**
     * Configuration for the circuit breaker.
     */
    private final CircuitBreakerConfiguration configuration;
    
    /**
     * Last exception that caused the circuit to open.
     */
    private final AtomicReference<Throwable> lastException = new AtomicReference<>();
    
    /**
     * Creates a new CircuitBreaker with default configuration.
     */
    public CircuitBreaker() {
        this(CircuitBreakerConfiguration.defaultConfiguration());
    }
    
    /**
     * Creates a new CircuitBreaker with the specified configuration.
     * @param configuration The circuit breaker configuration
     */
    public CircuitBreaker(final CircuitBreakerConfiguration configuration) {
        this.configuration = configuration;
    }
    
    /**
     * Executes an operation protected by the circuit breaker.
     * @param operation The operation to execute
     * @param <T> The return type of the operation
     * @return The result of the operation
     * @throws CircuitBreakerException if the circuit is open
     */
    public <T> T execute(final Supplier<T> operation) throws CircuitBreakerException {
        if (state == CircuitBreakerState.OPEN) {
            if (shouldAttemptReset()) {
                state = CircuitBreakerState.HALF_OPEN;
            } else {
                throw new CircuitBreakerException("Circuit breaker is OPEN", lastException.get());
            }
        }
        
        try {
            T result = operation.get();
            onSuccess();
            return result;
        } catch (Exception e) {
            onFailure(e);
            throw e;
        }
    }
    
    /**
     * Records the result of an operation execution.
     * @param successful true if the operation was successful, false otherwise
     */
    public void recordResult(final boolean successful) {
        if (successful) {
            onSuccess();
        } else {
            onFailure(new RuntimeException("Operation failed"));
        }
    }
    
    /**
     * Gets the current state of the circuit breaker.
     * @return The current circuit breaker state
     */
    public CircuitBreakerState getState() {
        return state;
    }
    
    /**
     * Gets the current failure count.
     * @return The number of consecutive failures
     */
    public int getFailureCount() {
        return failureCount.get();
    }
    
    /**
     * Gets the current success count since last reset.
     * @return The number of successful operations
     */
    public int getSuccessCount() {
        return successCount.get();
    }
    
    /**
     * Gets statistics about the circuit breaker operation.
     * @return The circuit breaker statistics
     */
    public CircuitBreakerStatistics getStatistics() {
        return CircuitBreakerStatistics.builder()
            .state(state)
            .failureCount(failureCount.get())
            .successCount(successCount.get())
            .lastFailureTime(Instant.ofEpochMilli(lastFailureTime.get()))
            .circuitOpenTime(circuitOpenTime.get() > 0 ? Instant.ofEpochMilli(circuitOpenTime.get()) : null)
            .lastException(lastException.get())
            .configuration(configuration)
            .build();
    }
    
    /**
     * Manually resets the circuit breaker to CLOSED state.
     */
    public void reset() {
        state = CircuitBreakerState.CLOSED;
        failureCount.set(0);
        successCount.set(0);
        lastFailureTime.set(0);
        circuitOpenTime.set(0);
        lastException.set(null);
    }
    
    /**
     * Manually opens the circuit breaker.
     * @param reason The reason for opening the circuit
     */
    public void open(final String reason) {
        state = CircuitBreakerState.OPEN;
        circuitOpenTime.set(System.currentTimeMillis());
        lastException.set(new RuntimeException(reason));
    }
    
    /**
     * Handles successful operation execution.
     */
    protected void onSuccess() {
        successCount.incrementAndGet();
        
        if (state == CircuitBreakerState.HALF_OPEN) {
            if (successCount.get() >= configuration.getSuccessThreshold()) {
                // Enough successful operations in half-open state, close the circuit
                reset();
            }
        } else if (state == CircuitBreakerState.CLOSED) {
            // Reset failure count on successful operation in closed state
            failureCount.set(0);
        }
    }
    
    /**
     * Handles failed operation execution.
     * @param exception The exception that caused the failure
     */
    protected void onFailure(final Exception exception) {
        failureCount.incrementAndGet();
        lastFailureTime.set(System.currentTimeMillis());
        lastException.set(exception);
        
        if (state == CircuitBreakerState.HALF_OPEN) {
            // Failure in half-open state, go back to open
            state = CircuitBreakerState.OPEN;
            circuitOpenTime.set(System.currentTimeMillis());
        } else if (state == CircuitBreakerState.CLOSED) {
            // Check if we should open the circuit
            if (failureCount.get() >= configuration.getFailureThreshold()) {
                state = CircuitBreakerState.OPEN;
                circuitOpenTime.set(System.currentTimeMillis());
            }
        }
    }
    
    /**
     * Checks if we should attempt to reset the circuit from OPEN to HALF_OPEN.
     * @return true if reset should be attempted, false otherwise
     */
    protected boolean shouldAttemptReset() {
        if (circuitOpenTime.get() == 0) {
            return false;
        }
        
        Duration timeOpen = Duration.ofMillis(System.currentTimeMillis() - circuitOpenTime.get());
        return timeOpen.compareTo(configuration.getTimeout()) >= 0;
    }
}