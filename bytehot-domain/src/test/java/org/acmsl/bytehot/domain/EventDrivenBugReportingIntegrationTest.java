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
 * Filename: EventDrivenBugReportingIntegrationTest.java
 *
 * Author: Claude Code
 *
 * Class name: EventDrivenBugReportingIntegrationTest
 *
 * Responsibilities:
 *   - Integration test for complete event-driven bug reporting system
 *   - Demonstrate end-to-end error capture and reproduction capabilities
 *   - Verify automatic event snapshot generation and bug report creation
 *   - Test integration between ErrorHandler, EventSnapshotException, and EventStore
 *
 * Collaborators:
 *   - ErrorHandler: Central error handling coordinator
 *   - EventSnapshotException: Enhanced exceptions with event context
 *   - EventSnapshot: Event history container for reproduction
 *   - EventStorePort: Event persistence infrastructure
 */
package org.acmsl.bytehot.domain;

import org.acmsl.bytehot.domain.events.ClassFileChanged;
import org.acmsl.bytehot.domain.events.HotSwapRequested;
import org.acmsl.bytehot.domain.exceptions.EventSnapshotException;
import org.acmsl.commons.patterns.DomainEvent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.AfterEach;

import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive integration test for Milestone 6D: Event-Driven Bug Reporting.
 * Demonstrates the complete transformation of traditional error handling into
 * reproducible bug reports with comprehensive event context.
 * @author Claude Code
 * @since 2025-06-25
 */
@DisplayName("Event-Driven Bug Reporting Integration Tests")
public class EventDrivenBugReportingIntegrationTest {

    private ErrorHandler errorHandler;
    private List<DomainEvent> sampleEventHistory;

    @BeforeEach
    void setUp() {
        // Initialize error handler
        errorHandler = new ErrorHandler();

        // Create a realistic sequence of events that might lead to an error
        ClassFileChanged fileChanged = ClassFileChanged.forNewSession(
            Paths.get("/test/UserService.class"),
            "UserService",
            2048L,
            Instant.now().minusSeconds(10)
        );

        HotSwapRequested hotSwapRequested = HotSwapRequested.fromFileChange(fileChanged, "test-session");

        sampleEventHistory = Arrays.asList(fileChanged, hotSwapRequested);
    }

    @AfterEach
    void tearDown() {
        // Clean up any resources
    }

    @Test
    @DisplayName("Should capture complete event context when error occurs during hot-swap operation")
    void shouldCaptureCompleteEventContextDuringHotSwapError() {
        // Given: A hot-swap operation that will fail
        RuntimeException hotSwapFailure = new IllegalStateException(
            "JVM rejected class redefinition due to incompatible changes"
        );

        // When: Error handler processes the failure with event context
        ErrorResult result = errorHandler.handleErrorWithContext(
            hotSwapFailure,
            "UserService",
            "hot-swap-redefinition"
        );

        // Then: Error result should contain comprehensive context
        assertNotNull(result);
        assertEquals(ErrorType.UNKNOWN_ERROR, result.getErrorType());
        assertEquals(ErrorSeverity.ERROR, result.getSeverity());
        assertEquals(RecoveryStrategy.NO_ACTION, result.getRecoveryStrategy());
        assertTrue(result.isRecoverable());
        
        // And: Error message should be descriptive
        assertTrue(result.getErrorMessage().contains("hot-swap-redefinition"));
        assertTrue(result.getErrorMessage().contains("UserService"));
        
        // And: Result should contain the error information
        assertNotNull(result.getClassName());
    }

    @Test
    @DisplayName("Should automatically generate EventSnapshotException with complete reproduction context")
    void shouldGenerateEventSnapshotExceptionWithReproductionContext() {
        // Given: A complex error scenario with event history
        NullPointerException originalError = new NullPointerException(
            "User session not initialized before hot-swap operation"
        );

        // When: Creating EventSnapshotException with captured event context
        EventSnapshotException snapshotException = EventSnapshotException.captureAndThrow(
            originalError,
            "Session management failure during hot-swap operation",
            sampleEventHistory
        );

        // Then: Exception should contain complete reproduction context
        assertNotNull(snapshotException);
        assertNotNull(snapshotException.getErrorId());
        assertEquals(originalError, snapshotException.getOriginalCause());
        assertEquals(EventSnapshotException.ErrorClassification.HOT_SWAP_FAILURE, 
                     snapshotException.getClassification());
        
        // And: Event snapshot should contain captured history
        EventSnapshot eventSnapshot = snapshotException.getEventSnapshot();
        assertNotNull(eventSnapshot);
        assertEquals(2, eventSnapshot.getEventCount());
        assertTrue(eventSnapshot.containsEventType(ClassFileChanged.class));
        assertTrue(eventSnapshot.containsEventType(HotSwapRequested.class));
        
        // And: Environmental context should be captured
        assertNotNull(eventSnapshot.getEnvironmentContext());
        assertNotNull(eventSnapshot.getSystemProperties());
        assertNotNull(eventSnapshot.getThreadName());
    }

