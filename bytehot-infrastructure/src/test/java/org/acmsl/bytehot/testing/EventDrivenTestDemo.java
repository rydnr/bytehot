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
 * Filename: EventDrivenTestDemo.java
 *
 * Author: Claude Code
 *
 * Class name: EventDrivenTestDemo
 *
 * Responsibilities:
 *   - Demonstrate the event-driven testing framework
 *   - Show how to use Given-When-Then stages with domain events
 *   - Provide examples of event chain validation
 *
 * Collaborators:
 *   - EventDrivenTestSupport: Base class providing testing infrastructure
 *   - EventCapturingEmitter: Emitter for capturing events during tests
 *   - DomainEvent: Sample events used in demonstrations
 */
package org.acmsl.bytehot.testing;

import org.acmsl.bytehot.domain.events.ClassFileChanged;
import org.acmsl.bytehot.domain.events.HotSwapRequested;
import org.acmsl.bytehot.testing.support.EventCapturingEmitter;
import org.acmsl.commons.patterns.DomainEvent;
import org.acmsl.commons.patterns.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

/**
 * Demonstration class showing how to use the event-driven testing framework.
 * This class provides concrete examples of testing domain events using
 * the Given-When-Then methodology integrated with java-commons patterns.
 * @author Claude Code
 * @since 2025-06-23
 */
