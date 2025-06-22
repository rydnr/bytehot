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
 * Filename: FlowCondition.java
 *
 * Author: Claude (Anthropic AI)
 *
 * Class name: FlowCondition
 *
 * Responsibilities:
 *   - Define conditions that must be met for flow detection
 *
 * Collaborators:
 *   - Flow: The flow that uses this condition
 *   - VersionedDomainEvent: Events evaluated against the condition
 */
package org.acmsl.bytehot.domain;

import org.acmsl.commons.patterns.eventsourcing.VersionedDomainEvent;
import org.acmsl.commons.patterns.dao.ValueObject;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.function.Predicate;

/**
 * Represents conditions that must be met for flow detection.
 * @author Claude (Anthropic AI)
 * @since 2025-06-19
 */
@RequiredArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public final class FlowCondition implements ValueObject {

    /**
     * Name of the condition for identification.
     */
    @Getter
    @NonNull
    private final String name;

    /**
     * Description of what this condition checks.
     */
    @Getter
    @NonNull
    private final String description;

    /**
     * Predicate function that evaluates the condition.
     */
    @Getter
    @NonNull
    private final Predicate<List<VersionedDomainEvent>> condition;

    /**
     * Creates a condition that checks for user-specific events.
     * @param userId The user ID to check for
     * @return A condition that ensures all events belong to the specified user
     */
    @NonNull
    public static FlowCondition sameUser(@NonNull final UserId userId) {
        return FlowCondition.builder()
            .name("Same User")
            .description("All events must belong to user: " + userId.getValue())
            .condition(events -> events.stream()
                .allMatch(event -> userId.equals(event.getUserId())))
            .build();
    }

    /**
     * Creates a condition that checks for events within a time window.
     * @param maxDurationMillis Maximum duration between first and last event
     * @return A condition that ensures events are within the time window
     */
    @NonNull
    public static FlowCondition withinTimeWindow(final long maxDurationMillis) {
        return FlowCondition.builder()
            .name("Time Window")
            .description("Events must occur within " + maxDurationMillis + "ms")
            .condition(events -> {
                if (events.size() < 2) {
                    return true;
                }
                
                long firstTimestamp = events.get(0).getTimestamp().toEpochMilli();
                long lastTimestamp = events.get(events.size() - 1).getTimestamp().toEpochMilli();
                
                return (lastTimestamp - firstTimestamp) <= maxDurationMillis;
            })
            .build();
    }

    /**
     * Creates a condition that checks for sequential order of events.
     * @return A condition that ensures events are in chronological order
     */
    @NonNull
    public static FlowCondition sequentialOrder() {
        return FlowCondition.builder()
            .name("Sequential Order")
            .description("Events must be in chronological order")
            .condition(events -> {
                for (int i = 1; i < events.size(); i++) {
                    if (events.get(i).getTimestamp().isBefore(events.get(i - 1).getTimestamp())) {
                        return false;
                    }
                }
                return true;
            })
            .build();
    }

    /**
     * Creates a composite condition that requires all provided conditions to be met.
     * @param conditions The conditions that must all be met
     * @return A condition that is satisfied only if all input conditions are satisfied
     */
    @NonNull
    public static FlowCondition allOf(@NonNull final FlowCondition... conditions) {
        return FlowCondition.builder()
            .name("All Of")
            .description("All conditions must be met")
            .condition(events -> {
                for (FlowCondition condition : conditions) {
                    if (!condition.condition.test(events)) {
                        return false;
                    }
                }
                return true;
            })
            .build();
    }

    /**
     * Evaluates this condition against a list of events.
     * @param events The events to evaluate
     * @return true if the condition is satisfied, false otherwise
     */
    public boolean evaluate(@Nullable final List<VersionedDomainEvent> events) {
        if (condition == null) {
            return true;
        }
        
        return condition.test(events);
    }
}