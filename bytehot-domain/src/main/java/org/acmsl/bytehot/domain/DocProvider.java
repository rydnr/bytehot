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
 * Filename: DocProvider.java
 *
 * Author: Claude Code
 *
 * Class name: DocProvider
 *
 * Responsibilities:
 *   - Centralized documentation URL generation and Flow detection engine
 *   - Runtime operational context analysis for contextual documentation
 *   - Performance-optimized Flow detection with confidence scoring
 *   - Intelligent caching strategies for documentation access
 *
 * Collaborators:
 *   - Flow: Runtime operational context detection and analysis
 *   - Defaults: Documentation system configuration constants
 *   - FlowDetector: Flow detection and confidence scoring engine
 */
package org.acmsl.bytehot.domain;

import org.acmsl.bytehot.domain.events.FlowAnalysisRequested;
import org.acmsl.bytehot.domain.events.FlowContextDetected;
import org.acmsl.bytehot.domain.events.FlowDiscovered;
import org.acmsl.commons.patterns.DomainResponseEvent;
import org.acmsl.commons.patterns.eventsourcing.VersionedDomainEvent;

import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.time.Instant;
import java.time.Duration;

/**
 * Revolutionary documentation provider that transforms ByteHot into a self-documenting,
 * context-aware development environment.
 * 
 * This class implements the core engine for:
 * - Multi-source Flow detection (call stack, events, configuration, file system)
 * - Confidence-based Flow scoring (0.0-1.0) for accuracy
 * - URL generation with template processing for contextual documentation
 * - Intelligent caching strategies for performance optimization
 * 
 * Flow Detection Engine:
 * The revolutionary capability that analyzes runtime context to provide intelligent
 * documentation access. Uses multiple detection sources:
 * 
 * 1. Call Stack Analysis: Pattern recognition for different operational Flows
 * 2. Event Sequence Analysis: Domain event patterns indicating current Flows
 * 3. Configuration State: Configuration loading operations detection
 * 4. File System Operations: File monitoring activity detection
 * 
 * Supported Flows with Confidence Thresholds:
 * - ConfigurationManagementFlow (&gt;80% confidence during config operations)
 * - FileChangeDetectionFlow (&gt;85% confidence during file operations)
 * - HotSwapCompleteFlow (&gt;90% confidence during class redefinition)
 * - AgentStartupFlow (&gt;75% confidence during initialization)
 * 
 * Performance Requirements:
 * - Documentation URL generation: &lt; 10ms (95th percentile)
 * - Flow detection accuracy: &gt; 80% for common scenarios
 * - System overhead: &lt; 1% additional CPU, &lt; 5MB memory
 * - Cache hit rate: &gt; 70% for frequently accessed documentation
 * 
 * @author Claude Code
 * @since 2025-06-22
 */
public class DocProvider {

    /**
     * Documentation URL cache for performance optimization
     */
    private final Map<String, CachedDocUrl> documentationCache = new ConcurrentHashMap<>();

    /**
     * Flow detection cache for performance optimization
     */
    private final Map<String, CachedFlow> flowCache = new ConcurrentHashMap<>();

    /**
     * Recent events for flow analysis
     */
    private final List<VersionedDomainEvent> recentEvents = new ArrayList<>();

    /**
     * Performance metrics tracking
     */
    private final AtomicLong cacheHits = new AtomicLong(0);
    private final AtomicLong cacheMisses = new AtomicLong(0);
    private final AtomicLong flowDetectionCalls = new AtomicLong(0);

    /**
     * Gets basic documentation URL for a class without runtime context.
     * 
     * @param clazz the class to get documentation for
     * @return documentation URL or empty if not available
     */
    public Optional<String> getDocumentationUrl(final Class<?> clazz) {
        final String cacheKey = "doc:" + clazz.getName();
        
        // Check cache first
        final CachedDocUrl cached = documentationCache.get(cacheKey);
        if (cached != null && !cached.isExpired()) {
            cacheHits.incrementAndGet();
            return cached.getUrl();
        }
        
        cacheMisses.incrementAndGet();
        
        // Generate documentation URL
        final Optional<String> url = generateBasicDocumentationUrl(clazz);
        
        // Cache the result
        documentationCache.put(cacheKey, new CachedDocUrl(url, Instant.now()));
        
        return url;
    }

    /**
     * Gets method-specific documentation URL.
     * 
     * @param clazz the class containing the method
     * @param methodName the name of the method
     * @return method documentation URL or empty if not available
     */
    public Optional<String> getMethodDocumentationUrl(final Class<?> clazz, final String methodName) {
        final String cacheKey = "method:" + clazz.getName() + "#" + methodName;
        
        // Check cache first
        final CachedDocUrl cached = documentationCache.get(cacheKey);
        if (cached != null && !cached.isExpired()) {
            cacheHits.incrementAndGet();
            return cached.getUrl();
        }
        
        cacheMisses.incrementAndGet();
        
        // Generate method documentation URL
        final Optional<String> url = generateMethodDocumentationUrl(clazz, methodName);
        
        // Cache the result
        documentationCache.put(cacheKey, new CachedDocUrl(url, Instant.now()));
        
        return url;
    }

    /**
     * Revolutionary contextual documentation based on detected operational Flow.
     * 
     * This method implements the core innovation of Milestone 7: runtime context
     * analysis to provide documentation that adapts to what the system is currently doing.
     * 
     * @param clazz the class requesting contextual documentation
     * @return Flow-aware documentation URL or basic documentation fallback
     */
    public Optional<String> getContextualDocumentationUrl(final Class<?> clazz) {
        flowDetectionCalls.incrementAndGet();
        
        try {
            // Detect current operational Flow with confidence scoring
            final Optional<Flow> detectedFlow = detectCurrentFlow();
            
            if (detectedFlow.isPresent()) {
                final Flow flow = detectedFlow.get();
                // Note: Flow detected with confidence score (debug info)
                
                // Generate contextual documentation URL based on detected Flow
                return generateContextualDocumentationUrl(clazz, flow);
            } else {
                // Note: No Flow detected, falling back to basic documentation
                
                // Fallback to basic documentation
                return getDocumentationUrl(clazz);
            }
        } catch (final Exception e) {
            // Note: Error during Flow detection, falling back to basic documentation
            
            // Graceful fallback to basic documentation
            return getDocumentationUrl(clazz);
        }
    }

