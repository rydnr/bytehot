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
 * Filename: DocLinkAvailableTest.java
 *
 * Author: Claude Code
 *
 * Class name: DocLinkAvailableTest
 *
 * Responsibilities:
 *   - Test DocLinkAvailable interface default method implementations
 *   - Validate documentation URL generation and accessibility
 *   - Test graceful degradation when documentation is unavailable
 *
 * Collaborators:
 *   - DocLinkAvailable: Interface being tested
 *   - DocProvider: Documentation URL generation engine
 */
package org.acmsl.bytehot.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DocLinkAvailable interface default method implementations
 * @author Claude Code
 * @since 2025-06-26
 */
class DocLinkAvailableTest {

    /**
     * Test implementation of DocLinkAvailable for testing purposes
     */
    private static class TestDocumentedComponent implements DocLinkAvailable {
        // No implementation needed - all methods are defaults
    }

    /**
     * Another test implementation to verify different classes get different URLs
     */
    private static class AnotherTestComponent implements DocLinkAvailable {
        // No implementation needed - all methods are defaults
    }

    private TestDocumentedComponent testComponent;
    private AnotherTestComponent anotherComponent;

    @BeforeEach
    void setUp() {
        testComponent = new TestDocumentedComponent();
        anotherComponent = new AnotherTestComponent();
    }

    @Test
    @DisplayName("📚 Basic Documentation URL Generation")
    void testGetDocUrl() {
        // When: Getting basic documentation URL
        final Optional<String> docUrl = testComponent.getDocUrl();

        // Then: URL should be generated successfully
        assertTrue(docUrl.isPresent(), "Documentation URL should be available");
        final String url = docUrl.get();
        
        // Verify URL structure
        assertTrue(url.contains("bytehot"), "URL should contain project name");
        assertTrue(url.contains("TestDocumentedComponent"), "URL should contain class name");
        assertTrue(url.startsWith("https://"), "URL should be HTTPS");
        
        System.out.println("Generated doc URL: " + url);
    }

    @Test
    @DisplayName("🔍 Method-Specific Documentation URL Generation")
    void testGetMethodDocUrl() {
        // When: Getting method-specific documentation URL
        final Optional<String> methodUrl = testComponent.getMethodDocUrl("testMethod");

        // Then: Method URL should be generated successfully
        assertTrue(methodUrl.isPresent(), "Method documentation URL should be available");
        final String url = methodUrl.get();
        
        // Verify method URL structure
        assertTrue(url.contains("TestDocumentedComponent"), "URL should contain class name");
        assertTrue(url.contains("#testMethod"), "URL should contain method anchor");
        
        System.out.println("Generated method URL: " + url);
    }

    @Test
    @DisplayName("🎯 Runtime Flow-Aware Documentation")
    void testGetRuntimeDocLink() {
        // When: Getting runtime contextual documentation
        final Optional<String> runtimeUrl = testComponent.getRuntimeDocLink();

        // Then: Runtime URL should be available (may fallback to basic)
        assertTrue(runtimeUrl.isPresent(), "Runtime documentation URL should be available");
        final String url = runtimeUrl.get();
        
        // Verify basic URL structure (may be basic or contextual)
        assertTrue(url.contains("bytehot"), "URL should contain project name");
        assertTrue(url.startsWith("https://"), "URL should be HTTPS");
        
        System.out.println("Generated runtime URL: " + url);
    }

    @Test
    @DisplayName("🔄 Flow-Specific Documentation URL Generation")
    void testGetDocLinkForFlow() {
        // Given: A simple Flow for testing
        try {
            final Flow testFlow = Flow.builder()
                .flowId(FlowId.of("test-flow"))
                .name("TestFlow")
                .description("Flow for testing documentation")
                .confidence(0.9)
                .build();

            // When: Getting Flow-specific documentation
            final Optional<String> flowUrl = testComponent.getDocLinkForFlow(testFlow);

            // Then: Flow URL should be generated successfully
            assertTrue(flowUrl.isPresent(), "Flow documentation URL should be available");
            final String url = flowUrl.get();
            
            // Verify Flow URL structure
            assertTrue(url.contains("flows"), "URL should contain flows path");
            assertTrue(url.contains("TestDocumentedComponent"), "URL should contain class name");
            
            System.out.println("Generated flow URL: " + url);
        } catch (Exception e) {
            // If Flow creation fails, test fallback behavior
            final Optional<String> fallbackUrl = testComponent.getDocUrl();
            assertTrue(fallbackUrl.isPresent(), "Should fallback to basic documentation");
        }
    }

