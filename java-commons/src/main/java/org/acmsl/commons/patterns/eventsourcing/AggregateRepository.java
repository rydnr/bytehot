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
 * Filename: AggregateRepository.java
 *
 * Author: Claude Code
 *
 * Interface name: AggregateRepository
 *
 * Responsibilities:
 *   - Provide generic repository interface for EventSourcing aggregates
 *   - Define standard CRUD operations for aggregate roots
 *   - Support event history access and aggregate reconstruction
 *   - Enable optimistic concurrency control with versioning
 *
 * Collaborators:
 *   - AggregateRoot: Aggregates managed by this repository
 *   - VersionedDomainEvent: Events used for aggregate reconstruction
 *   - Repository: Base repository interface from patterns package
 */
package org.acmsl.commons.patterns.eventsourcing;

import org.acmsl.commons.patterns.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Generic repository interface for EventSourcing aggregate roots.
 * Provides standard CRUD operations with EventSourcing-specific
 * capabilities for event history access and aggregate reconstruction.
 * 
 * @param <T> the type of aggregate root managed by this repository
 * @author Claude Code
 * @since 2025-06-26
 */
public interface AggregateRepository<T extends AggregateRoot> extends Repository {

    /**
     * Finds an aggregate by its unique identifier, reconstructing it
     * from its complete event history.
     * 
     * @param aggregateId the unique identifier of the aggregate
     * @return Optional containing the aggregate if found, empty otherwise
     * @throws AggregateRepositoryException if reconstruction fails
     */
    Optional<T> findById(final String aggregateId) throws AggregateRepositoryException;

    /**
     * Finds an aggregate by its unique identifier up to a specific version,
     * reconstructing it from events up to that point in time.
     * 
     * @param aggregateId the unique identifier of the aggregate
     * @param version the maximum version to reconstruct to
     * @return Optional containing the aggregate if found, empty otherwise
     * @throws AggregateRepositoryException if reconstruction fails
     */
    Optional<T> findByIdAndVersion(final String aggregateId, final long version) throws AggregateRepositoryException;

    /**
     * Saves an aggregate by persisting its uncommitted events to the EventStore.
     * This operation includes optimistic concurrency control.
     * 
     * @param aggregate the aggregate to save
     * @throws AggregateRepositoryException if saving fails
     * @throws ConcurrencyException if version conflict occurs
     */
    void save(final T aggregate) throws AggregateRepositoryException, ConcurrencyException;

    /**
     * Checks if an aggregate exists with the given identifier.
     * 
     * @param aggregateId the unique identifier to check
     * @return true if the aggregate exists, false otherwise
     * @throws AggregateRepositoryException if the check fails
     */
    boolean exists(final String aggregateId) throws AggregateRepositoryException;

    /**
     * Gets the complete event history for an aggregate.
     * 
     * @param aggregateId the unique identifier of the aggregate
     * @return list of all events for the aggregate, ordered by version
     * @throws AggregateRepositoryException if retrieval fails
     */
    List<VersionedDomainEvent> getEventHistory(final String aggregateId) throws AggregateRepositoryException;

    /**
     * Gets the event history for an aggregate since a specific version.
     * 
     * @param aggregateId the unique identifier of the aggregate
     * @param sinceVersion the version to start from (exclusive)
     * @return list of events since the specified version
     * @throws AggregateRepositoryException if retrieval fails
     */
    List<VersionedDomainEvent> getEventHistorySince(final String aggregateId, final long sinceVersion) 
            throws AggregateRepositoryException;

    /**
     * Gets the current version of an aggregate without reconstructing it.
     * 
     * @param aggregateId the unique identifier of the aggregate
     * @return the current version, or 0 if the aggregate doesn't exist
     * @throws AggregateRepositoryException if version retrieval fails
     */
    long getCurrentVersion(final String aggregateId) throws AggregateRepositoryException;

    /**
     * Deletes all events for an aggregate. This operation is irreversible.
     * Use with extreme caution.
     * 
     * @param aggregateId the unique identifier of the aggregate to delete
     * @throws AggregateRepositoryException if deletion fails
     */
    void delete(final String aggregateId) throws AggregateRepositoryException;

    /**
     * Gets all aggregate identifiers of this type.
     * Use with caution for large datasets.
     * 
     * @return list of all aggregate identifiers
     * @throws AggregateRepositoryException if retrieval fails
     */
    List<String> getAllAggregateIds() throws AggregateRepositoryException;

    /**
     * Creates a new aggregate instance from its event history.
     * This method is typically used by repository implementations
     * to reconstruct aggregates from events.
     * 
     * @param aggregateId the identifier of the aggregate to create
     * @param events the events to apply for reconstruction
     * @return the reconstructed aggregate
     * @throws AggregateRepositoryException if reconstruction fails
     */
    T reconstructFromEvents(final String aggregateId, final List<VersionedDomainEvent> events) 
            throws AggregateRepositoryException;

    /**
     * Exception thrown when repository operations fail.
     */
    class AggregateRepositoryException extends RuntimeException {
        /**
         * Serial version UID for serialization compatibility.
         */
        private static final long serialVersionUID = 1L;

        /**
         * Creates a new instance.
         * @param message the exception message.
         */
        public AggregateRepositoryException(final String message) {
            super(message);
        }

        /**
         * Creates a new instance.
         * @param message the exception message.
         * @param cause the underlying cause.
         */
        public AggregateRepositoryException(final String message, final Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Exception thrown when optimistic concurrency control detects version conflicts.
     */
    class ConcurrencyException extends RuntimeException {
        /**
         * Serial version UID for serialization compatibility.
         */
        private static final long serialVersionUID = 1L;

        /**
         * The aggregate id.
         */
        protected final String aggregateId;
        /**
         * The expected version.
         */
        protected final long expectedVersion;
        /**
         * The actual version.
         */
        protected final long actualVersion;

        /**
         * Creates a new instance.
         * @param aggregateId the id of the aggregate.
         * @param expectedVersion the expected version.
         * @param actualVersion the actual version.
         */
        public ConcurrencyException(final String aggregateId, final long expectedVersion, final long actualVersion) {
            super(String.format("Concurrency conflict for aggregate %s: expected version %d, actual version %d", 
                               aggregateId, expectedVersion, actualVersion));
            this.aggregateId = aggregateId;
            this.expectedVersion = expectedVersion;
            this.actualVersion = actualVersion;
        }

        /**
         * Retrieves the aggregate id.
         * @return such id.
         */
        public String getAggregateId() {
            return aggregateId;
        }

        /**
         * Retrieves the expected version.
         * @return such version.
         */
        public long getExpectedVersion() {
            return expectedVersion;
        }

        /**
         * Retrieves the actual version.
         * @return such version.
         */
        public long getActualVersion() {
            return actualVersion;
        }
    }
}
