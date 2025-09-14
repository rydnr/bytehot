#!/bin/bash

# ByteHot Monorepo Split Automation Script
# Course 16: Repository Architecture & Decomposition
# 
# This script demonstrates industry best practices for monorepo decomposition
# with complete git history preservation and automated validation.
#
# Prerequisites:
# - git-filter-repo installed (brew install git-filter-repo)
# - GitHub CLI installed (brew install gh)
# - Proper GitHub authentication configured
# - Write access to target GitHub organizations

set -euo pipefail

# Configuration
readonly SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" &> /dev/null && pwd)"
readonly SOURCE_REPO="rydnr/bytehot"
readonly WORK_DIR="${SCRIPT_DIR}/split-work"
readonly LOG_FILE="${SCRIPT_DIR}/split-operation.log"

# Color codes for output
readonly RED='\033[0;31m'
readonly GREEN='\033[0;32m'
readonly YELLOW='\033[1;33m'
readonly BLUE='\033[0;34m'
readonly NC='\033[0m' # No Color

# Repository mapping configuration
# Format: "source_module:target_org/target_repo:groupId"
declare -a REPO_MAPPINGS=(
    "java-commons:java-eda/shared:org.acmsl.javaeda"
    "java-commons-infrastructure:java-eda/shared-infrastructure:org.acmsl.javaeda"
    "javaeda-domain:java-eda/domain:org.acmsl.javaeda"
    "javaeda-infrastructure:java-eda/infrastructure:org.acmsl.javaeda"
    "javaeda-application:java-eda/application:org.acmsl.javaeda"
    "bytehot-domain:bytehot/domain:org.acmsl.bytehot"
    "bytehot-application:bytehot/application:org.acmsl.bytehot"
    "bytehot-infrastructure:bytehot/infrastructure:org.acmsl.bytehot"
    "bytehot-plugin-commons:bytehot/plugin-commons:org.acmsl.bytehot"
    "bytehot-maven-plugin:bytehot/maven-plugin:org.acmsl.bytehot.plugin"
    "bytehot-spring-plugin:bytehot/spring-plugin:org.acmsl.bytehot.plugin"
    "bytehot-intellij-plugin:bytehot/intellij-plugin:org.acmsl.bytehot.plugin"
    "bytehot-eclipse-plugin:bytehot/eclipse-plugin:org.acmsl.bytehot.plugin"
    "bytehot-vscode-extension:bytehot/vscode-extension:org.acmsl.bytehot.plugin"
    "bytehot-gradle-plugin:bytehot/gradle-plugin:org.acmsl.bytehot.plugin"
)

# Logging functions
log() {
    echo -e "[$(date +'%Y-%m-%d %H:%M:%S')] $*" | tee -a "$LOG_FILE"
}

log_info() {
    log "${BLUE}[INFO]${NC} $*"
}

log_success() {
    log "${GREEN}[SUCCESS]${NC} $*"
}

log_warning() {
    log "${YELLOW}[WARNING]${NC} $*"
}

log_error() {
    log "${RED}[ERROR]${NC} $*"
}

# Prerequisites validation
check_prerequisites() {
    log_info "Checking prerequisites..."
    
    # Check git-filter-repo
    if ! command -v git-filter-repo &> /dev/null; then
        log_error "git-filter-repo is required but not installed"
        log_info "Install with: brew install git-filter-repo"
        exit 1
    fi
    
    # Check GitHub CLI
    if ! command -v gh &> /dev/null; then
        log_error "GitHub CLI is required but not installed"
        log_info "Install with: brew install gh"
        exit 1
    fi
    
    # Check GitHub authentication
    if ! gh auth status &> /dev/null; then
        log_error "GitHub CLI authentication required"
        log_info "Authenticate with: gh auth login"
        exit 1
    fi
    
    # Check disk space (require at least 10GB)
    local available_space
    available_space=$(df -h "$SCRIPT_DIR" | awk 'NR==2 {print $4}' | sed 's/G//')
    if [[ ${available_space%.*} -lt 10 ]]; then
        log_warning "Low disk space detected. Recommend at least 10GB available"
    fi
    
    log_success "Prerequisites validated"
}

