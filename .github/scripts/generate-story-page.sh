#!/bin/bash
set -euo pipefail

# Generate story.html from story.org with new matrix style

echo "📖 Creating story page from story.org..."

# Ensure bytehot directory exists
mkdir -p bytehot

if [ -f "story.org" ]; then
    echo "📄 Found story.org, converting to HTML with new style..."
    
    # Convert org to HTML content first
    temp_content=$(mktemp)
    if pandoc -f org -t html5 --toc story.org -o "$temp_content" --metadata title="ByteHot Story"; then
        echo "✅ Pandoc conversion successful"
        
        # Create story page with new matrix style
        cat > bytehot/story.html << 'HTML_EOF'
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ByteHot Story - Revolutionary Development Journey</title>
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
            opacity: 0.1;
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
            gap: 2rem;
            flex-wrap: wrap;
        }

        .nav-link {
            color: #00ff00;
            text-decoration: none;
            padding: 0.5rem 1rem;
            border: 1px solid #00ff00;
            border-radius: 4px;
            transition: all 0.3s ease;
            font-weight: bold;
            text-transform: uppercase;
            letter-spacing: 1px;
        }

        .nav-link:hover {
            background: #00ff00;
            color: #0f0f23;
            box-shadow: 0 0 20px #00ff00;
            transform: translateY(-2px);
        }

        .content {
            margin-top: 120px;
            padding: 2rem;
            max-width: 1200px;
            margin-left: auto;
            margin-right: auto;
        }

        .story-title {
            font-size: 3rem;
            text-align: center;
            margin-bottom: 2rem;
            color: #00ff00;
            text-shadow: 0 0 20px #00ff00;
            animation: glow 2s ease-in-out infinite alternate;
        }

        @keyframes glow {
            from { text-shadow: 0 0 20px #00ff00; }
            to { text-shadow: 0 0 30px #00ff00, 0 0 40px #00ff00; }
        }

        .story-content {
            background: rgba(26, 26, 58, 0.8);
            border: 1px solid #00ff00;
            border-radius: 12px;
            padding: 3rem;
            margin: 2rem 0;
        }

        .story-content h1 {
            color: #00cccc;
            font-size: 2rem;
            margin: 2rem 0 1rem 0;
            text-shadow: 0 0 10px #00cccc;
        }

        .story-content h2 {
            color: #00ff00;
            font-size: 1.5rem;
            margin: 1.5rem 0 1rem 0;
            border-left: 4px solid #00ff00;
            padding-left: 1rem;
        }

        .story-content h3 {
            color: #ffffff;
            font-size: 1.2rem;
            margin: 1rem 0 0.5rem 0;
        }

        .story-content p {
            color: #ffffff;
            margin: 1rem 0;
            line-height: 1.8;
        }

        .story-content ul, .story-content ol {
            color: #ffffff;
            margin: 1rem 0;
            padding-left: 2rem;
        }

        .story-content li {
            margin: 0.5rem 0;
        }

        .story-content code {
            background: rgba(0, 0, 0, 0.5);
            color: #00ff00;
            padding: 0.2rem 0.4rem;
            border-radius: 4px;
            font-family: 'Courier New', monospace;
        }

        .story-content pre {
            background: rgba(0, 0, 0, 0.8);
            border: 1px solid #00ff00;
            border-radius: 8px;
            padding: 1rem;
            overflow-x: auto;
            margin: 1rem 0;
        }

        .story-content pre code {
            background: none;
            padding: 0;
        }

        .story-content a {
            color: #00cccc;
            text-decoration: none;
            border-bottom: 1px dotted #00cccc;
        }

        .story-content a:hover {
            color: #ffffff;
            border-bottom-color: #ffffff;
            text-shadow: 0 0 5px #00cccc;
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
            .story-title {
                font-size: 2rem;
            }
            
            .nav-links {
                gap: 1rem;
            }
            
            .nav-link {
                padding: 0.4rem 0.8rem;
                font-size: 0.9rem;
            }
            
            .content {
                padding: 1rem;
            }
            
            .story-content {
                padding: 2rem;
            }
        }
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
        <h1 class="story-title">📖 ByteHot Story</h1>
        <p style="text-align: center; color: #00cccc; font-size: 1.2rem; margin-bottom: 3rem;">
            The Revolutionary Journey of JVM Hot-Swapping Development
        </p>
        
        <div class="story-content">
HTML_EOF
        
        # Add converted content
        cat "$temp_content" >> bytehot/story.html
        
        # Close HTML structure
        cat >> bytehot/story.html << 'HTML_EOF'
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
        // Matrix rain effect
        const canvas = document.createElement('canvas');
        const ctx = canvas.getContext('2d');
        document.querySelector('.matrix-bg').appendChild(canvas);

        canvas.width = window.innerWidth;
        canvas.height = window.innerHeight;

        const matrix = "BYTEHOT0123456789@#$%^&*()*&^%+-/~{[|`]}";
        const drops = [];

        for(let x = 0; x < canvas.width / 10; x++) {
            drops[x] = 1;
        }

        function draw() {
            ctx.fillStyle = 'rgba(15, 15, 35, 0.04)';
            ctx.fillRect(0, 0, canvas.width, canvas.height);

            ctx.fillStyle = '#00ff00';
            ctx.font = '10px Courier New';

            for(let i = 0; i < drops.length; i++) {
                const text = matrix[Math.floor(Math.random() * matrix.length)];
                ctx.fillText(text, i * 10, drops[i] * 10);

                if(drops[i] * 10 > canvas.height && Math.random() > 0.975) {
                    drops[i] = 0;
                }
                drops[i]++;
            }
        }

        setInterval(draw, 33);

        // Resize canvas on window resize
        window.addEventListener('resize', () => {
            canvas.width = window.innerWidth;
            canvas.height = window.innerHeight;
        });
    </script>
</body>
</html>
HTML_EOF
        
        echo "✅ Story page created from story.org"
        
        # Clean up temp file
        rm "$temp_content"
    else
        echo "⚠️ Pandoc conversion failed, creating placeholder story page"
        cat > bytehot/story.html << 'HTML_EOF'
<!DOCTYPE html>
<html>
<head>
    <title>ByteHot Story</title>
</head>
<body>
    <h1>ByteHot Story</h1>
    <p>Story content could not be generated from story.org</p>
</body>
</html>
HTML_EOF
        # Clean up temp file
        rm -f "$temp_content"
    fi
else
    echo "⚠️ story.org not found, creating placeholder story page"
    cat > bytehot/story.html << 'HTML_EOF'
<!DOCTYPE html>
<html>
<head>
    <title>ByteHot Story</title>
</head>
<body>
    <h1>ByteHot Story</h1>
    <p>story.org file not found in repository root</p>
</body>
</html>
HTML_EOF
fi