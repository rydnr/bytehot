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
 * Filename: AbstractVersionedDomainEvent.java
 *
 * Author: Claude Code
 *
 * Class name: AbstractVersionedDomainEvent
 *
 * Responsibilities:
 *   - Provide base implementation for all versioned domain events
 *   - Handle EventSourcing metadata consistently
 *   - Enable easy creation of new versioned events
 *
 * Collaborators:
 *   - VersionedDomainEvent: Interface this class implements
 *   - EventMetadata: Contains the event metadata
 */
package org.acmsl.bytehot.domain.events;

import org.acmsl.bytehot.domain.EventMetadata;
import org.acmsl.bytehot.domain.VersionedDomainEvent;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.Instant;

/**
 * Abstract base class for all versioned domain events
 * @author Claude Code
 * @since 2025-06-17
 */
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public abstract class AbstractVersionedDomainEvent implements VersionedDomainEvent {

    /**
     * Unique identifier for this event instance
     */
    @Getter
    private final String eventId;

    /**
     * Type of aggregate this event belongs to
     */
    @Getter
    private final String aggregateType;

    /**
     * Unique identifier of the aggregate instance
     */
    @Getter
    private final String aggregateId;

    /**
     * Version of this event for the aggregate
     */
    @Getter
    private final long aggregateVersion;

    /**
     * Timestamp when the event occurred
     */
    @Getter
    private final Instant timestamp;

    /**
     * ID of the previous event in this aggregate's history
     */
    @Getter
    private final String previousEventId;

    /**
     * Version of the event schema for migration purposes
     */
    @Getter
    private final int schemaVersion;

    /**
     * User who triggered this event
     */
    @Getter
    private final String userId;

    /**
     * Correlation ID for tracing related events
     */
    @Getter
    private final String correlationId;

    /**
     * Constructor that takes EventMetadata
     * @param metadata the event metadata
     */
    protected AbstractVersionedDomainEvent(EventMetadata metadata) {
        this(
            metadata.getEventId(),
            metadata.getAggregateType(),
            metadata.getAggregateId(),
            metadata.getAggregateVersion(),
            metadata.getTimestamp(),
            metadata.getPreviousEventId(),
            metadata.getSchemaVersion(),
            metadata.getUserId(),
            metadata.getCorrelationId()
        );
    }

    /**
     * Factory method to create event metadata for new aggregates
     * @param aggregateType the type of aggregate
     * @param aggregateId the unique identifier of the aggregate
     * @return metadata for the first event
     */
    protected static EventMetadata createMetadataForNewAggregate(
        String aggregateType,
        String aggregateId
    ) {
        return EventMetadata.forNewAggregate(aggregateType, aggregateId);
    }

    /**
     * Factory method to create event metadata for existing aggregates
     * @param aggregateType the type of aggregate
     * @param aggregateId the unique identifier of the aggregate
     * @param previousEventId the ID of the previous event
     * @param currentVersion the current version of the aggregate
     * @return metadata for the next event
     */
    protected static EventMetadata createMetadataForExistingAggregate(
        String aggregateType,
        String aggregateId,
        String previousEventId,
        long currentVersion
    ) {
        return EventMetadata.forExistingAggregate(
            aggregateType,
            aggregateId,
            previousEventId,
            currentVersion
        );
    }

    /**
     * Factory method to create event metadata with user context
     * @param aggregateType the type of aggregate
     * @param aggregateId the unique identifier of the aggregate
     * @param previousEventId the ID of the previous event (null for first event)
     * @param currentVersion the current version of the aggregate (0 for first event)
     * @param userId the user who triggered this event
     * @return metadata with user context
     */
    protected static EventMetadata createMetadataWithUser(
        String aggregateType,
        String aggregateId,
        String previousEventId,
        long currentVersion,
        String userId
    ) {
        return EventMetadata.withUser(
            aggregateType,
            aggregateId,
            previousEventId,
            currentVersion,
            userId
        );
    }

    /**
     * Factory method to create event metadata with correlation
     * @param aggregateType the type of aggregate
     * @param aggregateId the unique identifier of the aggregate
     * @param previousEventId the ID of the previous event (null for first event)
     * @param currentVersion the current version of the aggregate (0 for first event)
     * @param userId the user who triggered this event
     * @param correlationId the correlation ID for tracing
     * @return metadata with correlation
     */
    protected static EventMetadata createMetadataWithCorrelation(
        String aggregateType,
        String aggregateId,
        String previousEventId,
        long currentVersion,
        String userId,
        String correlationId
    ) {
        return EventMetadata.withCorrelation(
            aggregateType,
            aggregateId,
            previousEventId,
            currentVersion,
            userId,
            correlationId
        );
    }

    /**
     * Helper method to get the last event ID for an aggregate
     * This would typically query the EventStore, but for now returns null
     * @param aggregateType the type of aggregate
     * @param aggregateId the unique identifier of the aggregate
     * @return the last event ID, or null if no events exist
     */
    protected static String getLastEventId(String aggregateType, String aggregateId) {
        // TODO: This should query the EventStore to get the last event ID
        // For now, we return null which means this will be treated as the first event
        // This will be implemented when we integrate with the EventStore
        return null;
    }

    /**
     * Helper method to get the current version for an aggregate
     * This would typically query the EventStore, but for now returns 0
     * @param aggregateType the type of aggregate
     * @param aggregateId the unique identifier of the aggregate
     * @return the current version, or 0 if no events exist
     */
    protected static long getCurrentVersion(String aggregateType, String aggregateId) {
        // TODO: This should query the EventStore to get the current version
        // For now, we return 0 which means the next event will be version 1
        // This will be implemented when we integrate with the EventStore
        return 0L;
    }

    /**
     * Creates a copy of this event with updated version information
     * @param newVersion the new aggregate version
     * @return a new event with updated version
     */
    public AbstractVersionedDomainEvent withVersion(long newVersion) {
        // This is abstract because each concrete event class needs to implement
        // its own copy constructor with the new version
        throw new UnsupportedOperationException(
            "Subclasses must implement withVersion method"
        );
    }

    /**
     * Gets a human-readable description of this event
     * @return a description of the event
     */
    public String getDescription() {
        return String.format(
            "%s[aggregateType=%s, aggregateId=%s, version=%d]",
            getEventType(),
            aggregateType,
            aggregateId,
            aggregateVersion
        );
    }
}