# ByteHot Getting Started Guide

Welcome to ByteHot! This comprehensive guide will help you get started with ByteHot, a revolutionary JVM agent that enables bytecode hot-swapping at runtime, allowing you to modify your Java code without restarting your application.

## Table of Contents

1. [What is ByteHot?](#what-is-bytehot)
2. [Prerequisites](#prerequisites)
3. [Installation](#installation)
4. [Quick Start](#quick-start)
5. [Configuration](#configuration)
6. [Basic Usage](#basic-usage)
7. [Advanced Features](#advanced-features)
8. [Troubleshooting](#troubleshooting)
9. [Best Practices](#best-practices)
10. [Examples](#examples)

## What is ByteHot?

ByteHot is a JVM agent that revolutionizes Java development by enabling **hot-swapping** - the ability to modify your Java classes at runtime without restarting your application. This dramatically improves development productivity by eliminating the time-consuming restart cycle.

### Key Features

- 🔥 **Real-time Hot-swapping**: Modify classes while your application runs
- 📁 **Automatic File Watching**: Detects changes to `.class` files automatically  
- 🛡️ **Safe Bytecode Validation**: Ensures only compatible changes are applied
- 📊 **Event-driven Architecture**: Complete audit trail of all changes
- 👤 **User-aware Operations**: Tracks who made what changes when
- 🧪 **Revolutionary Testing**: Event-driven testing framework for reliable tests
- 🔍 **Flow Detection**: Automatically discovers and documents your development patterns
- 🐛 **Advanced Bug Reporting**: Captures complete event context for reproducible bug reports

### How It Works

ByteHot uses a sophisticated event-driven architecture based on Domain-Driven Design (DDD) and Hexagonal Architecture principles:

1. **File System Monitoring**: Watches your compiled `.class` files for changes
2. **Bytecode Analysis**: Validates that changes are compatible with hot-swapping
3. **Hot-swap Operations**: Uses JVM instrumentation to redefine classes at runtime
4. **Instance Management**: Updates existing object instances with new behavior
5. **Event Sourcing**: Records all operations as events for complete auditability

## Prerequisites

### System Requirements

- **Java 17+**: ByteHot requires Java 17 or later
- **Maven 3.6+**: For building from source
- **Git**: For version control (if building from source)

### Development Environment

- **IDE**: Any Java IDE (IntelliJ IDEA, Eclipse, VS Code with Java extensions)
- **Build Tool**: Maven or Gradle project structure
- **Operating System**: Linux, macOS, or Windows

### Supported Frameworks

ByteHot works with any Java application, including:
- **Spring Boot** applications
- **Jakarta EE** applications  
- **Standalone** Java applications
- **Microservices** architectures

## Installation

### Option 1: Download Pre-built JAR (Recommended)

1. Download the latest ByteHot agent JAR from the [releases page](https://github.com/rydnr/bytehot/releases)
2. Save it to a convenient location (e.g., `~/tools/bytehot-agent.jar`)

### Option 2: Build from Source

```bash
# Clone the repository
git clone https://github.com/rydnr/bytehot.git
cd bytehot

# Build the project
mvn clean package

# The agent JAR will be at: bytehot/target/bytehot-agent.jar
```

## Quick Start

### 1. Create a Simple Java Application

```java
// src/main/java/com/example/HelloWorld.java
package com.example;

public class HelloWorld {
    public static void main(String[] args) throws InterruptedException {
        HelloWorld app = new HelloWorld();
        while (true) {
            app.sayHello();
            Thread.sleep(2000);
        }
    }
    
    public void sayHello() {
        System.out.println("Hello, World! Current time: " + System.currentTimeMillis());
    }
}
```

### 2. Compile Your Application

```bash
# Compile your Java application
javac -d target/classes src/main/java/com/example/HelloWorld.java
```

### 3. Run with ByteHot Agent

```bash
# Run with ByteHot agent attached
java -javaagent:path/to/bytehot-agent.jar \
     -Dbytehot.watch.paths=target/classes \
     -Dbytehot.watch.patterns=**/*.class \
     -cp target/classes \
     com.example.HelloWorld
```

### 4. Make Live Changes

1. **Keep the application running** from step 3
2. **Modify the `sayHello()` method**:

```java
public void sayHello() {
    System.out.println("🔥 Hot-swapped! Hello from ByteHot! Time: " + System.currentTimeMillis());
}
```

3. **Recompile** (the agent will detect the change automatically):

```bash
javac -d target/classes src/main/java/com/example/HelloWorld.java
```

4. **Watch the magic happen** - your running application will start using the new method without restarting!

## Configuration

ByteHot can be configured using system properties or environment variables.

### System Properties

| Property | Description | Default | Example |
|----------|-------------|---------|---------|
| `bytehot.watch.paths` | Directories to watch for changes | `target/classes` | `target/classes,build/classes` |
| `bytehot.watch.patterns` | File patterns to monitor | `**/*.class` | `**/*.class,**/*.jar` |
| `bytehot.user.id` | Explicit user identification | Auto-detected | `john.doe@company.com` |
| `bytehot.session.id` | Session identifier | Auto-generated | `dev-session-123` |
| `bytehot.logging.level` | Logging verbosity | `INFO` | `DEBUG` |
| `bytehot.validation.strict` | Strict bytecode validation | `true` | `false` |

### Environment Variables

```bash
export BYTEHOT_WATCH_PATHS="target/classes,build/classes"
export BYTEHOT_WATCH_PATTERNS="**/*.class"
export BYTEHOT_USER_ID="developer@company.com"
export BYTEHOT_LOGGING_LEVEL="DEBUG"
```

### Configuration Examples

#### Spring Boot Application

```bash
java -javaagent:bytehot-agent.jar \
     -Dbytehot.watch.paths=target/classes \
     -Dbytehot.user.id=$(git config user.email) \
     -jar target/my-spring-app.jar
```

#### Multi-module Maven Project

```bash
java -javaagent:bytehot-agent.jar \
     -Dbytehot.watch.paths=module1/target/classes,module2/target/classes \
     -Dbytehot.watch.patterns=**/*.class \
     -cp "module1/target/classes:module2/target/classes" \
     com.example.MainApplication
```

#### Gradle Project

```bash
java -javaagent:bytehot-agent.jar \
     -Dbytehot.watch.paths=build/classes/java/main \
     -Dbytehot.watch.patterns=**/*.class \
     -cp build/classes/java/main \
     com.example.Application
```

## Basic Usage

### File System Monitoring

ByteHot automatically monitors specified directories for `.class` file changes:

```bash
# Monitor single directory
-Dbytehot.watch.paths=target/classes

# Monitor multiple directories
-Dbytehot.watch.paths=target/classes,target/test-classes

# Custom patterns
-Dbytehot.watch.patterns=**/*.class,**/service/**/*.class
```

### User Identification

ByteHot automatically identifies users through multiple sources:

1. **Explicit configuration**: `-Dbytehot.user.id=john.doe@company.com`
2. **Git configuration**: Uses `git config user.email`
3. **System user**: Falls back to system username + hostname
4. **Anonymous**: Generates anonymous identifier if needed

### Session Management

ByteHot creates user sessions to track development activities:

```bash
# Explicit session ID
-Dbytehot.session.id=feature-development-session

# Auto-generated session (default)
# Sessions include: user, timestamp, environment info
```

### Hot-swap Lifecycle

Understanding the hot-swap process helps you work effectively with ByteHot:

1. **File Change Detection**: ByteHot detects `.class` file modifications
2. **Bytecode Analysis**: Validates the changes are hot-swap compatible
3. **Class Redefinition**: Uses JVM instrumentation to update the class
4. **Instance Updates**: Updates existing object instances with new behavior
5. **Event Recording**: Records the entire operation as events for audit trails

### Compatible Changes

ByteHot supports these types of changes:

✅ **Supported Changes**:
- Method body modifications
- Adding new methods
- Adding new fields (with caution)
- Changing method implementations
- Adding static methods
- Modifying static initializers

❌ **Unsupported Changes**:
- Changing method signatures
- Removing methods
- Changing class hierarchy
- Modifying constructor signatures
- Adding/removing interfaces

## Advanced Features

### Event-Driven Testing

ByteHot includes a revolutionary testing framework that uses events:

```java
@Test
void shouldUpdateInstancesAfterHotSwap() {
    // Given: System state from real events
    given()
        .event(new ByteHotAgentAttached("agent-123"))
        .event(new ClassFileChanged("/path/MyClass.class"));
    
    // When: The event we want to test
    when()
        .event(new InstanceUpdateRequested("MyClass", 2));
    
    // Then: Expected resulting events
    then()
        .expectEvent(InstancesUpdated.class)
        .withUpdatedCount(2)
        .withSuccessful(true);
}
```

### Flow Detection

ByteHot automatically discovers your development patterns:

```bash
# Enable flow detection
-Dbytehot.flow.detection.enabled=true

# Configure pattern analysis
-Dbytehot.flow.analysis.window=PT30M  # 30 minutes
-Dbytehot.flow.confidence.threshold=0.8
```

### User Analytics

Track your development productivity:

```java
// ByteHot automatically tracks:
// - Hot-swap success rates
// - Time saved by avoiding restarts  
// - Most frequently modified classes
// - Development patterns and workflows
```

### Bug Reporting with Event Context

When errors occur, ByteHot captures complete context:

```java
try {
    // Your code
} catch (Exception e) {
    // ByteHot automatically captures:
    // - Complete event history leading to the error
    // - User context and session information
    // - System state snapshot
    // - Reproducible test case generation
}
```

## Troubleshooting

### Common Issues

#### 1. Agent Not Attaching

**Problem**: ByteHot agent doesn't seem to be working

**Solutions**:
```bash
# Verify agent path is correct
ls -la path/to/bytehot-agent.jar

# Check Java version
java -version  # Should be 17+

# Enable debug logging
-Dbytehot.logging.level=DEBUG
```

#### 2. Classes Not Hot-swapping

**Problem**: Changes aren't being applied

**Solutions**:
```bash
# Verify watch paths are correct
-Dbytehot.watch.paths=target/classes  # Check this matches your build output

# Check file permissions
ls -la target/classes/com/example/

# Verify bytecode compatibility
# Make sure you're only changing method bodies, not signatures
```

#### 3. File System Events Not Detected

**Problem**: File changes aren't being detected

**Solutions**:
```bash
# Check if patterns match your files
-Dbytehot.watch.patterns=**/*.class

# Verify directory exists and is writable
test -d target/classes && test -w target/classes

# Check for filesystem-specific issues (Docker, network drives)
# Use absolute paths if needed
-Dbytehot.watch.paths=/absolute/path/to/classes
```

#### 4. Hot-swap Rejected

**Problem**: ByteHot rejects your changes

**Diagnosis**:
- Check logs for validation errors
- Ensure you're only modifying method bodies
- Avoid changing method signatures or class structure

**Solutions**:
```java
// Good: Changing method implementation
public void process() {
    // New implementation
    logger.info("Updated processing logic");
}

// Bad: Changing method signature
// public void process(String param) { ... }  // Don't do this
```

### Debug Mode

Enable comprehensive debugging:

```bash
java -javaagent:bytehot-agent.jar \
     -Dbytehot.logging.level=DEBUG \
     -Dbytehot.validation.verbose=true \
     -Dbytehot.events.trace=true \
     -cp target/classes \
     com.example.Application
```

### Log Analysis

ByteHot provides detailed logs:

```
[ByteHot] Agent attached successfully
[ByteHot] Watching: target/classes
[ByteHot] User identified: john.doe@company.com
[ByteHot] Session started: session-abc123
[ByteHot] File change detected: target/classes/com/example/Service.class
[ByteHot] Bytecode validation: PASSED
[ByteHot] Hot-swap executed successfully
[ByteHot] Instances updated: 3 objects
```

## Best Practices

### 1. Development Workflow

```bash
# Recommended development setup
export BYTEHOT_AGENT="path/to/bytehot-agent.jar"
export BYTEHOT_WATCH="target/classes"

# Create an alias for easy startup
alias run-with-bytehot='java -javaagent:$BYTEHOT_AGENT -Dbytehot.watch.paths=$BYTEHOT_WATCH'

# Use it in your projects
run-with-bytehot -cp target/classes com.example.Application
```

### 2. IDE Integration

#### IntelliJ IDEA

1. Go to **Run/Debug Configurations**
2. Add VM options:
```
-javaagent:path/to/bytehot-agent.jar
-Dbytehot.watch.paths=target/classes
-Dbytehot.user.id=${USER}@${DOMAIN}
```
3. Enable **Build project automatically**
4. Enable **Compiler > Build project automatically**

#### Eclipse

1. Right-click project → **Run As** → **Run Configurations**
2. Go to **Arguments** tab
3. Add to **VM arguments**:
```
-javaagent:path/to/bytehot-agent.jar
-Dbytehot.watch.paths=target/classes
```

#### VS Code

Add to `.vscode/launch.json`:

```json
{
    "type": "java",
    "request": "launch",
    "mainClass": "com.example.Application",
    "vmArgs": [
        "-javaagent:path/to/bytehot-agent.jar",
        "-Dbytehot.watch.paths=target/classes"
    ]
}
```

### 3. Team Development

```bash
# Share ByteHot configuration in your project
# Create scripts/run-dev.sh
#!/bin/bash
BYTEHOT_AGENT="tools/bytehot-agent.jar"
MAIN_CLASS="com.example.Application"

java -javaagent:$BYTEHOT_AGENT \
     -Dbytehot.watch.paths=target/classes \
     -Dbytehot.user.id=$(git config user.email) \
     -cp target/classes \
     $MAIN_CLASS
```

### 4. Testing Strategy

```java
// Use ByteHot's event-driven testing for better tests
public class MyServiceTest extends EventDrivenTestSupport {
    
    @Test
    void shouldHandleUserRequest() {
        given()
            .event(new UserRegistered(userId))
            .event(new ServiceInitialized());
        
        when()
            .event(new ProcessUserRequest(userId, request));
        
        then()
            .expectEvent(RequestProcessed.class)
            .withUserId(userId)
            .withSuccessful(true);
    }
}
```

### 5. Production Considerations

**⚠️ Important**: ByteHot is designed for development environments.

For production:
- **Never** use ByteHot in production environments
- Use proper deployment processes for production changes
- Consider using ByteHot for **staging/testing** environments only

### 6. Performance Tips

```bash
# Optimize file watching for large projects
-Dbytehot.watch.patterns=**/service/**/*.class,**/controller/**/*.class

# Exclude test classes from watching
-Dbytehot.watch.patterns=**/*.class,!**/*Test.class

# Tune validation settings
-Dbytehot.validation.timeout=5000  # 5 seconds
-Dbytehot.hotswap.retries=3
```

## Examples

### Example 1: Spring Boot REST API

```java
// src/main/java/com/example/UserController.java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        // Initial implementation
        return ResponseEntity.ok(userService.findById(id));
    }
}
```

**Run with ByteHot**:
```bash
java -javaagent:bytehot-agent.jar \
     -Dbytehot.watch.paths=target/classes \
     -jar target/my-spring-app.jar
```

**Hot-swap the endpoint**:
```java
@GetMapping("/{id}")
public ResponseEntity<User> getUser(@PathVariable Long id) {
    // Enhanced implementation with caching
    User user = cacheService.get(id);
    if (user == null) {
        user = userService.findById(id);
        cacheService.put(id, user);
    }
    return ResponseEntity.ok(user);
}
```

Recompile and watch your running API immediately use the new caching logic!

### Example 2: Background Service

```java
// com/example/DataProcessor.java
@Component
public class DataProcessor {
    
    @Scheduled(fixedRate = 5000)
    public void processData() {
        // Original processing logic
        List<Data> data = dataRepository.findUnprocessed();
        data.forEach(this::processItem);
    }
    
    private void processItem(Data item) {
        // Simple processing
        item.setStatus("PROCESSED");
        dataRepository.save(item);
    }
}
```

**Hot-swap to add error handling**:
```java
private void processItem(Data item) {
    try {
        // Enhanced processing with validation
        if (item.isValid()) {
            item.setStatus("PROCESSED");
            item.setProcessedAt(Instant.now());
        } else {
            item.setStatus("VALIDATION_FAILED");
            item.setErrorMessage("Invalid data format");
        }
        dataRepository.save(item);
    } catch (Exception e) {
        logger.error("Processing failed for item {}", item.getId(), e);
        item.setStatus("ERROR");
        item.setErrorMessage(e.getMessage());
        dataRepository.save(item);
    }
}
```

### Example 3: Event-Driven Testing

```java
public class HotSwapIntegrationTest extends EventDrivenTestSupport {
    
    @Test
    void shouldCompleteFullHotSwapFlow() {
        // Given: ByteHot is running and monitoring files
        given()
            .event(new ByteHotAgentAttached("test-agent"))
            .event(new WatchPathConfigured("target/test-classes", "**/*.class"))
            .event(new UserSessionStarted(UserId.of("test-user")));
        
        // When: A class file is modified
        when()
            .event(new ClassFileChanged("target/test-classes/TestService.class", "TestService", 2048));
        
        // Then: Complete hot-swap flow should execute
        then()
            .expectSequence()
                .then(ClassMetadataExtracted.class)
                .then(BytecodeValidated.class)
                .then(HotSwapRequested.class)
                .then(ClassRedefinitionSucceeded.class)
                .then(InstancesUpdated.class)
            .inOrder()
            .expectEvent(UserStatisticsUpdated.class)
                .withSuccessfulHotSwaps(1);
    }
}
```

## Next Steps

Now that you're familiar with ByteHot basics:

1. **Try the Quick Start** example with your own application
2. **Explore Advanced Features** like flow detection and user analytics
3. **Integrate with your IDE** for seamless development
4. **Read the Architecture Documentation** to understand ByteHot's design
5. **Join the Community** and contribute to ByteHot's development

### Additional Resources

- 📚 **[Architecture Guide](docs/architecture.md)**: Deep dive into ByteHot's design
- 🔧 **[API Documentation](javadocs/)**: Complete API reference
- 🎯 **[Examples Repository](examples/)**: More comprehensive examples
- 🐛 **[Issue Tracker](https://github.com/rydnr/bytehot/issues)**: Report bugs or request features
- 💬 **[Discussions](https://github.com/rydnr/bytehot/discussions)**: Community support and ideas

### Contributing

ByteHot is open source and welcomes contributions:

```bash
# Fork the repository
git clone https://github.com/your-username/bytehot.git

# Create a feature branch
git checkout -b feature/amazing-feature

# Make your changes and test
mvn test

# Follow ByteHot's commit conventions
git commit -m "✨ Add amazing feature

Implement amazing new functionality that improves developer experience.

🤖 Generated with [Claude Code](https://claude.ai/code)

Co-Authored-By: YourName <your.email@domain.com>"

# Submit a pull request
```

Welcome to the ByteHot community! 🔥 Happy hot-swapping! 🚀