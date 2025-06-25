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
 * Filename: EmissionTarget.java
 *
 * Author: Claude Code
 *
 * Enum name: EmissionTarget
 *
 * Responsibilities:
 *   - Define targets for event emission (console, file, both)
 *   - Support configuration of event output destinations
 */
package org.acmsl.bytehot.infrastructure;

/**
 * Event emission target enumeration for controlling where events are output.
 * Used by event emitters to determine the destination of emitted events.
 * @author Claude Code
 * @since 2025-06-25
 */
public enum EmissionTarget {
    /**
     * Console Target - Emit events to console/standard output.
     * Events are written to the console using System.out.println(),
     * providing immediate visibility during development and debugging.
     */
    CONSOLE,
    
    /**
     * File Target - Emit events to a log file.
     * Events are written to a persistent log file for later analysis,
     * auditing, and long-term storage of event history.
     */
    FILE,
    
    /**
     * Both Targets - Emit events to both console and file.
     * Events are written to both console and file simultaneously,
     * providing immediate visibility and persistent storage.
     */
    BOTH
}