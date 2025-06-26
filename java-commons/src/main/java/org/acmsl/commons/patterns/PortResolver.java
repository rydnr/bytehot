//;-*- mode: java -*-
/*
                        ACM-SL Commons

    Copyright (C) 2002-today  Jose San Leandro Armendariz
                              chous@acm-sl.org

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    Thanks to ACM S.L. for distributing this library under the LGPL license.
    Contact info: jose.sanleandro@acm-sl.com

 ******************************************************************************
 *
 * Filename: PortResolver.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Provides actual implementations of Port interfaces with plugin awareness.
 *
 */
package org.acmsl.commons.patterns;

import org.acmsl.commons.patterns.Port;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Enhanced PortResolver with plugin-aware discovery capabilities.
 * Provides actual implementations of Port interfaces with support for
 * plugin-based architecture and runtime discovery of implementations.
 * 
 * @param <P> the type of port to resolve
 * @author <a href="mailto:rydnr@acm-sl.org">rydnr</a>
 * @since 2025-06-08
 */
@SuppressWarnings("unused")
public interface PortResolver<P extends Port> {

    /**
     * Resolves all implementations of a given port.
     * @param port such port.
     * @return A list of implementations.
     */
    List<? extends P> resolveAll(final P port);

    /**
     * Resolves the main implementation of a given port.
     * @param port such port.
     * @return The port implementation, if any.
     */
    Optional<? extends P> resolve(final P port);

    /**
     * Resolves all implementations of a given port type by class.
     * @param portType the class of the port to resolve
     * @return A list of implementations found
     */
    List<? extends P> resolveAllByType(final Class<P> portType);

    /**
     * Resolves the main implementation of a given port type by class.
     * @param portType the class of the port to resolve
     * @return The port implementation, if any
     */
    Optional<? extends P> resolveByType(final Class<P> portType);

    /**
     * Checks if any implementation exists for the given port.
     * @param port the port to check
     * @return true if at least one implementation is available
     */
    default boolean hasImplementation(final P port) {
        return !resolveAll(port).isEmpty();
    }

    /**
     * Checks if any implementation exists for the given port type.
     * @param portType the port type to check
     * @return true if at least one implementation is available
     */
    default boolean hasImplementationByType(final Class<P> portType) {
        return !resolveAllByType(portType).isEmpty();
    }

    /**
     * Gets all available port types that have implementations.
     * @return set of port types with available implementations
     */
    Set<Class<? extends Port>> getAvailablePortTypes();

    /**
     * Gets the count of implementations for a given port.
     * @param port the port to count implementations for
     * @return the number of available implementations
     */
    default int getImplementationCount(final P port) {
        return resolveAll(port).size();
    }

    /**
     * Gets the count of implementations for a given port type.
     * @param portType the port type to count implementations for
     * @return the number of available implementations
     */
    default int getImplementationCountByType(final Class<P> portType) {
        return resolveAllByType(portType).size();
    }

    /**
     * Plugin-aware discovery methods for runtime loading.
     */
    
    /**
     * Discovers and loads plugins from the specified plugin directory.
     * @param pluginDirectory the directory to scan for plugins
     * @throws PluginDiscoveryException if plugin discovery fails
     */
    void discoverPlugins(final String pluginDirectory) throws PluginDiscoveryException;

    /**
     * Registers a plugin manually with the resolver.
     * @param plugin the plugin to register
     * @throws PluginRegistrationException if plugin registration fails
     */
    void registerPlugin(final Plugin plugin) throws PluginRegistrationException;

    /**
     * Unregisters a plugin from the resolver.
     * @param pluginId the ID of the plugin to unregister
     * @return true if the plugin was found and unregistered
     */
    boolean unregisterPlugin(final String pluginId);

    /**
     * Gets all currently registered plugins.
     * @return list of registered plugins
     */
    List<Plugin> getRegisteredPlugins();

    /**
     * Gets a plugin by its ID.
     * @param pluginId the plugin ID
     * @return the plugin if found
     */
    Optional<Plugin> getPlugin(final String pluginId);

