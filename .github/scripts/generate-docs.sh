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
    # Copy the main index.html from docs/ as the primary index if it exists
    if [ -f "docs/index.html" ]; then
        cp docs/index.html bytehot/index.html
        echo "✅ Copied existing index.html as main page"
    else
        echo "ℹ️ No index.html found in docs/, will generate from story.org later"
    fi

    # Copy all other docs files and preserve structure
    cp -r docs/* bytehot/ 2>/dev/null || true
    echo "✅ Copied all documentation files"
else
    echo "⚠️ docs directory not found!"
fi

# Step 2: Generate story page from story.org (will be linked in nav)
echo "📖 Generating story page from story.org..."
bash ./.github/scripts/generate-html-from-org.sh "story.org" "📖 ByteHot Story" "The Revolutionary Journey of JVM Hot-Swapping Development"

# Step 3: Generate journal page from journal.org
echo "📔 Generating journal page..."
bash ./.github/scripts/generate-html-from-org.sh "journal.org" "📔 ByteHot Development Journal"

# Step 3: Generate getting-started page from GETTING_STARTED.org
echo "📔 Generating getting-started page..."
bash ./.github/scripts/generate-html-from-org.sh "GETTING_STARTED.org" "🚀 Getting Started Guide"

# Step 3.5: Generate index.html if it doesn't exist
if [ ! -f "bytehot/index.html" ]; then
    echo "🏠 Generating index.html from story.org..."
    bash ./.github/scripts/generate-index.sh
fi

# Step 4: Apply the new unified style to all HTML files
echo "🎨 Applying unified style to all HTML files..."

# Get the CSS content safely
CSS_CONTENT=$(bash .github/scripts/unified-style.sh)

for file in bytehot/*.html; do
    if [ -f "$file" ]; then
        echo "  🎨 Styling $file..."
        # Create a temporary file with the new content
        temp_file=$(mktemp)
        
        # Use awk to replace the style section safely
        awk -v css_content="$CSS_CONTENT" '
        /<style>/ { 
            print "<style>"
            print css_content
            in_style = 1
            next
        }
        /<\/style>/ && in_style { 
            print "</style>"
            in_style = 0
            next
        }
        !in_style { print }
        ' "$file" > "$temp_file"
        
        # Replace the original file
        mv "$temp_file" "$file"
    fi
done

# Step 4.5: Replace NAV placeholders with actual navigation
echo "🧭 Replacing navigation placeholders..."
NAV_CONTENT=$(bash .github/scripts/nav.sh)

for file in bytehot/*.html; do
    if [ -f "$file" ]; then
        if grep -q '${NAV}' "$file"; then
            echo "  🧭 Adding navigation to $file..."
            # Create a temporary file with nav replacement
            temp_file=$(mktemp)
            
            # Replace ${NAV} with actual navigation content
            awk -v nav_content="$NAV_CONTENT" '
            /\${NAV}/ { 
                print nav_content
                next
            }
            { print }
            ' "$file" > "$temp_file"
            
            # Replace the original file
            mv "$temp_file" "$file"
        fi
    fi
done

# Step 4.6: Replace CSS placeholders with actual CSS content
echo "🎨 Replacing CSS placeholders..."

for file in bytehot/*.html; do
    if [ -f "$file" ]; then
        if grep -q '${CSS}' "$file"; then
            echo "  🎨 Adding CSS to $file..."
            # Create a temporary file with CSS replacement
            temp_file=$(mktemp)
            
            # Replace ${CSS} with actual CSS content
            awk -v css_content="$CSS_CONTENT" '
            /\${CSS}/ { 
                print css_content
                next
            }
            { print }
            ' "$file" > "$temp_file"
            
            # Replace the original file
            mv "$temp_file" "$file"
        fi
    fi
done

# Step 5: Fix broken links in the copied files
echo "🔧 Fixing broken links..."
bash ./.github/scripts/fix-links.sh

# Step 6: Fix event links to point to HTML files instead of org files
echo "🔗 Fixing event links..."
bash ./.github/scripts/fix-event-links.sh

# Step 7: Create missing HTML files for broken links (with proper styling)
echo "🔧 Creating missing HTML files..."
bash ./.github/scripts/create-missing-html.sh

# Step 8: Create literate programming documentation (standalone file)
echo "📖 Creating literate programming documentation..."
bash ./.github/scripts/create-literate-docs.sh

# Step 9: Final placeholder replacement for newly created files
echo "🔧 Final placeholder replacement..."
NAV_CONTENT=$(bash .github/scripts/nav.sh)
CSS_CONTENT=$(bash .github/scripts/unified-style.sh)
FOOTER_CONTENT=$(bash .github/scripts/footer.sh)
MATRIX_CONTENT=$(bash .github/scripts/matrix.sh)

for file in bytehot/*.html; do
    if [ -f "$file" ]; then
        # Check and replace NAV placeholders
        if grep -q '${NAV}' "$file"; then
            echo "  🧭 Adding navigation to $file..."
            temp_file=$(mktemp)
            awk -v nav_content="$NAV_CONTENT" '/\${NAV}/ { print nav_content; next } { print }' "$file" > "$temp_file"
            mv "$temp_file" "$file"
        fi
        
        # Check and replace CSS placeholders  
        if grep -q '${CSS}' "$file"; then
            echo "  🎨 Adding CSS to $file..."
            temp_file=$(mktemp)
            awk -v css_content="$CSS_CONTENT" '/\${CSS}/ { print css_content; next } { print }' "$file" > "$temp_file"
            mv "$temp_file" "$file"
        fi
        
        # Check and replace FOOTER placeholders
        if grep -q '${FOOTER}' "$file"; then
            echo "  🦶 Adding footer to $file..."
            temp_file=$(mktemp)
            awk -v footer_content="$FOOTER_CONTENT" '/\${FOOTER}/ { print footer_content; next } { print }' "$file" > "$temp_file"
            mv "$temp_file" "$file"
        fi
        
        # Check and replace MATRIX placeholders
        if grep -q '${MATRIX}' "$file"; then
            echo "  🔢 Adding matrix style to $file..."
            temp_file=$(mktemp)
            awk -v matrix_content="$MATRIX_CONTENT" '/\${MATRIX}/ { print matrix_content; next } { print }' "$file" > "$temp_file"
            mv "$temp_file" "$file"
        fi
    fi
done

echo "✅ Documentation generation completed successfully!"
