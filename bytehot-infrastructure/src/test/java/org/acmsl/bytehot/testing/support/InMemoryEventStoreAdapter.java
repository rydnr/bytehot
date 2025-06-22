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
 * Filename: InMemoryEventStoreAdapter.java
 *
 * Author: Claude Code
 *
 * Class name: InMemoryEventStoreAdapter
 *
 * Responsibilities:
 *   - In-memory implementation of EventStore for testing isolation
 *   - Fast event storage and retrieval without filesystem dependencies
 *   - Supports all EventStore operations for test scenarios
 *   - Provides test-specific features like reset and inspection
 *
 * Collaborators:
 *   - EventStorePort: Interface this adapter implements
 *   - EventDrivenTestSupport: Uses this for isolated test storage
 *   - VersionedDomainEvent: Events stored in memory
 */
package org.acmsl.bytehot.testing.support;

import org.acmsl.bytehot.domain.EventStoreException;
import org.acmsl.bytehot.domain.EventStorePort;
import org.acmsl.commons.patterns.eventsourcing.VersionedDomainEvent;

import org.acmsl.commons.patterns.Adapter;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * In-memory implementation of EventStore for testing isolation.
 * This adapter provides fast, isolated event storage without filesystem
 * dependencies, making tests faster and more reliable.
 * 
 * @author Claude Code
 * @since 2025-06-17
 */
public class InMemoryEventStoreAdapter implements EventStorePort, Adapter<EventStorePort> {

    /**
     * In-memory storage organized by aggregate type and ID
     * Structure: aggregateType -> aggregateId -> List<VersionedDomainEvent>
     */
    private final Map<String, Map<String, List<VersionedDomainEvent>>> eventStorage;

    /**
     * Version tracking for aggregates
     * Key format: "aggregateType/aggregateId"
     */
    private final Map<String, AtomicLong> aggregateVersions;

    /**
     * All events in chronological order for global queries
     */
    private final List<VersionedDomainEvent> chronologicalEvents;

    /**
     * Creation timestamp for health checking
     */
    private final Instant createdAt;

    /**
     * Creates a new in-memory event store.
     */
    public InMemoryEventStoreAdapter() {
        this.eventStorage = new ConcurrentHashMap<>();
        this.aggregateVersions = new ConcurrentHashMap<>();
        this.chronologicalEvents = Collections.synchronizedList(new ArrayList<>());
        this.createdAt = Instant.now();
    }

    @Override
    public void save(VersionedDomainEvent event) throws EventStoreException {
        try {
            String aggregateType = event.getAggregateType();
            String aggregateId = event.getAggregateId();
            
            // Get or create aggregate storage
            Map<String, List<VersionedDomainEvent>> typeStorage = 
                eventStorage.computeIfAbsent(aggregateType, k -> new ConcurrentHashMap<>());
            
            List<VersionedDomainEvent> aggregateEvents = 
                typeStorage.computeIfAbsent(aggregateId, k -> Collections.synchronizedList(new ArrayList<>()));
            
            // Update version if needed
            String aggregateKey = aggregateType + "/" + aggregateId;
            AtomicLong currentVersion = aggregateVersions.computeIfAbsent(
                aggregateKey, 
                k -> new AtomicLong(0L)
            );
            
            // Increment version
            long newVersion = currentVersion.incrementAndGet();
            
            // Store the event
            aggregateEvents.add(event);
            chronologicalEvents.add(event);
            
        } catch (Exception e) {
            throw new EventStoreException(
                "Failed to save event in memory: " + e.getMessage(),
                e,
                EventStoreException.OperationType.SAVE,
                event.getAggregateType(),
                event.getAggregateId()
            );
        }
    }

    @Override
    public List<VersionedDomainEvent> getEventsForAggregate(String aggregateType, String aggregateId) 
            throws EventStoreException {
        try {
            Map<String, List<VersionedDomainEvent>> typeStorage = eventStorage.get(aggregateType);
            if (typeStorage == null) {
                return new ArrayList<>();
            }
            
            List<VersionedDomainEvent> events = typeStorage.get(aggregateId);
            if (events == null) {
                return new ArrayList<>();
            }
            
            // Return copy to prevent modification
            return new ArrayList<>(events);
            
        } catch (Exception e) {
            throw new EventStoreException(
                "Failed to retrieve events from memory: " + e.getMessage(),
                e,
                EventStoreException.OperationType.RETRIEVE,
                aggregateType,
                aggregateId
            );
        }
    }

