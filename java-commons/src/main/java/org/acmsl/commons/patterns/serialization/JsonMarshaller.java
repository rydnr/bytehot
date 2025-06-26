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
 * Filename: JsonMarshaller.java
 *
 * Author: Claude (Anthropic AI)
 *
 * Class name: JsonMarshaller
 *
 * Responsibilities:
 *   - Provide high-level JSON marshalling/unmarshalling operations
 *   - Support custom type handling and configuration
 *   - Enable EventSourcing and domain object serialization
 *   - Offer both typed and generic serialization methods
 *
 * Collaborators:
 *   - JsonSerializationConfig: Configuration for serialization behavior
 *   - SerializationException: Handles serialization errors
 */
package org.acmsl.commons.patterns.serialization;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.Map;

/**
 * High-level interface for JSON marshalling and unmarshalling operations.
 * Provides a clean API for converting objects to/from JSON with support
 * for custom configuration and type handling.
 * @author Claude (Anthropic AI)
 * @since 2025-06-26
 */
public interface JsonMarshaller {

    /**
     * Marshalls an object to JSON string.
     * @param object the object to serialize
     * @return JSON representation of the object
     * @throws SerializationException if marshalling fails
     */
    @NonNull
    String marshall(@Nullable final Object object) throws SerializationException;

    /**
     * Marshalls an object to JSON string with custom configuration.
     * @param object the object to serialize
     * @param config custom serialization configuration
     * @return JSON representation of the object
     * @throws SerializationException if marshalling fails
     */
    @NonNull
    String marshall(@Nullable final Object object, @NonNull final JsonSerializationConfig config) 
        throws SerializationException;

    /**
     * Marshalls an object to JSON byte array for efficient storage/transmission.
     * @param object the object to serialize
     * @return JSON byte array representation
     * @throws SerializationException if marshalling fails
     */
    @NonNull
    byte[] marshallToBytes(@Nullable final Object object) throws SerializationException;

    /**
     * Marshalls an object to JSON byte array with custom configuration.
     * @param object the object to serialize
     * @param config custom serialization configuration
     * @return JSON byte array representation
     * @throws SerializationException if marshalling fails
     */
    @NonNull
    byte[] marshallToBytes(@Nullable final Object object, @NonNull final JsonSerializationConfig config) 
        throws SerializationException;

    /**
     * Unmarshalls JSON string to an object of the specified type.
     * @param <T> the target type
     * @param json the JSON string to deserialize
     * @param targetType the class of the target type
     * @return deserialized object
     * @throws SerializationException if unmarshalling fails
     */
    @Nullable
    <T> T unmarshall(@NonNull final String json, @NonNull final Class<T> targetType) 
        throws SerializationException;

    /**
     * Unmarshalls JSON string to an object with custom configuration.
     * @param <T> the target type
     * @param json the JSON string to deserialize
     * @param targetType the class of the target type
     * @param config custom deserialization configuration
     * @return deserialized object
     * @throws SerializationException if unmarshalling fails
     */
    @Nullable
    <T> T unmarshall(@NonNull final String json, @NonNull final Class<T> targetType, 
                    @NonNull final JsonSerializationConfig config) throws SerializationException;

    /**
     * Unmarshalls JSON byte array to an object of the specified type.
     * @param <T> the target type
     * @param jsonBytes the JSON byte array to deserialize
     * @param targetType the class of the target type
     * @return deserialized object
     * @throws SerializationException if unmarshalling fails
     */
    @Nullable
    <T> T unmarshall(@NonNull final byte[] jsonBytes, @NonNull final Class<T> targetType) 
        throws SerializationException;

    /**
     * Unmarshalls JSON byte array to an object with custom configuration.
     * @param <T> the target type
     * @param jsonBytes the JSON byte array to deserialize
     * @param targetType the class of the target type
     * @param config custom deserialization configuration
     * @return deserialized object
     * @throws SerializationException if unmarshalling fails
     */
    @Nullable
    <T> T unmarshall(@NonNull final byte[] jsonBytes, @NonNull final Class<T> targetType, 
                    @NonNull final JsonSerializationConfig config) throws SerializationException;

    /**
     * Unmarshalls JSON string to a List of objects of the specified type.
     * @param <T> the element type
     * @param json the JSON string to deserialize
     * @param elementType the class of the element type
     * @return list of deserialized objects
     * @throws SerializationException if unmarshalling fails
     */
    @NonNull
    <T> List<T> unmarshallList(@NonNull final String json, @NonNull final Class<T> elementType) 
        throws SerializationException;

    /**
     * Unmarshalls JSON string to a Map with String keys and values of the specified type.
     * @param <T> the value type
     * @param json the JSON string to deserialize
     * @param valueType the class of the value type
     * @return map of deserialized objects
     * @throws SerializationException if unmarshalling fails
     */
    @NonNull
    <T> Map<String, T> unmarshallMap(@NonNull final String json, @NonNull final Class<T> valueType) 
        throws SerializationException;

    /**
     * Converts an object to a generic Map representation (useful for dynamic access).
     * @param object the object to convert
     * @return Map representation of the object
     * @throws SerializationException if conversion fails
     */
    @NonNull
    Map<String, Object> toMap(@Nullable final Object object) throws SerializationException;

    /**
     * Converts a Map to an object of the specified type.
     * @param <T> the target type
     * @param map the map to convert
     * @param targetType the class of the target type
     * @return converted object
     * @throws SerializationException if conversion fails
     */
    @Nullable
    <T> T fromMap(@NonNull final Map<String, Object> map, @NonNull final Class<T> targetType) 
        throws SerializationException;

    /**
     * Checks if the marshaller can handle the given type.
     * @param type the type to check
     * @return true if the type can be marshalled/unmarshalled
     */
    boolean canHandle(@NonNull final Class<?> type);

    /**
     * Gets the current configuration used by this marshaller.
     * @return the current configuration
     */
    @NonNull
    JsonSerializationConfig getConfiguration();

    /**
     * Creates a new marshaller with the specified configuration.
     * @param config the new configuration
     * @return a new marshaller instance with the given configuration
     */
    @NonNull
    JsonMarshaller withConfiguration(@NonNull final JsonSerializationConfig config);
}