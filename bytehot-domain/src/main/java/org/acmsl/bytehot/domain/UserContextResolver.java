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
 * Filename: UserContextResolver.java
 *
 * Author: Claude Code
 *
 * Class name: UserContextResolver
 *
 * Responsibilities:
 *   - Manage thread-local user context for operations
 *   - Auto-discover users when context is not set
 *   - Provide utility methods for user context propagation
 *   - Support nested user context with proper cleanup
 *
 * Collaborators:
 *   - UserId: User identifier value object
 *   - UserIdentificationStrategy: Auto-discovery logic
 *   - ThreadLocal: Thread-safe context storage
 */
package org.acmsl.bytehot.domain;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.function.Supplier;

/**
 * Thread-safe user context resolver with auto-discovery capabilities.
 * Manages user context propagation throughout ByteHot operations.
 * @author Claude Code
 * @since 2025-06-19
 */
public final class UserContextResolver {

    private static final ThreadLocal<UserId> currentUser = new ThreadLocal<>();

    private UserContextResolver() {
        // Utility class - prevent instantiation
    }

    /**
     * Sets the current user for this thread.
     * @param userId the user ID to set as current
     */
    public static void setCurrentUser(@NonNull final UserId userId) {
        currentUser.set(userId);
    }

    /**
     * Gets the current user, auto-discovering if needed.
     * This method never returns null - it will auto-discover or create an anonymous user.
     * @return the current user ID (never null)
     */
    @NonNull
    public static UserId getCurrentUser() {
        UserId userId = currentUser.get();
        if (userId == null) {
            userId = autoDiscoverUser();
            setCurrentUser(userId);
        }
        return userId;
    }

    /**
     * Gets the current user without auto-discovery.
     * @return the current user ID or null if not set
     */
    @Nullable
    public static UserId getCurrentUserOrNull() {
        return currentUser.get();
    }

    /**
     * Checks if a user context is currently set.
     * @return true if a user context is set for this thread
     */
    public static boolean hasUserContext() {
        return currentUser.get() != null;
    }

    /**
     * Clears the current user context for this thread.
     * Should be called when the operation completes to prevent memory leaks.
     */
    public static void clearCurrentUser() {
        currentUser.remove();
    }

    /**
     * Executes code with a specific user context.
     * Automatically restores the previous context when done.
     * @param userId the user ID to use during execution
     * @param action the code to execute
     * @param <T> the return type
     * @return the result of the action
     */
    @NonNull
    public static <T> T withUser(@NonNull final UserId userId, @NonNull final Supplier<T> action) {
        UserId previousUser = currentUser.get();
        try {
            setCurrentUser(userId);
            return action.get();
        } finally {
            if (previousUser != null) {
                setCurrentUser(previousUser);
            } else {
                clearCurrentUser();
            }
        }
    }

    /**
     * Executes code with a specific user context (void version).
     * Automatically restores the previous context when done.
     * @param userId the user ID to use during execution
     * @param action the code to execute
     */
    public static void withUser(@NonNull final UserId userId, @NonNull final Runnable action) {
        withUser(userId, () -> {
            action.run();
            return null;
        });
    }

    /**
     * Executes code ensuring a user context is set.
     * If no context is currently set, auto-discovers and sets one.
     * @param action the code to execute
     * @param <T> the return type
     * @return the result of the action
     */
    @NonNull
    public static <T> T withEnsuredUser(@NonNull final Supplier<T> action) {
        if (!hasUserContext()) {
            UserId autoDiscovered = autoDiscoverUser();
            return withUser(autoDiscovered, action);
        }
        return action.get();
    }

    /**
     * Executes code ensuring a user context is set (void version).
     * If no context is currently set, auto-discovers and sets one.
     * @param action the code to execute
     */
    public static void withEnsuredUser(@NonNull final Runnable action) {
        withEnsuredUser(() -> {
            action.run();
            return null;
        });
    }

    /**
     * Auto-discovers user from environment using UserIdentificationStrategy.
     * @return auto-discovered user ID
     */
    @NonNull
    private static UserId autoDiscoverUser() {
        return UserIdentificationStrategy.getInstance().identifyUser();
    }

    /**
     * Gets a debug description of the current user context.
     * Useful for logging and debugging.
     * @return debug description of the current context
     */
    @NonNull
    public static String getContextDescription() {
        UserId user = getCurrentUserOrNull();
        if (user == null) {
            return "No user context set";
        }
        
        if (user.isAnonymous()) {
            return "Anonymous user: " + user.getValue();
        }
        
        return "User: " + user.getDisplayName() + " (" + user.getValue() + ")";
    }

    /**
     * Creates a user context from a string user ID.
     * Convenience method for setting context from string values.
     * @param userIdString the user ID as a string
     */
    public static void setCurrentUser(@NonNull final String userIdString) {
        setCurrentUser(UserId.of(userIdString));
    }

    /**
     * Ensures user is registered and authenticated for the current context.
     * This method will trigger user registration and authentication if needed.
     * @return the current user (guaranteed to be registered)
     */
    @NonNull
    public static UserId ensureUserRegistered() {
        UserId userId = getCurrentUser();
        
        // Check if user exists and register if needed
        if (!User.userExists(userId)) {
            // Trigger user registration through the domain
            User.accept(new org.acmsl.bytehot.domain.events.UserRegistrationRequested(
                userId, 
                org.acmsl.bytehot.domain.events.UserRegistered.UserRegistrationSource.AUTOMATIC
            ));
        }
        
        return userId;
    }

    /**
     * Gets or creates a session for the current user.
     * This ensures the user has an active session for operations.
     * @return session ID for the current user
     */
    @NonNull
    public static String ensureUserSession() {
        UserId userId = ensureUserRegistered();
        
        // Start session through the domain
        String sessionId = User.generateSessionId();
        User.accept(new org.acmsl.bytehot.domain.events.UserSessionStartRequested(
            userId, 
            java.util.Map.of("source", "auto-session", "sessionId", sessionId) // Environment info
        ));
        
        return sessionId;
    }
}