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
 * Filename: FilesystemEventStoreAdapterTest.java
 *
 * Author: Claude Code
 *
 * Class name: FilesystemEventStoreAdapterTest
 *
 * Responsibilities:
 *   - Test the FilesystemEventStoreAdapter implementation
 *   - Verify EventSourcing functionality works correctly
 *   - Demonstrate Milestone 6A capabilities
 *
 * Collaborators:
 *   - FilesystemEventStoreAdapter: The adapter being tested
 *   - ClassFileChanged: Enhanced event for testing
 */
package org.acmsl.bytehot.infrastructure.eventsourcing;

import org.acmsl.bytehot.domain.EventStoreException;
import org.acmsl.bytehot.domain.EventStorePort;
import org.acmsl.bytehot.domain.VersionedDomainEvent;
import org.acmsl.bytehot.domain.events.ClassFileChanged;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;

/**
 * Test class for FilesystemEventStoreAdapter
 * @author Claude Code
 * @since 2025-06-17
 */
class FilesystemEventStoreAdapterTest {

    @TempDir
    Path tempDir;

    private EventStorePort eventStore;

    @BeforeEach
    void setUp() {
        // Create event store with temporary directory
        eventStore = new FilesystemEventStoreAdapter(tempDir.toString());
    }

    @Test
    void shouldBeHealthyAfterInitialization() {
        // Given: Event store is initialized
        // When: Checking health
        boolean healthy = eventStore.isHealthy();
        
        // Then: Event store should be healthy
        assertThat(healthy).isTrue();
    }

    @Test
    void shouldSaveAndRetrieveEvent() throws EventStoreException {
        // Given: A valid domain event
        Path classFile = Paths.get("target/classes/MyClass.class");
        ClassFileChanged event = ClassFileChanged.forNewSession(
            classFile,
            "MyClass",
            1024L,
            Instant.now()
        );

        // When: Event is saved
        eventStore.save(event);

        // Then: Event can be retrieved
        List<VersionedDomainEvent> events = eventStore.getEventsForAggregate(
            "filewatch", 
            classFile.toString()
        );

        assertThat(events).hasSize(1);
        VersionedDomainEvent retrievedEvent = events.get(0);
        assertThat(retrievedEvent.getEventType()).isEqualTo("ClassFileChanged");
        assertThat(retrievedEvent.getAggregateType()).isEqualTo("filewatch");
        assertThat(retrievedEvent.getAggregateId()).isEqualTo(classFile.toString());
        assertThat(retrievedEvent.getAggregateVersion()).isEqualTo(1L);
    }

    @Test
    void shouldMaintainEventOrdering() throws EventStoreException {
        // Given: Multiple events for the same aggregate
        Path classFile = Paths.get("target/classes/OrderTest.class");
        
        ClassFileChanged event1 = ClassFileChanged.forNewSession(
            classFile, "OrderTest", 1024L, Instant.now()
        );
        
        ClassFileChanged event2 = ClassFileChanged.forExistingSession(
            classFile, "OrderTest", 2048L, Instant.now().plusSeconds(1),
            event1.getEventId(), 1L
        );
        
        ClassFileChanged event3 = ClassFileChanged.forExistingSession(
            classFile, "OrderTest", 3072L, Instant.now().plusSeconds(2),
            event2.getEventId(), 2L
        );

        // When: Events are saved in order
        eventStore.save(event1);
        eventStore.save(event2);
        eventStore.save(event3);

        // Then: Events are retrieved in chronological order
        List<VersionedDomainEvent> events = eventStore.getEventsForAggregate(
            "filewatch", 
            classFile.toString()
        );

        assertThat(events).hasSize(3);
        assertThat(events.get(0).getAggregateVersion()).isEqualTo(1L);
        assertThat(events.get(1).getAggregateVersion()).isEqualTo(2L);
        assertThat(events.get(2).getAggregateVersion()).isEqualTo(3L);
    }

