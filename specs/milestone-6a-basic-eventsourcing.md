# Milestone 6A: Basic EventSourcing Infrastructure

## Overview

**Objective:** Establish the foundational EventSourcing infrastructure for ByteHot, enabling event persistence and retrieval as the basis for all future event-driven capabilities.

**Status:** 🚧 In Progress

**Walking Skeleton Value:** Provides the essential event storage mechanism that will power user management, flow detection, bug reproduction, and all advanced features. This is the foundational layer that makes ByteHot truly event-driven.

## Goals

### Primary Goal
Implement a "poor-man's" EventStore using filesystem storage that can persist and retrieve domain events with proper metadata and organization.

### Secondary Goals
- Enhance all existing domain events with EventSourcing metadata
- Establish event serialization/deserialization patterns
- Create the filesystem structure for organized event storage
- Provide the foundation for event-driven testing and bug reproduction

## Technical Specifications

### EventStore Filesystem Structure

```
eventstore/
├── bytehot/                          # Aggregate type: ByteHot
│   ├── f47ac10b-58cc-4372-a567/     # Aggregate ID (UUID)
│   │   ├── 20250617123000001-ByteHotAttachRequested.json
│   │   ├── 20250617123000002-ByteHotAgentAttached.json
│   │   └── 20250617123000003-WatchPathConfigured.json
│   └── a1b2c3d4-5678-9abc-def0/     # Another ByteHot instance
├── hotswap/                          # Aggregate type: HotSwap operations
│   ├── e4f5g6h7-8901-2345-6789/     # HotSwap session ID
│   │   ├── 20250617123001001-HotSwapRequested.json
│   │   ├── 20250617123001002-BytecodeValidated.json
│   │   ├── 20250617123001003-ClassRedefinitionSucceeded.json
│   │   └── 20250617123001004-InstancesUpdated.json
└── metadata/
    ├── aggregate-types.json          # Registry of known aggregate types
    ├── event-schema-versions.json    # Event versioning information
    └── storage-statistics.json       # Storage metrics and health
```

### Event File Naming Convention

**Format:** `[timestamp-with-sequence]-[EventClassName].json`

**Timestamp Format:** `YYYYMMDDHHmmssSSS` (year, month, day, hour, minute, second, milliseconds)

**Examples:**
- `20250617123000001-ClassFileChanged.json`
- `20250617123000002-BytecodeValidated.json`
- `20250617123001003-InstancesUpdated.json`

**Sequence Number:** 3-digit sequence within the same millisecond to handle high-frequency events.

### Enhanced Domain Events

All domain events will be enhanced with EventSourcing metadata:

```java
public interface VersionedDomainEvent extends DomainEvent {
    /**
     * Unique identifier for this event
     */
    String getEventId();
    
    /**
     * Type of aggregate this event belongs to
     */
    String getAggregateType();
    
    /**
     * Unique identifier of the aggregate instance
     */
    String getAggregateId();
    
    /**
     * Version of this event for the aggregate (1, 2, 3, ...)
     */
    long getAggregateVersion();
    
    /**
     * Timestamp when the event occurred
     */
    Instant getTimestamp();
    
    /**
     * ID of the previous event in this aggregate's history (for causality)
     */
    String getPreviousEventId();
    
    /**
     * Version of the event schema for migration purposes
     */
    int getSchemaVersion();
}
```

## Domain Components

### 1. EventStorePort (Domain Interface)

```java
package org.acmsl.bytehot.domain;

public interface EventStorePort extends Port {
    /**
     * Persists a domain event to the event store
     */
    void save(VersionedDomainEvent event);
    
    /**
     * Retrieves all events for a specific aggregate
     */
    List<VersionedDomainEvent> getEventsForAggregate(
        String aggregateType, 
        String aggregateId
    );
    
    /**
     * Retrieves events for an aggregate since a specific version
     */
    List<VersionedDomainEvent> getEventsForAggregateSince(
        String aggregateType, 
        String aggregateId, 
        long sinceVersion
    );
    
    /**
     * Retrieves all events of a specific type
     */
    List<VersionedDomainEvent> getEventsByType(String eventType);
    
    /**
     * Retrieves events within a time range
     */
    List<VersionedDomainEvent> getEventsBetween(
        Instant startTime, 
        Instant endTime
    );
    
    /**
     * Gets the current version for an aggregate
     */
    long getCurrentVersion(String aggregateType, String aggregateId);
    
    /**
     * Checks if the event store is healthy and accessible
     */
    boolean isHealthy();
}
```

