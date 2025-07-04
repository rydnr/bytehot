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
 * Filename: ErrorType.java
 *
 * Author: Claude Code
 *
 * Class name: ErrorType
 *
 * Responsibilities:
 *   - Define categories of errors for classification purposes
 *   - Provide semantic grouping of errors by their nature
 *   - Support error handling strategy selection
 *
 * Collaborators:
 *   - ErrorClassification: Uses this enum for error categorization
 *   - ErrorPattern: References this enum for pattern matching
 */
package org.acmsl.bytehot.infrastructure.production;

/**
 * Enumeration of error types for classification purposes.
 * @author Claude Code
 * @since 2025-07-04
 */
public enum ErrorType {
    
    /**
     * Network-related errors (timeouts, connection failures).
     */
    NETWORK,
    
    /**
     * Input/output errors (file access, stream operations).
     */
    IO,
    
    /**
     * Memory-related errors (out of memory, allocation failures).
     */
    MEMORY,
    
    /**
     * Timeout errors (operation timeouts, deadlock detection).
     */
    TIMEOUT,
    
    /**
     * Validation errors (invalid input, constraint violations).
     */
    VALIDATION,
    
    /**
     * Security-related errors (authentication, authorization).
     */
    SECURITY,
    
    /**
     * Unsupported operation errors.
     */
    UNSUPPORTED,
    
    /**
     * Runtime errors (null pointer, illegal state).
     */
    RUNTIME,
    
    /**
     * Configuration errors (missing properties, invalid settings).
     */
    CONFIGURATION,
    
    /**
     * External dependency errors (database, web service).
     */
    EXTERNAL_DEPENDENCY,
    
    /**
     * Hot-swap specific errors (incompatible changes, agent failures).
     */
    HOTSWAP,
    
    /**
     * Unknown or unclassified errors.
     */
    UNKNOWN
}