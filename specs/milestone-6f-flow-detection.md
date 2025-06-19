# Milestone 6F: Flow Detection

## Overview

**Objective:** Automatically discover business flows from event chains, enabling self-documenting system behavior and process discovery through EventSourcing analysis.

**Status:** 📋 PLANNED

**Value:** Self-documenting system behavior that automatically identifies and documents business processes from event patterns, providing deep insights into how the ByteHot system operates in practice.

## Goals

### Primary Goal
Implement intelligent flow detection that analyzes event sequences to automatically discover and document business processes, making the system self-aware of its own behavior patterns.

### Secondary Goals
- Automatic discovery of common development workflows and patterns
- Real-time flow documentation generation from event analysis
- Flow persistence and visualization for process understanding
- Pattern recognition for identifying optimization opportunities
- Integration with existing EventSourcing infrastructure

## Technical Specifications

### Flow Detection Architecture

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   Event Store   │    │  Flow Detector   │    │   Flow Store    │
│                 │    │                  │    │                 │
│ Event History   │───▶│ Pattern Analysis │───▶│ Discovered      │
│ Event Chains    │    │ Sequence Mining  │    │ Flows           │
│ Correlations    │    │ Flow Recognition │    │ Process Maps    │
│                 │    │                  │    │                 │
│                 │    │ Flow Triggers    │    │ Flow            │
│                 │◄───│ Event Patterns   │◄───│ Documentation   │
│                 │    │ Business Logic   │    │                 │
└─────────────────┘    └──────────────────┘    └─────────────────┘
```

### Core Components

#### FlowDetector Aggregate
```java
/**
 * Domain aggregate responsible for analyzing event sequences and discovering business flows.
 */
public class FlowDetector {
    /**
     * Analyzes a sequence of events to identify potential business flows.
     * @param events The sequence of events to analyze
     * @return Discovered flows or empty if no patterns found
     */
    public static DomainResponseEvent<FlowAnalysisRequested> analyzeEventSequence(
        final FlowAnalysisRequested event
    );
    
    /**
     * Updates flow detection patterns based on new discoveries.
     * @param event The flow pattern update event
     * @return Updated flow patterns or error
     */
    public static DomainResponseEvent<FlowPatternUpdateRequested> updateFlowPatterns(
        final FlowPatternUpdateRequested event
    );
}
```

#### Flow Value Object
```java
/**
 * Immutable representation of a discovered business flow.
 */
@Value
@Builder
public class Flow {
    /**
     * Unique identifier for the flow.
     */
    FlowId flowId;
    
    /**
     * Human-readable name for the flow.
     */
    String name;
    
    /**
     * Description of what this flow represents.
     */
    String description;
    
    /**
     * Sequence of event types that form this flow.
     */
    List<Class<? extends DomainEvent>> eventSequence;
    
    /**
     * Minimum number of events required to identify this flow.
     */
    int minimumEventCount;
    
    /**
     * Maximum time window for events to be considered part of the same flow.
     */
    Duration maximumTimeWindow;
    
    /**
     * Confidence level for flow detection (0.0 to 1.0).
     */
    double confidence;
    
    /**
     * Optional conditions that must be met for flow detection.
     */
    Optional<FlowCondition> conditions;
}
```

#### FlowDetectionPort
```java
/**
 * Port for flow detection and persistence operations.
 */
public interface FlowDetectionPort extends Port {
    /**
     * Analyzes a sequence of events to detect flows.
     * @param events The events to analyze
     * @return Detected flows
     */
    CompletableFuture<List<Flow>> detectFlows(List<VersionedDomainEvent> events);
    
    /**
     * Stores a discovered flow for future reference.
     * @param flow The flow to store
     * @return Success or failure result
     */
    CompletableFuture<FlowStorageResult> storeFlow(Flow flow);
    
    /**
     * Retrieves all known flows.
     * @return All stored flows
     */
    CompletableFuture<List<Flow>> getAllFlows();
    
    /**
     * Searches for flows matching specific criteria.
     * @param criteria The search criteria
     * @return Matching flows
     */
    CompletableFuture<List<Flow>> searchFlows(FlowSearchCriteria criteria);
}
```

### Domain Events

#### FlowAnalysisRequested
```java
/**
 * Event requesting analysis of event sequences for flow detection.
 */
