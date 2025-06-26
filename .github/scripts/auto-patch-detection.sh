#!/bin/bash
set -euo pipefail

# ByteHot Auto Patch Detection Script
# Analyzes commits to determine if a patch release should be created

JAVA_VERSION="${1:-17}"

# Analyze commit for patch-level changes
detect_patch_changes() {
    # Get the last commit message (first line only to avoid multiline issues)
    COMMIT_MSG=$(git log -1 --pretty=%s)
    echo "commit_msg=$COMMIT_MSG" >&2
    
    # Check if this is a patch-worthy commit or manual dispatch
    PATCH_WORTHY=false
    CATEGORY=""
    
    # If manually triggered, force patch creation
    if [ "${GITHUB_EVENT_NAME:-}" = "workflow_dispatch" ]; then
        PATCH_WORTHY=true
        CATEGORY="manual"
        echo "🎯 Manual patch release triggered" >&2
    fi
    
    # Bug fix patterns
    if [[ "$COMMIT_MSG" =~ ^(fix:|🐛|✅.*fix|✅.*Fix) ]] || \
       [[ "$COMMIT_MSG" =~ Fix.*bug ]] || \
       [[ "$COMMIT_MSG" =~ Resolve.*issue ]]; then
        PATCH_WORTHY=true
        CATEGORY="bugfix"
    fi
    
    # Security patterns  
    if [[ "$COMMIT_MSG" =~ ^(security:|🔒) ]] || \
       [[ "$COMMIT_MSG" =~ Security.*fix ]] || \
       [[ "$COMMIT_MSG" =~ vulnerability ]]; then
        PATCH_WORTHY=true
        CATEGORY="security"
    fi
    
    # Configuration fix patterns
    if [[ "$COMMIT_MSG" =~ ^(config:|🔧) ]] || \
       [[ "$COMMIT_MSG" =~ Configuration.*fix ]] || \
       [[ "$COMMIT_MSG" =~ configuration.*loading ]]; then
        PATCH_WORTHY=true
        CATEGORY="config"
    fi
    
    # Performance patterns
    if [[ "$COMMIT_MSG" =~ ^(perf:|⚡) ]] || \
       [[ "$COMMIT_MSG" =~ Performance.*improvement ]]; then
        PATCH_WORTHY=true
        CATEGORY="performance"
    fi
    
    echo "patch_worthy=$PATCH_WORTHY"
    echo "category=$CATEGORY"
    
    if [ "$PATCH_WORTHY" = "true" ]; then
        echo "🎯 Patch-level change detected: $COMMIT_MSG" >&2
    else
        echo "ℹ️ No patch-level change detected" >&2
    fi
}

# Get current version and calculate next patch version
get_version_info() {
    # Get the latest semver tag
    LATEST_TAG=$(git tag --list --sort=-version:refname | grep -E '^[0-9]+\.[0-9]+\.[0-9]+$' | head -1)
    
    if [ -z "$LATEST_TAG" ]; then
        echo "No existing semver tags found, starting with 1.0.0"
        echo "current=1.0.0"
        echo "next=1.0.1"
    else
        echo "current=$LATEST_TAG"
        
        # Parse version components  
        IFS='.' read -r major minor patch <<< "$LATEST_TAG"
        
        # Increment patch version
        new_patch=$((patch + 1))
        NEW_VERSION="$major.$minor.$new_patch"
        
        echo "next=$NEW_VERSION"
        echo "📈 Version increment: $LATEST_TAG → $NEW_VERSION"
    fi
}

# Check if tag already exists
check_tag_exists() {
    local new_version="$1"
    
    if git tag --list | grep -q "^$new_version$"; then
        echo "exists=true"
        echo "⚠️ Tag $new_version already exists, skipping auto-release"
        return 0
    else
        echo "exists=false"
        echo "✅ Tag $new_version is available for creation"
        return 1
    fi
}

# Test build before tagging
test_build() {
    echo "🧪 Running tests before creating patch release..."
    
    # First ensure dependencies are built and installed
    echo "📦 Building project without tests..."
    mvn clean install -DskipTests -q
    
    # Run tests
    echo "🧪 Running test suite to verify basic functionality..."
    mvn test -Dsurefire.failIfNoSpecifiedTests=false -q
    BROAD_TEST_EXIT=$?
    
    if [ $BROAD_TEST_EXIT -eq 0 ]; then
        echo "✅ Test suite passed, proceeding with patch release"
    else
        echo "❌ Critical test failures detected, aborting auto patch release"
        exit 1
    fi
}

