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
 * Filename: DocProviderUrlGenerationTest.java
 *
 * Author: Claude Code
 *
 * Class name: DocProviderUrlGenerationTest
 *
 * Responsibilities:
 *   - Test URL generation functionality in DocProvider
 *   - Verify different URL generation strategies
 *   - Validate URL format and content correctness
 *
 * Collaborators:
 *   - DocProvider: Documentation URL generation service
 *   - Flow: Flow context for contextual URLs
 *   - Defaults: Documentation system configuration
 */
package org.acmsl.bytehot.domain;

import org.acmsl.bytehot.domain.events.ClassFileChanged;
import org.acmsl.bytehot.domain.events.HotSwapRequested;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for documentation URL generation functionality.
 * @author Claude Code
 * @since 2025-06-24
 */
public class DocProviderUrlGenerationTest {

    private DocProvider docProvider;

    @BeforeEach
    public void setUp() {
        docProvider = new DocProvider();
    }

    @Test
    @DisplayName("Should generate basic class documentation URLs with correct format")
    public void shouldGenerateBasicClassDocumentationUrls() {
        // When
        final Optional<String> stringUrl = docProvider.getDocumentationUrl(String.class);
        final Optional<String> listUrl = docProvider.getDocumentationUrl(List.class);
        final Optional<String> docProviderUrl = docProvider.getDocumentationUrl(DocProvider.class);

        // Then
        assertTrue(stringUrl.isPresent());
        assertTrue(listUrl.isPresent());
        assertTrue(docProviderUrl.isPresent());

        // Verify URL format includes class name and package structure
        assertTrue(stringUrl.get().contains("String.html"));
        assertTrue(stringUrl.get().contains("java/lang"));
        
        assertTrue(listUrl.get().contains("List.html"));
        assertTrue(listUrl.get().contains("java/util"));

        assertTrue(docProviderUrl.get().contains("DocProvider.html"));
        assertTrue(docProviderUrl.get().contains("org/acmsl/bytehot/domain"));
    }

    @Test
    @DisplayName("Should generate method-specific documentation URLs")
    public void shouldGenerateMethodSpecificDocumentationUrls() {
        // When
        final Optional<String> toStringUrl = docProvider.getMethodDocumentationUrl(String.class, "toString");
        final Optional<String> lengthUrl = docProvider.getMethodDocumentationUrl(String.class, "length");
        final Optional<String> addUrl = docProvider.getMethodDocumentationUrl(List.class, "add");

        // Then
        assertTrue(toStringUrl.isPresent());
        assertTrue(lengthUrl.isPresent());
        assertTrue(addUrl.isPresent());

        // Verify method URLs include fragment identifiers
        assertTrue(toStringUrl.get().contains("String.html#toString"));
        assertTrue(lengthUrl.get().contains("String.html#length"));
        assertTrue(addUrl.get().contains("List.html#add"));
    }

    @Test
    @DisplayName("Should generate contextual documentation URLs based on Flow context")
    public void shouldGenerateContextualDocumentationUrls() {
        // Given - Create flow context and add triggering events
        final ClassFileChanged triggerEvent = ClassFileChanged.forNewSession(
            Paths.get("/test/TestClass.class"),
            "TestClass",
            1024,
            Instant.now()
        );
        docProvider.addRecentEvent(triggerEvent);

        // When
        final Optional<String> contextualUrl = docProvider.getContextualDocumentationUrl(String.class);

        // Then
        assertTrue(contextualUrl.isPresent());
        // Should get either contextual URL (with flows/) or basic fallback
        final String url = contextualUrl.get();
        assertTrue(url.contains(".html"));
        // Contextual URLs should be flow-specific if flow detected
        if (url.contains("flows/")) {
            assertTrue(url.contains("String-in-"));
        }
    }

    @Test
    @DisplayName("Should generate explicit flow documentation URLs")
    public void shouldGenerateExplicitFlowDocumentationUrls() {
        // Given - Create explicit flow context
        final Flow hotSwapFlow = Flow.builder()
            .flowId(FlowId.of("hot-swap-demo"))
            .name("HotSwapDemo")
            .description("Demonstration hot-swap flow")
            .eventSequence(List.of())
            .minimumEventCount(1)
            .maximumTimeWindow(Duration.ofMinutes(1))
            .confidence(0.9)
            .conditions(Optional.empty())
            .build();

        // When
        final Optional<String> flowUrl = docProvider.getFlowDocumentationUrl(String.class, hotSwapFlow);

        // Then
        assertTrue(flowUrl.isPresent());
        assertTrue(flowUrl.get().contains("flows/"));
        assertTrue(flowUrl.get().contains("hotswapdemo"));
        assertTrue(flowUrl.get().contains("String-in-hotswapdemo"));
    }

