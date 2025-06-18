# ByteHot Configuration Guide

This guide provides comprehensive information about configuring ByteHot for your development and production environments.

## Configuration Sources

ByteHot supports multiple configuration sources with a hierarchical priority system:

1. **System Properties** (highest priority)
2. **Environment Variables**
3. **YAML Configuration Files**
4. **Properties Files**
5. **Default Values** (lowest priority)

## Configuration File Formats

### YAML Configuration (Recommended)

Create a `bytehot.yml` file in your project root or classpath:

```yaml
bytehot:
  # File watching configuration
  watch:
    paths:
      - path: "target/classes"
        patterns: ["*.class"]
        recursive: true
      - path: "build/classes/java/main"
        patterns: ["*.class"]
        recursive: true
    intervals:
      scan: 500ms
      debounce: 200ms
  
  # Hot-swap behavior
  hotswap:
    enabled: true
    validation:
      strict: false
      compatibility_checks: true
    retry:
      max_attempts: 3
      backoff: exponential
  
  # Instance management
  instances:
    tracking: true
    update_strategy: AUTOMATIC
    preserve_state: true
    framework_integration: true
  
  # Error handling and recovery
  error_handling:
    rollback_enabled: true
    max_rollback_depth: 10
    recovery_strategy: PRESERVE_CURRENT_STATE
  
  # EventSourcing configuration
  eventsourcing:
    enabled: true
    store_type: filesystem
    store_path: "./eventstore"
    retention_days: 30
  
  # User management
  user:
    auto_discovery: true
    session_tracking: true
    analytics_enabled: true
  
  # Logging and monitoring
  logging:
    level: INFO
    events:
      emit_to_console: true
      emit_to_file: false
      file_path: "./bytehot.log"
  
  # Development features
  development:
    debug_mode: false
    metrics_collection: true
    test_mode: false
```

### Properties Configuration

Create a `bytehot.properties` file:

```properties
# File watching
bytehot.watch.paths=target/classes,build/classes/java/main
bytehot.watch.patterns=*.class
bytehot.watch.recursive=true
bytehot.watch.intervals.scan=500ms
bytehot.watch.intervals.debounce=200ms

# Hot-swap behavior
bytehot.hotswap.enabled=true
bytehot.hotswap.validation.strict=false
bytehot.hotswap.validation.compatibility_checks=true

# Instance management
bytehot.instances.tracking=true
bytehot.instances.update_strategy=AUTOMATIC
bytehot.instances.preserve_state=true

# EventSourcing
bytehot.eventsourcing.enabled=true
bytehot.eventsourcing.store_type=filesystem
bytehot.eventsourcing.store_path=./eventstore

# User management
bytehot.user.auto_discovery=true
bytehot.user.session_tracking=true

# Logging
bytehot.logging.level=INFO
bytehot.logging.events.emit_to_console=true
```

## Environment Variables

All configuration options can be overridden using environment variables with the `BYTEHOT_` prefix:

```bash
# File watching
export BYTEHOT_WATCH_PATHS="target/classes,build/classes"
export BYTEHOT_WATCH_RECURSIVE=true
export BYTEHOT_WATCH_INTERVALS_SCAN=500ms

# Hot-swap behavior
export BYTEHOT_HOTSWAP_ENABLED=true
export BYTEHOT_HOTSWAP_VALIDATION_STRICT=false

# Instance management
export BYTEHOT_INSTANCES_UPDATE_STRATEGY=AUTOMATIC
export BYTEHOT_INSTANCES_PRESERVE_STATE=true

# EventSourcing
export BYTEHOT_EVENTSOURCING_ENABLED=true
export BYTEHOT_EVENTSOURCING_STORE_PATH=/opt/bytehot/eventstore

# User management
export BYTEHOT_USER_AUTO_DISCOVERY=true
export BYTEHOT_USER_SESSION_TRACKING=true

# Production settings
export BYTEHOT_DEVELOPMENT_DEBUG_MODE=false
export BYTEHOT_LOGGING_LEVEL=WARN
```

## System Properties

Use JVM system properties for runtime configuration:

```bash
java -javaagent:bytehot-agent.jar \
     -Dbytehot.watch.paths=target/classes \
     -Dbytehot.hotswap.enabled=true \
     -Dbytehot.logging.level=DEBUG \
     -jar your-application.jar
```

## Configuration Options Reference

