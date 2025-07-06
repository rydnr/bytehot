# Tutorial 01: Your First Hot-Swap

**Duration:** 30 minutes  
**Goal:** Experience the magic of hot-swapping with ByteHot  
**Prerequisites:** Basic Java knowledge  

## What You'll Learn

In this tutorial, you'll:
- Install and configure the ByteHot agent
- Create a simple Java application
- Perform your first hot-swap operation
- Understand the hot-swap workflow and file watching

## Overview

By the end of this tutorial, you'll have successfully modified a running Java application without restarting it. You'll see firsthand how ByteHot monitors file changes and applies them instantly to your running code.

## Prerequisites

- **Java 11+**: ByteHot requires Java 11 or later
- **Maven 3.6+**: For building the example project
- **Text Editor/IDE**: Any editor (IntelliJ IDEA, Eclipse, VS Code, etc.)
- **Terminal/Command Line**: For running commands

## Step 1: Get ByteHot

### Option A: Download Pre-built Agent (Recommended)

Download the latest ByteHot agent from the releases page:

```bash
wget https://github.com/rydnr/bytehot/releases/latest/download/bytehot-application-latest-SNAPSHOT-agent.jar
```

### Option B: Build from Source

```bash
# Clone the ByteHot repository
git clone https://github.com/rydnr/bytehot.git
cd bytehot

# Build the project
mvn clean package -DskipTests

# The agent JAR will be located at:
# bytehot-application/target/bytehot-application-*-agent.jar
```

For this tutorial, we'll assume you have the agent JAR at `~/bytehot-agent.jar`.

## Step 2: Create Your First Hot-Swappable Application

Let's create a simple Java application that we can hot-swap.

### Create Project Structure

```bash
mkdir hot-swap-demo
cd hot-swap-demo
mkdir -p src/main/java/com/example/demo
```

### Create the Main Application

Create `src/main/java/com/example/demo/HelloWorld.java`:

```java
package com.example.demo;

/**
 * A simple application to demonstrate ByteHot hot-swapping
 */
public class HelloWorld {
    
    private String message = "Hello, World!";
    private int counter = 0;
    
    public void run() {
        System.out.println("=== ByteHot Demo Application Started ===");
        
        while (true) {
            displayMessage();
            
            try {
                Thread.sleep(2000); // Wait 2 seconds between messages
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    /**
     * This method will be hot-swapped during the demo
     */
    public void displayMessage() {
        counter++;
        System.out.printf("[%d] %s%n", counter, message);
    }
    
    /**
     * This method will also be modified during hot-swap
     */
    public String getMessage() {
        return message;
    }
    
    public static void main(String[] args) {
        new HelloWorld().run();
    }
}
```

### Create Maven Configuration

Create `pom.xml` in the project root:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.example</groupId>
    <artifactId>hot-swap-demo</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    
    <properties>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>11</source>
                    <target>11</target>
                </configuration>
            </plugin>
            
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>exec-maven-plugin</artifactId>
                <version>3.1.0</version>
                <configuration>
                    <mainClass>com.example.demo.HelloWorld</mainClass>
                    <options>
                        <option>-javaagent:${user.home}/bytehot-agent.jar</option>
                    </options>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

### Build the Application

```bash
mvn clean compile
```

You should see output like:
```
[INFO] BUILD SUCCESS
```

## Step 3: Configure ByteHot

Create a configuration file `bytehot.yml` in your project root:

```yaml
bytehot:
  watch:
    - path: "target/classes"
      patterns: ["*.class"]
      recursive: true
      pollInterval: 1000  # Check for changes every 1 second
  
  # Optional: Configure logging
  logging:
    level: INFO
    includeTimestamps: true
    
  # Optional: User identification (for audit trails)
  user:
    name: "Developer"
    email: "developer@example.com"
```

## Step 4: Run with ByteHot Agent

Now let's run our application with the ByteHot agent attached:

```bash
java -javaagent:~/bytehot-agent.jar \
     -Dbytehot.config=bytehot.yml \
     -cp target/classes \
     com.example.demo.HelloWorld
```

You should see output similar to:
```
=== ByteHot Demo Application Started ===
[1] Hello, World!
[2] Hello, World!
[3] Hello, World!
...
```

**🔥 Keep this running!** Don't stop the application - we're about to hot-swap it.

## Step 5: Your First Hot-Swap

Now for the exciting part! We'll modify the running application without stopping it.

### Open a New Terminal

Keep the application running in the first terminal, and open a second terminal in the same project directory.

### Modify the Code

Edit `src/main/java/com/example/demo/HelloWorld.java` and change the `displayMessage()` method:

```java
/**
 * This method will be hot-swapped during the demo
 */
public void displayMessage() {
    counter++;
    System.out.printf("🔥 [%d] HOT-SWAPPED: %s 🔥%n", counter, message);
    
    // Add some extra flair
    if (counter % 5 == 0) {
        System.out.println("   ✨ ByteHot is working perfectly! ✨");
    }
}
```

### Trigger the Hot-Swap

Compile the changed class:

```bash
mvn compile
```

### Watch the Magic Happen!

Go back to your first terminal where the application is running. You should see the output change immediately to:

