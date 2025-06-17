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
 * Filename: VersionedDomainEvent.java
 *
 * Author: Claude Code
 *
 * Class name: VersionedDomainEvent
 *
 * Responsibilities:
 *   - Define interface for domain events with EventSourcing metadata
 *   - Provide aggregate association and versioning information
 *   - Enable event causality tracking and audit trails
 *
 * Collaborators:
 *   - DomainEvent: Base interface for all domain events
 *   - EventStorePort: Uses this interface for event persistence
 */
package org.acmsl.bytehot.domain;

import org.acmsl.commons.patterns.DomainEvent;

import java.time.Instant;

/**
 * Interface for domain events with EventSourcing metadata
 * @author Claude Code
 * @since 2025-06-17
 */
public interface VersionedDomainEvent extends DomainEvent {

    /**
     * Unique identifier for this event instance
     * @return the event ID (typically a UUID)
     */
    String getEventId();

    /**
     * Type of aggregate this event belongs to
     * @return the aggregate type (e.g., "user", "hotswap", "bytehot")
     */
    String getAggregateType();

    /**
     * Unique identifier of the aggregate instance
     * @return the aggregate ID (typically a UUID or meaningful identifier)
     */
    String getAggregateId();

    /**
     * Version of this event for the aggregate (1, 2, 3, ...)
     * @return the aggregate version after applying this event
     */
    long getAggregateVersion();

    /**
     * Timestamp when the event occurred
     * @return the event timestamp
     */
    Instant getTimestamp();

    /**
     * ID of the previous event in this aggregate's history (for causality)
     * @return the previous event ID, or null if this is the first event
     */
    String getPreviousEventId();

    /**
     * Version of the event schema for migration purposes
     * @return the schema version (starts at 1)
     */
    int getSchemaVersion();

    /**
     * User who triggered this event (if applicable)
     * @return the user ID, or null if not user-triggered
     */
    String getUserId();

    /**
     * Correlation ID for tracing related events across aggregates
     * @return the correlation ID, or null if not part of a correlation
     */
    String getCorrelationId();

    /**
     * Gets the simple class name of this event for type identification
     * @return the event type name
     */
    default String getEventType() {
        return this.getClass().getSimpleName();
    }

    /**
     * Checks if this event is the first event for its aggregate
     * @return true if this is the first event (version 1, no previous event)
     */
    default boolean isFirstEvent() {
        return getAggregateVersion() == 1L && getPreviousEventId() == null;
    }

    /**
     * Checks if this event has a causality chain (has a previous event)
     * @return true if this event follows another event
     */
    default boolean hasCausality() {
        return getPreviousEventId() != null;
    }

    /**
     * Checks if this event is associated with a user
     * @return true if this event has a user ID
     */
    default boolean hasUser() {
        return getUserId() != null;
    }

    /**
     * Checks if this event is part of a correlation
     * @return true if this event has a correlation ID
     */
    default boolean isCorrelated() {
        return getCorrelationId() != null;
    }
}