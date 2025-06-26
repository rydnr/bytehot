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
 * Filename: EventSourcedAggregateRepository.java
 *
 * Author: Claude Code
 *
 * Class name: EventSourcedAggregateRepository
 *
 * Responsibilities:
 *   - Provide base implementation of AggregateRepository using EventStore
 *   - Handle aggregate reconstruction from event streams
 *   - Implement optimistic concurrency control
 *   - Bridge between aggregate domain model and EventStore persistence
 *
 * Collaborators:
 *   - AggregateRepository: Interface this class implements
 *   - EventStore: Underlying event persistence mechanism
 *   - AggregateRoot: Aggregates managed by this repository
 *   - AggregateFactory: Creates new aggregate instances
 */
package org.acmsl.commons.patterns.eventsourcing;

import java.util.List;
import java.util.Optional;

/**
 * Abstract base implementation of AggregateRepository that uses EventStore
 * for persistence. Provides common EventSourcing operations while allowing
 * concrete implementations to define aggregate-specific behavior.
 * 
 * @param <T> the type of aggregate root managed by this repository
 * @author Claude Code
 * @since 2025-06-26
 */
public abstract class EventSourcedAggregateRepository<T extends AggregateRoot> implements AggregateRepository<T> {

    /**
     * EventStore interface for this package
     */
    public interface EventStore {
        void save(final String aggregateType, final String aggregateId, 
                 final List<VersionedDomainEvent> events, final long expectedVersion) throws EventStoreException;
        List<VersionedDomainEvent> getEventsForAggregate(final String aggregateType, final String aggregateId) throws EventStoreException;
        List<VersionedDomainEvent> getEventsForAggregateSince(final String aggregateType, 
                                                             final String aggregateId, final long sinceVersion) throws EventStoreException;
        long getCurrentVersion(final String aggregateType, final String aggregateId) throws EventStoreException;
        boolean aggregateExists(final String aggregateType, final String aggregateId) throws EventStoreException;
        List<String> getAggregateIds(final String aggregateType) throws EventStoreException;
        
        class EventStoreException extends RuntimeException {
            /**
             * Serial version UID for serialization compatibility.
             */
            private static final long serialVersionUID = 1L;

            public EventStoreException(final String message) {
                super(message);
            }
            public EventStoreException(final String message, final Throwable cause) {
                super(message, cause);
            }
        }
    }

    /**
     * Factory interface for creating aggregate instances
     * @param <T> the aggregate type parameter
     */
    public interface AggregateFactory<T extends AggregateRoot> {
        T createEmpty(final String aggregateId);
        String getAggregateType();
    }

    /**
     * The event store
     */
    protected final EventStore eventStore;

    /**
     * The aggregate factory
     */
    protected final AggregateFactory<T> aggregateFactory;

    /**
     * Constructor with EventStore and AggregateFactory dependencies.
     * 
     * @param eventStore the EventStore for persistence
     * @param aggregateFactory the factory for creating aggregate instances
     */
    public EventSourcedAggregateRepository(final EventStore eventStore, final AggregateFactory<T> aggregateFactory) {
        if (eventStore == null) {
            throw new IllegalArgumentException("EventStore cannot be null");
        }
        if (aggregateFactory == null) {
            throw new IllegalArgumentException("AggregateFactory cannot be null");
        }
        
        this.eventStore = eventStore;
        this.aggregateFactory = aggregateFactory;
    }

    /**
     * Finds an aggregate by id
     * @param aggregateId the id to look for
     * @return the aggregate, if any
     * @throws AggregateRepositoryException if the repository is not available
     */
    @Override
    public Optional<T> findById(final String aggregateId) throws AggregateRepositoryException {
        if (aggregateId == null || aggregateId.trim().isEmpty()) {
            return Optional.empty();
        }

        try {
            final String aggregateType = aggregateFactory.getAggregateType();
            final List<VersionedDomainEvent> events = eventStore.getEventsForAggregate(aggregateType, aggregateId);
            
            if (events.isEmpty()) {
                return Optional.empty();
            }

            final T aggregate = reconstructFromEvents(aggregateId, events);
            return Optional.of(aggregate);
        } catch (final EventStore.EventStoreException e) {
            throw new AggregateRepositoryException("Failed to find aggregate with ID: " + aggregateId, e);
        }
    }

