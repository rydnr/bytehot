# ByteHot VS Code Extension

Live coding with instant hot-swapping for Java applications in Visual Studio Code.

## Features

- **Zero-configuration live mode** - Start hot-swapping with a single click
- **Automatic project analysis** - Detects main class from Maven, Gradle, or source files
- **Bundled agent** - No external dependencies required
- **Status bar integration** - Real-time live mode status
- **Rich configuration** - Customize JVM arguments and main class
- **Output channel** - Detailed logging and debugging information

## Quick Start

1. Open a Java project in VS Code
2. Click the ByteHot status bar item or use `Ctrl+Shift+P` → "ByteHot: Start Live Mode"
3. Your application starts with hot-swapping enabled
4. Make code changes and see them reflected instantly

## Commands

- `ByteHot: Start Live Mode` - Start live coding mode
- `ByteHot: Stop Live Mode` - Stop live coding mode  
- `ByteHot: Toggle Live Mode` - Toggle live mode on/off
- `ByteHot: Show Output` - Show ByteHot output channel

## Configuration

| Setting | Description | Default |
|---------|-------------|---------|
| `bytehot.enableAutoDetection` | Automatically detect main class and project configuration | `true` |
| `bytehot.mainClass` | Override main class for live mode (leave empty for auto-detection) | `""` |
| `bytehot.jvmArgs` | Additional JVM arguments for live mode | `[]` |

### Example Configuration

```json
{
    "bytehot.mainClass": "com.example.MyApplication",
    "bytehot.jvmArgs": ["-Xmx1g", "-Dspring.profiles.active=dev"]
}
```

## Project Support

ByteHot automatically detects project configuration from:

- **Maven projects** - Reads `pom.xml` for main class and dependencies
- **Gradle projects** - Reads `build.gradle`/`build.gradle.kts` for application configuration
- **Source scanning** - Scans Java source files for `main` methods

## How It Works

1. **Agent Discovery** - Extracts bundled ByteHot agent to temporary location
2. **Project Analysis** - Analyzes project structure and build configuration
3. **Classpath Building** - Constructs proper classpath from build outputs
4. **Process Launch** - Launches application with ByteHot agent attached
5. **Hot-swapping** - Agent enables real-time class reloading

## Status Bar

The ByteHot status bar item shows current state:

- **$(play) ByteHot: Ready** - Ready to start live mode
- **$(debug-stop) ByteHot: Active** - Live mode is running

Click the status bar item to toggle live mode.

## Troubleshooting

### Agent Not Found
If you see "ByteHot agent JAR not found", ensure:
- Project has been built (`mvn compile` or `./gradlew build`)
- ByteHot application JAR is available in local Maven repository

### No Main Class Detected
If main class detection fails:
- Ensure your project has a class with `public static void main(String[] args)`
- Set `bytehot.mainClass` in VS Code settings to override auto-detection
- Check the ByteHot output channel for detection details

### Live Mode Won't Start
Check the ByteHot output channel (`View` → `Output` → select "ByteHot") for detailed error information.

## Development

To develop this extension:

```bash
# Install dependencies
npm install

# Compile TypeScript
npm run compile

# Watch for changes
npm run watch

# Run tests
npm test

# Package extension
npm run package
```

## Requirements

- Visual Studio Code 1.60.0 or higher
- Java 17 or higher
- Maven or Gradle project (optional, but recommended)

## License

GPL-3.0 - See LICENSE file for details.

## Contributing

See the main [ByteHot repository](https://github.com/rydnr/bytehot) for contribution guidelines.