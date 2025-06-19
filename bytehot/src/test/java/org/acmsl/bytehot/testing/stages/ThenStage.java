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
 * Filename: ThenStage.java
 *
 * Author: Claude Code
 *
 * Class name: ThenStage
 *
 * Responsibilities:
 *   - Verify expected events were produced in response to test events
 *   - Provide fluent assertions for event-driven test verification
 *   - Support complex event pattern matching and validation
 *   - Enable comprehensive testing of domain event flows
 *
 * Collaborators:
 *   - EventTestContext: Provides access to test results for verification
 *   - AssertJ: Underlying assertion framework for fluent verification
 *   - DomainEvent classes: Events being verified in assertions
 */
package org.acmsl.bytehot.testing.stages;

import org.acmsl.bytehot.testing.support.EventTestContext;

import org.acmsl.commons.patterns.DomainEvent;

import org.assertj.core.api.Assertions;

import java.time.Instant;
import java.util.List;
import java.util.function.Predicate;

/**
 * The "Then" stage of event-driven testing that verifies expected events.
 * This stage provides fluent assertions for verifying that the correct events
 * were produced in response to the test event, enabling comprehensive testing
 * of domain event flows and business logic.
 * 
 * @author Claude Code
 * @since 2025-06-17
 */
public class ThenStage {

    /**
     * Test context containing the events to verify
     */
    private final EventTestContext context;

    /**
     * Creates a new Then stage.
     * 
     * @param context the test context containing events to verify
     */
    public ThenStage(EventTestContext context) {
        this.context = context;
    }

    /**
     * Verifies that exactly the specified number of events were produced.
     * This includes both resulting events and emitted events.
     * 
     * @param expectedCount the expected total number of events
     * @return this stage for fluent chaining
     */
    public ThenStage eventCount(int expectedCount) {
        Assertions.assertThat(context.getResultCount())
            .describedAs("Total number of events produced")
            .isEqualTo(expectedCount);
        return this;
    }

    /**
     * Verifies that at least one event was produced.
     * 
     * @return this stage for fluent chaining
     */
    public ThenStage hasEvents() {
        Assertions.assertThat(context.hasResults())
            .describedAs("Events were produced")
            .isTrue();
        return this;
    }

    /**
     * Verifies that no events were produced.
     * 
     * @return this stage for fluent chaining
     */
    public ThenStage noEvents() {
        Assertions.assertThat(context.hasResults())
            .describedAs("No events were produced")
            .isFalse();
        return this;
    }

    /**
     * Verifies that an event of the specified type was produced.
     * 
     * @param eventType the type of event to verify
     * @return this stage for fluent chaining
     */
    public ThenStage hasEventOfType(Class<? extends DomainEvent> eventType) {
        boolean hasEvent = context.getAllEvents().stream()
            .anyMatch(eventType::isInstance);
        
        Assertions.assertThat(hasEvent)
            .describedAs("Event of type %s was produced", eventType.getSimpleName())
            .isTrue();
        return this;
    }

    /**
     * Verifies that no event of the specified type was produced.
     * 
     * @param eventType the type of event that should not be present
     * @return this stage for fluent chaining
     */
    public ThenStage hasNoEventOfType(Class<? extends DomainEvent> eventType) {
        boolean hasEvent = context.getAllEvents().stream()
            .anyMatch(eventType::isInstance);
        
        Assertions.assertThat(hasEvent)
            .describedAs("No event of type %s was produced", eventType.getSimpleName())
            .isFalse();
        return this;
    }

    /**
     * Verifies that exactly the specified number of events of the given type were produced.
     * 
     * @param eventType the type of event to count
     * @param expectedCount the expected number of events of this type
     * @return this stage for fluent chaining
     */
    public ThenStage eventCountOfType(Class<? extends DomainEvent> eventType, int expectedCount) {
        long actualCount = context.getAllEvents().stream()
            .filter(eventType::isInstance)
            .count();
        
        Assertions.assertThat(actualCount)
            .describedAs("Number of events of type %s", eventType.getSimpleName())
            .isEqualTo(expectedCount);
        return this;
    }

