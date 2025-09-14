#!/usr/bin/env bash

# Complete Migration Script for ByteHot Repository Restructuring
# Orchestrates the entire migration process following the 6-phase plan

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
NC='\033[0m' # No Color

# Function to print colored output
print_color() {
    local color=$1
    local message=$2
    echo -e "${color}${message}${NC}"
}

# Configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

# Migration phases configuration
declare -A phase1_modules=(
    ["java-commons"]="rydnr java-commons org.acmsl.commons java-commons"
    ["java-commons-infrastructure"]="rydnr java-commons-infrastructure org.acmsl.commons java-commons-infrastructure"
)

declare -A phase2_modules=(
    ["javaeda-domain"]="java-eda domain org.acmsl.javaeda javaeda-domain"
    ["javaeda-infrastructure"]="java-eda infrastructure org.acmsl.javaeda javaeda-infrastructure"
    ["javaeda-application"]="java-eda application org.acmsl.javaeda javaeda-application"
)

declare -A phase3_modules=(
    ["bytehot-domain"]="bytehot domain org.acmsl.bytehot bytehot-domain"
    ["bytehot-infrastructure"]="bytehot infrastructure org.acmsl.bytehot bytehot-infrastructure"
    ["bytehot-application"]="bytehot application org.acmsl.bytehot bytehot-application"
)

declare -A phase4_modules=(
    ["bytehot-plugin-commons"]="bytehot plugin-commons org.acmsl.bytehot.plugins plugin-commons"
    ["bytehot-spring-plugin"]="bytehot spring-plugin org.acmsl.bytehot.plugins spring-plugin"
    ["bytehot-maven-plugin"]="bytehot maven-plugin org.acmsl.bytehot.plugins maven-plugin"
    ["bytehot-gradle-plugin"]="bytehot gradle-plugin org.acmsl.bytehot.plugins gradle-plugin"
    ["bytehot-intellij-plugin"]="bytehot intellij-plugin org.acmsl.bytehot.plugins intellij-plugin"
    ["bytehot-eclipse-plugin"]="bytehot eclipse-plugin org.acmsl.bytehot.plugins eclipse-plugin"
    ["bytehot-vscode-extension"]="bytehot vscode-plugin org.acmsl.bytehot.plugins vscode-plugin"
)

# Function to show usage
usage() {
    echo "Usage: $0 [phase|all|dry-run]"
    echo ""
    echo "Commands:"
    echo "  phase1     - Extract foundation libraries (java-commons)"
    echo "  phase2     - Extract JavaEDA framework modules"
    echo "  phase3     - Extract ByteHot core modules"
    echo "  phase4     - Extract ByteHot plugin modules"
    echo "  all        - Run all phases sequentially"
    echo "  dry-run    - Show what would be done without executing"
    echo "  status     - Show current migration status"
    echo ""
    echo "Examples:"
    echo "  $0 phase1              # Extract foundation libraries only"
    echo "  $0 all                 # Complete migration"
    echo "  $0 dry-run             # Preview all operations"
    exit 1
}

# Function to check prerequisites
check_prerequisites() {
    print_color $BLUE "Checking prerequisites..."

    # Check git filter-repo
    if ! command -v git-filter-repo &>/dev/null; then
        print_color $RED "Error: git-filter-repo is required but not installed"
        print_color $YELLOW "Install with: pip install git-filter-repo"
        exit 1
    fi

    # Check GitHub CLI
    if ! command -v gh &>/dev/null; then
        print_color $RED "Error: GitHub CLI (gh) is required but not installed"
        print_color $YELLOW "Install from: https://cli.github.com/"
        exit 1
    fi

    # Check GitHub authentication
    if ! gh auth status &>/dev/null; then
        print_color $RED "Error: Not authenticated with GitHub CLI"
        print_color $YELLOW "Run: gh auth login"
        exit 1
    fi

    # Check Maven
    if ! command -v mvn &>/dev/null; then
        print_color $RED "Error: Maven is required but not installed"
        exit 1
    fi

    print_color $GREEN "✓ All prerequisites satisfied"
}

# Function to extract modules for a phase
extract_phase_modules() {
    local phase_name=$1
    local -n modules_ref=$2

    print_color $PURPLE "=== $phase_name ==="

    for module in "${!modules_ref[@]}"; do
        local params=(${modules_ref[$module]})
        local org="${params[0]}"
        local repo="${params[1]}"
        local group_id="${params[2]}"
        local artifact_id="${params[3]}"

        print_color $BLUE "Extracting module: $module → github.com/$org/$repo"

        if [[ "$DRY_RUN" == "true" ]]; then
            print_color $YELLOW "DRY RUN: Would extract $module to $org/$repo ($group_id:$artifact_id)"
            continue
        fi

        # Extract the module
        if ! "$SCRIPT_DIR/extract-module.sh" "$module" "$org" "$repo" "$group_id" "$artifact_id"; then
            print_color $RED "Failed to extract module: $module"
            return 1
        fi

        print_color $GREEN "✓ Extracted $module successfully"
    done

    print_color $GREEN "✓ $phase_name completed successfully"
}

# Function to test extracted modules
test_extracted_modules() {
    local phase_name=$1
    local -n modules_ref=$2

    print_color $BLUE "Testing extracted modules for $phase_name..."

    local migration_dir="$REPO_ROOT/migration"

    for module in "${!modules_ref[@]}"; do
        local params=(${modules_ref[$module]})
        local repo="${params[1]}"
        local extracted_dir="$migration_dir/$repo"

        if [[ -d "$extracted_dir" ]]; then
            print_color $YELLOW "Testing build for $repo..."
            cd "$extracted_dir"

            if mvn clean compile test -q; then
                print_color $GREEN "✓ $repo builds and tests successfully"
            else
                print_color $RED "✗ $repo build/test failed"
                return 1
            fi
        fi
    done

    cd "$REPO_ROOT"
    print_color $GREEN "✓ All extracted modules tested successfully"
}

