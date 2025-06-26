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
 * Filename: JacksonJsonMarshaller.java
 *
 * Author: Claude (Anthropic AI)
 *
 * Class name: JacksonJsonMarshaller
 *
 * Responsibilities:
 *   - Implement JsonMarshaller using Jackson ObjectMapper
 *   - Configure Jackson based on JsonSerializationConfig
 *   - Handle all JSON marshalling/unmarshalling operations
 *   - Provide comprehensive error handling and context
 *
 * Collaborators:
 *   - ObjectMapper: Jackson's main serialization engine
 *   - JsonSerializationConfig: Configuration settings
 *   - SerializationException: Error handling
 */
package org.acmsl.commons.patterns.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Jackson-based implementation of JsonMarshaller interface.
 * Provides comprehensive JSON serialization capabilities with configurable behavior.
 * @author Claude (Anthropic AI)
 * @since 2025-06-26
 */
@EqualsAndHashCode
@ToString
public class JacksonJsonMarshaller implements JsonMarshaller {

    /**
     * The Jackson ObjectMapper configured for this marshaller.
     */
    @NonNull
    private final ObjectMapper objectMapper;

    /**
     * The configuration used by this marshaller.
     */
    @NonNull
    private final JsonSerializationConfig config;

    /**
     * Creates a new JacksonJsonMarshaller with the specified configuration.
     * @param config the configuration to use
     */
    public JacksonJsonMarshaller(@NonNull final JsonSerializationConfig config) {
        this.config = config;
        this.objectMapper = createConfiguredObjectMapper(config);
    }

    @Override
    @NonNull
    public String marshall(@Nullable final Object object) throws SerializationException {
        return marshall(object, config);
    }

    @Override
    @NonNull
    public String marshall(@Nullable final Object object, @NonNull final JsonSerializationConfig customConfig) 
            throws SerializationException {
        try {
            final ObjectMapper mapper = getMapperForConfig(customConfig);
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw SerializationException.forMarshalling(
                "Failed to marshall object to JSON", 
                e, 
                object != null ? object.getClass() : null
            );
        }
    }

    @Override
    @NonNull
    public byte[] marshallToBytes(@Nullable final Object object) throws SerializationException {
        return marshallToBytes(object, config);
    }

    @Override
    @NonNull
    public byte[] marshallToBytes(@Nullable final Object object, @NonNull final JsonSerializationConfig customConfig) 
            throws SerializationException {
        try {
            final ObjectMapper mapper = getMapperForConfig(customConfig);
            return mapper.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            throw SerializationException.forMarshalling(
                "Failed to marshall object to JSON bytes", 
                e, 
                object != null ? object.getClass() : null
            );
        }
    }

    @Override
    @Nullable
    public <T> T unmarshall(@NonNull final String json, @NonNull final Class<T> targetType) 
            throws SerializationException {
        return unmarshall(json, targetType, config);
    }

    @Override
    @Nullable
    public <T> T unmarshall(@NonNull final String json, @NonNull final Class<T> targetType, 
                           @NonNull final JsonSerializationConfig customConfig) throws SerializationException {
        try {
            final ObjectMapper mapper = getMapperForConfig(customConfig);
            return mapper.readValue(json, targetType);
        } catch (JsonProcessingException e) {
            throw SerializationException.forUnmarshalling(
                "Failed to unmarshall JSON to object", 
                e, 
                targetType, 
                json
            );
        }
    }

    @Override
    @Nullable
    public <T> T unmarshall(@NonNull final byte[] jsonBytes, @NonNull final Class<T> targetType) 
            throws SerializationException {
        return unmarshall(jsonBytes, targetType, config);
    }

    @Override
    @Nullable
    public <T> T unmarshall(@NonNull final byte[] jsonBytes, @NonNull final Class<T> targetType, 
                           @NonNull final JsonSerializationConfig customConfig) throws SerializationException {
        try {
            final ObjectMapper mapper = getMapperForConfig(customConfig);
            return mapper.readValue(jsonBytes, targetType);
        } catch (IOException e) {
            throw SerializationException.forUnmarshalling(
                "Failed to unmarshall JSON bytes to object", 
                e, 
                targetType, 
                new String(jsonBytes)
            );
        }
    }