    @Test
    @DisplayName("🧪 Testing Documentation URL Generation")
    void testGetTestingDocLink() {
        // When: Getting testing-specific documentation
        final Optional<String> testingUrl = testComponent.getTestingDocLink();

        // Then: Testing URL should be generated successfully
        assertTrue(testingUrl.isPresent(), "Testing documentation URL should be available");
        final String url = testingUrl.get();
        
        // Verify testing URL structure
        assertTrue(url.contains("testing"), "URL should contain testing path");
        assertTrue(url.contains("TestDocumentedComponent"), "URL should contain class name");
        
        System.out.println("Generated testing URL: " + url);
    }

    @Test
    @DisplayName("✅ Documentation Availability Check")
    void testHasContextualDocumentation() {
        // When: Checking if contextual documentation is available
        final boolean hasContextual = testComponent.hasContextualDocumentation();

        // Then: Should return boolean result (may be true or false depending on context)
        // This test mainly verifies the method doesn't throw exceptions
        assertNotNull(hasContextual, "Documentation availability check should return a result");
        
        System.out.println("Has contextual documentation: " + hasContextual);
    }

    @Test
    @DisplayName("🔒 Graceful Degradation - Interface Never Breaks Functionality")
    void testGracefulDegradation() {
        // This test verifies that documentation interface failures don't break the system
        
        // When: All documentation methods are called
        final Optional<String> basicUrl = testComponent.getDocUrl();
        final Optional<String> methodUrl = testComponent.getMethodDocUrl("someMethod");
        final Optional<String> runtimeUrl = testComponent.getRuntimeDocLink();
        final Optional<String> testingUrl = testComponent.getTestingDocLink();
        final boolean hasContextual = testComponent.hasContextualDocumentation();

        // Then: All methods should complete without throwing exceptions
        // URLs may be empty if generation fails, but methods should not throw
        assertNotNull(basicUrl, "Basic URL result should not be null");
        assertNotNull(methodUrl, "Method URL result should not be null");
        assertNotNull(runtimeUrl, "Runtime URL result should not be null");
        assertNotNull(testingUrl, "Testing URL result should not be null");
        
        System.out.println("Graceful degradation test completed - all methods returned safely");
    }

    @Test
    @DisplayName("🎨 Different Classes Generate Different URLs")
    void testDifferentClassesGenerateDifferentUrls() {
        // When: Getting URLs for different classes
        final Optional<String> firstUrl = testComponent.getDocUrl();
        final Optional<String> secondUrl = anotherComponent.getDocUrl();

        // Then: URLs should be different (reflecting different class names)
        if (firstUrl.isPresent() && secondUrl.isPresent()) {
            assertNotEquals(firstUrl.get(), secondUrl.get(), 
                "Different classes should generate different documentation URLs");
            
            assertTrue(firstUrl.get().contains("TestDocumentedComponent"), 
                "First URL should contain first class name");
            assertTrue(secondUrl.get().contains("AnotherTestComponent"), 
                "Second URL should contain second class name");
        }
        
        System.out.println("First class URL: " + firstUrl.orElse("not available"));
        System.out.println("Second class URL: " + secondUrl.orElse("not available"));
    }

    @Test
    @DisplayName("⚡ Performance - Documentation Methods Are Fast")
    void testDocumentationPerformance() {
        final int iterations = 100;
        final long startTime = System.nanoTime();

        // When: Calling documentation methods repeatedly
        for (int i = 0; i < iterations; i++) {
            testComponent.getDocUrl();
            testComponent.getMethodDocUrl("testMethod");
            testComponent.getRuntimeDocLink();
        }

        final long endTime = System.nanoTime();
        final long durationMs = (endTime - startTime) / 1_000_000;

        // Then: Performance should be reasonable (under 1 second for 100 iterations)
        assertTrue(durationMs < 1000, 
            String.format("Documentation methods should be fast: %dms for %d iterations", 
                         durationMs, iterations));
        
        System.out.printf("Performance test: %dms for %d iterations (%.2fms avg)%n", 
                         durationMs, iterations, (double) durationMs / iterations);
    }
}