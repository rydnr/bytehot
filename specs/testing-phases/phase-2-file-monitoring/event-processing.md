# Phase 2.2: Event Processing Testing

## Objective
Validate ByteHot's file event processing pipeline, ensuring file system events are properly transformed into domain events and routed through the application layer.

## Prerequisites
- Phase 2.1 (File Watcher) completed successfully
- Understanding of domain event architecture
- Knowledge of ClassFileChanged event structure
- Test .class files prepared for monitoring

## Test Scenarios

### 2.2.1 Class File Change Detection

**Description**: Test detection and processing of .class file modifications.

**Test Steps**:

1. **Basic Class File Change Test**
```bash
# Prepare test class file
mkdir -p target/test-classes/event-test
echo "Initial bytecode content" > target/test-classes/event-test/TestClass.class

mvn -Dtest=*FileWatcherAdapterTest#testClassFileChangeDetection test
```

2. **Event Generation Test**
```bash
mvn -Dtest=*ClassFileChangedTest test
```

**Manual Verification**:
```bash
# Start monitoring and modify file
cd target/test-classes/event-test

# Modify file and check event generation
echo "Modified bytecode content" > TestClass.class
echo "Additional modification" >> TestClass.class

# Check logs for ClassFileChanged events
grep "ClassFileChanged" target/test-logs/*.log
```

**Expected Results**:
- ✅ .class file modifications detected
- ✅ ClassFileChanged events generated
- ✅ Event contains correct file path
- ✅ Event includes file size and timestamp

### 2.2.2 Event Metadata Extraction

**Description**: Test extraction of metadata from file change events.

**Test Steps**:

1. **Class Name Extraction Test**
```bash
mvn -Dtest=*FileWatcherAdapterTest#testClassNameExtraction test
```

2. **File Size Tracking Test**
```bash
mvn -Dtest=*EventMetadataTest test
```

**Manual Verification**:
```java
// Test metadata extraction
Path classFile = Paths.get("target/test-classes/event-test/UserService.class");
ClassFileChanged event = ClassFileChanged.forNewSession(
    classFile, 
    "UserService", 
    Files.size(classFile),
    Instant.now()
);

assert "UserService".equals(event.getClassName());
assert event.getFileSize() > 0;
assert event.getDetectionTimestamp() != null;
assert event.getClassFile().equals(classFile);
```

**Expected Results**:
- ✅ Class names extracted correctly from file paths
- ✅ File sizes reported accurately
- ✅ Timestamps reflect detection time
- ✅ File paths preserved in absolute form

### 2.2.3 Event Routing to Application Layer

**Description**: Test routing of file events through ByteHotApplication.

**Test Steps**:

1. **Event Routing Test**
```bash
mvn -Dtest=org.acmsl.bytehot.application.ByteHotApplicationIntegrationTest#testClassFileEventProcessing test
```

2. **Application Event Processing Test**
```bash
mvn -Dtest=*ByteHotApplicationTest#testProcessClassFileChanged test
```

**Manual Verification**:
```java
// Test event routing
ByteHotApplication app = ByteHotApplication.getInstance();
ClassFileChanged event = createTestEvent();

// Verify processing
app.processClassFileChanged(event);

// Check that event was processed (logs, state changes, etc.)
// This would be verified through mocks or test observers
```

**Expected Results**:
- ✅ Events routed to ByteHotApplication.processClassFileChanged()
- ✅ No exceptions during event processing
- ✅ Event processing completes promptly
- ✅ Application state reflects event processing

### 2.2.4 Event Filtering and Validation

**Description**: Test filtering of relevant events and validation of event data.

**Test Steps**:

1. **File Type Filtering Test**
```bash
# Create files of different types
touch target/test-classes/event-test/TestClass.class
touch target/test-classes/event-test/TestSource.java
touch target/test-classes/event-test/TestData.txt

mvn -Dtest=*FileWatcherAdapterTest#testFileTypeFiltering test
```

2. **Event Validation Test**
```bash
mvn -Dtest=*ClassFileChangedTest#testEventValidation test
```

**Manual Verification**:
```bash
# Monitor different file types
cd target/test-classes/event-test

# Modify different file types
echo "change" >> TestClass.class     # Should generate event
echo "change" >> TestSource.java     # Should be ignored
echo "change" >> TestData.txt        # Should be ignored

# Check event logs
grep "ClassFileChanged" target/test-logs/*.log | wc -l  # Should be 1
```

**Expected Results**:
- ✅ Only .class files generate ClassFileChanged events
- ✅ Non-class files are ignored appropriately
- ✅ Event data passes validation checks
- ✅ Malformed events are rejected gracefully

### 2.2.5 Event Timing and Ordering

**Description**: Test event timing accuracy and ordering guarantees.

**Test Steps**:

1. **Event Timing Test**
```bash
mvn -Dtest=*EventTimingTest test
```

2. **Event Ordering Test**
```bash
mvn -Dtest=*FileWatcherAdapterTest#testEventOrdering test
```

