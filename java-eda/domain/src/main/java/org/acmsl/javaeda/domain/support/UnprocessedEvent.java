package org.acmsl.javaeda.domain.support;

import org.acmsl.javaeda.domain.Event;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
  Represents events that couldn't be processed.

  Class name: UnprocessedEvent

  Responsibilities:
    - Represent events that couldn't be processed.

  Collaborators:
    - org.acmsl.javaeda.infrastructure.events.ControlEvent
 */
public interface UnprocessedEvent<E extends Event> extends ControlEvent {
    /**
     * Retrieves the original event.
     * @return such instance.
     */
    @NonNull
    E getOriginal();
}
