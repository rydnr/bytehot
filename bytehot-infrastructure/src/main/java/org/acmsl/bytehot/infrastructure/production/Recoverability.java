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
 * Filename: Recoverability.java
 *
 * Author: Claude Code
 *
 * Class name: Recoverability
 *
 * Responsibilities:
 *   - Define recoverability levels for errors
 *   - Guide automatic recovery strategy selection
 *   - Support retry and fallback decision making
 *
 * Collaborators:
 *   - ErrorClassification: Uses this enum for recovery assessment
 *   - RecoveryManager: References this enum for strategy selection
 */
package org.acmsl.bytehot.infrastructure.production;

/**
 * Enumeration of error recoverability levels.
 * @author Claude Code
 * @since 2025-07-04
 */
public enum Recoverability {
    
    /**
     * Errors that are likely to resolve themselves or can be retried successfully.
     */
    TRANSIENT,
    
    /**
     * Errors that are permanent and cannot be recovered through retry.
     */
    PERMANENT,
    
    /**
     * Errors where recoverability is unknown or uncertain.
     */
    UNKNOWN
}