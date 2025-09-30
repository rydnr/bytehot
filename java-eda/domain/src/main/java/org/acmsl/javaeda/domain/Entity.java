package org.acmsl.javaeda.domain;

import java.util.UUID;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
  Represents an Entity: something meaningful, unique, with set of attributes, and, in good designs, behavior.

  Class name: Entity

  Responsibilities:
    - Defines uniqueness constraints.
    - A cohesive group of related attribute values.
    - Specifies attributes, including primary key's and those used for filtering, using ValueObject's decorators.

  Collaborators:
    - ValueObject: Parent class.
    - Repo: To rebuild me from persistence layers.
 */
public interface Entity extends ValueObject {
    /**
     * Retrieves the id.
     * @return such information.
     */
    @NonNull UUID getId();

    /**
     * Retrieves the version.
     * @return such information.
     */
    long getVersion();

    /**
     * Retrieves the primary key.
     * @return such information.
     */
    @NonNull
    PrimaryKey getPrimaryKey();

    /**
     * Checks if this instance as been removed.
     * @return such information.
     */
    boolean isRemoved();
}
