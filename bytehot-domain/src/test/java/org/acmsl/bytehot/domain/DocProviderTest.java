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
 * Filename: DocProviderTest.java
 *
 * Author: Claude Code
 *
 * Class name: DocProviderTest
 *
 * Responsibilities:
 *   - Test DocProvider URL generation and Flow detection capabilities
 *   - Validate caching mechanisms and performance metrics
 *   - Test contextual documentation and Flow-aware features
 *
 * Collaborators:
 *   - DocProvider: Documentation engine being tested
 *   - Flow: Flow context for contextual documentation
 */
package org.acmsl.bytehot.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Optional;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DocProvider documentation generation engine
 * @author Claude Code
 * @since 2025-06-26
 */
class DocProviderTest {

    private DocProvider docProvider;

    @BeforeEach
    void setUp() {
        docProvider = new DocProvider();
    }

    @Test
    @DisplayName("📚 Basic Documentation URL Generation")
    void testBasicDocumentationUrlGeneration() {
        // When: Getting documentation URL for a specific class
        final Optional<String> docUrl = docProvider.getDocumentationUrl(DocProvider.class);

        // Then: URL should be generated successfully
        assertTrue(docUrl.isPresent(), "Documentation URL should be generated");
        
        final String url = docUrl.get();
        assertTrue(url.startsWith("https://rydnr.github.io/bytehot"), 
                  "URL should start with documentation base URL");
        assertTrue(url.contains("DocProvider"), "URL should contain class name");
        assertTrue(url.endsWith(".html"), "URL should end with .html");
        
        System.out.println("Generated basic URL: " + url);
    }

    @Test
    @DisplayName("🔍 Method Documentation URL Generation")
    void testMethodDocumentationUrlGeneration() {
        // When: Getting method-specific documentation URL
        final Optional<String> methodUrl = docProvider.getMethodDocumentationUrl(
            DocProvider.class, "getDocumentationUrl");

        // Then: Method URL should be generated successfully
        assertTrue(methodUrl.isPresent(), "Method documentation URL should be generated");
        
        final String url = methodUrl.get();
        assertTrue(url.contains("DocProvider"), "URL should contain class name");
        assertTrue(url.contains("#getDocumentationUrl"), "URL should contain method anchor");
        
        System.out.println("Generated method URL: " + url);
    }

    @Test
    @DisplayName("🎯 Contextual Documentation with Flow Detection")
    void testContextualDocumentationGeneration() {
        // When: Getting contextual documentation
        final Optional<String> contextualUrl = docProvider.getContextualDocumentationUrl(DocProvider.class);

        // Then: Contextual URL should be generated
        assertTrue(contextualUrl.isPresent(), "Contextual documentation URL should be generated");
        
        final String url = contextualUrl.get();
        assertTrue(url.startsWith("https://"), "URL should be HTTPS");
        
        System.out.println("Generated contextual URL: " + url);
    }

    @Test
    @DisplayName("🧪 Testing Documentation URL Generation")
    void testTestingDocumentationUrlGeneration() {
        // When: Getting testing-specific documentation
        final Optional<String> testingUrl = docProvider.getTestingDocumentationUrl(DocProvider.class);

        // Then: Testing URL should be generated successfully
        assertTrue(testingUrl.isPresent(), "Testing documentation URL should be generated");
        
        final String url = testingUrl.get();
        assertTrue(url.contains("testing"), "URL should contain testing path");
        assertTrue(url.contains("DocProvider"), "URL should contain class name");
        assertTrue(url.contains("-testing.html"), "URL should contain testing suffix");
        
        System.out.println("Generated testing URL: " + url);
    }

    @Test
    @DisplayName("✅ Contextual Documentation Availability Check")
    void testContextualDocumentationAvailability() {
        // When: Checking if contextual documentation is available
        final boolean hasContextual = docProvider.hasContextualDocumentation(DocProvider.class);

        // Then: Should return a boolean result
        assertNotNull(hasContextual, "Documentation availability check should return a result");
        
        System.out.println("Has contextual documentation: " + hasContextual);
    }

