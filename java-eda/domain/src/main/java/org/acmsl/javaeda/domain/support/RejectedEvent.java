package org.acmsl.javaeda.domain.support;

import org.acmsl.javaeda.domain.Event;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
  Represents events that have been rejected.

  Class name: RejectedEvent

  Responsibilities:
    - Represent events that have been rejected.

  Collaborators:
    - org.acmsl.javaeda.infrastructure.events.BaseUnprocessedEvent
 */
public class RejectedEvent<E extends Event> extends BaseUnprocessedEvent<E> {
    /**
     * Creates a new instance.
     * @param original the original event.
     */
    public RejectedEvent(@NonNull final E original) {
        super(original);
    }
}
