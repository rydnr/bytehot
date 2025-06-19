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
 * Filename: FlowDetectionPort.java
 *
 * Author: Claude (Anthropic AI)
 *
 * Class name: FlowDetectionPort
 *
 * Responsibilities:
 *   - Define interface for flow detection and persistence operations
 *
 * Collaborators:
 *   - Flow: Domain flows to be stored and retrieved
 *   - VersionedDomainEvent: Events to be analyzed for flow patterns
 *   - FlowSearchCriteria: Criteria for searching stored flows
 */
package org.acmsl.bytehot.infrastructure;

import org.acmsl.bytehot.domain.Flow;
import org.acmsl.bytehot.domain.FlowId;
import org.acmsl.bytehot.domain.FlowSearchCriteria;
import org.acmsl.bytehot.domain.FlowStatistics;
import org.acmsl.bytehot.domain.FlowStorageResult;
import org.acmsl.bytehot.domain.VersionedDomainEvent;
import org.acmsl.commons.patterns.Port;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Port for flow detection and persistence operations.
 * @author Claude (Anthropic AI)
 * @since 2025-06-19
 */
public interface FlowDetectionPort extends Port {

    /**
     * Analyzes a sequence of events to detect flows.
     * @param events The events to analyze
     * @return Detected flows with confidence levels
     */
    @NonNull
    CompletableFuture<List<Flow>> detectFlows(@NonNull List<VersionedDomainEvent> events);

    /**
     * Stores a discovered flow for future reference.
     * @param flow The flow to store
     * @return Success or failure result with details
     */
    @NonNull
    CompletableFuture<FlowStorageResult> storeFlow(@NonNull Flow flow);

    /**
     * Retrieves all known flows.
     * @return All stored flows
     */
    @NonNull
    CompletableFuture<List<Flow>> getAllFlows();

    /**
     * Searches for flows matching specific criteria.
     * @param criteria The search criteria
     * @return Matching flows
     */
    @NonNull
    CompletableFuture<List<Flow>> searchFlows(@NonNull FlowSearchCriteria criteria);

    /**
     * Retrieves flows by their confidence level.
     * @param minimumConfidence The minimum confidence level
     * @return Flows with confidence above the threshold
     */
    @NonNull
    CompletableFuture<List<Flow>> getFlowsByConfidence(double minimumConfidence);

    /**
     * Deletes a flow from storage.
     * @param flowId The ID of the flow to delete
     * @return Success or failure result
     */
    @NonNull
    CompletableFuture<FlowStorageResult> deleteFlow(@NonNull FlowId flowId);

    /**
     * Updates an existing flow with new information.
     * @param flow The updated flow
     * @return Success or failure result
     */
    @NonNull
    CompletableFuture<FlowStorageResult> updateFlow(@NonNull Flow flow);

    /**
     * Gets statistics about stored flows.
     * @return Flow statistics including counts and confidence distributions
     */
    @NonNull
    CompletableFuture<FlowStatistics> getFlowStatistics();
}