```
[5] Hello, World!
[6] Hello, World!
🔥 [7] HOT-SWAPPED: Hello, World! 🔥
🔥 [8] HOT-SWAPPED: Hello, World! 🔥
🔥 [9] HOT-SWAPPED: Hello, World! 🔥
🔥 [10] HOT-SWAPPED: Hello, World! 🔥
   ✨ ByteHot is working perfectly! ✨
```

**🎉 Congratulations! You just performed your first hot-swap!**

## Step 6: Multiple Hot-Swaps

Let's do another hot-swap to see how easy it is:

### Modify the Message

Change the `message` field and the `displayMessage()` method:

```java
private String message = "ByteHot Rocks! 🚀";

public void displayMessage() {
    counter++;
    System.out.printf("⚡ [%d] %s ⚡%n", counter, message);
    
    // Show timestamp for each message
    System.out.printf("    Time: %s%n", 
        java.time.LocalTime.now().toString());
}
```

### Compile and Watch

```bash
mvn compile
```

Your running application should immediately start showing:

```
⚡ [15] ByteHot Rocks! 🚀 ⚡
    Time: 14:30:25.123
⚡ [16] ByteHot Rocks! 🚀 ⚡
    Time: 14:30:27.456
```

## Understanding What Happened

### The Hot-Swap Process

1. **File Watching**: ByteHot monitors the `target/classes` directory
2. **Change Detection**: When you ran `mvn compile`, new `.class` files were generated
3. **Bytecode Analysis**: ByteHot analyzed the changes to ensure they're compatible
4. **Class Redefinition**: The JVM's instrumentation API redefined the class
5. **Instance Updates**: All existing instances were updated with the new behavior

### Key Concepts

- **File System Monitoring**: ByteHot watches for changes to `.class` files
- **Hot-Swap Compatibility**: Method body changes are fully supported
- **Instance Preservation**: Existing object instances maintain their state
- **Real-time Updates**: Changes take effect immediately without restart

## What You Can Hot-Swap

✅ **Supported Changes:**
- Method body implementations
- Adding new methods
- Changing method logic
- Modifying return values
- Adding private fields (with limitations)

❌ **Unsupported Changes:**
- Changing method signatures
- Adding/removing public fields
- Changing class hierarchy
- Modifying constructors significantly

## Troubleshooting

### Common Issues

#### 1. "Hot-swap not taking effect"

**Check these:**
- Is the agent JAR path correct?
- Did you compile after making changes?
- Are you watching the right directory?

```bash
# Verify agent is loaded
jps -v | grep bytehot

# Check configuration
cat bytehot.yml
```

#### 2. "ClassNotFoundException or NoClassDefFoundError"

**Solutions:**
- Ensure classpath includes `target/classes`
- Verify package structure matches directory structure
- Check that Maven compilation succeeded

#### 3. "Hot-swap rejected"

**Possible causes:**
- Incompatible changes (method signature modifications)
- Security manager restrictions
- JVM version compatibility

**Check logs for details:**
The ByteHot agent provides detailed logging about why hot-swaps might be rejected.

## Next Steps

🎯 **Congratulations!** You've successfully:
- ✅ Set up ByteHot with a Java application
- ✅ Performed multiple hot-swap operations
- ✅ Understood the basic hot-swap workflow
- ✅ Experienced zero-downtime code updates

### Continue Your Learning Journey

- **[Tutorial 02: Spring Boot Integration](../02-spring-boot-integration/)** - Learn how to use ByteHot with Spring Boot applications
- **[Tutorial 03: Event-Driven Development](../03-event-driven-development/)** - Explore ByteHot's event-driven architecture
- **[Examples](../../../examples/)** - See ByteHot in real-world applications

### Advanced Topics

Once you're comfortable with basic hot-swapping:
- Framework integration (Spring, CDI, Guice)
- Production deployment strategies
- Performance monitoring and optimization
- Enterprise security and compliance

## Summary

In this tutorial, you learned:

1. **Setup**: How to configure ByteHot agent with a Java application
2. **Configuration**: Basic ByteHot configuration with YAML
3. **Hot-Swapping**: The complete workflow from code change to runtime update
4. **Monitoring**: How ByteHot watches for file system changes
5. **Limitations**: What types of changes are supported vs. unsupported

ByteHot transforms the Java development experience by eliminating the restart cycle. With hot-swapping, you can:
- Fix bugs without losing application state
- Test changes instantly in running applications
- Iterate faster during development
- Apply patches to production systems without downtime

**Ready for more?** Continue with [Tutorial 02: Spring Boot Integration](../02-spring-boot-integration/) to see how ByteHot works with real-world frameworks!

---

## Appendix: Quick Reference

### Starting Application with ByteHot
```bash
java -javaagent:bytehot-agent.jar \
     -Dbytehot.config=bytehot.yml \
     -cp target/classes \
     com.example.demo.HelloWorld
```

### Triggering Hot-Swap
```bash
# 1. Modify Java source code
# 2. Compile changes
mvn compile
# 3. Changes apply automatically
```

### Configuration Template
```yaml
bytehot:
  watch:
    - path: "target/classes"
      patterns: ["*.class"]
      recursive: true
      pollInterval: 1000
```

**🔥 Happy Hot-Swapping!**