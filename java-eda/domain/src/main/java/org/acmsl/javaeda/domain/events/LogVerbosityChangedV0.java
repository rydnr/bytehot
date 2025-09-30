package org.acmsl.javaeda.domain.events;

import org.acmsl.javaeda.domain.BaseEvent;
import org.acmsl.javaeda.domain.Invariant;
import org.acmsl.javaeda.domain.LogSettingsPrimaryKey;
import org.acmsl.javaeda.domain.PrimaryKey;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
  Whenever the logging verbosity changes.

  Class name: LogVerbosityChangedV0

  Responsibilities:
    - Represents a change in the logging verbosity.

  Collaborators:
    - None
 */
@SuppressWarnings({"PMD.DataClass","PMD.ExcessiveParameterList"})
public class LogVerbosityChangedV0 extends BaseEvent {
    /**
     * The version.
     */
    public static final long VERSION = 0L;

    /**
     * Whether the TRACE level is enabled.
     */
    private final boolean traceEnabled;

    /**
     * Whether the DEBUG level is enabled.
     */
    private final boolean debugEnabled;

    /**
     * Whether the INFO level is enabled.
     */
    private final boolean infoEnabled;

    /**
     * Whether the WARN level is enabled.
     */
    private final boolean warnEnabled;

    /**
     * Whether the ERROR level is enabled.
     */
    private final boolean errorEnabled;

    /**
     * Creates a new {@code LogVerbosityChanged} instance.
     * @param timestamp the event timestamp.
     * @param entityVersion the expected entity version.
     * @param traceEnabled whether the TRACE level is enabled.
     * @param debugEnabled whether the DEBUG level is enabled.
     * @param infoEnabled whether the INFO level is enabled.
     * @param warnEnabled whether the WARN level is enabled.
     * @param errorEnabled whether the ERROR level is enabled.
     * @param invariants the invariants.
     */
    @SuppressWarnings("unused")
    public LogVerbosityChangedV0(
        @NonNull final Instant timestamp,
        final long entityVersion,
        final boolean traceEnabled,
        final boolean debugEnabled,
        final boolean infoEnabled,
        final boolean warnEnabled,
        final boolean errorEnabled,
        final Invariant<?>... invariants) {
        this(timestamp, entityVersion, traceEnabled, debugEnabled, infoEnabled, warnEnabled, errorEnabled, Collections.emptyList(), invariants);
    }

    /**
     * Creates a new {@code LogVerbosityChanged} instance.
     * @param timestamp the event timestamp.
     * @param entityVersion the expected entity version.
     * @param traceEnabled whether the TRACE level is enabled.
     * @param debugEnabled whether the DEBUG level is enabled.
     * @param infoEnabled whether the INFO level is enabled.
     * @param warnEnabled whether the WARN level is enabled.
     * @param errorEnabled whether the ERROR level is enabled.
     * @param previousEvents the previous events.
     * @param invariants the invariants.
     */
    public LogVerbosityChangedV0(
        @NonNull final Instant timestamp,
        final long entityVersion,
        final boolean traceEnabled,
        final boolean debugEnabled,
        final boolean infoEnabled,
        final boolean warnEnabled,
        final boolean errorEnabled,
        @NonNull final List<UUID> previousEvents,
        final Invariant<?>... invariants) {
        this(timestamp, entityVersion, traceEnabled, debugEnabled, infoEnabled, warnEnabled, errorEnabled, previousEvents, Arrays.asList(invariants));
    }

    /**
     * Creates a new {@code LogVerbosityChanged} instance.
     * @param timestamp the event timestamp.
     * @param entityVersion the expected entity version.
     * @param traceEnabled whether the TRACE level is enabled.
     * @param debugEnabled whether the DEBUG level is enabled.
     * @param infoEnabled whether the INFO level is enabled.
     * @param warnEnabled whether the WARN level is enabled.
     * @param errorEnabled whether the ERROR level is enabled.
     * @param previousEvents the previous events.
     * @param invariants the invariants.
     */
    public LogVerbosityChangedV0(
        @NonNull final Instant timestamp,
        final long entityVersion,
        final boolean traceEnabled,
        final boolean debugEnabled,
        final boolean infoEnabled,
        final boolean warnEnabled,
        final boolean errorEnabled,
        @NonNull final List<UUID> previousEvents,
        @NonNull final List<? extends Invariant<?>> invariants) {
        this(timestamp, null, entityVersion, traceEnabled, debugEnabled, infoEnabled, warnEnabled, errorEnabled, previousEvents, invariants);
    }

