package org.acmsl.javaeda.domain.support;

import org.acmsl.javaeda.domain.Event;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
  Represents events that need to be deferred because they seem to be out of order.

  Class name: DeferredEvent

  Responsibilities:
    - Represent events that need to be deferred.

  Collaborators:
    - org.acmsl.javaeda.infrastructure.events.BaseUnprocessedEvent
*/
public class DeferredEvent<E extends Event> extends BaseUnprocessedEvent<E> {
    /**
     * Creates a new instance.
     * @param original the original event.
     */
    public DeferredEvent(@NonNull final E original) {
        super(original);
    }
}
