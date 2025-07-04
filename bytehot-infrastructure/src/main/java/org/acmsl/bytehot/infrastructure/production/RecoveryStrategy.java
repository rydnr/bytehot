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
 * Filename: RecoveryStrategy.java
 *
 * Author: Claude Code
 *
 * Class name: RecoveryStrategy
 *
 * Responsibilities:
 *   - Define interface for error recovery strategies
 *   - Enable pluggable recovery behavior
 *   - Support context-aware recovery decisions
 *
 * Collaborators:
 *   - RecoveryManager: Uses strategies for error recovery
 *   - ErrorClassification: Provides context for recovery decisions
 *   - RecoveryResult: Represents recovery outcomes
 */
package org.acmsl.bytehot.infrastructure.production;

/**
 * Interface for error recovery strategies.
 * @author Claude Code
 * @since 2025-07-04
 */
public interface RecoveryStrategy {
    
    /**
     * Attempts to recover from the classified error.
     * @param classification The error classification
     * @param attempt The recovery attempt context
     * @return The recovery result
     */
    RecoveryResult recover(ErrorClassification classification, RecoveryAttempt attempt);
    
    /**
     * Checks if this strategy can handle the given error classification.
     * @param classification The error classification
     * @return true if the strategy can handle the error, false otherwise
     */
    boolean canHandle(ErrorClassification classification);
    
    /**
     * Gets the name of this recovery strategy.
     * @return The strategy name
     */
    String getStrategyName();
    
    /**
     * Gets the priority of this strategy for conflict resolution.
     * Lower numbers indicate higher priority.
     * @return The strategy priority
     */
    default int getPriority() {
        return 100;
    }
    
    /**
     * Gets the expected recovery time for this strategy.
     * @param classification The error classification
     * @return The expected recovery time in milliseconds
     */
    default long getExpectedRecoveryTimeMs(final ErrorClassification classification) {
        return 1000; // Default 1 second
    }
    
    /**
     * Checks if this strategy supports the given error type.
     * @param errorType The error type
     * @return true if supported, false otherwise
     */
    default boolean supportsErrorType(final ErrorType errorType) {
        return true; // Default supports all error types
    }
    
    /**
     * Checks if this strategy is safe to use in the given context.
     * @param classification The error classification
     * @param attempt The recovery attempt context
     * @return true if safe to use, false otherwise
     */
    default boolean isSafeToUse(final ErrorClassification classification, final RecoveryAttempt attempt) {
        return true; // Default is safe
    }
}