package org.acmsl.javaeda.domain.invariants;

import org.acmsl.javaeda.domain.Application;
import org.acmsl.javaeda.domain.BaseObject;
import org.acmsl.javaeda.domain.Invariant;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
  An invariant for the EDA application.

  Class name: ApplicationInvariant.

  Responsibilities:
    - Provide the application instance as an invariant.

  Collaborators:
    - None
*/
@SuppressWarnings({"unused"})
public class ApplicationInvariant extends BaseObject implements Invariant<Application> {

    /**
     * The application instance.
     */
    private final Application application;

    /**
     * Creates an application invariant.
     */
    public ApplicationInvariant() {
        this(null);
    }

    /**
     * Creates an application invariant.
     * @param application the application.
     */
    public ApplicationInvariant(@Nullable final Application application) {
        super();
        this.application = application;
    }

    /**
     * Returns {@code "eda.application"}.
     * @return such literal.
     */
    @Override
    @NonNull
    public String getName() {
        return "eda.application";
    }

    /**
     * Returns the application instance.
     * @return such information.
     */
    @Override
    @NonNull
    public Optional<Application> getValue() {
        return Optional.ofNullable(this.application);
    }
}
