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

*/
package org.acmsl.bytehot.domain.events;

import org.acmsl.bytehot.domain.DocumentationGenerationStrategy;
import org.acmsl.bytehot.domain.Flow;
import org.acmsl.bytehot.domain.FlowId;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for DocumentationLinkGenerated event.
 * @author Claude Code
 * @since 2025-06-24
 */
public class DocumentationLinkGeneratedTest {

    @Test
    @DisplayName("Should create basic documentation link generation event")
    public void shouldCreateBasicDocumentationLinkGenerationEvent() {
        // Given
        final DocumentationRequested originalRequest = DocumentationRequested.forClass(String.class, "user123");
        final String generatedUrl = "https://docs.example.com/String.html";
        final Duration generationTime = Duration.ofMillis(25);

        // When
        final DocumentationLinkGenerated event = DocumentationLinkGenerated.forBasicGeneration(
            originalRequest, generatedUrl, generationTime
        );

        // Then
        assertNotNull(event);
        assertEquals(originalRequest, event.getOriginalRequest());
        assertEquals(originalRequest, event.getPreceding());
        assertEquals(generatedUrl, event.getGeneratedUrl());
        assertEquals(generationTime, event.getGenerationTime());
        assertEquals(DocumentationGenerationStrategy.BASIC, event.getStrategy());
        assertFalse(event.isWasCached());
        assertTrue(event.getAppliedFlowContext().isEmpty());
        assertNotNull(event.getGeneratedAt());
        assertEquals("documentation-generation", event.getAggregateType());
    }

    @Test
    @DisplayName("Should create contextual documentation link generation event")
    public void shouldCreateContextualDocumentationLinkGenerationEvent() {
        // Given
        final DocumentationRequested originalRequest = DocumentationRequested.forClass(String.class, "user123");
        final String generatedUrl = "https://docs.example.com/String-contextual.html";
        final Flow flowContext = Flow.builder()
            .flowId(FlowId.of("test-flow"))
            .name("test-flow")
            .description("Test flow")
            .eventSequence(List.of())
            .minimumEventCount(1)
            .maximumTimeWindow(Duration.ofMinutes(1))
            .confidence(0.8)
            .conditions(Optional.empty())
            .build();
        final Duration generationTime = Duration.ofMillis(45);

        // When
        final DocumentationLinkGenerated event = DocumentationLinkGenerated.forContextualGeneration(
            originalRequest, generatedUrl, flowContext, generationTime
        );

        // Then
        assertNotNull(event);
        assertEquals(originalRequest, event.getOriginalRequest());
        assertEquals(generatedUrl, event.getGeneratedUrl());
        assertEquals(generationTime, event.getGenerationTime());
        assertEquals(DocumentationGenerationStrategy.CONTEXTUAL, event.getStrategy());
        assertFalse(event.isWasCached());
        assertTrue(event.getAppliedFlowContext().isPresent());
        assertEquals(flowContext, event.getAppliedFlowContext().get());
        assertTrue(event.hasFlowContext());
        assertTrue(event.isContextualGeneration());
    }

    @Test
    @DisplayName("Should create cached documentation link event")
    public void shouldCreateCachedDocumentationLinkEvent() {
        // Given
        final DocumentationRequested originalRequest = DocumentationRequested.forClass(String.class, "user123");
        final String cachedUrl = "https://docs.example.com/String-cached.html";
        final Duration cacheRetrievalTime = Duration.ofMillis(2);

        // When
        final DocumentationLinkGenerated event = DocumentationLinkGenerated.forCachedGeneration(
            originalRequest, cachedUrl, cacheRetrievalTime
        );

        // Then
        assertNotNull(event);
        assertEquals(originalRequest, event.getOriginalRequest());
        assertEquals(cachedUrl, event.getGeneratedUrl());
        assertEquals(cacheRetrievalTime, event.getGenerationTime());
        assertEquals(DocumentationGenerationStrategy.CACHED, event.getStrategy());
        assertTrue(event.isWasCached());
        assertTrue(event.getAppliedFlowContext().isEmpty());
        assertTrue(event.isFastGeneration());
    }

    @Test
    @DisplayName("Should correctly categorize generation performance")
    public void shouldCorrectlyCategorizeGenerationPerformance() {
        // Given
        final DocumentationRequested originalRequest = DocumentationRequested.forClass(String.class, "user123");

        // When
        final DocumentationLinkGenerated fastEvent = DocumentationLinkGenerated.forBasicGeneration(
            originalRequest, "url", Duration.ofMillis(5)
        );
        final DocumentationLinkGenerated normalEvent = DocumentationLinkGenerated.forBasicGeneration(
            originalRequest, "url", Duration.ofMillis(25)
        );
        final DocumentationLinkGenerated slowEvent = DocumentationLinkGenerated.forBasicGeneration(
            originalRequest, "url", Duration.ofMillis(75)
        );

        // Then
        assertEquals("FAST", fastEvent.getPerformanceCategory());
        assertTrue(fastEvent.isFastGeneration());

        assertEquals("NORMAL", normalEvent.getPerformanceCategory());
        assertFalse(normalEvent.isFastGeneration());

        assertEquals("SLOW", slowEvent.getPerformanceCategory());
        assertFalse(slowEvent.isFastGeneration());
    }

    @Test
    @DisplayName("Should properly handle contextual generation detection")
    public void shouldProperlyHandleContextualGenerationDetection() {
        // Given
        final DocumentationRequested originalRequest = DocumentationRequested.forClass(String.class, "user123");
        final Flow flowContext = Flow.builder()
            .flowId(FlowId.of("test-flow-context"))
            .name("test-flow-context")
            .description("Test flow")
            .eventSequence(List.of())
            .minimumEventCount(1)
            .maximumTimeWindow(Duration.ofMinutes(1))
            .confidence(0.8)
            .conditions(Optional.empty())
            .build();

        // When
        final DocumentationLinkGenerated basicEvent = DocumentationLinkGenerated.forBasicGeneration(
            originalRequest, "url", Duration.ofMillis(10)
        );
        final DocumentationLinkGenerated contextualEvent = DocumentationLinkGenerated.forContextualGeneration(
            originalRequest, "url", flowContext, Duration.ofMillis(10)
        );

        // Then
        assertFalse(basicEvent.isContextualGeneration());
        assertFalse(basicEvent.hasFlowContext());

        assertTrue(contextualEvent.isContextualGeneration());
        assertTrue(contextualEvent.hasFlowContext());
    }

    @Test
    @DisplayName("Should implement DomainResponseEvent properly")
    public void shouldImplementDomainResponseEventProperly() {
        // Given
        final DocumentationRequested originalRequest = DocumentationRequested.forClass(String.class, "user123");

        // When
        final DocumentationLinkGenerated event = DocumentationLinkGenerated.forBasicGeneration(
            originalRequest, "url", Duration.ofMillis(10)
        );

        // Then
        assertEquals(originalRequest, event.getPreceding());
        assertNotNull(event.getEventId());
        assertNotNull(event.getCorrelationId());
        assertEquals(originalRequest.getEventId(), event.getCorrelationId());
    }
}