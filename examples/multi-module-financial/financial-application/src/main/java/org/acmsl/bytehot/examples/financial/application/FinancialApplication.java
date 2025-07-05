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
 * Filename: FinancialApplication.java
 *
 * Author: Claude Code
 *
 * Class name: FinancialApplication
 *
 * Responsibilities:
 *   - Route domain events to appropriate aggregates
 *   - Coordinate transaction processing workflow
 *   - Manage infrastructure adapters and ports
 *
 * Collaborators:
 *   - Transaction: Domain aggregate for transaction processing
 *   - TransactionRequestedEvent: Incoming domain event
 *   - FinancialEventRouter: Routes events to domain handlers
 */
package org.acmsl.bytehot.examples.financial.application;

import org.acmsl.bytehot.examples.financial.domain.Transaction;
import org.acmsl.bytehot.examples.financial.domain.TransactionRequestedEvent;
import org.acmsl.commons.patterns.Application;
import org.acmsl.commons.patterns.DomainEvent;
import org.acmsl.commons.patterns.DomainResponseEvent;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Main application class for the financial transaction processing system.
 * Coordinates event routing and transaction processing workflows.
 * @author Claude Code
 * @since 2025-07-04
 */
public class FinancialApplication implements Application {

    /**
     * Event router for domain events.
     */
    @NotNull
    private final FinancialEventRouter eventRouter;

    /**
     * Creates a new FinancialApplication instance.
     * @param eventRouter The event router for domain events
     */
    public FinancialApplication(final FinancialEventRouter eventRouter) {
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
     * Processes a transaction request event specifically.
     * @param event The transaction request event
     * @return List of resulting domain response events
     */
    public List<DomainResponseEvent<TransactionRequestedEvent>> processTransactionRequest(
            final TransactionRequestedEvent event) {
        return Transaction.accept(event);
    }

    /**
     * Processes a transaction request event asynchronously.
     * @param event The transaction request event
     * @return CompletableFuture with resulting domain response events
     */
    public CompletableFuture<List<DomainResponseEvent<TransactionRequestedEvent>>> processTransactionRequestAsync(
            final TransactionRequestedEvent event) {
        return CompletableFuture.supplyAsync(() -> processTransactionRequest(event));
    }

    /**
     * Gets the event router.
     * @return The event router instance
     */
    public FinancialEventRouter getEventRouter() {
        return eventRouter;
    }
}