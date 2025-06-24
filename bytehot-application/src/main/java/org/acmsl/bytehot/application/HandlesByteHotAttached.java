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
 * Filename: HandlesByteHotAttached.java
 *
 * Author: rydnr
 *
 * Class name: HandlesByteHotAttached
 *
 * Responsibilities: Define the methods to implement to accept a ByteHotAttachRequested.
 *
 * Collaborators:
 *   - org.acmsl.bytehot.domain.events.ByteHotAttachRequested
 *   - org.acmsl.commons.patterns.DomainEvent
 *   - org.acmsl.commons.patterns.DomainResponseEvent
 */
package org.acmsl.bytehot.application;

import org.acmsl.bytehot.domain.events.ByteHotAttachRequested;

import org.acmsl.commons.patterns.Application;
import org.acmsl.commons.patterns.DomainEvent;
import org.acmsl.commons.patterns.DomainResponseEvent;

import java.util.List;

/**
 * Defines the methods to implement to accept a ByteHotAttachRequested.
 * This interface extends the generic Application interface and provides
 * a specific method for handling ByteHotAttachRequested events.
 * @author <a href="mailto:rydnr@acm-sl.org">rydnr</a>
 * @since 2025-06-07
 */
public interface HandlesByteHotAttached
    extends Application {

    /**
     * Handles a ByteHotAttachRequested event specifically.
     * @param event the ByteHotAttachRequested event to process
     * @return a list of response events
     */
    List<DomainResponseEvent<ByteHotAttachRequested>> handleByteHotAttachRequested(final ByteHotAttachRequested event);

    /**
     * Default implementation that dispatches to the specific handler.
     * @param event the domain event to process
     * @return a list of response events
     */
    @Override
    default List<? extends DomainResponseEvent<?>> accept(final DomainEvent event) {
        if (event instanceof ByteHotAttachRequested attachEvent) {
            return handleByteHotAttachRequested(attachEvent);
        }
        throw new UnsupportedOperationException(
            "HandlesByteHotAttached does not support events of type: " + event.getClass().getSimpleName());
    }
}
