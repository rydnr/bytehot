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

/**
 * User profile value object with immutable operations
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
     * The user identifier
     */
    private final UserId userId;

    /**
     * Full name of the user
     */
    private final String fullName;

    /**
     * Email address of the user
     */
    private final String email;

    /**
     * URL to user's avatar image
     */
    private final String avatarUrl;

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
            .build();
    }
}