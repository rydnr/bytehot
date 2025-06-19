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
 * Filename: EventStorePort.java
 *
 * Author: Claude Code
 *
 * Class name: EventStorePort
 *
 * Responsibilities:
 *   - Define interface for EventSourcing event persistence operations
 *   - Abstract event storage from domain logic
 *   - Enable different storage strategies (filesystem, database, etc.)
 *
 * Collaborators:
 *   - VersionedDomainEvent: Events stored in the event store
 *   - FilesystemEventStoreAdapter: Infrastructure implementation
 */
package org.acmsl.bytehot.domain;

import org.acmsl.bytehot.domain.EventStoreException;

import org.acmsl.commons.patterns.Port;
import org.acmsl.commons.patterns.eventsourcing.VersionedDomainEvent;

import java.time.Instant;
import java.util.List;

/**
 * Port interface for EventSourcing event persistence operations
 * @author Claude Code
 * @since 2025-06-17
 */
public interface EventStorePort extends Port {

    /**
     * Persists a domain event to the event store
     * @param event the event to save
     * @throws EventStoreException if the event cannot be saved
     */
    void save(VersionedDomainEvent event) throws EventStoreException;

    /**
     * Retrieves all events for a specific aggregate
     * @param aggregateType the type of aggregate (e.g., "user", "hotswap")
     * @param aggregateId the unique identifier of the aggregate instance
     * @return list of events ordered by version (oldest first)
     * @throws EventStoreException if events cannot be retrieved
     */
    List<VersionedDomainEvent> getEventsForAggregate(
        String aggregateType,
        String aggregateId
    ) throws EventStoreException;

    /**
     * Retrieves events for an aggregate since a specific version
     * @param aggregateType the type of aggregate
     * @param aggregateId the unique identifier of the aggregate instance
     * @param sinceVersion retrieve events after this version (exclusive)
     * @return list of events ordered by version (oldest first)
     * @throws EventStoreException if events cannot be retrieved
     */
    List<VersionedDomainEvent> getEventsForAggregateSince(
        String aggregateType,
        String aggregateId,
        long sinceVersion
    ) throws EventStoreException;

    /**
     * Retrieves all events of a specific type across all aggregates
     * @param eventType the simple class name of the event
     * @return list of events ordered by timestamp (oldest first)
     * @throws EventStoreException if events cannot be retrieved
     */
    List<VersionedDomainEvent> getEventsByType(String eventType) throws EventStoreException;

    /**
     * Retrieves events within a time range across all aggregates
     * @param startTime start of the time range (inclusive)
     * @param endTime end of the time range (inclusive)
     * @return list of events ordered by timestamp (oldest first)
     * @throws EventStoreException if events cannot be retrieved
     */
    List<VersionedDomainEvent> getEventsBetween(
        Instant startTime,
        Instant endTime
    ) throws EventStoreException;

    /**
     * Gets the current version for an aggregate
     * @param aggregateType the type of aggregate
     * @param aggregateId the unique identifier of the aggregate instance
     * @return the current version number (0 if no events exist)
     * @throws EventStoreException if version cannot be retrieved
     */
    long getCurrentVersion(String aggregateType, String aggregateId) throws EventStoreException;

    /**
     * Checks if the event store is healthy and accessible
     * @return true if the event store can be accessed and used
     */
    boolean isHealthy();

    /**
     * Gets the total number of events stored
     * @return the total count of events across all aggregates
     * @throws EventStoreException if count cannot be retrieved
     */
    long getTotalEventCount() throws EventStoreException;

    /**
     * Gets the number of events for a specific aggregate
     * @param aggregateType the type of aggregate
     * @param aggregateId the unique identifier of the aggregate instance
     * @return the count of events for this aggregate
     * @throws EventStoreException if count cannot be retrieved
     */
    long getEventCountForAggregate(
        String aggregateType,
        String aggregateId
    ) throws EventStoreException;

    /**
     * Checks if an aggregate exists (has any events)
     * @param aggregateType the type of aggregate
     * @param aggregateId the unique identifier of the aggregate instance
     * @return true if the aggregate has at least one event
     * @throws EventStoreException if existence cannot be checked
     */
    boolean aggregateExists(String aggregateType, String aggregateId) throws EventStoreException;

    /**
     * Gets all known aggregate types
     * @return list of aggregate type names
     * @throws EventStoreException if aggregate types cannot be retrieved
     */
    List<String> getAggregateTypes() throws EventStoreException;

    /**
     * Gets all aggregate IDs for a specific type
     * @param aggregateType the type of aggregate
     * @return list of aggregate IDs
     * @throws EventStoreException if aggregate IDs cannot be retrieved
     */
    List<String> getAggregateIds(String aggregateType) throws EventStoreException;
}