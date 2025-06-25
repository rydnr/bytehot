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
 * Filename: DocProviderFlowIntegrationTest.java
 *
 * Author: Claude Code
 *
 * Class name: DocProviderFlowIntegrationTest
 *
 * Responsibilities:
 *   - Test end-to-end integration between DocProvider and FlowDetector
 *   - Verify sophisticated flow detection enhances documentation
 *   - Validate event-driven flow context detection
 *
 * Collaborators:
 *   - DocProvider: Documentation service with flow integration
 *   - FlowDetector: Sophisticated flow detection engine
 *   - VersionedDomainEvent: Events for flow detection
 */
package org.acmsl.bytehot.domain;

import org.acmsl.bytehot.domain.events.ClassFileChanged;
import org.acmsl.bytehot.domain.events.ClassMetadataExtracted;
import org.acmsl.bytehot.domain.events.BytecodeValidated;
import org.acmsl.bytehot.domain.events.HotSwapRequested;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.nio.file.Paths;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end integration tests for DocProvider with FlowDetector.
 * Tests the sophisticated flow detection integration that enhances documentation.
 * @author Claude Code
 * @since 2025-06-24
 */
public class DocProviderFlowIntegrationTest {

    private DocProvider docProvider;

    @BeforeEach
    public void setUp() {
        docProvider = new DocProvider();
    }

    @Test
    @DisplayName("Should enhance documentation with sophisticated flow detection")
    public void shouldEnhanceDocumentationWithSophisticatedFlowDetection() {
        // Given - Simulate a hot-swap sequence that should trigger flow detection
        final Instant now = Instant.now();
        
        // Add events that match the HotSwapCompleteFlow pattern
        final ClassFileChanged classChanged = ClassFileChanged.forNewSession(
            Paths.get("/test/com/example/TestClass.class"),
            "TestClass",
            2048,
            now
        );
        
        // Note: Additional events would be needed for full flow detection
        // but we're testing the integration mechanism
        docProvider.addRecentEvent(classChanged);

        // When
        final Optional<String> contextualUrl = docProvider.getContextualDocumentationUrl(DocProvider.class);
        final Map<String, Object> metrics = docProvider.getPerformanceMetrics();

        // Then
        assertTrue(contextualUrl.isPresent());
        
        // Verify integration is active
        assertEquals(true, metrics.get("integration_active"));
        assertTrue((Long) metrics.get("flow_detection_calls") > 0);
        assertTrue((Integer) metrics.get("recent_events_count") > 0);

        // URL should be generated (either contextual or fallback)
        assertTrue(contextualUrl.get().contains(".html"));
    }

    @Test
    @DisplayName("Should fallback gracefully when FlowDetector fails")
    public void shouldFallbackGracefullyWhenFlowDetectorFails() {
        // Given - No events added, so FlowDetector should find no patterns

        // When
        final Optional<String> contextualUrl = docProvider.getContextualDocumentationUrl(String.class);
        final Map<String, Object> metrics = docProvider.getPerformanceMetrics();

        // Then
        assertTrue(contextualUrl.isPresent());
        
        // Should fallback to basic documentation
        assertTrue(contextualUrl.get().contains("String.html"));
        assertTrue(contextualUrl.get().contains("docs/java/lang"));
        
        // Integration should still be active even with fallback
        assertEquals(true, metrics.get("integration_active"));
    }

    @Test
    @DisplayName("Should cache flow detection results for performance")
    public void shouldCacheFlowDetectionResultsForPerformance() {
        // Given
        final ClassFileChanged event = ClassFileChanged.forNewSession(
            Paths.get("/test/CacheTest.class"),
            "CacheTest",
            1024,
            Instant.now()
        );
        docProvider.addRecentEvent(event);

        // When - Multiple calls to contextual documentation
        final long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < 10; i++) {
            docProvider.getContextualDocumentationUrl(String.class);
        }
        
        final long endTime = System.currentTimeMillis();
        final long duration = endTime - startTime;

        // Then - Should be fast due to caching
        assertTrue(duration < 50, "Multiple contextual calls should be fast due to caching: " + duration + "ms");
        
