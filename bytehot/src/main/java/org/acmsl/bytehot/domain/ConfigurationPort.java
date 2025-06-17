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
 * Filename: ConfigurationPort.java
 *
 * Author: Claude Code
 *
 * Class name: ConfigurationPort
 *
 * Responsibilities:
 *   - Define interface for configuration loading operations
 *   - Abstract configuration source from domain logic
 *   - Enable different configuration strategies (file, env, etc.)
 *
 * Collaborators:
 *   - WatchConfiguration: Uses this port to load configuration
 *   - ConfigurationAdapter: Infrastructure implementation
 */
package org.acmsl.bytehot.domain;

import org.acmsl.commons.patterns.Port;

/**
 * Port interface for configuration loading operations
 * @author Claude Code
 * @since 2025-06-17
 */
public interface ConfigurationPort
    extends Port {

    /**
     * Loads watch configuration from the configured source
     * @return loaded watch configuration
     * @throws Exception if configuration loading fails
     */
    WatchConfiguration loadWatchConfiguration() throws Exception;

    /**
     * Checks if configuration source is available
     * @return true if configuration can be loaded
     */
    boolean isConfigurationAvailable();

    /**
     * Returns the configuration source description
     * @return human-readable description of configuration source
     */
    String getConfigurationSource();
}