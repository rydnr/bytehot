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
 * Filename: JsonMarshallerFactory.java
 *
 * Author: Claude (Anthropic AI)
 *
 * Class name: JsonMarshallerFactory
 *
 * Responsibilities:
 *   - Create JsonMarshaller instances with appropriate configurations
 *   - Provide factory methods for common use cases
 *   - Abstract the creation complexity from clients
 *   - Enable easy switching between implementations
 *
 * Collaborators:
 *   - JsonMarshaller: Creates instances of this interface
 *   - JsonSerializationConfig: Uses for configuration
 */
package org.acmsl.commons.patterns.serialization;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Factory for creating JsonMarshaller instances with various configurations.
 * Provides convenient methods for common serialization scenarios.
 * @author Claude (Anthropic AI)
 * @since 2025-06-26
 */
public final class JsonMarshallerFactory {

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private JsonMarshallerFactory() {
        // Utility class
    }

    /**
     * Creates a JsonMarshaller with default configuration.
     * @return JsonMarshaller with default settings
     */
    @NonNull
    public static JsonMarshaller createDefault() {
        return create(JsonSerializationConfig.defaultConfig());
    }

    /**
     * Creates a JsonMarshaller with the specified configuration.
     * @param config the configuration to use
     * @return JsonMarshaller with the given configuration
     * @throws IllegalArgumentException if config is null
     */
    @NonNull
    public static JsonMarshaller create(@NonNull final JsonSerializationConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("Configuration cannot be null");
        }
        
        return new JacksonJsonMarshaller(config);
    }

    /**
     * Creates a JsonMarshaller optimized for EventSourcing scenarios.
     * Includes type information and preserves field order for reproducibility.
     * @return JsonMarshaller optimized for EventSourcing
     */
    @NonNull
    public static JsonMarshaller createForEventSourcing() {
        return create(JsonSerializationConfig.eventSourcingConfig());
    }

    /**
     * Creates a JsonMarshaller optimized for bug report generation.
     * Includes comprehensive information for debugging purposes.
     * @return JsonMarshaller optimized for bug reports
     */
    @NonNull
    public static JsonMarshaller createForBugReports() {
        return create(JsonSerializationConfig.bugReportConfig());
    }

    /**
     * Creates a JsonMarshaller with compact output configuration.
     * Minimizes JSON size by excluding nulls and empty values.
     * @return JsonMarshaller with compact configuration
     */
    @NonNull
    public static JsonMarshaller createCompact() {
        return create(JsonSerializationConfig.compactConfig());
    }

    /**
     * Creates a JsonMarshaller with pretty-print configuration.
     * Useful for debugging and human-readable output.
     * @return JsonMarshaller with pretty-print configuration
     */
    @NonNull
    public static JsonMarshaller createPrettyPrint() {
        return create(JsonSerializationConfig.prettyPrintConfig());
    }

    /**
     * Creates a JsonMarshaller with strict configuration.
     * Fails on unknown properties and empty beans for data integrity.
     * @return JsonMarshaller with strict configuration
     */
    @NonNull
    public static JsonMarshaller createStrict() {
        return create(JsonSerializationConfig.strictConfig());
    }
}