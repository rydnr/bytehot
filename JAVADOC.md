# Javadoc Validation for ByteHot

This document describes the javadoc validation tools and processes for the ByteHot project.

## Overview

ByteHot uses strict javadoc validation to ensure comprehensive documentation. This prevents GitHub Actions failures and maintains high documentation quality.

## Validation Tools

### 1. Comprehensive Javadoc Checker

**Location**: `.github/scripts/validate-all-javadoc.sh`

**Usage**:
```bash
bash .github/scripts/validate-all-javadoc.sh
```

**What it detects**:
- Missing javadoc comments on public/protected methods
- Missing javadoc comments on constructors
- Missing `@param` annotations
- Missing `@return` annotations
- Missing package-info.java comments
- HTML entity errors in javadoc
- Use of default constructors without comments

### 2. Quick Javadoc Checker

**Location**: `.github/scripts/check-javadoc.sh`

**Usage**:
```bash
bash .github/scripts/check-javadoc.sh
```

**What it does**:
- Runs Maven javadoc aggregate validation
- Provides basic issue reporting
- Faster than comprehensive checker

### 3. Pre-commit Hook

**Installation**:
```bash
bash .github/scripts/install-hooks.sh
```

**What it does**:
- Automatically validates javadoc before each commit
- Prevents commits that would fail GitHub Actions
- Can be bypassed with `git commit --no-verify` (not recommended)

## Common Javadoc Issues and Fixes

### Missing Method Comments

**Issue**:
```
warning: no comment
    public void someMethod() {
    ^
```

**Fix**:
```java
/**
 * Description of what this method does.
 * @param param1 description of parameter
 * @return description of return value
 */
public ReturnType someMethod(String param1) {
    // implementation
}
```

### Missing @param Annotations

**Issue**:
```
warning: no @param for parameterName
    public void method(String parameterName) {
    ^
```

**Fix**:
```java
/**
 * Method description.
 * @param parameterName description of this parameter
 */
public void method(String parameterName) {
    // implementation
}
```

### Missing @return Annotations

**Issue**:
```
warning: no @return
    public String getValue() {
    ^
```

**Fix**:
```java
/**
 * Method description.
 * @return description of the returned value
 */
public String getValue() {
    return value;
}
```

### Missing Package Comments

**Issue**:
```
warning: no comment
package org.acmsl.example;
^
```

**Fix**: Create or update `package-info.java`:
```java
/**
 * This package contains classes for handling example functionality.
 * 
 * <p>The main components include:
 * <ul>
 *   <li>ExampleClass - does something</li>
 *   <li>AnotherClass - does something else</li>
 * </ul>
 * 
 * @author Author Name
 * @since 1.0.0
 */
package org.acmsl.example;
```

### Constructor Comments

**Issue**:
```
warning: use of default constructor, which does not provide a comment
```

**Fix**:
```java
/**
 * Default constructor.
 */
public MyClass() {
    // initialization
}
```

## Development Workflow

1. **Before committing**: The pre-commit hook automatically validates javadoc
2. **Manual checking**: Run `bash .github/scripts/validate-all-javadoc.sh`
3. **Fix issues**: Address all javadoc warnings and errors
4. **Commit**: Only clean code with complete javadoc will be accepted

## CI/CD Integration

The GitHub Actions documentation workflow uses the same validation rules. If javadoc validation fails locally, it will also fail in CI/CD.

## Best Practices

1. **Write javadoc as you code**: Don't wait until the end
2. **Be descriptive**: Explain what, why, and how
3. **Document all parameters**: Even if the name seems obvious
4. **Document return values**: Explain what is returned and when
5. **Use proper HTML**: Javadoc supports HTML formatting
6. **Include examples**: When appropriate, show usage examples

## Troubleshooting

### Hook not running
```bash
# Reinstall hooks
bash .github/scripts/install-hooks.sh
```

### Too many issues to fix at once
```bash
# Temporarily bypass (not recommended for production)
git commit --no-verify -m "WIP: fixing javadoc issues"
```

### False positives
If the validation script reports false positives, please report them as issues for script improvement.

## Module-Specific Notes

- **java-commons**: Shared utilities require comprehensive documentation
- **bytehot-domain**: Domain models need business logic documentation
- **bytehot-application**: Application services need workflow documentation
- **bytehot-infrastructure**: Infrastructure adapters need integration documentation