# Phase 1.2: Adapter Discovery Testing

## Objective
Validate ByteHot's dynamic adapter discovery mechanism, ensuring all infrastructure adapters are properly found, instantiated, and injected into the ports registry.

## Prerequisites
- Phase 1.1 (Core Components) completed successfully
- Built-in adapters compiled and available in classpath
- Understanding of hexagonal architecture ports/adapters pattern

## Test Scenarios

### 1.2.1 Built-in Adapter Injection

**Description**: Verify that all core infrastructure adapters are discovered and injected automatically.

**Test Steps**:

1. **Application Initialization Test**
```bash
mvn -Dtest=org.acmsl.bytehot.application.ByteHotApplicationIntegrationTest test
```

2. **Individual Adapter Injection Test**
```bash
# Test each adapter individually
mvn -Dtest=*ConfigurationAdapterTest test
mvn -Dtest=*FileWatcherAdapterTest test
mvn -Dtest=*InstrumentationAdapterTest test
mvn -Dtest=*EventEmitterAdapterTest test
```

**Manual Verification**:
```java
// Verify adapter discovery through application
ByteHotApplication.initialize(mockInstrumentation);

Ports ports = Ports.getInstance();
ConfigurationPort configPort = ports.resolve(ConfigurationPort.class);
FileWatcherPort watcherPort = ports.resolve(FileWatcherPort.class);
InstrumentationPort instrPort = ports.resolve(InstrumentationPort.class);
EventEmitterPort emitterPort = ports.resolve(EventEmitterPort.class);

assert configPort != null;
assert watcherPort != null;
assert instrPort != null;
assert emitterPort != null;
```

**Expected Results**:
- ✅ ConfigurationAdapter injected for ConfigurationPort
- ✅ FileWatcherAdapter injected for FileWatcherPort
- ✅ InstrumentationAdapter injected for InstrumentationPort
- ✅ EventEmitterAdapter injected for EventEmitterPort
- ✅ No exceptions during injection process

### 1.2.2 Classpath Scanning

**Description**: Test the classpath scanning mechanism for discovering additional adapters.

**Test Steps**:

1. **Package Scanning Test**
```bash
# Run tests that verify package scanning
mvn -Dtest=*ByteHotApplicationTest#testDiscoverAdaptersFromClasspath test
```

2. **Adapter Validation Test**
```bash
mvn -Dtest=*ByteHotApplicationTest#testIsValidAdapter test
```

**Manual Verification**:
```bash
# Check scanning output
java -cp target/classes:target/test-classes \
  -Djava.util.logging.level=FINE \
  org.acmsl.bytehot.application.ByteHotApplication \
  --scan-only
```

**Expected Results**:
- ✅ All infrastructure packages scanned successfully
- ✅ Valid adapters identified correctly
- ✅ Invalid classes filtered out appropriately
- ✅ No false positives in adapter detection

### 1.2.3 Adapter Interface Compliance

**Description**: Verify that all discovered adapters implement the required interfaces correctly.

**Test Steps**:

1. **Interface Compliance Test**
```bash
mvn -Dtest=*AdapterComplianceTest test
```

2. **Port Interface Resolution Test**
```bash
mvn -Dtest=*ByteHotApplicationTest#testGetPortInterface test
```

**Manual Verification**:
```java
// Test each adapter's interface compliance
List<Class<?>> adapterClasses = findAdapterClasses();
for (Class<?> adapterClass : adapterClasses) {
    assert Adapter.class.isAssignableFrom(adapterClass);
    Class<? extends Port> portInterface = getPortInterface(adapterClass);
    assert Port.class.isAssignableFrom(portInterface);
}
```

**Expected Results**:
- ✅ All adapters implement `Adapter<T extends Port>` interface
- ✅ Port interface resolution works for all adapters
- ✅ Adapter instantiation succeeds without exceptions
- ✅ `adapts()` method returns correct port interface

### 1.2.4 Dynamic Injection Process

**Description**: Test the complete dynamic injection process from discovery to availability.

**Test Steps**:

1. **Full Injection Cycle Test**
```bash
mvn -Dtest=*ByteHotApplicationTest#testDiscoverAndInjectAdapters test
```

2. **Port Resolution After Injection Test**
```bash
mvn -Dtest=*PortsTest#testResolveAfterInjection test
```

