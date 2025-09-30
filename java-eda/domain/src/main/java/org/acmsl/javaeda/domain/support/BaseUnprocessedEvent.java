package org.acmsl.javaeda.domain.support;

import org.acmsl.javaeda.domain.Event;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
  Base class for unprocessed events.

  Class name: BaseUnprocessedEvent

  Responsibilities:
    - Common attributes from unprocessed events.

  Collaborators:
    - org.acmsl.javaeda.infrastructure.events.UnprocessedEvent
 */
public abstract class BaseUnprocessedEvent<E extends Event> implements UnprocessedEvent<E> {
    /**
     * The original event.
     */
    private final E original;

    /**
     * Creates a new instance.
     * @param original the original event.
     */
    public BaseUnprocessedEvent(@NonNull final E original) {
        this.original = original;
    }

    /**
     * Retrieves the original event.
     * @return such instance.
     */
    @Override
    @NonNull
    public E getOriginal() {
        return this.original;
    }
}
