package org.acmsl.javaeda.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
  Loads all {@link EventEntityClassResolver}s.

  Class name: EventEntityClassResolvers

  Responsibilities:
    - Read the discovered implementations of EventEntityClassResolver instances.

  Collaborators:
    - org.acmsl.javaeda.annotations.EventEntityClassResolverAnnotationProcessor
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class EventEntityClassResolvers extends BaseObject {

    /**
     * Singleton implemented to avoid double-check locking.
     */
    protected static final class EventEntityClassResolversSingletonContainer {
        /**
         * The singleton instance.
         */
        public static final EventEntityClassResolvers SINGLETON = new EventEntityClassResolvers();
    }

    /**
     * Retrieves the singleton instance.
     * @return such instance.
     */
    @NonNull
    public static EventEntityClassResolvers getInstance() {
        return EventEntityClassResolversSingletonContainer.SINGLETON;
    }

    /**
     * Loads the resolver.
     * @param classLoader the {@link ClassLoader} instance.
     * @return the {@link EventEntityClassResolver} instance.
     */
    @NonNull
    public EventEntityClassResolver load(@NonNull final ClassLoader classLoader) {
        final List<EventEntityClassResolver> providers = new ArrayList<>();
        ServiceLoader.load(EventEntityClassResolver.class, classLoader).forEach(providers::add);
        providers.sort(Comparator.comparingInt(EventEntityClassResolver::priority).reversed());

        final Map<Class<? extends Event>, Class<? extends Entity>> merged = new HashMap<>();
        for (final EventEntityClassResolver provider : providers) {
            for (final Map.Entry<Class<? extends Event>, Class<? extends Entity>> entry: provider.entries().entrySet()) {
                merged.putIfAbsent(entry.getKey(), entry.getValue());
            }
        }

        return new EventEntityClassResolver() {
            /**
             * {@inheritDoc}
             */
            @Override
            @NonNull
            public Optional<Class<? extends Entity>> resolveExact(@NonNull final Class<? extends Event> clazz) {
                Optional<Class<? extends Entity>> result = Optional.empty();
                final Class<? extends Entity> hit = merged.get(clazz);
                if (hit != null) {
                    result = Optional.of(hit);
                } else {
                    for (final EventEntityClassResolver provider : providers) {
                        final Optional<Class<? extends Entity>> resolved = provider.resolveExact(clazz);
                        if (resolved.isPresent()) {
                            result = resolved;
                            break;
                        }
                    }
                }
                return result;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            @NonNull
            public Map<Class<? extends Event>, Class<? extends Entity>> entries() {
                return merged;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public int priority() {
                return Integer.MAX_VALUE;
            }
        };
    }
}
