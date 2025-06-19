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
 * Filename: User.java
 *
 * Author: Claude Code
 *
 * Class name: User
 *
 * Responsibilities:
 *   - Represent user aggregate root with EventSourcing capabilities
 *   - Handle user lifecycle operations (register, authenticate, sessions)
 *   - Reconstruct user state from event history
 *   - Enforce user business invariants
 *
 * Collaborators:
 *   - UserId: User identifier
 *   - UserProfile: User profile information
 *   - UserPreferences: User configuration
 *   - UserStatistics: User analytics
 *   - UserSession: Session management
 *   - EventStorePort: Event persistence
 *   - User domain events: Event representations
 */
package org.acmsl.bytehot.domain;

import org.acmsl.bytehot.domain.events.UserAuthenticationRequested;
import org.acmsl.bytehot.domain.events.UserAuthenticated;
import org.acmsl.bytehot.domain.events.UserRegistrationRequested;
import org.acmsl.bytehot.domain.events.UserRegistered;
import org.acmsl.bytehot.domain.events.UserSessionStartRequested;
import org.acmsl.bytehot.domain.events.UserSessionStarted;

import org.acmsl.commons.patterns.DomainResponseEvent;
import org.acmsl.commons.patterns.eventsourcing.VersionedDomainEvent;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * User aggregate root with EventSourcing reconstruction capabilities
 * @author Claude Code
 * @since 2025-06-18
 */
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
public class User {

    /**
     * User identifier
     */
    private final UserId userId;

    /**
     * User profile information
     */
    private final UserProfile profile;

    /**
     * User preferences and configuration
     */
    private final UserPreferences preferences;

    /**
     * User usage statistics
     */
    private final UserStatistics statistics;

    /**
     * User registration timestamp
     */
    private final Instant registeredAt;

    /**
     * Last activity timestamp
     */
    private final Instant lastActiveAt;

    /**
     * Aggregate version for EventSourcing
     */
    private final long version;

    /**
     * Primary port: Register a new user
     * @param event the user registration request event
     * @return UserRegistered event
     */
    public static DomainResponseEvent<UserRegistrationRequested> accept(final UserRegistrationRequested event) {
        // Create user profile
        final UserProfile profile = UserProfile.defaultProfile(event.getUserId());
        
        // Create new user
        final User user = new User(
            event.getUserId(),
            profile,
            UserPreferences.defaults(),
            UserStatistics.empty(),
            Instant.now(),
            Instant.now(),
            1L
        );
        
        return UserRegistered.of(event, profile, event.getSource());
    }

    /**
     * Primary port: Authenticate a user
     * @param event the user authentication request event
     * @return UserAuthenticated event
     */
    public static DomainResponseEvent<UserAuthenticationRequested> accept(final UserAuthenticationRequested event) {
        // Try to reconstruct user from events
        final User user = reconstructFromEvents(event.getUserId());
        
        if (user == null) {
            // User doesn't exist, auto-register first
            final UserRegistrationRequested registrationEvent = new UserRegistrationRequested(
                event.getUserId(), 
                UserRegistered.UserRegistrationSource.AUTOMATIC
            );
            accept(registrationEvent);
        }
        
        // Get user profile (after potential registration)
        final User authenticatedUser = reconstructFromEvents(event.getUserId());
        final UserProfile profile = authenticatedUser != null ? authenticatedUser.getProfile() : 
                                   UserProfile.defaultProfile(event.getUserId());
        
        return UserAuthenticated.success(event, profile);
    }

    /**
     * Primary port: Start a user session
     * @param event the user session start request event
     * @return UserSessionStarted event
     */
    public static DomainResponseEvent<UserSessionStartRequested> accept(final UserSessionStartRequested event) {
        // Reconstruct user to get current state
        final User user = reconstructFromEvents(event.getUserId());
        
        if (user == null) {
            throw new IllegalStateException("Cannot start session for non-existent user: " + event.getUserId());
        }
        
        // Create session
        final String sessionId = generateSessionId();
        final UserSession session = UserSession.start(sessionId, event.getUserId(), Instant.now(), event.getEnvironment());
        
        return UserSessionStarted.of(event, session);
    }

