/*
                        Commons

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
 * Filename: EventHandler.java
 *
 * Author: Claude Code
 *
 * Interface name: EventHandler
 *
 * Responsibilities:
 *   - Define contract for handling domain events from EventBus
 *   - Support both synchronous and asynchronous event processing
 *   - Provide error handling and event processing metadata
 *
 * Collaborators:
 *   - DomainEvent: Events processed by this handler
 *   - EventBus: Registers handlers for event processing
 */
package org.acmsl.commons.patterns;

/**
 * Generic EventHandler interface for processing domain events from an EventBus.
 * Provides contract for handling specific types of domain events with proper
 * error handling and processing metadata.
 * 
 * @param <T> the specific type of DomainEvent this handler processes
 * @author Claude Code
 * @since 2025-06-26
 */
@FunctionalInterface
public interface EventHandler<T extends DomainEvent> {

    /**
     * Handles the given domain event.
     * 
     * @param event the domain event to process
     * @throws EventHandlerException if event processing fails
     */
    void handle(final T event) throws EventHandlerException;

    /**
     * Gets the priority of this handler for ordering when multiple handlers
     * are registered for the same event type. Lower numbers indicate higher priority.
     * 
     * @return the handler priority (default: 0)
     */
    default int getPriority() {
        return 0;
    }

    /**
     * Indicates whether this handler can process events asynchronously.
     * 
     * @return true if async processing is supported (default: true)
     */
    default boolean supportsAsyncProcessing() {
        return true;
    }

    /**
     * Gets a descriptive name for this handler, useful for logging and debugging.
     * 
     * @return the handler name (default: class simple name)
     */
    default String getHandlerName() {
        return this.getClass().getSimpleName();
    }

    /**
     * Exception thrown when event handling fails.
     */
    class EventHandlerException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public EventHandlerException(final String message) {
            super(message);
        }

        public EventHandlerException(final String message, final Throwable cause) {
            super(message, cause);
        }
    }
}