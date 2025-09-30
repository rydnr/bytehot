package org.acmsl.javaeda.domain;

import java.util.List;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
  The Application interface.

  Class name: Application

  Responsibilities:
    - Accept events from primary adapters.
    - Emit events using secondary adapters.

  Collaborators:
    - None
*/
@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface Application {
    /**
     * Accepts an incoming event.
     * @param event the event.
     * @return the list of resulting events.
     */
    <E extends Event> List<Event> accept(@NonNull E event);

    /**
     * Retrieves this application as invariant.
     * @return an {@link Invariant} wrapper.
     */
    default Invariant<Application> asInvariant() {
        return new Invariant<>() {
            /**
             * {@inheritDoc}
             */
            @Override
            @NonNull
            public String getName() {
                return "application";
            }

            /**
             * {@inheritDoc}
             */
            @Override
            @NonNull
            public Optional<Application> getValue() {
                return Optional.ofNullable(Application.this);
            }
        };
    }

    /**
     * Checks if there's an application invariant in given list.
     * @param invariants the list of invariants.
     * @return {@code true} in that case.
     */
    static boolean includedIn(@NonNull final List<? extends Invariant<?>> invariants) {
        boolean result = false;
        for (final Invariant<?> invariant : invariants) {
            final Object inv = invariant.getValue().orElse(null);
            if (inv instanceof Application app) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Extracts the application invariant in given list, if any.
     * @param invariants the list of invariants.
     * @return the application invariant, or {@code null} if not found.
     */
    @Nullable
    static Application extractFrom(@NonNull final List<? extends Invariant<?>> invariants) {
        Application result = null;
        for (final Invariant<?> invariant : invariants) {
            final Object inv = invariant.getValue().orElse(null);
            if (inv instanceof Application app) {
                result = app;
                break;
            }
        }
        return result;
    }
}
