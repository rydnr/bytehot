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

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

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
    @NonNull
    private final String eventId;

    /**
     * Type of aggregate this event belongs to.
     */
    @Getter
    @NonNull
    private final String aggregateType;

    /**
     * Unique identifier of the aggregate instance.
     */
    @Getter
    @NonNull
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
    @NonNull
    private final Instant timestamp;

    /**
     * ID of the previous event in this aggregate's history.
     */
    @Getter
    @Nullable
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
    @Nullable
    private final String userId;

    /**
     * Correlation ID for tracing related events.
     */
    @Getter
    @Nullable
    private final String correlationId;

    /**
     * Causation ID linking this event to the command that triggered it.
     */
    @Getter
    @Nullable
    private final String causationId;

    /**
     * Stream position in the event store for this event.
     */
    @Getter
    @Nullable
    private final Long streamPosition;

    /**
     * Constructor that takes EventMetadata for convenient event creation.
     * @param metadata the event metadata containing all necessary information
     */
    protected AbstractVersionedDomainEvent(@NonNull final EventMetadata metadata) {
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
        @NonNull final String eventId,
        @NonNull final String aggregateType,
        @NonNull final String aggregateId,
        final long aggregateVersion,
        @NonNull final Instant timestamp,
        @Nullable final String previousEventId,
        final int schemaVersion,
        @Nullable final String userId,
        @Nullable final String correlationId,
        @Nullable final String causationId,
        @Nullable final Long streamPosition
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
    @NonNull
    protected static EventMetadata createMetadataForNewAggregate(
        @NonNull final String aggregateType,
        @NonNull final String aggregateId
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
    @NonNull
    protected static EventMetadata createMetadataForExistingAggregate(
        @NonNull final String aggregateType,
        @NonNull final String aggregateId,
        @NonNull final String previousEventId,
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
    @NonNull
    protected static EventMetadata createMetadataWithUser(
        @NonNull final String aggregateType,
        @NonNull final String aggregateId,
        @Nullable final String previousEventId,
        final long currentVersion,
        @NonNull final String userId
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
    @NonNull
    protected static EventMetadata createMetadataWithFullContext(
        @NonNull final String aggregateType,
        @NonNull final String aggregateId,
        @Nullable final String previousEventId,
        final long currentVersion,
        @Nullable final String userId,
        @Nullable final String correlationId,
        @Nullable final String causationId
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
    @NonNull
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
    @NonNull
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
    @NonNull
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
    public boolean canFollowDirectly(@NonNull final VersionedDomainEvent previousEvent) {
        return belongsToSameAggregate(previousEvent) &&
               getAggregateVersion() == previousEvent.getAggregateVersion() + 1 &&
               previousEvent.getEventId().equals(getPreviousEventId());
    }

    /**
     * Creates an event metadata object that would be appropriate for the next event
     * in this aggregate's event stream.
     * @return metadata for the next event
     */
    @NonNull
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
    @NonNull
    public EventMetadata createNextEventMetadata(@NonNull final String userId) {
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
    @NonNull
    public EventMetadata createNextEventMetadata(
        @Nullable final String userId,
        @Nullable final String correlationId,
        @Nullable final String causationId
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