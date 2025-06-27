/*
                        Java Commons Infrastructure

    Copyright (C) 2025-today  rydnr@acm-sl.org

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 3 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public v3
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    Thanks to ACM S.L. for distributing this library under the GPLv3 license.
    Contact info: jose.sanleandro@acm-sl.com

 ******************************************************************************
 *
 * Filename: BaseConfigurationAdapter.java
 *
 * Author: rydnr
 *
 * Description: Base configuration adapter providing hierarchical configuration
 *              loading from multiple sources with proper fallback handling.
 *
 */
package org.acmsl.commons.infrastructure.config;

import org.acmsl.commons.patterns.Adapter;
import org.acmsl.commons.patterns.Port;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Base configuration adapter providing hierarchical configuration loading.
 * 
 * This adapter implements a template method pattern that allows subclasses to
 * customize configuration sources while leveraging common infrastructure for
 * source prioritization, caching, and error handling.
 * 
 * Configuration Loading Priority:
 * 1. System Properties
 * 2. Environment Variables  
 * 3. Project-specific configuration sources
 * 4. Default configuration
 * 
 * @param <T> the configuration type
 * @author rydnr
 * @since 2025-06-27
 */
public abstract class BaseConfigurationAdapter<T> implements Adapter<Port> {

    /**
     * Logger instance for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseConfigurationAdapter.class);

    /**
     * Cached configuration instance.
     */
    @Nullable
    private T cachedConfiguration;

    /**
     * Whether configuration has been loaded.
     */
    private boolean configurationLoaded = false;

    /**
     * Loads configuration from the configured sources.
     * 
     * @return the loaded configuration
     * @throws ConfigurationException if configuration loading fails
     */
    @NonNull
    public T loadConfiguration() throws ConfigurationException {
        if (configurationLoaded && cachedConfiguration != null) {
            return cachedConfiguration;
        }

        try {
            final T configuration = loadConfigurationInternal();
            cachedConfiguration = configuration;
            configurationLoaded = true;
            return configuration;
        } catch (final Exception e) {
            throw new ConfigurationException("Failed to load configuration", e);
        }
    }

    /**
     * Checks if configuration is currently available from any source.
     * 
     * @return true if configuration is available
     */
    public boolean isConfigurationAvailable() {
        return hasSystemPropertyConfiguration() ||
               hasEnvironmentVariableConfiguration() ||
               hasProjectSpecificConfiguration() ||
               canCreateDefaultConfiguration();
    }

    /**
     * Returns a description of the configuration source being used.
     * 
     * @return configuration source description
     */
    @NonNull
    public String getConfigurationSource() {
        if (hasSystemPropertyConfiguration()) {
            return "System Properties";
        }
        if (hasEnvironmentVariableConfiguration()) {
            return "Environment Variables";
        }
        if (hasProjectSpecificConfiguration()) {
            return "Project Configuration Files";
        }
        return "Default Configuration";
    }

    /**
     * Clears the cached configuration, forcing reload on next access.
     */
    public void clearCache() {
        cachedConfiguration = null;
        configurationLoaded = false;
    }

    /**
     * Returns the configuration class for type safety.
     * 
     * @return the configuration class
     */
    @NonNull
    protected abstract Class<T> getConfigurationClass();

    /**
     * Returns the list of project-specific configuration sources.
     * 
     * @return list of configuration sources
     */
    @NonNull
    protected abstract List<ConfigurationSource> getProjectSpecificSources();

    /**
     * Returns the environment variable prefix for this configuration.
     * 
     * @return environment variable prefix
     */
    @NonNull
    protected String getEnvironmentPrefix() {
        return getConfigurationClass().getSimpleName().toUpperCase() + "_";
    }

    /**
     * Returns the system property prefix for this configuration.
     * 
     * @return system property prefix
     */
    @NonNull
    protected String getSystemPropertyPrefix() {
        return getConfigurationClass().getSimpleName().toLowerCase() + ".";
    }

    /**
     * Creates a default configuration when no other sources are available.
     * 
     * @return default configuration instance
     */
    @NonNull
    protected abstract T createDefaultConfiguration();

