package org.acmsl.javaeda.domain;

import java.util.Collections;
import java.util.function.Supplier;
import java.util.List;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
  Registry of available ports.

  Class name: Ports

  Responsibilities:
    - Maintain a registry of available ports.
    - Act as a singleton

  Collaborators:
    - Port: The resolved interfaces.
    - Adapter implementations in the infrastructure layer.
    - Application that provides the adapters when running the bounded context.
 */
public class Ports {
    private Map<Class<? extends Port>, List<? extends Port>> mappings;

    /**
     * The singleton provider.
     */
    private static volatile Supplier<Ports> provider;

    /**
     * Defensive approach against the double-locking check.
     */
    protected static final class PortsSingletonContainer {
        /**
         *  The actual singleton.
         */
        private static final Ports SINGLETON = new Ports();
    }

    /**
     * Default constructor.
     */
    protected Ports() {
        super();
    }

    /**
     * Initializes the mappings.
     */
    protected void init(@NonNull final Map<Class<? extends Port>, List<? extends Port>> mappings) {
        this.mappings = mappings;
    }

    /**
     * Override the singleton provider (mainly to simplify testing)
     * @param override the new supplier.
     */
    @SuppressWarnings("PMD.CommentDefaultAccessModifier")
    static void setProvider(@Nullable final Supplier<Ports> override) {
        provider = override;
    }

    /**
     * Retrieves the singleton instance.
     * @return Such instance.
     */
    @NonNull
    public static Ports getInstance() {
        return (provider != null) ? provider.get() : PortsSingletonContainer.SINGLETON;
    }

    /**
     * Initializes the Ports. It should only be called from EDA-Application.
     * @param mappings the port -> adapters mappings.
     */
    public static void initialize(@NonNull final Map<Class<? extends Port>, List<? extends Port>> mappings) {
        getInstance().init(mappings);
    }

    /**
     * Resolves given port.
     * @param port the {@code Port} to resolve.
     * @return The list of adapters found at runtime.
     */
    @SuppressWarnings("unchecked")
    @NonNull
    public <P extends Port> List<P> resolve(@NonNull final Class<P> port) {
        final List<P> result;

        if (this.mappings != null && this.mappings.containsKey(port)) {
            result = (List<P>) this.mappings.get(port);
        } else {
            result = Collections.emptyList();
        }

        return result;
    }

    /**
     * Resolves the first adapter found.
     * @param port the {@code Port} to resolve.
     * @return The first adapter, or {@code null} if not found.
    */
    @SuppressWarnings({"PMD.NullAssignment", "PMD.ConfusingTernary"})
    @Nullable
    public <P extends Port> P resolveFirst(@NonNull final Class<P> port) {
        final P result;
        final List<P> adapters = this.resolve(port);

        result = (!adapters.isEmpty()) ? adapters.get(0) : null;

        return result;
    }
}
