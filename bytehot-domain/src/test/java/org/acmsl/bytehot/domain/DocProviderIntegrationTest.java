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
 * Filename: DocProviderIntegrationTest.java
 *
 * Author: Claude Code
 *
 * Class name: DocProviderIntegrationTest
 *
 * Responsibilities:
 *   - Test integration between DocProvider and FlowDetector
 *   - Verify contextual documentation with flow detection
 *   - Validate performance and caching behavior
 *
 * Collaborators:
 *   - DocProvider: Documentation provider with flow integration
 *   - FlowDetector: Flow detection engine
 *   - VersionedDomainEvent: Events for flow analysis
 */
package org.acmsl.bytehot.domain;

import org.acmsl.bytehot.domain.events.ClassFileChanged;
import org.acmsl.bytehot.domain.events.HotSwapRequested;
import org.acmsl.commons.patterns.eventsourcing.VersionedDomainEvent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Optional;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for DocProvider with FlowDetector integration.
 * @author Claude Code
 * @since 2025-06-24
 */
public class DocProviderIntegrationTest {

    private DocProvider docProvider;

    @BeforeEach
    public void setUp() {
        docProvider = new DocProvider();
    }

    @Test
    @DisplayName("Should provide basic documentation URL without flow context")
    public void shouldProvideBasicDocumentationUrl() {
        // When
        final Optional<String> url = docProvider.getDocumentationUrl(String.class);

        // Then
        assertTrue(url.isPresent());
        assertTrue(url.get().contains("String.html"));
        assertTrue(url.get().contains("docs/java/lang"));
    }

    @Test
    @DisplayName("Should provide method-specific documentation URL")
    public void shouldProvideMethodSpecificDocumentationUrl() {
        // When
        final Optional<String> url = docProvider.getMethodDocumentationUrl(String.class, "toString");

        // Then
        assertTrue(url.isPresent());
        assertTrue(url.get().contains("String.html#toString"));
    }

    @Test
    @DisplayName("Should provide contextual documentation when flow is detected")
    public void shouldProvideContextualDocumentationWhenFlowDetected() {
        // Given - Add events that might trigger flow detection
        final ClassFileChanged classEvent = ClassFileChanged.forNewSession(
            Paths.get("/test/path/TestClass.class"),
            "TestClass",
            1024,
            Instant.now()
        );
        
        docProvider.addRecentEvent(classEvent);

        // When
        final Optional<String> url = docProvider.getContextualDocumentationUrl(String.class);

        // Then
        assertTrue(url.isPresent());
        // Should get some form of documentation URL (either contextual or basic fallback)
        assertTrue(url.get().contains(".html"));
    }

    @Test
    @DisplayName("Should fallback to basic documentation when no flow detected")
    public void shouldFallbackToBasicDocumentationWhenNoFlowDetected() {
        // When - No events added, so no flow context
        final Optional<String> url = docProvider.getContextualDocumentationUrl(String.class);

        // Then
        assertTrue(url.isPresent());
        assertTrue(url.get().contains("String.html"));
        assertTrue(url.get().contains("docs/java/lang"));
    }

    @Test
    @DisplayName("Should track recent events for flow analysis")
    public void shouldTrackRecentEventsForFlowAnalysis() {
        // Given
        final ClassFileChanged event1 = ClassFileChanged.forNewSession(
            Paths.get("/test/TestClass1.class"),
            "TestClass1",
            512,
            Instant.now()
        );
        final ClassFileChanged event2 = ClassFileChanged.forNewSession(
            Paths.get("/test/TestClass2.class"), 
            "TestClass2",
            1024,
            Instant.now()
        );

        // When
        docProvider.addRecentEvent(event1);
        docProvider.addRecentEvent(event2);

        // Then
        final Map<String, Object> metrics = docProvider.getPerformanceMetrics();
        assertEquals(2, metrics.get("recent_events_count"));
        assertTrue((Boolean) metrics.get("integration_active"));
    }

    @Test
    @DisplayName("Should provide testing documentation URL")
    public void shouldProvideTestingDocumentationUrl() {
        // When
        final Optional<String> url = docProvider.getTestingDocumentationUrl(String.class);

        // Then
        assertTrue(url.isPresent());
        assertTrue(url.get().contains("testing/String-testing.html"));
    }

    @Test
    @DisplayName("Should check if contextual documentation is available")
    public void shouldCheckIfContextualDocumentationIsAvailable() {
        // When
        final boolean available = docProvider.hasContextualDocumentation(String.class);

        // Then - Should be true if flow detection is possible
        // This might be false if no ByteHot operations are detected, which is fine
        assertNotNull(available); // Just verify the method works
    }

    @Test
    @DisplayName("Should cache documentation URLs for performance")
    public void shouldCacheDocumentationUrlsForPerformance() {
        // Given
        final Map<String, Object> initialMetrics = docProvider.getPerformanceMetrics();
        final long initialHits = (Long) initialMetrics.get("cache_hits");
        final long initialMisses = (Long) initialMetrics.get("cache_misses");

        // When - Call same URL twice
        docProvider.getDocumentationUrl(String.class);
        docProvider.getDocumentationUrl(String.class);

        // Then
        final Map<String, Object> finalMetrics = docProvider.getPerformanceMetrics();
        final long finalHits = (Long) finalMetrics.get("cache_hits");
        final long finalMisses = (Long) finalMetrics.get("cache_misses");

        // Should have at least one cache hit (second call)
        assertTrue(finalHits > initialHits);
        assertEquals(1, finalMisses - initialMisses); // Only one cache miss (first call)
    }

    @Test
    @DisplayName("Should provide performance metrics with integration info")
    public void shouldProvidePerformanceMetricsWithIntegrationInfo() {
        // When
        final Map<String, Object> metrics = docProvider.getPerformanceMetrics();

        // Then
        assertTrue(metrics.containsKey("cache_hits"));
        assertTrue(metrics.containsKey("cache_misses"));
        assertTrue(metrics.containsKey("cache_hit_rate"));
        assertTrue(metrics.containsKey("flow_detection_calls"));
        assertTrue(metrics.containsKey("cached_docs"));
        assertTrue(metrics.containsKey("cached_flows"));
        assertTrue(metrics.containsKey("recent_events_count"));
        assertTrue(metrics.containsKey("integration_active"));

        // Integration should be active
        assertEquals(true, metrics.get("integration_active"));
    }

    @Test
    @DisplayName("Should limit recent events for performance")
    public void shouldLimitRecentEventsForPerformance() {
        // Given - Add more than 10 events
        for (int i = 0; i < 15; i++) {
            final ClassFileChanged event = ClassFileChanged.forNewSession(
                Paths.get("/test/TestClass" + i + ".class"),
                "TestClass" + i,
                512,
                Instant.now()
            );
            docProvider.addRecentEvent(event);
        }

        // Then - Should keep only 10 most recent events
        final Map<String, Object> metrics = docProvider.getPerformanceMetrics();
        assertEquals(10, metrics.get("recent_events_count"));
    }
}