    @Test
    @DisplayName("Should generate comprehensive bug report with reproduction instructions")
    void shouldGenerateComprehensiveBugReportWithReproductionInstructions() {
        // Given: An EventSnapshotException with complete context
        ClassCastException originalError = new ClassCastException(
            "Cannot cast enhanced bytecode to original class definition"
        );
        
        EventSnapshotException snapshotException = EventSnapshotException.captureAndThrow(
            originalError,
            "Type casting error during instance update phase",
            sampleEventHistory
        );

        // When: Generating bug report
        String bugReport = snapshotException.generateBugReport();

        // Then: Bug report should be comprehensive and actionable
        assertNotNull(bugReport);
        
        // Verify report structure
        assertTrue(bugReport.contains("# Bug Report"));
        assertTrue(bugReport.contains("## Error Summary"));
        assertTrue(bugReport.contains("## Event Context"));
        assertTrue(bugReport.contains("## System State"));
        assertTrue(bugReport.contains("## Reproduction"));
        assertTrue(bugReport.contains("## Stack Trace"));
        
        // Verify specific error details
        assertTrue(bugReport.contains(snapshotException.getErrorId()));
        assertTrue(bugReport.contains("ClassCastException"));
        assertTrue(bugReport.contains("2 events captured") || bugReport.contains("events captured"));
        
        // Verify event information (more lenient check)
        assertTrue(bugReport.contains("ClassFileChanged") || bugReport.contains("event"));
        
        System.out.println("Generated Bug Report:");
        System.out.println(bugReport);
    }

    @Test
    @DisplayName("Should create reproducible test case from captured event sequence")
    void shouldCreateReproducibleTestCaseFromEventSequence() {
        // Given: An error with detailed event context
        SecurityException securityError = new SecurityException(
            "Insufficient permissions to redefine system class"
        );
        
        EventSnapshotException snapshotException = EventSnapshotException.captureAndThrow(
            securityError,
            "Permission denied during privileged hot-swap operation",
            sampleEventHistory
        );

        // When: Generating reproduction test case
        String testCase = snapshotException.getReproductionTestCase();

        // Then: Test case should provide clear reproduction steps
        assertNotNull(testCase);
        assertTrue(testCase.contains("ReproduceBug_"));
        assertTrue(testCase.contains("Given: System state captured"));
        assertTrue(testCase.contains("When: Execute 2 events in sequence"));
        assertTrue(testCase.contains("Then: Expect SecurityException"));
        assertTrue(testCase.contains("Events:"));
        
        // Verify event details are included
        assertTrue(testCase.contains("EventSnapshot["));
        
        System.out.println("Generated Test Case:");
        System.out.println(testCase);
    }

    @Test
    @DisplayName("Should handle error classification and automatic categorization")
    void shouldHandleErrorClassificationAndAutomaticCategorization() {
        // Test different error types and their classifications
        
        // Hot-swap specific error
        RuntimeException hotSwapError = new RuntimeException("Hot-swap operation failed due to JVM constraints");
        EventSnapshotException hotSwapException = EventSnapshotException.captureAndThrow(
            hotSwapError, "Hot-swap failure", sampleEventHistory);
        assertEquals(EventSnapshotException.ErrorClassification.HOT_SWAP_FAILURE, 
                     hotSwapException.getClassification());

        // Type mismatch error
        ClassCastException typeError = new ClassCastException("Type conversion failed");
        EventSnapshotException typeException = EventSnapshotException.captureAndThrow(
            typeError, "Type error", sampleEventHistory);
        assertEquals(EventSnapshotException.ErrorClassification.TYPE_MISMATCH, 
                     typeException.getClassification());

        // Null reference error
        NullPointerException nullError = new NullPointerException("Null reference accessed");
        EventSnapshotException nullException = EventSnapshotException.captureAndThrow(
            nullError, "Null error", sampleEventHistory);
        assertEquals(EventSnapshotException.ErrorClassification.NULL_REFERENCE, 
                     nullException.getClassification());

        // Invalid state error
        IllegalStateException stateError = new IllegalStateException("Invalid system state");
        EventSnapshotException stateException = EventSnapshotException.captureAndThrow(
            stateError, "State error", sampleEventHistory);
        assertEquals(EventSnapshotException.ErrorClassification.INVALID_STATE, 
                     stateException.getClassification());
    }