    /**
     * Verifies that an event matching the specified predicate was produced.
     * 
     * @param predicate the condition the event must satisfy
     * @param description a description of what the predicate checks
     * @return this stage for fluent chaining
     */
    public ThenStage hasEventMatching(Predicate<DomainEvent> predicate, String description) {
        boolean hasMatchingEvent = context.getAllEvents().stream()
            .anyMatch(predicate);
        
        Assertions.assertThat(hasMatchingEvent)
            .describedAs("Event matching '%s' was produced", description)
            .isTrue();
        return this;
    }

    /**
     * Verifies that all events match the specified predicate.
     * 
     * @param predicate the condition all events must satisfy
     * @param description a description of what the predicate checks
     * @return this stage for fluent chaining
     */
    public ThenStage allEventsMatch(Predicate<DomainEvent> predicate, String description) {
        List<DomainEvent> allEvents = context.getAllEvents();
        
        Assertions.assertThat(allEvents)
            .describedAs("All events match '%s'", description)
            .allMatch(predicate);
        return this;
    }

    /**
     * Verifies that events were produced in a specific order.
     * This checks that each event type appears in the specified sequence.
     * 
     * @param eventTypes the expected sequence of event types
     * @return this stage for fluent chaining
     */
    public ThenStage eventsInOrder(Class<? extends DomainEvent>... eventTypes) {
        List<DomainEvent> resultingEvents = context.getResultingEvents();
        
        Assertions.assertThat(resultingEvents)
            .describedAs("Events produced in correct order")
            .hasSize(eventTypes.length);
        
        for (int i = 0; i < eventTypes.length; i++) {
            Assertions.assertThat(resultingEvents.get(i))
                .describedAs("Event at position %d", i)
                .isInstanceOf(eventTypes[i]);
        }
        
        return this;
    }

    /**
     * Verifies that an exception occurred during test execution.
     * 
     * @return this stage for fluent chaining
     */
    public ThenStage hasException() {
        Assertions.assertThat(context.getTestException())
            .describedAs("Exception occurred during test execution")
            .isPresent();
        return this;
    }

    /**
     * Verifies that no exception occurred during test execution.
     * 
     * @return this stage for fluent chaining
     */
    public ThenStage noException() {
        Assertions.assertThat(context.getTestException())
            .describedAs("No exception occurred during test execution")
            .isEmpty();
        return this;
    }

    /**
     * Verifies that an exception of a specific type occurred.
     * 
     * @param exceptionType the expected type of exception
     * @return this stage for fluent chaining
     */
    public ThenStage hasExceptionOfType(Class<? extends Exception> exceptionType) {
        Assertions.assertThat(context.getTestException())
            .describedAs("Exception of type %s occurred", exceptionType.getSimpleName())
            .isPresent()
            .get()
            .isInstanceOf(exceptionType);
        return this;
    }

    /**
     * Verifies that events were emitted to external systems.
     * 
     * @return this stage for fluent chaining
     */
    public ThenStage hasEmittedEvents() {
        Assertions.assertThat(context.getEmittedEvents())
            .describedAs("Events were emitted to external systems")
            .isNotEmpty();
        return this;
    }

    /**
     * Verifies that no events were emitted to external systems.
     * 
     * @return this stage for fluent chaining
     */
    public ThenStage noEmittedEvents() {
        Assertions.assertThat(context.getEmittedEvents())
            .describedAs("No events were emitted to external systems")
            .isEmpty();
        return this;
    }

    /**
     * Verifies that exactly the specified number of events were emitted.
     * 
     * @param expectedCount the expected number of emitted events
     * @return this stage for fluent chaining
     */
    public ThenStage emittedEventCount(int expectedCount) {
        Assertions.assertThat(context.getEmittedEvents())
            .describedAs("Number of emitted events")
            .hasSize(expectedCount);
        return this;
    }

