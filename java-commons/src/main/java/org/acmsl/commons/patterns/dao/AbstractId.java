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
 * Filename: AbstractId.java
 *
 * Author: Claude (Anthropic AI)
 *
 * Class name: AbstractId
 *
 * Responsibilities:
 *   - Provide base functionality for all ID value objects
 *   - Ensure immutability and value object semantics
 *   - Support common ID operations like validation and formatting
 *
 * Collaborators:
 *   - ValueObject: Marker interface for value objects
 */
package org.acmsl.commons.patterns.dao;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.UUID;
import java.util.function.Function;

/**
 * Abstract base class for all identifier value objects.
 * Provides common functionality for string-based identifiers with validation and factory methods.
 * @param <T> The concrete identifier type
 * @author Claude (Anthropic AI)
 * @since 2025-06-19
 */
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public abstract class AbstractId<T extends AbstractId<T>> implements ValueObject {

    /**
     * The identifier value.
     */
    @Getter
    @NonNull
    private final String value;

    /**
     * Creates a random identifier using UUID.
     * @param constructor Function to create the concrete identifier type
     * @param <T> The concrete identifier type
     * @return A new identifier with a random UUID value
     */
    @NonNull
    public static <T extends AbstractId<T>> T random(@NonNull final Function<String, T> constructor) {
        return constructor.apply(UUID.randomUUID().toString());
    }

    /**
     * Creates an identifier from an existing string value with validation.
     * @param value The string value for the identifier
     * @param constructor Function to create the concrete identifier type
     * @param <T> The concrete identifier type
     * @return An identifier with the specified value
     * @throws IllegalArgumentException if value is null or empty
     */
    @NonNull
    public static <T extends AbstractId<T>> T of(@Nullable final String value, 
                                                  @NonNull final Function<String, T> constructor) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Identifier value cannot be null or empty");
        }
        return constructor.apply(value.trim());
    }

    /**
     * Creates an identifier with a prefix and random UUID suffix.
     * @param prefix The prefix for the identifier
     * @param constructor Function to create the concrete identifier type
     * @param <T> The concrete identifier type
     * @return A new identifier with the specified prefix and random suffix
     */
    @NonNull
    public static <T extends AbstractId<T>> T withPrefix(@NonNull final String prefix,
                                                          @NonNull final Function<String, T> constructor) {
        return constructor.apply(prefix + "-" + UUID.randomUUID().toString());
    }

    /**
     * Validates that the identifier value follows a specific pattern.
     * Override in subclasses to provide custom validation logic.
     * @param value The value to validate
     * @return true if the value is valid
     */
    protected boolean isValidValue(@NonNull final String value) {
        return !value.trim().isEmpty();
    }

    /**
     * Gets the identifier value as a string.
     * @return The identifier value
     */
    @NonNull
    public String asString() {
        return value;
    }
}