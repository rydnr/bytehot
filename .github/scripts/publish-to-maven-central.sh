#!/usr/bin/env bash

# publish-to-maven-central.sh
# Publishes all ByteHot modules to Maven Central in dependency order

set -e

echo "=== Maven Central Publishing Script ==="
echo "This script will publish all ByteHot modules to Maven Central"
echo "Prerequisites:"
echo "- Sonatype OSSRH account configured in ~/.m2/settings.xml"
echo "- GPG key setup and configured"
echo "- Internet connection for uploading artifacts"
echo ""

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
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
    echo -e "ℹ️ $1"
}

# Function to check if Maven Central publishing prerequisites are met
check_prerequisites() {
    print_info "Checking prerequisites..."
    
    # Check if ~/.m2/settings.xml exists
    if [ ! -f ~/.m2/settings.xml ]; then
        print_error "~/.m2/settings.xml not found. Please configure OSSRH credentials."
        exit 1
    fi
    
    # Check if GPG is available
    if ! command -v gpg &> /dev/null; then
        print_error "GPG not found. Please install GPG and set up signing keys."
        exit 1
    fi
    
    # Check if we have any GPG keys
    if ! gpg --list-secret-keys &> /dev/null; then
        print_error "No GPG secret keys found. Please generate and configure GPG keys."
        exit 1
    fi
    
    print_status "Prerequisites check passed"
}

# Function to publish a module
publish_module() {
    local module_name=$1
    local module_dir=$2
    
    print_info "Publishing $module_name..."
    
    if [ ! -d "$module_dir" ]; then
        print_error "Module directory $module_dir does not exist"
        return 1
    fi
    
    cd "$module_dir"
    
    # Run the Maven deploy with release profile
    if mvn clean deploy -P release -DskipTests; then
        print_status "$module_name published successfully"
        cd - > /dev/null
        return 0
    else
        print_error "Failed to publish $module_name"
        cd - > /dev/null
        return 1
    fi
}

# Function to wait for Maven Central propagation
wait_for_propagation() {
    local wait_time=${1:-60}
    print_info "Waiting ${wait_time} seconds for Maven Central propagation..."
    sleep $wait_time
}

# Main publishing function
main() {
    print_info "Starting Maven Central publishing process..."
    
    # Check prerequisites
    check_prerequisites
    
    # Confirm with user
    print_warning "This will publish all modules to Maven Central. This action cannot be undone."
    read -p "Do you want to continue? (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        print_info "Publishing cancelled by user"
        exit 0
    fi
    
    # Change to migration directory
    cd /home/chous/github/rydnr/bytehot/migration
    
    # Phase 1: Foundation modules (independent)
    print_info "=== Phase 1: Foundation Modules ==="
    
    publish_module "java-commons" "java-commons"
    wait_for_propagation 30
    
    publish_module "java-commons-infrastructure" "java-commons-infrastructure"
    wait_for_propagation 30
    
    print_status "Phase 1 completed successfully"
    
    # Phase 2: JavaEDA framework
    print_info "=== Phase 2: JavaEDA Framework ==="
    
    publish_module "javaeda-domain" "javaeda-domain"
    wait_for_propagation 30
    
    publish_module "javaeda-infrastructure" "javaeda-infrastructure"
    wait_for_propagation 30
    
    publish_module "javaeda-application" "javaeda-application"
    wait_for_propagation 30
    
    print_status "Phase 2 completed successfully"
    
    # Phase 3: ByteHot core
    print_info "=== Phase 3: ByteHot Core ==="
    
    publish_module "bytehot-domain" "domain"
    wait_for_propagation 30
    
    publish_module "bytehot-infrastructure" "infrastructure"
    wait_for_propagation 30
    
    publish_module "bytehot-application" "application"
    wait_for_propagation 30
    
    print_status "Phase 3 completed successfully"
    
    # Phase 4: ByteHot plugins
    print_info "=== Phase 4: ByteHot Plugins ==="
    
    publish_module "bytehot-plugin-commons" "plugin-commons"
    wait_for_propagation 30
    
    # All other plugins can be published in parallel since they only depend on plugin-commons
    local plugin_modules=(
        "bytehot-maven-plugin:maven-plugin"
        "bytehot-gradle-plugin:gradle-plugin"
        "bytehot-eclipse-plugin:eclipse-plugin"
        "bytehot-intellij-plugin:intellij-plugin"
        "bytehot-spring-plugin:spring-plugin"
        "bytehot-vscode-plugin:vscode-plugin"
    )
    
    for plugin_def in "${plugin_modules[@]}"; do
        IFS=':' read -r plugin_name plugin_dir <<< "$plugin_def"
        publish_module "$plugin_name" "$plugin_dir"
    done
    
    print_status "Phase 4 completed successfully"
    
    # Final verification
    print_info "=== Publishing Complete ==="
    print_status "All modules have been published to Maven Central!"
    print_info "Verification URLs:"
    print_info "- Staging: https://s01.oss.sonatype.org/#stagingRepositories"
    print_info "- Search: https://search.maven.org/search?q=g:org.acmsl.bytehot"
    print_info "- Search: https://search.maven.org/search?q=g:org.acmsl.javaeda"
    print_info "- Search: https://search.maven.org/search?q=g:org.acmsl.commons"
    
    print_info "Note: It may take 10-15 minutes for artifacts to appear in Maven Central search"
    
    cd - > /dev/null
}

# Handle script interruption
trap 'echo -e "\n${RED}Publishing interrupted by user${NC}"; exit 1' INT

# Run main function
main "$@"