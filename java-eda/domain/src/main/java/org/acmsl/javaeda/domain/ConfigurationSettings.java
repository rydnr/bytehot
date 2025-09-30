package org.acmsl.javaeda.domain;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
  Access to the configuration layer.

  Class name: ConfigurationSettings

  Responsibilities:
    - Access configuration settings by name.

  Collaborators:
    - None
 */
public interface ConfigurationSettings extends Port {
    /**
     * Retrieves a string value.
     * @param key the key.
     * @return the value, or {@code null} if non-existent.
     */
    @Nullable
    String getString(@NonNull String key);

    /**
     * Retrieves an integer value.
     * @param key the key.
     * @return the value, or {@code null} if non-existent.
     */
    @Nullable
    Integer getInteger(@NonNull String key);

    /**
     * Retrieves a long value.
     * @param key the key.
     * @return the value, or {@code null} if non-existent.
     */
    @Nullable
    Long getLong(@NonNull String key);

    /**
     * Retrieves an array of strings.
     * @param key the key.
     * @return the value, or an empty array if non-existent.
     */
    @NonNull
    String[] getStringArray(@NonNull String key);
}
