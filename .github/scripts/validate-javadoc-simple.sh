#!/bin/bash
set -euo pipefail

# Simple Javadoc Validation Script for ByteHot
# Uses Maven javadoc plugin with strict checking to detect all issues

echo "🔍 Validating Javadoc across all ByteHot modules"
echo "==============================================="

TEMP_LOG=$(mktemp)
ISSUES_FOUND=0

# Function to run javadoc on individual modules
validate_module() {
    local module="$1"
    echo "📋 Validating module: $module"
    
    if [[ -d "$module" ]]; then
        cd "$module"
        
        # Run javadoc with strict checking
        if mvn javadoc:javadoc \
            -DfailOnError=true \
            -DfailOnWarnings=true \
            -Dadditionalparam="-Xdoclint:all" \
            -Dmaven.javadoc.skip=false \
            -Dquiet=false \
            > "../$TEMP_LOG" 2>&1; then
            echo "✅ $module - No javadoc issues"
        else
            echo "❌ $module - Javadoc issues found"
            echo "Issues in $module:" >> "../javadoc-issues.txt"
            grep -E "(warning|error).*:" "../$TEMP_LOG" | head -20 >> "../javadoc-issues.txt" || true
            echo "" >> "../javadoc-issues.txt"
            ISSUES_FOUND=$((ISSUES_FOUND + 1))
        fi
        
        cd ..
    fi
}

# Clean previous reports
rm -f javadoc-issues.txt

# Validate each module
for module in java-commons bytehot-domain bytehot-application bytehot-infrastructure; do
    validate_module "$module"
done

echo ""
echo "📊 Aggregate Validation"
echo "======================"

# Try aggregate javadoc as well
echo "📋 Running aggregate javadoc validation..."
if mvn javadoc:aggregate \
    -DfailOnError=true \
    -DfailOnWarnings=true \
    -Dadditionalparam="-Xdoclint:all" \
    -Dmaven.javadoc.skip=false \
    -Dquiet=false \
    > "$TEMP_LOG" 2>&1; then
    echo "✅ Aggregate javadoc validation passed"
else
    echo "❌ Aggregate javadoc validation failed"
    echo "Aggregate validation issues:" >> "javadoc-issues.txt"
    grep -E "(warning|error).*:" "$TEMP_LOG" | head -20 >> "javadoc-issues.txt" || true
    ISSUES_FOUND=$((ISSUES_FOUND + 1))
fi

# Display results
echo ""
echo "📈 Results Summary"
echo "=================="

if [[ -f "javadoc-issues.txt" ]] && [[ -s "javadoc-issues.txt" ]]; then
    echo "❌ Javadoc issues found:"
    echo ""
    cat "javadoc-issues.txt"
    
    total_issues=$(grep -c ":" "javadoc-issues.txt" || echo "0")
    echo ""
    echo "Total javadoc issues: $total_issues"
    echo ""
    echo "🔧 Fix these issues before committing to avoid GitHub Actions failures"
    
    # Cleanup
    rm -f "$TEMP_LOG" "javadoc-issues.txt"
    exit 1
else
    echo "✅ No javadoc issues found! All modules pass validation."
    
    # Cleanup
    rm -f "$TEMP_LOG" "javadoc-issues.txt"
    exit 0
fi