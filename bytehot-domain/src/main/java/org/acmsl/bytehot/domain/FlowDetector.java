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
 * Filename: FlowDetector.java
 *
 * Author: Claude (Anthropic AI)
 *
 * Class name: FlowDetector
 *
 * Responsibilities:
 *   - Analyze event sequences to discover business flows
 *   - Apply pattern matching to identify known flow patterns
 *   - Generate flow discovery events with confidence levels
 *
 * Collaborators:
 *   - FlowDetectionPort: Infrastructure interface for flow operations
 *   - Flow: Business flows discovered from event patterns
 *   - FlowAnalysisRequested: Events requesting flow analysis
 *   - FlowDiscovered: Events indicating successful flow discovery
 */
package org.acmsl.bytehot.domain;

import org.acmsl.bytehot.domain.events.FlowAnalysisRequested;
import org.acmsl.bytehot.domain.events.FlowDiscovered;
import org.acmsl.commons.patterns.eventsourcing.VersionedDomainEvent;
import org.acmsl.commons.patterns.DomainEvent;
import org.acmsl.commons.patterns.DomainResponseEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Domain aggregate responsible for analyzing event sequences and discovering business flows.
 * @author Claude (Anthropic AI)
 * @since 2025-06-19
 */
@RequiredArgsConstructor
public final class FlowDetector {

    /**
     * Analyzes a sequence of events to identify potential business flows.
     * @param event The flow analysis request containing events to analyze
     * @return Flow discovery events for any flows found, or empty list if none detected
     */
    @NonNull
    public static List<DomainResponseEvent<FlowAnalysisRequested>> analyzeEventSequence(
        @Nullable final FlowAnalysisRequested event
    ) {
        if (event == null || event.getEventsToAnalyze() == null || event.getEventsToAnalyze().isEmpty()) {
            return List.of();
        }

        List<DomainResponseEvent<FlowAnalysisRequested>> discoveredFlows = new ArrayList<>();
        
        // Get known flow patterns
        List<Flow> knownPatterns = getKnownFlowPatterns();
        
        // Analyze events against each known pattern
        for (Flow pattern : knownPatterns) {
            if (matchesPattern(event.getEventsToAnalyze(), pattern, event.getMinimumConfidence())) {
                FlowDiscovered discovered = FlowDiscovered.builder()
                    .originalEvent(event)
                    .discoveredFlow(pattern)
                    .triggeringEvents(event.getEventsToAnalyze())
                    .confidence(calculateConfidence(event.getEventsToAnalyze(), pattern))
                    .discoveredAt(Instant.now())
                    .build();
                    
                discoveredFlows.add(discovered);
            }
        }
        
        return discoveredFlows;
    }

    /**
     * Gets the list of known flow patterns for analysis.
     * @return List of predefined flow patterns
     */
    @NonNull
    private static List<Flow> getKnownFlowPatterns() {
        List<Flow> patterns = new ArrayList<>();
        
        // Hot-Swap Complete Flow
        patterns.add(createHotSwapCompleteFlow());
        
        // User Session Flow
        patterns.add(createUserSessionFlow());
        
        // Error Recovery Flow
        patterns.add(createErrorRecoveryFlow());
        
        return patterns;
    }

    /**
     * Creates the Hot-Swap Complete Flow pattern.
     * @return Flow pattern for complete hot-swap operations
     */
    @NonNull
    private static Flow createHotSwapCompleteFlow() {
        return Flow.builder()
            .flowId(FlowId.fromName("hot-swap-complete"))
            .name("Hot-Swap Complete Flow")
            .description("Complete hot-swap operation from file change to instance update")
            .eventSequence(List.of(
                org.acmsl.bytehot.domain.events.ClassFileChanged.class,
                org.acmsl.bytehot.domain.events.ClassMetadataExtracted.class,
                org.acmsl.bytehot.domain.events.BytecodeValidated.class,
                org.acmsl.bytehot.domain.events.HotSwapRequested.class,
                org.acmsl.bytehot.domain.events.ClassRedefinitionSucceeded.class,
                org.acmsl.bytehot.domain.events.InstancesUpdated.class
            ))
            .minimumEventCount(4)
            .maximumTimeWindow(java.time.Duration.ofSeconds(30))
            .confidence(0.95)
            .conditions(java.util.Optional.empty())
            .build();
    }

