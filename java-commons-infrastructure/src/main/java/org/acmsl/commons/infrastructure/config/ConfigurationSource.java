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
 * Filename: ConfigurationSource.java
 *
 * Author: rydnr
 *
 * Description: Interface representing a source of configuration data.
 *
 */
package org.acmsl.commons.infrastructure.config;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Interface representing a source of configuration data.
 * 
 * Configuration sources provide a uniform interface for loading configuration
 * from different sources such as files, environment variables, system properties,
 * databases, or remote services.
 * 
 * @author rydnr
 * @since 2025-06-27
 */
public interface ConfigurationSource {

    /**
     * Checks if this configuration source is available.
     * 
     * @return true if the source is available for loading
     */
    boolean isAvailable();

    /**
     * Loads configuration data from this source.
     * 
     * @return the configuration data, or null if not available
     * @throws ConfigurationException if loading fails
     */
    @Nullable
    Object loadConfiguration() throws ConfigurationException;

    /**
     * Returns a human-readable description of this configuration source.
     * 
     * @return source description
     */
    @NonNull
    String getDescription();

    /**
     * Returns the priority of this configuration source.
     * Higher values indicate higher priority.
     * 
     * @return source priority
     */
    int getPriority();
}