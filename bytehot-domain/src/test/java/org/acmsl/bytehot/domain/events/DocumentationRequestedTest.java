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

import org.acmsl.bytehot.domain.DocumentationType;
import org.acmsl.bytehot.domain.Flow;
import org.acmsl.bytehot.domain.FlowId;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for DocumentationRequested event.
 * @author Claude Code
 * @since 2025-06-24
 */
public class DocumentationRequestedTest {

    @Test
    @DisplayName("Should create basic class documentation request")
    public void shouldCreateBasicClassDocumentationRequest() {
        // Given
        final Class<?> requestingClass = String.class;
        final String userId = "user123";

        // When
        final DocumentationRequested event = DocumentationRequested.forClass(requestingClass, userId);

        // Then
        assertNotNull(event);
        assertEquals(requestingClass, event.getRequestingClass());
        assertEquals(userId, event.getRequestingUserId());
        assertEquals(DocumentationType.BASIC, event.getDocumentationType());
        assertTrue(event.getMethodName().isEmpty());
        assertTrue(event.getExplicitFlowContext().isEmpty());
        assertNotNull(event.getRequestedAt());
        assertNotNull(event.getEventId());
        assertEquals("documentation", event.getAggregateType());
        assertEquals(requestingClass.getSimpleName(), event.getAggregateId());
    }

    @Test
    @DisplayName("Should create method-specific documentation request")
    public void shouldCreateMethodSpecificDocumentationRequest() {
        // Given
        final Class<?> requestingClass = String.class;
        final String methodName = "toString";
        final String userId = "user456";

        // When
        final DocumentationRequested event = DocumentationRequested.forMethod(requestingClass, methodName, userId);

        // Then
        assertNotNull(event);
        assertEquals(requestingClass, event.getRequestingClass());
        assertEquals(userId, event.getRequestingUserId());
        assertEquals(DocumentationType.METHOD, event.getDocumentationType());
        assertTrue(event.getMethodName().isPresent());
        assertEquals(methodName, event.getMethodName().get());
        assertTrue(event.getExplicitFlowContext().isEmpty());
        assertNotNull(event.getRequestedAt());
        assertEquals("documentation", event.getAggregateType());
        assertEquals(requestingClass.getSimpleName() + "." + methodName, event.getAggregateId());
    }

    @Test
    @DisplayName("Should create contextual flow documentation request")
    public void shouldCreateContextualFlowDocumentationRequest() {
        // Given
        final Class<?> requestingClass = String.class;
        final Flow flowContext = Flow.builder()
            .flowId(FlowId.of("test-flow"))
            .name("test-flow")
            .description("Test flow description")
            .eventSequence(List.of())
            .minimumEventCount(1)
            .maximumTimeWindow(Duration.ofMinutes(1))
            .confidence(0.8)
            .conditions(Optional.empty())
            .build();
        final String userId = "user789";

        // When
        final DocumentationRequested event = DocumentationRequested.forContextualFlow(requestingClass, flowContext, userId);

        // Then
        assertNotNull(event);
        assertEquals(requestingClass, event.getRequestingClass());
        assertEquals(userId, event.getRequestingUserId());
        assertEquals(DocumentationType.CONTEXTUAL, event.getDocumentationType());
        assertTrue(event.getMethodName().isEmpty());
        assertTrue(event.getExplicitFlowContext().isPresent());
        assertEquals(flowContext, event.getExplicitFlowContext().get());
        assertNotNull(event.getRequestedAt());
        assertEquals("documentation", event.getAggregateType());
        assertEquals(requestingClass.getSimpleName() + "@" + flowContext.getName(), event.getAggregateId());
    }

    @Test
    @DisplayName("Should have proper versioned domain event structure")
    public void shouldHaveProperVersionedDomainEventStructure() {
        // Given
        final DocumentationRequested event = DocumentationRequested.forClass(String.class, "user123");

        // Then
        assertNotNull(event.getEventId());
        assertNotNull(event.getAggregateType());
        assertNotNull(event.getAggregateId());
        assertTrue(event.getAggregateVersion() >= 0);
        assertNotNull(event.getTimestamp());
        assertTrue(event.getSchemaVersion() >= 1);
        // Note: getUserId() from parent class may be null since we use forNewAggregate
        // The user context is stored in requestingUserId field
        assertNotNull(event.getRequestingUserId());
    }

    @Test
    @DisplayName("Should maintain immutability")
    public void shouldMaintainImmutability() {
        // Given
        final DocumentationRequested event = DocumentationRequested.forClass(String.class, "user123");

        // When/Then - all getters should return the same values consistently
        final String eventId1 = event.getEventId();
        final String eventId2 = event.getEventId();
        assertEquals(eventId1, eventId2);

        final Class<?> requestingClass1 = event.getRequestingClass();
        final Class<?> requestingClass2 = event.getRequestingClass();
        assertEquals(requestingClass1, requestingClass2);
    }
}