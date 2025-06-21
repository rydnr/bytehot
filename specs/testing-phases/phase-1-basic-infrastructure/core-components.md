# Phase 1.1: Core Components Testing

## Objective
Validate the fundamental building blocks of ByteHot's hexagonal architecture: the Ports registry, configuration management, event emission, and basic domain events.

## Prerequisites
- Maven build environment set up
- Java 17+ available
- ByteHot project compiled successfully

## Test Scenarios

### 1.1.1 Ports Registry Functionality

**Description**: Verify the `Ports` singleton works correctly and can manage port-adapter relationships.

**Test Steps**:

1. **Singleton Instance Test**
```bash
mvn -Dtest=org.acmsl.bytehot.domain.PortsTest test
```

**Manual Verification**:
```java
// Verify singleton behavior
Ports instance1 = Ports.getInstance();
Ports instance2 = Ports.getInstance();
assert instance1 == instance2;
```

2. **Port Injection Test**
```bash
# Run specific tests for port injection
mvn -Dtest=*PortsTest#testPortInjection test
```

**Expected Results**:
- ✅ Singleton returns same instance
- ✅ Port injection works without exceptions
- ✅ Port resolution returns injected adapters

### 1.1.2 Configuration Management

**Description**: Test configuration loading from multiple sources (YAML, environment variables, system properties).

**Test Steps**:

1. **YAML Configuration Test**
```bash
# Create test configuration
cat > test-config.yml << EOF
bytehot:
  watch:
    - path: "target/classes"
      patterns: ["*.class"]
      recursive: true
  port: 8080
EOF

mvn -Dtest=org.acmsl.bytehot.infrastructure.config.ConfigurationAdapterTest test
```

2. **Environment Variable Configuration**
```bash
export BYTEHOT_WATCH_PATHS="target/classes,build/classes"
export BYTEHOT_PORT=8080
mvn -Dtest=*ConfigurationAdapterTest#testEnvironmentVariables test
```

**Expected Results**:
- ✅ YAML configuration loads successfully
- ✅ Environment variables override YAML values
- ✅ Default values used when no configuration present
- ✅ Invalid configuration throws appropriate exceptions

### 1.1.3 Event Emitter Functionality

**Description**: Verify event emission to console and file outputs.

**Test Steps**:

1. **Console Output Test**
```bash
mvn -Dtest=org.acmsl.bytehot.infrastructure.events.EventEmitterAdapterTest test
```

2. **File Output Test**
```bash
# Test file output capability
mvn -Dtest=*EventEmitterAdapterTest#testFileOutput test
```

**Manual Verification**:
```bash
# Check for output files
ls -la /tmp/bytehot-events-*.log
cat /tmp/bytehot-events-*.log | head -5
```

**Expected Results**:
- ✅ Events appear in console output
- ✅ Events written to specified file paths
- ✅ Event format is consistent and readable
- ✅ Multiple events can be emitted without conflicts

### 1.1.4 Domain Event Creation

**Description**: Test instantiation and properties of core domain events.

**Test Steps**:

1. **Event Instantiation Test**
```bash
mvn -Dtest=org.acmsl.bytehot.domain.events.*Test test
```

2. **Event Metadata Test**
```bash
mvn -Dtest=*EventMetadataTest test
```

**Manual Verification**:
```java
// Test basic event properties
ByteHotAttachRequested event = new ByteHotAttachRequested(config, instrumentation);
assert event.getTimestamp() != null;
assert event.getConfiguration() == config;
assert event.getInstrumentation() == instrumentation;
```

**Expected Results**:
- ✅ All domain events can be instantiated
- ✅ Event timestamps are set automatically
- ✅ Event metadata is preserved correctly
- ✅ Events are immutable after creation

## Success Criteria

### Automated Tests
- [ ] All Ports-related tests pass
- [ ] Configuration adapter tests pass
- [ ] Event emitter adapter tests pass
- [ ] All domain event tests pass

### Manual Verification
- [ ] Ports singleton works correctly
- [ ] Configuration loads from all sources
- [ ] Events emit to console and files
- [ ] Domain events contain expected data

### Performance Criteria
- [ ] Port resolution < 1ms
- [ ] Configuration loading < 100ms
- [ ] Event emission < 10ms per event
- [ ] Event creation < 1ms

## Troubleshooting

### Common Issues

**Issue**: `Ports.getInstance()` returns null
**Solution**: Ensure proper class loading. Check for static initialization blocks.

**Issue**: Configuration not loading from YAML
**Solution**: 
- Verify YAML syntax with: `java -jar snakeyaml-validator.jar test-config.yml`
- Check file path and permissions
- Ensure YAML dependencies are in classpath

**Issue**: Events not appearing in output
**Solution**:
- Check EventEmitterAdapter is properly injected in Ports
- Verify output file permissions
- Check console log level settings

**Issue**: Domain events missing properties
**Solution**:
- Verify Lombok annotations are being processed
- Check constructor parameters are all provided
- Ensure immutability constraints are met

### Debug Commands

```bash
# Enable debug logging
export BYTEHOT_LOG_LEVEL=DEBUG
mvn test

# Check classpath for dependencies
mvn dependency:tree | grep -E "(snakeyaml|jackson|lombok)"

# Verify test resources
ls -la src/test/resources/
cat src/test/resources/test-config.yml

# Check port injection status
mvn -Dtest=*PortsTest test -X
```

## Next Steps

Once Phase 1.1 passes completely:
1. Proceed to [Adapter Discovery](adapter-discovery.md)
2. Document any issues found in [journal.org](../../../journal.org)
3. Update configuration if needed for your environment