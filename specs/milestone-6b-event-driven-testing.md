# Milestone 6B: Event-Driven Testing Framework

## Overview

**Objective:** Implement a revolutionary event-driven testing framework that uses Given/When/Then patterns with domain events, enabling tests to build system state from events, test with events, and verify results through events.

**Status:** 📋 Planned (Depends on Milestone 6A)

**Walking Skeleton Value:** Transforms all testing to be event-centric, making tests more realistic, bugs more reproducible, and system behavior more verifiable. This is the foundation that makes event-driven bug reporting possible.

## Goals

### Primary Goal
Create a testing framework where:
- **Given**: System state is built from pre-existing events
- **When**: The event under test is sent
- **Then**: Expected resulting events are verified

### Secondary Goals
- Enable automatic bug reproduction from event sequences
- Provide realistic testing scenarios using actual event data
- Create comprehensive event-based assertions
- Establish patterns for all future testing

## Revolutionary Testing Approach

### Traditional Testing vs Event-Driven Testing

#### Traditional Testing
```java
@Test
void shouldUpdateInstancesAfterHotSwap() {
    // Arrange: Mock objects and state
    InstanceTracker tracker = new InstanceTracker();
    Object instance = new TestClass();
    tracker.track(instance);
    
    // Act: Call method directly
    InstanceUpdater updater = new InstanceUpdater(tracker);
    InstancesUpdated result = updater.update(className, newBytecode);
    
    // Assert: Check return values
    assertThat(result.getUpdatedCount()).isEqualTo(1);
}
```

#### Event-Driven Testing
```java
@Test
void shouldUpdateInstancesAfterHotSwap() {
    // Given: System state from real events
    given()
        .event(new ByteHotAgentAttached("agent-123"))
        .event(new ClassFileChanged("/path/TestClass.class"))
        .event(new BytecodeValidated("TestClass", bytecode))
        .event(new ClassRedefinitionSucceeded("TestClass"));
    
    // When: The event we want to test
    when()
        .event(new InstanceUpdateRequested("TestClass", 2));
    
    // Then: Expected resulting events
    then()
        .expectEvent(InstancesUpdated.class)
        .withUpdatedCount(2)
        .withSuccessful(true);
}
```

## Technical Specifications

### Core Testing Framework

#### 1. EventDrivenTestSupport Base Class

```java
package org.acmsl.bytehot.testing;

public abstract class EventDrivenTestSupport {
    
    protected EventTestContext context;
    protected EventStorePort eventStore;
    protected ByteHotApplication application;
    
    @BeforeEach
    void setupEventDrivenTest() {
        this.context = new EventTestContext();
        this.eventStore = new InMemoryEventStoreAdapter();
        this.application = new ByteHotApplication();
        
        // Wire the test event store
        Ports.register(EventStorePort.class, eventStore);
    }
    
    /**
     * Starts the "Given" phase - building system state
     */
    protected GivenStage given() {
        return new GivenStage(context, eventStore);
    }
    
    /**
     * Starts the "When" phase - sending test event
     */
    protected WhenStage when() {
        return new WhenStage(context, application);
    }
    
    /**
     * Starts the "Then" phase - verifying results
     */
    protected ThenStage then() {
        return new ThenStage(context);
    }
    
    /**
     * Creates a test scenario from a bug report
     */
    protected BugReproductionStage reproduce(BugReport bugReport) {
        return new BugReproductionStage(bugReport, context);
    }
}
```

#### 2. GivenStage - Building System State

```java
package org.acmsl.bytehot.testing.stages;

public class GivenStage {
    private final EventTestContext context;
    private final EventStorePort eventStore;
    private final List<VersionedDomainEvent> priorEvents;
    
    public GivenStage(EventTestContext context, EventStorePort eventStore) {
        this.context = context;
        this.eventStore = eventStore;
        this.priorEvents = new ArrayList<>();
    }
    
    /**
     * Adds a prior event to build system state
     */
    public GivenStage event(VersionedDomainEvent event) {
        priorEvents.add(event);
        eventStore.save(event);
        context.recordPriorEvent(event);
        return this;
    }
    
    /**
     * Adds multiple events in sequence
     */
    public GivenStage events(VersionedDomainEvent... events) {
        Arrays.stream(events).forEach(this::event);
        return this;
    }
    
    /**
     * Loads events from a saved scenario
     */
    public GivenStage scenario(String scenarioName) {
        List<VersionedDomainEvent> scenarioEvents = 
            TestScenarioRepository.load(scenarioName);
        scenarioEvents.forEach(this::event);
        return this;
    }
    
    /**
     * Builds system state to a specific point in time
     */
    public GivenStage eventsUntil(Instant timestamp) {
        // Load all events until the specified timestamp
        return this;
    }
    
    /**
     * Builds system state from a bug report
     */
    public GivenStage bugContext(BugReport bugReport) {
        bugReport.getReproductionEvents().forEach(this::event);
        return this;
    }
}
```

