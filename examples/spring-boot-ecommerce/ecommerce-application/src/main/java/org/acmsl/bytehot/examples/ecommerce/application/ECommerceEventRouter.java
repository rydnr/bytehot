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
 * Filename: ECommerceEventRouter.java
 *
 * Author: Claude Code
 *
 * Class name: ECommerceEventRouter
 *
 * Responsibilities:
 *   - Route incoming domain events to appropriate aggregates
 *   - Handle unknown event types gracefully
 *   - Support hot-swappable event routing logic
 *
 * Collaborators:
 *   - Product: Domain aggregate for product management
 *   - ProductCreationRequestedEvent: Supported domain event type
 *   - ECommerceApplication: Uses this router for event processing
 */
package org.acmsl.bytehot.examples.ecommerce.application;

import org.acmsl.bytehot.examples.ecommerce.domain.Product;
import org.acmsl.bytehot.examples.ecommerce.domain.ProductCreationRequestedEvent;
import org.acmsl.commons.patterns.DomainEvent;
import org.acmsl.commons.patterns.DomainResponseEvent;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Event router for e-commerce domain events.
 * Routes incoming events to appropriate domain handlers.
 * @author Claude Code
 * @since 2025-07-04
 */
public class ECommerceEventRouter {

    /**
     * Logger for event routing activities.
     */
    private static final Logger LOGGER = Logger.getLogger(ECommerceEventRouter.class.getName());

    /**
     * Routes a domain event to the appropriate handler.
     * This method can be hot-swapped to change routing logic.
     * @param event The domain event to route
     * @return List of resulting domain response events
     */
    public List<DomainResponseEvent<?>> route(@NotNull final DomainEvent event) {
        LOGGER.info("Routing event: " + event.getClass().getSimpleName());
        
        List<DomainResponseEvent<?>> results = new ArrayList<>();
        
        // Route to appropriate aggregate based on event type (can be hot-swapped)
        if (event instanceof ProductCreationRequestedEvent) {
            ProductCreationRequestedEvent productEvent = (ProductCreationRequestedEvent) event;
            List<DomainResponseEvent<ProductCreationRequestedEvent>> productResults = 
                Product.accept(productEvent);
            results.addAll(productResults);
        } else {
            // Handle unknown event types (can be hot-swapped)
            LOGGER.warning("Unknown event type: " + event.getClass().getSimpleName());
            results.add(createUnknownEventResponse(event));
        }
        
        LOGGER.info("Routing completed. Generated " + results.size() + " response events.");
        return results;
    }

    /**
     * Creates a response for unknown event types.
     * This method can be hot-swapped to change unknown event handling.
     * @param event The unknown event
     * @return Domain response event indicating unknown type
     */
    protected DomainResponseEvent<?> createUnknownEventResponse(@NotNull final DomainEvent event) {
        return new UnknownEventResponse(event);
    }

    /**
     * Checks if an event type is supported.
     * This method can be hot-swapped to change supported event types.
     * @param eventClass The event class to check
     * @return true if supported, false otherwise
     */
    public boolean isEventSupported(@NotNull final Class<? extends DomainEvent> eventClass) {
        return ProductCreationRequestedEvent.class.isAssignableFrom(eventClass);
    }

    /**
     * Gets the list of supported event types.
     * This method can be hot-swapped to change supported event types.
     * @return List of supported event classes
     */
    public List<Class<? extends DomainEvent>> getSupportedEventTypes() {
        List<Class<? extends DomainEvent>> supportedTypes = new ArrayList<>();
        supportedTypes.add(ProductCreationRequestedEvent.class);
        return supportedTypes;
    }

    /**
     * Routes product-related events specifically.
     * This method can be hot-swapped to change product event handling.
     * @param event The product-related domain event
     * @return List of resulting domain response events
     */
    public List<DomainResponseEvent<?>> routeProductEvent(@NotNull final DomainEvent event) {
        List<DomainResponseEvent<?>> results = new ArrayList<>();
        
        if (event instanceof ProductCreationRequestedEvent) {
            ProductCreationRequestedEvent creationEvent = (ProductCreationRequestedEvent) event;
            List<DomainResponseEvent<ProductCreationRequestedEvent>> productResults = 
                Product.accept(creationEvent);
            results.addAll(productResults);
        }
        
        return results;
    }
}