    @Test
    void shouldTrackCurrentVersion() throws EventStoreException {
        // Given: An aggregate with multiple events
        Path classFile = Paths.get("target/classes/VersionTest.class");
        String aggregateType = "filewatch";
        String aggregateId = classFile.toString();

        // When: No events exist
        long initialVersion = eventStore.getCurrentVersion(aggregateType, aggregateId);

        // Then: Version should be 0
        assertThat(initialVersion).isEqualTo(0L);

        // When: First event is saved
        ClassFileChanged event1 = ClassFileChanged.forNewSession(
            classFile, "VersionTest", 1024L, Instant.now()
        );
        eventStore.save(event1);

        // Then: Version should be 1
        long versionAfterFirst = eventStore.getCurrentVersion(aggregateType, aggregateId);
        assertThat(versionAfterFirst).isEqualTo(1L);

        // When: Second event is saved
        ClassFileChanged event2 = ClassFileChanged.forExistingSession(
            classFile, "VersionTest", 2048L, Instant.now().plusSeconds(1),
            event1.getEventId(), 1L
        );
        eventStore.save(event2);

        // Then: Version should be 2
        long versionAfterSecond = eventStore.getCurrentVersion(aggregateType, aggregateId);
        assertThat(versionAfterSecond).isEqualTo(2L);
    }

    @Test
    void shouldCheckAggregateExistence() throws EventStoreException {
        // Given: An aggregate that doesn't exist
        String aggregateType = "filewatch";
        String aggregateId = "nonexistent/file.class";

        // When: Checking if aggregate exists
        boolean existsInitially = eventStore.aggregateExists(aggregateType, aggregateId);

        // Then: Should not exist
        assertThat(existsInitially).isFalse();

        // When: An event is saved for the aggregate
        Path classFile = Paths.get(aggregateId);
        ClassFileChanged event = ClassFileChanged.forNewSession(
            classFile, "NonExistent", 512L, Instant.now()
        );
        eventStore.save(event);

        // Then: Aggregate should now exist
        boolean existsAfterEvent = eventStore.aggregateExists(aggregateType, aggregateId);
        assertThat(existsAfterEvent).isTrue();
    }

    @Test
    void shouldCountEvents() throws EventStoreException {
        // Given: Multiple aggregates with different numbers of events
        Path file1 = Paths.get("target/classes/File1.class");
        Path file2 = Paths.get("target/classes/File2.class");
        
        // When: No events exist
        long initialTotal = eventStore.getTotalEventCount();
        assertThat(initialTotal).isEqualTo(0L);

        // When: Events are added to first aggregate
        ClassFileChanged event1a = ClassFileChanged.forNewSession(
            file1, "File1", 1024L, Instant.now()
        );
        ClassFileChanged event1b = ClassFileChanged.forExistingSession(
            file1, "File1", 2048L, Instant.now().plusSeconds(1),
            event1a.getEventId(), 1L
        );
        
        eventStore.save(event1a);
        eventStore.save(event1b);

        // When: Events are added to second aggregate
        ClassFileChanged event2a = ClassFileChanged.forNewSession(
            file2, "File2", 512L, Instant.now()
        );
        
        eventStore.save(event2a);

        // Then: Counts should be correct
        long file1Count = eventStore.getEventCountForAggregate("filewatch", file1.toString());
        long file2Count = eventStore.getEventCountForAggregate("filewatch", file2.toString());
        long totalCount = eventStore.getTotalEventCount();

        assertThat(file1Count).isEqualTo(2L);
        assertThat(file2Count).isEqualTo(1L);
        assertThat(totalCount).isEqualTo(3L);
    }