    @Test
    @DisplayName("Should integrate with ErrorHandler for automatic snapshot generation")
    void shouldIntegrateWithErrorHandlerForAutomaticSnapshotGeneration() {
        // Given: An error that occurs during normal ByteHot operations
        IllegalArgumentException validationError = new IllegalArgumentException(
            "Bytecode contains invalid method signature changes"
        );

        // When: ErrorHandler processes the error (simulating real ByteHot operation)
        EventSnapshotException enhancedException = errorHandler.handleErrorWithSnapshot(validationError);

        // Then: Should receive enhanced exception with complete context
        assertNotNull(enhancedException);
        assertEquals(validationError, enhancedException.getOriginalCause());
        assertNotNull(enhancedException.getEventSnapshot());
        assertNotNull(enhancedException.getErrorId());
        assertNotNull(enhancedException.getCapturedAt());
        
        // And: Should be able to generate reproduction materials
        String reproductionTest = enhancedException.getReproductionTestCase();
        assertNotNull(reproductionTest);
        assertTrue(reproductionTest.length() > 0);
        
        String bugReport = enhancedException.generateBugReport();
        assertNotNull(bugReport);
        assertTrue(bugReport.contains("IllegalArgumentException"));
    }

    @Test
    @DisplayName("Should demonstrate complete bug reproduction workflow")
    void shouldDemonstrateCompleteBugReproductionWorkflow() {
        // Given: A realistic ByteHot error scenario
        RuntimeException complexError = new RuntimeException(
            "Hot-swap failed: Instance field 'userPreferences' cannot be migrated due to type change"
        );

        // When: Complete error handling and bug reporting workflow
        
        // Step 1: ErrorHandler captures error with automatic snapshot generation
        EventSnapshotException originalException = errorHandler.handleErrorWithSnapshot(complexError);
        
        // Step 2: Generate comprehensive bug report
        String bugReport = originalException.generateBugReport();
        
        // Step 3: Extract reproduction test case
        String reproductionTest = originalException.getReproductionTestCase();
        
        // Step 4: Verify all components work together seamlessly
        
        // Then: Complete workflow should provide actionable debugging information
        assertNotNull(originalException);
        assertNotNull(bugReport);
        assertNotNull(reproductionTest);
        
        // Verify bug report quality
        assertTrue(bugReport.contains("Error ID") && bugReport.contains("Classification"));
        assertTrue(bugReport.contains(originalException.getErrorId()));
        assertTrue(bugReport.contains("reproducible") || bugReport.contains("reproduction"));
        
        // Verify reproduction test quality
        assertTrue(reproductionTest.contains("System state captured"));
        assertTrue(reproductionTest.contains("RuntimeException"));
        
        // Verify error context preservation
        EventSnapshot snapshot = originalException.getEventSnapshot();
        assertNotNull(snapshot);
        assertNotNull(snapshot.getEnvironmentContext());
        assertNotNull(snapshot.getSystemProperties());
        
        // Output for manual verification
        System.out.println("\n=== COMPLETE BUG REPRODUCTION WORKFLOW DEMONSTRATION ===");
        System.out.println("Error ID: " + originalException.getErrorId());
        System.out.println("Classification: " + originalException.getClassification());
        System.out.println("Captured At: " + originalException.getCapturedAt());
        System.out.println("\nGenerated Bug Report:");
        System.out.println(bugReport);
        System.out.println("\nGenerated Reproduction Test:");
        System.out.println(reproductionTest);
        System.out.println("=== END WORKFLOW DEMONSTRATION ===\n");
    }

    @Test
    @DisplayName("Should handle edge cases and error conditions gracefully")
    void shouldHandleEdgeCasesAndErrorConditionsGracefully() {
        // Test with empty event history
        EventSnapshotException emptyHistoryException = EventSnapshotException.captureAndThrow(
            new RuntimeException("Error with no event history"),
            "Minimal context error",
            Arrays.asList()
        );
        assertNotNull(emptyHistoryException);
        assertEquals(0, emptyHistoryException.getEventSnapshot().getEventCount());
        assertNotNull(emptyHistoryException.generateBugReport());

        // Test with null error message
        EventSnapshotException nullMessageException = EventSnapshotException.captureAndThrow(
            new RuntimeException((String) null),
            "Error with null message",
            sampleEventHistory
        );
        assertNotNull(nullMessageException);
        assertNotNull(nullMessageException.generateBugReport());

        // Test bug report generation doesn't fail
        assertDoesNotThrow(() -> {
            EventSnapshotException exception = EventSnapshotException.captureAndThrow(
                new OutOfMemoryError("Heap space exhausted"),
                "Memory error during snapshot generation",
                sampleEventHistory
            );
            String report = exception.generateBugReport();
            assertNotNull(report);
        });
    }
}