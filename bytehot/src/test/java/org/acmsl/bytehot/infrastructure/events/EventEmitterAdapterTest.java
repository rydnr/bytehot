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
 * Filename: EventEmitterAdapterTest.java
 *
 * Author: Claude Code
 *
 * Class name: EventEmitterAdapterTest
 *
 * Responsibilities:
 *   - Test the EventEmitterAdapter infrastructure implementation
 *   - Verify event emission functionality
 *   - Ensure proper port interface implementation
 *
 * Collaborators:
 *   - EventEmitterAdapter: The adapter being tested
 *   - EventEmitterPort: The port interface
 */
package org.acmsl.bytehot.infrastructure.events;

import org.acmsl.bytehot.domain.EventEmitterPort;
import org.acmsl.bytehot.domain.events.ByteHotAgentAttached;
import org.acmsl.bytehot.domain.events.ByteHotAttachRequested;
import org.acmsl.bytehot.domain.WatchConfiguration;

import org.acmsl.commons.patterns.DomainEvent;
import org.acmsl.commons.patterns.DomainResponseEvent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.instrument.Instrumentation;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for EventEmitterAdapter.
 * @author Claude Code
 * @since 2025-06-17
 */
public class EventEmitterAdapterTest {

    /**
     * The adapter under test
     */
    private EventEmitterAdapter adapter;

    /**
     * Sets up test environment
     */
    @BeforeEach
    public void setUp() {
        adapter = new EventEmitterAdapter();
    }

    /**
     * Tests that the adapter correctly implements the port interface
     */
    @Test
    public void adapts_returns_event_emitter_port() {
        assertEquals(EventEmitterPort.class, adapter.adapts());
    }

    /**
     * Tests emission availability check
     */
    @Test
    public void isEmissionAvailable_returns_true_for_console() {
        assertTrue(adapter.isEmissionAvailable());
    }

    /**
     * Tests emission target description
     */
    @Test
    public void getEmissionTarget_returns_description() {
        final String target = adapter.getEmissionTarget();
        
        assertNotNull(target);
        assertTrue(target.length() > 0);
    }

    /**
     * Tests event counter functionality
     */
    @Test
    public void getEmittedEventCount_starts_at_zero() {
        assertEquals(0, adapter.getEmittedEventCount());
    }

    /**
     * Tests emitting a single event
     */
    @Test
    public void emit_single_event_increases_counter() throws Exception {
        // Create a simple test event
        final ByteHotAttachRequested precedingEvent = new ByteHotAttachRequested(
            new WatchConfiguration(8080), 
            null // Mock instrumentation not needed for this test
        );
        final ByteHotAgentAttached testEvent = new ByteHotAgentAttached(
            precedingEvent, 
            new WatchConfiguration(8080)
        );
        
        adapter.emit(testEvent);
        
        assertEquals(1, adapter.getEmittedEventCount());
    }

    /**
     * Tests emitting multiple events
     */
    @Test 
    public void emit_multiple_events_increases_counter() throws Exception {
        // Create test events
        final ByteHotAttachRequested precedingEvent = new ByteHotAttachRequested(
            new WatchConfiguration(8080), 
            null
        );
        final ByteHotAgentAttached event1 = new ByteHotAgentAttached(
            precedingEvent, 
            new WatchConfiguration(8080)
        );
        final ByteHotAgentAttached event2 = new ByteHotAgentAttached(
            precedingEvent, 
            new WatchConfiguration(8080)
        );
        
        adapter.emit(List.of(event1, event2));
        
        assertEquals(2, adapter.getEmittedEventCount());
    }
}