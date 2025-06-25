#!/bin/bash
set -euo pipefail

# Create Missing HTML Files Script
# This script creates simple content files that will later be styled by apply-new-style.sh

echo "🔧 Creating missing HTML content files..."

# Create bytehot directory if not exists
mkdir -p bytehot

CSS="$(source ./.github/scripts/css.sh)"
NAV_CHILD="$(source ./.github/scripts/nav-child.sh)"
FOOTER="$(source ./.github/scripts/footer.sh)"
MATRIX="$(source ./.github/scripts/matrix.sh)"
# Convert GETTING_STARTED.org to simple HTML content (only if not already exists)
if [ -f "GETTING_STARTED.org" ] && [ ! -f "bytehot/GETTING_STARTED.html" ]; then
    echo "📖 Converting GETTING_STARTED.org to simple content..."

    # Convert to simple HTML without full styling (styling will be added later)
    pandoc GETTING_STARTED.org -o bytehot/GETTING_STARTED.html \
        --metadata title="Getting Started" || echo "⚠️ Failed to convert GETTING_STARTED.org"

    echo "✅ Created GETTING_STARTED.html content"
elif [ -f "bytehot/GETTING_STARTED.html" ]; then
    echo "✅ GETTING_STARTED.html already exists, skipping conversion"
fi

# Convert key org files to simple HTML content
declare -a org_files=("ErrorHandler" "RollbackManager" "InstanceTracker" "FrameworkIntegration")

for org_file in "${org_files[@]}"; do
    if [ -f "docs/${org_file}.org" ]; then
        echo "📖 Converting docs/${org_file}.org to simple content..."

        # Convert to simple HTML without full styling
        pandoc "docs/${org_file}.org" -o "bytehot/${org_file}.html" \
            --metadata title="${org_file}" || echo "⚠️ Failed to convert docs/${org_file}.org"

        echo "✅ Created ${org_file}.html content"
    else
        echo "⚠️ docs/${org_file}.org not found, creating placeholder..."

        # Create placeholder content for missing org files
        cat >"bytehot/${org_file}.html" <<EOF
<h1>${org_file}</h1>
<p>This is the ${org_file} class documentation.</p>
<h2>Overview</h2>
<p>The ${org_file} class is part of ByteHot's comprehensive architecture.</p>
<h2>Implementation</h2>
<p>Documentation for this class is being developed. Please check back soon for detailed implementation notes.</p>
<h2>Related Classes</h2>
<p>See also: <a href="ByteHot.html">ByteHot</a>, <a href="ByteHotApplication.html">ByteHotApplication</a></p>
EOF
        echo "✅ Created ${org_file}.html placeholder"
    fi
done

# Create javadocs directory with placeholder if it doesn't exist
if [ ! -d "bytehot/javadocs" ]; then
    echo "📚 Creating javadocs directory with placeholder..."
    mkdir -p bytehot/javadocs
    cat >bytehot/javadocs/index.html <<'EOF'
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>JavaDocs - ByteHot</title>
    <style>
${CSS}
        body { font-family: 'Courier New', monospace; background: linear-gradient(135deg, #0f0f23 0%, #1a1a3a 100%); color: #00ff00; line-height: 1.6; margin: 0; padding: 2rem; }
        .container { max-width: 1200px; margin: 0 auto; background: rgba(26, 26, 58, 0.8); border: 1px solid #00ff00; border-radius: 8px; padding: 2rem; text-align: center; }
        h1 { color: #00cccc; text-shadow: 0 0 10px #00cccc; font-size: 2.5rem; margin-bottom: 2rem; }
        p { color: #ffffff; font-size: 1.2rem; margin: 1rem 0; }
        a { color: #00cccc; text-decoration: none; padding: 0.5rem 1rem; border: 1px solid #00cccc; border-radius: 4px; display: inline-block; margin: 0.5rem; }
        a:hover { background: rgba(0, 204, 204, 0.1); text-shadow: 0 0 10px #00cccc; }
        .nav { margin-bottom: 2rem; }
        .nav a { margin: 0 1rem; }
    </style>
</head>
<body>
    <div class="container">
        <div class="nav">
${NAV_CHILD}
        </div>
        
        <h1>📚 ByteHot JavaDocs</h1>
        
        <p>📋 JavaDoc generation is in progress...</p>
        
        <p>The comprehensive API documentation will be available here once the build process completes.</p>
        
        <div style="margin: 2rem 0;">
            <a href="../events/">📡 Domain Events Documentation</a>
            <a href="../flows/">🌊 Process Flow Documentation</a>
            <a href="../ports/">🚪 Port Interface Documentation</a>
        </div>
        
        <p style="margin-top: 2rem; color: #888;">
            In the meantime, explore our comprehensive literate programming documentation above.
        </p>
${FOOTER}
      <script>
${MATRIX}
      </script>
    </div>
</body>
</html>
EOF
    echo "✅ Created javadocs placeholder"
fi

echo "✅ Missing HTML files creation completed!"