# Initialize work environment
initialize_workspace() {
    log_info "Initializing workspace..."
    
    # Clean up any previous attempts
    if [[ -d "$WORK_DIR" ]]; then
        log_warning "Removing existing work directory"
        rm -rf "$WORK_DIR"
    fi
    
    mkdir -p "$WORK_DIR"
    cd "$WORK_DIR"
    
    log_success "Workspace initialized at $WORK_DIR"
}

# Clone source repository
clone_source_repository() {
    log_info "Cloning source repository..."
    
    # Clone with full history but no working directory files for speed
    git clone --mirror "https://github.com/${SOURCE_REPO}.git" source-mirror
    
    # Create working copy from mirror
    git clone source-mirror source-repo
    cd source-repo
    
    # Verify repository structure
    if [[ ! -f "pom.xml" ]]; then
        log_error "Invalid source repository structure - no root pom.xml found"
        exit 1
    fi
    
    # Get repository statistics
    local total_commits
    total_commits=$(git rev-list --all --count)
    log_info "Source repository contains $total_commits commits"
    
    cd ..
    log_success "Source repository cloned and validated"
}

# Analyze repository dependencies
analyze_dependencies() {
    log_info "Analyzing repository dependencies..."
    
    cd source-repo
    
    # Create dependency analysis report
    cat > "../dependency-analysis.md" << 'EOF'
# ByteHot Monorepo Dependency Analysis

## Module Dependencies

This analysis shows the dependencies between modules in the monorepo,
which helps determine the optimal split order and identify potential issues.

EOF
    
    # Analyze each module's POM for dependencies
    for mapping in "${REPO_MAPPINGS[@]}"; do
        local module="${mapping%%:*}"
        if [[ -d "$module" && -f "$module/pom.xml" ]]; then
            echo "### $module" >> "../dependency-analysis.md"
            echo "" >> "../dependency-analysis.md"
            
            # Extract dependencies from POM
            grep -A 5 -B 1 "org.acmsl" "$module/pom.xml" >> "../dependency-analysis.md" || true
            echo "" >> "../dependency-analysis.md"
        fi
    done
    
    cd ..
    log_success "Dependency analysis completed"
}

# Split individual repository
split_repository() {
    local mapping="$1"
    local module="${mapping%%:*}"
    local target_info="${mapping#*:}"
    local target_repo="${target_info%%:*}"
    local group_id="${target_info#*:}"
    local repo_name="${target_repo#*/}"
    
    log_info "Splitting module: $module -> $target_repo"
    
    # Create working directory for this split
    local split_dir="split-$repo_name"
    rm -rf "$split_dir"
    
    # Clone from mirror for fresh start
    git clone source-mirror "$split_dir"
    cd "$split_dir"
    
    # Verify module exists
    if [[ ! -d "$module" ]]; then
        log_warning "Module $module not found in repository, skipping"
        cd ..
        return
    fi
    
    # Filter repository to keep only this module
    log_info "Filtering repository history for $module..."
    git filter-repo \
        --subdirectory-filter "$module/" \
        --prune-empty \
        --prune-degenerate \
        --tag-rename '':'v' \
        --force
    
    # Validate the split
    if [[ ! -f "pom.xml" ]]; then
        log_error "Split validation failed: no pom.xml found after filtering"
        cd ..
        return 1
    fi
    
    # Update POM for standalone repository
    update_pom_for_standalone "$group_id" "$repo_name"
    
    # Create README for the new repository
    create_repository_readme "$module" "$target_repo"
    
    # Commit the repository preparation changes
    git add .
    git commit -m "🔧 Prepare repository for standalone operation

- Update POM for independent building
- Add repository-specific README
- Configure for $target_repo distribution

🔥 Generated during monorepo split automation

Co-Authored-By: ByteHot Split Automation <noreply@bytehot.org>" || true
    
    # Create GitHub repository
    log_info "Creating GitHub repository: $target_repo"
    if gh repo create "$target_repo" --public --description "ByteHot $(echo $module | sed 's/.*-//' | sed 's/^./\U&/') - Professional Java Hot-Swapping" 2>/dev/null; then
        log_success "Repository $target_repo created"
    else
        log_warning "Repository $target_repo may already exist"
    fi
    
    # Push to GitHub
    log_info "Pushing to GitHub repository..."
    git remote add origin "https://github.com/$target_repo.git"
    git push -u origin main --force
    git push --tags
    
    cd ..
    log_success "Successfully split and pushed: $module -> $target_repo"
    
    # Cleanup
    rm -rf "$split_dir"
}

