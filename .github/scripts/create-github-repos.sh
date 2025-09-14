#!/usr/bin/env bash

# GitHub Repository Creation Script for ByteHot Restructuring
# Creates GitHub repositories for all extracted modules

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

# Check if GitHub CLI is installed
if ! command -v gh &>/dev/null; then
    print_color $RED "Error: GitHub CLI (gh) is not installed"
    print_color $YELLOW "Install with: https://cli.github.com/"
    exit 1
fi

# Check if user is authenticated
if ! gh auth status &>/dev/null; then
    print_color $RED "Error: Not authenticated with GitHub CLI"
    print_color $YELLOW "Run: gh auth login"
    exit 1
fi

print_color $BLUE "Creating GitHub repositories for ByteHot module extraction..."

# Repository definitions based on the restructuring plan
declare -A repos=(
    # Foundation Libraries
    ["rydnr/java-commons"]="Java Commons - Foundation utilities and patterns"
    ["rydnr/java-commons-infrastructure"]="Java Commons Infrastructure - Infrastructure support for foundation utilities"

    # JavaEDA Framework
    ["java-eda/domain"]="JavaEDA Domain - Domain-driven design framework domain layer"
    ["java-eda/infrastructure"]="JavaEDA Infrastructure - Infrastructure adapters for JavaEDA framework"
    ["java-eda/application"]="JavaEDA Application - Application layer for JavaEDA framework"

    # ByteHot Core
    ["bytehot/domain"]="ByteHot Domain - Core business logic for JVM bytecode hot-swapping"
    ["bytehot/infrastructure"]="ByteHot Infrastructure - Infrastructure adapters for ByteHot"
    ["bytehot/application"]="ByteHot Application - Application layer and JVM agent for ByteHot"

    # ByteHot Plugins
    ["bytehot/plugin-commons"]="ByteHot Plugin Commons - Shared utilities for ByteHot plugins"
    ["bytehot/spring-plugin"]="ByteHot Spring Plugin - Spring Framework integration for ByteHot"
    ["bytehot/maven-plugin"]="ByteHot Maven Plugin - Maven build integration for ByteHot"
    ["bytehot/gradle-plugin"]="ByteHot Gradle Plugin - Gradle build integration for ByteHot"
    ["bytehot/intellij-plugin"]="ByteHot IntelliJ Plugin - IntelliJ IDEA integration for ByteHot"
    ["bytehot/eclipse-plugin"]="ByteHot Eclipse Plugin - Eclipse IDE integration for ByteHot"
    ["bytehot/vscode-plugin"]="ByteHot VSCode Plugin - Visual Studio Code extension for ByteHot"
)

# Function to create or verify organization exists
create_organization() {
    local org_name=$1

    print_color $YELLOW "Checking organization: $org_name"

    # Check if organization exists (this will fail if it doesn't exist or user doesn't have access)
    if gh api "orgs/$org_name" &>/dev/null; then
        print_color $GREEN "✓ Organization $org_name exists and accessible"
    else
        print_color $RED "✗ Organization $org_name does not exist or is not accessible"
        print_color $YELLOW "Please create the organization manually at: https://github.com/organizations/new"
        print_color $YELLOW "Organization name: $org_name"
        return 1
    fi
}

# Function to create a repository
create_repository() {
    local repo_path=$1
    local description=$2
    local org_name=${repo_path%/*}
    local repo_name=${repo_path#*/}

    print_color $YELLOW "Creating repository: $repo_path"

    # Check if repository already exists
    if gh repo view "$repo_path" &>/dev/null; then
        print_color $YELLOW "Repository $repo_path already exists, skipping..."
        return 0
    fi

    # Create the repository
    if [[ "$org_name" == "rydnr" ]]; then
        # Personal repository
        gh repo create "$repo_path" \
            --description "$description" \
            --public \
            --add-readme=false \
            --gitignore=Maven \
            --license=gpl-3.0
    else
        # Organization repository
        gh repo create "$repo_path" \
            --description "$description" \
            --public \
            --add-readme=false \
            --gitignore=Maven \
            --license=gpl-3.0
    fi

    if [[ $? -eq 0 ]]; then
        print_color $GREEN "✓ Created repository: $repo_path"

        # Configure repository settings
        print_color $YELLOW "Configuring repository settings..."

        # Enable issues and projects
        gh repo edit "$repo_path" \
            --enable-issues \
            --enable-projects \
            --enable-wiki

        # Set up branch protection (will be configured later when main branch exists)
        print_color $YELLOW "Note: Branch protection will be configured after first push"

    else
        print_color $RED "✗ Failed to create repository: $repo_path"
        return 1
    fi
}

# Main execution
print_color $BLUE "Starting GitHub repository creation process..."

# Track organizations that need to be created
declare -a orgs_needed=()

# Extract unique organizations
for repo_path in "${!repos[@]}"; do
    org_name=${repo_path%/*}
    if [[ ! " ${orgs_needed[@]} " =~ " ${org_name} " ]]; then
        orgs_needed+=("$org_name")
    fi
done

# Check/create organizations first
print_color $BLUE "Checking required organizations..."
for org in "${orgs_needed[@]}"; do
    if [[ "$org" != "rydnr" ]]; then # Skip personal account
        if ! create_organization "$org"; then
            print_color $RED "Please create organization $org before continuing"
            print_color $YELLOW "Visit: https://github.com/organizations/new"
            exit 1
        fi
    fi
done

# Create repositories
print_color $BLUE "Creating repositories..."
failed_repos=()

for repo_path in "${!repos[@]}"; do
    description="${repos[$repo_path]}"
    if ! create_repository "$repo_path" "$description"; then
        failed_repos+=("$repo_path")
    fi
done

# Summary
print_color $BLUE "Repository creation summary:"
echo ""

if [[ ${#failed_repos[@]} -eq 0 ]]; then
    print_color $GREEN "✓ All repositories created successfully!"

    echo ""
    print_color $BLUE "Created repositories:"
    for repo_path in "${!repos[@]}"; do
        echo "  https://github.com/$repo_path"
    done

    echo ""
    print_color $YELLOW "Next steps:"
    echo "1. Extract modules using: .github/scripts/extract-module.sh"
    echo "2. Push extracted code to respective repositories"
    echo "3. Configure branch protection rules"
    echo "4. Set up CI/CD workflows"
    echo "5. Publish initial releases to Maven Central"

else
    print_color $RED "✗ Some repositories failed to create:"
    for repo in "${failed_repos[@]}"; do
        echo "  $repo"
    done

    print_color $YELLOW "Please resolve the issues and run the script again"
    exit 1
fi

print_color $GREEN "GitHub repository creation completed!"
