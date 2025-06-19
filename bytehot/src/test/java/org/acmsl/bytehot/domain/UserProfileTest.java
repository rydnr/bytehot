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
 * Filename: UserProfileTest.java
 *
 * Author: Claude Code
 *
 * Class name: UserProfileTest
 *
 * Responsibilities:
 *   - Test UserProfile value object functionality
 *   - Validate profile creation and default behavior
 *   - Ensure proper immutability and value equality
 *
 * Collaborators:
 *   - UserProfile: Value object under test
 *   - UserId: User identifier
 */
package org.acmsl.bytehot.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/**
 * Tests for UserProfile value object
 * @author Claude Code
 * @since 2025-06-18
 */
public class UserProfileTest {

    @Test
    public void shouldCreateDefaultProfile() {
        // Given
        final UserId userId = UserId.of("john.doe@example.com");
        
        // When
        final UserProfile profile = UserProfile.defaultProfile(userId);
        
        // Then
        assertNotNull(profile);
        assertEquals(userId, profile.getUserId());
        assertEquals("John Doe", profile.getDisplayName());
        assertNotNull(profile.getFullName());
        assertNull(profile.getEmail());
        assertNull(profile.getAvatarUrl());
    }

    @Test
    public void shouldCreateProfileWithDetails() {
        // Given
        final UserId userId = UserId.of("jane@example.com");
        final String fullName = "Jane Smith";
        final String email = "jane.smith@company.com";
        final String avatarUrl = "https://example.com/avatar.jpg";
        
        // When
        final UserProfile profile = UserProfile.builder()
            .userId(userId)
            .fullName(fullName)
            .email(email)
            .avatarUrl(avatarUrl)
            .build();
        
        // Then
        assertEquals(userId, profile.getUserId());
        assertEquals("Jane", profile.getDisplayName());
        assertEquals(fullName, profile.getFullName());
        assertEquals(email, profile.getEmail());
        assertEquals(avatarUrl, profile.getAvatarUrl());
    }

    @Test
    public void shouldExtractDisplayNameFromUserId() {
        // Given
        final UserId emailUser = UserId.of("john.doe@example.com");
        final UserId simpleUser = UserId.of("johndoe");
        final UserId anonymousUser = UserId.anonymous();
        
        // When
        final UserProfile emailProfile = UserProfile.defaultProfile(emailUser);
        final UserProfile simpleProfile = UserProfile.defaultProfile(simpleUser);
        final UserProfile anonymousProfile = UserProfile.defaultProfile(anonymousUser);
        
        // Then
        assertEquals("John Doe", emailProfile.getDisplayName());
        assertEquals("Johndoe", simpleProfile.getDisplayName());
        assertEquals("Anonymous User", anonymousProfile.getDisplayName());
    }

    @Test
    public void shouldUpdateProfile() {
        // Given
        final UserId userId = UserId.of("user@example.com");
        final UserProfile originalProfile = UserProfile.defaultProfile(userId);
        
        // When
        final UserProfile updatedProfile = originalProfile.withFullName("Updated Name");
        final UserProfile updatedEmailProfile = updatedProfile.withEmail("new@example.com");
        
        // Then
        assertNotEquals(originalProfile, updatedProfile);
        assertEquals("Updated Name", updatedProfile.getFullName());
        assertNull(updatedProfile.getEmail());
        
        assertEquals("new@example.com", updatedEmailProfile.getEmail());
        assertEquals("Updated Name", updatedEmailProfile.getFullName());
    }

    @Test
    public void shouldCreateFromGitConfig() {
        // Given
        final String gitUser = "johndoe";
        final String gitEmail = "john.doe@company.com";
        
        // When
        final UserProfile profile = UserProfile.fromGitConfig(gitUser, gitEmail);
        
        // Then
        assertEquals(UserId.fromGit(gitUser, gitEmail), profile.getUserId());
        assertEquals(gitEmail, profile.getEmail());
        assertEquals("John Doe", profile.getDisplayName());
    }

    @Test
    public void shouldHaveValueEquality() {
        // Given
        final UserId userId = UserId.of("same@example.com");
        final UserProfile profile1 = UserProfile.defaultProfile(userId);
        final UserProfile profile2 = UserProfile.defaultProfile(userId);
        final UserProfile profile3 = UserProfile.defaultProfile(UserId.of("different@example.com"));
        
        // When & Then
        assertEquals(profile1, profile2);
        assertEquals(profile1.hashCode(), profile2.hashCode());
        assertNotEquals(profile1, profile3);
    }

    @Test
    public void shouldBeImmutable() {
        // Given
        final UserId userId = UserId.of("user@example.com");
        final UserProfile originalProfile = UserProfile.defaultProfile(userId);
        
        // When
        final UserProfile modifiedProfile = originalProfile.withFullName("New Name");
        
        // Then
        assertNotEquals(originalProfile, modifiedProfile);
        assertNotNull(originalProfile.getFullName());
        assertNotEquals("New Name", originalProfile.getFullName());
        assertEquals("New Name", modifiedProfile.getFullName());
    }
}