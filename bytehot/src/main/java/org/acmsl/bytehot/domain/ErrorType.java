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
 *   - Classify different types of errors that can occur in ByteHot
 *   - Provide error categorization for recovery strategy selection
 *
 * Collaborators:
 *   - ErrorHandler: Uses this for error classification
 *   - RecoveryStrategy: Mapped to error types for recovery selection
 */
package org.acmsl.bytehot.domain;

/**
 * Enumeration of error types that can occur in ByteHot operations
 * @author Claude Code
 * @since 2025-06-17
 */
public enum ErrorType {
    
    /**
     * Bytecode validation errors (invalid class files, signature mismatches)
     */
    VALIDATION_ERROR,
    
    /**
     * Class redefinition failures (JVM rejection, unsupported changes)
     */
    REDEFINITION_FAILURE,
    
    /**
     * Instance update errors (state preservation failures, access issues)
     */
    INSTANCE_UPDATE_ERROR,
    
    /**
     * File system monitoring errors (path not found, permission denied)
     */
    FILE_SYSTEM_ERROR,
    
    /**
     * Critical system errors (out of memory, JVM crashes)
     */
    CRITICAL_SYSTEM_ERROR,
    
    /**
     * Configuration errors (invalid settings, missing parameters)
     */
    CONFIGURATION_ERROR,
    
    /**
     * Network or communication errors (remote service failures)
     */
    COMMUNICATION_ERROR,
    
    /**
     * Security-related errors (permission denied, access violations)
     */
    SECURITY_ERROR,
    
    /**
     * Resource exhaustion errors (disk space, memory limits)
     */
    RESOURCE_EXHAUSTION,
    
    /**
     * Unknown or unclassified errors
     */
    UNKNOWN_ERROR
}