    @Test
    void shouldRetrieveEventsByType() throws EventStoreException {
        // Given: Multiple events of the same type across different aggregates
        Path file1 = Paths.get("target/classes/TypeTest1.class");
        Path file2 = Paths.get("target/classes/TypeTest2.class");
        
        ClassFileChanged event1 = ClassFileChanged.forNewSession(
            file1, "TypeTest1", 1024L, Instant.now()
        );
        ClassFileChanged event2 = ClassFileChanged.forNewSession(
            file2, "TypeTest2", 2048L, Instant.now().plusSeconds(1)
        );
        
        eventStore.save(event1);
        eventStore.save(event2);

        // When: Retrieving events by type
        List<VersionedDomainEvent> classFileChangedEvents = 
            eventStore.getEventsByType("ClassFileChanged");

        // Then: All events of that type should be returned
        assertThat(classFileChangedEvents).hasSize(2);
        assertThat(classFileChangedEvents.get(0).getTimestamp())
            .isBefore(classFileChangedEvents.get(1).getTimestamp());
    }

    @Test
    void shouldRetrieveEventsByTimeRange() throws EventStoreException {
        // Given: Events at different times
        Instant baseTime = Instant.now();
        Instant time1 = baseTime;
        Instant time2 = baseTime.plusSeconds(10);
        Instant time3 = baseTime.plusSeconds(20);
        
        Path file = Paths.get("target/classes/TimeTest.class");
        
        ClassFileChanged event1 = ClassFileChanged.forNewSession(
            file, "TimeTest", 1024L, time1
        );
        ClassFileChanged event2 = ClassFileChanged.forExistingSession(
            file, "TimeTest", 2048L, time2,
            event1.getEventId(), 1L
        );
        ClassFileChanged event3 = ClassFileChanged.forExistingSession(
            file, "TimeTest", 3072L, time3,
            event2.getEventId(), 2L
        );
        
        eventStore.save(event1);
        eventStore.save(event2);
        eventStore.save(event3);

        // When: Retrieving events in a specific time range
        List<VersionedDomainEvent> eventsInRange = eventStore.getEventsBetween(
            baseTime.plusSeconds(5), 
            baseTime.plusSeconds(15)
        );

        // Then: Only events within the range should be returned
        assertThat(eventsInRange).hasSize(1);
        assertThat(eventsInRange.get(0).getAggregateVersion()).isEqualTo(2L);
    }

    @Test
    void shouldGetAggregateTypesAndIds() throws EventStoreException {
        // Given: Events for different aggregate types
        Path file1 = Paths.get("target/classes/MetaTest1.class");
        Path file2 = Paths.get("target/classes/MetaTest2.class");
        
        ClassFileChanged event1 = ClassFileChanged.forNewSession(
            file1, "MetaTest1", 1024L, Instant.now()
        );
        ClassFileChanged event2 = ClassFileChanged.forNewSession(
            file2, "MetaTest2", 2048L, Instant.now()
        );
        
        eventStore.save(event1);
        eventStore.save(event2);

        // When: Getting aggregate metadata
        List<String> aggregateTypes = eventStore.getAggregateTypes();
        List<String> filewatchAggregateIds = eventStore.getAggregateIds("filewatch");

        // Then: Metadata should be correct
        assertThat(aggregateTypes).contains("filewatch");
        assertThat(filewatchAggregateIds).contains(file1.toString(), file2.toString());
    }

    @Test
    void shouldRetrieveEventsSinceVersion() throws EventStoreException {
        // Given: Multiple events for an aggregate
        Path file = Paths.get("target/classes/SinceTest.class");
        
        ClassFileChanged event1 = ClassFileChanged.forNewSession(
            file, "SinceTest", 1024L, Instant.now()
        );
        ClassFileChanged event2 = ClassFileChanged.forExistingSession(
            file, "SinceTest", 2048L, Instant.now().plusSeconds(1),
            event1.getEventId(), 1L
        );
        ClassFileChanged event3 = ClassFileChanged.forExistingSession(
            file, "SinceTest", 3072L, Instant.now().plusSeconds(2),
            event2.getEventId(), 2L
        );
        
        eventStore.save(event1);
        eventStore.save(event2);
        eventStore.save(event3);

        // When: Retrieving events since version 1
        List<VersionedDomainEvent> eventsSinceV1 = eventStore.getEventsForAggregateSince(
            "filewatch", 
            file.toString(), 
            1L
        );

        // Then: Only events after version 1 should be returned
        assertThat(eventsSinceV1).hasSize(2);
        assertThat(eventsSinceV1.get(0).getAggregateVersion()).isEqualTo(2L);
        assertThat(eventsSinceV1.get(1).getAggregateVersion()).isEqualTo(3L);
    }

