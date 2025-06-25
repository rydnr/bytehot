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
import java.util.List;
import java.util.Set;
import java.util.Optional;

/**
 * User preferences domain object with validation, type safety, and behavioral logic.
 * Encapsulates preference management, defaults, and configuration behavior.
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
    protected final Map<String, Object> preferences;

    /**
     * Creates default user preferences
     * @return default preferences
     */
    public static UserPreferences defaultPreferences() {
        final Map<String, Object> defaultPrefs = new HashMap<>();
        
        // Notification preferences
        defaultPrefs.put("notification.enabled", true);
        defaultPrefs.put("notifications.hotswap", true);
        defaultPrefs.put("notifications.errors", true);
        defaultPrefs.put("notifications.format", "console");
        defaultPrefs.put("notifications.sound", false);
        
        // Output preferences
        defaultPrefs.put("output.verbose", false);
        defaultPrefs.put("output.colorized", true);
        defaultPrefs.put("output.timestamps", true);
        
        // Development preferences
        defaultPrefs.put("hotswap.autoRetry", true);
        defaultPrefs.put("hotswap.retryCount", 3);
        defaultPrefs.put("hotswap.timeout", 5000);
        
        // Logging preferences
        defaultPrefs.put("logging.level", "INFO");
        defaultPrefs.put("logging.includeStackTrace", false);
        
        // Analysis preferences
        defaultPrefs.put("analytics.enabled", true);
        defaultPrefs.put("analysis.autoAnalyze", true);
        defaultPrefs.put("analysis.confidenceThreshold", 0.7);
        
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

    /**
     * Gets a boolean preference with default fallback
     * @param key the preference key
     * @param defaultValue the default value if not found
     * @return the boolean value or default
     */
    public boolean getBoolean(final String key, final boolean defaultValue) {
        final Boolean value = get(key, Boolean.class);
        return value != null ? value : defaultValue;
    }

    /**
     * Gets a string preference with default fallback
     * @param key the preference key
     * @param defaultValue the default value if not found
     * @return the string value or default
     */
    public String getString(final String key, final String defaultValue) {
        final String value = get(key, String.class);
        return value != null ? value : defaultValue;
    }

    /**
     * Gets an integer preference with default fallback
     * @param key the preference key
     * @param defaultValue the default value if not found
     * @return the integer value or default
     */
    public int getInteger(final String key, final int defaultValue) {
        final Integer value = get(key, Integer.class);
        return value != null ? value : defaultValue;
    }

    /**
     * Validates if a preference key is valid for ByteHot configuration
     * @param key the preference key to validate
     * @return true if the key is valid
     */
    public boolean isValidPreferenceKey(final String key) {
        if (key == null || key.trim().isEmpty()) {
            return false;
        }
        
        Set<String> validPrefixes = Set.of(
            "notifications.", "output.", "hotswap.", "logging.", 
            "analysis.", "development.", "ui.", "performance."
        );
        
        return validPrefixes.stream().anyMatch(key::startsWith);
    }

    /**
     * Validates if a preference value is appropriate for the given key
     * @param key the preference key
     * @param value the preference value
     * @return validation result
     */
    public ValidationResult validatePreference(final String key, final Object value) {
        if (!isValidPreferenceKey(key)) {
            return ValidationResult.invalid("Invalid preference key: " + key);
        }
        
        if (value == null) {
            return ValidationResult.invalid("Preference value cannot be null");
        }
        
        // Type-specific validation
        if (key.contains("timeout") && value instanceof Number) {
            int timeout = ((Number) value).intValue();
            if (timeout < 100 || timeout > 60000) {
                return ValidationResult.invalid("Timeout must be between 100ms and 60000ms");
            }
        }
        
        if (key.contains("retryCount") && value instanceof Number) {
            int retries = ((Number) value).intValue();
            if (retries < 0 || retries > 10) {
                return ValidationResult.invalid("Retry count must be between 0 and 10");
            }
        }
        
        if (key.contains("confidenceThreshold") && value instanceof Number) {
            double threshold = ((Number) value).doubleValue();
            if (threshold < 0.0 || threshold > 1.0) {
                return ValidationResult.invalid("Confidence threshold must be between 0.0 and 1.0");
            }
        }
        
        if (key.contains("level") && value instanceof String) {
            String level = (String) value;
            Set<String> validLevels = Set.of("TRACE", "DEBUG", "INFO", "WARN", "ERROR");
            if (!validLevels.contains(level.toUpperCase())) {
                return ValidationResult.invalid("Invalid log level. Must be one of: " + validLevels);
            }
        }
        
        return ValidationResult.valid();
    }

    /**
     * Creates a copy with validated preference updates
     * @param key the preference key
     * @param value the preference value
     * @return new preferences with validated update
     * @throws IllegalArgumentException if validation fails
     */
    public UserPreferences updateValidated(final String key, final Object value) {
        ValidationResult validation = validatePreference(key, value);
        if (!validation.isValid()) {
            throw new IllegalArgumentException("Invalid preference: " + validation.getIssues().get(0));
        }
        
        return update(key, value);
    }

    /**
     * Bulk updates multiple preferences with validation
     * @param updates map of preference updates
     * @return new preferences with all updates applied
     * @throws IllegalArgumentException if any validation fails
     */
    public UserPreferences updateAll(final Map<String, Object> updates) {
        // Validate all updates first
        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            ValidationResult validation = validatePreference(entry.getKey(), entry.getValue());
            if (!validation.isValid()) {
                throw new IllegalArgumentException(
                    "Invalid preference " + entry.getKey() + ": " + validation.getIssues().get(0)
                );
            }
        }
        
        // Apply all updates
        Map<String, Object> newPreferences = new HashMap<>(this.preferences);
        newPreferences.putAll(updates);
        
        return UserPreferences.builder()
            .preferences(newPreferences)
            .build();
    }

    /**
     * Gets all preferences starting with a specific prefix
     * @param prefix the prefix to filter by
     * @return map of matching preferences
     */
    public Map<String, Object> getPreferencesWithPrefix(final String prefix) {
        return preferences.entrySet().stream()
            .filter(entry -> entry.getKey().startsWith(prefix))
            .collect(java.util.stream.Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue
            ));
    }

    /**
     * Determines if the user prefers development-optimized settings
     * @return true if development mode is preferred
     */
    public boolean isDevelopmentModePreferred() {
        return getBoolean("development.mode", false) 
            || getBoolean("output.verbose", false)
            || getString("logging.level", "INFO").equals("DEBUG");
    }

    /**
     * Determines if the user prefers production-optimized settings
     * @return true if production mode is preferred
     */
    public boolean isProductionModePreferred() {
        return !getBoolean("output.verbose", false)
            && !getBoolean("logging.includeStackTrace", false)
            && !getString("logging.level", "INFO").equals("DEBUG");
    }

    /**
     * Creates optimized preferences for development environment
     * @return preferences optimized for development
     */
    public UserPreferences optimizeForDevelopment() {
        Map<String, Object> devOptimizations = Map.of(
            "output.verbose", true,
            "output.colorized", true,
            "notifications.hotswap", true,
            "notifications.errors", true,
            "logging.level", "DEBUG",
            "logging.includeStackTrace", true,
            "hotswap.autoRetry", true,
            "analysis.autoAnalyze", true
        );
        
        return updateAll(devOptimizations);
    }

    /**
     * Creates optimized preferences for production environment
     * @return preferences optimized for production
     */
    public UserPreferences optimizeForProduction() {
        Map<String, Object> prodOptimizations = Map.of(
            "output.verbose", false,
            "notifications.sound", false,
            "logging.level", "WARN",
            "logging.includeStackTrace", false,
            "hotswap.retryCount", 1,
            "performance.optimization", true
        );
        
        return updateAll(prodOptimizations);
    }

    /**
     * Exports preferences to a flat map for serialization
     * @return flattened preference map
     */
    public Map<String, String> exportToStringMap() {
        return preferences.entrySet().stream()
            .collect(java.util.stream.Collectors.toMap(
                Map.Entry::getKey,
                entry -> String.valueOf(entry.getValue())
            ));
    }

    /**
     * Gets preference categories available in this configuration
     * @return set of available categories
     */
    public Set<String> getAvailableCategories() {
        return preferences.keySet().stream()
            .map(key -> key.contains(".") ? key.substring(0, key.indexOf(".")) : key)
            .collect(java.util.stream.Collectors.toSet());
    }

    /**
     * Generates a human-readable summary of current preferences
     * @return preference summary
     */
    public String generateSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("User Preferences Summary:\n");
        
        Set<String> categories = getAvailableCategories();
        for (String category : categories.stream().sorted().toList()) {
            summary.append(String.format("\n%s Configuration:\n", 
                category.substring(0, 1).toUpperCase() + category.substring(1)));
            
            Map<String, Object> categoryPrefs = getPreferencesWithPrefix(category + ".");
            for (Map.Entry<String, Object> entry : categoryPrefs.entrySet()) {
                String key = entry.getKey().substring(category.length() + 1);
                summary.append(String.format("  - %s: %s\n", key, entry.getValue()));
            }
        }
        
        return summary.toString();
    }

    /**
     * Simple validation result class
     */
    public static class ValidationResult {
        protected final boolean valid;
        protected final List<String> issues;

        protected ValidationResult(final boolean valid, final List<String> issues) {
            this.valid = valid;
            this.issues = List.copyOf(issues);
        }

        public static ValidationResult valid() {
            return new ValidationResult(true, List.of());
        }

        public static ValidationResult invalid(final String issue) {
            return new ValidationResult(false, List.of(issue));
        }

        public boolean isValid() { return valid; }
        public List<String> getIssues() { return issues; }
    }
}