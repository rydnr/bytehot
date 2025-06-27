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
 * Filename: ConfigurationException.java
 *
 * Author: rydnr
 *
 * Description: Exception thrown when configuration operations fail.
 *
 */
package org.acmsl.commons.infrastructure.config;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Exception thrown when configuration operations fail.
 * 
 * This exception is used to indicate failures in configuration loading,
 * parsing, validation, or transformation operations.
 * 
 * @author rydnr
 * @since 2025-06-27
 */
public class ConfigurationException extends Exception {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new ConfigurationException with the specified message.
     * 
     * @param message the exception message
     */
    public ConfigurationException(@NonNull final String message) {
        super(message);
    }

    /**
     * Creates a new ConfigurationException with the specified message and cause.
     * 
     * @param message the exception message
     * @param cause the underlying cause
     */
    public ConfigurationException(@NonNull final String message, @Nullable final Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new ConfigurationException with the specified cause.
     * 
     * @param cause the underlying cause
     */
    public ConfigurationException(@Nullable final Throwable cause) {
        super(cause);
    }
}