# TDD Quick Reference Guide

## Quick Setup
```bash
# Install TDD tools and hooks
.github/scripts/setup-tdd.sh

# Check TDD status
.github/scripts/tdd-helper.sh status
```

## TDD Cycle Commands

### 1. Red Phase (Failing Test)
```bash
# Add failing test for issue #123
.github/scripts/tdd-helper.sh test 123

# Or use git alias
git tdd-test 123
```

### 2. Green Phase (Make It Pass)
```bash
# Naive implementation (stubbed/hardcoded)
.github/scripts/tdd-helper.sh naive 123
git tdd-naive 123

# OR working implementation (real business logic)
.github/scripts/tdd-helper.sh implement 123
git tdd-impl 123
```

### 3. Refactor Phase (Improve Code)
```bash
# Refactor while keeping tests green
.github/scripts/tdd-helper.sh refactor 123
git tdd-refactor 123
```

## Commit Message Format

| Emoji | Code | Message Format | Usage |
|-------|------|----------------|-------|
| 🧪 | `:test-tube:` | `🧪 [#123] Add failing test` | After adding failing test |
| 🤔 | `:thinking-face:` | `🤔 [#123] Naive implementation` | Simple/stubbed solution |
| ✅ | `:white-check-mark:` | `✅ [#123] Working implementation` | Real business logic |
| 🚀 | `:rocket:` | `🚀 [#123] Refactor` | Code improvement |

## Example Workflow

```bash
# 1. Create GitHub issue #456 for "Add bytecode validation"

# 2. Add failing test
.github/scripts/tdd-helper.sh test 456
# Commit: 🧪 [#456] Add failing test

# 3. Implement solution
.github/scripts/tdd-helper.sh implement 456
# Commit: ✅ [#456] Working implementation

# 4. Refactor if needed
.github/scripts/tdd-helper.sh refactor 456
# Commit: 🚀 [#456] Refactor
```

## Git History Result
```
🚀 [#456] Refactor validation logic
✅ [#456] Working implementation of bytecode validation
🧪 [#456] Add failing test for bytecode validation
```

## Validation
```bash
# Check TDD compliance
.github/scripts/tdd-helper.sh validate

# View TDD status
.github/scripts/tdd-helper.sh status
```

## Testing Commands
```bash
# Run all tests
mvn test

# Run specific test
mvn test -Dtest=MyTestClass

# Run tests with coverage
mvn test jacoco:report
```

## Helper Aliases (after setup)
```bash
tdd test 123        # Add failing test
tdd naive 123       # Naive implementation
tdd impl 123        # Working implementation
tdd refactor 123    # Refactor code
tdd status          # Show TDD status
tdd validate        # Check compliance
```

## Files Created
- `.github/scripts/tdd-helper.sh` - Main TDD workflow script
- `.github/scripts/validate-tdd-commit.sh` - Commit message validation
- `.github/scripts/setup-tdd.sh` - One-time setup script
- `.github/hooks/commit-msg` - Git hook template
- `.github/TDD_WORKFLOW.md` - Complete documentation