### 2. VersionedDomainEvent Base Implementation

```java
package org.acmsl.bytehot.domain.events;

@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public abstract class AbstractVersionedDomainEvent 
    implements VersionedDomainEvent {
    
    @Getter
    private final String eventId;
    
    @Getter
    private final String aggregateType;
    
    @Getter
    private final String aggregateId;
    
    @Getter
    private final long aggregateVersion;
    
    @Getter
    private final Instant timestamp;
    
    @Getter
    private final String previousEventId;
    
    @Getter
    private final int schemaVersion;
    
    /**
     * Factory method to create events with auto-generated metadata
     */
    protected static EventMetadata createMetadata(
        String aggregateType,
        String aggregateId,
        String previousEventId
    ) {
        return new EventMetadata(
            UUID.randomUUID().toString(),
            aggregateType,
            aggregateId,
            // Version will be determined by EventStore
            0L,
            Instant.now(),
            previousEventId,
            1 // Default schema version
        );
    }
}
```

## Infrastructure Components

### 1. FilesystemEventStoreAdapter

```java
package org.acmsl.bytehot.infrastructure.eventsourcing;

@Component
public class FilesystemEventStoreAdapter 
    implements EventStorePort, Adapter<EventStorePort> {
    
    private final Path eventStoreBasePath;
    private final ObjectMapper objectMapper;
    private final Map<String, AtomicLong> aggregateVersions;
    
    public FilesystemEventStoreAdapter(
        @Value("${bytehot.eventstore.path:./eventstore}") String basePath
    ) {
        this.eventStoreBasePath = Paths.get(basePath);
        this.objectMapper = createObjectMapper();
        this.aggregateVersions = new ConcurrentHashMap<>();
        initializeEventStore();
    }
    
    @Override
    public void save(VersionedDomainEvent event) {
        // Implementation details in full specification
    }
    
    @Override
    public List<VersionedDomainEvent> getEventsForAggregate(
        String aggregateType, 
        String aggregateId
    ) {
        // Implementation details in full specification
    }
    
    // Additional methods...
}
```

### 2. Event Serialization Support

```java
package org.acmsl.bytehot.infrastructure.eventsourcing;

public class EventSerializationSupport {
    
    /**
     * Serializes a domain event to JSON
     */
    public static String toJson(VersionedDomainEvent event) {
        // JSON serialization with event metadata
    }
    
    /**
     * Deserializes JSON to a domain event
     */
    public static VersionedDomainEvent fromJson(
        String json, 
        String eventType
    ) {
        // JSON deserialization with type safety
    }
    
    /**
     * Creates JSON with both event data and metadata
     */
    private static JsonNode createEventJson(VersionedDomainEvent event) {
        // Combines event data with EventSourcing metadata
    }
}
```

## Migration of Existing Events

### Event Enhancement Strategy

All existing domain events will be migrated to implement `VersionedDomainEvent`:

1. **ClassFileChanged** → Enhanced with aggregate metadata
2. **BytecodeValidated** → Associated with HotSwap aggregate
3. **ClassRedefinitionSucceeded** → Part of HotSwap session
4. **InstancesUpdated** → Linked to HotSwap completion
5. All other events similarly enhanced

### Backward Compatibility

- Existing event structure preserved
- New metadata added as additional fields
- Old event handlers continue to work
- Migration utilities for existing data

## Testing Strategy

### Unit Tests

