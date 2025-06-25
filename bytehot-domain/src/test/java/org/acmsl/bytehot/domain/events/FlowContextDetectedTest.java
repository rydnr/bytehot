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

import org.acmsl.bytehot.domain.Flow;
import org.acmsl.bytehot.domain.FlowId;
import org.acmsl.commons.patterns.eventsourcing.VersionedDomainEvent;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for FlowContextDetected event.
 * @author Claude Code
 * @since 2025-06-24
 */
public class FlowContextDetectedTest {

    @Test
    @DisplayName("Should create new flow detection event")
    public void shouldCreateNewFlowDetectionEvent() {
        // Given
        final Flow detectedFlow = Flow.builder()
            .flowId(FlowId.of("test-flow"))
            .name("test-flow")
            .description("Test flow description")
            .eventSequence(List.of())
            .minimumEventCount(1)
            .maximumTimeWindow(Duration.ofMinutes(1))
            .confidence(0.8)
            .conditions(Optional.empty())
            .build();
        final double confidence = 0.85;
        final List<String> detectionSources = List.of("CALL_STACK", "EVENT_SEQUENCE");
        final List<VersionedDomainEvent> triggeringEvents = List.of();
        final Duration detectionTime = Duration.ofMillis(15);

        // When
        final FlowContextDetected event = FlowContextDetected.forNewDetection(
            detectedFlow, confidence, detectionSources, triggeringEvents, detectionTime
        );

        // Then
        assertNotNull(event);
        assertEquals(detectedFlow, event.getDetectedFlow());
        assertEquals(confidence, event.getConfidence(), 0.001);
        assertEquals(detectionSources, event.getDetectionSources());
        assertEquals(triggeringEvents, event.getTriggeringEvents());
        assertEquals(detectionTime, event.getDetectionTime());
        assertTrue(event.getPreviousFlow().isEmpty());
        assertFalse(event.isFlowTransition());
        assertNotNull(event.getDetectedAt());
        assertEquals("flow-detection", event.getAggregateType());
        assertEquals(detectedFlow.getName(), event.getAggregateId());
    }

    @Test
    @DisplayName("Should create flow transition event")
    public void shouldCreateFlowTransitionEvent() {
        // Given
        final Flow previousFlow = Flow.builder()
            .flowId(FlowId.of("old-flow"))
            .name("old-flow")
            .description("Old flow")
            .eventSequence(List.of())
            .minimumEventCount(1)
            .maximumTimeWindow(Duration.ofMinutes(1))
            .confidence(0.8)
            .conditions(Optional.empty())
            .build();
        final Flow detectedFlow = Flow.builder()
            .flowId(FlowId.of("new-flow"))
            .name("new-flow")
            .description("New flow")
            .eventSequence(List.of())
            .minimumEventCount(1)
            .maximumTimeWindow(Duration.ofMinutes(1))
            .confidence(0.8)
            .conditions(Optional.empty())
            .build();
        final double confidence = 0.92;
        final List<String> detectionSources = List.of("CONFIG_STATE", "FILE_SYSTEM");
        final List<VersionedDomainEvent> triggeringEvents = List.of();
        final Duration detectionTime = Duration.ofMillis(8);

        // When
        final FlowContextDetected event = FlowContextDetected.forFlowTransition(
            detectedFlow, previousFlow, confidence, detectionSources, triggeringEvents, detectionTime
        );

        // Then
        assertNotNull(event);
        assertEquals(detectedFlow, event.getDetectedFlow());
        assertEquals(confidence, event.getConfidence(), 0.001);
        assertEquals(detectionSources, event.getDetectionSources());
        assertEquals(detectionTime, event.getDetectionTime());
        assertTrue(event.getPreviousFlow().isPresent());
        assertEquals(previousFlow, event.getPreviousFlow().get());
        assertTrue(event.isFlowTransition());
        assertEquals("flow-detection", event.getAggregateType());
        assertEquals(previousFlow.getName() + "->" + detectedFlow.getName(), event.getAggregateId());
    }

