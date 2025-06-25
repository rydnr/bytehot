#!/bin/bash
set -euo pipefail

FILE="$1"
TITLE="$2"
SUBTITLE="$3"
NAME="$(basename "${FILE}")"
# Generate ${NAME}.html from ${FILE}

echo "📔 Creating ${NAME} page from ${FILE}..."

# Ensure bytehot directory exists
mkdir -p bytehot

CSS="$(source ./.github/scripts/css.sh)"
NAV="$(source ./.github/scripts/nav.sh)"
FOOTER="$(source ./.github/scripts/footer.sh)"
MATRIX="$(source ./.github/scripts/matrix.sh)"
if [ -f "${FILE}" ]; then
    echo "📖 Found ${FILE}, converting to HTML..."

    # Convert org to HTML content first
    temp_content=$(mktemp)
    if pandoc -f org -t html5 --toc "${FILE}" -o "$temp_content" --metadata title="${TITLE}"; then
        echo "✅ ${NAME} pandoc conversion successful"

        if [ "${SUBTITLE}" != "" ]; then
            SUBTITLE="<p style=\"text-align: center; color: #00cccc; font-size: 1.2rem; margin-bottom: 3rem;\">${SUBTITLE}</p>"
        fi
        # Create complete HTML structure with new matrix style
        cat >bytehot/"${NAME}.html" <<'HTML_EOF'
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${TITLE}</title>
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
            <h1>${TITLE}</h1>${SUBTITLE}
HTML_EOF

        # Add converted content
        cat "$temp_content" >>bytehot/${NAME}.html

        # Close HTML structure
        cat >>bytehot/${NAME}.html <<'HTML_EOF'
        </div>
    </div>
${FOOTER}
<script>
${MATRIX}
</script>
</body>
</html>
HTML_EOF
        echo "✅ ${NAME} page created from ${FILE}"

        # Clean up temp file
        rm "$temp_content"
    else
        echo "⚠️ ${NAME} pandoc conversion failed"
        # Clean up temp file
        rm -f "$temp_content"
        exit 1
    fi
else
    echo "⚠️ ${FILE} not found"
    exit 1
fi
