#!/bin/bash
set -euo pipefail

# Generate journal.html from journal.org

echo "📔 Creating journal page from journal.org..."

# Ensure bytehot directory exists
mkdir -p bytehot

if [ -f "journal.org" ]; then
    echo "📖 Found journal.org, converting to HTML..."
    
    # Convert org to HTML content first
    temp_content=$(mktemp)
    if pandoc -f org -t html5 --toc journal.org -o "$temp_content" --metadata title="ByteHot Development Journal"; then
        echo "✅ Journal pandoc conversion successful"
        
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
        
        # Add converted content
        cat "$temp_content" >> bytehot/journal.html
        
        # Close HTML structure
        echo '</body></html>' >> bytehot/journal.html
        echo "✅ Journal page created from journal.org"
        
        # Clean up temp file
        rm "$temp_content"
    else
        echo "⚠️ Journal pandoc conversion failed, creating basic fallback"
        cat > bytehot/journal.html << 'HTML_EOF'
<!DOCTYPE html>
<html>
<head>
    <title>ByteHot Development Journal</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <h1>📔 ByteHot Development Journal</h1>
    <p><em>Documentation of the development process and conversations</em></p>
    <p>📖 <strong>Note:</strong> The journal content from journal.org could not be converted. Please check the source file.</p>
</body>
</html>
HTML_EOF
        # Clean up temp file
        rm -f "$temp_content"
    fi
else
    echo "⚠️ journal.org not found, creating placeholder"
    cat > bytehot/journal.html << 'HTML_EOF'
<!DOCTYPE html>
<html>
<head>
    <title>ByteHot Development Journal</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <h1>📔 ByteHot Development Journal</h1>
    <p><em>Development journal not available</em></p>
    <p><em>journal.org file not found in repository root</em></p>
</body>
</html>
HTML_EOF
fi