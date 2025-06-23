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
 * Filename: EventDrivenTestSupport.java
 *
 * Author: Claude Code
 *
 * Class name: EventDrivenTestSupport
 *
 * Responsibilities:
 *   - Provide base infrastructure for event-driven testing
 *   - Integrate java-commons patterns with ByteHot testing
 *   - Support Given-When-Then testing style for domain events
 *
 * Collaborators:
 *   - DomainEvent: Events to be tested
 *   - EventTestContext: Test execution context
 *   - EventCapturingEmitter: Captures emitted events during tests
 */
package org.acmsl.bytehot.testing;

import org.acmsl.bytehot.testing.stages.GivenStage;
import org.acmsl.bytehot.testing.support.EventTestContext;
import org.acmsl.bytehot.testing.support.EventCapturingEmitter;
import org.acmsl.commons.patterns.DomainEvent;
import org.acmsl.commons.patterns.DomainResponseEvent;
import org.acmsl.commons.patterns.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Base class providing infrastructure for event-driven testing.
 * Integrates java-commons domain event patterns with ByteHot's
 * testing infrastructure to enable comprehensive domain testing.
 * @author Claude Code
 * @since 2025-06-23
 */
public abstract class EventDrivenTestSupport
    implements Test {

    /**
     * The test context that maintains state across test execution.
     */
    protected final EventTestContext context;

    /**
     * Creates a new event-driven test support instance.
     */
    protected EventDrivenTestSupport() {
        this.context = new EventTestContext();
    }

    /**
     * Starts a new test scenario with the given description.
     * @param description human-readable description of the test scenario
     * @return the Given stage to set up initial conditions
     */
    protected GivenStage scenario(final String description) {
        context.setScenarioDescription(description);
        context.reset();
        return new GivenStage(context);
    }

    /**
     * Waits for the expected number of events to be emitted.
     * This is useful for asynchronous event processing scenarios.
     * @param expectedEventCount the number of events to wait for
     * @param timeoutSeconds maximum time to wait in seconds
     * @return true if the expected events were received within timeout
     */
    protected boolean waitForEvents(final int expectedEventCount, final long timeoutSeconds) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                while (context.getEmittedEvents().size() < expectedEventCount) {
                    try {
                        Thread.sleep(100); // Poll every 100ms
                    } catch (final InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return false;
                    }
                }
                return true;
            }).get(timeoutSeconds, TimeUnit.SECONDS);
        } catch (final Exception e) {
            return false;
        }
    }

    /**
     * Gets all events emitted during the test scenario.
     * @return list of emitted domain events
     */
    protected List<DomainEvent> getEmittedEvents() {
        return context.getEmittedEvents();
    }

    /**
     * Gets events of a specific type emitted during the test scenario.
     * @param eventType the class of events to filter for
     * @param <T> the type of event
     * @return list of events of the specified type
     */
    protected <T extends DomainEvent> List<T> getEmittedEventsOfType(final Class<T> eventType) {
        return context.getEmittedEvents().stream()
            .filter(eventType::isInstance)
            .map(eventType::cast)
            .toList();
    }

    /**
     * Gets the last event emitted during the test scenario.
     * @return the most recently emitted event, or null if no events
     */
    protected DomainEvent getLastEmittedEvent() {
        final List<DomainEvent> events = context.getEmittedEvents();
        return events.isEmpty() ? null : events.get(events.size() - 1);
    }

    /**
     * Gets the last event of a specific type emitted during the test scenario.
     * @param eventType the class of event to find
     * @param <T> the type of event
     * @return the most recent event of the specified type, or null if none
     */
    protected <T extends DomainEvent> T getLastEmittedEventOfType(final Class<T> eventType) {
        final List<T> events = getEmittedEventsOfType(eventType);
        return events.isEmpty() ? null : events.get(events.size() - 1);
    }

    /**
     * Checks if any events were emitted during the test scenario.
     * @return true if at least one event was emitted
     */
    protected boolean hasEmittedEvents() {
        return !context.getEmittedEvents().isEmpty();
    }

    /**
     * Checks if a specific type of event was emitted during the test scenario.
     * @param eventType the class of event to check for
     * @return true if at least one event of the specified type was emitted
     */
    protected boolean hasEmittedEventOfType(final Class<? extends DomainEvent> eventType) {
        return context.getEmittedEvents().stream()
            .anyMatch(eventType::isInstance);
    }

    /**
     * Gets the count of events emitted during the test scenario.
     * @return the total number of emitted events
     */
    protected int getEmittedEventCount() {
        return context.getEmittedEvents().size();
    }

    /**
     * Gets the count of events of a specific type emitted during the test scenario.
     * @param eventType the class of events to count
     * @return the number of events of the specified type
     */
    protected int getEmittedEventCountOfType(final Class<? extends DomainEvent> eventType) {
        return (int) context.getEmittedEvents().stream()
            .filter(eventType::isInstance)
            .count();
    }

    /**
     * Clears all captured events and resets the test context.
     * This can be useful for multi-step test scenarios.
     */
    protected void clearCapturedEvents() {
        context.getEmittedEvents().clear();
    }

    /**
     * Gets the test context for advanced test scenarios.
     * @return the current test context
     */
    protected EventTestContext getContext() {
        return context;
    }

    /**
     * Validates that the given response event is properly linked to its preceding event.
     * @param responseEvent the response event to validate
     * @param expectedPrecedingEvent the expected preceding event
     * @param <E> the type of the preceding event
     * @return true if the response event is properly linked
     */
    protected <E extends DomainEvent> boolean validateEventChain(
        final DomainResponseEvent<E> responseEvent,
        final E expectedPrecedingEvent) {
        
        if (responseEvent == null) {
            return false;
        }
        
        final E preceding = responseEvent.getPreceding();
        return preceding != null && preceding.equals(expectedPrecedingEvent);
    }

    /**
     * Asserts that the event chain is valid for all response events.
     * This validates that each response event properly references its preceding event.
     * @throws AssertionError if any response event has an invalid chain
     */
    protected void assertValidEventChain() {
        final List<DomainEvent> events = context.getEmittedEvents();
        
        for (int i = 0; i < events.size(); i++) {
            final DomainEvent event = events.get(i);
            
            if (event instanceof DomainResponseEvent<?> responseEvent) {
                final DomainEvent preceding = responseEvent.getPreceding();
                
                if (preceding == null) {
                    throw new AssertionError(
                        "Response event at position " + i + " has null preceding event: " + event);
                }
                
                // Verify the preceding event appears earlier in the event sequence
                boolean foundPreceding = false;
                for (int j = 0; j < i; j++) {
                    if (events.get(j).equals(preceding)) {
                        foundPreceding = true;
                        break;
                    }
                }
                
                if (!foundPreceding) {
                    throw new AssertionError(
                        "Response event at position " + i + " references preceding event not found in sequence: " + 
                        responseEvent + " -> " + preceding);
                }
            }
        }
    }
}