    /**
     * Checks if a plugin is registered.
     * @param pluginId the plugin ID to check
     * @return true if the plugin is registered
     */
    default boolean isPluginRegistered(final String pluginId) {
        return getPlugin(pluginId).isPresent();
    }

    /**
     * Gets metadata about all available implementations.
     * @return implementation metadata summary
     */
    ImplementationMetadata getImplementationMetadata();

    /**
     * Plugin interface for extensible port implementations.
     */
    interface Plugin {
        /**
         * Gets the unique identifier for this plugin.
         * @return the plugin ID
         */
        String getId();

        /**
         * Gets the name of this plugin.
         * @return the plugin name
         */
        String getName();

        /**
         * Gets the version of this plugin.
         * @return the plugin version
         */
        String getVersion();

        /**
         * Gets the description of this plugin.
         * @return the plugin description
         */
        String getDescription();

        /**
         * Gets the port types this plugin provides implementations for.
         * @return set of supported port types
         */
        Set<Class<? extends Port>> getSupportedPortTypes();

        /**
         * Creates an instance of the specified port type.
         * @param portType the port type to create
         * @return the created port implementation
         * @throws PluginException if creation fails
         */
        <T extends Port> Optional<T> createPort(final Class<T> portType) throws PluginException;

        /**
         * Initializes the plugin with configuration.
         * @param configuration the plugin configuration
         * @throws PluginException if initialization fails
         */
        void initialize(final PluginConfiguration configuration) throws PluginException;

        /**
         * Shuts down the plugin and releases resources.
         * @throws PluginException if shutdown fails
         */
        void shutdown() throws PluginException;

        /**
         * Checks if this plugin is currently active.
         * @return true if the plugin is active
         */
        boolean isActive();
    }

    /**
     * Plugin configuration interface.
     */
    interface PluginConfiguration {
        /**
         * Gets a configuration property.
         * @param key the property key
         * @return the property value if present
         */
        Optional<String> getProperty(final String key);

        /**
         * Gets a configuration property with default value.
         * @param key the property key
         * @param defaultValue the default value
         * @return the property value or default
         */
        default String getProperty(final String key, final String defaultValue) {
            return getProperty(key).orElse(defaultValue);
        }

        /**
         * Gets all configuration properties.
         * @return all properties
         */
        java.util.Map<String, String> getAllProperties();
    }

    /**
     * Implementation metadata for discovery and introspection.
     */
    interface ImplementationMetadata {
        /**
         * Gets the total number of registered implementations.
         * @return the implementation count
         */
        int getTotalImplementationCount();

        /**
         * Gets the number of plugins providing implementations.
         * @return the plugin count
         */
        int getPluginCount();

        /**
         * Gets implementation counts by port type.
         * @return map of port types to implementation counts
         */
        java.util.Map<Class<? extends Port>, Integer> getImplementationCountsByType();

        /**
         * Gets a summary of all implementations.
         * @return formatted summary string
         */
        String getSummary();
    }

    /**
     * Exception thrown when plugin discovery fails.
     */
    class PluginDiscoveryException extends RuntimeException {
        /**
         * Serial version UID for serialization compatibility.
         */
        private static final long serialVersionUID = 1L;

        public PluginDiscoveryException(final String message) {
            super(message);
        }

        public PluginDiscoveryException(final String message, final Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Exception thrown when plugin registration fails.
     */
    class PluginRegistrationException extends RuntimeException {
        /**
         * Serial version UID for serialization compatibility.
         */
        private static final long serialVersionUID = 1L;

        public PluginRegistrationException(final String message) {
            super(message);
        }

        public PluginRegistrationException(final String message, final Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Exception thrown when plugin operations fail.
     */
    class PluginException extends RuntimeException {
        /**
         * Serial version UID for serialization compatibility.
         */
        private static final long serialVersionUID = 1L;

        public PluginException(final String message) {
            super(message);
        }

        public PluginException(final String message, final Throwable cause) {
            super(message, cause);
        }
    }
}
