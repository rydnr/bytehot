#!/usr/bin/env bash

# setup-maven-central-prerequisites.sh
# Interactive setup script for Maven Central publishing prerequisites

set -e

echo "=== Maven Central Prerequisites Setup ==="
echo "This script will help you set up the prerequisites for Maven Central publishing"
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

print_step() {
    echo -e "${YELLOW}🔧 $1${NC}"
}

# Function to check if a command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Function to setup GPG key
setup_gpg() {
    print_step "Setting up GPG key..."
    
    if ! command_exists gpg; then
        print_error "GPG is not installed. Please install GPG first:"
        print_info "  Ubuntu/Debian: sudo apt-get install gnupg"
        print_info "  CentOS/RHEL: sudo yum install gnupg"
        print_info "  macOS: brew install gnupg"
        return 1
    fi
    
    # Check if we already have keys
    if gpg --list-secret-keys --keyid-format LONG | grep -q "sec"; then
        print_info "GPG keys already exist:"
        gpg --list-secret-keys --keyid-format LONG
        echo
        read -p "Do you want to use existing keys? (Y/n): " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Nn]$ ]]; then
            print_info "Continuing with key generation..."
        else
            print_status "Using existing GPG keys"
            return 0
        fi
    fi
    
    print_info "Generating new GPG key..."
    print_info "Please use the following information:"
    print_info "  Real name: José San Leandro"
    print_info "  Email: rydnr@acm-sl.org"
    print_info "  Comment: (you can leave this empty)"
    print_info "  Passphrase: (choose a strong passphrase)"
    echo
    
    gpg --full-generate-key
    
    # Get the key ID
    print_info "Getting key ID..."
    KEY_ID=$(gpg --list-secret-keys --keyid-format LONG | grep "sec" | head -1 | sed 's/.*\/\([A-F0-9]*\).*/\1/')
    
    if [ -z "$KEY_ID" ]; then
        print_error "Failed to get key ID"
        return 1
    fi
    
    print_status "Key ID: $KEY_ID"
    
    # Upload to key servers
    print_info "Uploading key to key servers..."
    gpg --keyserver keyserver.ubuntu.com --send-keys "$KEY_ID" || print_warning "Failed to upload to keyserver.ubuntu.com"
    gpg --keyserver pgp.mit.edu --send-keys "$KEY_ID" || print_warning "Failed to upload to pgp.mit.edu"
    gpg --keyserver keys.openpgp.org --send-keys "$KEY_ID" || print_warning "Failed to upload to keys.openpgp.org"
    
    print_status "GPG key setup completed"
    echo "Key ID: $KEY_ID"
    echo "Please save this key ID for your records"
    echo
}

# Function to setup Maven settings
setup_maven_settings() {
    print_step "Setting up Maven settings..."
    
    local settings_file="$HOME/.m2/settings.xml"
    
    # Create .m2 directory if it doesn't exist
    mkdir -p "$HOME/.m2"
    
    # Check if settings.xml already exists
    if [ -f "$settings_file" ]; then
        print_warning "Maven settings.xml already exists"
        read -p "Do you want to backup and replace it? (y/N): " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            cp "$settings_file" "$settings_file.backup.$(date +%Y%m%d_%H%M%S)"
            print_info "Backed up existing settings.xml"
        else
            print_info "Please manually add the OSSRH server configuration to your settings.xml"
            print_info "See the Maven Central publishing guide for details"
            return 0
        fi
    fi
    
    # Get credentials
    echo
    print_info "Please enter your Sonatype OSSRH credentials:"
    print_info "If you don't have an account, create one at: https://issues.sonatype.org/"
    echo
    
    read -p "OSSRH Username: " ossrh_username
    read -s -p "OSSRH Password: " ossrh_password
    echo
    read -s -p "GPG Passphrase: " gpg_passphrase
    echo
    
    # Create settings.xml
    cat > "$settings_file" << EOF
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                              https://maven.apache.org/xsd/settings-1.0.0.xsd">
  
  <servers>
    <server>
      <id>ossrh</id>
      <username>${ossrh_username}</username>
      <password>${ossrh_password}</password>
    </server>
  </servers>
  
  <profiles>
    <profile>
      <id>ossrh</id>
      <activation>
        <activeByDefault>true</activeByDefault>
      </activation>
      <properties>
        <gpg.executable>gpg</gpg.executable>
        <gpg.passphrase>${gpg_passphrase}</gpg.passphrase>
      </properties>
    </profile>
  </profiles>
</settings>
EOF
    
    # Set secure permissions
    chmod 600 "$settings_file"
    
    print_status "Maven settings.xml created successfully"
    print_info "File location: $settings_file"
    print_warning "This file contains sensitive information. Keep it secure!"
    echo
}

