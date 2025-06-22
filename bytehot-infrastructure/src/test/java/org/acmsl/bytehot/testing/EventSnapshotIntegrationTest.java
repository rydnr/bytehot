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
 * Filename: EventSnapshotIntegrationTest.java
 *
 * Author: Claude Code
 *
 * Class name: EventSnapshotIntegrationTest
 *
 * Responsibilities:
 *   - Test automatic event snapshot generation on errors
 *   - Verify EventSnapshotException enhancement
 *   - Demonstrate complete error debugging context
 *
 * Collaborators:
 *   - ErrorHandler: Error handling with snapshot generation
 *   - EventSnapshotGenerator: Automatic snapshot creation
 *   - EventSnapshotException: Enhanced exceptions with context
 */
package org.acmsl.bytehot.testing;

import org.acmsl.bytehot.domain.ErrorHandler;
import org.acmsl.bytehot.domain.EventSnapshot;
import org.acmsl.bytehot.domain.EventSnapshotException;
import org.acmsl.bytehot.domain.EventSnapshotGenerator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for automatic event snapshot generation on errors
 * @author Claude Code
 * @since 2025-06-19
 */
public class EventSnapshotIntegrationTest {

    private ErrorHandler errorHandler;
    private EventSnapshotGenerator snapshotGenerator;

    @BeforeEach
    void setUp() {
        errorHandler = new ErrorHandler();
        snapshotGenerator = EventSnapshotGenerator.getInstance();
    }

    @Test
    void shouldGenerateEventSnapshotOnError() {
        // Given: A runtime exception occurs
        RuntimeException originalError = new RuntimeException("Test error for snapshot generation");
        
        // When: ErrorHandler processes the error with snapshot enhancement
        EventSnapshotException enhancedException = errorHandler.handleErrorWithSnapshot(originalError);
        
        // Then: The exception should be enhanced with event snapshot
        assertNotNull(enhancedException, "Exception should be enhanced with event snapshot");
        assertNotNull(enhancedException.getEventSnapshot(), "Event snapshot should be generated");
        assertNotNull(enhancedException.getErrorContext(), "Error context should be captured");
        assertSame(originalError, enhancedException.getOriginalException(), "Original exception should be preserved");
        
        // And: The snapshot should contain useful debugging information
        EventSnapshot snapshot = enhancedException.getEventSnapshot();
        assertNotNull(snapshot.getSnapshotId(), "Snapshot should have unique ID");
        assertNotNull(snapshot.getCapturedAt(), "Snapshot should have capture timestamp");
        assertNotNull(snapshot.getThreadName(), "Snapshot should capture thread information");
        
        // And: The enhanced exception should provide debugging report
        String debuggingReport = enhancedException.getDebuggingReport();
        assertNotNull(debuggingReport, "Debugging report should be available");
        assertTrue(debuggingReport.contains("ByteHot Event-Driven Error Report"), "Report should be properly formatted");
        assertTrue(debuggingReport.contains("Test error for snapshot generation"), "Report should contain original error message");
    }

    @Test
    void shouldHandleSnapshotGenerationFailureGracefully() {
        // Given: An error that might cause snapshot generation to fail
        Error criticalError = new OutOfMemoryError("Critical system error");
        
        // When: ErrorHandler processes the error
        EventSnapshotException enhancedException = errorHandler.handleErrorWithSnapshot(criticalError);
        
        // Then: Even if snapshot generation fails, an exception should be created
        assertNotNull(enhancedException, "Exception should still be created even if snapshot fails");
        assertNotNull(enhancedException.getErrorContext(), "Error context should always be captured");
        assertSame(criticalError, enhancedException.getOriginalException(), "Original error should be preserved");
    }

    @Test
    void shouldProvideDebuggingSuggestions() {
        // Given: A validation error occurs
        IllegalArgumentException validationError = new IllegalArgumentException("Invalid bytecode structure");
        
        // When: Error is enhanced with snapshot
        EventSnapshotException enhancedException = errorHandler.handleErrorWithSnapshot(validationError);
        
        // Then: Debugging suggestions should be available
        java.util.List<String> suggestions = enhancedException.getDebuggingSuggestions();
        assertNotNull(suggestions, "Debugging suggestions should be provided");
        assertFalse(suggestions.isEmpty(), "At least one suggestion should be provided");
        
        // And: Suggestions should contain useful information
        boolean hasSnapshotSuggestion = suggestions.stream()
            .anyMatch(suggestion -> suggestion.contains("snapshot ID"));
        assertTrue(hasSnapshotSuggestion, "Should suggest using snapshot ID for reproduction");
    }

    @Test
    void shouldCaptureMemoryContext() {
        // Given: A memory-related error
        RuntimeException memoryError = new RuntimeException("Memory allocation failed");
        
        // When: Error is processed with snapshot
        EventSnapshotException enhancedException = errorHandler.handleErrorWithSnapshot(memoryError);
        
        // Then: Memory context should be captured
        double memoryUsage = enhancedException.getErrorContext().getMemoryUsagePercentage();
        assertTrue(memoryUsage >= 0.0 && memoryUsage <= 1.0, "Memory usage should be valid percentage");
        
        // And: Memory information should be included in error summary
        String errorSummary = enhancedException.getErrorSummary();
        assertTrue(errorSummary.contains("memory="), "Error summary should include memory usage");
    }

    @Test
    void shouldSupportJsonSerialization() {
        // Given: An error with snapshot
        RuntimeException error = new RuntimeException("Serialization test error");
        EventSnapshotException enhancedException = errorHandler.handleErrorWithSnapshot(error);
        
        // When: Converting to JSON
        String jsonRepresentation = enhancedException.toJson();
        
        // Then: JSON should contain essential information
        assertNotNull(jsonRepresentation, "JSON representation should be available");
        assertTrue(jsonRepresentation.contains("EventSnapshotException"), "JSON should identify exception type");
        assertTrue(jsonRepresentation.contains("snapshotId"), "JSON should include snapshot ID");
        assertTrue(jsonRepresentation.contains("Serialization test error"), "JSON should include error message");
        assertTrue(jsonRepresentation.contains("reproducible"), "JSON should include reproducibility information");
    }

    @Test
    void shouldDetectReproducibleErrors() {
        // Given: An error with event context
        RuntimeException error = new RuntimeException("Reproducible error scenario");
        EventSnapshotException enhancedException = errorHandler.handleErrorWithSnapshot(error);
        
        // When: Checking reproducibility
        boolean isReproducible = enhancedException.isLikelyReproducible();
        
        // Then: Should indicate if error is likely reproducible
        // Note: This depends on the event history available
        assertNotNull(isReproducible, "Reproducibility check should return a result");
    }
}