    /**
     * Verifies that an event of the specified type was emitted.
     * 
     * @param eventType the type of event that should have been emitted
     * @return this stage for fluent chaining
     */
    public ThenStage hasEmittedEventOfType(Class<? extends DomainEvent> eventType) {
        boolean hasEmittedEvent = context.getEmittedEvents().stream()
            .anyMatch(eventType::isInstance);
        
        Assertions.assertThat(hasEmittedEvent)
            .describedAs("Emitted event of type %s", eventType.getSimpleName())
            .isTrue();
        return this;
    }

    /**
     * Verifies that all events occurred within a specific time window.
     * This can be useful for testing timing constraints.
     * 
     * @param maxDurationMs the maximum duration in milliseconds
     * @return this stage for fluent chaining
     */
    public ThenStage allEventsWithinTimeWindow(long maxDurationMs) {
        Instant testStart = context.getTestStartTime();
        Instant deadline = testStart.plusMillis(maxDurationMs);
        
        boolean allWithinWindow = context.getAllEvents().stream()
            .filter(event -> event instanceof org.acmsl.commons.patterns.eventsourcing.VersionedDomainEvent)
            .map(event -> ((org.acmsl.commons.patterns.eventsourcing.VersionedDomainEvent) event).getTimestamp())
            .allMatch(timestamp -> !timestamp.isAfter(deadline));
        
        Assertions.assertThat(allWithinWindow)
            .describedAs("All events occurred within %d ms time window", maxDurationMs)
            .isTrue();
        return this;
    }

    /**
     * Provides access to the first event of the specified type for detailed assertions.
     * This allows for complex, custom verifications on specific events.
     * 
     * @param eventType the type of event to retrieve
     * @param <T> the event type
     * @return the first event of the specified type
     * @throws AssertionError if no event of the specified type is found
     */
    public <T extends DomainEvent> T getFirstEventOfType(Class<T> eventType) {
        return context.getAllEvents().stream()
            .filter(eventType::isInstance)
            .map(eventType::cast)
            .findFirst()
            .orElseThrow(() -> new AssertionError(
                "No event of type " + eventType.getSimpleName() + " was found in test results"
            ));
    }

    /**
     * Provides access to all events of the specified type for detailed assertions.
     * 
     * @param eventType the type of events to retrieve
     * @param <T> the event type
     * @return list of all events of the specified type
     */
    public <T extends DomainEvent> List<T> getAllEventsOfType(Class<T> eventType) {
        return context.getAllEvents().stream()
            .filter(eventType::isInstance)
            .map(eventType::cast)
            .toList();
    }

    /**
     * Executes a custom verification function for complex assertions.
     * This allows for sophisticated, domain-specific validations.
     * 
     * @param verification the verification function to execute
     * @param description a description of what is being verified
     * @return this stage for fluent chaining
     */
    public ThenStage verify(Predicate<EventTestContext> verification, String description) {
        boolean verificationPassed = verification.test(context);
        
        Assertions.assertThat(verificationPassed)
            .describedAs("Custom verification: %s", description)
            .isTrue();
        return this;
    }

    /**
     * Prints a summary of all events for debugging purposes.
     * This is useful during test development and troubleshooting.
     * 
     * @return this stage for fluent chaining
     */
    public ThenStage debugPrintEvents() {
        System.out.println("=== Event Test Results ===");
        System.out.println(context.getSummary());
        
        List<DomainEvent> allEvents = context.getAllEvents();
        if (!allEvents.isEmpty()) {
            System.out.println("\nAll Events:");
            for (int i = 0; i < allEvents.size(); i++) {
                DomainEvent event = allEvents.get(i);
                System.out.printf("  %d. %s%n", i + 1, event.getClass().getSimpleName());
            }
        }
        
        System.out.println("==========================");
        return this;
    }

    /**
     * Gets the test context for advanced custom verifications.
     * This provides full access to the test state for complex assertions.
     * 
     * @return the event test context
     */
    public EventTestContext getContext() {
        return context;
    }
}