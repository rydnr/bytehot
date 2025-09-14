#!/usr/bin/env bash

# fix-gpg-signing.sh
# Fixes GPG signing issues for Maven Central publishing

set -e

echo "=== GPG Signing Fix Script ==="
echo "This script will help fix GPG signing issues for Maven Central publishing"
echo ""

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

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

# Function to get GPG key ID
get_gpg_key_id() {
    local key_id=$(gpg --list-secret-keys --keyid-format LONG | grep "sec" | head -1 | sed 's/.*\/\([A-F0-9]*\).*/\1/')
    echo "$key_id"
}

# Function to kill existing GPG agent
kill_gpg_agent() {
    print_step "Killing existing GPG agent..."
    gpg-connect-agent killagent /bye 2>/dev/null || true
    killall gpg-agent 2>/dev/null || true
    print_status "GPG agent killed"
}

# Function to configure GPG agent
configure_gpg_agent() {
    print_step "Configuring GPG agent..."
    
    # Create GPG agent config directory
    mkdir -p ~/.gnupg
    chmod 700 ~/.gnupg
    
    # Configure GPG agent
    cat > ~/.gnupg/gpg-agent.conf << 'EOF'
default-cache-ttl 28800
max-cache-ttl 86400
allow-loopback-pinentry
EOF
    
    # Configure GPG to use agent
    cat > ~/.gnupg/gpg.conf << 'EOF'
use-agent
pinentry-mode loopback
EOF
    
    print_status "GPG agent configured"
}

# Function to start GPG agent
start_gpg_agent() {
    print_step "Starting GPG agent..."
    
    # Start GPG agent
    gpg-agent --daemon --default-cache-ttl 28800 --max-cache-ttl 86400 --allow-loopback-pinentry
    
    print_status "GPG agent started"
}

# Function to test GPG signing with passphrase
test_gpg_signing() {
    print_step "Testing GPG signing..."
    
    local key_id=$(get_gpg_key_id)
    if [ -z "$key_id" ]; then
        print_error "No GPG key found. Please generate a GPG key first."
        return 1
    fi
    
    print_info "Using GPG key: $key_id"
    
    # Test signing with passphrase prompt
    echo "test signing" | gpg --batch --yes --passphrase-fd 0 --clearsign
    
    if [ $? -eq 0 ]; then
        print_status "GPG signing test passed"
        return 0
    else
        print_error "GPG signing test failed"
        return 1
    fi
}

# Function to configure Maven settings for GPG
configure_maven_gpg() {
    print_step "Configuring Maven settings for GPG..."
    
    local settings_file="$HOME/.m2/settings.xml"
    
    # Check if settings.xml exists
    if [ ! -f "$settings_file" ]; then
        print_error "Maven settings.xml not found at $settings_file"
        print_info "Please run the setup script first: .github/scripts/setup-maven-central-prerequisites.sh"
        return 1
    fi
    
    # Read GPG passphrase
    read -s -p "Enter your GPG passphrase: " gpg_passphrase
    echo
    
    # Update settings.xml with GPG configuration
    # First, create a backup
    cp "$settings_file" "$settings_file.backup.$(date +%Y%m%d_%H%M%S)"
    
    # Update or add GPG configuration
    if grep -q "<gpg.passphrase>" "$settings_file"; then
        # Update existing passphrase
        sed -i "s|<gpg.passphrase>.*</gpg.passphrase>|<gpg.passphrase>$gpg_passphrase</gpg.passphrase>|" "$settings_file"
    else
        # Add GPG configuration to the profile
        sed -i '/<properties>/a\        <gpg.executable>gpg</gpg.executable>\n        <gpg.passphrase>'"$gpg_passphrase"'</gpg.passphrase>' "$settings_file"
    fi
    
    print_status "Maven settings updated with GPG configuration"
}

# Function to test Maven GPG signing
test_maven_gpg() {
    print_step "Testing Maven GPG signing..."
    
    cd /home/chous/github/rydnr/bytehot/migration/java-commons
    
    # Test Maven GPG signing
    if mvn clean compile -P release -Dgpg.passphrase="$(grep -oP '<gpg.passphrase>\K[^<]*' ~/.m2/settings.xml)" -DskipTests; then
        print_status "Maven GPG signing test passed"
        return 0
    else
        print_error "Maven GPG signing test failed"
        return 1
    fi
}

# Main function
main() {
    print_info "Starting GPG signing fix process..."
    
    # Step 1: Kill existing GPG agent
    kill_gpg_agent
    
    # Step 2: Configure GPG agent
    configure_gpg_agent
    
    # Step 3: Start GPG agent
    start_gpg_agent
    
    # Step 4: Configure Maven settings
    configure_maven_gpg
    
    # Step 5: Test Maven GPG signing
    test_maven_gpg
    
    print_status "GPG signing fix completed!"
    print_info "You can now run the publishing script:"
    print_info "  .github/scripts/publish-to-maven-central.sh"
    echo
}

# Handle script interruption
trap 'echo -e "\n${RED}GPG fix interrupted by user${NC}"; exit 1' INT

# Run main function
main "$@"