# Update POM for standalone operation
update_pom_for_standalone() {
    local group_id="$1"
    local artifact_id="$2"
    
    # Create a temporary Python script for POM updating
    cat > update_pom.py << EOF
import xml.etree.ElementTree as ET
import sys

# Parse POM
tree = ET.parse('pom.xml')
root = tree.getroot()

# Define namespace
ns = {'maven': 'http://maven.apache.org/POM/4.0.0'}

# Update parent reference to java-eda/base
parent = root.find('maven:parent', ns)
if parent is not None:
    parent.find('maven:groupId', ns).text = 'org.acmsl.javaeda'
    parent.find('maven:artifactId', ns).text = 'base-pom'
    parent.find('maven:version', ns).text = 'latest-SNAPSHOT'

# Update artifact information if needed
groupId_elem = root.find('maven:groupId', ns)
if groupId_elem is not None:
    groupId_elem.text = '$group_id'

artifactId_elem = root.find('maven:artifactId', ns)
if artifactId_elem is not None:
    artifactId_elem.text = '$artifact_id'

# Write back to file
tree.write('pom.xml', encoding='utf-8', xml_declaration=True)
EOF
    
    # Apply POM updates
    python3 update_pom.py 2>/dev/null || {
        log_warning "Could not automatically update POM for $artifact_id"
    }
    
    rm -f update_pom.py
}