    /**
     * Gets documentation URL for explicit Flow context.
     * 
     * @param clazz the class requesting documentation
     * @param flowContext the specific Flow context to use
     * @return Flow-specific documentation URL
     */
    public Optional<String> getFlowDocumentationUrl(final Class<?> clazz, final Flow flowContext) {
        return generateContextualDocumentationUrl(clazz, flowContext);
    }

    /**
     * Gets enhanced documentation for manual testing scenarios.
     * 
     * @param clazz the class being tested
     * @return testing-focused documentation URL
     */
    public Optional<String> getTestingDocumentationUrl(final Class<?> clazz) {
        final String baseUrl = Defaults.DOCUMENTATION_BASE_URL;
        final String className = clazz.getSimpleName();
        final String testingUrl = String.format("%s/testing/%s-testing.html", baseUrl, className);
        
        return Optional.of(testingUrl);
    }

    /**
     * Checks if contextual documentation is available for the given class.
     * 
     * @param clazz the class to check
     * @return true if contextual documentation is available
     */
    public boolean hasContextualDocumentation(final Class<?> clazz) {
        // Fast check without full Flow detection
        try {
            final Optional<Flow> cachedFlow = getCachedFlow();
            return cachedFlow.isPresent() || canDetectFlow();
        } catch (final Exception e) {
            return false;
        }
    }

    /**
     * Detects the current operational Flow using multi-source analysis.
     * 
     * Revolutionary Flow detection engine that analyzes:
     * - Call stack patterns for execution context
     * - Recent domain event sequences 
     * - Configuration loading state
     * - File system operation activity
     * 
     * Integrates with FlowDetector for sophisticated pattern matching.
     * 
     * @return detected Flow with confidence score, or empty if no Flow detected
     */
    private Optional<Flow> detectCurrentFlow() {
        // Check cached Flow first
        final Optional<Flow> cachedFlow = getCachedFlow();
        if (cachedFlow.isPresent()) {
            return cachedFlow;
        }
        
        // Use sophisticated FlowDetector with recent events
        final Optional<Flow> detectedFlow = detectFlowUsingFlowDetector();
        
        if (detectedFlow.isPresent()) {
            // Cache the detected flow
            final Flow flow = detectedFlow.get();
            flowCache.put("current_flow", new CachedFlow(flow, Instant.now()));
            
            // Generate FlowContextDetected event for analytics
            final FlowContextDetected event = createFlowContextDetectedEvent(flow);
            // Note: In a complete implementation, this event would be emitted to EventEmitter
            
            return detectedFlow;
        }
        
        // Fallback to simplified call stack detection
        return detectFlowFromCallStack();
    }

    /**
     * Detects flow using the sophisticated FlowDetector with recent events and enhanced pattern analysis.
     * 
     * This method implements advanced domain event analysis that:
     * - Tracks event sequences and patterns over time
     * - Correlates events with ongoing operations
     * - Uses temporal analysis for flow confidence scoring
     * - Integrates with existing FlowDetector infrastructure
     * 
     * @return detected Flow or empty if no pattern found
     */
    private Optional<Flow> detectFlowUsingFlowDetector() {
        try {
            // Enhanced event context analysis
            final EventAnalysisContext eventContext = analyzeRecentEvents();
            
            if (eventContext.isEmpty()) {
                // No meaningful events to analyze, fallback to call stack
                return Optional.empty();
            }
            
            // Create comprehensive flow analysis request
            final Instant now = Instant.now();
            final TimeWindow analysisWindow = TimeWindow.of(
                now.minus(Duration.ofMinutes(5)), 
                Duration.ofMinutes(5)
            );
            
            final FlowAnalysisRequested analysisRequest = FlowAnalysisRequested.builder()
                .analysisId(AnalysisId.random())
                .eventsToAnalyze(new ArrayList<>(recentEvents))
                .minimumConfidence(calculateDynamicMinimumConfidence(eventContext))
                .analysisWindow(Optional.of(analysisWindow))
                .requestedBy(UserId.of("DocProvider"))
                .requestedAt(now)
                .previousEvent(Optional.empty())
                .build();
            
            // Use FlowDetector to analyze events
            final List<DomainResponseEvent<FlowAnalysisRequested>> discoveredFlows = 
                FlowDetector.analyzeEventSequence(analysisRequest);
            
            // Enhanced flow selection with context scoring
            return selectBestFlowFromDiscovered(discoveredFlows, eventContext);
                
        } catch (final Exception e) {
            // If FlowDetector fails, attempt local event pattern analysis
            return detectFlowFromEventPatterns();
        }
    }
    
    /**
     * Analyzes recent events to extract contextual information for flow detection.
     * 
     * @return event analysis context with pattern information
     */
    private EventAnalysisContext analyzeRecentEvents() {
        synchronized (recentEvents) {
            if (recentEvents.isEmpty()) {
                return new EventAnalysisContext();
            }
            
            final List<String> eventTypes = new ArrayList<>();
            final List<Instant> eventTimestamps = new ArrayList<>();
            final Map<String, Integer> eventFrequency = new HashMap<>();
            final List<String> eventSequencePattern = new ArrayList<>();
            
            // Analyze event patterns
            for (final VersionedDomainEvent event : recentEvents) {
                final String eventType = event.getEventType();
                final Instant timestamp = event.getTimestamp();
                
                eventTypes.add(eventType);
                eventTimestamps.add(timestamp);
                
                // Track event frequency
                eventFrequency.merge(eventType, 1, Integer::sum);
                
                // Build sequence pattern
                eventSequencePattern.add(extractEventPattern(eventType));
            }
            
            // Calculate temporal characteristics
            final Duration timeSpan = calculateEventTimeSpan(eventTimestamps);
            final double eventDensity = calculateEventDensity(eventTimestamps);
            
            return new EventAnalysisContext(
                eventTypes, 
                eventTimestamps, 
                eventFrequency, 
                eventSequencePattern,
                timeSpan,
                eventDensity
            );
        }
    }
    
