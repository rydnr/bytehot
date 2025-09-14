#!/usr/bin/env bash

# Module Extraction Script for ByteHot Repository Restructuring
# Extracts a module from the monorepo preserving git history

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

# Usage function
usage() {
    echo "Usage: $0 <module_name> <target_org> <target_repo> <group_id> <artifact_id>"
    echo ""
    echo "Examples:"
    echo "  $0 java-commons rydnr java-commons org.acmsl.commons java-commons"
    echo "  $0 bytehot-domain bytehot domain org.acmsl.bytehot bytehot-domain"
    echo "  $0 javaeda-domain java-eda domain org.acmsl.javaeda javaeda-domain"
    echo ""
    echo "Parameters:"
    echo "  module_name   - Name of the module directory to extract"
    echo "  target_org    - GitHub organization (rydnr, bytehot, java-eda)"
    echo "  target_repo   - Target repository name"
    echo "  group_id      - Maven group ID for published artifact"
    echo "  artifact_id   - Maven artifact ID for published artifact"
    exit 1
}

# Validate arguments
if [[ $# -ne 5 ]]; then
    print_color $RED "Error: Invalid number of arguments"
    usage
fi

MODULE_NAME="$1"
TARGET_ORG="$2"
TARGET_REPO="$3"
GROUP_ID="$4"
ARTIFACT_ID="$5"

# Validate module exists
if [[ ! -d "$REPO_ROOT/$MODULE_NAME" ]]; then
    print_color $RED "Error: Module '$MODULE_NAME' does not exist in $REPO_ROOT"
    exit 1
fi

print_color $BLUE "Extracting module: $MODULE_NAME"
print_color $YELLOW "Target: github.com/$TARGET_ORG/$TARGET_REPO"
print_color $YELLOW "Maven: $GROUP_ID:$ARTIFACT_ID"

# Create migration directory
mkdir -p "$MIGRATION_DIR"
cd "$MIGRATION_DIR"

# Create a unique temporary repository for this extraction
TEMP_REPO="bytehot-temp-$MODULE_NAME"
if [[ -d "$TEMP_REPO" ]]; then
    print_color $YELLOW "Removing existing temp repository..."
    rm -rf "$TEMP_REPO"
fi

print_color $YELLOW "Cloning repository for extraction..."
git clone "$REPO_ROOT" "$TEMP_REPO"

cd "$TEMP_REPO"

# Create extraction branch
EXTRACTION_BRANCH="extract-$MODULE_NAME"
git checkout -B "$EXTRACTION_BRANCH"

# Use git filter-repo to extract only the module
# Note: git filter-repo is the modern replacement for git filter-branch
if ! command -v git-filter-repo &> /dev/null; then
    print_color $RED "Error: git-filter-repo is required but not installed"
    print_color $YELLOW "Install with: pip install git-filter-repo"
    exit 1
fi

print_color $YELLOW "Extracting module with git filter-repo..."

# Extract only the module directory and relevant files
git filter-repo \
    --path "$MODULE_NAME/" \
    --path "pom.xml" \
    --path "LICENSE" \
    --path "README.md" \
    --path ".github/workflows/" \
    --path ".gitignore" \
    --force

# Move module contents to root
if [[ -d "$MODULE_NAME" ]]; then
    # Move all contents from module directory to root
    find "$MODULE_NAME" -mindepth 1 -maxdepth 1 -exec mv {} . \;
    rmdir "$MODULE_NAME"
fi

# Update pom.xml for standalone module
print_color $YELLOW "Updating pom.xml for standalone module..."

# Create new pom.xml specific to this module
cat > pom.xml << EOF
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" 
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/maven-v4_0_0.xsd">
  <modelVersion>4.0.0</modelVersion>
  
  <groupId>$GROUP_ID</groupId>
  <artifactId>$ARTIFACT_ID</artifactId>
  <version>1.0.0-SNAPSHOT</version>
  <packaging>jar</packaging>
  
  <name>$(echo "$ARTIFACT_ID" | sed 's/-/ /g' | sed 's/\b\w/\U&/g')</name>
  <description>Extracted from ByteHot monorepo - $(echo "$MODULE_NAME" | sed 's/-/ /g')</description>
  <url>https://github.com/$TARGET_ORG/$TARGET_REPO</url>
  <inceptionYear>2025</inceptionYear>
  
  <organization>
    <name>ACM-SL</name>
    <url>http://www.acm-sl.org</url>
  </organization>
  
  <licenses>
    <license>
      <name>GNU General Public License v3.0</name>
      <url>https://www.gnu.org/licenses/gpl-3.0.txt</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  
  <developers>
    <developer>
      <name>José San Leandro</name>
      <email>rydnr@acm-sl.org</email>
      <url>https://github.com/rydnr</url>
    </developer>
  </developers>
  
  <scm>
    <connection>scm:git:https://github.com/$TARGET_ORG/$TARGET_REPO.git</connection>
    <developerConnection>scm:git:git@github.com:$TARGET_ORG/$TARGET_REPO.git</developerConnection>
    <url>https://github.com/$TARGET_ORG/$TARGET_REPO</url>
  </scm>
  
  <issueManagement>
    <system>github</system>
    <url>https://github.com/$TARGET_ORG/$TARGET_REPO/issues</url>
  </issueManagement>
  
  <properties>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    
    <!-- Plugin versions -->
    <maven-compiler-plugin.version>3.11.0</maven-compiler-plugin.version>
    <maven-surefire-plugin.version>3.0.0</maven-surefire-plugin.version>
    <maven-source-plugin.version>3.3.0</maven-source-plugin.version>
    <maven-javadoc-plugin.version>3.5.0</maven-javadoc-plugin.version>
    <maven-gpg-plugin.version>3.1.0</maven-gpg-plugin.version>
    <nexus-staging-maven-plugin.version>1.6.13</nexus-staging-maven-plugin.version>
    
    <!-- Dependency versions -->
    <lombok.version>1.18.30</lombok.version>
    <checker-qual.version>3.39.0</checker-qual.version>
    <junit.version>5.10.1</junit.version>
    <assertj.version>3.24.2</assertj.version>
  </properties>
EOF

# Add dependencies based on module type
if [[ "$MODULE_NAME" == "java-commons" ]]; then
    cat >> pom.xml << 'EOF'
  
  <dependencies>
    <!-- Commons logging -->
    <dependency>
      <groupId>commons-logging</groupId>
      <artifactId>commons-logging</artifactId>
      <version>1.2</version>
    </dependency>
    
    <!-- Commons beanutils -->
    <dependency>
      <groupId>commons-beanutils</groupId>
      <artifactId>commons-beanutils</artifactId>
      <version>1.9.4</version>
    </dependency>
    
    <!-- Optional regex dependencies -->
    <dependency>
      <groupId>gnu-regexp</groupId>
      <artifactId>gnu-regexp</artifactId>
      <version>1.1.4</version>
      <optional>true</optional>
    </dependency>
    
    <dependency>
      <groupId>jakarta-regexp</groupId>
      <artifactId>jakarta-regexp</artifactId>
      <version>1.4</version>
      <optional>true</optional>
    </dependency>
    
    <dependency>
      <groupId>oro</groupId>
      <artifactId>oro</artifactId>
      <version>2.0.8</version>
      <optional>true</optional>
    </dependency>
    
    <!-- StringTemplate -->
    <dependency>
      <groupId>org.antlr</groupId>
      <artifactId>ST4</artifactId>
      <version>4.3.4</version>
    </dependency>
    
    <!-- Jackson for JSON serialization -->
    <dependency>
      <groupId>com.fasterxml.jackson.core</groupId>
      <artifactId>jackson-core</artifactId>
      <version>2.15.2</version>
    </dependency>
    <dependency>
      <groupId>com.fasterxml.jackson.core</groupId>
      <artifactId>jackson-databind</artifactId>
      <version>2.15.2</version>
    </dependency>
    <dependency>
      <groupId>com.fasterxml.jackson.datatype</groupId>
      <artifactId>jackson-datatype-jsr310</artifactId>
      <version>2.15.2</version>
    </dependency>
    
    <!-- Optional servlet API -->
    <dependency>
      <groupId>javax.servlet</groupId>
      <artifactId>javax.servlet-api</artifactId>
      <version>4.0.1</version>
      <optional>true</optional>
    </dependency>
    
    <!-- MX4J for JMX -->
    <dependency>
      <groupId>mx4j</groupId>
      <artifactId>mx4j</artifactId>
      <version>3.0.2</version>
      <optional>true</optional>
    </dependency>
    
    <!-- Lombok for reducing boilerplate -->
    <dependency>
      <groupId>org.projectlombok</groupId>
      <artifactId>lombok</artifactId>
      <version>${lombok.version}</version>
      <scope>provided</scope>
    </dependency>
    
    <!-- Checker Framework annotations -->
    <dependency>
      <groupId>org.checkerframework</groupId>
      <artifactId>checker-qual</artifactId>
      <version>${checker-qual.version}</version>
    </dependency>
    
    <!-- Test dependencies -->
    <dependency>
      <groupId>org.junit.jupiter</groupId>
      <artifactId>junit-jupiter</artifactId>
      <version>${junit.version}</version>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.assertj</groupId>
      <artifactId>assertj-core</artifactId>
      <version>${assertj.version}</version>
      <scope>test</scope>
    </dependency>
    
    <!-- Legacy JUnit 4 support for old tests -->
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>4.13.2</version>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.junit.vintage</groupId>
      <artifactId>junit-vintage-engine</artifactId>
      <version>${junit.version}</version>
      <scope>test</scope>
    </dependency>
  </dependencies>
EOF
elif [[ "$MODULE_NAME" == "java-commons-infrastructure" ]]; then
    # Java Commons Infrastructure has specific dependencies
    cat >> pom.xml << 'EOF'
  
  <dependencies>
    <!-- Java Commons Base -->
    <dependency>
      <groupId>org.acmsl.commons</groupId>
      <artifactId>java-commons</artifactId>
      <version>1.0.0</version>
    </dependency>

    <!-- Configuration Support -->
    <dependency>
      <groupId>org.yaml</groupId>
      <artifactId>snakeyaml</artifactId>
      <version>2.0</version>
    </dependency>

    <!-- JSON Processing -->
    <dependency>
      <groupId>com.fasterxml.jackson.core</groupId>
      <artifactId>jackson-core</artifactId>
      <version>2.15.2</version>
    </dependency>
    <dependency>
      <groupId>com.fasterxml.jackson.core</groupId>
      <artifactId>jackson-databind</artifactId>
      <version>2.15.2</version>
    </dependency>
    <dependency>
      <groupId>com.fasterxml.jackson.core</groupId>
      <artifactId>jackson-annotations</artifactId>
      <version>2.15.2</version>
    </dependency>
    <dependency>
      <groupId>com.fasterxml.jackson.dataformat</groupId>
      <artifactId>jackson-dataformat-yaml</artifactId>
      <version>2.15.2</version>
    </dependency>

    <!-- Logging -->
    <dependency>
      <groupId>org.slf4j</groupId>
      <artifactId>slf4j-api</artifactId>
      <version>2.0.7</version>
    </dependency>
    
    <!-- Lombok for reducing boilerplate -->
    <dependency>
      <groupId>org.projectlombok</groupId>
      <artifactId>lombok</artifactId>
      <version>${lombok.version}</version>
      <scope>provided</scope>
    </dependency>
    
    <!-- Checker Framework annotations -->
    <dependency>
      <groupId>org.checkerframework</groupId>
      <artifactId>checker-qual</artifactId>
      <version>${checker-qual.version}</version>
    </dependency>
    
    <!-- Test dependencies -->
    <dependency>
      <groupId>org.junit.jupiter</groupId>
      <artifactId>junit-jupiter</artifactId>
      <version>${junit.version}</version>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.assertj</groupId>
      <artifactId>assertj-core</artifactId>
      <version>${assertj.version}</version>
      <scope>test</scope>
    </dependency>
    
    <!-- Legacy JUnit 4 support for old tests -->
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>4.13.2</version>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.junit.vintage</groupId>
      <artifactId>junit-vintage-engine</artifactId>
      <version>${junit.version}</version>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.mockito</groupId>
      <artifactId>mockito-core</artifactId>
      <version>5.4.0</version>
      <scope>test</scope>
    </dependency>
  </dependencies>
EOF
elif [[ "$MODULE_NAME" == *"-domain" ]]; then
    # Domain modules have minimal dependencies
    cat >> pom.xml << 'EOF'
  
  <dependencies>
    <!-- Core domain dependencies only -->
    <dependency>
      <groupId>org.acmsl.commons</groupId>
      <artifactId>java-commons</artifactId>
      <version>1.0.0</version>
    </dependency>
    
    <!-- Lombok for reducing boilerplate -->
    <dependency>
      <groupId>org.projectlombok</groupId>
      <artifactId>lombok</artifactId>
      <version>${lombok.version}</version>
      <scope>provided</scope>
    </dependency>
    
    <!-- Checker Framework annotations -->
    <dependency>
      <groupId>org.checkerframework</groupId>
      <artifactId>checker-qual</artifactId>
      <version>${checker-qual.version}</version>
    </dependency>
    
    <!-- Test dependencies -->
    <dependency>
      <groupId>org.junit.jupiter</groupId>
      <artifactId>junit-jupiter</artifactId>
      <version>${junit.version}</version>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.assertj</groupId>
      <artifactId>assertj-core</artifactId>
      <version>${assertj.version}</version>
      <scope>test</scope>
    </dependency>
    
    <!-- Legacy JUnit 4 support for old tests -->
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>4.13.2</version>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.junit.vintage</groupId>
      <artifactId>junit-vintage-engine</artifactId>
      <version>${junit.version}</version>
      <scope>test</scope>
    </dependency>
  </dependencies>
EOF
else
    # Other modules need more dependencies - will be customized later
    cat >> pom.xml << 'EOF'
  
  <dependencies>
    <!-- Dependencies will be customized based on module requirements -->
    
    <!-- Lombok for reducing boilerplate -->
    <dependency>
      <groupId>org.projectlombok</groupId>
      <artifactId>lombok</artifactId>
      <version>${lombok.version}</version>
      <scope>provided</scope>
    </dependency>
    
    <!-- Checker Framework annotations -->
    <dependency>
      <groupId>org.checkerframework</groupId>
      <artifactId>checker-qual</artifactId>
      <version>${checker-qual.version}</version>
    </dependency>
    
    <!-- Test dependencies -->
    <dependency>
      <groupId>org.junit.jupiter</groupId>
      <artifactId>junit-jupiter</artifactId>
      <version>${junit.version}</version>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.assertj</groupId>
      <artifactId>assertj-core</artifactId>
      <version>${assertj.version}</version>
      <scope>test</scope>
    </dependency>
    
    <!-- Legacy JUnit 4 support for old tests -->
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>4.13.2</version>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.junit.vintage</groupId>
      <artifactId>junit-vintage-engine</artifactId>
      <version>${junit.version}</version>
      <scope>test</scope>
    </dependency>
  </dependencies>
EOF
fi

# Add build configuration
cat >> pom.xml << 'EOF'
  
  <build>
    <plugins>
      <!-- Compiler plugin -->
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>${maven-compiler-plugin.version}</version>
        <configuration>
          <source>17</source>
          <target>17</target>
          <compilerArgs>
            <arg>-parameters</arg>
          </compilerArgs>
        </configuration>
      </plugin>
      
      <!-- Surefire plugin for tests -->
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-surefire-plugin</artifactId>
        <version>${maven-surefire-plugin.version}</version>
      </plugin>
      
      <!-- Source plugin -->
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-source-plugin</artifactId>
        <version>${maven-source-plugin.version}</version>
        <executions>
          <execution>
            <id>attach-sources</id>
            <goals>
              <goal>jar-no-fork</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
      
      <!-- Javadoc plugin -->
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-javadoc-plugin</artifactId>
        <version>${maven-javadoc-plugin.version}</version>
        <executions>
          <execution>
            <id>attach-javadocs</id>
            <goals>
              <goal>jar</goal>
            </goals>
          </execution>
        </executions>
        <configuration>
          <doclint>none</doclint>
        </configuration>
      </plugin>
    </plugins>
  </build>
  
  <profiles>
    <!-- Release profile for Maven Central -->
    <profile>
      <id>release</id>
      <build>
        <plugins>
          <!-- GPG plugin for signing -->
          <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-gpg-plugin</artifactId>
            <version>${maven-gpg-plugin.version}</version>
            <executions>
              <execution>
                <id>sign-artifacts</id>
                <phase>verify</phase>
                <goals>
                  <goal>sign</goal>
                </goals>
              </execution>
            </executions>
          </plugin>
          
          <!-- Nexus staging plugin -->
          <plugin>
            <groupId>org.sonatype.plugins</groupId>
            <artifactId>nexus-staging-maven-plugin</artifactId>
            <version>${nexus-staging-maven-plugin.version}</version>
            <extensions>true</extensions>
            <configuration>
              <serverId>ossrh</serverId>
              <nexusUrl>https://s01.oss.sonatype.org/</nexusUrl>
              <autoReleaseAfterClose>true</autoReleaseAfterClose>
            </configuration>
          </plugin>
        </plugins>
      </build>
      
      <distributionManagement>
        <snapshotRepository>
          <id>ossrh</id>
          <url>https://s01.oss.sonatype.org/content/repositories/snapshots</url>
        </snapshotRepository>
        <repository>
          <id>ossrh</id>
          <url>https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/</url>
        </repository>
      </distributionManagement>
    </profile>
  </profiles>
</project>
EOF

# Create README.md for the extracted module
print_color $YELLOW "Creating README.md..."

cat > README.md << EOF
# $(echo "$ARTIFACT_ID" | sed 's/-/ /g' | sed 's/\b\w/\U&/g')

Extracted from the ByteHot monorepo - $(echo "$MODULE_NAME" | sed 's/-/ /g').

## Installation

### Maven
\`\`\`xml
<dependency>
  <groupId>$GROUP_ID</groupId>
  <artifactId>$ARTIFACT_ID</artifactId>
  <version>1.0.0</version>
</dependency>
\`\`\`

### Gradle
\`\`\`gradle
implementation '$GROUP_ID:$ARTIFACT_ID:1.0.0'
\`\`\`

## Usage

[Usage documentation will be added during migration]

## Building

\`\`\`bash
mvn clean install
\`\`\`

## Testing

\`\`\`bash
mvn test
\`\`\`

## License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE) file for details.

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct and the process for submitting pull requests.

## Changelog

See [CHANGELOG.md](CHANGELOG.md) for version history.
EOF

# Create CONTRIBUTING.md
cat > CONTRIBUTING.md << 'EOF'
# Contributing Guide

## Development Process

This project follows Test-Driven Development (TDD) with the following workflow:

### TDD Commit Pattern
- 🧪 `[#123] Add failing test` - After adding a failing test
- 🤔 `[#123] Naive implementation` - Simple/stubbed solution  
- ✅ `[#123] Working implementation` - Real business logic
- 🚀 `[#123] Refactor` - Code improvement

### Code Standards
- Follow Domain-Driven Design principles
- Use Hexagonal Architecture patterns
- Maintain clean code standards
- Include comprehensive tests

### Pull Request Process
1. Create an issue for your feature/bug
2. Follow TDD workflow
3. Submit PR with clear description
4. Ensure all tests pass
5. Request review from maintainers

## Code Style
- Use Java 17+ features
- Follow existing naming conventions
- Add Javadoc for public APIs
- Use Lombok annotations appropriately
EOF

# Create CHANGELOG.md
cat > CHANGELOG.md << 'EOF'
# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Initial extraction from ByteHot monorepo
- Maven Central publishing support
- CI/CD pipeline configuration

## [1.0.0] - 2025-01-XX

### Added
- Initial release extracted from ByteHot monorepo
EOF

# Create .gitignore
cat > .gitignore << 'EOF'
# Maven
target/
pom.xml.tag
pom.xml.releaseBackup
pom.xml.versionsBackup
pom.xml.next
release.properties
dependency-reduced-pom.xml
buildNumber.properties
.mvn/timing.properties
.mvn/wrapper/maven-wrapper.jar

# IDE
.idea/
*.iml
.vscode/
.settings/
.project
.classpath

# OS
.DS_Store
Thumbs.db

# Logs
*.log
EOF

# Commit the changes
print_color $YELLOW "Committing extraction changes..."
git add .
git commit -m "🚀 Extract $MODULE_NAME module from monorepo

- Standalone pom.xml with Maven Central publishing
- Updated dependencies for independent module
- Added README, CONTRIBUTING, and CHANGELOG
- Configured for $GROUP_ID:$ARTIFACT_ID"

# Move the extracted repository to final location
EXTRACTED_DIR="$MIGRATION_DIR/$TARGET_REPO"
if [[ -d "$EXTRACTED_DIR" ]]; then
    rm -rf "$EXTRACTED_DIR"
fi

print_color $YELLOW "Creating final extracted repository..."
# After git filter-repo, the current directory is already the extracted repo
# Just move it to the proper name
cd "$MIGRATION_DIR"
mv "$TEMP_REPO" "$TARGET_REPO"
EXTRACTED_DIR="$MIGRATION_DIR/$TARGET_REPO"

# Clean up - no longer needed since we moved the directory
print_color $YELLOW "Cleaning up temporary files..."

print_color $GREEN "✓ Module extraction completed successfully!"
print_color $BLUE "Extracted repository location: $EXTRACTED_DIR"
print_color $YELLOW "Next steps:"
echo "1. Review the extracted repository: cd $EXTRACTED_DIR"
echo "2. Test the build: mvn clean install"
echo "3. Create GitHub repository: github.com/$TARGET_ORG/$TARGET_REPO"
echo "4. Push extracted code: git remote add origin git@github.com:$TARGET_ORG/$TARGET_REPO.git"
echo "5. Push: git push -u origin main"

print_color $BLUE "Module extraction summary:"
echo "  Module: $MODULE_NAME"
echo "  Target: github.com/$TARGET_ORG/$TARGET_REPO"
echo "  Maven: $GROUP_ID:$ARTIFACT_ID"
echo "  Location: $EXTRACTED_DIR"