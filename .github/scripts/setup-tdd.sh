#!/usr/bin/env bash

# TDD Setup Script for ByteHot
# Configures the development environment for TDD workflow

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_color() {
    local color=$1
    local message=$2
    echo -e "${color}${message}${NC}"
}

print_color $BLUE "Setting up TDD workflow for ByteHot..."

# Get the repository root
REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$REPO_ROOT"

# Install git hooks
print_color $YELLOW "Installing git hooks..."
if [[ ! -d ".git/hooks" ]]; then
    print_color $RED "Error: Not in a git repository"
    exit 1
fi

# Copy commit-msg hook
cp .github/hooks/commit-msg .git/hooks/commit-msg
chmod +x .git/hooks/commit-msg
print_color $GREEN "✓ Installed commit-msg hook for TDD validation"

# Create aliases for TDD helper
print_color $YELLOW "Creating TDD aliases..."
TDDHELPER_PATH="$REPO_ROOT/.github/scripts/tdd-helper.sh"

# Add to .bashrc if it exists
if [[ -f "$HOME/.bashrc" ]]; then
    if ! grep -q "alias tdd=" "$HOME/.bashrc"; then
        echo "" >> "$HOME/.bashrc"
        echo "# ByteHot TDD aliases" >> "$HOME/.bashrc"
        echo "alias tdd='$TDDHELPER_PATH'" >> "$HOME/.bashrc"
        echo "alias tdd-test='$TDDHELPER_PATH test'" >> "$HOME/.bashrc"
        echo "alias tdd-naive='$TDDHELPER_PATH naive'" >> "$HOME/.bashrc"
        echo "alias tdd-impl='$TDDHELPER_PATH implement'" >> "$HOME/.bashrc"
        echo "alias tdd-refactor='$TDDHELPER_PATH refactor'" >> "$HOME/.bashrc"
        echo "alias tdd-status='$TDDHELPER_PATH status'" >> "$HOME/.bashrc"
        print_color $GREEN "✓ Added TDD aliases to .bashrc"
    else
        print_color $YELLOW "TDD aliases already exist in .bashrc"
    fi
fi

# Create local git aliases
print_color $YELLOW "Creating git aliases..."
git config alias.tdd-test "!f() { $TDDHELPER_PATH test \$1; }; f"
git config alias.tdd-naive "!f() { $TDDHELPER_PATH naive \$1; }; f"
git config alias.tdd-impl "!f() { $TDDHELPER_PATH implement \$1; }; f"
git config alias.tdd-refactor "!f() { $TDDHELPER_PATH refactor \$1; }; f"
git config alias.tdd-status "!$TDDHELPER_PATH status"
print_color $GREEN "✓ Created git aliases for TDD workflow"

# Verify Maven is available
print_color $YELLOW "Checking Maven installation..."
if ! command -v mvn &> /dev/null; then
    print_color $RED "Error: Maven is not installed or not in PATH"
    print_color $YELLOW "Please install Maven to run tests"
    exit 1
fi
print_color $GREEN "✓ Maven is available"

# Run initial test to verify setup
print_color $YELLOW "Running initial test to verify setup..."
if mvn test -q; then
    print_color $GREEN "✓ Tests are passing"
else
    print_color $YELLOW "Some tests are failing - this is normal during development"
fi

print_color $BLUE "TDD setup complete!"
echo ""
print_color $GREEN "TDD Workflow Commands:"
echo "  tdd test <issue>     - Add failing test"
echo "  tdd naive <issue>    - Implement naive solution"
echo "  tdd impl <issue>     - Implement working solution"
echo "  tdd refactor <issue> - Refactor code"
echo "  tdd status           - Show TDD status"
echo ""
print_color $GREEN "Git Aliases:"
echo "  git tdd-test <issue>     - Add failing test"
echo "  git tdd-naive <issue>    - Implement naive solution"
echo "  git tdd-impl <issue>     - Implement working solution"
echo "  git tdd-refactor <issue> - Refactor code"
echo "  git tdd-status           - Show TDD status"
echo ""
print_color $YELLOW "Note: Restart your terminal or run 'source ~/.bashrc' to use shell aliases"
print_color $BLUE "Read .github/TDD_WORKFLOW.md for detailed usage instructions"