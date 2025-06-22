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
/******************************************************************************
 *
 * Filename: JsonFlow.java
 *
 * Author: Claude (Anthropic AI)
 *
 * Class name: JsonFlow
 *
 * Responsibilities:
 *   - JSON serialization/deserialization of Flow domain objects
 *   - Convert between domain Flow and JSON representation
 *
 * Collaborators:
 *   - Flow: Domain object being serialized
 *   - FlowId: Flow identifier
 *   - FlowCondition: Optional flow conditions
 */
package org.acmsl.bytehot.infrastructure;

import org.acmsl.bytehot.domain.Flow;
import org.acmsl.bytehot.domain.FlowCondition;
import org.acmsl.bytehot.domain.FlowId;
import org.acmsl.commons.patterns.DomainEvent;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * JSON Data Transfer Object for Flow domain objects.
 * @author Claude (Anthropic AI)
 * @since 2025-06-19
 */
@Builder
@EqualsAndHashCode
@ToString
public final class JsonFlow {

    /**
     * Unique identifier for the flow.
     */
    @Getter
    @JsonProperty("flowId")
    private final String flowId;

    /**
     * Human-readable name for the flow.
     */
    @Getter
    @JsonProperty("name")
    private final String name;

    /**
     * Description of what this flow represents.
     */
    @Getter
    @JsonProperty("description")
    private final String description;

    /**
     * Sequence of event type names that form this flow.
     */
    @Getter
    @JsonProperty("eventSequence")
    private final List<String> eventSequence;

    /**
     * Minimum number of events required to identify this flow.
     */
    @Getter
    @JsonProperty("minimumEventCount")
    private final int minimumEventCount;

    /**
     * Maximum time window for events in milliseconds.
     */
    @Getter
    @JsonProperty("maximumTimeWindowMillis")
    private final long maximumTimeWindowMillis;

    /**
     * Confidence level for flow detection (0.0 to 1.0).
     */
    @Getter
    @JsonProperty("confidence")
    private final double confidence;

    /**
     * Optional condition name (simplified for JSON).
     */
    @Getter
    @JsonProperty("conditionName")
    private final String conditionName;

    @JsonCreator
    public JsonFlow(
        @JsonProperty("flowId") final String flowId,
        @JsonProperty("name") final String name,
        @JsonProperty("description") final String description,
        @JsonProperty("eventSequence") final List<String> eventSequence,
        @JsonProperty("minimumEventCount") final int minimumEventCount,
        @JsonProperty("maximumTimeWindowMillis") final long maximumTimeWindowMillis,
        @JsonProperty("confidence") final double confidence,
        @JsonProperty("conditionName") final String conditionName
    ) {
        this.flowId = flowId;
        this.name = name;
        this.description = description;
        this.eventSequence = eventSequence;
        this.minimumEventCount = minimumEventCount;
        this.maximumTimeWindowMillis = maximumTimeWindowMillis;
        this.confidence = confidence;
        this.conditionName = conditionName;
    }

    /**
     * Creates a JsonFlow from a domain Flow object.
     * @param flow The domain flow to convert
     * @return JsonFlow representation
     */
    public static JsonFlow fromDomain(final Flow flow) {
        if (flow == null) {
            return null;
        }

        List<String> eventTypeNames = flow.getEventSequence().stream()
            .map(Class::getSimpleName)
            .collect(Collectors.toList());

        String conditionName = flow.getConditions()
            .map(FlowCondition::getName)
            .orElse(null);

        return JsonFlow.builder()
            .flowId(flow.getFlowId().getValue())
            .name(flow.getName())
            .description(flow.getDescription())
            .eventSequence(eventTypeNames)
            .minimumEventCount(flow.getMinimumEventCount())
            .maximumTimeWindowMillis(flow.getMaximumTimeWindow().toMillis())
            .confidence(flow.getConfidence())
            .conditionName(conditionName)
            .build();
    }

    /**
     * Converts this JsonFlow to a domain Flow object.
     * @return Domain Flow representation
     */
    @SuppressWarnings("unchecked")
    public Flow toDomain() {
        // Convert event type names back to classes
        List<Class<? extends DomainEvent>> eventTypes = eventSequence.stream()
            .map(this::getEventClassByName)
            .collect(Collectors.toList());

        // For simplicity, we don't reconstruct complex conditions from JSON
        // In a production system, we might need a more sophisticated approach
        Optional<FlowCondition> conditions = Optional.empty();

        return Flow.builder()
            .flowId(FlowId.of(flowId))
            .name(name)
            .description(description)
            .eventSequence(eventTypes)
            .minimumEventCount(minimumEventCount)
            .maximumTimeWindow(Duration.ofMillis(maximumTimeWindowMillis))
            .confidence(confidence)
            .conditions(conditions)
            .build();
    }

    @SuppressWarnings("unchecked")
    private Class<? extends DomainEvent> getEventClassByName(final String eventTypeName) {
        try {
            // Try to load the class from the events package
            String packageName = "org.acmsl.bytehot.domain.events.";
            Class<?> clazz = Class.forName(packageName + eventTypeName);
            
            if (DomainEvent.class.isAssignableFrom(clazz)) {
                return (Class<? extends DomainEvent>) clazz;
            } else {
                throw new IllegalArgumentException("Class " + eventTypeName + " is not a DomainEvent");
            }
        } catch (ClassNotFoundException e) {
            // For missing event types, create a placeholder
            // In a production system, this might need more sophisticated handling
            throw new RuntimeException("Unknown event type: " + eventTypeName, e);
        }
    }
}