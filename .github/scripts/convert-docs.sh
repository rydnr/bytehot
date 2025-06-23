#!/bin/bash
set -euo pipefail

# Convert documentation files from org/md to HTML

echo "📚 Converting documentation files to HTML..."

# Ensure destination directories exist
mkdir -p bytehot/docs

# First, copy existing HTML files from docs/ if they exist
if [ -d "docs" ]; then
    echo "📂 Processing docs directory..."
    
    # Copy existing HTML files
    if ls docs/*.html 1> /dev/null 2>&1; then
        echo "📋 Copying existing HTML documentation files..."
        cp docs/*.html bytehot/docs/ 2>/dev/null || true
        echo "✅ Copied existing HTML files"
    fi
    
    # Copy subdirectories with HTML files
    for subdir in docs/*/; do
        if [ -d "$subdir" ]; then
            subdir_name=$(basename "$subdir")
            echo "📂 Processing subdirectory: $subdir_name"
            mkdir -p "bytehot/docs/$subdir_name"
            
            # Copy HTML files from subdirectory
            if ls "${subdir}"*.html 1> /dev/null 2>&1; then
                cp "${subdir}"*.html "bytehot/docs/$subdir_name/" 2>/dev/null || true
                echo "✅ Copied HTML files from $subdir_name"
            fi
        fi
    done
    
    # Convert org/md files if needed
    for file in docs/*.org docs/*.md; do
        if [ -f "$file" ]; then
            basename=$(basename "$file" .org)
            basename=$(basename "$basename" .md)
            echo "Converting: $file -> bytehot/docs/${basename}.html"
            
            # Create temp file for conversion
            temp_content=$(mktemp)
            
            if [[ "$file" == *.org ]]; then
                if pandoc -f org -t html5 --toc "$file" -o "$temp_content"; then
                    # Create proper HTML structure for each file
                    cat > "bytehot/docs/${basename}.html" << 'HTML_EOF'
<!DOCTYPE html>
<html>
<head>
HTML_EOF
                    echo "    <title>${basename} - ByteHot Documentation</title>" >> "bytehot/docs/${basename}.html"
                    cat >> "bytehot/docs/${basename}.html" << 'HTML_EOF'
    <link rel="stylesheet" href="../style.css">
</head>
<body>
HTML_EOF
                    cat "$temp_content" >> "bytehot/docs/${basename}.html"
                    echo '</body></html>' >> "bytehot/docs/${basename}.html"
                    echo "✅ Converted $file"
                else
                    echo "⚠️ Failed to convert $file"
                fi
            else
                if pandoc -f markdown -t html5 --toc "$file" -o "$temp_content"; then
                    # Create proper HTML structure for each file
                    cat > "bytehot/docs/${basename}.html" << 'HTML_EOF'
<!DOCTYPE html>
<html>
<head>
HTML_EOF
                    echo "    <title>${basename} - ByteHot Documentation</title>" >> "bytehot/docs/${basename}.html"
                    cat >> "bytehot/docs/${basename}.html" << 'HTML_EOF'
    <link rel="stylesheet" href="../style.css">
</head>
<body>
HTML_EOF
                    cat "$temp_content" >> "bytehot/docs/${basename}.html"
                    echo '</body></html>' >> "bytehot/docs/${basename}.html"
                    echo "✅ Converted $file"
                else
                    echo "⚠️ Failed to convert $file"
                fi
            fi
            
            # Clean up temp file
            rm -f "$temp_content"
        fi
    done
    echo "✅ Documentation files processed"
else
    echo "⚠️ docs directory not found, skipping doc conversion"
fi

