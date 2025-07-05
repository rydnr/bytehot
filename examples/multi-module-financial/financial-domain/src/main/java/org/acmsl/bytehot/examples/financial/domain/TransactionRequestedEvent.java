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
 * Filename: TransactionRequestedEvent.java
 *
 * Author: Claude Code
 *
 * Class name: TransactionRequestedEvent
 *
 * Responsibilities:
 *   - Represent incoming transaction request as domain event
 *   - Carry all necessary transaction data for processing
 *   - Enable hot-swappable event structure evolution
 *
 * Collaborators:
 *   - Transaction: Aggregate that processes this event
 */
package org.acmsl.bytehot.examples.financial.domain;

import org.acmsl.commons.patterns.DomainEvent;

import java.time.LocalDateTime;

/**
 * Domain event representing a transaction processing request.
 * @author Claude Code
 * @since 2025-07-04
 */
public class TransactionRequestedEvent implements DomainEvent {

    /**
     * Source account identifier.
     */
    private final String fromAccountId;

    /**
     * Destination account identifier.
     */
    private final String toAccountId;

    /**
     * Transaction amount.
     */
    private final Money amount;

    /**
     * Transaction type.
     */
    private final TransactionType type;

    /**
     * Transaction reference.
     */
    private final String reference;

    /**
     * Event timestamp.
     */
    private final LocalDateTime timestamp;

    /**
     * Previous event in the chain.
     */
    private final DomainEvent previousEvent;

    /**
     * Creates a new TransactionRequestedEvent.
     * @param fromAccountId Source account
     * @param toAccountId Destination account
     * @param amount Transaction amount
     * @param type Transaction type
     * @param reference Transaction reference
     * @param previousEvent Previous event in chain
     */
    public TransactionRequestedEvent(final String fromAccountId,
                                    final String toAccountId,
                                    final Money amount,
                                    final TransactionType type,
                                    final String reference,
                                    final DomainEvent previousEvent) {
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.type = type;
        this.reference = reference;
        this.timestamp = LocalDateTime.now();
        this.previousEvent = previousEvent;
    }

    /**
     * Creates a new TransactionRequestedEvent without previous event.
     * @param fromAccountId Source account
     * @param toAccountId Destination account
     * @param amount Transaction amount
     * @param type Transaction type
     * @param reference Transaction reference
     */
    public TransactionRequestedEvent(final String fromAccountId,
                                    final String toAccountId,
                                    final Money amount,
                                    final TransactionType type,
                                    final String reference) {
        this(fromAccountId, toAccountId, amount, type, reference, null);
    }

    @Override
    public DomainEvent getPreviousEvent() {
        return previousEvent;
    }

    public String getFromAccountId() {
        return fromAccountId;
    }

    public String getToAccountId() {
        return toAccountId;
    }

    public Money getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public String getReference() {
        return reference;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "TransactionRequestedEvent{" +
                "fromAccountId='" + fromAccountId + '\'' +
                ", toAccountId='" + toAccountId + '\'' +
                ", amount=" + amount +
                ", type=" + type +
                ", reference='" + reference + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}