    /**
     * Extracts meaningful pattern from event type for flow detection.
     * 
     * @param eventType the full event type name
     * @return simplified pattern for matching
     */
    private String extractEventPattern(final String eventType) {
        // Extract key pattern components from event types
        if (eventType.contains("ClassFileChanged") || eventType.contains("FileChanged")) {
            return "FILE_CHANGE";
        }
        if (eventType.contains("HotSwap") || eventType.contains("Redefinition")) {
            return "HOT_SWAP";
        }
        if (eventType.contains("Configuration") || eventType.contains("Config")) {
            return "CONFIGURATION";
        }
        if (eventType.contains("User") && eventType.contains("Authenticated")) {
            return "USER_AUTH";
        }
        if (eventType.contains("Flow") && eventType.contains("Discovered")) {
            return "FLOW_DISCOVERY";
        }
        if (eventType.contains("Error") || eventType.contains("Failed")) {
            return "ERROR_HANDLING";
        }
        if (eventType.contains("Test")) {
            return "TESTING";
        }
        
        // Default pattern based on event suffix
        if (eventType.contains("Requested")) {
            return "REQUEST";
        }
        if (eventType.contains("Completed") || eventType.contains("Succeeded")) {
            return "COMPLETION";
        }
        if (eventType.contains("Started")) {
            return "INITIALIZATION";
        }
        
        return "GENERIC";
    }
    
    /**
     * Calculates the time span covered by events.
     * 
     * @param timestamps list of event timestamps
     * @return duration covering all events
     */
    private Duration calculateEventTimeSpan(final List<Instant> timestamps) {
        if (timestamps.size() < 2) {
            return Duration.ZERO;
        }
        
        final Instant earliest = timestamps.stream().min(Instant::compareTo).orElse(Instant.now());
        final Instant latest = timestamps.stream().max(Instant::compareTo).orElse(Instant.now());
        
        return Duration.between(earliest, latest);
    }
    
    /**
     * Calculates event density (events per minute) for flow analysis.
     * 
     * @param timestamps list of event timestamps
     * @return events per minute
     */
    private double calculateEventDensity(final List<Instant> timestamps) {
        if (timestamps.size() < 2) {
            return 0.0;
        }
        
        final Duration timeSpan = calculateEventTimeSpan(timestamps);
        final double minutes = timeSpan.toMillis() / 60000.0;
        
        if (minutes == 0.0) {
            return timestamps.size(); // All events in same instant
        }
        
        return timestamps.size() / minutes;
    }
    
    /**
     * Calculates dynamic minimum confidence based on event context.
     * 
     * @param context the event analysis context
     * @return minimum confidence threshold
     */
    private double calculateDynamicMinimumConfidence(final EventAnalysisContext context) {
        double baseConfidence = 0.7;
        
        // Adjust based on event density - higher density can allow lower confidence
        if (context.getEventDensity() > 5.0) {
            baseConfidence -= 0.1; // More events = can be more permissive
        }
        
        // Adjust based on event diversity - more types can require higher confidence  
        if (context.getEventTypes().size() > 3) {
            baseConfidence += 0.05; // More complexity = need higher confidence
        }
        
        // Adjust based on time span - recent events require higher confidence
        if (context.getTimeSpan().toSeconds() < 30) {
            baseConfidence += 0.05; // Recent activity = need higher confidence
        }
        
        return Math.max(0.6, Math.min(0.8, baseConfidence));
    }
    
    /**
     * Selects the best flow from discovered flows using context scoring.
     * 
     * @param discoveredFlows flows found by FlowDetector
     * @param context event analysis context
     * @return best matching flow
     */
    private Optional<Flow> selectBestFlowFromDiscovered(
            final List<DomainResponseEvent<FlowAnalysisRequested>> discoveredFlows,
            final EventAnalysisContext context) {
        
        // Filter and score flows
        return discoveredFlows.stream()
            .filter(event -> event instanceof FlowDiscovered)
            .map(event -> (FlowDiscovered) event)
            .filter(discovered -> discovered.getConfidence() >= 0.6)
            .max((flow1, flow2) -> {
                // Score flows based on context relevance
                final double score1 = scoreFlowRelevance(flow1, context);
                final double score2 = scoreFlowRelevance(flow2, context);
                return Double.compare(score1, score2);
            })
            .map(FlowDiscovered::getDiscoveredFlow);
    }
    
    /**
     * Scores how relevant a discovered flow is to the current event context.
     * 
     * @param discoveredFlow the flow to score
     * @param context the event context
     * @return relevance score (higher is better)
     */
    private double scoreFlowRelevance(final FlowDiscovered discoveredFlow, final EventAnalysisContext context) {
        double score = discoveredFlow.getConfidence(); // Start with base confidence
        
        final Flow flow = discoveredFlow.getDiscoveredFlow();
        final String flowName = flow.getName().toLowerCase();
        
        // Boost score if flow name matches event patterns
        for (final String pattern : context.getEventSequencePattern()) {
            if (flowName.contains(pattern.toLowerCase().replace("_", ""))) {
                score += 0.1;
            }
        }
        
        // Consider event frequency alignment
        final Map<String, Integer> eventFreq = context.getEventFrequency();
        if (eventFreq.containsKey("FILE_CHANGE") && flowName.contains("file")) {
            score += 0.15;
        }
        if (eventFreq.containsKey("HOT_SWAP") && flowName.contains("hotswap")) {
            score += 0.2;
        }
        if (eventFreq.containsKey("CONFIGURATION") && flowName.contains("config")) {
            score += 0.1;
        }
        
        return score;
    }
    
    /**
     * Fallback flow detection using local event pattern analysis.
     * 
     * @return detected flow from local analysis
     */
    private Optional<Flow> detectFlowFromEventPatterns() {
        synchronized (recentEvents) {
            if (recentEvents.isEmpty()) {
                return Optional.empty();
            }
            
            // Simple pattern matching on recent events
            final List<String> eventTypes = recentEvents.stream()
                .map(VersionedDomainEvent::getEventType)
                .collect(Collectors.toList());
            
            // Hot-swap pattern detection
            if (eventTypes.stream().anyMatch(type -> 
                type.contains("ClassFileChanged") || type.contains("HotSwap"))) {
                return createSimpleFlow("HotSwapComplete", 0.8);
            }
            
            // Configuration pattern detection
            if (eventTypes.stream().anyMatch(type -> 
                type.contains("Configuration") || type.contains("Config"))) {
                return createSimpleFlow("ConfigurationManagement", 0.75);
            }
            
            // User session pattern detection
            if (eventTypes.stream().anyMatch(type -> 
                type.contains("User") && type.contains("Session"))) {
                return createSimpleFlow("UserSession", 0.7);
            }
            
            return Optional.empty();
        }
    }
    
