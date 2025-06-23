#!/bin/bash
set -euo pipefail

# Add Class Cross-References Script
# This script adds cross-references between classes and their documentation

echo "🔗 Adding class cross-references to documentation..."

# Function to add literate programming section to HTML files
add_literate_section() {
    local file=$1
    echo "🔗 Adding literate programming links to $file"
    
    if [ -f "$file" ]; then
        # Create a temporary file for the modified content
        temp_file=$(mktemp)
        
        # Check if the file already has a literate programming section
        if grep -q "literate-section" "$file"; then
            echo "   ✓ Already has literate programming section"
            return
        fi
        
        # Add literate programming section before the footer
        sed '/class="footer"/i \
        <div class="literate-section">\
            <h2>📚 Related Literate Programming Documentation</h2>\
            <p>Explore the comprehensive documentation for classes mentioned in this guide:</p>\
            <div class="docs-grid">\
                <a href="literate-docs.html" class="docs-link">📚 Complete Class Index</a>\
                <a href="ByteHot.html" class="docs-link">🏗️ ByteHot Core</a>\
                <a href="ByteHotApplication.html" class="docs-link">🎯 Application Layer</a>\
                <a href="ErrorHandler.html" class="docs-link">🛡️ Error Handler</a>\
                <a href="RollbackManager.html" class="docs-link">↩️ Rollback Manager</a>\
                <a href="InstanceTracker.html" class="docs-link">📊 Instance Tracker</a>\
            </div>\
        </div>' "$file" > "$temp_file"
        
        # Replace the original file
        mv "$temp_file" "$file"
        echo "   ✅ Added literate programming section to $file"
    else
        echo "   ⚠️ File $file not found"
    fi
}

# Function to enhance class references in content
enhance_class_references() {
    local file=$1
    echo "🔍 Enhancing class references in $file"
    
    if [ -f "$file" ]; then
        # Create a temporary file for the modified content
        temp_file=$(mktemp)
        
        # Add class reference links for key classes
        sed -e 's/\bByteHot\b/<a href="ByteHot.html" class="class-ref">ByteHot<\/a>/g' \
            -e 's/\bByteHotApplication\b/<a href="ByteHotApplication.html" class="class-ref">ByteHotApplication<\/a>/g' \
            -e 's/\bErrorHandler\b/<a href="ErrorHandler.html" class="class-ref">ErrorHandler<\/a>/g' \
            -e 's/\bRollbackManager\b/<a href="RollbackManager.html" class="class-ref">RollbackManager<\/a>/g' \
            -e 's/\bInstanceTracker\b/<a href="InstanceTracker.html" class="class-ref">InstanceTracker<\/a>/g' \
            -e 's/\bFrameworkIntegration\b/<a href="FrameworkIntegration.html" class="class-ref">FrameworkIntegration<\/a>/g' \
            -e 's/\bBytecodeValidator\b/<a href="BytecodeValidator.html" class="class-ref">BytecodeValidator<\/a>/g' \
            -e 's/\bHotSwapManager\b/<a href="HotSwapManager.html" class="class-ref">HotSwapManager<\/a>/g' \
            -e 's/\bClassFileWatcher\b/<a href="ClassFileWatcher.html" class="class-ref">ClassFileWatcher<\/a>/g' \
            -e 's/\bUserContextResolver\b/<a href="UserContextResolver.html" class="class-ref">UserContextResolver<\/a>/g' \
            "$file" > "$temp_file"
        
        # Replace the original file
        mv "$temp_file" "$file"
        echo "   ✅ Enhanced class references in $file"
    else
        echo "   ⚠️ File $file not found"
    fi
}

# Check if bytehot directory exists
if [ ! -d "bytehot" ]; then
    echo "⚠️ bytehot directory not found, skipping cross-reference addition"
    exit 0
fi

# Add literate programming sections to key documentation files
files_to_enhance=(
    "bytehot/GETTING_STARTED.html"
    "bytehot/ByteHot.html"
    "bytehot/ByteHotApplication.html"
    "bytehot/Ports.html"
    "bytehot/implementation.html"
    "bytehot/story.html"
)

for file in "${files_to_enhance[@]}"; do
    if [ -f "$file" ]; then
        add_literate_section "$file"
        enhance_class_references "$file"
    fi
done

# Add navigation consistency - ensure all pages have the literate docs link
echo "🧭 Ensuring consistent navigation across all pages..."

# Find all HTML files and update navigation
find bytehot -name "*.html" -type f | while read -r html_file; do
    if [ -f "$html_file" ]; then
        # Check if the navigation already includes literate docs
        if ! grep -q "literate-docs.html" "$html_file"; then
            echo "   📝 Updating navigation in $html_file"
            
            # Add literate docs link to navigation if missing
            sed -i 's|<a href="implementation.html" class="nav-link">⚙️ Implementation</a>|<a href="implementation.html" class="nav-link">⚙️ Implementation</a>\
            <a href="literate-docs.html" class="nav-link">📚 Literate Docs</a>|g' "$html_file"
        fi
    fi
done

echo "✅ Class cross-references and navigation updates completed!"