#!/bin/bash

# Script to add central publishing plugin to ByteHot modules

add_central_publishing_to_module() {
    local module_dir="$1"
    local pom_file="$module_dir/pom.xml"
    
    echo "Processing $module_dir..."
    
    # Check if already has central publishing plugin
    if grep -q "central-publishing-maven-plugin" "$pom_file"; then
        echo "  Already has central publishing plugin"
        return 0
    fi
    
    # Add version property
    sed -i 's|<nexus-staging-maven-plugin.version>1.6.13</nexus-staging-maven-plugin.version>|<nexus-staging-maven-plugin.version>1.6.13</nexus-staging-maven-plugin.version>\n    <central-publishing-maven-plugin.version>0.8.0</central-publishing-maven-plugin.version>|' "$pom_file"
    
    # Add plugin to main build section (before </plugins>)
    sed -i 's|</plugins>|      <plugin>\n        <groupId>org.sonatype.central</groupId>\n        <artifactId>central-publishing-maven-plugin</artifactId>\n        <version>${central-publishing-maven-plugin.version}</version>\n        <extensions>true</extensions>\n        <configuration>\n          <publishingServerId>ossrh</publishingServerId>\n          <autoPublish>true</autoPublish>\n        </configuration>\n      </plugin>\n    </plugins>|' "$pom_file"
    
    # Add GPG arguments to GPG plugin in release profile
    sed -i '/<id>sign-artifacts<\/id>/,/<\/execution>/ {
        /<\/execution>/ a\
            </executions>\
            <configuration>\
              <gpgArguments>\
                <arg>--pinentry-mode</arg>\
                <arg>loopback</arg>\
              </gpgArguments>\
            </configuration>
    }' "$pom_file"
    
    echo "  Added central publishing plugin"
}

# Process ByteHot modules
for module in infrastructure application; do
    add_central_publishing_to_module "$module"
done

echo "Done!"