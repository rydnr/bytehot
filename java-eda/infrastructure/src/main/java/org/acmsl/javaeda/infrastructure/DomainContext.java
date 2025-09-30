package org.acmsl.javaeda.infrastructure;

import org.acmsl.javaeda.domain.Application;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.stereotype.Component;

/**
  Spring component to auto-inject the {@code Application} instance into other Spring components.

  Class name: DomainContext

  Responsibilities:
    - Provide references to the Application instance to other components via dependency injection.

  Collaborators:
    - org.acmsl.javaeda.application.EDAApplication
    - Spring
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
@Component
public class DomainContext {
    /**
     * The EDA application reference.
    */
    private Application application;

    /**
     * Annotates the {@code Application} reference.
     * @param app the application instance.
     */
    public void setApplication(@NonNull final Application app) {
        this.application = app;
    }

    /**
     * Retrieves the {@code Application} reference.
     * @return the application instance.
     */
    @NonNull
    public Application getApplication() {
        return application;
    }
}
