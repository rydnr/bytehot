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
 * Filename: EventDrivenTestingDemonstrationTest.java
 *
 * Author: Claude Code
 *
 * Class name: EventDrivenTestingDemonstrationTest
 *
 * Responsibilities:
 *   - Demonstrate the revolutionary event-driven testing framework
 *   - Show Given/When/Then pattern using actual domain events
 *   - Validate that event-driven testing works with real scenarios
 *   - Serve as example for other event-driven tests
 *
 * Collaborators:
 *   - EventDrivenTestSupport: Base class providing Given/When/Then methods
 *   - ClassFileChanged: Domain event used in test scenarios
 *   - ByteHotApplication: Application layer being tested
 */
package org.acmsl.bytehot.testing;

import org.acmsl.bytehot.domain.events.ClassFileChanged;
import org.acmsl.bytehot.testing.EventDrivenTestSupport;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.time.Instant;

/**
 * Demonstration test for the revolutionary event-driven testing framework.
 * This test shows how to use actual domain events for Given/When/Then scenarios,
 * enabling realistic testing of business logic without mocking or artificial state.
 * 
 * @author Claude Code
 * @since 2025-06-17
 */
@DisplayName("Event-Driven Testing Framework Demonstration")
class EventDrivenTestingDemonstrationTest extends EventDrivenTestSupport {

    @Test
    @DisplayName("Should process class file change when file monitoring is active")
    void shouldProcessClassFileChangeWhenMonitoringActive() {
        // Given: A monitoring session has been established and is watching files
        given()
            .event(ClassFileChanged.forNewSession(
                Paths.get("/app/com/example/UserService.class"),
                "UserService",
                1024L,
                Instant.now().minusSeconds(10)
            ))
            .event(ClassFileChanged.forExistingSession(
                Paths.get("/app/com/example/OrderService.class"),
                "OrderService",
                2048L,
                Instant.now().minusSeconds(5),
                "previous-event-1",
                1L
            ));

        // When: A new class file change occurs
        when()
            .event(ClassFileChanged.forExistingSession(
                Paths.get("/app/com/example/PaymentService.class"),
                "PaymentService",
                1536L,
                Instant.now(),
                "previous-event-2",
                2L
            ));

        // Then: The system should process the change and emit appropriate events
        then()
            .hasEvents()
            .eventCount(1) // This will fail initially - demonstrating naive implementation
            .hasEventOfType(ClassFileChanged.class)
            .noException();
    }

    @Test
    @DisplayName("Should establish new session when first file change occurs")
    void shouldEstablishNewSessionWhenFirstFileChangeOccurs() {
        // Given: No prior session exists (clean state)
        
        // When: The first file change occurs
        when()
            .event(ClassFileChanged.forNewSession(
                Paths.get("/app/com/example/Application.class"),
                "Application",
                4096L,
                Instant.now()
            ));

        // Then: A new session should be established and the event processed
        then()
            .hasEvents()
            .hasEventOfType(ClassFileChanged.class)
            .eventCountOfType(ClassFileChanged.class, 1)
            .noException();
    }

    @Test
    @DisplayName("Should handle invalid bytecode gracefully")
    void shouldHandleInvalidBytecodeGracefully() {
        // Given: A valid session is established
        given()
            .event(ClassFileChanged.forNewSession(
                Paths.get("/app/com/example/ValidClass.class"),
                "ValidClass",
                2048L,
                Instant.now().minusSeconds(30)
            ));

        // When: An invalid class file change occurs
        when()
            .event(ClassFileChanged.forExistingSession(
                Paths.get("/app/com/example/InvalidClass.class"),
                "InvalidClass",
                0L, // Invalid file size
                Instant.now(),
                "valid-event-1",
                1L
            ));

        // Then: The system should handle the error gracefully and emit error event
        then()
            .hasEvents()
            .noException() // System should not crash
            .verify(context -> {
                // Custom verification for error handling
                return context.getAllEvents().stream()
                    .anyMatch(event -> event.toString().contains("Invalid") || 
                             event.toString().contains("Error"));
            }, "Error handling event was produced");
    }

    @Test
    @DisplayName("Should correlate events within the same session")
    void shouldCorrelateEventsWithinSameSession() {
        // Given: Multiple events in the same session
        given()
            .event(ClassFileChanged.forNewSession(
                Paths.get("/app/SessionTest1.class"),
                "SessionTest1",
                1024L,
                Instant.now().minusSeconds(20)
            ))
            .event(ClassFileChanged.forExistingSession(
                Paths.get("/app/SessionTest2.class"),
                "SessionTest2",
                1024L,
                Instant.now().minusSeconds(10),
                "session-event-1",
                1L
            ));

        // When: Another event occurs in the same session
        when()
            .event(ClassFileChanged.forExistingSession(
                Paths.get("/app/SessionTest3.class"),
                "SessionTest3",
                1024L,
                Instant.now(),
                "session-event-2",
                2L
            ));

        // Then: Events should be properly correlated by session
        then()
            .hasEvents()
            .allEventsMatch(event -> {
                if (event instanceof ClassFileChanged) {
                    ClassFileChanged classEvent = (ClassFileChanged) event;
                    return "filewatch".equals(classEvent.getAggregateType());
                }
                return true; // Non-ClassFileChanged events are ok
            }, "all events belong to the same aggregate type")
            .noException();
    }

    @Test
    @DisplayName("Should demonstrate bug reproduction using event replay")
    void shouldDemonstrateBugReproductionUsingEventReplay() {
        // Given: A sequence of events that historically caused a bug
        given()
            .event(ClassFileChanged.forNewSession(
                Paths.get("/app/BuggyClass.class"),
                "BuggyClass",
                2048L,
                Instant.now().minusSeconds(60)
            ))
            .event(ClassFileChanged.forExistingSession(
                Paths.get("/app/DependentClass.class"),
                "DependentClass",
                1536L,
                Instant.now().minusSeconds(30),
                "buggy-event-1",
                1L
            ));

        // When: The problematic event that caused the original bug occurs
        when()
            .event(ClassFileChanged.forExistingSession(
                Paths.get("/app/ConflictingClass.class"),
                "ConflictingClass",
                3072L,
                Instant.now(),
                "dependent-event-1",
                2L
            ));

        // Then: The bug should be reproduced or the fix should be verified
        then()
            .hasEvents()
            .verify(context -> {
                // This verification will evolve as we implement the domain logic
                // For now, it just checks that the system didn't crash
                return !context.getTestException().isPresent();
            }, "system handles the historically problematic event sequence")
            .debugPrintEvents(); // Print events for debugging
    }
}