package org.acmsl.javaeda.domain.support;

import org.acmsl.javaeda.annotations.EventHandlerDescriptor;
import org.acmsl.javaeda.domain.Entity;
import org.acmsl.javaeda.domain.Event;
import org.acmsl.javaeda.domain.EventStore;
import org.acmsl.javaeda.domain.EventEntityClassResolver;
import org.acmsl.javaeda.domain.EventEntityClassResolvers;
import org.acmsl.javaeda.domain.Ports;
import org.acmsl.javaeda.domain.RejectableEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
  Maintains a registry of {@code @Listen}-annotated methods.

  Class name: ListenRegistry

  Responsibilities:
    - Find all {@code META-INF/listen.index} files
    - Annotate the {@code @Listen}-annotated methods.
    - Provide a way to query them by event type.

  Collaborators:
    - org.acmsl.javaeda.annotations.ListenerDefinition: To parse {@code META-INF/listen.index} entries.
 */
@SuppressWarnings({"PMD.LongVariable","PMD.LawOfDemeter","PMD.AvoidInstantiatingObjectsInLoops","PMD.ConstructorCallsOverridableMethod","PMD.AvoidThrowingRawExceptionTypes","PMD.CouplingBetweenObjects","PMD.CognitiveComplexity"})
public class ListenRegistry extends EventAnnotationRegistry<Function<Event, List<Event>>> {

    /**
     * Creates a new {@code ListenRegistry}.
     */
    public ListenRegistry() {
        super("/META-INF/listen.index");
    }

    /**
     * Singleton implemented to avoid double-check locking.
     */
    protected static final class ListenRegistrySingletonContainer {
        /**
         * The singleton instance.
         */
        public static final ListenRegistry SINGLETON = new ListenRegistry();
    }

    /**
     * Retrieves the singleton instance.
     */
    @NonNull
    public static ListenRegistry getInstance() {
        return ListenRegistrySingletonContainer.SINGLETON;
    }

    /**
     * Dispatches given event to all listening methods.
     * @param event the event.
     * @return the list of resulting events.
     */
    public List<Event> accept(@NonNull final Event event) {
        final List<Event> result;

        final EventStore eventStore = Ports.getInstance().resolveFirst(EventStore.class);
        // Pre-dispatch rejection check
        if (event instanceof RejectableEvent<?> rejectable && eventStore != null) {
            final Optional<Class<?>> entityClass = lookupEntityClass(event);
            if (entityClass.isPresent()) {
                final List<Event> pastEvents = eventStore.read(entityClass.get(), event.getPrimaryKey());
                if (pastEvents.isEmpty()) {
                    result = new ArrayList<>();
                } else {
                    result = List.of(rejectable.reject());
                }
            } else {
                result = List.of();
            }
        } else {
            result = List.of();
        }

        if (result.isEmpty()) {
            final List<Function<Event, List<Event>>> listeners = getRegistry().getOrDefault(event.getClass(), List.of());

            for (final Function<Event, List<Event>> listener : listeners) {
                final List<Event> newEvents = listener.apply(event);
                if (newEvents != null) {
                    result.addAll(newEvents);
                }
            }
        }

        return result;
    }

