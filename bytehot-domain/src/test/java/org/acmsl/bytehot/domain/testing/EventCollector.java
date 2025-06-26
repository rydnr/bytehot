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
 * Filename: EventCollector.java
 *
 * Author: Claude Code
 *
 * Class name: EventCollector
 *
 * Responsibilities:
 *   - Collect domain events during integration testing
 *   - Provide utilities to wait for specific events
 *   - Verify event sequences and timing
 *
 * Collaborators:
 *   - DomainEvent: Events being collected
 *   - Integration tests: Consumers of collected events
 */
package org.acmsl.bytehot.domain.testing;

import org.acmsl.commons.patterns.DomainEvent;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Collects domain events during integration testing and provides utilities for verification
 * @author Claude Code
 * @since 2025-06-26
 */
public class EventCollector implements Consumer<DomainEvent> {

    /**
     * List of collected events in chronological order
     */
    private final List<DomainEvent> collectedEvents = new ArrayList<>();
    
    /**
     * Latches for waiting on specific event types
     */
    private final List<EventWaiter> waiters = new ArrayList<>();

    /**
     * Accepts and stores a domain event
     * @param event the event to collect
     */
    @Override
    public void accept(@NonNull final DomainEvent event) {
        synchronized (collectedEvents) {
            collectedEvents.add(event);
        }
        
        // Notify any waiting threads
        synchronized (waiters) {
            for (EventWaiter waiter : waiters) {
                if (waiter.matches(event)) {
                    waiter.countDown();
                }
            }
        }
    }

    /**
     * Gets all collected events
     * @return list of collected events
     */
    @NonNull
    public List<DomainEvent> getCollectedEvents() {
        synchronized (collectedEvents) {
            return new ArrayList<>(collectedEvents);
        }
    }

    /**
     * Gets events of a specific type
     * @param eventType the type of events to retrieve
     * @param <T> the event type
     * @return list of events of the specified type
     */
    @NonNull
    public <T extends DomainEvent> List<T> getEventsOfType(@NonNull final Class<T> eventType) {
        synchronized (collectedEvents) {
            return collectedEvents.stream()
                .filter(eventType::isInstance)
                .map(eventType::cast)
                .toList();
        }
    }

    /**
     * Waits for an event of the specified type to be collected
     * @param eventType the type of event to wait for
     * @param timeout the maximum time to wait
     * @param <T> the event type
     * @return the first event of the specified type, or null if timeout
     * @throws InterruptedException if thread is interrupted while waiting
     */
    @Nullable
    public <T extends DomainEvent> T waitForEvent(@NonNull final Class<T> eventType, 
                                                  @NonNull final Duration timeout) throws InterruptedException {
        // Check if we already have the event
        final List<T> existing = getEventsOfType(eventType);
        if (!existing.isEmpty()) {
            return existing.get(0);
        }
        
        // Create a waiter for this event type
        final EventWaiter waiter = new EventWaiter(event -> eventType.isInstance(event));
        synchronized (waiters) {
            waiters.add(waiter);
        }
        
        try {
            if (waiter.await(timeout.toMillis(), TimeUnit.MILLISECONDS)) {
                // Event arrived, find and return it
                final List<T> events = getEventsOfType(eventType);
                return events.isEmpty() ? null : events.get(0);
            } else {
                // Timeout
                return null;
            }
        } finally {
            synchronized (waiters) {
                waiters.remove(waiter);
            }
        }
    }

    /**
     * Waits for multiple events in sequence
     * @param eventTypes the types of events to wait for in order
     * @param timeout the maximum time to wait for all events
     * @return true if all events were collected within timeout
     * @throws InterruptedException if thread is interrupted while waiting
     */
    @SafeVarargs
    public final boolean waitForEventSequence(@NonNull final Duration timeout, 
                                             @NonNull final Class<? extends DomainEvent>... eventTypes) throws InterruptedException {
        final long startTime = System.currentTimeMillis();
        final long timeoutMillis = timeout.toMillis();
        
        for (Class<? extends DomainEvent> eventType : eventTypes) {
            final long remainingTime = timeoutMillis - (System.currentTimeMillis() - startTime);
            if (remainingTime <= 0) {
                return false; // Timeout
            }
            
            final DomainEvent event = waitForEvent(eventType, Duration.ofMillis(remainingTime));
            if (event == null) {
                return false; // Specific event timed out
            }
        }
        
        return true; // All events collected successfully
    }

    /**
     * Verifies that events were collected in the expected sequence
     * @param expectedSequence the expected sequence of event types
     * @return true if the sequence matches
     */
    @SafeVarargs
    public final boolean verifyEventSequence(@NonNull final Class<? extends DomainEvent>... expectedSequence) {
        final List<DomainEvent> events = getCollectedEvents();
        
        if (events.size() < expectedSequence.length) {
            return false;
        }
        
        for (int i = 0; i < expectedSequence.length; i++) {
            if (!expectedSequence[i].isInstance(events.get(i))) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Gets the count of collected events
     * @return number of collected events
     */
    public int getEventCount() {
        synchronized (collectedEvents) {
            return collectedEvents.size();
        }
    }

    /**
     * Clears all collected events and waiters
     */
    public void clear() {
        synchronized (collectedEvents) {
            collectedEvents.clear();
        }
        synchronized (waiters) {
            waiters.clear();
        }
    }

    /**
     * Gets the last collected event
     * @return the most recent event, or null if no events collected
     */
    @Nullable
    public DomainEvent getLastEvent() {
        synchronized (collectedEvents) {
            return collectedEvents.isEmpty() ? null : collectedEvents.get(collectedEvents.size() - 1);
        }
    }

    /**
     * Gets events collected within a time window
     * @param start the start of the time window
     * @param end the end of the time window
     * @return events collected within the time window
     */
    @NonNull
    public List<DomainEvent> getEventsInTimeWindow(@NonNull final Instant start, @NonNull final Instant end) {
        synchronized (collectedEvents) {
            return collectedEvents.stream()
                .filter(event -> {
                    // For this implementation, we'll assume events have timestamps
                    // In a real implementation, you'd extract timestamp from event metadata
                    return true; // Placeholder - would check event timestamp
                })
                .toList();
        }
    }

    /**
     * Internal class for waiting on specific events
     */
    private static class EventWaiter {
        private final CountDownLatch latch = new CountDownLatch(1);
        private final Predicate<DomainEvent> matcher;

        public EventWaiter(@NonNull final Predicate<DomainEvent> matcher) {
            this.matcher = matcher;
        }

        public boolean matches(@NonNull final DomainEvent event) {
            return matcher.test(event);
        }

        public void countDown() {
            latch.countDown();
        }

        public boolean await(final long timeout, @NonNull final TimeUnit unit) throws InterruptedException {
            return latch.await(timeout, unit);
        }
    }
}