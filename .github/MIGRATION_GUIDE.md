# ByteHot Repository Migration Guide

Complete guide for migrating the ByteHot monorepo to multiple focused repositories with Maven Central publishing.

## Overview

This migration transforms the ByteHot monorepo into 15 separate repositories organized across 3 GitHub organizations:
- **rydnr**: Foundation libraries (java-commons)
- **java-eda**: JavaEDA framework components  
- **bytehot**: ByteHot core and plugins

## Quick Start

### Prerequisites
```bash
# Install required tools
pip install git-filter-repo
# Install GitHub CLI from https://cli.github.com/
gh auth login
```

### 1. Create GitHub Repositories
```bash
# Create all required repositories and organizations
.github/scripts/create-github-repos.sh
```

### 2. Extract All Modules
```bash
# Complete migration (all phases)
.github/scripts/migrate-all-modules.sh all

# Or run individual phases
.github/scripts/migrate-all-modules.sh phase1  # Foundation libraries
.github/scripts/migrate-all-modules.sh phase2  # JavaEDA framework
.github/scripts/migrate-all-modules.sh phase3  # ByteHot core
.github/scripts/migrate-all-modules.sh phase4  # ByteHot plugins
```

### 3. Set Up CI/CD
```bash
# Configure GitHub Actions workflows
.github/scripts/setup-ci-cd.sh
```

### 4. Check Status
```bash
# View migration progress
.github/scripts/migrate-all-modules.sh status
```

## Migration Phases

### Phase 1: Foundation Libraries
Extract `java-commons` foundation modules first since other modules depend on them.

**Modules**:
- `java-commons` → `github:rydnr/java-commons`
- `java-commons-infrastructure` → `github:rydnr/java-commons-infrastructure`

**Dependencies**: None (foundation layer)

```bash
.github/scripts/migrate-all-modules.sh phase1
```

### Phase 2: JavaEDA Framework  
Extract JavaEDA framework components that build on the foundation libraries.

**Modules**:
- `javaeda-domain` → `github:java-eda/domain`
- `javaeda-infrastructure` → `github:java-eda/infrastructure`
- `javaeda-application` → `github:java-eda/application`

**Dependencies**: java-commons (Phase 1)

```bash
.github/scripts/migrate-all-modules.sh phase2
```

### Phase 3: ByteHot Core
Extract core ByteHot modules that implement the JVM agent functionality.

**Modules**:
- `bytehot-domain` → `github:bytehot/domain`
- `bytehot-infrastructure` → `github:bytehot/infrastructure`
- `bytehot-application` → `github:bytehot/application`

**Dependencies**: java-commons (Phase 1), javaeda-* (Phase 2)

```bash
.github/scripts/migrate-all-modules.sh phase3
```

### Phase 4: ByteHot Plugins
Extract ByteHot plugins that provide IDE and build tool integrations.

**Modules**:
- `bytehot-plugin-commons` → `github:bytehot/plugin-commons`
- `bytehot-spring-plugin` → `github:bytehot/spring-plugin`
- `bytehot-maven-plugin` → `github:bytehot/maven-plugin`
- `bytehot-gradle-plugin` → `github:bytehot/gradle-plugin`
- `bytehot-intellij-plugin` → `github:bytehot/intellij-plugin`
- `bytehot-eclipse-plugin` → `github:bytehot/eclipse-plugin`
- `bytehot-vscode-extension` → `github:bytehot/vscode-plugin`

**Dependencies**: java-commons (Phase 1), bytehot-domain (Phase 3)

```bash
.github/scripts/migrate-all-modules.sh phase4
```

## Manual Steps

### 1. Create GitHub Organizations
Before running scripts, manually create required organizations:

1. **java-eda** organization:
   - Visit: https://github.com/organizations/new
   - Name: `java-eda`
   - Description: "JavaEDA Framework - Domain-driven design framework for Java"

2. **bytehot** organization:
   - Visit: https://github.com/organizations/new  
   - Name: `bytehot`
   - Description: "ByteHot - JVM bytecode hot-swapping agent and tools"

### 2. Configure Maven Central Publishing

