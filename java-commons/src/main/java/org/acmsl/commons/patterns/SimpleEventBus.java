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
 * Filename: SimpleEventBus.java
 *
 * Author: Claude Code
 *
 * Class name: SimpleEventBus
 *
 * Responsibilities:
 *   - Provide simple in-memory implementation of EventBus
 *   - Support synchronous and asynchronous event publishing
 *   - Manage event handler registration and invocation
 *   - Provide thread-safe operations for concurrent usage
 *
 * Collaborators:
 *   - EventBus: Interface this class implements
 *   - EventHandler: Registered handlers for event processing
 *   - DomainEvent: Events published through this bus
 */
package org.acmsl.commons.patterns;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Simple thread-safe implementation of EventBus using in-memory storage.
 * Supports both synchronous and asynchronous event processing with
 * concurrent handler management.
 * 
 * @author Claude Code
 * @since 2025-06-26
 */
public class SimpleEventBus implements EventBus {

    /**
     * Map of event types to their registered handlers
     */
    protected final Map<Class<? extends DomainEvent>, Set<EventHandler<? extends DomainEvent>>> handlers;

    /**
     * Counter for published events
     */
    protected final AtomicLong publishedEventCount;

    /**
     * Executor for asynchronous event processing
     */
    protected final Executor asyncExecutor;

    /**
     * Flag indicating if this bus is healthy
     */
    protected volatile boolean healthy;

    /**
     * Default constructor using ForkJoinPool for async execution.
     */
    public SimpleEventBus() {
        this(ForkJoinPool.commonPool());
    }

    /**
     * Constructor with custom executor for async operations.
     * 
     * @param asyncExecutor the executor to use for async event processing
     */
    public SimpleEventBus(final Executor asyncExecutor) {
        this.handlers = new ConcurrentHashMap<>();
        this.publishedEventCount = new AtomicLong(0);
        this.asyncExecutor = asyncExecutor;
        this.healthy = true;
    }

    @Override
    public void publish(final DomainEvent event) throws EventBusException {
        if (!healthy) {
            throw new EventBusException("EventBus is not healthy");
        }

        if (event == null) {
            throw new EventBusException("Cannot publish null event");
        }

        try {
            final Set<EventHandler<? extends DomainEvent>> eventHandlers = getHandlersForEvent(event);
            
            for (final EventHandler<? extends DomainEvent> handler : eventHandlers) {
                try {
                    @SuppressWarnings("unchecked")
                    final EventHandler<DomainEvent> typedHandler = (EventHandler<DomainEvent>) handler;
                    typedHandler.handle(event);
                } catch (final Exception e) {
                    throw new EventBusException("Failed to handle event: " + event.getClass().getSimpleName(), e);
                }
            }

            publishedEventCount.incrementAndGet();
        } catch (final EventBusException e) {
            throw e;
        } catch (final Exception e) {
            throw new EventBusException("Unexpected error publishing event: " + event.getClass().getSimpleName(), e);
        }
    }

    @Override
    public CompletableFuture<Void> publishAsync(final DomainEvent event) {
        if (!healthy) {
            return CompletableFuture.failedFuture(new EventBusException("EventBus is not healthy"));
        }

        if (event == null) {
            return CompletableFuture.failedFuture(new EventBusException("Cannot publish null event"));
        }

        return CompletableFuture.runAsync(() -> {
            try {
                publish(event);
            } catch (final EventBusException e) {
                throw new RuntimeException(e);
            }
        }, asyncExecutor);
    }