# Function to show migration status
show_status() {
    print_color $BLUE "Migration Status Report"
    print_color $BLUE "======================"

    local migration_dir="$REPO_ROOT/migration"

    if [[ ! -d "$migration_dir" ]]; then
        print_color $YELLOW "No migration directory found - migration not started"
        return
    fi

    # Check Phase 1 - Foundation Libraries
    print_color $PURPLE "Phase 1: Foundation Libraries"
    for module in "${!phase1_modules[@]}"; do
        local params=(${phase1_modules[$module]})
        local repo="${params[1]}"
        local extracted_dir="$migration_dir/$repo"

        if [[ -d "$extracted_dir" ]]; then
            print_color $GREEN "  ✓ $module → $repo (extracted)"
        else
            print_color $YELLOW "  ○ $module → $repo (pending)"
        fi
    done

    # Check Phase 2 - JavaEDA Framework
    print_color $PURPLE "Phase 2: JavaEDA Framework"
    for module in "${!phase2_modules[@]}"; do
        local params=(${phase2_modules[$module]})
        local repo="${params[1]}"
        local extracted_dir="$migration_dir/$repo"

        if [[ -d "$extracted_dir" ]]; then
            print_color $GREEN "  ✓ $module → $repo (extracted)"
        else
            print_color $YELLOW "  ○ $module → $repo (pending)"
        fi
    done

    # Check Phase 3 - ByteHot Core
    print_color $PURPLE "Phase 3: ByteHot Core"
    for module in "${!phase3_modules[@]}"; do
        local params=(${phase3_modules[$module]})
        local repo="${params[1]}"
        local extracted_dir="$migration_dir/$repo"

        if [[ -d "$extracted_dir" ]]; then
            print_color $GREEN "  ✓ $module → $repo (extracted)"
        else
            print_color $YELLOW "  ○ $module → $repo (pending)"
        fi
    done

    # Check Phase 4 - ByteHot Plugins
    print_color $PURPLE "Phase 4: ByteHot Plugins"
    for module in "${!phase4_modules[@]}"; do
        local params=(${phase4_modules[$module]})
        local repo="${params[1]}"
        local extracted_dir="$migration_dir/$repo"

        if [[ -d "$extracted_dir" ]]; then
            print_color $GREEN "  ✓ $module → $repo (extracted)"
        else
            print_color $YELLOW "  ○ $module → $repo (pending)"
        fi
    done

    # Summary
    local total_modules=0
    local extracted_modules=0

    for phase in phase1_modules phase2_modules phase3_modules phase4_modules; do
        local -n phase_ref=$phase
        for module in "${!phase_ref[@]}"; do
            total_modules=$((total_modules + 1))
            local params=(${phase_ref[$module]})
            local repo="${params[1]}"
            local extracted_dir="$migration_dir/$repo"

            if [[ -d "$extracted_dir" ]]; then
                extracted_modules=$((extracted_modules + 1))
            fi
        done
    done

    echo ""
    print_color $BLUE "Summary: $extracted_modules/$total_modules modules extracted"

    local progress=$((extracted_modules * 100 / total_modules))
    print_color $BLUE "Progress: $progress%"
}

# Main execution
DRY_RUN=false

case "${1:-}" in
"phase1")
    check_prerequisites
    extract_phase_modules "Phase 1: Foundation Libraries" phase1_modules
    test_extracted_modules "Phase 1" phase1_modules
    ;;
"phase2")
    check_prerequisites
    extract_phase_modules "Phase 2: JavaEDA Framework" phase2_modules
    test_extracted_modules "Phase 2" phase2_modules
    ;;
"phase3")
    check_prerequisites
    extract_phase_modules "Phase 3: ByteHot Core" phase3_modules
    test_extracted_modules "Phase 3" phase3_modules
    ;;
"phase4")
    check_prerequisites
    extract_phase_modules "Phase 4: ByteHot Plugins" phase4_modules
    test_extracted_modules "Phase 4" phase4_modules
    ;;
"all")
    check_prerequisites
    print_color $BLUE "Starting complete migration process..."

    extract_phase_modules "Phase 1: Foundation Libraries" phase1_modules
    test_extracted_modules "Phase 1" phase1_modules

    extract_phase_modules "Phase 2: JavaEDA Framework" phase2_modules
    test_extracted_modules "Phase 2" phase2_modules

    extract_phase_modules "Phase 3: ByteHot Core" phase3_modules
    test_extracted_modules "Phase 3" phase3_modules

    extract_phase_modules "Phase 4: ByteHot Plugins" phase4_modules
    test_extracted_modules "Phase 4" phase4_modules

    print_color $GREEN "🎉 Complete migration finished successfully!"
    print_color $BLUE "All modules extracted to: $REPO_ROOT/migration/"
    ;;
"dry-run")
    DRY_RUN=true
    print_color $BLUE "DRY RUN MODE - No actual changes will be made"
    print_color $BLUE "============================================="

    extract_phase_modules "Phase 1: Foundation Libraries" phase1_modules
    extract_phase_modules "Phase 2: JavaEDA Framework" phase2_modules
    extract_phase_modules "Phase 3: ByteHot Core" phase3_modules
    extract_phase_modules "Phase 4: ByteHot Plugins" phase4_modules

    print_color $BLUE "DRY RUN completed - no changes made"
    ;;
"status")
    show_status
    ;;
*)
    usage
    ;;
esac
