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
 * Filename: UserProfile.java
 *
 * Author: Claude Code
 *
 * Class name: UserProfile
 *
 * Responsibilities:
 *   - Represent user profile information as immutable value object
 *   - Provide factory methods for different profile creation scenarios
 *   - Support profile updates through immutable operations
 *
 * Collaborators:
 *   - UserId: User identifier
 */
package org.acmsl.bytehot.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.regex.Pattern;
import java.util.Optional;

/**
 * User profile domain object encapsulating user identity and behavior.
 * Provides validation, preferences management, and notification capabilities.
 * @author Claude Code
 * @since 2025-06-18
 */
@RequiredArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
@Getter
public class UserProfile {

    /**
     * Email validation pattern
     */
    protected static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    /**
     * The user identifier
     */
    protected final UserId userId;

    /**
     * Full name of the user
     */
    protected final String fullName;

    /**
     * Email address of the user
     */
    protected final String email;

    /**
     * URL to user's avatar image
     */
    protected final String avatarUrl;

    /**
     * User preferences for development notifications
     */
    @Builder.Default
    protected final UserPreferences preferences = UserPreferences.defaultPreferences();

    /**
     * Creates a default profile for a user
     * @param userId the user identifier
     * @return default user profile
     */
    public static UserProfile defaultProfile(final UserId userId) {
        return UserProfile.builder()
            .userId(userId)
            .fullName(userId.getDisplayName())
            .email(null)
            .avatarUrl(null)
            .build();
    }

    /**
     * Creates a profile from Git configuration
     * @param gitUser the Git username
     * @param gitEmail the Git email
     * @return user profile from Git config
     */
    public static UserProfile fromGitConfig(final String gitUser, final String gitEmail) {
        final UserId userId = UserId.fromGit(gitUser, gitEmail);
        return UserProfile.builder()
            .userId(userId)
            .fullName(userId.getDisplayName())
            .email(gitEmail)
            .avatarUrl(null)
            .build();
    }

    /**
     * Gets the display name for this user
     * @return display name
     */
    public String getDisplayName() {
        return userId.getDisplayName();
    }

    /**
     * Creates a copy with updated full name
     * @param newFullName the new full name
     * @return updated profile
     */
    public UserProfile withFullName(final String newFullName) {
        return UserProfile.builder()
            .userId(this.userId)
            .fullName(newFullName)
            .email(this.email)
            .avatarUrl(this.avatarUrl)
            .preferences(this.preferences)
            .build();
    }

    /**
     * Creates a copy with updated email
     * @param newEmail the new email
     * @return updated profile
     */
    public UserProfile withEmail(final String newEmail) {
        return UserProfile.builder()
            .userId(this.userId)
            .fullName(this.fullName)
            .email(newEmail)
            .avatarUrl(this.avatarUrl)
            .preferences(this.preferences)
            .build();
    }

    /**
     * Creates a copy with updated avatar URL
     * @param newAvatarUrl the new avatar URL
     * @return updated profile
     */
    public UserProfile withAvatarUrl(final String newAvatarUrl) {
        return UserProfile.builder()
            .userId(this.userId)
            .fullName(this.fullName)
            .email(this.email)
            .avatarUrl(newAvatarUrl)
            .preferences(this.preferences)
            .build();
    }

    /**
     * Updates user preferences
     * @param newPreferences the new preferences
     * @return updated profile
     */
    public UserProfile withPreferences(final UserPreferences newPreferences) {
        return UserProfile.builder()
            .userId(this.userId)
            .fullName(this.fullName)
            .email(this.email)
            .avatarUrl(this.avatarUrl)
            .preferences(newPreferences)
            .build();
    }

    /**
     * Validates if the user profile has a valid email address
     * @return true if email is valid, false otherwise
     */
    public boolean hasValidEmail() {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Checks if the user profile is complete (has all required information)
     * @return true if profile is complete
     */
    public boolean isComplete() {
        return fullName != null && !fullName.trim().isEmpty() && hasValidEmail();
    }

    /**
     * Gets the user's first name from full name
     * @return first name or full name if no space separator
     */
    public String getFirstName() {
        if (fullName == null || fullName.trim().isEmpty()) {
            return userId.getDisplayName();
        }
        int spaceIndex = fullName.indexOf(' ');
        return spaceIndex > 0 ? fullName.substring(0, spaceIndex) : fullName;
    }

    /**
     * Gets the user's last name from full name
     * @return last name or empty optional if no space separator
     */
    public Optional<String> getLastName() {
        if (fullName == null || fullName.trim().isEmpty()) {
            return Optional.empty();
        }
        int spaceIndex = fullName.lastIndexOf(' ');
        return spaceIndex > 0 ? Optional.of(fullName.substring(spaceIndex + 1)) : Optional.empty();
    }

    /**
     * Determines if user should receive hot-swap notifications
     * @return true if user wants hot-swap notifications
     */
    public boolean wantsHotSwapNotifications() {
        return preferences.getBoolean("notifications.hotswap", true);
    }

    /**
     * Determines if user should receive error notifications
     * @return true if user wants error notifications
     */
    public boolean wantsErrorNotifications() {
        return preferences.getBoolean("notifications.errors", true);
    }

    /**
     * Gets the preferred notification format for this user
     * @return notification format preference
     */
    public String getPreferredNotificationFormat() {
        return preferences.getString("notifications.format", "console");
    }

    /**
     * Checks if this user prefers verbose output
     * @return true if user prefers verbose output
     */
    public boolean prefersVerboseOutput() {
        return preferences.getBoolean("output.verbose", false);
    }

    /**
     * Gets the user's email domain
     * @return email domain or empty optional if no valid email
     */
    public Optional<String> getEmailDomain() {
        if (!hasValidEmail()) {
            return Optional.empty();
        }
        int atIndex = email.lastIndexOf('@');
        return Optional.of(email.substring(atIndex + 1));
    }

    /**
     * Determines if this is a corporate/organizational user based on email domain
     * @return true if user appears to be from an organization
     */
    public boolean isCorporateUser() {
        return getEmailDomain()
            .map(domain -> !domain.matches(".*\\.(gmail|yahoo|hotmail|outlook|icloud)\\..*"))
            .orElse(false);
    }

    /**
     * Creates a personalized greeting for the user
     * @return personalized greeting message
     */
    public String createGreeting() {
        String name = getFirstName();
        if (isCorporateUser()) {
            return String.format("Welcome to ByteHot, %s! Your corporate environment is ready for hot-swapping.", name);
        } else {
            return String.format("Hi %s! ByteHot is ready to accelerate your development workflow.", name);
        }
    }

    /**
     * Determines the appropriate log level for this user
     * @return log level based on user preferences and profile
     */
    public String getPreferredLogLevel() {
        if (prefersVerboseOutput()) {
            return "DEBUG";
        } else if (isCorporateUser()) {
            return "INFO";
        } else {
            return "WARN";
        }
    }
}