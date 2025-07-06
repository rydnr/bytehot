# 💻 Hands-On Lab: ByteHot Installation & Setup

**Estimated Time:** 20-25 minutes  
**Difficulty:** Beginner  
**Prerequisites:** Completed Module 01 content  

## 🎯 Lab Objectives

By the end of this lab, you will have:
- ✅ Successfully downloaded and configured ByteHot
- ✅ Created a working development environment
- ✅ Verified your installation with a functional test
- ✅ Set up reusable project templates
- ✅ Troubleshot any common installation issues

---

## 🚀 Getting Started

### Lab Environment Setup

Before starting, ensure you have:
- [ ] Java 11+ installed (`java -version`)
- [ ] Maven or basic Java compilation tools
- [ ] Terminal/command line access
- [ ] Text editor or IDE

---

## 📋 Lab Exercises

### Exercise 1: Download and Install ByteHot (5 minutes)

#### Step 1.1: Create Your ByteHot Workspace
```bash
# Create a dedicated workspace for ByteHot
mkdir -p ~/bytehot-workspace/{tools,projects,configs}
cd ~/bytehot-workspace
```

#### Step 1.2: Download the ByteHot Agent
Choose your preferred method:

**Option A: Direct Download**
```bash
cd tools
wget https://github.com/rydnr/bytehot/releases/latest/download/bytehot-application-latest-SNAPSHOT-agent.jar -O bytehot-agent.jar
```

**Option B: Using curl**
```bash
cd tools
curl -L -o bytehot-agent.jar https://github.com/rydnr/bytehot/releases/latest/download/bytehot-application-latest-SNAPSHOT-agent.jar
```

#### Step 1.3: Verify the Download
```bash
# Check the file exists and has reasonable size
ls -lh bytehot-agent.jar

# Verify it's a valid JAR file
file bytehot-agent.jar
```

**Expected Output:**
```
-rw-r--r-- 1 user user 15M Jan 15 10:30 bytehot-agent.jar
bytehot-agent.jar: Java archive data (JAR)
```

#### ✅ Checkpoint 1
- [ ] ByteHot agent JAR downloaded successfully
- [ ] File size is reasonable (typically 10-20MB)
- [ ] File is recognized as a valid JAR archive

---

### Exercise 2: Environment Configuration (5 minutes)

#### Step 2.1: Create Configuration File
```bash
cd ~/bytehot-workspace/configs

# Create a comprehensive configuration
cat > bytehot-dev.yml << 'EOF'
bytehot:
  # File watching configuration
  watch:
    - path: "target/classes"
      patterns: ["*.class"]
      recursive: true
      pollInterval: 1000
    - path: "build/classes"  # Support for Gradle projects
      patterns: ["*.class"]
      recursive: true
      pollInterval: 1000
  
  # Logging configuration
  logging:
    level: INFO
    includeTimestamps: true
    showHotSwapEvents: true
    logToConsole: true
    
  # Development settings
  development:
    verboseOutput: true
    enableMetrics: true
    
  # User identification (for audit trails)
  user:
    name: "Lab Student"
    email: "student@bytehot-lab.com"
    role: "developer"
EOF
```

#### Step 2.2: Set Up Environment Variables
```bash
# Add to your shell profile (choose appropriate file)
# For bash: ~/.bashrc, for zsh: ~/.zshrc

echo '# ByteHot Configuration' >> ~/.bashrc
echo 'export BYTEHOT_HOME="$HOME/bytehot-workspace"' >> ~/.bashrc
echo 'export BYTEHOT_AGENT="$BYTEHOT_HOME/tools/bytehot-agent.jar"' >> ~/.bashrc
echo 'export BYTEHOT_CONFIG="$BYTEHOT_HOME/configs/bytehot-dev.yml"' >> ~/.bashrc

# Reload your shell configuration
source ~/.bashrc
```

#### Step 2.3: Verify Environment Setup
```bash
# Test environment variables
echo "ByteHot Home: $BYTEHOT_HOME"
echo "Agent Path: $BYTEHOT_AGENT"
echo "Config Path: $BYTEHOT_CONFIG"

# Verify files exist
test -f "$BYTEHOT_AGENT" && echo "✅ Agent found" || echo "❌ Agent missing"
test -f "$BYTEHOT_CONFIG" && echo "✅ Config found" || echo "❌ Config missing"
```