    /**
     * Reconstructs a user from their event history
     * @param userId the user identifier
     * @return reconstructed user, or null if not found
     */
    protected static User reconstructFromEvents(final UserId userId) {
        try {
            final EventStorePort eventStore = Ports.resolve(EventStorePort.class);
            final List<VersionedDomainEvent> events = eventStore.getEventsForAggregate("user", userId.getValue());
            
            if (events.isEmpty()) {
                return null;
            }
            
            User user = null;
            for (final VersionedDomainEvent event : events) {
                user = applyEvent(user, event);
            }
            
            return user;
        } catch (final Exception e) {
            // If EventStore is not available, return null
            return null;
        }
    }

    /**
     * Applies a single event to rebuild user state
     * @param currentUser the current user state (may be null)
     * @param event the event to apply
     * @return updated user state
     */
    protected static User applyEvent(final User currentUser, final VersionedDomainEvent event) {
        if (event instanceof UserRegistered) {
            final UserRegistered registeredEvent = (UserRegistered) event;
            return new User(
                registeredEvent.getUserProfile().getUserId(),
                registeredEvent.getUserProfile(),
                UserPreferences.defaults(),
                UserStatistics.empty(),
                Instant.now(),
                Instant.now(),
                1L
            );
        } else if (event instanceof UserAuthenticated) {
            final UserAuthenticated authEvent = (UserAuthenticated) event;
            if (currentUser == null) {
                throw new IllegalStateException("Cannot authenticate user without registration event");
            }
            return new User(
                currentUser.userId,
                currentUser.profile,
                currentUser.preferences,
                currentUser.statistics,
                currentUser.registeredAt,
                Instant.now(),
                currentUser.version + 1
            );
        } else if (event instanceof UserSessionStarted) {
            final UserSessionStarted sessionEvent = (UserSessionStarted) event;
            if (currentUser == null) {
                throw new IllegalStateException("Cannot start session without user registration");
            }
            // Update statistics with new session
            final UserStatistics updatedStats = currentUser.statistics.recordSession(
                sessionEvent.getSession().getStartedAt(),
                sessionEvent.getSession().getDuration()
            );
            
            return new User(
                currentUser.userId,
                currentUser.profile,
                currentUser.preferences,
                updatedStats,
                currentUser.registeredAt,
                Instant.now(),
                currentUser.version + 1
            );
        }
        
        // Unknown event type, return current state
        return currentUser;
    }

    /**
     * Helper method to get the last event ID for an aggregate
     * @param aggregateType the aggregate type
     * @param aggregateId the aggregate identifier
     * @return the last event ID, or null if none exist
     */
    protected static String getLastEventId(final String aggregateType, final String aggregateId) {
        try {
            final EventStorePort eventStore = Ports.resolve(EventStorePort.class);
            final List<VersionedDomainEvent> events = eventStore.getEventsForAggregate(aggregateType, aggregateId);
            if (events.isEmpty()) {
                return null;
            }
            return events.get(events.size() - 1).getEventId();
        } catch (final Exception e) {
            // If EventStore is not available, return null
            return null;
        }
    }

    /**
     * Helper method to check if a user exists
     * @param userId the user identifier
     * @return true if user exists
     */
    protected static boolean userExists(final UserId userId) {
        return reconstructFromEvents(userId) != null;
    }

    /**
     * Creates an updated user with new last active time
     * @param lastActive the new last active timestamp
     * @return updated user
     */
    public User updateLastActive(final Instant lastActive) {
        return new User(
            this.userId,
            this.profile,
            this.preferences,
            this.statistics,
            this.registeredAt,
            lastActive,
            this.version + 1
        );
    }

    /**
     * Creates an updated user with new preferences
     * @param newPreferences the new preferences
     * @return updated user
     */
    public User withPreferences(final UserPreferences newPreferences) {
        return new User(
            this.userId,
            this.profile,
            newPreferences,
            this.statistics,
            this.registeredAt,
            this.lastActiveAt,
            this.version + 1
        );
    }

    /**
     * Creates an updated user with new statistics
     * @param newStatistics the new statistics
     * @return updated user
     */
    public User withStatistics(final UserStatistics newStatistics) {
        return new User(
            this.userId,
            this.profile,
            this.preferences,
            newStatistics,
            this.registeredAt,
            this.lastActiveAt,
            this.version + 1
        );
    }

    /**
     * Generates a unique session ID
     * @return unique session identifier
     */
    protected static String generateSessionId() {
        return "session-" + UUID.randomUUID().toString();
    }
}