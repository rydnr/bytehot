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
 * Filename: DefaultConfigurationAdapter.java
 *
 * Author: Claude Code
 *
 * Class name: DefaultConfigurationAdapter
 *
 * Responsibilities:
 *   - Provide default configuration when no external config found
 *   - Serve as fallback configuration source
 *   - Handle basic configuration loading patterns
 *   - Support configuration persistence if needed
 *
 * Collaborators:
 *   - ConfigurationAdapter: Interface being implemented
 *   - PluginConfiguration: Configuration data being managed
 *   - Optional: Safe return type for configuration loading
 */
package org.acmsl.bytehot.plugin.core;

import java.util.Optional;

/**
 * Default implementation of ConfigurationAdapter that provides fallback configuration.
 * Always returns the default configuration provided during construction.
 * 
 * @author Claude Code
 * @since 2025-07-03
 */
public class DefaultConfigurationAdapter implements ConfigurationAdapter {

    /**
     * The default configuration to return.
     */
    protected final PluginConfiguration defaultConfiguration;

    /**
     * Creates a new default configuration adapter with the specified default configuration.
     * 
     * @param defaultConfiguration the default configuration to use
     */
    public DefaultConfigurationAdapter(final PluginConfiguration defaultConfiguration) {
        this.defaultConfiguration = defaultConfiguration;
    }

    @Override
    public Optional<PluginConfiguration> loadConfiguration() {
        // Always return the default configuration
        return Optional.ofNullable(defaultConfiguration);
    }

    @Override
    public boolean saveConfiguration(final PluginConfiguration config) {
        // Default implementation doesn't persist configuration
        // Subclasses can override to provide actual persistence
        return true;
    }

    @Override
    public String getAdapterName() {
        return "default";
    }
}