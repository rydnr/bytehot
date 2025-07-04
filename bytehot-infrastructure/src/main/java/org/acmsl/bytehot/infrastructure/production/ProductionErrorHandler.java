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
 * Filename: ProductionErrorHandler.java
 *
 * Author: Claude Code
 *
 * Class name: ProductionErrorHandler
 *
 * Responsibilities:
 *   - Classify and handle errors in production environments
 *   - Implement automatic recovery mechanisms for transient failures
 *   - Manage circuit breaker state and graceful degradation
 *   - Report incidents and maintain error statistics
 *
 * Collaborators:
 *   - ErrorClassifier: Categorizes errors by severity and type
 *   - RecoveryManager: Attempts automatic recovery for known failure patterns
 *   - IncidentReporter: Reports critical incidents to monitoring systems
 *   - CircuitBreaker: Prevents cascade failures during outages
 */
package org.acmsl.bytehot.infrastructure.production;

import org.acmsl.commons.patterns.Port;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

/**
 * Production-grade error handler that provides comprehensive error classification,
 * automatic recovery, and incident reporting for ByteHot operations.
 * @author Claude Code
 * @since 2025-07-04
 */
public class ProductionErrorHandler implements Port {
    
    /**
     * The error classifier for categorizing errors.
     */
    private final ErrorClassifier errorClassifier;
    
    /**
     * The recovery manager for automatic error recovery.
     */
    private final RecoveryManager recoveryManager;
    
    /**
     * The incident reporter for critical error reporting.
     */
    private final IncidentReporter incidentReporter;
    
    /**
     * The circuit breaker for preventing cascade failures.
     */
    private final CircuitBreaker circuitBreaker;
    
    /**
     * Creates a new ProductionErrorHandler with the specified components.
     * @param errorClassifier The error classifier to use
     * @param recoveryManager The recovery manager to use
     * @param incidentReporter The incident reporter to use
     * @param circuitBreaker The circuit breaker to use
     */
    public ProductionErrorHandler(final ErrorClassifier errorClassifier,
                                 final RecoveryManager recoveryManager,
                                 final IncidentReporter incidentReporter,
                                 final CircuitBreaker circuitBreaker) {
        this.errorClassifier = errorClassifier;
        this.recoveryManager = recoveryManager;
        this.incidentReporter = incidentReporter;
        this.circuitBreaker = circuitBreaker;
    }
    
    /**
     * Handles an error that occurred during a ByteHot operation.
     * @param error The error that occurred
     * @param context The operation context
     * @return The error handling result
     */
    public ErrorHandlingResult handleError(final Throwable error, final OperationContext context) {
        try {
            // 1. Classify error severity and type
            final ErrorClassification classification = errorClassifier.classify(error, context);
            
            // 2. Attempt automatic recovery
            final RecoveryResult recovery = recoveryManager.attemptRecovery(classification);
            
            // 3. Report incident if needed
            if (classification.requiresIncidentReport()) {
                incidentReporter.reportIncident(error, context, recovery);
            }
            
            // 4. Update circuit breaker state
            circuitBreaker.recordResult(recovery.isSuccessful());
            
            return ErrorHandlingResult.from(classification, recovery);
            
        } catch (Exception handlingError) {
            // If error handling itself fails, fall back to basic error reporting
            return handleErrorHandlingFailure(error, handlingError, context);
        }
    }
    
    /**
     * Handles an error asynchronously without blocking the calling thread.
     * @param error The error that occurred
     * @param context The operation context
     * @return A CompletableFuture containing the error handling result
     */
    public CompletableFuture<ErrorHandlingResult> handleErrorAsync(final Throwable error, 
                                                                   final OperationContext context) {
        return CompletableFuture.supplyAsync(() -> handleError(error, context));
    }
    
    /**
     * Checks if the system is currently in a degraded state.
     * @return true if the system is degraded, false otherwise
     */
    public boolean isSystemDegraded() {
        return circuitBreaker.getState() != CircuitBreakerState.CLOSED;
    }
    
    /**
     * Gets the current error statistics for monitoring and reporting.
     * @return The current error statistics
     */
    public ErrorStatistics getErrorStatistics() {
        return ErrorStatistics.builder()
            .totalErrors(getTotalErrorCount())
            .recoveredErrors(getRecoveredErrorCount())
            .incidentCount(getIncidentCount())
            .circuitBreakerState(circuitBreaker.getState())
            .lastErrorTime(getLastErrorTime())
            .build();
    }
    
    /**
     * Handles the case where error handling itself fails.
     * @param originalError The original error
     * @param handlingError The error that occurred during handling
     * @param context The operation context
     * @return A basic error handling result
     */
    protected ErrorHandlingResult handleErrorHandlingFailure(final Throwable originalError,
                                                            final Exception handlingError,
                                                            final OperationContext context) {
        // Log the meta-error
        System.err.println("Error handling failed for original error: " + originalError.getMessage());
        System.err.println("Handling error: " + handlingError.getMessage());
        
        // Return a basic failure result
        return ErrorHandlingResult.builder()
            .classification(ErrorClassification.unknown(originalError))
            .recovery(RecoveryResult.failed("Error handling system failure"))
            .handlingError(handlingError)
            .build();
    }
    
    /**
     * Gets the total number of errors handled.
     * @return The total error count
     */
    protected long getTotalErrorCount() {
        // Implementation would track this metric
        return 0;
    }
    
    /**
     * Gets the number of errors that were successfully recovered.
     * @return The recovered error count
     */
    protected long getRecoveredErrorCount() {
        // Implementation would track this metric
        return 0;
    }
    
    /**
     * Gets the number of incidents reported.
     * @return The incident count
     */
    protected long getIncidentCount() {
        // Implementation would track this metric
        return 0;
    }
    
    /**
     * Gets the timestamp of the last error.
     * @return The last error time, or null if no errors have occurred
     */
    protected Instant getLastErrorTime() {
        // Implementation would track this metric
        return null;
    }
}