    @Override
    public void publishBatch(final List<DomainEvent> events) throws EventBusException {
        if (!healthy) {
            throw new EventBusException("EventBus is not healthy");
        }

        if (events == null || events.isEmpty()) {
            return;
        }

        for (final DomainEvent event : events) {
            publish(event);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends DomainEvent> void subscribe(final Class<T> eventType, final EventHandler<T> handler) {
        if (eventType == null) {
            throw new IllegalArgumentException("Event type cannot be null");
        }
        if (handler == null) {
            throw new IllegalArgumentException("Handler cannot be null");
        }

        handlers.computeIfAbsent(eventType, k -> new CopyOnWriteArraySet<>())
                .add((EventHandler<? extends DomainEvent>) handler);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends DomainEvent> void unsubscribe(final Class<T> eventType, final EventHandler<T> handler) {
        if (eventType == null || handler == null) {
            return;
        }

        final Set<EventHandler<? extends DomainEvent>> eventHandlers = handlers.get(eventType);
        if (eventHandlers != null) {
            eventHandlers.remove((EventHandler<? extends DomainEvent>) handler);
            
            // Clean up empty handler sets
            if (eventHandlers.isEmpty()) {
                handlers.remove(eventType);
            }
        }
    }

    @Override
    public boolean hasHandlers(final Class<? extends DomainEvent> eventType) {
        if (eventType == null) {
            return false;
        }

        final Set<EventHandler<? extends DomainEvent>> eventHandlers = handlers.get(eventType);
        return eventHandlers != null && !eventHandlers.isEmpty();
    }

    @Override
    public int getHandlerCount(final Class<? extends DomainEvent> eventType) {
        if (eventType == null) {
            return 0;
        }

        final Set<EventHandler<? extends DomainEvent>> eventHandlers = handlers.get(eventType);
        return eventHandlers != null ? eventHandlers.size() : 0;
    }

    @Override
    public void clear() {
        handlers.clear();
        publishedEventCount.set(0);
    }

    @Override
    public long getPublishedEventCount() {
        return publishedEventCount.get();
    }

    @Override
    public boolean isHealthy() {
        return healthy;
    }

    /**
     * Sets the health status of this EventBus.
     * 
     * @param healthy the new health status
     */
    public void setHealthy(final boolean healthy) {
        this.healthy = healthy;
    }

    /**
     * Gets all handlers registered for the given event, including handlers
     * for parent event types.
     * 
     * @param event the event to find handlers for
     * @return set of handlers for the event
     */
    protected Set<EventHandler<? extends DomainEvent>> getHandlersForEvent(final DomainEvent event) {
        final Class<? extends DomainEvent> eventType = event.getClass();
        
        // Get direct handlers for this event type
        final Set<EventHandler<? extends DomainEvent>> directHandlers = handlers.getOrDefault(eventType, Set.of());
        
        // Get handlers for parent event types (simple inheritance check)
        final Set<EventHandler<? extends DomainEvent>> inheritedHandlers = handlers.entrySet().stream()
            .filter(entry -> entry.getKey().isAssignableFrom(eventType) && !entry.getKey().equals(eventType))
            .flatMap(entry -> entry.getValue().stream())
            .collect(Collectors.toSet());
        
        // Combine and sort by priority
        final Set<EventHandler<? extends DomainEvent>> allHandlers = new CopyOnWriteArraySet<>(directHandlers);
        allHandlers.addAll(inheritedHandlers);
        
        return allHandlers;
    }

    /**
     * Gets a summary of registered handlers for debugging.
     * 
     * @return summary string of handler registrations
     */
    public String getHandlerSummary() {
        final StringBuilder summary = new StringBuilder();
        summary.append("SimpleEventBus Handler Summary:\n");
        summary.append("Total event types: ").append(handlers.size()).append("\n");
        summary.append("Total published events: ").append(publishedEventCount.get()).append("\n");
        summary.append("Health status: ").append(healthy).append("\n");
        
        for (final Map.Entry<Class<? extends DomainEvent>, Set<EventHandler<? extends DomainEvent>>> entry : handlers.entrySet()) {
            summary.append("- ").append(entry.getKey().getSimpleName())
                   .append(": ").append(entry.getValue().size()).append(" handlers\n");
        }
        
        return summary.toString();
    }
}