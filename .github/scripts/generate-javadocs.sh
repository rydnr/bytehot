#!/bin/bash
set -euo pipefail

# ByteHot Javadocs Generation Script
# Generates comprehensive Javadocs for all modules

echo "🔧 Attempting to generate Javadocs..."

# Create javadocs directories for all modules
mkdir -p target/site/apidocs

# Strategy 1: Try Maven aggregate javadoc for all modules
echo "📋 Strategy 1: Maven aggregate javadoc for all modules..."
mvn javadoc:aggregate \
    -DfailOnError=true \
    -Dadditionalparam="-Xdoclint:none" \
    -Dmaven.javadoc.skip=false \
    -Dquiet=false || echo "Maven aggregate javadoc failed"

# Strategy 2: Try site plugin to generate all documentation
if [ ! -d "target/site/apidocs" ] || [ -z "$(ls -A target/site/apidocs 2>/dev/null)" ]; then
    echo "📋 Strategy 2: Using Maven site plugin..."
    mvn site \
        -DgenerateReports=true \
        -Dmaven.javadoc.failOnError=true \
        -Dquiet=false || echo "Maven site generation failed"
fi

# Strategy 3: Individual module javadocs if aggregate fails
if [ ! -d "target/site/apidocs" ] || [ -z "$(ls -A target/site/apidocs 2>/dev/null)" ]; then
    echo "📋 Strategy 3: Individual module javadocs..."

    # Try each module individually
    for module in java-commons bytehot-domain bytehot-infrastructure bytehot-application; do
        if [ -d "$module" ]; then
            echo "  Generating javadocs for $module..."
            (cd "$module" && mvn javadoc:javadoc -DfailOnError=true -Dquiet=false) || echo "    Failed to generate for $module"
        fi
    done

    # Aggregate results manually
    echo "  Aggregating individual results..."
    mkdir -p target/site/apidocs
    for module in java-commons bytehot-domain bytehot-infrastructure bytehot-application; do
        if [ -d "$module/target/site/apidocs" ]; then
            cp -r "$module/target/site/apidocs"/* target/site/apidocs/ 2>/dev/null || true
        fi
    done
fi

# Verify javadocs were generated
if [ -d "target/site/apidocs" ] && [ -n "$(ls -A target/site/apidocs 2>/dev/null)" ]; then
    echo "✅ Javadocs generated successfully"
    echo "📊 Generated files:"
    find target/site/apidocs -name "*.html" 2>/dev/null | head -10 || echo "No HTML files found to list"
else
    echo "❌ Javadocs generation failed completely"
    echo "All generation strategies failed:"
    echo "  - Maven aggregate javadoc"
    echo "  - Maven site plugin"
    echo "  - Individual module javadocs"
    echo ""
    echo "This indicates a real problem that needs to be fixed."
    echo "Check the Maven output above for specific errors."
    exit 1
fi

echo "✅ Javadocs generation process completed"
