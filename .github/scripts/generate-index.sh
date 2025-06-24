#!/bin/bash
set -euo pipefail

# Generate index.html from story.org

echo "📄 Creating index page from story.org..."

CSS="$(source ./.github/scripts/css.sh)"
NAV="$(source ./.github/scripts/nav.sh)"
FOOTER="$(source ./.github/scripts/footer.sh)"
MATRIX="$(source ./.github/scripts/matrix.sh)"
# Ensure bytehot directory exists
mkdir -p bytehot

if [ -f "story.org" ]; then
    echo "📖 Found story.org, converting to HTML..."

    # Convert org to HTML content first
    temp_content=$(mktemp)
    if pandoc -f org -t html5 --toc story.org -o "$temp_content" --metadata title="ByteHot - Revolutionary JVM Hot-Swapping Agent"; then
        echo "✅ Pandoc conversion successful"

        # Create complete HTML structure
        cat >bytehot/index.html <<'HTML_EOF'
<!DOCTYPE html>
<html>
<head>
    <title>ByteHot - Revolutionary JVM Hot-Swapping Agent</title>
  <style>
${CSS}
  </style>
</head>
<body>
    <div class="matrix-bg"></div>

    <nav class="nav-header">
${NAV}
    </nav>
    <div class="content">

HTML_EOF

        # Add converted content
        cat "$temp_content" >>bytehot/index.html

        # Close HTML structure
        cat <<EOF >>bytehot/index.html
</div>
${FOOTER}
<script>
${MATRIX}
</script>
</body>
</html>
EOF
        echo "✅ Index page created from story.org"

        # Clean up temp file
        rm "$temp_content"
    else
        echo "⚠️ Pandoc conversion failed, using fallback"
        cat >bytehot/index.html <<'HTML_EOF'
<!DOCTYPE html>
<html>
<head>
    <title>ByteHot - Revolutionary JVM Hot-Swapping Agent</title>
    <style>
${CSS}
    </style>
</head>
<body>
    <div class="matrix-bg"></div>

    <nav class="nav-header">
${NAV}
    </nav>
    <div class="content">
    <h1>🔥 ByteHot - Revolutionary JVM Hot-Swapping Agent</h1>
    <p><em>A revolutionary JVM agent enabling bytecode hot-swapping at runtime</em></p>
    
    <h2>🚀 Key Features</h2>
    <ul>
        <li>🔄 Real-time bytecode hot-swapping without JVM restart</li>
        <li>🏗️ Domain-Driven Design with hexagonal architecture</li>
        <li>📊 Comprehensive event-driven testing framework</li>
        <li>🛡️ Advanced error recovery and rollback mechanisms</li>
        <li>📚 Revolutionary literate programming documentation</li>
    </ul>
    
    <p>📖 <strong>Note:</strong> The full documentation from story.org could not be converted. Please check the source file.</p>
</div>
${FOOTER}
<script>
${MATRIX}
</script>
</body>
</html>
HTML_EOF
        # Clean up temp file
        rm -f "$temp_content"
    fi
else
    echo "⚠️ story.org not found"
    exit 1
fi
