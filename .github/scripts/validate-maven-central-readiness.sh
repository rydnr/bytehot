#!/usr/bin/env bash

# validate-maven-central-readiness.sh
# Validates that all modules are ready for Maven Central publishing

set -e

echo "=== Maven Central Readiness Validation ==="
echo "This script validates that all modules are ready for Maven Central publishing"
echo ""

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️ $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_info() {
    echo -e "${BLUE}ℹ️ $1${NC}"
}

# Validation results
validation_errors=0
validation_warnings=0

# Function to validate a single module
validate_module() {
    local module_name=$1
    local module_dir=$2
    
    print_info "Validating $module_name..."
    
    if [ ! -d "$module_dir" ]; then
        print_error "Module directory $module_dir does not exist"
        ((validation_errors++))
        return 1
    fi
    
    cd "$module_dir"
    
    # Check if pom.xml exists
    if [ ! -f "pom.xml" ]; then
        print_error "$module_name: pom.xml not found"
        ((validation_errors++))
        cd - > /dev/null
        return 1
    fi
    
    # Check required metadata
    local required_elements=(
        "name"
        "description"
        "url"
        "licenses"
        "developers"
        "scm"
    )
    
    for element in "${required_elements[@]}"; do
        if ! grep -q "<$element>" pom.xml; then
            print_error "$module_name: Missing required element: $element"
            ((validation_errors++))
        fi
    done
    
    # Check if version is not SNAPSHOT
    if grep -q "SNAPSHOT" pom.xml; then
        print_error "$module_name: Version should not be SNAPSHOT for Maven Central"
        ((validation_errors++))
    fi
    
    # Check if release profile exists
    if ! grep -q '<id>release</id>' pom.xml; then
        print_error "$module_name: Missing release profile"
        ((validation_errors++))
    fi
    
    # Check if GPG plugin is configured
    if ! grep -q 'maven-gpg-plugin' pom.xml; then
        print_error "$module_name: Missing GPG plugin configuration"
        ((validation_errors++))
    fi
    
    # Check if Nexus staging plugin is configured
    if ! grep -q 'nexus-staging-maven-plugin' pom.xml; then
        print_error "$module_name: Missing Nexus staging plugin configuration"
        ((validation_errors++))
    fi
    
    # Check if source plugin is configured
    if ! grep -q 'maven-source-plugin' pom.xml; then
        print_error "$module_name: Missing source plugin configuration"
        ((validation_errors++))
    fi
    
    # Check if javadoc plugin is configured
    if ! grep -q 'maven-javadoc-plugin' pom.xml; then
        print_error "$module_name: Missing Javadoc plugin configuration"
        ((validation_errors++))
    fi
    
    # Test compilation
    if ! mvn clean compile -q > /dev/null 2>&1; then
        print_error "$module_name: Compilation failed"
        ((validation_errors++))
    fi
    
    # Test release profile build
    if ! mvn clean package -P release -DskipTests -q > /dev/null 2>&1; then
        print_error "$module_name: Release profile build failed"
        ((validation_errors++))
    else
        # Check if all required artifacts are generated
        local target_dir="target"
        local version=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
        local artifact_id=$(mvn help:evaluate -Dexpression=project.artifactId -q -DforceStdout)
        
        local required_artifacts=(
            "$target_dir/$artifact_id-$version.jar"
            "$target_dir/$artifact_id-$version-sources.jar"
            "$target_dir/$artifact_id-$version-javadoc.jar"
        )
        
        for artifact in "${required_artifacts[@]}"; do
            if [ ! -f "$artifact" ]; then
                print_error "$module_name: Missing required artifact: $artifact"
                ((validation_errors++))
            fi
        done
    fi
    
    cd - > /dev/null
    
    if [ $validation_errors -eq 0 ]; then
        print_status "$module_name validation passed"
    fi
    
    return 0
}

# Function to validate prerequisites
validate_prerequisites() {
    print_info "Validating prerequisites..."
    
    # Check if ~/.m2/settings.xml exists
    if [ ! -f ~/.m2/settings.xml ]; then
        print_error "~/.m2/settings.xml not found"
        print_info "Run: .github/scripts/setup-maven-central-prerequisites.sh"
        ((validation_errors++))
    else
        # Check if OSSRH server is configured
        if ! grep -q '<id>ossrh</id>' ~/.m2/settings.xml; then
            print_error "OSSRH server not configured in settings.xml"
            ((validation_errors++))
        fi
    fi
    
    # Check if GPG is available
    if ! command -v gpg &> /dev/null; then
        print_error "GPG not found"
        ((validation_errors++))
    else
        # Check if we have any GPG keys
        if ! gpg --list-secret-keys &> /dev/null; then
            print_error "No GPG secret keys found"
            ((validation_errors++))
        fi
    fi
    
    # Check if Maven is available
    if ! command -v mvn &> /dev/null; then
        print_error "Maven not found"
        ((validation_errors++))
    fi
    
    if [ $validation_errors -eq 0 ]; then
        print_status "Prerequisites validation passed"
    fi
}

# Main validation function
main() {
    print_info "Starting Maven Central readiness validation..."
    
    # Reset counters
    validation_errors=0
    validation_warnings=0
    
    # Validate prerequisites
    validate_prerequisites
    
    # Change to migration directory
    cd /home/chous/github/rydnr/bytehot/migration
    
    # Module definitions (name:directory)
    local modules=(
        "java-commons:java-commons"
        "java-commons-infrastructure:java-commons-infrastructure"
        "javaeda-domain:javaeda-domain"
        "javaeda-infrastructure:javaeda-infrastructure"
        "javaeda-application:javaeda-application"
        "bytehot-domain:domain"
        "bytehot-infrastructure:infrastructure"
        "bytehot-application:application"
        "bytehot-plugin-commons:plugin-commons"
        "bytehot-maven-plugin:maven-plugin"
        "bytehot-gradle-plugin:gradle-plugin"
        "bytehot-eclipse-plugin:eclipse-plugin"
        "bytehot-intellij-plugin:intellij-plugin"
        "bytehot-spring-plugin:spring-plugin"
        "bytehot-vscode-plugin:vscode-plugin"
    )
    
    # Validate each module
    for module_def in "${modules[@]}"; do
        IFS=':' read -r module_name module_dir <<< "$module_def"
        validate_module "$module_name" "$module_dir"
    done
    
    cd - > /dev/null
    
    # Summary
    echo
    print_info "=== Validation Summary ==="
    if [ $validation_errors -eq 0 ]; then
        print_status "All validations passed! Ready for Maven Central publishing"
        print_info "You can now run: .github/scripts/publish-to-maven-central.sh"
    else
        print_error "Found $validation_errors validation errors"
        print_info "Please fix the errors before publishing to Maven Central"
        exit 1
    fi
    
    if [ $validation_warnings -gt 0 ]; then
        print_warning "Found $validation_warnings warnings (non-blocking)"
    fi
    
    echo
}

# Handle script interruption
trap 'echo -e "\n${RED}Validation interrupted by user${NC}"; exit 1' INT

# Run main function
main "$@"