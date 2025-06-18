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
 * Filename: EventCapturingEmitter.java
 *
 * Author: Claude Code
 *
 * Class name: EventCapturingEmitter
 *
 * Responsibilities:
 *   - Capture events emitted during test execution
 *   - Provide test access to emitted events for verification
 *   - Replace production event emitter during testing
 *   - Support event inspection and analysis for testing
 *
 * Collaborators:
 *   - EventEmitterPort: Interface this adapter implements
 *   - WhenStage: Uses this to capture emitted events
 *   - EventTestContext: Stores captured events for verification
 */
package org.acmsl.bytehot.testing.support;

import org.acmsl.bytehot.domain.EventEmitterPort;

import org.acmsl.commons.patterns.Adapter;
import org.acmsl.commons.patterns.DomainEvent;
import org.acmsl.commons.patterns.DomainResponseEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Test adapter that captures events emitted during test execution.
 * This allows tests to verify what events were emitted to external systems
 * without actually sending them to production systems.
 * 
 * @author Claude Code
 * @since 2025-06-17
 */
public class EventCapturingEmitter implements EventEmitterPort, Adapter<EventEmitterPort> {

    /**
     * List of captured events in the order they were emitted
     */
    private final List<DomainResponseEvent<?>> capturedEvents;

    /**
     * Counter for emitted events
     */
    private long emittedEventCount;

    /**
     * Whether emission is enabled (for testing emission toggles)
     */
    private boolean emissionEnabled;

    /**
     * Creates a new event capturing emitter.
     */
    public EventCapturingEmitter() {
        this.capturedEvents = Collections.synchronizedList(new ArrayList<>());
        this.emittedEventCount = 0L;
        this.emissionEnabled = true;
    }

    @Override
    public void emit(DomainResponseEvent<?> event) throws Exception {
        if (emissionEnabled) {
            capturedEvents.add(event);
            emittedEventCount++;
        }
    }

    @Override
    public void emit(List<DomainResponseEvent<?>> events) throws Exception {
        if (emissionEnabled) {
            capturedEvents.addAll(events);
            emittedEventCount += events.size();
        }
    }

    @Override
    public boolean isEmissionAvailable() {
        return emissionEnabled;
    }

    @Override
    public String getEmissionTarget() {
        return "test-event-capturer";
    }

    @Override
    public long getEmittedEventCount() {
        return emittedEventCount;
    }

    @Override
    public EventEmitterPort adapts() {
        return this;
    }

    /**
     * Gets all captured events in the order they were emitted.
     * 
     * @return immutable list of captured events
     */
    @SuppressWarnings("unchecked")
    public List<DomainEvent> getCapturedEvents() {
        return (List<DomainEvent>) (List<?>) Collections.unmodifiableList(new ArrayList<>(capturedEvents));
    }

    /**
     * Enables event emission (for testing emission toggles).
     */
    public void enableEmission() {
        this.emissionEnabled = true;
    }

    /**
     * Disables event emission (for testing emission toggles).
     */
    public void disableEmission() {
        this.emissionEnabled = false;
    }

    /**
     * Gets the number of captured events.
     * 
     * @return the count of captured events
     */
    public int getCapturedEventCount() {
        return capturedEvents.size();
    }

    /**
     * Checks if any events were captured.
     * 
     * @return true if events were captured
     */
    public boolean hasCapturedEvents() {
        return !capturedEvents.isEmpty();
    }

    /**
     * Finds the first captured event of the specified type.
     * 
     * @param eventType the type of event to find
     * @param <T> the event type
     * @return the first matching event, or null if not found
     */
    @SuppressWarnings("unchecked")
    public <T extends DomainEvent> T findFirstEvent(Class<T> eventType) {
        return (T) capturedEvents.stream()
            .filter(eventType::isInstance)
            .findFirst()
            .orElse(null);
    }

    /**
     * Finds all captured events of the specified type.
     * 
     * @param eventType the type of events to find
     * @param <T> the event type
     * @return list of matching events
     */
    @SuppressWarnings("unchecked")
    public <T extends DomainEvent> List<T> findAllEvents(Class<T> eventType) {
        return (List<T>) capturedEvents.stream()
            .filter(eventType::isInstance)
            .toList();
    }

    /**
     * Checks if an event of the specified type was captured.
     * 
     * @param eventType the type of event to check for
     * @return true if an event of this type was captured
     */
    public boolean hasEventOfType(Class<? extends DomainEvent> eventType) {
        return capturedEvents.stream()
            .anyMatch(eventType::isInstance);
    }

    /**
     * Gets all captured events (legacy method for compatibility).
     * 
     * @return list of all captured events
     */
    @SuppressWarnings("unchecked")
    public List<DomainEvent> getAllEvents() {
        return (List<DomainEvent>) (List<?>) Collections.unmodifiableList(new ArrayList<>(capturedEvents));
    }

    /**
     * Clears all captured events.
     * This allows reusing the same emitter for multiple test phases.
     */
    public void clear() {
        capturedEvents.clear();
        emittedEventCount = 0L;
    }

    /**
     * Creates a summary of captured events for debugging.
     * 
     * @return formatted string describing captured events
     */
    public String getSummary() {
        if (capturedEvents.isEmpty()) {
            return "No events captured";
        }
        
        StringBuilder summary = new StringBuilder();
        summary.append("Captured Events (").append(capturedEvents.size()).append("):\n");
        
        for (int i = 0; i < capturedEvents.size(); i++) {
            DomainResponseEvent<?> event = capturedEvents.get(i);
            
            summary.append("  ").append(i + 1).append(". ")
                   .append(event.getClass().getSimpleName())
                   .append("\n");
        }
        
        return summary.toString();
    }
}