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
 * Filename: EventSnapshotGenerator.java
 *
 * Author: Claude Code
 *
 * Class name: EventSnapshotGenerator
 *
 * Responsibilities:
 *   - Automatically generate event snapshots when errors occur
 *   - Configure snapshot depth and filtering criteria
 *   - Integrate with existing error handling infrastructure
 *   - Optimize performance to minimize overhead
 *
 * Collaborators:
 *   - EventStorePort: Access to complete event history
 *   - EventSnapshot: Generated snapshot with context
 *   - ErrorContext: Environmental context capture
 *   - CausalChainAnalyzer: Causal analysis of events
 */
package org.acmsl.bytehot.domain;

import org.acmsl.bytehot.domain.EventSnapshot;
import org.acmsl.bytehot.domain.ErrorContext;
import org.acmsl.bytehot.domain.CausalChain;
import org.acmsl.bytehot.domain.EventStorePort;
import org.acmsl.bytehot.domain.Ports;
import org.acmsl.commons.patterns.eventsourcing.VersionedDomainEvent;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import lombok.Builder;
import lombok.Getter;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Generates comprehensive event snapshots automatically when errors occur.
 * Provides configurable depth and filtering to optimize performance while
 * capturing relevant context for bug reproduction.
 * @author Claude Code
 * @since 2025-06-19
 */
public class EventSnapshotGenerator {

    /**
     * Configuration for snapshot generation
     */
    @Builder
    @Getter
    public static class SnapshotConfig {
        /**
         * Maximum number of events to include in snapshot
         */
        @Builder.Default
        private final int maxEvents = 100;

        /**
         * Maximum time window to look back for events
         */
        @Builder.Default
        private final Duration maxTimeWindow = Duration.ofMinutes(5);

        /**
         * Whether to include causal analysis
         */
        @Builder.Default
        private final boolean includeCausalAnalysis = true;

        /**
         * Whether to include performance metrics
         */
        @Builder.Default
        private final boolean includePerformanceMetrics = true;

        /**
         * Custom event filter (null means no filtering)
         */
        @Nullable
        private final Predicate<VersionedDomainEvent> eventFilter;

        /**
         * Minimum confidence level for causal analysis
         */
        @Builder.Default
        private final double minCausalConfidence = 0.3;
    }

    /**
     * Default configuration for snapshot generation
     */
    private static final SnapshotConfig DEFAULT_CONFIG = SnapshotConfig.builder().build();

    /**
     * Current configuration
     */
    @NonNull
    private final SnapshotConfig config;

    /**
     * Singleton instance
     */
    private static EventSnapshotGenerator instance;

    /**
     * Creates a new generator with the specified configuration
     * @param config snapshot generation configuration
     */
    public EventSnapshotGenerator(@NonNull final SnapshotConfig config) {
        this.config = config;
    }

    /**
     * Gets the singleton instance with default configuration
     * @return singleton instance
     */
    @NonNull
    public static EventSnapshotGenerator getInstance() {
        if (instance == null) {
            synchronized (EventSnapshotGenerator.class) {
                if (instance == null) {
                    instance = new EventSnapshotGenerator(DEFAULT_CONFIG);
                }
            }
        }
        return instance;
    }

    /**
     * Creates a new instance with custom configuration
     * @param config custom configuration
     * @return configured generator instance
     */
    @NonNull
    public static EventSnapshotGenerator withConfig(@NonNull final SnapshotConfig config) {
        return new EventSnapshotGenerator(config);
    }

    /**
     * Generates a complete event snapshot for the current error context
     * @return comprehensive event snapshot
     */
    @NonNull
    public EventSnapshot generateSnapshot() {
        return generateSnapshot(null);
    }

    /**
     * Generates an event snapshot with optional additional context
     * @param additionalContext extra context to include
     * @return comprehensive event snapshot
     */
    @NonNull
    public EventSnapshot generateSnapshot(@Nullable final Map<String, Object> additionalContext) {
        try {
            // Capture current error context
            ErrorContext errorContext = ErrorContext.capture();
            
            // Get event store
            EventStorePort eventStore = Ports.resolve(EventStorePort.class);
            
            // Calculate time window for event retrieval
            Instant cutoffTime = errorContext.getCapturedAt().minus(config.getMaxTimeWindow());
            
            // Retrieve relevant events
            List<VersionedDomainEvent> recentEvents = eventStore.getEventsBetween(cutoffTime, errorContext.getCapturedAt());
            
            // Apply filtering if configured
            List<VersionedDomainEvent> filteredEvents = filterEvents(recentEvents);
            
            // Limit to max events (keep most recent)
            List<VersionedDomainEvent> limitedEvents = limitEvents(filteredEvents);
            
            // Generate causal analysis if enabled
            CausalChain causalChain = null;
            if (config.isIncludeCausalAnalysis() && !limitedEvents.isEmpty()) {
                causalChain = analyzeCausalChain(limitedEvents);
            }
            
            // Capture performance metrics
            Map<String, Object> performanceMetrics = config.isIncludePerformanceMetrics() ?
                capturePerformanceMetrics() : Map.of();
            
            // Add additional context if provided
            Map<String, String> environmentContext = captureEnvironmentContext();
            if (additionalContext != null) {
                Map<String, String> extendedContext = new java.util.HashMap<>(environmentContext);
                additionalContext.forEach((key, value) -> 
                    extendedContext.put(key, String.valueOf(value))
                );
                environmentContext = extendedContext;
            }
            
            // Create the snapshot
            return EventSnapshot.create(
                limitedEvents,
                errorContext.getUserId(),
                environmentContext,
                errorContext.getThreadName(),
                errorContext.getSystemProperties(),
                causalChain,
                performanceMetrics
            );
            
        } catch (Exception e) {
            // Fallback: create minimal snapshot even if something goes wrong
            return createFallbackSnapshot(e);
        }
    }

