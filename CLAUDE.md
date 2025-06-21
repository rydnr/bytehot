ByteHot is a JVM agent that enables bytecode hot-swapping at runtime. This guide describes its modular architecture (DDD + Hexagonal), coding conventions, test practices, and contribution standards.

# Design and style guidelines

This Agents.md file provides guidance when working with this codebase.

## Modules

This project consists of two modules:
- `/java-commons`: Support project. Used to save time and give consistency, but not the main focus of this repository.
- `/bytehot`: The main project: a JVM agent to refresh bytecode at runtime (*hot-swap*).

## ByteHot project Structure

### Overall layout

The ByteHot module follows the standard (Maven) folder structure:
- `/src`: Source files.
  - `/main`: Application sources.
    - `/java`: Java source code.
    - `/resources`: Configuration files and internationalization.
   - `/test`: Test sources.

### Domain-Driven Design and Hexagonal Architecture layout

ByteHot project uses a strict folder structure to respect the boundaries of the three layers: Domain, Application and Infrastructure:

- `/src/main/java/org/acmsl/bytehot/domain`: The Domain layer.
- `/src/main/java/org/acmsl/bytehot/application`: The Application layer.
- `/src/main/java/org/acmsl/bytehot/infrastructure`: The Infrastructure layer.

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