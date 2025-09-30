package org.acmsl.javaeda.domain;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
  Logging interface.

  Class name: Logging

  Responsibilities:
    - Provide logging mechanisms.

  Collaborators:
    - None
 */
public interface Logging {

    /**
     * Retrieves the logging category.
     * @return the category.
     */
    @NonNull
    String getCategory();

    /**
     * Log a message at the TRACE level according to the specified format and arguments.
     * This form avoids superfluous string concatenation when the logger is disabled for the TRACE level.
     * However, this variant incurs the hidden (and relatively small) cost of creating an Object[] before invoking the method,
     * even if this logger is disabled for TRACE.
     */
    void trace(@NonNull String format, @NonNull Object... args);

    /**
     * Is the logger instance enabled for the TRACE level?
     * @return {@code true} in that case.
     */
    boolean isTraceEnabled();

    /**
     * Log a message at the DEBUG level according to the specified format and arguments.
     * This form avoids superfluous string concatenation when the logger is disabled for the DEBUG level.
     * However, this variant incurs the hidden (and relatively small) cost of creating an Object[] before invoking the method,
     * even if this logger is disabled for DEBUG.
     */
    void debug(@NonNull String format, @NonNull Object... args);

    /**
     * Is the logger instance enabled for the DEBUG level?
     * @return {@code true} in that case.
     */
    boolean isDebugEnabled();

    /**
     * Log a message at the INFO level according to the specified format and arguments.
     * This form avoids superfluous string concatenation when the logger is disabled for the INFO level.
     * However, this variant incurs the hidden (and relatively small) cost of creating an Object[] before invoking the method,
     * even if this logger is disabled for INFO.
     */
    void info(@NonNull String format, @NonNull Object... args);

    /**
     * Is the logger instance enabled for the INFO level?
     * @return {@code true} in that case.
     */
    boolean isInfoEnabled();

    /**
     * Log a message at the WARN level according to the specified format and arguments.
     * This form avoids superfluous string concatenation when the logger is disabled for the WARN level.
     * However, this variant incurs the hidden (and relatively small) cost of creating an Object[] before invoking the method,
     * even if this logger is disabled for WARN.
     */
    void warn(@NonNull String format, @NonNull Object... args);

    /**
     * Is the logger instance enabled for the WARN level?
     * @return {@code true} in that case.
     */
    boolean isWarnEnabled();

    /**
     * Log a message at the ERROR level according to the specified format and arguments.
     * This form avoids superfluous string concatenation when the logger is disabled for the ERROR level.
     * However, this variant incurs the hidden (and relatively small) cost of creating an Object[] before invoking the method,
     * even if this logger is disabled for DEBUG.
     */
    void error(@NonNull String format, @NonNull Object... args);

    /**
     * Is the logger instance enabled for the ERROR level?
     * @return {@code true} in that case.
     */
    boolean isErrorEnabled();
}
