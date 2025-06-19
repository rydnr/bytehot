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

import org.acmsl.commons.patterns.dao.AbstractId;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.UUID;

/**
 * Type-safe identifier for ByteHot users with intelligent auto-discovery capabilities.
 * Supports multiple identification sources and provides enhanced user context.
 * @author Claude Code
 * @since 2025-06-18
 */
@EqualsAndHashCode(callSuper = true)
@ToString
public final class UserId extends AbstractId<UserId> {

    private UserId(@NonNull final String value) {
        super(value);
    }

    /**
     * Creates a UserId from a string value.
     * @param value the user identifier value
     * @return a new UserId instance
     */
    @NonNull
    public static UserId of(@NonNull final String value) {
        return new UserId(value);
    }

    /**
     * Creates an anonymous user ID for unknown users.
     * @return a new anonymous UserId instance
     */
    @NonNull
    public static UserId anonymous() {
        return new UserId("anonymous-" + UUID.randomUUID().toString());
    }

    /**
     * Creates a user ID from an email address.
     * @param email the email address
     * @return a new UserId instance based on the email
     */
    @NonNull
    public static UserId fromEmail(@NonNull final String email) {
        return new UserId(email.toLowerCase().trim());
    }

    /**
     * Creates a user ID from Git configuration.
     * Prefers email over username for better identification.
     * @param gitUser the Git username
     * @param gitEmail the Git email (can be null)
     * @return a new UserId instance based on Git config
     */
    @NonNull
    public static UserId fromGit(@NonNull final String gitUser, @Nullable final String gitEmail) {
        if (gitEmail != null && !gitEmail.trim().isEmpty()) {
            return fromEmail(gitEmail);
        }
        return new UserId(gitUser.trim());
    }

    /**
     * Creates a user ID from system information.
     * @param systemUser the system username
     * @param hostname the hostname (can be null)
     * @return a new UserId instance based on system info
     */
    @NonNull
    public static UserId fromSystem(@NonNull final String systemUser, @Nullable final String hostname) {
        if (hostname != null && !hostname.trim().isEmpty()) {
            return new UserId(systemUser.trim() + "@" + hostname.trim());
        }
        return new UserId(systemUser.trim());
    }

    /**
     * Auto-discovers a user ID from available sources.
     * Uses UserIdentificationStrategy to find the best available identifier.
     * @return a new UserId instance from auto-discovery
     */
    @NonNull
    public static UserId autoDiscover() {
        return UserIdentificationStrategy.getInstance().identifyUser();
    }

    /**
     * Checks if this represents an anonymous user.
     * @return true if this is an anonymous user ID
     */
    public boolean isAnonymous() {
        return getValue().startsWith("anonymous-");
    }

    /**
     * Checks if this appears to be an email-based user ID.
     * @return true if the value contains an @ symbol
     */
    public boolean isEmailBased() {
        return getValue().contains("@") && !getValue().startsWith("anonymous-");
    }

    /**
     * Gets a human-readable display name for the user.
     * Extracts meaningful names from email addresses or returns the raw value.
     * @return a display-friendly name for the user
     */
    @NonNull
    public String getDisplayName() {
        if (isAnonymous()) {
            return "Anonymous User";
        }

        String value = getValue();
        
        // Extract name from email addresses
        if (isEmailBased()) {
            String localPart = value.substring(0, value.indexOf("@"));
            // Convert common patterns like "john.doe" to "John Doe"
            String cleaned = localPart.replace(".", " ")
                          .replace("_", " ")
                          .replace("-", " ")
                          .toLowerCase();
            // Capitalize first letter of each word
            StringBuilder result = new StringBuilder();
            boolean capitalizeNext = true;
            for (char c : cleaned.toCharArray()) {
                if (Character.isWhitespace(c)) {
                    capitalizeNext = true;
                    result.append(c);
                } else if (capitalizeNext) {
                    result.append(Character.toUpperCase(c));
                    capitalizeNext = false;
                } else {
                    result.append(c);
                }
            }
            return result.toString();
        }

        // For non-email IDs, return as-is but capitalize first letter
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }

    /**
     * Gets the domain part of an email-based user ID.
     * @return the domain part or empty string if not email-based
     */
    @NonNull
    public String getDomain() {
        if (isEmailBased() && !isAnonymous()) {
            return getValue().substring(getValue().indexOf("@") + 1);
        }
        return "";
    }

    /**
     * Gets the local part of an email-based user ID.
     * @return the local part or the full value if not email-based
     */
    @NonNull
    public String getLocalPart() {
        if (isEmailBased() && !isAnonymous()) {
            return getValue().substring(0, getValue().indexOf("@"));
        }
        return getValue();
    }

    /**
     * Checks if this user ID represents the same user as another.
     * Performs case-insensitive comparison for email-based IDs.
     * @param other the other UserId to compare
     * @return true if they represent the same user
     */
    public boolean isSameUser(@Nullable final UserId other) {
        if (other == null) {
            return false;
        }
        
        // Anonymous users are never the same
        if (this.isAnonymous() || other.isAnonymous()) {
            return false;
        }
        
        // Case-insensitive comparison for email-based IDs
        if (this.isEmailBased() && other.isEmailBased()) {
            return this.getValue().equalsIgnoreCase(other.getValue());
        }
        
        // Exact comparison for other types
        return this.getValue().equals(other.getValue());
    }
}