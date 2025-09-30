package org.acmsl.javaeda.domain;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
  Resolves the entity class associated to each Event.

  Class name: EventEntityClassResolver

  Responsibilities:
    - Resolve the entity class for a given event class.

  Collaborators:
    - Java SPI
*/
@SuppressWarnings({"PMD.CognitiveComplexity","PMD.CyclomaticComplexity","PMD.ImplicitFunctionalInterface"})
public interface EventEntityClassResolver {
    /**
     * Retrieves the priority. Higher numbers take precedence when merging.
     * @return the priority.
     */
    default int priority() {
        return 0;
    }

    /**
     * Exact-class lookup; return empty if unknown.
     * @param eventClass the event class.
     * @return the entity class.
     */
    Optional<Class<? extends Entity>> resolveExact(@NonNull Class<? extends Event> eventClass);

    /**
     * Retrieves the mapping entries. Bulk view to speed up merging.
     * @return such mappings.
     */
    default Map<Class<? extends Event>, Class<? extends Entity>> entries() {
        return Map.of();
    }

    /**
     * Polymorphic resolution (exact, then interfaces, then superclasses).
     * @param eventClass the class to resolve.
     * @return the associated entity.
     */
    @SuppressWarnings("PMD.AvoidDeeplyNestedIfStmts")
    default Optional<Class<? extends Entity>> resolvePolymorphic(@NonNull final Class<? extends Event> eventClass) {
        // 1) exact
        Optional<Class<? extends Entity>> result = resolveExact(eventClass);
        if (!result.isPresent()) {
            // 2) interfaces breadth-first
            final Deque<Class<?>> deque = new ArrayDeque<>();
            deque.add(eventClass);
            final Set<Class<?>> seen = new HashSet<>();
            boolean breakWhile = false;
            while (!breakWhile && !deque.isEmpty()) {
                final Class<?> clazz = deque.removeFirst();
                if (!seen.add(clazz)) {
                    continue;
                }
                for (final Class<?> itf : clazz.getInterfaces()) {
                    if (Event.class.isAssignableFrom(itf)) {
                        @SuppressWarnings("unchecked")
                        final Class<? extends Event> asEvent = (Class<? extends Event>) itf;
                        final Optional<Class<? extends Entity>> resolved = resolveExact(asEvent);
                        if (resolved.isPresent()) {
                            result = resolved;
                            breakWhile = true;
                            break;
                        }
                        deque.addLast(itf);
                    }
                }
                final Class<?> sup = clazz.getSuperclass();
                if (sup != null && Event.class.isAssignableFrom(sup)) {
                    deque.addLast(sup);
                }
            }
        }
        return result;
    }
}