**Manual Verification**:
```java
// Test complete injection cycle
Ports ports = Ports.getInstance();
// Clear any existing ports
ports.clear(); 

// Perform discovery and injection
ByteHotApplication.discoverAndInjectAdapters(mockInstrumentation);

// Verify all expected ports are available
assert ports.resolve(ConfigurationPort.class) != null;
assert ports.resolve(FileWatcherPort.class) != null;
assert ports.resolve(InstrumentationPort.class) != null;
assert ports.resolve(EventEmitterPort.class) != null;
```

**Expected Results**:
- ✅ Discovery finds all expected adapters
- ✅ Injection completes without errors
- ✅ All ports resolvable after injection
- ✅ Thread safety during concurrent access

### 1.2.5 Error Handling in Discovery

**Description**: Test error handling when adapters cannot be loaded or injected.

**Test Steps**:

1. **Missing Dependency Test**
```bash
# Test with missing dependencies
mvn -Dtest=*AdapterDiscoveryErrorTest test
```

2. **Invalid Adapter Test**
```bash
mvn -Dtest=*ByteHotApplicationTest#testInvalidAdapterHandling test
```

**Manual Verification**:
```bash
# Test with corrupted classpath
export CORRUPTED_CP="invalid-path.jar:$CLASSPATH"
java -cp "$CORRUPTED_CP" -Dtest=*AdapterDiscoveryTest test
```

**Expected Results**:
- ✅ Graceful handling of missing dependencies
- ✅ Invalid adapters skipped without crashing
- ✅ Appropriate error messages logged
- ✅ System continues with available adapters

## Success Criteria

### Automated Tests
- [ ] ByteHotApplicationIntegrationTest passes
- [ ] All individual adapter tests pass
- [ ] Adapter discovery tests pass
- [ ] Port resolution tests pass
- [ ] Error handling tests pass

### Manual Verification
- [ ] All built-in adapters discovered and injected
- [ ] Classpath scanning works correctly
- [ ] Port interfaces resolved properly
- [ ] Graceful error handling observed

### Performance Criteria
- [ ] Adapter discovery < 500ms
- [ ] Adapter instantiation < 100ms per adapter
- [ ] Port resolution < 5ms after injection
- [ ] Memory usage reasonable (< 50MB for discovery)

## Troubleshooting

### Common Issues

**Issue**: Adapters not discovered
**Solution**: 
- Check classpath includes infrastructure packages
- Verify adapter classes extend `Adapter<T>` interface
- Ensure adapters have default constructors

**Issue**: Port resolution returns null
**Solution**:
- Verify adapter injection completed successfully
- Check adapter's `adapts()` method returns correct interface
- Ensure no type mismatches in generic parameters

**Issue**: ClassNotFoundException during discovery
**Solution**:
- Check all required dependencies in classpath
- Verify package structure matches expectations
- Use `mvn dependency:tree` to check dependencies

**Issue**: Concurrent access exceptions
**Solution**:
- Ensure `Ports` class thread safety
- Check for race conditions in adapter injection
- Verify singleton pattern implementation

### Debug Commands

```bash
# Enable verbose adapter discovery logging
export BYTEHOT_DISCOVERY_DEBUG=true
mvn test -Dtest=*AdapterDiscoveryTest

# Check adapter classes in classpath
find target/classes -name "*Adapter.class" -type f

# Verify adapter interface compliance
javap -cp target/classes org.acmsl.bytehot.infrastructure.*.Adapter

# Test discovery in isolation
mvn exec:java -Dexec.mainClass="org.acmsl.bytehot.application.ByteHotApplication" \
  -Dexec.args="--discover-only"

# Check for dependency conflicts
mvn dependency:analyze
```

### Logging Configuration

```properties
# Add to test resources/logging.properties
org.acmsl.bytehot.application.level=FINE
org.acmsl.bytehot.domain.Ports.level=FINE
java.util.logging.ConsoleHandler.level=FINE
```

## Next Steps

Once Phase 1.2 passes completely:
1. Proceed to [Phase 2: File System Monitoring](../phase-2-file-monitoring/file-watcher.md)
2. Document adapter discovery performance in [journal.org](../../../journal.org)
3. Consider creating custom adapters to test extensibility
4. Verify adapter discovery works in different deployment scenarios (JAR, WAR, etc.)