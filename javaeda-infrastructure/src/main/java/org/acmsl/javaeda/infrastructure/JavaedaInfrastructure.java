/*
                        JavaEDA Framework

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
 * Filename: JavaedaInfrastructure.java
 *
 * Author: JavaEDA Framework Generator
 *
 * Class name: JavaedaInfrastructure
 *
 * Responsibilities:
 *   - Provide infrastructure layer utilities for JavaEDA framework
 *   - Support adapter discovery and port resolution
 *   - Enable external system integration patterns
 *   - Provide infrastructure monitoring and health checking
 */
package org.acmsl.javaeda.infrastructure;

import org.acmsl.javaeda.domain.JavaedaFoundation;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Foundation class for JavaEDA infrastructure layer providing adapter and integration utilities.
 * @author JavaEDA Framework Generator
 * @since 1.0.0
 */
public class JavaedaInfrastructure {

    /**
     * Infrastructure layer identifier
     */
    @NonNull
    public static final String LAYER_NAME = "Infrastructure";

    /**
     * Private constructor to prevent instantiation
     */
    private JavaedaInfrastructure() {
        // Utility class
    }

    /**
     * Gets the infrastructure layer name
     * @return infrastructure layer name
     */
    @NonNull
    public static String getLayerName() {
        return LAYER_NAME;
    }

    /**
     * Gets a formatted infrastructure layer identifier
     * @return infrastructure layer identifier
     */
    @NonNull
    public static String getLayerIdentifier() {
        return JavaedaFoundation.getName() + " " + LAYER_NAME + " Layer v" + JavaedaFoundation.getVersion();
    }

    /**
     * Checks if the infrastructure layer is properly initialized
     * @return true if infrastructure is ready
     */
    public static boolean isInfrastructureReady() {
        return JavaedaFoundation.isInitialized();
    }

    /**
     * Validates infrastructure adapter configuration
     * @param adapterName the adapter name to validate
     * @param config the adapter configuration
     * @throws IllegalArgumentException if configuration is invalid
     */
    public static void validateAdapterConfig(@NonNull final String adapterName, @NonNull final Object config) {
        JavaedaFoundation.requireNonEmpty(adapterName, "adapterName");
        JavaedaFoundation.requireNonNull(config, "adapterConfig");
    }

    /**
     * Gets infrastructure layer status information
     * @return status description
     */
    @NonNull
    public static String getStatus() {
        return String.format("%s: %s", 
            getLayerIdentifier(), 
            isInfrastructureReady() ? "Ready" : "Not Ready");
    }
}