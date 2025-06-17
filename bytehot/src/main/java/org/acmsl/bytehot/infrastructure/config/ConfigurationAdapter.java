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
 * Filename: ConfigurationAdapter.java
 *
 * Author: Claude Code
 *
 * Class name: ConfigurationAdapter
 *
 * Responsibilities:
 *   - Implement configuration loading from various sources
 *   - Handle YAML, properties, and environment variable configuration
 *   - Provide infrastructure implementation of ConfigurationPort
 *
 * Collaborators:
 *   - ConfigurationPort: Interface this adapter implements
 *   - WatchConfiguration: Domain model for configuration
 */
package org.acmsl.bytehot.infrastructure.config;

import org.acmsl.bytehot.domain.ConfigurationPort;
import org.acmsl.bytehot.domain.FolderWatch;
import org.acmsl.bytehot.domain.WatchConfiguration;

import org.acmsl.commons.patterns.Adapter;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Infrastructure adapter for configuration loading operations
 * @author Claude Code
 * @since 2025-06-17
 */
public class ConfigurationAdapter
    implements ConfigurationPort, Adapter<ConfigurationPort> {

    /**
     * Default configuration file names to search for
     */
    private static final String[] DEFAULT_CONFIG_FILES = {
        "bytehot.yml",
        "bytehot.yaml",
        "application.yml",
        "application.yaml"
    };

    /**
     * Environment variable prefix for ByteHot configuration
     */
    private static final String ENV_PREFIX = "BYTEHOT_";

    /**
     * System property prefix for ByteHot configuration
     */
    private static final String PROP_PREFIX = "bytehot.";

    /**
     * Loads watch configuration from the configured source
     */
    @Override
    public WatchConfiguration loadWatchConfiguration() throws Exception {
        // Try loading from system properties first
        final WatchConfiguration systemPropsConfig = loadFromSystemProperties();
        if (systemPropsConfig != null) {
            return systemPropsConfig;
        }

        // Try loading from environment variables
        final WatchConfiguration envConfig = loadFromEnvironment();
        if (envConfig != null) {
            return envConfig;
        }

        // Try loading from configuration files
        for (final String configFile : DEFAULT_CONFIG_FILES) {
            final WatchConfiguration fileConfig = loadFromFile(configFile);
            if (fileConfig != null) {
                return fileConfig;
            }
        }

        // Return default configuration if nothing found
        return createDefaultConfiguration();
    }

    /**
     * Checks if configuration source is available
     */
    @Override
    public boolean isConfigurationAvailable() {
        // Check if any system properties are set
        if (System.getProperty(PROP_PREFIX + "watch.paths") != null) {
            return true;
        }

        // Check if any environment variables are set
        if (System.getenv(ENV_PREFIX + "WATCH_PATHS") != null) {
            return true;
        }

        // Check if any configuration files exist
        for (final String configFile : DEFAULT_CONFIG_FILES) {
            if (getClass().getClassLoader().getResourceAsStream(configFile) != null) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns the configuration source description
     */
    @Override
    public String getConfigurationSource() {
        if (System.getProperty(PROP_PREFIX + "watch.paths") != null) {
            return "System Properties";
        }

        if (System.getenv(ENV_PREFIX + "WATCH_PATHS") != null) {
            return "Environment Variables";
        }

        for (final String configFile : DEFAULT_CONFIG_FILES) {
            if (getClass().getClassLoader().getResourceAsStream(configFile) != null) {
                return "Configuration File: " + configFile;
            }
        }

        return "Default Configuration";
    }

    /**
     * Returns the port interface this adapter implements
     */
    @Override
    public Class<ConfigurationPort> adapts() {
        return ConfigurationPort.class;
    }

    /**
     * Loads configuration from system properties
     */
    protected WatchConfiguration loadFromSystemProperties() {
        final String watchPaths = System.getProperty(PROP_PREFIX + "watch.paths");
        if (watchPaths == null) {
            return null;
        }

        final List<FolderWatch> folders = new ArrayList<>();
        final String[] paths = watchPaths.split(",");
        
        for (final String pathStr : paths) {
            final Path path = Paths.get(pathStr.trim());
            final List<String> patterns = List.of("*.class");
            final boolean recursive = Boolean.parseBoolean(
                System.getProperty(PROP_PREFIX + "watch.recursive", "true")
            );
            
            folders.add(new FolderWatch(path, 1000));
        }

        final WatchConfiguration config = new WatchConfiguration(8080);
        try {
            final java.lang.reflect.Field foldersField = WatchConfiguration.class.getDeclaredField("folders");
            foldersField.setAccessible(true);
            foldersField.set(config, folders);
        } catch (final Exception e) {
            throw new RuntimeException("Failed to set folders in WatchConfiguration", e);
        }
        return config;
    }

    /**
     * Loads configuration from environment variables
     */
    protected WatchConfiguration loadFromEnvironment() {
        final String watchPaths = System.getenv(ENV_PREFIX + "WATCH_PATHS");
        if (watchPaths == null) {
            return null;
        }

        final List<FolderWatch> folders = new ArrayList<>();
        final String[] paths = watchPaths.split(",");
        
        for (final String pathStr : paths) {
            final Path path = Paths.get(pathStr.trim());
            final List<String> patterns = List.of("*.class");
            final boolean recursive = Boolean.parseBoolean(
                System.getenv(ENV_PREFIX + "WATCH_RECURSIVE")
            );
            
            folders.add(new FolderWatch(path, 1000));
        }

        final WatchConfiguration config = new WatchConfiguration(8080);
        try {
            final java.lang.reflect.Field foldersField = WatchConfiguration.class.getDeclaredField("folders");
            foldersField.setAccessible(true);
            foldersField.set(config, folders);
        } catch (final Exception e) {
            throw new RuntimeException("Failed to set folders in WatchConfiguration", e);
        }
        return config;
    }

    /**
     * Loads configuration from a YAML file
     */
    @SuppressWarnings("unchecked")
    protected WatchConfiguration loadFromFile(final String filename) {
        try (final InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filename)) {
            if (inputStream == null) {
                return null;
            }

            final Yaml yaml = new Yaml();
            final Map<String, Object> data = yaml.load(inputStream);
            
            if (data == null || !data.containsKey("bytehot")) {
                return null;
            }

            final Map<String, Object> bytehotConfig = (Map<String, Object>) data.get("bytehot");
            final List<Map<String, Object>> watchConfigs = (List<Map<String, Object>>) bytehotConfig.get("watch");
            
            if (watchConfigs == null) {
                return null;
            }

            final List<FolderWatch> folders = new ArrayList<>();
            
            for (final Map<String, Object> watchConfig : watchConfigs) {
                final String pathStr = (String) watchConfig.get("path");
                final List<String> patterns = (List<String>) watchConfig.getOrDefault("patterns", List.of("*.class"));
                final boolean recursive = (Boolean) watchConfig.getOrDefault("recursive", true);
                
                final Path path = Paths.get(pathStr);
                folders.add(new FolderWatch(path, 1000));
            }

            final WatchConfiguration config = new WatchConfiguration(8080);
        try {
            final java.lang.reflect.Field foldersField = WatchConfiguration.class.getDeclaredField("folders");
            foldersField.setAccessible(true);
            foldersField.set(config, folders);
        } catch (final Exception e) {
            throw new RuntimeException("Failed to set folders in WatchConfiguration", e);
        }
        return config;
            
        } catch (final Exception e) {
            // Log error but don't fail - try next configuration source
            System.err.println("Failed to load configuration from " + filename + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Creates default configuration when no explicit configuration is found
     */
    protected WatchConfiguration createDefaultConfiguration() {
        final List<FolderWatch> folders = new ArrayList<>();
        
        // Default watch configuration for common build output directories
        final String[] defaultPaths = {
            "target/classes",
            "build/classes",
            "out/production/classes"
        };
        
        for (final String pathStr : defaultPaths) {
            final Path path = Paths.get(pathStr);
            if (path.toFile().exists()) {
                folders.add(new FolderWatch(path, 1000));
            }
        }

        // If no build directories found, watch current directory
        if (folders.isEmpty()) {
            folders.add(new FolderWatch(Paths.get("."), 1000));
        }

        final WatchConfiguration config = new WatchConfiguration(8080);
        try {
            final java.lang.reflect.Field foldersField = WatchConfiguration.class.getDeclaredField("folders");
            foldersField.setAccessible(true);
            foldersField.set(config, folders);
        } catch (final Exception e) {
            throw new RuntimeException("Failed to set folders in WatchConfiguration", e);
        }
        return config;
    }
}