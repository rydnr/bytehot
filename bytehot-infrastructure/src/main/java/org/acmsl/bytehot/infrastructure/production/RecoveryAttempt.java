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
 * Filename: RecoveryAttempt.java
 *
 * Author: Claude Code
 *
 * Class name: RecoveryAttempt
 *
 * Responsibilities:
 *   - Track recovery attempt context and state
 *   - Manage retry logic and backoff strategies
 *   - Prevent infinite recovery loops
 *
 * Collaborators:
 *   - RecoveryManager: Creates and manages recovery attempts
 *   - RecoveryStrategy: Uses attempt context for recovery decisions
 *   - RetryPolicy: Provides retry configuration
 */
package org.acmsl.bytehot.infrastructure.production;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a recovery attempt with context and state tracking.
 * @author Claude Code
 * @since 2025-07-04
 */
public class RecoveryAttempt {
    
    /**
     * The error classification being recovered from.
     */
    private final ErrorClassification classification;
    
    /**
     * The retry policy governing this attempt.
     */
    private final RetryPolicy retryPolicy;
    
    /**
     * The time when the first attempt was made.
     */
    private final Instant firstAttemptTime;
    
    /**
     * The current number of attempts made.
     */
    private int attemptCount;
    
    /**
     * The time when the last attempt was made.
     */
    private Instant lastAttemptTime;
    
    /**
     * The history of recovery results.
     */
    private final List<RecoveryResult> attemptHistory;
    
    /**
     * The total time spent on recovery attempts.
     */
    private Duration totalRecoveryTime;
    
    /**
     * Whether this recovery attempt is currently active.
     */
    private boolean active;
    
    /**
     * Creates a new RecoveryAttempt.
     * @param classification The error classification
     * @param retryPolicy The retry policy
     */
    public RecoveryAttempt(final ErrorClassification classification,
                          final RetryPolicy retryPolicy) {
        this.classification = classification;
        this.retryPolicy = retryPolicy;
        this.firstAttemptTime = Instant.now();
        this.attemptCount = 0;
        this.lastAttemptTime = null;
        this.attemptHistory = new ArrayList<>();
        this.totalRecoveryTime = Duration.ZERO;
        this.active = true;
    }
    
    /**
     * Records a new recovery attempt.
     */
    public void recordAttempt() {
        attemptCount++;
        lastAttemptTime = Instant.now();
    }
    
    /**
     * Records the result of a recovery attempt.
     * @param result The recovery result
     */
    public void recordResult(final RecoveryResult result) {
        attemptHistory.add(result);
        
        if (result.getDuration().isPresent()) {
            totalRecoveryTime = totalRecoveryTime.plus(result.getDuration().get());
        }
        
        if (result.isSuccessful()) {
            active = false;
        }
    }
    
    /**
     * Checks if another recovery attempt can be made.
     * @return true if another attempt is allowed, false otherwise
     */
    public boolean canAttemptRecovery() {
        if (!active) {
            return false;
        }
        
        // Check maximum attempts
        if (attemptCount >= retryPolicy.getMaxAttempts()) {
            return false;
        }
        
        // Check total time limit
        Duration totalTime = Duration.between(firstAttemptTime, Instant.now());
        if (totalTime.compareTo(retryPolicy.getMaxTotalTime()) > 0) {
            return false;
        }
        
        // Check if enough time has passed since last attempt
        if (lastAttemptTime != null) {
            Duration timeSinceLastAttempt = Duration.between(lastAttemptTime, Instant.now());
            Duration requiredWaitTime = retryPolicy.getWaitTime(attemptCount);
            
            if (timeSinceLastAttempt.compareTo(requiredWaitTime) < 0) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Gets the time to wait before the next attempt.
     * @return The wait time, or Duration.ZERO if ready to attempt
     */
    public Duration getTimeUntilNextAttempt() {
        if (!active || attemptCount >= retryPolicy.getMaxAttempts()) {
            return Duration.ofDays(365); // Effectively never
        }
        
        if (lastAttemptTime == null) {
            return Duration.ZERO;
        }
        
        Duration timeSinceLastAttempt = Duration.between(lastAttemptTime, Instant.now());
        Duration requiredWaitTime = retryPolicy.getWaitTime(attemptCount);
        
        if (timeSinceLastAttempt.compareTo(requiredWaitTime) >= 0) {
            return Duration.ZERO;
        }
        
        return requiredWaitTime.minus(timeSinceLastAttempt);
    }
    
    /**
     * Gets the error classification.
     * @return The error classification
     */
    public ErrorClassification getClassification() {
        return classification;
    }
    
    /**
     * Gets the retry policy.
     * @return The retry policy
     */
    public RetryPolicy getRetryPolicy() {
        return retryPolicy;
    }
    
    /**
     * Gets the first attempt time.
     * @return The first attempt time
     */
    public Instant getFirstAttemptTime() {
        return firstAttemptTime;
    }
    
    /**
     * Gets the current attempt count.
     * @return The attempt count
     */
    public int getAttemptCount() {
        return attemptCount;
    }
    
    /**
     * Gets the last attempt time.
     * @return The last attempt time, or null if no attempts made
     */
    public Instant getLastAttemptTime() {
        return lastAttemptTime;
    }
    
    /**
     * Gets the attempt history.
     * @return An immutable list of recovery results
     */
    public List<RecoveryResult> getAttemptHistory() {
        return Collections.unmodifiableList(attemptHistory);
    }
    
    /**
     * Gets the total recovery time.
     * @return The total recovery time
     */
    public Duration getTotalRecoveryTime() {
        return totalRecoveryTime;
    }
    
    /**
     * Checks if this recovery attempt is active.
     * @return true if active, false otherwise
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * Gets the total time since the first attempt.
     * @return The total elapsed time
     */
    public Duration getTotalElapsedTime() {
        return Duration.between(firstAttemptTime, Instant.now());
    }
    
    /**
     * Gets the success rate of recovery attempts.
     * @return The success rate (0.0 to 1.0)
     */
    public double getSuccessRate() {
        if (attemptHistory.isEmpty()) {
            return 0.0;
        }
        
        long successCount = attemptHistory.stream()
            .mapToLong(result -> result.isSuccessful() ? 1 : 0)
            .sum();
        
        return (double) successCount / attemptHistory.size();
    }
    
    /**
     * Gets the last recovery result.
     * @return The last recovery result, or null if none
     */
    public RecoveryResult getLastResult() {
        if (attemptHistory.isEmpty()) {
            return null;
        }
        
        return attemptHistory.get(attemptHistory.size() - 1);
    }
    
    /**
     * Deactivates this recovery attempt.
     */
    public void deactivate() {
        active = false;
    }
}