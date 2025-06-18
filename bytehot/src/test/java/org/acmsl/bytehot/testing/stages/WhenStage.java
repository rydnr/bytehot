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
 *   - Send the event under test through the actual application layer
 *   - Capture all resulting events and responses for verification
 *   - Support different types of event triggers (direct, command, external)
 *   - Provide fluent interface for event execution in tests
 *
 * Collaborators:
 *   - EventTestContext: Records the test event and results
 *   - ByteHotApplication: Processes the test event
 *   - EventCapturingEmitter: Captures emitted events during processing
 */
package org.acmsl.bytehot.testing.stages;

import org.acmsl.bytehot.application.ByteHotApplication;
import org.acmsl.bytehot.domain.EventEmitterPort;
import org.acmsl.bytehot.domain.Ports;
import org.acmsl.bytehot.testing.support.EventCapturingEmitter;
import org.acmsl.bytehot.testing.support.EventTestContext;

import org.acmsl.commons.patterns.DomainEvent;
import org.acmsl.commons.patterns.DomainResponseEvent;

import java.util.List;

/**
 * The "When" stage of event-driven testing that sends the event under test.
 * This stage processes events through the actual application layer to ensure
 * realistic testing of business logic and event handling.
 * 
 * @author Claude Code
 * @since 2025-06-17
 */
public class WhenStage {

    /**
     * Test context to record the test event and results
     */
    private final EventTestContext context;

    /**
     * ByteHot application for event processing
     */
    private final ByteHotApplication application;

    /**
     * Creates a new When stage.
     * 
     * @param context the test context to record results
     * @param application the application for event processing
     */
    public WhenStage(EventTestContext context, ByteHotApplication application) {
        this.context = context;
        this.application = application;
    }

    /**
     * Sends the event under test through the application layer.
     * This captures all resulting events and responses for verification.
     * 
     * @param event the event to test
     * @return this stage for fluent chaining
     */
    public WhenStage event(DomainEvent event) {
        try {
            // Record the test event
            context.setTestEvent(event);
            
            // Set up event capturing
            EventCapturingEmitter emitter = new EventCapturingEmitter();
            Ports.getInstance().inject(EventEmitterPort.class, emitter);
            
            // Process the event through the application
            List<DomainResponseEvent<?>> results = application.accept(event);
            
            // Capture the results
            context.setResultingEvents(results);
            context.setEmittedEvents(emitter.getCapturedEvents());
            
            return this;
            
        } catch (Exception e) {
            // Record the exception for verification
            context.setTestException(e);
            return this;
        }
    }

    /**
     * Sends a command that will be converted to a domain event.
     * This allows testing with command objects that generate events.
     * 
     * @param command the command to convert and send
     * @return this stage for fluent chaining
     */
    public WhenStage command(Object command) {
        // Convert command to appropriate domain event
        DomainEvent event = convertCommandToEvent(command);
        return event(event);
    }

    /**
     * Simulates an external trigger that generates events.
     * This is useful for testing file system changes, network events, etc.
     * 
     * @param triggerType the type of external trigger
     * @param params parameters for the trigger
     * @return this stage for fluent chaining
     */
    public WhenStage externalTrigger(String triggerType, Object... params) {
        try {
            DomainEvent event = createExternalEvent(triggerType, params);
            return event(event);
        } catch (Exception e) {
            context.setTestException(e);
            return this;
        }
    }

    /**
     * Simulates a file change event for testing file system monitoring.
     * 
     * @param filePath the path of the changed file
     * @return this stage for fluent chaining
     */
    public WhenStage fileChanged(String filePath) {
        return externalTrigger("FILE_CHANGED", filePath);
    }

    /**
     * Simulates a hot-swap request for testing hot-swap operations.
     * 
     * @param className the name of the class to hot-swap
     * @param newBytecode the new bytecode (can be mock data for testing)
     * @return this stage for fluent chaining
     */
    public WhenStage hotSwapRequested(String className, byte[] newBytecode) {
        return externalTrigger("HOT_SWAP_REQUESTED", className, newBytecode);
    }

    /**
     * Simulates an agent attachment for testing startup scenarios.
     * 
     * @param configuration the configuration for the agent
     * @return this stage for fluent chaining
     */
    public WhenStage agentAttached(Object configuration) {
        return externalTrigger("AGENT_ATTACHED", configuration);
    }

    /**
     * Sends multiple events in sequence and captures all results.
     * This is useful for testing event chains or complex scenarios.
     * 
     * @param events the events to send in sequence
     * @return this stage for fluent chaining
     */
    public WhenStage events(DomainEvent... events) {
        for (DomainEvent event : events) {
            event(event);
        }
        return this;
    }

    /**
     * Delays execution for testing timing-sensitive scenarios.
     * This can be useful for testing timeouts or asynchronous operations.
     * 
     * @param milliseconds the delay in milliseconds
     * @return this stage for fluent chaining
     */
    public WhenStage delay(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            context.setTestException(e);
        }
        return this;
    }

    /**
     * Executes a custom action during the When phase.
     * This allows for complex test scenarios that require custom logic.
     * 
     * @param action the action to execute
     * @return this stage for fluent chaining
     */
    public WhenStage execute(Runnable action) {
        try {
            action.run();
        } catch (Exception e) {
            context.setTestException(e);
        }
        return this;
    }

    /**
     * Gets the number of events that were produced by the test.
     * This can be useful for immediate verification or debugging.
     * 
     * @return the total number of resulting and emitted events
     */
    public int getResultCount() {
        return context.getResultCount();
    }

    /**
     * Checks if any events were produced by the test.
     * 
     * @return true if there are resulting or emitted events
     */
    public boolean hasResults() {
        return context.hasResults();
    }

    /**
     * Checks if an exception occurred during event processing.
     * 
     * @return true if an exception was recorded
     */
    public boolean hasException() {
        return context.getTestException().isPresent();
    }

    /**
     * Gets a summary of the results for debugging.
     * 
     * @return a formatted string describing the test results
     */
    public String getResultSummary() {
        if (!hasResults()) {
            return "No events produced";
        }
        
        StringBuilder summary = new StringBuilder();
        summary.append("Results: ");
        summary.append(context.getResultingEvents().size()).append(" direct, ");
        summary.append(context.getEmittedEvents().size()).append(" emitted");
        
        if (hasException()) {
            summary.append(" (with exception: ").append(context.getTestException().get().getMessage()).append(")");
        }
        
        return summary.toString();
    }

    /**
     * Converts a command object to a domain event.
     * This is a simplified implementation - in practice, this would use
     * a proper command-to-event mapping system.
     * 
     * @param command the command to convert
     * @return the corresponding domain event
     */
    private DomainEvent convertCommandToEvent(Object command) {
        // This is a placeholder implementation
        // In practice, this would use a proper command-to-event converter
        throw new UnsupportedOperationException("Command conversion not yet implemented");
    }

    /**
     * Creates an external event based on trigger type and parameters.
     * This factory method creates appropriate events for external triggers.
     * 
     * @param triggerType the type of trigger
     * @param params the trigger parameters
     * @return the created event
     */
    private DomainEvent createExternalEvent(String triggerType, Object... params) {
        // This is a placeholder implementation
        // In practice, this would create appropriate events based on trigger type
        throw new UnsupportedOperationException("External event creation not yet implemented");
    }
}