    @Override
    public List<VersionedDomainEvent> getEventsForAggregateSince(String aggregateType, String aggregateId, long sinceVersion) 
            throws EventStoreException {
        List<VersionedDomainEvent> allEvents = getEventsForAggregate(aggregateType, aggregateId);
        return allEvents.stream()
            .filter(event -> event.getAggregateVersion() > sinceVersion)
            .collect(Collectors.toList());
    }

    @Override
    public List<VersionedDomainEvent> getEventsByType(String eventType) throws EventStoreException {
        return chronologicalEvents.stream()
            .filter(event -> eventType.equals(event.getEventType()))
            .collect(Collectors.toList());
    }

    @Override
    public List<VersionedDomainEvent> getEventsBetween(Instant startTime, Instant endTime) throws EventStoreException {
        return chronologicalEvents.stream()
            .filter(event -> {
                Instant eventTime = event.getTimestamp();
                return !eventTime.isBefore(startTime) && !eventTime.isAfter(endTime);
            })
            .collect(Collectors.toList());
    }

    @Override
    public long getCurrentVersion(String aggregateType, String aggregateId) throws EventStoreException {
        String aggregateKey = aggregateType + "/" + aggregateId;
        AtomicLong version = aggregateVersions.get(aggregateKey);
        return version != null ? version.get() : 0L;
    }

    @Override
    public long getEventCountForAggregate(String aggregateType, String aggregateId) throws EventStoreException {
        List<VersionedDomainEvent> events = getEventsForAggregate(aggregateType, aggregateId);
        return events.size();
    }

    @Override
    public boolean aggregateExists(String aggregateType, String aggregateId) throws EventStoreException {
        return getEventCountForAggregate(aggregateType, aggregateId) > 0;
    }

    @Override
    public List<String> getAggregateTypes() throws EventStoreException {
        return new ArrayList<>(eventStorage.keySet());
    }

    @Override
    public List<String> getAggregateIds(String aggregateType) throws EventStoreException {
        Map<String, List<VersionedDomainEvent>> typeStorage = eventStorage.get(aggregateType);
        if (typeStorage == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(typeStorage.keySet());
    }

    @Override
    public boolean isHealthy() {
        // In-memory store is always healthy if it exists
        return true;
    }

    @Override
    public long getTotalEventCount() throws EventStoreException {
        return chronologicalEvents.size();
    }

    @Override
    public Class<EventStorePort> adapts() {
        return EventStorePort.class;
    }

    /**
     * Gets health status information for debugging.
     * This is a test-specific method not part of the EventStorePort interface.
     * 
     * @return health status map
     */
    public Map<String, Object> getHealthStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("healthy", true);
        status.put("type", "in-memory");
        status.put("createdAt", createdAt);
        status.put("totalEvents", chronologicalEvents.size());
        status.put("aggregateTypes", eventStorage.size());
        
        // Count total aggregates
        int totalAggregates = eventStorage.values().stream()
            .mapToInt(Map::size)
            .sum();
        status.put("totalAggregates", totalAggregates);
        
        return status;
    }

    /**
     * Test-specific method to clear all stored events.
     * This allows tests to start with a clean state.
     */
    public void clear() {
        eventStorage.clear();
        aggregateVersions.clear();
        chronologicalEvents.clear();
    }

    /**
     * Test-specific method to get all events in chronological order.
     * Useful for debugging and test verification.
     * 
     * @return all events in the order they were stored
     */
    public List<VersionedDomainEvent> getAllEvents() {
        return new ArrayList<>(chronologicalEvents);
    }

    /**
     * Test-specific method to get the number of stored events.
     * 
     * @return total number of events in the store
     */
    public int getEventCount() {
        return chronologicalEvents.size();
    }

    /**
     * Test-specific method to check if the store is empty.
     * 
     * @return true if no events are stored
     */
    public boolean isEmpty() {
        return chronologicalEvents.isEmpty();
    }

    /**
     * Test-specific method to get storage statistics.
     * 
     * @return map containing detailed storage statistics
     */
    public Map<String, Integer> getStorageStats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("totalEvents", chronologicalEvents.size());
        stats.put("aggregateTypes", eventStorage.size());
        
        int totalAggregates = eventStorage.values().stream()
            .mapToInt(Map::size)
            .sum();
        stats.put("totalAggregates", totalAggregates);
        
        return stats;
    }
}