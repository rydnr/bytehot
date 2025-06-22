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
 * Filename: CausalChain.java
 *
 * Author: Claude Code
 *
 * Class name: CausalChain
 *
 * Responsibilities:
 *   - Represent causal relationships between events leading to an error
 *   - Analyze event sequences for cause-and-effect patterns
 *   - Provide insights for debugging and root cause analysis
 *   - Support visualization of event flow causality
 *
 * Collaborators:
 *   - VersionedDomainEvent: Events being analyzed for causality
 *   - Flow: Flow detection patterns for causal analysis
 */
package org.acmsl.bytehot.domain;

import org.acmsl.commons.patterns.eventsourcing.VersionedDomainEvent;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Represents the causal chain of events that led to an error condition.
 * Provides analysis of cause-and-effect relationships in event sequences.
 * @author Claude Code
 * @since 2025-06-19
 */
@RequiredArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
@Getter
public class CausalChain {

    /**
     * The primary event that appears to be the root cause
     */
    @Nullable
    private final VersionedDomainEvent rootCause;

    /**
     * Sequence of events showing the causal progression
     */
    @NonNull
    private final List<CausalLink> causalLinks;

    /**
     * Confidence level in the causal analysis (0.0 to 1.0)
     */
    private final double confidence;

    /**
     * Contributing factors that may have influenced the error
     */
    @NonNull
    private final List<String> contributingFactors;

    /**
     * Analysis metadata and additional insights
     */
    @NonNull
    private final Map<String, Object> analysisMetadata;

    /**
     * Represents a single causal link in the chain
     */
    @RequiredArgsConstructor
    @Builder
    @EqualsAndHashCode
    @ToString
    @Getter
    public static class CausalLink {
        /**
         * The cause event
         */
        @NonNull
        private final VersionedDomainEvent cause;

        /**
         * The effect event
         */
        @NonNull
        private final VersionedDomainEvent effect;

        /**
         * Time between cause and effect
         */
        @NonNull
        private final Duration timeDelta;

        /**
         * Confidence in this specific causal relationship
         */
        private final double linkConfidence;

        /**
         * Description of the causal relationship
         */
        @NonNull
        private final String relationship;
    }

    /**
     * Creates an empty causal chain (no causality detected)
     * @return empty causal chain with zero confidence
     */
    @NonNull
    public static CausalChain empty() {
        return CausalChain.builder()
            .rootCause(null)
            .causalLinks(List.of())
            .confidence(0.0)
            .contributingFactors(List.of())
            .analysisMetadata(Map.of())
            .build();
    }

    /**
     * Creates a simple causal chain from a single root cause
     * @param rootCause the primary cause event
     * @param confidence confidence in this analysis
     * @return causal chain with single root cause
     */
    @NonNull
    public static CausalChain fromRootCause(
            @NonNull final VersionedDomainEvent rootCause,
            final double confidence) {
        return CausalChain.builder()
            .rootCause(rootCause)
            .causalLinks(List.of())
            .confidence(confidence)
            .contributingFactors(List.of())
            .analysisMetadata(Map.of("analysisType", "simple"))
            .build();
    }

    /**
     * Gets the length of the causal chain
     * @return number of causal links
     */
    public int getChainLength() {
        return causalLinks.size();
    }

    /**
     * Checks if this chain has high confidence (> 0.8)
     * @return true if confidence is high
     */
    public boolean isHighConfidence() {
        return confidence > 0.8;
    }

    /**
     * Checks if this chain has a clear root cause
     * @return true if root cause is identified
     */
    public boolean hasRootCause() {
        return rootCause != null;
    }

    /**
     * Gets the total time span of the causal chain
     * @return duration from first cause to final effect
     */
    @NonNull
    public Duration getTotalDuration() {
        if (causalLinks.isEmpty()) {
            return Duration.ZERO;
        }
        
        return causalLinks.stream()
            .map(CausalLink::getTimeDelta)
            .reduce(Duration.ZERO, Duration::plus);
    }

    /**
     * Gets the weakest link in the causal chain
     * @return causal link with lowest confidence
     */
    @NonNull
    public Optional<CausalLink> getWeakestLink() {
        return causalLinks.stream()
            .min((a, b) -> Double.compare(a.getLinkConfidence(), b.getLinkConfidence()));
    }

    /**
     * Gets a human-readable description of the causal chain
     * @return descriptive summary of causality
     */
    @NonNull
    public String getDescription() {
        if (causalLinks.isEmpty()) {
            if (rootCause != null) {
                return "Root cause identified: " + rootCause.getEventType();
            }
            return "No clear causal pattern detected";
        }

        StringBuilder description = new StringBuilder();
        if (rootCause != null) {
            description.append("Root cause: ").append(rootCause.getEventType()).append(" → ");
        }

        for (int i = 0; i < causalLinks.size(); i++) {
            CausalLink link = causalLinks.get(i);
            if (i > 0) {
                description.append(" → ");
            }
            description.append(link.getEffect().getEventType());
        }

        description.append(" (confidence: ").append(String.format("%.1f%%", confidence * 100)).append(")");
        return description.toString();
    }

    /**
     * Gets events that are likely related to the error based on timing
     * @param maxTimeDelta maximum time difference to consider related
     * @return list of potentially related events
     */
    @NonNull
    public List<VersionedDomainEvent> getRelatedEvents(@NonNull final Duration maxTimeDelta) {
        return causalLinks.stream()
            .filter(link -> link.getTimeDelta().compareTo(maxTimeDelta) <= 0)
            .flatMap(link -> List.of(link.getCause(), link.getEffect()).stream())
            .distinct()
            .toList();
    }

    /**
     * Checks if the causal chain suggests a specific pattern
     * @param pattern the pattern name to check for
     * @return true if this pattern is present
     */
    public boolean hasPattern(@NonNull final String pattern) {
        return analysisMetadata.containsKey("patterns") &&
               analysisMetadata.get("patterns") instanceof List<?> patterns &&
               patterns.contains(pattern);
    }

    /**
     * Gets debugging suggestions based on the causal analysis
     * @return list of suggested debugging steps
     */
    @NonNull
    public List<String> getDebuggingSuggestions() {
        Object suggestions = analysisMetadata.get("debuggingSuggestions");
        if (suggestions instanceof List<?> list) {
            return list.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .toList();
        }
        return List.of();
    }

    /**
     * Adds a contributing factor to the analysis
     * @param factor description of the contributing factor
     * @return updated causal chain
     */
    @NonNull
    public CausalChain addContributingFactor(@NonNull final String factor) {
        List<String> updatedFactors = new java.util.ArrayList<>(contributingFactors);
        updatedFactors.add(factor);
        return this.toBuilder()
            .contributingFactors(updatedFactors)
            .build();
    }
}