#### ✅ Checkpoint 2
- [ ] Configuration file created with development settings
- [ ] Environment variables set correctly
- [ ] All paths resolve to existing files

---

### Exercise 3: Create Test Application (8 minutes)

#### Step 3.1: Set Up Test Project
```bash
cd ~/bytehot-workspace/projects
mkdir bytehot-installation-test
cd bytehot-installation-test

# Create Maven-style directory structure
mkdir -p src/main/java/com/example/lab
```

#### Step 3.2: Create Test Application
```bash
cat > src/main/java/com/example/lab/InstallationTest.java << 'EOF'
package com.example.lab;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * ByteHot Installation Test Application
 * This application will be used to verify ByteHot installation is working correctly.
 */
public class InstallationTest {
    
    private static int messageCount = 0;
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    public static void main(String[] args) {
        System.out.println("🔥 ByteHot Installation Test Started");
        System.out.println("📋 This application will help verify your ByteHot setup");
        System.out.println("🎯 Keep this running and modify the displayMessage() method in another terminal");
        System.out.println();
        
        // Run indefinitely, displaying messages every 3 seconds
        while (true) {
            try {
                displayMessage();
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    /**
     * This method will be modified during the hot-swap test.
     * Try changing the message format or adding new information.
     */
    public static void displayMessage() {
        messageCount++;
        String currentTime = LocalTime.now().format(TIME_FORMAT);
        
        System.out.printf("[%s] Message #%d - ByteHot installation test running%n", 
                         currentTime, messageCount);
        
        // Display milestone messages
        if (messageCount % 10 == 0) {
            System.out.println("   🎉 Milestone: " + messageCount + " messages displayed!");
        }
    }
    
    /**
     * Helper method to get application status.
     * This can also be modified during testing.
     */
    public static String getStatus() {
        return String.format("Application running for %d messages", messageCount);
    }
}
EOF
```

#### Step 3.3: Create Build Script
```bash
cat > compile.sh << 'EOF'
#!/bin/bash

echo "🔨 Compiling ByteHot Installation Test..."

# Create output directory
mkdir -p target/classes

# Compile the Java source
javac -d target/classes src/main/java/com/example/lab/InstallationTest.java

if [ $? -eq 0 ]; then
    echo "✅ Compilation successful"
    echo "📁 Classes available in: target/classes"
else
    echo "❌ Compilation failed"
    exit 1
fi
EOF

chmod +x compile.sh
```

#### Step 3.4: Create Run Script
```bash
cat > run-with-bytehot.sh << 'EOF'
#!/bin/bash

echo "🚀 Starting application with ByteHot agent..."

# Check if agent exists
if [ ! -f "$BYTEHOT_AGENT" ]; then
    echo "❌ ByteHot agent not found at: $BYTEHOT_AGENT"
    exit 1
fi

# Check if config exists
if [ ! -f "$BYTEHOT_CONFIG" ]; then
    echo "❌ ByteHot config not found at: $BYTEHOT_CONFIG"
    exit 1
fi

# Run the application with ByteHot
java -javaagent:"$BYTEHOT_AGENT" \
     -Dbytehot.config="$BYTEHOT_CONFIG" \
     -cp target/classes \
     com.example.lab.InstallationTest
EOF

chmod +x run-with-bytehot.sh
```

#### ✅ Checkpoint 3
- [ ] Test application created with clear modification targets
- [ ] Build script created and executable
- [ ] Run script created with proper ByteHot configuration
- [ ] Project structure follows Maven conventions

---

### Exercise 4: Installation Verification (7 minutes)

#### Step 4.1: Compile and Run Test
```bash
# Compile the test application
./compile.sh

# Run the application with ByteHot (this will run indefinitely)
./run-with-bytehot.sh
```

**Expected Output:**
```
🚀 Starting application with ByteHot agent...
🔥 ByteHot Installation Test Started
📋 This application will help verify your ByteHot setup
🎯 Keep this running and modify the displayMessage() method in another terminal

[10:30:15] Message #1 - ByteHot installation test running
[10:30:18] Message #2 - ByteHot installation test running
[10:30:21] Message #3 - ByteHot installation test running
...
```

