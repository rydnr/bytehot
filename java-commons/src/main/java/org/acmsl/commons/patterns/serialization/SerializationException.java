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
 * Filename: SerializationException.java
 *
 * Author: Claude (Anthropic AI)
 *
 * Class name: SerializationException
 *
 * Responsibilities:
 *   - Represent errors during JSON serialization/deserialization
 *   - Provide detailed error context and debugging information
 *   - Support chaining of underlying exceptions
 */
package org.acmsl.commons.patterns.serialization;


import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Exception thrown when JSON serialization or deserialization fails.
 * Provides detailed context about the failure for debugging purposes.
 * @author Claude (Anthropic AI)
 * @since 2025-06-26
 */
public class SerializationException extends RuntimeException {

    /**
     * Serial version UID for serialization compatibility.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The type that was being processed when the error occurred.
     */
    @Nullable
    private final Class<?> targetType;

    /**
     * The JSON content that caused the error (truncated if too long).
     */
    @Nullable
    private final String jsonContent;

    /**
     * The operation that failed (e.g., "marshalling", "unmarshalling").
     */
    @Nullable
    private final String operation;

    /**
     * Creates a new SerializationException with a message.
     * @param message the error message
     */
    public SerializationException(@NonNull final String message) {
        super(message);
        this.targetType = null;
        this.jsonContent = null;
        this.operation = null;
    }

    /**
     * Creates a new SerializationException with a message and cause.
     * @param message the error message
     * @param cause the underlying cause
     */
    public SerializationException(@NonNull final String message, @NonNull final Throwable cause) {
        super(message, cause);
        this.targetType = null;
        this.jsonContent = null;
        this.operation = null;
    }

    /**
     * Creates a new SerializationException with detailed context.
     * @param message the error message
     * @param cause the underlying cause
     * @param targetType the type being processed
     * @param jsonContent the JSON content (will be truncated if too long)
     * @param operation the operation that failed
     */
    public SerializationException(
        @NonNull final String message, 
        @Nullable final Throwable cause,
        @Nullable final Class<?> targetType,
        @Nullable final String jsonContent,
        @Nullable final String operation
    ) {
        super(buildDetailedMessage(message, targetType, jsonContent, operation), cause);
        this.targetType = targetType;
        this.jsonContent = truncateJsonContent(jsonContent);
        this.operation = operation;
    }

    /**
     * Creates a new SerializationException for marshalling failures.
     * @param message the error message
     * @param cause the underlying cause
     * @param objectType the type of object being marshalled
     * @return new SerializationException
     */
    @NonNull
    public static SerializationException forMarshalling(
        @NonNull final String message,
        @Nullable final Throwable cause,
        @Nullable final Class<?> objectType
    ) {
        return new SerializationException(message, cause, objectType, null, "marshalling");
    }

    /**
     * Creates a new SerializationException for unmarshalling failures.
     * @param message the error message
     * @param cause the underlying cause
     * @param targetType the target type for unmarshalling
     * @param jsonContent the JSON content that caused the failure
     * @return new SerializationException
     */
    @NonNull
    public static SerializationException forUnmarshalling(
        @NonNull final String message,
        @Nullable final Throwable cause,
        @Nullable final Class<?> targetType,
        @Nullable final String jsonContent
    ) {
        return new SerializationException(message, cause, targetType, jsonContent, "unmarshalling");
    }

    /**
     * Gets the type that was being processed when the error occurred.
     * @return the target type, or null if not available
     */
    @Nullable
    public Class<?> getTargetType() {
        return targetType;
    }

    /**
     * Gets the JSON content that caused the error (truncated if too long).
     * @return the JSON content, or null if not available
     */
    @Nullable
    public String getJsonContent() {
        return jsonContent;
    }

    /**
     * Gets the operation that failed.
     * @return the operation name, or null if not available
     */
    @Nullable
    public String getOperation() {
        return operation;
    }

    /**
     * Builds a detailed error message including context information.
     */
    @NonNull
    private static String buildDetailedMessage(
        @NonNull final String baseMessage,
        @Nullable final Class<?> targetType,
        @Nullable final String jsonContent,
        @Nullable final String operation
    ) {
        final StringBuilder sb = new StringBuilder(baseMessage);
        
        if (operation != null) {
            sb.append(" during ").append(operation);
        }
        
        if (targetType != null) {
            sb.append(" for type ").append(targetType.getSimpleName());
        }
        
        if (jsonContent != null && !jsonContent.isEmpty()) {
            final String truncated = truncateJsonContent(jsonContent);
            sb.append(". JSON content: ").append(truncated);
        }
        
        return sb.toString();
    }

    /**
     * Truncates JSON content for inclusion in error messages.
     */
    @Nullable
    private static String truncateJsonContent(@Nullable final String jsonContent) {
        if (jsonContent == null) {
            return null;
        }
        
        final int maxLength = 500;
        if (jsonContent.length() <= maxLength) {
            return jsonContent;
        }
        
        return jsonContent.substring(0, maxLength) + "... (truncated)";
    }
}