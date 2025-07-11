# ByteHot 🔥

> **The future of Java is hot. The future is ByteHot.**

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://openjdk.java.net/)
[![License](https://img.shields.io/badge/License-GPLv3-blue.svg)](LICENSE)
[![Architecture](https://img.shields.io/badge/Architecture-Hexagonal-green.svg)](docs/)
[![TDD](https://img.shields.io/badge/Development-TDD-red.svg)](journal.org)

*Imagine a world where your Java applications don't need to restart to incorporate changes. Where fixing a bug or adding a feature doesn't mean interrupting running services, losing precious state, or causing downtime. Where development iteration happens in seconds, not minutes.*

**This is the world that ByteHot makes possible.**

## 🚀 What is ByteHot?

ByteHot is a revolutionary JVM agent that enables **true hot-swapping of bytecode at runtime**—not just method implementations, but comprehensive class evolution with intelligent instance management and bulletproof reliability.

### ⚡ The Problem: The Restart Tyranny

Every Java developer knows the pain:
- **Development Friction**: Lengthy restart cycles that break flow state
- **Production Constraints**: Planned downtime windows for minor fixes  
- **Testing Bottlenecks**: Slow iteration on integration testing
- **Lost Context**: Stateful applications lose precious runtime context

Traditional Java hot-swap is limited to method body changes—add a field, change a method signature, or modify class structure, and you're back to restart hell.

### 🎯 The ByteHot Solution

ByteHot transforms this reality by providing:

#### 🔄 Advanced Hot-Swapping
ByteHot provides comprehensive bytecode hot-swapping capabilities with intelligent validation, error handling, and state management for method body changes and compatible class modifications.

#### 🧠 Intelligent Instance Management  
Sophisticated instance tracking and updating system that preserves object state across class redefinitions with multiple update strategies and framework integration.

#### 🔌 Framework Integration
Built-in support for dependency injection frameworks through extensible adapter patterns. Includes integration points for Spring, CDI, Guice, and other frameworks.

#### 🛡️ Enterprise Reliability
Complete error handling and recovery system with rollback capabilities, comprehensive audit trails, and production-ready monitoring and observability.

## 🏗️ Architecture Excellence

ByteHot is built with **Hexagonal Architecture** and **Domain-Driven Design** principles, organized into distinct Maven modules:

### 📦 Module Structure
```
bytehot/
├── java-commons/              # Shared utilities and patterns
├── bytehot-domain/           # Pure business logic
│   ├── src/main/java/org/acmsl/bytehot/domain/
│   │   ├── events/          # Domain events
│   │   ├── *.java          # Aggregates, entities, value objects
│   │   └── *Port.java      # Secondary ports (interfaces)
│   └── docs/               # Domain documentation
├── bytehot-application/      # Use cases and orchestration
│   ├── src/main/java/org/acmsl/bytehot/application/
│   │   └── ByteHotApplication.java
│   └── docs/               # Application documentation
├── bytehot-infrastructure/   # External adapters
│   ├── src/main/java/org/acmsl/bytehot/infrastructure/
│   │   ├── agent/          # JVM agent implementation
│   │   ├── config/         # Configuration adapters
│   │   ├── filesystem/     # File system adapters
│   │   └── events/         # Event emission adapters
│   └── docs/               # Infrastructure documentation
└── bytehot/                 # Legacy module (being phased out)
```

### 🎯 Clean Architecture
```
┌─────────────────────────────────────┐
│      bytehot-infrastructure        │
│  ┌─────────────────────────────┐   │
│  │      bytehot-application    │   │
│  │  ┌─────────────────────┐   │   │
│  │  │   bytehot-domain    │   │   │
│  │  │  • ByteHot Core     │   │   │
│  │  │  • Instance Mgmt    │   │   │
│  │  │  • Error Handling   │   │   │
│  │  └─────────────────────┘   │   │
│  └─────────────────────────────┘   │
│  • File System   • JVM Agent      │
│  • Configuration • Event Emission  │
└─────────────────────────────────────┘
```

### 🔌 Ports & Adapters
- **ConfigurationPort/Adapter**: Multiple configuration sources (YAML, properties, environment)
- **FileWatcherPort/Adapter**: File system monitoring with pattern matching and recursion  
- **InstrumentationPort/Adapter**: JVM instrumentation with safety checks and error handling
- **EventEmitterPort/Adapter**: Event emission to multiple targets (console, files, both)
- **EventStorePort/Adapter**: EventSourcing persistence with filesystem storage
- **Dynamic Discovery**: Automatic adapter detection and injection at runtime

### ⚡ EventSourcing & User Management
- **Complete Event History**: Every operation captured as immutable domain events
- **Time-Travel Debugging**: Reproduce any issue by replaying exact event sequences
- **User-Aware Operations**: Automatic user discovery from Git configuration and environment
- **Session Management**: Track development sessions with environment snapshots
- **Analytics & Insights**: Personal productivity metrics and usage analytics

## 🚀 Quick Start

### Installation

1. **Download the ByteHot agent JAR**:
```bash
wget https://github.com/rydnr/bytehot/releases/latest/download/bytehot-infrastructure-agent.jar
```

2. **Add to your JVM startup**:
```bash
java -javaagent:bytehot-infrastructure-agent.jar -jar your-application.jar
```

**Or build from source:**
```bash
git clone https://github.com/rydnr/bytehot.git
cd bytehot
mvn clean package
# Agent JAR will be at: bytehot-infrastructure/target/bytehot-infrastructure-*-agent.jar
```

### Configuration

Create a `bytehot.yml` configuration file:

```yaml
bytehot:
  watch:
    - path: "target/classes"
      patterns: ["*.class"]
      recursive: true
    - path: "build/classes"  
      patterns: ["*.class"]
      recursive: true
```

Or use environment variables:
```bash
export BYTEHOT_WATCH_PATHS="target/classes,build/classes"
export BYTEHOT_WATCH_RECURSIVE=true
```

### Your First Hot-Swap

1. **Start your application** with ByteHot agent
2. **Make a change** to your Java code
3. **Compile** the changed class
4. **Watch the magic** - your changes are live immediately!

```java
// Before: 
public String greet() {
    return "Hello World";
}

// After: Save and compile - no restart needed!
public String greet() {
    return "Hello ByteHot! 🔥";
}
```

## 🎮 The Developer Experience

### 🔄 The Inner Loop Accelerated
- Make a change → Save the file → See it running immediately
- No restart, no lost state, no broken flow

### 🐛 The Debugging Renaissance  
- Add logging statements without restarting
- Modify method behavior while debugging
- Preserve breakpoint state across changes
- Keep your debugging session alive

### 🧪 The Testing Revolution
- **Event-Driven Testing**: Revolutionary Given/When/Then framework using actual domain events
- **Bug Reproduction**: Automatic test case generation from historical event sequences
- **No More Mocks**: Test with real events instead of artificial mock objects
- **Living Documentation**: Tests serve as executable documentation of system behavior
- Modify test implementations on the fly and maintain test state across iterations

## 🏢 Production Ready

### ⚡ Zero-Downtime Operations
- Apply hotfixes without service interruption
- Fix critical bugs in running production systems  
- Update configurations without restart
- Maintain service availability during changes

### 📊 Operational Excellence
- Comprehensive audit trails of all changes
- Performance metrics for every operation
- Automated recovery from failures
- Integration with monitoring and alerting

### 🔒 Enterprise Security
- Rollback capabilities for all operations
- Snapshot-based transaction guarantees
- Conflict resolution strategies
- Complete audit trails for compliance

## 🛠️ Technical Highlights

### 🔧 JVM Integration
- Deep integration with the Instrumentation API
- Careful bytecode validation and compatibility checking
- Sophisticated class redefinition coordination  
- Memory-efficient operation with minimal overhead

### ⚡ Concurrency Mastery
- Thread-safe instance tracking with weak references
- Lock-free metrics collection for performance
- Atomic operations for consistency
- Coordinated updates across multiple threads

### 🌐 Framework Understanding
- Deep knowledge of Spring's proxy mechanisms
- CDI contextual instance management
- Guice binding and provider coordination
- Graceful fallback to reflection when frameworks aren't available

## 📖 Documentation

- **[Architecture Story](story.org)** - The complete vision and narrative
- **[Development Journal](journal.org)** - Technical implementation details
- **[API Documentation](docs/)** - Comprehensive class documentation
- **[Configuration Guide](docs/configuration.md)** - Setup and configuration options

## 🚀 Development

ByteHot follows strict **Test-Driven Development** with **Domain-Driven Design**:

```bash
# Run tests
mvn test

# Build the project  
mvn package

# Run with ByteHot agent
java -javaagent:target/bytehot-agent.jar -jar your-app.jar
```

### 🧪 Testing Philosophy
- **🧪 Red**: Write failing tests first
- **✅ Green**: Implement minimal code to pass
- **🚀 Refactor**: Clean up and optimize
- **📚 Document**: Maintain literate programming docs

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guidelines](CONTRIBUTING.md).

### 🏗️ Architecture Principles
1. **Domain Purity**: Keep business logic free of infrastructure concerns
2. **Hexagonal Design**: Use ports and adapters for all external dependencies
3. **Event-Driven**: Communicate through immutable domain events
4. **Test-First**: Write tests before implementation
5. **Documentation**: Maintain literate programming documentation

## 📄 License

ByteHot is licensed under the [GNU General Public License v3.0](LICENSE).

## 🌟 The Future Vision

ByteHot represents the future of Java development:

- **Beyond Hot-Swap**: Dynamic feature toggling, runtime optimization
- **Ecosystem Integration**: IDE deep integration, build tool coordination  
- **Developer Empowerment**: Faster feedback loops, reduced friction
- **Self-Healing Applications**: Adaptive behavior based on usage patterns

---

## 💡 Why ByteHot?

> *"The best way to predict the future is to invent it." - Alan Kay*

In a world where software needs to evolve continuously, where uptime is critical, and where developer productivity determines business success, ByteHot provides the foundation for the next generation of Java applications.

**ByteHot: Inventing the future of Java runtime evolution.**

---

<div align="center">

**⭐ Star this repository if ByteHot is changing your Java development experience!**

[🚀 Get Started](#quick-start) | [📖 Documentation](docs/) | [🤝 Contributing](CONTRIBUTING.md) | [💬 Discussions](https://github.com/rydnr/bytehot/discussions)

</div>