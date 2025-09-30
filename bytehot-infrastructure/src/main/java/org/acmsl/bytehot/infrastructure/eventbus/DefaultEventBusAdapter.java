/*
                        ByteHot

    Copyright (C) 2025-today  rydnr@acm-sl.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
/*******************************************************************************
 *
 * Filename: DefaultEventBusAdapter.java
 *
 * Author: Claude
 *
 * Class name: DefaultEventBusAdapter
 *
 * Responsibilities:
 *   - Provide a default implementation of EventBusPort
 *   - Handle event publishing and subscription
 *   - Manage asynchronous event delivery
 *
 * Collaborators:
 *   - EventBusPort: The port this adapter implements
 *   - DomainEvent: The events to be published
 *   - EventHandler: The handlers for events
 */
package org.acmsl.bytehot.infrastructure.eventbus;

import org.acmsl.bytehot.domain.ports.EventBusPort;

import org.acmsl.commons.patterns.Adapter;
import org.acmsl.commons.patterns.DomainEvent;
import org.acmsl.commons.patterns.EventHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Default implementation of EventBusPort for event orchestration.
 * @author Claude
 * @since 2025-01-21
 */
@EqualsAndHashCode
@ToString
public class DefaultEventBusAdapter
    implements EventBusPort, Adapter<EventBusPort> {

    /**
     * The map of event handlers organized by event type.
     */
    private final Map<Class<? extends DomainEvent>, List<EventHandler<? extends DomainEvent>>> handlers;

    /**
     * The executor service for asynchronous event handling.
     */
    private final ExecutorService executorService;

    /**
     * Counter for published events.
     */
    private long publishedEventCount = 0;

    /**
     * Creates a new DefaultEventBusAdapter.
     */
    public DefaultEventBusAdapter() {
        this.handlers = new ConcurrentHashMap<>();
        this.executorService = Executors.newCachedThreadPool(r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            t.setName("ByteHot-EventBus-" + t.getId());
            return t;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<EventBusPort> adapts() {
        return EventBusPort.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publish(@NonNull final DomainEvent event) {
        publishedEventCount++;
        final List<EventHandler<? extends DomainEvent>> eventHandlers =
            handlers.getOrDefault(event.getClass(), Collections.emptyList());

        for (final EventHandler handler : eventHandlers) {
            try {
                @SuppressWarnings("unchecked")
                final EventHandler<DomainEvent> typedHandler =
                    (EventHandler<DomainEvent>) handler;
                typedHandler.handle(event);
            } catch (final Exception e) {
                System.err.println("Error handling event " + event.getClass().getSimpleName() +
                                 ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<Void> publishAsync(@NonNull final DomainEvent event) {
        return CompletableFuture.runAsync(() -> publish(event), executorService);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publishBatch(@NonNull final List<DomainEvent> events) {
        for (final DomainEvent event : events) {
            publish(event);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends DomainEvent> void subscribe(
        @NonNull final Class<T> eventType,
        @NonNull final EventHandler<T> handler) {

        handlers.compute(eventType, (type, existingHandlers) -> {
            final List<EventHandler<? extends DomainEvent>> list =
                existingHandlers != null ? new ArrayList<>(existingHandlers) : new ArrayList<>();
            list.add(handler);
            return list;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends DomainEvent> void unsubscribe(
        @NonNull final Class<T> eventType,
        @NonNull final EventHandler<T> handler) {

        handlers.compute(eventType, (type, existingHandlers) -> {
            if (existingHandlers != null) {
                final List<EventHandler<? extends DomainEvent>> list =
                    new ArrayList<>(existingHandlers);
                list.remove(handler);
                return list.isEmpty() ? null : list;
            }
            return null;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isHealthy() {
        return !executorService.isShutdown() && !executorService.isTerminated();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getPublishedEventCount() {
        return publishedEventCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        handlers.clear();
        publishedEventCount = 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getHandlerCount(final Class<? extends DomainEvent> eventType) {
        final List<EventHandler<? extends DomainEvent>> eventHandlers =
            handlers.get(eventType);
        return eventHandlers != null ? eventHandlers.size() : 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasHandlers(final Class<? extends DomainEvent> eventType) {
        final List<EventHandler<? extends DomainEvent>> eventHandlers =
            handlers.get(eventType);
        return eventHandlers != null && !eventHandlers.isEmpty();
    }

    /**
     * Shuts down the event bus and releases resources.
     */
    public void shutdown() {
        executorService.shutdown();
    }
}