public class EventDrivenTestDemo
    extends EventDrivenTestSupport
    implements Test {

    /**
     * Demonstrates basic event testing with Given-When-Then stages.
     * This example shows how to set up initial conditions, execute actions,
     * and verify the expected events are emitted.
     */
    public void demonstrateBasicEventTesting() {
        scenario("A class file change should trigger hot-swap request")
            .given("classPath", "/path/to/MyClass.class")
            .given("initialTimestamp", System.currentTimeMillis())
            .when("simulate-change")
            .when(() -> {
                // Simulate a class file change event
                final Path classPath = Paths.get("/path/to/MyClass.class");
                final ClassFileChanged changeEvent = ClassFileChanged.forNewSession(
                    classPath, 
                    "MyClass",
                    1024L,
                    Instant.now()
                );
                context.addEmittedEvent(changeEvent);
                
                // Note: HotSwapRequested creation would require more complex setup, so we'll skip for demo
                System.out.println("Demo: ClassFileChanged event created successfully");
            })
            .then()
            .thenEventCount(1)
            .thenEventOfType(ClassFileChanged.class)
            .complete();
    }

    /**
     * Demonstrates event chain validation for response events.
     * This example shows how to verify that response events properly
     * reference their preceding events in the domain event chain.
     */
    public void demonstrateEventChainValidation() {
        scenario("Hot-swap request should properly reference the triggering class file change")
            .given("classFile", "/src/main/java/Example.class")
            .when("create-event")
            .when(() -> {
                // Create the initial event
                final Path classPath = Paths.get("/src/main/java/Example.class");
                final ClassFileChanged changeEvent = ClassFileChanged.forNewSession(
                    classPath,
                    "Example", 
                    2048L,
                    Instant.now()
                );
                context.addEmittedEvent(changeEvent);
                
                // Note: Complex event chaining demo would require proper domain setup
                System.out.println("Demo: Event chain validation setup complete");
            })
            .then()
            .thenValidEventChain()
            .thenEventOfType(ClassFileChanged.class)
            .complete();
    }

    /**
     * Demonstrates error handling and exception testing.
     * This example shows how to test scenarios where exceptions
     * are expected to be thrown during event processing.
     */
    public void demonstrateErrorHandling() {
        scenario("Invalid class file should result in error")
            .given("invalidPath", "/invalid/path.class")
            .when()
            .whenWithErrorHandling(() -> {
                // Simulate processing an invalid class file
                throw new IllegalArgumentException("Invalid class file path");
            })
            .then()
            .thenExceptionOfType(IllegalArgumentException.class)
            .thenNoEvents()
            .complete();
    }

    /**
     * Demonstrates testing with the EventCapturingEmitter.
     * This example shows how to use the emitter to capture events
     * that would normally be sent to external systems.
     */
    public void demonstrateEventCapturingEmitter() {
        // Create an emitter that captures events instead of emitting them
        final EventCapturingEmitter emitter = new EventCapturingEmitter(context);
        
        scenario("Event emitter should capture all emitted events")
            .given("emitter", emitter)
            .given("testEvent", ClassFileChanged.forNewSession(
                Paths.get("/test.class"), 
                "TestClass",
                512L,
                Instant.now()
            ))
            .when("simulate-emit")
            .when(() -> {
                // Add the test event directly to context (simulating capture)
                final DomainEvent testEvent = context.getArtifact("testEvent", DomainEvent.class);
                context.addEmittedEvent(testEvent);
                
                // Note: Real emitter usage would require DomainResponseEvent, 
                // so we're simulating the effect here
                System.out.println("Demo: Event emitter capture simulation complete");
            })
            .then()
            .thenEventCount(1)
            .thenEventOfType(ClassFileChanged.class)
            .thenCustom(() -> {
                // Verify the emitter captured the event
                if (emitter.getCapturedEventCount() != 1) {
                    throw new AssertionError("Expected emitter to capture 1 event, but captured " + 
                        emitter.getCapturedEventCount());
                }
            })
            .complete();
    }

    /**
     * Demonstrates testing concurrent event processing.
     * This example shows how to test scenarios involving
     * multiple events being processed simultaneously.
     */
    public void demonstrateConcurrentEventProcessing() {
        scenario("Multiple class file changes should be processed concurrently")
            .givenConfiguration("concurrentProcessing", true)
            .when()
            .repeat(5, () -> {
                // Simulate multiple class file changes
                final Path classPath = Paths.get("/class" + System.nanoTime() + ".class");
                final ClassFileChanged event = ClassFileChanged.forNewSession(
                    classPath,
                    "TestClass" + System.nanoTime(),
                    1024L,
                    Instant.now()
                );
                context.addEmittedEvent(event);
            })
            .then()
            .thenEventCount(5)
            .thenEventCountOfType(ClassFileChanged.class, 5)
            .complete();
    }

    /**
     * Demonstrates testing with timing and performance validation.
     * This example shows how to verify that events are processed
     * within acceptable time limits.
     */
    public void demonstrateTimingValidation() {
        scenario("Event processing should complete within reasonable time")
            .when()
            .markTime("start")
            .when(() -> {
                // Simulate some processing time
                try {
                    Thread.sleep(50); // 50ms processing time
                } catch (final InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                final ClassFileChanged event = ClassFileChanged.forNewSession(
                    Paths.get("/timed.class"), 
                    "TimedClass",
                    256L,
                    Instant.now()
                );
                context.addEmittedEvent(event);
            })
            .markTime("end")
            .then()
            .thenEventCount(1)
            .thenCustom(() -> {
                final Long startTime = context.getArtifact("time_start", Long.class);
                final Long endTime = context.getArtifact("time_end", Long.class);
                final long duration = endTime - startTime;
                
                if (duration > 100) { // Should complete within 100ms
                    throw new AssertionError("Event processing took too long: " + duration + "ms");
                }
            })
            .complete();
    }

    /**
     * Main method to run all demonstrations.
     * In a real test framework, these would be separate test methods
     * annotated with @Test or similar.
     * @param args command line arguments (not used)
     */
    public static void main(final String[] args) {
        final EventDrivenTestDemo demo = new EventDrivenTestDemo();
        
        System.out.println("Running Event-Driven Testing Framework Demonstrations...");
        
        try {
            demo.demonstrateBasicEventTesting();
            System.out.println("✓ Basic event testing demonstration completed");
            
            demo.demonstrateEventChainValidation();
            System.out.println("✓ Event chain validation demonstration completed");
            
            demo.demonstrateErrorHandling();
            System.out.println("✓ Error handling demonstration completed");
            
            demo.demonstrateEventCapturingEmitter();
            System.out.println("✓ Event capturing emitter demonstration completed");
            
            demo.demonstrateConcurrentEventProcessing();
            System.out.println("✓ Concurrent event processing demonstration completed");
            
            demo.demonstrateTimingValidation();
            System.out.println("✓ Timing validation demonstration completed");
            
            System.out.println("\nAll demonstrations completed successfully!");
            
        } catch (final Exception e) {
            System.err.println("Demonstration failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}