    @Test 
    void shouldDemonstrateEventSourcingCapabilities() throws EventStoreException {
        // This test demonstrates the complete EventSourcing capabilities of Milestone 6A
        
        // Given: A file being monitored over time with multiple changes
        Path monitoredFile = Paths.get("src/main/java/com/example/Service.class");
        String className = "Service";
        
        // Event 1: File first created/detected
        ClassFileChanged creation = ClassFileChanged.forNewSession(
            monitoredFile, className, 1024L, Instant.now()
        );
        eventStore.save(creation);
        
        // Event 2: File modified (method added)
        ClassFileChanged modification1 = ClassFileChanged.forExistingSession(
            monitoredFile, className, 1536L, Instant.now().plusSeconds(30),
            creation.getEventId(), 1L
        );
        eventStore.save(modification1);
        
        // Event 3: File modified again (refactoring)
        ClassFileChanged modification2 = ClassFileChanged.forExistingSession(
            monitoredFile, className, 1792L, Instant.now().plusSeconds(60),
            modification1.getEventId(), 2L
        );
        eventStore.save(modification2);
        
        // When: Reconstructing the complete history
        List<VersionedDomainEvent> completeHistory = eventStore.getEventsForAggregate(
            "filewatch", 
            monitoredFile.toString()
        );
        
        // Then: Complete audit trail is available
        assertThat(completeHistory).hasSize(3);
        
        // Verify chronological order
        assertThat(completeHistory.get(0).getAggregateVersion()).isEqualTo(1L);
        assertThat(completeHistory.get(1).getAggregateVersion()).isEqualTo(2L);
        assertThat(completeHistory.get(2).getAggregateVersion()).isEqualTo(3L);
        
        // Verify causality chain
        assertThat(completeHistory.get(0).getPreviousEventId()).isNull(); // First event
        assertThat(completeHistory.get(1).getPreviousEventId()).isEqualTo(creation.getEventId());
        assertThat(completeHistory.get(2).getPreviousEventId()).isEqualTo(modification1.getEventId());
        
        // Verify we can reconstruct state at any point in time
        List<VersionedDomainEvent> stateAtVersion2 = eventStore.getEventsForAggregateSince(
            "filewatch", 
            monitoredFile.toString(), 
            0L
        ).stream()
        .filter(event -> event.getAggregateVersion() <= 2L)
        .toList();
        
        assertThat(stateAtVersion2).hasSize(2);
        
        // Verify metadata is properly stored
        VersionedDomainEvent latestEvent = completeHistory.get(2);
        assertThat(latestEvent.getEventId()).isNotNull();
        assertThat(latestEvent.getTimestamp()).isNotNull();
        assertThat(latestEvent.getAggregateType()).isEqualTo("filewatch");
        assertThat(latestEvent.getAggregateId()).isEqualTo(monitoredFile.toString());
        assertThat(latestEvent.getSchemaVersion()).isEqualTo(1);
        
        // Verify current version tracking
        long currentVersion = eventStore.getCurrentVersion("filewatch", monitoredFile.toString());
        assertThat(currentVersion).isEqualTo(3L);
        
        // Verify aggregate existence
        boolean exists = eventStore.aggregateExists("filewatch", monitoredFile.toString());
        assertThat(exists).isTrue();
        
        System.out.println("✅ Milestone 6A EventSourcing capabilities successfully demonstrated!");
        System.out.println("   - Event persistence and retrieval: ✅");
        System.out.println("   - Event ordering and versioning: ✅");
        System.out.println("   - Causality chain tracking: ✅");
        System.out.println("   - Aggregate state reconstruction: ✅");
        System.out.println("   - Time-based event queries: ✅");
        System.out.println("   - Complete audit trail: ✅");
    }
}