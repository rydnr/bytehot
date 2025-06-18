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
 * Filename: UserSession.java
 *
 * Author: Claude Code
 *
 * Class name: UserSession
 *
 * Responsibilities:
 *   - Represent an active user session
 *   - Track session metadata and environment information
 *   - Support session state management
 *
 * Collaborators:
 *   - UserId: User identifier
 *   - EnvironmentInfo: Environment details
 */
package org.acmsl.bytehot.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

/**
 * User session value object for session management
 * @author Claude Code
 * @since 2025-06-18
 */
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
public class UserSession {

    /**
     * Unique session identifier
     */
    private final String sessionId;

    /**
     * User identifier for this session
     */
    private final UserId userId;

    /**
     * Session start timestamp
     */
    private final Instant startedAt;

    /**
     * Environment information
     */
    private final Map<String, String> environment;

    /**
     * Session end timestamp (null if active)
     */
    private final Instant endedAt;

    /**
     * Creates a new active session
     * @param sessionId unique session identifier
     * @param userId user identifier
     * @param startedAt session start time
     * @param environment environment information
     * @return new user session
     */
    public static UserSession start(final String sessionId, final UserId userId, 
                                  final Instant startedAt, final Map<String, String> environment) {
        return new UserSession(sessionId, userId, startedAt, environment, null);
    }

    /**
     * Ends this session
     * @param endTime session end time
     * @return session with end time set
     */
    public UserSession end(final Instant endTime) {
        return new UserSession(this.sessionId, this.userId, this.startedAt, 
                             this.environment, endTime);
    }

    /**
     * Checks if the session is currently active
     * @return true if session is active (not ended)
     */
    public boolean isActive() {
        return endedAt == null;
    }

    /**
     * Gets the session duration (if ended) or current duration (if active)
     * @return session duration
     */
    public Duration getDuration() {
        final Instant end = endedAt != null ? endedAt : Instant.now();
        return Duration.between(startedAt, end);
    }

    /**
     * Gets an environment variable value
     * @param key environment variable name
     * @return environment variable value, or null if not present
     */
    public String getEnvironmentVariable(final String key) {
        return environment.get(key);
    }

    /**
     * Gets the Java version for this session
     * @return Java version string
     */
    public String getJavaVersion() {
        return getEnvironmentVariable("java.version");
    }

    /**
     * Gets the operating system for this session
     * @return OS name string
     */
    public String getOperatingSystem() {
        return getEnvironmentVariable("os.name");
    }

    /**
     * Gets the working directory for this session
     * @return working directory path
     */
    public String getWorkingDirectory() {
        return getEnvironmentVariable("user.dir");
    }
}