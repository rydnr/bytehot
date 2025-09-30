package org.acmsl.javaeda.domain;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import java.time.Instant;

/**
  A value object.

  Class name: ValueObject

  Responsibilities:
    - Represents a cohesive set of attributes.
    - Subclasses can add validations.

  Collaborators:
    - None
 */
public interface ValueObject {

    /**
     * Retrieves when I was created.
     * @return Such instant.
     */
    @NonNull
    Instant created();

    /**
     * Retrieves when I was removed.
     * @return Such instant.
     */
    @Nullable
    Instant removed();
}