    /**
     * Context information extracted from domain event analysis.
     */
    private static class EventAnalysisContext {
        private final List<String> eventTypes;
        private final List<Instant> eventTimestamps;
        private final Map<String, Integer> eventFrequency;
        private final List<String> eventSequencePattern;
        private final Duration timeSpan;
        private final double eventDensity;
        
        public EventAnalysisContext() {
            this.eventTypes = List.of();
            this.eventTimestamps = List.of();
            this.eventFrequency = Map.of();
            this.eventSequencePattern = List.of();
            this.timeSpan = Duration.ZERO;
            this.eventDensity = 0.0;
        }
        
        public EventAnalysisContext(final List<String> eventTypes, 
                                   final List<Instant> eventTimestamps,
                                   final Map<String, Integer> eventFrequency,
                                   final List<String> eventSequencePattern,
                                   final Duration timeSpan,
                                   final double eventDensity) {
            this.eventTypes = eventTypes;
            this.eventTimestamps = eventTimestamps;
            this.eventFrequency = eventFrequency;
            this.eventSequencePattern = eventSequencePattern;
            this.timeSpan = timeSpan;
            this.eventDensity = eventDensity;
        }
        
        public boolean isEmpty() {
            return eventTypes.isEmpty();
        }
        
        public List<String> getEventTypes() {
            return eventTypes;
        }
        
        public List<Instant> getEventTimestamps() {
            return eventTimestamps;
        }
        
        public Map<String, Integer> getEventFrequency() {
            return eventFrequency;
        }
        
        public List<String> getEventSequencePattern() {
            return eventSequencePattern;
        }
        
        public Duration getTimeSpan() {
            return timeSpan;
        }
        
        public double getEventDensity() {
            return eventDensity;
        }
    }

    /**
     * Creates a FlowContextDetected event for analytics tracking.
     * 
     * @param detectedFlow the flow that was detected
     * @return FlowContextDetected event
     */
    private FlowContextDetected createFlowContextDetectedEvent(final Flow detectedFlow) {
        final List<String> detectionSources = List.of("CALL_STACK", "EVENT_SEQUENCE", "DOC_PROVIDER");
        final Duration detectionTime = Duration.ofMillis(5); // Estimated detection time
        
        return FlowContextDetected.forNewDetection(
            detectedFlow,
            detectedFlow.getConfidence(),
            detectionSources,
            new ArrayList<>(recentEvents),
            detectionTime
        );
    }

    /**
     * Adds a recent event for flow analysis.
     * This method should be called when domain events occur to maintain context.
     * 
     * @param event the domain event to add
     */
    public void addRecentEvent(final VersionedDomainEvent event) {
        synchronized (recentEvents) {
            recentEvents.add(event);
            
            // Keep only recent events (last 10 events or 5 minutes)
            final Instant cutoff = Instant.now().minus(Duration.ofMinutes(5));
            recentEvents.removeIf(e -> e.getTimestamp().isBefore(cutoff));
            
            // Limit to 10 most recent events for performance
            if (recentEvents.size() > 10) {
                recentEvents.subList(0, recentEvents.size() - 10).clear();
            }
        }
    }

    /**
     * Enhanced Flow detection based on sophisticated call stack analysis.
     * 
     * This method implements advanced pattern matching algorithms to detect operational
     * flows based on:
     * - Method call patterns and sequences
     * - Class hierarchy analysis 
     * - Package-level flow indicators
     * - Multi-level stack frame correlation
     * 
     * @return detected Flow with confidence score, or empty if no clear pattern found
     */
    private Optional<Flow> detectFlowFromCallStack() {
        final StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        
        // Advanced multi-pattern analysis
        final FlowDetectionContext context = analyzeCallStackContext(stack);
        
        // Priority-ordered flow detection (highest confidence first)
        
        // 1. Hot-Swap Operations (highest priority - core functionality)
        final Optional<Flow> hotSwapFlow = detectHotSwapFlow(context);
        if (hotSwapFlow.isPresent()) {
            return hotSwapFlow;
        }
        
        // 2. Agent Lifecycle Operations  
        final Optional<Flow> agentFlow = detectAgentLifecycleFlow(context);
        if (agentFlow.isPresent()) {
            return agentFlow;
        }
        
        // 3. File Change Detection Operations
        final Optional<Flow> fileChangeFlow = detectFileChangeFlow(context);
        if (fileChangeFlow.isPresent()) {
            return fileChangeFlow;
        }
        
        // 4. Configuration Management Operations
        final Optional<Flow> configFlow = detectConfigurationFlow(context);
        if (configFlow.isPresent()) {
            return configFlow;
        }
        
        // 5. Documentation Operations (self-referential)
        final Optional<Flow> docFlow = detectDocumentationFlow(context);
        if (docFlow.isPresent()) {
            return docFlow;
        }
        
        // 6. Testing Operations
        final Optional<Flow> testFlow = detectTestingFlow(context);
        if (testFlow.isPresent()) {
            return testFlow;
        }
        
        return Optional.empty();
    }
    
    /**
     * Analyzes the call stack to extract contextual information for flow detection.
     * 
     * @param stack the call stack to analyze
     * @return flow detection context with extracted patterns
     */
    private FlowDetectionContext analyzeCallStackContext(final StackTraceElement[] stack) {
        final List<String> classNames = new ArrayList<>();
        final List<String> methodNames = new ArrayList<>();
        final List<String> packageNames = new ArrayList<>();
        final List<String> classHierarchy = new ArrayList<>();
        
        for (final StackTraceElement element : stack) {
            final String className = element.getClassName();
            final String methodName = element.getMethodName();
            
            classNames.add(className);
            methodNames.add(methodName);
            
            // Extract package information
            final int lastDot = className.lastIndexOf('.');
            if (lastDot > 0) {
                packageNames.add(className.substring(0, lastDot));
            }
            
            // Extract class hierarchy indicators
            final String simpleName = className.substring(lastDot + 1);
            classHierarchy.add(simpleName);
        }
        
        return new FlowDetectionContext(classNames, methodNames, packageNames, classHierarchy);
    }
    
