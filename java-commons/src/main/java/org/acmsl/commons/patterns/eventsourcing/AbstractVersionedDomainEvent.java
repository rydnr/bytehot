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
 * Filename: AbstractVersionedDomainEvent.java
 *
 * Author: Claude (Anthropic AI)
 *
 * Class name: AbstractVersionedDomainEvent
 *
 * Responsibilities:
 *   - Provide base implementation for all versioned domain events
 *   - Handle EventSourcing metadata consistently across any domain
 *   - Enable easy creation of new versioned events
 *   - Support event sourcing patterns and best practices
 *
 * Collaborators:
 *   - VersionedDomainEvent: Interface this class implements
 *   - EventMetadata: Contains the event metadata
 */
package org.acmsl.commons.patterns.eventsourcing;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.Instant;

/**
 * Abstract base class for all versioned domain events.
 * Provides consistent EventSourcing metadata handling across any domain.
 * @author Claude (Anthropic AI)
 * @since 2025-06-19
 */
@EqualsAndHashCode
@ToString
public abstract class AbstractVersionedDomainEvent implements VersionedDomainEvent {

    /**
     * Unique identifier for this event instance.
     */
    @Getter
    
    private final String eventId;

    /**
     * Type of aggregate this event belongs to.
     */
    @Getter
    
    private final String aggregateType;

    /**
     * Unique identifier of the aggregate instance.
     */
    @Getter
    
    private final String aggregateId;

    /**
     * Version of this event for the aggregate.
     */
    @Getter
    private final long aggregateVersion;

    /**
     * Timestamp when the event occurred.
     */
    @Getter
    
    private final Instant timestamp;

    /**
     * ID of the previous event in this aggregate's history.
     */
    @Getter
    
    private final String previousEventId;

    /**
     * Version of the event schema for migration purposes.
     */
    @Getter
    private final int schemaVersion;

    /**
     * User who triggered this event.
     */
    @Getter
    
    private final String userId;

    /**
     * Correlation ID for tracing related events.
     */
    @Getter
    
    private final String correlationId;

    /**
     * Causation ID linking this event to the command that triggered it.
     */
    @Getter
    
    private final String causationId;

    /**
     * Stream position in the event store for this event.
     */
    @Getter
    
    private final Long streamPosition;

    /**
     * Constructor that takes EventMetadata for convenient event creation.
     * @param metadata the event metadata containing all necessary information
     */
    protected AbstractVersionedDomainEvent(final EventMetadata metadata) {
        this(
            metadata.getEventId(),
            metadata.getAggregateType(),
            metadata.getAggregateId(),
            metadata.getAggregateVersion(),
            metadata.getTimestamp(),
            metadata.getPreviousEventId(),
            metadata.getSchemaVersion(),
            metadata.getUserId(),
            metadata.getCorrelationId(),
            metadata.getCausationId(),
            metadata.getStreamPosition()
        );
    }

    /**
     * Full constructor with all metadata fields.
     * @param eventId unique identifier for this event instance
     * @param aggregateType type of aggregate this event belongs to
     * @param aggregateId unique identifier of the aggregate instance
     * @param aggregateVersion version of this event for the aggregate
     * @param timestamp timestamp when the event occurred
     * @param previousEventId ID of the previous event in this aggregate's history
     * @param schemaVersion version of the event schema for migration purposes
     * @param userId user who triggered this event
     * @param correlationId correlation ID for tracing related events
     * @param causationId causation ID linking this event to the command that triggered it
     * @param streamPosition stream position in the event store for this event
     */
    protected AbstractVersionedDomainEvent(
        final String eventId,
        final String aggregateType,
        final String aggregateId,
        final long aggregateVersion,
        final Instant timestamp,
        final String previousEventId,
        final int schemaVersion,
        final String userId,
        final String correlationId,
        final String causationId,
        final Long streamPosition
    ) {
        this.eventId = eventId;
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.aggregateVersion = aggregateVersion;
        this.timestamp = timestamp;
        this.previousEventId = previousEventId;
        this.schemaVersion = schemaVersion;
        this.userId = userId;
        this.correlationId = correlationId;
        this.causationId = causationId;
        this.streamPosition = streamPosition;
    }

    /**
     * Factory method to create event metadata for new aggregates.
     * @param aggregateType the type of aggregate
     * @param aggregateId the unique identifier of the aggregate
     * @return metadata for the first event
     */
    
    protected static EventMetadata createMetadataForNewAggregate(
        final String aggregateType,
        final String aggregateId
    ) {
        return EventMetadata.forNewAggregate(aggregateType, aggregateId);
    }

    /**
     * Factory method to create event metadata for existing aggregates.
     * @param aggregateType the type of aggregate
     * @param aggregateId the unique identifier of the aggregate
     * @param previousEventId the ID of the previous event
     * @param currentVersion the current version of the aggregate
     * @return metadata for the next event
     */
    
