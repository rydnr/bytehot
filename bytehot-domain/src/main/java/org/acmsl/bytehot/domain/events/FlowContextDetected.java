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
 * Filename: FlowContextDetected.java
 *
 * Author: Claude Code
 *
 * Class name: FlowContextDetected
 *
 * Responsibilities:
 *   - Represent when runtime Flow context is identified
 *   - Capture confidence and detection source information
 *   - Enable flow detection analytics and improvements
 *
 * Collaborators:
 *   - AbstractVersionedDomainEvent: Base class providing event metadata
 *   - Flow: The detected flow context
 *   - VersionedDomainEvent: Events that triggered the detection
 */
package org.acmsl.bytehot.domain.events;

import org.acmsl.commons.patterns.eventsourcing.AbstractVersionedDomainEvent;
import org.acmsl.commons.patterns.eventsourcing.EventMetadata;
import org.acmsl.bytehot.domain.Flow;
import org.acmsl.commons.patterns.eventsourcing.VersionedDomainEvent;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Event triggered when runtime Flow context is identified.
 * This event captures the details of flow detection including confidence
 * levels, detection sources, and performance metrics.
 * @author Claude Code
 * @since 2025-06-24
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class FlowContextDetected 
    extends AbstractVersionedDomainEvent {

    /**
     * The flow that was detected.
     */
    @Getter
    private final Flow detectedFlow;

    /**
     * Confidence level of the detection (0.0 to 1.0).
     */
    @Getter
    private final double confidence;

    /**
     * Sources used for flow detection (e.g., CALL_STACK, EVENT_SEQUENCE, CONFIG_STATE, FILE_SYSTEM).
     */
    @Getter
    private final List<String> detectionSources;

    /**
     * Events that triggered or contributed to this flow detection.
     */
    @Getter
    private final List<VersionedDomainEvent> triggeringEvents;

    /**
     * Time taken to perform the detection.
     */
    @Getter
    private final Duration detectionTime;

    /**
     * Previous flow context if this represents a flow transition.
     */
    @Getter
    private final Optional<Flow> previousFlow;

    /**
     * Timestamp when the flow context was detected.
     */
    @Getter
    private final Instant detectedAt;

    /**
     * Creates a new FlowContextDetected event.
     * @param metadata event metadata
     * @param detectedFlow the flow that was detected
     * @param confidence confidence level of the detection (0.0 to 1.0)
     * @param detectionSources sources used for flow detection
     * @param triggeringEvents events that triggered the detection
     * @param detectionTime time taken to perform the detection
     * @param previousFlow previous flow context if this is a transition
     * @param detectedAt timestamp when the flow context was detected
     */
    public FlowContextDetected(
        final EventMetadata metadata,
        final Flow detectedFlow,
        final double confidence,
        final List<String> detectionSources,
        final List<VersionedDomainEvent> triggeringEvents,
        final Duration detectionTime,
        final Optional<Flow> previousFlow,
        final Instant detectedAt
    ) {
        super(metadata);
        this.detectedFlow = detectedFlow;
        this.confidence = confidence;
        this.detectionSources = List.copyOf(detectionSources);
        this.triggeringEvents = List.copyOf(triggeringEvents);
        this.detectionTime = detectionTime;
        this.previousFlow = previousFlow;
        this.detectedAt = detectedAt;
    }

    /**
     * Factory method for creating a new flow detection event.
     * @param detectedFlow the flow that was detected
     * @param confidence confidence level of the detection
     * @param detectionSources sources used for detection
     * @param triggeringEvents events that triggered the detection
     * @param detectionTime time taken for detection
     * @return new FlowContextDetected event
     */
    public static FlowContextDetected forNewDetection(
        final Flow detectedFlow,
        final double confidence,
        final List<String> detectionSources,
        final List<VersionedDomainEvent> triggeringEvents,
        final Duration detectionTime
    ) {
        final EventMetadata metadata = createMetadataForNewAggregate(
            "flow-detection", 
            detectedFlow.getName()
        );
        
        return new FlowContextDetected(
            metadata,
            detectedFlow,
            confidence,
            detectionSources,
            triggeringEvents,
            detectionTime,
            Optional.empty(),
            Instant.now()
        );
    }

    /**
     * Factory method for creating a flow transition event.
     * @param detectedFlow the new flow that was detected
     * @param previousFlow the previous flow context
     * @param confidence confidence level of the detection
     * @param detectionSources sources used for detection
     * @param triggeringEvents events that triggered the detection
     * @param detectionTime time taken for detection
     * @return new FlowContextDetected event
     */
    public static FlowContextDetected forFlowTransition(
        final Flow detectedFlow,
        final Flow previousFlow,
        final double confidence,
        final List<String> detectionSources,
        final List<VersionedDomainEvent> triggeringEvents,
        final Duration detectionTime
    ) {
        final EventMetadata metadata = createMetadataForNewAggregate(
            "flow-detection", 
            previousFlow.getName() + "->" + detectedFlow.getName()
        );
        
        return new FlowContextDetected(
            metadata,
            detectedFlow,
            confidence,
            detectionSources,
            triggeringEvents,
            detectionTime,
            Optional.of(previousFlow),
            Instant.now()
        );
    }

    /**
     * Checks if this detection represents a high-confidence result.
     * @return true if confidence is >= 0.8
     */
    public boolean isHighConfidence() {
        return confidence >= 0.8;
    }

    /**
     * Checks if this detection represents a flow transition.
     * @return true if there was a previous flow
     */
    public boolean isFlowTransition() {
        return previousFlow.isPresent();
    }

    /**
     * Gets the detection performance category based on detection time.
     * @return performance category (FAST, NORMAL, SLOW)
     */
    public String getPerformanceCategory() {
        if (detectionTime.toMillis() < 10) {
            return "FAST";
        } else if (detectionTime.toMillis() < 50) {
            return "NORMAL";
        } else {
            return "SLOW";
        }
    }
}