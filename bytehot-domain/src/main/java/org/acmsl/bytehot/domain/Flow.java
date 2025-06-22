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
 * Filename: Flow.java
 *
 * Author: Claude (Anthropic AI)
 *
 * Class name: Flow
 *
 * Responsibilities:
 *   - Represent discovered business flow with event sequence and metadata
 *
 * Collaborators:
 *   - FlowId: Unique identifier for the flow
 *   - DomainEvent: Events that form the flow sequence
 *   - FlowCondition: Optional conditions for flow detection
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
 * Immutable representation of a discovered business flow in the ByteHot system.
 * @author Claude (Anthropic AI)
 * @since 2025-06-19
 */
@RequiredArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public final class Flow implements ValueObject {

    /**
     * Unique identifier for the flow.
     */
    @Getter
    @NonNull
    private final FlowId flowId;

    /**
     * Human-readable name for the flow.
     */
    @Getter
    @NonNull
    private final String name;

    /**
     * Description of what this flow represents.
     */
    @Getter
    @NonNull
    private final String description;

    /**
     * Sequence of event types that form this flow.
     */
    @Getter
    @NonNull
    private final List<Class<? extends DomainEvent>> eventSequence;

    /**
     * Minimum number of events required to identify this flow.
     */
    @Getter
    private final int minimumEventCount;

    /**
     * Maximum time window for events to be considered part of the same flow.
     */
    @Getter
    @NonNull
    private final Duration maximumTimeWindow;

    /**
     * Confidence level for flow detection (0.0 to 1.0).
     */
    @Getter
    private final double confidence;

    /**
     * Optional conditions that must be met for flow detection.
     */
    @Getter
    @NonNull
    private final Optional<FlowCondition> conditions;

    /**
     * Validates the flow configuration.
     * @return true if the flow is valid, false otherwise
     */
    public boolean isValid() {
        return flowId != null
            && name != null && !name.trim().isEmpty()
            && description != null && !description.trim().isEmpty()
            && eventSequence != null && !eventSequence.isEmpty()
            && minimumEventCount > 0
            && minimumEventCount <= eventSequence.size()
            && maximumTimeWindow != null && !maximumTimeWindow.isNegative()
            && confidence >= 0.0 && confidence <= 1.0;
    }

    /**
     * Checks if this flow matches a given event sequence.
     * @param events The event sequence to check
     * @return true if the event sequence matches this flow pattern
     */
    public boolean matches(@Nullable final List<Class<? extends DomainEvent>> events) {
        if (events == null || events.size() < minimumEventCount) {
            return false;
        }

        // Check if the event sequence contains our pattern
        return containsPattern(events, eventSequence);
    }

    /**
     * Gets the event types that can start this flow.
     * @return List of event types that can initiate this flow
     */
    @NonNull
    public List<Class<? extends DomainEvent>> getStartingEventTypes() {
        return eventSequence.isEmpty() 
            ? List.of() 
            : List.of(eventSequence.get(0));
    }

    /**
     * Gets the event types that can end this flow.
     * @return List of event types that can complete this flow
     */
    @NonNull
    public List<Class<? extends DomainEvent>> getEndingEventTypes() {
        return eventSequence.isEmpty() 
            ? List.of() 
            : List.of(eventSequence.get(eventSequence.size() - 1));
    }

    /**
     * Creates a flow with updated confidence level.
     * @param newConfidence The new confidence level
     * @return A new Flow instance with updated confidence
     */
    @NonNull
    public Flow withConfidence(final double newConfidence) {
        if (newConfidence < 0.0 || newConfidence > 1.0) {
            throw new IllegalArgumentException("Confidence must be between 0.0 and 1.0");
        }
        
        return Flow.builder()
            .flowId(this.flowId)
            .name(this.name)
            .description(this.description)
            .eventSequence(this.eventSequence)
            .minimumEventCount(this.minimumEventCount)
            .maximumTimeWindow(this.maximumTimeWindow)
            .confidence(newConfidence)
            .conditions(this.conditions)
            .build();
    }

    private boolean containsPattern(
        @NonNull final List<Class<? extends DomainEvent>> events,
        @NonNull final List<Class<? extends DomainEvent>> pattern
    ) {
        if (pattern.isEmpty()) {
            return true;
        }
        
        for (int i = 0; i <= events.size() - pattern.size(); i++) {
            boolean matches = true;
            for (int j = 0; j < pattern.size(); j++) {
                if (!events.get(i + j).equals(pattern.get(j))) {
                    matches = false;
                    break;
                }
            }
            if (matches) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Checks if this flow matches a given event sequence using string event types.
     * @param eventTypeNames The event type names to check
     * @return true if the event sequence matches this flow pattern
     */
    public boolean matchesByName(@Nullable final List<String> eventTypeNames) {
        if (eventTypeNames == null || eventTypeNames.size() < minimumEventCount) {
            return false;
        }

        // Convert our event sequence to simple class names for comparison
        List<String> patternNames = eventSequence.stream()
            .map(Class::getSimpleName)
            .collect(java.util.stream.Collectors.toList());

        // Check if the event sequence contains our pattern
        return containsPatternByName(eventTypeNames, patternNames);
    }

    private boolean containsPatternByName(
        @NonNull final List<String> eventTypeNames,
        @NonNull final List<String> patternNames
    ) {
        if (patternNames.isEmpty()) {
            return true;
        }
        
        for (int i = 0; i <= eventTypeNames.size() - patternNames.size(); i++) {
            boolean matches = true;
            for (int j = 0; j < patternNames.size(); j++) {
                if (!eventTypeNames.get(i + j).equals(patternNames.get(j))) {
                    matches = false;
                    break;
                }
            }
            if (matches) {
                return true;
            }
        }
        
        return false;
    }
}