#!/bin/bash
set -euo pipefail

# ByteHot Documentation Generation Script
# This script generates comprehensive documentation for the ByteHot project

echo "🚀 Starting ByteHot documentation generation..."

# Create necessary directories
echo "📁 Creating documentation directories..."
mkdir -p bytehot/docs
mkdir -p bytehot/javadocs

# Copy CSS styling
echo "📎 Copying ByteHot CSS styling..."
if cp .github/resources/bytehot-style.css bytehot/style.css; then
    echo "✅ ByteHot CSS successfully copied"
else
    echo "⚠️ CSS file not found, creating fallback..."
    cat > bytehot/style.css << 'EOF'
/* ByteHot Documentation Styling - Fallback */
body { font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif; max-width: 1280px; margin: 0 auto; padding: 0; line-height: 1.6; color: #333; }
h1 { color: #2c3e50; font-size: 2.5rem; border-bottom: 3px solid #3498db; padding-bottom: 0.5rem; }
h2 { color: #34495e; font-size: 1.8rem; border-left: 4px solid #3498db; padding-left: 1rem; }
code { background: #f8f9fa; color: #e74c3c; padding: 0.2rem 0.4rem; border-radius: 4px; font-weight: 600; }
pre { background: #f8f9fa; border: 1px solid #e1e8ed; padding: 1.5rem; border-radius: 8px; overflow-x: auto; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
.navigation { background: linear-gradient(135deg, #2c3e50 0%, #3498db 100%); color: white; padding: 0.75rem 1rem; margin-bottom: 2rem; width: 100%; box-sizing: border-box; display: flex; flex-wrap: wrap; align-items: center; }
.navigation a { color: #ecf0f1; text-decoration: none; padding: 0.4rem 0.8rem; margin: 0.2rem 0.3rem; border-radius: 4px; white-space: nowrap; }
.navigation a:hover { background: rgba(255,255,255,0.1); }
.footer { background: linear-gradient(135deg, #2c3e50 0%, #34495e 100%); color: #ecf0f1; text-align: center; padding: 0.5rem 0; margin-top: 2rem; width: 100%; box-sizing: border-box; font-size: 0.9rem; }
.footer a { color: #3498db; text-decoration: none; }
.footer a:hover { text-decoration: underline; }
main { padding: 2rem; }
ul, ol { text-align: left; margin: 1rem 0; padding-left: 2rem; }
li { margin: 0.5rem 0; text-align: left; }
ul li { list-style-type: disc; list-style-position: outside; }
ol li { list-style-type: decimal; list-style-position: outside; }
@media (max-width: 768px) { .navigation { padding: 0.5rem; } .navigation a { padding: 0.3rem 0.6rem; margin: 0.1rem 0.2rem; font-size: 0.9rem; } main { padding: 1rem; } }
@media (max-width: 480px) { .navigation { flex-direction: column; align-items: stretch; } .navigation a { margin: 0.1rem 0; text-align: center; } }
EOF
fi

# Generate index page from story.org
echo "📄 Generating index page..."
./.github/scripts/generate-index.sh

# Generate journal page  
echo "📔 Generating journal page..."
./.github/scripts/generate-journal.sh

# Convert org documentation files
echo "📚 Converting documentation files..."
./.github/scripts/convert-docs.sh

# Generate implementation page
echo "📋 Generating implementation page..."
./.github/scripts/generate-implementation.sh

echo "✅ Documentation generation completed successfully!"