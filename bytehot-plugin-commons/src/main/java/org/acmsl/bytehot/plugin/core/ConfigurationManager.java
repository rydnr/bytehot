/*
                        ByteHot

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
 * Filename: ConfigurationManager.java
 *
 * Author: Claude Code
 *
 * Class name: ConfigurationManager
 *
 * Responsibilities:
 *   - Load plugin configuration from various sources
 *   - Validate configuration integrity and completeness
 *   - Manage configuration adapters for different tools
 *   - Provide unified configuration access interface
 *
 * Collaborators:
 *   - PluginConfiguration: Configuration data structure
 *   - ConfigurationAdapter: Tool-specific configuration loaders
 *   - ConfigurationValidator: Configuration validation logic
 *   - PluginBase: Uses configuration during initialization
 */
package org.acmsl.bytehot.plugin.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Manages plugin configuration loading and validation with adapter pattern support.
 * Supports multiple configuration sources with automatic discovery and validation.
 * 
 * @author Claude Code
 * @since 2025-07-03
 */
public class ConfigurationManager {

    /**
     * Current active configuration.
     */
    protected PluginConfiguration configuration;

    /**
     * List of registered configuration adapters.
     */
    protected final List<ConfigurationAdapter> adapters = new ArrayList<>();

    /**
     * Registers a configuration adapter for loading from specific sources.
     * 
     * @param adapter the configuration adapter to register
     */
    public void registerAdapter(final ConfigurationAdapter adapter) {
        if (adapter != null) {
            adapters.add(adapter);
        }
    }

    /**
     * Loads configuration using registered adapters in order.
     * Returns true if any adapter successfully loads and validates configuration.
     * 
     * @return true if configuration was successfully loaded and validated, false otherwise
     */
    public boolean loadConfiguration() {
        for (ConfigurationAdapter adapter : adapters) {
            final Optional<PluginConfiguration> config = adapter.loadConfiguration();
            if (config.isPresent()) {
                if (validateConfiguration(config.get())) {
                    configuration = config.get();
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Gets the currently loaded configuration.
     * 
     * @return the current configuration, or null if none loaded
     */
    public PluginConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * Validates configuration using built-in validation rules.
     * 
     * @param config the configuration to validate
     * @return true if configuration is valid, false otherwise
     */
    protected boolean validateConfiguration(final PluginConfiguration config) {
        if (config == null) {
            return false;
        }

        final ConfigurationValidator validator = new ConfigurationValidator();
        return validator.validate(config).isEmpty();
    }
}

/**
 * Interface for configuration adapters that load configuration from specific sources.
 * Implementations handle tool-specific configuration formats and locations.
 */
interface ConfigurationAdapter {

    /**
     * Attempts to load configuration from this adapter's source.
     * 
     * @return Optional containing configuration if found and parseable, empty otherwise
     */
    Optional<PluginConfiguration> loadConfiguration();

    /**
     * Saves configuration to this adapter's target location.
     * 
     * @param config the configuration to save
     * @return true if save was successful, false otherwise
     */
    boolean saveConfiguration(final PluginConfiguration config);

    /**
     * Gets the name of this configuration adapter.
     * 
     * @return the adapter name for identification
     */
    String getAdapterName();
}

/**
 * Validates plugin configuration for completeness and correctness.
 * Returns a list of validation errors, empty if configuration is valid.
 */
class ConfigurationValidator {

    /**
     * Validates a plugin configuration.
     * 
     * @param config the configuration to validate
     * @return list of validation errors, empty if valid
     */
    public List<String> validate(final PluginConfiguration config) {
        final List<String> errors = new ArrayList<>();

        if (config == null) {
            errors.add("Configuration cannot be null");
            return errors;
        }

        if (!config.isValid()) {
            errors.add("Configuration validation failed");
        }

        if (config.getConfigurationName() == null || config.getConfigurationName().trim().isEmpty()) {
            errors.add("Configuration name cannot be null or empty");
        }

        return errors;
    }
}