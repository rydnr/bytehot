ByteHot is a JVM agent that enables bytecode hot-swapping at runtime. This guide describes its modular architecture (DDD + Hexagonal), coding conventions, test practices, and contribution standards.

# Design and style guidelines

This file provides guidance when working with this codebase.

## Modules

This project consists of different modules following Domain-Driven Design and Hexagonal Architecture:
- `/pom.xml`: The parent pom, that centralizes version management and configuration of Maven plugins.
- `/java-commons`: Support project. Used to save time and give consistency, but not the main focus of this repository.
- `/java-commons-infrastructure`: Support for infrastructure classes.
- `/javaeda-domain`: Support for domain classes.
- `/javaeda-infrastructure`: Application-unaware adapters.
- `/javaeda-application`: Core application layer that discovers infrastructure adapters and routes events.
- `/bytehot-domain`: Bytehot domain with business rules and domain events.
- `/bytehot-application`: Bytehot application layer.
- `/bytehot-infrastructure`: Bytehot-specific infrastructure adapters for external systems and JVM agent.


Note: Only `bytehot-*` modules are really part of this project. The rest of them are included temporarily to simplify the development.

## ByteHot project Structure

### Overall layout

Each ByteHot module follows the standard (Maven) folder structure:
- `/src`: Source files.
  - `/main`: Application sources.
    - `/java`: Java source code.
    - `/resources`: Configuration files and internationalization.
   - `/test`: Test sources.

- The agent JAR is built from the application module (`bytehot-application`).
- The infrastructure module cannot have a dependency to the application module.
- Infrastructure adapters only know about the `Application` interface from `java-commons`.
- The "orchestrator" is the application layer, not the infrastructure.
- The `-agent` JAR includes all dependencies and is created at `bytehot-application/target/bytehot-application-latest-SNAPSHOT-agent.jar`.

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

### Domain classes

The domain classes cannot include anything but immutable POJOs, and no annotations besides Lombok's, Dagger's, or custom annotations from the Application layer. The reason is to leverage DDD's benefit of eliminating accidental complexity from the domain layer. Lombok's is an exception because it's convenient (I see it as a language feature), and Dagger's because it's indirectly part of the Application layer, to resolve the Adapter when a Port is requested.

This is important. DDD and Hexagonal are used so that all functional behavior is kept under the domain (essential complexity), and it's isolated from the evolution of the technologies used to communicate with the outside world, which change at another pace.

There's an important class in the domain layer: org.acmsl.bytehot.domain.Ports. This class is able to resolve the actual implementation of a given Port. When a domain object needs to communicate with a Port (i.e., an interface extending org.acmsl.commons.patterns.Port), it asks for the concrete implementation to Ports#resolve([concrete Port interface]). This class doesn't configure itself. It just maintains a mapping.
Aggregates

Aggregates are just domain classes that accept incoming events: they are primary ports. The method signature is:

```java
public static DomainResponseEvent<[class of the incoming event]> accept(final [class of the incoming event] event) {
    /*
     - 1. Obtain an instance of the aggregate usually from the information contained in the incoming event
     - 2. Call a method of the instance with the information contained in the incoming event
     - 3. Return a list of events from the result of calling the previous method
     */
}
```

### Domain events

Domain events are immutable POJOs with no behavior. They contain just the relevant context, or snapshot, and maintain a reference to the previous event, if they are causally related. The convention used in this project is that events are idempotent: the same incoming event does not produce redundant side effects, and returns the same original event(s) as the first time. However, if the domain logic does not succeed, the resulting event reflects that. Incoming events extend from org.acmsl.commons.patterns.DomainEvent, and resulting events extend from org.acmsl.commons.patterns.DomainResponseEvent. Each DomainEvent result in zero or more DomainResponseEvent. They may reflect a success or a failure. Failures are also considered (and designed as) events.

### Application layer

This layer consists on only one package: `org.acmsl.bytehot.application`

The application layer should be minimal and stable and require little to no changes over time, but it's crucial. It has a few responsibilities:

- for each port, detect compatible adapters in the infrastructure layer and inject them into the port;
- routing events from the outside to primary ports in the domain layer;
- detecting secondary ports in the infrastructure.

