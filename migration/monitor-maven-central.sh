#!/bin/bash

echo "📡 Monitoring Maven Central availability for ByteHot modules..."
echo "=============================================================="

# Function to check module availability
check_module() {
    local group_id=$1
    local artifact_id=$2
    local version=$3
    
    url="https://repo1.maven.org/maven2/${group_id//./\/}/$artifact_id/$version/$artifact_id-$version.pom"
    
    if curl -s --head "$url" | grep -q "200 OK"; then
        return 0
    else
        return 1
    fi
}

# All modules to monitor
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

# Monitor until all modules are available
MAX_ATTEMPTS=60  # Check for 30 minutes (every 30 seconds)
ATTEMPT=0

while [ $ATTEMPT -lt $MAX_ATTEMPTS ]; do
    AVAILABLE_COUNT=0
    TOTAL_COUNT=${#MODULES[@]}
    
    clear
    echo "📡 Monitoring Maven Central availability - Attempt $((ATTEMPT+1))/$MAX_ATTEMPTS"
    echo "=============================================================="
    echo "Time: $(date)"
    echo ""
    
    for module in "${MODULES[@]}"; do
        IFS=':' read -r group_id artifact_id version <<< "$module"
        
        if check_module "$group_id" "$artifact_id" "$version"; then
            echo "✅ $module - Available"
            ((AVAILABLE_COUNT++))
        else
            echo "⏳ $module - Still syncing..."
        fi
    done
    
    echo ""
    echo "📊 Progress: $AVAILABLE_COUNT/$TOTAL_COUNT modules available"
    
    if [ $AVAILABLE_COUNT -eq $TOTAL_COUNT ]; then
        echo ""
        echo "🎉 All modules are now available on Maven Central!"
        echo "✅ Ready to run integration tests!"
        exit 0
    fi
    
    ((ATTEMPT++))
    
    if [ $ATTEMPT -lt $MAX_ATTEMPTS ]; then
        echo "⏰ Checking again in 30 seconds..."
        sleep 30
    fi
done

echo ""
echo "⚠️  Timeout reached. Some modules may still be syncing."
echo "ℹ️  This is normal for Maven Central sync. Try running the integration test manually."
exit 1