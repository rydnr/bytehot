package org.acmsl.javaeda.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
  Primary keys for the {@link LogSettings} entity.

  Class name: LogSettingsPrimaryKey

  Responsibilities:
    - Extract a primary key from the invariants.

  Collaborators:
    - org.acmsl.javaeda.domain.Invariant
*/
public class LogSettingsPrimaryKey implements PrimaryKey {

    /**
     * The invariants.
     */
    private final List<? extends Invariant<?>> invariants;

    /**
     * Creates a new instance.
     * @param invariants a map of invariant class, invariant value.
     */
    public LogSettingsPrimaryKey(final Invariant<?>... invariants) {
        this(Arrays.asList(invariants));
    }

    /**
     * Creates a new instance.
     * @param invariants a map of invariant class, invariant value.
     */
    public LogSettingsPrimaryKey(@NonNull final List<? extends Invariant<?>> invariants) {
        super();
        this.invariants = invariants;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public Map<String, Object> getAttributes() {
        return this.invariants.stream().collect(Collectors.toMap(Invariant::getName, Invariant::getValue));
    }
}
