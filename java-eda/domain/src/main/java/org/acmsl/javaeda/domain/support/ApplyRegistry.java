package org.acmsl.javaeda.domain.support;

import org.acmsl.javaeda.annotations.EventHandlerDescriptor;
import org.acmsl.javaeda.domain.Entity;
import org.acmsl.javaeda.domain.Event;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
  Maintains a registry of {@code @Apply}-annotated methods.

  Class name: Applyegistry

  Responsibilities:
    - Find all {@code META-INF/apply.index} files
    - Annotate the {@code @Apply}-annotated methods.
    - Provide a way to query them by event type.

  Collaborators:
    - org.acmsl.javaeda.annotations.ListenerDefinition: To parse {@code META-INF/apply.index} entries.
 */
@SuppressWarnings({"PMD.LongVariable","PMD.LawOfDemeter","PMD.AvoidInstantiatingObjectsInLoops","PMD.ConstructorCallsOverridableMethod","PMD.AvoidThrowingRawExceptionTypes","PMD.CouplingBetweenObjects","PMD.CognitiveComplexity"})
public class ApplyRegistry extends EventAnnotationRegistry<BiConsumer<Event, Entity>> {

    /**
     * Creates a new {@code ApplyRegistry}.
     */
    public ApplyRegistry() {
        super("/META-INF/apply.index");
    }

    /**
     * Singleton implemented to avoid double-check locking.
     */
    protected static final class ApplyRegistrySingletonContainer {
        /**
         * The singleton instance.
         */
        public static final ApplyRegistry SINGLETON = new ApplyRegistry();
    }

    /**
     * Retrieves the singleton instance.
     */
    @NonNull
    public static ApplyRegistry getInstance() {
        return ApplyRegistrySingletonContainer.SINGLETON;
    }

    /**
     * Dispatches given event to all apply methods.
     * @param event the event.
     */
    public void apply(@NonNull final Event event, @NonNull final Entity entity) {

        final List<BiConsumer<Event, Entity>> mappings = getRegistry().getOrDefault(event.getClass(), List.of());

        for (final BiConsumer<Event, Entity> mapping : mappings) {
            mapping.accept(event, entity);
        }
    }

    /**
      * Registers the mapping described by given descriptor.
      * @param descriptor the {@code EventHandlerDescriptor} instance.
      */
    @Override
    @SuppressWarnings({"unchecked", "PMD.CognitiveComplexity"})
    protected void registerMapping(@NonNull final EventHandlerDescriptor descriptor) {
        try {
            final Class<?> clazz = Class.forName(descriptor.getClassName());
            final Class<?> eventType = Class.forName(descriptor.getEventType());
            final Method method = clazz.getMethod(descriptor.getMethodName(), eventType);

            final BiConsumer<Event, Entity> invoker;

            invoker = (event, entity) -> {
                try {
                    method.invoke(entity, event);
                } catch (final IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(String.format("Failed to apply event %s in entity %s: %s", event, entity, e));
                }
            };

            getRegistry().computeIfAbsent(eventType, t -> new ArrayList<>()).add(invoker);
        } catch (final ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException("Failed to register @Apply mapping: " + descriptor, e);
        }
    }
}