# Create automated patch tag
create_patch_tag() {
    local new_version="$1"
    local category="$2"
    local commit_msg="$3"
    
    BUILD_DATE=$(date -u +'%Y-%m-%d %H:%M:%S UTC')
    
    # Configure git for tagging
    git config user.name "github-actions[bot]"
    git config user.email "github-actions[bot]@users.noreply.github.com"
    
    # Create tag message based on category
    case "$category" in
        "bugfix")
            TAG_MSG="ByteHot $new_version - Bug Fix

🐛 **Automated Patch Release**
- **Change Type**: Bug Fix
- **Trigger Commit**: $commit_msg
- **Auto-Generated**: $BUILD_DATE

This patch release was automatically created based on bug fix commit detection.

🤖 Generated with [Claude Code](https://claude.ai/code)

Co-Authored-By: Claude <noreply@anthropic.com>"
            ;;
        "security")
            TAG_MSG="ByteHot $new_version - Security Fix

🔒 **Automated Security Patch**
- **Change Type**: Security Fix
- **Trigger Commit**: $commit_msg
- **Auto-Generated**: $BUILD_DATE

This security patch was automatically created based on security-related commit detection.

🤖 Generated with [Claude Code](https://claude.ai/code)

Co-Authored-By: Claude <noreply@anthropic.com>"
            ;;
        "config")
            TAG_MSG="ByteHot $new_version - Configuration Fix

🔧 **Automated Configuration Patch**
- **Change Type**: Configuration Fix
- **Trigger Commit**: $commit_msg
- **Auto-Generated**: $BUILD_DATE

This patch release was automatically created based on configuration fix commit detection.

🤖 Generated with [Claude Code](https://claude.ai/code)

Co-Authored-By: Claude <noreply@anthropic.com>"
            ;;
        "performance")
            TAG_MSG="ByteHot $new_version - Performance Improvement

⚡ **Automated Performance Patch**
- **Change Type**: Performance Improvement
- **Trigger Commit**: $commit_msg
- **Auto-Generated**: $BUILD_DATE

This patch release was automatically created based on performance improvement commit detection.

🤖 Generated with [Claude Code](https://claude.ai/code)

Co-Authored-By: Claude <noreply@anthropic.com>"
            ;;
        "manual")
            TAG_MSG="ByteHot $new_version - Manual Patch Release

🎯 **Manual Patch Release**
- **Change Type**: Manual Release
- **Trigger Commit**: $commit_msg
- **Auto-Generated**: $BUILD_DATE

This patch release was manually triggered via GitHub Actions workflow dispatch.

🤖 Generated with [Claude Code](https://claude.ai/code)

Co-Authored-By: Claude <noreply@anthropic.com>"
            ;;
        *)
            TAG_MSG="ByteHot $new_version - Automated Patch

🔄 **Automated Patch Release**
- **Change Type**: General Fix
- **Trigger Commit**: $commit_msg
- **Auto-Generated**: $BUILD_DATE

This patch release was automatically created based on patch-level commit detection.

🤖 Generated with [Claude Code](https://claude.ai/code)

Co-Authored-By: Claude <noreply@anthropic.com>"
            ;;
    esac
    
    # Create and push the tag
    git tag -a "$new_version" -m "$TAG_MSG"
    git push origin "$new_version"
    
    echo "🚀 Created and pushed tag: $new_version"
    echo "📋 Category: $category"
}

# Main execution
main() {
    echo "🔍 Analyzing commit for patch-level changes..."
    
    # Detect patch changes
    PATCH_INFO=$(detect_patch_changes)
    eval "$PATCH_INFO"
    
    if [ "$patch_worthy" != "true" ]; then
        echo "ℹ️ No patch-level changes detected in this commit"
        exit 0
    fi
    
    # Get version information
    VERSION_INFO=$(get_version_info)
    eval "$VERSION_INFO"
    
    # Check if tag already exists
    if check_tag_exists "$next"; then
        echo "⚠️ Patch-level change detected but tag already exists"
        exit 0
    fi
    
    # Test build
    test_build
    
    # Create patch tag
    create_patch_tag "$next" "$category" "$commit_msg"
    
    echo "✅ Automated patch release completed: $next"
}

main