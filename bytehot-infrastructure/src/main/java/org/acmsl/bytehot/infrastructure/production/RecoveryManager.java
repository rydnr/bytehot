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
 * Filename: RecoveryManager.java
 *
 * Author: Claude Code
 *
 * Class name: RecoveryManager
 *
 * Responsibilities:
 *   - Attempt automatic recovery for classified errors
 *   - Implement retry strategies with exponential backoff
 *   - Manage recovery state and prevent infinite loops
 *   - Provide fallback mechanisms for critical operations
 *
 * Collaborators:
 *   - ErrorClassification: Input for recovery strategy selection
 *   - RecoveryStrategy: Specific recovery implementation
 *   - RecoveryResult: Result of recovery attempt
 *   - RetryPolicy: Configuration for retry behavior
 */
package org.acmsl.bytehot.infrastructure.production;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages automatic recovery attempts for errors based on their classification.
 * @author Claude Code
 * @since 2025-07-04
 */
public class RecoveryManager {
    
    /**
     * Map of recovery strategies by error type.
     */
    private final Map<ErrorType, RecoveryStrategy> recoveryStrategies;
    
    /**
     * Map of recovery attempts to prevent infinite loops.
     */
    private final Map<String, RecoveryAttempt> recoveryAttempts;
    
    /**
     * Default retry policy for recovery attempts.
     */
    private final RetryPolicy defaultRetryPolicy;
    
    /**
     * Maximum number of concurrent recovery attempts.
     */
    private final int maxConcurrentRecoveries;
    
    /**
     * Creates a new RecoveryManager with default configuration.
     */
    public RecoveryManager() {
        this(RetryPolicy.defaultPolicy(), 10);
    }
    
    /**
     * Creates a new RecoveryManager with the specified configuration.
     * @param defaultRetryPolicy The default retry policy
     * @param maxConcurrentRecoveries Maximum concurrent recoveries
     */
    public RecoveryManager(final RetryPolicy defaultRetryPolicy, final int maxConcurrentRecoveries) {
        this.defaultRetryPolicy = defaultRetryPolicy;
        this.maxConcurrentRecoveries = maxConcurrentRecoveries;
        this.recoveryStrategies = new ConcurrentHashMap<>();
        this.recoveryAttempts = new ConcurrentHashMap<>();
        
        initializeDefaultStrategies();
    }
    
    /**
     * Attempts recovery for the given error classification.
     * @param classification The error classification
     * @return The recovery result
     */
    public RecoveryResult attemptRecovery(final ErrorClassification classification) {
        // Check if recovery is appropriate
        if (!shouldAttemptRecovery(classification)) {
            return RecoveryResult.skipped("Recovery not appropriate for this error type");
        }
        
        // Check recovery attempt limits
        String recoveryKey = createRecoveryKey(classification);
        RecoveryAttempt attempt = getOrCreateRecoveryAttempt(recoveryKey, classification);
        
        if (!attempt.canAttemptRecovery()) {
            return RecoveryResult.failed("Maximum recovery attempts exceeded");
        }
        
        // Select and execute recovery strategy
        RecoveryStrategy strategy = selectRecoveryStrategy(classification);
        
        try {
            attempt.recordAttempt();
            RecoveryResult result = strategy.recover(classification, attempt);
            
            if (result.isSuccessful()) {
                // Remove successful recovery from tracking
                recoveryAttempts.remove(recoveryKey);
            }
            
            return result;
            
        } catch (Exception e) {
            return RecoveryResult.failed("Recovery strategy failed: " + e.getMessage());
        }
    }
    
    /**
     * Registers a recovery strategy for a specific error type.
     * @param errorType The error type
     * @param strategy The recovery strategy
     */
    public void registerStrategy(final ErrorType errorType, final RecoveryStrategy strategy) {
        recoveryStrategies.put(errorType, strategy);
    }
    