#### Step 4.2: Test Hot-Swap (Keep Application Running!)
Open a **new terminal** and navigate to your project:

```bash
cd ~/bytehot-workspace/projects/bytehot-installation-test

# Modify the displayMessage method
sed -i 's/ByteHot installation test running/🔥 HOT-SWAPPED MESSAGE! 🔥/' src/main/java/com/example/lab/InstallationTest.java

# Recompile to trigger hot-swap
./compile.sh
```

#### Step 4.3: Verify Hot-Swap Worked
Go back to your first terminal. You should see the output change from:
```
[10:30:24] Message #4 - ByteHot installation test running
```

To:
```
[10:30:27] Message #5 - 🔥 HOT-SWAPPED MESSAGE! 🔥
```

**🎉 If you see this change, congratulations! ByteHot is working correctly!**

#### ✅ Checkpoint 4
- [ ] Application starts successfully with ByteHot agent
- [ ] No error messages related to ByteHot loading
- [ ] Hot-swap modification takes effect immediately
- [ ] Application continues running without restart

---

## 🔧 Troubleshooting Guide

### Common Issues and Solutions

#### Issue: "Could not find or load main class"
```bash
# Verify compilation worked
ls -la target/classes/com/example/lab/
# Should show: InstallationTest.class

# Check classpath
echo $CLASSPATH
```

#### Issue: "Agent JAR not found"
```bash
# Verify agent path
ls -la "$BYTEHOT_AGENT"

# If missing, re-download
cd ~/bytehot-workspace/tools
wget https://github.com/rydnr/bytehot/releases/latest/download/bytehot-application-latest-SNAPSHOT-agent.jar -O bytehot-agent.jar
```

#### Issue: "Hot-swap not taking effect"
```bash
# Check if ByteHot is monitoring the right directory
echo "Watching: target/classes"
ls -la target/classes/com/example/lab/

# Verify file timestamps
stat target/classes/com/example/lab/InstallationTest.class
```

#### Issue: "Permission errors"
```bash
# Fix permissions
chmod 644 "$BYTEHOT_AGENT"
chmod 644 "$BYTEHOT_CONFIG"
```

---

## 🏆 Lab Completion

### Final Verification Checklist

Complete these final checks to ensure your installation is perfect:

- [ ] **Agent Download**: ByteHot agent JAR is downloaded and accessible
- [ ] **Configuration**: YAML configuration file is properly formatted
- [ ] **Environment**: Environment variables are set and persistent
- [ ] **Test Application**: Sample application compiles and runs
- [ ] **Agent Loading**: Application starts with ByteHot agent without errors
- [ ] **Hot-Swap Test**: Code modification triggers successful hot-swap
- [ ] **Output Verification**: Modified behavior appears in running application

### Achievement Unlocked! 🏅

Congratulations! You've successfully:
- **🔧 Installed ByteHot**: Downloaded and configured the agent
- **⚙️ Set Up Environment**: Created persistent configuration
- **🧪 Verified Installation**: Tested with working application
- **🔥 Performed Hot-Swap**: Successfully modified running code

### Performance Metrics

Track your lab performance:
- **Installation Time**: _____ minutes
- **Issues Encountered**: _____
- **Hot-Swap Success**: ✅ Yes / ❌ No
- **Confidence Level**: ⭐⭐⭐⭐⭐ (1-5 stars)

---

## 🎯 Next Steps

Now that you have ByteHot installed and working, you're ready for Module 02 where you'll:
- Create more complex applications for hot-swapping
- Learn advanced hot-swap techniques
- Explore different types of code modifications
- Understand ByteHot's monitoring and feedback systems

**Keep your test application running** - we'll use it in the next module!

---

## 📝 Lab Notes

Use this space to record any observations, issues, or insights from the lab:

```
Lab Notes:
- Installation method used: [ ] Download [ ] Build from source
- Time to complete: _____ minutes
- Challenging parts: 
- Additional configurations made:
- Questions for further exploration:
```

**🔥 Excellent work completing the ByteHot Installation Lab!**