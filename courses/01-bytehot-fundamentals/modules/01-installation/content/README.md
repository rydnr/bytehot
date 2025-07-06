# Module 01: ByteHot Installation & Setup

**Duration:** 30 minutes  
**Learning Objectives:**
- Download and install the ByteHot agent
- Understand JVM agent configuration
- Set up your development environment for hot-swapping
- Verify your installation is working correctly

---

## 🎯 Module Overview

Welcome to your first module in ByteHot mastery! In this module, you'll get ByteHot up and running on your development machine. By the end of this module, you'll have a fully configured ByteHot environment ready for hot-swapping adventures.

### What You'll Accomplish
- ✅ Download the ByteHot agent JAR
- ✅ Configure your development environment
- ✅ Understand the `-javaagent` parameter
- ✅ Verify your installation with a simple test
- ✅ Set up project templates for future use

---

## 📚 Learning Content

### 🔧 Understanding ByteHot Architecture

Before we dive into installation, let's understand what ByteHot is and how it works:

**ByteHot** is a JVM agent that enables real-time hot-swapping of Java bytecode. Here's how it integrates with your Java application:

```
┌─────────────────────────────────────┐
│         Your Java Application      │
│  ┌─────────────────────────────┐   │
│  │          JVM Process        │   │
│  │  ┌─────────────────────┐   │   │
│  │  │    ByteHot Agent    │   │   │
│  │  │  • File Monitoring  │   │   │
│  │  │  • Bytecode Analysis│   │   │
│  │  │  • Class Redefinition│  │   │
│  │  └─────────────────────┘   │   │
│  └─────────────────────────────┘   │
└─────────────────────────────────────┘
```

### 🚀 Key Concepts

**JVM Agent**: A special JAR file that gets loaded before your main application, giving ByteHot access to the JVM's instrumentation capabilities.

**Hot-Swapping**: The process of replacing running code with new versions without stopping the application.

**File Watching**: ByteHot monitors your compiled `.class` files for changes and automatically triggers hot-swap operations.

---

## 📥 Installation Methods

### Method 1: Download Pre-Built Agent (Recommended)

The fastest way to get started is to download the pre-built ByteHot agent:

```bash
# Create a tools directory (optional but recommended)
mkdir -p ~/tools/bytehot
cd ~/tools/bytehot

# Download the latest ByteHot agent
wget https://github.com/rydnr/bytehot/releases/latest/download/bytehot-application-latest-SNAPSHOT-agent.jar

# Rename for easier use
mv bytehot-application-latest-SNAPSHOT-agent.jar bytehot-agent.jar

# Verify the download
ls -la bytehot-agent.jar
```

**Expected Output:**
```
-rw-r--r--  1 user user 15234567 Jan 15 10:30 bytehot-agent.jar
```

### Method 2: Build from Source (Advanced)

If you want the latest development version or want to contribute to ByteHot:

```bash
# Clone the repository
git clone https://github.com/rydnr/bytehot.git
cd bytehot

# Build the project (this may take a few minutes)
mvn clean package -DskipTests

# Locate the agent JAR
find . -name "*agent.jar" -type f
# Should show: ./bytehot-application/target/bytehot-application-*-agent.jar

# Copy to your tools directory
cp bytehot-application/target/bytehot-application-*-agent.jar ~/tools/bytehot/bytehot-agent.jar
```

---

## ⚙️ Environment Configuration

### Setting Up Environment Variables

For convenience, let's set up environment variables:

```bash
# Add to your shell profile (.bashrc, .zshrc, etc.)
export BYTEHOT_AGENT_PATH="$HOME/tools/bytehot/bytehot-agent.jar"
export BYTEHOT_CONFIG_PATH="$HOME/tools/bytehot/bytehot.yml"

# Reload your shell configuration
source ~/.bashrc  # or ~/.zshrc
```

### Creating a Default Configuration

Create a basic configuration file:

