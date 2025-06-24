#!/bin/bash
set -euo pipefail

# Create Clean Documentation Files Script
# This script creates clean HTML files from org sources with proper styling

echo "🧹 Creating clean documentation files..."

# Create bytehot directory if not exists
mkdir -p bytehot

# Function to create a properly styled HTML file
create_styled_html() {
    local org_file="$1"
    local output_file="$2"
    local title="$3"
    local css="$(source css.sh)"
    local nav="$(source nav.sh)"
    local matrix="$(source matrix.sh)"
    local footer="$(source footer.sh)"

    echo "📖 Creating $output_file from $org_file..."

    if [ ! -f "$org_file" ]; then
        echo "⚠️ Source file $org_file not found, creating placeholder..."

        # Create placeholder content
        local class_name=$(basename "$output_file" .html)
        cat >"$output_file" <<EOF
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>$title - ByteHot Documentation</title>
    <style>
${css}
    </style>
</head>
<body>
    <div class="matrix-bg"></div>
    
    <nav class="nav-header">
${nav}
    </nav>

    <div class="content">
        <div class="doc-container">
            <h1>$class_name</h1>
            
            <h2>Overview</h2>
            <p>The <code>$class_name</code> class is part of ByteHot's comprehensive architecture for runtime bytecode hot-swapping.</p>
            
            <h2>Responsibilities</h2>
            <p>This class handles essential functionality within the ByteHot system, following Domain-Driven Design principles and hexagonal architecture patterns.</p>
            
            <h2>Implementation</h2>
            <p>Detailed implementation documentation for this class is being developed. Please check back soon for comprehensive literate programming documentation.</p>
            
            <h2>Related Classes</h2>
            <p>See also:</p>
            <ul>
                <li><a href="ByteHot.html">ByteHot</a> - Core domain aggregate</li>
                <li><a href="ByteHotApplication.html">ByteHotApplication</a> - Application layer coordination</li>
                <li><a href="literate-docs.html">Complete Class Index</a> - All documented classes</li>
            </ul>
        </div>
    </div>

    <footer class="footer">
${footer}
    </footer>

    <script>
${matrix}
    </script>
</body>
</html>
EOF
        echo "✅ Created placeholder $output_file"
        return
    fi

    # Extract content from org file using pandoc to get clean HTML content
    temp_content=$(mktemp)

    # Convert org to clean HTML content only
    pandoc "$org_file" -t html --no-highlight 2>/dev/null |
        sed -n '/<h1/,/<\/html>/p' |
        sed '/<\/html>/d' >"$temp_content"

    # If pandoc failed or produced no content, create placeholder
    if [ ! -s "$temp_content" ]; then
        echo "⚠️ Pandoc conversion failed, creating placeholder for $output_file"
        local class_name=$(basename "$output_file" .html)
        cat >"$temp_content" <<EOF
<h1>$class_name</h1>
<h2>Overview</h2>
<p>Documentation for the <code>$class_name</code> class.</p>
<h2>Implementation</h2>
<p>Detailed implementation notes will be available soon.</p>
EOF
    fi

    # Create the complete HTML file with styling and navigation
    cat >"$output_file" <<EOF
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>$title - ByteHot Documentation</title>
    <style>
${css}
    </style>
</head>
<body>
    <div class="matrix-bg"></div>
    
    <nav class="nav-header">
${nav}
    </nav>

    <div class="content">
        <div class="doc-container">
EOF

    # Add the converted content
    cat "$temp_content" >>"$output_file"

    # Close the HTML structure
    cat >>"$output_file" <<'EOF'
        </div>
    </div>

    <footer class="footer">
${footer}
    </footer>

    <script>
${matrix}
    </script>
</body>
</html>
EOF

    # Clean up
    rm -f "$temp_content"
    echo "✅ Created $output_file"
}

# Create the missing documentation files
create_styled_html "docs/ErrorHandler.org" "bytehot/ErrorHandler.html" "ErrorHandler"
create_styled_html "docs/RollbackManager.org" "bytehot/RollbackManager.html" "RollbackManager"
create_styled_html "docs/InstanceTracker.org" "bytehot/InstanceTracker.html" "InstanceTracker"
create_styled_html "docs/FrameworkIntegration.org" "bytehot/FrameworkIntegration.html" "FrameworkIntegration"

echo "✅ Clean documentation files created successfully!"
