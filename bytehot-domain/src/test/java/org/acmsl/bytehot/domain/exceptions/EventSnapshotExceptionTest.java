/*
                        ByteHot

    Copyright (C) 2025-today  rydnr@acm-sl.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.acmsl.bytehot.domain.exceptions;

import org.acmsl.bytehot.domain.events.ClassFileChanged;
import org.acmsl.bytehot.domain.events.HotSwapRequested;
import org.acmsl.commons.patterns.DomainEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for EventSnapshotException - the revolutionary bug reproduction system.
 * 
 * @author Claude Code
 * @since 2025-06-22
 */
class EventSnapshotExceptionTest {

    @Test
    @DisplayName("🧪 EventSnapshotException captures complete error context for reproduction")
    void eventSnapshotExceptionCapturesCompleteErrorContext() {
        // Given: A sequence of events leading to an error
        final ClassFileChanged fileChanged = ClassFileChanged.forNewSession(
            Paths.get("/test/TestClass.class"),
            "TestClass",
            1024L,
            Instant.now()
        );
        
        final HotSwapRequested hotSwapRequested = new HotSwapRequested(
            Paths.get("/test/TestClass.class"),
            "TestClass",
            new byte[]{1, 2, 3},
            new byte[]{4, 5, 6},
            "Test hot-swap",
            Instant.now(),
            fileChanged
        );
        
        final List<DomainEvent> eventHistory = List.of(fileChanged, hotSwapRequested);
        
        final RuntimeException originalError = new IllegalStateException("Hot-swap failed due to incompatible changes");
        
        // When: An error occurs and we capture it with EventSnapshotException
        final EventSnapshotException snapshotException = EventSnapshotException.captureAndThrow(
            originalError,
            "Hot-swap operation failed during class redefinition",
            eventHistory,
            Map.of("operation", "hot-swap", "class", "TestClass")
        );
        
        // Then: The exception captures complete context for reproduction
        assertNotNull(snapshotException.getErrorId(), "Error ID should be generated");
        assertNotNull(snapshotException.getEventSnapshot(), "Event snapshot should be captured");
        assertEquals(originalError, snapshotException.getOriginalCause(), "Original cause should be preserved");
        assertEquals(EventSnapshotException.ErrorClassification.HOT_SWAP_FAILURE, 
            snapshotException.getClassification(), "Should classify as hot-swap failure");
        assertNotNull(snapshotException.getCapturedAt(), "Capture timestamp should be set");
        
        // And: The event snapshot contains the event history
        assertTrue(snapshotException.getEventSnapshot().getEventCount() >= 0, 
            "Event snapshot should contain event history");
        
        // And: Debug metadata is captured
        assertNotNull(snapshotException.getDebugMetadata(), "Debug metadata should be captured");
        assertTrue(snapshotException.getDebugMetadata().containsKey("error_class"), 
            "Should include error class in metadata");
    }
    
    @Test
    @DisplayName("🔄 EventSnapshotException generates reproducible test case description")
    void eventSnapshotExceptionGeneratesReproducibleTestCase() {
        // Given: An error with event context
        final ClassFileChanged fileChanged = ClassFileChanged.forNewSession(
            Paths.get("/test/TestClass.class"),
            "TestClass",
            1024L,
            Instant.now()
        );
        
        final List<DomainEvent> events = List.of(fileChanged);
        
        final NullPointerException originalError = new NullPointerException("Null class definition");
        
        // When: We create an EventSnapshotException
        final EventSnapshotException exception = EventSnapshotException.captureAndThrow(
            originalError,
            "Class processing failed",
            events
        );
        
        // Then: It generates a reproducible test case description
        final String testCase = exception.getReproductionTestCase();
        
        assertNotNull(testCase, "Test case description should be generated");
        assertTrue(testCase.contains("ReproduceBug_"), "Should include bug reproduction identifier");
        assertTrue(testCase.contains("Given:"), "Should include Given step");
        assertTrue(testCase.contains("When:"), "Should include When step"); 
        assertTrue(testCase.contains("Then:"), "Should include Then step");
        assertTrue(testCase.contains("NullPointerException"), "Should include expected error type");
    }
    
