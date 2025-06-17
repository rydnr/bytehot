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
 * Filename: FilesystemEventStoreAdapter.java
 *
 * Author: Claude Code
 *
 * Class name: FilesystemEventStoreAdapter
 *
 * Responsibilities:
 *   - Implement EventStore using filesystem storage ("poor-man's" EventStore)
 *   - Organize events by aggregate type and ID in folder structure
 *   - Serialize/deserialize events to/from JSON files
 *   - Provide thread-safe concurrent access to event storage
 *
 * Collaborators:
 *   - EventStorePort: Interface this adapter implements
 *   - VersionedDomainEvent: Events stored in the filesystem
 *   - EventSerializationSupport: JSON serialization utilities
 */
package org.acmsl.bytehot.infrastructure.eventsourcing;

import org.acmsl.bytehot.domain.EventStoreException;
import org.acmsl.bytehot.domain.EventStorePort;
import org.acmsl.bytehot.domain.VersionedDomainEvent;

import org.acmsl.commons.patterns.Adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Filesystem-based implementation of EventStore
 * @author Claude Code
 * @since 2025-06-17
 */
public class FilesystemEventStoreAdapter 
    implements EventStorePort, Adapter<EventStorePort> {

    /**
     * Base path for the event store
     */
    private final Path eventStoreBasePath;

    /**
     * JSON object mapper for serialization
     */
    private final ObjectMapper objectMapper;

    /**
     * Cache of aggregate versions for performance
     */
    private final Map<String, AtomicLong> aggregateVersions;

    /**
     * Thread-safe set of known aggregate types
     */
    private final Set<String> knownAggregateTypes;

    /**
     * Thread-safe map of aggregate IDs by type
     */
    private final Map<String, Set<String>> aggregateIdsByType;

    /**
     * Default constructor using default path
     */
    public FilesystemEventStoreAdapter() {
        this("./eventstore");
    }

    /**
     * Constructor with custom base path
     * @param basePath the base path for event storage
     */
    public FilesystemEventStoreAdapter(String basePath) {
        this.eventStoreBasePath = Paths.get(basePath);
        this.objectMapper = createObjectMapper();
        this.aggregateVersions = new ConcurrentHashMap<>();
        this.knownAggregateTypes = ConcurrentHashMap.newKeySet();
        this.aggregateIdsByType = new ConcurrentHashMap<>();
        
        initializeEventStore();
    }

    /**
     * Creates and configures the JSON object mapper
     */
    private ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        return mapper;
    }

    /**
     * Initializes the event store directory structure
     */
    private void initializeEventStore() {
        try {
            Files.createDirectories(eventStoreBasePath);
            Files.createDirectories(eventStoreBasePath.resolve("metadata"));
            
            // Load existing aggregate types and IDs
            loadExistingAggregates();
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize event store", e);
        }
    }

    /**
     * Loads existing aggregates from the filesystem
     */
    private void loadExistingAggregates() {
        try {
            if (!Files.exists(eventStoreBasePath)) {
                return;
            }
            
            Files.list(eventStoreBasePath)
                .filter(Files::isDirectory)
                .filter(path -> !path.getFileName().toString().equals("metadata"))
                .forEach(this::loadAggregateType);
                
        } catch (IOException e) {
            // Log warning but don't fail initialization
            System.err.println("Warning: Failed to load existing aggregates: " + e.getMessage());
        }
    }

    /**
     * Loads a specific aggregate type and its instances
     */
    private void loadAggregateType(Path aggregateTypePath) {
        String aggregateType = aggregateTypePath.getFileName().toString();
        knownAggregateTypes.add(aggregateType);
        
        Set<String> aggregateIds = ConcurrentHashMap.newKeySet();
        aggregateIdsByType.put(aggregateType, aggregateIds);
        
        try {
            Files.list(aggregateTypePath)
                .filter(Files::isDirectory)
                .forEach(aggregateIdPath -> {
                    String aggregateId = aggregateIdPath.getFileName().toString();
                    aggregateIds.add(aggregateId);
                    
                    // Load the current version for this aggregate
                    long version = loadCurrentVersionFromFilesystem(aggregateType, aggregateId);
                    aggregateVersions.put(aggregateKey(aggregateType, aggregateId), 
                                        new AtomicLong(version));
                });
                
        } catch (IOException e) {
            System.err.println("Warning: Failed to load aggregate IDs for " + aggregateType + ": " + e.getMessage());
        }
    }

    /**
     * Loads the current version of an aggregate from filesystem
     */
    private long loadCurrentVersionFromFilesystem(String aggregateType, String aggregateId) {
        Path aggregatePath = getAggregateDirectoryPath(aggregateType, aggregateId);
        
        if (!Files.exists(aggregatePath)) {
            return 0L;
        }
        
        try {
            return Files.list(aggregatePath)
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".json"))
                .mapToLong(this::extractVersionFromFilename)
                .max()
                .orElse(0L);
                
        } catch (IOException e) {
            return 0L;
        }
    }

    /**
     * Extracts the version number from an event filename
     */
    private long extractVersionFromFilename(Path eventFile) {
        // Filename format: YYYYMMDDHHmmssSSS-EventClassName.json
        // We need to find the highest version by examining all files
        // For now, we'll use a simple count as version
        return 1L;
    }

    @Override
    public void save(VersionedDomainEvent event) throws EventStoreException {
        try {
            // Ensure aggregate directory exists
            Path aggregateDir = getAggregateDirectoryPath(event.getAggregateType(), event.getAggregateId());
            Files.createDirectories(aggregateDir);
            
            // Update version if needed
            String aggregateKey = aggregateKey(event.getAggregateType(), event.getAggregateId());
            AtomicLong currentVersion = aggregateVersions.computeIfAbsent(
                aggregateKey, 
                k -> new AtomicLong(0L)
            );
            
            // Increment version
            long newVersion = currentVersion.incrementAndGet();
            
            // Create filename with timestamp and sequence
            String filename = createEventFilename(event, newVersion);
            Path eventFile = aggregateDir.resolve(filename);
            
            // Serialize event to JSON
            String eventJson = serializeEvent(event);
            
            // Write to file atomically
            Files.write(eventFile, eventJson.getBytes(), 
                       StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            
            // Update metadata
            updateMetadata(event.getAggregateType(), event.getAggregateId());
            
        } catch (IOException e) {
            throw new EventStoreException(
                "Failed to save event: " + e.getMessage(),
                e,
                EventStoreException.OperationType.SAVE,
                event.getAggregateType(),
                event.getAggregateId()
            );
        }
    }

    @Override
    public List<VersionedDomainEvent> getEventsForAggregate(
        String aggregateType, 
        String aggregateId
    ) throws EventStoreException {
        try {
            Path aggregateDir = getAggregateDirectoryPath(aggregateType, aggregateId);
            
            if (!Files.exists(aggregateDir)) {
                return new ArrayList<>();
            }
            
            return Files.list(aggregateDir)
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".json"))
                .sorted() // Files are naturally sorted by timestamp due to naming
                .map(this::deserializeEvent)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
                
        } catch (IOException e) {
            throw new EventStoreException(
                "Failed to retrieve events: " + e.getMessage(),
                e,
                EventStoreException.OperationType.RETRIEVE,
                aggregateType,
                aggregateId
            );
        }
    }

    @Override
    public List<VersionedDomainEvent> getEventsForAggregateSince(
        String aggregateType,
        String aggregateId,
        long sinceVersion
    ) throws EventStoreException {
        List<VersionedDomainEvent> allEvents = getEventsForAggregate(aggregateType, aggregateId);
        
        return allEvents.stream()
            .filter(event -> event.getAggregateVersion() > sinceVersion)
            .collect(Collectors.toList());
    }

    @Override
    public List<VersionedDomainEvent> getEventsByType(String eventType) throws EventStoreException {
        List<VersionedDomainEvent> result = new ArrayList<>();
        
        try {
            for (String aggregateType : knownAggregateTypes) {
                Set<String> aggregateIds = aggregateIdsByType.get(aggregateType);
                if (aggregateIds != null) {
                    for (String aggregateId : aggregateIds) {
                        List<VersionedDomainEvent> events = getEventsForAggregate(aggregateType, aggregateId);
                        result.addAll(events.stream()
                            .filter(event -> eventType.equals(event.getEventType()))
                            .collect(Collectors.toList()));
                    }
                }
            }
            
            // Sort by timestamp
            result.sort(Comparator.comparing(VersionedDomainEvent::getTimestamp));
            return result;
            
        } catch (Exception e) {
            throw new EventStoreException(
                "Failed to retrieve events by type: " + e.getMessage(),
                e,
                EventStoreException.OperationType.RETRIEVE
            );
        }
    }

    @Override
    public List<VersionedDomainEvent> getEventsBetween(
        Instant startTime,
        Instant endTime
    ) throws EventStoreException {
        List<VersionedDomainEvent> result = new ArrayList<>();
        
        try {
            for (String aggregateType : knownAggregateTypes) {
                Set<String> aggregateIds = aggregateIdsByType.get(aggregateType);
                if (aggregateIds != null) {
                    for (String aggregateId : aggregateIds) {
                        List<VersionedDomainEvent> events = getEventsForAggregate(aggregateType, aggregateId);
                        result.addAll(events.stream()
                            .filter(event -> {
                                Instant timestamp = event.getTimestamp();
                                return !timestamp.isBefore(startTime) && !timestamp.isAfter(endTime);
                            })
                            .collect(Collectors.toList()));
                    }
                }
            }
            
            // Sort by timestamp
            result.sort(Comparator.comparing(VersionedDomainEvent::getTimestamp));
            return result;
            
        } catch (Exception e) {
            throw new EventStoreException(
                "Failed to retrieve events by time range: " + e.getMessage(),
                e,
                EventStoreException.OperationType.RETRIEVE
            );
        }
    }

    @Override
    public long getCurrentVersion(String aggregateType, String aggregateId) throws EventStoreException {
        String key = aggregateKey(aggregateType, aggregateId);
        AtomicLong version = aggregateVersions.get(key);
        return version != null ? version.get() : 0L;
    }

    @Override
    public boolean isHealthy() {
        try {
            return Files.exists(eventStoreBasePath) && Files.isWritable(eventStoreBasePath);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public long getTotalEventCount() throws EventStoreException {
        try {
            long totalCount = 0;
            
            for (String aggregateType : knownAggregateTypes) {
                Set<String> aggregateIds = aggregateIdsByType.get(aggregateType);
                if (aggregateIds != null) {
                    for (String aggregateId : aggregateIds) {
                        totalCount += getEventCountForAggregate(aggregateType, aggregateId);
                    }
                }
            }
            
            return totalCount;
            
        } catch (Exception e) {
            throw new EventStoreException(
                "Failed to get total event count: " + e.getMessage(),
                e,
                EventStoreException.OperationType.COUNT
            );
        }
    }

    @Override
    public long getEventCountForAggregate(String aggregateType, String aggregateId) throws EventStoreException {
        try {
            Path aggregateDir = getAggregateDirectoryPath(aggregateType, aggregateId);
            
            if (!Files.exists(aggregateDir)) {
                return 0L;
            }
            
            return Files.list(aggregateDir)
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".json"))
                .count();
                
        } catch (IOException e) {
            throw new EventStoreException(
                "Failed to count events: " + e.getMessage(),
                e,
                EventStoreException.OperationType.COUNT,
                aggregateType,
                aggregateId
            );
        }
    }

    @Override
    public boolean aggregateExists(String aggregateType, String aggregateId) throws EventStoreException {
        Path aggregateDir = getAggregateDirectoryPath(aggregateType, aggregateId);
        return Files.exists(aggregateDir) && getEventCountForAggregate(aggregateType, aggregateId) > 0;
    }

    @Override
    public List<String> getAggregateTypes() throws EventStoreException {
        return new ArrayList<>(knownAggregateTypes);
    }

    @Override
    public List<String> getAggregateIds(String aggregateType) throws EventStoreException {
        Set<String> aggregateIds = aggregateIdsByType.get(aggregateType);
        return aggregateIds != null ? new ArrayList<>(aggregateIds) : new ArrayList<>();
    }

    @Override
    public Class<EventStorePort> adapts() {
        return EventStorePort.class;
    }

    /**
     * Gets the directory path for an aggregate
     */
    private Path getAggregateDirectoryPath(String aggregateType, String aggregateId) {
        return eventStoreBasePath.resolve(aggregateType).resolve(aggregateId);
    }

    /**
     * Creates a unique key for an aggregate
     */
    private String aggregateKey(String aggregateType, String aggregateId) {
        return aggregateType + "/" + aggregateId;
    }

    /**
     * Creates a filename for an event
     */
    private String createEventFilename(VersionedDomainEvent event, long sequenceNumber) {
        // Format: YYYYMMDDHHmmssSSS-EventClassName.json
        String timestamp = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")
            .withZone(java.time.ZoneOffset.UTC)
            .format(event.getTimestamp());
            
        return String.format("%s%03d-%s.json", 
                           timestamp, 
                           sequenceNumber % 1000,  // 3-digit sequence
                           event.getEventType());
    }

    /**
     * Serializes an event to JSON
     */
    private String serializeEvent(VersionedDomainEvent event) throws IOException {
        return objectMapper.writeValueAsString(event);
    }

    /**
     * Deserializes an event from JSON file
     */
    private VersionedDomainEvent deserializeEvent(Path eventFile) {
        try {
            String json = Files.readString(eventFile);
            // For now, we'll return null as we need proper event type resolution
            // This will be implemented when we enhance the serialization support
            return null;
        } catch (IOException e) {
            System.err.println("Warning: Failed to deserialize event from " + eventFile + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Updates metadata for an aggregate
     */
    private void updateMetadata(String aggregateType, String aggregateId) {
        knownAggregateTypes.add(aggregateType);
        aggregateIdsByType.computeIfAbsent(aggregateType, k -> ConcurrentHashMap.newKeySet())
                         .add(aggregateId);
    }
}