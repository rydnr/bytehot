package org.acmsl.javaeda.domain;

import org.acmsl.javaeda.annotations.Apply;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.UUID;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
  The base event.

  Class name: Event

  Responsibilities:
    - Represents a change in the system state.
    - It's the only way to communicate among EDA domains.

  Collaborators:
    - EventEmitter: Emits Events.
    - EventListener: Listens to Events.
*/
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public interface Event {
    /**
     * Retrieves the timestamp.
     * @return such information.
     */
    @SuppressWarnings("unused")
    @NonNull
    Instant getTimestamp();

    /**
     * Retrieves the id.
     * @return such information.
     */
    UUID getId();

    /**
     * Checks if it represents an error.
     * @return {@code true} in such case.
     */
    default boolean isError() {
        return false;
    }

    /**
     * Retrieves the event version.
     * @return such version.
     */
    long getVersion();

    /**
     * Retrieves the identifier of the entity.
     * @return such identifier.
     */
    @SuppressWarnings("unused")
    @Nullable
    UUID getEntityId();

    /**
     * Retrieves the expected version of the entity receiving this event.
     * @return such version.
     */
    @SuppressWarnings("unused")
    long getEntityVersion();

    /**
     * Retrieves the primary key of the associated entity.
     * @return such primary key.
     */
    @NonNull
    PrimaryKey getPrimaryKey();

    /**
     * Retrieves the ids of the previous events.
     * @return such information.
     */
    @NonNull
    List<UUID> getPreviousEvents();

    /**
     * Retrieves the ids of all events including to this one.
     * @return such list.
     */
    @NonNull
    default List<UUID> getEventHistory() {
        return Stream.concat(getPreviousEvents().stream(), Stream.of(getId())).collect(Collectors.toList());
    }

    /**
     * Retrieves the invariants (tenant, profile, etc.).
     * @return such information.
     */
    @SuppressWarnings("unused")
    @NonNull
    List<? extends Invariant<?>> getInvariants();

    /**
     * Dispatches this event to the entity.
     * @param entity the entity.
     */
    default void dispatch(@SuppressWarnings("unused") @NonNull final Entity entity) {}

    /**
     * Double-dispatch mechanism to make given entity apply this event.
     * @param entity the entity.
     */
    @SuppressWarnings({"unused","PMD.AvoidAccessibilityAlteration","PMD.AvoidThrowingRawExceptionTypes"})
    default void applyTo(@NonNull final Entity entity) {
        final Class<?> entityClass = entity.getClass();
        final Class<?> eventClass = this.getClass();
        boolean found = false;
        for (final Method method : entityClass.getMethods()) {
            final Apply annotation = method.getAnnotation(Apply.class);
            if (annotation != null
                && method.getParameterCount() == 1
                && method.getParameterTypes()[0].equals(eventClass)) {

                try {
                    method.setAccessible(true);
                    method.invoke(entity, this);
                    found = true;
                } catch (final IllegalAccessException | InvocationTargetException exception) {
                    throw new RuntimeException("Failed to invoke annotated @Apply method " + method, exception);
                }
            }
        }

        if (!found) {
            // ❌ No method found: treat as error
            throw new IllegalStateException(
                String.format(
                    "No @Apply method found in %s for event %s",
                    entityClass.getSimpleName(), eventClass.getSimpleName()));
        }
    }
}
