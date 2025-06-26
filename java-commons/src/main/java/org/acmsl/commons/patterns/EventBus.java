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
 * Filename: EventBus.java
 *
 * Author: Claude Code
 *
 * Interface name: EventBus
 *
 * Responsibilities:
 *   - Provide abstraction for domain event publishing and subscription
 *   - Enable decoupled communication between domain components
 *   - Support synchronous and asynchronous event delivery
 *   - Integrate with existing DomainEvent and DomainResponseEvent patterns
 *
 * Collaborators:
 *   - DomainEvent: Events published through the bus
 *   - EventHandler: Subscribers to specific event types
 *   - Application: Uses EventBus for orchestrating domain events
 */
package org.acmsl.commons.patterns;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Generic EventBus abstraction for domain event publishing and subscription.
 * Provides decoupled communication mechanism for domain events with support
 * for both synchronous and asynchronous processing.
 * 
 * @author Claude Code
 * @since 2025-06-26
 */
public interface EventBus {

    /**
     * Publishes a domain event synchronously to all registered handlers.
     * 
     * @param event the domain event to publish
     * @throws EventBusException if publication fails
     */
    void publish(final DomainEvent event) throws EventBusException;

    /**
     * Publishes a domain event asynchronously to all registered handlers.
     * 
     * @param event the domain event to publish
     * @return CompletableFuture that completes when all handlers have processed the event
     */
    CompletableFuture<Void> publishAsync(final DomainEvent event);

    /**
     * Publishes multiple domain events as a batch.
     * 
     * @param events the domain events to publish
     * @throws EventBusException if batch publication fails
     */
    void publishBatch(final List<DomainEvent> events) throws EventBusException;

    /**
     * Registers an event handler for a specific event type.
     * 
     * @param eventType the type of event to handle
     * @param handler the handler to register
     * @param <T> the specific DomainEvent type
     */
    <T extends DomainEvent> void subscribe(final Class<T> eventType, final EventHandler<T> handler);

    /**
     * Unregisters an event handler for a specific event type.
     * 
     * @param eventType the type of event to stop handling
     * @param handler the handler to unregister
     * @param <T> the specific DomainEvent type
     */
    <T extends DomainEvent> void unsubscribe(final Class<T> eventType, final EventHandler<T> handler);

    /**
     * Checks if there are any handlers registered for the given event type.
     * 
     * @param eventType the event type to check
     * @return true if handlers are registered, false otherwise
     */
    boolean hasHandlers(final Class<? extends DomainEvent> eventType);

    /**
     * Gets the number of registered handlers for a specific event type.
     * 
     * @param eventType the event type to check
     * @return the number of registered handlers
     */
    int getHandlerCount(final Class<? extends DomainEvent> eventType);

    /**
     * Clears all registered handlers.
     */
    void clear();

    /**
     * Gets the total number of events published through this bus.
     * 
     * @return the total event count
     */
    long getPublishedEventCount();

    /**
     * Checks if the EventBus is currently healthy and able to process events.
     * 
     * @return true if healthy, false otherwise
     */
    boolean isHealthy();

    /**
     * Exception thrown when EventBus operations fail.
     */
    class EventBusException extends RuntimeException {
        public EventBusException(final String message) {
            super(message);
        }

        public EventBusException(final String message, final Throwable cause) {
            super(message, cause);
        }
    }
}