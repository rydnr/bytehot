#!/bin/bash
set -euo pipefail

# ByteHot Org Files Preprocessing Script
# Fixes org-mode code blocks for proper Pandoc conversion

echo "🔧 Pre-processing org files to ensure proper code block formatting..."

# Create a Python script to fix org-mode code blocks
cat > fix_org.py << 'EOF'
import re
import os
from pathlib import Path

def fix_org_code_blocks(content):
    # Convert markdown-style code blocks to org-mode format
    content = re.sub(r'^```(\w+)?\s*$', lambda m: f'#+begin_src {m.group(1) or ""}', content, flags=re.MULTILINE)
    content = re.sub(r'^```\s*$', '#+end_src', content, flags=re.MULTILINE)
    return content

processed_count = 0
for org_file in Path('.').rglob('*.org'):
    try:
        with open(org_file, 'r', encoding='utf-8') as f:
            content = f.read()
        fixed_content = fix_org_code_blocks(content)
        if fixed_content != content:
            with open(org_file, 'w', encoding='utf-8') as f:
                f.write(fixed_content)
            print(f'Fixed: {org_file}')
            processed_count += 1
    except Exception as e:
        print(f'Error: {org_file}: {e}')

print(f'Processed {processed_count} files')
EOF

# Run the Python script
python3 fix_org.py

# Clean up
rm fix_org.py

echo "✅ Org files preprocessing completed"