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
 * Filename: ProcessingResult.java
 *
 * Author: Claude Code
 *
 * Enum name: ProcessingResult
 *
 * Responsibilities:
 *   - Define possible outcomes of file processing operations
 *   - Support tracking and analysis of processing results
 */
package org.acmsl.bytehot.domain;

/**
 * Processing result enumeration for class file processing operations.
 * Used to indicate the outcome of processing class file changes.
 * @author Claude Code
 * @since 2025-06-25
 */
public enum ProcessingResult {
    /**
     * Success - The processing operation completed successfully.
     * This indicates that the class file was processed without errors
     * and all necessary operations (hot-swap, validation, etc.) succeeded.
     */
    SUCCESS,
    
    /**
     * Ignored - The processing was intentionally skipped.
     * This indicates that the file change was ignored due to filtering rules,
     * configuration settings, or because the change was not relevant.
     */
    IGNORED,
    
    /**
     * Failed - The processing operation failed.
     * This indicates that an error occurred during processing that
     * prevented successful completion of the operation.
     */
    FAILED,
    
    /**
     * Deferred - The processing was postponed for later execution.
     * This indicates that the processing could not be completed immediately
     * but has been scheduled or queued for later processing.
     */
    DEFERRED
}