#### 3. WhenStage - Sending Test Event

```java
package org.acmsl.bytehot.testing.stages;

public class WhenStage {
    private final EventTestContext context;
    private final ByteHotApplication application;
    
    public WhenStage(EventTestContext context, ByteHotApplication application) {
        this.context = context;
        this.application = application;
    }
    
    /**
     * Sends the event under test
     */
    public WhenStage event(DomainEvent event) {
        context.setTestEvent(event);
        
        // Capture all resulting events
        EventCapturingEmitter emitter = new EventCapturingEmitter();
        Ports.register(EventEmitterPort.class, emitter);
        
        // Send the event through the application
        List<DomainResponseEvent<?>> results = application.accept(event);
        
        // Capture the results
        context.setResultingEvents(results);
        context.setEmittedEvents(emitter.getCapturedEvents());
        
        return this;
    }
    
    /**
     * Sends a command that will generate events
     */
    public WhenStage command(Object command) {
        // Convert command to appropriate domain event
        DomainEvent event = CommandToEventConverter.convert(command);
        return event(event);
    }
    
    /**
     * Simulates an external trigger (file change, etc.)
     */
    public WhenStage externalTrigger(String triggerType, Object... params) {
        // Create appropriate external event
        return this;
    }
}
```

#### 4. ThenStage - Verifying Results

```java
package org.acmsl.bytehot.testing.stages;

public class ThenStage {
    private final EventTestContext context;
    
    public ThenStage(EventTestContext context) {
        this.context = context;
    }
    
    /**
     * Expects a specific event type in the results
     */
    public EventExpectation expectEvent(Class<? extends DomainEvent> eventType) {
        return new EventExpectation(context, eventType);
    }
    
    /**
     * Expects multiple events in sequence
     */
    public SequenceExpectation expectSequence() {
        return new SequenceExpectation(context);
    }
    
    /**
     * Expects no events (operation should be silent)
     */
    public ThenStage expectNoEvents() {
        List<DomainEvent> events = context.getResultingEvents();
        assertThat(events).isEmpty();
        return this;
    }
    
    /**
     * Expects an error event
     */
    public ErrorExpectation expectError() {
        return new ErrorExpectation(context);
    }
    
    /**
     * Verifies system state after events
     */
    public StateExpectation expectState() {
        return new StateExpectation(context);
    }
    
    /**
     * Saves this test scenario for reuse
     */
    public ThenStage saveScenario(String scenarioName) {
        TestScenarioRepository.save(scenarioName, context);
        return this;
    }
}
```

### Event Assertions and Matchers

#### 1. EventExpectation - Single Event Assertions

```java
package org.acmsl.bytehot.testing.expectations;

public class EventExpectation {
    private final EventTestContext context;
    private final Class<? extends DomainEvent> expectedEventType;
    private final Map<String, Object> expectedProperties;
    
    public EventExpectation(
        EventTestContext context, 
        Class<? extends DomainEvent> eventType
    ) {
        this.context = context;
        this.expectedEventType = eventType;
        this.expectedProperties = new HashMap<>();
    }
    
    /**
     * Expects a specific property value
     */
    public EventExpectation with(String property, Object value) {
        expectedProperties.put(property, value);
        return this;
    }
    
    /**
     * Convenience methods for common properties
     */
    public EventExpectation withClassName(String className) {
        return with("className", className);
    }
    
    public EventExpectation withAggregateId(String aggregateId) {
        return with("aggregateId", aggregateId);
    }
    
    public EventExpectation withSuccessful(boolean successful) {
        return with("successful", successful);
    }
    
    /**
     * Expects the event to have occurred within a time window
     */
    public EventExpectation within(Duration timeWindow) {
        Instant testStart = context.getTestStartTime();
        Instant maxTime = testStart.plus(timeWindow);
        
        return with("timestamp", lessThan(maxTime));
    }
    
    /**
     * Expects the event to have specific causality
     */
    public EventExpectation causedBy(Class<? extends DomainEvent> causeEventType) {
        // Find the causing event in the test context
        return this;
    }
    
    /**
     * Verifies the expectation
     */
    public void verify() {
        List<DomainEvent> events = context.getResultingEvents();
        
        Optional<DomainEvent> matchingEvent = events.stream()
            .filter(event -> expectedEventType.isInstance(event))
            .findFirst();
        
        assertThat(matchingEvent)
            .as("Expected event of type %s", expectedEventType.getSimpleName())
            .isPresent();
        
        DomainEvent event = matchingEvent.get();
        verifyProperties(event);
    }
    
    private void verifyProperties(DomainEvent event) {
        for (Map.Entry<String, Object> entry : expectedProperties.entrySet()) {
            String property = entry.getKey();
            Object expectedValue = entry.getValue();
            
            Object actualValue = ReflectionUtils.getProperty(event, property);
            
            assertThat(actualValue)
                .as("Property %s of event %s", property, event.getClass().getSimpleName())
                .isEqualTo(expectedValue);
        }
    }
}
```

