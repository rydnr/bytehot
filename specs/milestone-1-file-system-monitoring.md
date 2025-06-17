# Milestone 1: File System Monitoring

## Overview

This milestone establishes the foundation for ByteHot by implementing file system monitoring capabilities that detect changes to `.class` files on disk. This is the entry point for the hot-swap workflow: when a developer recompiles a class, ByteHot must detect the change and initiate the hot-swap process.

## Objectives

- **Detect .class file changes** in real-time using Java NIO WatchService
- **Filter out non-.class files** to focus only on relevant changes
- **Emit domain events** for different types of file system operations
- **Handle race conditions** during file creation to ensure accurate file size detection
- **Provide robust error handling** for file system failures

## Domain Events

### 1. ClassFileChanged
**Trigger:** When an existing `.class` file is modified on disk
**Use Case:** Developer modifies an existing class and recompiles it

**Event Properties:**
- `classFile` (Path) - Path to the modified .class file
- `className` (String) - Class name extracted from filename
- `fileSize` (long) - Size of the modified file in bytes
- `timestamp` (Instant) - When the change was detected

**Example Scenario:**
```java
// Developer modifies MyClass.java and runs: javac MyClass.java
// ByteHot detects: MyClass.class was modified
ClassFileChanged event = new ClassFileChanged(
    Paths.get("/target/classes/com/example/MyClass.class"),
    "MyClass", 
    2048L, 
    Instant.now()
);
```

### 2. ClassFileCreated
**Trigger:** When a new `.class` file appears on disk
**Use Case:** Developer creates a new class or moves a class file

**Event Properties:**
- `classFile` (Path) - Path to the new .class file
- `className` (String) - Class name extracted from filename
- `fileSize` (long) - Size of the new file in bytes
- `timestamp` (Instant) - When the creation was detected

**Technical Challenge:** File creation events may fire before the file is fully written, leading to size=0. Solution: Retry file size reading with small delays.

### 3. ClassFileDeleted
**Trigger:** When a `.class` file is removed from disk
**Use Case:** Developer deletes a class or cleans build directory

**Event Properties:**
- `classFile` (Path) - Path to the deleted .class file
- `className` (String) - Class name extracted from filename
- `timestamp` (Instant) - When the deletion was detected

**Note:** File size is not available for deleted files.

## Implementation Components

### ClassFileWatcher
**Responsibility:** Monitor directories for .class file changes and emit domain events

**Key Features:**
- Extends `FolderWatch` for consistent monitoring infrastructure
- Uses Java NIO `WatchService` for efficient file system monitoring
- Registers for CREATE, MODIFY, DELETE events
- Filters events to only process `.class` files
- Handles race conditions with retry logic for file size detection

**Constants:**
```java
private static final String CLASS_FILE_EXTENSION = ".class";
private static final int MAX_FILE_SIZE_RETRY_ATTEMPTS = 5;
private static final int FILE_SIZE_RETRY_DELAY_MS = 10;
```

**Main Method:**
```java
public void watchClassFiles(Consumer<Object> onClassFileEvent) throws IOException
```

### Event Creation Pattern
Each file system event type maps to a specific domain event:

```java
private Object createClassFileEvent(Path classFile, WatchEvent.Kind<?> eventKind) {
    if (eventKind == StandardWatchEventKinds.ENTRY_CREATE) {
        return createClassFileCreatedEvent(classFile);
    } else if (eventKind == StandardWatchEventKinds.ENTRY_MODIFY) {
        return createClassFileChangedEvent(classFile);
    } else if (eventKind == StandardWatchEventKinds.ENTRY_DELETE) {
        return createClassFileDeletedEvent(classFile);
    }
    return null;
}
```

## Technical Requirements

### Performance
- **Non-blocking monitoring:** File watching runs in background thread
- **Efficient filtering:** Only .class files are processed, others ignored
- **Minimal overhead:** Use native file system notifications via WatchService

### Reliability
- **Race condition handling:** Wait for files to be fully written before processing
- **Error recovery:** Continue monitoring even if individual file processing fails
- **Resource cleanup:** Properly close WatchService in finally blocks

### Extensibility
- **Plugin architecture:** Easy to add new file types or monitoring behaviors
- **Event-driven design:** Other components subscribe to file system events
- **Configurable polling:** Customizable poll intervals for different environments

## Integration Points

### Input
- **File system changes** detected by Java NIO WatchService
- **Configuration** specifying which directories to monitor

### Output
- **Domain events** (ClassFileChanged, ClassFileCreated, ClassFileDeleted)
- **Error notifications** for file processing failures

### Dependencies
- `FolderWatch` - Base monitoring infrastructure
- `java.nio.file.WatchService` - Core file system monitoring
- Domain event infrastructure

## Testing Strategy

### Unit Tests
- **Event generation:** Verify correct events for file operations
- **File filtering:** Ensure only .class files trigger events
- **Race condition handling:** Test file size detection during creation
- **Error scenarios:** Handle malformed files, permission issues

### Integration Tests
- **Real file operations:** Create, modify, delete actual .class files
- **Multiple file scenarios:** Batch operations, rapid changes
- **Directory monitoring:** Monitor subdirectories, nested packages

### Test Data
```java
// Mock bytecode for testing
private byte[] createSimpleClassBytecode(String className) {
    String content = "MOCK_BYTECODE_FOR_" + className;
    return content.getBytes();
}
```

## Success Criteria

### Functional
- ✅ **ClassFileChanged events** triggered for file modifications
- ✅ **ClassFileCreated events** triggered for new files with correct size
- ✅ **ClassFileDeleted events** triggered for file removals
- ✅ **Non-.class files ignored** (no events for .java, .txt, etc.)

### Technical
- ✅ **Race conditions resolved** - file size always > 0 for created files
- ✅ **Error handling** - continue monitoring after individual failures
- ✅ **Resource management** - WatchService properly closed
- ✅ **Performance** - minimal CPU usage during monitoring

### Quality
- ✅ **Test coverage** - 6/6 tests passing (2 per event type)
- ✅ **Code quality** - refactored to remove technical debt
- ✅ **Documentation** - clear javadoc and specifications

## Completion Status: ✅ COMPLETED

**Implementation:** All three domain events implemented and tested
**Test Results:** 6/6 tests passing
**Refactoring:** Technical debt removed, constants extracted, error handling improved
**Integration:** Ready for Milestone 2 (Bytecode Analysis)