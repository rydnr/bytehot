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
 * Filename: UserPreferencesTest.java
 *
 * Author: Claude Code
 *
 * Class name: UserPreferencesTest
 *
 * Responsibilities:
 *   - Test UserPreferences value object functionality
 *   - Validate preference storage and retrieval
 *   - Ensure immutable update operations
 *
 * Collaborators:
 *   - UserPreferences: Value object under test
 */
package org.acmsl.bytehot.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests for UserPreferences value object
 * @author Claude Code
 * @since 2025-06-18
 */
public class UserPreferencesTest {

    @Test
    public void shouldCreateDefaultPreferences() {
        // Given & When
        final UserPreferences preferences = UserPreferences.defaults();
        
        // Then
        assertNotNull(preferences);
        assertTrue(preferences.getBoolean("notification.enabled"));
        assertTrue(preferences.getBoolean("analytics.enabled"));
        assertTrue(preferences.getBoolean("hotswap.autoRetry"));
        assertEquals("INFO", preferences.getString("logging.level"));
    }

    @Test
    public void shouldUpdatePreferences() {
        // Given
        final UserPreferences originalPreferences = UserPreferences.defaults();
        
        // When
        final UserPreferences updatedPreferences = originalPreferences.update("notification.enabled", false);
        
        // Then
        assertNotEquals(originalPreferences, updatedPreferences);
        assertTrue(originalPreferences.getBoolean("notification.enabled"));
        assertFalse(updatedPreferences.getBoolean("notification.enabled"));
    }

    @Test
    public void shouldGetTypedValues() {
        // Given
        final UserPreferences preferences = UserPreferences.defaults()
            .update("timeout.seconds", 30)
            .update("debug.enabled", true)
            .update("theme.name", "dark");
        
        // When & Then
        assertEquals(Integer.valueOf(30), preferences.getInteger("timeout.seconds"));
        assertTrue(preferences.getBoolean("debug.enabled"));
        assertEquals("dark", preferences.getString("theme.name"));
    }

    @Test
    public void shouldHandleMultipleUpdates() {
        // Given
        final UserPreferences preferences = UserPreferences.defaults();
        
        // When
        final UserPreferences updated = preferences
            .update("notification.enabled", false)
            .update("analytics.enabled", false)
            .update("logging.level", "DEBUG");
        
        // Then
        assertFalse(updated.getBoolean("notification.enabled"));
        assertFalse(updated.getBoolean("analytics.enabled"));
        assertEquals("DEBUG", updated.getString("logging.level"));
        assertTrue(updated.getBoolean("hotswap.autoRetry")); // unchanged
    }

    @Test
    public void shouldHaveValueEquality() {
        // Given
        final UserPreferences preferences1 = UserPreferences.defaults();
        final UserPreferences preferences2 = UserPreferences.defaults();
        final UserPreferences preferences3 = preferences1.update("test", "value");
        
        // When & Then
        assertEquals(preferences1, preferences2);
        assertEquals(preferences1.hashCode(), preferences2.hashCode());
        assertNotEquals(preferences1, preferences3);
    }

    @Test
    public void shouldBeImmutable() {
        // Given
        final UserPreferences originalPreferences = UserPreferences.defaults();
        
        // When
        final UserPreferences modifiedPreferences = originalPreferences.update("new.setting", "value");
        
        // Then
        assertNotEquals(originalPreferences, modifiedPreferences);
        assertFalse(originalPreferences.getString("new.setting") != null);
        assertEquals("value", modifiedPreferences.getString("new.setting"));
    }

    @Test
    public void shouldSupportGenericGet() {
        // Given
        final UserPreferences preferences = UserPreferences.defaults()
            .update("custom.timeout", 5000L)
            .update("custom.enabled", true);
        
        // When & Then
        assertEquals(Long.valueOf(5000L), preferences.get("custom.timeout", Long.class));
        assertEquals(Boolean.TRUE, preferences.get("custom.enabled", Boolean.class));
        assertEquals("INFO", preferences.get("logging.level", String.class));
    }
}