### File Watching Configuration

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `bytehot.watch.paths` | String[] | `["target/classes"]` | Directories to watch for changes |
| `bytehot.watch.patterns` | String[] | `["*.class"]` | File patterns to monitor |
| `bytehot.watch.recursive` | Boolean | `true` | Watch subdirectories recursively |
| `bytehot.watch.intervals.scan` | Duration | `500ms` | File system scan interval |
| `bytehot.watch.intervals.debounce` | Duration | `200ms` | Debounce time for file changes |

### Hot-Swap Configuration

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `bytehot.hotswap.enabled` | Boolean | `true` | Enable hot-swap functionality |
| `bytehot.hotswap.validation.strict` | Boolean | `false` | Strict bytecode validation |
| `bytehot.hotswap.validation.compatibility_checks` | Boolean | `true` | Check class compatibility |
| `bytehot.hotswap.retry.max_attempts` | Integer | `3` | Maximum retry attempts |
| `bytehot.hotswap.retry.backoff` | String | `exponential` | Retry backoff strategy |

### Instance Management Configuration

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `bytehot.instances.tracking` | Boolean | `true` | Track object instances |
| `bytehot.instances.update_strategy` | Enum | `AUTOMATIC` | Instance update strategy |
| `bytehot.instances.preserve_state` | Boolean | `true` | Preserve object state |
| `bytehot.instances.framework_integration` | Boolean | `true` | Enable framework integration |

**Instance Update Strategies:**
- `AUTOMATIC`: Automatically choose best strategy
- `REFLECTION`: Use reflection for updates  
- `PROXY_REFRESH`: Refresh framework proxies
- `FACTORY_RESET`: Recreate through factories
- `NO_UPDATE`: Skip instance updates

### Error Handling Configuration

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `bytehot.error_handling.rollback_enabled` | Boolean | `true` | Enable automatic rollback |
| `bytehot.error_handling.max_rollback_depth` | Integer | `10` | Maximum rollback history |
| `bytehot.error_handling.recovery_strategy` | Enum | `PRESERVE_CURRENT_STATE` | Error recovery strategy |

**Recovery Strategies:**
- `REJECT_CHANGE`: Reject problematic changes
- `ROLLBACK_CHANGES`: Rollback to previous state
- `PRESERVE_CURRENT_STATE`: Keep current state
- `EMERGENCY_SHUTDOWN`: Shutdown on critical errors
- `FALLBACK_MODE`: Enter safe mode

### EventSourcing Configuration

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `bytehot.eventsourcing.enabled` | Boolean | `true` | Enable EventSourcing |
| `bytehot.eventsourcing.store_type` | String | `filesystem` | Event store implementation |
| `bytehot.eventsourcing.store_path` | String | `./eventstore` | Event store directory |
| `bytehot.eventsourcing.retention_days` | Integer | `30` | Event retention period |
| `bytehot.eventsourcing.compression` | Boolean | `false` | Compress stored events |

### User Management Configuration

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `bytehot.user.auto_discovery` | Boolean | `true` | Auto-discover user from Git/env |
| `bytehot.user.session_tracking` | Boolean | `true` | Track user sessions |
| `bytehot.user.analytics_enabled` | Boolean | `true` | Collect usage analytics |
| `bytehot.user.preferences_file` | String | `~/.bytehot/preferences.yml` | User preferences file |

### Logging Configuration

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `bytehot.logging.level` | String | `INFO` | Logging level |
| `bytehot.logging.events.emit_to_console` | Boolean | `true` | Log events to console |
| `bytehot.logging.events.emit_to_file` | Boolean | `false` | Log events to file |
| `bytehot.logging.events.file_path` | String | `./bytehot.log` | Log file path |

**Logging Levels:** `TRACE`, `DEBUG`, `INFO`, `WARN`, `ERROR`

## Framework-Specific Configuration

### Spring Boot Integration

```yaml
bytehot:
  frameworks:
    spring:
      enabled: true
      refresh_context: true
      preserve_beans: true
      proxy_refresh: true
```

### Maven Integration

Add to your `pom.xml`:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <argLine>-javaagent:${project.build.directory}/bytehot-agent.jar</argLine>
        <systemPropertyVariables>
            <bytehot.watch.paths>${project.build.outputDirectory}</bytehot.watch.paths>
            <bytehot.hotswap.enabled>true</bytehot.hotswap.enabled>
        </systemPropertyVariables>
    </configuration>
</plugin>
```

### Gradle Integration

Add to your `build.gradle`:

```gradle
test {
    jvmArgs "-javaagent:build/libs/bytehot-agent.jar"
    systemProperty "bytehot.watch.paths", "build/classes/java/main"
    systemProperty "bytehot.hotswap.enabled", "true"
}