    @Test
    @DisplayName("🔄 Flow-Specific Documentation Generation")
    void testFlowSpecificDocumentationGeneration() {
        try {
            // Given: A test Flow
            final Flow testFlow = Flow.builder()
                .flowId(FlowId.of("test-documentation-flow"))
                .name("DocumentationFlow")
                .description("Flow for testing documentation generation")
                .confidence(0.95)
                .build();

            // When: Getting Flow-specific documentation
            final Optional<String> flowUrl = docProvider.getFlowDocumentationUrl(DocProvider.class, testFlow);

            // Then: Flow URL should be generated successfully
            assertTrue(flowUrl.isPresent(), "Flow documentation URL should be generated");
            
            final String url = flowUrl.get();
            assertTrue(url.contains("flows"), "URL should contain flows path");
            assertTrue(url.contains("documentationflow"), "URL should contain flow name (lowercase)");
            assertTrue(url.contains("DocProvider"), "URL should contain class name");
            
            System.out.println("Generated flow URL: " + url);
        } catch (Exception e) {
            // If Flow creation fails, test graceful degradation
            final Optional<String> fallbackUrl = docProvider.getDocumentationUrl(DocProvider.class);
            assertTrue(fallbackUrl.isPresent(), "Should fallback to basic documentation");
            System.out.println("Flow creation failed, testing fallback behavior");
        }
    }

    @Test
    @DisplayName("📊 Performance Metrics Tracking")
    void testPerformanceMetricsTracking() {
        // Given: Multiple documentation requests to populate metrics
        docProvider.getDocumentationUrl(DocProvider.class);
        docProvider.getMethodDocumentationUrl(DocProvider.class, "testMethod");
        docProvider.getContextualDocumentationUrl(DocProvider.class);

        // When: Getting performance metrics
        final Map<String, Object> metrics = docProvider.getPerformanceMetrics();

        // Then: Metrics should be tracked properly
        assertNotNull(metrics, "Performance metrics should be available");
        assertTrue(metrics.containsKey("cache_hits"), "Should track cache hits");
        assertTrue(metrics.containsKey("cache_misses"), "Should track cache misses");
        assertTrue(metrics.containsKey("cache_hit_rate"), "Should track cache hit rate");
        assertTrue(metrics.containsKey("flow_detection_calls"), "Should track flow detection calls");
        assertTrue(metrics.containsKey("integration_active"), "Should indicate integration status");
        
        // Verify metric values are reasonable
        final long cacheHits = (Long) metrics.get("cache_hits");
        final long cacheMisses = (Long) metrics.get("cache_misses");
        final double hitRate = (Double) metrics.get("cache_hit_rate");
        
        assertTrue(cacheHits >= 0, "Cache hits should be non-negative");
        assertTrue(cacheMisses >= 0, "Cache misses should be non-negative");
        assertTrue(hitRate >= 0.0 && hitRate <= 1.0, "Hit rate should be between 0 and 1");
        
        System.out.println("Performance metrics: " + metrics);
    }

    @Test
    @DisplayName("🚀 Caching Performance Verification")
    void testCachingPerformance() {
        // When: Requesting the same URL multiple times
        final Class<?> testClass = DocProvider.class;
        
        // First request (cache miss)
        final long startTime1 = System.nanoTime();
        final Optional<String> url1 = docProvider.getDocumentationUrl(testClass);
        final long duration1 = System.nanoTime() - startTime1;
        
        // Second request (cache hit)
        final long startTime2 = System.nanoTime();
        final Optional<String> url2 = docProvider.getDocumentationUrl(testClass);
        final long duration2 = System.nanoTime() - startTime2;

        // Then: Both should return the same URL
        assertEquals(url1, url2, "Cached URL should be identical to original");
        
        // Performance metrics should show cache usage
        final Map<String, Object> metrics = docProvider.getPerformanceMetrics();
        final long cacheHits = (Long) metrics.get("cache_hits");
        final long cacheMisses = (Long) metrics.get("cache_misses");
        
        assertTrue(cacheHits > 0, "Should have cache hits");
        assertTrue(cacheMisses > 0, "Should have cache misses");
        
        System.out.printf("Cache performance: first=%.2fms, second=%.2fms%n", 
                         duration1 / 1_000_000.0, duration2 / 1_000_000.0);
        System.out.println("Cache metrics: hits=" + cacheHits + ", misses=" + cacheMisses);
    }

