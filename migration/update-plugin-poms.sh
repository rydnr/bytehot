#!/bin/bash

# Update all plugin POMs to use central-publishing-maven-plugin

PLUGIN_DIRS="maven-plugin gradle-plugin eclipse-plugin intellij-plugin spring-plugin vscode-plugin"

for dir in $PLUGIN_DIRS; do
    echo "Updating $dir/pom.xml..."
    
    # Update plugin version
    sed -i 's/<nexus-staging-maven-plugin\.version>1\.6\.13<\/nexus-staging-maven-plugin\.version>/<central-publishing-maven-plugin.version>0.8.0<\/central-publishing-maven-plugin.version>/' "/home/chous/github/rydnr/bytehot/migration/$dir/pom.xml"
    
    # Replace nexus-staging-maven-plugin with central-publishing-maven-plugin
    sed -i '/<groupId>org\.sonatype\.plugins<\/groupId>/,/<\/plugin>/c\
          <!-- Central Publishing plugin -->\
          <plugin>\
            <groupId>org.sonatype.central</groupId>\
            <artifactId>central-publishing-maven-plugin</artifactId>\
            <version>${central-publishing-maven-plugin.version}</version>\
            <extensions>true</extensions>\
            <configuration>\
              <publishingServerId>ossrh</publishingServerId>\
              <autoPublish>true</autoPublish>\
            </configuration>\
          </plugin>' "/home/chous/github/rydnr/bytehot/migration/$dir/pom.xml"
    
    # Add GPG pinentry-mode configuration
    sed -i '/goals>/,/<\/execution>/{
        /<\/goals>/a\
                <configuration>\
                  <gpgArguments>\
                    <arg>--pinentry-mode</arg>\
                    <arg>loopback</arg>\
                  </gpgArguments>\
                </configuration>
    }' "/home/chous/github/rydnr/bytehot/migration/$dir/pom.xml"
    
    # Remove distributionManagement section
    sed -i '/<distributionManagement>/,/<\/distributionManagement>/d' "/home/chous/github/rydnr/bytehot/migration/$dir/pom.xml"
    
    echo "Updated $dir/pom.xml"
done

echo "All plugin POMs updated!"