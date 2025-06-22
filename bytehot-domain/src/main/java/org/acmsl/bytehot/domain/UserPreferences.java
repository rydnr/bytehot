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
 * Filename: UserPreferences.java
 *
 * Author: Claude Code
 *
 * Class name: UserPreferences
 *
 * Responsibilities:
 *   - Store user preferences and configuration settings
 *   - Provide type-safe accessors for common preference types
 *   - Support immutable preference updates
 *
 * Collaborators:
 *   - None (pure value object)
 */
package org.acmsl.bytehot.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * User preferences value object with type-safe operations
 * @author Claude Code
 * @since 2025-06-18
 */
@RequiredArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
@Getter
public class UserPreferences {

    /**
     * The preferences map
     */
    private final Map<String, Object> preferences;

    /**
     * Creates default user preferences
     * @return default preferences
     */
    public static UserPreferences defaults() {
        final Map<String, Object> defaultPrefs = new HashMap<>();
        defaultPrefs.put("notification.enabled", true);
        defaultPrefs.put("analytics.enabled", true);
        defaultPrefs.put("hotswap.autoRetry", true);
        defaultPrefs.put("logging.level", "INFO");
        
        return UserPreferences.builder()
            .preferences(defaultPrefs)
            .build();
    }

    /**
     * Updates a preference value, returning a new immutable instance
     * @param key the preference key
     * @param value the preference value
     * @return new preferences with updated value
     */
    public UserPreferences update(final String key, final Object value) {
        final Map<String, Object> newPreferences = new HashMap<>(this.preferences);
        newPreferences.put(key, value);
        return UserPreferences.builder()
            .preferences(newPreferences)
            .build();
    }

    /**
     * Gets a preference value with type casting
     * @param key the preference key
     * @param type the expected type
     * @param <T> the type parameter
     * @return the typed preference value
     */
    @SuppressWarnings("unchecked")
    public <T> T get(final String key, final Class<T> type) {
        final Object value = preferences.get(key);
        if (value == null) {
            return null;
        }
        return type.cast(value);
    }

    /**
     * Gets a boolean preference value
     * @param key the preference key
     * @return the boolean value
     */
    public boolean getBoolean(final String key) {
        final Boolean value = get(key, Boolean.class);
        return value != null ? value : false;
    }

    /**
     * Gets a string preference value
     * @param key the preference key
     * @return the string value
     */
    public String getString(final String key) {
        return get(key, String.class);
    }

    /**
     * Gets an integer preference value
     * @param key the preference key
     * @return the integer value
     */
    public Integer getInteger(final String key) {
        return get(key, Integer.class);
    }

    /**
     * Gets a long preference value
     * @param key the preference key
     * @return the long value
     */
    public Long getLong(final String key) {
        return get(key, Long.class);
    }
}