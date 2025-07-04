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
 *   - Define severity levels for error classification
 *   - Support prioritization of error handling and reporting
 *   - Enable severity-based alerting and escalation
 *
 * Collaborators:
 *   - ErrorClassification: Uses this enum for severity assessment
 *   - ErrorPattern: References this enum for pattern definition
 */
package org.acmsl.bytehot.infrastructure.production;

/**
 * Enumeration of error severity levels.
 * @author Claude Code
 * @since 2025-07-04
 */
public enum ErrorSeverity {
    
    /**
     * Low severity errors that don't significantly impact operation.
     */
    LOW,
    
    /**
     * Medium severity errors that may impact some functionality.
     */
    MEDIUM,
    
    /**
     * High severity errors that significantly impact operation.
     */
    HIGH,
    
    /**
     * Critical errors that may cause system failure or data loss.
     */
    CRITICAL
}