@Value
@Builder
public class FlowAnalysisRequested implements DomainEvent {
    /**
     * Unique identifier for the analysis request.
     */
    AnalysisId analysisId;
    
    /**
     * Events to be analyzed for flow detection.
     */
    List<VersionedDomainEvent> eventsToAnalyze;
    
    /**
     * Optional time window for analysis.
     */
    Optional<TimeWindow> analysisWindow;
    
    /**
     * Minimum confidence level required for flow detection.
     */
    double minimumConfidence;
    
    /**
     * User who requested the analysis.
     */
    UserId requestedBy;
    
    /**
     * Timestamp when analysis was requested.
     */
    Instant requestedAt;
}
```

#### FlowDiscovered
```java
/**
 * Event indicating a new business flow has been discovered.
 */
@Value
@Builder
public class FlowDiscovered implements DomainResponseEvent<FlowAnalysisRequested> {
    /**
     * The original analysis request.
     */
    FlowAnalysisRequested originalEvent;
    
    /**
     * The discovered flow.
     */
    Flow discoveredFlow;
    
    /**
     * Events that triggered the flow detection.
     */
    List<VersionedDomainEvent> triggeringEvents;
    
    /**
     * Confidence level of the discovery.
     */
    double confidence;
    
    /**
     * Timestamp when flow was discovered.
     */
    Instant discoveredAt;
}
```

#### FlowPatternUpdated
```java
/**
 * Event indicating flow detection patterns have been updated.
 */
@Value
@Builder
public class FlowPatternUpdated implements DomainResponseEvent<FlowPatternUpdateRequested> {
    /**
     * The original pattern update request.
     */
    FlowPatternUpdateRequested originalEvent;
    
    /**
     * Updated flow patterns.
     */
    List<FlowPattern> updatedPatterns;
    
    /**
     * Reason for the pattern update.
     */
    String updateReason;
    
    /**
     * Timestamp when patterns were updated.
     */
    Instant updatedAt;
}
```

## Flow Detection Algorithms

### 1. Sequence Mining Algorithm

**Event Sequence Analysis:**
```java
public class EventSequenceMiner {
    /**
     * Mines frequent event sequences from historical data.
     * @param events Historical events to mine
     * @param minimumSupport Minimum frequency for sequence to be considered
     * @return Frequent event sequences
     */
    public List<EventSequence> mineFrequentSequences(
        List<VersionedDomainEvent> events,
        double minimumSupport
    );
    
    /**
     * Identifies temporal patterns in event sequences.
     * @param sequences Event sequences to analyze
     * @return Temporal patterns with timing information
     */
    public List<TemporalPattern> identifyTemporalPatterns(
        List<EventSequence> sequences
    );
}
```

### 2. Pattern Recognition Engine

**Flow Pattern Matching:**
```java
public class FlowPatternMatcher {
    /**
     * Matches event sequences against known flow patterns.
     * @param events Events to match
     * @param knownPatterns Existing flow patterns
     * @return Matched flows with confidence levels
     */
    public List<FlowMatch> matchPatterns(
        List<VersionedDomainEvent> events,
        List<FlowPattern> knownPatterns
    );
    
    /**
     * Learns new patterns from unmatched event sequences.
     * @param unmatchedSequences Sequences that didn't match existing patterns
     * @return Newly discovered patterns
     */
    public List<FlowPattern> learnNewPatterns(
        List<EventSequence> unmatchedSequences
    );
}
```

### 3. Real-Time Flow Detection

**Stream Processing:**
```java
public class RealTimeFlowDetector {
    /**
     * Processes events in real-time to detect flows as they happen.
     * @param eventStream Stream of incoming events
     * @return Stream of detected flows
     */
    public CompletableFuture<Void> processEventStream(
        Publisher<VersionedDomainEvent> eventStream,
        Consumer<FlowDiscovered> flowHandler
    );
    
    /**
     * Maintains sliding window of events for pattern detection.
     * @param windowSize Size of the sliding window
     * @param windowDuration Time duration for the window
     */
    public void configureSlidingWindow(int windowSize, Duration windowDuration);
}
```

## Pre-Defined Flow Patterns

### 1. Hot-Swap Complete Flow
```java
public static final FlowPattern HOT_SWAP_COMPLETE_FLOW = FlowPattern.builder()
    .name("Hot-Swap Complete Flow")
    .description("Complete hot-swap operation from file change to instance update")
    .eventSequence(List.of(
        ClassFileChanged.class,
        ClassMetadataExtracted.class,
        BytecodeValidated.class,
        HotSwapRequested.class,
        ClassRedefinitionSucceeded.class,
        InstancesUpdated.class
    ))
    .minimumEventCount(4)
    .maximumTimeWindow(Duration.ofSeconds(30))
    .confidence(0.95)
    .build();
