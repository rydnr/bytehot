#!/bin/bash
set -euo pipefail

# Comprehensive Documentation Creation Script
# This script creates all missing documentation files with consistent styling and cross-references

echo "🚀 Creating comprehensive documentation with literate programming links..."

# Create bytehot directory if not exists
mkdir -p bytehot

# Common navigation template that matches the root index
read -r -d '' COMMON_NAV_TEMPLATE << 'EOF' || true
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>$title$</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Courier New', monospace; background: linear-gradient(135deg, #0f0f23 0%, #1a1a3a 100%); color: #00ff00; line-height: 1.6; overflow-x: hidden; }
        .matrix-bg { position: fixed; top: 0; left: 0; width: 100%; height: 100%; pointer-events: none; z-index: -1; opacity: 0.1; }
        .nav-header { position: fixed; top: 0; left: 0; right: 0; background: rgba(15, 15, 35, 0.95); backdrop-filter: blur(10px); padding: 1rem 2rem; z-index: 1000; border-bottom: 2px solid #00ff00; }
        .nav-links { display: flex; justify-content: center; gap: 2rem; flex-wrap: wrap; }
        .nav-link { color: #00ff00; text-decoration: none; padding: 0.5rem 1rem; border: 1px solid #00ff00; border-radius: 4px; transition: all 0.3s ease; font-weight: bold; text-transform: uppercase; letter-spacing: 1px; }
        .nav-link:hover { background: #00ff00; color: #0f0f23; box-shadow: 0 0 20px #00ff00; transform: translateY(-2px); }
        .nav-link.getting-started { background: linear-gradient(45deg, #ff6b00, #ff0080); border-color: #ff6b00; color: white; animation: highlight 3s ease-in-out infinite; }
        @keyframes highlight { 0%, 100% { box-shadow: 0 0 10px #ff6b00; } 50% { box-shadow: 0 0 30px #ff6b00, 0 0 40px #ff0080; } }
        .nav-link.getting-started:hover { background: linear-gradient(45deg, #ff8b20, #ff20a0); transform: translateY(-3px) scale(1.05); }
        .container { max-width: 1200px; margin: 0 auto; background: rgba(26, 26, 58, 0.8); border: 1px solid #00ff00; border-radius: 8px; padding: 2rem; margin-top: 6rem; }
        h1, h2, h3 { color: #00cccc; text-shadow: 0 0 10px #00cccc; }
        h1 { font-size: 2.5rem; text-align: center; margin-bottom: 2rem; }
        h2 { font-size: 2rem; margin: 2rem 0 1rem 0; }
        h3 { font-size: 1.5rem; margin: 1.5rem 0 0.5rem 0; }
        p { color: #ffffff; margin: 1rem 0; line-height: 1.8; }
        code { background: rgba(0, 255, 0, 0.1); padding: 0.2rem 0.4rem; border-radius: 4px; color: #ffffff; }
        pre { background: #000; border: 1px solid #00ff00; border-radius: 4px; padding: 1rem; overflow-x: auto; color: #00ff00; margin: 1rem 0; }
        a { color: #00cccc; text-decoration: none; }
        a:hover { text-shadow: 0 0 10px #00cccc; }
        .docs-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 1.5rem; margin: 2rem 0; }
        .docs-link { display: block; color: #00cccc; text-decoration: none; padding: 1rem; border: 1px solid #00cccc; border-radius: 6px; transition: all 0.3s ease; text-align: center; font-weight: bold; }
        .docs-link:hover { background: rgba(0, 204, 204, 0.1); transform: translateY(-2px); box-shadow: 0 5px 15px rgba(0, 204, 204, 0.3); }
        .literate-section { background: rgba(0, 0, 0, 0.3); border-radius: 12px; padding: 2rem; margin: 2rem 0; border: 2px solid #00cccc; }
        .class-ref { background: rgba(0, 255, 0, 0.1); padding: 0.3rem 0.6rem; border-radius: 4px; border: 1px solid #00ff00; display: inline-block; margin: 0.2rem; color: #00ff00; text-decoration: none; font-weight: bold; }
        .class-ref:hover { background: rgba(0, 255, 0, 0.2); box-shadow: 0 0 10px #00ff00; }
        .footer { text-align: center; margin-top: 3rem; padding-top: 2rem; border-top: 1px solid #00ff00; color: #888; }
        .footer a { color: #00ff00; }
        .footer a:hover { text-shadow: 0 0 10px #00ff00; }
        ul, ol { color: #ffffff; margin: 1rem 0 1rem 2rem; }
        li { margin: 0.5rem 0; }
        .revolutionary-banner { background: linear-gradient(45deg, #ff0080, #00ff00, #0080ff); background-size: 400% 400%; animation: gradient 4s ease infinite; padding: 2rem; text-align: center; margin: 2rem 0; position: relative; overflow: hidden; border-radius: 8px; }
        @keyframes gradient { 0% { background-position: 0% 50%; } 50% { background-position: 100% 50%; } 100% { background-position: 0% 50%; } }
        .revolutionary-banner::before { content: ''; position: absolute; top: 0; left: 0; right: 0; bottom: 0; background: rgba(15, 15, 35, 0.8); }
        .revolutionary-content { position: relative; z-index: 1; }
        .revolutionary-title { font-size: 1.8rem; margin-bottom: 1rem; color: #ffffff; text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.8); }
    </style>
</head>
<body>
    <div class="matrix-bg"></div>
    
    <nav class="nav-header">
        <div class="nav-links">
            <a href="index.html" class="nav-link">🏠 Home</a>
            <a href="story.html" class="nav-link">📖 Story</a>
            <a href="GETTING_STARTED.html" class="nav-link getting-started">🚀 Getting Started</a>
            <a href="implementation.html" class="nav-link">⚙️ Implementation</a>
            <a href="literate-docs.html" class="nav-link">📚 Literate Docs</a>
            <a href="events/" class="nav-link">📡 Events</a>
            <a href="flows/" class="nav-link">🌊 Flows</a>
            <a href="ports/" class="nav-link">🚪 Ports</a>
            <a href="javadocs/" class="nav-link">📖 JavaDocs</a>
            <a href="https://github.com/rydnr/bytehot" class="nav-link">🔗 GitHub</a>
        </div>
    </nav>

    <div class="container">
        $body$
        
        <div class="footer">
            <p>&copy; 2025 ByteHot Project. Licensed under <a href="https://www.gnu.org/licenses/gpl-3.0.html">GPLv3</a></p>
            <p><a href="https://github.com/rydnr/bytehot">GitHub</a> • <a href="https://github.com/rydnr/bytehot/issues">Issues</a> • <a href="https://github.com/rydnr/bytehot/discussions">Discussions</a></p>
        </div>
    </div>

    <script>
        // Matrix rain effect
        const canvas = document.createElement('canvas');
        const ctx = canvas.getContext('2d');
        document.querySelector('.matrix-bg').appendChild(canvas);
        canvas.width = window.innerWidth;
        canvas.height = window.innerHeight;
        const matrix = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789@#$%^&*()*&^%+-/~{[|`]}";
        const drops = [];
        for(let x = 0; x < canvas.width / 10; x++) { drops[x] = 1; }
        function draw() {
            ctx.fillStyle = 'rgba(15, 15, 35, 0.04)';
            ctx.fillRect(0, 0, canvas.width, canvas.height);
            ctx.fillStyle = '#00ff00';
            ctx.font = '10px Courier New';
            for(let i = 0; i < drops.length; i++) {
                const text = matrix[Math.floor(Math.random() * matrix.length)];
                ctx.fillText(text, i * 10, drops[i] * 10);
                if(drops[i] * 10 > canvas.height && Math.random() > 0.975) { drops[i] = 0; }
                drops[i]++;
            }
        }
        setInterval(draw, 33);
        window.addEventListener('resize', () => { canvas.width = window.innerWidth; canvas.height = window.innerHeight; });
    </script>
</body>
</html>
EOF

# Function to convert org file to HTML with common template
convert_org_to_html() {
    local org_file=$1
    local html_file=$2
    local title=$3
    
    if [ -f "$org_file" ]; then
        echo "📖 Converting $org_file to $html_file..."
        
        # Create temporary template file
        temp_template=$(mktemp)
        echo "$COMMON_NAV_TEMPLATE" > "$temp_template"
        
        pandoc "$org_file" -o "$html_file" \
            --metadata title="$title" \
            --template="$temp_template" || echo "⚠️ Failed to convert $org_file"
        
        rm -f "$temp_template"
        echo "✅ Created $html_file"
    else
        echo "⚠️ $org_file not found"
    fi
}

# Create missing documentation files
echo "📋 Creating missing documentation files..."

# Implementation overview
if [ ! -f "bytehot/implementation.html" ]; then
    echo "🏗️ Creating implementation.html..."
    cat > bytehot/implementation.html << 'EOF'
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Implementation - ByteHot Documentation</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Courier New', monospace; background: linear-gradient(135deg, #0f0f23 0%, #1a1a3a 100%); color: #00ff00; line-height: 1.6; overflow-x: hidden; }
        .matrix-bg { position: fixed; top: 0; left: 0; width: 100%; height: 100%; pointer-events: none; z-index: -1; opacity: 0.1; }
        .nav-header { position: fixed; top: 0; left: 0; right: 0; background: rgba(15, 15, 35, 0.95); backdrop-filter: blur(10px); padding: 1rem 2rem; z-index: 1000; border-bottom: 2px solid #00ff00; }
        .nav-links { display: flex; justify-content: center; gap: 2rem; flex-wrap: wrap; }
        .nav-link { color: #00ff00; text-decoration: none; padding: 0.5rem 1rem; border: 1px solid #00ff00; border-radius: 4px; transition: all 0.3s ease; font-weight: bold; text-transform: uppercase; letter-spacing: 1px; }
        .nav-link:hover { background: #00ff00; color: #0f0f23; box-shadow: 0 0 20px #00ff00; transform: translateY(-2px); }
        .nav-link.getting-started { background: linear-gradient(45deg, #ff6b00, #ff0080); border-color: #ff6b00; color: white; animation: highlight 3s ease-in-out infinite; }
        @keyframes highlight { 0%, 100% { box-shadow: 0 0 10px #ff6b00; } 50% { box-shadow: 0 0 30px #ff6b00, 0 0 40px #ff0080; } }
        .nav-link.getting-started:hover { background: linear-gradient(45deg, #ff8b20, #ff20a0); transform: translateY(-3px) scale(1.05); }
        .container { max-width: 1200px; margin: 0 auto; background: rgba(26, 26, 58, 0.8); border: 1px solid #00ff00; border-radius: 8px; padding: 2rem; margin-top: 6rem; }
        h1, h2, h3 { color: #00cccc; text-shadow: 0 0 10px #00cccc; }
        h1 { font-size: 2.5rem; text-align: center; margin-bottom: 2rem; }
        p { color: #ffffff; margin: 1rem 0; line-height: 1.8; }
        a { color: #00cccc; text-decoration: none; }
        a:hover { text-shadow: 0 0 10px #00cccc; }
        .docs-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 1.5rem; margin: 2rem 0; }
        .docs-link { display: block; color: #00cccc; text-decoration: none; padding: 1rem; border: 1px solid #00cccc; border-radius: 6px; transition: all 0.3s ease; text-align: center; font-weight: bold; }
        .docs-link:hover { background: rgba(0, 204, 204, 0.1); transform: translateY(-2px); box-shadow: 0 5px 15px rgba(0, 204, 204, 0.3); }
        .footer { text-align: center; margin-top: 3rem; padding-top: 2rem; border-top: 1px solid #00ff00; color: #888; }
    </style>
</head>
<body>
    <div class="matrix-bg"></div>
    
    <nav class="nav-header">
        <div class="nav-links">
            <a href="index.html" class="nav-link">🏠 Home</a>
            <a href="story.html" class="nav-link">📖 Story</a>
            <a href="GETTING_STARTED.html" class="nav-link getting-started">🚀 Getting Started</a>
            <a href="implementation.html" class="nav-link">⚙️ Implementation</a>
            <a href="literate-docs.html" class="nav-link">📚 Literate Docs</a>
            <a href="events/" class="nav-link">📡 Events</a>
            <a href="flows/" class="nav-link">🌊 Flows</a>
            <a href="ports/" class="nav-link">🚪 Ports</a>
            <a href="javadocs/" class="nav-link">📖 JavaDocs</a>
            <a href="https://github.com/rydnr/bytehot" class="nav-link">🔗 GitHub</a>
        </div>
    </nav>

    <div class="container">
        <h1>🏗️ ByteHot Implementation Guide</h1>
        
        <p>ByteHot follows a strict Domain-Driven Design (DDD) and Hexagonal Architecture pattern, ensuring maintainable, testable, and extensible code that evolves with your needs.</p>
        
        <h2>🎯 Core Architecture Components</h2>
        <div class="docs-grid">
            <a href="ByteHot.html" class="docs-link">🏗️ Core Architecture</a>
            <a href="ByteHotApplication.html" class="docs-link">🎯 Application Layer</a>
            <a href="Ports.html" class="docs-link">🔌 Ports & Adapters</a>
            <a href="ByteHotAgent.html" class="docs-link">🤖 JVM Agent</a>
        </div>
        
        <h2>🔧 Infrastructure Components</h2>
        <div class="docs-grid">
            <a href="ErrorHandler.html" class="docs-link">🛡️ Error Handling</a>
            <a href="RollbackManager.html" class="docs-link">↩️ Rollback System</a>
            <a href="InstanceTracker.html" class="docs-link">📊 Instance Tracking</a>
            <a href="FrameworkIntegration.html" class="docs-link">🔗 Framework Integration</a>
        </div>
        
        <h2>📡 Event-Driven Architecture</h2>
        <div class="docs-grid">
            <a href="events/" class="docs-link">📡 Domain Events</a>
            <a href="flows/" class="docs-link">🌊 Process Flows</a>
            <a href="ports/" class="docs-link">🚪 Port Interfaces</a>
        </div>
        
        <div class="footer">
            <p>&copy; 2025 ByteHot Project. Licensed under <a href="https://www.gnu.org/licenses/gpl-3.0.html">GPLv3</a></p>
        </div>
    </div>
</body>
</html>
EOF
    echo "✅ Created implementation.html"
fi

# Convert specific org files to HTML with common template
convert_org_to_html "docs/ErrorHandler.org" "bytehot/ErrorHandler.html" "Error Handler - ByteHot Documentation"
convert_org_to_html "docs/RollbackManager.org" "bytehot/RollbackManager.html" "Rollback Manager - ByteHot Documentation"
convert_org_to_html "docs/InstanceTracker.org" "bytehot/InstanceTracker.html" "Instance Tracker - ByteHot Documentation"
convert_org_to_html "docs/FrameworkIntegration.org" "bytehot/FrameworkIntegration.html" "Framework Integration - ByteHot Documentation"

echo "✅ Missing documentation files creation completed!"