    /**
     * Finds an aggregate by id and version
     * @param aggregateId the id to look for
     * @param version the version
     * @return the aggregate, if any
     * @throws AggregateRepositoryException if the repository is not available
     */
    @Override
    public Optional<T> findByIdAndVersion(final String aggregateId, final long version) throws AggregateRepositoryException {
        if (aggregateId == null || aggregateId.trim().isEmpty() || version < 0) {
            return Optional.empty();
        }

        try {
            final String aggregateType = aggregateFactory.getAggregateType();
            final List<VersionedDomainEvent> allEvents = eventStore.getEventsForAggregate(aggregateType, aggregateId);
            
            if (allEvents.isEmpty()) {
                return Optional.empty();
            }

            // Filter events up to the specified version
            final List<VersionedDomainEvent> eventsUpToVersion = allEvents.stream()
                .filter(event -> event.getAggregateVersion() <= version)
                .toList();

            if (eventsUpToVersion.isEmpty()) {
                return Optional.empty();
            }

            final T aggregate = reconstructFromEvents(aggregateId, eventsUpToVersion);
            return Optional.of(aggregate);
        } catch (final EventStore.EventStoreException e) {
            throw new AggregateRepositoryException("Failed to find aggregate with ID: " + aggregateId + " and version: " + version, e);
        }
    }

    /**
     * Saves an aggregate
     * @param aggregate the aggregate
     * @throws AggregateRepositoryException if the repository is not available
     * @throws ConcurrencyException if the operation failed
     */
    @Override
    public void save(final T aggregate) throws AggregateRepositoryException, ConcurrencyException {
        if (aggregate == null) {
            throw new AggregateRepositoryException("Cannot save null aggregate");
        }

        final List<VersionedDomainEvent> uncommittedEvents = aggregate.getUncommittedEvents();
        if (uncommittedEvents.isEmpty()) {
            return; // Nothing to save
        }

        try {
            final String aggregateType = aggregate.getAggregateType();
            final String aggregateId = aggregate.getAggregateId();
            final long expectedVersion = aggregate.getVersion() - uncommittedEvents.size();

            eventStore.save(aggregateType, aggregateId, uncommittedEvents, expectedVersion);
            aggregate.markEventsAsCommitted();
        } catch (final EventStore.EventStoreException e) {
            if (e.getMessage().contains("version conflict") || e.getMessage().contains("concurrency")) {
                // Try to extract version information for a proper ConcurrencyException
                throw new ConcurrencyException(aggregate.getAggregateId(), 
                                             aggregate.getVersion() - uncommittedEvents.size(),
                                             getCurrentVersion(aggregate.getAggregateId()));
            }
            throw new AggregateRepositoryException("Failed to save aggregate: " + aggregate.getAggregateId(), e);
        }
    }

    /**
     * Checks whether an aggregate exists
     * @param aggregateId the id of the aggregate
     * @return true in such case
     * @throws AggregateRepositoryException if the repository is not available
     */
    @Override
    public boolean exists(final String aggregateId) throws AggregateRepositoryException {
        if (aggregateId == null || aggregateId.trim().isEmpty()) {
            return false;
        }

        try {
            final String aggregateType = aggregateFactory.getAggregateType();
            return eventStore.aggregateExists(aggregateType, aggregateId);
        } catch (final EventStore.EventStoreException e) {
            throw new AggregateRepositoryException("Failed to check existence of aggregate: " + aggregateId, e);
        }
    }

    /**
     * Retrieves the event history
     * @param aggregateId the id of the aggregate
     * @return the past events
     * @throws AggregateRepositoryException if the repository is not available
     */
    @Override
    public List<VersionedDomainEvent> getEventHistory(final String aggregateId) throws AggregateRepositoryException {
        if (aggregateId == null || aggregateId.trim().isEmpty()) {
            throw new AggregateRepositoryException("Aggregate ID cannot be null or empty");
        }

        try {
            final String aggregateType = aggregateFactory.getAggregateType();
            return eventStore.getEventsForAggregate(aggregateType, aggregateId);
        } catch (final EventStore.EventStoreException e) {
            throw new AggregateRepositoryException("Failed to get event history for aggregate: " + aggregateId, e);
        }
    }

