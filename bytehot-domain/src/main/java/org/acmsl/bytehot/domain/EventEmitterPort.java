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
 * Filename: EventEmitterPort.java
 *
 * Author: Claude Code
 *
 * Class name: EventEmitterPort
 *
 * Responsibilities:
 *   - Define interface for emitting domain events to external systems
 *   - Abstract event publishing implementation from domain logic
 *   - Enable different event emission strategies (logging, messaging, etc.)
 *
 * Collaborators:
 *   - ByteHotApplication: Uses this port to emit events
 *   - EventEmitterAdapter: Infrastructure implementation
 */
package org.acmsl.bytehot.domain;

import org.acmsl.commons.patterns.DomainResponseEvent;
import org.acmsl.commons.patterns.Port;

import java.util.List;

/**
 * Port interface for emitting domain events to external systems
 * @author Claude Code
 * @since 2025-06-17
 */
public interface EventEmitterPort
    extends Port {

    /**
     * Emits a single domain event
     * @param event the domain event to emit
     * @throws Exception if emission fails
     */
    void emit(final DomainResponseEvent<?> event) throws Exception;

    /**
     * Emits multiple domain events
     * @param events the domain events to emit
     * @throws Exception if emission fails
     */
    void emit(final List<DomainResponseEvent<?>> events) throws Exception;

    /**
     * Checks if event emission is available
     * @return true if events can be emitted
     */
    boolean isEmissionAvailable();

    /**
     * Returns the emission target description
     * @return human-readable description of where events are emitted
     */
    String getEmissionTarget();

    /**
     * Returns the number of events emitted
     * @return count of emitted events
     */
    long getEmittedEventCount();
}