/*
                        ByteHot

    Copyright (C) 2025-today  rydnr@acm-sl.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
/******************************************************************************
 *
 * Filename: FlowStorageResult.java
 *
 * Author: Claude (Anthropic AI)
 *
 * Class name: FlowStorageResult
 *
 * Responsibilities:
 *   - Represent result of flow storage operations
 *
 * Collaborators:
 *   - FlowDetectionPort: Returns storage results from operations
 *   - FlowId: Identifies the flow that was operated on
 */
package org.acmsl.bytehot.domain;

import org.acmsl.commons.patterns.dao.ValueObject;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Optional;

/**
 * Result of flow storage operations indicating success or failure.
 * @author Claude (Anthropic AI)
 * @since 2025-06-19
 */
@RequiredArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public final class FlowStorageResult implements ValueObject {

    /**
     * The ID of the flow that was operated on.
     */
    @Getter
    private final FlowId flowId;

    /**
     * Whether the operation was successful.
     */
    @Getter
    private final boolean success;

    /**
     * Optional error message if operation failed.
     */
    @Getter
    private final Optional<String> errorMessage;

    /**
     * Optional details about the operation.
     */
    @Getter
    private final Optional<String> details;

    /**
     * Creates a successful storage result.
     * @param flowId The ID of the flow that was successfully operated on
     * @return A successful FlowStorageResult
     */
    public static FlowStorageResult success(final FlowId flowId) {
        return FlowStorageResult.builder()
            .flowId(flowId)
            .success(true)
            .errorMessage(Optional.empty())
            .details(Optional.empty())
            .build();
    }

    /**
     * Creates a successful storage result with details.
     * @param flowId The ID of the flow that was successfully operated on
     * @param details Additional details about the operation
     * @return A successful FlowStorageResult with details
     */
    public static FlowStorageResult success(final FlowId flowId, final String details) {
        return FlowStorageResult.builder()
            .flowId(flowId)
            .success(true)
            .errorMessage(Optional.empty())
            .details(Optional.ofNullable(details))
            .build();
    }

    /**
     * Creates a failed storage result.
     * @param flowId The ID of the flow that failed to be operated on
     * @param errorMessage The error message describing the failure
     * @return A failed FlowStorageResult
     */
    public static FlowStorageResult failure(final FlowId flowId, final String errorMessage) {
        return FlowStorageResult.builder()
            .flowId(flowId)
            .success(false)
            .errorMessage(Optional.ofNullable(errorMessage))
            .details(Optional.empty())
            .build();
    }

    /**
     * Creates a failed storage result with details.
     * @param flowId The ID of the flow that failed to be operated on
     * @param errorMessage The error message describing the failure
     * @param details Additional details about the failure
     * @return A failed FlowStorageResult with details
     */
    public static FlowStorageResult failure(
        final FlowId flowId,
        final String errorMessage,
        final String details
    ) {
        return FlowStorageResult.builder()
            .flowId(flowId)
            .success(false)
            .errorMessage(Optional.ofNullable(errorMessage))
            .details(Optional.ofNullable(details))
            .build();
    }

    /**
     * Checks if the operation was successful.
     * @return true if the operation succeeded
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Checks if the operation failed.
     * @return true if the operation failed
     */
    public boolean isFailure() {
        return !success;
    }

    /**
     * Gets the error message if the operation failed.
     * @return The error message, or empty string if successful
     */
    public String getErrorMessageOrEmpty() {
        return errorMessage.orElse("");
    }

    /**
     * Gets the details if present.
     * @return The details, or empty string if not present
     */
    public String getDetailsOrEmpty() {
        return details.orElse("");
    }
}