There are additional classes, but the core class is org.acmsl.bytehot.application.ByteHotApplication. This class provides methods that accept a domain event, routes it to the domain layer, and emits the resulting events, if any. Emitting an event means forwarding it to secondary adapters registered in the infrastructure layer.
Infrastructure layer

This layer provides actual implementations of Ports and Repositories. While they must respect the Ports' interfaces, they are technology-driven. They deal with the accidental complexity. This layer has more freedom regarding its structure. However, there's a general layout.

- `org.acmsl.bytehot.infrastructure`: Base package.
- `org.acmsl.bytehot.infrastructure.cli`: Entrypoints from the command-line. Classes in this package deal with parsing and validating command-line parameters and flags. Command-line parameters are grouped around domain events, and each class should handle one event. They should be able to coexist, and handle only the parameters relevant to the event they deal with. Once they can build an event instance, they only call the `ByteHotApplication#accept(event)`.

A diagram showing the flow triggered by the command-line:

```
[ JVM with the ByteHot agent]- -(arguments) ->[ ByteHotCLI ]- -(event)- ->[ ByteHotApplication.accept() ]- -(event)- ->[ ByteHot.accept() ]- -(events)- ->[ DbusEmitter.emit() ]
```

## Persistence

This project, if persistence is needed, uses Event Sourcing. In that case all repositories should store events, and include a sequential number to represent the version of the entity. The repository implementations are not defined in advance, but if they interact with external services (EventStore, SQLite, Redis, PostgreSQL, to cite a few), the repository must provide a Docker Compose file to run them.

If the domain logic eventually requires performing queries, repositories will adopt CQRS: all queries will be performed in a read database. Obviously, the infrastructure implementation of the repository would need to trigger synchronization mechanisms to make the read database eventually consistent with the write database (which uses EventSourcing).
Boundaries

There is tension between Domain and Infrastructure. While we are defending Domain from accidental complexity, sometimes the Domain is about technology, and that might be confusing. In general, Domain won't deal with files, sockets, URLs, or APIs. ByteHot watches files and folders for changes, so the files and folders being watched belong to the domain. However, how the configuration is provided (a yaml file, via environment variables, or by sending messages to a server port) might not. The rule of thumb is: if the Infrastructure layer contains code we need to know to explain the Domain's behavior, then it should be moved to the Domain. We cannot afford a simple Domain at the expense of making it incomplete or inconsistent. Infrastructure should grow as we add more adapters, not as we add more functional features.
Methodology

## TDD

ByteHot uses TDD and makes the process explicit in Git and Github. Note: by "naive" I mean returning exactly what the test will verify, with no actual business logic involved.

For a new feature, create an issue.
Once a new test is written and it's failing, a new commit is created. The commit message starts with the :test-tube: emoji, a blank space, and the issue reference inside square brackets.
Once the test is passed, a new commit is created. If it's a naive (stubbed) implementation, the commit message starts with the :thinking-face: emoji. Otherwise, we'll use the "White Heavy Check Mark" emoji. After it, a blank space and the issue reference inside square brackets.
Now, if the code needs refactoring, a new commit is created. The commit message starts with the :rocket: emoji, a blank space, and the issue reference inside square brackets.

In summary,

| Emoji                 | Meaning                | When to Use                      |
| 🧪 :test-tube:        | A new failing test     | After test is added              |
| 🤔 :thinking-face:    | Naive implementation   | When test passes trivially       |
| ✅ :white-check-mark: | Working implementation | When test passes with real logic |
| 🚀                    | Refactor               | Improving code after green       |

## Versioning

Tags should follow the semantic versioning recommendations, but leaving the patch group to CI/CD tools. Versions should not include the `v` prefix.

# Documentation

All source code should be generated from their org-mode-formatted literate-programming document, describing the class.
Before each commit, ensure the associated org-mode documents are up-to-date.

### Documentation Requirements
- Don't use markdown, only org-mode format, but in the README.md file.

# Coding conventions

## General conventions

All files must include the GPLv3 preamble at the beginning.

