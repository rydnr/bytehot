#!/bin/bash
# Install ByteHot Git Hooks

set -euo pipefail

echo "🔧 Installing ByteHot Git Hooks"
echo "==============================="

# Ensure .git/hooks directory exists
mkdir -p .git/hooks

# Install pre-commit hook
echo "📋 Installing pre-commit hook for javadoc validation..."

if [[ -f ".github/hooks/pre-commit" ]]; then
    cp ".github/hooks/pre-commit" ".git/hooks/pre-commit"
    chmod +x ".git/hooks/pre-commit"
    echo "✅ Pre-commit hook installed successfully"
else
    echo "❌ Pre-commit hook source not found at .github/hooks/pre-commit"
    exit 1
fi

echo ""
echo "🎉 Git hooks installation complete!"
echo ""
echo "📝 The pre-commit hook will now:"
echo "   - Validate javadoc before each commit"
echo "   - Prevent commits that would fail GitHub Actions"
echo "   - Help maintain documentation quality"
echo ""
echo "💡 To bypass the hook (not recommended):"
echo "   git commit --no-verify"
echo ""
echo "🔧 To reinstall hooks later:"
echo "   bash .github/scripts/install-hooks.sh"