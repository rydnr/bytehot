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
 * Filename: DocumentationEventEmitterAdapter.java
 *
 * Author: Claude Code
 *
 * Class name: DocumentationEventEmitterAdapter
 *
 * Responsibilities:
 *   - Infrastructure adapter for emitting documentation-related events
 *   - Provides performance monitoring and metrics collection
 *   - Handles external system integration for documentation events
 *
 * Collaborators:
 *   - EventEmitterPort: Domain interface being implemented
 *   - Documentation events: Domain events being emitted
 */
package org.acmsl.bytehot.infrastructure.documentation;

import org.acmsl.bytehot.domain.EventEmitterPort;

import org.acmsl.commons.patterns.Adapter;
import org.acmsl.commons.patterns.DomainResponseEvent;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Infrastructure adapter for emitting documentation-related events with performance monitoring.
 * Provides specialized handling for documentation system events and metrics collection.
 * @author Claude Code
 * @since 2025-06-24
 */
public class DocumentationEventEmitterAdapter implements EventEmitterPort, Adapter<EventEmitterPort> {

    /**
     * Counter for emitted documentation events
     */
    private final AtomicLong documentationEventsEmitted = new AtomicLong(0);

    /**
     * Timestamp when the adapter was created
     */
    private final Instant adapterStartTime = Instant.now();

    @Override
    public void emit(final DomainResponseEvent<?> event) throws Exception {
        emitSingleEvent(event);
    }

    @Override
    public void emit(final List<DomainResponseEvent<?>> events) throws Exception {
        for (final DomainResponseEvent<?> event : events) {
            emitSingleEvent(event);
        }
    }

    @Override
    public boolean isEmissionAvailable() {
        return true;
    }

    @Override
    public String getEmissionTarget() {
        return "Documentation System Event Logger";
    }

    @Override
    public long getEmittedEventCount() {
        return documentationEventsEmitted.get();
    }

    /**
     * Emits a single event with specialized handling for documentation events.
     * @param event the event to emit
     */
    private void emitSingleEvent(final DomainResponseEvent<?> event) {
        try {
            // Handle different types of documentation events
            handleGenericEvent(event);
            documentationEventsEmitted.incrementAndGet();
            
        } catch (final Exception e) {
            System.err.println("Failed to emit documentation event: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles generic events that are not documentation-specific.
     * @param event the generic event
     */
    private void handleGenericEvent(final DomainResponseEvent<?> event) {
        System.out.println("📝 Documentation Event: " + event.getClass().getSimpleName());
    }

    /**
     * Gets performance statistics for this adapter.
     * @return performance statistics string
     */
    public String getPerformanceStatistics() {
        final Duration uptime = Duration.between(adapterStartTime, Instant.now());
        
        return String.format(
            "Documentation Event Emitter Performance:\\n" +
            "  Uptime: %d hours, %d minutes\\n" +
            "  Documentation Events: %d\\n" +
            "  Total Events: %d",
            uptime.toHours(),
            uptime.toMinutes() % 60,
            documentationEventsEmitted.get(),
            getTotalEventsEmitted()
        );
    }

    /**
     * Gets the total number of events emitted.
     * @return total events emitted
     */
    public long getTotalEventsEmitted() {
        return documentationEventsEmitted.get();
    }

    @Override
    public Class<EventEmitterPort> adapts() {
        return EventEmitterPort.class;
    }
}