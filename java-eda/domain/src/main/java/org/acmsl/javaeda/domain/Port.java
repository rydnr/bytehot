package org.acmsl.javaeda.domain;

/**
  Represents a primary or secondary port.

  Class name: Port

  Responsibilities:
    - They are interfaces that get implemented by adapters in the infrastructure layer.
    - Port implementations interact with the outside.

  Collaborators:
    - Adapter implementations in the infrastructure layer.
    - Application that resolve Ports with adapters when running the bounded context.
    - Ports maintain a registry of Port instances.
 */
@SuppressWarnings("PMD.ShortClassName")
public interface Port {
    /**
     * Checks if the port is enabled.
     * @return <code>true</code> in such case.
     */
    boolean enabled();

    /**
     * Initializes the port.
     */
    void initialize();
}