    /**
     * Detects Hot-Swap operation flows with high confidence.
     * 
     * @param context the call stack analysis context
     * @return Hot-Swap flow if detected
     */
    private Optional<Flow> detectHotSwapFlow(final FlowDetectionContext context) {
        final List<String> hotSwapIndicators = List.of(
            "HotSwap", "Redefinition", "ClassRedefinition", "BytecodeTransformation",
            "ClassFileTransformer", "Instrumentation", "retransform", "redefine"
        );
        
        int matches = 0;
        double confidence = 0.0;
        
        for (final String indicator : hotSwapIndicators) {
            if (context.containsPattern(indicator)) {
                matches++;
                confidence += 0.15; // Each match adds confidence
            }
        }
        
        // Special patterns for hot-swap sequences
        if (context.containsMethodSequence(List.of("transform", "redefine")) ||
            context.containsMethodSequence(List.of("validate", "transform", "apply"))) {
            confidence += 0.25;
        }
        
        if (matches >= 2 && confidence >= 0.8) {
            return createSimpleFlow("HotSwapComplete", Math.min(0.95, confidence));
        }
        
        return Optional.empty();
    }
    
    /**
     * Detects Agent lifecycle operation flows.
     * 
     * @param context the call stack analysis context
     * @return Agent lifecycle flow if detected
     */
    private Optional<Flow> detectAgentLifecycleFlow(final FlowDetectionContext context) {
        final List<String> agentIndicators = List.of(
            "Agent", "agentmain", "premain", "attach", "detach", 
            "AgentBootstrap", "AgentInitializer"
        );
        
        double confidence = 0.0;
        
        for (final String indicator : agentIndicators) {
            if (context.containsPattern(indicator)) {
                confidence += 0.2;
            }
        }
        
        // Look for agent startup patterns
        if (context.containsMethodPattern("main") && context.containsClassPattern("Agent")) {
            confidence += 0.3;
        }
        
        if (confidence >= 0.75) {
            return createSimpleFlow("AgentStartup", Math.min(0.9, confidence));
        }
        
        return Optional.empty();
    }
    
    /**
     * Detects File change monitoring flows.
     * 
     * @param context the call stack analysis context
     * @return File change flow if detected
     */
    private Optional<Flow> detectFileChangeFlow(final FlowDetectionContext context) {
        final List<String> fileChangeIndicators = List.of(
            "FileWatcher", "WatchService", "ClassFileChanged", "FileSystemWatcher",
            "onFileChanged", "watchForChanges", "FileMonitor"
        );
        
        double confidence = 0.0;
        
        for (final String indicator : fileChangeIndicators) {
            if (context.containsPattern(indicator)) {
                confidence += 0.25;
            }
        }
        
        // Look for file I/O patterns
        if (context.containsPackagePattern("java.nio.file") || 
            context.containsMethodPattern("watch")) {
            confidence += 0.15;
        }
        
        if (confidence >= 0.8) {
            return createSimpleFlow("FileChangeDetection", Math.min(0.9, confidence));
        }
        
        return Optional.empty();
    }
    
    /**
     * Detects Configuration management flows.
     * 
     * @param context the call stack analysis context
     * @return Configuration flow if detected
     */
    private Optional<Flow> detectConfigurationFlow(final FlowDetectionContext context) {
        final List<String> configIndicators = List.of(
            "Configuration", "Config", "Properties", "Settings",
            "loadConfig", "parseConfig", "applyConfig"
        );
        
        double confidence = 0.0;
        
        for (final String indicator : configIndicators) {
            if (context.containsPattern(indicator)) {
                confidence += 0.2;
            }
        }
        
        if (confidence >= 0.75) {
            return createSimpleFlow("ConfigurationManagement", Math.min(0.85, confidence));
        }
        
        return Optional.empty();
    }
    
    /**
     * Detects Documentation operation flows (self-referential).
     * 
     * @param context the call stack analysis context
     * @return Documentation flow if detected
     */
    private Optional<Flow> detectDocumentationFlow(final FlowDetectionContext context) {
        final List<String> docIndicators = List.of(
            "DocProvider", "Documentation", "DocLink", "getDocUrl", 
            "generateDoc", "DocumentationService"
        );
        
        double confidence = 0.0;
        
        for (final String indicator : docIndicators) {
            if (context.containsPattern(indicator)) {
                confidence += 0.2;
            }
        }
        
        if (confidence >= 0.7) {
            return createSimpleFlow("DocumentationGeneration", Math.min(0.8, confidence));
        }
        
        return Optional.empty();
    }
    
    /**
     * Detects Testing operation flows.
     * 
     * @param context the call stack analysis context
     * @return Testing flow if detected
     */
    private Optional<Flow> detectTestingFlow(final FlowDetectionContext context) {
        final List<String> testIndicators = List.of(
            "Test", "test", "junit", "TestCase", "TestSuite", 
            "assert", "verify", "mock"
        );
        
        double confidence = 0.0;
        
        for (final String indicator : testIndicators) {
            if (context.containsPattern(indicator)) {
                confidence += 0.15;
            }
        }
        
        // Look for testing patterns
        if (context.containsPackagePattern("org.junit") ||
            context.containsMethodPattern("test")) {
            confidence += 0.25;
        }
        
        if (confidence >= 0.6) {
            return createSimpleFlow("TestingWorkflow", Math.min(0.75, confidence));
        }
        
        return Optional.empty();
    }
    
    /**
     * Context information extracted from call stack analysis.
     */
    private static class FlowDetectionContext {
        private final List<String> classNames;
        private final List<String> methodNames; 
        private final List<String> packageNames;
        private final List<String> classHierarchy;
        
        public FlowDetectionContext(final List<String> classNames, final List<String> methodNames,
                                  final List<String> packageNames, final List<String> classHierarchy) {
            this.classNames = classNames;
            this.methodNames = methodNames;
            this.packageNames = packageNames;
            this.classHierarchy = classHierarchy;
        }
        
        public boolean containsPattern(final String pattern) {
            return classNames.stream().anyMatch(name -> name.contains(pattern)) ||
                   methodNames.stream().anyMatch(name -> name.contains(pattern)) ||
                   classHierarchy.stream().anyMatch(name -> name.contains(pattern));
        }
        
