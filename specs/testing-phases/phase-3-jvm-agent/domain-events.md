# Phase 3.2: Domain Events Testing

## Objective
Validate the complete domain event flow from agent attachment through ByteHot's hexagonal architecture, ensuring events are properly created, processed, and emitted throughout the system.

## Prerequisites
- Phase 3.1 (Agent Lifecycle) completed successfully
- Understanding of ByteHot's event-driven architecture
- Knowledge of domain events vs response events
- Agent successfully attached and initialized

## Test Scenarios

### 3.2.1 ByteHotAttachRequested Event Flow

**Description**: Test the creation and processing of ByteHotAttachRequested events during agent attachment.

**Test Steps**:

1. **Event Creation Test**
```bash
mvn -Dtest=org.acmsl.bytehot.domain.events.ByteHotAttachRequestedTest test
```

2. **Event Processing Test**
```bash
mvn -Dtest=*ByteHotTest#testAcceptAttachRequested test
```

**Manual Verification**:
```java
// Test event creation
WatchConfiguration config = new WatchConfiguration(8080);
Instrumentation mockInst = Mockito.mock(Instrumentation.class);
ByteHotAttachRequested event = new ByteHotAttachRequested(config, mockInst);

assert event.getConfiguration() == config;
assert event.getInstrumentation() == mockInst;
assert event.getTimestamp() != null;
```

**Expected Results**:
- ✅ ByteHotAttachRequested event created correctly
- ✅ Event contains valid configuration and instrumentation
- ✅ Event processed by ByteHot aggregate
- ✅ Processing completes without exceptions

### 3.2.2 ByteHotAgentAttached Response Event

**Description**: Test the generation and properties of ByteHotAgentAttached response events.

**Test Steps**:

1. **Response Event Generation Test**
```bash
mvn -Dtest=org.acmsl.bytehot.domain.events.ByteHotAgentAttachedTest test
```

2. **Event Response Flow Test**
```bash
mvn -Dtest=*ByteHotTest#testAttachRequestResponse test
```

**Manual Verification**:
```java
// Test response event generation
ByteHotAttachRequested request = createTestAttachRequest();
List<DomainResponseEvent<ByteHotAttachRequested>> responses = ByteHot.accept(request);

assert responses.size() == 1;
DomainResponseEvent<ByteHotAttachRequested> response = responses.get(0);
assert response instanceof ByteHotAgentAttached;

ByteHotAgentAttached attached = (ByteHotAgentAttached) response;
assert attached.getCause() == request;
assert attached.isSuccessful();
```

**Expected Results**:
- ✅ ByteHotAgentAttached event generated
- ✅ Response event links to original request
- ✅ Success status correctly set
- ✅ Event metadata properly populated

### 3.2.3 Application Layer Event Routing

**Description**: Test event routing through ByteHotApplication.accept().

**Test Steps**:

1. **Application Accept Test**
```bash
mvn -Dtest=*ByteHotApplicationTest#testAcceptEvent test
```

2. **Event Routing Integration Test**
```bash
mvn -Dtest=*ByteHotApplicationIntegrationTest#testEventRouting test
```

**Manual Verification**:
```java
// Test application layer routing
ByteHotApplication app = ByteHotApplication.getInstance();
ByteHotAttachRequested request = createTestAttachRequest();

// Ensure application is initialized
ByteHotApplication.initialize(mockInstrumentation);

List<DomainResponseEvent<ByteHotAttachRequested>> responses = app.accept(request);

assert responses != null;
assert !responses.isEmpty();
assert responses.get(0) instanceof ByteHotAgentAttached;
```

**Expected Results**:
- ✅ Events routed to domain layer correctly
- ✅ Response events returned to application layer
- ✅ No events lost during routing
- ✅ Event processing completes successfully

### 3.2.4 Event Emission Through EventEmitterPort

**Description**: Test emission of response events through the EventEmitterPort infrastructure.

**Test Steps**:

1. **Event Emission Test**
```bash
mvn -Dtest=*EventEmitterAdapterTest#testDomainEventEmission test
```

2. **Event Output Verification Test**
```bash
mvn -Dtest=*ByteHotApplicationIntegrationTest#testEventEmission test
```

**Manual Verification**:
```bash
# Start application with agent and monitor output
java -javaagent:target/bytehot-*-agent.jar \
     -Dbhconfig=test-config.yml \
     -cp target/test-classes TestApplication 2>&1 | tee agent-output.log

# Check for emitted events
grep "ByteHotAgentAttached" agent-output.log
```

**Expected Results**:
- ✅ Response events emitted to configured outputs
- ✅ Event format consistent and readable
- ✅ All response events from accept() are emitted
- ✅ No emission failures or exceptions

### 3.2.5 Event Metadata and Causality

**Description**: Test event metadata preservation and causal relationships.

**Test Steps**:

1. **Event Metadata Test**
```bash
mvn -Dtest=*EventMetadataTest test
```

2. **Causal Chain Test**
```bash
mvn -Dtest=*CausalChainTest test
```

**Manual Verification**:
```java
// Test event causality
ByteHotAttachRequested request = createTestAttachRequest();
UUID requestId = request.getId();
Instant requestTime = request.getTimestamp();

List<DomainResponseEvent<ByteHotAttachRequested>> responses = ByteHot.accept(request);
ByteHotAgentAttached response = (ByteHotAgentAttached) responses.get(0);

// Verify causal relationship
assert response.getCause() == request;
assert response.getCauseId().equals(requestId);
assert response.getTimestamp().isAfter(requestTime);
```