```

### 2. User Session Flow
```java
public static final FlowPattern USER_SESSION_FLOW = FlowPattern.builder()
    .name("User Session Flow")
    .description("User authentication and session management")
    .eventSequence(List.of(
        UserDiscoveryRequested.class,
        UserAuthenticated.class,
        UserSessionStarted.class
    ))
    .minimumEventCount(2)
    .maximumTimeWindow(Duration.ofMinutes(5))
    .confidence(0.90)
    .build();
```

### 3. Error Recovery Flow
```java
public static final FlowPattern ERROR_RECOVERY_FLOW = FlowPattern.builder()
    .name("Error Recovery Flow")
    .description("System error detection and recovery process")
    .eventSequence(List.of(
        ClassRedefinitionFailed.class,
        ErrorRecoveryInitiated.class,
        RollbackRequested.class,
        SystemRecovered.class
    ))
    .minimumEventCount(3)
    .maximumTimeWindow(Duration.ofMinutes(2))
    .confidence(0.85)
    .build();
```

## Infrastructure Adapters

### FilesystemFlowStoreAdapter
```java
/**
 * Filesystem-based adapter for storing and retrieving flows.
 */
@Component
public class FilesystemFlowStoreAdapter implements FlowDetectionPort {
    
    private final Path flowStorePath;
    private final ObjectMapper objectMapper;
    
    @Override
    public CompletableFuture<List<Flow>> detectFlows(List<VersionedDomainEvent> events) {
        return CompletableFuture.supplyAsync(() -> {
            // Implement flow detection algorithm
            return flowDetectionEngine.analyzeEvents(events);
        });
    }
    
    @Override
    public CompletableFuture<FlowStorageResult> storeFlow(Flow flow) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Path flowFile = flowStorePath.resolve(flow.getFlowId().getValue() + ".json");
                String jsonContent = objectMapper.writeValueAsString(new JsonFlow(flow));
                Files.writeString(flowFile, jsonContent);
                return FlowStorageResult.success(flow.getFlowId());
            } catch (Exception e) {
                return FlowStorageResult.failure(flow.getFlowId(), e.getMessage());
            }
        });
    }
}
```

### InMemoryFlowDetectionEngine
```java
/**
 * In-memory flow detection engine for development and testing.
 */
@Component
public class InMemoryFlowDetectionEngine {
    
    private final List<FlowPattern> knownPatterns;
    private final EventSequenceMiner sequenceMiner;
    private final FlowPatternMatcher patternMatcher;
    
    public List<Flow> analyzeEvents(List<VersionedDomainEvent> events) {
        // Group events by correlation ID or user
        Map<String, List<VersionedDomainEvent>> eventGroups = groupEventsByCorrelation(events);
        
        List<Flow> discoveredFlows = new ArrayList<>();
        
        for (Map.Entry<String, List<VersionedDomainEvent>> group : eventGroups.entrySet()) {
            List<VersionedDomainEvent> groupEvents = group.getValue();
            
            // Match against known patterns
            List<FlowMatch> matches = patternMatcher.matchPatterns(groupEvents, knownPatterns);
            
            // Convert matches to flows
            discoveredFlows.addAll(convertMatchesToFlows(matches));
        }
        
        return discoveredFlows;
    }
}
```

## Flow Visualization and Documentation

### Flow Documentation Generator
```java
/**
 * Generates human-readable documentation for discovered flows.
 */
public class FlowDocumentationGenerator {
    
