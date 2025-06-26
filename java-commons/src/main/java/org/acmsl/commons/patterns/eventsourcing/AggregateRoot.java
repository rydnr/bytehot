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
 * Filename: AggregateRoot.java
 *
 * Author: Claude Code
 *
 * Interface name: AggregateRoot
 *
 * Responsibilities:
 *   - Define contract for EventSourcing aggregate roots
 *   - Provide methods for event application and state reconstruction
 *   - Support aggregate versioning and identity management
 *   - Enable proper EventSourcing lifecycle management
 *
 * Collaborators:
 *   - VersionedDomainEvent: Events applied to reconstruct aggregate state
 *   - AggregateRepository: Repository that manages aggregate persistence
 *   - EventStore: Underlying event persistence mechanism
 */
package org.acmsl.commons.patterns.eventsourcing;

import java.util.List;

/**
 * Interface defining the contract for EventSourcing aggregate roots.
 * Aggregates implement this interface to support event-driven state
 * reconstruction and proper EventSourcing lifecycle management.
 * 
 * @author Claude Code
 * @since 2025-06-26
 */
public interface AggregateRoot {

    /**
     * Gets the unique identifier for this aggregate.
     * 
     * @return the aggregate identifier
     */
    String getAggregateId();

    /**
     * Gets the aggregate type name for EventSourcing persistence.
     * 
     * @return the aggregate type name
     */
    String getAggregateType();

    /**
     * Gets the current version of this aggregate.
     * The version is incremented each time an event is applied.
     * 
     * @return the current aggregate version
     */
    long getVersion();

    /**
     * Gets the list of uncommitted events that have been applied
     * to this aggregate since the last save.
     * 
     * @return list of uncommitted events
     */
    List<VersionedDomainEvent> getUncommittedEvents();

    /**
     * Marks all currently uncommitted events as committed.
     * This is typically called after successful persistence to EventStore.
     */
    void markEventsAsCommitted();

    /**
     * Applies a domain event to this aggregate to reconstruct its state.
     * This method is used during aggregate reconstruction from EventStore.
     * 
     * @param event the event to apply
     * @throws AggregateException if event application fails
     */
    void applyEvent(final VersionedDomainEvent event) throws AggregateException;

    /**
     * Applies a new domain event to this aggregate and adds it to
     * the uncommitted events list for later persistence.
     * 
     * @param event the new event to apply and track
     * @throws AggregateException if event application fails
     */
    void applyAndTrackEvent(final VersionedDomainEvent event) throws AggregateException;

    /**
     * Checks if this aggregate has any uncommitted events.
     * 
     * @return true if there are uncommitted events, false otherwise
     */
    default boolean hasUncommittedEvents() {
        final List<VersionedDomainEvent> uncommittedEvents = getUncommittedEvents();
        return uncommittedEvents != null && !uncommittedEvents.isEmpty();
    }

    /**
     * Gets the total number of events that have been applied to this aggregate.
     * 
     * @return the total event count
     */
    default long getTotalEventCount() {
        return getVersion();
    }

    /**
     * Validates the aggregate's current state for consistency.
     * This method can be overridden to provide aggregate-specific validation.
     * 
     * @throws AggregateException if the aggregate state is invalid
     */
    default void validateState() throws AggregateException {
        // Default implementation does nothing
        // Subclasses can override to provide validation logic
    }

    /**
     * Exception thrown when aggregate operations fail.
     */
    class AggregateException extends RuntimeException {
        /**
         * Serial version UID for serialization compatibility.
         */
        private static final long serialVersionUID = 1L;

        /**
         * Creates a new instance.
         * @param message the message.
         */
        public AggregateException(final String message) {
            super(message);
        }

        /**
         * Creates a new instance.
         * @param message the message.
         * @param cause the underlying cause.
         */
        public AggregateException(final String message, final Throwable cause) {
            super(message, cause);
        }
    }
}
