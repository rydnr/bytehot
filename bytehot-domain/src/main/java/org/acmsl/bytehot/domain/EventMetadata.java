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
 * Filename: EventMetadata.java
 *
 * Author: Claude Code
 *
 * Class name: EventMetadata
 *
 * Responsibilities:
 *   - Hold EventSourcing metadata for domain events
 *   - Provide factory methods for creating event metadata
 *   - Enable consistent metadata generation across events
 *
 * Collaborators:
 *   - VersionedDomainEvent: Uses this metadata
 *   - AbstractVersionedDomainEvent: Base implementation using this metadata
 */
package org.acmsl.bytehot.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

/**
 * Value object containing EventSourcing metadata for domain events
 * @author Claude Code
 * @since 2025-06-17
 */
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
public class EventMetadata {

    /**
     * Unique identifier for this event instance
     */
    private final String eventId;

    /**
     * Type of aggregate this event belongs to
     */
    private final String aggregateType;

    /**
     * Unique identifier of the aggregate instance
     */
    private final String aggregateId;

    /**
     * Version of this event for the aggregate
     */
    private final long aggregateVersion;

    /**
     * Timestamp when the event occurred
     */
    private final Instant timestamp;

    /**
     * ID of the previous event in this aggregate's history
     */
    private final String previousEventId;

    /**
     * Version of the event schema for migration purposes
     */
    private final int schemaVersion;

    /**
     * User who triggered this event
     */
    private final String userId;

    /**
     * Correlation ID for tracing related events
     */
    private final String correlationId;

    /**
     * Creates event metadata for a new aggregate (first event)
     * @param aggregateType the type of aggregate
     * @param aggregateId the unique identifier of the aggregate
     * @return metadata for the first event
     */
    public static EventMetadata forNewAggregate(String aggregateType, String aggregateId) {
        return new EventMetadata(
            UUID.randomUUID().toString(),
            aggregateType,
            aggregateId,
            1L, // First version
            Instant.now(),
            null, // No previous event
            1, // Default schema version
            null, // No user context yet
            null // No correlation yet
        );
    }

    /**
     * Creates event metadata for an existing aggregate
     * @param aggregateType the type of aggregate
     * @param aggregateId the unique identifier of the aggregate
     * @param previousEventId the ID of the previous event
     * @param currentVersion the current version of the aggregate
     * @return metadata for the next event
     */
    public static EventMetadata forExistingAggregate(
        String aggregateType,
        String aggregateId,
        String previousEventId,
        long currentVersion
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
            null // No correlation yet
        );
    }

    /**
     * Creates event metadata for an existing aggregate with custom timestamp
     * @param aggregateType the type of aggregate
     * @param aggregateId the unique identifier of the aggregate
     * @param previousEventId the ID of the previous event
     * @param currentVersion the current version of the aggregate
     * @param timestamp the timestamp for this event
     * @return metadata for the next event
     */
    public static EventMetadata forExistingAggregate(
        String aggregateType,
        String aggregateId,
        String previousEventId,
        long currentVersion,
        Instant timestamp
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
            null // No correlation yet
        );
    }

    /**
     * Creates event metadata with user context
     * @param aggregateType the type of aggregate
     * @param aggregateId the unique identifier of the aggregate
     * @param previousEventId the ID of the previous event (null for first event)
     * @param currentVersion the current version of the aggregate (0 for first event)
     * @param userId the user who triggered this event
     * @return metadata with user context
     */
    public static EventMetadata withUser(
        String aggregateType,
        String aggregateId,
        String previousEventId,
        long currentVersion,
        String userId
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
            null // No correlation yet
        );
    }

    /**
     * Creates event metadata with correlation ID
     * @param aggregateType the type of aggregate
     * @param aggregateId the unique identifier of the aggregate
     * @param previousEventId the ID of the previous event (null for first event)
     * @param currentVersion the current version of the aggregate (0 for first event)
     * @param userId the user who triggered this event
     * @param correlationId the correlation ID for tracing
     * @return metadata with correlation
     */
    public static EventMetadata withCorrelation(
        String aggregateType,
        String aggregateId,
        String previousEventId,
        long currentVersion,
        String userId,
        String correlationId
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
            correlationId
        );
    }

    /**
     * Creates a copy of this metadata with a different version
     * @param newVersion the new aggregate version
     * @return metadata with updated version
     */
    public EventMetadata withVersion(long newVersion) {
        return new EventMetadata(
            this.eventId,
            this.aggregateType,
            this.aggregateId,
            newVersion,
            this.timestamp,
            this.previousEventId,
            this.schemaVersion,
            this.userId,
            this.correlationId
        );
    }

    /**
     * Creates a copy of this metadata with a user ID
     * @param userId the user ID to set
     * @return metadata with user context
     */
    public EventMetadata withUserId(String userId) {
        return new EventMetadata(
            this.eventId,
            this.aggregateType,
            this.aggregateId,
            this.aggregateVersion,
            this.timestamp,
            this.previousEventId,
            this.schemaVersion,
            userId,
            this.correlationId
        );
    }

    /**
     * Creates a copy of this metadata with a correlation ID
     * @param correlationId the correlation ID to set
     * @return metadata with correlation
     */
    public EventMetadata withCorrelationId(String correlationId) {
        return new EventMetadata(
            this.eventId,
            this.aggregateType,
            this.aggregateId,
            this.aggregateVersion,
            this.timestamp,
            this.previousEventId,
            this.schemaVersion,
            this.userId,
            correlationId
        );
    }

    /**
     * Checks if this is metadata for the first event of an aggregate
     * @return true if this is the first event
     */
    public boolean isFirstEvent() {
        return aggregateVersion == 1L && previousEventId == null;
    }

    /**
     * Checks if this metadata has user context
     * @return true if user ID is present
     */
    public boolean hasUser() {
        return userId != null;
    }

    /**
     * Checks if this metadata has correlation
     * @return true if correlation ID is present
     */
    public boolean hasCorrelation() {
        return correlationId != null;
    }
}