**Expected Results**:
- ✅ Event IDs unique and properly set
- ✅ Timestamps reflect creation time
- ✅ Causal relationships preserved
- ✅ Response events link to original requests

### 3.2.6 Agent Integration Event Flow

**Description**: Test the complete event flow during actual agent operations.

**Test Steps**:

1. **End-to-End Agent Event Test**
```bash
mvn -Dtest=*ByteHotAgentIntegrationTest#testCompleteEventFlow test
```

2. **Real JVM Agent Event Test**
```bash
# This test requires actual JVM with agent
java -javaagent:target/bytehot-*-agent.jar \
     -Dbhconfig=test-config.yml \
     -cp target/test-classes:target/test-classes/support \
     EventFlowTestApplication
```

**Manual Verification**:
```bash
# Monitor complete event flow
java -javaagent:target/bytehot-*-agent.jar \
     -Dbhconfig=test-config.yml \
     -Djava.util.logging.level=FINE \
     -cp target/test-classes TestApplication 2>&1 | \
     grep -E "(ByteHotAttachRequested|ByteHotAgentAttached|ClassFileChanged)"

# Expected sequence:
# 1. ByteHotAttachRequested created
# 2. Event processed by domain
# 3. ByteHotAgentAttached generated
# 4. Response event emitted
# 5. Agent ready for file events
```

**Expected Results**:
- ✅ Complete event flow executes correctly
- ✅ Events processed in correct order
- ✅ No events lost or duplicated
- ✅ Agent becomes operational after event flow

### 3.2.7 Error Events and Exception Handling

**Description**: Test generation and handling of error events during domain processing.

**Test Steps**:

1. **Error Event Generation Test**
```bash
mvn -Dtest=*ByteHotNotStartedTest test
```

2. **Error Event Handling Test**
```bash
mvn -Dtest=*ErrorHandlingIntegrationTest test
```

**Manual Verification**:
```java
// Test error event generation
WatchConfiguration invalidConfig = null; // Invalid configuration
ByteHotAttachRequested invalidRequest = new ByteHotAttachRequested(invalidConfig, null);

try {
    List<DomainResponseEvent<ByteHotAttachRequested>> responses = ByteHot.accept(invalidRequest);
    // Should generate error response event
    assert responses.get(0) instanceof ByteHotNotStarted;
} catch (Exception e) {
    // Or handle via exception, depending on design
}
```

**Expected Results**:
- ✅ Error conditions generate appropriate events
- ✅ Error events contain diagnostic information
- ✅ System continues operating after errors
- ✅ Error events properly emitted and logged

## Success Criteria

### Automated Tests
- [ ] ByteHotAttachRequested event tests pass
- [ ] ByteHotAgentAttached response tests pass
- [ ] Application layer routing tests pass
- [ ] Event emission tests pass
- [ ] Event metadata tests pass
- [ ] Integration event flow tests pass
- [ ] Error event handling tests pass

### Manual Verification
- [ ] Complete event flow works end-to-end
- [ ] Events appear in outputs/logs correctly
- [ ] Event causality preserved throughout flow
- [ ] Error conditions handled gracefully
- [ ] No event loss or corruption observed

### Performance Criteria
- [ ] Event creation < 1ms
- [ ] Event processing < 10ms
- [ ] Event emission < 5ms
- [ ] Complete flow < 50ms
- [ ] Memory usage stable during event processing

## Troubleshooting

### Common Issues

**Issue**: Events not being processed
**Solution**:
- Verify ByteHotApplication is properly initialized
- Check that adapters are injected correctly
- Ensure domain aggregate is accessible
- Verify event types match expected interfaces

**Issue**: Response events not generated
**Solution**:
- Check domain logic in ByteHot.accept()
- Verify event validation passes
- Ensure all required event properties set
- Test with simplified mock events

**Issue**: Events not being emitted
**Solution**:
- Verify EventEmitterPort is resolved correctly
- Check emission configuration and permissions
- Test EventEmitterAdapter functionality separately
- Monitor for threading issues in emission

**Issue**: Event metadata missing or incorrect
**Solution**:
- Check event constructor parameters
- Verify timestamp generation
- Test ID generation uniqueness
- Ensure causality linking works correctly

### Debug Commands

```bash
# Enable detailed event logging
export BYTEHOT_EVENT_DEBUG=true
java -javaagent:target/bytehot-*-agent.jar \
     -Djava.util.logging.level=FINEST \
     -cp target/test-classes TestApplication

# Monitor event processing
jstack $(pgrep java) | grep -A 10 -B 5 "event\|Event"

# Check event emission files
find /tmp -name "*bytehot*" -type f -exec cat {} \;

# Test event serialization
java -cp target/test-classes EventSerializationTest

# Monitor memory during event processing
jstat -gc $(pgrep java) 1s 10
```

### Event Flow Debugging

```java
// Add to test code for debugging
public class EventFlowDebugger {
    public static void traceEventFlow(DomainEvent event) {
        System.out.println("Event: " + event.getClass().getSimpleName());
        System.out.println("ID: " + event.getId());
        System.out.println("Timestamp: " + event.getTimestamp());
        if (event instanceof DomainResponseEvent) {
            DomainResponseEvent<?> response = (DomainResponseEvent<?>) event;
            System.out.println("Cause ID: " + response.getCauseId());
        }
    }
}
```

## Next Steps

Once Phase 3.2 passes completely:
1. Proceed to [Phase 4: Event Sourcing & User Management](../phase-4-event-sourcing/event-store.md)
2. Test event flow with realistic workloads
3. Benchmark event processing performance
4. Test event flow under error conditions
5. Document event format specifications for external integration