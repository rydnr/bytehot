package org.acmsl.javaeda.domain;

import org.acmsl.javaeda.domain.events.LogVerbosityChangedV0;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.LogManager;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
  Port for logging mechanisms.

  Class name: LoggingPort

  Responsibilities:
    - Creates {@code Logging} instances.

  Collaborators:
    - None
 */
@SuppressWarnings({"PMD.AvoidDuplicateLiterals"})
public class LoggingPortFallback implements LoggingPort {

    /**
     * Whether the trace level is enabled.
     */
    private boolean traceEnabled;

    /**
     * Whether the debug level is enabled.
     */
    private boolean debugEnabled;

    /**
     * Whether the info level is enabled.
     */
    private boolean infoEnabled = true;

    /**
     * Whether the warn level is enabled.
     */
    private boolean warnEnabled = true;

    /**
     * Whether the error level is enabled.
     */
    private boolean errorEnabled = true;

    /**
     * The cached fallbacks.
     */
    private final Map<String, Logging> loggingFallbacks;

    /**
     * A singleton implemented to avoid the double-check locking.
     */
    protected static final class LoggingPortFallbackSingletonContainer {
        /**
         * The singleton instance.
         */
        @SuppressWarnings({"PMD.AvoidProtectedFieldInFinalClass"})
        protected static final LoggingPortFallback SINGLETON = new LoggingPortFallback();
    }

    /**
     * Creates a new instance.
     */
    protected LoggingPortFallback() {
        this.loggingFallbacks = new HashMap<>();
    }

    /**
     * Initializes this fallback mechanism.
     */
    @Override
    public void initialize() {
    }

    /**
     * Initializes this fallback mechanism.
     * @param event the event.
     */
    public void initialize(@NonNull final LogVerbosityChangedV0 event) {
        this.traceEnabled = event.isTraceEnabled();
        this.debugEnabled = event.isDebugEnabled();
        this.infoEnabled = event.isInfoEnabled();
        this.warnEnabled = event.isWarnEnabled();
        this.errorEnabled = event.isErrorEnabled();
    }

    /**
     * Retrieves the singleton instance.
     * @return such instance.
     */
    public static LoggingPortFallback getInstance() {
        return LoggingPortFallbackSingletonContainer.SINGLETON;
    }

    /**
     * Creates a new {@code Logging} instance.
     * @return the Logging instance.
     */
    @Override
    @NonNull
    public Logging getLogger() {
        return getLogger("");
    }

    /**
     * Creates a new {@code Logging} instance for given category.
     * @param category the category.
     * @return the Logging instance.
     */
    @Override
    @NonNull
    public Logging getLogger(@NonNull final String category) {
        final Logging result;
        if (this.loggingFallbacks.containsKey(category)) {
            result = this.loggingFallbacks.get(category);
        } else {
            result = new LoggingFallback(category);
            this.loggingFallbacks.put(category, result);
        }
        return result;
    }

    /**
     * Checks if the port is enabled.
     * @return <code>true</code> in such case.
     */
    @Override
    public boolean enabled() {
        return true;
    }

    /**
     * Is logging enabled for the TRACE level?
     * @return {@code true} in that case.
     */
    @Override
    public boolean isTraceEnabled() {
        return this.traceEnabled;
    }

    /**
     * Enables or disables the TRACE level.
     * @param flag {@code true} to enable TRACE level.
     */
    @Override
    public void setTraceEnabled(final boolean flag) {
        this.traceEnabled = flag;
    }

    /**
     * Is logging enabled for the DEBUG level?
     * @return {@code true} in that case.
     */
    @Override
    public boolean isDebugEnabled() {
        return this.debugEnabled;
    }

    /**
     * Enables or disables the DEBUG level.
     * @param flag {@code true} to enable DEBUG level.
     */
    @Override
    public void setDebugEnabled(final boolean flag) {
        this.debugEnabled = flag;
    }

    /**
     * Is logging enabled for the INFO level?
     * @return {@code true} in that case.
     */
    @Override
    public boolean isInfoEnabled() {
        return this.infoEnabled;
    }

    /**
     * Enables or disables the INFO level.
     * @param flag {@code true} to enable INFO level.
     */
    @Override
    public void setInfoEnabled(final boolean flag) {
        this.infoEnabled = flag;
    }

    /**
     * Is logging enabled for the WARN level?
     * @return {@code true} in that case.
     */
    @Override
    public boolean isWarnEnabled() {
        return this.warnEnabled;
    }

    /**
     * Enables or disables the WARN level.
     * @param flag {@code true} to enable WARN level.
     */
    @Override
    public void setWarnEnabled(final boolean flag) {
        this.warnEnabled = flag;
    }

    /**
     * Is logging enabled for the ERROR level?
     * @return {@code true} in that case.
     */
    @Override
    public boolean isErrorEnabled() {
        return this.errorEnabled;
    }

    /**
     * Enables or disables the ERROR level.
     * @param flag {@code true} to enable ERROR level.
     */
    @Override
    public void setErrorEnabled(final boolean flag) {
        this.errorEnabled = flag;
    }

    /**
     * Fallback implementation.
     */
    protected static class LoggingFallback implements Logging {
        /**
         * The category.
         */
        private final String category;

        /**
         * A memoized logger.
         */
        private Logger logger;

        /**
         * Creates a new instance.
         * @param category the category.
         */
        protected LoggingFallback(@NonNull final String category) {
            this.category = category;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @NonNull
        public String getCategory() {
            return this.category;
        }

        /**
         * Retrieves a logger instance.
         * @return Such instance.
         */
        protected Logger getLogger() {
            if (this.logger == null) {
                this.logger = LogManager.getLogManager().getLogger(getCategory());
            }
            if (this.logger == null) {
                this.logger = LogManager.getLogManager().getLogger("");
            }
            return this.logger;
        }

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("PMD.GuardLogStatement")
        @Override
        public void trace(@NonNull final String format, @NonNull final Object... args) {
            if (isTraceEnabled()) {
                getLogger().finest(String.format(format, args));
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isTraceEnabled() {
            return getInstance().isTraceEnabled() && getLogger().isLoggable(Level.FINEST);
        }

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("PMD.GuardLogStatement")
        @Override
        public void debug(@NonNull final String format, @NonNull final Object... args) {
            if (isDebugEnabled()) {
                getLogger().fine(String.format(format, args));
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isDebugEnabled() {
            return getInstance().isDebugEnabled() && getLogger().isLoggable(Level.FINE);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void info(@NonNull final String format, @NonNull final Object... args) {
            if (isInfoEnabled()) {
                getLogger().info(String.format(format, args));
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isInfoEnabled() {
            return getInstance().isInfoEnabled() && getLogger().isLoggable(Level.INFO);
        }

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("PMD.GuardLogStatement")
        @Override
        public void warn(@NonNull final String format, @NonNull final Object... args) {
            if (isWarnEnabled()) {
                getLogger().warning(String.format(format, args));
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isWarnEnabled() {
            return getInstance().isWarnEnabled() && getLogger().isLoggable(Level.WARNING);
        }

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("PMD.GuardLogStatement")
        @Override
        public void error(@NonNull final String format, @NonNull final Object... args) {
            if (isErrorEnabled()) {
                getLogger().severe(String.format(format, args));
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isErrorEnabled() {
            return getInstance().isErrorEnabled() && getLogger().isLoggable(Level.SEVERE);
        }
    }
}
