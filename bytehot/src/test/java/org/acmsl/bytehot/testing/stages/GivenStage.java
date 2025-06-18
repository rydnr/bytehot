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
 * Filename: GivenStage.java
 *
 * Author: Claude Code
 *
 * Class name: GivenStage
 *
 * Responsibilities:
 *   - Build system state from domain events for testing
 *   - Support realistic test scenarios using actual event sequences
 *   - Enable loading of saved test scenarios and bug reproduction contexts
 *   - Provide fluent interface for event-driven test setup
 *
 * Collaborators:
 *   - EventTestContext: Records the events used to build state
 *   - EventStorePort: Stores events for later retrieval and replay
 *   - VersionedDomainEvent: Events that build system state
 */
package org.acmsl.bytehot.testing.stages;

import org.acmsl.bytehot.domain.EventStorePort;
import org.acmsl.bytehot.domain.VersionedDomainEvent;
import org.acmsl.bytehot.testing.support.BugReport;
import org.acmsl.bytehot.testing.support.EventTestContext;
import org.acmsl.bytehot.testing.support.TestScenarioRepository;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

/**
 * The "Given" stage of event-driven testing that builds system state from events.
 * This revolutionary approach allows tests to establish realistic conditions by
 * replaying actual domain events rather than mocking objects or setting up
 * artificial state.
 * 
 * @author Claude Code
 * @since 2025-06-17
 */
public class GivenStage {

    /**
     * Test context to record the prior events
     */
    private final EventTestContext context;

    /**
     * Event store for persisting setup events
     */
    private final EventStorePort eventStore;

    /**
     * Creates a new Given stage.
     * 
     * @param context the test context to record events
     * @param eventStore the event store for persistence
     */
    public GivenStage(EventTestContext context, EventStorePort eventStore) {
        this.context = context;
        this.eventStore = eventStore;
    }

    /**
     * Adds a prior event to build system state.
     * This event will be persisted and recorded as part of the test setup.
     * 
     * @param event the prior event to add
     * @return this stage for fluent chaining
     */
    public GivenStage event(VersionedDomainEvent event) {
        try {
            // Store the event in the event store
            eventStore.save(event);
            
            // Record it in the test context
            context.recordPriorEvent(event);
            
            return this;
        } catch (Exception e) {
            throw new RuntimeException("Failed to save prior event: " + e.getMessage(), e);
        }
    }

    /**
     * Adds multiple events in sequence to build system state.
     * This is equivalent to calling event() for each event individually.
     * 
     * @param events the events to add in order
     * @return this stage for fluent chaining
     */
    public GivenStage events(VersionedDomainEvent... events) {
        Arrays.stream(events).forEach(this::event);
        return this;
    }

    /**
     * Adds a list of events in sequence to build system state.
     * 
     * @param events the list of events to add
     * @return this stage for fluent chaining
     */
    public GivenStage events(List<VersionedDomainEvent> events) {
        events.forEach(this::event);
        return this;
    }

    /**
     * Loads events from a saved test scenario.
     * This allows reusing common test setups and sharing scenarios
     * between different tests.
     * 
     * @param scenarioName the name of the saved scenario
     * @return this stage for fluent chaining
     */
    public GivenStage scenario(String scenarioName) {
        try {
            List<VersionedDomainEvent> scenarioEvents = 
                TestScenarioRepository.load(scenarioName);
            
            scenarioEvents.forEach(this::event);
            return this;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load scenario '" + scenarioName + "': " + e.getMessage(), e);
        }
    }

    /**
     * Builds system state to a specific point in time.
     * This loads all events from the event store that occurred before
     * the specified timestamp.
     * 
     * @param timestamp the cutoff time for events
     * @return this stage for fluent chaining
     */
    public GivenStage eventsUntil(Instant timestamp) {
        try {
            // Load all events up to the timestamp
            List<VersionedDomainEvent> historicalEvents = 
                eventStore.getEventsBetween(Instant.EPOCH, timestamp);
            
            historicalEvents.forEach(this::event);
            return this;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load events until " + timestamp + ": " + e.getMessage(), e);
        }
    }

