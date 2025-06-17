# Milestone 2: Bytecode Analysis

## Overview

This milestone implements bytecode analysis capabilities that examine `.class` files to extract metadata and validate compatibility for hot-swapping. Before attempting to hot-swap a class, ByteHot must understand the class structure and ensure the changes are safe for runtime redefinition.

## Objectives

- **Extract class metadata** from bytecode (class name, superclass, interfaces, fields, methods)
- **Validate bytecode compatibility** for hot-swap operations
- **Identify safe vs unsafe changes** (method body changes OK, schema changes rejected)
- **Provide detailed validation feedback** for debugging and troubleshooting
- **Handle validation failures gracefully** with domain events

## Domain Events

### 1. ClassMetadataExtracted
**Trigger:** When class information is successfully parsed from bytecode
**Use Case:** Understanding class structure before validation

**Event Properties:**
- `classFile` (Path) - Path to the analyzed .class file
- `className` (String) - Fully qualified class name
- `superClassName` (String) - Fully qualified superclass name
- `interfaces` (List<String>) - List of implemented interfaces
- `fields` (List<String>) - List of declared fields
- `methods` (List<String>) - List of declared methods
- `timestamp` (Instant) - When metadata was extracted

**Example Scenario:**
```java
// Analyzing: com/example/UserService.class
ClassMetadataExtracted event = new ClassMetadataExtracted(
    Paths.get("/classes/com/example/UserService.class"),
    "com.example.UserService",
    "java.lang.Object",
    List.of("java.io.Serializable", "com.example.Service"),
    List.of("userRepository", "logger"),
    List.of("<init>", "findUser", "saveUser", "deleteUser"),
    Instant.now()
);
```

### 2. BytecodeValidated
**Trigger:** When bytecode passes validation checks for hot-swap compatibility
**Use Case:** Confirming changes are safe for runtime redefinition

**Event Properties:**
- `classFile` (Path) - Path to the validated .class file
- `className` (String) - Name of the validated class
- `validForHotSwap` (boolean) - Always true for validated bytecode
- `validationDetails` (String) - Description of what was validated
- `timestamp` (Instant) - When validation completed

**Safe Changes (Will Pass Validation):**
- Method body modifications
- Adding/removing private methods (in some JVMs)
- Changing method implementations
- Modifying static initializers

**Example Validation Messages:**
- `"Bytecode validation passed - method body changes only"`
- `"Bytecode validation passed - method body changes detected"`
- `"Bytecode validation passed - compatible changes detected"`

### 3. BytecodeRejected
**Trigger:** When bytecode fails validation checks (incompatible changes)
**Use Case:** Preventing unsafe hot-swap attempts that would crash the JVM

**Event Properties:**
- `classFile` (Path) - Path to the rejected .class file
- `className` (String) - Name of the rejected class
- `validForHotSwap` (boolean) - Always false for rejected bytecode
- `rejectionReason` (String) - Detailed reason for rejection
- `timestamp` (Instant) - When rejection occurred

**Unsafe Changes (Will Be Rejected):**
- Adding/removing fields (schema changes)
- Changing class hierarchy (extending different class)
- Adding/removing interfaces
- Changing method signatures
- Adding/removing public/protected methods

**Example Rejection Reasons:**
- `"Bytecode validation failed - schema changes (field addition) not supported"`
- `"Bytecode validation failed - schema changes (field removal) not supported"`
- `"Bytecode validation failed - interface changes not supported"`

## Implementation Components

### BytecodeAnalyzer
**Responsibility:** Extract metadata from .class file bytecode

**Key Method:**
```java
public ClassMetadataExtracted extractMetadata(Path classFile) throws IOException
```

**Mock Bytecode Format (for testing):**
```
VALID_BYTECODE:ClassName:extends:SuperClass:interfaces:Interface1,Interface2:fields:field1,field2:methods:method1,method2
```

**Features:**
- Parse class name, superclass, interfaces
- Extract field and method lists
- Handle empty collections gracefully
- Provide detailed error messages for invalid bytecode

### BytecodeValidator
**Responsibility:** Validate bytecode for hot-swap compatibility

**Key Method:**
```java
public BytecodeValidated validate(Path classFile) throws IOException, BytecodeValidationException
```

**Validation Logic:**
```java
if (content.startsWith("COMPATIBLE_BYTECODE:")) {
    return createValidatedEvent(classFile, content);
} else if (content.startsWith("INCOMPATIBLE_BYTECODE:")) {
    throw new BytecodeValidationException(createRejectedEvent(classFile, content));
}
```

**Mock Bytecode Formats:**
- **Compatible:** `COMPATIBLE_BYTECODE:ClassName:changes:method_body_only`
- **Incompatible:** `INCOMPATIBLE_BYTECODE:ClassName:changes:schema_changes:reason:field_addition`

### BytecodeValidationException
**Responsibility:** Handle validation failures with embedded domain events

**Properties:**
- `message` (String) - Exception message
- `rejectionEvent` (BytecodeRejected) - Domain event with failure details
- `serialVersionUID` - For proper serialization

