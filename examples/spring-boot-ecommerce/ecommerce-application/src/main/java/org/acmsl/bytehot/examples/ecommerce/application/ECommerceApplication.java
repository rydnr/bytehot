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
 * Filename: ECommerceApplication.java
 *
 * Author: Claude Code
 *
 * Class name: ECommerceApplication
 *
 * Responsibilities:
 *   - Route domain events to appropriate aggregates
 *   - Coordinate e-commerce business workflows
 *   - Manage infrastructure adapters and ports
 *
 * Collaborators:
 *   - Product: Domain aggregate for product management
 *   - ProductCreationRequestedEvent: Incoming domain event
 *   - ECommerceEventRouter: Routes events to domain handlers
 */
package org.acmsl.bytehot.examples.ecommerce.application;

import org.acmsl.bytehot.examples.ecommerce.domain.Product;
import org.acmsl.bytehot.examples.ecommerce.domain.ProductCreationRequestedEvent;
import org.acmsl.commons.patterns.Application;
import org.acmsl.commons.patterns.DomainEvent;
import org.acmsl.commons.patterns.DomainResponseEvent;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Main application class for the e-commerce system.
 * Coordinates event routing and product management workflows.
 * @author Claude Code
 * @since 2025-07-04
 */
public class ECommerceApplication implements Application {

    /**
     * Event router for domain events.
     */
    @NotNull
    private final ECommerceEventRouter eventRouter;

    /**
     * Creates a new ECommerceApplication instance.
     * @param eventRouter The event router for domain events
     */
    public ECommerceApplication(final ECommerceEventRouter eventRouter) {
        this.eventRouter = eventRouter;
    }

    /**
     * Accepts and processes a domain event.
     * @param event The domain event to process
     * @return List of resulting domain response events
     */
    @Override
    public List<DomainResponseEvent<?>> accept(final DomainEvent event) {
        return eventRouter.route(event);
    }

    /**
     * Processes a domain event asynchronously.
     * @param event The domain event to process
     * @return CompletableFuture with resulting domain response events
     */
    public CompletableFuture<List<DomainResponseEvent<?>>> acceptAsync(final DomainEvent event) {
        return CompletableFuture.supplyAsync(() -> accept(event));
    }

    /**
     * Processes a product creation request event specifically.
     * @param event The product creation request event
     * @return List of resulting domain response events
     */
    public List<DomainResponseEvent<ProductCreationRequestedEvent>> processProductCreationRequest(
            final ProductCreationRequestedEvent event) {
        return Product.accept(event);
    }

    /**
     * Processes a product creation request event asynchronously.
     * @param event The product creation request event
     * @return CompletableFuture with resulting domain response events
     */
    public CompletableFuture<List<DomainResponseEvent<ProductCreationRequestedEvent>>> processProductCreationRequestAsync(
            final ProductCreationRequestedEvent event) {
        return CompletableFuture.supplyAsync(() -> processProductCreationRequest(event));
    }

    /**
     * Gets the event router.
     * @return The event router instance
     */
    public ECommerceEventRouter getEventRouter() {
        return eventRouter;
    }
}