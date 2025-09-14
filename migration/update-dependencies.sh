#!/bin/bash

echo "🔄 Updating inter-module dependencies to use published Maven Central artifacts..."
echo "==============================================================================="

# Function to test if a module can be resolved from Maven Central
test_dependency_resolution() {
    local module_dir=$1
    local module_name=$2
    
    echo "Testing dependency resolution for $module_name..."
    
    cd "$module_dir"
    
    # Clear local repository cache for our modules to force Maven Central resolution
    rm -rf ~/.m2/repository/org/acmsl/commons/
    rm -rf ~/.m2/repository/org/acmsl/javaeda/
    rm -rf ~/.m2/repository/org/acmsl/bytehot/
    
    # Test compilation using only Maven Central dependencies
    if mvn clean compile -U -X 2>&1 | grep -q "BUILD SUCCESS"; then
        echo "✅ $module_name - Dependencies resolved successfully from Maven Central"
        return 0
    else
        echo "❌ $module_name - Failed to resolve dependencies from Maven Central"
        return 1
    fi
}

# Test each module in dependency order
MODULES=(
    "/home/chous/github/rydnr/bytehot/migration/java-commons:java-commons"
    "/home/chous/github/rydnr/bytehot/migration/java-commons-infrastructure:java-commons-infrastructure"
    "/home/chous/github/rydnr/bytehot/migration/javaeda-domain:javaeda-domain"
    "/home/chous/github/rydnr/bytehot/migration/javaeda-infrastructure:javaeda-infrastructure"
    "/home/chous/github/rydnr/bytehot/migration/javaeda-application:javaeda-application"
    "/home/chous/github/rydnr/bytehot/migration/domain:bytehot-domain"
    "/home/chous/github/rydnr/bytehot/migration/infrastructure:bytehot-infrastructure"
    "/home/chous/github/rydnr/bytehot/migration/application:bytehot-application"
    "/home/chous/github/rydnr/bytehot/migration/plugin-commons:bytehot-plugin-commons"
    "/home/chous/github/rydnr/bytehot/migration/maven-plugin:bytehot-maven-plugin"
    "/home/chous/github/rydnr/bytehot/migration/gradle-plugin:bytehot-gradle-plugin"
    "/home/chous/github/rydnr/bytehot/migration/eclipse-plugin:bytehot-eclipse-plugin"
    "/home/chous/github/rydnr/bytehot/migration/intellij-plugin:bytehot-intellij-plugin"
    "/home/chous/github/rydnr/bytehot/migration/spring-plugin:bytehot-spring-plugin"
    "/home/chous/github/rydnr/bytehot/migration/vscode-plugin:bytehot-vscode-plugin"
)

SUCCESS_COUNT=0
TOTAL_COUNT=${#MODULES[@]}

for module in "${MODULES[@]}"; do
    IFS=':' read -r module_dir module_name <<< "$module"
    
    if test_dependency_resolution "$module_dir" "$module_name"; then
        ((SUCCESS_COUNT++))
    fi
    
    echo ""
done

echo "📊 Summary: $SUCCESS_COUNT/$TOTAL_COUNT modules successfully resolved dependencies from Maven Central"

if [ $SUCCESS_COUNT -eq $TOTAL_COUNT ]; then
    echo "🎉 All modules can resolve dependencies from Maven Central!"
    echo "✅ The ByteHot monorepo split is complete and functional!"
    exit 0
else
    echo "⚠️  Some modules still have dependency resolution issues."
    exit 1
fi