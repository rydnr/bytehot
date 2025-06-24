#!/bin/bash
set -euo pipefail

# Generate journal.html from journal.org

echo "📔 Creating journal page from journal.org..."

# Ensure bytehot directory exists
mkdir -p bytehot

CSS="$(source ./.github/scripts/css.sh)"
NAV="$(source ./.github/scripts/nav.sh)"
FOOTER="$(source ./.github/scripts/footer.sh)"
MATRIX="$(source ./.github/scripts/matrix.sh)"
if [ -f "journal.org" ]; then
    echo "📖 Found journal.org, converting to HTML..."

    # Convert org to HTML content first
    temp_content=$(mktemp)
    if pandoc -f org -t html5 --toc journal.org -o "$temp_content" --metadata title="ByteHot Development Journal"; then
        echo "✅ Journal pandoc conversion successful"

        # Create complete HTML structure with new matrix style
        cat >bytehot/journal.html <<'HTML_EOF'
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ByteHot Development Journal</title>
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
        <div class="journal-container">
            <h1>📔 ByteHot Development Journal</h1>
HTML_EOF

        # Add converted content
        cat "$temp_content" >>bytehot/journal.html

        # Close HTML structure
        cat >>bytehot/journal.html <<'HTML_EOF'
        </div>
    </div>
${FOOTER}
<script>
${MATRIX}
</script>
</body>
</html>
HTML_EOF
        echo "✅ Journal page created from journal.org"

        # Clean up temp file
        rm "$temp_content"
    else
        echo "⚠️ Journal pandoc conversion failed, creating basic fallback"
        cat >bytehot/journal.html <<'HTML_EOF'
<!DOCTYPE html>
<html>
<head>
    <title>ByteHot Development Journal</title>
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
    <h1>📔 ByteHot Development Journal</h1>
    <p><em>Documentation of the development process and conversations</em></p>
    <p>📖 <strong>Note:</strong> The journal content from journal.org could not be converted. Please check the source file.</p>
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
    echo "⚠️ journal.org not found"
    exit 1
fi