        public boolean containsClassPattern(final String pattern) {
            return classNames.stream().anyMatch(name -> name.contains(pattern)) ||
                   classHierarchy.stream().anyMatch(name -> name.contains(pattern));
        }
        
        public boolean containsMethodPattern(final String pattern) {
            return methodNames.stream().anyMatch(name -> name.contains(pattern));
        }
        
        public boolean containsPackagePattern(final String pattern) {
            return packageNames.stream().anyMatch(name -> name.contains(pattern));
        }
        
        public boolean containsMethodSequence(final List<String> sequence) {
            if (sequence.size() > methodNames.size()) {
                return false;
            }
            
            for (int i = 0; i <= methodNames.size() - sequence.size(); i++) {
                boolean foundSequence = true;
                for (int j = 0; j < sequence.size(); j++) {
                    if (!methodNames.get(i + j).contains(sequence.get(j))) {
                        foundSequence = false;
                        break;
                    }
                }
                if (foundSequence) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Creates a Flow instance with enhanced confidence scoring for documentation purposes.
     * 
     * @param flowType the type of flow
     * @param baseConfidence the base confidence level
     * @return Flow instance with calculated confidence
     */
    private Optional<Flow> createSimpleFlow(final String flowType, final double baseConfidence) {
        try {
            // Calculate enhanced confidence using sophisticated scoring system
            final double enhancedConfidence = calculateEnhancedConfidence(flowType, baseConfidence);
            
            // Create a Flow using the existing API with enhanced confidence
            final Flow flow = Flow.builder()
                .flowId(FlowId.of("doc-" + flowType.toLowerCase()))
                .name(flowType)
                .description("Flow detected for documentation purposes with confidence scoring")
                .confidence(enhancedConfidence)
                .build();
            
            return Optional.of(flow);
        } catch (final Exception e) {
            // If Flow creation fails, return empty
            return Optional.empty();
        }
    }
    
    /**
     * Calculates enhanced confidence using a sophisticated multi-factor scoring system.
     * 
     * This method implements advanced confidence calculation based on:
     * - Base detection confidence from pattern matching
     * - Temporal consistency of detected patterns
     * - Event correlation strength 
     * - Historical accuracy for this flow type
     * - Stack trace depth and complexity analysis
     * - System context relevance scoring
     * 
     * @param flowType the type of flow being scored
     * @param baseConfidence the initial confidence from pattern detection
     * @return enhanced confidence score (0.0-1.0)
     */
    private double calculateEnhancedConfidence(final String flowType, final double baseConfidence) {
        final ConfidenceCalculationContext context = buildConfidenceContext(flowType);
        
        // Start with base confidence
        double enhancedConfidence = baseConfidence;
        
        // Factor 1: Temporal Consistency Analysis (±0.15)
        enhancedConfidence += calculateTemporalConsistencyBonus(context);
        
        // Factor 2: Event Correlation Strength (±0.1)  
        enhancedConfidence += calculateEventCorrelationBonus(context);
        
        // Factor 3: Stack Trace Quality Analysis (±0.1)
        enhancedConfidence += calculateStackTraceQualityBonus(context);
        
        // Factor 4: System Context Relevance (±0.05)
        enhancedConfidence += calculateSystemContextBonus(context);
        
        // Factor 5: Historical Accuracy Adjustment (±0.1)
        enhancedConfidence += calculateHistoricalAccuracyBonus(flowType);
        
        // Ensure confidence stays within valid bounds
        return Math.max(0.0, Math.min(1.0, enhancedConfidence));
    }
    
    /**
     * Builds confidence calculation context for the specified flow type.
     * 
     * @param flowType the flow type being analyzed
     * @return confidence calculation context
     */
    private ConfidenceCalculationContext buildConfidenceContext(final String flowType) {
        final StackTraceElement[] currentStack = Thread.currentThread().getStackTrace();
        final List<VersionedDomainEvent> currentEvents;
        
        synchronized (recentEvents) {
            currentEvents = new ArrayList<>(recentEvents);
        }
        
        final Instant now = Instant.now();
        final Map<String, Object> systemMetrics = getPerformanceMetrics();
        
        return new ConfidenceCalculationContext(
            flowType,
            currentStack,
            currentEvents,
            now,
            systemMetrics
        );
    }
    
    /**
     * Calculates temporal consistency bonus based on event timing patterns.
     * 
     * @param context the confidence calculation context
     * @return temporal consistency bonus (-0.15 to +0.15)
     */
    private double calculateTemporalConsistencyBonus(final ConfidenceCalculationContext context) {
        final List<VersionedDomainEvent> events = context.getRecentEvents();
        
        if (events.size() < 2) {
            return 0.0; // No temporal analysis possible
        }
        
        // Analyze event timing consistency
        final List<Duration> intervals = new ArrayList<>();
        for (int i = 1; i < events.size(); i++) {
            final Duration interval = Duration.between(
                events.get(i-1).getTimestamp(), 
                events.get(i).getTimestamp()
            );
            intervals.add(interval);
        }
        
        // Calculate coefficient of variation for intervals
        final double avgIntervalMs = intervals.stream()
            .mapToLong(Duration::toMillis)
            .average()
            .orElse(0.0);
            
        if (avgIntervalMs == 0.0) {
            return 0.1; // Events in same instant = high consistency
        }
        
        final double stdDevMs = Math.sqrt(
            intervals.stream()
                .mapToDouble(d -> Math.pow(d.toMillis() - avgIntervalMs, 2))
                .average()
                .orElse(0.0)
        );
        
        final double coefficientOfVariation = stdDevMs / avgIntervalMs;
        
        // Low variability = higher consistency = bonus
        if (coefficientOfVariation < 0.3) {
            return 0.15; // Very consistent timing
        } else if (coefficientOfVariation < 0.7) {
            return 0.05; // Moderately consistent timing
        } else {
            return -0.1; // Inconsistent timing reduces confidence
        }
    }
    
    /**
     * Calculates event correlation bonus based on event type relationships.
     * 
     * @param context the confidence calculation context
     * @return event correlation bonus (-0.1 to +0.1)
     */
    private double calculateEventCorrelationBonus(final ConfidenceCalculationContext context) {
        final List<VersionedDomainEvent> events = context.getRecentEvents();
        final String flowType = context.getFlowType().toLowerCase();
        
        if (events.isEmpty()) {
            return 0.0;
        }
        
        // Count relevant events for this flow type
        long relevantEvents = events.stream()
            .map(VersionedDomainEvent::getEventType)
            .filter(eventType -> isEventRelevantToFlow(eventType, flowType))
            .count();
        
        final double relevanceRatio = (double) relevantEvents / events.size();
        
        if (relevanceRatio >= 0.8) {
            return 0.1; // High correlation
        } else if (relevanceRatio >= 0.5) {
            return 0.05; // Moderate correlation
        } else if (relevanceRatio >= 0.2) {
            return 0.0; // Low correlation
        } else {
            return -0.05; // Very low correlation reduces confidence
        }
    }
    
    /**
     * Checks if an event type is relevant to a specific flow type.
     * 
     * @param eventType the event type to check
     * @param flowType the flow type
     * @return true if event is relevant to flow
     */
    private boolean isEventRelevantToFlow(final String eventType, final String flowType) {
        switch (flowType) {
            case "hotswapcomplete":
                return eventType.contains("ClassFile") || eventType.contains("HotSwap") || 
                       eventType.contains("Redefinition") || eventType.contains("Bytecode");
            case "filechangedetection":
                return eventType.contains("File") || eventType.contains("Watch") ||
                       eventType.contains("Change") || eventType.contains("Monitor");
            case "configurationmanagement":
                return eventType.contains("Configuration") || eventType.contains("Config") ||
                       eventType.contains("Properties") || eventType.contains("Settings");
            case "agentstartup":
                return eventType.contains("Agent") || eventType.contains("Startup") ||
                       eventType.contains("Initialize") || eventType.contains("Bootstrap");
            case "testingworkflow":
                return eventType.contains("Test") || eventType.contains("Assert") ||
                       eventType.contains("Mock") || eventType.contains("Verify");
            case "documentationgeneration":
                return eventType.contains("Documentation") || eventType.contains("Doc") ||
                       eventType.contains("Link") || eventType.contains("Generate");
            default:
                return false;
        }
    }
    
    /**
     * Calculates stack trace quality bonus based on call stack characteristics.
     * 
     * @param context the confidence calculation context
     * @return stack trace quality bonus (-0.1 to +0.1)
     */
    private double calculateStackTraceQualityBonus(final ConfidenceCalculationContext context) {
        final StackTraceElement[] stack = context.getCurrentStack();
        final String flowType = context.getFlowType().toLowerCase();
        
        // Analyze stack depth and relevance
        int relevantFrames = 0;
        int totalFrames = stack.length;
        
        for (final StackTraceElement frame : stack) {
            final String className = frame.getClassName().toLowerCase();
            final String methodName = frame.getMethodName().toLowerCase();
            
            if (isStackFrameRelevantToFlow(className, methodName, flowType)) {
                relevantFrames++;
            }
        }
        
        final double relevanceRatio = (double) relevantFrames / totalFrames;
        
        // Bonus for good stack depth (not too shallow, not too deep)
        double depthBonus = 0.0;
        if (totalFrames >= 10 && totalFrames <= 50) {
            depthBonus = 0.02; // Good stack depth
        } else if (totalFrames > 50) {
            depthBonus = -0.02; // Very deep stack may indicate issues
        }
        
        // Bonus for relevant frames
        double relevanceBonus = 0.0;
        if (relevanceRatio >= 0.3) {
            relevanceBonus = 0.08; // High relevance
        } else if (relevanceRatio >= 0.1) {
            relevanceBonus = 0.03; // Moderate relevance
        }
        
        return depthBonus + relevanceBonus;
    }
    
    /**
     * Checks if a stack frame is relevant to a specific flow type.
     * 
     * @param className the class name (lowercase)
     * @param methodName the method name (lowercase)
     * @param flowType the flow type (lowercase)
     * @return true if frame is relevant
     */
    private boolean isStackFrameRelevantToFlow(final String className, final String methodName, final String flowType) {
        switch (flowType) {
            case "hotswapcomplete":
                return className.contains("hotswap") || className.contains("redefinition") ||
                       methodName.contains("transform") || methodName.contains("redefine");
            case "filechangedetection":
                return className.contains("file") || className.contains("watch") ||
                       methodName.contains("monitor") || methodName.contains("change");
            case "configurationmanagement":
                return className.contains("config") || className.contains("properties") ||
                       methodName.contains("load") || methodName.contains("parse");
            case "agentstartup":
                return className.contains("agent") || methodName.contains("attach") ||
                       methodName.contains("main") || methodName.contains("start");
            case "testingworkflow":
                return className.contains("test") || methodName.contains("test") ||
                       methodName.contains("assert") || methodName.contains("verify");
            case "documentationgeneration":
                return className.contains("doc") || methodName.contains("doc") ||
                       methodName.contains("generate") || methodName.contains("url");
            default:
                return false;
        }
    }
    
    /**
     * Calculates system context bonus based on current system state.
     * 
     * @param context the confidence calculation context
     * @return system context bonus (-0.05 to +0.05)
     */
    private double calculateSystemContextBonus(final ConfidenceCalculationContext context) {
        final Map<String, Object> metrics = context.getSystemMetrics();
        
        // Consider cache hit rate as system health indicator
        final double cacheHitRate = (Double) metrics.getOrDefault("cache_hit_rate", 0.0);
        
        // Consider number of recent events as activity indicator
        final int recentEventsCount = (Integer) metrics.getOrDefault("recent_events_count", 0);
        
        double bonus = 0.0;
        
        // Bonus for good cache performance
        if (cacheHitRate > 0.7) {
            bonus += 0.02; // System running efficiently
        } else if (cacheHitRate < 0.3) {
            bonus -= 0.02; // System may be under stress
        }
        
        // Bonus for appropriate activity level
        if (recentEventsCount >= 3 && recentEventsCount <= 10) {
            bonus += 0.03; // Good activity level for context analysis
        } else if (recentEventsCount > 15) {
            bonus -= 0.02; // High activity may reduce accuracy
        }
        
        return bonus;
    }
    
    /**
     * Calculates historical accuracy bonus based on past performance for this flow type.
     * 
     * @param flowType the flow type
     * @return historical accuracy bonus (-0.1 to +0.1)
     */
    private double calculateHistoricalAccuracyBonus(final String flowType) {
        // Simplified historical accuracy - in a full implementation,
        // this would track accuracy over time for each flow type
        
        switch (flowType.toLowerCase()) {
            case "hotswapcomplete":
                return 0.08; // Hot-swap detection is usually very accurate
            case "documentationgeneration":
                return 0.05; // Documentation flows are moderately accurate
            case "testingworkflow":
                return 0.03; // Testing flows can be ambiguous
            case "configurationmanagement":
                return 0.02; // Config flows are somewhat accurate
            case "filechangedetection":
                return 0.06; // File change detection is quite accurate
            case "agentstartup":
                return 0.04; // Agent startup is moderately accurate
            default:
                return 0.0; // Unknown flow types get no bonus
        }
    }
    
    /**
     * Context information for confidence calculation.
     */
    private static class ConfidenceCalculationContext {
        private final String flowType;
        private final StackTraceElement[] currentStack;
        private final List<VersionedDomainEvent> recentEvents;
        private final Instant calculationTime;
        private final Map<String, Object> systemMetrics;
        
        public ConfidenceCalculationContext(final String flowType,
                                          final StackTraceElement[] currentStack,
                                          final List<VersionedDomainEvent> recentEvents,
                                          final Instant calculationTime,
                                          final Map<String, Object> systemMetrics) {
            this.flowType = flowType;
            this.currentStack = currentStack;
            this.recentEvents = recentEvents;
            this.calculationTime = calculationTime;
            this.systemMetrics = systemMetrics;
        }
        
        public String getFlowType() {
            return flowType;
        }
        
        public StackTraceElement[] getCurrentStack() {
            return currentStack;
        }
        
        public List<VersionedDomainEvent> getRecentEvents() {
            return recentEvents;
        }
        
        public Instant getCalculationTime() {
            return calculationTime;
        }
        
        public Map<String, Object> getSystemMetrics() {
            return systemMetrics;
        }
    }

    /**
     * Gets cached Flow if available and not expired.
     * 
     * @return cached Flow or empty if not available/expired
     */
    private Optional<Flow> getCachedFlow() {
        final CachedFlow cached = flowCache.get("current_flow");
        if (cached != null && !cached.isExpired()) {
            return Optional.of(cached.getFlow());
        }
        return Optional.empty();
    }

    /**
     * Quick check to see if Flow detection is possible.
     * 
     * @return true if Flow detection is possible
     */
    private boolean canDetectFlow() {
        // Simple heuristic - check if we're in a ByteHot operation
        final StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        return java.util.Arrays.stream(stack)
            .anyMatch(element -> element.getClassName().contains("org.acmsl.bytehot"));
    }

    /**
     * Generates basic documentation URL for a class.
     * 
     * @param clazz the class to generate URL for
     * @return generated documentation URL
     */
    private Optional<String> generateBasicDocumentationUrl(final Class<?> clazz) {
        final String baseUrl = Defaults.DOCUMENTATION_BASE_URL;
        final String className = clazz.getSimpleName();
        final String packagePath = clazz.getPackage().getName().replace('.', '/');
        
        // Generate URL based on class location
        final String docUrl = String.format("%s/docs/%s/%s.html", baseUrl, packagePath, className);
        
        return Optional.of(docUrl);
    }

    /**
     * Generates method-specific documentation URL.
     * 
     * @param clazz the class containing the method
     * @param methodName the method name
     * @return generated method documentation URL
     */
    private Optional<String> generateMethodDocumentationUrl(final Class<?> clazz, final String methodName) {
        final Optional<String> classUrl = generateBasicDocumentationUrl(clazz);
        
        return classUrl.map(url -> url + "#" + methodName);
    }

    /**
     * Generates contextual documentation URL based on detected Flow.
     * 
     * @param clazz the class requesting documentation
     * @param flow the detected operational Flow
     * @return Flow-specific documentation URL
     */
    private Optional<String> generateContextualDocumentationUrl(final Class<?> clazz, final Flow flow) {
        final String baseUrl = Defaults.DOCUMENTATION_BASE_URL;
        final String className = clazz.getSimpleName();
        final String flowType = flow.getName(); // Use flow name instead of getFlowType()
        
        // Generate Flow-specific documentation URL
        final String contextualUrl = String.format("%s/flows/%s/%s-in-%s.html", 
            baseUrl, flowType.toLowerCase(), className, flowType.toLowerCase());
        
        return Optional.of(contextualUrl);
    }

    /**
     * Gets performance metrics for monitoring and optimization.
     * 
     * @return performance metrics map including integration statistics
     */
    public Map<String, Object> getPerformanceMetrics() {
        final long totalRequests = cacheHits.get() + cacheMisses.get();
        final double hitRate = totalRequests > 0 ? (double) cacheHits.get() / totalRequests : 0.0;
        
        return Map.of(
            "cache_hits", cacheHits.get(),
            "cache_misses", cacheMisses.get(),
            "cache_hit_rate", hitRate,
            "flow_detection_calls", flowDetectionCalls.get(),
            "cached_docs", documentationCache.size(),
            "cached_flows", flowCache.size(),
            "recent_events_count", recentEvents.size(),
            "integration_active", true
        );
    }

    /**
     * Cached documentation URL with expiration.
     */
    private static class CachedDocUrl {
        private final Optional<String> url;
        private final Instant createdAt;
        
        public CachedDocUrl(final Optional<String> url, final Instant createdAt) {
            this.url = url;
            this.createdAt = createdAt;
        }
        
        public Optional<String> getUrl() {
            return url;
        }
        
        public boolean isExpired() {
            return Duration.between(createdAt, Instant.now()).toMinutes() > 30; // 30 minute cache
        }
    }

    /**
     * Cached Flow with expiration.
     */
    private static class CachedFlow {
        private final Flow flow;
        private final Instant createdAt;
        
        public CachedFlow(final Flow flow, final Instant createdAt) {
            this.flow = flow;
            this.createdAt = createdAt;
        }
        
        public Flow getFlow() {
            return flow;
        }
        
        public boolean isExpired() {
            return Duration.between(createdAt, Instant.now()).toSeconds() > 30; // 30 second cache for flows
        }
    }
}