    /**
     * Registers the listener described by given descriptor.
     * @param descriptor the {@code EventHandlerDescriptor} instance.
     */
    @Override
    @SuppressWarnings({"unchecked", "PMD.CognitiveComplexity"})
    protected void registerMapping(@NonNull final EventHandlerDescriptor descriptor) {
        try {
            final Class<?> clazz = Class.forName(descriptor.getClassName());
            final Class<?> eventType = Class.forName(descriptor.getEventType());
            final Method method = clazz.getMethod(descriptor.getMethodName(), eventType);

            final Function<Event, List<Event>> invoker;

            invoker = event -> {
                final List<Event> result = new ArrayList<>();
                final List<DeferredEvent<?>> infrastructureEvents = new ArrayList<>();
                final Optional<Entity> domainInstance = buildInstance(event, false);
                if (domainInstance.isPresent()) {
                    if (event.getEntityVersion() == domainInstance.get().getVersion()) {
                        try {
                            result.addAll((List<Event>) method.invoke(domainInstance.get(), event));
                        } catch (final IllegalAccessException | InvocationTargetException e) {
                            throw new RuntimeException("Failed to invoke listener", e);
                        }
                    } else {
                        if (event instanceof RejectableEvent<?> rej) {
                            result.add(rej.reject());
                        } else {
                            infrastructureEvents.add(new DeferredEvent<>(event));
                        }
                    }
                    if (!infrastructureEvents.isEmpty()) {
                        final List<SecondaryPort> secondaryPorts = Ports.getInstance().resolve(SecondaryPort.class);
                        for (final SecondaryPort port : secondaryPorts) {
                            for (final DeferredEvent deferredEvent: infrastructureEvents) {
                                port.reject(deferredEvent);
                            }
                        }
                    }
                } else {
                    throw new RuntimeException(String.format("Cannot build an instance for %s", event.toString()));
                }
                return result;
            };

            getRegistry().computeIfAbsent(eventType, ignored -> new ArrayList<>()).add(invoker);

        } catch (final ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException("Failed to register listener: " + descriptor, e);
        }
    }

    /**
     * Looks up the entity class for given event.
     * @param event the event.
     * @return the entity class, if found.
     */
    @NonNull
    public Optional<Class<?>> lookupEntityClass(@NonNull final Event event) {
        final EventEntityClassResolver resolver = EventEntityClassResolvers.getInstance().load(Thread.currentThread().getContextClassLoader());
        final Class<?> entityClass =
            resolver.resolvePolymorphic(event.getClass())
            .orElseThrow(() -> new IllegalStateException("No owning entity for " + event.getClass()));
       return Optional.ofNullable(entityClass);
    }

    /**
     * Builds the domain instance associated to given event.
     * @param event the event.
     * @param logWarnings whether to log warnings.
     * @return a domain instance rebuilt from the event store events.
     */
    @SuppressWarnings({"unchecked","PMD.CognitiveComplexity","PMD.CyclomaticComplexity"})
    @NonNull
    public Optional<Entity> buildInstance(@NonNull final Event event, final boolean logWarnings) {
        Entity result = null;
        final Optional<Class<?>> entityClass = lookupEntityClass(event);
        if (entityClass.isPresent()) {
            try {
                for (final Constructor<?> ctor : entityClass.get().getDeclaredConstructors()) {
                    if (isSupported(ctor)) {
                        result = (Entity) ctor.newInstance();
                        break;
                    }
                }
                if (result == null && getLogger().isErrorEnabled()) {
                    getLogger().error("‼️ No supported constructor defined for " + entityClass.get());
                }
            } catch (final InstantiationException | IllegalAccessException | InvocationTargetException e) {
                if (getLogger().isErrorEnabled()) {
                    getLogger().error("‼️ Cannot materialize entity from event " + event, e);
                }
            }
            if (result != null) {
                @Nullable final EventStore eventStore = Ports.getInstance().resolveFirst(EventStore.class);
                if (eventStore == null) {
                    if (logWarnings && getLogger().isErrorEnabled()) {
                        getLogger().error("‼️ No event store available");
                    }
                } else {
                    final List<Event> events = eventStore.read(entityClass.get(), event.getPrimaryKey());
                    final ApplyRegistry applyRegistry = ApplyRegistry.getInstance();
                    for (final Event storedEvent: events) {
                        applyRegistry.apply(storedEvent, result);
                    }
                }
            }
        }
        return Optional.ofNullable(result);
    }

    /**
     * Checks if given constructor is supported.
     * @param constructor the constructor.
     * @return {@code true} in such case.
     */
    protected boolean isSupported(@NonNull final Constructor<?> constructor) {
        return constructor.getParameterCount() == 0;
    }
}
