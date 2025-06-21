# Phase 3.1: Agent Lifecycle Testing

## Objective
Validate ByteHot's JVM agent lifecycle management, including proper agent attachment (both at startup and runtime), initialization, instrumentation setup, and graceful shutdown.

## Prerequisites
- Phase 2 (File System Monitoring) completed successfully
- Understanding of JVM agent architecture
- Knowledge of Java Instrumentation API
- Agent JAR built with proper manifest

## Test Scenarios

### 3.1.1 Agent JAR Manifest Validation

**Description**: Verify the agent JAR contains correct manifest entries for JVM agent functionality.

**Test Steps**:

1. **JAR Manifest Test**
```bash
# Build agent JAR
mvn clean package

# Check manifest entries
jar -tf target/bytehot-*-agent.jar | head -5
unzip -p target/bytehot-*-agent.jar META-INF/MANIFEST.MF
```

2. **Manifest Content Validation**
```bash
mvn -Dtest=*AgentJarBuilderTest test
```

**Manual Verification**:
```bash
# Verify required manifest entries
unzip -p target/bytehot-*-agent.jar META-INF/MANIFEST.MF | grep -E "(Premain-Class|Agent-Class|Can-Redefine-Classes|Can-Retransform-Classes)"

# Expected output:
# Premain-Class: org.acmsl.bytehot.infrastructure.agent.ByteHotAgent
# Agent-Class: org.acmsl.bytehot.infrastructure.agent.ByteHotAgent
# Can-Redefine-Classes: true
# Can-Retransform-Classes: true
```

**Expected Results**:
- ✅ Premain-Class points to ByteHotAgent
- ✅ Agent-Class points to ByteHotAgent
- ✅ Can-Redefine-Classes is true
- ✅ Can-Retransform-Classes is true
- ✅ JAR includes all required dependencies

### 3.1.2 Agent Startup (premain) Testing

**Description**: Test agent initialization when attached at JVM startup using -javaagent.

**Test Steps**:

1. **Basic Premain Test**
```bash
# Create test configuration
cat > test-agent-config.yml << EOF
bytehot:
  watch:
    - path: "target/test-classes"
      patterns: ["*.class"]
      recursive: true
  port: 8080
EOF

# Test agent startup
mvn -Dtest=org.acmsl.bytehot.infrastructure.agent.ByteHotAgentTest test
```

2. **Integration Test with Real JVM**
```bash
mvn -Dtest=org.acmsl.bytehot.integration.ByteHotAgentIntegrationTest test
```

**Manual Verification**:
```bash
# Test with actual JVM startup
java -javaagent:target/bytehot-*-agent.jar \
     -Dbhconfig=test-agent-config.yml \
     -cp target/test-classes \
     -Djava.util.logging.level=FINE \
     org.acmsl.bytehot.TestApplication

# Check for initialization messages
# Expected: "ByteHot agent initialized successfully"
```

**Expected Results**:
- ✅ Agent premain method executes without errors
- ✅ InstrumentationProvider initialized with JVM instrumentation
- ✅ ByteHotApplication initialization completes
- ✅ Agent attachment event processed successfully
- ✅ Success message printed to stdout

### 3.1.3 Runtime Agent Attachment (agentmain) Testing

**Description**: Test agent attachment to running JVM using Java Attach API.

**Test Steps**:

1. **Runtime Attachment Test**
```bash
mvn -Dtest=*ByteHotAgentTest#testAgentmain test
```

2. **Dynamic Attachment Integration Test**
```bash
mvn -Dtest=*ByteHotAgentIntegrationTest#testRuntimeAttachment test
```

**Manual Verification**:
```bash
# Start a simple Java application
java -cp target/test-classes TestApplication &
APP_PID=$!

# Attach agent to running process
java -cp "target/bytehot-*-agent.jar:$JAVA_HOME/lib/tools.jar" \
     com.sun.tools.attach.VirtualMachine $APP_PID target/bytehot-*-agent.jar

# Check attachment success
kill $APP_PID
```

**Expected Results**:
- ✅ Agent attaches to running JVM successfully
- ✅ Same initialization as startup attachment
- ✅ No disruption to running application
- ✅ Agent functionality available immediately

### 3.1.4 Instrumentation Provider Setup

**Description**: Validate proper setup of InstrumentationProvider singleton.

**Test Steps**:

1. **InstrumentationProvider Test**
```bash
mvn -Dtest=org.acmsl.bytehot.domain.InstrumentationProviderTest test
```

2. **Instrumentation Capabilities Test**
```bash
mvn -Dtest=*InstrumentationProviderTest#testCapabilities test
```

**Manual Verification**:
```java
// Test InstrumentationProvider setup
// (This would be done within agent context)
Instrumentation inst = InstrumentationProvider.getInstrumentation();
assert inst != null;
assert inst.isRedefineClassesSupported();
assert inst.isRetransformClassesSupported();
assert inst.isModifiableClass(String.class);
```

**Expected Results**:
- ✅ InstrumentationProvider singleton initialized
- ✅ Instrumentation instance available globally
- ✅ Class redefinition capabilities confirmed
- ✅ Retransformation capabilities confirmed
- ✅ Thread-safe access to instrumentation

### 3.1.5 Application Initialization

**Description**: Test ByteHotApplication initialization during agent startup.

**Test Steps**:

1. **Application Bootstrap Test**
```bash
mvn -Dtest=*ByteHotApplicationTest#testInitialization test
```

