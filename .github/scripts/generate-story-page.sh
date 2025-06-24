#!/bin/bash
set -euo pipefail

# Generate story.html from story.org with new matrix style

echo "📖 Creating story page from story.org..."

# Ensure bytehot directory exists
mkdir -p bytehot

CSS="$(source ./.github/scripts/css.sh)"
NAV="$(source ./.github/scripts/nav.sh)"
FOOTER="$(source ./.github/scripts/footer.sh)"
MATRIX="$(source ./.github/scripts/matrix.sh)"

if [ -f "story.org" ]; then
    echo "📄 Found story.org, converting to HTML with new style..."

    # Convert org to HTML content first
    temp_content=$(mktemp)
    if pandoc -f org -t html5 --toc story.org -o "$temp_content" --metadata title="ByteHot Story"; then
        echo "✅ Pandoc conversion successful"

        # Create story page with new matrix style
        cat >bytehot/story.html <<'HTML_EOF'
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ByteHot Story - Revolutionary Development Journey</title>
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
        <h1 class="story-title">📖 ByteHot Story</h1>
        <p style="text-align: center; color: #00cccc; font-size: 1.2rem; margin-bottom: 3rem;">
            The Revolutionary Journey of JVM Hot-Swapping Development
        </p>
        
        <div class="story-content">
HTML_EOF

        # Add converted content
        cat "$temp_content" >>bytehot/story.html

        # Close HTML structure
        cat >>bytehot/story.html <<'HTML_EOF'
        </div>
    </div>

${FOOTER}
    <script>
${MATRIX}
    </script>
</body>
</html>
HTML_EOF

        echo "✅ Story page created from story.org"

        # Clean up temp file
        rm "$temp_content"
    else
        echo "⚠️ Pandoc conversion failed"
        exit 1
    fi
fi
