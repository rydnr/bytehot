#!/bin/bash
set -euo pipefail

# Apply new matrix style to all documentation pages

echo "🎨 Applying new matrix style to all documentation pages..."

# Function to convert a page to new style
convert_to_new_style() {
    local file="$1"
    local title="$2"
    local nav_type="$3"  # "root" or "subdir"
    
    if [ ! -f "$file" ]; then
        return
    fi
    
    echo "🎨 Converting $file to new style..."
    
    # Extract content between <body> and </body>, excluding existing nav and footer
    temp_content=$(mktemp)
    temp_full=$(mktemp)
    
    # Extract just the main content, excluding navigation and footer
    awk '
    /<body[^>]*>/ { in_body = 1; next }
    /<\/body>/ { in_body = 0; next }
    in_body && /<div class="navigation">/ { in_nav = 1; next }
    in_body && in_nav && /<\/div>/ { in_nav = 0; next }
    in_body && !in_nav && /<main>/ { in_main = 1; next }
    in_body && !in_nav && /<\/main>/ { in_main = 0; next }
    in_body && !in_nav && /<div class="footer">/ { in_footer = 1; next }
    in_body && !in_nav && in_footer && /<\/div>/ { in_footer = 0; next }
    in_body && !in_nav && !in_footer { print }
    ' "$file" > "$temp_content"
    
    # If content is too small, try a different extraction method
    if [ ! -s "$temp_content" ] || [ $(wc -l < "$temp_content") -lt 3 ]; then
        # Fallback: extract everything between body tags
        sed -n '/<body[^>]*>/,/<\/body>/p' "$file" | sed '1d;$d' > "$temp_content"
    fi
    
    # Set navigation links based on location
    if [ "$nav_type" = "subdir" ]; then
        nav_links='
            <a href="../index.html" class="nav-link">Home</a>
            <a href="../story.html" class="nav-link">📖 Story</a>
            <a href="../GETTING_STARTED.html" class="nav-link">🚀 Getting Started</a>
            <a href="../implementation.html" class="nav-link">⚙️ Implementation</a>
            <a href="../journal.html" class="nav-link">📔 Journal</a>
            <a href="../javadocs/" class="nav-link">JavaDocs</a>
            <a href="https://github.com/rydnr/bytehot" class="nav-link">GitHub</a>'
    else
        nav_links='
            <a href="index.html" class="nav-link">Home</a>
            <a href="story.html" class="nav-link">📖 Story</a>
            <a href="GETTING_STARTED.html" class="nav-link">🚀 Getting Started</a>
            <a href="implementation.html" class="nav-link">⚙️ Implementation</a>
            <a href="journal.html" class="nav-link">📔 Journal</a>
            <a href="javadocs/" class="nav-link">JavaDocs</a>
            <a href="https://github.com/rydnr/bytehot" class="nav-link">GitHub</a>'
    fi
    
    # Create new styled page
    cat > "$temp_full" << HTML_EOF
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>$title - ByteHot Documentation</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Courier New', monospace;
            background: linear-gradient(135deg, #0f0f23 0%, #1a1a3a 100%);
            color: #00ff00;
            line-height: 1.6;
            overflow-x: hidden;
        }

        .matrix-bg {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            pointer-events: none;
            z-index: -1;
            opacity: 0.05;
        }

        .nav-header {
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            background: rgba(15, 15, 35, 0.95);
            backdrop-filter: blur(10px);
            padding: 1rem 2rem;
            z-index: 1000;
            border-bottom: 2px solid #00ff00;
        }

        .nav-links {
            display: flex;
            justify-content: center;
            gap: 1.5rem;
            flex-wrap: wrap;
        }

        .nav-link {
            color: #00ff00;
            text-decoration: none;
            padding: 0.4rem 0.8rem;
            border: 1px solid #00ff00;
            border-radius: 4px;
            transition: all 0.3s ease;
            font-weight: bold;
            font-size: 0.9rem;
        }

        .nav-link:hover {
            background: #00ff00;
            color: #0f0f23;
            box-shadow: 0 0 15px #00ff00;
            transform: translateY(-2px);
        }

        .content {
            margin-top: 100px;
            padding: 2rem;
            max-width: 1200px;
            margin-left: auto;
            margin-right: auto;
        }

        .doc-container {
            background: rgba(26, 26, 58, 0.8);
            border: 1px solid #00ff00;
            border-radius: 12px;
            padding: 3rem;
            margin: 2rem 0;
        }

        .doc-container h1 {
            color: #00cccc;
            font-size: 2.5rem;
            margin: 0 0 2rem 0;
            text-shadow: 0 0 15px #00cccc;
            text-align: center;
        }

        .doc-container h2 {
            color: #00ff00;
            font-size: 1.8rem;
            margin: 2rem 0 1rem 0;
            border-left: 4px solid #00ff00;
            padding-left: 1rem;
            text-shadow: 0 0 10px #00ff00;
        }

        .doc-container h3 {
            color: #00cccc;
            font-size: 1.4rem;
            margin: 1.5rem 0 1rem 0;
        }

        .doc-container h4, .doc-container h5, .doc-container h6 {
            color: #ffffff;
            margin: 1rem 0 0.5rem 0;
        }

        .doc-container p {
            color: #ffffff;
            margin: 1rem 0;
            line-height: 1.8;
        }

        .doc-container ul, .doc-container ol {
            color: #ffffff;
            margin: 1rem 0;
            padding-left: 2rem;
        }

        .doc-container li {
            margin: 0.5rem 0;
            line-height: 1.6;
        }

        .doc-container code {
            background: rgba(0, 0, 0, 0.5);
            color: #00ff00;
            padding: 0.2rem 0.4rem;
            border-radius: 4px;
            font-family: 'Courier New', monospace;
            border: 1px solid rgba(0, 255, 0, 0.3);
        }

        .doc-container pre {
            background: rgba(0, 0, 0, 0.8);
            border: 1px solid #00ff00;
            border-radius: 8px;
            padding: 1.5rem;
            overflow-x: auto;
            margin: 1.5rem 0;
        }

        .doc-container pre code {
            background: none;
            padding: 0;
            border: none;
        }

        .doc-container a {
            color: #00cccc;
            text-decoration: none;
            border-bottom: 1px dotted #00cccc;
            transition: all 0.3s ease;
        }

        .doc-container a:hover {
            color: #ffffff;
            border-bottom-color: #ffffff;
            text-shadow: 0 0 5px #00cccc;
        }

        .doc-container table {
            width: 100%;
            border-collapse: collapse;
            margin: 1.5rem 0;
            background: rgba(0, 0, 0, 0.3);
        }

        .doc-container th, .doc-container td {
            border: 1px solid #00ff00;
            padding: 0.8rem;
            text-align: left;
        }

        .doc-container th {
            background: rgba(0, 255, 0, 0.1);
            color: #00ff00;
            font-weight: bold;
        }

        .doc-container td {
            color: #ffffff;
        }

        .doc-container blockquote {
            border-left: 4px solid #00cccc;
            padding-left: 1.5rem;
            margin: 1.5rem 0;
            color: #00cccc;
            font-style: italic;
            background: rgba(0, 204, 204, 0.05);
            padding: 1rem 1rem 1rem 1.5rem;
            border-radius: 0 8px 8px 0;
        }

        .footer {
            background: rgba(0, 0, 0, 0.8);
            padding: 2rem;
            text-align: center;
            border-top: 2px solid #00ff00;
            margin-top: 4rem;
        }

        .footer a {
            color: #00ff00;
            text-decoration: none;
        }

        .footer a:hover {
            text-shadow: 0 0 10px #00ff00;
        }

        @media (max-width: 768px) {
            .nav-links {
                gap: 0.8rem;
            }
            
            .nav-link {
                padding: 0.3rem 0.6rem;
                font-size: 0.8rem;
            }
            
            .content {
                padding: 1rem;
            }
            
            .doc-container {
                padding: 2rem;
            }
            
            .doc-container h1 {
                font-size: 2rem;
            }
        }
    </style>
</head>
<body>
    <div class="matrix-bg"></div>
    
    <nav class="nav-header">
        <div class="nav-links">$nav_links
        </div>
    </nav>

    <div class="content">
        <div class="doc-container">
HTML_EOF
    
    # Add the extracted content
    cat "$temp_content" >> "$temp_full"
    
    # Close the HTML structure
    cat >> "$temp_full" << 'HTML_EOF'
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

        for(let x = 0; x < canvas.width / 15; x++) {
            drops[x] = 1;
        }

        function draw() {
            ctx.fillStyle = 'rgba(15, 15, 35, 0.05)';
            ctx.fillRect(0, 0, canvas.width, canvas.height);

            ctx.fillStyle = '#00ff00';
            ctx.font = '12px Courier New';

            for(let i = 0; i < drops.length; i++) {
                const text = matrix[Math.floor(Math.random() * matrix.length)];
                ctx.fillText(text, i * 15, drops[i] * 15);

                if(drops[i] * 15 > canvas.height && Math.random() > 0.98) {
                    drops[i] = 0;
                }
                drops[i]++;
            }
        }

        setInterval(draw, 50);

        // Resize canvas on window resize
        window.addEventListener('resize', () => {
            canvas.width = window.innerWidth;
            canvas.height = window.innerHeight;
        });
    </script>
</body>
</html>
HTML_EOF
    
    # Replace the original file with the new styled version
    mv "$temp_full" "$file"
    echo "✅ Converted $file to new matrix style"
    
    # Clean up temp files
    rm -f "$temp_content"
}

# Apply new style to documentation files
if [ -d "bytehot" ]; then
    echo "🎨 Converting documentation files to new matrix style..."
    
    # Skip the main index.html as it already has the new style
    # Convert all other HTML files in root (except index.html)
    for file in bytehot/*.html; do
        if [ -f "$file" ] && [ "$(basename "$file")" != "index.html" ]; then
            filename=$(basename "$file" .html)
            convert_to_new_style "$file" "$filename" "root"
        fi
    done
    
    # Convert files in subdirectories
    for subdir in bytehot/*/; do
        if [ -d "$subdir" ]; then
            subdir_name=$(basename "$subdir")
            echo "📂 Converting files in $subdir_name/"
            
            for file in "$subdir"*.html; do
                if [ -f "$file" ]; then
                    filename=$(basename "$file" .html)
                    convert_to_new_style "$file" "$subdir_name/$filename" "subdir"
                fi
            done
        fi
    done
    
    echo "✅ New matrix style applied to all documentation pages"
else
    echo "⚠️ bytehot directory not found"
fi