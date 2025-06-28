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
 * Filename: JavaedaFoundation.java
 *
 * Author: JavaEDA Framework Generator
 *
 * Class name: JavaedaFoundation
 *
 * Responsibilities:
 *   - Provide foundational utilities for JavaEDA framework
 *   - Enable basic domain-driven design patterns
 *   - Support event-driven architecture concepts
 */
package org.acmsl.javaeda.domain;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Foundation class for JavaEDA framework providing core utilities.
 * @author JavaEDA Framework Generator
 * @since 1.0.0
 */
public class JavaedaFoundation {

    /**
     * Framework version
     */
    @NonNull
    public static final String VERSION = "1.0.0";

    /**
     * Framework name
     */
    @NonNull
    public static final String NAME = "JavaEDA Framework";

    /**
     * Private constructor to prevent instantiation
     */
    private JavaedaFoundation() {
        // Utility class
    }

    /**
     * Gets the framework version
     * @return framework version string
     */
    @NonNull
    public static String getVersion() {
        return VERSION;
    }

    /**
     * Gets the framework name
     * @return framework name
     */
    @NonNull
    public static String getName() {
        return NAME;
    }

    /**
     * Gets a formatted framework identifier
     * @return framework identifier
     */
    @NonNull
    public static String getIdentifier() {
        return NAME + " v" + VERSION;
    }

    /**
     * Validates that a string is not null or empty
     * @param value the value to validate
     * @param fieldName the field name for error messages
     * @throws IllegalArgumentException if validation fails
     */
    public static void requireNonEmpty(@NonNull final String value, @NonNull final String fieldName) {
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty");
        }
    }

    /**
     * Validates that a value is not null
     * @param value the value to validate
     * @param fieldName the field name for error messages
     * @param <T> the value type
     * @throws IllegalArgumentException if validation fails
     */
    public static <T> void requireNonNull(final T value, @NonNull final String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null");
        }
    }

    /**
     * Checks if the framework is properly initialized
     * @return true if framework is ready
     */
    public static boolean isInitialized() {
        return true; // Basic implementation
    }
}