# Create repository-specific README
create_repository_readme() {
    local module="$1"
    local target_repo="$2"
    local component_name=$(echo "$module" | sed 's/.*-//' | sed 's/^./\U&/')
    
    cat > README.md << EOF
# ByteHot $component_name

Professional Java hot-swapping technology - $component_name module.

## Overview

This repository contains the $component_name component of ByteHot, extracted from the original monorepo for independent development and distribution.

## Quick Start

### Maven Dependency

\`\`\`xml
<dependency>
    <groupId>org.acmsl.bytehot</groupId>
    <artifactId>$target_repo</artifactId>
    <version>latest-SNAPSHOT</version>
</dependency>
\`\`\`

### Building from Source

\`\`\`bash
git clone https://github.com/$target_repo.git
cd ${target_repo#*/}
mvn clean install
\`\`\`

## Documentation

- [ByteHot Learning Academy](https://github.com/rydnr/bytehot/blob/main/DEVELOPER_JOURNEY.md)
- [Course 16: Repository Architecture](https://github.com/rydnr/bytehot/tree/main/courses/16-repository-architecture-decomposition)
- [API Documentation](docs/)

## Contributing

1. Fork the repository
2. Create a feature branch: \`git checkout -b feature/amazing-feature\`
3. Commit your changes: \`git commit -m 'Add amazing feature'\`
4. Push to the branch: \`git push origin feature/amazing-feature\`
5. Open a Pull Request

## License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE) file for details.

## Repository Split Information

This repository was created by splitting the ByteHot monorepo on $(date +%Y-%m-%d).
Original module path: \`$module/\`

For the complete development history and context, see the [ByteHot Development Journal](https://github.com/rydnr/bytehot/blob/main/journal.org).

---

**Part of the ByteHot ecosystem** - The future of Java hot-swapping technology.
EOF
}

# Distribute journal content
distribute_journal() {
    log_info "Distributing journal content across repositories..."
    
    # This is a placeholder for journal distribution logic
    # In a real implementation, this would:
    # 1. Parse journal.org
    # 2. Extract sections relevant to each repository
    # 3. Create repository-specific journal files
    # 4. Maintain cross-references
    
    log_info "Journal distribution logic would be implemented here"
    log_info "This involves parsing journal.org and distributing content by module relevance"
}

# Validate split results
validate_split() {
    log_info "Validating split results..."
    
    local validation_report="validation-report.md"
    echo "# Monorepo Split Validation Report" > "$validation_report"
    echo "" >> "$validation_report"
    echo "Generated on: $(date)" >> "$validation_report"
    echo "" >> "$validation_report"
    
    for mapping in "${REPO_MAPPINGS[@]}"; do
        local target_repo="${mapping#*:}"
        target_repo="${target_repo%%:*}"
        
        echo "## $target_repo" >> "$validation_report"
        
        # Check if repository exists and is accessible
        if gh repo view "$target_repo" &> /dev/null; then
            echo "- ✅ Repository exists and is accessible" >> "$validation_report"
            
            # Check if it has commits
            local commit_count
            commit_count=$(gh api "repos/$target_repo/commits" --jq 'length')
            echo "- ✅ Contains $commit_count commits" >> "$validation_report"
        else
            echo "- ❌ Repository not found or not accessible" >> "$validation_report"
        fi
        
        echo "" >> "$validation_report"
    done
    
    log_success "Validation report created: $validation_report"
}

# Create coordination documentation
create_coordination_docs() {
    log_info "Creating multi-repository coordination documentation..."
    
    cat > multi-repo-coordination.md << 'EOF'
# Multi-Repository Coordination Guide

## Repository Structure

After the monorepo split, ByteHot is organized into two GitHub organizations:

### java-eda Organization
- `java-eda/base` - Parent POM with dependency versions
- `java-eda/shared` - Core utilities (was java-commons)
- `java-eda/shared-infrastructure` - Infrastructure utilities
- `java-eda/domain` - JavaEDA domain framework
- `java-eda/infrastructure` - JavaEDA infrastructure framework
- `java-eda/application` - JavaEDA application framework

### bytehot Organization
- `bytehot/domain` - ByteHot domain logic
- `bytehot/application` - ByteHot application layer
- `bytehot/infrastructure` - ByteHot infrastructure adapters
- `bytehot/plugin-commons` - Shared plugin utilities
- Plugin repositories for various IDE and build tool integrations

## Development Workflow

### 1. Dependency Updates
- Central version management through `java-eda/base`
- Automated dependency updates via Dependabot
- Coordinated releases using semantic versioning

### 2. Cross-Repository Changes
- Use feature branches across related repositories
- Coordinate pull requests for dependent changes
- Maintain backward compatibility during transitions

### 3. Release Coordination
- Base repositories release first (java-eda/*)
- Application layer repositories follow
- Plugin repositories release last

## Automation

### GitHub Actions Workflows
Each repository includes:
- Build and test automation
- Dependency update coordination
- Release automation with semantic versioning
- Cross-repository notification systems

### Dependency Management
- Parent POM version management
- Automated version updates
- Compatibility matrix maintenance
- Breaking change coordination

## Communication

### Repository Owners
- Clear ownership assignment for each repository
- Cross-functional team coordination
- Regular sync meetings for dependent repositories

### Documentation
- Repository-specific documentation
- Cross-reference maintenance
- Centralized learning resources

## Governance

### Access Control
- Organization-level permissions
- Repository-specific access controls
- Security policy enforcement

### Quality Standards
- Consistent code quality across repositories
- Shared CI/CD pipeline standards
- Common testing and documentation requirements
EOF

    log_success "Multi-repository coordination documentation created"
}

# Main execution function
main() {
    log_info "Starting ByteHot monorepo split automation"
    log_info "This process will take approximately 30-60 minutes"
    
    # Validate prerequisites
    check_prerequisites
    
    # Initialize workspace
    initialize_workspace
    
    # Clone and analyze source repository
    clone_source_repository
    analyze_dependencies
    
    # Process each repository mapping
    for mapping in "${REPO_MAPPINGS[@]}"; do
        split_repository "$mapping"
    done
    
    # Create coordination documentation
    create_coordination_docs
    
    # Distribute journal content (placeholder)
    distribute_journal
    
    # Validate results
    validate_split
    
    log_success "Monorepo split automation completed successfully!"
    log_info "Review the validation report and coordination documentation"
    log_info "Next steps:"
    log_info "1. Set up Maven Central publishing (Course 17)"
    log_info "2. Configure cross-repository automation"
    log_info "3. Establish development workflows"
    log_info "4. Train team on multi-repository practices"
}

# Script execution
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    main "$@"
fi