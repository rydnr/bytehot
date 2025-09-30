package org.acmsl.javaeda.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
  The base class of events.

  Class name: BaseEvent

  Responsibilities:
    - Common logic for events.

  Collaborators:
    - None
*/
@SuppressWarnings({"PMD.DataClass","PMD.ShortVariable"})
public abstract class BaseEvent implements Event {
    /**
     * The timestamp.
     */
    private final Instant timestamp;

    /**
     * The event id.
     */
    private final UUID id;

    /**
     * The entity id.
     */
    private final UUID entityId;

    /**
     * The entity version.
     */
    private final long entityVersion;

    /**
     * The previous events.
     */
    private final List<UUID> previousEvents;

    /**
     * The invariants.
     */
    private final List<? extends Invariant<?>> invariants;

    /**
     * The primary key.
     */
    private final PrimaryKey primaryKey;

    /**
     * Creates a new instance.
     * @param timestamp the event timestamp.
     * @param entityId the id of the entity.
     * @param primaryKey the primary key.
     * @param entityVersion the expected entity version.
     * @param previousEvents the previous events.
     * @param invariants the invariants.
     */
    protected BaseEvent(
        @NonNull final Instant timestamp,
        @Nullable final UUID entityId,
        final PrimaryKey primaryKey,
        final long entityVersion,
        @NonNull final List<UUID> previousEvents,
        final Invariant<?>... invariants) {
        this(timestamp, UuidCreator.getTimeOrderedEpoch(), entityId, primaryKey, entityVersion, previousEvents, invariants);
    }

    /**
     * Creates a new instance.
     * @param timestamp the event timestamp.
     * @param entityId the id of the entity.
     * @param primaryKey the primary key.
     * @param entityVersion the expected entity version.
     * @param previousEvents the previous events.
     * @param invariants the invariants.
     */
    protected BaseEvent(
        @NonNull final Instant timestamp,
        @Nullable final UUID entityId,
        final PrimaryKey primaryKey,
        final long entityVersion,
        @NonNull final List<UUID> previousEvents,
        @NonNull final List<? extends Invariant<?>> invariants) {
        this(timestamp, UuidCreator.getTimeOrderedEpoch(), entityId, primaryKey, entityVersion, previousEvents, invariants);
    }

    /**
     * Creates a new instance.
     * @param timestamp the event timestamp.
     * @param reconstructedId the reconstructed event id.
     * @param entityId the id of the entity.
     * @param primaryKey the primary key.
     * @param entityVersion the expected entity version.
     * @param previousEvents the previous events.
     * @param invariants the invariants.
     */
    protected BaseEvent(
        @NonNull final Instant timestamp,
        @Nullable final UUID reconstructedId,
        @Nullable final UUID entityId,
        final PrimaryKey primaryKey,
        final long entityVersion,
        @NonNull final List<UUID> previousEvents,
        final Invariant<?>... invariants) {
        this(timestamp, reconstructedId, entityId, primaryKey, entityVersion, previousEvents, Arrays.asList(invariants));
    }

    /**
     * Creates a new instance.
     * @param timestamp the event timestamp.
     * @param reconstructedId the reconstructed event id.
     * @param entityId the id of the entity.
     * @param primaryKey the primary key.
     * @param entityVersion the expected entity version.
     * @param previousEvents the previous events.
     * @param invariants the invariants.
     */
    protected BaseEvent(
        @NonNull final Instant timestamp,
        @Nullable final UUID reconstructedId,
        @Nullable final UUID entityId,
        @NonNull final PrimaryKey primaryKey,
        final long entityVersion,
        @NonNull final List<UUID> previousEvents,
        @NonNull final List<? extends Invariant<?>> invariants) {
        super();
        this.timestamp = timestamp;
        this.id = (reconstructedId == null) ? UuidCreator.getTimeOrderedEpoch() : reconstructedId;
        this.entityId = entityId;
        this.primaryKey = primaryKey;
        this.entityVersion = entityVersion;
        this.previousEvents = previousEvents;
        this.invariants = invariants;
    }

    /**
     * Retrieves the timestamp.
     * @return such information.
     */
    @Override
    @NonNull
    public Instant getTimestamp() {
        return this.timestamp;
    }

    /**
     * Retrieves the id.
     * @return such information.
     */
    @Override
    public UUID getId() {
        return this.id;
    }

    /**
     * Retrieves the identifier of the entity.
     * @return such identifier.
     */
    @Override
    @Nullable
    public UUID getEntityId() {
        return this.entityId;
    }

    /**
     * Retrieves the expected version of the entity receiving this event.
     * @return such version.
     */
    @Override
    public long getEntityVersion() {
        return this.entityVersion;
    }

    /**
     * Retrieves the primary key of the associated instance.
     * @return such primary key.
     */
    @Override
    @NonNull
    public PrimaryKey getPrimaryKey() {
        return this.primaryKey;
    }

    /**
     * Retrieves the ids of the previous events.
     * @return such information.
     */
    @Override
    @NonNull
    public List<UUID> getPreviousEvents() {
        return this.previousEvents;
    }

    /**
     * Retrieves the invariants.
     * @return such information.
     */
    @Override
    @NonNull
    public List<? extends Invariant<?>> getInvariants() {
        return this.invariants;
    }
}
