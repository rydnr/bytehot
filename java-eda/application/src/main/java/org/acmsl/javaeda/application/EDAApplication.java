package org.acmsl.javaeda.application;

import org.acmsl.javaeda.domain.Application;
import org.acmsl.javaeda.domain.BaseObject;
import org.acmsl.javaeda.domain.Event;
import org.acmsl.javaeda.domain.EventStore;
import org.acmsl.javaeda.domain.Port;
import org.acmsl.javaeda.domain.Ports;
import org.acmsl.javaeda.domain.PrimaryPort;
import org.acmsl.javaeda.domain.support.ListenRegistry;
import org.acmsl.javaeda.domain.support.SecondaryPort;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.event.EventListener;

/**
 * Main Spring Boot entry‑point that discovers, instantiates and wires all {@code Port} implementations,
 * registers them in {@link Ports}, and asynchronously triggers every {@link PrimaryPort} once the
 * application context is ready.
 */
@SuppressWarnings({"PMD.AvoidInstantiatingObjectsInLoops", "PMD.AvoidAccessibilityAlteration","PMD.ExcessiveImports","PMD.LongVariable","PMD.CyclomaticComplexity","PMD.CouplingBetweenObjects"})
public abstract class EDAApplication extends BaseObject implements Application, ApplicationContextAware {

    /** Immutable mapping <ImplementationClass, List<instances>> populated at start‑up. */
    private final Map<Class<? extends Port>, List<Port>> portMappings = new HashMap<>();

    /** Collected primary ports to be triggered after {@code Ports#initialize(Map)}. */
    private final List<PrimaryPort> primaryPorts = new ArrayList<>();

    /**
     * The application context.
     */
    private ApplicationContext applicationContext;

    /**
     * Default constructor.
     */
    protected EDAApplication() {
        super();
    }

    /**
     * Launches the application.
     * @param source the source instance.
     * @param args the command-line args.
     */
    protected static void launch(@NonNull final Class<?> source, @NonNull final String... args) {
        final SpringApplication app = new SpringApplication(source);
        app.run(args);
    }

    /**
     * Annotates Spring's {@code ApplicationContext}.
     * @param context the application context.
     */
    @Override
    public void setApplicationContext(@NonNull final ApplicationContext context) {
        this.applicationContext = context;
    }

    /**
     * Retrieves the {@code ApplicationContext}.
     * @return such instance.
     */
    protected ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * Called by Spring once this bean is fully constructed (dependencies injected, etc.).
     * Performs the entire Port discovery and initialization workflow.
     */
    @EventListener(ApplicationReadyEvent.class)
    protected void bootstrap() {
        discoverAndInstantiatePorts();
        triggerPrimaryPortsAsync();
    }

    /**
     * Uses class‑path reflection to find every concrete {@code Port} implementation and create an
     * instance for each. Fails fast if instantiation is impossible.
     */
    protected void discoverAndInstantiatePorts() {
        // Scan the entire classpath; adjust base package if you want a narrower scope.
        final Reflections reflections = new Reflections(new ConfigurationBuilder()
                .forPackages("")
                .addScanners(Scanners.SubTypes));

        final Set<Class<? extends Port>> implementations = reflections.getSubTypesOf(Port.class);
        if (implementations.isEmpty()) {
            throw new IllegalStateException("No Port implementations found in classpath!");
        }

        for (final Class<? extends Port> impl : implementations) {

            if (Modifier.isAbstract(impl.getModifiers())) {
                continue; // Skip abstract base classes
            }
            final Port instance;
            final Map<String, ? extends Port> beans = getApplicationContext().getBeansOfType(impl);
            if (beans.isEmpty()) {
                instance = createInstance(impl); // fallback to manual
            } else {
                instance = beans.values().iterator().next(); // use Spring-managed instance
            }
            for (@NonNull final Class<? extends Port> portInterface: getPortClassList(impl)) {
                portMappings.computeIfAbsent(portInterface, k -> new ArrayList<>()).add(instance);
            }
            if (instance instanceof PrimaryPort primary) {
                primaryPorts.add(primary);
            }
            // Ports is initialized each time should the adapters need other ports when creating instances.
            Ports.initialize(Collections.unmodifiableMap(portMappings));
        }

        for (final List<Port> adapters : portMappings.values()) {
            for (final Port adapter : adapters) {
                adapter.initialize();
            }
        }
    }

