package org.acmsl.javaeda.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
  Catalog of {@link Invariant}s available at runtime.

  Class name: Invariants

  Responsibilities:
    - Provide the invariants available at runtime.

  Collaborators:
    - Google auto-discover
*/
@SuppressWarnings("unused")
public class Invariants {

    /**
     * Default constructor.
     */
    protected Invariants() {
    }

    /**
     * Singleton implemented to avoid double-check locking.
     */
    protected static final class InvariantsSingletonContainer {
        /**
         * The singleton instance.
         */
        private static final Invariants SINGLETON = new Invariants();
    }

    /**
     * Retrieves the {@code Invariants} instance.
     * @return such instance.
     */
    public static Invariants getInstance() {
        return InvariantsSingletonContainer.SINGLETON;
    }

    /**
     * Retrieves the available Invariants.
     * @return A map of Invariant name and Invariant value.
     */
    @NonNull
    public List<? extends Invariant<?>> discover() {
        final List<Invariant<?>> result = new ArrayList<>();
        final ServiceLoader<Invariant> loader = ServiceLoader.load(Invariant.class);
        for (final Invariant<?> inv : loader) {
            result.add(inv);
        }
        return result;
    }
}