## Java conventions

From top to bottom, the organization of a Java file must follow this convention:

```java
/*
                        ByteHot

    Copyright (C) 2025-today  rydnr@acm-sl.org

    [GPLv3 preamble]
```

Now a the CRC card just before the package directive.

CRC cards summarize class responsibilities and collaborators at a glance. They help with understanding and maintainability, especially in DDD codebases.

Use the following this template:

```java
 ******************************************************************************
 *
 * Filename: [class name].java
 *
 * Author: [name of the IA]
 *
 * Class name: [class name]
 *
 * Responsibilities:
 *   - [Brief description of the responsibily]
 *
 * Collaborators:
 *   - [name of the collaborator class]: [Brief description of the collaboration]
 */
```

After the package, the imports should be grouped:

- First, bytehot classes (packages starting with `org.acmsl.bytehot`), alphabetically.
- A newline.
- Second, non Java dependencies.
- A newline.
- Finally, Java dependencies.

Imports must not use wildcards. Static imports are not allowed either.

Then, the class comment:

```
/**
 * [Main responsibility or summary of why the class or interface exists]
 * @author [name of the IA]
 * @since [date when it was created]
 */
```

Afterwards, Lombok class annotations, sorted, if the class has state.

For each attribute, if any, a Javadoc comment, the @Getter annotation from Lombok, and the attribute declaration:

```java
/**
 * [attribute description]
 */
@Getter
private final [type or class] [attribute];
[mandatory newline]
```

Other considerations:

- No data/record classes are allowed. Classes should model behavior, not only data. Events or Exceptions might be corner cases to this general rule, which is a guide, not an obligation.
- Also, classes should not be stateless. They should encapsulate state and behavior.
- No private methods or constants are allowed. `protected` to preserve encapsulation. `private` methods or constants violate the Open-Closed Principle in SOLID.
- Plugins and other side developments must be isolated in their own module or repository.
- Classes can include (static) Factory Methods to create instance of the enclosing class from other information sources.
- Classes extending other classes should add a new line before the extends, and use 4-space indentation.
- Classes implementing interfaces should add a new line before the implements, and use 4-space indentation. All interfaces can be added to the same line.
- `extends` precede `implements`.
- Indentation style follows K&R.
- Method parameters are declared `final` and are annotated with Checker annotations (`@Notnull`, `@Nullable`, etc.).
- Exceptions in `catch` blocks are declared final.
- Use latest Java syntax and idioms.
- All Enums must be defined in a class of their own. Each enum value must be documented.
- Source code must not use `org.jetbrains.annotations` classes.
- All methods should be annotated with `checkerframework` annotations for parameters and return values.

### YAML Conventions
- No heredocs are allowed in YAML files. Use separate scripts for non-trivial steps.

### Dependency Management
- Use `maven-enforcer-plugin` to ensure `bytehot-domain` can only depend on java-commons and not on other libraries that serialize to xml or json, for example.
- Child poms cannot define versions for dependencies, only parent pom can, in the `<dependencyManagement>` section.

### CI/CD Workflows
- CI/CD workflows shouldn't create complete documents on the fly. Instead, they should convert existing files and do certain transformations on them if necessary. Exceptions are headers and footers, css, javascript, or index files.
- CI/CD pipeline definitions should delegate all logic to scripts. Don't use inline shell snippets.
- Github Action workflows should rely each non-trivial step on external scripts (under `.github/scripts`).
- Github Action workflows should not be redundant. One should address the Github Pages documentation, another the Continuous Integration, another the Release generation. There must not be variants of any of them. All changes should be focused on improving the existing workflows.
- **All Github Actions workflows should fail fast, instead of trying to find non-optimal workarounds.**
- All Github Pages content should define the CSS (provided by `.github/scripts/css.sh`), header (provided by `.github/scripts/header.sh`), footer (provided by `.github/scripts/footer.sh`) and the Matrix style (provided by `.github/scripts/matrix.sh`).

## Dependency Management

- Versions in maven plugins in `pom.xml` files should be defined as variables defined in the top-level pom.xml, to make sure all modules use the same versions.