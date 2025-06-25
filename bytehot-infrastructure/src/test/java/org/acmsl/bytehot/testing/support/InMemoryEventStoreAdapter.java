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
 *   - Provide in-memory EventStore implementation for testing
 *   - Fast, isolated event storage without filesystem dependencies
 *   - Thread-safe concurrent access for test scenarios
 *   - Test data cleanup and reset capabilities
 *
 * Collaborators:
 *   - EventStorePort: Interface this adapter implements
 *   - VersionedDomainEvent: Events stored in memory
 *   - EventDrivenTestSupport: Uses this for test isolation
 */
package org.acmsl.bytehot.testing.support;

import org.acmsl.bytehot.domain.EventStoreException;
import org.acmsl.bytehot.domain.EventStorePort;
import org.acmsl.bytehot.domain.OperationType;

import org.acmsl.commons.patterns.Adapter;
import org.acmsl.commons.patterns.Test;
import org.acmsl.commons.patterns.eventsourcing.VersionedDomainEvent;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * In-memory implementation of EventStore for testing scenarios.
 * Provides fast, isolated event storage without filesystem dependencies.
 * @author Claude Code
 * @since 2025-06-24
 */
public class InMemoryEventStoreAdapter implements EventStorePort, Adapter<EventStorePort>, Test {

    /**
     * Storage for events organized by aggregate type and ID
     * Structure: aggregateType -> aggregateId -> List<VersionedDomainEvent>
     */
    private final Map<String, Map<String, List<VersionedDomainEvent>>> eventStorage;

    /**
     * Version tracking for each aggregate
     * Key format: "aggregateType:aggregateId"
     */
    private final Map<String, AtomicLong> aggregateVersions;

    /**
     * Global event storage for cross-aggregate queries
     * Maintains insertion order for time-based queries
     */
    private final List<VersionedDomainEvent> globalEventLog;

    /**
     * Track when the store was created for health monitoring
     */
    private final Instant createdAt;

    /**
     * Whether this store is currently healthy
     */
    private volatile boolean healthy;

    /**
     * Creates a new in-memory event store for testing
     */
    public InMemoryEventStoreAdapter() {
        this.eventStorage = new ConcurrentHashMap<>();
        this.aggregateVersions = new ConcurrentHashMap<>();
        this.globalEventLog = new CopyOnWriteArrayList<>();
        this.createdAt = Instant.now();
        this.healthy = true;
    }

    @Override
    public void save(@NonNull final VersionedDomainEvent event) throws EventStoreException {
        validateHealthy();
        
        try {
            String aggregateType = event.getAggregateType();
            String aggregateId = event.getAggregateId();
            String versionKey = aggregateType + ":" + aggregateId;
            
            // Ensure storage structures exist
            eventStorage.computeIfAbsent(aggregateType, k -> new ConcurrentHashMap<>())
                       .computeIfAbsent(aggregateId, k -> new CopyOnWriteArrayList<>())
                       .add(event);
            
            // Update version tracking
            aggregateVersions.computeIfAbsent(versionKey, k -> new AtomicLong(0))
                           .set(event.getAggregateVersion());
            
            // Add to global log for cross-aggregate queries
            globalEventLog.add(event);
            
        } catch (Exception e) {
            throw new EventStoreException("Failed to save event to in-memory store: " + e.getMessage(), e, OperationType.SAVE);
        }
    }

    @Override
    public List<VersionedDomainEvent> getEventsForAggregate(
        @NonNull final String aggregateType,
        @NonNull final String aggregateId
    ) throws EventStoreException {
        validateHealthy();
        
        try {
            return eventStorage.getOrDefault(aggregateType, Map.of())
                             .getOrDefault(aggregateId, List.of())
                             .stream()
                             .sorted(Comparator.comparing(VersionedDomainEvent::getAggregateVersion))
                             .collect(Collectors.toList());
        } catch (Exception e) {
            throw new EventStoreException("Failed to retrieve events for aggregate: " + e.getMessage(), e, OperationType.RETRIEVE);
        }
    }

    @Override
    public List<VersionedDomainEvent> getEventsForAggregateSince(
        @NonNull final String aggregateType,
        @NonNull final String aggregateId,
        final long sinceVersion
    ) throws EventStoreException {
        validateHealthy();
        
        try {
            return getEventsForAggregate(aggregateType, aggregateId)
                .stream()
                .filter(event -> event.getAggregateVersion() > sinceVersion)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new EventStoreException("Failed to retrieve events since version: " + e.getMessage(), e, OperationType.RETRIEVE);
        }
    }

