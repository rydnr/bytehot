package org.acmsl.javaeda.domain.invariants;

import org.acmsl.javaeda.domain.Tenant;
import org.acmsl.javaeda.domain.BaseObject;
import org.acmsl.javaeda.domain.Invariant;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
  An invariant for the tenant.

  Class name: TenantInvariant.

  Responsibilities:
    - Provide the tenant as an invariant.

  Collaborators:
    - None
*/
@SuppressWarnings({"unused"})
public class TenantInvariant extends BaseObject implements Invariant<Tenant> {

    /**
     * The tenant instance.
     */
    private final Tenant tenant;

    /**
     * Creates a tenant invariant.
     */
    public TenantInvariant() {
        this(null);
    }

    /**
     * Creates a tenant invariant.
     * @param tenant the tenant.
     */
    public TenantInvariant(@Nullable final Tenant tenant) {
        super();
        this.tenant = tenant;
    }

    /**
     * Returns {@code "eda.tenant"}.
     * @return such literal.
     */
    @Override
    @NonNull
    public String getName() {
        return "eda.tenant";
    }

    /**
     * Returns the {@link Tenant} instance.
     * @return such information.
     */
    @Override
    @NonNull
    public Optional<Tenant> getValue() {
        return Optional.ofNullable(this.tenant);
    }
}