    /**
     * Transforms raw configuration data into the target configuration type.
     * 
     * @param rawConfiguration the raw configuration data
     * @return transformed configuration
     */
    @NonNull
    protected T transformConfiguration(@NonNull final Object rawConfiguration) {
        // Default implementation assumes no transformation needed
        return getConfigurationClass().cast(rawConfiguration);
    }

    /**
     * Validates the loaded configuration.
     * 
     * @param configuration the configuration to validate
     * @throws ConfigurationException if validation fails
     */
    protected void validateConfiguration(@NonNull final T configuration) throws ConfigurationException {
        // Default implementation does no validation
        // Subclasses can override to add specific validation logic
    }

    /**
     * Internal configuration loading implementation.
     * 
     * @return loaded configuration
     * @throws ConfigurationException if loading fails
     */
    @NonNull
    protected T loadConfigurationInternal() throws ConfigurationException {
        // Try system properties first
        final Optional<T> systemPropsConfig = loadFromSystemProperties();
        if (systemPropsConfig.isPresent()) {
            final T config = systemPropsConfig.get();
            validateConfiguration(config);
            return config;
        }

        // Try environment variables
        final Optional<T> envConfig = loadFromEnvironmentVariables();
        if (envConfig.isPresent()) {
            final T config = envConfig.get();
            validateConfiguration(config);
            return config;
        }

        // Try project-specific sources
        for (final ConfigurationSource source : getProjectSpecificSources()) {
            final Optional<T> sourceConfig = loadFromSource(source);
            if (sourceConfig.isPresent()) {
                final T config = sourceConfig.get();
                validateConfiguration(config);
                return config;
            }
        }

        // Fall back to default configuration
        final T defaultConfig = createDefaultConfiguration();
        validateConfiguration(defaultConfig);
        return defaultConfig;
    }

    /**
     * Loads configuration from system properties.
     * 
     * @return optional configuration
     */
    @NonNull
    protected Optional<T> loadFromSystemProperties() {
        try {
            final SystemPropertyConfigurationSource source = 
                new SystemPropertyConfigurationSource(getSystemPropertyPrefix());
            return loadFromSource(source);
        } catch (final Exception e) {
            LOGGER.debug("Failed to load configuration from system properties", e);
            return Optional.empty();
        }
    }

    /**
     * Loads configuration from environment variables.
     * 
     * @return optional configuration
     */
    @NonNull
    protected Optional<T> loadFromEnvironmentVariables() {
        try {
            final EnvironmentVariableConfigurationSource source = 
                new EnvironmentVariableConfigurationSource(getEnvironmentPrefix());
            return loadFromSource(source);
        } catch (final Exception e) {
            LOGGER.debug("Failed to load configuration from environment variables", e);
            return Optional.empty();
        }
    }

    /**
     * Loads configuration from a specific source.
     * 
     * @param source the configuration source
     * @return optional configuration
     */
    @NonNull
    protected Optional<T> loadFromSource(@NonNull final ConfigurationSource source) {
        try {
            if (!source.isAvailable()) {
                return Optional.empty();
            }

            final Object rawConfiguration = source.loadConfiguration();
            if (rawConfiguration == null) {
                return Optional.empty();
            }

            final T configuration = transformConfiguration(rawConfiguration);
            return Optional.of(configuration);
        } catch (final Exception e) {
            LOGGER.debug("Failed to load configuration from source: {}", source.getDescription(), e);
            return Optional.empty();
        }
    }

    /**
     * Checks if system property configuration is available.
     * 
     * @return true if available
     */
    protected boolean hasSystemPropertyConfiguration() {
        return new SystemPropertyConfigurationSource(getSystemPropertyPrefix()).isAvailable();
    }

    /**
     * Checks if environment variable configuration is available.
     * 
     * @return true if available
     */
    protected boolean hasEnvironmentVariableConfiguration() {
        return new EnvironmentVariableConfigurationSource(getEnvironmentPrefix()).isAvailable();
    }

    /**
     * Checks if project-specific configuration is available.
     * 
     * @return true if available
     */
    protected boolean hasProjectSpecificConfiguration() {
        return getProjectSpecificSources().stream()
            .anyMatch(ConfigurationSource::isAvailable);
    }

    /**
     * Checks if default configuration can be created.
     * 
     * @return true if default configuration can be created
     */
    protected boolean canCreateDefaultConfiguration() {
        return true; // Default implementation always allows default configuration
    }
}