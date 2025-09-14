# Module 01: Monorepo Analysis & Decomposition Planning

**Duration**: 90 minutes  
**Difficulty**: Expert  
**Prerequisites**: Advanced Git knowledge, Maven multi-module experience

## Learning Objectives

By the end of this module, you will be able to:
- Analyze complex monorepo structures to identify optimal decomposition boundaries
- Create comprehensive dependency maps showing inter-module relationships
- Design decomposition strategies that preserve development velocity
- Plan repository splits that minimize coordination overhead
- Design validation frameworks for split operations

## Overview

Monorepo decomposition is both an art and a science. This module provides the analytical framework and practical tools to plan successful repository splits that enhance rather than hinder development productivity.

## 1. Monorepo Analysis Framework

### 1.1 Structural Assessment

Before any decomposition, we must understand the current structure:

#### Module Boundary Analysis
```bash
# Analyze module structure
find . -name "pom.xml" -not -path "./target/*" | head -20
```

For ByteHot, this reveals:
- `/pom.xml` - Parent POM with dependency management
- `/java-commons/pom.xml` - Shared utilities
- `/java-commons-infrastructure/pom.xml` - Infrastructure utilities  
- `/javaeda-domain/pom.xml` - Domain framework
- `/javaeda-infrastructure/pom.xml` - Infrastructure framework
- `/javaeda-application/pom.xml` - Application framework
- `/bytehot-domain/pom.xml` - ByteHot core domain
- `/bytehot-application/pom.xml` - ByteHot application layer
- `/bytehot-infrastructure/pom.xml` - ByteHot infrastructure

#### Dependency Visualization

Create a dependency graph to understand relationships:

```bash
# Maven dependency tree analysis
mvn dependency:tree -DoutputFile=dependencies.txt
mvn dependency:analyze -DoutputFile=dependency-analysis.txt
```

### 1.2 Code Ownership Mapping

Identify natural boundaries based on team ownership:

```bash
# Analyze commit patterns by directory
git log --format="%ae %f" --name-only | \
  grep -E "\.(java|xml|yml)$" | \
  sort | uniq -c | sort -nr | head -50
```

### 1.3 Change Frequency Analysis

Understand which modules change together:

```bash
# Find files that frequently change together
git log --format="" --name-only | \
  grep -E "\.(java|xml)$" | \
  sort | uniq -c | sort -nr | head -20
```

## 2. Dependency Mapping

### 2.1 Direct Dependencies

Map direct Maven dependencies between modules:

```xml
<!-- Example: bytehot-application depends on bytehot-domain -->
<dependency>
    <groupId>org.acmsl.bytehot</groupId>
    <artifactId>bytehot-domain</artifactId>
    <version>${project.version}</version>
</dependency>
```

### 2.2 Circular Dependency Detection

Identify and plan resolution for circular dependencies:

```bash
# Use Maven dependency plugin to detect cycles
mvn dependency:analyze-dep-mgt | grep -i cycle
```

### 2.3 ByteHot Dependency Analysis

Based on the monorepo structure:

```
java-commons (Foundation)
├── java-commons-infrastructure (extends java-commons)
└── javaeda-domain (depends on java-commons)
    ├── javaeda-infrastructure (extends javaeda-domain)
    ├── javaeda-application (extends javaeda-domain)
    └── bytehot-domain (extends javaeda-domain)
        ├── bytehot-application (extends javaeda-application + bytehot-domain)
        └── bytehot-infrastructure (extends javaeda-infrastructure + bytehot-domain)
```

## 3. Split Strategy Design

### 3.1 Repository Organization Strategy

For ByteHot, we'll create two GitHub organizations:

#### java-eda Organization
- `java-eda/base` - Parent POM with dependency versions
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
- Plugin repositories for IDE and build tool integrations

### 3.2 Split Order Planning

Dependencies dictate split order:

1. **Foundation Layer** (java-eda/base, java-eda/shared)
2. **Framework Layer** (java-eda/domain, java-eda/infrastructure, java-eda/application)
3. **Application Layer** (bytehot/domain, bytehot/application, bytehot/infrastructure)
4. **Plugin Layer** (bytehot/plugin-commons, specific plugins)

### 3.3 Transition Strategy

#### Phase 1: Preparation
- Create GitHub organizations
- Set up base repository with parent POM
- Prepare automation scripts

#### Phase 2: Foundation Split
- Split and migrate java-commons modules
- Update dependency references
- Validate builds

#### Phase 3: Framework Split
- Split JavaEDA framework modules
- Update parent POM references
- Coordinate version releases