```bash
# Create configuration file
cat > ~/tools/bytehot/bytehot.yml << 'EOF'
bytehot:
  # File watching configuration
  watch:
    - path: "target/classes"
      patterns: ["*.class"]
      recursive: true
      pollInterval: 1000
  
  # Logging configuration
  logging:
    level: INFO
    includeTimestamps: true
    showHotSwapEvents: true
    
  # User identification
  user:
    name: "Student"
    email: "student@example.com"
EOF
```

---

## 🧪 Installation Verification

Let's create a simple test to verify ByteHot is working:

### Quick Test Application

Create a test directory and simple Java class:

```bash
# Create test directory
mkdir -p ~/bytehot-test/src/main/java/com/example
cd ~/bytehot-test

# Create a simple Java file
cat > src/main/java/com/example/TestApp.java << 'EOF'
package com.example;

public class TestApp {
    private static int counter = 0;
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("🔥 ByteHot Test Application Started");
        
        while (true) {
            displayMessage();
            Thread.sleep(2000);
        }
    }
    
    public static void displayMessage() {
        counter++;
        System.out.printf("[%d] ByteHot is ready for action!%n", counter);
    }
}
EOF
```

### Create Build Configuration

```bash
# Create simple build script
cat > compile.sh << 'EOF'
#!/bin/bash
mkdir -p target/classes
javac -d target/classes src/main/java/com/example/TestApp.java
echo "✅ Compilation complete"
EOF

chmod +x compile.sh
```

### Run the Test

```bash
# Compile the test application
./compile.sh

# Run with ByteHot agent
java -javaagent:$BYTEHOT_AGENT_PATH \
     -Dbytehot.config=$BYTEHOT_CONFIG_PATH \
     -cp target/classes \
     com.example.TestApp
```

**Expected Output:**
```
🔥 ByteHot Test Application Started
[1] ByteHot is ready for action!
[2] ByteHot is ready for action!
[3] ByteHot is ready for action!
...
```

---

## ✅ Verification Checklist

Congratulations! If you see the test application running, ByteHot is successfully installed. Let's verify everything is working:

### Installation Verification Steps

- [ ] **Agent JAR Downloaded**: ByteHot agent JAR is in your tools directory
- [ ] **Configuration Created**: Default bytehot.yml configuration file exists
- [ ] **Environment Variables**: BYTEHOT_AGENT_PATH and BYTEHOT_CONFIG_PATH are set
- [ ] **Test Application Runs**: Simple test app starts with ByteHot agent
- [ ] **No Error Messages**: Application runs without ByteHot-related errors

### Troubleshooting Common Issues

#### Issue: "Could not find or load main class"
**Solution**: Check your classpath and ensure the class is compiled correctly.

#### Issue: "Agent JAR not found"
**Solution**: Verify the path in BYTEHOT_AGENT_PATH exists and points to the correct JAR file.

#### Issue: "Permission denied"
**Solution**: Ensure the JAR file has read permissions:
```bash
chmod 644 $BYTEHOT_AGENT_PATH
```

---

## 🎯 Key Takeaways

By completing this module, you've learned:

1. **ByteHot Architecture**: How the agent integrates with the JVM
2. **Installation Methods**: Both pre-built and source-based installation
3. **Configuration Basics**: Setting up default configurations
4. **Environment Setup**: Proper environment variable configuration
5. **Verification Process**: How to test your installation

### What's Next?

In the next module, you'll use this ByteHot installation to perform your first actual hot-swap operation. You'll see the magic of modifying running code in real-time!

---

## 📝 Module Completion

Before moving to the next module:

1. ✅ Complete the **Knowledge Check Quiz** below
2. ✅ Finish the **Hands-On Lab Exercise**
3. ✅ Earn your **Installation Expert** badge!

Ready to test your knowledge? Let's see how well you understood the installation process!

[📝 Take the Knowledge Check Quiz →](../quiz/knowledge-check.yml)  
[💻 Start the Hands-On Lab →](../lab/installation-lab.md)