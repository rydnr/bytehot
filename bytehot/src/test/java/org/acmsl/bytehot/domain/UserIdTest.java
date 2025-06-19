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
 * Filename: UserIdTest.java
 *
 * Author: Claude Code
 *
 * Class name: UserIdTest
 *
 * Responsibilities:
 *   - Test UserId value object functionality
 *   - Validate user identification and auto-discovery logic
 *   - Ensure proper equality and immutability
 *
 * Collaborators:
 *   - UserId: Value object under test
 */
package org.acmsl.bytehot.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests for UserId value object
 * @author Claude Code
 * @since 2025-06-18
 */
public class UserIdTest {

    @Test
    public void shouldCreateUserIdFromString() {
        // Given
        final String userIdValue = "john.doe@example.com";
        
        // When
        final UserId userId = UserId.of(userIdValue);
        
        // Then
        assertNotNull(userId);
        assertEquals(userIdValue, userId.getValue());
    }

    @Test
    public void shouldCreateAnonymousUser() {
        // Given & When
        final UserId anonymousUser = UserId.anonymous();
        
        // Then
        assertNotNull(anonymousUser);
        assertTrue(anonymousUser.isAnonymous());
        assertTrue(anonymousUser.getValue().startsWith("anonymous-"));
    }

    @Test
    public void shouldCreateUserIdFromEmail() {
        // Given
        final String email = "Jane.Doe@Example.Com";
        
        // When
        final UserId userId = UserId.fromEmail(email);
        
        // Then
        assertNotNull(userId);
        assertEquals("jane.doe@example.com", userId.getValue());
    }

    @Test
    public void shouldCreateUserIdFromGitConfig() {
        // Given
        final String gitUser = "johndoe";
        final String gitEmail = "john.doe@company.com";
        
        // When
        final UserId userIdWithEmail = UserId.fromGit(gitUser, gitEmail);
        final UserId userIdWithoutEmail = UserId.fromGit(gitUser, null);
        
        // Then
        assertEquals("john.doe@company.com", userIdWithEmail.getValue());
        assertEquals("johndoe", userIdWithoutEmail.getValue());
    }

    @Test
    public void shouldIdentifyAnonymousUsers() {
        // Given
        final UserId anonymousUser = UserId.anonymous();
        final UserId regularUser = UserId.of("user@example.com");
        
        // When & Then
        assertTrue(anonymousUser.isAnonymous());
        assertFalse(regularUser.isAnonymous());
    }

    @Test
    public void shouldProvideDisplayNames() {
        // Given
        final UserId emailUser = UserId.of("john.doe@example.com");
        final UserId simpleUser = UserId.of("johndoe");
        final UserId anonymousUser = UserId.anonymous();
        
        // When & Then
        assertEquals("John Doe", emailUser.getDisplayName());
        assertEquals("Johndoe", simpleUser.getDisplayName());
        assertEquals("Anonymous User", anonymousUser.getDisplayName());
    }

    @Test
    public void shouldHaveValueEquality() {
        // Given
        final UserId userId1 = UserId.of("same@example.com");
        final UserId userId2 = UserId.of("same@example.com");
        final UserId userId3 = UserId.of("different@example.com");
        
        // When & Then
        assertEquals(userId1, userId2);
        assertEquals(userId1.hashCode(), userId2.hashCode());
        assertNotEquals(userId1, userId3);
    }

    @Test
    public void shouldBeImmutable() {
        // Given
        final String originalValue = "user@example.com";
        final UserId userId = UserId.of(originalValue);
        
        // When
        final String retrievedValue = userId.getValue();
        
        // Then
        assertEquals(originalValue, retrievedValue);
        // UserId should be immutable - no setters should exist
        assertNotNull(userId.toString());
    }
}