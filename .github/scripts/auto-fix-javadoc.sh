#!/usr/bin/env bash
set -euo pipefail

# Automated Javadoc Fixer for ByteHot
# Fixes common javadoc patterns automatically

echo "🔧 Automated Javadoc Fixing for ByteHot"
echo "======================================"

TEMP_DIR=$(mktemp -d)
ISSUES_FILE="$TEMP_DIR/issues.txt"
FIXED_COUNT=0

# Get current issues
echo "📋 Analyzing current javadoc issues..."
bash .github/scripts/validate-all-javadoc.sh 2>&1 | grep -E "(warning|error).*:" | sort -u > "$ISSUES_FILE"
TOTAL_ISSUES=$(wc -l < "$ISSUES_FILE")

echo "Found $TOTAL_ISSUES issues to fix"

# Function to fix missing @param for generic types
fix_generic_params() {
    echo "🔍 Fixing missing @param for generic types..."
    
    # Fix <T> parameters
    grep "no @param for <T>" "$ISSUES_FILE" | while IFS=: read -r file line_num message; do
        if [[ -f "$file" ]]; then
            echo "   Fixing <T> in $file:$line_num"
            
            # Find the javadoc block above the method and add @param <T>
            # Look for the method line and find the javadoc above it
            METHOD_LINE=$line_num
            
            # Find the javadoc end (line with */) before the method
            JAVADOC_END=$(awk -v start="$METHOD_LINE" 'NR < start && /\*\// { print NR }' "$file" | tail -1)
            
            if [[ -n "$JAVADOC_END" ]]; then
                # Add @param <T> before the closing */
                sed -i "${JAVADOC_END}i\\     * @param <T> the type parameter" "$file"
                ((FIXED_COUNT++))
            fi
        fi
    done
    
    # Fix <C> parameters  
    grep "no @param for <C>" "$ISSUES_FILE" | while IFS=: read -r file line_num message; do
        if [[ -f "$file" ]]; then
            echo "   Fixing <C> in $file:$line_num"
            
            METHOD_LINE=$line_num
            JAVADOC_END=$(awk -v start="$METHOD_LINE" 'NR < start && /\*\// { print NR }' "$file" | tail -1)
            
            if [[ -n "$JAVADOC_END" ]]; then
                sed -i "${JAVADOC_END}i\\     * @param <C> the type parameter" "$file"
                ((FIXED_COUNT++))
            fi
        fi
    done
    
    # Fix <O> parameters
    grep "no @param for <O>" "$ISSUES_FILE" | while IFS=: read -r file line_num message; do
        if [[ -f "$file" ]]; then
            echo "   Fixing <O> in $file:$line_num"
            
            METHOD_LINE=$line_num
            JAVADOC_END=$(awk -v start="$METHOD_LINE" 'NR < start && /\*\// { print NR }' "$file" | tail -1)
            
            if [[ -n "$JAVADOC_END" ]]; then
                sed -i "${JAVADOC_END}i\\     * @param <O> the type parameter" "$file"
                ((FIXED_COUNT++))
            fi
        fi
    done
}

# Function to add missing @return annotations
fix_missing_returns() {
    echo "🔍 Fixing missing @return annotations..."
    
    grep "no @return" "$ISSUES_FILE" | while IFS=: read -r file line_num message; do
        if [[ -f "$file" ]]; then
            echo "   Fixing missing @return in $file:$line_num"
            
            # Get the method signature to determine return type
            METHOD_LINE=$(sed -n "${line_num}p" "$file")
            
            # Extract return type from method signature
            if [[ "$METHOD_LINE" =~ (public|protected|private)?[[:space:]]*([a-zA-Z_][a-zA-Z0-9_\<\>\[\]]*)[[:space:]]+[a-zA-Z_] ]]; then
                RETURN_TYPE="${BASH_REMATCH[2]}"
                
                # Generate appropriate @return description based on type
                RETURN_DESC=""
                case "$RETURN_TYPE" in
                    "boolean") RETURN_DESC="true if successful, false otherwise" ;;
                    "int"|"long"|"double"|"float") RETURN_DESC="the calculated value" ;;
                    "String") RETURN_DESC="the string representation" ;;
                    "List"|"Collection") RETURN_DESC="the list of items" ;;
                    "Optional") RETURN_DESC="an optional containing the result if available" ;;
                    "void") continue ;; # Skip void methods
                    *) RETURN_DESC="the result of the operation" ;;
                esac
                
                # Find javadoc end and add @return
                JAVADOC_END=$(awk -v start="$line_num" 'NR < start && /\*\// { print NR }' "$file" | tail -1)
                
                if [[ -n "$JAVADOC_END" ]] && [[ -n "$RETURN_DESC" ]]; then
                    sed -i "${JAVADOC_END}i\\     * @return $RETURN_DESC" "$file"
                    ((FIXED_COUNT++))
                fi
            fi
        fi
    done
}

