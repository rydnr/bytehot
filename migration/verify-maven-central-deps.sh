#!/bin/bash

echo "🔍 Verifying Maven Central dependency availability..."
echo "=================================================="

# All published modules with their expected coordinates
MODULES=(
    "org.acmsl.commons:java-commons:1.0.0"
    "org.acmsl.commons:java-commons-infrastructure:1.0.0"
    "org.acmsl.javaeda:javaeda-domain:1.0.0"
    "org.acmsl.javaeda:javaeda-infrastructure:1.0.0"
    "org.acmsl.javaeda:javaeda-application:1.0.0"
    "org.acmsl.bytehot:bytehot-domain:1.0.0"
    "org.acmsl.bytehot:bytehot-infrastructure:1.0.0"
    "org.acmsl.bytehot:bytehot-application:1.0.0"
    "org.acmsl.bytehot:bytehot-plugin-commons:1.0.0"
    "org.acmsl.bytehot:bytehot-maven-plugin:1.0.0"
    "org.acmsl.bytehot:bytehot-gradle-plugin:1.0.0"
    "org.acmsl.bytehot:bytehot-eclipse-plugin:1.0.0"
    "org.acmsl.bytehot:bytehot-intellij-plugin:1.0.0"
    "org.acmsl.bytehot:bytehot-spring-plugin:1.0.0"
    "org.acmsl.bytehot:bytehot-vscode-plugin:1.0.0"
)

AVAILABLE_COUNT=0
TOTAL_COUNT=${#MODULES[@]}

for module in "${MODULES[@]}"; do
    IFS=':' read -r group_id artifact_id version <<< "$module"
    
    # Check if module is available in Maven Central
    url="https://repo1.maven.org/maven2/${group_id//./\/}/$artifact_id/$version/$artifact_id-$version.pom"
    
    if curl -s --head "$url" | grep -q "200 OK"; then
        echo "✅ $module - Available"
        ((AVAILABLE_COUNT++))
    else
        echo "⏳ $module - Not yet available (may still be syncing)"
    fi
done

echo ""
echo "📊 Summary: $AVAILABLE_COUNT/$TOTAL_COUNT modules available on Maven Central"

if [ $AVAILABLE_COUNT -eq $TOTAL_COUNT ]; then
    echo "🎉 All modules are available! Ready to update dependencies."
    exit 0
else
    echo "⚠️  Some modules are still syncing. This is normal and can take 10-30 minutes."
    exit 1
fi