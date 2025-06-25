#!/bin/bash
set -euo pipefail

# ByteHot Javadocs Copy Script
# Copies generated Javadocs to documentation site

echo "📖 Copying Javadocs to documentation site..."

if [ -d "target/site/apidocs" ]; then
    # Ensure javadocs directory exists
    mkdir -p bytehot/javadocs/
    
    # Copy Javadocs content
    cp -r target/site/apidocs/* bytehot/javadocs/ 2>/dev/null || true
    
    echo "✅ Javadocs copied to documentation site"
    
    # List what was copied
    echo "📋 Javadocs files copied:"
    find bytehot/javadocs -name "*.html" -type f 2>/dev/null | head -10 || echo "No HTML files found"
    echo "📊 Total Javadoc files: $(find bytehot/javadocs -name "*.html" -type f 2>/dev/null | wc -l)"
else
    echo "⚠️ No Javadocs found to copy"
    
    # Create placeholder if javadocs directory doesn't exist
    mkdir -p bytehot/javadocs/
    echo "<html><body><h1>Javadocs</h1><p>Javadocs generation in progress...</p></body></html>" > bytehot/javadocs/index.html
    echo "📄 Created Javadocs placeholder"
fi