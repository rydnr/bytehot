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
        
        # Create complete HTML structure with new matrix style
        cat > bytehot/journal.html << 'HTML_EOF'
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ByteHot Development Journal</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Courier New', monospace; background: linear-gradient(135deg, #0f0f23 0%, #1a1a3a 100%); color: #00ff00; line-height: 1.6; overflow-x: hidden; }
        .matrix-bg { position: fixed; top: 0; left: 0; width: 100%; height: 100%; pointer-events: none; z-index: -1; opacity: 0.1; }
        .nav-header { position: fixed; top: 0; left: 0; right: 0; background: rgba(15, 15, 35, 0.95); backdrop-filter: blur(10px); padding: 1rem 2rem; z-index: 1000; border-bottom: 2px solid #00ff00; }
        .nav-links { display: flex; justify-content: center; gap: 2rem; flex-wrap: wrap; }
        .nav-link { color: #00ff00; text-decoration: none; padding: 0.5rem 1rem; border: 1px solid #00ff00; border-radius: 4px; transition: all 0.3s ease; font-weight: bold; }
        .nav-link:hover { background: #00ff00; color: #0f0f23; box-shadow: 0 0 20px #00ff00; transform: translateY(-2px); }
        .content { margin-top: 120px; padding: 2rem; max-width: 1200px; margin-left: auto; margin-right: auto; }
        .journal-container { background: rgba(26, 26, 58, 0.8); border: 1px solid #00ff00; border-radius: 12px; padding: 3rem; margin: 2rem 0; }
        .journal-container h1 { color: #00cccc; font-size: 2.5rem; margin-bottom: 2rem; text-shadow: 0 0 15px #00cccc; text-align: center; }
        .journal-container h2 { color: #00ff00; font-size: 1.8rem; margin: 2rem 0 1rem 0; border-left: 4px solid #00ff00; padding-left: 1rem; }
        .journal-container p { color: #ffffff; margin: 1rem 0; line-height: 1.8; }
        .footer { background: rgba(0, 0, 0, 0.8); padding: 2rem; text-align: center; border-top: 2px solid #00ff00; margin-top: 4rem; }
        .footer a { color: #00ff00; text-decoration: none; }
    </style>
</head>
<body>
    <div class="matrix-bg"></div>
    <nav class="nav-header">
        <div class="nav-links">
            <a href="index.html" class="nav-link">Home</a>
            <a href="story.html" class="nav-link">📖 Story</a>
            <a href="GETTING_STARTED.html" class="nav-link">🚀 Getting Started</a>
            <a href="implementation.html" class="nav-link">⚙️ Implementation</a>
            <a href="journal.html" class="nav-link">📔 Journal</a>
            <a href="javadocs/" class="nav-link">JavaDocs</a>
            <a href="https://github.com/rydnr/bytehot" class="nav-link">GitHub</a>
        </div>
    </nav>
    <div class="content">
        <div class="journal-container">
            <h1>📔 ByteHot Development Journal</h1>
HTML_EOF
        
        # Add converted content
        cat "$temp_content" >> bytehot/journal.html
        
        # Close HTML structure
        cat >> bytehot/journal.html << 'HTML_EOF'
        </div>
    </div>
    <footer class="footer">
        <p>&copy; 2025 ByteHot Project. Licensed under <a href="https://www.gnu.org/licenses/gpl-3.0.html">GPLv3</a></p>
        <p><a href="https://github.com/rydnr/bytehot">GitHub</a> • <a href="https://github.com/rydnr/bytehot/issues">Issues</a></p>
    </footer>
</body>
</html>
HTML_EOF
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