# Function to display OSSRH account creation instructions
display_ossrh_instructions() {
    print_step "Sonatype OSSRH Account Setup Instructions"
    echo
    print_info "To publish to Maven Central, you need a Sonatype OSSRH account:"
    echo
    print_info "1. Create a Jira account at: https://issues.sonatype.org/"
    print_info "2. Create a 'New Project' ticket with the following details:"
    echo
    print_info "   Project: Community Support - Open Source Project Repository Hosting (OSSRH)"
    print_info "   Issue Type: New Project"
    print_info "   Summary: Request for org.acmsl.* group ID"
    print_info "   Group Id: org.acmsl"
    print_info "   Project URL: https://github.com/rydnr/bytehot"
    print_info "   SCM URL: https://github.com/rydnr/bytehot.git"
    print_info "   Username: rydnr"
    echo
    print_info "3. Wait for approval (usually takes 1-2 business days)"
    print_info "4. Once approved, you can use the credentials to configure Maven"
    echo
    print_warning "Note: You need to prove ownership of the domain or GitHub account"
    print_info "For GitHub-based group IDs, create a temporary public repository named:"
    print_info "  OSSRH-[ticket-number]"
    echo
}

# Function to test the setup
test_setup() {
    print_step "Testing Maven Central setup..."
    
    # Test GPG
    print_info "Testing GPG..."
    if echo "test" | gpg --clearsign >/dev/null 2>&1; then
        print_status "GPG signing test passed"
    else
        print_error "GPG signing test failed"
        return 1
    fi
    
    # Test Maven settings
    print_info "Testing Maven settings..."
    if [ -f "$HOME/.m2/settings.xml" ]; then
        print_status "Maven settings.xml exists"
    else
        print_error "Maven settings.xml not found"
        return 1
    fi
    
    # Test release profile
    print_info "Testing release profile build..."
    cd /home/chous/github/rydnr/bytehot/migration/java-commons
    if mvn clean package -P release -DskipTests >/dev/null 2>&1; then
        print_status "Release profile build test passed"
    else
        print_error "Release profile build test failed"
        return 1
    fi
    
    print_status "All tests passed! You're ready to publish to Maven Central"
    echo
}

# Main function
main() {
    print_info "Starting Maven Central prerequisites setup..."
    echo
    
    # Check if we're in the right directory
    if [ ! -f "pom.xml" ]; then
        print_error "This script must be run from the project root directory"
        exit 1
    fi
    
    # Display OSSRH instructions first
    display_ossrh_instructions
    
    read -p "Have you already created and got approval for your OSSRH account? (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        print_warning "Please create and get approval for your OSSRH account first"
        print_info "Run this script again after you have approval"
        exit 0
    fi
    
    # Setup GPG
    setup_gpg
    
    # Setup Maven settings
    setup_maven_settings
    
    # Test setup
    test_setup
    
    print_status "Maven Central prerequisites setup completed!"
    print_info "You can now run the publishing script:"
    print_info "  .github/scripts/publish-to-maven-central.sh"
    echo
}

# Handle script interruption
trap 'echo -e "\n${RED}Setup interrupted by user${NC}"; exit 1' INT

# Run main function
main "$@"