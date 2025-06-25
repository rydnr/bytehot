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
 * Filename: ErrorClassification.java
 *
 * Author: Claude Code
 *
 * Enum name: ErrorClassification
 *
 * Responsibilities:
 *   - Classify errors for automatic categorization and analysis
 *   - Provide display names and labels for error reporting
 */
package org.acmsl.bytehot.domain;

/**
 * Error classification enumeration for automatic categorization and analysis
 * of exceptions and failures in the ByteHot system.
 * @author Claude Code
 * @since 2025-06-25
 */
public enum ErrorClassification {
    /**
     * Hot-Swap Failure - Errors related to bytecode hot-swapping operations.
     * This includes JVM redefinition failures, instrumentation issues,
     * and class loading problems during hot-swap operations.
     */
    HOT_SWAP_FAILURE("Hot-Swap Failure", "hotswap"),
    
    /**
     * Null Reference - Null pointer exceptions and null reference errors.
     * This classification covers NPEs and other null-related runtime errors
     * that indicate missing object initialization or improper state handling.
     */
    NULL_REFERENCE("Null Reference", "null-pointer"),
    
    /**
     * Type Mismatch - Type casting and compatibility errors.
     * This includes ClassCastException, type incompatibility issues,
     * and problems with type conversion or generic type handling.
     */
    TYPE_MISMATCH("Type Mismatch", "type-error"),
    
    /**
     * Invalid State - System or object state inconsistency errors.
     * This covers IllegalStateException and other errors that occur
     * when the system is in an unexpected or invalid state.
     */
    INVALID_STATE("Invalid State", "state-error"),
    
    /**
     * File Monitoring Error - File system monitoring and I/O related errors.
     * This includes file watch service failures, file access issues,
     * and problems with file system event detection.
     */
    FILE_MONITORING_ERROR("File Monitoring Error", "file-monitoring"),
    
    /**
     * Error Capture Failure - Problems with error capture and reporting mechanisms.
     * This covers failures in the error reporting system itself,
     * including snapshot generation and event serialization issues.
     */
    CAPTURE_FAILURE("Error Capture Failure", "capture-error"),
    
    /**
     * Unknown Error - Unclassified or unrecognized error types.
     * This is the default classification for errors that don't fit
     * into other categories and require manual analysis.
     */
    UNKNOWN("Unknown Error", "unknown");

    /**
     * Human-readable display name for the error classification
     */
    private final String displayName;
    
    /**
     * Short label for the error classification used in logging and metrics
     */
    private final String label;

    /**
     * Creates an error classification with the specified display name and label.
     * @param displayName the human-readable display name
     * @param label the short label for logging and metrics
     */
    ErrorClassification(final String displayName, final String label) {
        this.displayName = displayName;
        this.label = label;
    }

    /**
     * Gets the human-readable display name.
     * @return the display name for this error classification
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the short label for logging and metrics.
     * @return the label for this error classification
     */
    public String getLabel() {
        return label;
    }
}