#!/bin/bash
set -euo pipefail

# Create Missing HTML Files Script
# This script creates HTML files for the broken links identified on GitHub Pages

echo "🔧 Creating missing HTML files for broken links..."

# Create bytehot directory if not exists
mkdir -p bytehot

# Convert GETTING_STARTED.org to HTML
if [ -f "GETTING_STARTED.org" ]; then
    echo "📖 Converting GETTING_STARTED.org to HTML..."
    
    # Create a temporary template file
    temp_template=$(mktemp)
    cat > "$temp_template" <<'EOF'
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>$title$</title>
    <style>
        body { font-family: 'Courier New', monospace; background: linear-gradient(135deg, #0f0f23 0%, #1a1a3a 100%); color: #00ff00; line-height: 1.6; margin: 0; padding: 2rem; }
        .container { max-width: 1200px; margin: 0 auto; background: rgba(26, 26, 58, 0.8); border: 1px solid #00ff00; border-radius: 8px; padding: 2rem; }
        h1, h2, h3 { color: #00cccc; text-shadow: 0 0 10px #00cccc; }
        h1 { font-size: 2.5rem; text-align: center; margin-bottom: 2rem; }
        code { background: rgba(0, 255, 0, 0.1); padding: 0.2rem 0.4rem; border-radius: 4px; color: #ffffff; }
        pre { background: #000; border: 1px solid #00ff00; border-radius: 4px; padding: 1rem; overflow-x: auto; color: #00ff00; }
        a { color: #00cccc; text-decoration: none; }
        a:hover { text-shadow: 0 0 10px #00cccc; }
        .nav { text-align: center; margin-bottom: 2rem; }
        .nav a { margin: 0 1rem; padding: 0.5rem 1rem; border: 1px solid #00ff00; border-radius: 4px; }
    </style>
</head>
<body>
    <div class="container">
        <div class="nav">
            <a href="index.html">🏠 Home</a>
            <a href="story.html">📖 Story</a>
            <a href="implementation.html">⚙️ Implementation</a>
            <a href="javadocs/">📚 JavaDocs</a>
            <a href="https://github.com/rydnr/bytehot">🔗 GitHub</a>
        </div>
        $body$
    </div>
</body>
</html>
EOF
    
    pandoc GETTING_STARTED.org -o bytehot/GETTING_STARTED.html \
        --metadata title="Getting Started - ByteHot" \
        --template="$temp_template" || echo "⚠️ Failed to convert GETTING_STARTED.org"
    
    rm -f "$temp_template"
    echo "✅ Created GETTING_STARTED.html"
fi

# Convert key org files to HTML
declare -a org_files=("ByteHot" "ByteHotApplication" "Ports")

for org_file in "${org_files[@]}"; do
    if [ -f "docs/${org_file}.org" ]; then
        echo "📖 Converting docs/${org_file}.org to HTML..."
        
        # Create a temporary template file for org docs
        temp_template_org=$(mktemp)
        cat > "$temp_template_org" <<'EOF'
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>$title$</title>
    <style>
        body { font-family: 'Courier New', monospace; background: linear-gradient(135deg, #0f0f23 0%, #1a1a3a 100%); color: #00ff00; line-height: 1.6; margin: 0; padding: 2rem; }
        .container { max-width: 1200px; margin: 0 auto; background: rgba(26, 26, 58, 0.8); border: 1px solid #00ff00; border-radius: 8px; padding: 2rem; }
        h1, h2, h3 { color: #00cccc; text-shadow: 0 0 10px #00cccc; }
        h1 { font-size: 2.5rem; text-align: center; margin-bottom: 2rem; }
        code { background: rgba(0, 255, 0, 0.1); padding: 0.2rem 0.4rem; border-radius: 4px; color: #ffffff; }
        pre { background: #000; border: 1px solid #00ff00; border-radius: 4px; padding: 1rem; overflow-x: auto; color: #00ff00; }
        a { color: #00cccc; text-decoration: none; }
        a:hover { text-shadow: 0 0 10px #00cccc; }
        .nav { text-align: center; margin-bottom: 2rem; }
        .nav a { margin: 0 1rem; padding: 0.5rem 1rem; border: 1px solid #00ff00; border-radius: 4px; }
        .footer { text-align: center; margin-top: 3rem; padding-top: 2rem; border-top: 1px solid #00ff00; color: #888; }
    </style>
</head>
<body>
    <div class="container">
        <div class="nav">
            <a href="index.html">🏠 Home</a>
            <a href="story.html">📖 Story</a>
            <a href="GETTING_STARTED.html">🚀 Getting Started</a>
            <a href="implementation.html">⚙️ Implementation</a>
            <a href="events/">📡 Events</a>
            <a href="flows/">🌊 Flows</a>
            <a href="ports/">🚪 Ports</a>
            <a href="javadocs/">📚 JavaDocs</a>
            <a href="https://github.com/rydnr/bytehot">🔗 GitHub</a>
        </div>
        $body$
        <div class="footer">
            <p>&copy; 2025 ByteHot Project. Licensed under <a href="https://www.gnu.org/licenses/gpl-3.0.html">GPLv3</a></p>
        </div>
    </div>
</body>
</html>
EOF
        
        pandoc "docs/${org_file}.org" -o "bytehot/${org_file}.html" \
            --metadata title="${org_file} - ByteHot Documentation" \
            --template="$temp_template_org" || echo "⚠️ Failed to convert docs/${org_file}.org"
        
        rm -f "$temp_template_org"
        echo "✅ Created ${org_file}.html"
    else
        echo "⚠️ docs/${org_file}.org not found"
    fi
done

# Create javadocs directory with placeholder if it doesn't exist
if [ ! -d "bytehot/javadocs" ]; then
    echo "📚 Creating javadocs directory with placeholder..."
    mkdir -p bytehot/javadocs
    cat > bytehot/javadocs/index.html <<'EOF'
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>JavaDocs - ByteHot</title>
    <style>
        body { font-family: 'Courier New', monospace; background: linear-gradient(135deg, #0f0f23 0%, #1a1a3a 100%); color: #00ff00; line-height: 1.6; margin: 0; padding: 2rem; }
        .container { max-width: 1200px; margin: 0 auto; background: rgba(26, 26, 58, 0.8); border: 1px solid #00ff00; border-radius: 8px; padding: 2rem; text-align: center; }
        h1 { color: #00cccc; text-shadow: 0 0 10px #00cccc; font-size: 2.5rem; margin-bottom: 2rem; }
        p { color: #ffffff; font-size: 1.2rem; margin: 1rem 0; }
        a { color: #00cccc; text-decoration: none; padding: 0.5rem 1rem; border: 1px solid #00cccc; border-radius: 4px; display: inline-block; margin: 0.5rem; }
        a:hover { background: rgba(0, 204, 204, 0.1); text-shadow: 0 0 10px #00cccc; }
        .nav { margin-bottom: 2rem; }
        .nav a { margin: 0 1rem; }
    </style>
</head>
<body>
    <div class="container">
        <div class="nav">
            <a href="../index.html">🏠 Home</a>
            <a href="../story.html">📖 Story</a>
            <a href="../GETTING_STARTED.html">🚀 Getting Started</a>
            <a href="../implementation.html">⚙️ Implementation</a>
            <a href="https://github.com/rydnr/bytehot">🔗 GitHub</a>
        </div>
        
        <h1>📚 ByteHot JavaDocs</h1>
        
        <p>📋 JavaDoc generation is in progress...</p>
        
        <p>The comprehensive API documentation will be available here once the build process completes.</p>
        
        <div style="margin: 2rem 0;">
            <a href="../events/">📡 Domain Events Documentation</a>
            <a href="../flows/">🌊 Process Flow Documentation</a>
            <a href="../ports/">🚪 Port Interface Documentation</a>
        </div>
        
        <p style="margin-top: 2rem; color: #888;">
            In the meantime, explore our comprehensive literate programming documentation above.
        </p>
    </div>
</body>
</html>
EOF
    echo "✅ Created javadocs placeholder"
fi

echo "✅ Missing HTML files creation completed!"