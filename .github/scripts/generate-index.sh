#!/bin/bash
set -euo pipefail

# Generate index.html from story.org

echo "📄 Creating index page from story.org..."

# Ensure bytehot directory exists
mkdir -p bytehot

if [ -f "story.org" ]; then
    echo "📖 Found story.org, converting to HTML..."
    
    # Convert org to HTML content first
    temp_content=$(mktemp)
    if pandoc -f org -t html5 --toc story.org -o "$temp_content" --metadata title="ByteHot - Revolutionary JVM Hot-Swapping Agent"; then
        echo "✅ Pandoc conversion successful"
        
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
        
        # Add converted content
        cat "$temp_content" >> bytehot/index.html
        
        # Close HTML structure
        echo '</body></html>' >> bytehot/index.html
        echo "✅ Index page created from story.org"
        
        # Clean up temp file
        rm "$temp_content"
    else
        echo "⚠️ Pandoc conversion failed, using fallback"
        cat > bytehot/index.html << 'HTML_EOF'
<!DOCTYPE html>
<html>
<head>
    <title>ByteHot - Revolutionary JVM Hot-Swapping Agent</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <h1>🔥 ByteHot - Revolutionary JVM Hot-Swapping Agent</h1>
    <p><em>A revolutionary JVM agent enabling bytecode hot-swapping at runtime</em></p>
    
    <h2>🚀 Key Features</h2>
    <ul>
        <li>🔄 Real-time bytecode hot-swapping without JVM restart</li>
        <li>🏗️ Domain-Driven Design with hexagonal architecture</li>
        <li>📊 Comprehensive event-driven testing framework</li>
        <li>🛡️ Advanced error recovery and rollback mechanisms</li>
        <li>📚 Revolutionary literate programming documentation</li>
    </ul>
    
    <p>📖 <strong>Note:</strong> The full documentation from story.org could not be converted. Please check the source file.</p>
</body>
</html>
HTML_EOF
        # Clean up temp file
        rm -f "$temp_content"
    fi
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
    <p><em>story.org file not found in repository root</em></p>
</body>
</html>
HTML_EOF
fi