#### Phase 4: Application Split
- Split ByteHot core modules
- Update cross-repository dependencies
- Establish CI/CD pipelines

#### Phase 5: Plugin Split
- Split plugin modules
- Set up plugin-specific workflows
- Configure release coordination

## 4. Hands-On Exercise: ByteHot Analysis

### Exercise 4.1: Dependency Mapping

1. Clone the ByteHot repository
2. Generate dependency tree for each module
3. Create visual dependency map
4. Identify potential circular dependencies

```bash
# Complete dependency analysis
for module in java-commons* javaeda-* bytehot-*; do
  if [ -d "$module" ]; then
    echo "=== $module ==="
    cd "$module"
    mvn dependency:tree -DoutputType=dot -DoutputFile="../$module-deps.dot"
    cd ..
  fi
done
```

### Exercise 4.2: Change Pattern Analysis

Analyze git history to understand change patterns:

```bash
# Find modules that change together frequently
git log --format="" --name-only --since="6 months ago" | \
  grep -E "^(java-commons|javaeda|bytehot)" | \
  cut -d'/' -f1 | sort | uniq -c | sort -nr
```

### Exercise 4.3: Split Plan Creation

Create a detailed split plan including:

1. **Repository mapping table**
2. **Dependency coordination strategy**  
3. **Timeline with milestones**
4. **Risk assessment and mitigation**
5. **Rollback procedures**

## 5. Validation Framework

### 5.1 Pre-Split Validation

Before splitting, validate the plan:

```bash
# Validate module independence
for module in bytehot-domain bytehot-application bytehot-infrastructure; do
  echo "Testing $module independence..."
  cd "$module"
  mvn clean compile -DskipTests
  cd ..
done
```

### 5.2 Post-Split Validation

After splitting, ensure everything works:

1. **Build Validation**: Each repository builds independently
2. **Dependency Validation**: Cross-repository dependencies resolve
3. **Test Validation**: All tests pass in new structure
4. **Integration Validation**: End-to-end workflows function

### 5.3 Continuous Validation

Ongoing validation through automation:

```yaml
# GitHub Action for cross-repository validation
name: Cross-Repository Validation
on:
  repository_dispatch:
    types: [dependency-update]

jobs:
  validate:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout dependent repositories
        uses: actions/checkout@v3
        with:
          repository: bytehot/application
          token: ${{ secrets.CROSS_REPO_TOKEN }}
      
      - name: Update dependency and test
        run: |
          # Update dependency version
          # Run integration tests
          # Report results
```

## 6. Tools and Automation

### 6.1 Analysis Tools

- **git-filter-repo**: History-preserving repository splitting
- **Maven Dependency Plugin**: Dependency analysis
- **jdeps**: Java dependency analysis
- **Graphviz**: Dependency visualization

### 6.2 Automation Scripts

The module includes these automation scripts:

1. `dependency-analyzer.sh` - Comprehensive dependency analysis
2. `change-pattern-analyzer.sh` - Git history analysis  
3. `split-validator.sh` - Pre/post-split validation
4. `repository-mapper.sh` - Repository mapping generation

## 7. Common Pitfalls and Solutions

### 7.1 Circular Dependencies

**Problem**: Modules depend on each other creating cycles
**Solution**: Dependency inversion, interface extraction

### 7.2 Shared Code Management

**Problem**: Code used across multiple modules
**Solution**: Extract to shared libraries with clear interfaces

### 7.3 Version Synchronization

**Problem**: Coordinating versions across repositories
**Solution**: Parent POM strategy with centralized version management

### 7.4 Build Orchestration

**Problem**: Building dependent repositories in correct order
**Solution**: Semantic versioning with automated dependency updates

## Module Assessment

### Quiz Questions

1. What are the key factors to consider when determining repository boundaries?
2. How do you detect circular dependencies in a Maven multi-module project?
3. What is the recommended split order for ByteHot repositories and why?
4. How would you validate that a repository split was successful?

### Practical Exercise

**Scenario**: You have a monorepo with 15 modules and complex dependencies. Design a complete decomposition strategy including:

1. Dependency analysis results
2. Repository organization plan
3. Split timeline with dependencies
4. Validation strategy
5. Risk mitigation plan

**Deliverable**: Comprehensive decomposition plan document with supporting analysis

## Next Steps

In Module 02, we'll dive deep into git history preservation techniques, learning how to use git-filter-repo to split repositories while maintaining complete commit history and metadata.

---

**Continue to Module 02: Git History Preservation Techniques →**