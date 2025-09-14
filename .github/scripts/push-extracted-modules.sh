#!/usr/bin/env bash

# Push Extracted Modules Script for ByteHot Repository Restructuring
# Pushes all extracted modules to their respective GitHub repositories

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

# Configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
MIGRATION_DIR="$REPO_ROOT/migration"

print_color $BLUE "=== ByteHot Module Push Script ==="
print_color $YELLOW "This script will push all extracted modules to GitHub"
print_color $YELLOW "Git history is preserved from the original monorepo"
echo

# Check if migration directory exists
if [[ ! -d "$MIGRATION_DIR" ]]; then
    print_color $RED "Error: Migration directory not found at $MIGRATION_DIR"
    print_color $YELLOW "Please run the extraction scripts first"
    exit 1
fi

# Repository mappings: local_directory:organization/repository
declare -A REPO_MAPPINGS=(
    # Foundation modules
    ["java-commons"]="rydnr/java-commons"
    ["java-commons-infrastructure"]="rydnr/java-commons-infrastructure"
    
    # JavaEDA framework modules  
    ["domain"]="java-eda/domain"
    ["infrastructure"]="java-eda/infrastructure"
    ["application"]="java-eda/application"
    
    # ByteHot core modules (these override the JavaEDA ones)
    # Note: The script extracts ByteHot modules to the same directory names
    # We need to handle this mapping carefully
    
    # ByteHot plugin modules
    ["plugin-commons"]="bytehot/plugin-commons"
    ["maven-plugin"]="bytehot/maven-plugin"
    ["gradle-plugin"]="bytehot/gradle-plugin"
    ["eclipse-plugin"]="bytehot/eclipse-plugin"
    ["intellij-plugin"]="bytehot/intellij-plugin"
    ["spring-plugin"]="bytehot/spring-plugin"
    ["vscode-plugin"]="bytehot/vscode-plugin"
)

# Function to determine if directory contains ByteHot or JavaEDA modules
get_module_type() {
    local dir=$1
    local pom_file="$MIGRATION_DIR/$dir/pom.xml"
    
    if [[ -f "$pom_file" ]]; then
        if grep -q "org.acmsl.bytehot" "$pom_file"; then
            echo "bytehot"
        elif grep -q "org.acmsl.javaeda" "$pom_file"; then
            echo "javaeda"
        else
            echo "unknown"
        fi
    else
        echo "no-pom"
    fi
}

# Function to push a module to GitHub
push_module() {
    local local_dir=$1
    local github_repo=$2
    local module_path="$MIGRATION_DIR/$local_dir"
    
    print_color $YELLOW "Processing: $local_dir → github.com/$github_repo"
    
    # Check if directory exists
    if [[ ! -d "$module_path" ]]; then
        print_color $RED "  ✗ Directory not found: $module_path"
        return 1
    fi
    
    cd "$module_path"
    
    # Check if it's a git repository
    if [[ ! -d ".git" ]]; then
        print_color $RED "  ✗ Not a git repository: $module_path"
        return 1
    fi
    
    # Check if we have commits
    if ! git log --oneline -n 1 >/dev/null 2>&1; then
        print_color $RED "  ✗ No commits found in repository"
        return 1
    fi
    
    # Show current branch and last commit
    current_branch=$(git branch --show-current)
    last_commit=$(git log --oneline -n 1)
    print_color $BLUE "  Current branch: $current_branch"
    print_color $BLUE "  Last commit: $last_commit"
    
    # Add GitHub remote (remove if it already exists)
    if git remote get-url origin >/dev/null 2>&1; then
        print_color $YELLOW "  Updating existing origin remote..."
        git remote set-url origin "https://github.com/$github_repo.git"
    else
        print_color $YELLOW "  Adding GitHub remote..."
        git remote add origin "https://github.com/$github_repo.git"
    fi
    
    # Push to GitHub
    print_color $YELLOW "  Pushing to GitHub..."
    if git push -u origin "$current_branch" 2>&1; then
        print_color $GREEN "  ✓ Successfully pushed to github.com/$github_repo"
    else
        print_color $RED "  ✗ Failed to push to github.com/$github_repo"
        return 1
    fi
    
    echo
}

# Main execution
print_color $BLUE "Starting module push process..."
echo

cd "$MIGRATION_DIR"

# Get list of directories to process
available_dirs=($(find . -maxdepth 1 -type d -name "[!.]*" | sed 's|./||' | sort))

print_color $YELLOW "Available directories in migration folder:"
for dir in "${available_dirs[@]}"; do
    module_type=$(get_module_type "$dir")
    echo "  $dir ($module_type)"
done
echo

# Process known modules based on their type
success_count=0
failure_count=0

for dir in "${available_dirs[@]}"; do
    # Skip temp directories
    if [[ "$dir" == *"temp"* ]]; then
        continue
    fi
    
    module_type=$(get_module_type "$dir")
    github_repo=""
    
    # Determine GitHub repository based on module type and directory
    case "$dir" in
        "java-commons"|"java-commons-infrastructure")
            github_repo="${REPO_MAPPINGS[$dir]}"
            ;;
        "domain")
            if [[ "$module_type" == "bytehot" ]]; then
                github_repo="bytehot/domain"
            elif [[ "$module_type" == "javaeda" ]]; then
                github_repo="java-eda/domain"
            fi
            ;;
        "infrastructure")
            if [[ "$module_type" == "bytehot" ]]; then
                github_repo="bytehot/infrastructure"
            elif [[ "$module_type" == "javaeda" ]]; then
                github_repo="java-eda/infrastructure"
            fi
            ;;
        "application")
            if [[ "$module_type" == "bytehot" ]]; then
                github_repo="bytehot/application"
            elif [[ "$module_type" == "javaeda" ]]; then
                github_repo="java-eda/application"
            fi
            ;;
        "plugin-commons")
            github_repo="bytehot/plugin-commons"
            ;;
        "maven-plugin")
            github_repo="bytehot/maven-plugin"
            ;;
        "gradle-plugin")
            github_repo="bytehot/gradle-plugin"
            ;;
        "eclipse-plugin")
            github_repo="bytehot/eclipse-plugin"
            ;;
        "intellij-plugin")
            github_repo="bytehot/intellij-plugin"
            ;;
        "spring-plugin")
            github_repo="bytehot/spring-plugin"
            ;;
        "vscode-plugin")
            github_repo="bytehot/vscode-plugin"
            ;;
        *)
            print_color $YELLOW "Unknown module: $dir"
            ;;
    esac
    
    if [[ -n "$github_repo" ]]; then
        if push_module "$dir" "$github_repo"; then
            ((success_count++))
        else
            ((failure_count++))
        fi
    else
        print_color $YELLOW "Skipping $dir - no GitHub repository mapping found"
    fi
done

echo
print_color $BLUE "=== Push Summary ==="
print_color $GREEN "Successfully pushed: $success_count modules"
if [[ $failure_count -gt 0 ]]; then
    print_color $RED "Failed to push: $failure_count modules"
else
    print_color $GREEN "Failed to push: 0 modules"
fi

echo
print_color $BLUE "Next steps:"
echo "1. Verify repositories on GitHub have the correct content and history"
echo "2. Set up CI/CD pipelines: .github/scripts/setup-ci-cd.sh"
echo "3. Publish to Maven Central with release profiles"
echo "4. Update dependencies to use published artifacts"

if [[ $failure_count -eq 0 ]]; then
    print_color $GREEN "🎉 All modules successfully pushed to GitHub!"
    exit 0
else
    print_color $YELLOW "⚠️  Some modules failed to push. Check the output above for details."
    exit 1
fi