    /**
     * Generates Markdown documentation for a flow.
     * @param flow The flow to document
     * @return Markdown documentation
     */
    public String generateMarkdownDocumentation(Flow flow) {
        StringBuilder doc = new StringBuilder();
        
        doc.append("# ").append(flow.getName()).append("\n\n");
        doc.append("## Description\n");
        doc.append(flow.getDescription()).append("\n\n");
        
        doc.append("## Event Sequence\n");
        for (int i = 0; i < flow.getEventSequence().size(); i++) {
            Class<? extends DomainEvent> eventType = flow.getEventSequence().get(i);
            doc.append(i + 1).append(". ").append(eventType.getSimpleName()).append("\n");
        }
        
        doc.append("\n## Flow Characteristics\n");
        doc.append("- **Minimum Events:** ").append(flow.getMinimumEventCount()).append("\n");
        doc.append("- **Time Window:** ").append(flow.getMaximumTimeWindow()).append("\n");
        doc.append("- **Confidence:** ").append(flow.getConfidence() * 100).append("%\n");
        
        return doc.toString();
    }
    
    /**
     * Generates Mermaid diagram for flow visualization.
     * @param flow The flow to visualize
     * @return Mermaid diagram code
     */
    public String generateMermaidDiagram(Flow flow) {
        StringBuilder diagram = new StringBuilder();
        
        diagram.append("graph LR\n");
        
        List<Class<? extends DomainEvent>> sequence = flow.getEventSequence();
        for (int i = 0; i < sequence.size(); i++) {
            String current = "E" + i + "[" + sequence.get(i).getSimpleName() + "]";
            diagram.append("    ").append(current).append("\n");
            
            if (i < sequence.size() - 1) {
                String next = "E" + (i + 1);
                diagram.append("    E").append(i).append(" --> ").append(next).append("\n");
            }
        }
        
        return diagram.toString();
    }
}
```

## Integration with Existing System

### EventSourcing Integration
- **Event Store Access:** Read historical events for pattern analysis
- **Event Subscription:** Real-time flow detection as events occur
- **Flow Persistence:** Store discovered flows as domain events
- **Replay Capability:** Re-analyze historical periods for new patterns

### User Management Integration
- **User-Specific Flows:** Detect patterns specific to individual users
- **Personal Analytics:** Track individual development workflow patterns
- **Team Patterns:** Identify common team development practices
- **Productivity Insights:** Measure efficiency of different workflows

### Application Layer Integration
```java
@Component
public class FlowDetectionService {
    
    private final EventStorePort eventStore;
    private final FlowDetectionPort flowDetection;
    private final UserManagementPort userManagement;
    
    /**
     * Analyzes recent events for a specific user to detect their workflow patterns.
     * @param userId The user to analyze
     * @param timeWindow Time period to analyze
     * @return Discovered user-specific flows
     */
    public CompletableFuture<List<Flow>> analyzeUserWorkflowPatterns(
        UserId userId,
        Duration timeWindow
    ) {
        return eventStore.getEventsByUserAndTimeRange(userId, timeWindow)
            .thenCompose(events -> flowDetection.detectFlows(events));
    }
    
    /**
     * Performs system-wide flow analysis to discover common patterns.
     * @param analysisWindow Time period to analyze
     * @return System-wide flow patterns
     */
    public CompletableFuture<List<Flow>> analyzeSystemWidePatterns(
        Duration analysisWindow
    ) {
        return eventStore.getEventsInTimeRange(analysisWindow)
            .thenCompose(events -> flowDetection.detectFlows(events));
    }
}
```

## Testing Strategy

### Flow Detection Testing
```java
class FlowDetectionTest {
    
    @Test
    void should_detect_hot_swap_complete_flow() {
        // Given: A sequence of events forming a complete hot-swap flow
        List<VersionedDomainEvent> events = Arrays.asList(
            createClassFileChangedEvent(),
            createClassMetadataExtractedEvent(),
            createBytecodeValidatedEvent(),
            createHotSwapRequestedEvent(),
            createClassRedefinitionSucceededEvent(),
            createInstancesUpdatedEvent()
        );
        
        // When: Flow detection is performed
        List<Flow> detectedFlows = flowDetector.detectFlows(events).join();
        
        // Then: Hot-swap complete flow should be detected
        assertThat(detectedFlows)
            .hasSize(1)
            .first()
            .extracting(Flow::getName)
            .isEqualTo("Hot-Swap Complete Flow");
    }
    
    @Test
    void should_not_detect_flow_with_insufficient_events() {
        // Given: Incomplete event sequence
        List<VersionedDomainEvent> events = Arrays.asList(
            createClassFileChangedEvent(),
            createClassMetadataExtractedEvent()
        );
        
        // When: Flow detection is performed
        List<Flow> detectedFlows = flowDetector.detectFlows(events).join();
        
        // Then: No flows should be detected
        assertThat(detectedFlows).isEmpty();
    }
}
```

### Pattern Learning Testing
```java
class FlowPatternLearningTest {
    