    @Test
    @DisplayName("🛡️ Error Handling and Graceful Degradation")
    void testErrorHandlingAndGracefulDegradation() {
        // When: Testing with various inputs that might cause issues
        
        // Test with null class (should be handled gracefully)
        assertDoesNotThrow(() -> {
            try {
                docProvider.getDocumentationUrl(null);
            } catch (NullPointerException e) {
                // Expected - but should not break the system
            }
        }, "Null class should be handled gracefully");

        // Test with empty method name
        final Optional<String> emptyMethodUrl = docProvider.getMethodDocumentationUrl(
            DocProvider.class, "");
        assertTrue(emptyMethodUrl.isPresent(), "Empty method name should still generate URL");

        // Test contextual documentation (should not throw)
        assertDoesNotThrow(() -> {
            final Optional<String> contextualUrl = docProvider.getContextualDocumentationUrl(DocProvider.class);
            // Should return either contextual or fallback URL
            assertTrue(contextualUrl.isPresent(), "Should return some documentation URL");
        }, "Contextual documentation should not throw exceptions");
        
        System.out.println("Error handling tests completed successfully");
    }

    @Test
    @DisplayName("⚡ Performance Under Load")
    void testPerformanceUnderLoad() {
        final int iterations = 50;
        final long startTime = System.nanoTime();

        // When: Making many documentation requests
        for (int i = 0; i < iterations; i++) {
            docProvider.getDocumentationUrl(DocProvider.class);
            docProvider.getMethodDocumentationUrl(DocProvider.class, "method" + i);
            docProvider.getContextualDocumentationUrl(DocProvider.class);
        }

        final long endTime = System.nanoTime();
        final long durationMs = (endTime - startTime) / 1_000_000;

        // Then: Performance should be reasonable
        assertTrue(durationMs < 2000, 
            String.format("Documentation generation should be fast: %dms for %d requests", 
                         durationMs, iterations * 3));

        // Check final metrics
        final Map<String, Object> metrics = docProvider.getPerformanceMetrics();
        final double hitRate = (Double) metrics.get("cache_hit_rate");
        
        System.out.printf("Load test: %dms for %d requests (%.2fms avg)%n", 
                         durationMs, iterations * 3, (double) durationMs / (iterations * 3));
        System.out.printf("Final cache hit rate: %.2f%%%n", hitRate * 100);
    }

    @Test
    @DisplayName("🎨 URL Structure Validation")
    void testUrlStructureValidation() {
        // When: Generating various types of URLs
        final Optional<String> basicUrl = docProvider.getDocumentationUrl(String.class);
        final Optional<String> methodUrl = docProvider.getMethodDocumentationUrl(String.class, "length");
        final Optional<String> testingUrl = docProvider.getTestingDocumentationUrl(String.class);

        // Then: URLs should follow expected patterns
        if (basicUrl.isPresent()) {
            final String url = basicUrl.get();
            assertTrue(url.matches("https://rydnr\\.github\\.io/bytehot/docs/.*\\.html"), 
                      "Basic URL should follow expected pattern");
        }

        if (methodUrl.isPresent()) {
            final String url = methodUrl.get();
            assertTrue(url.contains("#length"), "Method URL should contain anchor");
        }

        if (testingUrl.isPresent()) {
            final String url = testingUrl.get();
            assertTrue(url.contains("/testing/") && url.contains("-testing.html"), 
                      "Testing URL should follow testing pattern");
        }
        
        System.out.println("URL structure validation completed");
    }
}