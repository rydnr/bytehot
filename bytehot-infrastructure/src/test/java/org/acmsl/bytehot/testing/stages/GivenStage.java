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
 *   - Represent the Given stage in Given-When-Then testing
 *   - Set up initial conditions and test artifacts
 *   - Transition to When stage for action execution
 *
 * Collaborators:
 *   - EventTestContext: Context for storing test state and artifacts
 *   - WhenStage: Next stage in the Given-When-Then flow
 *   - DomainEvent: Events that may be set up during Given stage
 */
package org.acmsl.bytehot.testing.stages;

import org.acmsl.bytehot.testing.support.EventTestContext;
import org.acmsl.commons.patterns.DomainEvent;
import org.acmsl.commons.patterns.Test;

import java.util.function.Supplier;

/**
 * Represents the Given stage in a Given-When-Then test scenario.
 * This stage is responsible for setting up initial conditions,
 * test data, and configuration before executing actions.
 * @author Claude Code
 * @since 2025-06-23
 */
public class GivenStage
    implements Test {

    /**
     * The test context that maintains state throughout the test scenario.
     */
    private final EventTestContext context;

    /**
     * Creates a new Given stage with the specified test context.
     * @param context the test context to use for state management
     */
    public GivenStage(final EventTestContext context) {
        this.context = context;
    }

    /**
     * Sets up an initial artifact in the test context.
     * @param key the identifier for the artifact
     * @param value the artifact to store
     * @return this Given stage for method chaining
     */
    public GivenStage given(final String key, final Object value) {
        context.storeArtifact(key, value);
        return this;
    }

    /**
     * Sets up an artifact using a supplier for lazy evaluation.
     * @param key the identifier for the artifact
     * @param supplier the supplier that will provide the artifact value
     * @return this Given stage for method chaining
     */
    public GivenStage given(final String key, final Supplier<Object> supplier) {
        context.storeArtifact(key, supplier.get());
        return this;
    }

    /**
     * Sets up an initial domain event in the test context.
     * This can be useful for testing event chains or responses.
     * @param event the initial domain event
     * @return this Given stage for method chaining
     */
    public GivenStage givenEvent(final DomainEvent event) {
        context.addEmittedEvent(event);
        return this;
    }

    /**
     * Sets up multiple initial conditions using a description.
     * @param description human-readable description of what is being set up
     * @return this Given stage for method chaining
     */
    public GivenStage givenThat(final String description) {
        // Store the description as metadata
        context.storeArtifact("given_description", description);
        return this;
    }

    /**
     * Sets up a condition with a boolean value for later assertion.
     * @param condition the condition description
     * @param value the boolean value of the condition
     * @return this Given stage for method chaining
     */
    public GivenStage givenCondition(final String condition, final boolean value) {
        context.storeArtifact("condition_" + condition, value);
        return this;
    }

    /**
     * Sets up configuration or environment state.
     * @param configKey the configuration key
     * @param configValue the configuration value
     * @return this Given stage for method chaining
     */
    public GivenStage givenConfiguration(final String configKey, final Object configValue) {
        context.storeArtifact("config_" + configKey, configValue);
        return this;
    }

    /**
     * Executes a setup action without storing its result.
     * This is useful for performing initialization that doesn't need to be captured.
     * @param setupAction the action to execute during setup
     * @return this Given stage for method chaining
     */
    public GivenStage givenAction(final Runnable setupAction) {
        setupAction.run();
        return this;
    }

    /**
     * Transitions to the When stage to begin executing actions.
     * @return a new When stage configured with the current test context
     */
    public WhenStage when() {
        return new WhenStage(context);
    }

    /**
     * Transitions to the When stage with a descriptive action.
     * @param actionDescription human-readable description of what action will be performed
     * @return a new When stage configured with the current test context
     */
    public WhenStage when(final String actionDescription) {
        context.storeArtifact("when_description", actionDescription);
        return new WhenStage(context);
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
            "GivenStage[scenario='%s', artifacts=%d]",
            context.getScenarioDescription(),
            context.getArtifactCount()
        );
    }
}