#!/bin/bash

echo "🧪 End-to-End Integration Test for ByteHot Split"
echo "================================================"

# Create a test project that uses all published modules
TEST_DIR="/tmp/bytehot-integration-test"
rm -rf "$TEST_DIR"
mkdir -p "$TEST_DIR"

cd "$TEST_DIR"

# Create a test Maven project
cat > pom.xml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/maven-v4_0_0.xsd">
  <modelVersion>4.0.0</modelVersion>
  
  <groupId>org.acmsl.bytehot</groupId>
  <artifactId>bytehot-integration-test</artifactId>
  <version>1.0.0-SNAPSHOT</version>
  <packaging>jar</packaging>
  
  <properties>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
  </properties>
  
  <dependencies>
    <!-- Test all published modules -->
    <dependency>
      <groupId>org.acmsl.commons</groupId>
      <artifactId>java-commons</artifactId>
      <version>1.0.0</version>
    </dependency>
    
    <dependency>
      <groupId>org.acmsl.commons</groupId>
      <artifactId>java-commons-infrastructure</artifactId>
      <version>1.0.0</version>
    </dependency>
    
    <dependency>
      <groupId>org.acmsl.javaeda</groupId>
      <artifactId>javaeda-domain</artifactId>
      <version>1.0.0</version>
    </dependency>
    
    <dependency>
      <groupId>org.acmsl.javaeda</groupId>
      <artifactId>javaeda-infrastructure</artifactId>
      <version>1.0.0</version>
    </dependency>
    
    <dependency>
      <groupId>org.acmsl.javaeda</groupId>
      <artifactId>javaeda-application</artifactId>
      <version>1.0.0</version>
    </dependency>
    
    <dependency>
      <groupId>org.acmsl.bytehot</groupId>
      <artifactId>bytehot-domain</artifactId>
      <version>1.0.0</version>
    </dependency>
    
    <dependency>
      <groupId>org.acmsl.bytehot</groupId>
      <artifactId>bytehot-infrastructure</artifactId>
      <version>1.0.0</version>
    </dependency>
    
    <dependency>
      <groupId>org.acmsl.bytehot</groupId>
      <artifactId>bytehot-application</artifactId>
      <version>1.0.0</version>
    </dependency>
    
    <dependency>
      <groupId>org.acmsl.bytehot</groupId>
      <artifactId>bytehot-plugin-commons</artifactId>
      <version>1.0.0</version>
    </dependency>
    
    <dependency>
      <groupId>org.acmsl.bytehot</groupId>
      <artifactId>bytehot-maven-plugin</artifactId>
      <version>1.0.0</version>
    </dependency>
    
    <dependency>
      <groupId>org.acmsl.bytehot</groupId>
      <artifactId>bytehot-gradle-plugin</artifactId>
      <version>1.0.0</version>
    </dependency>
    
    <dependency>
      <groupId>org.acmsl.bytehot</groupId>
      <artifactId>bytehot-eclipse-plugin</artifactId>
      <version>1.0.0</version>
    </dependency>
    
    <dependency>
      <groupId>org.acmsl.bytehot</groupId>
      <artifactId>bytehot-intellij-plugin</artifactId>
      <version>1.0.0</version>
    </dependency>
    
    <dependency>
      <groupId>org.acmsl.bytehot</groupId>
      <artifactId>bytehot-spring-plugin</artifactId>
      <version>1.0.0</version>
    </dependency>
    
    <dependency>
      <groupId>org.acmsl.bytehot</groupId>
      <artifactId>bytehot-vscode-plugin</artifactId>
      <version>1.0.0</version>
    </dependency>
  </dependencies>
  
  <build>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.11.0</version>
        <configuration>
          <source>17</source>
          <target>17</target>
        </configuration>
      </plugin>
    </plugins>
  </build>
</project>
EOF

# Create a simple test class that uses the modules
mkdir -p src/main/java/org/acmsl/bytehot/test

cat > src/main/java/org/acmsl/bytehot/test/IntegrationTest.java << 'EOF'
package org.acmsl.bytehot.test;

/**
 * Integration test to verify all ByteHot modules can be loaded from Maven Central.
 * This class imports key classes from each module to ensure they're available.
 */
public class IntegrationTest {
    
    public static void main(String[] args) {
        System.out.println("🧪 ByteHot Integration Test Starting...");
        
        try {
            // Test java-commons
            Class.forName("org.acmsl.commons.patterns.Application");
            System.out.println("✅ java-commons loaded successfully");
            
            // Test javaeda-domain
            Class.forName("org.acmsl.javaeda.domain.JavaedaFoundation");
            System.out.println("✅ javaeda-domain loaded successfully");
            
            // Test bytehot-domain
            Class.forName("org.acmsl.bytehot.domain.ByteHot");
            System.out.println("✅ bytehot-domain loaded successfully");
            
            // Test bytehot-application
            Class.forName("org.acmsl.bytehot.application.ByteHotApplication");
            System.out.println("✅ bytehot-application loaded successfully");
            
            // Test plugin-commons
            Class.forName("org.acmsl.bytehot.plugin.core.PluginBase");
            System.out.println("✅ bytehot-plugin-commons loaded successfully");
            
            // Test some additional key classes
            Class.forName("org.acmsl.bytehot.domain.events.ClassFileChanged");
            System.out.println("✅ bytehot-domain events loaded successfully");
            
            System.out.println("🎉 All modules loaded successfully from Maven Central!");
            System.out.println("✅ ByteHot monorepo split is complete and functional!");
            
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Failed to load class: " + e.getMessage());
            System.exit(1);
        }
    }
}
EOF

echo "📋 Created integration test project at: $TEST_DIR"
echo "🔨 Attempting to compile and run integration test..."

# Clear local repository cache to force Maven Central resolution
rm -rf ~/.m2/repository/org/acmsl/commons/
rm -rf ~/.m2/repository/org/acmsl/javaeda/
rm -rf ~/.m2/repository/org/acmsl/bytehot/

# Try to compile the test
if mvn clean compile -U; then
    echo "✅ Compilation successful! All dependencies resolved from Maven Central."
    
    # Run the integration test
    if mvn exec:java -Dexec.mainClass="org.acmsl.bytehot.test.IntegrationTest"; then
        echo "🎉 Integration test passed! ByteHot split is successful!"
        exit 0
    else
        echo "❌ Integration test failed during execution."
        exit 1
    fi
else
    echo "❌ Compilation failed. Some dependencies may not be available yet."
    echo "ℹ️  This is normal if modules are still syncing to Maven Central (10-30 minutes)."
    exit 1
fi