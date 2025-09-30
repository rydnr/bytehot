package org.acmsl.javaeda.domain;

import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
  An identifier of the profile the application is running on.

  Class name: EdaProfile

  Responsibilities:
    - Distinguish one profile from another.

  Collaborators:
    - None
 */
public interface EdaProfile extends Invariant<String> {

    /**
     * Retrieves the name of this invariant.
     * @return the {@code "eda.profile"}.
     */
    @Override
    @NonNull
    default String getName() {
        return "eda.profile";
    }

    /**
     * Retrieves the EDA profile from the {@code eda.profile} property, or from the {@code EDA_PROFILE} environment variable.
     * @return the profile.
     */
    @Override
    @NonNull
    default Optional<String> getValue() {
        return Optional.ofNullable(System.getProperty("eda.profile", System.getenv("EDA_PROFILE")));
    }
}
