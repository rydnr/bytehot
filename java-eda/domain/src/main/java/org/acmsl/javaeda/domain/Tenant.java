package org.acmsl.javaeda.domain;

import java.util.UUID;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
  Represents a tenant.

  Class name: Tenant

  Responsibilities:
    - Represent a tenant.

  Collaborators:
    - None
 */
@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface Tenant {
    /**
     * Retrieves the tenant id.
     * @return the id.
     */
    @NonNull
    UUID getId();
}
