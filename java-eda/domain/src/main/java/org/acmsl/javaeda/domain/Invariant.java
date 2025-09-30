package org.acmsl.javaeda.domain;

import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
  Represents an invariant.

  Class name: Invariant

  Responsibilities:
    - Represent an invariant.

  Collaborators:
    - None
 */
public interface Invariant<T> {
    /**
     * Retrieves the invariant name.
     * @return the name.
     */
    @NonNull
    String getName();

    /**
     * Retrieves the invariant value.
     * @return the value.
     */
    @NonNull
    Optional<T> getValue();
}
