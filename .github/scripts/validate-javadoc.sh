#!/bin/bash
set -euo pipefail

# ByteHot Comprehensive Javadoc Validation Script
# Detects all missing @param and @return annotations before GitHub Actions

echo "🔍 Comprehensive Javadoc Validation for ByteHot"
echo "================================================"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

ISSUES_FOUND=0
TEMP_DIR=$(mktemp -d)
REPORT_FILE="$TEMP_DIR/javadoc-issues.txt"

echo "📋 Scanning for javadoc issues across all modules..."

# Function to check a single Java file for javadoc issues
check_file_javadoc() {
    local file="$1"
    local module_name=$(echo "$file" | cut -d'/' -f1)
    
    # Extract all public/protected methods and constructors with their line numbers
    grep -n -A 20 -E "^\s*(public|protected)\s+.*(\(.*\)|class\s+\w+)" "$file" | \
    while IFS=: read -r line_num content; do
        if [[ "$content" =~ ^[[:space:]]*(public|protected)[[:space:]]+.* ]]; then
            # Check if this is a method or constructor
            if [[ "$content" =~ \( ]] && [[ ! "$content" =~ ^[[:space:]]*\/\/ ]] && [[ ! "$content" =~ ^[[:space:]]*\* ]]; then
                # Extract method signature
                method_sig=$(echo "$content" | sed 's/^[[:space:]]*//' | cut -d'{' -f1)
                
                # Check if method has parameters
                if [[ "$method_sig" =~ \([[:space:]]*[^)]*[a-zA-Z][^)]*\) ]]; then
                    # Look for javadoc above this line
                    javadoc_start=$((line_num - 1))
                    javadoc_found=false
                    params_documented=true
                    return_documented=true
                    
                    # Check previous 15 lines for javadoc
                    for ((i=javadoc_start; i>=javadoc_start-15 && i>=1; i--)); do
                        prev_line=$(sed -n "${i}p" "$file")
                        if [[ "$prev_line" =~ /\*\* ]]; then
                            javadoc_found=true
                            break
                        elif [[ "$prev_line" =~ ^[[:space:]]*$ ]] || [[ "$prev_line" =~ ^[[:space:]]*\/\/ ]]; then
                            continue
                        else
                            break
                        fi
                    done
                    
                    if [[ "$javadoc_found" == true ]]; then
                        # Extract parameter names from method signature
                        param_section=$(echo "$method_sig" | sed 's/.*(\(.*\)).*/\1/')
                        if [[ "$param_section" != "" ]] && [[ "$param_section" != "$method_sig" ]]; then
                            # Parse parameters
                            IFS=',' read -ra params <<< "$param_section"
                            for param in "${params[@]}"; do
                                # Extract parameter name (last word)
                                param_name=$(echo "$param" | awk '{print $NF}' | sed 's/.*[[:space:]]//')
                                if [[ "$param_name" != "" ]] && [[ ! "$param_name" =~ ^\.\.\. ]]; then
                                    # Check if @param exists for this parameter
                                    param_doc_found=false
                                    for ((j=i; j<=line_num+5; j++)); do
                                        doc_line=$(sed -n "${j}p" "$file" 2>/dev/null || echo "")
                                        if [[ "$doc_line" =~ @param[[:space:]]+${param_name}[[:space:]] ]]; then
                                            param_doc_found=true
                                            break
                                        elif [[ "$doc_line" =~ ^\s*(public|protected) ]]; then
                                            break
                                        fi
                                    done
                                    
                                    if [[ "$param_doc_found" == false ]]; then
                                        echo "❌ $file:$line_num - Missing @param $param_name" >> "$REPORT_FILE"
                                        params_documented=false
                                    fi
                                fi
                            done
                        fi
                        
                        # Check for @return if method returns something
                        if [[ "$method_sig" =~ ^[[:space:]]*(public|protected)[[:space:]]+[^[:space:]]+[[:space:]] ]] && \
                           [[ ! "$method_sig" =~ [[:space:]]void[[:space:]] ]] && \
                           [[ ! "$content" =~ [[:space:]]+(class|interface|enum)[[:space:]] ]]; then
                            return_doc_found=false
                            for ((j=i; j<=line_num+5; j++)); do
                                doc_line=$(sed -n "${j}p" "$file" 2>/dev/null || echo "")
                                if [[ "$doc_line" =~ @return ]]; then
                                    return_doc_found=true
                                    break
                                elif [[ "$doc_line" =~ ^\s*(public|protected) ]]; then
                                    break
                                fi
                            done
                            
                            if [[ "$return_doc_found" == false ]]; then
                                echo "❌ $file:$line_num - Missing @return" >> "$REPORT_FILE"
                                return_documented=false
                            fi
                        fi
                    else
                        echo "❌ $file:$line_num - Missing javadoc for method: $method_sig" >> "$REPORT_FILE"
                    fi
                fi
            fi
        fi
    done
}

# Function to run Maven javadoc with strict checking
run_maven_javadoc_check() {
    echo "🔧 Running Maven javadoc with strict validation..."
    
    # Try Maven javadoc with doclint
    if mvn javadoc:aggregate -DfailOnError=true -Dadditionalparam="-Xdoclint:all" -Dmaven.javadoc.skip=false -q > "$TEMP_DIR/maven-javadoc.log" 2>&1; then
        echo "✅ Maven javadoc validation passed"
    else
        echo "❌ Maven javadoc validation failed"
        echo "📄 Maven javadoc errors:"
        grep -E "(warning|error)" "$TEMP_DIR/maven-javadoc.log" | head -20 >> "$REPORT_FILE"
        ISSUES_FOUND=$((ISSUES_FOUND + 1))
    fi
}

# Scan all Java files in src/main/java
echo "🔍 Scanning source files for javadoc issues..."

find . -name "*.java" -path "*/src/main/java/*" | while read -r file; do
    echo "   Checking: $file"
    check_file_javadoc "$file"
done

# Run Maven javadoc validation
run_maven_javadoc_check

# Generate report
echo ""
echo "📊 Javadoc Validation Report"
echo "============================"

if [[ -f "$REPORT_FILE" ]] && [[ -s "$REPORT_FILE" ]]; then
    echo -e "${RED}❌ Issues found:${NC}"
    cat "$REPORT_FILE"
    
    echo ""
    echo -e "${YELLOW}📈 Summary:${NC}"
    echo "   Total issues: $(wc -l < "$REPORT_FILE")"
    echo "   Missing @param: $(grep -c "@param" "$REPORT_FILE" || echo "0")"
    echo "   Missing @return: $(grep -c "@return" "$REPORT_FILE" || echo "0")"
    echo "   Missing javadoc: $(grep -c "Missing javadoc" "$REPORT_FILE" || echo "0")"
    
    ISSUES_FOUND=$((ISSUES_FOUND + $(wc -l < "$REPORT_FILE")))
else
    echo -e "${GREEN}✅ No javadoc issues found!${NC}"
fi

# Cleanup
rm -rf "$TEMP_DIR"

if [[ $ISSUES_FOUND -gt 0 ]]; then
    echo ""
    echo -e "${RED}💥 Total issues found: $ISSUES_FOUND${NC}"
    echo -e "${BLUE}🔧 Fix these issues before pushing to avoid GitHub Actions failures${NC}"
    exit 1
else
    echo ""
    echo -e "${GREEN}🎉 All javadoc validation checks passed!${NC}"
    exit 0
fi