    @Test
    @DisplayName("Should generate testing documentation URLs")
    public void shouldGenerateTestingDocumentationUrls() {
        // When
        final Optional<String> stringTestUrl = docProvider.getTestingDocumentationUrl(String.class);
        final Optional<String> docProviderTestUrl = docProvider.getTestingDocumentationUrl(DocProvider.class);

        // Then
        assertTrue(stringTestUrl.isPresent());
        assertTrue(docProviderTestUrl.isPresent());

        assertTrue(stringTestUrl.get().contains("testing/"));
        assertTrue(stringTestUrl.get().contains("String-testing.html"));
        
        assertTrue(docProviderTestUrl.get().contains("testing/"));
        assertTrue(docProviderTestUrl.get().contains("DocProvider-testing.html"));
    }

    @Test
    @DisplayName("Should generate consistent URLs for same inputs")
    public void shouldGenerateConsistentUrlsForSameInputs() {
        // When - Generate same URL multiple times
        final Optional<String> url1 = docProvider.getDocumentationUrl(String.class);
        final Optional<String> url2 = docProvider.getDocumentationUrl(String.class);
        final Optional<String> url3 = docProvider.getDocumentationUrl(String.class);

        // Then
        assertTrue(url1.isPresent());
        assertTrue(url2.isPresent());
        assertTrue(url3.isPresent());

        assertEquals(url1.get(), url2.get());
        assertEquals(url2.get(), url3.get());
    }

    @Test
    @DisplayName("Should use base URL from Defaults configuration")
    public void shouldUseBaseUrlFromDefaultsConfiguration() {
        // When
        final Optional<String> url = docProvider.getDocumentationUrl(String.class);

        // Then
        assertTrue(url.isPresent());
        assertTrue(url.get().startsWith(Defaults.DOCUMENTATION_BASE_URL));
    }

    @Test
    @DisplayName("Should handle classes from different packages correctly")
    public void shouldHandleClassesFromDifferentPackagesCorrectly() {
        // Given - Classes from different packages
        final Class<?>[] testClasses = {
            String.class,                    // java.lang
            List.class,                      // java.util
            DocProvider.class,               // org.acmsl.bytehot.domain
            ClassFileChanged.class           // org.acmsl.bytehot.domain.events
        };

        // When
        for (final Class<?> testClass : testClasses) {
            final Optional<String> url = docProvider.getDocumentationUrl(testClass);

            // Then
            assertTrue(url.isPresent(), "URL should be present for " + testClass.getName());
            
            final String packagePath = testClass.getPackage().getName().replace('.', '/');
            assertTrue(url.get().contains(packagePath), 
                "URL should contain package path for " + testClass.getName());
            
            assertTrue(url.get().contains(testClass.getSimpleName() + ".html"),
                "URL should contain class name for " + testClass.getName());
        }
    }

    @Test
    @DisplayName("Should handle URL generation performance requirements")
    public void shouldHandleUrlGenerationPerformanceRequirements() {
        // Given
        final long startTime = System.currentTimeMillis();

        // When - Generate URLs for multiple classes rapidly
        for (int i = 0; i < 100; i++) {
            docProvider.getDocumentationUrl(String.class);
            docProvider.getMethodDocumentationUrl(String.class, "toString");
            docProvider.getTestingDocumentationUrl(String.class);
        }

        final long endTime = System.currentTimeMillis();
        final long duration = endTime - startTime;

        // Then - Should be fast (< 100ms for 300 operations due to caching)
        assertTrue(duration < 100, "URL generation should be fast due to caching: " + duration + "ms");
    }

    @Test
    @DisplayName("Should maintain URL format consistency across different generation methods")
    public void shouldMaintainUrlFormatConsistencyAcrossDifferentGenerationMethods() {
        // When
        final Optional<String> basicUrl = docProvider.getDocumentationUrl(String.class);
        final Optional<String> contextualUrl = docProvider.getContextualDocumentationUrl(String.class);
        final Optional<String> testingUrl = docProvider.getTestingDocumentationUrl(String.class);

        // Then
        assertTrue(basicUrl.isPresent());
        assertTrue(contextualUrl.isPresent());
        assertTrue(testingUrl.isPresent());

        // All URLs should use the same base URL
        final String baseUrl = Defaults.DOCUMENTATION_BASE_URL;
        assertTrue(basicUrl.get().startsWith(baseUrl));
        assertTrue(contextualUrl.get().startsWith(baseUrl));
        assertTrue(testingUrl.get().startsWith(baseUrl));

        // All URLs should end with .html
        assertTrue(basicUrl.get().endsWith(".html"));
        assertTrue(contextualUrl.get().endsWith(".html"));
        assertTrue(testingUrl.get().endsWith(".html"));
    }
}