    /**
     * Gets recovery statistics for monitoring.
     * @return The recovery statistics
     */
    public RecoveryStatistics getRecoveryStatistics() {
        return RecoveryStatistics.builder()
            .totalAttempts(getTotalRecoveryAttempts())
            .successfulAttempts(getSuccessfulRecoveryAttempts())
            .activeRecoveries(recoveryAttempts.size())
            .build();
    }
    
    /**
     * Checks if recovery should be attempted for the given classification.
     * @param classification The error classification
     * @return true if recovery should be attempted, false otherwise
     */
    protected boolean shouldAttemptRecovery(final ErrorClassification classification) {
        // Don't attempt recovery for permanent errors
        if (classification.getRecoverability() == Recoverability.PERMANENT) {
            return false;
        }
        
        // Don't exceed concurrent recovery limit
        if (recoveryAttempts.size() >= maxConcurrentRecoveries) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Selects the appropriate recovery strategy for the classification.
     * @param classification The error classification
     * @return The recovery strategy to use
     */
    protected RecoveryStrategy selectRecoveryStrategy(final ErrorClassification classification) {
        RecoveryStrategy strategy = recoveryStrategies.get(classification.getErrorType());
        
        if (strategy != null) {
            return strategy;
        }
        
        // Fall back to default strategy based on recoverability
        return createDefaultStrategy(classification.getRecoverability());
    }
    
    /**
     * Creates a recovery key for tracking attempts.
     * @param classification The error classification
     * @return The recovery key
     */
    protected String createRecoveryKey(final ErrorClassification classification) {
        return classification.getErrorType() + ":" + 
               classification.getError().getClass().getSimpleName() + ":" +
               classification.getError().getMessage();
    }
    
    /**
     * Gets or creates a recovery attempt for the given key.
     * @param key The recovery key
     * @param classification The error classification
     * @return The recovery attempt
     */
    protected RecoveryAttempt getOrCreateRecoveryAttempt(final String key, 
                                                        final ErrorClassification classification) {
        return recoveryAttempts.computeIfAbsent(key, k -> 
            new RecoveryAttempt(classification, defaultRetryPolicy));
    }
    
    /**
     * Initializes default recovery strategies.
     */
    protected void initializeDefaultStrategies() {
        // Network error recovery: Retry with exponential backoff
        registerStrategy(ErrorType.NETWORK, new NetworkRecoveryStrategy());
        
        // Timeout error recovery: Increase timeout and retry
        registerStrategy(ErrorType.TIMEOUT, new TimeoutRecoveryStrategy());
        
        // IO error recovery: Retry with file system checks
        registerStrategy(ErrorType.IO, new IORecoveryStrategy());
        
        // Memory error recovery: Trigger GC and retry
        registerStrategy(ErrorType.MEMORY, new MemoryRecoveryStrategy());
        
        // External dependency recovery: Circuit breaker and fallback
        registerStrategy(ErrorType.EXTERNAL_DEPENDENCY, new ExternalDependencyRecoveryStrategy());
    }
    
    /**
     * Creates a default recovery strategy based on recoverability.
     * @param recoverability The error recoverability
     * @return The default recovery strategy
     */
    protected RecoveryStrategy createDefaultStrategy(final Recoverability recoverability) {
        switch (recoverability) {
            case TRANSIENT:
                return new SimpleRetryStrategy(defaultRetryPolicy);
            case UNKNOWN:
                return new CautiousRetryStrategy(defaultRetryPolicy);
            default:
                return new NoRecoveryStrategy();
        }
    }
    
    /**
     * Gets the total number of recovery attempts.
     * @return The total recovery attempts
     */
    protected long getTotalRecoveryAttempts() {
        // Implementation would track this metric
        return 0;
    }
    
    /**
     * Gets the number of successful recovery attempts.
     * @return The successful recovery attempts
     */
    protected long getSuccessfulRecoveryAttempts() {
        // Implementation would track this metric
        return 0;
    }
}