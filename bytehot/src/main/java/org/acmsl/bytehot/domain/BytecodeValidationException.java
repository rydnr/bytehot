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
 * Filename: BytecodeValidationException.java
 *
 * Author: Claude Code
 *
 * Class name: BytecodeValidationException
 *
 * Responsibilities:
 *   - Represent bytecode validation failures with associated rejection event
 *
 * Collaborators:
 *   - BytecodeRejected: Domain event for rejected bytecode
 */
package org.acmsl.bytehot.domain;

import org.acmsl.bytehot.domain.events.BytecodeRejected;

import lombok.Getter;

/**
 * Exception thrown when bytecode validation fails for hot-swap compatibility
 * @author Claude Code
 * @since 2025-06-16
 */
public class BytecodeValidationException extends Exception {

    /**
     * Serial version UID for serialization
     */
    private static final long serialVersionUID = 1L;

    /**
     * The rejection event containing details about why validation failed
     * @return the rejection event
     */
    @Getter
    private final BytecodeRejected rejectionEvent;

    /**
     * Creates a new BytecodeValidationException
     * @param rejectionEvent the rejection event with failure details
     */
    public BytecodeValidationException(final BytecodeRejected rejectionEvent) {
        super("Bytecode validation failed: " + rejectionEvent.getRejectionReason());
        this.rejectionEvent = rejectionEvent;
    }

    /**
     * Creates a new BytecodeValidationException with a custom message
     * @param message the exception message
     * @param rejectionEvent the rejection event with failure details
     */
    public BytecodeValidationException(final String message, final BytecodeRejected rejectionEvent) {
        super(message);
        this.rejectionEvent = rejectionEvent;
    }
}