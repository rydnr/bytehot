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
 * Filename: SystemPropertyConfigurationSource.java
 *
 * Author: rydnr
 *
 * Description: Configuration source that loads from system properties.
 *
 */
package org.acmsl.commons.infrastructure.config;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Configuration source that loads configuration from system properties.
 * 
 * This source scans all system properties for those matching the specified
 * prefix and creates a configuration map from them.
 * 
 * @author rydnr
 * @since 2025-06-27
 */
public class SystemPropertyConfigurationSource implements ConfigurationSource {

    /**
     * The system property prefix to look for.
     */
    @NonNull
    private final String prefix;

    /**
     * Priority for system property configuration source.
     */
    private static final int PRIORITY = 1000;

    /**
     * Creates a new system property configuration source.
     * 
     * @param prefix the property prefix to scan for
     */
    public SystemPropertyConfigurationSource(@NonNull final String prefix) {
        this.prefix = prefix;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAvailable() {
        final Properties systemProperties = System.getProperties();
        return systemProperties.stringPropertyNames().stream()
            .anyMatch(name -> name.startsWith(prefix));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    public Object loadConfiguration() throws ConfigurationException {
        try {
            final Properties systemProperties = System.getProperties();
            final Map<String, Object> configurationMap = new HashMap<>();

            systemProperties.stringPropertyNames().stream()
                .filter(name -> name.startsWith(prefix))
                .forEach(name -> {
                    final String key = name.substring(prefix.length());
                    final String value = systemProperties.getProperty(name);
                    configurationMap.put(key, value);
                });

            return configurationMap.isEmpty() ? null : configurationMap;
        } catch (final Exception e) {
            throw new ConfigurationException("Failed to load system property configuration", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public String getDescription() {
        return "System Properties (prefix: " + prefix + ")";
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
     * @return the property prefix
     */
    @NonNull
    public String getPrefix() {
        return prefix;
    }
}