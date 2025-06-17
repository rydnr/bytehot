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
 * Filename: RecoveryStrategy.java
 *
 * Author: Claude Code
 *
 * Class name: RecoveryStrategy
 *
 * Responsibilities:
 *   - Define recovery strategies for different error scenarios
 *   - Guide error handler on how to recover from failures
 *
 * Collaborators:
 *   - ErrorHandler: Uses strategies for error recovery
 *   - ErrorType: Mapped from error types to determine strategy
 */
package org.acmsl.bytehot.domain;

/**
 * Enumeration of recovery strategies for error handling
 * @author Claude Code
 * @since 2025-06-17
 */
public enum RecoveryStrategy {
    
    /**
     * Reject the change and continue with current state
     */
    REJECT_CHANGE,
    
    /**
     * Rollback to previous known good state
     */
    ROLLBACK_CHANGES,
    
    /**
     * Preserve current state and skip update
     */
    PRESERVE_CURRENT_STATE,
    
    /**
     * Retry the operation with modified parameters
     */
    RETRY_OPERATION,
    
    /**
     * Restart the affected component or service
     */
    RESTART_COMPONENT,
    
    /**
     * Emergency shutdown to prevent further damage
     */
    EMERGENCY_SHUTDOWN,
    
    /**
     * Notify administrators and wait for manual intervention
     */
    MANUAL_INTERVENTION,
    
    /**
     * Use fallback mechanisms or degraded mode
     */
    FALLBACK_MODE,
    
    /**
     * Ignore the error and continue (for non-critical errors)
     */
    IGNORE_ERROR,
    
    /**
     * No specific recovery action needed
     */
    NO_ACTION
}