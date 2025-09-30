package org.acmsl.javaeda.infrastructure.eventstore;

import org.acmsl.javaeda.domain.Event;
import org.acmsl.javaeda.domain.EventEntityClassResolver;
import org.acmsl.javaeda.domain.EventEntityClassResolvers;
import org.acmsl.javaeda.domain.EventStore;
import org.acmsl.javaeda.domain.PrimaryKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
  An in-memory event store.

  Class name: InMemoryEventStore

  Responsibilities:
    - Manage the persistence of domain events in memory.

  Collaborators:
    - None
*/
public class InMemoryEventStore implements EventStore {
    /**
     * The list of events per entity.
     */
    private final Map<String, List<Event>> eventMap;

    /**
     * Creates a new instance.
     */
    public InMemoryEventStore() {
        super();
        this.eventMap = new HashMap<>();
    }

    /**
     * Initializes the instance.
     */
    @Override
    public void initialize() {}


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean enabled() {
        return true;
    }

    /**
     * Builds a key containing the entity class and the primary key.
     * @param entityClass the entity class.
     * @param primaryKey the primary key.
     * @return a key.
     */
    @NonNull
    protected String buildKey(@NonNull final Class<?> entityClass, @NonNull final PrimaryKey primaryKey) {
        return String.format("%s.%s", entityClass.toString(), primaryKey.toString());
    }

    /**
     * Stores a new event.
     * @param event the event to store.
     */
    @Override
    public void store(@NonNull final Event event) {
        final EventEntityClassResolver resolver = EventEntityClassResolvers.getInstance().load(Thread.currentThread().getContextClassLoader());
        final Class<?> entityClass =
            resolver.resolvePolymorphic(event.getClass())
            .orElseThrow(() -> new IllegalStateException("No owning entity for " + event.getClass()));
        final PrimaryKey primaryKey = event.getPrimaryKey();
        final List<Event> events;
        final String key = buildKey(entityClass, primaryKey);
        if (this.eventMap.containsKey(key)) {
            events = this.eventMap.get(key);
        } else {
            events = new ArrayList<>();
            this.eventMap.put(key, events);
        }
        events.add(event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public List<Event> read(@NonNull final Class<?> entityClass, @NonNull final PrimaryKey primaryKey) {
        final List<Event> result;
        final String key = buildKey(entityClass, primaryKey);
        if (this.eventMap.containsKey(key)) {
            result = this.eventMap.get(key);
        } else {
            result = List.of();
        }
        return result;
    }
}
