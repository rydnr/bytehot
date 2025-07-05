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
 * Filename: UnknownEventResponse.java
 *
 * Author: Claude Code
 *
 * Class name: UnknownEventResponse
 *
 * Responsibilities:
 *   - Represent response to unknown or unsupported domain events
 *   - Carry information about the original unknown event
 *   - Enable hot-swappable unknown event handling
 *
 * Collaborators:
 *   - FinancialEventRouter: Creates instances of this response
 *   - DomainEvent: Original unknown event being responded to
 */
package org.acmsl.bytehot.examples.financial.application;

import org.acmsl.commons.patterns.DomainEvent;
import org.acmsl.commons.patterns.DomainResponseEvent;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Domain response event for unknown or unsupported events.
 * @author Claude Code
 * @since 2025-07-04
 */
public class UnknownEventResponse implements DomainResponseEvent<DomainEvent> {

    /**
     * Original unknown event.
     */
    @NotNull
    private final DomainEvent originalEvent;

    /**
     * Response timestamp.
     */
    @NotNull
    private final LocalDateTime timestamp;

    /**
     * Error message.
     */
    @NotNull
    private final String errorMessage;

    /**
     * Creates a new UnknownEventResponse.
     * @param originalEvent The original unknown event
     */
    public UnknownEventResponse(@NotNull final DomainEvent originalEvent) {
        this.originalEvent = originalEvent;
        this.timestamp = LocalDateTime.now();
        this.errorMessage = "Unknown event type: " + originalEvent.getClass().getSimpleName();
    }

    @Override
    public DomainEvent getOriginalEvent() {
        return originalEvent;
    }

    @Override
    public DomainEvent getPreviousEvent() {
        return originalEvent;
    }

    /**
     * Gets the error message.
     * @return The error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Gets the response timestamp.
     * @return The timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "UnknownEventResponse{" +
                "eventType='" + originalEvent.getClass().getSimpleName() + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}