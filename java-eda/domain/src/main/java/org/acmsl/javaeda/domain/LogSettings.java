package org.acmsl.javaeda.domain;

import org.acmsl.javaeda.annotations.Apply;
import org.acmsl.javaeda.annotations.Listen;
import org.acmsl.javaeda.domain.events.CreateLogSettingsRequestedV0;
import org.acmsl.javaeda.domain.events.LogSettingsCreatedV0;
import org.acmsl.javaeda.domain.events.ChangeLogVerbosityRequestedV0;
import org.acmsl.javaeda.domain.events.LogVerbosityChangedV0;
import java.time.Instant;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
  Log settings.

  Class name: LogSettings

  Responsibilities:
    - Minimal logging configuration.

  Collaborators:
    - None
 */
@SuppressWarnings({"unused","PMD.AvoidDuplicateLiterals"})
public class LogSettings extends BaseEntity {

    /**
     * Whether the TRACE level is enabled or not.
     */
    private boolean traceEnabled;

    /**
     * Whether the DEBUG level is enabled or not.
     */
    private boolean debugEnabled;

    /**
     * Whether the INFO level is enabled or not.
     */
    private boolean infoEnabled;

    /**
     * Whether the WARN level is enabled or not.
     */
    private boolean warnEnabled;

    /**
     * Whether the ERROR level is enabled or not.
     */
    private boolean errorEnabled;

    /**
     * Creates a new instance.
     * @param invariants the invariants (tenant, profile, etc.).
     */
    public LogSettings(final Invariant<?>... invariants) {
        super(invariants);
    }

    /**
     * Is logging enabled for the TRACE level?
     * @return {@code true} in that case.
     */
    @SuppressWarnings("unused")
    public boolean isTraceEnabled() {
        return this.traceEnabled;
    }

    /**
     * Determine if logging is enabled for the TRACE level.
     * @param flag {@code true} in to enable; ${code false} otherwise.
     */
    protected void setTraceEnabled(final boolean flag) {
        this.traceEnabled = flag;
    }

    /**
     * Is logging enabled for the DEBUG level?
     * @return {@code true} in that case.
     */
    @SuppressWarnings("unused")
    public boolean isDebugEnabled() {
        return this.debugEnabled;
    }

    /**
     * Determine if logging is enabled for the DEBUG level.
     * @param flag {@code true} in to enable; ${code false} otherwise.
     */
    protected void setDebugEnabled(final boolean flag) {
        this.debugEnabled = flag;
    }

    /**
     * Is logging enabled for the INFO level?
     * @return {@code true} in that case.
     */
    @SuppressWarnings("unused")
    public boolean isInfoEnabled() {
        return this.infoEnabled;
    }

    /**
     * Determine if logging is enabled for the INFO level.
     * @param flag {@code true} in to enable; ${code false} otherwise.
     */
    protected void setInfoEnabled(final boolean flag) {
        this.infoEnabled = flag;
    }

    /**
     * Is logging enabled for the WARN level?
     * @return {@code true} in that case.
     */
    @SuppressWarnings("unused")
    public boolean isWarnEnabled() {
        return this.warnEnabled;
    }

    /**
     * Determine if logging is enabled for the WARN level.
     * @param flag {@code true} in to enable; ${code false} otherwise.
     */
    protected void setWarnEnabled(final boolean flag) {
        this.warnEnabled = flag;
    }

    /**
     * Is logging enabled for the ERROR level?
     * @return {@code true} in that case.
     */
    @SuppressWarnings("unused")
    public boolean isErrorEnabled() {
        return this.errorEnabled;
    }

    /**
     * Determine if logging is enabled for the ERROR level.
     * @param flag {@code true} in to enable; ${code false} otherwise.
     */
    protected void setErrorEnabled(final boolean flag) {
        this.errorEnabled = flag;
    }

    /**
     * Listens incoming {@code LogVerbosityChangedV0} events.
     * @param event the event.
     * @return an empty list.
     */
    @SuppressWarnings("unused")
    @Listen
    @NonNull
    public List<? extends Event> listen(@NonNull final CreateLogSettingsRequestedV0 event) {

        return List.of(
            new LogSettingsCreatedV0(
                event.getTimestamp(),
                event.isTraceEnabled(),
                event.isDebugEnabled(),
                event.isInfoEnabled(),
                event.isWarnEnabled(),
                event.isErrorEnabled(),
                event.getEventHistory(),
                event.getInvariants()));
    }

    /**
     * Applies given event.
     * @param event the {@link LogSettingsCreatedV0} event.
     */
    @Apply
    public void apply(@NonNull final LogSettingsCreatedV0 event) {
        rawApply(event);
        increaseVersion();
    }

    /**
     * Applies given event.
     * @param event the {@link LogSettingsCreatedV0} event.
     */
    protected void rawApply(@NonNull final LogSettingsCreatedV0 event) {
        setCreated(event.getTimestamp());
        setTraceEnabled(event.isTraceEnabled());
        setDebugEnabled(event.isDebugEnabled());
        setInfoEnabled(event.isInfoEnabled());
        setWarnEnabled(event.isWarnEnabled());
        setErrorEnabled(event.isErrorEnabled());
        setPrimaryKey(event.getPrimaryKey());
    }

    /**
     * Listens incoming {@code ChangeLogVerbosityRequestedV0} events.
     * @param event the event.
     * @return an empty list.
     */
    @SuppressWarnings("unused")
    @Listen
    @NonNull
    public List<? extends Event> listen(@NonNull final ChangeLogVerbosityRequestedV0 event) {
        final LogVerbosityChangedV0 result =
            new LogVerbosityChangedV0(
                Instant.now(),
                getId(),
                getPrimaryKey(),
                getVersion(),
                event.isTraceEnabled(),
                event.isDebugEnabled(),
                event.isInfoEnabled(),
                event.isWarnEnabled(),
                event.isErrorEnabled(),
                event.getEventHistory(),
                event.getInvariants());
        this.rawApply(result);

        @NonNull final List<LoggingPort> loggingAdapters = Ports.getInstance().resolve(LoggingPort.class);

        for (@NonNull final LoggingPort loggingPort : loggingAdapters) {
            loggingPort.setTraceEnabled(this.isTraceEnabled());
            loggingPort.setDebugEnabled(this.isDebugEnabled());
            loggingPort.setInfoEnabled(this.isInfoEnabled());
            loggingPort.setWarnEnabled(this.isWarnEnabled());
            loggingPort.setErrorEnabled(this.isErrorEnabled());
        }
        LoggingPortFallback.getInstance().initialize(result);

        return List.of(result);
    }

    /**
     * Listens incoming {@code LogVerbosityChangeRequestedV0} events.
     * @param event the event.
     */
    @Apply
    public void apply(@NonNull final LogVerbosityChangedV0 event) {
        rawApply(event);
        increaseVersion();
    }

    /**
     * Listens incoming {@code LogVerbosityChangeRequestedV0} events.
     * @param event the event.
     */
    protected void rawApply(@NonNull final LogVerbosityChangedV0 event) {
        this.setTraceEnabled(event.isTraceEnabled());
        this.setDebugEnabled(event.isDebugEnabled());
        this.setInfoEnabled(event.isInfoEnabled());
        this.setWarnEnabled(event.isWarnEnabled());
        this.setErrorEnabled(event.isErrorEnabled());
    }
}
