#!/bin/bash
set -euo pipefail

# Generate journal.html from journal.org

echo "📔 Creating journal page from journal.org..."

if [ -f "journal.org" ]; then
    # Convert content only, then wrap in proper HTML structure
    pandoc -f org -t html5 --toc journal.org -o bytehot/journal_content.html --metadata title="ByteHot Development Journal" || echo "journal.org conversion failed"
    
    # Create complete HTML structure
    cat > bytehot/journal.html << 'HTML_EOF'
<!DOCTYPE html>
<html>
<head>
    <title>ByteHot Development Journal</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
HTML_EOF
    
    # Add content if it exists
    if [ -f "bytehot/journal_content.html" ]; then
        cat bytehot/journal_content.html >> bytehot/journal.html
        rm bytehot/journal_content.html
    fi
    
    # Close HTML structure
    echo '</body></html>' >> bytehot/journal.html
    echo "✅ Journal page created from journal.org"
else
    echo "⚠️ journal.org not found, skipping journal generation"
fi