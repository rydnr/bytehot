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
 * Filename: EventSnapshot.java
 *
 * Author: Claude Code
 *
 * Class name: EventSnapshot
 *
 * Responsibilities:
 *   - Capture complete event history leading to an error condition
 *   - Preserve environmental context for bug reproduction
 *   - Provide serializable format for bug reports and test generation
 *   - Support causal chain analysis and debugging insights
 *
 * Collaborators:
 *   - VersionedDomainEvent: Events captured in the snapshot
 *   - UserId: User context at time of error
 *   - ErrorContext: Environmental and system context
 */
package org.acmsl.bytehot.domain;

import org.acmsl.bytehot.domain.UserId;
import org.acmsl.commons.patterns.eventsourcing.VersionedDomainEvent;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Immutable snapshot of complete event context at the time of an error.
 * Contains all information necessary to reproduce the exact conditions that led to a bug.
 * @author Claude Code
 * @since 2025-06-19
 */
@RequiredArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
@Getter
public class EventSnapshot {

    /**
     * Unique identifier for this snapshot
     */
    @NonNull
    private final String snapshotId;

    /**
     * When this snapshot was captured
     */
    @NonNull
    private final Instant capturedAt;

    /**
     * Ordered sequence of events leading to the error
     */
    @NonNull
    private final List<VersionedDomainEvent> eventHistory;

    /**
     * User context at the time of error
     */
    @Nullable
    private final UserId userId;

    /**
     * System and environmental context
     */
    @NonNull
    private final Map<String, String> environmentContext;

    /**
     * Thread information where error occurred
     */
    @NonNull
    private final String threadName;

    /**
     * JVM and system properties at error time
     */
    @NonNull
    private final Map<String, String> systemProperties;

    /**
     * Causal chain analysis results
     */
    @Nullable
    private final CausalChain causalChain;

    /**
     * Performance metrics at error time
     */
    @NonNull
    private final Map<String, Object> performanceMetrics;

    /**
     * Creates a new snapshot with generated ID and current timestamp
     * @param eventHistory the event sequence leading to error
     * @param userId user context (can be null)
     * @param environmentContext environmental information
     * @param threadName thread where error occurred
     * @param systemProperties JVM properties snapshot
     * @param causalChain causal analysis (can be null)
     * @param performanceMetrics performance data
     * @return new EventSnapshot instance
     */
    @NonNull
    public static EventSnapshot create(
            @NonNull final List<VersionedDomainEvent> eventHistory,
            @Nullable final UserId userId,
            @NonNull final Map<String, String> environmentContext,
            @NonNull final String threadName,
            @NonNull final Map<String, String> systemProperties,
            @Nullable final CausalChain causalChain,
            @NonNull final Map<String, Object> performanceMetrics) {
        return EventSnapshot.builder()
            .snapshotId(UUID.randomUUID().toString())
            .capturedAt(Instant.now())
            .eventHistory(eventHistory)
            .userId(userId)
            .environmentContext(environmentContext)
            .threadName(threadName)
            .systemProperties(systemProperties)
            .causalChain(causalChain)
            .performanceMetrics(performanceMetrics)
            .build();
    }

    /**
     * Gets the total number of events in the snapshot
     * @return event count
     */
    public int getEventCount() {
        return eventHistory.size();
    }

    /**
     * Gets events within a specific time window
     * @param from start time (inclusive)
     * @param to end time (inclusive)
     * @return filtered event list
     */
    @NonNull
    public List<VersionedDomainEvent> getEventsInTimeWindow(@NonNull final Instant from, @NonNull final Instant to) {
        return eventHistory.stream()
            .filter(event -> !event.getTimestamp().isBefore(from) && !event.getTimestamp().isAfter(to))
            .toList();
    }

    /**
     * Gets events of a specific type
     * @param eventType the event class to filter by
     * @param <T> the event type
     * @return filtered events of the specified type
     */
    @NonNull
    public <T extends VersionedDomainEvent> List<T> getEventsOfType(@NonNull final Class<T> eventType) {
        return eventHistory.stream()
            .filter(eventType::isInstance)
            .map(eventType::cast)
            .toList();
    }

    /**
     * Gets the most recent event in the snapshot
     * @return the last event, or null if no events
     */
    @Nullable
    public VersionedDomainEvent getLastEvent() {
        return eventHistory.isEmpty() ? null : eventHistory.get(eventHistory.size() - 1);
    }

    /**
     * Gets the first event in the snapshot
     * @return the first event, or null if no events
     */
    @Nullable
    public VersionedDomainEvent getFirstEvent() {
        return eventHistory.isEmpty() ? null : eventHistory.get(0);
    }

    /**
     * Checks if the snapshot contains events of a specific type
     * @param eventType the event class to check for
     * @return true if events of this type are present
     */
    public boolean containsEventType(@NonNull final Class<? extends VersionedDomainEvent> eventType) {
        return eventHistory.stream().anyMatch(eventType::isInstance);
    }

    /**
     * Gets a summary description of the snapshot for debugging
     * @return human-readable snapshot summary
     */
    @NonNull
    public String getSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("EventSnapshot[")
            .append("id=").append(snapshotId.substring(0, 8)).append("...")
            .append(", events=").append(eventHistory.size())
            .append(", user=").append(userId != null ? userId.getDisplayName() : "anonymous")
            .append(", thread=").append(threadName)
            .append(", captured=").append(capturedAt)
            .append("]");
        return summary.toString();
    }

    /**
     * Gets the time span covered by the events in this snapshot
     * @return duration from first to last event, or zero if no events
     */
    public java.time.@NonNull Duration getTimeSpan() {
        if (eventHistory.isEmpty()) {
            return java.time.Duration.ZERO;
        }
        VersionedDomainEvent first = getFirstEvent();
        VersionedDomainEvent last = getLastEvent();
        return java.time.Duration.between(first.getTimestamp(), last.getTimestamp());
    }

    /**
     * Creates a reduced snapshot containing only the most recent events
     * @param maxEvents maximum number of events to include
     * @return new snapshot with limited event history
     */
    @NonNull
    public EventSnapshot limitToRecentEvents(final int maxEvents) {
        if (maxEvents >= eventHistory.size()) {
            return this;
        }
        
        List<VersionedDomainEvent> limitedHistory = eventHistory.subList(
            Math.max(0, eventHistory.size() - maxEvents), 
            eventHistory.size()
        );
        
        return this.toBuilder()
            .eventHistory(limitedHistory)
            .build();
    }
}