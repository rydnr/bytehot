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
 * Filename: ErrorHandler.java
 *
 * Author: Claude Code
 *
 * Class name: ErrorHandler
 *
 * Responsibilities:
 *   - Central coordinator for error handling in ByteHot operations
 *   - Classify errors and determine appropriate recovery strategies
 *   - Track error patterns and provide comprehensive error context
 *
 * Collaborators:
 *   - ErrorResult: Contains error handling results
 *   - ErrorType: Classifies different types of errors
 *   - RecoveryStrategy: Defines recovery approaches
 *   - ClassRedefinitionFailed: Handles hot-swap failures
 */
package org.acmsl.bytehot.domain;

import org.acmsl.bytehot.domain.events.ClassRedefinitionFailed;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Central error handler for ByteHot operations
 * @author Claude Code
 * @since 2025-06-17
 */
public class ErrorHandler {

    /**
     * Tracks error counts per class name
     */
    private final ConcurrentHashMap<String, AtomicInteger> errorCounts = new ConcurrentHashMap<>();

    /**
     * Threshold for detecting error patterns
     */
    private static final int ERROR_PATTERN_THRESHOLD = 3;

    /**
     * Handles a general error and determines recovery strategy
     * @param error the exception that occurred
     * @param className the class name where error occurred
     * @return error handling result with recovery strategy
     */
    public ErrorResult handleError(final Throwable error, final String className) {
        return handleErrorWithContext(error, className, null);
    }

    /**
     * Handles an error with full context information
     * @param error the exception that occurred
     * @param className the class name where error occurred
     * @param operation the operation being performed
     * @return error handling result with recovery strategy
     */
    public ErrorResult handleErrorWithContext(final Throwable error, final String className, final String operation) {
        // Increment error count for this class
        if (className != null) {
            errorCounts.computeIfAbsent(className, k -> new AtomicInteger(0)).incrementAndGet();
        }

        // Classify the error type
        final ErrorType errorType = classifyError(error);
        
        // Assess severity
        final ErrorSeverity severity = assessSeverity(error);
        
        // Determine recovery strategy based on error type and context
        final RecoveryStrategy strategy = determineRecoveryStrategy(errorType, error, operation);
        
        // Determine if error is recoverable
        final boolean recoverable = isRecoverable(errorType, severity);
        
        // Build error message
        final String errorMessage = buildErrorMessage(error, className, operation);

        return ErrorResult.create(
            errorType,
            severity,
            strategy,
            recoverable,
            errorMessage,
            className,
            operation,
            error
        );
    }

    /**
     * Handles class redefinition failure specifically
     * @param redefinitionFailed the redefinition failure event
     * @return error handling result
     */
    public ErrorResult handleRedefinitionFailure(final ClassRedefinitionFailed redefinitionFailed) {
        final String className = redefinitionFailed.getClassName();
        
        // Track error for this class
        errorCounts.computeIfAbsent(className, k -> new AtomicInteger(0)).incrementAndGet();

        final String errorMessage = "Class redefinition failed: " + redefinitionFailed.getFailureReason() +
                                   " (JVM Error: " + redefinitionFailed.getJvmError() + ")";

        return ErrorResult.create(
            ErrorType.REDEFINITION_FAILURE,
            ErrorSeverity.ERROR,
            RecoveryStrategy.ROLLBACK_CHANGES,
            true, // Redefinition failures are generally recoverable
            errorMessage,
            className,
            "class-redefinition",
            null // No specific exception cause in this event
        );
    }

    /**
     * Assesses the severity of an error
     * @param error the exception
     * @return severity level
     */
    public ErrorSeverity assessSeverity(final Throwable error) {
        if (error instanceof OutOfMemoryError || error instanceof StackOverflowError) {
            return ErrorSeverity.CRITICAL;
        }
        
        if (error instanceof SecurityException) {
            return ErrorSeverity.ERROR;
        }
        
        if (error instanceof IllegalArgumentException || error instanceof IllegalStateException) {
            return ErrorSeverity.WARNING;
        }
        
        if (error instanceof RuntimeException) {
            return ErrorSeverity.ERROR;
        }
        
        return ErrorSeverity.ERROR; // Default severity
    }