    /**
     * Builds system state from the events between two timestamps.
     * This allows testing scenarios from specific time periods.
     * 
     * @param start the start time (inclusive)
     * @param end the end time (inclusive)
     * @return this stage for fluent chaining
     */
    public GivenStage eventsBetween(Instant start, Instant end) {
        try {
            List<VersionedDomainEvent> rangeEvents = 
                eventStore.getEventsBetween(start, end);
            
            rangeEvents.forEach(this::event);
            return this;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load events between " + start + " and " + end + ": " + e.getMessage(), e);
        }
    }

    /**
     * Builds system state from a bug report context.
     * This revolutionary feature allows bugs to be reproduced by replaying
     * the exact event sequence that led to the error.
     * 
     * @param bugReport the bug report containing reproduction events
     * @return this stage for fluent chaining
     */
    public GivenStage bugContext(BugReport bugReport) {
        bugReport.getReproductionEvents().forEach(this::event);
        return this;
    }

    /**
     * Loads all events for a specific aggregate to build its complete state.
     * This is useful when testing operations on existing aggregates.
     * 
     * @param aggregateType the type of aggregate
     * @param aggregateId the ID of the aggregate
     * @return this stage for fluent chaining
     */
    public GivenStage aggregateState(String aggregateType, String aggregateId) {
        try {
            List<VersionedDomainEvent> aggregateEvents = 
                eventStore.getEventsForAggregate(aggregateType, aggregateId);
            
            aggregateEvents.forEach(this::event);
            return this;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load aggregate state for " + aggregateType + "/" + aggregateId + ": " + e.getMessage(), e);
        }
    }

    /**
     * Loads events of a specific type to build domain-specific state.
     * This is useful for testing scenarios involving specific event types.
     * 
     * @param eventType the type of events to load
     * @return this stage for fluent chaining
     */
    public GivenStage eventsOfType(String eventType) {
        try {
            List<VersionedDomainEvent> typeEvents = 
                eventStore.getEventsByType(eventType);
            
            typeEvents.forEach(this::event);
            return this;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load events of type " + eventType + ": " + e.getMessage(), e);
        }
    }

    /**
     * Loads events for an aggregate since a specific version.
     * This allows testing incremental changes from a known state.
     * 
     * @param aggregateType the type of aggregate
     * @param aggregateId the ID of the aggregate
     * @param sinceVersion the version to start from (exclusive)
     * @return this stage for fluent chaining
     */
    public GivenStage aggregateStatesSince(String aggregateType, String aggregateId, long sinceVersion) {
        try {
            List<VersionedDomainEvent> recentEvents = 
                eventStore.getEventsForAggregateSince(aggregateType, aggregateId, sinceVersion);
            
            recentEvents.forEach(this::event);
            return this;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load aggregate events since version " + sinceVersion + ": " + e.getMessage(), e);
        }
    }

    /**
     * Gets the number of events that have been added to build state.
     * This can be useful for verification or debugging.
     * 
     * @return the number of prior events recorded
     */
    public int getEventCount() {
        return context.getPriorEvents().size();
    }

    /**
     * Checks if any events have been added to build state.
     * 
     * @return true if events have been added, false otherwise
     */
    public boolean hasEvents() {
        return !context.getPriorEvents().isEmpty();
    }

    /**
     * Gets a summary of the events that have been added.
     * This is useful for debugging test setup.
     * 
     * @return a formatted string describing the prior events
     */
    public String getEventSummary() {
        List<VersionedDomainEvent> events = context.getPriorEvents();
        if (events.isEmpty()) {
            return "No prior events";
        }
        
        StringBuilder summary = new StringBuilder();
        summary.append("Prior events (").append(events.size()).append("):\n");
        
        for (int i = 0; i < events.size(); i++) {
            VersionedDomainEvent event = events.get(i);
            summary.append("  ").append(i + 1).append(". ")
                   .append(event.getEventType())
                   .append(" (").append(event.getAggregateType()).append("/").append(event.getAggregateId()).append(")")
                   .append("\n");
        }
        
        return summary.toString();
    }
}