    /**
     * Generates a snapshot specifically for an exception
     * @param exception the exception that occurred
     * @return event snapshot with exception context
     */
    @NonNull
    public EventSnapshot generateSnapshotForException(@NonNull final Throwable exception) {
        Map<String, Object> exceptionContext = Map.of(
            "exceptionType", exception.getClass().getName(),
            "exceptionMessage", exception.getMessage() != null ? exception.getMessage() : "",
            "stackTraceLength", exception.getStackTrace().length
        );
        
        return generateSnapshot(exceptionContext);
    }

    /**
     * Filters events based on configured criteria
     */
    @NonNull
    private List<VersionedDomainEvent> filterEvents(@NonNull final List<VersionedDomainEvent> events) {
        if (config.getEventFilter() == null) {
            return events;
        }
        
        return events.stream()
            .filter(config.getEventFilter())
            .toList();
    }

    /**
     * Limits events to the configured maximum, keeping the most recent
     */
    @NonNull
    private List<VersionedDomainEvent> limitEvents(@NonNull final List<VersionedDomainEvent> events) {
        if (events.size() <= config.getMaxEvents()) {
            return events;
        }
        
        // Keep the most recent events
        return events.subList(
            events.size() - config.getMaxEvents(),
            events.size()
        );
    }

    /**
     * Analyzes causal relationships in the event sequence
     */
    @Nullable
    private CausalChain analyzeCausalChain(@NonNull final List<VersionedDomainEvent> events) {
        try {
            // Simple causal analysis - can be enhanced with more sophisticated algorithms
            if (events.size() < 2) {
                return null;
            }
            
            // Look for patterns and timing relationships
            VersionedDomainEvent lastEvent = events.get(events.size() - 1);
            
            // Find potential root cause (simplified heuristic)
            VersionedDomainEvent potentialRootCause = findPotentialRootCause(events);
            
            if (potentialRootCause != null) {
                double confidence = calculateCausalConfidence(events, potentialRootCause);
                
                if (confidence >= config.getMinCausalConfidence()) {
                    return CausalChain.fromRootCause(potentialRootCause, confidence)
                        .addContributingFactor("Event sequence analysis");
                }
            }
            
            return CausalChain.empty();
            
        } catch (Exception e) {
            // If causal analysis fails, return empty chain
            return CausalChain.empty();
        }
    }

    /**
     * Finds potential root cause using simple heuristics
     */
    @Nullable
    private VersionedDomainEvent findPotentialRootCause(@NonNull final List<VersionedDomainEvent> events) {
        // Simple heuristic: look for error-related events or unusual patterns
        for (VersionedDomainEvent event : events) {
            String eventType = event.getEventType().toLowerCase();
            if (eventType.contains("error") || 
                eventType.contains("fail") || 
                eventType.contains("reject")) {
                return event;
            }
        }
        
        // If no obvious error events, return the first event as potential root cause
        return events.isEmpty() ? null : events.get(0);
    }

    /**
     * Calculates confidence in causal relationship
     */
    private double calculateCausalConfidence(
            @NonNull final List<VersionedDomainEvent> events,
            @NonNull final VersionedDomainEvent rootCause) {
        // Simplified confidence calculation based on timing and sequence
        long rootCauseIndex = events.indexOf(rootCause);
        if (rootCauseIndex == -1) {
            return 0.0;
        }
        
        // Higher confidence if root cause is earlier in sequence
        double positionFactor = 1.0 - (double) rootCauseIndex / events.size();
        
        // Higher confidence if there are clear temporal relationships
        double timingFactor = 0.7; // Default moderate confidence
        
        return Math.min(1.0, positionFactor * 0.6 + timingFactor * 0.4);
    }

    /**
     * Captures current performance metrics
     */
    @NonNull
    private Map<String, Object> capturePerformanceMetrics() {
        Runtime runtime = Runtime.getRuntime();
        Map<String, Object> metrics = new java.util.HashMap<>();
        
        metrics.put("freeMemory", runtime.freeMemory());
        metrics.put("totalMemory", runtime.totalMemory());
        metrics.put("maxMemory", runtime.maxMemory());
        metrics.put("availableProcessors", runtime.availableProcessors());
        metrics.put("currentTimeMillis", System.currentTimeMillis());
        metrics.put("nanoTime", System.nanoTime());
        
        return metrics;
    }

    /**
     * Captures environment context for the snapshot
     */
    @NonNull
    private Map<String, String> captureEnvironmentContext() {
        Map<String, String> context = new java.util.HashMap<>();
        
        context.put("timestamp", Instant.now().toString());
        context.put("javaVersion", System.getProperty("java.version", "unknown"));
        context.put("osName", System.getProperty("os.name", "unknown"));
        context.put("osVersion", System.getProperty("os.version", "unknown"));
        context.put("userTimezone", System.getProperty("user.timezone", "unknown"));
        
        return context;
    }

    /**
     * Creates a minimal fallback snapshot if generation fails
     */
    @NonNull
    private EventSnapshot createFallbackSnapshot(@NonNull final Exception generationError) {
        return EventSnapshot.create(
            List.of(), // Empty event history
            null, // No user context
            Map.of("fallback", "true", "error", generationError.getMessage()),
            Thread.currentThread().getName(),
            Map.of("java.version", System.getProperty("java.version", "unknown")),
            null, // No causal analysis
            Map.of("fallbackGeneration", true)
        );
    }

    /**
     * Gets the current configuration
     * @return snapshot generation configuration
     */
    @NonNull
    public SnapshotConfig getConfig() {
        return config;
    }
}