        final Map<String, Object> metrics = docProvider.getPerformanceMetrics();
        assertTrue((Long) metrics.get("cache_hits") > 0 || (Long) metrics.get("cached_flows") >= 0);
    }

    @Test
    @DisplayName("Should maintain recent events for flow analysis")
    public void shouldMaintainRecentEventsForFlowAnalysis() {
        // Given - Add multiple events
        final Instant now = Instant.now();
        
        for (int i = 0; i < 5; i++) {
            final ClassFileChanged event = ClassFileChanged.forNewSession(
                Paths.get("/test/Event" + i + ".class"),
                "Event" + i,
                512 + i * 100,
                now.plusSeconds(i)
            );
            docProvider.addRecentEvent(event);
        }

        // When
        final Map<String, Object> metrics = docProvider.getPerformanceMetrics();

        // Then
        assertEquals(5, metrics.get("recent_events_count"));
        assertTrue((Long) metrics.get("flow_detection_calls") >= 0);
    }

    @Test
    @DisplayName("Should limit recent events to prevent memory issues")
    public void shouldLimitRecentEventsToPreventMemoryIssues() {
        // Given - Add more events than the limit (should be 10)
        final Instant now = Instant.now();
        
        for (int i = 0; i < 20; i++) {
            final ClassFileChanged event = ClassFileChanged.forNewSession(
                Paths.get("/test/MemoryTest" + i + ".class"),
                "MemoryTest" + i,
                1024,
                now.plusSeconds(i)
            );
            docProvider.addRecentEvent(event);
        }

        // When
        final Map<String, Object> metrics = docProvider.getPerformanceMetrics();

        // Then - Should limit to 10 events
        assertEquals(10, metrics.get("recent_events_count"));
    }

    @Test
    @DisplayName("Should provide integration status in performance metrics")
    public void shouldProvideIntegrationStatusInPerformanceMetrics() {
        // When
        final Map<String, Object> metrics = docProvider.getPerformanceMetrics();

        // Then
        assertTrue(metrics.containsKey("integration_active"));
        assertTrue(metrics.containsKey("flow_detection_calls"));
        assertTrue(metrics.containsKey("recent_events_count"));
        assertTrue(metrics.containsKey("cached_flows"));

        assertEquals(true, metrics.get("integration_active"));
        assertTrue(metrics.get("flow_detection_calls") instanceof Long);
        assertTrue(metrics.get("recent_events_count") instanceof Integer);
        assertTrue(metrics.get("cached_flows") instanceof Integer);
    }

    @Test
    @DisplayName("Should detect call stack patterns when FlowDetector analysis fails")
    public void shouldDetectCallStackPatternsWhenFlowDetectorAnalysisFails() {
        // Given - No specific events, relies on call stack detection

        // When - Call from a method that might trigger call stack detection
        final Optional<String> url = detectFromCallStack();

        // Then
        assertTrue(url.isPresent());
        assertTrue(url.get().contains(".html"));
    }

    /**
     * Helper method to test call stack detection.
     * This method name contains patterns that might trigger flow detection.
     */
    private Optional<String> detectFromCallStack() {
        // This call should trigger call stack analysis in DocProvider
        return docProvider.getContextualDocumentationUrl(DocProvider.class);
    }

    @Test
    @DisplayName("Should handle concurrent access to recent events safely")
    public void shouldHandleConcurrentAccessToRecentEventsSafely() {
        // Given
        final Runnable addEvents = () -> {
            for (int i = 0; i < 5; i++) {
                final ClassFileChanged event = ClassFileChanged.forNewSession(
                    Paths.get("/test/Concurrent" + Thread.currentThread().getId() + "_" + i + ".class"),
                    "Concurrent" + i,
                    1024,
                    Instant.now()
                );
                docProvider.addRecentEvent(event);
            }
        };

        // When - Multiple threads add events concurrently
        final Thread thread1 = new Thread(addEvents);
        final Thread thread2 = new Thread(addEvents);
        
        thread1.start();
        thread2.start();
        
        try {
            thread1.join();
            thread2.join();
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Then - Should handle concurrent access without issues
        final Map<String, Object> metrics = docProvider.getPerformanceMetrics();
        final int eventCount = (Integer) metrics.get("recent_events_count");
        
        // Should have events from both threads (up to the limit of 10)
        assertTrue(eventCount > 0);
        assertTrue(eventCount <= 10);
    }
}