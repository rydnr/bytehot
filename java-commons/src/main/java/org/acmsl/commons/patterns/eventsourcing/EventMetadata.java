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
 * Filename: EventMetadata.java
 *
 * Author: Claude (Anthropic AI)
 *
 * Class name: EventMetadata
 *
 * Responsibilities:
 *   - Hold EventSourcing metadata for domain events
 *   - Provide factory methods for creating event metadata
 *   - Enable consistent metadata generation across events
 *   - Support event sourcing patterns across any domain
 *
 * Collaborators:
 *   - VersionedDomainEvent: Uses this metadata
 *   - AbstractVersionedDomainEvent: Base implementation using this metadata
 */
package org.acmsl.commons.patterns.eventsourcing;

import org.acmsl.commons.patterns.dao.ValueObject;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * Value object containing EventSourcing metadata for domain events.
 * Provides comprehensive metadata support for event sourcing patterns across any domain.
 * @author Claude (Anthropic AI)
 * @since 2025-06-19
 */
@EqualsAndHashCode
@ToString
public final class EventMetadata implements ValueObject {

    /**
     * Unique identifier for this event instance.
     */
    
    private final String eventId;

    /**
     * Type of aggregate this event belongs to.
     */
    
    private final String aggregateType;

    /**
     * Unique identifier of the aggregate instance.
     */
    
    private final String aggregateId;

    /**
     * Version of this event for the aggregate.
     */
    private final long aggregateVersion;

    /**
     * Timestamp when the event occurred.
     */
    
    private final Instant timestamp;

    /**
     * ID of the previous event in this aggregate's history.
     */
    
    private final String previousEventId;

    /**
     * Version of the event schema for migration purposes.
     */
    private final int schemaVersion;

    /**
     * User who triggered this event.
     */
    
    private final String userId;

    /**
     * Correlation ID for tracing related events.
     */
    
    private final String correlationId;

    /**
     * Causation ID linking this event to the command that triggered it.
     */
    
    private final String causationId;

    /**
     * Stream position in the event store for this event.
     */
    
    private final Long streamPosition;

