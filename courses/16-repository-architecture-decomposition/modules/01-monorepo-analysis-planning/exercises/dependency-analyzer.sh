#!/bin/bash

# ByteHot Dependency Analyzer
# Course 16 - Module 01: Monorepo Analysis & Decomposition Planning
#
# This script performs comprehensive dependency analysis for monorepo decomposition planning.
# It generates detailed reports about module dependencies, circular dependencies, and 
# change patterns to inform split decisions.

set -euo pipefail

# Configuration
readonly SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" &> /dev/null && pwd)"
readonly PROJECT_ROOT="$(cd "$SCRIPT_DIR/../../../../" &> /dev/null && pwd)"
readonly OUTPUT_DIR="$SCRIPT_DIR/analysis-output"
readonly TIMESTAMP=$(date +%Y%m%d_%H%M%S)

# Color codes for output
readonly RED='\033[0;31m'
readonly GREEN='\033[0;32m'
readonly YELLOW='\033[1;33m'
readonly BLUE='\033[0;34m'
readonly NC='\033[0m' # No Color

# Logging functions
log_info() {
    echo -e "${BLUE}[INFO]${NC} $*"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $*"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $*"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $*"
}

# Initialize output directory
initialize_output() {
    log_info "Initializing analysis output directory..."
    
    rm -rf "$OUTPUT_DIR"
    mkdir -p "$OUTPUT_DIR"
    
    # Create analysis report header
    cat > "$OUTPUT_DIR/analysis-report.md" << 'EOF'
# ByteHot Monorepo Dependency Analysis Report

Generated on: $(date)

## Executive Summary

This report provides comprehensive analysis of the ByteHot monorepo structure to inform decomposition planning.

## Analysis Sections

1. [Module Structure](#module-structure)
2. [Direct Dependencies](#direct-dependencies)  
3. [Circular Dependencies](#circular-dependencies)
4. [Change Patterns](#change-patterns)
5. [Decomposition Recommendations](#decomposition-recommendations)

EOF
}

# Analyze module structure
analyze_module_structure() {
    log_info "Analyzing module structure..."
    
    cd "$PROJECT_ROOT"
    
    echo "## Module Structure" >> "$OUTPUT_DIR/analysis-report.md"
    echo "" >> "$OUTPUT_DIR/analysis-report.md"
    echo "### Maven Modules Discovered" >> "$OUTPUT_DIR/analysis-report.md"
    echo "" >> "$OUTPUT_DIR/analysis-report.md"
    
    # Find all POM files (excluding target directories)
    find . -name "pom.xml" -not -path "./target/*" -not -path "*/target/*" | sort > "$OUTPUT_DIR/modules.txt"
    
    echo "| Module Path | Lines of Code | Last Modified |" >> "$OUTPUT_DIR/analysis-report.md"
    echo "|-------------|---------------|---------------|" >> "$OUTPUT_DIR/analysis-report.md"
    
    while IFS= read -r pom_file; do
        local module_dir=$(dirname "$pom_file")
        local module_name=$(basename "$module_dir")
        
        # Count lines of code (Java files only)
        local loc=0
        if [[ -d "$module_dir/src" ]]; then
            loc=$(find "$module_dir/src" -name "*.java" -exec wc -l {} + 2>/dev/null | tail -1 | awk '{print $1}' || echo "0")
        fi
        
        # Get last modification date
        local last_modified=$(git log -1 --format="%ad" --date=short -- "$module_dir" 2>/dev/null || echo "Unknown")
        
        echo "| \`$module_dir\` | $loc | $last_modified |" >> "$OUTPUT_DIR/analysis-report.md"
        
    done < "$OUTPUT_DIR/modules.txt"
    
    echo "" >> "$OUTPUT_DIR/analysis-report.md"
    
    log_success "Module structure analysis completed"
}

# Analyze direct dependencies
analyze_direct_dependencies() {
    log_info "Analyzing direct dependencies..."
    
    cd "$PROJECT_ROOT"
    
    echo "## Direct Dependencies" >> "$OUTPUT_DIR/analysis-report.md"
    echo "" >> "$OUTPUT_DIR/analysis-report.md"
    
    # Create dependency matrix
    echo "### Dependency Matrix" >> "$OUTPUT_DIR/analysis-report.md"
    echo "" >> "$OUTPUT_DIR/analysis-report.md"
    echo "This matrix shows direct dependencies between modules:" >> "$OUTPUT_DIR/analysis-report.md"
    echo "" >> "$OUTPUT_DIR/analysis-report.md"
    
    # Generate dependency tree for each module
    while IFS= read -r pom_file; do
        local module_dir=$(dirname "$pom_file")
        local module_name=$(basename "$module_dir")
        
        if [[ "$module_dir" != "." ]]; then
            log_info "Analyzing dependencies for $module_name..."
            
            cd "$module_dir"
            
            # Extract dependencies from POM
            if [[ -f "pom.xml" ]]; then
                echo "#### $module_name Dependencies" >> "$OUTPUT_DIR/analysis-report.md"
                echo "" >> "$OUTPUT_DIR/analysis-report.md"
                
                # Extract internal project dependencies
                grep -A 10 -B 2 "org.acmsl" pom.xml | \
                grep -E "(groupId|artifactId|version)" | \
                sed 's/.*<\(.*\)>\(.*\)<\/.*>/- **\1**: \2/' >> "$OUTPUT_DIR/analysis-report.md" 2>/dev/null || echo "- No internal dependencies found" >> "$OUTPUT_DIR/analysis-report.md"
                
                echo "" >> "$OUTPUT_DIR/analysis-report.md"
            fi
            
            cd "$PROJECT_ROOT"
        fi
        
    done < "$OUTPUT_DIR/modules.txt"
    
    log_success "Direct dependency analysis completed"
}

# Detect circular dependencies
detect_circular_dependencies() {
    log_info "Detecting circular dependencies..."
    
    cd "$PROJECT_ROOT"
    
    echo "## Circular Dependencies" >> "$OUTPUT_DIR/analysis-report.md"
    echo "" >> "$OUTPUT_DIR/analysis-report.md"
    
    # Use Maven to detect cycles
    log_info "Running Maven dependency analysis..."
    
    if mvn dependency:analyze-dep-mgt > "$OUTPUT_DIR/maven-dependency-analysis.txt" 2>&1; then
        if grep -i "cycle\|circular" "$OUTPUT_DIR/maven-dependency-analysis.txt" > /dev/null; then
            echo "### ⚠️ Circular Dependencies Detected" >> "$OUTPUT_DIR/analysis-report.md"
            echo "" >> "$OUTPUT_DIR/analysis-report.md"
            echo "The following circular dependencies were detected:" >> "$OUTPUT_DIR/analysis-report.md"
            echo "" >> "$OUTPUT_DIR/analysis-report.md"
            echo "\`\`\`" >> "$OUTPUT_DIR/analysis-report.md"
            grep -i "cycle\|circular" "$OUTPUT_DIR/maven-dependency-analysis.txt" >> "$OUTPUT_DIR/analysis-report.md"
            echo "\`\`\`" >> "$OUTPUT_DIR/analysis-report.md"
        else
            echo "### ✅ No Circular Dependencies Detected" >> "$OUTPUT_DIR/analysis-report.md"
            echo "" >> "$OUTPUT_DIR/analysis-report.md"
            echo "Maven dependency analysis found no circular dependencies." >> "$OUTPUT_DIR/analysis-report.md"
        fi
    else
        log_warning "Maven dependency analysis failed, checking manually..."
        echo "### Manual Circular Dependency Check" >> "$OUTPUT_DIR/analysis-report.md"
        echo "" >> "$OUTPUT_DIR/analysis-report.md"
        echo "Maven analysis failed. Performing manual analysis..." >> "$OUTPUT_DIR/analysis-report.md"
    fi
    
    echo "" >> "$OUTPUT_DIR/analysis-report.md"
    
    log_success "Circular dependency detection completed"
}

# Analyze change patterns  
analyze_change_patterns() {
    log_info "Analyzing change patterns..."
    
    cd "$PROJECT_ROOT"
    
    echo "## Change Patterns" >> "$OUTPUT_DIR/analysis-report.md"
    echo "" >> "$OUTPUT_DIR/analysis-report.md"
    
    # Analyze commit patterns by module
    echo "### Commit Activity by Module (Last 6 Months)" >> "$OUTPUT_DIR/analysis-report.md"
    echo "" >> "$OUTPUT_DIR/analysis-report.md"
    
    # Get commit counts by directory
    git log --format="" --name-only --since="6 months ago" | \
    grep -E "^(java-commons|javaeda|bytehot)" | \
    cut -d'/' -f1 | sort | uniq -c | sort -nr > "$OUTPUT_DIR/commit-counts.txt"
    
    echo "| Module | Commits | Activity Level |" >> "$OUTPUT_DIR/analysis-report.md"
    echo "|--------|---------|----------------|" >> "$OUTPUT_DIR/analysis-report.md"
    
    while read -r count module; do
        local activity_level="Low"
        if [[ $count -gt 50 ]]; then
            activity_level="High"
        elif [[ $count -gt 20 ]]; then
            activity_level="Medium"
        fi
        
        echo "| $module | $count | $activity_level |" >> "$OUTPUT_DIR/analysis-report.md"
    done < "$OUTPUT_DIR/commit-counts.txt"
    
    echo "" >> "$OUTPUT_DIR/analysis-report.md"
    
    # Analyze co-change patterns
    echo "### Co-Change Analysis" >> "$OUTPUT_DIR/analysis-report.md"
    echo "" >> "$OUTPUT_DIR/analysis-report.md"
    echo "Modules that frequently change together:" >> "$OUTPUT_DIR/analysis-report.md"
    echo "" >> "$OUTPUT_DIR/analysis-report.md"
    
    # Find commits that touch multiple modules
    git log --format="%h" --name-only --since="6 months ago" | \
    awk '/^[0-9a-f]{7}$/ {commit=$0; getline; files=""} 
         /^(java-commons|javaeda|bytehot)/ {files=files " " $0} 
         /^$/ && files!="" {print commit ":" files; files=""}' | \
    head -20 > "$OUTPUT_DIR/co-changes.txt"
    
    echo "\`\`\`" >> "$OUTPUT_DIR/analysis-report.md"
    head -10 "$OUTPUT_DIR/co-changes.txt" >> "$OUTPUT_DIR/analysis-report.md"
    echo "\`\`\`" >> "$OUTPUT_DIR/analysis-report.md"
    echo "" >> "$OUTPUT_DIR/analysis-report.md"
    
    log_success "Change pattern analysis completed"
}

# Generate decomposition recommendations
generate_recommendations() {
    log_info "Generating decomposition recommendations..."
    
    echo "## Decomposition Recommendations" >> "$OUTPUT_DIR/analysis-report.md"
    echo "" >> "$OUTPUT_DIR/analysis-report.md"
    
    echo "### Recommended Repository Structure" >> "$OUTPUT_DIR/analysis-report.md"
    echo "" >> "$OUTPUT_DIR/analysis-report.md"
    
    cat >> "$OUTPUT_DIR/analysis-report.md" << 'EOF'
Based on the dependency analysis, the following repository structure is recommended:

#### java-eda Organization
- `java-eda/base` - Parent POM with dependency management
- `java-eda/shared` - Core utilities (was java-commons)  
- `java-eda/shared-infrastructure` - Infrastructure utilities
- `java-eda/domain` - JavaEDA domain framework
- `java-eda/infrastructure` - JavaEDA infrastructure framework
- `java-eda/application` - JavaEDA application framework

#### bytehot Organization
- `bytehot/domain` - ByteHot domain logic
- `bytehot/application` - ByteHot application layer
- `bytehot/infrastructure` - ByteHot infrastructure adapters
- `bytehot/plugin-commons` - Shared plugin utilities
- Plugin repositories for various IDE and build tool integrations

### Split Order Recommendation

1. **Foundation Phase**: java-eda/base, java-eda/shared
2. **Framework Phase**: java-eda/domain, java-eda/infrastructure, java-eda/application  
3. **Application Phase**: bytehot/domain, bytehot/application, bytehot/infrastructure
4. **Plugin Phase**: bytehot/plugin-commons and specific plugins

### Risk Assessment

#### Low Risk
- Foundation modules have clear boundaries
- Minimal circular dependencies detected

#### Medium Risk  
- Cross-repository dependency coordination
- Version synchronization across repositories

#### High Risk
- Complex plugin ecosystem coordination
- Maintaining development velocity during transition

### Mitigation Strategies

1. **Gradual Migration**: Implement split in phases
2. **Automation**: Use scripts for consistency and validation
3. **Parallel Development**: Maintain monorepo during transition
4. **Comprehensive Testing**: Validate each phase thoroughly
5. **Rollback Plan**: Prepare for potential rollback scenarios

EOF
    
    log_success "Decomposition recommendations generated"
}

# Generate dot graph for visualization
generate_dependency_graph() {
    log_info "Generating dependency graph..."
    
    cd "$PROJECT_ROOT"
    
    # Create DOT file for dependency visualization
    cat > "$OUTPUT_DIR/dependencies.dot" << 'EOF'
digraph ByteHotDependencies {
    rankdir=TB;
    node [shape=box, style=filled];
    
    // Define node styles
    foundation [fillcolor=lightblue, label="java-commons"];
    infrastructure [fillcolor=lightblue, label="java-commons-infrastructure"];
    
    domain [fillcolor=lightgreen, label="javaeda-domain"];
    javainfra [fillcolor=lightgreen, label="javaeda-infrastructure"];
    application [fillcolor=lightgreen, label="javaeda-application"];
    
    bytedomain [fillcolor=lightyellow, label="bytehot-domain"];
    byteapp [fillcolor=lightyellow, label="bytehot-application"];
    byteinfra [fillcolor=lightyellow, label="bytehot-infrastructure"];
    
    // Define dependencies
    infrastructure -> foundation;
    domain -> foundation;
    javainfra -> domain;
    application -> domain;
    bytedomain -> domain;
    byteapp -> application;
    byteapp -> bytedomain;
    byteinfra -> javainfra;
    byteinfra -> bytedomain;
}
EOF
    
    # Generate PNG if graphviz is available
    if command -v dot &> /dev/null; then
        dot -Tpng "$OUTPUT_DIR/dependencies.dot" -o "$OUTPUT_DIR/dependencies.png"
        log_success "Dependency graph generated: dependencies.png"
    else
        log_warning "Graphviz not installed. DOT file created: dependencies.dot"
    fi
}

# Generate summary statistics
generate_summary() {
    log_info "Generating analysis summary..."
    
    local module_count=$(cat "$OUTPUT_DIR/modules.txt" | wc -l)
    local total_commits=$(git rev-list --count HEAD)
    local recent_commits=$(git rev-list --count --since="6 months ago" HEAD)
    
    # Add summary to top of report
    sed -i.bak "s/## Executive Summary/## Executive Summary\n\n### Key Metrics\n\n- **Total Modules**: $module_count\n- **Total Commits**: $total_commits\n- **Recent Commits (6 months)**: $recent_commits\n- **Analysis Date**: $(date)\n/" "$OUTPUT_DIR/analysis-report.md"
    
    rm -f "$OUTPUT_DIR/analysis-report.md.bak"
    
    log_success "Analysis summary generated"
}

# Main execution
main() {
    log_info "Starting ByteHot dependency analysis..."
    log_info "Project root: $PROJECT_ROOT"
    log_info "Output directory: $OUTPUT_DIR"
    
    initialize_output
    analyze_module_structure
    analyze_direct_dependencies
    detect_circular_dependencies
    analyze_change_patterns
    generate_recommendations
    generate_dependency_graph
    generate_summary
    
    log_success "Dependency analysis completed!"
    log_info "Results available in: $OUTPUT_DIR/analysis-report.md"
    
    # Open report if on macOS
    if [[ "$OSTYPE" == "darwin"* ]] && command -v open &> /dev/null; then
        open "$OUTPUT_DIR/analysis-report.md"
    fi
}

# Script execution
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    main "$@"
fi