#### 2. SequenceExpectation - Multiple Event Verification

```java
package org.acmsl.bytehot.testing.expectations;

public class SequenceExpectation {
    private final EventTestContext context;
    private final List<Class<? extends DomainEvent>> expectedSequence;
    
    public SequenceExpectation(EventTestContext context) {
        this.context = context;
        this.expectedSequence = new ArrayList<>();
    }
    
    /**
     * Adds an expected event to the sequence
     */
    public SequenceExpectation then(Class<? extends DomainEvent> eventType) {
        expectedSequence.add(eventType);
        return this;
    }
    
    /**
     * Verifies the exact sequence of events
     */
    public void inOrder() {
        List<DomainEvent> actualEvents = context.getResultingEvents();
        
        assertThat(actualEvents)
            .as("Expected event sequence length")
            .hasSize(expectedSequence.size());
        
        for (int i = 0; i < expectedSequence.size(); i++) {
            Class<? extends DomainEvent> expectedType = expectedSequence.get(i);
            DomainEvent actualEvent = actualEvents.get(i);
            
            assertThat(actualEvent)
                .as("Event at position %d should be of type %s", i, expectedType.getSimpleName())
                .isInstanceOf(expectedType);
        }
    }
    
    /**
     * Verifies events occurred but order doesn't matter
     */
    public void inAnyOrder() {
        List<DomainEvent> actualEvents = context.getResultingEvents();
        
        for (Class<? extends DomainEvent> expectedType : expectedSequence) {
            boolean found = actualEvents.stream()
                .anyMatch(expectedType::isInstance);
            
            assertThat(found)
                .as("Expected event of type %s", expectedType.getSimpleName())
                .isTrue();
        }
    }
}
```

### Bug Reproduction Framework

#### 1. BugReport - Capturing Event Context

```java
package org.acmsl.bytehot.testing.bugs;

@Value
@Builder
public class BugReport {
    /**
     * Unique identifier for this bug report
     */
    String bugId;
    
    /**
     * Description of the bug
     */
    String description;
    
    /**
     * Exception that was thrown (if any)
     */
    Throwable exception;
    
    /**
     * Complete sequence of events that led to the bug
     */
    List<VersionedDomainEvent> reproductionEvents;
    
    /**
     * System state at the time of the bug
     */
    Map<String, Object> systemState;
    
    /**
     * Environment information
     */
    EnvironmentInfo environment;
    
    /**
     * Timestamp when the bug occurred
     */
    Instant occurredAt;
    
    /**
     * User who encountered the bug
     */
    String userId;
    
    /**
     * Creates a test case from this bug report
     */
    public TestCase toTestCase() {
        return TestCase.builder()
            .name("BugReproduction_" + bugId)
            .reproductionEvents(reproductionEvents)
            .expectedBehavior("Should not throw " + exception.getClass().getSimpleName())
            .build();
    }
    
    /**
     * Saves this bug report for analysis
     */
    public void save() {
        BugReportRepository.save(this);
    }
}
```

#### 2. Automatic Bug Report Generation

```java
package org.acmsl.bytehot.testing.bugs;

public class EventSnapshotException extends RuntimeException {
    private final BugReport bugReport;
    
    public EventSnapshotException(String message, Throwable cause) {
        super(message, cause);
        this.bugReport = captureBugReport(message, cause);
    }
    
    private BugReport captureBugReport(String message, Throwable cause) {
        // Get event history from current context
        List<VersionedDomainEvent> events = getCurrentEventHistory();
        
        // Capture system state
        Map<String, Object> systemState = captureSystemState();
        
        // Build comprehensive bug report
        return BugReport.builder()
            .bugId(UUID.randomUUID().toString())
            .description(message)
            .exception(cause)
            .reproductionEvents(events)
            .systemState(systemState)
            .environment(EnvironmentInfo.current())
            .occurredAt(Instant.now())
            .userId(getCurrentUserId())
            .build();
    }
    
    public BugReport getBugReport() {
        return bugReport;
    }
    
    /**
     * Creates a test case that reproduces this bug
     */
    public TestCase createReproductionTest() {
        return bugReport.toTestCase();
    }
}
```

