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
 * Filename: UserId.java
 *
 * Author: Claude Code
 *
 * Class name: UserId
 *
 * Responsibilities:
 *   - Represent user identifiers as immutable value object
 *   - Provide auto-discovery logic for user identification
 *   - Support various user identification formats (email, git, anonymous)
 *
 * Collaborators:
 *   - None (pure value object)
 */
package org.acmsl.bytehot.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.UUID;

/**
 * User identifier value object with auto-discovery capabilities
 * @author Claude Code
 * @since 2025-06-18
 */
@RequiredArgsConstructor(staticName = "of")
@EqualsAndHashCode
@ToString
public class UserId {

    /**
     * The user identifier value
     */
    @Getter
    private final String value;

    /**
     * Creates an anonymous user ID
     * @return anonymous user ID
     */
    public static UserId anonymous() {
        return UserId.of("anonymous-" + UUID.randomUUID().toString());
    }

    /**
     * Creates a user ID from email, normalizing to lowercase
     * @param email the email address
     * @return user ID based on email
     */
    public static UserId fromEmail(final String email) {
        return UserId.of(email.toLowerCase());
    }

    /**
     * Creates a user ID from Git configuration
     * @param gitUser the Git username
     * @param gitEmail the Git email (may be null)
     * @return user ID from Git config
     */
    public static UserId fromGit(final String gitUser, final String gitEmail) {
        if (gitEmail != null && !gitEmail.isEmpty()) {
            return fromEmail(gitEmail);
        }
        return UserId.of(gitUser);
    }

    /**
     * Checks if this is an anonymous user
     * @return true if anonymous user
     */
    public boolean isAnonymous() {
        return value.startsWith("anonymous-");
    }

    /**
     * Gets a display name for the user
     * @return human-readable display name
     */
    public String getDisplayName() {
        if (isAnonymous()) {
            return "Anonymous User";
        }
        
        if (value.contains("@")) {
            return value.substring(0, value.indexOf("@"));
        }
        
        return value;
    }
}