**Manual Verification**:
```bash
# Test rapid sequential changes
cd target/test-classes/event-test

# Make rapid changes to test ordering
for i in {1..5}; do
  echo "Change $i" >> TestClass.class
  sleep 0.1
done

# Check event timestamps are in order
grep "ClassFileChanged.*TestClass" target/test-logs/*.log | \
  cut -d' ' -f3 | sort -c
```

**Expected Results**:
- ✅ Event timestamps reflect actual modification times
- ✅ Events processed in chronological order
- ✅ No duplicate events for single modifications
- ✅ Rapid changes handled correctly

### 2.2.6 Error Handling in Event Processing

**Description**: Test error handling when event processing fails.

**Test Steps**:

1. **Corrupted File Event Test**
```bash
# Create corrupted .class file
echo "Not valid bytecode" > target/test-classes/event-test/Corrupted.class

mvn -Dtest=*EventProcessingErrorTest test
```

2. **Application Error Handling Test**
```bash
mvn -Dtest=*ByteHotApplicationTest#testEventProcessingErrors test
```

**Manual Verification**:
```java
// Test error handling
ClassFileChanged corruptEvent = new ClassFileChanged(
    null,  // Invalid file path
    "",    // Empty class name
    -1,    // Invalid file size
    null   // Invalid timestamp
);

// Should handle gracefully
app.processClassFileChanged(corruptEvent);
// Verify error logged but no crash
```

**Expected Results**:
- ✅ Invalid events logged but don't crash system
- ✅ Processing continues after individual failures
- ✅ Error details captured for debugging
- ✅ System remains stable under error conditions

### 2.2.7 Event Emission and Persistence

**Description**: Test event emission to external systems and optional persistence.

**Test Steps**:

1. **Event Emission Test**
```bash
mvn -Dtest=*EventEmitterAdapterTest#testClassFileEventEmission test
```

2. **Event Persistence Test**
```bash
mvn -Dtest=*FilesystemEventStoreAdapterTest test
```

**Manual Verification**:
```bash
# Check event emission to console
tail -f target/test-logs/events.log &

# Modify file and observe events
echo "Test modification" > target/test-classes/event-test/TestClass.class

# Check console output and log files
grep "ClassFileChanged" target/test-logs/events.log
```

**Expected Results**:
- ✅ Events emitted to configured outputs
- ✅ Event format is consistent and readable
- ✅ Events persist across application restarts
- ✅ Event store maintains event order

## Success Criteria

### Automated Tests
- [ ] Class file change detection tests pass
- [ ] Event metadata extraction tests pass
- [ ] Event routing tests pass
- [ ] Event filtering tests pass
- [ ] Event timing tests pass
- [ ] Error handling tests pass
- [ ] Event emission tests pass

### Manual Verification
- [ ] Real-time detection of .class file changes
- [ ] Correct event metadata extraction
- [ ] Events reach application layer
- [ ] Non-class files properly filtered
- [ ] Event ordering maintained
- [ ] Graceful error handling observed
- [ ] Events appear in outputs/logs

### Performance Criteria
- [ ] Event detection latency < 1 second
- [ ] Event processing < 10ms per event
- [ ] Memory usage stable under load
- [ ] No event loss under normal conditions
- [ ] Backpressure handling for high-frequency changes

## Troubleshooting

### Common Issues

**Issue**: Events not generated for file changes
**Solution**:
- Verify file system supports change notifications
- Check file patterns match exactly
- Ensure watch service is active
- Verify file permissions

**Issue**: Event metadata incorrect
**Solution**:
- Check file path resolution
- Verify timestamp accuracy
- Test file size calculation
- Validate class name extraction

**Issue**: Events lost or duplicated
**Solution**:
- Check event processing thread safety
- Verify event deduplication logic
- Monitor queue sizes and backpressure
- Test with slower file systems

**Issue**: High memory usage from events
**Solution**:
- Check event retention policies
- Verify weak references used appropriately
- Monitor event store growth
- Test garbage collection behavior

### Debug Commands

```bash
# Monitor file events at OS level
sudo strace -e write,close -p $(pgrep java) 2>&1 | grep -i class

# Check event processing threads
jstack $(pgrep java) | grep -A 5 -B 5 "event\|FileWatcher"

# Monitor event emission
tail -f target/test-logs/events.log | grep ClassFileChanged

# Check memory usage patterns
jstat -gc $(pgrep java) 1s

# Test event ordering
find target/test-logs -name "*.log" -exec grep -h "ClassFileChanged" {} \; | sort
```

### Event Processing Configuration

```yaml
# bytehot.yml event processing options
bytehot:
  events:
    buffer-size: 1000
    processing-threads: 2
    retention-period: 24h
    emission-targets:
      - console
      - file:/tmp/bytehot-events.log
```

## Next Steps

Once Phase 2.2 passes completely:
1. Proceed to [Phase 3: JVM Agent Integration](../phase-3-jvm-agent/agent-lifecycle.md)
2. Test event processing under realistic load
3. Validate event processing performance benchmarks
4. Test integration with event store systems
5. Document event format specifications for external consumers