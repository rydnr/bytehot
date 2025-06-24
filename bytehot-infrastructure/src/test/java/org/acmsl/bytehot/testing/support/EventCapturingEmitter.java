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
 *   - Capture domain events emitted during test execution
 *   - Provide test implementation of EventEmitterPort
 *   - Support thread-safe event capture for concurrent testing
 *
 * Collaborators:
 *   - EventTestContext: Context where captured events are stored
 *   - DomainEvent: Events being captured during tests
 *   - EventEmitterPort: Interface being implemented for testing
 */
package org.acmsl.bytehot.testing.support;

import org.acmsl.bytehot.domain.EventEmitterPort;
import org.acmsl.commons.patterns.DomainEvent;
import org.acmsl.commons.patterns.DomainResponseEvent;
import org.acmsl.commons.patterns.Test;

import java.util.List;

/**
 * Test implementation of EventEmitterPort that captures all emitted events
 * for verification in event-driven tests. This emitter stores events in
 * the test context instead of actually emitting them to external systems.
 * @author Claude Code
 * @since 2025-06-23
 */
public class EventCapturingEmitter
    implements EventEmitterPort, Test {

    /**
     * The test context where captured events are stored.
     */
    private final EventTestContext context;

    /**
     * Flag to control whether events should be captured.
     * This can be useful for testing error scenarios.
     */
    private boolean captureEnabled;

    /**
     * Creates a new event capturing emitter.
     * @param context the test context to store captured events
     */
    public EventCapturingEmitter(final EventTestContext context) {
        this.context = context;
        this.captureEnabled = true;
    }

    /**
     * Emits a single domain response event by capturing it in the test context.
     * @param event the domain response event to emit
     * @throws Exception if emission fails (never in test implementation)
     */
    @Override
    public void emit(final DomainResponseEvent<?> event) throws Exception {
        if (captureEnabled && event != null) {
            context.addEmittedEvent(event);
        }
    }

    /**
     * Emits multiple domain response events by capturing them in the test context.
     * @param events the list of domain response events to emit
     * @throws Exception if emission fails (never in test implementation)
     */
    @Override
    public void emit(final List<DomainResponseEvent<?>> events) throws Exception {
        if (captureEnabled && events != null) {
            for (final DomainResponseEvent<?> event : events) {
                if (event != null) {
                    context.addEmittedEvent(event);
                }
            }
        }
    }

    /**
     * Enables or disables event capture.
     * When disabled, events will be ignored instead of captured.
     * @param enabled true to enable capture, false to disable
     */
    public void setCaptureEnabled(final boolean enabled) {
        this.captureEnabled = enabled;
    }

    /**
     * Checks if event capture is currently enabled.
     * @return true if events are being captured
     */
    public boolean isCaptureEnabled() {
        return captureEnabled;
    }

    /**
     * Gets the test context associated with this emitter.
     * @return the test context where events are captured
     */
    public EventTestContext getContext() {
        return context;
    }

    /**
     * Checks if event emission is available (always true for test implementation).
     * @return true always
     */
    @Override
    public boolean isEmissionAvailable() {
        return true;
    }

    /**
     * Returns the emission target description for testing.
     * @return description of the test emission target
     */
    @Override
    public String getEmissionTarget() {
        return "Test Event Capture (EventTestContext)";
    }

    /**
     * Returns the number of events emitted.
     * @return count of emitted events
     */
    @Override
    public long getEmittedEventCount() {
        return context.getEventCount();
    }

    /**
     * Gets the number of events captured by this emitter.
     * This is a convenience method that delegates to the context.
     * @return the count of captured events
     */
    public int getCapturedEventCount() {
        return context.getEventCount();
    }

    /**
     * Clears all captured events from the test context.
     * This can be useful for multi-phase test scenarios.
     */
    public void clearCapturedEvents() {
        context.clearEmittedEvents();
    }

    /**
     * Gets all events captured by this emitter.
     * This is a convenience method that delegates to the context.
     * @return list of all captured domain events
     */
    public List<DomainEvent> getCapturedEvents() {
        return context.getEmittedEvents();
    }

    /**
     * Temporarily disables event capture, executes the given runnable,
     * and then restores the previous capture state. This is useful for
     * testing scenarios where you want to perform actions without
     * capturing their events.
     * @param action the action to execute with capture disabled
     */
    public void withCaptureDisabled(final Runnable action) {
        final boolean previousState = captureEnabled;
        try {
            captureEnabled = false;
            action.run();
        } finally {
            captureEnabled = previousState;
        }
    }

    @Override
    public String toString() {
        return String.format(
            "EventCapturingEmitter[captureEnabled=%s, capturedEvents=%d]",
            captureEnabled,
            getCapturedEventCount()
        );
    }
}