    /**
     * Creates a new {@code LogVerbosityChanged} instance.
     * @param timestamp the event timestamp.
     * @param reconstructedId the reconstructed id.
     * @param entityVersion the expected entity version.
     * @param traceEnabled whether the TRACE level is enabled.
     * @param debugEnabled whether the DEBUG level is enabled.
     * @param infoEnabled whether the INFO level is enabled.
     * @param warnEnabled whether the WARN level is enabled.
     * @param errorEnabled whether the ERROR level is enabled.
     * @param previousEvents the previous events.
     * @param invariants the invariants.
     */
    public LogVerbosityChangedV0(
        @NonNull final Instant timestamp,
        @Nullable final UUID reconstructedId,
        final long entityVersion,
        final boolean traceEnabled,
        final boolean debugEnabled,
        final boolean infoEnabled,
        final boolean warnEnabled,
        final boolean errorEnabled,
        @NonNull final List<UUID> previousEvents,
        @NonNull final List<? extends Invariant<?>> invariants) {
        this(
            timestamp,
            reconstructedId,
            new LogSettingsPrimaryKey(invariants),
            entityVersion,
            traceEnabled,
            debugEnabled,
            infoEnabled,
            warnEnabled,
            errorEnabled,
            previousEvents,
            invariants);
    }

    /**
     * Creates a new {@code LogVerbosityChanged} instance.
     * @param timestamp the event timestamp.
     * @param reconstructedId the reconstructed id.
     * @param primaryKey the primary key.
     * @param entityVersion the expected entity version.
     * @param traceEnabled whether the TRACE level is enabled.
     * @param debugEnabled whether the DEBUG level is enabled.
     * @param infoEnabled whether the INFO level is enabled.
     * @param warnEnabled whether the WARN level is enabled.
     * @param errorEnabled whether the ERROR level is enabled.
     * @param previousEvents the previous events.
     * @param invariants the invariants.
     */
    public LogVerbosityChangedV0(
        @NonNull final Instant timestamp,
        @Nullable final UUID reconstructedId,
        @NonNull final PrimaryKey primaryKey,
        final long entityVersion,
        final boolean traceEnabled,
        final boolean debugEnabled,
        final boolean infoEnabled,
        final boolean warnEnabled,
        final boolean errorEnabled,
        @NonNull final List<UUID> previousEvents,
        @NonNull final List<? extends Invariant<?>> invariants) {
        super(
            timestamp,
            reconstructedId,
            primaryKey,
            entityVersion,
            previousEvents,
            invariants);
        this.traceEnabled = traceEnabled;
        this.debugEnabled = this.traceEnabled || debugEnabled;
        this.infoEnabled = this.debugEnabled || infoEnabled;
        this.warnEnabled = this.infoEnabled || warnEnabled;
        this.errorEnabled = this.warnEnabled || errorEnabled;
    }

    /**
     * Retrieves the event version.
     * @return such version.
     */
    @Override
    public long getVersion() {
        return VERSION;
    }

    /**
     * Checks whether the TRACE level is enabled.
     * @return {@code true} in such case.
     */
    public boolean isTraceEnabled() {
        return this.traceEnabled;
    }

    /**
     * Checks whether the DEBUG level is enabled.
     * @return {@code true} in such case.
     */
    public boolean isDebugEnabled() {
        return this.debugEnabled;
    }

    /**
     * Checks whether the INFO level is enabled.
     * @return {@code true} in such case.
     */
    public boolean isInfoEnabled() {
        return this.infoEnabled;
    }

    /**
     * Checks whether the WARN level is enabled.
     * @return {@code true} in such case.
     */
    public boolean isWarnEnabled() {
        return this.warnEnabled;
    }

    /**
     * Checks whether the ERROR level is enabled.
     * @return {@code true} in such case.
     */
    public boolean isErrorEnabled() {
        return this.errorEnabled;
    }
}
