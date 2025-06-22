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
 *   - Base class for revolutionary event-driven testing
 *   - Provides Given/When/Then pattern using domain events
 *   - Enables realistic testing scenarios with event sequences
 *   - Foundation for bug reproduction from event snapshots
 *
 * Collaborators:
 *   - EventTestContext: Test execution context and state
 *   - GivenStage: Building system state from events
 *   - WhenStage: Sending test events
 *   - ThenStage: Verifying expected results
 *   - EventStorePort: Event persistence for test scenarios
 */
package org.acmsl.bytehot.testing;

import org.acmsl.bytehot.application.ByteHotApplication;
import org.acmsl.bytehot.domain.EventStorePort;
import org.acmsl.bytehot.domain.Ports;
import org.acmsl.bytehot.testing.stages.GivenStage;
import org.acmsl.bytehot.testing.stages.ThenStage;
import org.acmsl.bytehot.testing.stages.WhenStage;
import org.acmsl.bytehot.testing.support.BugReport;
import org.acmsl.bytehot.testing.support.BugReproductionStage;
import org.acmsl.bytehot.testing.support.EventTestContext;
import org.acmsl.bytehot.testing.support.InMemoryEventStoreAdapter;

import org.junit.jupiter.api.BeforeEach;

/**
 * Base class for revolutionary event-driven testing that transforms traditional testing
 * into event-centric scenarios. This enables:
 * 
 * - Given: System state built from realistic event sequences
 * - When: Test events sent through the actual application layer
 * - Then: Verification of expected resulting events
 * - Bug Reproduction: Automatic test generation from bug reports
 * 
 * @author Claude Code
 * @since 2025-06-17
 */
public abstract class EventDrivenTestSupport {

    /**
     * Test execution context containing all test state and events
     */
    protected EventTestContext context;

    /**
     * Event store for test scenarios (in-memory for isolation)
     */
    protected EventStorePort eventStore;

    /**
     * ByteHot application for event processing
     */
    protected ByteHotApplication application;

    /**
     * Sets up the event-driven testing environment before each test.
     * Creates isolated test context with in-memory event storage.
     */
    @BeforeEach
    void setupEventDrivenTest() {
        // Create isolated test context
        this.context = new EventTestContext();
        
        // Use in-memory event store for test isolation
        this.eventStore = new InMemoryEventStoreAdapter();
        
        // Create application instance for event processing
        this.application = ByteHotApplication.getInstance();
        
        // Wire the test event store (temporarily override production store)
        Ports.getInstance().inject(EventStorePort.class, (InMemoryEventStoreAdapter) eventStore);
        
        // Application is ready to use after adapter injection
        // No additional initialization needed for testing
    }

    /**
     * Starts the "Given" phase - building system state from events.
     * 
     * This allows tests to establish realistic system state by replaying
     * actual domain events rather than mocking objects.
     * 
     * @return GivenStage for fluent event building
     */
    protected GivenStage given() {
        return new GivenStage(context, eventStore);
    }

    /**
     * Starts the "When" phase - sending the event under test.
     * 
     * This sends events through the actual application layer to test
     * real event processing and business logic.
     * 
     * @return WhenStage for event execution
     */
    protected WhenStage when() {
        return new WhenStage(context, application);
    }

    /**
     * Starts the "Then" phase - verifying expected results.
     * 
     * This verifies that the expected events were produced as a result
     * of the test scenario, enabling comprehensive result validation.
     * 
     * @return ThenStage for result verification
     */
    protected ThenStage then() {
        return new ThenStage(context);
    }

    /**
     * Creates a test scenario from a bug report for reproduction.
     * 
     * This revolutionary feature allows bugs to be automatically reproduced
     * by replaying the exact event sequence that led to the error.
     * 
     * @param bugReport the bug report containing reproduction events
     * @return BugReproductionStage for bug reproduction testing
     */
    protected BugReproductionStage reproduce(BugReport bugReport) {
        return new BugReproductionStage(bugReport, context, eventStore, application);
    }

    /**
     * Gets the current test context for advanced scenarios.
     * Useful for custom assertions or complex test setup.
     * 
     * @return the current event test context
     */
    protected EventTestContext getTestContext() {
        return context;
    }

    /**
     * Gets the test event store for direct access if needed.
     * Useful for advanced event querying or debugging.
     * 
     * @return the in-memory event store used for testing
     */
    protected EventStorePort getTestEventStore() {
        return eventStore;
    }

    /**
     * Clears all test state for complex multi-scenario tests.
     * This resets the context while preserving the test setup.
     */
    protected void clearTestState() {
        this.context = new EventTestContext();
        // Note: We keep the same eventStore instance for consistency
    }
}