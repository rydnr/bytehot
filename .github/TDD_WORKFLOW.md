# TDD Workflow Guide for ByteHot

This guide implements the Test-Driven Development (TDD) methodology as documented in [`CLAUDE.md`](../CLAUDE.md).

## Overview

ByteHot uses TDD and makes the process explicit in Git and GitHub through emoji-based commit messages that track the Red-Green-Refactor cycle.

## TDD Cycle

### 1. Create Issue
- Create a GitHub issue describing the feature or bug
- Reference the issue number in all TDD commits

### 2. Red Phase - Failing Test
Write a failing test that describes the desired behavior:

```bash
# Using the TDD helper script
.github/scripts/tdd-helper.sh test 42

# Or manually:
# 1. Write your failing test
# 2. Verify it fails: mvn test
# 3. Commit with: git commit -m "🧪 [#42] Add failing test for feature X"
```

**Commit Format**: `🧪 [#issue_number] Add failing test`

### 3. Green Phase - Make It Pass
Implement the simplest solution to make the test pass:

#### Option A: Naive Implementation
For simple/stubbed solutions that make the test pass trivially:

```bash
.github/scripts/tdd-helper.sh naive 42
```

**Commit Format**: `🤔 [#issue_number] Naive implementation`

#### Option B: Working Implementation
For complete solutions with real business logic:

```bash
.github/scripts/tdd-helper.sh implement 42
```

**Commit Format**: `✅ [#issue_number] Working implementation`

### 4. Refactor Phase - Improve Code
Clean up the implementation while keeping tests green:

```bash
.github/scripts/tdd-helper.sh refactor 42
```

**Commit Format**: `🚀 [#issue_number] Refactor`

## TDD Emoji Reference

| Emoji | Code | Meaning | When to Use |
|-------|------|---------|-------------|
| 🧪 | `:test-tube:` | A new failing test | After test is added |
| 🤔 | `:thinking-face:` | Naive implementation | When test passes trivially |
| ✅ | `:white-check-mark:` | Working implementation | When test passes with real logic |
| 🚀 | `:rocket:` | Refactor | Improving code after green |

## TDD Helper Scripts

### Main TDD Helper
Located at `.github/scripts/tdd-helper.sh`

**Usage:**
```bash
# Add failing test
.github/scripts/tdd-helper.sh test <issue_number>

# Implement naive solution
.github/scripts/tdd-helper.sh naive <issue_number>

# Implement working solution
.github/scripts/tdd-helper.sh implement <issue_number>

# Refactor code
.github/scripts/tdd-helper.sh refactor <issue_number>

# Show TDD status
.github/scripts/tdd-helper.sh status

# Validate TDD workflow compliance
.github/scripts/tdd-helper.sh validate
```

### Commit Message Validation
Located at `.github/scripts/validate-tdd-commit.sh`

Validates that commit messages follow the TDD pattern. Can be used as a git hook.

## Example TDD Workflow

Let's implement a new feature for hot-swap validation:

### 1. Create Issue
Create GitHub issue #123: "Add bytecode validation before hot-swap"

### 2. Write Failing Test
```bash
# Add failing test
.github/scripts/tdd-helper.sh test 123

# This will prompt you to:
# 1. Write your failing test
# 2. Verify it fails
# 3. Commit with "🧪 [#123] Add failing test"
```

### 3. Implement Solution
```bash
# Start with naive implementation
.github/scripts/tdd-helper.sh naive 123

# Or go directly to working implementation
.github/scripts/tdd-helper.sh implement 123
```

### 4. Refactor (if needed)
```bash
# Refactor the code
.github/scripts/tdd-helper.sh refactor 123
```

### 5. Final Git History
```
🚀 [#123] Refactor validation logic for better performance
✅ [#123] Working implementation of bytecode validation
🧪 [#123] Add failing test for bytecode validation
```

## Best Practices

### Test Writing
- Write tests that describe behavior, not implementation
- Use descriptive test names that explain the scenario
- Follow the Given-When-Then pattern
- Test edge cases and error conditions

### Implementation
- Start with the simplest solution that makes the test pass
- Use naive implementation (🤔) for stubbed/hardcoded solutions
- Use working implementation (✅) for real business logic
- Refactor (🚀) only when tests are green

### Commit Guidelines
- Always include the issue number in square brackets
- Use the appropriate emoji for the TDD phase
- Keep commit messages concise but descriptive
- One commit per TDD phase

## Integration with ByteHot Architecture

### Domain Tests
- Located in `bytehot-domain/src/test/java/`
- Focus on business logic and domain events
- Use mock objects for external dependencies

### Application Tests
- Located in `bytehot-application/src/test/java/`
- Test orchestration and use case flows
- Integration tests for complete workflows

### Infrastructure Tests
- Located in `bytehot-infrastructure/src/test/java/`
- Test adapters and external integrations
- Use test containers for external dependencies

## Testing Framework

ByteHot uses:
- **JUnit 5** for test framework
- **AssertJ** for fluent assertions
- **Mockito** for mocking
- **TestContainers** for integration tests

## Continuous Integration

The TDD workflow integrates with CI/CD:
- All tests must pass before merge
- Commit message validation ensures TDD compliance
- Coverage reports track test effectiveness

## Troubleshooting

### Common Issues

**Tests not failing when expected:**
```bash
# Check test is properly written
mvn test -Dtest=YourTestClass

# Verify test is actually running
mvn test -Dtest=YourTestClass -X
```

**Commit message validation fails:**
```bash
# Check format matches TDD pattern
git commit -m "🧪 [#123] Add failing test for feature X"

# For non-TDD commits, don't use issue reference
git commit -m "Update documentation"
```

**Tests fail after refactoring:**
```bash
# Run specific test
mvn test -Dtest=YourTestClass

# Check for compilation errors
mvn compile
```

### Getting Help

1. Check the [TDD section in CLAUDE.md](../CLAUDE.md#tdd)
2. Run `.github/scripts/tdd-helper.sh validate` to check compliance
3. Use `.github/scripts/tdd-helper.sh status` to see current state
4. Review existing TDD commits for examples: `git log --grep="🧪\|🤔\|✅\|🚀"`

## Example Test Structure

```java
@Test
@DisplayName("Should validate bytecode before hot-swap")
void shouldValidateBytecodeBeforeHotSwap() {
    // Given
    final BytecodeValidator validator = new BytecodeValidator();
    final byte[] invalidBytecode = new byte[]{0x01, 0x02, 0x03};
    
    // When & Then
    assertThatThrownBy(() -> validator.validate(invalidBytecode))
        .isInstanceOf(BytecodeValidationException.class)
        .hasMessageContaining("Invalid bytecode format");
}
```

## Summary

The TDD workflow in ByteHot ensures:
- ✅ High code quality through test-first development
- 🧪 Explicit tracking of development phases in Git
- 🤔 Clear distinction between naive and working implementations
- 🚀 Continuous refactoring and improvement
- 📊 Measurable progress through commit history

Follow this workflow for all new features and bug fixes to maintain the high quality standards of the ByteHot project.