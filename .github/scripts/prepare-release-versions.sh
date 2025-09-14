#!/usr/bin/env bash

# Prepare Release Versions Script for Maven Central Publishing
# Updates all modules from SNAPSHOT to release versions

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

print_color $BLUE "=== ByteHot Release Version Preparation ==="
print_color $YELLOW "Updating all modules from SNAPSHOT to release versions for Maven Central"
echo

# Check if migration directory exists
if [[ ! -d "$MIGRATION_DIR" ]]; then
    print_color $RED "Error: Migration directory not found at $MIGRATION_DIR"
    exit 1
fi

cd "$MIGRATION_DIR"

# Get list of all module directories
modules=($(find . -maxdepth 1 -type d -name "[!.]*" | sed 's|./||' | grep -v "bytehot-temp" | sort))

print_color $YELLOW "Found ${#modules[@]} modules to update:"
for module in "${modules[@]}"; do
    echo "  $module"
done
echo

# Function to update version in pom.xml
update_pom_version() {
    local module_dir=$1
    local pom_file="$module_dir/pom.xml"
    
    if [[ -f "$pom_file" ]]; then
        print_color $YELLOW "  Updating version in $pom_file"
        
        # Update main project version
        sed -i 's|<version>1\.0\.0-SNAPSHOT</version>|<version>1.0.0</version>|g' "$pom_file"
        
        # Update dependency versions for our modules
        sed -i 's|<version>1\.0\.0-SNAPSHOT</version><!-- ByteHot/JavaEDA dependency -->|<version>1.0.0</version>|g' "$pom_file"
        sed -i 's|<version>latest-SNAPSHOT</version>|<version>1.0.0</version>|g' "$pom_file"
        
        # Update specific dependency references
        sed -i '/org\.acmsl\.commons/,/<\/dependency>/ s|<version>1\.0\.0-SNAPSHOT</version>|<version>1.0.0</version>|g' "$pom_file"
        sed -i '/org\.acmsl\.javaeda/,/<\/dependency>/ s|<version>1\.0\.0-SNAPSHOT</version>|<version>1.0.0</version>|g' "$pom_file"
        sed -i '/org\.acmsl\.bytehot/,/<\/dependency>/ s|<version>1\.0\.0-SNAPSHOT</version>|<version>1.0.0</version>|g' "$pom_file"
        
        # Verify the changes
        if grep -q "1.0.0-SNAPSHOT" "$pom_file"; then
            print_color $YELLOW "  Warning: Some SNAPSHOT versions may remain in $pom_file"
            grep -n "1.0.0-SNAPSHOT" "$pom_file" || true
        else
            print_color $GREEN "  ✓ All versions updated successfully"
        fi
    else
        print_color $YELLOW "  No pom.xml found, skipping"
    fi
}

# Process each module
success_count=0
warning_count=0

for module in "${modules[@]}"; do
    print_color $BLUE "Processing module: $module"
    
    module_path="$MIGRATION_DIR/$module"
    
    if [[ -d "$module_path" ]]; then
        cd "$module_path"
        
        # Check if it's a git repository
        if [[ -d ".git" ]]; then
            # Update pom.xml version
            update_pom_version "."
            
            # Check for changes
            if git diff --quiet; then
                print_color $YELLOW "  No changes needed"
            else
                print_color $GREEN "  Changes detected, committing..."
                git add pom.xml
                git commit -m "🔖 Prepare v1.0.0 release - Update version from SNAPSHOT to release"
                ((success_count++))
            fi
        else
            print_color $RED "  ✗ Not a git repository: $module_path"
            ((warning_count++))
        fi
        
        cd "$MIGRATION_DIR"
    else
        print_color $RED "  ✗ Directory not found: $module_path"
        ((warning_count++))
    fi
    
    echo
done

echo
print_color $BLUE "=== Release Version Update Summary ==="
print_color $GREEN "Successfully updated: $success_count modules"
if [[ $warning_count -gt 0 ]]; then
    print_color $YELLOW "Warnings/skipped: $warning_count modules"
fi

echo
print_color $BLUE "Next steps for Maven Central publishing:"
echo "1. Push the updated versions to GitHub:"
echo "   - git push origin main (for each repository)"
echo ""
echo "2. Set up Sonatype OSSRH credentials:"
echo "   - Create account at https://issues.sonatype.org/"
echo "   - Configure ~/.m2/settings.xml with ossrh credentials"
echo "   - Set up GPG signing key"
echo ""
echo "3. Publish in dependency order:"
echo "   - java-commons (foundation)"
echo "   - java-commons-infrastructure"
echo "   - javaeda-* modules"
echo "   - bytehot-* modules"
echo ""
echo "4. Use release profile: mvn clean deploy -P release"

if [[ $warning_count -eq 0 ]]; then
    print_color $GREEN "🎉 All modules ready for Maven Central publishing!"
else
    print_color $YELLOW "⚠️  Some modules had issues. Please review the output above."
fi