    @Test
    void should_learn_new_pattern_from_repeated_sequences() {
        // Given: Multiple occurrences of the same event sequence
        List<EventSequence> repeatedSequences = createRepeatedSequences();
        
        // When: Pattern learning is performed
        List<FlowPattern> learnedPatterns = patternMatcher.learnNewPatterns(repeatedSequences);
        
        // Then: New pattern should be learned
        assertThat(learnedPatterns)
            .hasSize(1)
            .first()
            .extracting(FlowPattern::getConfidence)
            .satisfies(confidence -> assertThat(confidence).isGreaterThan(0.8));
    }
}
```

## Performance Considerations

### Scalability
- **Incremental Analysis:** Process events in batches to avoid memory issues
- **Caching:** Cache frequently accessed flow patterns for quick matching
- **Asynchronous Processing:** Use CompletableFuture for non-blocking operations
- **Index Optimization:** Create indexes on event timestamps and user IDs

### Memory Management
- **Sliding Windows:** Limit memory usage with bounded event windows
- **Pattern Pruning:** Remove low-confidence patterns periodically
- **Event Compression:** Store only essential event information for analysis
- **Garbage Collection:** Proactive cleanup of temporary analysis data

### Real-Time Processing
- **Stream Processing:** Use reactive streams for real-time flow detection
- **Buffering:** Buffer events to handle burst loads
- **Backpressure:** Implement backpressure handling for high event volumes
- **Parallel Processing:** Process independent event groups in parallel

## File Structure

```
bytehot/src/main/java/org/acmsl/bytehot/
├── domain/
│   ├── FlowDetector.java                 # Flow detection aggregate
│   ├── Flow.java                         # Flow value object
│   ├── FlowId.java                       # Flow identifier
│   ├── FlowPattern.java                  # Flow pattern definition
│   ├── FlowCondition.java                # Flow detection conditions
│   └── events/
│       ├── FlowAnalysisRequested.java    # Request flow analysis
│       ├── FlowDiscovered.java           # Flow discovery result
│       ├── FlowPatternUpdateRequested.java
│       └── FlowPatternUpdated.java
├── application/
│   └── FlowDetectionService.java         # Application service
└── infrastructure/
    ├── FlowDetectionPort.java            # Flow detection port
    ├── FilesystemFlowStoreAdapter.java   # Filesystem adapter
    ├── InMemoryFlowDetectionEngine.java  # In-memory engine
    ├── EventSequenceMiner.java           # Sequence mining
    ├── FlowPatternMatcher.java           # Pattern matching
    ├── RealTimeFlowDetector.java         # Real-time detection
    └── FlowDocumentationGenerator.java   # Documentation generation
```

## Success Criteria

### Flow Detection Accuracy
- **Pattern Recognition:** >90% accuracy for known flow patterns
- **False Positive Rate:** <5% for flow detection
- **Coverage:** Detect at least 80% of actual business flows
- **Confidence Calibration:** Confidence scores align with actual accuracy

### Performance Metrics
- **Detection Latency:** <500ms for real-time flow detection
- **Analysis Throughput:** >1000 events/second for batch analysis
- **Memory Usage:** <100MB for typical flow detection operations
- **Storage Efficiency:** Compressed flow patterns under 1KB each

### User Experience
- **Documentation Quality:** Auto-generated flow documentation is readable and accurate
- **Visualization:** Flow diagrams are clear and informative
- **Integration:** Seamless integration with existing EventSourcing infrastructure
- **Extensibility:** Easy to add new flow patterns and detection algorithms

---

## Walking Skeleton Value

**Self-Documenting System Behavior:** ByteHot becomes aware of its own behavioral patterns, automatically documenting how it operates in practice and identifying optimization opportunities.

**Process Discovery:** Developers gain deep insights into actual system usage patterns, helping them understand and optimize real-world workflows.

**Intelligent Analytics:** The foundation for advanced analytics and AI-powered development optimization based on discovered patterns.

**Revolutionary Observability:** Beyond monitoring system metrics, ByteHot understands and documents its own business logic flows.

**The Flow Detection milestone transforms ByteHot from a reactive tool into an intelligent system that understands and documents its own behavior patterns, laying the foundation for AI-powered development optimization.**