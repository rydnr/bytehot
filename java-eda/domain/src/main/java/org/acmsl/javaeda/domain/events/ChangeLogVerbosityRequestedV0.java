package org.acmsl.javaeda.domain.events;

import org.acmsl.javaeda.domain.BaseEvent;
import org.acmsl.javaeda.domain.Invariant;
import org.acmsl.javaeda.domain.LogSettingsPrimaryKey;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
  Request to change the log verbosity.

  Class name: ChangeLogVerbosityRequestedV0

  Responsibilities:
    - Represent a request to change the log verbosity.

  Collaborators:
    - org.acmsl.javaeda.domain.LogSettings
    - org.acmsl.javaeda.domain.LogSettingsPrimaryKey
*/
@SuppressWarnings({"PMD.DataClass","PMD.AvoidDuplicateLiterals"})
public class ChangeLogVerbosityRequestedV0 extends BaseEvent {
    /**
     * The version.
     */
    public static final long VERSION = 0L;

    /**
     * Whether the TRACE level is enabled or not.
     */
    private final boolean traceEnabled;

    /**
     * Whether the DEBUG level is enabled or not.
     */
    private final boolean debugEnabled;

    /**
     * Whether the INFO level is enabled or not.
     */
    private final boolean infoEnabled;

    /**
     * Whether the WARN level is enabled or not.
     */
    private final boolean warnEnabled;

    /**
     * Whether the ERROR level is enabled or not.
     */
    private final boolean errorEnabled;

    /**
     * Creates a new instance.
     * @param timestamp the event timestamp.
     * @param traceEnabled whether TRACE level is enabled.
     * @param debugEnabled whether DEBUG level is enabled.
     * @param infoEnabled whether INFO level is enabled.
     * @param warnEnabled whether WARN level is enabled.
     * @param errorEnabled whether ERROR level is enabled.
     * @param invariants the invariants (tenant, profile, etc.).
     */
    public ChangeLogVerbosityRequestedV0(
        @NonNull final Instant timestamp,
        final boolean traceEnabled,
        final boolean debugEnabled,
        final boolean infoEnabled,
        final boolean warnEnabled,
        final boolean errorEnabled,
        final Invariant<?>... invariants) {
        this(
            timestamp,
            traceEnabled,
            debugEnabled,
            infoEnabled,
            warnEnabled,
            errorEnabled,
            Arrays.asList(invariants));
    }

    /**
     * Creates a new instance.
     * @param timestamp the event timestamp.
     * @param traceEnabled whether TRACE level is enabled.
     * @param debugEnabled whether DEBUG level is enabled.
     * @param infoEnabled whether INFO level is enabled.
     * @param warnEnabled whether WARN level is enabled.
     * @param errorEnabled whether ERROR level is enabled.
     * @param invariants the invariants (tenant, profile, etc.).
     */
    public ChangeLogVerbosityRequestedV0(
        @NonNull final Instant timestamp,
        final boolean traceEnabled,
        final boolean debugEnabled,
        final boolean infoEnabled,
        final boolean warnEnabled,
        final boolean errorEnabled,
        @NonNull final List<? extends Invariant<?>> invariants) {
        this(
            timestamp,
            traceEnabled,
            debugEnabled,
            infoEnabled,
            warnEnabled,
            errorEnabled,
            Collections.emptyList(),
            invariants);
    }

    /**
     * Creates a new instance.
     * @param timestamp the event timestamp.
     * @param traceEnabled whether TRACE level is enabled.
     * @param debugEnabled whether DEBUG level is enabled.
     * @param infoEnabled whether INFO level is enabled.
     * @param warnEnabled whether WARN level is enabled.
     * @param errorEnabled whether ERROR level is enabled.
     * @param previousEvents the ids of the previous events.
     * @param invariants the invariants (tenant, profile, etc.).
     */
    public ChangeLogVerbosityRequestedV0(
        @NonNull final Instant timestamp,
        final boolean traceEnabled,
        final boolean debugEnabled,
        final boolean infoEnabled,
        final boolean warnEnabled,
        final boolean errorEnabled,
        @NonNull final List<UUID> previousEvents,
        final List<? extends Invariant<?>> invariants) {
        this(timestamp, null, traceEnabled, debugEnabled, infoEnabled, warnEnabled, errorEnabled, previousEvents, invariants);
    }

    /**
     * Creates a new instance.
     * @param timestamp the event timestamp.
     * @param reconstructedId the reconstructed id.
     * @param traceEnabled whether TRACE level is enabled.
     * @param debugEnabled whether DEBUG level is enabled.
     * @param infoEnabled whether INFO level is enabled.
     * @param warnEnabled whether WARN level is enabled.
     * @param errorEnabled whether ERROR level is enabled.
     * @param previousEvents the ids of the previous events.
     * @param invariants the invariants (tenant, profile, etc.).
     */
    public ChangeLogVerbosityRequestedV0(
        @NonNull final Instant timestamp,
        @Nullable final UUID reconstructedId,
        final boolean traceEnabled,
        final boolean debugEnabled,
        final boolean infoEnabled,
        final boolean warnEnabled,
        final boolean errorEnabled,
        @NonNull final List<UUID> previousEvents,
        @NonNull final List<? extends Invariant<?>> invariants) {
        super(timestamp, reconstructedId, new LogSettingsPrimaryKey(invariants), 0L, previousEvents, invariants);
        this.traceEnabled = traceEnabled;
        this.debugEnabled = debugEnabled;
        this.infoEnabled = infoEnabled;
        this.warnEnabled = warnEnabled;
        this.errorEnabled = errorEnabled;
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
     * Is logging enabled for the DEBUG level?
     * @return {@code true} in that case.
     */
    @SuppressWarnings("unused")
    public boolean isDebugEnabled() {
        return this.debugEnabled;
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
     * Is logging enabled for the WARN level?
     * @return {@code true} in that case.
     */
    @SuppressWarnings("unused")
    public boolean isWarnEnabled() {
        return this.warnEnabled;
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
     * Retrieves the event version.
     * @return such information.
     */
    @Override
    public long getVersion() {
        return VERSION;
    }
}
