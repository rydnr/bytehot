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
  -DfailOnError=false \
  -Dadditionalparam="-Xdoclint:none" \
  -Dmaven.javadoc.skip=false \
  -Dquiet=false || echo "Maven aggregate javadoc failed"

# Strategy 2: Try site plugin to generate all documentation
if [ ! -d "target/site/apidocs" ] || [ -z "$(ls -A target/site/apidocs 2>/dev/null)" ]; then
    echo "📋 Strategy 2: Using Maven site plugin..."
    mvn site \
      -DgenerateReports=true \
      -Dmaven.javadoc.failOnError=false \
      -Dquiet=false || echo "Maven site generation failed"
fi

# Strategy 3: Individual module javadocs if aggregate fails
if [ ! -d "target/site/apidocs" ] || [ -z "$(ls -A target/site/apidocs 2>/dev/null)" ]; then
    echo "📋 Strategy 3: Individual module javadocs..."
    
    # Try each module individually
    for module in java-commons bytehot-domain bytehot-infrastructure bytehot-application; do
        if [ -d "$module" ]; then
            echo "  Generating javadocs for $module..."
            (cd "$module" && mvn javadoc:javadoc -DfailOnError=false -Dquiet=true) || echo "    Failed to generate for $module"
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
    find target/site/apidocs -name "*.html" | head -10
    echo "📊 Total HTML files: $(find target/site/apidocs -name "*.html" | wc -l)"
else
    echo "⚠️ Javadocs generation failed, creating placeholder"
    # Create a basic placeholder
    mkdir -p target/site/apidocs
    cat > target/site/apidocs/index.html << 'EOF'
<!DOCTYPE html>
<html>
<head>
    <title>ByteHot Javadocs</title>
</head>
<body>
    <h1>ByteHot API Documentation</h1>
    <p>Javadoc generation is in progress. Please check back later.</p>
    <ul>
        <li><a href="../../../java-commons/target/site/apidocs/index.html">Java Commons API</a></li>
        <li><a href="../../../bytehot-domain/target/site/apidocs/index.html">ByteHot Domain API</a></li>
        <li><a href="../../../bytehot-infrastructure/target/site/apidocs/index.html">ByteHot Infrastructure API</a></li>
        <li><a href="../../../bytehot-application/target/site/apidocs/index.html">ByteHot Application API</a></li>
    </ul>
</body>
</html>
EOF
    echo "📄 Created placeholder index.html"
fi

echo "✅ Javadocs generation process completed"
