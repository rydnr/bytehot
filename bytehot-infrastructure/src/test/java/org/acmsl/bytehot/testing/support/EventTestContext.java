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
 *   - Maintain test execution state across Given-When-Then stages
 *   - Capture and store emitted domain events during test execution
 *   - Provide access to test artifacts and configuration
 *
 * Collaborators:
 *   - DomainEvent: Events captured during test execution
 *   - EventCapturingEmitter: Emitter that captures events to this context
 *   - Application layer classes: Components under test
 */
package org.acmsl.bytehot.testing.support;

import org.acmsl.commons.patterns.DomainEvent;
import org.acmsl.commons.patterns.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Context object that maintains state throughout event-driven test execution.
 * This class is thread-safe to support concurrent event processing during tests.
 * @author Claude Code
 * @since 2025-06-23
 */
public class EventTestContext
    implements Test {

    /**
     * Description of the current test scenario.
     */
    private String scenarioDescription;

    /**
     * List of events emitted during test execution.
     * Using CopyOnWriteArrayList for thread safety.
     */
    private final List<DomainEvent> emittedEvents;

    /**
     * Map of test artifacts created during test execution.
     * This can store domain objects, configuration, or other test data.
     */
    private final Map<String, Object> artifacts;

    /**
     * Timestamp when the test scenario started.
     */
    private Instant startTime;

    /**
     * Flag indicating whether the test scenario is currently running.
     */
    private boolean scenarioRunning;

    /**
     * Creates a new event test context.
     */
    public EventTestContext() {
        this.emittedEvents = new CopyOnWriteArrayList<>();
        this.artifacts = new HashMap<>();
        this.scenarioRunning = false;
    }

    /**
     * Sets the description of the current test scenario.
     * @param description human-readable description of what is being tested
     */
    public void setScenarioDescription(final String description) {
        this.scenarioDescription = description;
    }

    /**
     * Gets the description of the current test scenario.
     * @return the scenario description, or null if not set
     */
    public String getScenarioDescription() {
        return scenarioDescription;
    }

    /**
     * Resets the context for a new test scenario.
     * This clears all captured events and artifacts while preserving configuration.
     */
    public void reset() {
        emittedEvents.clear();
        artifacts.clear();
        startTime = Instant.now();
        scenarioRunning = true;
    }

    /**
     * Marks the scenario as completed.
     * This can be used to track test timing and prevent further event capture.
     */
    public void completeScenario() {
        scenarioRunning = false;
    }

    /**
     * Checks if the scenario is currently running.
     * @return true if the scenario is active
     */
    public boolean isScenarioRunning() {
        return scenarioRunning;
    }

    /**
     * Gets the time when the current scenario started.
     * @return the start timestamp, or null if no scenario has been started
     */
    public Instant getStartTime() {
        return startTime;
    }

    /**
     * Adds an emitted event to the context.
     * This method is thread-safe and can be called concurrently.
     * @param event the domain event that was emitted
     */
    public void addEmittedEvent(final DomainEvent event) {
        if (event != null) {
            emittedEvents.add(event);
        }
    }

    /**
     * Gets all events emitted during the test scenario.
     * Returns a read-only view to prevent external modification.
     * @return immutable list of emitted events
     */
    public List<DomainEvent> getEmittedEvents() {
        return Collections.unmodifiableList(new ArrayList<>(emittedEvents));
    }

    /**
     * Clears all emitted events.
     * This can be useful for multi-phase test scenarios.
     */
    public void clearEmittedEvents() {
        emittedEvents.clear();
    }

    /**
     * Stores a test artifact with the given key.
     * Test artifacts can be domain objects, configuration, or any test data.
     * @param key unique identifier for the artifact
     * @param artifact the object to store
     */
    public void storeArtifact(final String key, final Object artifact) {
        artifacts.put(key, artifact);
    }

    /**
     * Retrieves a test artifact by key.
     * @param key the identifier of the artifact to retrieve
     * @return the stored artifact, or null if not found
     */
    public Object getArtifact(final String key) {
        return artifacts.get(key);
    }

    /**
     * Retrieves a test artifact by key with type casting.
     * @param key the identifier of the artifact to retrieve
     * @param type the expected type of the artifact
     * @param <T> the type to cast to
     * @return the stored artifact cast to the specified type, or null if not found or wrong type
     */
    @SuppressWarnings("unchecked")
    public <T> T getArtifact(final String key, final Class<T> type) {
        final Object artifact = artifacts.get(key);
        if (artifact != null && type.isInstance(artifact)) {
            return (T) artifact;
        }
        return null;
    }

    /**
     * Checks if an artifact with the given key exists.
     * @param key the identifier to check
     * @return true if an artifact exists with this key
     */
    public boolean hasArtifact(final String key) {
        return artifacts.containsKey(key);
    }

    /**
     * Removes an artifact from the context.
     * @param key the identifier of the artifact to remove
     * @return the removed artifact, or null if not found
     */
    public Object removeArtifact(final String key) {
        return artifacts.remove(key);
    }

    /**
     * Gets all artifact keys stored in this context.
     * @return set of all artifact keys
     */
    public java.util.Set<String> getArtifactKeys() {
        return Collections.unmodifiableSet(artifacts.keySet());
    }

    /**
     * Gets the number of events emitted during the test scenario.
     * @return the count of emitted events
     */
    public int getEventCount() {
        return emittedEvents.size();
    }

    /**
     * Gets the number of artifacts stored in the context.
     * @return the count of stored artifacts
     */
    public int getArtifactCount() {
        return artifacts.size();
    }

    /**
     * Gets a summary of the test context state for debugging.
     * @return human-readable string describing the context state
     */
    public String getSummary() {
        return String.format(
            "EventTestContext[scenario='%s', events=%d, artifacts=%d, running=%s]",
            scenarioDescription,
            emittedEvents.size(),
            artifacts.size(),
            scenarioRunning
        );
    }

    @Override
    public String toString() {
        return getSummary();
    }
}