# Function to fix package statement comments
fix_package_comments() {
    echo "🔍 Fixing package statement comments..."
    
    grep "package.*warning: no comment" "$ISSUES_FILE" | while IFS=: read -r file line_num message; do
        if [[ -f "$file" ]] && [[ "$file" == *"package-info.java" ]]; then
            echo "   Fixing package comment in $file:$line_num"
            
            # The package line should already have javadoc above it
            # If it doesn't, the file structure might be wrong
            # For package-info.java files, just ensure proper format
            
            PACKAGE_LINE=$(grep -n "^package " "$file" | cut -d: -f1)
            if [[ -n "$PACKAGE_LINE" ]]; then
                # Check if there's already a javadoc block
                if ! grep -q "/\*\*" "$file"; then
                    # Add basic package documentation
                    PACKAGE_NAME=$(grep "^package " "$file" | sed 's/package //;s/;//')
                    sed -i "1i/**\\n * Package $PACKAGE_NAME.\\n */" "$file"
                    ((FIXED_COUNT++))
                fi
            fi
        fi
    done
}

# Function to add missing method comments
add_missing_method_comments() {
    echo "🔍 Adding missing method comments..."
    
    grep "warning: no comment" "$ISSUES_FILE" | grep -v "package" | while IFS=: read -r file line_num message; do
        if [[ -f "$file" ]]; then
            echo "   Adding comment to $file:$line_num"
            
            METHOD_LINE=$(sed -n "${line_num}p" "$file")
            
            # Extract method name
            if [[ "$METHOD_LINE" =~ [[:space:]]*([a-zA-Z_][a-zA-Z0-9_]*)[[:space:]]*\( ]]; then
                METHOD_NAME="${BASH_REMATCH[1]}"
                
                # Generate basic comment based on method name
                COMMENT=""
                case "$METHOD_NAME" in
                    *"get"*) COMMENT="Gets the ${METHOD_NAME#get}" ;;
                    *"set"*) COMMENT="Sets the ${METHOD_NAME#set}" ;;
                    *"is"*|*"has"*) COMMENT="Checks if ${METHOD_NAME#is}" ;;
                    *"create"*) COMMENT="Creates a new instance" ;;
                    *"build"*) COMMENT="Builds the ${METHOD_NAME#build}" ;;
                    *"execute"*|*"run"*) COMMENT="Executes the operation" ;;
                    *) COMMENT="Performs ${METHOD_NAME} operation" ;;
                esac
                
                # Add basic javadoc above the method
                sed -i "${line_num}i\\    /**\\n     * $COMMENT.\\n     */" "$file"
                ((FIXED_COUNT++))
            fi
        fi
    done
}

# Run the fixes in order of safety (most mechanical first)
echo ""
echo "🚀 Starting automated fixes..."

fix_generic_params
fix_missing_returns  
fix_package_comments
add_missing_method_comments

# Cleanup
rm -rf "$TEMP_DIR"

echo ""
echo "✅ Automated fixing completed!"
echo "   Fixed: $FIXED_COUNT issues"
echo "   Original total: $TOTAL_ISSUES issues"

# Run validation again to see remaining issues
echo ""
echo "📊 Checking remaining issues..."
if bash .github/scripts/validate-all-javadoc.sh 2>&1 | grep -q "🎉 All javadoc validation checks passed!"; then
    echo "🎉 All javadoc issues have been fixed!"
else
    REMAINING=$(bash .github/scripts/validate-all-javadoc.sh 2>&1 | grep "🔧 Fix these" | grep -o "[0-9]\\+ issue" | grep -o "[0-9]\\+" || echo "0")
    echo "📋 Remaining issues: $REMAINING"
    echo "   Run the script again or fix manually for complex cases"
fi