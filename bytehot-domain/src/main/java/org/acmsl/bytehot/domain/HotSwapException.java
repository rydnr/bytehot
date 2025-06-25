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
 * Filename: HotSwapException.java
 *
 * Author: Claude Code
 *
 * Class name: HotSwapException
 *
 * Responsibilities:
 *   - Represent hot-swap operation failures with embedded domain event
 *
 * Collaborators:
 *   - ClassRedefinitionFailed: Domain event for redefinition failures
 */
package org.acmsl.bytehot.domain;

import org.acmsl.bytehot.domain.events.ClassRedefinitionFailed;

import lombok.Getter;

/**
 * Exception thrown when hot-swap operations fail
 * @author Claude Code
 * @since 2025-06-17
 */
public class HotSwapException extends Exception
    implements ErrorClassifiable {

    /**
     * Serial version UID for serialization
     */
    private static final long serialVersionUID = 1L;

    /**
     * The failure event containing details about why hot-swap failed
     */
    @Getter
    private final ClassRedefinitionFailed failureEvent;

    /**
     * Creates a new HotSwapException
     * @param failureEvent the failure event with failure details
     */
    public HotSwapException(final ClassRedefinitionFailed failureEvent) {
        super("Hot-swap operation failed: " + failureEvent.getFailureReason());
        this.failureEvent = failureEvent;
    }

    /**
     * Creates a new HotSwapException with a custom message
     * @param message the exception message
     * @param failureEvent the failure event with failure details
     */
    public HotSwapException(final String message, final ClassRedefinitionFailed failureEvent) {
        super(message);
        this.failureEvent = failureEvent;
    }

    /**
     * Creates a new HotSwapException with a cause
     * @param failureEvent the failure event with failure details
     * @param cause the original exception from JVM
     */
    public HotSwapException(final ClassRedefinitionFailed failureEvent, final Throwable cause) {
        super("Hot-swap operation failed: " + failureEvent.getFailureReason(), cause);
        this.failureEvent = failureEvent;
    }

    /**
     * Accepts an error classifier and returns the appropriate error type.
     * @param classifier the error classifier visitor
     * @return the error type for hot-swap exceptions
     */
    @Override
    public ErrorType acceptClassifier(final ErrorClassifier classifier) {
        return classifier.classifyHotSwapException(this);
    }

    /**
     * Accepts an error severity assessor and returns the appropriate severity.
     * @param assessor the error severity assessor visitor
     * @return the error severity for hot-swap exceptions
     */
    @Override
    public ErrorSeverity acceptSeverityAssessor(final ErrorSeverityAssessor assessor) {
        return assessor.assessHotSwapException(this);
    }
}