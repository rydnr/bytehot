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
 * Filename: OperationType.java
 *
 * Author: Claude Code
 *
 * Enum name: OperationType
 *
 * Responsibilities:
 *   - Define types of event store operations that can fail
 *   - Support error categorization and recovery strategies
 */
package org.acmsl.bytehot.domain;

/**
 * Event store operation type enumeration for error categorization.
 * Used to classify the type of event store operation that failed.
 * @author Claude Code
 * @since 2025-06-25
 */
public enum OperationType {
    /**
     * Save Operation - Persisting events to the event store.
     * This operation type indicates that the system was attempting
     * to save or persist events to the event store but encountered an error.
     */
    SAVE,
    
    /**
     * Retrieve Operation - Reading events from the event store.
     * This operation type indicates that the system was attempting
     * to retrieve or read events from the event store but failed.
     */
    RETRIEVE,
    
    /**
     * Count Operation - Counting events in the event store.
     * This operation type indicates that the system was attempting
     * to count events in the event store but encountered an error.
     */
    COUNT,
    
    /**
     * Health Check Operation - Verifying event store health.
     * This operation type indicates that the system was attempting
     * to perform a health check on the event store but failed.
     */
    HEALTH_CHECK,
    
    /**
     * Initialization Operation - Initializing the event store.
     * This operation type indicates that the system was attempting
     * to initialize the event store but encountered an error.
     */
    INITIALIZATION
}