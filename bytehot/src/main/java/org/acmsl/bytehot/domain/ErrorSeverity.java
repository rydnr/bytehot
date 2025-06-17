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
 * Filename: ErrorSeverity.java
 *
 * Author: Claude Code
 *
 * Class name: ErrorSeverity
 *
 * Responsibilities:
 *   - Define severity levels for errors in ByteHot operations
 *   - Support error prioritization and alerting decisions
 *
 * Collaborators:
 *   - ErrorHandler: Uses severity for error processing
 *   - ErrorResult: Contains severity information
 */
package org.acmsl.bytehot.domain;

/**
 * Enumeration of error severity levels
 * @author Claude Code
 * @since 2025-06-17
 */
public enum ErrorSeverity {
    
    /**
     * Informational messages (not really errors)
     */
    INFO,
    
    /**
     * Warning level - potential issues that don't prevent operation
     */
    WARNING,
    
    /**
     * Error level - operation failed but system can continue
     */
    ERROR,
    
    /**
     * Critical level - system stability is at risk
     */
    CRITICAL,
    
    /**
     * Fatal level - immediate shutdown or intervention required
     */
    FATAL
}