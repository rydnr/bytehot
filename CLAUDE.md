ByteHot is a JVM agent that enables bytecode hot-swapping at runtime. This guide describes its modular architecture (DDD + Hexagonal), coding conventions, test practices, and contribution standards.

# Design and style guidelines

This Agents.md file provides guidance when working with this codebase.

## Modules

This project consists of four modules following Domain-Driven Design and Hexagonal Architecture:
- `/java-commons`: Support project. Used to save time and give consistency, but not the main focus of this repository.
- `/bytehot-domain`: Pure domain logic with business rules and domain events.
- `/bytehot-application`: Application layer orchestrating use cases and workflow.
- `/bytehot-infrastructure`: Infrastructure adapters for external systems and JVM agent.

## ByteHot project Structure

### Overall layout

Each ByteHot module follows the standard (Maven) folder structure:
- `/src`: Source files.
  - `/main`: Application sources.
    - `/java`: Java source code.
    - `/resources`: Configuration files and internationalization.
   - `/test`: Test sources.

### Domain-Driven Design and Hexagonal Architecture layout

ByteHot project uses a strict module structure to respect the boundaries of the three layers:

- `/bytehot-domain/src/main/java/org/acmsl/bytehot/domain`: The Domain layer.
- `/bytehot-application/src/main/java/org/acmsl/bytehot/application`: The Application layer.
- `/bytehot-infrastructure/src/main/java/org/acmsl/bytehot/infrastructure`: The Infrastructure layer.

Note on primary and secondary ports:

- Primary Port (Domain): Entry point for domain logic (e.g., Aggregates receiving events).
- Primary Adapter (Infrastructure): An element in the infrastructure layer that interacts with the outside and reacts upon new external events.
- Secondary Port (Domain): Interface the domain uses to communicate with outside world, because the domain is requesting something from the outside.
- Secondary Adapter (Infrastructure): Implementation of a secondary port.

Following the *DDD* terminology, we use the term "primary port" to identify points in which the domain receives events from the outside. We use also the term "primary port" to the instance in the infrastructure layer responsible of interacting with the outside, gathering information, and packaging it as an incoming domain event, even though technically is a *primary adapter*. "Secondary ports" are infrastructure entities that communicate with the outside world, as requested by the Domain layer. From the domain perspective, any interaction comes from a domain event, and always produces cero, one or more events.

From a code standpoint, a "Port" is an interface in the Domain layer, and an "Adapter" is a class in the Infrastructure layer that implements a "Port", and gets injected in the Domain by the Application layer (and later available through the `#resolve(Port)` method in the `Ports` class).

## Memories

### Documentation Requirements
- Don't use markdown, only org-mode format, but in the README.md file.

### Versioning
- Tags should follow the semantic versioning recommendations, but leaving the patch group to CI/CD tools. Versions should not include the `v` prefix.

### YAML Conventions
- No heredocs are allowed in YAML files.

### Development Workflow
- Before each commit, make sure the literate programming documentation is synchronized with the updated code.

### Architecture Notes
- The agent JAR is built from the application module (bytehot-application).
- The infrastructure module cannot have a dependency to the application module.
- Infrastructure adapters only know about the `Application` interface from java-commons.
- The "orchestrator" is the application layer, not the infrastructure.
- The `-agent` JAR includes all dependencies and is created at `/bytehot-application/target/bytehot-application-latest-SNAPSHOT-agent.jar`.
- Infrastructure adapters discover and invoke the application layer through reflection following hexagonal architecture principles.

### Module Dependencies
- `bytehot-domain`: Only depends on `java-commons` and has no external dependencies.
- `bytehot-infrastructure`: Depends on `bytehot-domain` and `java-commons` (runtime dependency on application for agent JAR only).
- `bytehot-application`: Depends on `bytehot-domain` and `bytehot-infrastructure` (and `java-commons` if needed).

### Dependency Management
- Use maven-enforcer-plugin to ensure bytehot-domain can only depend on java-commons and not on other libraries that serialize to xml or json, for example.

### CI/CD Workflows
- CI/CD workflows shouldn't create complete documents on the fly. Instead, they should convert existing files and do certain transformations on them if necessary. Exceptions are headers and footers, css, javascript, or index files.
- CI/CD pipeline definitions should delegate all logic to scripts. Don't use inline shell snippets.
- Github Action workflows should rely each non-trivial step on external scripts (under .github/scripts).
- Github Action workflows should not be redundant. One should address the Github Pages documentation, another the Continuous Integration, another the Release generation. There must not be variants of any of them. All changes should be focused on improving the existing workflows.
- **All Github Actions workflows should fail fast, instead of trying to find non-optimal workarounds.**

### Import Guidelines
- No static imports or wildcard imports are allowed. If possible, group imports by their top-level groups.

### Github Pages Requirements
- All Github Pages content should define the CSS (provided by .github/scripts/css.sh), header (provided by .github/scripts/header.sh), footer (provided by .github/scripts/footer.sh) and the Matrix style (provided by .github/scripts/matrix.sh).

### Code Quality
- Source code must not use org.jetbrains.annotations classes.
- All methods should be annotated with checkerframework annotations.

### Enum Definitions
- All Enums must be defined in a class of their own. Each enum value must be documented.

### Class Modeling
- No data/record classes are allowed. Classes should model behavior, not only data. Events or Exceptions might be corner cases to this general rule, which is a guide, not an obligation.
- Also, classes should not be stateless. They should encapsulate state and behavior.

### Development Principles
- No private methods or constants are allowed. `protected` to preserve encapsulation. `private` methods or constants violate the Open-Closed Principle in SOLID.
- Plugins and other side developments must be isolated in their own module or repository.

### Specification Management
- Don't move the specifications to specify modules or repositories. Copy them instead. They're useful also for the overall roadmap.
