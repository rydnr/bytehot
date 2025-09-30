package org.acmsl.javaeda.domain;

import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
  The way to store events permanently.

  Class name: EventStore

  Responsibilities:
    - Manage the persistence of domain events.

  Collaborators:
    - None
*/
public interface EventStore extends Port {
    /**
     * Stores a new event.
     * @param event the event to store.
     */
    void store(@NonNull Event event);

    /**
     * Retrieves all events for an entity matching given primary key.
     * @param entityClass the class of the entity.
     * @param primaryKey the key of the entity.
     * @return the list of previous events for that entity.
     */
    @NonNull
    List<Event> read(@NonNull Class<?> entityClass, @NonNull PrimaryKey primaryKey);
}