2. **Adapter Injection During Init Test**
```bash
mvn -Dtest=*ByteHotApplicationIntegrationTest#testInitializationAdapterInjection test
```

**Manual Verification**:
```java
// Test application initialization state
assert ByteHotApplication.adaptersInitialized;

Ports ports = Ports.getInstance();
assert ports.resolve(ConfigurationPort.class) != null;
assert ports.resolve(FileWatcherPort.class) != null;
assert ports.resolve(InstrumentationPort.class) != null;
assert ports.resolve(EventEmitterPort.class) != null;
```

**Expected Results**:
- ✅ ByteHotApplication.initialize() completes
- ✅ All built-in adapters injected
- ✅ Ports registry populated correctly
- ✅ Application ready for domain events

### 3.1.6 Configuration Loading During Startup

**Description**: Test configuration loading during agent initialization.

**Test Steps**:

1. **Configuration Loading Test**
```bash
# Test with different config sources
export BYTEHOT_PORT=9090
java -javaagent:target/bytehot-*-agent.jar \
     -Dbhconfig=test-agent-config.yml \
     -cp target/test-classes TestApplication
```

2. **Configuration Error Handling Test**
```bash
# Test with missing configuration
java -javaagent:target/bytehot-*-agent.jar \
     -cp target/test-classes TestApplication

# Should fail gracefully with clear error message
```

**Manual Verification**:
```bash
# Test various configuration scenarios
# 1. Valid YAML file
# 2. Environment variables only
# 3. System properties
# 4. Missing configuration (should fail)
# 5. Invalid YAML syntax (should fail)
```

**Expected Results**:
- ✅ Configuration loads from specified sources
- ✅ Environment variables override file settings
- ✅ Missing configuration causes clear error
- ✅ Invalid configuration handled gracefully
- ✅ Default values used appropriately

### 3.1.7 Agent Error Handling

**Description**: Test error handling during agent initialization and operation.

**Test Steps**:

1. **Initialization Error Test**
```bash
mvn -Dtest=*ByteHotAgentTest#testInitializationErrors test
```

2. **Recovery from Errors Test**
```bash
mvn -Dtest=*ByteHotAgentTest#testErrorRecovery test
```

**Manual Verification**:
```bash
# Test error scenarios
# 1. Insufficient permissions
# 2. Missing dependencies
# 3. Invalid configuration
# 4. JVM incompatibility

# Each should fail gracefully without crashing JVM
```

**Expected Results**:
- ✅ Initialization errors logged clearly
- ✅ JVM continues running despite agent errors
- ✅ Partial functionality available when possible
- ✅ Error reporting includes actionable information

## Success Criteria

### Automated Tests
- [ ] Agent JAR manifest tests pass
- [ ] ByteHotAgentTest passes
- [ ] InstrumentationProviderTest passes
- [ ] ByteHotAgentIntegrationTest passes
- [ ] Application initialization tests pass
- [ ] Configuration loading tests pass
- [ ] Error handling tests pass

### Manual Verification
- [ ] Agent attaches successfully at startup
- [ ] Agent attaches successfully at runtime
- [ ] InstrumentationProvider works correctly
- [ ] Application initializes completely
- [ ] Configuration loads from all sources
- [ ] Errors handled gracefully

### Performance Criteria
- [ ] Agent startup time < 500ms
- [ ] Memory overhead < 20MB
- [ ] No significant JVM startup delay
- [ ] Configuration loading < 100ms
- [ ] Thread overhead minimal

## Troubleshooting

### Common Issues

**Issue**: Agent fails to load
**Solution**:
- Check JAR file permissions and location
- Verify manifest entries are correct
- Ensure compatible JVM version
- Check for conflicting agents

**Issue**: InstrumentationProvider returns null
**Solution**:
- Verify agent premain/agentmain called
- Check for static initialization issues
- Ensure proper instrumentation passed to agent
- Test singleton pattern implementation

**Issue**: Application initialization fails
**Solution**:
- Check adapter dependency availability
- Verify configuration file location and syntax
- Ensure proper classpath setup
- Check for port binding conflicts

**Issue**: Configuration not loading
**Solution**:
- Verify file paths are absolute or relative to working directory
- Check file permissions and format
- Test environment variable names and values
- Validate YAML syntax

### Debug Commands

```bash
# Enable JVM agent debugging
export JAVA_TOOL_OPTIONS="-XX:+PrintGCDetails -XX:+TraceClassLoading"

# Check agent loading
java -verbose:class -javaagent:target/bytehot-*-agent.jar \
     -cp target/test-classes TestApplication 2>&1 | grep ByteHot

# Test JAR integrity
jar -tvf target/bytehot-*-agent.jar | grep -E "(Agent|MANIFEST)"

# Check instrumentation capabilities
java -javaagent:target/bytehot-*-agent.jar \
     -Djava.util.logging.level=FINE \
     -cp target/test-classes InstrumentationTest

# Monitor agent memory usage
jcmd $(pgrep java) VM.memory
```

### Configuration for Testing

```yaml
# test-agent-config.yml
bytehot:
  debug: true
  watch:
    - path: "target/test-classes"
      patterns: ["*.class"]
      recursive: true
  agent:
    initialization-timeout: 30s
    error-handling: lenient
```

## Next Steps

Once Phase 3.1 passes completely:
1. Proceed to [Domain Events](domain-events.md)
2. Test agent with production applications
3. Benchmark agent performance impact
4. Test compatibility with other agents
5. Document agent configuration best practices