    /**
     * Retrieves the port interfaces of a given adapter class.
     * @param adapterClass the adapter class.
     * @return list of port interfaces implemented by the adapter.
     */
    @NonNull
    protected List<Class<? extends Port>> getPortClassList(@NonNull final Class<? extends Port> adapterClass) {
        final Set<Class<?>> visited = new HashSet<>();
        final List<Class<? extends Port>> result = new ArrayList<>();

        final Queue<Class<?>> toVisit = new LinkedList<>();
        toVisit.add(adapterClass);

        while (!toVisit.isEmpty()) {
            final Class<?> current = toVisit.poll();
            if (current == null || !visited.add(current)) {
                continue;
            }

            // Check all interfaces of the current class
            for (@NonNull final Class<?> iface : current.getInterfaces()) {
                toVisit.add(iface); // explore sub-interfaces
                if (Port.class.isAssignableFrom(iface)
                        && !iface.equals(Port.class)
                        && !iface.equals(PrimaryPort.class)
                        && !iface.equals(SecondaryPort.class)) {
                    @SuppressWarnings("unchecked")
                    final Class<? extends Port> portInterface = (Class<? extends Port>) iface;
                    result.add(portInterface);
                }
            }

            // Traverse superclass as well
            toVisit.add(current.getSuperclass());
        }

        return result;
    }

    /**
     * Attempts to instantiate {@code impl} by first looking for a constructor that accepts this
     * {@code EDAApplication} and falling back to a default constructor. Throws if neither exists.
     * @param impl the implementation.
     * @return the adapter.
     */
    @SuppressWarnings({"PMD.AvoidCatchingThrowable"})
    @Nullable
    protected Port createInstance(@NonNull final Class<? extends Port> impl) {
        Port result = null;
        try {
            // Prefer (EDAApplication) constructor so ports can access the app instance directly.
            for (final Constructor<?> ctor : impl.getDeclaredConstructors()) {
                if (ctor.getParameterCount() == 1 && ctor.getParameterTypes()[0].isAssignableFrom(EDAApplication.class)) {
                    ctor.setAccessible(true);
                    result = (Port) ctor.newInstance(this);
                }
            }
            if (result == null) {
                // Fallback to no‑args constructor
                final Constructor<? extends Port> defaultCtor = impl.getDeclaredConstructor();
                defaultCtor.setAccessible(true);
                result = defaultCtor.newInstance();
            }
        } catch (final Throwable e) {
            throw new IllegalStateException("Failed to instantiate Port implementation: " + impl.getName(), e);
        }
        return result;
    }

    /**
      * Dispatches {@code PrimaryPort.accept(EDAApplication)} calls asynchronously.
      */
    protected void triggerPrimaryPortsAsync() {
        try (@NonNull ExecutorService executor = Executors.newCachedThreadPool()) {
            for (@NonNull final PrimaryPort primary : primaryPorts) {
                executor.submit(() -> primary.accept(this));
            }
        }
    }

    /**
     * Accepts an event.
     * @param event the event.
     * @return a list of resulting events.
     */
    @Override
    @NonNull
    public <E extends Event> List<Event> accept(@NonNull final E event) {
        final List<Event> result = ListenRegistry.getInstance().accept(event);
        if (!result.isEmpty()) {
            final List<EventStore> eventStores = Ports.getInstance().resolve(EventStore.class);
            for (final EventStore store : eventStores) {
                for (final Event resultingEvent : result) {
                    store.store(resultingEvent);
                }
            }
        }
        return result;
    }
}
