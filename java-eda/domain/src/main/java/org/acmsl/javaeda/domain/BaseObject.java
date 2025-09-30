package org.acmsl.javaeda.domain;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
  Ancestor of all EDA classes.

  Class name: BaseObject

  Responsibilities:
    - Define common behavior for all EDA classes.

  Collaborators:
    - None
 */
public class BaseObject {

    /**
     * Default constructor.
     */
    protected BaseObject() {
        super();
    }

    /**
     * Retrieves a {@code Logging} instance .
     * @return the logging instance.
     */
    @NonNull
    public Logging getLogger() {
        return getLogger(getClass().getName());
    }

    /**
     * Retrieves a {@code Logging} instance for given category.
     * @param category the category.
     * @return the logging instance.
     */
    @NonNull
    public Logging getLogger(@NonNull final String category) {
        LoggingPort port = Ports.getInstance().resolveFirst(LoggingPort.class);

        if (port == null) {
            port = LoggingPortFallback.getInstance();
        }

        return port.getLogger(category);
    }
}
