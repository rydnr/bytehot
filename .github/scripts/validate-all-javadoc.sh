#!/bin/bash
set -euo pipefail

# Comprehensive Javadoc Validation for ByteHot
# Detects ALL javadoc issues using the exact same validation as GitHub Actions

echo "🔍 Comprehensive Javadoc Validation for ByteHot"
echo "==============================================="

TEMP_DIR=$(mktemp -d)
ISSUES_FILE="$TEMP_DIR/all-issues.txt"
TOTAL_ISSUES=0

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo "📋 Running Maven javadoc with strict doclint validation..."

# Strategy 1: Try aggregate javadoc with full doclint
echo "🔍 Strategy 1: Maven aggregate javadoc with -Xdoclint:all..."
if mvn javadoc:aggregate \
    -DfailOnError=true \
    -DfailOnWarnings=true \
    -Dadditionalparam="-Xdoclint:all" \
    -Dmaven.javadoc.skip=false \
    -Dquiet=false \
    > "$TEMP_DIR/aggregate.log" 2>&1; then
    echo "✅ Aggregate javadoc passed"
else
    echo "❌ Aggregate javadoc failed"
    grep -E "(warning|error).*:" "$TEMP_DIR/aggregate.log" >> "$ISSUES_FILE" 2>/dev/null || true
fi

# Strategy 2: Individual module validation with strict settings
echo ""
echo "🔍 Strategy 2: Individual module validation..."

for module in java-commons bytehot-domain bytehot-infrastructure bytehot-application; do
    if [[ -d "$module" ]]; then
        echo "   Validating $module..."
        
        (cd "$module" && mvn javadoc:javadoc \
            -DfailOnError=true \
            -DfailOnWarnings=true \
            -Dadditionalparam="-Xdoclint:all" \
            -Dmaven.javadoc.skip=false \
            -Dquiet=false \
            > "../$TEMP_DIR/$module.log" 2>&1) || true
            
        # Extract issues from this module
        if [[ -f "$TEMP_DIR/$module.log" ]]; then
            grep -E "(warning|error).*:" "$TEMP_DIR/$module.log" >> "$ISSUES_FILE" 2>/dev/null || true
        fi
    fi
done

# Strategy 3: Direct javadoc command (most strict)
echo ""
echo "🔍 Strategy 3: Direct javadoc command validation..."

# Find all source directories
SOURCE_PATHS=""
for module in java-commons bytehot-domain bytehot-infrastructure bytehot-application; do
    if [[ -d "$module/src/main/java" ]]; then
        if [[ -z "$SOURCE_PATHS" ]]; then
            SOURCE_PATHS="$module/src/main/java"
        else
            SOURCE_PATHS="$SOURCE_PATHS:$module/src/main/java"
        fi
    fi
done

# Build classpath
CLASSPATH=""
for module in java-commons bytehot-domain bytehot-infrastructure bytehot-application; do
    if [[ -d "$module/target/classes" ]]; then
        if [[ -z "$CLASSPATH" ]]; then
            CLASSPATH="$module/target/classes"
        else
            CLASSPATH="$CLASSPATH:$module/target/classes"
        fi
    fi
done

# Add Maven dependencies to classpath
if command -v mvn >/dev/null 2>&1; then
    DEP_CLASSPATH=$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout 2>/dev/null || echo "")
    if [[ -n "$DEP_CLASSPATH" ]]; then
        CLASSPATH="$CLASSPATH:$DEP_CLASSPATH"
    fi
fi

# Run direct javadoc command
if [[ -n "$SOURCE_PATHS" ]]; then
    javadoc \
        -d "$TEMP_DIR/direct-javadoc" \
        -sourcepath "$SOURCE_PATHS" \
        -subpackages org.acmsl \
        -cp "$CLASSPATH" \
        -Xdoclint:all \
        -private \
        > "$TEMP_DIR/direct.log" 2>&1 || true
        
    # Extract issues from direct javadoc
    grep -E "(warning|error).*:" "$TEMP_DIR/direct.log" >> "$ISSUES_FILE" 2>/dev/null || true
fi

# Process and deduplicate issues
echo ""
echo "📊 Processing Results..."

if [[ -f "$ISSUES_FILE" ]] && [[ -s "$ISSUES_FILE" ]]; then
    # Remove duplicates and sort
    sort -u "$ISSUES_FILE" > "$TEMP_DIR/unique-issues.txt"
    
    echo -e "${RED}❌ Javadoc Issues Found:${NC}"
    echo "=========================="
    
    # Group issues by type
    echo ""
    echo -e "${YELLOW}Missing Comments:${NC}"
    grep "no comment" "$TEMP_DIR/unique-issues.txt" || echo "   None"
    
    echo ""
    echo -e "${YELLOW}Missing @param Annotations:${NC}"
    grep "no @param" "$TEMP_DIR/unique-issues.txt" || echo "   None"
    
    echo ""
    echo -e "${YELLOW}Missing @return Annotations:${NC}"
    grep "no @return" "$TEMP_DIR/unique-issues.txt" || echo "   None"
    
    echo ""
    echo -e "${YELLOW}Other Issues:${NC}"
    grep -v "no comment\|no @param\|no @return" "$TEMP_DIR/unique-issues.txt" || echo "   None"
    
    echo ""
    echo -e "${BLUE}📈 Summary:${NC}"
    echo "   Missing comments: $(grep -c "no comment" "$TEMP_DIR/unique-issues.txt" || echo "0")"
    echo "   Missing @param: $(grep -c "no @param" "$TEMP_DIR/unique-issues.txt" || echo "0")"
    echo "   Missing @return: $(grep -c "no @return" "$TEMP_DIR/unique-issues.txt" || echo "0")"
    echo "   Other issues: $(grep -c -v "no comment\|no @param\|no @return" "$TEMP_DIR/unique-issues.txt" || echo "0")"
    
    TOTAL_ISSUES=$(wc -l < "$TEMP_DIR/unique-issues.txt")
    echo "   Total unique issues: $TOTAL_ISSUES"
    
    echo ""
    echo -e "${RED}💥 Full Issue List:${NC}"
    echo "==================="
    cat "$TEMP_DIR/unique-issues.txt"
    
else
    echo -e "${GREEN}✅ No javadoc issues found!${NC}"
fi

# Cleanup
rm -rf "$TEMP_DIR"

if [[ $TOTAL_ISSUES -gt 0 ]]; then
    echo ""
    echo -e "${RED}🔧 Fix these $TOTAL_ISSUES issue(s) before pushing to avoid GitHub Actions failures${NC}"
    exit 1
else
    echo ""
    echo -e "${GREEN}🎉 All javadoc validation checks passed!${NC}"
    exit 0
fi