#!/bin/bash
set -euo pipefail

# Add navigation and footer to HTML files

echo "🎨 Adding navigation and footer to HTML files..."

# Navigation HTML
NAVIGATION='<div class="navigation">
    <a href="index.html">🏠 Home</a>
    <a href="implementation.html">🏗️ Implementation</a>
    <a href="journal.html">📔 Journal</a>
    <a href="docs/">📚 Docs</a>
    <a href="javadocs/">📖 Javadocs</a>
    <a href="https://github.com/rydnr/bytehot">🔗 GitHub</a>
</div>'

# Footer HTML
FOOTER='<div class="footer">
    ByteHot - Revolutionary JVM Hot-Swapping | 
    <a href="https://github.com/rydnr/bytehot">GitHub</a> | 
    <a href="https://rydnr.github.io/bytehot/docs/">Documentation</a> | 
    <a href="https://rydnr.github.io/bytehot/javadocs/">API Docs</a>
</div>'

# Function to add navigation and footer to an HTML file
add_nav_footer() {
    local file="$1"
    local temp_file="${file}.tmp"
    
    if [ -f "$file" ]; then
        # Create temp file with navigation and footer
        awk -v nav="$NAVIGATION" -v footer="$FOOTER" '
        /<body>/ {
            print $0
            print nav
            print "<main>"
            next
        }
        /<\/body>/ {
            print "</main>"
            print footer
            print $0
            next
        }
        { print }
        ' "$file" > "$temp_file"
        
        mv "$temp_file" "$file"
        echo "✅ Added navigation to $file"
    fi
}

# Add navigation to main pages
for file in bytehot/index.html bytehot/journal.html bytehot/implementation.html; do
    add_nav_footer "$file"
done

# Add navigation to docs pages
if [ -d "bytehot/docs" ]; then
    for file in bytehot/docs/*.html; do
        if [ -f "$file" ]; then
            # Update navigation for docs (relative paths)
            doc_nav='<div class="navigation">
    <a href="../index.html">🏠 Home</a>
    <a href="../implementation.html">🏗️ Implementation</a>
    <a href="../journal.html">📔 Journal</a>
    <a href="../docs/">📚 Docs</a>
    <a href="../javadocs/">📖 Javadocs</a>
    <a href="https://github.com/rydnr/bytehot">🔗 GitHub</a>
</div>'
            
            awk -v nav="$doc_nav" -v footer="$FOOTER" '
            /<body>/ {
                print $0
                print nav
                print "<main>"
                next
            }
            /<\/body>/ {
                print "</main>"
                print footer
                print $0
                next
            }
            { print }
            ' "$file" > "${file}.tmp"
            
            mv "${file}.tmp" "$file"
            echo "✅ Added navigation to $file"
        fi
    done
fi

echo "✅ Navigation and footer added to all HTML files"