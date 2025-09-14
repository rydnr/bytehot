#!/usr/bin/env bash

# TDD Helper Script for ByteHot
# Implements the TDD workflow as documented in CLAUDE.md

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

# Function to show usage
show_usage() {
    echo "TDD Helper Script for ByteHot"
    echo ""
    echo "Usage: $0 <command> [options]"
    echo ""
    echo "Commands:"
    echo "  test <issue_number>     - Add failing test and commit with 🧪"
    echo "  naive <issue_number>    - Implement naive solution and commit with 🤔"
    echo "  implement <issue_number> - Implement working solution and commit with ✅"
    echo "  refactor <issue_number> - Refactor code and commit with 🚀"
    echo "  status                  - Show current TDD status"
    echo "  validate                - Validate TDD workflow compliance"
    echo ""
    echo "Examples:"
    echo "  $0 test 42             - Add failing test for issue #42"
    echo "  $0 naive 42            - Implement naive solution for issue #42"
    echo "  $0 implement 42        - Implement working solution for issue #42"
    echo "  $0 refactor 42         - Refactor code for issue #42"
}

# Function to run tests
run_tests() {
    print_color $BLUE "Running tests..."
    mvn test -q || return 1
}

# Function to check if tests are failing
tests_failing() {
    ! run_tests
}

# Function to check if tests are passing
tests_passing() {
    run_tests
}

# Function to commit with TDD emoji pattern
tdd_commit() {
    local emoji=$1
    local issue_number=$2
    local stage=$3
    
    local commit_message="${emoji} [#${issue_number}] ${stage}"
    
    print_color $YELLOW "Committing: ${commit_message}"
    git add -A
    git commit -m "${commit_message}"
    
    print_color $GREEN "✓ Committed: ${commit_message}"
}

# Function to add failing test
add_failing_test() {
    local issue_number=$1
    
    if [[ -z "$issue_number" ]]; then
        print_color $RED "Error: Issue number is required"
        show_usage
        exit 1
    fi
    
    print_color $BLUE "TDD Step 1: Add failing test for issue #${issue_number}"
    
    # Check if we have uncommitted changes
    if ! git diff --quiet; then
        print_color $RED "Error: You have uncommitted changes. Please commit or stash them first."
        exit 1
    fi
    
    print_color $YELLOW "Please add your failing test and press Enter when ready..."
    read -r
    
    # Verify tests are failing
    if tests_passing; then
        print_color $RED "Error: Tests are passing. Please ensure your new test is failing."
        exit 1
    fi
    
    tdd_commit "🧪" "$issue_number" "Add failing test"
    
    print_color $GREEN "✓ TDD Step 1 complete: Failing test added and committed"
    print_color $BLUE "Next: Run '$0 naive $issue_number' or '$0 implement $issue_number'"
}

# Function to implement naive solution
implement_naive() {
    local issue_number=$1
    
    if [[ -z "$issue_number" ]]; then
        print_color $RED "Error: Issue number is required"
        show_usage
        exit 1
    fi
    
    print_color $BLUE "TDD Step 2: Implement naive solution for issue #${issue_number}"
    
    # Check if we have uncommitted changes
    if ! git diff --quiet; then
        print_color $RED "Error: You have uncommitted changes. Please commit or stash them first."
        exit 1
    fi
    
    print_color $YELLOW "Please implement your naive solution and press Enter when ready..."
    read -r
    
    # Verify tests are now passing
    if tests_failing; then
        print_color $RED "Error: Tests are still failing. Please fix your implementation."
        exit 1
    fi
    
    tdd_commit "🤔" "$issue_number" "Naive implementation"
    
    print_color $GREEN "✓ TDD Step 2 complete: Naive implementation added and committed"
    print_color $BLUE "Next: Run '$0 implement $issue_number' or '$0 refactor $issue_number'"
}

# Function to implement working solution
implement_working() {
    local issue_number=$1
    
    if [[ -z "$issue_number" ]]; then
        print_color $RED "Error: Issue number is required"
        show_usage
        exit 1
    fi
    
    print_color $BLUE "TDD Step 2/3: Implement working solution for issue #${issue_number}"
    
    # Check if we have uncommitted changes
    if ! git diff --quiet; then
        print_color $RED "Error: You have uncommitted changes. Please commit or stash them first."
        exit 1
    fi
    
    print_color $YELLOW "Please implement your working solution and press Enter when ready..."
    read -r
    
    # Verify tests are passing
    if tests_failing; then
        print_color $RED "Error: Tests are failing. Please fix your implementation."
        exit 1
    fi
    
    tdd_commit "✅" "$issue_number" "Working implementation"
    
    print_color $GREEN "✓ TDD Step 2/3 complete: Working implementation added and committed"
    print_color $BLUE "Next: Run '$0 refactor $issue_number' if refactoring is needed"
}

