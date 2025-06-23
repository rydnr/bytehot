#!/bin/bash
set -euo pipefail

# Fix broken links in the copied documentation files

echo "🔧 Fixing broken links in documentation..."

# Function to fix links in a file
fix_links_in_file() {
    local file="$1"
    if [ -f "$file" ]; then
        echo "🔍 Fixing links in $file"
        
        # Create temp file for modifications
        temp_file=$(mktemp)
        
        # Fix common broken links
        sed \
            -e 's|href="docs/implementation\.html"|href="implementation.html"|g' \
            -e 's|href="docs/GETTING_STARTED\.html"|href="GETTING_STARTED.html"|g' \
            -e 's|href="docs/ports/"|href="ports/"|g' \
            -e 's|href="docs/flows/"|href="flows/"|g' \
            -e 's|href="docs/events/"|href="events/"|g' \
            -e 's|href="docs/InstrumentationPort\.html"|href="InstrumentationPort.html"|g' \
            -e 's|href="docs/FileWatcherPort\.html"|href="FileWatcherPort.html"|g' \
            -e 's|href="docs/"|href=""|g' \
            "$file" > "$temp_file"
        
        # Only replace if different
        if ! cmp -s "$file" "$temp_file"; then
            mv "$temp_file" "$file"
            echo "✅ Fixed links in $file"
        else
            rm "$temp_file"
        fi
    fi
}

# Fix links in main files
if [ -d "bytehot" ]; then
    # Fix main index.html
    fix_links_in_file "bytehot/index.html"
    
    # Fix implementation.html links
    fix_links_in_file "bytehot/implementation.html"
    
    # Fix all HTML files in the root
    for file in bytehot/*.html; do
        if [ -f "$file" ]; then
            fix_links_in_file "$file"
        fi
    done
    
    # Fix subdirectory HTML files
    for subdir in bytehot/*/; do
        if [ -d "$subdir" ]; then
            for file in "$subdir"*.html; do
                if [ -f "$file" ]; then
                    fix_links_in_file "$file"
                fi
            done
        fi
    done
    
    echo "✅ Link fixing completed"
else
    echo "⚠️ bytehot directory not found"
fi