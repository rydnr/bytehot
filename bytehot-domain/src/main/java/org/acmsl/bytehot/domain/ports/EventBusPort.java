/*
                        ByteHot

    Copyright (C) 2025-today  rydnr@acm-sl.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
/*******************************************************************************
 *
 * Filename: EventBusPort.java
 *
 * Author: Claude
 *
 * Class name: EventBusPort
 *
 * Responsibilities:
 *   - Define the contract for event bus operations in the domain
 *
 * Collaborators:
 *   - DomainEvent: The events to be published
 *   - EventHandler: The handlers for events
 */
package org.acmsl.bytehot.domain.ports;

import org.acmsl.commons.patterns.DomainEvent;
import org.acmsl.commons.patterns.EventBus;
import org.acmsl.commons.patterns.EventHandler;
import org.acmsl.commons.patterns.Port;

import java.util.concurrent.CompletableFuture;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Port interface for event bus operations in the domain layer.
 * @author Claude
 * @since 2025-01-21
 */
public interface EventBusPort
    extends Port, EventBus {
    // Inherits all methods from EventBus interface
    // This interface exists to combine Port and EventBus for hexagonal architecture
}