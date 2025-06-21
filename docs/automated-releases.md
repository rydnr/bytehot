# Automated GitHub Releases for ByteHot

This document explains ByteHot's comprehensive automated release system using GitHub Actions.

## Overview

ByteHot now includes sophisticated GitHub Actions workflows that automatically create rich, detailed releases whenever you push a tag. The system provides:

- **Intelligent changelog generation** based on commit analysis
- **Comprehensive metadata extraction** and documentation
- **Multiple release workflows** for different use cases
- **Quality assurance** with testing and validation
- **Rich release notes** with categorized changes

## 🚀 Quick Start

### Creating a Release

1. **Tag your commit** with an appropriate version:
   ```bash
   # Stable release
   git tag -s v1.2.0 -m "ByteHot v1.2.0 - Stable Release"
   
   # Milestone release  
   git tag -s v1.2.0-milestone-8-complete -m "Milestone 8 Complete"
   
   # Development release
   git tag -s dev-2025.06.21 -m "Development snapshot"
   ```

2. **Push the tag**:
   ```bash
   git push origin v1.2.0
   ```

3. **GitHub Actions automatically**:
   - Builds and tests the project
   - Generates comprehensive changelog
   - Creates release artifacts with checksums
   - Publishes release with rich documentation

## 🔧 Available Workflows

### 1. Smart Release Workflow (`smart-release.yml`)

**Recommended for most releases**

- **Triggers**: Any tag (`v*`, `milestone-*`)
- **Features**:
  - Python-based intelligent commit categorization
  - Recognizes ByteHot emoji conventions
  - Generates comprehensive changelogs
  - Release type auto-detection
  - Issue reference extraction
  - Technical documentation integration

