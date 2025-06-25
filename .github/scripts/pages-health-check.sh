#!/bin/bash
set -euo pipefail

# ByteHot Pages Health Check Script
# Checks GitHub Pages site health and documentation availability

REPO_OWNER="${GITHUB_REPOSITORY_OWNER:-}"
REPO_NAME="${GITHUB_REPOSITORY##*/}"
PAGES_URL="https://${REPO_OWNER}.github.io/${REPO_NAME}/"

echo "Checking Pages URL: $PAGES_URL"

# Check if the pages site is accessible
check_main_site() {
    echo "🌐 Checking main GitHub Pages site..."
    
    HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" "$PAGES_URL" || echo "000")
    
    if [ "$HTTP_STATUS" = "200" ]; then
        echo "✅ GitHub Pages site is healthy (HTTP $HTTP_STATUS)"
        return 0
    else
        echo "❌ GitHub Pages site returned HTTP $HTTP_STATUS"
        return 1
    fi
}

# Check if Javadocs are accessible
check_javadocs() {
    echo "📚 Checking Javadocs accessibility..."
    
    JAVADOC_URL="${PAGES_URL}javadocs/"
    JAVADOC_STATUS=$(curl -s -o /dev/null -w "%{http_code}" "$JAVADOC_URL" || echo "000")
    
    if [ "$JAVADOC_STATUS" = "200" ]; then
        echo "✅ Javadocs are accessible (HTTP $JAVADOC_STATUS)"
        return 0
    else
        echo "⚠️ Javadocs returned HTTP $JAVADOC_STATUS"
        return 1
    fi
}

# Validate key documentation links
validate_documentation_links() {
    echo "🔗 Validating documentation links..."
    
    # Check key documentation pages
    PAGES_TO_CHECK=(
        ""                                    # Main page
        "journal.html"                        # Development journal
        "GETTING_STARTED.html"               # Getting started guide
        "story.html"                         # ByteHot story
        "implementation.html"                # Implementation docs
        "literate-docs.html"                 # Literate docs
        "javadocs/index.html"                # API documentation
    )
    
    FAILED_PAGES=()
    SUCCESSFUL_PAGES=()
    
    for page in "${PAGES_TO_CHECK[@]}"; do
        URL="${PAGES_URL}${page}"
        echo "Checking: $URL"
        
        HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" "$URL" || echo "000")
        
        if [ "$HTTP_STATUS" = "200" ]; then
            echo "✅ $page - OK"
            SUCCESSFUL_PAGES+=("$page")
        else
            echo "❌ $page - HTTP $HTTP_STATUS"
            FAILED_PAGES+=("$page")
        fi
    done
    
    echo ""
    echo "📊 Documentation Health Summary:"
    echo "  ✅ Successful pages: ${#SUCCESSFUL_PAGES[@]}"
    echo "  ❌ Failed pages: ${#FAILED_PAGES[@]}"
    
    if [ ${#FAILED_PAGES[@]} -gt 0 ]; then
        echo ""
        echo "❌ Failed pages:"
        printf '  - %s\n' "${FAILED_PAGES[@]}"
        return 1
    else
        echo ""
        echo "✅ All documentation pages are accessible"
        return 0
    fi
}

# Check specific content elements
check_content_elements() {
    echo "🔍 Checking specific content elements..."
    
    # Check main page has proper title
    MAIN_CONTENT=$(curl -s "$PAGES_URL" || echo "")
    if [[ "$MAIN_CONTENT" =~ "ByteHot" ]]; then
        echo "✅ Main page contains ByteHot branding"
    else
        echo "⚠️ Main page may be missing proper branding"
    fi
    
    # Check for CSS styling
    if [[ "$MAIN_CONTENT" =~ "style" ]] || [[ "$MAIN_CONTENT" =~ "css" ]]; then
        echo "✅ Main page includes styling"
    else
        echo "⚠️ Main page may be missing CSS styling"
    fi
    
    # Check for navigation
    if [[ "$MAIN_CONTENT" =~ "nav" ]] || [[ "$MAIN_CONTENT" =~ "Getting Started" ]]; then
        echo "✅ Main page includes navigation elements"
    else
        echo "⚠️ Main page may be missing navigation"
    fi
}

# Generate health report
generate_health_report() {
    local main_site_ok="$1"
    local javadocs_ok="$2"
    local docs_ok="$3"
    
    echo ""
    echo "📋 ByteHot GitHub Pages Health Report"
    echo "======================================"
    echo "Site URL: $PAGES_URL"
    echo "Check Time: $(date -u +'%Y-%m-%d %H:%M:%S UTC')"
    echo ""
    echo "Health Status:"
    
    if [ "$main_site_ok" = "0" ]; then
        echo "  ✅ Main Site: Healthy"
    else
        echo "  ❌ Main Site: Unhealthy"
    fi
    
    if [ "$javadocs_ok" = "0" ]; then
        echo "  ✅ Javadocs: Available"
    else
        echo "  ⚠️ Javadocs: Unavailable"
    fi
    
    if [ "$docs_ok" = "0" ]; then
        echo "  ✅ Documentation Links: All working"
    else
        echo "  ❌ Documentation Links: Some broken"
    fi
    
    # Overall health
    if [ "$main_site_ok" = "0" ] && [ "$docs_ok" = "0" ]; then
        echo ""
        echo "🎉 Overall Status: HEALTHY"
        echo "All critical components are functioning properly."
        return 0
    else
        echo ""
        echo "⚠️ Overall Status: NEEDS ATTENTION"
        echo "Some components require investigation."
        return 1
    fi
}

# Main execution
main() {
    echo "🚀 Starting ByteHot GitHub Pages health check..."
    echo ""
    
    # Run all checks
    check_main_site
    MAIN_SITE_OK=$?
    
    check_javadocs
    JAVADOCS_OK=$?
    
    validate_documentation_links
    DOCS_OK=$?
    
    check_content_elements
    
    # Generate final report
    generate_health_report $MAIN_SITE_OK $JAVADOCS_OK $DOCS_OK
    OVERALL_STATUS=$?
    
    if [ $OVERALL_STATUS -eq 0 ]; then
        echo "✅ GitHub Pages health check completed successfully"
    else
        echo "❌ GitHub Pages health check found issues"
        exit 1
    fi
}

main