/*
                        ByteHot

    Copyright (C) 2025-today  rydnr@acm-sl.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.acmsl.bytehot.domain;

import org.acmsl.bytehot.domain.events.ClassFileChanged;
import org.acmsl.bytehot.domain.events.ClassMetadataExtracted;
import org.acmsl.bytehot.domain.events.BytecodeValidated;
import org.acmsl.commons.patterns.DomainEvent;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for Flow domain value object.
 * @author Claude (Anthropic AI)
 * @since 2025-06-19
 */
final class FlowTest {

    @Test
    void should_create_valid_flow() {
        // Given: Valid flow parameters
        FlowId flowId = FlowId.random();
        String name = "Test Flow";
        String description = "A test flow for validation";
        List<Class<? extends DomainEvent>> eventSequence = List.of(
            ClassFileChanged.class,
            ClassMetadataExtracted.class
        );
        int minimumEventCount = 2;
        Duration maximumTimeWindow = Duration.ofMinutes(5);
        double confidence = 0.85;

        // When: Creating a flow
        Flow flow = Flow.builder()
            .flowId(flowId)
            .name(name)
            .description(description)
            .eventSequence(eventSequence)
            .minimumEventCount(minimumEventCount)
            .maximumTimeWindow(maximumTimeWindow)
            .confidence(confidence)
            .conditions(Optional.empty())
            .build();

        // Then: Flow should be valid and contain expected values
        assertThat(flow.isValid()).isTrue();
        assertThat(flow.getFlowId()).isEqualTo(flowId);
        assertThat(flow.getName()).isEqualTo(name);
        assertThat(flow.getDescription()).isEqualTo(description);
        assertThat(flow.getEventSequence()).containsExactlyElementsOf(eventSequence);
        assertThat(flow.getMinimumEventCount()).isEqualTo(minimumEventCount);
        assertThat(flow.getMaximumTimeWindow()).isEqualTo(maximumTimeWindow);
        assertThat(flow.getConfidence()).isEqualTo(confidence);
        assertThat(flow.getConditions()).isEmpty();
    }

    @Test
    void should_identify_invalid_flow_with_null_fields() {
        // Given: Flow with null required fields
        Flow flow = Flow.builder()
            .flowId(null)
            .name(null)
            .description(null)
            .eventSequence(null)
            .minimumEventCount(0)
            .maximumTimeWindow(null)
            .confidence(-1.0)
            .conditions(Optional.empty())
            .build();

        // When/Then: Flow should be invalid
        assertThat(flow.isValid()).isFalse();
    }

    @Test
    void should_identify_invalid_flow_with_empty_name() {
        // Given: Flow with empty name
        Flow flow = Flow.builder()
            .flowId(FlowId.random())
            .name("")
            .description("Valid description")
            .eventSequence(List.of(ClassFileChanged.class))
            .minimumEventCount(1)
            .maximumTimeWindow(Duration.ofMinutes(5))
            .confidence(0.8)
            .conditions(Optional.empty())
            .build();

        // When/Then: Flow should be invalid
        assertThat(flow.isValid()).isFalse();
    }

    @Test
    void should_identify_invalid_flow_with_confidence_out_of_range() {
        // Given: Flow with confidence greater than 1.0
        Flow flow = Flow.builder()
            .flowId(FlowId.random())
            .name("Test Flow")
            .description("Valid description")
            .eventSequence(List.of(ClassFileChanged.class))
            .minimumEventCount(1)
            .maximumTimeWindow(Duration.ofMinutes(5))
            .confidence(1.5)
            .conditions(Optional.empty())
            .build();

        // When/Then: Flow should be invalid
        assertThat(flow.isValid()).isFalse();
    }

    @Test
    void should_match_exact_event_sequence() {
        // Given: Flow with specific event sequence
        Flow flow = Flow.builder()
            .flowId(FlowId.random())
            .name("Test Flow")
            .description("Test description")
            .eventSequence(List.of(ClassFileChanged.class, ClassMetadataExtracted.class))
            .minimumEventCount(2)
            .maximumTimeWindow(Duration.ofMinutes(5))
            .confidence(0.8)
            .conditions(Optional.empty())
            .build();

        // When: Testing with matching event sequence
        List<Class<? extends DomainEvent>> events = List.of(
            ClassFileChanged.class,
            ClassMetadataExtracted.class
        );

        // Then: Should match
        assertThat(flow.matches(events)).isTrue();
    }

    @Test
    void should_match_event_sequence_with_extra_events() {
        // Given: Flow with specific event sequence
        Flow flow = Flow.builder()
            .flowId(FlowId.random())
            .name("Test Flow")
            .description("Test description")
            .eventSequence(List.of(ClassFileChanged.class, ClassMetadataExtracted.class))
            .minimumEventCount(2)
            .maximumTimeWindow(Duration.ofMinutes(5))
            .confidence(0.8)
            .conditions(Optional.empty())
            .build();

        // When: Testing with event sequence containing the pattern plus extra events
        List<Class<? extends DomainEvent>> events = List.of(
            ClassFileChanged.class,
            ClassMetadataExtracted.class,
            BytecodeValidated.class
        );

        // Then: Should match (pattern is contained)
        assertThat(flow.matches(events)).isTrue();
    }

    @Test
    void should_not_match_insufficient_events() {
        // Given: Flow requiring minimum 2 events
        Flow flow = Flow.builder()
            .flowId(FlowId.random())
            .name("Test Flow")
            .description("Test description")
            .eventSequence(List.of(ClassFileChanged.class, ClassMetadataExtracted.class))
            .minimumEventCount(2)
            .maximumTimeWindow(Duration.ofMinutes(5))
            .confidence(0.8)
            .conditions(Optional.empty())
            .build();

        // When: Testing with only one event
        List<Class<? extends DomainEvent>> events = List.of(ClassFileChanged.class);

        // Then: Should not match
        assertThat(flow.matches(events)).isFalse();
    }