**Usage Pattern:**
```java
try {
    BytecodeValidated event = validator.validate(classFile);
    // Process successful validation
} catch (BytecodeValidationException e) {
    BytecodeRejected rejection = e.getRejectionEvent();
    // Handle validation failure
}
```

## Hot-Swap Compatibility Rules

### Safe Changes (✅ Allowed)
1. **Method Body Changes**
   - Modifying method implementations
   - Changing control flow, algorithms
   - Adding/removing local variables
   - Changing private method calls

2. **Static Initializer Changes**
   - Modifying static initialization code
   - Changing static field initialization

### Unsafe Changes (❌ Rejected)
1. **Schema Changes**
   - Adding/removing instance fields
   - Adding/removing static fields
   - Changing field types

2. **Hierarchy Changes**
   - Changing superclass
   - Adding/removing interfaces
   - Changing class modifiers

3. **Method Signature Changes**
   - Adding/removing methods
   - Changing method signatures
   - Changing method modifiers

4. **Constructor Changes**
   - Adding/removing constructors
   - Changing constructor signatures

## Technical Requirements

### Accuracy
- **Precise validation:** Only allow changes that won't crash JVM
- **Conservative approach:** When in doubt, reject the change
- **Clear feedback:** Provide specific reasons for rejection

### Performance
- **Fast analysis:** Minimize bytecode parsing overhead
- **Efficient validation:** Quick compatibility checks
- **Cached results:** Avoid re-analyzing unchanged files

### Extensibility
- **Pluggable validators:** Easy to add new validation rules
- **Configurable policies:** Different strictness levels
- **JVM-specific rules:** Handle differences between Java versions

## Integration Points

### Input
- **File system events** from Milestone 1 (ClassFileChanged, ClassFileCreated)
- **Bytecode files** to analyze and validate

### Output
- **Metadata events** (ClassMetadataExtracted)
- **Validation events** (BytecodeValidated, BytecodeRejected)
- **Exception events** embedded in validation failures

### Dependencies
- Domain event infrastructure
- File I/O operations
- Future: ASM library for real bytecode parsing

## Testing Strategy

### Unit Tests
- **Metadata extraction:** Verify correct parsing of class information
- **Validation success:** Test compatible bytecode scenarios
- **Validation failure:** Test incompatible bytecode scenarios
- **Exception handling:** Verify proper error propagation

### Test Scenarios
```java
// Valid metadata extraction
ClassMetadataExtracted event = analyzer.extractMetadata(complexClassFile);
assertEquals("ComplexClass", event.getClassName());
assertEquals(2, event.getInterfaces().size());
assertEquals(3, event.getMethods().size());

// Successful validation
BytecodeValidated validated = validator.validate(compatibleClassFile);
assertTrue(validated.isValidForHotSwap());
assertTrue(validated.getValidationDetails().contains("method body changes"));

// Validation failure
assertThrows(BytecodeValidationException.class, 
    () -> validator.validate(incompatibleClassFile));
```

### Integration Tests
- **End-to-end analysis:** File detection → metadata extraction → validation
- **Real bytecode:** Test with actual compiled .class files
- **Performance testing:** Large classes, complex hierarchies

## Mock Implementation

### Test Bytecode Formats
The implementation uses mock bytecode formats for testing:

**Metadata Format:**
```
VALID_BYTECODE:ClassName:extends:SuperClass:interfaces:Interface1,Interface2:fields:field1,field2:methods:method1,method2
```

**Compatible Changes:**
```
COMPATIBLE_BYTECODE:ClassName:changes:method_body_only
COMPATIBLE_BYTECODE:ClassName:changes:method_body_changes:methods:someMethod
```

**Incompatible Changes:**
```
INCOMPATIBLE_BYTECODE:ClassName:changes:schema_changes:reason:field_addition:fields:newField
INCOMPATIBLE_BYTECODE:ClassName:changes:schema_changes:reason:field_removal
```

### Future Enhancement
Replace mock bytecode parsing with ASM library for real bytecode analysis:
- Read actual class files
- Parse method bytecode
- Detect structural changes
- Support advanced validation rules

## Success Criteria

### Functional
- ✅ **ClassMetadataExtracted events** with complete class information
- ✅ **BytecodeValidated events** for compatible changes
- ✅ **BytecodeRejected events** for incompatible changes via exceptions
- ✅ **Detailed validation feedback** for debugging

### Technical
- ✅ **Exception handling** - validation failures properly encapsulated
- ✅ **Event consistency** - all events contain required information
- ✅ **Type safety** - proper exception types and error handling
- ✅ **Performance** - efficient parsing and validation

### Quality
- ✅ **Test coverage** - 6/6 tests passing (2 metadata, 2 validation, 2 rejection)
- ✅ **Error messages** - clear, actionable validation feedback
- ✅ **Documentation** - comprehensive specs and javadoc

## Completion Status: ✅ COMPLETED

**Implementation:** All three domain events implemented and tested
**Test Results:** 6/6 tests passing
- ClassMetadataExtractedTest: 2/2 ✅
- BytecodeValidatedTest: 2/2 ✅  
- BytecodeRejectedTest: 2/2 ✅
**Integration:** Ready for Milestone 3 (Hot-Swap Operations)