    /**
     * Creates the User Session Flow pattern.
     * @return Flow pattern for user authentication and session management
     */
    @NonNull
    private static Flow createUserSessionFlow() {
        return Flow.builder()
            .flowId(FlowId.fromName("user-session"))
            .name("User Session Flow")
            .description("User authentication and session management")
            .eventSequence(List.of(
                org.acmsl.bytehot.domain.events.FlowAnalysisRequested.class,
                org.acmsl.bytehot.domain.events.UserAuthenticated.class,
                org.acmsl.bytehot.domain.events.UserSessionStarted.class
            ))
            .minimumEventCount(2)
            .maximumTimeWindow(java.time.Duration.ofMinutes(5))
            .confidence(0.90)
            .conditions(java.util.Optional.empty())
            .build();
    }

    /**
     * Creates the Error Recovery Flow pattern.
     * @return Flow pattern for system error detection and recovery
     */
    @NonNull
    private static Flow createErrorRecoveryFlow() {
        return Flow.builder()
            .flowId(FlowId.fromName("error-recovery"))
            .name("Error Recovery Flow")
            .description("System error detection and recovery process")
            .eventSequence(List.of(
                org.acmsl.bytehot.domain.events.ClassRedefinitionFailed.class
            ))
            .minimumEventCount(1)
            .maximumTimeWindow(java.time.Duration.ofMinutes(2))
            .confidence(0.85)
            .conditions(java.util.Optional.empty())
            .build();
    }

    /**
     * Checks if a sequence of events matches a flow pattern.
     * @param events The events to check
     * @param pattern The flow pattern to match against
     * @param minimumConfidence The minimum confidence required
     * @return true if the events match the pattern with sufficient confidence
     */
    private static boolean matchesPattern(
        @NonNull final List<VersionedDomainEvent> events,
        @NonNull final Flow pattern,
        final double minimumConfidence
    ) {
        if (events.size() < pattern.getMinimumEventCount()) {
            return false;
        }

        // Extract event types from versioned domain events
        List<String> eventTypes = events.stream()
            .map(VersionedDomainEvent::getEventType)
            .collect(Collectors.toList());

        // Check if the pattern matches
        boolean matches = pattern.matchesByName(eventTypes);
        
        if (!matches) {
            return false;
        }

        // Check confidence level
        double confidence = calculateConfidence(events, pattern);
        
        return confidence >= minimumConfidence;
    }

    /**
     * Calculates the confidence level for a pattern match.
     * @param events The events that matched
     * @param pattern The pattern that was matched
     * @return Confidence level between 0.0 and 1.0
     */
    private static double calculateConfidence(
        @NonNull final List<VersionedDomainEvent> events,
        @NonNull final Flow pattern
    ) {
        // Start with the pattern's base confidence
        double confidence = pattern.getConfidence();
        
        // Adjust based on the completeness of the match
        double completeness = (double) events.size() / pattern.getEventSequence().size();
        confidence *= Math.min(1.0, completeness);
        
        // Check time window adherence
        if (events.size() > 1) {
            Instant firstEvent = events.get(0).getTimestamp();
            Instant lastEvent = events.get(events.size() - 1).getTimestamp();
            java.time.Duration actualDuration = java.time.Duration.between(firstEvent, lastEvent);
            
            if (actualDuration.compareTo(pattern.getMaximumTimeWindow()) > 0) {
                // Events took longer than expected, reduce confidence
                double timeRatio = (double) pattern.getMaximumTimeWindow().toMillis() / actualDuration.toMillis();
                confidence *= Math.max(0.5, timeRatio);
            }
        }
        
        // Apply pattern conditions if any
        if (pattern.getConditions().isPresent()) {
            if (!pattern.getConditions().get().evaluate(events)) {
                confidence *= 0.5; // Reduce confidence if conditions not met
            }
        }
        
        return Math.max(0.0, Math.min(1.0, confidence));
    }
}