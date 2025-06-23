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
 * Filename: WhenStage.java
 *
 * Author: Claude Code
 *
 * Class name: WhenStage
 *
 * Responsibilities:
 *   - Represent the When stage in Given-When-Then testing
 *   - Execute actions and capture their results
 *   - Transition to Then stage for verification
 *
 * Collaborators:
 *   - EventTestContext: Context for storing test state and action results
 *   - ThenStage: Next stage in the Given-When-Then flow
 *   - DomainEvent: Events that may be generated during action execution
 */
package org.acmsl.bytehot.testing.stages;

import org.acmsl.bytehot.testing.support.EventTestContext;
import org.acmsl.commons.patterns.DomainEvent;
import org.acmsl.commons.patterns.Test;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents the When stage in a Given-When-Then test scenario.
 * This stage is responsible for executing the action under test
 * and capturing any results or events that are produced.
 * @author Claude Code
 * @since 2025-06-23
 */
public class WhenStage
    implements Test {

    /**
     * The test context that maintains state throughout the test scenario.
     */
    private final EventTestContext context;

    /**
     * Creates a new When stage with the specified test context.
     * @param context the test context to use for state management
     */
    public WhenStage(final EventTestContext context) {
        this.context = context;
    }

    /**
     * Executes an action and stores its result in the test context.
     * @param resultKey the key to store the result under
     * @param action the action to execute
     * @param <T> the type of the result
     * @return this When stage for method chaining
     */
    public <T> WhenStage when(final String resultKey, final Supplier<T> action) {
        final T result = action.get();
        context.storeArtifact(resultKey, result);
        return this;
    }

    /**
     * Executes an action without storing its result.
     * This is useful for actions that produce side effects (like events) rather than return values.
     * @param action the action to execute
     * @return this When stage for method chaining
     */
    public WhenStage when(final Runnable action) {
        action.run();
        return this;
    }

    /**
     * Executes an action that transforms an existing artifact.
     * @param inputKey the key of the input artifact
     * @param outputKey the key to store the result under
     * @param transformation the function to apply to the input
     * @param <T> the type of the input
     * @param <R> the type of the result
     * @return this When stage for method chaining
     */
    public <T, R> WhenStage when(final String inputKey, final String outputKey, 
                                final Function<T, R> transformation) {
        @SuppressWarnings("unchecked")
        final T input = (T) context.getArtifact(inputKey);
        if (input != null) {
            final R result = transformation.apply(input);
            context.storeArtifact(outputKey, result);
        }
        return this;
    }

    /**
     * Executes an action that may produce a domain event.
     * @param eventProducer the supplier that produces the event
     * @return this When stage for method chaining
     */
    public WhenStage whenEvent(final Supplier<DomainEvent> eventProducer) {
        final DomainEvent event = eventProducer.get();
        if (event != null) {
            context.addEmittedEvent(event);
        }
        return this;
    }

    /**
     * Executes an action with error handling and captures any exception.
     * @param action the action to execute
     * @return this When stage for method chaining
     */
    public WhenStage whenWithErrorHandling(final Runnable action) {
        try {
            action.run();
            context.storeArtifact("exception", null);
        } catch (final Exception e) {
            context.storeArtifact("exception", e);
        }
        return this;
    }

    /**
     * Executes an action with error handling and captures both result and exception.
     * @param resultKey the key to store the result under
     * @param action the action to execute
     * @param <T> the type of the result
     * @return this When stage for method chaining
     */
    public <T> WhenStage whenWithErrorHandling(final String resultKey, final Supplier<T> action) {
        try {
            final T result = action.get();
            context.storeArtifact(resultKey, result);
            context.storeArtifact("exception", null);
        } catch (final Exception e) {
            context.storeArtifact("exception", e);
        }
        return this;
    }

    /**
     * Marks a specific point in time for timing measurements.
     * @param marker the name of the timing marker
     * @return this When stage for method chaining
     */
    public WhenStage markTime(final String marker) {
        context.storeArtifact("time_" + marker, System.currentTimeMillis());
        return this;
    }

    /**
     * Executes an action multiple times (useful for testing concurrent scenarios).
     * @param times the number of times to execute the action
     * @param action the action to execute
     * @return this When stage for method chaining
     */
    public WhenStage repeat(final int times, final Runnable action) {
        for (int i = 0; i < times; i++) {
            action.run();
        }
        return this;
    }

    /**
     * Transitions to the Then stage to begin verification.
     * @return a new Then stage configured with the current test context
     */
    public ThenStage then() {
        return new ThenStage(context);
    }

    /**
     * Transitions to the Then stage with a descriptive expectation.
     * @param expectationDescription human-readable description of what is expected
     * @return a new Then stage configured with the current test context
     */
    public ThenStage then(final String expectationDescription) {
        context.storeArtifact("then_description", expectationDescription);
        return new ThenStage(context);
    }

    /**
     * Gets the test context for advanced scenarios.
     * @return the current test context
     */
    public EventTestContext getContext() {
        return context;
    }

    @Override
    public String toString() {
        return String.format(
            "WhenStage[scenario='%s', artifacts=%d, events=%d]",
            context.getScenarioDescription(),
            context.getArtifactCount(),
            context.getEventCount()
        );
    }
}