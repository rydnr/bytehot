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
 * Filename: VersionedDomainEvent.java
 *
 * Author: Claude (Anthropic AI)
 *
 * Class name: VersionedDomainEvent
 *
 * Responsibilities:
 *   - Define interface for domain events with EventSourcing metadata
 *   - Provide aggregate association and versioning information
 *   - Enable event causality tracking and audit trails
 *   - Support event sourcing patterns across any domain
 *
 * Collaborators:
 *   - DomainEvent: Base interface for all domain events
 *   - EventStore: Uses this interface for event persistence
 *   - EventMetadata: Metadata associated with the event
 */
package org.acmsl.commons.patterns.eventsourcing;

import org.acmsl.commons.patterns.DomainEvent;

import java.time.Duration;
import java.time.Instant;

/**
 * Interface for domain events with EventSourcing metadata.
 * Provides comprehensive support for event sourcing patterns including versioning,
 * causality tracking, and aggregate association across any domain.
 * @author Claude (Anthropic AI)
 * @since 2025-06-19
 */
public interface VersionedDomainEvent extends DomainEvent {

    /**
     * Unique identifier for this event instance.
     * @return the event ID (typically a UUID)
     */
    
    String getEventId();

    /**
     * Type of aggregate this event belongs to.
     * @return the aggregate type (e.g., "user", "order", "account")
     */
    
    String getAggregateType();

    /**
     * Unique identifier of the aggregate instance.
     * @return the aggregate ID (typically a UUID or meaningful identifier)
     */
    
    String getAggregateId();

    /**
     * Version of this event for the aggregate (1, 2, 3, ...).
     * Used for optimistic concurrency control and event ordering.
     * @return the aggregate version after applying this event
     */
    long getAggregateVersion();

    /**
     * Timestamp when the event occurred.
     * @return the event timestamp
     */
    
    Instant getTimestamp();

    /**
     * ID of the previous event in this aggregate's history (for causality).
     * Enables building causal chains and event ordering verification.
     * @return the previous event ID, or null if this is the first event
     */
    
    String getPreviousEventId();

    /**
     * Version of the event schema for migration purposes.
     * Enables handling schema evolution in event sourced systems.
     * @return the schema version (starts at 1)
     */
    int getSchemaVersion();

    /**
     * User who triggered this event (if applicable).
     * Supports audit trail and user-context tracking.
     * @return the user ID, or null if not user-triggered or system event
     */
    
    String getUserId();

    /**
     * Correlation ID for tracing related events across aggregates.
     * Enables distributed tracing and workflow correlation.
     * @return the correlation ID, or null if not part of a correlation
     */
    
    String getCorrelationId();

    /**
     * Causation ID linking this event to the command that triggered it.
     * Enables command-event traceability and debugging.
     * @return the causation ID, or null if not applicable
     */
    
    default String getCausationId() {
        return null;
    }

    /**
     * Stream position in the event store for this event.
     * Used for efficient event retrieval and ordering.
     * @return the stream position, or null if not yet persisted
     */
    
    default Long getStreamPosition() {
        return null;
    }

    /**
     * Gets the simple class name of this event for type identification.
     * @return the event type name
     */
    
    default String getEventType() {
        return this.getClass().getSimpleName();
    }

    /**
     * Gets the full class name of this event for precise type identification.
     * @return the fully qualified event type name
     */
    
    default String getFullEventType() {
        return this.getClass().getName();
    }

    /**
     * Checks if this event is the first event for its aggregate.
     * @return true if this is the first event (version 1, no previous event)
     */
    default boolean isFirstEvent() {
        return getAggregateVersion() == 1L && getPreviousEventId() == null;
    }

    /**
     * Checks if this event has a causality chain (has a previous event).
     * @return true if this event follows another event
     */
    default boolean hasCausality() {
        return getPreviousEventId() != null;
    }

    /**
     * Checks if this event is associated with a user.
     * @return true if this event has a user ID
     */
    default boolean hasUser() {
        return getUserId() != null && !getUserId().trim().isEmpty();
    }

    /**
     * Checks if this event is part of a correlation.
     * @return true if this event has a correlation ID
     */
    default boolean isCorrelated() {
        return getCorrelationId() != null && !getCorrelationId().trim().isEmpty();
    }

    /**
     * Checks if this event has causation tracking.
     * @return true if this event has a causation ID
     */
    default boolean hasCausation() {
        return getCausationId() != null && !getCausationId().trim().isEmpty();
    }

    /**
     * Checks if this is a system-generated event (no user association).
     * @return true if this event was generated by the system
     */
    default boolean isSystemEvent() {
        return !hasUser();
    }

    /**
     * Gets the age of this event from the current time.
     * @return duration since the event occurred
     */
    
    default Duration getAge() {
        return Duration.between(getTimestamp(), Instant.now());
    }

    /**
     * Checks if this event occurred before the specified instant.
     * @param instant the instant to compare against
     * @return true if this event occurred before the specified instant
     */
    default boolean occurredBefore(final Instant instant) {
        return getTimestamp().isBefore(instant);
    }

    /**
     * Checks if this event occurred after the specified instant.
     * @param instant the instant to compare against
     * @return true if this event occurred after the specified instant
     */
    default boolean occurredAfter(final Instant instant) {
        return getTimestamp().isAfter(instant);
    }

    /**
     * Checks if this event belongs to the same aggregate as another event.
     * @param other the other event to compare
     * @return true if both events belong to the same aggregate instance
     */
    default boolean belongsToSameAggregate(final VersionedDomainEvent other) {
        return getAggregateType().equals(other.getAggregateType()) &&
               getAggregateId().equals(other.getAggregateId());
    }
}