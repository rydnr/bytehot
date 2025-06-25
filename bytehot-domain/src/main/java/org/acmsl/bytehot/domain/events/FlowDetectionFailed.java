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
 * Filename: FlowDetectionFailed.java
 *
 * Author: Claude Code
 *
 * Class name: FlowDetectionFailed
 *
 * Responsibilities:
 *   - Represent when Flow context detection fails
 *   - Capture failure reasons and diagnostic information
 *   - Enable flow detection system improvements
 *
 * Collaborators:
 *   - AbstractVersionedDomainEvent: Base class providing event metadata
 *   - VersionedDomainEvent: Events that were analyzed for flow detection
 */
package org.acmsl.bytehot.domain.events;

import org.acmsl.bytehot.domain.EventMetadata;
import org.acmsl.commons.patterns.eventsourcing.VersionedDomainEvent;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Event triggered when Flow context detection fails.
 * This event captures diagnostic information about failed flow detection
 * attempts and enables improvement of the flow detection system.
 * @author Claude Code
 * @since 2025-06-24
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class FlowDetectionFailed 
    extends AbstractVersionedDomainEvent {

    /**
     * Events that were analyzed during the failed detection attempt.
     */
    @Getter
    private final List<VersionedDomainEvent> analyzedEvents;

    /**
     * Sources attempted for flow detection.
     */
    @Getter
    private final List<String> attemptedSources;

    /**
     * Primary reason for the detection failure.
     */
    @Getter
    private final String failureReason;

    /**
     * Detailed error message or diagnostic information.
     */
    @Getter
    private final Optional<String> diagnosticMessage;

    /**
     * Time spent attempting the detection.
     */
    @Getter
    private final Duration attemptDuration;

    /**
     * Maximum confidence level achieved during the attempt.
     */
    @Getter
    private final double maxConfidenceReached;

    /**
     * Timestamp when the detection failure occurred.
     */
    @Getter
    private final Instant failedAt;

    /**
     * Creates a new FlowDetectionFailed event.
     * @param metadata event metadata
     * @param analyzedEvents events that were analyzed during the attempt
     * @param attemptedSources sources attempted for flow detection
     * @param failureReason primary reason for the detection failure
     * @param diagnosticMessage detailed error message or diagnostic information
     * @param attemptDuration time spent attempting the detection
     * @param maxConfidenceReached maximum confidence level achieved
     * @param failedAt timestamp when the detection failure occurred
     */
    public FlowDetectionFailed(
        final EventMetadata metadata,
        final List<VersionedDomainEvent> analyzedEvents,
        final List<String> attemptedSources,
        final String failureReason,
        final Optional<String> diagnosticMessage,
        final Duration attemptDuration,
        final double maxConfidenceReached,
        final Instant failedAt
    ) {
        super(metadata);
        this.analyzedEvents = List.copyOf(analyzedEvents);
        this.attemptedSources = List.copyOf(attemptedSources);
        this.failureReason = failureReason;
        this.diagnosticMessage = diagnosticMessage;
        this.attemptDuration = attemptDuration;
        this.maxConfidenceReached = maxConfidenceReached;
        this.failedAt = failedAt;
    }

    /**
     * Factory method for creating a flow detection failure event.
     * @param analyzedEvents events that were analyzed during the attempt
     * @param attemptedSources sources attempted for flow detection
     * @param failureReason primary reason for the detection failure
     * @param attemptDuration time spent attempting the detection
     * @param maxConfidenceReached maximum confidence level achieved
     * @return new FlowDetectionFailed event
     */
    public static FlowDetectionFailed forFailedAttempt(
        final List<VersionedDomainEvent> analyzedEvents,
        final List<String> attemptedSources,
        final String failureReason,
        final Duration attemptDuration,
        final double maxConfidenceReached
    ) {
        final EventMetadata metadata = createMetadataForNewAggregate(
            "flow-detection", 
            "failed-" + System.currentTimeMillis()
        );
        
        return new FlowDetectionFailed(
            metadata,
            analyzedEvents,
            attemptedSources,
            failureReason,
            Optional.empty(),
            attemptDuration,
            maxConfidenceReached,
            Instant.now()
        );
    }

    /**
     * Factory method for creating a detailed flow detection failure event.
     * @param analyzedEvents events that were analyzed during the attempt
     * @param attemptedSources sources attempted for flow detection
     * @param failureReason primary reason for the detection failure
     * @param diagnosticMessage detailed error message or diagnostic information
     * @param attemptDuration time spent attempting the detection
     * @param maxConfidenceReached maximum confidence level achieved
     * @return new FlowDetectionFailed event
     */
    public static FlowDetectionFailed forDetailedFailure(
        final List<VersionedDomainEvent> analyzedEvents,
        final List<String> attemptedSources,
        final String failureReason,
        final String diagnosticMessage,
        final Duration attemptDuration,
        final double maxConfidenceReached
    ) {
        final EventMetadata metadata = createMetadataForNewAggregate(
            "flow-detection", 
            "failed-" + System.currentTimeMillis()
        );
        
        return new FlowDetectionFailed(
            metadata,
            analyzedEvents,
            attemptedSources,
            failureReason,
            Optional.of(diagnosticMessage),
            attemptDuration,
            maxConfidenceReached,
            Instant.now()
        );
    }

    /**
     * Checks if the failure was due to insufficient data.
     * @return true if failure reason indicates insufficient data
     */
    public boolean isDataInsufficiencyFailure() {
        return failureReason.contains("INSUFFICIENT_DATA") || 
               failureReason.contains("NO_EVENTS");
    }

    /**
     * Checks if some confidence was achieved despite the failure.
     * @return true if max confidence reached is greater than 0.3
     */
    public boolean hadPartialSuccess() {
        return maxConfidenceReached > 0.3;
    }

    /**
     * Gets the failure severity based on confidence achieved and attempt duration.
     * @return severity level (LOW, MEDIUM, HIGH, CRITICAL)
     */
    public String getFailureSeverity() {
        if (hadPartialSuccess() && attemptDuration.toMillis() < 100) {
            return "LOW";
        } else if (hadPartialSuccess() || attemptDuration.toMillis() < 100) {
            return "MEDIUM";
        } else if (!hadPartialSuccess() && attemptDuration.toMillis() > 500) {
            return "CRITICAL";
        } else {
            return "HIGH";
        }
    }

    /**
     * Checks if this failure suggests a systematic issue with flow detection.
     * @return true if multiple sources were attempted and all failed
     */
    public boolean indicatesSystematicIssue() {
        return attemptedSources.size() >= 3 && maxConfidenceReached < 0.1;
    }
}