#### Sonatype OSSRH Setup
1. Create account at https://issues.sonatype.org/
2. Create Jira ticket for group ID verification:
   - `org.acmsl.commons` (for java-commons)
   - `org.acmsl.javaeda` (for JavaEDA framework)
   - `org.acmsl.bytehot` (for ByteHot)
   - `org.acmsl.bytehot.plugins` (for ByteHot plugins)

#### GPG Key Setup
```bash
# Generate GPG key for signing
gpg --full-generate-key

# Export private key (for GitHub secrets)
gpg --armor --export-secret-keys YOUR_KEY_ID

# Publish public key to keyservers
gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID
gpg --keyserver keys.openpgp.org --send-keys YOUR_KEY_ID
```

### 3. Configure GitHub Secrets
For each repository, configure these secrets in Settings → Secrets and variables → Actions:

- `OSSRH_USERNAME` - Sonatype OSSRH username
- `OSSRH_TOKEN` - Sonatype OSSRH token
- `MAVEN_GPG_PRIVATE_KEY` - GPG private key (ASCII armored)
- `MAVEN_GPG_PASSPHRASE` - GPG key passphrase

### 4. Push Extracted Repositories
After extraction, push each repository:

```bash
cd migration/java-commons
git remote add origin git@github.com:rydnr/java-commons.git
git push -u origin main

cd ../domain  # java-eda/domain
git remote add origin git@github.com:java-eda/domain.git
git push -u origin main

# Repeat for all extracted repositories...
```

### 5. Configure Branch Protection
For each repository:
1. Go to Settings → Branches
2. Add rule for `main` branch
3. Enable:
   - Require pull request reviews
   - Require status checks to pass
   - Require branches to be up to date
   - Include administrators

## Directory Structure After Migration

```
migration/
├── java-commons/                    # rydnr/java-commons
├── java-commons-infrastructure/     # rydnr/java-commons-infrastructure
├── domain/                          # java-eda/domain (javaeda-domain)
├── infrastructure/                  # java-eda/infrastructure (javaeda-infrastructure)  
├── application/                     # java-eda/application (javaeda-application)
├── domain/                          # bytehot/domain (bytehot-domain)
├── infrastructure/                  # bytehot/infrastructure (bytehot-infrastructure)
├── application/                     # bytehot/application (bytehot-application)
├── plugin-commons/                  # bytehot/plugin-commons
├── spring-plugin/                   # bytehot/spring-plugin
├── maven-plugin/                    # bytehot/maven-plugin
├── gradle-plugin/                   # bytehot/gradle-plugin
├── intellij-plugin/                 # bytehot/intellij-plugin
├── eclipse-plugin/                  # bytehot/eclipse-plugin
└── vscode-plugin/                   # bytehot/vscode-plugin
```

## Maven Coordinates After Migration

### Foundation Libraries
```xml
<!-- Java Commons -->
<dependency>
  <groupId>org.acmsl.commons</groupId>
  <artifactId>java-commons</artifactId>
  <version>1.0.0</version>
</dependency>

<!-- Java Commons Infrastructure -->
<dependency>
  <groupId>org.acmsl.commons</groupId>
  <artifactId>java-commons-infrastructure</artifactId>
  <version>1.0.0</version>
</dependency>
```

### JavaEDA Framework
```xml
<!-- JavaEDA Domain -->
<dependency>
  <groupId>org.acmsl.javaeda</groupId>
  <artifactId>javaeda-domain</artifactId>
  <version>1.0.0</version>
</dependency>

<!-- JavaEDA Infrastructure -->
<dependency>
  <groupId>org.acmsl.javaeda</groupId>
  <artifactId>javaeda-infrastructure</artifactId>
  <version>1.0.0</version>
</dependency>

<!-- JavaEDA Application -->
<dependency>
  <groupId>org.acmsl.javaeda</groupId>
  <artifactId>javaeda-application</artifactId>
  <version>1.0.0</version>
</dependency>
```

