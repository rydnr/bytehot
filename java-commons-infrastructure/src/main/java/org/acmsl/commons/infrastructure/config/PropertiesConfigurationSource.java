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
 * Filename: PropertiesConfigurationSource.java
 *
 * Author: rydnr
 *
 * Description: Configuration source that loads from properties files.
 *
 */
package org.acmsl.commons.infrastructure.config;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Configuration source that loads configuration from properties files.
 * 
 * This source can load properties files from the classpath or from the file system.
 * It converts the properties to a map for consistent handling with other sources.
 * 
 * @author rydnr
 * @since 2025-06-27
 */
public class PropertiesConfigurationSource implements ConfigurationSource {

    /**
     * The properties file path or resource name.
     */
    @NonNull
    private final String filePath;

    /**
     * Whether to load from classpath resources.
     */
    private final boolean loadFromClasspath;

    /**
     * Priority for properties configuration source.
     */
    private static final int PRIORITY = 400;

    /**
     * Creates a new properties configuration source that loads from classpath.
     * 
     * @param resourceName the classpath resource name
     */
    public PropertiesConfigurationSource(@NonNull final String resourceName) {
        this.filePath = resourceName;
        this.loadFromClasspath = true;
    }

    /**
     * Creates a new properties configuration source.
     * 
     * @param filePath the file path
     * @param loadFromClasspath whether to load from classpath
     */
    public PropertiesConfigurationSource(@NonNull final String filePath, final boolean loadFromClasspath) {
        this.filePath = filePath;
        this.loadFromClasspath = loadFromClasspath;
    }

    /**
     * Creates a new properties configuration source for external files.
     * 
     * @param filePath the external file path
     * @return new properties configuration source
     */
    @NonNull
    public static PropertiesConfigurationSource fromFile(@NonNull final String filePath) {
        return new PropertiesConfigurationSource(filePath, false);
    }

    /**
     * Creates a new properties configuration source for classpath resources.
     * 
     * @param resourceName the classpath resource name
     * @return new properties configuration source
     */
    @NonNull
    public static PropertiesConfigurationSource fromClasspath(@NonNull final String resourceName) {
        return new PropertiesConfigurationSource(resourceName, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAvailable() {
        if (loadFromClasspath) {
            return getClass().getClassLoader().getResourceAsStream(filePath) != null;
        } else {
            final Path path = Paths.get(filePath);
            return Files.exists(path) && Files.isReadable(path);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    public Object loadConfiguration() throws ConfigurationException {
        try {
            final InputStream inputStream = loadFromClasspath ?
                getClass().getClassLoader().getResourceAsStream(filePath) :
                Files.newInputStream(Paths.get(filePath));

            if (inputStream == null) {
                return null;
            }

            try (inputStream) {
                final Properties properties = new Properties();
                properties.load(inputStream);
                
                // Convert properties to map for consistent handling
                final Map<String, Object> configurationMap = new HashMap<>();
                properties.stringPropertyNames().forEach(key -> 
                    configurationMap.put(key, properties.getProperty(key)));
                
                return configurationMap.isEmpty() ? null : configurationMap;
            }
        } catch (final Exception e) {
            throw new ConfigurationException("Failed to load properties configuration from: " + filePath, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public String getDescription() {
        return "Properties File (" + (loadFromClasspath ? "classpath" : "file system") + "): " + filePath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPriority() {
        return PRIORITY;
    }

    /**
     * Returns the file path or resource name.
     * 
     * @return the file path
     */
    @NonNull
    public String getFilePath() {
        return filePath;
    }

    /**
     * Returns whether this source loads from classpath.
     * 
     * @return true if loading from classpath
     */
    public boolean isLoadFromClasspath() {
        return loadFromClasspath;
    }
}