#!/bin/bash
set -euo pipefail

# ByteHot Documentation Generation Script
# This script generates comprehensive documentation for the ByteHot project
# NEW APPROACH: Use docs/ content as main site, story.org as linked page

echo "🚀 Starting ByteHot documentation generation..."

# Create necessary directories
echo "📁 Creating documentation directories..."
mkdir -p bytehot

# Step 1: Copy the new-style docs/ content as the main site
echo "📂 Moving docs/ content to root site..."
if [ -d "docs" ]; then
    # Copy the main index.html from docs/ as the primary index
    cp docs/index.html bytehot/index.html
    echo "✅ Copied new-style index.html as main page"
    
    # Copy all other docs files and preserve structure
    cp -r docs/* bytehot/ 2>/dev/null || true
    echo "✅ Copied all documentation files"
else
    echo "⚠️ docs directory not found!"
fi

# Step 2: Generate story page from story.org (will be linked in nav)
echo "📖 Generating story page from story.org..."
bash ./.github/scripts/generate-story-page.sh

# Step 3: Generate journal page from journal.org
echo "📔 Generating journal page..."
bash ./.github/scripts/generate-journal.sh

# Step 4: Fix broken links in the copied files
echo "🔧 Fixing broken links..."
bash ./.github/scripts/fix-links.sh

# Step 5: Fix event links to point to HTML files instead of org files
echo "🔗 Fixing event links..."
bash ./.github/scripts/fix-event-links.sh

# Step 6: Create missing HTML files for broken links
echo "🔧 Creating missing HTML files..."
bash ./.github/scripts/create-missing-html.sh

# Step 7: Create comprehensive documentation files
echo "📚 Creating comprehensive documentation..."
bash ./.github/scripts/create-comprehensive-docs.sh

# Step 8: Create literate programming documentation
echo "📖 Creating literate programming documentation..."
bash ./.github/scripts/create-literate-docs.sh

# Step 9: Add class cross-references
echo "🔗 Adding class cross-references..."
bash ./.github/scripts/add-class-crossrefs.sh

# Step 10: Apply new style to all documentation pages
echo "🎨 Applying new matrix style to all pages..."
bash ./.github/scripts/apply-new-style.sh

echo "✅ Documentation generation completed successfully!"