### ByteHot Core
```xml
<!-- ByteHot Domain -->
<dependency>
  <groupId>org.acmsl.bytehot</groupId>
  <artifactId>bytehot-domain</artifactId>
  <version>1.0.0</version>
</dependency>

<!-- ByteHot Infrastructure -->
<dependency>
  <groupId>org.acmsl.bytehot</groupId>
  <artifactId>bytehot-infrastructure</artifactId>
  <version>1.0.0</version>
</dependency>

<!-- ByteHot Application (JVM Agent) -->
<dependency>
  <groupId>org.acmsl.bytehot</groupId>
  <artifactId>bytehot-application</artifactId>
  <version>1.0.0</version>
</dependency>
```

### ByteHot Plugins
```xml
<!-- Plugin Commons -->
<dependency>
  <groupId>org.acmsl.bytehot.plugins</groupId>
  <artifactId>plugin-commons</artifactId>
  <version>1.0.0</version>
</dependency>

<!-- Spring Plugin -->
<dependency>
  <groupId>org.acmsl.bytehot.plugins</groupId>
  <artifactId>spring-plugin</artifactId>
  <version>1.0.0</version>
</dependency>

<!-- Maven Plugin -->
<dependency>
  <groupId>org.acmsl.bytehot.plugins</groupId>
  <artifactId>maven-plugin</artifactId>
  <version>1.0.0</version>
</dependency>
```

## Testing the Migration

### Verify Extracted Repositories
```bash
# Test each extracted module builds successfully
cd migration/java-commons
mvn clean install

cd ../domain  # java-eda/domain
mvn clean install

# Repeat for all modules...
```

### Integration Testing
```bash
# Create test project that uses extracted dependencies
mkdir test-integration
cd test-integration

# Create pom.xml with extracted dependencies
cat > pom.xml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0">
  <modelVersion>4.0.0</modelVersion>
  
  <groupId>org.acmsl.test</groupId>
  <artifactId>integration-test</artifactId>
  <version>1.0.0-SNAPSHOT</version>
  
  <properties>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
  </properties>
  
  <dependencies>
    <dependency>
      <groupId>org.acmsl.commons</groupId>
      <artifactId>java-commons</artifactId>
      <version>1.0.0-SNAPSHOT</version>
    </dependency>
    <dependency>
      <groupId>org.acmsl.bytehot</groupId>
      <artifactId>bytehot-domain</artifactId>
      <version>1.0.0-SNAPSHOT</version>
    </dependency>
  </dependencies>
</project>
EOF

# Test dependencies resolve correctly
mvn clean compile
```

## Troubleshooting

### Common Issues

#### 1. git-filter-repo Not Found
```bash
pip install git-filter-repo
```

#### 2. GitHub Authentication Issues
```bash
gh auth login
gh auth status
```

#### 3. Maven Dependencies Not Resolving
Check that dependencies are published in correct order:
1. Foundation libraries first
2. Framework modules second  
3. Application modules last

#### 4. GPG Signing Failures
```bash
# Verify GPG key is available
gpg --list-secret-keys

# Test signing
echo "test" | gpg --clearsign
```

### Recovery Procedures

#### Rollback to Monorepo
If migration issues occur:
1. Keep original monorepo intact
2. Use git branches for testing
3. Can revert to monorepo dependencies
4. Coordinate releases across repositories

#### Partial Migration
Run individual phases if complete migration fails:
```bash
.github/scripts/migrate-all-modules.sh phase1
# Fix issues, then continue
.github/scripts/migrate-all-modules.sh phase2
```

## Post-Migration

### Update Main Repository
After successful migration, update the main ByteHot repository:
1. Remove extracted modules from pom.xml
2. Update dependencies to use published artifacts
3. Update documentation and examples
4. Create migration announcement

### Maintenance
- Monitor Maven Central download metrics
- Coordinate releases across repositories  
- Update inter-repository dependencies
- Maintain documentation consistency

## Timeline

**Estimated Duration**: 6-10 weeks total

- **Phase 1**: Setup and foundation libraries (1-2 weeks)
- **Phase 2-5**: Module extraction (4-6 weeks)  
- **Phase 6**: Integration and testing (1-2 weeks)

## Success Criteria

- [ ] All 15 modules successfully extracted with git history
- [ ] All artifacts published to Maven Central
- [ ] CI/CD pipelines operational for all repositories
- [ ] Integration tests passing with new dependencies
- [ ] Documentation updated and accessible
- [ ] Smooth transition for existing users
- [ ] Clear migration guides and communication