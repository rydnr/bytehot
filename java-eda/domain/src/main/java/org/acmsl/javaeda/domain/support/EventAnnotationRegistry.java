package org.acmsl.javaeda.domain.support;

import org.acmsl.javaeda.annotations.EventHandlerDescriptor;
import org.acmsl.javaeda.domain.BaseObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
  Maintains a registry of event annotation methods.

  Class name: EventAnnotationRegistry

  Responsibilities:
    - Provide a way to query annotated methods by event type.

  Collaborators:
    - None
 */
@SuppressWarnings({"PMD.LongVariable","PMD.LawOfDemeter","PMD.AvoidInstantiatingObjectsInLoops","PMD.ConstructorCallsOverridableMethod","PMD.AvoidThrowingRawExceptionTypes","PMD.CouplingBetweenObjects","PMD.CognitiveComplexity"})
public abstract class EventAnnotationRegistry<T> extends BaseObject {

    /**
     * The registry.
     */
    private final Map<Class<?>, List<T>> registry = new HashMap<>();

    /**
     * The location of the index file.
     */
    private final String indexFileLocation;

    /**
     * Creates a new {@code EventAnnotationRegistry}.
     * @param indexFileLocation the location of the index file.
     */
    protected EventAnnotationRegistry(@NonNull final String indexFileLocation) {
        super();
        this.indexFileLocation = indexFileLocation;
        loadMappings(indexFileLocation);
    }

    /**
     * Retrieves the registry.
     * @return such mapping.
     */
    @NonNull
    protected Map<Class<?>, List<T>> getRegistry() {
        return this.registry;
    }

    /**
     * Retrieves the index file location.
     * @return such location.
     */
    @SuppressWarnings("unused")
    @NonNull
    public String getIndexFileLocation() {
        return this.indexFileLocation;
    }

    /**
     * Loads all mappings from the index file.
     */
    protected void loadMappings(@NonNull final String indexFileLocation) {
        try {
            final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            final Enumeration<URL> resources = classLoader.getResources(indexFileLocation);

            while (resources.hasMoreElements()) {
                final URL url = resources.nextElement();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                    reader.lines()
                          .filter(line -> !line.isBlank())
                          .map(EventHandlerDescriptor::parse)
                          .forEach(this::registerMapping);
                }
            }
        } catch (final IOException e) {
            throw new IllegalStateException("Failed to load @Apply metadata", e);
        }
    }

    /**
     * Registers the mapping described by given descriptor.
     * @param descriptor the {@code ApplyDescriptor} instance.
     */
    protected abstract void registerMapping(@NonNull EventHandlerDescriptor descriptor);
}
