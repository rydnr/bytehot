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

# Verify javadocs were generated
if [ -d "target/site/apidocs" ] && [ -n "$(ls -A target/site/apidocs 2>/dev/null)" ]; then
    echo "✅ Javadocs generated successfully"
    ls -la target/site/apidocs/
else
    echo "⚠️ Javadocs generation failed"
    exit 1
fi