    @Test
    void should_not_match_different_event_sequence() {
        // Given: Flow with specific event sequence
        Flow flow = Flow.builder()
            .flowId(FlowId.random())
            .name("Test Flow")
            .description("Test description")
            .eventSequence(List.of(ClassFileChanged.class, ClassMetadataExtracted.class))
            .minimumEventCount(2)
            .maximumTimeWindow(Duration.ofMinutes(5))
            .confidence(0.8)
            .conditions(Optional.empty())
            .build();

        // When: Testing with different event sequence
        List<Class<? extends DomainEvent>> events = List.of(
            BytecodeValidated.class,
            ClassMetadataExtracted.class
        );

        // Then: Should not match
        assertThat(flow.matches(events)).isFalse();
    }

    @Test
    void should_get_starting_event_types() {
        // Given: Flow with event sequence
        Flow flow = Flow.builder()
            .flowId(FlowId.random())
            .name("Test Flow")
            .description("Test description")
            .eventSequence(List.of(ClassFileChanged.class, ClassMetadataExtracted.class))
            .minimumEventCount(2)
            .maximumTimeWindow(Duration.ofMinutes(5))
            .confidence(0.8)
            .conditions(Optional.empty())
            .build();

        // When: Getting starting event types
        List<Class<? extends DomainEvent>> startingTypes = flow.getStartingEventTypes();

        // Then: Should return first event type
        assertThat(startingTypes).containsExactly(ClassFileChanged.class);
    }

    @Test
    void should_get_ending_event_types() {
        // Given: Flow with event sequence
        Flow flow = Flow.builder()
            .flowId(FlowId.random())
            .name("Test Flow")
            .description("Test description")
            .eventSequence(List.of(ClassFileChanged.class, ClassMetadataExtracted.class))
            .minimumEventCount(2)
            .maximumTimeWindow(Duration.ofMinutes(5))
            .confidence(0.8)
            .conditions(Optional.empty())
            .build();

        // When: Getting ending event types
        List<Class<? extends DomainEvent>> endingTypes = flow.getEndingEventTypes();

        // Then: Should return last event type
        assertThat(endingTypes).containsExactly(ClassMetadataExtracted.class);
    }

    @Test
    void should_create_flow_with_updated_confidence() {
        // Given: Existing flow
        Flow originalFlow = Flow.builder()
            .flowId(FlowId.random())
            .name("Test Flow")
            .description("Test description")
            .eventSequence(List.of(ClassFileChanged.class))
            .minimumEventCount(1)
            .maximumTimeWindow(Duration.ofMinutes(5))
            .confidence(0.7)
            .conditions(Optional.empty())
            .build();

        // When: Creating flow with updated confidence
        Flow updatedFlow = originalFlow.withConfidence(0.9);

        // Then: New flow should have updated confidence but same other properties
        assertThat(updatedFlow.getConfidence()).isEqualTo(0.9);
        assertThat(updatedFlow.getFlowId()).isEqualTo(originalFlow.getFlowId());
        assertThat(updatedFlow.getName()).isEqualTo(originalFlow.getName());
        assertThat(updatedFlow.getDescription()).isEqualTo(originalFlow.getDescription());
        assertThat(updatedFlow.getEventSequence()).isEqualTo(originalFlow.getEventSequence());
    }

    @Test
    void should_reject_invalid_confidence_when_updating() {
        // Given: Existing flow
        Flow flow = Flow.builder()
            .flowId(FlowId.random())
            .name("Test Flow")
            .description("Test description")
            .eventSequence(List.of(ClassFileChanged.class))
            .minimumEventCount(1)
            .maximumTimeWindow(Duration.ofMinutes(5))
            .confidence(0.7)
            .conditions(Optional.empty())
            .build();

        // When/Then: Updating with invalid confidence should throw exception
        assertThatThrownBy(() -> flow.withConfidence(1.5))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Confidence must be between 0.0 and 1.0");
    }

    @Test
    void should_handle_empty_event_sequence() {
        // Given: Flow with empty event sequence
        Flow flow = Flow.builder()
            .flowId(FlowId.random())
            .name("Empty Flow")
            .description("Flow with no events")
            .eventSequence(List.of())
            .minimumEventCount(0)
            .maximumTimeWindow(Duration.ofMinutes(5))
            .confidence(0.5)
            .conditions(Optional.empty())
            .build();

        // When: Getting starting and ending event types
        List<Class<? extends DomainEvent>> startingTypes = flow.getStartingEventTypes();
        List<Class<? extends DomainEvent>> endingTypes = flow.getEndingEventTypes();

        // Then: Should return empty lists
        assertThat(startingTypes).isEmpty();
        assertThat(endingTypes).isEmpty();
    }

    @Test
    void should_consider_flow_invalid_with_minimum_event_count_greater_than_sequence_size() {
        // Given: Flow with minimum event count greater than sequence size
        Flow flow = Flow.builder()
            .flowId(FlowId.random())
            .name("Invalid Flow")
            .description("Flow with inconsistent counts")
            .eventSequence(List.of(ClassFileChanged.class))
            .minimumEventCount(5)  // More than the sequence size
            .maximumTimeWindow(Duration.ofMinutes(5))
            .confidence(0.8)
            .conditions(Optional.empty())
            .build();

        // When/Then: Flow should be invalid
        assertThat(flow.isValid()).isFalse();
    }
}