    @Override
    public List<VersionedDomainEvent> getEventsByType(@NonNull final String eventType) throws EventStoreException {
        validateHealthy();
        
        try {
            return globalEventLog.stream()
                .filter(event -> event.getClass().getSimpleName().equals(eventType))
                .sorted(Comparator.comparing(VersionedDomainEvent::getTimestamp))
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new EventStoreException("Failed to retrieve events by type: " + e.getMessage(), e, OperationType.RETRIEVE);
        }
    }

    @Override
    public List<VersionedDomainEvent> getEventsBetween(
        @NonNull final Instant startTime,
        @NonNull final Instant endTime
    ) throws EventStoreException {
        validateHealthy();
        
        try {
            return globalEventLog.stream()
                .filter(event -> {
                    Instant timestamp = event.getTimestamp();
                    return (timestamp.equals(startTime) || timestamp.isAfter(startTime)) &&
                           (timestamp.equals(endTime) || timestamp.isBefore(endTime));
                })
                .sorted(Comparator.comparing(VersionedDomainEvent::getTimestamp))
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new EventStoreException("Failed to retrieve events in time range: " + e.getMessage(), e, OperationType.RETRIEVE);
        }
    }

    @Override
    public long getCurrentVersion(@NonNull final String aggregateType, @NonNull final String aggregateId) 
            throws EventStoreException {
        validateHealthy();
        
        try {
            String versionKey = aggregateType + ":" + aggregateId;
            AtomicLong version = aggregateVersions.get(versionKey);
            return version != null ? version.get() : 0L;
        } catch (Exception e) {
            throw new EventStoreException("Failed to get current version: " + e.getMessage(), e, OperationType.RETRIEVE);
        }
    }

    @Override
    public boolean isHealthy() {
        return healthy;
    }

    @Override
    public long getTotalEventCount() throws EventStoreException {
        validateHealthy();
        
        try {
            return globalEventLog.size();
        } catch (Exception e) {
            throw new EventStoreException("Failed to get total event count: " + e.getMessage(), e, OperationType.COUNT);
        }
    }

    @Override
    public long getEventCountForAggregate(
        @NonNull final String aggregateType,
        @NonNull final String aggregateId
    ) throws EventStoreException {
        validateHealthy();
        
        try {
            return eventStorage.getOrDefault(aggregateType, Map.of())
                             .getOrDefault(aggregateId, List.of())
                             .size();
        } catch (Exception e) {
            throw new EventStoreException("Failed to get event count for aggregate: " + e.getMessage(), e, OperationType.COUNT);
        }
    }

    @Override
    public boolean aggregateExists(@NonNull final String aggregateType, @NonNull final String aggregateId) 
            throws EventStoreException {
        validateHealthy();
        
        try {
            return eventStorage.getOrDefault(aggregateType, Map.of())
                             .containsKey(aggregateId);
        } catch (Exception e) {
            throw new EventStoreException("Failed to check aggregate existence: " + e.getMessage(), e, OperationType.RETRIEVE);
        }
    }

    @Override
    public List<String> getAggregateTypes() throws EventStoreException {
        validateHealthy();
        
        try {
            return new ArrayList<>(eventStorage.keySet());
        } catch (Exception e) {
            throw new EventStoreException("Failed to get aggregate types: " + e.getMessage(), e, OperationType.RETRIEVE);
        }
    }

    @Override
    public List<String> getAggregateIds(@NonNull final String aggregateType) throws EventStoreException {
        validateHealthy();
        
        try {
            return eventStorage.getOrDefault(aggregateType, Map.of())
                             .keySet()
                             .stream()
                             .collect(Collectors.toList());
        } catch (Exception e) {
            throw new EventStoreException("Failed to get aggregate IDs: " + e.getMessage(), e, OperationType.RETRIEVE);
        }
    }

    @Override
    public Class<EventStorePort> adapts() {
        return EventStorePort.class;
    }

    // Test-specific methods for setup and cleanup

    /**
     * Clears all stored events and resets the store to initial state.
     * Useful for test isolation between test cases.
     */
    public void clearAllEvents() {
        eventStorage.clear();
        aggregateVersions.clear();
        globalEventLog.clear();
    }

