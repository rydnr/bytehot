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
 * Filename: EventTestContext.java
 *
 * Author: Claude Code
 *
 * Class name: EventTestContext
 *
 * Responsibilities:
 *   - Maintains test execution state for event-driven tests
 *   - Tracks prior events (Given), test event (When), and results (Then)
 *   - Provides context for assertions and verifications
 *   - Supports test scenario serialization and reproduction
 *
 * Collaborators:
 *   - EventDrivenTestSupport: Uses this context for test coordination
 *   - Given/When/Then stages: Share state through this context
 *   - Event assertion classes: Access test results through context
 */
package org.acmsl.bytehot.testing.support;

import org.acmsl.bytehot.domain.VersionedDomainEvent;

import org.acmsl.commons.patterns.DomainEvent;
import org.acmsl.commons.patterns.DomainResponseEvent;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Test execution context that maintains all state for event-driven testing.
 * This context tracks the complete test scenario including setup events,
 * the event under test, and all resulting events for verification.
 * 
 * @author Claude Code
 * @since 2025-06-17
 */
public class EventTestContext {

    /**
     * Events used to build system state (Given phase)
     */
    private final List<VersionedDomainEvent> priorEvents;

    /**
     * The event being tested (When phase)
     */
    private DomainEvent testEvent;

    /**
     * Events produced by the application as direct responses
     */
    private List<DomainResponseEvent<?>> resultingEvents;

    /**
     * Events emitted to external systems (captured for verification)
     */
    private List<DomainEvent> emittedEvents;

    /**
     * Test execution timestamp for correlation
     */
    private final Instant testStartTime;

    /**
     * Test name or identifier for debugging
     */
    private String testName;

    /**
     * Any exception that occurred during test execution
     */
    private Exception testException;

    /**
     * Creates a new event test context with empty state.
     */
    public EventTestContext() {
        this.priorEvents = new ArrayList<>();
        this.resultingEvents = new ArrayList<>();
        this.emittedEvents = new ArrayList<>();
        this.testStartTime = Instant.now();
    }

    /**
     * Records a prior event used to build system state.
     * These events represent the "Given" conditions of the test.
     * 
     * @param event the prior event to record
     */
    public void recordPriorEvent(VersionedDomainEvent event) {
        priorEvents.add(event);
    }

    /**
     * Sets the event under test (When phase).
     * 
     * @param event the event being tested
     */
    public void setTestEvent(DomainEvent event) {
        this.testEvent = event;
    }

    /**
     * Sets the events produced by the application in response to the test event.
     * 
     * @param events the resulting events from application processing
     */
    public void setResultingEvents(List<DomainResponseEvent<?>> events) {
        this.resultingEvents = new ArrayList<>(events);
    }

    /**
     * Sets the events emitted to external systems during test execution.
     * 
     * @param events the emitted events captured during testing
     */
    public void setEmittedEvents(List<DomainEvent> events) {
        this.emittedEvents = new ArrayList<>(events);
    }

    /**
     * Gets all prior events used to build system state.
     * 
     * @return immutable list of prior events
     */
    public List<VersionedDomainEvent> getPriorEvents() {
        return Collections.unmodifiableList(priorEvents);
    }

    /**
     * Gets the event under test.
     * 
     * @return the test event, or empty if not set
     */
    public Optional<DomainEvent> getTestEvent() {
        return Optional.ofNullable(testEvent);
    }

    /**
     * Gets the events produced by the application.
     * 
     * @return immutable list of resulting events
     */
    @SuppressWarnings("unchecked")
    public List<DomainEvent> getResultingEvents() {
        return (List<DomainEvent>) (List<?>) Collections.unmodifiableList(resultingEvents);
    }

    /**
     * Gets the events emitted to external systems.
     * 
     * @return immutable list of emitted events
     */
    public List<DomainEvent> getEmittedEvents() {
        return Collections.unmodifiableList(emittedEvents);
    }

    /**
     * Gets all events (prior + resulting + emitted) for comprehensive analysis.
     * 
     * @return immutable list of all events in the test context
     */
    public List<DomainEvent> getAllEvents() {
        List<DomainEvent> allEvents = new ArrayList<>();
        allEvents.addAll(priorEvents);
        if (testEvent != null) {
            allEvents.add(testEvent);
        }
        allEvents.addAll(getResultingEvents());
        allEvents.addAll(emittedEvents);
        return Collections.unmodifiableList(allEvents);
    }

    /**
     * Gets the test execution start time.
     * 
     * @return the timestamp when this test context was created
     */
    public Instant getTestStartTime() {
        return testStartTime;
    }

    /**
     * Sets the test name for debugging and reporting.
     * 
     * @param testName the name or identifier of the test
     */
    public void setTestName(String testName) {
        this.testName = testName;
    }

    /**
     * Gets the test name.
     * 
     * @return the test name, or empty if not set
     */
    public Optional<String> getTestName() {
        return Optional.ofNullable(testName);
    }

    /**
     * Records an exception that occurred during test execution.
     * 
     * @param exception the exception to record
     */
    public void setTestException(Exception exception) {
        this.testException = exception;
    }

    /**
     * Gets any exception that occurred during test execution.
     * 
     * @return the test exception, or empty if none occurred
     */
    public Optional<Exception> getTestException() {
        return Optional.ofNullable(testException);
    }

    /**
     * Finds the first event of the specified type in the results.
     * 
     * @param eventType the type of event to find
     * @param <T> the event type
     * @return the first matching event, or empty if not found
     */
    public <T extends DomainEvent> Optional<T> findResultingEvent(Class<T> eventType) {
        return getResultingEvents().stream()
            .filter(eventType::isInstance)
            .map(eventType::cast)
            .findFirst();
    }

    /**
     * Finds all events of the specified type in the results.
     * 
     * @param eventType the type of events to find
     * @param <T> the event type
     * @return list of matching events
     */
    public <T extends DomainEvent> List<T> findAllResultingEvents(Class<T> eventType) {
        return getResultingEvents().stream()
            .filter(eventType::isInstance)
            .map(eventType::cast)
            .toList();
    }

    /**
     * Checks if any events were produced by the test.
     * 
     * @return true if there are resulting or emitted events
     */
    public boolean hasResults() {
        return !resultingEvents.isEmpty() || !emittedEvents.isEmpty();
    }

    /**
     * Gets the total number of events produced by the test.
     * 
     * @return total count of resulting and emitted events
     */
    public int getResultCount() {
        return resultingEvents.size() + emittedEvents.size();
    }

    /**
     * Creates a summary string of the test context for debugging.
     * 
     * @return formatted summary of the test context
     */
    public String getSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("EventTestContext Summary:\n");
        summary.append("  Test: ").append(testName != null ? testName : "unnamed").append("\n");
        summary.append("  Started: ").append(testStartTime).append("\n");
        summary.append("  Prior Events: ").append(priorEvents.size()).append("\n");
        summary.append("  Test Event: ").append(testEvent != null ? testEvent.getClass().getSimpleName() : "none").append("\n");
        summary.append("  Resulting Events: ").append(resultingEvents.size()).append("\n");
        summary.append("  Emitted Events: ").append(emittedEvents.size()).append("\n");
        if (testException != null) {
            summary.append("  Exception: ").append(testException.getMessage()).append("\n");
        }
        return summary.toString();
    }
}