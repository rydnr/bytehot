/*
                        ACM-SL Commons

    Copyright (C) 2002-today  Jose San Leandro Armendariz
                              chous@acm-sl.org

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the License, or any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-307  USA

    Thanks to ACM S.L. for distributing this library under the GPL license.
    Contact info: jose.sanleandro@acm-sl.com

 ******************************************************************************
 *
 * Filename: SimpleOperationResult.java
 *
 * Author: Claude (Anthropic AI)
 *
 * Class name: SimpleOperationResult
 *
 * Responsibilities:
 *   - Provide concrete implementation of OperationResult interface
 *   - Support both successful and failed operation results
 *   - Include rich metadata and error context
 *
 * Collaborators:
 *   - OperationResult: Interface this class implements
 *   - ErrorSeverity: Error severity classification
 */
package org.acmsl.commons.patterns.results;

import org.acmsl.commons.patterns.ErrorSeverity;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Simple implementation of OperationResult providing comprehensive result metadata.
 * Supports both successful and failed operations with rich error context.
 * @param <T> The type of data returned on successful operations
 * @author Claude (Anthropic AI)
 * @since 2025-06-19
 */
@EqualsAndHashCode
@ToString
public final class SimpleOperationResult<T> implements OperationResult<T> {

    /**
     * Unique identifier for this operation.
     */
    
    private final String operationId;

    /**
     * Whether the operation was successful.
     */
    private final boolean successful;
    
    @Override
    public boolean isSuccessful() {
        return successful;
    }

    @Override
    
    public String getOperationId() {
        return operationId;
    }

    @Override
    
    public Optional<T> getResult() {
        return result;
    }

    @Override
    
    public Optional<String> getErrorMessage() {
        return errorMessage;
    }

    @Override
    
    public Instant getTimestamp() {
        return timestamp;
    }

    /**
     * The result data if successful.
     */
    
    private final Optional<T> result;

    /**
     * Error message if the operation failed.
     */
    
    private final Optional<String> errorMessage;

    /**
     * When the operation completed.
     */
    
    private final Instant timestamp;

    /**
     * Additional details about the operation.
     */
    @Getter
    
    private final Optional<String> details;

    /**
     * Error severity if the operation failed.
     */
    @Getter
    
    private final Optional<ErrorSeverity> errorSeverity;

    /**
     * Exception that caused the failure, if any.
     */
    @Getter
    
    private final Optional<Throwable> cause;

    /**
     * Context where the operation was performed.
     */
    @Getter
    
    private final Optional<String> context;

    /**
     * Constructor for SimpleOperationResult.
     */
    public SimpleOperationResult(final String operationId,
                                final boolean successful,
                                final Optional<T> result,
                                final Optional<String> errorMessage,
                                final Instant timestamp,
                                final Optional<String> details,
                                final Optional<ErrorSeverity> errorSeverity,
                                final Optional<Throwable> cause,
                                final Optional<String> context) {
        this.operationId = operationId;
        this.successful = successful;
        this.result = result;
        this.errorMessage = errorMessage;
        this.timestamp = timestamp;
        this.details = details;
        this.errorSeverity = errorSeverity;
        this.cause = cause;
        this.context = context;
    }

    /**
     * Creates a successful operation result.
     * @param operationId Unique identifier for the operation
     * @param result The successful result data
     * @param timestamp When the operation completed
     * @param <T> The type of the result data
     * @return A successful OperationResult
     */
    
    public static <T> SimpleOperationResult<T> success(
        final String operationId,
        final T result,
        final Instant timestamp
    ) {
        return new SimpleOperationResult<>(
            operationId,
            true,
            Optional.ofNullable(result),
            Optional.empty(),
            timestamp,
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty()
        );
    }

    /**
     * Creates a successful operation result with details.
     * @param operationId Unique identifier for the operation
     * @param result The successful result data
     * @param timestamp When the operation completed
     * @param details Additional details about the operation
     * @param <T> The type of the result data
     * @return A successful OperationResult with details
     */
    
    public static <T> SimpleOperationResult<T> success(
        final String operationId,
        final T result,
        final Instant timestamp,
        final String details
    ) {
        return new SimpleOperationResult<>(
            operationId,
            true,
            Optional.ofNullable(result),
            Optional.empty(),
            timestamp,
            Optional.ofNullable(details),
            Optional.empty(),
            Optional.empty(),
            Optional.empty()
        );
    }

    /**
     * Creates a failed operation result.
     * @param operationId Unique identifier for the operation
     * @param errorMessage Error message describing the failure
     * @param timestamp When the operation failed
     * @param severity Error severity level
     * @param cause Exception that caused the failure
     * @param <T> The type of the result data
     * @return A failed OperationResult
     */
    
    public static <T> SimpleOperationResult<T> failure(
        final String operationId,
        final String errorMessage,
        final Instant timestamp,
        final ErrorSeverity severity,
        final Throwable cause
    ) {
        return new SimpleOperationResult<>(
            operationId,
            false,
            Optional.empty(),
            Optional.ofNullable(errorMessage),
            timestamp,
            Optional.empty(),
            Optional.of(severity),
            Optional.ofNullable(cause),
            Optional.empty()
        );
    }

    /**
     * Creates a failed operation result with context.
     * @param operationId Unique identifier for the operation
     * @param errorMessage Error message describing the failure
     * @param timestamp When the operation failed
     * @param severity Error severity level
     * @param cause Exception that caused the failure
     * @param context Context where the operation was performed
     * @param <T> The type of the result data
     * @return A failed OperationResult with context
     */
    
    public static <T> SimpleOperationResult<T> failure(
        final String operationId,
        final String errorMessage,
        final Instant timestamp,
        final ErrorSeverity severity,
        final Throwable cause,
        final String context
    ) {
        return new SimpleOperationResult<>(
            operationId,
            false,
            Optional.empty(),
            Optional.ofNullable(errorMessage),
            timestamp,
            Optional.empty(),
            Optional.of(severity),
            Optional.ofNullable(cause),
            Optional.ofNullable(context)
        );
    }

    /**
     * Creates a successful operation result with auto-generated ID and current timestamp.
     * @param result The successful result data
     * @param <T> The type of the result data
     * @return A successful OperationResult
     */
    
    public static <T> SimpleOperationResult<T> success(final T result) {
        return success(UUID.randomUUID().toString(), result, Instant.now());
    }

    /**
     * Creates a failed operation result with auto-generated ID and current timestamp.
     * @param errorMessage Error message describing the failure
     * @param severity Error severity level
     * @param <T> The type of the result data
     * @return A failed OperationResult
     */
    
    public static <T> SimpleOperationResult<T> failure(
        final String errorMessage,
        final ErrorSeverity severity
    ) {
        return failure(UUID.randomUUID().toString(), errorMessage, Instant.now(), severity, null);
    }

    /**
     * Creates a failed operation result with auto-generated ID, current timestamp, and exception.
     * @param errorMessage Error message describing the failure
     * @param severity Error severity level
     * @param cause Exception that caused the failure
     * @param <T> The type of the result data
     * @return A failed OperationResult
     */
    
    public static <T> SimpleOperationResult<T> failure(
        final String errorMessage,
        final ErrorSeverity severity,
        final Throwable cause
    ) {
        return failure(UUID.randomUUID().toString(), errorMessage, Instant.now(), severity, cause);
    }
}