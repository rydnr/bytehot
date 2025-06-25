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
 * Filename: OperationResult.java
 *
 * Author: Claude (Anthropic AI)
 *
 * Class name: OperationResult
 *
 * Responsibilities:
 *   - Define interface for operation result objects
 *   - Provide consistent success/failure semantics
 *   - Support rich error context and metadata
 *
 * Collaborators:
 *   - ErrorSeverity: Severity information for failed operations
 *   - ErrorCategory: Classification of operation failures
 */
package org.acmsl.commons.patterns.results;

import org.acmsl.commons.patterns.ErrorSeverity;

import java.time.Instant;
import java.util.Optional;
import java.util.function.Function;

/**
 * Interface for operation result objects providing consistent success/failure semantics.
 * Supports rich error context and metadata for comprehensive error handling.
 * @param <T> The type of data returned on successful operations
 * @author Claude (Anthropic AI)
 * @since 2025-06-19
 */
public interface OperationResult<T> {

    /**
     * Checks if the operation was successful.
     * @return true if the operation completed successfully
     */
    boolean isSuccessful();

    /**
     * Checks if the operation failed.
     * @return true if the operation failed
     */
    default boolean isFailure() {
        return !isSuccessful();
    }

    /**
     * Gets the result data if the operation was successful.
     * @return The operation result, empty if the operation failed
     */
    
    Optional<T> getResult();

    /**
     * Gets the error message if the operation failed.
     * @return The error message, empty if the operation was successful
     */
    
    Optional<String> getErrorMessage();

    /**
     * Gets the timestamp when the operation completed.
     * @return The completion timestamp
     */
    
    Instant getTimestamp();

    /**
     * Gets the unique identifier for this operation.
     * @return The operation identifier
     */
    
    String getOperationId();

    /**
     * Gets additional details about the operation.
     * @return Optional details, empty if no additional information is available
     */
    
    default Optional<String> getDetails() {
        return Optional.empty();
    }

    /**
     * Gets the error severity if the operation failed.
     * @return The error severity, empty if the operation was successful
     */
    
    default Optional<ErrorSeverity> getErrorSeverity() {
        return Optional.empty();
    }

    /**
     * Gets the exception that caused the failure, if any.
     * @return The causing exception, empty if no exception or operation was successful
     */
    
    default Optional<Throwable> getCause() {
        return Optional.empty();
    }

    /**
     * Gets the context where the operation was performed.
     * @return Optional context information
     */
    
    default Optional<String> getContext() {
        return Optional.empty();
    }

    /**
     * Checks if this operation requires immediate attention.
     * @return true if immediate attention is required
     */
    default boolean requiresImmediateAttention() {
        return getErrorSeverity()
            .map(severity -> severity.isAtLeastAsSevereAs(ErrorSeverity.CRITICAL))
            .orElse(false);
    }

    /**
     * Gets the error message or a default message if successful.
     * @return The error message or empty string if successful
     */
    
    default String getErrorMessageOrEmpty() {
        return getErrorMessage().orElse("");
    }

    /**
     * Gets the details or a default message if not available.
     * @return The details or empty string if not available
     */
    
    default String getDetailsOrEmpty() {
        return getDetails().orElse("");
    }

    /**
     * Maps the result to another type if the operation was successful.
     * @param mapper Function to transform the result
     * @param <U> The target type
     * @return A new OperationResult with the mapped result
     */
    
    default <U> OperationResult<U> map(final Function<T, U> mapper) {
        if (isSuccessful() && getResult().isPresent()) {
            U mappedResult = mapper.apply(getResult().get());
            return SimpleOperationResult.success(getOperationId(), mappedResult, getTimestamp());
        } else {
            return SimpleOperationResult.failure(
                getOperationId(),
                getErrorMessageOrEmpty(),
                getTimestamp(),
                getErrorSeverity().orElse(ErrorSeverity.ERROR),
                getCause().orElse(null)
            );
        }
    }
}