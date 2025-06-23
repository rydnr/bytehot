#!/bin/bash
set -euo pipefail

# Generate index.html from story.org

echo "📄 Creating index page from story.org..."

if [ -f "bytehot/story.org" ]; then
    # Convert content only, then wrap in proper HTML structure
    pandoc -f org -t html5 --toc bytehot/story.org -o bytehot/index_content.html --metadata title="ByteHot - Revolutionary JVM Hot-Swapping Agent" || echo "story.org conversion failed, using fallback"
    
    # Create complete HTML structure
    cat > bytehot/index.html << 'HTML_EOF'
<!DOCTYPE html>
<html>
<head>
    <title>ByteHot - Revolutionary JVM Hot-Swapping Agent</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
HTML_EOF
    
    # Add content if it exists
    if [ -f "bytehot/index_content.html" ]; then
        cat bytehot/index_content.html >> bytehot/index.html
        rm bytehot/index_content.html
    fi
    
    # Close HTML structure
    echo '</body></html>' >> bytehot/index.html
    echo "✅ Index page created from story.org"
else
    echo "⚠️ story.org not found, creating fallback index.html"
    cat > bytehot/index.html << 'HTML_EOF'
<!DOCTYPE html>
<html>
<head>
    <title>ByteHot Documentation</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <h1>ByteHot Documentation</h1>
    <p>Documentation generation in progress...</p>
</body>
</html>
HTML_EOF
fi