    /**
     * Constructor for creating EventMetadata instances.
     */
    public EventMetadata(final String eventId,
                        final String aggregateType,
                        final String aggregateId,
                        final long aggregateVersion,
                        final Instant timestamp,
                        final String previousEventId,
                        final int schemaVersion,
                        final String userId,
                        final String correlationId,
                        final String causationId,
                        final Long streamPosition) {
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
     * Gets the unique identifier for this event instance.
     * @return the event ID
     */
    
    public String getEventId() {
        return eventId;
    }

    /**
     * Gets the type of aggregate this event belongs to.
     * @return the aggregate type
     */
    
    public String getAggregateType() {
        return aggregateType;
    }

    /**
     * Gets the unique identifier of the aggregate instance.
     * @return the aggregate ID
     */
    
    public String getAggregateId() {
        return aggregateId;
    }

    /**
     * Gets the version of this event for the aggregate.
     * @return the aggregate version
     */
    public long getAggregateVersion() {
        return aggregateVersion;
    }

    /**
     * Gets the timestamp when the event occurred.
     * @return the event timestamp
     */
    
    public Instant getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the ID of the previous event in the aggregate's history.
     * @return the previous event ID or null if this is the first event
     */
    
    public String getPreviousEventId() {
        return previousEventId;
    }

    /**
     * Gets the schema version for this event.
     * @return the schema version
     */
    public int getSchemaVersion() {
        return schemaVersion;
    }

    /**
     * Gets the ID of the user who triggered this event.
     * @return the user ID or null if no user context
     */
    
    public String getUserId() {
        return userId;
    }

    /**
     * Gets the correlation ID for tracing related events.
     * @return the correlation ID or null if not correlated
     */
    
    public String getCorrelationId() {
        return correlationId;
    }

    /**
     * Gets the causation ID linking this event to the command that triggered it.
     * @return the causation ID or null if not available
     */
    
    public String getCausationId() {
        return causationId;
    }

    /**
     * Gets the stream position in the event store for this event.
     * @return the stream position or null if not set
     */
    
    public Long getStreamPosition() {
        return streamPosition;
    }

    /**
     * Creates event metadata for a new aggregate (first event).
     * @param aggregateType the type of aggregate
     * @param aggregateId the unique identifier of the aggregate
     * @return metadata for the first event
     */
    
    public static EventMetadata forNewAggregate(final String aggregateType, 
                                                final String aggregateId) {
        return new EventMetadata(
            UUID.randomUUID().toString(),
            aggregateType,
            aggregateId,
            1L, // First version
            Instant.now(),
            null, // No previous event
            1, // Default schema version
            null, // No user context yet
            null, // No correlation yet
            null, // No causation yet
            null // Not yet persisted
        );
    }

    /**
     * Creates event metadata for an existing aggregate.
     * @param aggregateType the type of aggregate
     * @param aggregateId the unique identifier of the aggregate
     * @param previousEventId the ID of the previous event
     * @param currentVersion the current version of the aggregate
     * @return metadata for the next event
     */
    
    public static EventMetadata forExistingAggregate(
        final String aggregateType,
        final String aggregateId,
        final String previousEventId,
        final long currentVersion
    ) {
        return new EventMetadata(
            UUID.randomUUID().toString(),
            aggregateType,
            aggregateId,
            currentVersion + 1,
            Instant.now(),
            previousEventId,
            1, // Default schema version
            null, // No user context yet
            null, // No correlation yet
            null, // No causation yet
            null // Not yet persisted
        );
    }

    /**
     * Creates event metadata for an existing aggregate with custom timestamp.
     * @param aggregateType the type of aggregate
     * @param aggregateId the unique identifier of the aggregate
     * @param previousEventId the ID of the previous event
     * @param currentVersion the current version of the aggregate
     * @param timestamp the timestamp for this event
     * @return metadata for the next event
     */
    
    public static EventMetadata forExistingAggregate(
        final String aggregateType,
        final String aggregateId,
        final String previousEventId,
        final long currentVersion,
        final Instant timestamp
    ) {
        return new EventMetadata(
            UUID.randomUUID().toString(),
            aggregateType,
            aggregateId,
            currentVersion + 1,
            timestamp,
            previousEventId,
            1, // Default schema version
            null, // No user context yet
            null, // No correlation yet
            null, // No causation yet
            null // Not yet persisted
        );
    }

    /**
     * Creates event metadata with user context.
     * @param aggregateType the type of aggregate
     * @param aggregateId the unique identifier of the aggregate
     * @param previousEventId the ID of the previous event (null for first event)
     * @param currentVersion the current version of the aggregate (0 for first event)
     * @param userId the user who triggered this event
     * @return metadata with user context
     */
    
    public static EventMetadata withUser(
        final String aggregateType,
        final String aggregateId,
        final String previousEventId,
        final long currentVersion,
        final String userId
    ) {
        return new EventMetadata(
            UUID.randomUUID().toString(),
            aggregateType,
            aggregateId,
            currentVersion + 1,
            Instant.now(),
            previousEventId,
            1, // Default schema version
            userId,
            null, // No correlation yet
            null, // No causation yet
            null // Not yet persisted
        );
    }

    /**
     * Creates event metadata with full correlation context.
     * @param aggregateType the type of aggregate
     * @param aggregateId the unique identifier of the aggregate
     * @param previousEventId the ID of the previous event (null for first event)
     * @param currentVersion the current version of the aggregate (0 for first event)
     * @param userId the user who triggered this event
     * @param correlationId the correlation ID for tracing
     * @param causationId the causation ID linking to the command
     * @return metadata with full correlation context
     */
    
    public static EventMetadata withFullContext(
        final String aggregateType,
        final String aggregateId,
        final String previousEventId,
        final long currentVersion,
        final String userId,
        final String correlationId,
        final String causationId
    ) {
        return new EventMetadata(
            UUID.randomUUID().toString(),
            aggregateType,
            aggregateId,
            currentVersion + 1,
            Instant.now(),
            previousEventId,
            1, // Default schema version
            userId,
            correlationId,
            causationId,
            null // Not yet persisted
        );
    }

    /**
     * Creates a copy of this metadata with a different version.
     * @param newVersion the new aggregate version
     * @return metadata with updated version
     */
    
    public EventMetadata withVersion(final long newVersion) {
        return new EventMetadata(
            eventId,
            aggregateType,
            aggregateId,
            newVersion,
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
     * Creates a copy of this metadata with a user ID.
     * @param userId the user ID to set
     * @return metadata with user context
     */
    
    public EventMetadata withUserId(final String userId) {
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
     * Creates a copy of this metadata with a correlation ID.
     * @param correlationId the correlation ID to set
     * @return metadata with correlation
     */
    
    public EventMetadata withCorrelationId(final String correlationId) {
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
     * Creates a copy of this metadata with a causation ID.
     * @param causationId the causation ID to set
     * @return metadata with causation
     */
    
    public EventMetadata withCausationId(final String causationId) {
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
     * Creates a copy of this metadata with a stream position.
     * @param streamPosition the stream position to set
     * @return metadata with stream position
     */
    
    public EventMetadata withStreamPosition(final Long streamPosition) {
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
     * Creates a copy of this metadata with a different timestamp.
     * @param timestamp the new timestamp
     * @return metadata with updated timestamp
     */
    
    public EventMetadata withTimestamp(final Instant timestamp) {
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
     * Checks if this is metadata for the first event of an aggregate.
     * @return true if this is the first event
     */
    public boolean isFirstEvent() {
        return aggregateVersion == 1L && previousEventId == null;
    }

    /**
     * Checks if this metadata has user context.
     * @return true if user ID is present and not empty
     */
    public boolean hasUser() {
        return userId != null && !userId.trim().isEmpty();
    }

    /**
     * Checks if this metadata has correlation.
     * @return true if correlation ID is present and not empty
     */
    public boolean hasCorrelation() {
        return correlationId != null && !correlationId.trim().isEmpty();
    }

    /**
     * Checks if this metadata has causation.
     * @return true if causation ID is present and not empty
     */
    public boolean hasCausation() {
        return causationId != null && !causationId.trim().isEmpty();
    }

    /**
     * Checks if this metadata has stream position.
     * @return true if stream position is present
     */
    public boolean hasStreamPosition() {
        return streamPosition != null;
    }

    /**
     * Checks if this is a system event (no user).
     * @return true if no user is associated with this event
     */
    public boolean isSystemEvent() {
        return !hasUser();
    }

    /**
     * Gets the age of this event from the current time.
     * @return duration since the event occurred
     */
    
    public Duration getAge() {
        return Duration.between(timestamp, Instant.now());
    }
}