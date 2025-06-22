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
 * Filename: FlowSearchCriteria.java
 *
 * Author: Claude (Anthropic AI)
 *
 * Class name: FlowSearchCriteria
 *
 * Responsibilities:
 *   - Define search criteria for finding flows
 *
 * Collaborators:
 *   - FlowDetectionPort: Uses criteria for searching flows
 *   - Flow: Target type for search operations
 */
package org.acmsl.bytehot.domain;

import org.acmsl.commons.patterns.DomainEvent;
import org.acmsl.commons.patterns.dao.ValueObject;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

/**
 * Criteria for searching flows in the system.
 * @author Claude (Anthropic AI)
 * @since 2025-06-19
 */
@RequiredArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
@Getter
public final class FlowSearchCriteria implements ValueObject {

    /**
     * Optional name pattern to match (supports wildcards).
     */
    @Getter
    @NonNull
    private final Optional<String> namePattern;

    /**
     * Optional description pattern to match (supports wildcards).
     */
    @Getter
    @NonNull
    private final Optional<String> descriptionPattern;

    /**
     * Minimum confidence level for flows.
     */
    @Getter
    @NonNull
    private final Optional<Double> minimumConfidence;

    /**
     * Maximum confidence level for flows.
     */
    @Getter
    @NonNull
    private final Optional<Double> maximumConfidence;

    /**
     * Minimum number of events in flow sequence.
     */
    @Getter
    @NonNull
    private final Optional<Integer> minimumEventCount;

    /**
     * Maximum time window for flows.
     */
    @Getter
    @NonNull
    private final Optional<Duration> maximumTimeWindow;

    /**
     * Event types that must be present in the flow.
     */
    @Getter
    @NonNull
    private final List<Class<? extends DomainEvent>> requiredEventTypes;

    /**
     * Event types that must not be present in the flow.
     */
    @Getter
    @NonNull
    private final List<Class<? extends DomainEvent>> excludedEventTypes;

    /**
     * Creates criteria for finding flows by name pattern.
     * @param namePattern The name pattern (supports * wildcards)
     * @return Search criteria for name matching
     */
    @NonNull
    public static FlowSearchCriteria byNamePattern(@Nullable final String namePattern) {
        return FlowSearchCriteria.builder()
            .namePattern(Optional.ofNullable(namePattern))
            .descriptionPattern(Optional.empty())
            .minimumConfidence(Optional.empty())
            .maximumConfidence(Optional.empty())
            .minimumEventCount(Optional.empty())
            .maximumTimeWindow(Optional.empty())
            .requiredEventTypes(List.of())
            .excludedEventTypes(List.of())
            .build();
    }

    /**
     * Creates criteria for finding flows by confidence range.
     * @param minimumConfidence The minimum confidence level
     * @param maximumConfidence The maximum confidence level
     * @return Search criteria for confidence matching
     */
    @NonNull
    public static FlowSearchCriteria byConfidenceRange(
        final double minimumConfidence,
        final double maximumConfidence
    ) {
        return FlowSearchCriteria.builder()
            .namePattern(Optional.empty())
            .descriptionPattern(Optional.empty())
            .minimumConfidence(Optional.of(minimumConfidence))
            .maximumConfidence(Optional.of(maximumConfidence))
            .minimumEventCount(Optional.empty())
            .maximumTimeWindow(Optional.empty())
            .requiredEventTypes(List.of())
            .excludedEventTypes(List.of())
            .build();
    }

    /**
     * Creates criteria for finding flows containing specific event types.
     * @param eventTypes The event types that must be present
     * @return Search criteria for event type matching
     */
    @SafeVarargs
    @SuppressWarnings("varargs")
    @NonNull
    public static FlowSearchCriteria containingEventTypes(
        @NonNull final Class<? extends DomainEvent>... eventTypes
    ) {
        return FlowSearchCriteria.builder()
            .namePattern(Optional.empty())
            .descriptionPattern(Optional.empty())
            .minimumConfidence(Optional.empty())
            .maximumConfidence(Optional.empty())
            .minimumEventCount(Optional.empty())
            .maximumTimeWindow(Optional.empty())
            .requiredEventTypes(List.of(eventTypes))
            .excludedEventTypes(List.of())
            .build();
    }

    /**
     * Creates criteria that matches all flows (no filtering).
     * @return Search criteria that matches everything
     */
    @NonNull
    public static FlowSearchCriteria all() {
        return FlowSearchCriteria.builder()
            .namePattern(Optional.empty())
            .descriptionPattern(Optional.empty())
            .minimumConfidence(Optional.empty())
            .maximumConfidence(Optional.empty())
            .minimumEventCount(Optional.empty())
            .maximumTimeWindow(Optional.empty())
            .requiredEventTypes(List.of())
            .excludedEventTypes(List.of())
            .build();
    }

    /**
     * Checks if a flow matches these criteria.
     * @param flow The flow to test
     * @return true if the flow matches all criteria
     */
    public boolean matches(@Nullable final Flow flow) {
        if (flow == null) {
            return false;
        }

        // Check name pattern
        if (namePattern.isPresent() && !matchesPattern(flow.getName(), namePattern.get())) {
            return false;
        }

        // Check description pattern
        if (descriptionPattern.isPresent() && !matchesPattern(flow.getDescription(), descriptionPattern.get())) {
            return false;
        }

        // Check confidence range
        if (minimumConfidence.isPresent() && flow.getConfidence() < minimumConfidence.get()) {
            return false;
        }

        if (maximumConfidence.isPresent() && flow.getConfidence() > maximumConfidence.get()) {
            return false;
        }

        // Check minimum event count
        if (minimumEventCount.isPresent() && flow.getEventSequence().size() < minimumEventCount.get()) {
            return false;
        }

        // Check maximum time window
        if (maximumTimeWindow.isPresent() && flow.getMaximumTimeWindow().compareTo(maximumTimeWindow.get()) > 0) {
            return false;
        }

        // Check required event types
        for (Class<? extends DomainEvent> requiredType : requiredEventTypes) {
            if (!flow.getEventSequence().contains(requiredType)) {
                return false;
            }
        }

        // Check excluded event types
        for (Class<? extends DomainEvent> excludedType : excludedEventTypes) {
            if (flow.getEventSequence().contains(excludedType)) {
                return false;
            }
        }

        return true;
    }

    private boolean matchesPattern(@Nullable final String text, @Nullable final String pattern) {
        if (text == null || pattern == null) {
            return false;
        }

        // Simple wildcard matching (* matches any sequence)
        String regexPattern = pattern
            .replace("*", ".*")
            .toLowerCase();

        return text.toLowerCase().matches(regexPattern);
    }
}