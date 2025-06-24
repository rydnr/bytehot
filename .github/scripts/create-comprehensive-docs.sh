#!/bin/bash
set -euo pipefail

# Comprehensive Documentation Creation Script
# This script creates all missing documentation files with consistent styling and cross-references

echo "🚀 Creating comprehensive documentation with literate programming links..."

# Create bytehot directory if not exists
mkdir -p bytehot

CSS="$(source ./.github/scripts/css.sh)"
NAV="$(source ./.github/scripts/nav.sh)"
FOOTER="$(source ./.github/scripts/footer.sh)"
MATRIX="$(source ./.github/scripts/matrix.sh)"
# Common navigation template that matches the root index
read -r -d '' COMMON_NAV_TEMPLATE <<'EOF' || true
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>$title$</title>
    <style>
${CSS}
    </style>
</head>
<body>
    <div class="matrix-bg"></div>
    
    <nav class="nav-header">
        <div class="nav-links">
${NAV}
        </div>
    </nav>

    <div class="container">
        $body$

${FOOTER}
    </div>

    <script>
${matrix}
    </script>
</body>
</html>
EOF

# Function to convert org file to HTML with common template
convert_org_to_html() {
    local org_file=$1
    local html_file=$2
    local title=$3

    if [ -f "$org_file" ]; then
        echo "📖 Converting $org_file to $html_file..."

        # Create temporary template file
        temp_template=$(mktemp)
        echo "$COMMON_NAV_TEMPLATE" >"$temp_template"

        pandoc "$org_file" -o "$html_file" \
            --metadata title="$title" \
            --template="$temp_template" || echo "⚠️ Failed to convert $org_file"

        rm -f "$temp_template"
        echo "✅ Created $html_file"
    else
        echo "⚠️ $org_file not found"
    fi
}

# Create missing documentation files
echo "📋 Creating missing documentation files..."

# Implementation overview
if [ ! -f "bytehot/implementation.html" ]; then
    echo "🏗️ Creating implementation.html..."
    cat >bytehot/implementation.html <<'EOF'
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Implementation - ByteHot Documentation</title>
    <style>
${CSS}
    </style>
</head>
<body>
    <div class="matrix-bg"></div>
    
    <nav class="nav-header">
${NAV}
    </nav>

    <div class="container">
        <h1>🏗️ ByteHot Implementation Guide</h1>
        
        <p>ByteHot follows a strict Domain-Driven Design (DDD) and Hexagonal Architecture pattern, ensuring maintainable, testable, and extensible code that evolves with your needs.</p>
        
        <h2>🎯 Core Architecture Components</h2>
        <div class="docs-grid">
            <a href="ByteHot.html" class="docs-link">🏗️ Core Architecture</a>
            <a href="ByteHotApplication.html" class="docs-link">🎯 Application Layer</a>
            <a href="Ports.html" class="docs-link">🔌 Ports & Adapters</a>
            <a href="ByteHotAgent.html" class="docs-link">🤖 JVM Agent</a>
        </div>
        
        <h2>🔧 Infrastructure Components</h2>
        <div class="docs-grid">
            <a href="ErrorHandler.html" class="docs-link">🛡️ Error Handling</a>
            <a href="RollbackManager.html" class="docs-link">↩️ Rollback System</a>
            <a href="InstanceTracker.html" class="docs-link">📊 Instance Tracking</a>
            <a href="FrameworkIntegration.html" class="docs-link">🔗 Framework Integration</a>
        </div>
        
        <h2>📡 Event-Driven Architecture</h2>
        <div class="docs-grid">
            <a href="events/" class="docs-link">📡 Domain Events</a>
            <a href="flows/" class="docs-link">🌊 Process Flows</a>
            <a href="ports/" class="docs-link">🚪 Port Interfaces</a>
        </div>
${FOOTER}
    </div>
</body>
</html>
EOF
    echo "✅ Created implementation.html"
fi

# Convert specific org files to HTML with common template
convert_org_to_html "docs/ErrorHandler.org" "bytehot/ErrorHandler.html" "Error Handler - ByteHot Documentation"
convert_org_to_html "docs/RollbackManager.org" "bytehot/RollbackManager.html" "Rollback Manager - ByteHot Documentation"
convert_org_to_html "docs/InstanceTracker.org" "bytehot/InstanceTracker.html" "Instance Tracker - ByteHot Documentation"
convert_org_to_html "docs/FrameworkIntegration.org" "bytehot/FrameworkIntegration.html" "Framework Integration - ByteHot Documentation"

echo "✅ Missing documentation files creation completed!"
