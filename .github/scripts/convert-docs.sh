#!/bin/bash
set -euo pipefail

# Convert documentation files from org/md to HTML

echo "📚 Converting documentation files to HTML..."

# Process docs directory if it exists
if [ -d "docs" ]; then
    echo "📂 Processing docs directory..."
    for file in docs/*.org docs/*.md; do
        if [ -f "$file" ]; then
            basename=$(basename "$file" .org)
            basename=$(basename "$basename" .md)
            echo "Converting: $file -> bytehot/docs/${basename}.html"
            
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
            
            if [[ "$file" == *.org ]]; then
                pandoc -f org -t html5 --toc "$file" -o "bytehot/docs/${basename}_content.html" || echo "Failed to convert $file"
            else
                pandoc -f markdown -t html5 --toc "$file" -o "bytehot/docs/${basename}_content.html" || echo "Failed to convert $file"
            fi
            
            # Add content if conversion succeeded
            if [ -f "bytehot/docs/${basename}_content.html" ]; then
                cat "bytehot/docs/${basename}_content.html" >> "bytehot/docs/${basename}.html"
                rm "bytehot/docs/${basename}_content.html"
            fi
            
            # Close HTML structure
            echo '</body></html>' >> "bytehot/docs/${basename}.html"
        fi
    done
    echo "✅ Documentation files converted"
else
    echo "⚠️ docs directory not found, skipping doc conversion"
fi

# Process subdirectories
for subdir in docs/*/; do
    if [ -d "$subdir" ]; then
        echo "📂 Processing subdirectory: $subdir"
        for file in "$subdir"*.org "$subdir"*.md; do
            if [ -f "$file" ]; then
                basename=$(basename "$file" .org)
                basename=$(basename "$basename" .md)
                relative_subdir=$(basename "$subdir")
                echo "Converting: $file -> ${subdir}${basename}.html"
                
                # Create proper HTML structure
                cat > "${subdir}${basename}.html" << 'HTML_EOF'
<!DOCTYPE html>
<html>
<head>
HTML_EOF
                echo "    <title>${basename} - ByteHot Documentation</title>" >> "${subdir}${basename}.html"
                cat >> "${subdir}${basename}.html" << 'HTML_EOF'
    <link rel="stylesheet" href="../../style.css">
</head>
<body>
HTML_EOF
                
                if [[ "$file" == *.org ]]; then
                    pandoc -f org -t html5 --toc "$file" -o "${subdir}${basename}_content.html" || echo "Failed to convert $file"
                else
                    pandoc -f markdown -t html5 --toc "$file" -o "${subdir}${basename}_content.html" || echo "Failed to convert $file"
                fi
                
                # Add content if conversion succeeded
                if [ -f "${subdir}${basename}_content.html" ]; then
                    cat "${subdir}${basename}_content.html" >> "${subdir}${basename}.html"
                    rm "${subdir}${basename}_content.html"
                fi
                
                # Close HTML structure
                echo '</body></html>' >> "${subdir}${basename}.html"
            fi
        done
    fi
done