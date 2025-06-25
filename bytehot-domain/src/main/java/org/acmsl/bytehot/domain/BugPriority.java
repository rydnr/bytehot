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
 * Filename: BugPriority.java
 *
 * Author: Claude Code
 *
 * Enum name: BugPriority
 *
 * Responsibilities:
 *   - Define priority levels for bug tracking and issue management
 *   - Support prioritization in bug report generation and tracking systems
 */
package org.acmsl.bytehot.domain;

/**
 * Bug priority enumeration for issue tracking and prioritization.
 * Used by bug report generation and issue management systems to
 * prioritize fixes and allocate resources appropriately.
 * @author Claude Code
 * @since 2025-06-25
 */
public enum BugPriority {
    /**
     * Low priority - Minor issues that can be addressed in future releases.
     * These issues have minimal impact on functionality and don't affect
     * critical system operations or user experience significantly.
     */
    LOW,
    
    /**
     * Medium priority - Moderate issues that should be addressed in upcoming releases.
     * These issues have noticeable impact but don't prevent core functionality
     * from working and reasonable workarounds may exist.
     */
    MEDIUM,
    
    /**
     * High priority - Important issues that should be addressed soon.
     * These issues significantly impact functionality or user experience
     * and should be prioritized for the current or next release cycle.
     */
    HIGH,
    
    /**
     * Critical priority - Urgent issues requiring immediate attention.
     * These issues cause system failures, data loss, security vulnerabilities,
     * or completely block important functionality and must be fixed immediately.
     */
    CRITICAL
}