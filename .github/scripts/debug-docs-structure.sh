#!/bin/bash
set -euo pipefail

# ByteHot Documentation Structure Debug Script
# Debugs generated documentation structure and content

echo "🔍 Debugging generated documentation structure..."

echo "Root files:"
ls -la bytehot/ 2>/dev/null || echo "No bytehot directory"

echo "Sample file content (first 20 lines):"
if [ -f "bytehot/index.html" ]; then
    echo "=== bytehot/index.html ==="
    head -20 bytehot/index.html
fi

if [ -f "bytehot/implementation.html" ]; then
    echo "=== bytehot/implementation.html ==="
    head -10 bytehot/implementation.html
fi

if [ -f "bytehot/GETTING_STARTED.html" ]; then
    echo "=== bytehot/GETTING_STARTED.html ==="
    head -10 bytehot/GETTING_STARTED.html
fi

echo "📋 Generated documentation files:"
find bytehot -name "*.html" -type f 2>/dev/null | head -20 || echo "No HTML files found"

# Count files more safely to avoid broken pipe
html_count=$(find bytehot -name "*.html" -type f 2>/dev/null | wc -l || echo "0")
echo "📊 Total HTML files: $html_count"

# Check for required styling components per CLAUDE.md
echo "🎨 Checking styling compliance..."

check_styling_compliance() {
    local file="$1"
    local has_css=false
    local has_nav=false
    local has_footer=false
    
    if [ -f "$file" ]; then
        if grep -q "style.*font-family.*Courier" "$file"; then
            has_css=true
        fi
        
        if grep -q "nav.*nav-header" "$file"; then
            has_nav=true
        fi
        
        if grep -q "footer" "$file"; then
            has_footer=true
        fi
        
        echo "File: $(basename "$file")"
        echo "  CSS: $has_css, Nav: $has_nav, Footer: $has_footer"
    fi
}

# Check key files for styling compliance
for html_file in bytehot/index.html bytehot/GETTING_STARTED.html bytehot/story.html; do
    if [ -f "$html_file" ]; then
        check_styling_compliance "$html_file"
    fi
done

echo "✅ Documentation structure debug completed"