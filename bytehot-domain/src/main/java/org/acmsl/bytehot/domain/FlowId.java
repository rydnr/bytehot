/*
                        ByteHot

    Copyright (C) 2025-today  rydnr@acm-sl.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
/******************************************************************************
 *
 * Filename: FlowId.java
 *
 * Author: Claude (Anthropic AI)
 *
 * Class name: FlowId
 *
 * Responsibilities:
 *   - Represent unique identifier for discovered business flows
 *
 * Collaborators:
 *   - Flow: The flow domain entity this identifier belongs to
 */
package org.acmsl.bytehot.domain;

import org.acmsl.commons.patterns.dao.ValueObject;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.UUID;

/**
 * Unique identifier for discovered business flows in the ByteHot system.
 * @author Claude (Anthropic AI)
 * @since 2025-06-19
 */
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public final class FlowId implements ValueObject {

    /**
     * The unique identifier value.
     */
    @Getter
    @NonNull
    private final String value;

    /**
     * Creates a new random FlowId.
     * @return A new FlowId with a random UUID
     */
    @NonNull
    public static FlowId random() {
        return new FlowId(UUID.randomUUID().toString());
    }

    /**
     * Creates a FlowId from an existing string value.
     * @param value The string value for the flow ID
     * @return A FlowId with the specified value
     */
    @NonNull
    public static FlowId of(@Nullable final String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("FlowId value cannot be null or empty");
        }
        return new FlowId(value.trim());
    }

    /**
     * Creates a FlowId based on a flow name for deterministic identification.
     * @param flowName The name of the flow
     * @return A FlowId based on the flow name
     */
    @NonNull
    public static FlowId fromName(@Nullable final String flowName) {
        if (flowName == null || flowName.trim().isEmpty()) {
            throw new IllegalArgumentException("Flow name cannot be null or empty");
        }
        
        // Create deterministic ID based on flow name
        String normalizedName = flowName.trim()
            .toLowerCase()
            .replaceAll("[^a-z0-9]+", "-")
            .replaceAll("^-+|-+$", "");
            
        return new FlowId("flow-" + normalizedName);
    }
}