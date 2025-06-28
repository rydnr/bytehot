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
 * Filename: JavaedaApplication.java
 *
 * Author: JavaEDA Framework Generator
 *
 * Class name: JavaedaApplication
 *
 * Responsibilities:
 *   - Provide application layer utilities for JavaEDA framework
 *   - Support use case orchestration and workflow management
 *   - Enable command and query handling coordination
 *   - Provide application-level monitoring and health checking
 */
package org.acmsl.javaeda.application;

import org.acmsl.javaeda.domain.JavaedaFoundation;
import org.acmsl.javaeda.infrastructure.JavaedaInfrastructure;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Foundation class for JavaEDA application layer providing orchestration and workflow utilities.
 * @author JavaEDA Framework Generator
 * @since 1.0.0
 */
public class JavaedaApplication {

    /**
     * Application layer identifier
     */
    @NonNull
    public static final String LAYER_NAME = "Application";

    /**
     * Private constructor to prevent instantiation
     */
    private JavaedaApplication() {
        // Utility class
    }

    /**
     * Gets the application layer name
     * @return application layer name
     */
    @NonNull
    public static String getLayerName() {
        return LAYER_NAME;
    }

    /**
     * Gets a formatted application layer identifier
     * @return application layer identifier
     */
    @NonNull
    public static String getLayerIdentifier() {
        return JavaedaFoundation.getName() + " " + LAYER_NAME + " Layer v" + JavaedaFoundation.getVersion();
    }

    /**
     * Checks if the application layer is properly initialized
     * @return true if application layer is ready
     */
    public static boolean isApplicationReady() {
        return JavaedaFoundation.isInitialized() && JavaedaInfrastructure.isInfrastructureReady();
    }

    /**
     * Validates use case configuration
     * @param useCaseName the use case name to validate
     * @param config the use case configuration
     * @throws IllegalArgumentException if configuration is invalid
     */
    public static void validateUseCaseConfig(@NonNull final String useCaseName, @NonNull final Object config) {
        JavaedaFoundation.requireNonEmpty(useCaseName, "useCaseName");
        JavaedaFoundation.requireNonNull(config, "useCaseConfig");
    }

    /**
     * Gets application layer status information
     * @return status description
     */
    @NonNull
    public static String getStatus() {
        return String.format("%s: %s", 
            getLayerIdentifier(), 
            isApplicationReady() ? "Ready" : "Not Ready");
    }

    /**
     * Gets complete framework status across all layers
     * @return complete framework status
     */
    @NonNull
    public static String getFrameworkStatus() {
        return String.format("JavaEDA Framework Status:\n- Domain: %s\n- Infrastructure: %s\n- Application: %s",
            JavaedaFoundation.isInitialized() ? "Ready" : "Not Ready",
            JavaedaInfrastructure.isInfrastructureReady() ? "Ready" : "Not Ready", 
            isApplicationReady() ? "Ready" : "Not Ready");
    }
}