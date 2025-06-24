#!/bin/bash
set -euo pipefail

# Apply new matrix style to all documentation pages

echo "🎨 Applying new matrix style to all documentation pages..."

# Function to convert a page to new style
convert_to_new_style() {
    local file="$1"
    local title="$2"
    local nav_type="$3" # "root" or "subdir"
    local nav="$(source ./.github/scripts/nav.sh)"
    local nav_child="$(source ./.github/scripts/nav-child.sh)"
    local css="$(source ./.github/scripts/css.sh)"
    local footer="$(source ./.github/scripts/footer.sh)"
    local matrix="$(source ./.github/scripts/matrix.sh)"

    if [ ! -f "$file" ]; then
        return
    fi

    echo "🎨 Converting $file to new style..."

    # Extract content more intelligently, removing all existing structure
    temp_content=$(mktemp)
    temp_full=$(mktemp)

    # Extract just the main content, excluding all navigation, headers, scripts, and footers
    awk '
    /<body[^>]*>/ { in_body = 1; next }
    /<\/body>/ { in_body = 0; next }
    in_body && /<nav/ { in_nav = 1; skip_line = 1; next }
    in_body && in_nav && /<\/nav>/ { in_nav = 0; skip_line = 1; next }
    in_body && /<div class="nav/ { in_nav = 1; skip_line = 1; next }
    in_body && in_nav && /<\/div>/ { in_nav = 0; skip_line = 1; next }
    in_body && /<div class="container">/ { in_container = 1; next }
    in_body && in_container && /<\/div>/ && !seen_content { in_container = 0; next }
    in_body && /<footer/ { in_footer = 1; skip_line = 1; next }
    in_body && in_footer && /<\/footer>/ { in_footer = 0; skip_line = 1; next }
    in_body && /<script/ { in_script = 1; skip_line = 1; next }
    in_body && in_script && /<\/script>/ { in_script = 0; skip_line = 1; next }
    in_body && /<div class="matrix-bg">/ { skip_line = 1; next }
    in_body && !in_nav && !in_footer && !in_script && !skip_line { 
        if ($0 !~ /^[[:space:]]*$/ || seen_content) {
            seen_content = 1; 
            print; 
        }
    }
    { skip_line = 0 }
    ' "$file" >"$temp_content"

    # If content is too small, try extracting just h1-h6 and p elements
    if [ ! -s "$temp_content" ] || [ $(wc -l <"$temp_content") -lt 3 ]; then
        # Fallback: extract just content elements using pandoc if the file is HTML
        if command -v pandoc >/dev/null 2>&1; then
            pandoc "$file" -t html --extract-media=. 2>/dev/null |
                awk '/<h[1-6]|<p|<ul|<ol|<li|<pre|<code|<blockquote|<table/ { print }' >"$temp_content"
        fi

        # Final fallback: simple sed extraction
        if [ ! -s "$temp_content" ] || [ $(wc -l <"$temp_content") -lt 3 ]; then
            sed -n '/<body[^>]*>/,/<\/body>/p' "$file" |
                sed '1d;$d' |
                grep -v '<nav\|<\/nav>\|<footer\|<\/footer>\|<script\|<\/script>\|class="nav\|matrix-bg' >"$temp_content"
        fi
    fi

    # Set navigation links based on location
    if [ "$nav_type" = "subdir" ]; then
        nav_links="${nav_child}"
    else
        nav_links="${nav}"
    fi

    # Create new styled page
    cat >"$temp_full" <<HTML_EOF
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
${nav_links}
    </nav>

    <div class="content">
        <div class="doc-container">
HTML_EOF

    # Add the extracted content
    cat "$temp_content" >>"$temp_full"

    # Close the HTML structure
    cat >>"$temp_full" <<'HTML_EOF'
        </div>
    </div>
${footer}
    <script>
${matrix}
    </script>
</body>
</html>
HTML_EOF

    # Replace the original file with the new styled version
    mv "$temp_full" "$file"
    echo "✅ Converted $file to new matrix style"

    # Clean up temp files
    rm -f "$temp_content"
}

# Apply new style to documentation files
if [ -d "bytehot" ]; then
    echo "🎨 Converting documentation files to new matrix style..."

    # Skip the main index.html as it already has the new style
    # Convert all other HTML files in root (except index.html)
    for file in bytehot/*.html; do
        if [ -f "$file" ] && [ "$(basename "$file")" != "index.html" ]; then
            filename=$(basename "$file" .html)
            convert_to_new_style "$file" "$filename" "root"
        fi
    done

    # Convert files in subdirectories
    for subdir in bytehot/*/; do
        if [ -d "$subdir" ]; then
            subdir_name=$(basename "$subdir")
            echo "📂 Converting files in $subdir_name/"

            for file in "$subdir"*.html; do
                if [ -f "$file" ]; then
                    filename=$(basename "$file" .html)
                    convert_to_new_style "$file" "$subdir_name/$filename" "subdir"
                fi
            done
        fi
    done

    echo "✅ New matrix style applied to all documentation pages"
else
    echo "⚠️ bytehot directory not found"
fi
