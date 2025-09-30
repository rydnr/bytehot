package org.acmsl.javaeda.domain;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
  An event that can be rejected.

  Class name: RejectableEvent

  Responsibilities:
    - Represent an event that can be rejected.

  Collaborators:
    - org.acmsl.javaeda.domain.support.ListenRegistry
 */
public interface RejectableEvent<E extends Event> extends Event {
    /**
     * Creates a rejected event.
     * @return such event.
     */
    @NonNull
    E reject();
}
