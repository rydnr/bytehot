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
 * Filename: EnvironmentVariableConfigurationSource.java
 *
 * Author: rydnr
 *
 * Description: Configuration source that loads from environment variables.
 *
 */
package org.acmsl.commons.infrastructure.config;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration source that loads configuration from environment variables.
 * 
 * This source scans all environment variables for those matching the specified
 * prefix and creates a configuration map from them.
 * 
 * @author rydnr
 * @since 2025-06-27
 */
public class EnvironmentVariableConfigurationSource implements ConfigurationSource {

    /**
     * The environment variable prefix to look for.
     */
    @NonNull
    private final String prefix;

    /**
     * Priority for environment variable configuration source.
     */
    private static final int PRIORITY = 900;

    /**
     * Creates a new environment variable configuration source.
     * 
     * @param prefix the environment variable prefix to scan for
     */
    public EnvironmentVariableConfigurationSource(@NonNull final String prefix) {
        this.prefix = prefix;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAvailable() {
        final Map<String, String> environmentVariables = System.getenv();
        return environmentVariables.keySet().stream()
            .anyMatch(name -> name.startsWith(prefix));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    public Object loadConfiguration() throws ConfigurationException {
        try {
            final Map<String, String> environmentVariables = System.getenv();
            final Map<String, Object> configurationMap = new HashMap<>();

            environmentVariables.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(prefix))
                .forEach(entry -> {
                    final String key = entry.getKey().substring(prefix.length()).toLowerCase();
                    final String value = entry.getValue();
                    configurationMap.put(key, value);
                });

            return configurationMap.isEmpty() ? null : configurationMap;
        } catch (final Exception e) {
            throw new ConfigurationException("Failed to load environment variable configuration", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public String getDescription() {
        return "Environment Variables (prefix: " + prefix + ")";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPriority() {
        return PRIORITY;
    }

    /**
     * Returns the prefix used by this configuration source.
     * 
     * @return the environment variable prefix
     */
    @NonNull
    public String getPrefix() {
        return prefix;
    }
}