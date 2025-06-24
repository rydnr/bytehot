#!/bin/bash
set -euo pipefail

# Add navigation and footer to HTML files

echo "🎨 Adding navigation and footer to HTML files..."

# Navigation HTML
NAVIGATION="$(source ./.github/scripts/nav.sh)"

# Footer HTML
FOOTER="$(source ./.github/scripts/footer.sh)"

# Function to add navigation and footer to an HTML file
add_nav_footer() {
    local file="$1"
    local nav_to_use="$2"
    local temp_file="${file}.tmp"

    if [ -f "$file" ]; then
        # Check if navigation already exists
        if grep -q "class=\"navigation\"" "$file"; then
            echo "⚠️ Navigation already exists in $file, skipping"
            return
        fi

        # Create temp file with navigation and footer
        awk -v nav="$nav_to_use" -v footer="$FOOTER" '
        /<body>/ {
            print $0
            print nav
            print "<main>"
            in_body = 1
            next
        }
        /<\/body>/ {
            if (in_body) {
                print "</main>"
                print footer
            }
            print $0
            next
        }
        { print }
        ' "$file" >"$temp_file"

        # Only replace if the temp file is not empty and different
        if [ -s "$temp_file" ]; then
            mv "$temp_file" "$file"
            echo "✅ Added navigation to $file"
        else
            rm -f "$temp_file"
            echo "⚠️ Failed to add navigation to $file"
        fi
    fi
}

# Add navigation to main pages
for file in bytehot/index.html bytehot/journal.html bytehot/implementation.html; do
    add_nav_footer "$file" "$NAVIGATION"
done

# Add navigation to docs pages
if [ -d "bytehot/docs" ]; then
    # Update navigation for docs (relative paths)
    DOC_NAV="$(source ./.github/scripts/nav-child.sh)"

    for file in bytehot/docs/*.html; do
        if [ -f "$file" ]; then
            add_nav_footer "$file" "$DOC_NAV"
        fi
    done
fi

echo "✅ Navigation and footer added to all HTML files"