## Test Examples

### 1. Simple Event-Driven Test

```java
class EventDrivenHotSwapTest extends EventDrivenTestSupport {
    
    @Test
    void shouldSuccessfullyCompleteHotSwapFlow() {
        // Given: ByteHot is attached and watching files
        given()
            .event(new ByteHotAgentAttached("agent-123"))
            .event(new WatchPathConfigured("/target/classes", "*.class"));
        
        // When: A class file changes
        when()
            .event(new ClassFileChanged("/target/classes/MyClass.class", "MyClass", 1024));
        
        // Then: Complete hot-swap flow occurs
        then()
            .expectSequence()
                .then(ClassMetadataExtracted.class)
                .then(BytecodeValidated.class)
                .then(HotSwapRequested.class)
                .then(ClassRedefinitionSucceeded.class)
                .then(InstancesUpdated.class)
            .inOrder();
    }
}
```

### 2. Bug Reproduction Test

```java
class BugReproductionTest extends EventDrivenTestSupport {
    
    @Test
    void shouldReproduceConcurrentModificationBug() {
        // Given: Bug report from production
        BugReport bugReport = BugReportRepository.load("BUG-2025-001");
        
        // When: Reproducing the exact event sequence
        reproduce(bugReport)
            .replayEvents()
            .expectException(ConcurrentModificationException.class);
        
        // Then: Bug is reproduced and can be debugged
        // This test will fail until the bug is fixed
    }
}
```

### 3. Complex Scenario Test

```java
class ComplexScenarioTest extends EventDrivenTestSupport {
    
    @Test
    void shouldHandleFrameworkIntegrationScenario() {
        // Given: Spring application with ByteHot
        given()
            .scenario("spring-application-startup")
            .event(new SpringContextLoaded("app-context-123"))
            .event(new BeansRegistered(List.of("userService", "orderService")));
        
        // When: Hot-swapping a service class
        when()
            .event(new ClassRedefinitionSucceeded("UserService"));
        
        // Then: Spring beans are properly updated
        then()
            .expectEvent(SpringBeansRefreshed.class)
                .withBeanNames(List.of("userService"))
                .withSuccessful(true)
            .expectEvent(InstancesUpdated.class)
                .withUpdatedCount(1);
    }
}
```

## Integration with Existing Testing

### Migration Strategy

1. **Phase 1**: New tests use event-driven approach
2. **Phase 2**: Convert existing tests gradually
3. **Phase 3**: Deprecate non-event-driven testing utilities

### Compatibility Layer

```java
// Traditional test can still be written
@Test
void traditionalTest() {
    // Existing test code continues to work
}

// But can be enhanced with event verification
@Test
void enhancedTraditionalTest() {
    // Traditional setup
    HotSwapManager manager = new HotSwapManager();
    
    // Traditional action
    manager.performHotSwap("MyClass", bytecode);
    
    // Event-driven verification
    then()
        .expectEvent(ClassRedefinitionSucceeded.class)
        .withClassName("MyClass");
}
```

## Success Criteria

### Functional Requirements
- ✅ Can build system state from event sequences
- ✅ Can test with domain events as inputs
- ✅ Can verify results through expected events
- ✅ Can reproduce bugs from event snapshots
- ✅ Integrates with existing JUnit framework

### Quality Requirements
- ✅ Event-driven tests are more readable than traditional tests
- ✅ Bug reports automatically include reproduction information
- ✅ Test scenarios can be saved and reused
- ✅ Performance impact < 5% compared to traditional tests

### Developer Experience
- ✅ Easy migration from traditional testing
- ✅ Comprehensive IDE support and debugging
- ✅ Clear error messages and test failures
- ✅ Rich assertion library for events

## Future Enhancements

### Property-Based Testing with Events
- Generate random event sequences
- Verify system invariants hold
- Discover edge cases automatically

### Visual Test Debugging
- Event flow visualization during test execution
- Timeline view of event sequences
- Interactive test debugging

### AI-Powered Test Generation
- Generate test scenarios from production events
- Suggest test cases based on code changes
- Automatic regression test creation

---

**Milestone 6B revolutionizes ByteHot testing by making events first-class citizens in the testing process. This approach provides more realistic tests, easier bug reproduction, and a foundation for advanced testing capabilities.**