    /**
     * Clears events for a specific aggregate.
     * @param aggregateType the type of aggregate to clear
     * @param aggregateId the ID of the aggregate to clear
     */
    public void clearEventsForAggregate(@NonNull final String aggregateType, @NonNull final String aggregateId) {
        Map<String, List<VersionedDomainEvent>> typeStorage = eventStorage.get(aggregateType);
        if (typeStorage != null) {
            List<VersionedDomainEvent> events = typeStorage.remove(aggregateId);
            if (events != null) {
                // Remove from global log as well
                globalEventLog.removeAll(events);
                
                // Clear version tracking
                String versionKey = aggregateType + ":" + aggregateId;
                aggregateVersions.remove(versionKey);
            }
        }
    }

    /**
     * Simulates store becoming unhealthy for testing error conditions.
     * @param healthy whether the store should be healthy
     */
    public void setHealthy(final boolean healthy) {
        this.healthy = healthy;
    }

    /**
     * Gets diagnostic information about the current state of the store.
     * @return diagnostic information for debugging tests
     */
    @NonNull
    public String getDiagnosticInfo() {
        StringBuilder info = new StringBuilder();
        info.append("InMemoryEventStoreAdapter Diagnostic Info:\n");
        info.append("  Created: ").append(createdAt).append("\n");
        info.append("  Healthy: ").append(healthy).append("\n");
        info.append("  Total Events: ").append(globalEventLog.size()).append("\n");
        info.append("  Aggregate Types: ").append(eventStorage.size()).append("\n");
        
        for (String aggregateType : eventStorage.keySet()) {
            Map<String, List<VersionedDomainEvent>> typeStorage = eventStorage.get(aggregateType);
            info.append("    ").append(aggregateType).append(": ").append(typeStorage.size()).append(" aggregates\n");
            
            for (String aggregateId : typeStorage.keySet()) {
                int eventCount = typeStorage.get(aggregateId).size();
                long currentVersion = aggregateVersions
                    .getOrDefault(aggregateType + ":" + aggregateId, new AtomicLong(0))
                    .get();
                info.append("      ").append(aggregateId).append(": ")
                    .append(eventCount).append(" events, version ").append(currentVersion).append("\n");
            }
        }
        
        return info.toString();
    }

    /**
     * Gets all events stored in the order they were added.
     * Useful for test assertions and debugging.
     * @return all events in insertion order
     */
    @NonNull
    public List<VersionedDomainEvent> getAllEventsInOrder() {
        return new ArrayList<>(globalEventLog);
    }

    /**
     * Checks if the store contains any events for the given aggregate.
     * @param aggregateType the aggregate type
     * @param aggregateId the aggregate ID
     * @return true if there are events for this aggregate
     */
    public boolean hasEventsForAggregate(@NonNull final String aggregateType, @NonNull final String aggregateId) {
        return eventStorage.getOrDefault(aggregateType, Map.of())
                         .getOrDefault(aggregateId, List.of())
                         .size() > 0;
    }

    /**
     * Gets the latest event for a specific aggregate.
     * @param aggregateType the aggregate type
     * @param aggregateId the aggregate ID
     * @return the latest event, or null if no events exist
     */
    @org.checkerframework.checker.nullness.qual.Nullable
    public VersionedDomainEvent getLatestEventForAggregate(
        @NonNull final String aggregateType,
        @NonNull final String aggregateId
    ) {
        List<VersionedDomainEvent> events = eventStorage
            .getOrDefault(aggregateType, Map.of())
            .getOrDefault(aggregateId, List.of());
        
        return events.stream()
            .max(Comparator.comparing(VersionedDomainEvent::getAggregateVersion))
            .orElse(null);
    }

    /**
     * Simulates a complete store failure for testing error handling.
     * All operations will throw exceptions until recovery is called.
     */
    public void simulateStoreFailure() {
        this.healthy = false;
    }

    /**
     * Recovers from a simulated store failure.
     */
    public void recoverFromFailure() {
        this.healthy = true;
    }

    /**
     * Validates that the store is healthy and throws an exception if not.
     * @throws EventStoreException if the store is not healthy
     */
    private void validateHealthy() throws EventStoreException {
        if (!healthy) {
            throw new EventStoreException("InMemoryEventStore is not healthy", OperationType.HEALTH_CHECK);
        }
    }
}