    /**
     * Retrieves the event history from a point in time
     * @param aggregateId the id of the aggregate
     * @param sinceVersion the initial version
     * @return the past events
     * @throws AggregateRepositoryException if the repository is not available
     */
    @Override
    public List<VersionedDomainEvent> getEventHistorySince(final String aggregateId, final long sinceVersion) 
            throws AggregateRepositoryException {
        if (aggregateId == null || aggregateId.trim().isEmpty()) {
            throw new AggregateRepositoryException("Aggregate ID cannot be null or empty");
        }

        try {
            final String aggregateType = aggregateFactory.getAggregateType();
            return eventStore.getEventsForAggregateSince(aggregateType, aggregateId, sinceVersion);
        } catch (final EventStore.EventStoreException e) {
            throw new AggregateRepositoryException("Failed to get event history since version " + sinceVersion + 
                                                 " for aggregate: " + aggregateId, e);
        }
    }

    /**
     * Retrieves the current version of an aggregate
     * @param aggregateId the id of the aggregate
     * @return the current version
     * @throws AggregateRepositoryException if the repository is not available
     */
    @Override
    public long getCurrentVersion(final String aggregateId) throws AggregateRepositoryException {
        if (aggregateId == null || aggregateId.trim().isEmpty()) {
            return 0;
        }

        try {
            final String aggregateType = aggregateFactory.getAggregateType();
            return eventStore.getCurrentVersion(aggregateType, aggregateId);
        } catch (final EventStore.EventStoreException e) {
            throw new AggregateRepositoryException("Failed to get current version for aggregate: " + aggregateId, e);
        }
    }

    /**
     * Deletes an aggregate
     * @param aggregateId the id of the aggregate
     * @throws AggregateRepositoryException if the repository is not available
     */
    @Override
    public void delete(final String aggregateId) throws AggregateRepositoryException {
        throw new AggregateRepositoryException("Delete operation not supported for EventSourcing aggregates. " +
                                             "Consider using a 'deleted' event instead to maintain audit trail.");
    }

    /**
     * Retrieves all aggregate ids
     * @return such ids
     * @throws AggregateRepositoryException if the repository is not available
     */
    @Override
    public List<String> getAllAggregateIds() throws AggregateRepositoryException {
        try {
            final String aggregateType = aggregateFactory.getAggregateType();
            return eventStore.getAggregateIds(aggregateType);
        } catch (final EventStore.EventStoreException e) {
            throw new AggregateRepositoryException("Failed to get all aggregate IDs for type: " + 
                                                 aggregateFactory.getAggregateType(), e);
        }
    }

    /**
     * Reconstructs an aggregate from its events
     * @param aggregateId the id of the aggregate
     * @param events the events
     * @throws AggregateRepositoryException if the repository is not available
     */
    @Override
    public T reconstructFromEvents(final String aggregateId, final List<VersionedDomainEvent> events) 
            throws AggregateRepositoryException {
        if (aggregateId == null || aggregateId.trim().isEmpty()) {
            throw new AggregateRepositoryException("Cannot reconstruct aggregate with null or empty ID");
        }

        if (events == null || events.isEmpty()) {
            throw new AggregateRepositoryException("Cannot reconstruct aggregate with no events");
        }

        try {
            final T aggregate = aggregateFactory.createEmpty(aggregateId);
            
            for (final VersionedDomainEvent event : events) {
                aggregate.applyEvent(event);
            }

            aggregate.validateState();
            return aggregate;
        } catch (final AggregateRoot.AggregateException e) {
            throw new AggregateRepositoryException("Failed to reconstruct aggregate: " + aggregateId, e);
        }
    }

    /**
     * Gets the aggregate type managed by this repository.
     * 
     * @return the aggregate type name
     */
    public String getAggregateType() {
        return aggregateFactory.getAggregateType();
    }

    /**
     * Gets the EventStore used by this repository.
     * 
     * @return the EventStore instance
     */
    protected EventStore getEventStore() {
        return eventStore;
    }

    /**
     * Gets the AggregateFactory used by this repository.
     * 
     * @return the AggregateFactory instance
     */
    protected AggregateFactory<T> getAggregateFactory() {
        return aggregateFactory;
    }
}