**Example output**: See [v1.1.0-milestone-7-complete](https://github.com/rydnr/bytehot/releases/tag/v1.1.0-milestone-7-complete)

### 2. Automated Release Workflow (`automated-release.yml`)

**For comprehensive releases with enhanced features**

- **Triggers**: Version tags (`v*`, `milestone-*`, `*-v*`)
- **Features**:
  - Enhanced metadata extraction
  - Multi-category changelog generation
  - Artifact validation and checksums
  - Breaking change detection
  - Migration guide generation
  - Post-release notifications

### 3. Original Release Workflow (`release.yml`)

**Simple, lightweight releases**

- **Triggers**: `milestone-*`, `v*` tags
- **Features**:
  - Basic changelog generation
  - Standard artifact packaging
  - Simple release notes

## 📊 Commit Convention Integration

The automated system recognizes ByteHot's emoji-based commit conventions:

| Emoji | Category | Examples |
|-------|----------|----------|
| 🧪 | Tests & Validation | `🧪 Add configuration loading test` |
| ✅ | Features & Implementation | `✅ Fix YAML configuration loading` |
| 🔥 | Major Features | `🔥 Add documentation introspection` |
| 📚 | Documentation | `📚 Update GETTING_STARTED guide` |
| 🔒 | Security & Dependencies | `🔒 Upgrade Apache Commons` |
| 🏗️ | Infrastructure | `🏗️ Add GitHub Actions workflows` |
| 🐛 | Bug Fixes | `🐛 Fix null pointer in config` |
| 🚀 | Performance | `🚀 Optimize bytecode validation` |

### Best Practices for Commits

1. **Use descriptive emojis** from ByteHot conventions
2. **Include context** in square brackets: `[Configuration Loading Issue]`
3. **Reference issues** when applicable: `#123` or `[#123]`
4. **Follow TDD methodology** markers for test-related commits

## 🏷️ Tag Naming Conventions

### Stable Releases
```bash
v1.0.0         # Major release
v1.1.0         # Minor release  
v1.1.1         # Patch release
```

### Milestone Releases
```bash
v1.1.0-milestone-7-complete      # Milestone completion
milestone-8-planning             # Milestone planning
milestone-8-documentation        # Milestone phase
```

### Development Releases
```bash
dev-2025.06.21                   # Date-based development
feature-doc-introspection-v1     # Feature branch release
experimental-flow-detection      # Experimental features
```

## 📦 Generated Artifacts

Each release automatically includes:

### JAR Files
- **`bytehot-{version}-agent.jar`** (~14MB): Main agent with all dependencies
- **`bytehot-{version}.jar`** (~300KB): Slim version without dependencies  
- **`bytehot-latest-SNAPSHOT-agent.jar`**: Compatibility alias for CI/CD

### Checksums
- **`SHA256SUMS`**: SHA256 checksums for all artifacts
- **`SHA512SUMS`**: SHA512 checksums for verification

### Documentation
- **Comprehensive release notes** with categorized changes
- **Quick start instructions** and configuration examples
- **Links to documentation** and specifications
- **Migration guides** for breaking changes

## 🔍 Release Types & Behavior

### Stable Releases (`v1.0.0`)
- **Prerelease**: No
- **Latest**: Yes (marked as latest release)
- **Documentation**: Full migration guides
- **Artifacts**: All versions generated

### Milestone Releases (`milestone-X`)
- **Prerelease**: Yes
- **Latest**: No
- **Documentation**: Milestone-specific information
- **Artifacts**: Standard set with milestone metadata

### Development Releases
- **Prerelease**: Yes  
- **Latest**: No
- **Documentation**: Development notes and warnings
- **Artifacts**: Basic set for testing

## 🧪 Quality Assurance

### Automated Testing
Before creating any release, the system:
1. **Runs full test suite** (`mvn clean verify`)
2. **Validates all JAR files** for integrity
3. **Checks artifact sizes** for consistency
4. **Generates checksums** for verification

### Validation Steps
- JAR file integrity verification
- Size consistency checks (agent JAR should be ~14MB)
- Checksum generation and validation
- Release note generation verification

## 📚 Customization

### Modifying Changelog Categories

Edit the Python script in `smart-release.yml` to adjust categories:

```python
categories = {
    '🎯': ('New Category', ['keyword1', 'keyword2']),
    # ... other categories
}
```

### Adjusting Release Templates

Modify the changelog template in either workflow file:

```python
changelog = f"""# ByteHot {tag}

> **Custom Release Information**

## What's Changed
...
"""
```

### Adding Custom Metadata

Extend the metadata extraction in the workflow:

```yaml
- name: Extract custom metadata
  id: custom
  run: |
    # Custom metadata extraction logic
    echo "custom_field=value" >> $GITHUB_OUTPUT
```

## 🔗 Integration Examples

### Manual Release Creation (Testing)

```bash
# Create and push a test tag
git tag -s test-automated-release-v1.0 -m "Test automated release system"
git push origin test-automated-release-v1.0

# Watch the Actions tab for workflow execution
# Release will be created automatically at:
# https://github.com/rydnr/bytehot/releases/tag/test-automated-release-v1.0
```

### CI/CD Pipeline Integration

```yaml
# In your CI/CD pipeline
- name: Create release on successful build
  if: startsWith(github.ref, 'refs/tags/')
  run: |
    echo "Tag detected: ${GITHUB_REF#refs/tags/}"
    echo "Automated release will be created by GitHub Actions"
    # No additional action needed - GitHub Actions handles it
```

### Script-based Tagging

```bash
#!/bin/bash
# release.sh - Helper script for creating releases

VERSION=${1:-""}
TYPE=${2:-"stable"}

if [ -z "$VERSION" ]; then
    echo "Usage: $0 <version> [stable|milestone|dev]"
    exit 1
fi

case $TYPE in
    stable)
        TAG="v$VERSION"
        MESSAGE="ByteHot v$VERSION - Stable Release"
        ;;
    milestone)
        TAG="v$VERSION-milestone-complete"
        MESSAGE="ByteHot Milestone $VERSION Complete"
        ;;
    dev)
        TAG="dev-$(date +%Y.%m.%d)-$VERSION"
        MESSAGE="ByteHot Development Release $VERSION"
        ;;
esac

echo "Creating release: $TAG"
git tag -s "$TAG" -m "$MESSAGE"
git push origin "$TAG"

echo "✅ Release $TAG created and pushed"
echo "🔗 GitHub Actions will create the release automatically"
echo "📋 Monitor progress: https://github.com/rydnr/bytehot/actions"
```

## 🔧 Troubleshooting

### Common Issues

1. **Workflow doesn't trigger**
   - Verify tag name matches patterns in workflow
   - Check that tag was pushed to origin
   - Ensure workflows are enabled in repository settings

2. **Build fails during release**
   - Check test failures in Actions logs
   - Verify Maven configuration is correct
   - Ensure all dependencies are available

3. **Artifacts not generated**
   - Check Maven packaging step for errors
   - Verify JAR files are created in target/ directories
   - Review artifact copying logic in workflow

4. **Release notes missing information**
   - Verify commit messages follow ByteHot conventions
   - Check Python script execution for errors
   - Review commit categorization logic

### Manual Intervention

If automated release fails, you can:

1. **Re-run the workflow** from GitHub Actions tab
2. **Create release manually** using generated artifacts
3. **Debug workflow** by adding debugging steps
4. **Contact maintainers** for complex issues

## 📈 Future Enhancements

Planned improvements to the automated release system:

- **Multi-language changelog** generation
- **Integration with project management** tools
- **Automated dependency updates** in release notes
- **Performance metrics** inclusion
- **User notification** system for releases
- **Release scheduling** and batching

---

This automated release system ensures consistent, comprehensive releases that match ByteHot's professional development standards and provide users with rich, actionable release information.