package org.acmsl.javaeda.domain.support;

import org.acmsl.javaeda.domain.Event;
import org.acmsl.javaeda.domain.Port;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
  Represents a secondary port.

  Class name: SecondaryPort

  Responsibilities:
    - Emits events to the outside.

  Collaborators:
    - None
 */
public interface SecondaryPort extends Port {
    /**
     * Emits an event.
     * @param event the event.
     */
    void emit(@NonNull Event event);

    /**
     * Rejects an event.
     * @param event the event.
     */
    void reject(@NonNull DeferredEvent<?> event);
}