run {
    jvmArgs "-javaagent:build/libs/bytehot-agent.jar"
}
```

## Development Environment Setup

### IDE Integration

#### IntelliJ IDEA

1. Go to **Run/Debug Configurations**
2. Add VM options: `-javaagent:path/to/bytehot-agent.jar`
3. Set system properties as needed
4. Enable auto-compilation for hot-swap

#### Eclipse

1. Go to **Run Configurations**
2. Add VM arguments: `-javaagent:path/to/bytehot-agent.jar`
3. Configure system properties in **Arguments** tab
4. Enable **Project → Build Automatically**

#### VS Code

Add to `.vscode/launch.json`:

```json
{
    "configurations": [
        {
            "type": "java",
            "name": "Launch with ByteHot",
            "vmArgs": [
                "-javaagent:${workspaceFolder}/bytehot-agent.jar",
                "-Dbytehot.watch.paths=${workspaceFolder}/target/classes"
            ]
        }
    ]
}
```

## Production Configuration

### Recommended Production Settings

```yaml
bytehot:
  hotswap:
    enabled: false  # Disable in production
  eventsourcing:
    enabled: true
    store_path: "/opt/bytehot/eventstore"
    retention_days: 90
  user:
    auto_discovery: false
    analytics_enabled: true
  logging:
    level: WARN
    events:
      emit_to_console: false
      emit_to_file: true
      file_path: "/var/log/bytehot/bytehot.log"
  development:
    debug_mode: false
    metrics_collection: true
```

### Security Considerations

1. **Disable Hot-Swap in Production**: Set `bytehot.hotswap.enabled=false`
2. **Secure Event Store**: Restrict access to event store directory
3. **Log Management**: Configure appropriate log rotation and retention
4. **User Privacy**: Consider disabling user analytics in sensitive environments

## Troubleshooting

### Common Configuration Issues

#### Agent Not Loading
```bash
# Verify agent path is correct
java -javaagent:/absolute/path/to/bytehot-agent.jar -jar app.jar

# Check agent is loaded
jps -v | grep bytehot
```

#### File Watching Not Working
```yaml
bytehot:
  watch:
    paths:
      - path: "target/classes"  # Verify path exists
        patterns: ["*.class"]   # Check pattern matches files
        recursive: true
    intervals:
      scan: 100ms  # Reduce scan interval for faster detection
```

#### EventStore Issues
```bash
# Check directory permissions
ls -la ./eventstore/

# Verify disk space
df -h ./eventstore/

# Check configuration
cat bytehot.yml | grep eventsourcing -A 5
```

### Configuration Validation

ByteHot validates configuration on startup and provides detailed error messages:

```
[ERROR] ByteHot Configuration Error:
- bytehot.watch.paths: Directory 'invalid/path' does not exist
- bytehot.hotswap.retry.max_attempts: Value '-1' must be positive
- bytehot.eventsourcing.store_path: Permission denied for directory '/restricted'
```

### Debug Configuration

Enable debug logging to troubleshoot configuration issues:

```bash
java -javaagent:bytehot-agent.jar \
     -Dbytehot.logging.level=DEBUG \
     -Dbytehot.development.debug_mode=true \
     -jar your-app.jar
```

## Configuration Examples

### Development Environment
```yaml
bytehot:
  watch:
    intervals:
      scan: 100ms  # Fast scanning for immediate feedback
  hotswap:
    validation:
      strict: false  # Lenient validation for experimentation
  logging:
    level: DEBUG  # Verbose logging for troubleshooting
  development:
    debug_mode: true
    test_mode: true
```

### CI/CD Environment
```yaml
bytehot:
  hotswap:
    enabled: false  # Disable hot-swap in CI
  eventsourcing:
    enabled: true
    store_path: "/tmp/bytehot-ci"
  user:
    auto_discovery: false  # Use CI user context
  logging:
    level: INFO
    events:
      emit_to_file: true
```

### Production Environment
```yaml
bytehot:
  hotswap:
    enabled: false  # Never enable in production
  eventsourcing:
    enabled: true
    store_path: "/opt/bytehot/eventstore"
    retention_days: 365  # Long retention for compliance
  error_handling:
    recovery_strategy: EMERGENCY_SHUTDOWN  # Fail-safe behavior
  logging:
    level: ERROR
    events:
      emit_to_file: true
      file_path: "/var/log/bytehot/production.log"
```

This configuration guide provides comprehensive coverage of ByteHot's configuration options. For additional help, consult the [API Documentation](.) or reach out through [GitHub Discussions](https://github.com/rydnr/bytehot/discussions).