    @Override
    @NonNull
    public <T> List<T> unmarshallList(@NonNull final String json, @NonNull final Class<T> elementType) 
            throws SerializationException {
        try {
            final TypeReference<List<T>> typeRef = new TypeReference<List<T>>() {};
            return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, elementType));
        } catch (JsonProcessingException e) {
            throw SerializationException.forUnmarshalling(
                "Failed to unmarshall JSON to List", 
                e, 
                List.class, 
                json
            );
        }
    }

    @Override
    @NonNull
    public <T> Map<String, T> unmarshallMap(@NonNull final String json, @NonNull final Class<T> valueType) 
            throws SerializationException {
        try {
            return objectMapper.readValue(json, objectMapper.getTypeFactory().constructMapType(Map.class, String.class, valueType));
        } catch (JsonProcessingException e) {
            throw SerializationException.forUnmarshalling(
                "Failed to unmarshall JSON to Map", 
                e, 
                Map.class, 
                json
            );
        }
    }

    @Override
    @NonNull
    public Map<String, Object> toMap(@Nullable final Object object) throws SerializationException {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = objectMapper.convertValue(object, Map.class);
            return map != null ? map : new java.util.HashMap<>();
        } catch (IllegalArgumentException e) {
            throw SerializationException.forMarshalling(
                "Failed to convert object to Map", 
                e, 
                object != null ? object.getClass() : null
            );
        }
    }

    @Override
    @Nullable
    public <T> T fromMap(@NonNull final Map<String, Object> map, @NonNull final Class<T> targetType) 
            throws SerializationException {
        try {
            return objectMapper.convertValue(map, targetType);
        } catch (IllegalArgumentException e) {
            throw SerializationException.forUnmarshalling(
                "Failed to convert Map to object", 
                e, 
                targetType, 
                map.toString()
            );
        }
    }

    @Override
    public boolean canHandle(@NonNull final Class<?> type) {
        // Jackson can handle most types
        return objectMapper.canSerialize(type);
    }

    @Override
    @NonNull
    public JsonSerializationConfig getConfiguration() {
        return config;
    }

    @Override
    @NonNull
    public JsonMarshaller withConfiguration(@NonNull final JsonSerializationConfig newConfig) {
        return new JacksonJsonMarshaller(newConfig);
    }

    /**
     * Gets the appropriate ObjectMapper for the given configuration.
     * Uses the current mapper if config matches, otherwise creates a new one.
     */
    @NonNull
    private ObjectMapper getMapperForConfig(@NonNull final JsonSerializationConfig requestedConfig) {
        if (config.equals(requestedConfig)) {
            return objectMapper;
        }
        return createConfiguredObjectMapper(requestedConfig);
    }

    /**
     * Creates and configures an ObjectMapper based on the provided configuration.
     */
    @NonNull
    private static ObjectMapper createConfiguredObjectMapper(@NonNull final JsonSerializationConfig config) {
        final ObjectMapper mapper = new ObjectMapper();

        // Configure basic serialization features
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(SerializationFeature.INDENT_OUTPUT, config.isPrettyPrint());
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, config.isFailOnEmptyBeans());
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, config.isWrapRootValue());
        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, config.isPreserveFieldOrder());

        // Configure deserialization features
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, config.isFailOnUnknownProperties());

        // Configure null and empty handling
        if (!config.isIncludeNulls()) {
            mapper.setDefaultPropertyInclusion(
                com.fasterxml.jackson.annotation.JsonInclude.Value.construct(
                    com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL,
                    config.isIncludeEmpty() ? 
                        com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS : 
                        com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY
                )
            );
        }

        // Configure Java Time module
        if (config.isUseJavaTimeModule()) {
            mapper.registerModule(new JavaTimeModule());
        }

        // Configure type information for polymorphic serialization
        if (config.isIncludeTypeInfo()) {
            mapper.activateDefaultTyping(
                mapper.getPolymorphicTypeValidator(),
                DefaultTyping.NON_FINAL
            );
        }

        // Configure date/time formatting
        if (config.getDateTimePattern() != null) {
            mapper.setDateFormat(new java.text.SimpleDateFormat(config.getDateTimePattern()));
        }

        // Configure timezone
        mapper.setTimeZone(java.util.TimeZone.getTimeZone(config.getTimeZone()));

        return mapper;
    }
}