    protected static EventMetadata createMetadataForExistingAggregate(
        final String aggregateType,
        final String aggregateId,
        final String previousEventId,
        final long currentVersion
    ) {
        return EventMetadata.forExistingAggregate(
            aggregateType,
            aggregateId,
            previousEventId,
            currentVersion
        );
    }

    /**
     * Factory method to create event metadata with user context.
     * @param aggregateType the type of aggregate
     * @param aggregateId the unique identifier of the aggregate
     * @param previousEventId the ID of the previous event (null for first event)
     * @param currentVersion the current version of the aggregate (0 for first event)
     * @param userId the user who triggered this event
     * @return metadata with user context
     */
    
    protected static EventMetadata createMetadataWithUser(
        final String aggregateType,
        final String aggregateId,
        final String previousEventId,
        final long currentVersion,
        final String userId
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
     * Factory method to create event metadata with full context.
     * @param aggregateType the type of aggregate
     * @param aggregateId the unique identifier of the aggregate
     * @param previousEventId the ID of the previous event (null for first event)
     * @param currentVersion the current version of the aggregate (0 for first event)
     * @param userId the user who triggered this event
     * @param correlationId the correlation ID for tracing
     * @param causationId the causation ID for command tracing
     * @return metadata with full context
     */
    
    protected static EventMetadata createMetadataWithFullContext(
        final String aggregateType,
        final String aggregateId,
        final String previousEventId,
        final long currentVersion,
        final String userId,
        final String correlationId,
        final String causationId
    ) {
        return EventMetadata.withFullContext(
            aggregateType,
            aggregateId,
            previousEventId,
            currentVersion,
            userId,
            correlationId,
            causationId
        );
    }

    /**
     * Gets the current metadata for this event as an EventMetadata object.
     * @return the event metadata
     */
    
    public EventMetadata getMetadata() {
        return new EventMetadata(
            eventId,
            aggregateType,
            aggregateId,
            aggregateVersion,
            timestamp,
            previousEventId,
            schemaVersion,
            userId,
            correlationId,
            causationId,
            streamPosition
        );
    }

    /**
     * Gets a human-readable description of this event.
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

    /**
     * Gets a detailed string representation of this event including all metadata.
     * @return detailed string representation
     */
    
    public String toDetailedString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(getEventType()).append(" {");
        sb.append("\n  eventId=").append(eventId);
        sb.append("\n  aggregateType=").append(aggregateType);
        sb.append("\n  aggregateId=").append(aggregateId);
        sb.append("\n  aggregateVersion=").append(aggregateVersion);
        sb.append("\n  timestamp=").append(timestamp);
        sb.append("\n  previousEventId=").append(previousEventId);
        sb.append("\n  schemaVersion=").append(schemaVersion);
        sb.append("\n  userId=").append(userId);
        sb.append("\n  correlationId=").append(correlationId);
        sb.append("\n  causationId=").append(causationId);
        sb.append("\n  streamPosition=").append(streamPosition);
        sb.append("\n}");
        return sb.toString();
    }

    /**
     * Checks if this event can be directly applied after another event.
     * This checks if the versions are consecutive and belong to the same aggregate.
     * @param previousEvent the previous event to check against
     * @return true if this event can follow the previous event
     */
    public boolean canFollowDirectly(final VersionedDomainEvent previousEvent) {
        return belongsToSameAggregate(previousEvent) &&
               getAggregateVersion() == previousEvent.getAggregateVersion() + 1 &&
               previousEvent.getEventId().equals(getPreviousEventId());
    }

    /**
     * Creates an event metadata object that would be appropriate for the next event
     * in this aggregate's event stream.
     * @return metadata for the next event
     */
    
    public EventMetadata createNextEventMetadata() {
        return EventMetadata.forExistingAggregate(
            aggregateType,
            aggregateId,
            eventId,
            aggregateVersion
        );
    }

    /**
     * Creates an event metadata object for the next event with user context.
     * @param userId the user who will trigger the next event
     * @return metadata for the next event with user context
     */
    
    public EventMetadata createNextEventMetadata(final String userId) {
        return EventMetadata.withUser(
            aggregateType,
            aggregateId,
            eventId,
            aggregateVersion,
            userId
        );
    }

    /**
     * Creates an event metadata object for the next event with full context.
     * @param userId the user who will trigger the next event
     * @param correlationId the correlation ID for the next event
     * @param causationId the causation ID for the next event
     * @return metadata for the next event with full context
     */
    
    public EventMetadata createNextEventMetadata(
        final String userId,
        final String correlationId,
        final String causationId
    ) {
        return EventMetadata.withFullContext(
            aggregateType,
            aggregateId,
            eventId,
            aggregateVersion,
            userId,
            correlationId,
            causationId
        );
    }
}