    /**
     * Gets the error count for a specific class
     * @param className the class name
     * @return number of errors for this class
     */
    public int getErrorCount(final String className) {
        final AtomicInteger count = errorCounts.get(className);
        return count != null ? count.get() : 0;
    }

    /**
     * Detects if there's an error pattern for a class
     * @param className the class name
     * @return true if error pattern detected
     */
    public boolean detectErrorPattern(final String className) {
        return getErrorCount(className) >= ERROR_PATTERN_THRESHOLD;
    }

    /**
     * Classifies the type of error based on the exception
     * @param error the exception
     * @return error type classification
     */
    protected ErrorType classifyError(final Throwable error) {
        if (error instanceof BytecodeValidationException) {
            return ErrorType.VALIDATION_ERROR;
        }
        
        if (error instanceof InstanceUpdateException) {
            return ErrorType.INSTANCE_UPDATE_ERROR;
        }
        
        if (error instanceof HotSwapException) {
            return ErrorType.REDEFINITION_FAILURE;
        }
        
        if (error instanceof SecurityException) {
            return ErrorType.SECURITY_ERROR;
        }
        
        if (error instanceof OutOfMemoryError || error instanceof StackOverflowError) {
            return ErrorType.CRITICAL_SYSTEM_ERROR;
        }
        
        if (error instanceof java.nio.file.NoSuchFileException || 
            error instanceof java.nio.file.AccessDeniedException) {
            return ErrorType.FILE_SYSTEM_ERROR;
        }
        
        return ErrorType.UNKNOWN_ERROR; // Default classification
    }

    /**
     * Determines the recovery strategy based on error type and context
     * @param errorType the classified error type
     * @param error the original exception
     * @param operation the operation being performed
     * @return recommended recovery strategy
     */
    protected RecoveryStrategy determineRecoveryStrategy(final ErrorType errorType, final Throwable error, final String operation) {
        switch (errorType) {
            case VALIDATION_ERROR:
                return RecoveryStrategy.REJECT_CHANGE;
                
            case REDEFINITION_FAILURE:
                return RecoveryStrategy.ROLLBACK_CHANGES;
                
            case INSTANCE_UPDATE_ERROR:
                return RecoveryStrategy.PRESERVE_CURRENT_STATE;
                
            case CRITICAL_SYSTEM_ERROR:
                return RecoveryStrategy.EMERGENCY_SHUTDOWN;
                
            case SECURITY_ERROR:
                return RecoveryStrategy.MANUAL_INTERVENTION;
                
            case FILE_SYSTEM_ERROR:
                return RecoveryStrategy.RETRY_OPERATION;
                
            case CONFIGURATION_ERROR:
                return RecoveryStrategy.FALLBACK_MODE;
                
            default:
                return RecoveryStrategy.NO_ACTION;
        }
    }

    /**
     * Determines if an error is recoverable based on type and severity
     * @param errorType the error type
     * @param severity the error severity
     * @return true if error is recoverable
     */
    protected boolean isRecoverable(final ErrorType errorType, final ErrorSeverity severity) {
        // Critical system errors are generally not recoverable
        if (errorType == ErrorType.CRITICAL_SYSTEM_ERROR || severity == ErrorSeverity.FATAL) {
            return false;
        }
        
        // Most other errors are recoverable with appropriate strategies
        return true;
    }

    /**
     * Builds a comprehensive error message
     * @param error the exception
     * @param className the class name
     * @param operation the operation
     * @return formatted error message
     */
    protected String buildErrorMessage(final Throwable error, final String className, final String operation) {
        final StringBuilder message = new StringBuilder();
        
        if (operation != null) {
            message.append("Operation '").append(operation).append("' failed");
        } else {
            message.append("Error occurred");
        }
        
        if (className != null) {
            message.append(" for class ").append(className);
        }
        
        message.append(": ").append(error.getMessage());
        
        return message.toString();
    }
}