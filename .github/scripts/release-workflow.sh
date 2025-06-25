#!/bin/bash
set -euo pipefail

# ByteHot Release Workflow Script
# This script handles all release logic previously embedded in YAML workflows

TAG="$1"
JAVA_VERSION="${2:-17}"

echo "🚀 Starting ByteHot release for tag: $TAG"

# Extract tag metadata and validate semver
extract_tag_metadata() {
    local tag="$1"
    
    # Validate and determine release type
    if [[ "$tag" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
        echo "type=stable"
        echo "prerelease=false"
        echo "title=ByteHot Release"
        
        # Extract version components
        IFS='.' read -r major minor patch <<< "$tag"
        echo "major=$major"
        echo "minor=$minor"
        echo "patch=$patch"
        
        # Determine phase based on major version
        if [ "$major" -eq 0 ]; then
            echo "phase=Development"
        elif [ "$major" -eq 1 ]; then
            echo "phase=Core Foundation Complete"
        elif [ "$major" -eq 2 ]; then
            echo "phase=EventSourcing Complete"
        elif [ "$major" -eq 3 ]; then
            echo "phase=Advanced Features"
        else
            echo "phase=Future Phase"
        fi
        
    elif [[ "$tag" =~ milestone ]]; then
        echo "type=milestone"
        echo "prerelease=true"
        echo "title=Milestone Release"
        echo "phase=Milestone"
        # Extract milestone number
        if [[ "$tag" =~ milestone-([0-9a-zA-Z]+) ]]; then
            echo "milestone=${BASH_REMATCH[1]}"
        fi
    elif [[ "$tag" =~ ^v[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
        echo "type=stable"
        echo "prerelease=false"
        echo "title=ByteHot Release"
        echo "phase=Stable"
    elif [[ "$tag" =~ ^test- ]]; then
        echo "type=test"
        echo "prerelease=true"
        echo "title=Test Release"
        echo "phase=Testing"
    else
        echo "❌ Invalid tag format: $tag"
        echo "✅ Valid formats: X.Y.Z (semver), milestone-*, v*, test-*"
        exit 1
    fi
    
    echo "build_date=$(date -u +'%Y-%m-%d %H:%M:%S UTC')"
    echo "commit_short=${GITHUB_SHA:0:8}"
}

# Build and test project
build_and_test() {
    echo "🔨 Building ByteHot $TAG"
    mvn clean verify -B
}

# Package release artifacts
package_artifacts() {
    local tag="$1"
    
    mvn package -DskipTests -B
    
    mkdir -p artifacts
    
    # Copy and rename artifacts
    find . -name "bytehot-*.jar" -path "*/target/*" | while read jar; do
        basename_file=$(basename "$jar")
        # Create version-specific names
        versioned=$(echo "$basename_file" | sed "s/latest-SNAPSHOT/$tag/g")
        cp "$jar" "artifacts/$versioned"
        cp "$jar" "artifacts/$basename_file"  # Keep original for compatibility
    done
    
    # Copy commons jar
    find . -name "java-commons-*.jar" -path "*/target/*" -exec cp {} artifacts/ \;
    
    # Generate checksums
    cd artifacts
    sha256sum *.jar > SHA256SUMS
    sha512sum *.jar > SHA512SUMS
    cd ..
    
    echo "📦 Release artifacts:"
    ls -la artifacts/
}

# Generate changelog
generate_changelog() {
    local tag="$1"
    
    # Get previous tag for comparison
    PREVIOUS_TAG=$(git describe --tags --abbrev=0 HEAD^ 2>/dev/null || echo "")
    
    if [ -n "$PREVIOUS_TAG" ]; then
        COMMIT_RANGE="$PREVIOUS_TAG..HEAD"
        RANGE_DESC="since $PREVIOUS_TAG"
    else
        COMMIT_RANGE=""
        RANGE_DESC="from project start"
    fi
    
    # Use Python script for smart changelog generation
    python3 .github/scripts/generate-changelog.py "$tag" "$COMMIT_RANGE" "$RANGE_DESC" > RELEASE_NOTES.md
    
    echo "📝 Generated changelog:"
    echo "$(wc -l < RELEASE_NOTES.md) lines"
}

# Validate release artifacts
validate_artifacts() {
    local tag="$1"
    
    echo "🔍 Validating release artifacts"
    
    # Check that main artifacts exist
    if [ ! -f "artifacts/bytehot-${tag}-agent.jar" ] && [ ! -f "artifacts/bytehot-${tag}.jar" ]; then
        echo "❌ Main JAR artifacts not found!"
        exit 1
    fi
    
    # Validate JAR files can be read
    for jar in artifacts/*.jar; do
        if ! jar tf "$jar" >/dev/null 2>&1; then
            echo "❌ Invalid JAR file: $jar"
            exit 1
        fi
        echo "✅ Validated: $jar"
    done
    
    echo "✅ All artifacts validated successfully"
}

# Create GitHub Release
create_github_release() {
    local tag="$1"
    local metadata_file="$2"
    
    # Source metadata
    source "$metadata_file"
    
    # Create release using GitHub CLI
    gh release create "$tag" \
        --title "ByteHot $tag - $title" \
        --notes-file RELEASE_NOTES.md \
        --prerelease="$prerelease" \
        artifacts/*.jar artifacts/SHA256SUMS artifacts/SHA512SUMS
}

# Main execution
main() {
    # Extract metadata and save to file
    METADATA_FILE=$(mktemp)
    extract_tag_metadata "$TAG" > "$METADATA_FILE"
    
    # Build and test
    build_and_test
    
    # Package artifacts
    package_artifacts "$TAG"
    
    # Generate changelog
    generate_changelog "$TAG"
    
    # Validate artifacts
    validate_artifacts "$TAG"
    
    # Create GitHub release
    create_github_release "$TAG" "$METADATA_FILE"
    
    # Cleanup
    rm "$METADATA_FILE"
    
    echo "🎉 Release $TAG completed successfully!"
    echo "🔗 Release URL: https://github.com/${GITHUB_REPOSITORY}/releases/tag/$TAG"
}

main