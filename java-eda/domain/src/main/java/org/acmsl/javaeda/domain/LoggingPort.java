package org.acmsl.javaeda.domain;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
  Port for logging mechanisms.

  Class name: LoggingPort

  Responsibilities:
    - Creates {@code Logging} instances.

  Collaborators:
    - None
 */
public interface LoggingPort extends Port {

    /**
     * Creates a new {@code Logging} instance.
     * @return the Logging instance.
     */
    @NonNull
    Logging getLogger();

    /**
     * Creates a new {@code Logging} instance for given category.
     * @param category the category.
     * @return the Logging instance.
     */
    @NonNull
    Logging getLogger(@NonNull String category);

    /**
     * Sets whether the TRACE level is enabled or not.
     * @param flag {@code true} to enable TRACE level.
     */
    void setTraceEnabled(boolean flag);

    /**
     * Is logging enabled for the TRACE level?
     * @return {@code true} in that case.
     */
    boolean isTraceEnabled();

    /**
     * Sets whether the DEBUG level is enabled or not.
     * @param flag {@code true} to enable DEBUG level.
     */
    void setDebugEnabled(boolean flag);

    /**
     * Is logging enabled for the DEBUG level?
     * @return {@code true} in that case.
     */
    boolean isDebugEnabled();

    /**
     * Sets whether the INFO level is enabled or not.
     * @param flag {@code true} to enable INFO level.
     */
    void setInfoEnabled(boolean flag);

    /**
     * Is logging enabled for the INFO level?
     * @return {@code true} in that case.
     */
    boolean isInfoEnabled();

    /**
     * Sets whether the WARN level is enabled or not.
     * @param flag {@code true} to enable WARN level.
     */
    void setWarnEnabled(boolean flag);

    /**
     * Is logging enabled for the WARN level?
     * @return {@code true} in that case.
     */
    boolean isWarnEnabled();

    /**
     * Sets whether the ERROR level is enabled or not.
     * @param flag {@code true} to enable ERROR level.
     */
    void setErrorEnabled(boolean flag);

    /**
     * Is logging enabled for the ERROR level?
     * @return {@code true} in that case.
     */
     boolean isErrorEnabled();
}
