#!/usr/bin/env bash

# Git commit message validation for TDD workflow
# This script validates that commit messages follow the TDD pattern from CLAUDE.md

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_color() {
    local color=$1
    local message=$2
    echo -e "${color}${message}${NC}" >&2
}

# Read the commit message
commit_message_file="$1"
commit_message=$(cat "$commit_message_file")

# Skip validation for merge commits
if [[ "$commit_message" =~ ^Merge ]]; then
    exit 0
fi

# Skip validation for revert commits
if [[ "$commit_message" =~ ^Revert ]]; then
    exit 0
fi

# Skip validation for automated commits
if [[ "$commit_message" =~ ^(Initial commit|Update) ]]; then
    exit 0
fi

# TDD commit pattern validation
validate_tdd_pattern() {
    local message="$1"
    
    # Check for TDD emoji patterns
    if [[ "$message" =~ ^(🧪|🤔|✅|🚀) ]]; then
        # Valid TDD commit, check for issue reference
        if [[ "$message" =~ ^[^[:space:]]+\ \[#[0-9]+\] ]]; then
            return 0
        else
            print_color $RED "TDD commit missing issue reference: $message"
            print_color $YELLOW "Expected format: <emoji> [#issue_number] <description>"
            return 1
        fi
    fi
    
    # Check for issue reference pattern without TDD emoji
    if [[ "$message" =~ ^\[#[0-9]+\] ]]; then
        print_color $RED "Issue reference found but missing TDD emoji: $message"
        print_color $YELLOW "Use one of: 🧪 🤔 ✅ 🚀"
        return 1
    fi
    
    # Allow non-TDD commits (documentation, configuration, etc.)
    return 0
}

# Validate commit message
if ! validate_tdd_pattern "$commit_message"; then
    print_color $RED "Commit message validation failed!"
    echo ""
    print_color $YELLOW "TDD Commit Format Guidelines:"
    echo "🧪 [#123] Add failing test - After test is added"
    echo "🤔 [#123] Naive implementation - When test passes trivially"
    echo "✅ [#123] Working implementation - When test passes with real logic"
    echo "🚀 [#123] Refactor - Improving code after green"
    echo ""
    print_color $YELLOW "For non-TDD commits, use standard format without issue reference"
    exit 1
fi

print_color $GREEN "✓ Commit message validation passed"
exit 0