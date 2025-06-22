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
 * Filename: UserManagementTest.java
 *
 * Author: Claude Code
 *
 * Class name: UserManagementTest
 *
 * Responsibilities:
 *   - Test user management functionality and value objects
 *   - Verify user identification, auto-discovery, and context management
 *   - Validate UserId creation patterns and validation logic
 *   - Test user context propagation and thread safety
 *
 * Collaborators:
 *   - UserId: User identifier with auto-discovery capabilities  
 *   - UserContextResolver: Thread-safe user context management
 *   - UserIdentificationStrategy: Auto-discovery implementation
 */
package org.acmsl.bytehot.testing;

import org.acmsl.bytehot.domain.UserId;
import org.acmsl.bytehot.domain.UserContextResolver;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for user management functionality.
 * Tests user identification, auto-discovery, and context management.
 * @author Claude Code
 * @since 2025-06-19
 */
@DisplayName("User Management - Integration Tests")
class UserManagementTest {

    @AfterEach
    void clearUserContext() {
        // Clean up user context after each test
        UserContextResolver.clearCurrentUser();
    }

    @Test
    @DisplayName("Should handle user context resolution")
    void shouldHandleUserContextResolution() {
        // Test user context management
        UserId testUser = UserId.of("context-test@example.com");
        
        // Set user context
        UserContextResolver.setCurrentUser(testUser);
        
        // Verify context is set
        UserId currentUser = UserContextResolver.getCurrentUser();
        assertEquals(testUser, currentUser, "User context should be preserved");
        
        // Test context clearing
        UserContextResolver.clearCurrentUser();
        
        // Verify auto-discovery works when no context is set
        UserId autoDiscovered = UserContextResolver.getCurrentUser();
        assertNotNull(autoDiscovered, "Auto-discovery should provide a user");
    }

    @Test
    @DisplayName("Should support UserId auto-discovery features")
    void shouldSupportUserIdAutoDiscovery() {
        // Test different UserId creation methods
        UserId emailUser = UserId.fromEmail("test@example.com");
        assertTrue(emailUser.isEmailBased(), "Email-based user should be detected");
        assertFalse(emailUser.isAnonymous(), "Email user should not be anonymous");
        assertEquals("Test", emailUser.getDisplayName(), "Display name should be extracted from email");

        UserId gitUser = UserId.fromGit("johndoe", "john.doe@company.com");
        assertTrue(gitUser.isEmailBased(), "Git user with email should be email-based");

        UserId systemUser = UserId.fromSystem("developer", "laptop.local");
        assertEquals("developer@laptop.local", systemUser.getValue(), "System user should include hostname");

        UserId anonymousUser = UserId.anonymous();
        assertTrue(anonymousUser.isAnonymous(), "Anonymous user should be detected");
        assertEquals("Anonymous User", anonymousUser.getDisplayName(), "Anonymous display name should be correct");
    }

    @Test
    @DisplayName("Should support user identification and comparison")
    void shouldSupportUserIdentificationAndComparison() {
        UserId user1 = UserId.fromEmail("John.Doe@Example.COM");
        UserId user2 = UserId.fromEmail("john.doe@example.com");
        
        // Should recognize same users despite case differences
        assertTrue(user1.isSameUser(user2), "Email-based users should be case-insensitive");
        
        UserId anonymousUser1 = UserId.anonymous();
        UserId anonymousUser2 = UserId.anonymous();
        
        // Anonymous users should never be considered the same
        assertFalse(anonymousUser1.isSameUser(anonymousUser2), "Anonymous users should never be the same");
        
        // Test domain and local part extraction
        UserId emailUser = UserId.fromEmail("alice@company.org");
        assertEquals("company.org", emailUser.getDomain(), "Domain should be extracted correctly");
        assertEquals("alice", emailUser.getLocalPart(), "Local part should be extracted correctly");
    }

    @Test
    @DisplayName("Should support user context propagation")
    void shouldSupportUserContextPropagation() {
        UserId originalUser = UserId.of("original@example.com");
        UserId temporaryUser = UserId.of("temp@example.com");
        
        // Set initial context
        UserContextResolver.setCurrentUser(originalUser);
        
        // Test context propagation with supplier
        String result = UserContextResolver.withUser(temporaryUser, () -> {
            UserId currentUser = UserContextResolver.getCurrentUser();
            assertEquals(temporaryUser, currentUser, "Context should be switched within scope");
            return "operation-completed";
        });
        
        assertEquals("operation-completed", result, "Operation should complete successfully");
        
        // Verify original context is restored
        UserId restoredUser = UserContextResolver.getCurrentUser();
        assertEquals(originalUser, restoredUser, "Original context should be restored");
    }

    @Test
    @DisplayName("Should support auto-discovery from multiple sources")
    void shouldSupportAutoDiscoveryFromMultipleSources() {
        // Test auto-discovery functionality
        UserId autoDiscovered = UserId.autoDiscover();
        assertNotNull(autoDiscovered, "Auto-discovery should always return a user");
        assertNotNull(autoDiscovered.getValue(), "Auto-discovered user should have a value");
        assertFalse(autoDiscovered.getValue().isEmpty(), "Auto-discovered user value should not be empty");
    }

    @Test
    @DisplayName("Should support user display name formatting")
    void shouldSupportUserDisplayNameFormatting() {
        // Test various display name patterns
        UserId dotSeparated = UserId.fromEmail("john.doe@example.com");
        assertEquals("John Doe", dotSeparated.getDisplayName(), "Dot-separated email should format correctly");
        
        UserId underscoreSeparated = UserId.fromEmail("jane_smith@company.org");
        assertEquals("Jane Smith", underscoreSeparated.getDisplayName(), "Underscore-separated email should format correctly");
        
        UserId hyphenSeparated = UserId.fromEmail("bob-wilson@test.com");
        assertEquals("Bob Wilson", hyphenSeparated.getDisplayName(), "Hyphen-separated email should format correctly");
        
        UserId mixedCase = UserId.fromEmail("ALICE.brown@example.net");
        assertEquals("Alice Brown", mixedCase.getDisplayName(), "Mixed case email should format correctly");
    }

    @Test
    @DisplayName("Should handle context management edge cases")
    void shouldHandleContextManagementEdgeCases() {
        // Test nested context management
        UserId user1 = UserId.of("user1@example.com");
        UserId user2 = UserId.of("user2@example.com");
        
        UserContextResolver.setCurrentUser(user1);
        
        // Test nested context changes
        UserContextResolver.withUser(user2, () -> {
            assertEquals(user2, UserContextResolver.getCurrentUser(), "Inner context should be set");
            
            // Test another nested level
            UserContextResolver.withUser(user1, () -> {
                assertEquals(user1, UserContextResolver.getCurrentUser(), "Nested context should be set");
            });
            
            // Verify previous context is restored
            assertEquals(user2, UserContextResolver.getCurrentUser(), "Intermediate context should be restored");
        });
        
        // Verify original context is restored
        assertEquals(user1, UserContextResolver.getCurrentUser(), "Original context should be restored");
    }
}