# Function to refactor code
refactor_code() {
    local issue_number=$1
    
    if [[ -z "$issue_number" ]]; then
        print_color $RED "Error: Issue number is required"
        show_usage
        exit 1
    fi
    
    print_color $BLUE "TDD Step 3: Refactor code for issue #${issue_number}"
    
    # Check if we have uncommitted changes
    if ! git diff --quiet; then
        print_color $RED "Error: You have uncommitted changes. Please commit or stash them first."
        exit 1
    fi
    
    print_color $YELLOW "Please refactor your code and press Enter when ready..."
    read -r
    
    # Verify tests are still passing after refactoring
    if tests_failing; then
        print_color $RED "Error: Tests are failing after refactoring. Please fix the issues."
        exit 1
    fi
    
    tdd_commit "🚀" "$issue_number" "Refactor"
    
    print_color $GREEN "✓ TDD Step 3 complete: Code refactored and committed"
    print_color $BLUE "TDD cycle complete for issue #${issue_number}"
}

# Function to show TDD status
show_status() {
    print_color $BLUE "TDD Status for ByteHot"
    echo ""
    
    # Show recent TDD commits
    print_color $YELLOW "Recent TDD commits:"
    git log --oneline -10 --grep="🧪\|🤔\|✅\|🚀" || print_color $RED "No TDD commits found"
    
    echo ""
    
    # Show current test status
    print_color $YELLOW "Current test status:"
    if tests_passing; then
        print_color $GREEN "✓ All tests passing"
    else
        print_color $RED "✗ Some tests failing"
    fi
    
    echo ""
    
    # Show uncommitted changes
    if ! git diff --quiet; then
        print_color $YELLOW "Uncommitted changes detected"
        git status --porcelain
    else
        print_color $GREEN "✓ No uncommitted changes"
    fi
}

# Function to validate TDD workflow compliance
validate_workflow() {
    print_color $BLUE "Validating TDD workflow compliance..."
    
    local errors=0
    
    # Check for proper TDD commit pattern in recent commits
    local recent_commits=$(git log --oneline -20 --pretty=format:"%s")
    
    print_color $YELLOW "Checking commit message patterns..."
    
    # Look for TDD pattern violations
    while IFS= read -r commit_msg; do
        if [[ "$commit_msg" =~ ^\[#[0-9]+\] ]]; then
            if [[ ! "$commit_msg" =~ ^(🧪|🤔|✅|🚀) ]]; then
                print_color $RED "✗ Invalid TDD commit format: $commit_msg"
                ((errors++))
            fi
        fi
    done <<< "$recent_commits"
    
    # Check test structure
    print_color $YELLOW "Checking test structure..."
    local test_files=$(find . -name "*Test.java" -type f | wc -l)
    if [[ $test_files -lt 10 ]]; then
        print_color $RED "✗ Low test coverage: Only $test_files test files found"
        ((errors++))
    else
        print_color $GREEN "✓ Good test coverage: $test_files test files found"
    fi
    
    # Check for proper test naming
    local bad_test_names=$(find . -name "*Test.java" -type f -exec grep -l "public void test" {} \; | wc -l)
    if [[ $bad_test_names -gt 0 ]]; then
        print_color $RED "✗ Found $bad_test_names files with old JUnit 3 style test methods"
        ((errors++))
    fi
    
    # Summary
    echo ""
    if [[ $errors -eq 0 ]]; then
        print_color $GREEN "✓ TDD workflow compliance: PASSED"
        return 0
    else
        print_color $RED "✗ TDD workflow compliance: FAILED ($errors errors)"
        return 1
    fi
}

# Main script logic
case "${1:-}" in
    test)
        add_failing_test "$2"
        ;;
    naive)
        implement_naive "$2"
        ;;
    implement)
        implement_working "$2"
        ;;
    refactor)
        refactor_code "$2"
        ;;
    status)
        show_status
        ;;
    validate)
        validate_workflow
        ;;
    *)
        show_usage
        exit 1
        ;;
esac