    @Test
    @DisplayName("Should correctly classify confidence levels")
    public void shouldCorrectlyClassifyConfidenceLevels() {
        // Given
        final Flow flow = Flow.builder()
            .flowId(FlowId.of("test-flow"))
            .name("test-flow")
            .description("Test flow")
            .eventSequence(List.of())
            .minimumEventCount(1)
            .maximumTimeWindow(Duration.ofMinutes(1))
            .confidence(0.8)
            .conditions(Optional.empty())
            .build();
        final List<String> sources = List.of("CALL_STACK");
        final List<VersionedDomainEvent> events = List.of();
        final Duration time = Duration.ofMillis(10);

        // When
        final FlowContextDetected highConfidenceEvent = FlowContextDetected.forNewDetection(
            flow, 0.85, sources, events, time
        );
        final FlowContextDetected mediumConfidenceEvent = FlowContextDetected.forNewDetection(
            flow, 0.65, sources, events, time
        );
        final FlowContextDetected lowConfidenceEvent = FlowContextDetected.forNewDetection(
            flow, 0.45, sources, events, time
        );

        // Then
        assertTrue(highConfidenceEvent.isHighConfidence());
        assertFalse(mediumConfidenceEvent.isHighConfidence());
        assertFalse(lowConfidenceEvent.isHighConfidence());
    }

    @Test
    @DisplayName("Should correctly categorize detection performance")
    public void shouldCorrectlyCategorizeDetectionPerformance() {
        // Given
        final Flow flow = Flow.builder()
            .flowId(FlowId.of("test-flow-perf"))
            .name("test-flow-perf")
            .description("Test flow")
            .eventSequence(List.of())
            .minimumEventCount(1)
            .maximumTimeWindow(Duration.ofMinutes(1))
            .confidence(0.8)
            .conditions(Optional.empty())
            .build();
        final double confidence = 0.8;
        final List<String> sources = List.of("CALL_STACK");
        final List<VersionedDomainEvent> events = List.of();

        // When
        final FlowContextDetected fastEvent = FlowContextDetected.forNewDetection(
            flow, confidence, sources, events, Duration.ofMillis(5)
        );
        final FlowContextDetected normalEvent = FlowContextDetected.forNewDetection(
            flow, confidence, sources, events, Duration.ofMillis(25)
        );
        final FlowContextDetected slowEvent = FlowContextDetected.forNewDetection(
            flow, confidence, sources, events, Duration.ofMillis(75)
        );

        // Then
        assertEquals("FAST", fastEvent.getPerformanceCategory());
        assertEquals("NORMAL", normalEvent.getPerformanceCategory());
        assertEquals("SLOW", slowEvent.getPerformanceCategory());
    }

    @Test
    @DisplayName("Should maintain immutable collections")
    public void shouldMaintainImmutableCollections() {
        // Given
        final Flow flow = Flow.builder()
            .flowId(FlowId.of("test-flow-perf"))
            .name("test-flow-perf")
            .description("Test flow")
            .eventSequence(List.of())
            .minimumEventCount(1)
            .maximumTimeWindow(Duration.ofMinutes(1))
            .confidence(0.8)
            .conditions(Optional.empty())
            .build();
        final List<String> detectionSources = List.of("CALL_STACK", "EVENT_SEQUENCE");
        final List<VersionedDomainEvent> triggeringEvents = List.of();

        // When
        final FlowContextDetected event = FlowContextDetected.forNewDetection(
            flow, 0.8, detectionSources, triggeringEvents, Duration.ofMillis(10)
        );

        // Then
        // Note: List.of() creates immutable lists, so these may be the same reference
        // The important thing is that the content is preserved and immutable
        assertEquals(detectionSources, event.getDetectionSources());
        assertEquals(triggeringEvents, event.getTriggeringEvents());

        // Collections should be immutable (defensive copies or immutable views)
        assertThrows(UnsupportedOperationException.class, () -> {
            event.getDetectionSources().add("NEW_SOURCE");
        });
        assertThrows(UnsupportedOperationException.class, () -> {
            event.getTriggeringEvents().add(null);
        });
    }

    @Test
    @DisplayName("Should have proper versioned domain event structure")
    public void shouldHaveProperVersionedDomainEventStructure() {
        // Given
        final Flow flow = Flow.builder()
            .flowId(FlowId.of("test-flow-perf"))
            .name("test-flow-perf")
            .description("Test flow")
            .eventSequence(List.of())
            .minimumEventCount(1)
            .maximumTimeWindow(Duration.ofMinutes(1))
            .confidence(0.8)
            .conditions(Optional.empty())
            .build();

        // When
        final FlowContextDetected event = FlowContextDetected.forNewDetection(
            flow, 0.8, List.of("CALL_STACK"), List.of(), Duration.ofMillis(10)
        );

        // Then
        assertNotNull(event.getEventId());
        assertNotNull(event.getAggregateType());
        assertNotNull(event.getAggregateId());
        assertTrue(event.getAggregateVersion() >= 0);
        assertNotNull(event.getTimestamp());
        assertTrue(event.getSchemaVersion() >= 1);
        // Note: getUserId() from parent class may be null since we use forNewAggregate
    }
}