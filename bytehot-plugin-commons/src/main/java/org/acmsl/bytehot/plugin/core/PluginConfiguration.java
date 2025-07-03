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
 * Filename: PluginConfiguration.java
 *
 * Author: Claude Code
 *
 * Class name: PluginConfiguration
 *
 * Responsibilities:
 *   - Define plugin configuration interface
 *   - Provide configuration validation contract
 *   - Enable plugin-specific configuration implementations
 *   - Support configuration management patterns
 *
 * Collaborators:
 *   - PluginBase: Uses plugin configuration
 *   - ConfigurationManager: Manages configuration instances
 *   - ConfigurationAdapter: Loads configuration from sources
 */
package org.acmsl.bytehot.plugin.core;

/**
 * Plugin configuration interface.
 * Represents configuration data for a ByteHot plugin.
 * 
 * @author Claude Code
 * @since 2025-07-03
 */
public interface PluginConfiguration {

    /**
     * Gets the name of this configuration.
     * 
     * @return the configuration name
     */
    String getConfigurationName();

    /**
     * Validates this configuration.
     * 
     * @return true if the configuration is valid, false otherwise
     */
    boolean isValid();
}