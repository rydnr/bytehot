package org.acmsl.javaeda.domain;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
  Represents a primary port.

  Class name: PrimaryPort

  Responsibilities:
    - Receives stimulus from the outside.

  Collaborators:
    - None
 */
public interface PrimaryPort extends Port {
    /**
     * Accepts an application.
     * @param application the application.
     */
    void accept(@NonNull Application application);
}
