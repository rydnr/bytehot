#!/bin/bash
set -euo pipefail

# Create Clean Documentation Files Script
# This script creates clean HTML files from org sources with proper styling

echo "🧹 Creating clean documentation files..."

# Create bytehot directory if not exists
mkdir -p bytehot

# Function to create a properly styled HTML file
create_styled_html() {
    local org_file="$1"
    local output_file="$2"
    local title="$3"
    
    echo "📖 Creating $output_file from $org_file..."
    
    if [ ! -f "$org_file" ]; then
        echo "⚠️ Source file $org_file not found, creating placeholder..."
        
        # Create placeholder content
        local class_name=$(basename "$output_file" .html)
        cat > "$output_file" <<EOF
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>$title - ByteHot Documentation</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Courier New', monospace; background: linear-gradient(135deg, #0f0f23 0%, #1a1a3a 100%); color: #00ff00; line-height: 1.6; overflow-x: hidden; }
        .matrix-bg { position: fixed; top: 0; left: 0; width: 100%; height: 100%; pointer-events: none; z-index: -1; opacity: 0.05; }
        .nav-header { position: fixed; top: 0; left: 0; right: 0; background: rgba(15, 15, 35, 0.95); backdrop-filter: blur(10px); padding: 1rem 2rem; z-index: 1000; border-bottom: 2px solid #00ff00; }
        .nav-links { display: flex; justify-content: center; gap: 1.5rem; flex-wrap: wrap; }
        .nav-link { color: #00ff00; text-decoration: none; padding: 0.4rem 0.8rem; border: 1px solid #00ff00; border-radius: 4px; transition: all 0.3s ease; font-weight: bold; font-size: 0.9rem; }
        .nav-link:hover { background: #00ff00; color: #0f0f23; box-shadow: 0 0 15px #00ff00; transform: translateY(-2px); }
        .content { margin-top: 100px; padding: 2rem; max-width: 1200px; margin-left: auto; margin-right: auto; }
        .doc-container { background: rgba(26, 26, 58, 0.8); border: 1px solid #00ff00; border-radius: 12px; padding: 3rem; margin: 2rem 0; }
        .doc-container h1 { color: #00cccc; font-size: 2.5rem; margin: 0 0 2rem 0; text-shadow: 0 0 15px #00cccc; text-align: center; }
        .doc-container h2 { color: #00ff00; font-size: 1.8rem; margin: 2rem 0 1rem 0; border-left: 4px solid #00ff00; padding-left: 1rem; text-shadow: 0 0 10px #00ff00; }
        .doc-container h3 { color: #00cccc; font-size: 1.4rem; margin: 1.5rem 0 1rem 0; }
        .doc-container p { color: #ffffff; margin: 1rem 0; line-height: 1.8; }
        .doc-container ul, .doc-container ol { color: #ffffff; margin: 1rem 0; padding-left: 2rem; }
        .doc-container li { margin: 0.5rem 0; line-height: 1.6; }
        .doc-container code { background: rgba(0, 0, 0, 0.5); color: #00ff00; padding: 0.2rem 0.4rem; border-radius: 4px; font-family: 'Courier New', monospace; border: 1px solid rgba(0, 255, 0, 0.3); }
        .doc-container pre { background: rgba(0, 0, 0, 0.8); border: 1px solid #00ff00; border-radius: 8px; padding: 1.5rem; overflow-x: auto; margin: 1.5rem 0; }
        .doc-container pre code { background: none; padding: 0; border: none; }
        .doc-container a { color: #00cccc; text-decoration: none; border-bottom: 1px dotted #00cccc; transition: all 0.3s ease; }
        .doc-container a:hover { color: #ffffff; border-bottom-color: #ffffff; text-shadow: 0 0 5px #00cccc; }
        .footer { background: rgba(0, 0, 0, 0.8); padding: 2rem; text-align: center; border-top: 2px solid #00ff00; margin-top: 4rem; }
        .footer a { color: #00ff00; text-decoration: none; }
        .footer a:hover { text-shadow: 0 0 10px #00ff00; }
        @media (max-width: 768px) { .nav-links { gap: 0.8rem; } .nav-link { padding: 0.3rem 0.6rem; font-size: 0.8rem; } .content { padding: 1rem; } .doc-container { padding: 2rem; } .doc-container h1 { font-size: 2rem; } }
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
            <a href="literate-docs.html" class="nav-link">📚 Literate Docs</a>
            <a href="journal.html" class="nav-link">📔 Journal</a>
            <a href="javadocs/" class="nav-link">JavaDocs</a>
            <a href="https://github.com/rydnr/bytehot" class="nav-link">GitHub</a>
        </div>
    </nav>

    <div class="content">
        <div class="doc-container">
            <h1>$class_name</h1>
            
            <h2>Overview</h2>
            <p>The <code>$class_name</code> class is part of ByteHot's comprehensive architecture for runtime bytecode hot-swapping.</p>
            
            <h2>Responsibilities</h2>
            <p>This class handles essential functionality within the ByteHot system, following Domain-Driven Design principles and hexagonal architecture patterns.</p>
            
            <h2>Implementation</h2>
            <p>Detailed implementation documentation for this class is being developed. Please check back soon for comprehensive literate programming documentation.</p>
            
            <h2>Related Classes</h2>
            <p>See also:</p>
            <ul>
                <li><a href="ByteHot.html">ByteHot</a> - Core domain aggregate</li>
                <li><a href="ByteHotApplication.html">ByteHotApplication</a> - Application layer coordination</li>
                <li><a href="literate-docs.html">Complete Class Index</a> - All documented classes</li>
            </ul>
        </div>
    </div>

    <footer class="footer">
        <p>&copy; 2025 ByteHot Project. Licensed under <a href="https://www.gnu.org/licenses/gpl-3.0.html">GPLv3</a></p>
        <p>
            <a href="https://github.com/rydnr/bytehot">GitHub</a> • 
            <a href="https://github.com/rydnr/bytehot/issues">Issues</a> • 
            <a href="https://github.com/rydnr/bytehot/discussions">Discussions</a>
        </p>
    </footer>

    <script>
        // Matrix rain effect (lighter for documentation pages)
        const canvas = document.createElement('canvas');
        const ctx = canvas.getContext('2d');
        document.querySelector('.matrix-bg').appendChild(canvas);
        canvas.width = window.innerWidth;
        canvas.height = window.innerHeight;
        const matrix = "BYTEHOT0123456789";
        const drops = [];
        for(let x = 0; x < canvas.width / 15; x++) { drops[x] = 1; }
        function draw() {
            ctx.fillStyle = 'rgba(15, 15, 35, 0.05)';
            ctx.fillRect(0, 0, canvas.width, canvas.height);
            ctx.fillStyle = '#00ff00';
            ctx.font = '12px Courier New';
            for(let i = 0; i < drops.length; i++) {
                const text = matrix[Math.floor(Math.random() * matrix.length)];
                ctx.fillText(text, i * 15, drops[i] * 15);
                if(drops[i] * 15 > canvas.height && Math.random() > 0.98) { drops[i] = 0; }
                drops[i]++;
            }
        }
        setInterval(draw, 50);
        window.addEventListener('resize', () => { canvas.width = window.innerWidth; canvas.height = window.innerHeight; });
    </script>
</body>
</html>
EOF
        echo "✅ Created placeholder $output_file"
        return
    fi
    
    # Extract content from org file using pandoc to get clean HTML content
    temp_content=$(mktemp)
    
    # Convert org to clean HTML content only
    pandoc "$org_file" -t html --no-highlight 2>/dev/null | \
    sed -n '/<h1/,/<\/html>/p' | \
    sed '/<\/html>/d' > "$temp_content"
    
    # If pandoc failed or produced no content, create placeholder
    if [ ! -s "$temp_content" ]; then
        echo "⚠️ Pandoc conversion failed, creating placeholder for $output_file"
        local class_name=$(basename "$output_file" .html)
        cat > "$temp_content" <<EOF
<h1>$class_name</h1>
<h2>Overview</h2>
<p>Documentation for the <code>$class_name</code> class.</p>
<h2>Implementation</h2>
<p>Detailed implementation notes will be available soon.</p>
EOF
    fi
    
    # Create the complete HTML file with styling and navigation
    cat > "$output_file" <<EOF
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>$title - ByteHot Documentation</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Courier New', monospace; background: linear-gradient(135deg, #0f0f23 0%, #1a1a3a 100%); color: #00ff00; line-height: 1.6; overflow-x: hidden; }
        .matrix-bg { position: fixed; top: 0; left: 0; width: 100%; height: 100%; pointer-events: none; z-index: -1; opacity: 0.05; }
        .nav-header { position: fixed; top: 0; left: 0; right: 0; background: rgba(15, 15, 35, 0.95); backdrop-filter: blur(10px); padding: 1rem 2rem; z-index: 1000; border-bottom: 2px solid #00ff00; }
        .nav-links { display: flex; justify-content: center; gap: 1.5rem; flex-wrap: wrap; }
        .nav-link { color: #00ff00; text-decoration: none; padding: 0.4rem 0.8rem; border: 1px solid #00ff00; border-radius: 4px; transition: all 0.3s ease; font-weight: bold; font-size: 0.9rem; }
        .nav-link:hover { background: #00ff00; color: #0f0f23; box-shadow: 0 0 15px #00ff00; transform: translateY(-2px); }
        .content { margin-top: 100px; padding: 2rem; max-width: 1200px; margin-left: auto; margin-right: auto; }
        .doc-container { background: rgba(26, 26, 58, 0.8); border: 1px solid #00ff00; border-radius: 12px; padding: 3rem; margin: 2rem 0; }
        .doc-container h1 { color: #00cccc; font-size: 2.5rem; margin: 0 0 2rem 0; text-shadow: 0 0 15px #00cccc; text-align: center; }
        .doc-container h2 { color: #00ff00; font-size: 1.8rem; margin: 2rem 0 1rem 0; border-left: 4px solid #00ff00; padding-left: 1rem; text-shadow: 0 0 10px #00ff00; }
        .doc-container h3 { color: #00cccc; font-size: 1.4rem; margin: 1.5rem 0 1rem 0; }
        .doc-container h4, .doc-container h5, .doc-container h6 { color: #ffffff; margin: 1rem 0 0.5rem 0; }
        .doc-container p { color: #ffffff; margin: 1rem 0; line-height: 1.8; }
        .doc-container ul, .doc-container ol { color: #ffffff; margin: 1rem 0; padding-left: 2rem; }
        .doc-container li { margin: 0.5rem 0; line-height: 1.6; }
        .doc-container code { background: rgba(0, 0, 0, 0.5); color: #00ff00; padding: 0.2rem 0.4rem; border-radius: 4px; font-family: 'Courier New', monospace; border: 1px solid rgba(0, 255, 0, 0.3); }
        .doc-container pre { background: rgba(0, 0, 0, 0.8); border: 1px solid #00ff00; border-radius: 8px; padding: 1.5rem; overflow-x: auto; margin: 1.5rem 0; }
        .doc-container pre code { background: none; padding: 0; border: none; }
        .doc-container a { color: #00cccc; text-decoration: none; border-bottom: 1px dotted #00cccc; transition: all 0.3s ease; }
        .doc-container a:hover { color: #ffffff; border-bottom-color: #ffffff; text-shadow: 0 0 5px #00cccc; }
        .doc-container table { width: 100%; border-collapse: collapse; margin: 1.5rem 0; background: rgba(0, 0, 0, 0.3); }
        .doc-container th, .doc-container td { border: 1px solid #00ff00; padding: 0.8rem; text-align: left; }
        .doc-container th { background: rgba(0, 255, 0, 0.1); color: #00ff00; font-weight: bold; }
        .doc-container td { color: #ffffff; }
        .doc-container blockquote { border-left: 4px solid #00cccc; padding-left: 1.5rem; margin: 1.5rem 0; color: #00cccc; font-style: italic; background: rgba(0, 204, 204, 0.05); padding: 1rem 1rem 1rem 1.5rem; border-radius: 0 8px 8px 0; }
        .footer { background: rgba(0, 0, 0, 0.8); padding: 2rem; text-align: center; border-top: 2px solid #00ff00; margin-top: 4rem; }
        .footer a { color: #00ff00; text-decoration: none; }
        .footer a:hover { text-shadow: 0 0 10px #00ff00; }
        @media (max-width: 768px) { .nav-links { gap: 0.8rem; } .nav-link { padding: 0.3rem 0.6rem; font-size: 0.8rem; } .content { padding: 1rem; } .doc-container { padding: 2rem; } .doc-container h1 { font-size: 2rem; } }
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
            <a href="literate-docs.html" class="nav-link">📚 Literate Docs</a>
            <a href="journal.html" class="nav-link">📔 Journal</a>
            <a href="javadocs/" class="nav-link">JavaDocs</a>
            <a href="https://github.com/rydnr/bytehot" class="nav-link">GitHub</a>
        </div>
    </nav>

    <div class="content">
        <div class="doc-container">
EOF

    # Add the converted content
    cat "$temp_content" >> "$output_file"
    
    # Close the HTML structure
    cat >> "$output_file" <<'EOF'
        </div>
    </div>

    <footer class="footer">
        <p>&copy; 2025 ByteHot Project. Licensed under <a href="https://www.gnu.org/licenses/gpl-3.0.html">GPLv3</a></p>
        <p>
            <a href="https://github.com/rydnr/bytehot">GitHub</a> • 
            <a href="https://github.com/rydnr/bytehot/issues">Issues</a> • 
            <a href="https://github.com/rydnr/bytehot/discussions">Discussions</a>
        </p>
    </footer>

    <script>
        // Matrix rain effect (lighter for documentation pages)
        const canvas = document.createElement('canvas');
        const ctx = canvas.getContext('2d');
        document.querySelector('.matrix-bg').appendChild(canvas);
        canvas.width = window.innerWidth;
        canvas.height = window.innerHeight;
        const matrix = "BYTEHOT0123456789";
        const drops = [];
        for(let x = 0; x < canvas.width / 15; x++) { drops[x] = 1; }
        function draw() {
            ctx.fillStyle = 'rgba(15, 15, 35, 0.05)';
            ctx.fillRect(0, 0, canvas.width, canvas.height);
            ctx.fillStyle = '#00ff00';
            ctx.font = '12px Courier New';
            for(let i = 0; i < drops.length; i++) {
                const text = matrix[Math.floor(Math.random() * matrix.length)];
                ctx.fillText(text, i * 15, drops[i] * 15);
                if(drops[i] * 15 > canvas.height && Math.random() > 0.98) { drops[i] = 0; }
                drops[i]++;
            }
        }
        setInterval(draw, 50);
        window.addEventListener('resize', () => { canvas.width = window.innerWidth; canvas.height = window.innerHeight; });
    </script>
</body>
</html>
EOF
    
    # Clean up
    rm -f "$temp_content"
    echo "✅ Created $output_file"
}

# Create the missing documentation files
create_styled_html "docs/ErrorHandler.org" "bytehot/ErrorHandler.html" "ErrorHandler"
create_styled_html "docs/RollbackManager.org" "bytehot/RollbackManager.html" "RollbackManager"
create_styled_html "docs/InstanceTracker.org" "bytehot/InstanceTracker.html" "InstanceTracker"
create_styled_html "docs/FrameworkIntegration.org" "bytehot/FrameworkIntegration.html" "FrameworkIntegration"

echo "✅ Clean documentation files created successfully!"