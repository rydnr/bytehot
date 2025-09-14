#!/usr/bin/env bash

# CI/CD Setup Script for Extracted Repositories
# Sets up GitHub Actions workflows for all extracted repositories

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

# Function to create CI workflow for a repository
create_ci_workflow() {
    local repo_dir=$1
    local group_id=$2
    local artifact_id=$3
    
    local workflows_dir="$repo_dir/.github/workflows"
    mkdir -p "$workflows_dir"
    
    # Create CI workflow
    cat > "$workflows_dir/ci.yml" << 'EOF'
name: Continuous Integration

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  test:
    runs-on: ubuntu-latest
    
    strategy:
      matrix:
        java-version: [17, 21]
        
    steps:
      - name: Checkout code
        uses: actions/checkout@v4
        
      - name: Set up JDK ${{ matrix.java-version }}
        uses: actions/setup-java@v3
        with:
          java-version: ${{ matrix.java-version }}
          distribution: 'temurin'
          cache: maven
          
      - name: Run tests
        run: mvn clean test
        
      - name: Run integration tests
        run: mvn clean verify
        
      - name: Upload test reports
        if: always()
        uses: actions/upload-artifact@v3
        with:
          name: test-reports-java-${{ matrix.java-version }}
          path: target/surefire-reports/
          
  code-quality:
    runs-on: ubuntu-latest
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v4
        
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: maven
          
      - name: Run code quality checks
        run: |
          mvn clean compile
          mvn spotbugs:check || true
          mvn checkstyle:check || true
          
      - name: Generate test coverage
        run: mvn clean test jacoco:report
        
      - name: Upload coverage reports
        uses: actions/upload-artifact@v3
        with:
          name: coverage-reports
          path: target/site/jacoco/
EOF

    # Create publish workflow
    cat > "$workflows_dir/publish.yml" << EOF
name: Publish to Maven Central

on:
  release:
    types: [created]
  workflow_dispatch:
    inputs:
      version:
        description: 'Version to release'
        required: true
        default: '1.0.0'

env:
  MAVEN_GROUP_ID: $group_id
  MAVEN_ARTIFACT_ID: $artifact_id

jobs:
  publish:
    runs-on: ubuntu-latest
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v4
        
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: maven
          
      - name: Configure Maven settings
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
          server-id: ossrh
          server-username: MAVEN_USERNAME
          server-password: MAVEN_PASSWORD
          gpg-private-key: \${{ secrets.MAVEN_GPG_PRIVATE_KEY }}
          gpg-passphrase: MAVEN_GPG_PASSPHRASE
          
      - name: Import GPG key
        run: |
          echo "\${{ secrets.MAVEN_GPG_PRIVATE_KEY }}" | gpg --batch --import
          gpg --list-secret-keys --keyid-format LONG
          
      - name: Update version for release
        if: github.event_name == 'workflow_dispatch'
        run: |
          mvn versions:set -DnewVersion=\${{ github.event.inputs.version }}
          mvn versions:commit
          
      - name: Run tests
        run: mvn clean test
        
      - name: Verify build
        run: mvn clean verify
        
      - name: Publish to Maven Central
        run: mvn clean deploy -P release --no-transfer-progress
        env:
          MAVEN_USERNAME: \${{ secrets.OSSRH_USERNAME }}
          MAVEN_PASSWORD: \${{ secrets.OSSRH_TOKEN }}
          MAVEN_GPG_PASSPHRASE: \${{ secrets.MAVEN_GPG_PASSPHRASE }}
          
      - name: Create GitHub release
        if: github.event_name == 'workflow_dispatch'
        uses: actions/create-release@v1
        env:
          GITHUB_TOKEN: \${{ secrets.GITHUB_TOKEN }}
        with:
          tag_name: v\${{ github.event.inputs.version }}
          release_name: Release v\${{ github.event.inputs.version }}
          body: |
            Release v\${{ github.event.inputs.version }}
            
            Published to Maven Central:
            \`\`\`xml
            <dependency>
              <groupId>$group_id</groupId>
              <artifactId>$artifact_id</artifactId>
              <version>\${{ github.event.inputs.version }}</version>
            </dependency>
            \`\`\`
            
            ## Changes
            See [CHANGELOG.md](CHANGELOG.md) for detailed changes.
          draft: false
          prerelease: false
EOF

    # Create documentation workflow
    cat > "$workflows_dir/docs.yml" << 'EOF'
name: Documentation

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  docs:
    runs-on: ubuntu-latest
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v4
        
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: maven
          
      - name: Generate Javadocs
        run: mvn clean javadoc:javadoc
        
      - name: Deploy to GitHub Pages
        if: github.ref == 'refs/heads/main'
        uses: peaceiris/actions-gh-pages@v3
        with:
          github_token: ${{ secrets.GITHUB_TOKEN }}
          publish_dir: target/site/apidocs
          destination_dir: javadoc
EOF

    print_color $GREEN "✓ Created CI/CD workflows for $(basename "$repo_dir")"
}

# Function to set up repository secrets documentation
create_secrets_documentation() {
    local repo_dir=$1
    
    cat > "$repo_dir/.github/SECRETS.md" << 'EOF'
# Required GitHub Secrets

This repository requires the following secrets to be configured for Maven Central publishing:

## Sonatype OSSRH Credentials
- `OSSRH_USERNAME` - Your Sonatype OSSRH username
- `OSSRH_TOKEN` - Your Sonatype OSSRH token (not password)

## GPG Signing
- `MAVEN_GPG_PRIVATE_KEY` - Your GPG private key (ASCII armored)
- `MAVEN_GPG_PASSPHRASE` - Passphrase for your GPG key

## Setup Instructions

### 1. Sonatype OSSRH Account
1. Create account at https://issues.sonatype.org/
2. Create Jira ticket for group ID verification
3. Generate authentication token

### 2. GPG Key Setup
```bash
# Generate GPG key
gpg --full-generate-key

# Export private key (ASCII armored)
gpg --armor --export-secret-keys YOUR_KEY_ID

# Export public key to keyservers
gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID
gpg --keyserver keys.openpgp.org --send-keys YOUR_KEY_ID
```

### 3. Configure GitHub Secrets
1. Go to repository Settings → Secrets and variables → Actions
2. Add the four secrets listed above
3. Test with a manual workflow run

### 4. Verify Publishing
1. Create a test release
2. Check workflow execution
3. Verify artifact appears in Sonatype staging
4. Confirm auto-release to Maven Central
EOF

    print_color $GREEN "✓ Created secrets documentation for $(basename "$repo_dir")"
}

# Repository configurations
declare -A repo_configs=(
    ["java-commons"]="org.acmsl.commons java-commons"
    ["java-commons-infrastructure"]="org.acmsl.commons java-commons-infrastructure"
    ["domain"]="org.acmsl.javaeda javaeda-domain"
    ["infrastructure"]="org.acmsl.javaeda javaeda-infrastructure"
    ["application"]="org.acmsl.javaeda javaeda-application"
    ["domain"]="org.acmsl.bytehot bytehot-domain"
    ["infrastructure"]="org.acmsl.bytehot bytehot-infrastructure"
    ["application"]="org.acmsl.bytehot bytehot-application"
    ["plugin-commons"]="org.acmsl.bytehot.plugins plugin-commons"
    ["spring-plugin"]="org.acmsl.bytehot.plugins spring-plugin"
    ["maven-plugin"]="org.acmsl.bytehot.plugins maven-plugin"
    ["gradle-plugin"]="org.acmsl.bytehot.plugins gradle-plugin"
    ["intellij-plugin"]="org.acmsl.bytehot.plugins intellij-plugin"
    ["eclipse-plugin"]="org.acmsl.bytehot.plugins eclipse-plugin"
    ["vscode-plugin"]="org.acmsl.bytehot.plugins vscode-plugin"
)

# Main execution
print_color $BLUE "Setting up CI/CD workflows for extracted repositories..."

if [[ ! -d "$MIGRATION_DIR" ]]; then
    print_color $YELLOW "No migration directory found. Run module extraction first."
    exit 1
fi

# Process each extracted repository
for repo_dir in "$MIGRATION_DIR"/*; do
    if [[ -d "$repo_dir" ]]; then
        repo_name=$(basename "$repo_dir")
        
        if [[ -n "${repo_configs[$repo_name]}" ]]; then
            config=(${repo_configs[$repo_name]})
            group_id="${config[0]}"
            artifact_id="${config[1]}"
            
            print_color $YELLOW "Setting up CI/CD for $repo_name..."
            create_ci_workflow "$repo_dir" "$group_id" "$artifact_id"
            create_secrets_documentation "$repo_dir"
        else
            print_color $YELLOW "No configuration found for $repo_name, skipping..."
        fi
    fi
done

print_color $GREEN "✓ CI/CD setup completed for all repositories"

print_color $BLUE "Next steps:"
echo "1. Push extracted repositories to GitHub"
echo "2. Configure required secrets in each repository"
echo "3. Enable GitHub Pages for documentation"
echo "4. Set up branch protection rules"
echo "5. Test CI/CD pipelines with initial releases"

print_color $YELLOW "See .github/SECRETS.md in each repository for secret configuration details"