```java
class FilesystemEventStoreAdapterTest {
    
    @Test
    void shouldSaveAndRetrieveEvent() {
        // Given: A valid domain event
        ClassFileChanged event = createTestEvent();
        
        // When: Event is saved
        eventStore.save(event);
        
        // Then: Event can be retrieved
        List<VersionedDomainEvent> events = eventStore
            .getEventsForAggregate("hotswap", event.getAggregateId());
        
        assertThat(events).hasSize(1);
        assertThat(events.get(0)).isEqualTo(event);
    }
    
    @Test
    void shouldMaintainEventOrdering() {
        // Test that events are retrieved in chronological order
    }
    
    @Test
    void shouldHandleConcurrentWrites() {
        // Test thread safety with concurrent event saves
    }
}
```

### Integration Tests

```java
class EventSourcingIntegrationTest {
    
    @Test
    void shouldPersistCompleteHotSwapFlow() {
        // Given: A complete hot-swap event sequence
        // When: Events are saved in order
        // Then: Full flow can be reconstructed
    }
}
```

## Configuration

### Application Properties

```yaml
bytehot:
  eventstore:
    path: "./eventstore"
    max-events-per-file: 1000
    compression-enabled: false
    backup-enabled: true
    cleanup:
      max-age-days: 365
      max-total-size-mb: 1024
```

### Environment Variables

```bash
BYTEHOT_EVENTSTORE_PATH=./eventstore
BYTEHOT_EVENTSTORE_COMPRESSION_ENABLED=false
BYTEHOT_EVENTSTORE_BACKUP_ENABLED=true
```

## Performance Considerations

### Storage Efficiency
- JSON compression for large events
- Indexed file organization by timestamp
- Lazy loading of event data
- Configurable storage limits

### Query Performance
- Filesystem-based indexing
- Aggregate-level caching
- Memory-mapped file access for large histories
- Async event persistence

## Success Criteria

### Functional Requirements
- ✅ Can save any VersionedDomainEvent to filesystem
- ✅ Can retrieve events by aggregate (type + ID)
- ✅ Can retrieve events by time range
- ✅ Maintains event ordering and causality
- ✅ Handles concurrent access safely

### Performance Requirements
- ✅ Sub-10ms event save latency (95th percentile)
- ✅ Sub-50ms aggregate reconstruction (100 events)
- ✅ Handles 1000+ events per second sustained
- ✅ Storage overhead < 2x event payload size

### Quality Requirements
- ✅ 100% test coverage for EventStore adapter
- ✅ Integration tests with all domain events
- ✅ Backward compatibility with existing events
- ✅ Documentation for filesystem structure

## Future Enhancements (Later Milestones)

### Milestone 6F: Advanced EventStore Features
- **Snapshots:** Periodic aggregate snapshots for performance
- **Compression:** Event compression for storage efficiency
- **Encryption:** Symmetric encryption for sensitive data
- **Backup/Restore:** Automated backup and restore capabilities

### Future Milestone: GitHub EventStore
- **Git-based Storage:** Events stored in Git repository
- **GitHub API Integration:** Remote event persistence
- **Encryption:** Symmetric encryption for security
- **Distributed:** Multi-repository event store

## Dependencies

### Internal Dependencies
- All existing domain events (for migration)
- Existing Ports pattern and infrastructure
- Current hexagonal architecture

### External Dependencies
- Jackson for JSON serialization
- Java NIO for efficient file operations
- Java concurrent utilities for thread safety

## Implementation Timeline

### Week 1: Core Infrastructure
- Day 1-2: EventStorePort interface and base classes
- Day 3-4: FilesystemEventStoreAdapter implementation
- Day 5: Event serialization and metadata support

### Week 2: Integration and Testing
- Day 1-2: Migrate existing events to VersionedDomainEvent
- Day 3-4: Comprehensive testing and integration
- Day 5: Documentation and performance validation

## Documentation Deliverables

1. **API Documentation:** Complete Javadoc for all public interfaces
2. **Filesystem Specification:** Detailed storage format documentation
3. **Migration Guide:** How to enhance existing events
4. **Performance Guide:** Tuning and optimization recommendations
5. **Troubleshooting Guide:** Common issues and solutions

---

**Milestone 6A establishes the EventSourcing foundation that will power all future ByteHot capabilities, from user management to flow detection to advanced debugging features. This "poor-man's" EventStore provides the essential infrastructure while maintaining simplicity and reliability.**