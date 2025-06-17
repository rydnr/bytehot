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
 * Filename: InstanceUpdateException.java
 *
 * Author: Claude Code
 *
 * Class name: InstanceUpdateException
 *
 * Responsibilities:
 *   - Represent errors during instance update operations
 *   - Provide context about what failed during instance updates
 *
 * Collaborators:
 *   - RuntimeException: Parent exception class
 *   - InstanceUpdater: Throws this exception when updates fail
 */
package org.acmsl.bytehot.domain;

/**
 * Exception thrown when instance update operations fail
 * @author Claude Code
 * @since 2025-06-17
 */
public class InstanceUpdateException extends RuntimeException {

    /**
     * Creates a new InstanceUpdateException with a message
     * @param message the error message
     */
    public InstanceUpdateException(final String message) {
        super(message);
    }

    /**
     * Creates a new InstanceUpdateException with a message and cause
     * @param message the error message
     * @param cause the underlying cause
     */
    public InstanceUpdateException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new InstanceUpdateException with a cause
     * @param cause the underlying cause
     */
    public InstanceUpdateException(final Throwable cause) {
        super(cause);
    }
}