/*
                        ACM-SL Commons

    Copyright (C) 2002-today  Jose San Leandro Armendariz
                              chous@acm-sl.org

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the License, or any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-307  USA

    Thanks to ACM S.L. for distributing this library under the GPL license.
    Contact info: jose.sanleandro@acm-sl.com

 ******************************************************************************
 *
 * Filename: JsonSerializationConfig.java
 *
 * Author: Claude (Anthropic AI)
 *
 * Class name: JsonSerializationConfig
 *
 * Responsibilities:
 *   - Configure JSON serialization behavior
 *   - Support EventSourcing-specific serialization requirements
 *   - Enable customization of type handling and formatting
 *   - Provide preset configurations for common use cases
 *
 * Collaborators:
 *   - JsonMarshaller: Uses this configuration for serialization behavior
 */
package org.acmsl.commons.patterns.serialization;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.ZoneId;
import java.util.Map;
import java.util.Set;

/**
 * Configuration for JSON serialization behavior.
 * Provides fine-grained control over how objects are serialized to/from JSON.
 * @author Claude (Anthropic AI)
 * @since 2025-06-26
 */
@RequiredArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
@Getter
public class JsonSerializationConfig {

    /**
     * Whether to format JSON output with indentation for readability.
     */
    @Builder.Default
    private final boolean prettyPrint = false;

    /**
     * Whether to include null values in the JSON output.
     */
    @Builder.Default
    private final boolean includeNulls = false;

    /**
     * Whether to include empty collections and maps in the JSON output.
     */
    @Builder.Default
    private final boolean includeEmpty = true;

    /**
     * Whether to include type information for polymorphic deserialization.
     */
    @Builder.Default
    private final boolean includeTypeInfo = false;

    /**
     * Whether to fail on unknown properties during deserialization.
     */
    @Builder.Default
    private final boolean failOnUnknownProperties = false;

    /**
     * Whether to fail on empty beans during serialization.
     */
    @Builder.Default
    private final boolean failOnEmptyBeans = false;

    /**
     * The date/time format pattern to use for temporal types.
     */
    @Nullable
    private final String dateTimePattern;

    /**
     * The timezone to use for date/time serialization.
     */
    @Builder.Default
    @NonNull
    private final ZoneId timeZone = ZoneId.systemDefault();

    /**
     * Custom property names mapping (original -> custom).
     */
    @Nullable
    private final Map<String, String> propertyNameMapping;

    /**
     * Properties to exclude from serialization.
     */
    @Nullable
    private final Set<String> excludedProperties;

    /**
     * Properties to include in serialization (if specified, only these are included).
     */
    @Nullable
    private final Set<String> includedProperties;

    /**
     * Whether to preserve field order during serialization.
     */
    @Builder.Default
    private final boolean preserveFieldOrder = false;

    /**
     * Whether to use Java 8 time module for temporal types.
     */
    @Builder.Default
    private final boolean useJavaTimeModule = true;

    /**
     * Maximum depth for object serialization to prevent infinite recursion.
     */
    @Builder.Default
    private final int maxDepth = 50;

    /**
     * Whether to wrap root values in an object (useful for security).
     */
    @Builder.Default
    private final boolean wrapRootValue = false;

    /**
     * Creates a default configuration suitable for most use cases.
     * @return default configuration
     */
    @NonNull
    public static JsonSerializationConfig defaultConfig() {
        return JsonSerializationConfig.builder().build();
    }

    /**
     * Creates a configuration optimized for EventSourcing scenarios.
     * Includes type information and preserves field order for reproducibility.
     * @return EventSourcing-optimized configuration
     */
    @NonNull
    public static JsonSerializationConfig eventSourcingConfig() {
        return JsonSerializationConfig.builder()
            .includeTypeInfo(true)
            .preserveFieldOrder(true)
            .includeNulls(false)
            .failOnUnknownProperties(false)
            .useJavaTimeModule(true)
            .build();
    }

    /**
     * Creates a configuration for pretty-printed JSON output.
     * Useful for debugging and human-readable output.
     * @return pretty-print configuration
     */
    @NonNull
    public static JsonSerializationConfig prettyPrintConfig() {
        return JsonSerializationConfig.builder()
            .prettyPrint(true)
            .includeNulls(false)
            .build();
    }

    /**
     * Creates a configuration for compact JSON output.
     * Minimizes output size by excluding nulls and empty values.
     * @return compact configuration
     */
    @NonNull
    public static JsonSerializationConfig compactConfig() {
        return JsonSerializationConfig.builder()
            .prettyPrint(false)
            .includeNulls(false)
            .includeEmpty(false)
            .build();
    }

    /**
     * Creates a configuration for strict JSON processing.
     * Fails on unknown properties and empty beans for data integrity.
     * @return strict configuration
     */
    @NonNull
    public static JsonSerializationConfig strictConfig() {
        return JsonSerializationConfig.builder()
            .failOnUnknownProperties(true)
            .failOnEmptyBeans(true)
            .includeTypeInfo(true)
            .build();
    }

    /**
     * Creates a configuration for bug report serialization.
     * Optimized for capturing complete context with type information.
     * @return bug report configuration
     */
    @NonNull
    public static JsonSerializationConfig bugReportConfig() {
        return JsonSerializationConfig.builder()
            .prettyPrint(true)
            .includeTypeInfo(true)
            .includeNulls(true)
            .includeEmpty(true)
            .preserveFieldOrder(true)
            .failOnUnknownProperties(false)
            .useJavaTimeModule(true)
            .build();
    }

    /**
     * Creates a new configuration with pretty printing enabled.
     * @return new configuration with pretty printing
     */
    @NonNull
    public JsonSerializationConfig withPrettyPrint() {
        return toBuilder().prettyPrint(true).build();
    }

    /**
     * Creates a new configuration with type information enabled.
     * @return new configuration with type information
     */
    @NonNull
    public JsonSerializationConfig withTypeInfo() {
        return toBuilder().includeTypeInfo(true).build();
    }

    /**
     * Creates a new configuration with custom date/time pattern.
     * @param pattern the date/time pattern to use
     * @return new configuration with custom pattern
     */
    @NonNull
    public JsonSerializationConfig withDateTimePattern(@NonNull final String pattern) {
        return toBuilder().dateTimePattern(pattern).build();
    }

    /**
     * Creates a new configuration with custom timezone.
     * @param zoneId the timezone to use
     * @return new configuration with custom timezone
     */
    @NonNull
    public JsonSerializationConfig withTimeZone(@NonNull final ZoneId zoneId) {
        return toBuilder().timeZone(zoneId).build();
    }

    /**
     * Creates a new configuration excluding specific properties.
     * @param properties the properties to exclude
     * @return new configuration with excluded properties
     */
    @NonNull
    public JsonSerializationConfig withExcludedProperties(@NonNull final Set<String> properties) {
        return toBuilder().excludedProperties(properties).build();
    }

    /**
     * Creates a new configuration including only specific properties.
     * @param properties the properties to include
     * @return new configuration with included properties
     */
    @NonNull
    public JsonSerializationConfig withIncludedProperties(@NonNull final Set<String> properties) {
        return toBuilder().includedProperties(properties).build();
    }
}