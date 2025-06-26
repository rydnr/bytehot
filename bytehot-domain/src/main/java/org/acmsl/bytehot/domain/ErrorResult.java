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
 * Filename: ErrorResult.java
 *
 * Author: Claude Code
 *
 * Class name: ErrorResult
 *
 * Responsibilities:
 *   - Encapsulate error handling results and recovery recommendations
 *   - Provide comprehensive error context and metadata
 *
 * Collaborators:
 *   - ErrorHandler: Produces ErrorResult instances
 *   - ErrorType: Classifies the type of error
 *   - RecoveryStrategy: Recommends recovery approach
 */
package org.acmsl.bytehot.domain;

import java.time.Instant;
import java.util.UUID;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Result of error handling operation containing recovery strategy and context
 * @author Claude Code
 * @since 2025-06-17
 */
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class ErrorResult {

    /**
     * Unique identifier for this error occurrence
     */
    @Getter
    private final String errorId;

    /**
     * Type classification of the error
     */
    @Getter
    private final ErrorType errorType;

    /**
     * Severity level of the error
     */
    @Getter
    private final ErrorSeverity severity;

    /**
     * Recommended recovery strategy
     */
    @Getter
    private final RecoveryStrategy recoveryStrategy;

    /**
     * Whether the error is recoverable
     */
    @Getter
    private final boolean recoverable;

    /**
     * Human-readable error message
     */
    @Getter
    private final String errorMessage;

    /**
     * Class name where the error occurred
     */
    @Getter
    private final String className;

    /**
     * Operation that was being performed when error occurred
     */
    @Getter
    private final String operation;

    /**
     * Original exception that caused the error
     */
    @Getter
    private final Throwable cause;

    /**
     * Timestamp when the error occurred
     */
    @Getter
    private final Instant timestamp;

    /**
     * Creates an ErrorResult with generated error ID and current timestamp
     * @param errorType the error type
     * @param severity the severity
     * @param recoveryStrategy the recovery strategy
     * @param recoverable whether it's recoverable
     * @param errorMessage the error message
     * @param className the name of the class
     * @param operation the operation
     * @param cause the underlying cause
     * @return the {@code ErrorResult}
     */
    public static ErrorResult create(
            final ErrorType errorType,
            final ErrorSeverity severity,
            final RecoveryStrategy recoveryStrategy,
            final boolean recoverable,
            final String errorMessage,
            final String className,
            final String operation,
            final Throwable cause) {
        
        return new ErrorResult(
            UUID.randomUUID().toString(),
            errorType,
            severity,
            recoveryStrategy,
            recoverable,
            errorMessage,
            className,
            operation,
            cause,
            Instant.now()
        );
    }

    /**
     * Creates an ErrorResult with simplified parameters
     * @param errorType the error type
     * @param recoveryStrategy the recovery strategy
     * @param recoverable whether it's recoverable
     * @param errorMessage the error message
     * @return the {@code ErrorResult}
     */
    public static ErrorResult create(
            final ErrorType errorType,
            final RecoveryStrategy recoveryStrategy,
            final boolean recoverable,
            final String errorMessage) {
        
        return create(
            errorType,
            ErrorSeverity.ERROR, // Default severity
            recoveryStrategy,
            recoverable,
            errorMessage,
            null, // No specific class
            null, // No specific operation
            null  // No specific cause
        );
    }

    /**
     * Checks if this error requires immediate attention
     * @return true if severity is CRITICAL or FATAL
     */
    public boolean requiresImmediateAttention() {
        return severity == ErrorSeverity.CRITICAL || severity == ErrorSeverity.FATAL;
    }

    /**
     * Checks if this error should trigger alerts
     * @return true if severity is ERROR or higher
     */
    public boolean shouldTriggerAlert() {
        return severity.ordinal() >= ErrorSeverity.ERROR.ordinal();
    }

    /**
     * Gets a short description of the error for logging
     * @return concise error description
     */
    public String getShortDescription() {
        final StringBuilder desc = new StringBuilder();
        desc.append(errorType.name());
        if (className != null) {
            desc.append(" in ").append(className);
        }
        if (operation != null) {
            desc.append(" during ").append(operation);
        }
        return desc.toString();
    }
}
