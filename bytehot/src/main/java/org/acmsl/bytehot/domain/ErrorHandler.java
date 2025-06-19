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
 *   - EventSnapshotException: Enhanced exceptions with event context
 *   - EventSnapshotGenerator: Automatic snapshot generation
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
     * Handles an error with full context information, automatically generating event snapshots
     * @param error the exception that occurred
     * @param className the class name where error occurred
     * @param operation the operation being performed
     * @return error handling result with recovery strategy
     */
    public ErrorResult handleErrorWithContext(final Throwable error, final String className, final String operation) {
        // Generate event snapshot for comprehensive debugging
        EventSnapshot eventSnapshot = generateEventSnapshot(error);
        
        // Create enhanced exception with event context if snapshot was generated
        Throwable enhancedException = enhanceExceptionWithSnapshot(error, eventSnapshot);
        // Increment error count for this class
        if (className != null) {
            errorCounts.computeIfAbsent(className, k -> new AtomicInteger(0)).incrementAndGet();
        }

        // Classify the error type (use enhanced exception)
        final ErrorType errorType = classifyError(enhancedException);
        
        // Assess severity (use enhanced exception)
        final ErrorSeverity severity = assessSeverity(enhancedException);
        
        // Determine recovery strategy based on error type and context
        final RecoveryStrategy strategy = determineRecoveryStrategy(errorType, enhancedException, operation);
        
        // Determine if error is recoverable
        final boolean recoverable = isRecoverable(errorType, severity);
        
        // Build error message (use enhanced exception)
        final String errorMessage = buildErrorMessage(enhancedException, className, operation);

        return ErrorResult.create(
            errorType,
            severity,
            strategy,
            recoverable,
            errorMessage,
            className,
            operation,
            enhancedException
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
        // If this is an enhanced exception, check the original exception
        Throwable actualError = error;
        if (error instanceof EventSnapshotException) {
            EventSnapshotException enhanced = (EventSnapshotException) error;
            actualError = enhanced.getOriginalException();
            if (actualError == null) {
                actualError = error;
            }
        }
        
        if (actualError instanceof OutOfMemoryError || actualError instanceof StackOverflowError) {
            return ErrorSeverity.CRITICAL;
        }
        
        if (actualError instanceof SecurityException) {
            return ErrorSeverity.ERROR;
        }
        
        if (actualError instanceof IllegalArgumentException || actualError instanceof IllegalStateException) {
            return ErrorSeverity.WARNING;
        }
        
        if (actualError instanceof RuntimeException) {
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
        // If this is an enhanced exception, check the original exception
        Throwable actualError = error;
        if (error instanceof EventSnapshotException) {
            EventSnapshotException enhanced = (EventSnapshotException) error;
            actualError = enhanced.getOriginalException();
            if (actualError == null) {
                actualError = error;
            }
        }
        
        if (actualError instanceof BytecodeValidationException) {
            return ErrorType.VALIDATION_ERROR;
        }
        
        if (actualError instanceof InstanceUpdateException) {
            return ErrorType.INSTANCE_UPDATE_ERROR;
        }
        
        if (actualError instanceof HotSwapException) {
            return ErrorType.REDEFINITION_FAILURE;
        }
        
        if (actualError instanceof SecurityException) {
            return ErrorType.SECURITY_ERROR;
        }
        
        if (actualError instanceof OutOfMemoryError || actualError instanceof StackOverflowError) {
            return ErrorType.CRITICAL_SYSTEM_ERROR;
        }
        
        if (actualError instanceof java.nio.file.NoSuchFileException || 
            actualError instanceof java.nio.file.AccessDeniedException) {
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

    /**
     * Generates an event snapshot for the given error
     * @param error the exception that occurred
     * @return event snapshot, or null if generation fails
     */
    protected EventSnapshot generateEventSnapshot(final Throwable error) {
        try {
            EventSnapshotGenerator generator = EventSnapshotGenerator.getInstance();
            return generator.generateSnapshotForException(error);
        } catch (Exception e) {
            // If snapshot generation fails, don't let it break error handling
            return null;
        }
    }

    /**
     * Enhances an exception with event snapshot context if available
     * @param originalError the original exception
     * @param eventSnapshot the generated snapshot (can be null)
     * @return enhanced exception or original if enhancement fails
     */
    protected Throwable enhanceExceptionWithSnapshot(final Throwable originalError, final EventSnapshot eventSnapshot) {
        if (eventSnapshot == null) {
            return originalError;
        }
        
        try {
            ErrorContext errorContext = ErrorContext.capture();
            return new EventSnapshotException(originalError, eventSnapshot, errorContext);
        } catch (Exception e) {
            // If enhancement fails, return original error
            return originalError;
        }
    }

    /**
     * Creates an EventSnapshotException from an error and snapshot
     * @param error the original exception
     * @param snapshot the event snapshot
     * @return enhanced exception with complete context
     */
    public EventSnapshotException createEventSnapshotException(final Throwable error, final EventSnapshot snapshot) {
        ErrorContext errorContext = ErrorContext.capture();
        return new EventSnapshotException(error, snapshot, errorContext);
    }

    /**
     * Handles an error with automatic snapshot generation and enhancement
     * @param error the exception that occurred
     * @return EventSnapshotException with complete debugging context
     */
    public EventSnapshotException handleErrorWithSnapshot(final Throwable error) {
        EventSnapshot snapshot = generateEventSnapshot(error);
        return createEventSnapshotException(error, snapshot);
    }
}