# ByteHot Tutorial 01 - Example Project

This is the example project for **Tutorial 01: Your First Hot-Swap**.

## Quick Start

1. **Download ByteHot Agent** (if you haven't already):
   ```bash
   wget https://github.com/rydnr/bytehot/releases/latest/download/bytehot-application-latest-SNAPSHOT-agent.jar -O ~/bytehot-agent.jar
   ```

2. **Build the project**:
   ```bash
   mvn clean compile
   ```

3. **Run with ByteHot**:
   ```bash
   java -javaagent:~/bytehot-agent.jar \
        -Dbytehot.config=bytehot.yml \
        -cp target/classes \
        com.example.demo.HelloWorld
   ```

4. **In another terminal, modify the code and recompile**:
   ```bash
   # Edit src/main/java/com/example/demo/HelloWorld.java
   mvn compile
   # Watch your changes take effect immediately!
   ```

## What to Try

### Hot-Swap Examples

#### 1. Simple Message Change
Modify the `displayMessage()` method:
```java
public void displayMessage() {
    counter++;
    System.out.printf("🔥 [%d] HOT-SWAPPED: %s 🔥%n", counter, message);
}
```

#### 2. Add Timestamps
```java
public void displayMessage() {
    counter++;
    System.out.printf("[%d] %s (at %s)%n", 
        counter, message, java.time.LocalTime.now());
}
```

#### 3. Conditional Logic
```java
public void displayMessage() {
    counter++;
    System.out.printf("[%d] %s%n", counter, message);
    
    if (counter % 5 == 0) {
        System.out.println("   ✨ Fifth message milestone! ✨");
    }
}
```

#### 4. Change the Message Field
```java
private String message = "ByteHot is Amazing! 🚀";
```

## Configuration

The `bytehot.yml` file contains:
- **Watch Configuration**: Monitors `target/classes` for changes
- **Logging**: Shows hot-swap events and validation details
- **User Tracking**: Records who performed hot-swaps
- **Behavior Settings**: Controls backup creation and timeouts

## Troubleshooting

### Application won't start
- Check that Java 11+ is being used
- Verify the ByteHot agent JAR path is correct
- Ensure `target/classes` directory exists (run `mvn compile`)

### Hot-swap not working
- Verify you're compiling after changes: `mvn compile`
- Check the console output for ByteHot log messages
- Make sure you're only changing method bodies, not signatures

### Performance issues
- Hot-swapping may cause a brief pause during class redefinition
- This is normal and should only last a few milliseconds

## Files in this Project

- **`pom.xml`**: Maven configuration with ByteHot integration
- **`bytehot.yml`**: ByteHot configuration file
- **`src/main/java/com/example/demo/HelloWorld.java`**: Main application class
- **`README.md`**: This file

## Next Steps

Once you're comfortable with basic hot-swapping:
1. Try the more advanced tutorials
2. Experiment with your own Java projects
3. Explore ByteHot's event-driven architecture
4. Learn about framework integration (Spring Boot, etc.)

## Support

If you encounter issues:
1. Check the main tutorial documentation: `../README.md`
2. Review the troubleshooting section
3. Examine ByteHot's console output for error messages
4. Visit the ByteHot GitHub repository for help

Happy hot-swapping! 🔥