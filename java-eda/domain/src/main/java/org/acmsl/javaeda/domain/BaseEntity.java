package org.acmsl.javaeda.domain;

import java.util.Arrays;
import java.util.List;
import java.time.Instant;
import java.util.UUID;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
  Base class for entities.

  Class name: BaseEntity

  Responsibilities:
    - Defines common logic.

  Collaborators:
    - None
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public abstract class BaseEntity extends BaseObject implements Entity {
    /**
     * The id.
     */
    private UUID entityId;

    /**
     * The primary key.
     */
    private PrimaryKey primaryKey;

    /**
     * The version.
     */
    private long version;

    /**
     * The invariants, if any.
     */
    private final List<? extends Invariant<?>> invariants;

    /**
     * When this entity was created.
     */
    private Instant whenCreated;

    /**
     * When this entity was removed.
     */
    private Instant whenRemoved;

    /**
     * Creates a new instance.
     * @param invariants the invariants (tenant, profile, etc.).
     */
    protected BaseEntity(final Invariant<?>... invariants) {
        this(Arrays.asList(invariants));
    }

    /**
     * Creates a new instance.
     * @param invariants the invariants (tenant, profile, etc.).
     */
    protected BaseEntity(@NonNull final List<? extends Invariant<?>> invariants) {
        this(null, null, 0L, invariants);
    }

    /**
     * Creates a new instance.
     * @param entityId the id of the entity.
     * @param primaryKey the primary key.
     * @param entityVersion the expected entity version.
     * @param invariants the invariants (tenant, profile, etc.).
     */
    protected BaseEntity(
        @Nullable final UUID entityId,
        @Nullable final PrimaryKey primaryKey,
        final long entityVersion,
        final Invariant<?>... invariants) {
        this(entityId, primaryKey, entityVersion, Arrays.asList(invariants));
    }

    /**
     * Creates a new instance.
     * @param entityId the id of the entity.
     * @param primaryKey the primary key.
     * @param entityVersion the expected entity version.
     * @param invariants the invariants (tenant, profile, etc.).
     */
    protected BaseEntity(
        @Nullable final UUID entityId,
        @Nullable final PrimaryKey primaryKey,
        final long entityVersion,
        @NonNull final List<? extends Invariant<?>> invariants) {
        super();
        this.entityId = entityId;
        this.primaryKey = primaryKey;
        this.version = entityVersion;
        this.invariants = invariants;
    }

    /**
     * Retrieves the entity id.
     * @return such identifier.
     */
    @Override
    @NonNull
    public UUID getId() {
        return this.entityId;
    }

    /**
     * Specifies the entity id.
     * @param entityId the id.
     */
    @SuppressWarnings("unused")
    protected void setId(@NonNull final UUID entityId) {
        this.entityId = entityId;
    }

    /**
     * Retrieves the version.
     * @return such information.
     */
    @Override
    public long getVersion() {
        return this.version;
    }

    /**
     * Increases the version of this entity.
     */
    protected void increaseVersion() {
        this.version++;
    }

    /**
     * Specifies the primary key.
     * @param primaryKey the key.
     */
    @SuppressWarnings("unused")
    protected void setPrimaryKey(@NonNull final PrimaryKey primaryKey) {
        this.primaryKey = primaryKey;
    }

    /**
     * Retrieves the primary key.
     * @return the primary key.
     */
    @Override
    @NonNull
    public PrimaryKey getPrimaryKey() {
        return this.primaryKey;
    }

    /**
     * Retrieves the invariants.
     * @return such list.
     */
    @SuppressWarnings("unused")
    @NonNull
    public List<? extends Invariant<?>> getInvariants() {
        return this.invariants;
    }

    /**
     * Retrieves when this entity was created.
     * @return such timestamp.
     */
    @Override
    @NonNull
    public Instant created() {
        return this.whenCreated;
    }

    /**
     * Specifies when the instance was created.
     * @param when the timestamp.
     */
    @SuppressWarnings("unused")
    protected void setCreated(@NonNull final Instant when) {
        this.whenCreated = when;
    }

    /**
     * Retrieves when this entity was removed.
     * @return such timestamp.
     */
    @Override
    @Nullable
    public Instant removed() {
        return this.whenRemoved;
    }

    /**
     * Marks this instance as removed.
     * @param when when it was removed.
     */
    protected void setRemoved(@NonNull final Instant when) {
        this.whenRemoved = when;
    }

    /**
     * Checks if this instance as been removed.
     * @return such information.
     */
    @Override
    public boolean isRemoved() {
        return this.whenRemoved != null;
    }
}
