#!/bin/bash
set -euo pipefail

# ByteHot Javadoc Checker - Direct and Simple
# Replicates the exact GitHub Actions javadoc validation

echo "🔍 ByteHot Javadoc Validation"
echo "============================="

# Run the exact same javadoc commands as GitHub Actions
echo "📋 Running Maven javadoc aggregate with strict validation..."

# First try: Maven aggregate with fail on warnings
echo "Strategy 1: Maven aggregate javadoc..."
if mvn javadoc:aggregate \
    -DfailOnError=true \
    -DfailOnWarnings=true \
    -Dadditionalparam="-Xdoclint:all" \
    -Dmaven.javadoc.skip=false \
    2>&1 | tee /tmp/javadoc-output.log; then
    
    echo "✅ Maven aggregate javadoc passed"
    AGGREGATE_PASSED=true
else
    echo "❌ Maven aggregate javadoc failed"
    AGGREGATE_PASSED=false
fi

# Extract and display warnings/errors
echo ""
echo "📊 Javadoc Issues Found:"
echo "========================"

if grep -E "(warning|error).*:" /tmp/javadoc-output.log > /tmp/javadoc-issues.txt 2>/dev/null; then
    echo "❌ Issues detected:"
    cat /tmp/javadoc-issues.txt
    
    echo ""
    echo "📈 Summary:"
    echo "   Missing @param: $(grep -c "no @param" /tmp/javadoc-issues.txt || echo "0")"
    echo "   Missing @return: $(grep -c "no @return" /tmp/javadoc-issues.txt || echo "0")"
    echo "   Other warnings: $(grep -c -v "@param\|@return" /tmp/javadoc-issues.txt || echo "0")"
    echo "   Total issues: $(wc -l < /tmp/javadoc-issues.txt)"
    
    # Cleanup
    rm -f /tmp/javadoc-output.log /tmp/javadoc-issues.txt
    
    echo ""
    echo "🔧 Fix these issues before pushing to avoid GitHub Actions failures"
    exit 1
else
    echo "✅ No javadoc issues found!"
    
    # Cleanup
    rm -f /tmp/javadoc-output.log /tmp/javadoc-issues.txt
    
    echo ""
    echo "🎉 All javadoc validation checks passed!"
    exit 0
fi