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
 *   - Represent the Then stage in Given-When-Then testing
 *   - Perform assertions and verify expected outcomes
 *   - Provide comprehensive verification methods for domain events
 *
 * Collaborators:
 *   - EventTestContext: Context containing test state and results
 *   - DomainEvent: Events to be verified and asserted
 *   - AssertionError: Thrown when assertions fail
 */
package org.acmsl.bytehot.testing.stages;

import org.acmsl.bytehot.testing.support.EventTestContext;
import org.acmsl.commons.patterns.DomainEvent;
import org.acmsl.commons.patterns.DomainResponseEvent;
import org.acmsl.commons.patterns.Test;

import java.util.List;
import java.util.function.Predicate;

/**
 * Represents the Then stage in a Given-When-Then test scenario.
 * This stage is responsible for performing assertions and verifying
 * that the expected outcomes have occurred.
 * @author Claude Code
 * @since 2025-06-23
 */
public class ThenStage
    implements Test {

    /**
     * The test context that maintains state throughout the test scenario.
     */
    private final EventTestContext context;

    /**
     * Creates a new Then stage with the specified test context.
     * @param context the test context to use for verification
     */
    public ThenStage(final EventTestContext context) {
        this.context = context;
    }

    /**
     * Asserts that a specific artifact exists in the test context.
     * @param key the key of the artifact to check
     * @return this Then stage for method chaining
     * @throws AssertionError if the artifact does not exist
     */
    public ThenStage thenArtifactExists(final String key) {
        if (!context.hasArtifact(key)) {
            throw new AssertionError("Expected artifact '" + key + "' to exist, but it was not found");
        }
        return this;
    }

    /**
     * Asserts that a specific artifact has the expected value.
     * @param key the key of the artifact to check
     * @param expectedValue the expected value
     * @return this Then stage for method chaining
     * @throws AssertionError if the artifact does not have the expected value
     */
    public ThenStage thenArtifactEquals(final String key, final Object expectedValue) {
        final Object actualValue = context.getArtifact(key);
        if (!java.util.Objects.equals(actualValue, expectedValue)) {
            throw new AssertionError(
                String.format("Expected artifact '%s' to equal '%s', but was '%s'", 
                    key, expectedValue, actualValue));
        }
        return this;
    }

    /**
     * Asserts that a specific number of events were emitted.
     * @param expectedCount the expected number of events
     * @return this Then stage for method chaining
     * @throws AssertionError if the actual count does not match
     */
    public ThenStage thenEventCount(final int expectedCount) {
        final int actualCount = context.getEventCount();
        if (actualCount != expectedCount) {
            throw new AssertionError(
                String.format("Expected %d events to be emitted, but %d were emitted", 
                    expectedCount, actualCount));
        }
        return this;
    }

    /**
     * Asserts that at least one event of the specified type was emitted.
     * @param eventType the class of event to check for
     * @return this Then stage for method chaining
     * @throws AssertionError if no event of the specified type was emitted
     */
    public ThenStage thenEventOfType(final Class<? extends DomainEvent> eventType) {
        final boolean found = context.getEmittedEvents().stream()
            .anyMatch(eventType::isInstance);
        if (!found) {
            throw new AssertionError(
                "Expected at least one event of type " + eventType.getSimpleName() + 
                " to be emitted, but none were found");
        }
        return this;
    }

    /**
     * Asserts that a specific number of events of the given type were emitted.
     * @param eventType the class of event to count
     * @param expectedCount the expected number of events of this type
     * @return this Then stage for method chaining
     * @throws AssertionError if the actual count does not match
     */
    public ThenStage thenEventCountOfType(final Class<? extends DomainEvent> eventType, 
                                         final int expectedCount) {
        final long actualCount = context.getEmittedEvents().stream()
            .filter(eventType::isInstance)
            .count();
        if (actualCount != expectedCount) {
            throw new AssertionError(
                String.format("Expected %d events of type %s, but %d were emitted", 
                    expectedCount, eventType.getSimpleName(), actualCount));
        }
        return this;
    }

    /**
     * Asserts that no events were emitted during the test scenario.
     * @return this Then stage for method chaining
     * @throws AssertionError if any events were emitted
     */
    public ThenStage thenNoEvents() {
        final int eventCount = context.getEventCount();
        if (eventCount > 0) {
            throw new AssertionError(
                String.format("Expected no events to be emitted, but %d were emitted", eventCount));
        }
        return this;
    }

    /**
     * Asserts that no events of the specified type were emitted.
     * @param eventType the class of event that should not be emitted
     * @return this Then stage for method chaining
     * @throws AssertionError if any events of the specified type were emitted
     */
    public ThenStage thenNoEventOfType(final Class<? extends DomainEvent> eventType) {
        final boolean found = context.getEmittedEvents().stream()
            .anyMatch(eventType::isInstance);
        if (found) {
            throw new AssertionError(
                "Expected no events of type " + eventType.getSimpleName() + 
                " to be emitted, but at least one was found");
        }
        return this;
    }

    /**
     * Asserts that an event satisfying the given predicate was emitted.
     * @param eventType the class of event to check
     * @param predicate the condition the event must satisfy
     * @param <T> the type of event
     * @return this Then stage for method chaining
     * @throws AssertionError if no matching event was found
     */
    public <T extends DomainEvent> ThenStage thenEventMatching(
        final Class<T> eventType, final Predicate<T> predicate) {
        
        final boolean found = context.getEmittedEvents().stream()
            .filter(eventType::isInstance)
            .map(eventType::cast)
            .anyMatch(predicate);
            
        if (!found) {
            throw new AssertionError(
                "Expected an event of type " + eventType.getSimpleName() + 
                " matching the given predicate, but none were found");
        }
        return this;
    }

    /**
     * Asserts that an exception was thrown during the When stage.
     * @return this Then stage for method chaining
     * @throws AssertionError if no exception was captured
     */
    public ThenStage thenExceptionThrown() {
        final Exception exception = context.getArtifact("exception", Exception.class);
        if (exception == null) {
            throw new AssertionError("Expected an exception to be thrown, but none was captured");
        }
        return this;
    }

    /**
     * Asserts that a specific type of exception was thrown during the When stage.
     * @param exceptionType the expected exception type
     * @return this Then stage for method chaining
     * @throws AssertionError if no exception of the specified type was captured
     */
    public ThenStage thenExceptionOfType(final Class<? extends Exception> exceptionType) {
        final Exception exception = context.getArtifact("exception", Exception.class);
        if (exception == null) {
            throw new AssertionError(
                "Expected an exception of type " + exceptionType.getSimpleName() + 
                " to be thrown, but no exception was captured");
        }
        if (!exceptionType.isInstance(exception)) {
            throw new AssertionError(
                String.format("Expected exception of type %s, but got %s", 
                    exceptionType.getSimpleName(), exception.getClass().getSimpleName()));
        }
        return this;
    }

    /**
     * Asserts that no exception was thrown during the When stage.
     * @return this Then stage for method chaining
     * @throws AssertionError if an exception was captured
     */
    public ThenStage thenNoException() {
        final Exception exception = context.getArtifact("exception", Exception.class);
        if (exception != null) {
            throw new AssertionError(
                "Expected no exception to be thrown, but got: " + exception.getClass().getSimpleName() +
                " - " + exception.getMessage());
        }
        return this;
    }

    /**
     * Asserts that all response events properly reference their preceding events.
     * @return this Then stage for method chaining
     * @throws AssertionError if any response event has an invalid chain
     */
    public ThenStage thenValidEventChain() {
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
        return this;
    }

    /**
     * Executes a custom assertion using the test context.
     * @param assertion the custom assertion to execute
     * @return this Then stage for method chaining
     */
    public ThenStage thenCustom(final Runnable assertion) {
        assertion.run();
        return this;
    }

    /**
     * Completes the test scenario by marking it as finished.
     * This is optional but can be useful for cleanup or timing measurements.
     */
    public void complete() {
        context.completeScenario();
    }

    /**
     * Gets the test context for advanced verification scenarios.
     * @return the current test context
     */
    public EventTestContext getContext() {
        return context;
    }

    @Override
    public String toString() {
        return String.format(
            "ThenStage[scenario='%s', artifacts=%d, events=%d, completed=%s]",
            context.getScenarioDescription(),
            context.getArtifactCount(),
            context.getEventCount(),
            !context.isScenarioRunning()
        );
    }
}