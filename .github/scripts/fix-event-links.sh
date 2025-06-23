#!/bin/bash
set -euo pipefail

# Fix event links in events/index.html to point to .html files instead of .org files

echo "🔧 Fixing event links to point to HTML files instead of org files..."

# Function to fix event links in index files
fix_event_links() {
    local file="$1"
    if [ -f "$file" ]; then
        echo "🔍 Fixing event links in $file"
        
        # Create temp file for modifications
        temp_file=$(mktemp)
        
        # Replace .org links with .html links
        sed \
            -e 's|href="./\([^"]*\)\.org"|href="./\1.html"|g' \
            -e 's|href="\([^"]*\)\.org"|href="\1.html"|g' \
            "$file" > "$temp_file"
        
        # Only replace if different
        if ! cmp -s "$file" "$temp_file"; then
            mv "$temp_file" "$file"
            echo "✅ Fixed event links in $file"
        else
            rm "$temp_file"
        fi
    fi
}

# Fix event links in the generated site
if [ -d "bytehot" ]; then
    # Fix events index
    fix_event_links "bytehot/events/index.html"
    
    # Fix flows index if it exists
    fix_event_links "bytehot/flows/index.html"
    
    # Fix ports index if it exists  
    fix_event_links "bytehot/ports/index.html"
    
    # Fix any other index files that might have .org links
    for index_file in bytehot/*/index.html; do
        if [ -f "$index_file" ]; then
            fix_event_links "$index_file"
        fi
    done
    
    echo "✅ Event link fixing completed"
else
    echo "⚠️ bytehot directory not found"
fi