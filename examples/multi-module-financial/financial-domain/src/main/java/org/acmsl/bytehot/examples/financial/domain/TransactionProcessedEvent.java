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
 * Filename: TransactionProcessedEvent.java
 *
 * Author: Claude Code
 *
 * Class name: TransactionProcessedEvent
 *
 * Responsibilities:
 *   - Represent successful transaction processing as domain response event
 *   - Carry processed transaction data for downstream systems
 *   - Enable hot-swappable success event structure evolution
 *
 * Collaborators:
 *   - Transaction: Aggregate that generates this event
 *   - TransactionRequestedEvent: Original event being responded to
 */
package org.acmsl.bytehot.examples.financial.domain;

import org.acmsl.commons.patterns.DomainEvent;
import org.acmsl.commons.patterns.DomainResponseEvent;

import java.time.LocalDateTime;

/**
 * Domain response event representing successful transaction processing.
 * @author Claude Code
 * @since 2025-07-04
 */
public class TransactionProcessedEvent implements DomainResponseEvent<TransactionRequestedEvent> {

    /**
     * Original transaction request event.
     */
    private final TransactionRequestedEvent originalEvent;

    /**
     * Processed transaction.
     */
    private final Transaction transaction;

    /**
     * Event timestamp.
     */
    private final LocalDateTime timestamp;

    /**
     * Creates a new TransactionProcessedEvent.
     * @param originalEvent The original transaction request event
     * @param transaction The processed transaction
     */
    public TransactionProcessedEvent(final TransactionRequestedEvent originalEvent,
                                    final Transaction transaction) {
        this.originalEvent = originalEvent;
        this.transaction = transaction;
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public TransactionRequestedEvent getOriginalEvent() {
        return originalEvent;
    }

    @Override
    public DomainEvent getPreviousEvent() {
        return originalEvent;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "TransactionProcessedEvent{" +
                "transactionId='" + transaction.getTransactionId() + '\'' +
                ", status=" + transaction.getStatus() +
                ", fees=" + transaction.getFees() +
                ", riskLevel=" + transaction.getRiskLevel() +
                ", timestamp=" + timestamp +
                '}';
    }
}