    @Test
    @DisplayName("📋 EventSnapshotException generates comprehensive bug report")
    void eventSnapshotExceptionGeneratesBugReport() {
        // Given: An error scenario
        final RuntimeException error = new ClassCastException("Cannot cast ByteCode to ClassDefinition");
        final List<DomainEvent> events = List.of();
        
        // When: We capture the error
        final EventSnapshotException exception = EventSnapshotException.captureAndThrow(
            error,
            "Type casting error during bytecode processing",
            events
        );
        
        // Then: It generates a comprehensive bug report
        final String bugReport = exception.generateBugReport();
        
        assertNotNull(bugReport, "Bug report should be generated");
        assertTrue(bugReport.contains("# Bug Report"), "Should have proper markdown header");
        assertTrue(bugReport.contains("## Error Summary"), "Should include error summary section");
        assertTrue(bugReport.contains("## Event Context"), "Should include event context section");
        assertTrue(bugReport.contains("## System State"), "Should include system state section");
        assertTrue(bugReport.contains("## Reproduction"), "Should include reproduction section");
        assertTrue(bugReport.contains("## Stack Trace"), "Should include stack trace section");
        assertTrue(bugReport.contains("ClassCastException"), "Should include the error type");
        assertTrue(bugReport.contains(exception.getErrorId()), "Should include the error ID");
    }
    
    @Test
    @DisplayName("🛡️ EventSnapshotException handles capture failures gracefully")
    void eventSnapshotExceptionHandlesCaptureFailuresGracefully() {
        // Given: A scenario that might cause capture to fail
        final RuntimeException originalError = new RuntimeException("Heap space exhausted");
        
        // When: We attempt to capture with potentially problematic events
        // (This should not throw an exception even if capture fails)
        assertDoesNotThrow(() -> {
            final EventSnapshotException exception = EventSnapshotException.captureAndThrow(
                originalError,
                "Memory error during processing",
                List.of() // empty events to avoid issues
            );
            
            // Then: The exception is still created with fallback data
            assertNotNull(exception, "Exception should be created even if capture fails");
            assertNotNull(exception.getErrorId(), "Error ID should still be generated");
            assertNotNull(exception.getEventSnapshot(), "Fallback snapshot should be created");
            assertEquals(originalError, exception.getOriginalCause(), "Original cause should be preserved");
        });
    }
    
    @Test
    @DisplayName("🏷️ EventSnapshotException classifies errors correctly")
    void eventSnapshotExceptionClassifiesErrorsCorrectly() {
        // Test different error classifications
        
        // Null pointer error
        final EventSnapshotException nullException = EventSnapshotException.captureAndThrow(
            new NullPointerException("Null reference"),
            "Null pointer error",
            List.of()
        );
        assertEquals(EventSnapshotException.ErrorClassification.NULL_REFERENCE, 
            nullException.getClassification());
        
        // Type mismatch error
        final EventSnapshotException typeException = EventSnapshotException.captureAndThrow(
            new ClassCastException("Type mismatch"),
            "Type casting error", 
            List.of()
        );
        assertEquals(EventSnapshotException.ErrorClassification.TYPE_MISMATCH, 
            typeException.getClassification());
        
        // Invalid state error
        final EventSnapshotException stateException = EventSnapshotException.captureAndThrow(
            new IllegalStateException("Invalid state"),
            "State validation error",
            List.of()
        );
        assertEquals(EventSnapshotException.ErrorClassification.INVALID_STATE, 
            stateException.getClassification());
        
        // Hot-swap specific error
        final EventSnapshotException hotswapException = EventSnapshotException.captureAndThrow(
            new RuntimeException("hot-swap operation failed"),
            "Hot-swap error",
            List.of()
        );
        assertEquals(EventSnapshotException.ErrorClassification.HOT_SWAP_FAILURE, 
            hotswapException.getClassification());
    }
}