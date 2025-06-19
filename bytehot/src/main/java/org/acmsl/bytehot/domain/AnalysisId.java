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
 * Filename: AnalysisId.java
 *
 * Author: Claude (Anthropic AI)
 *
 * Class name: AnalysisId
 *
 * Responsibilities:
 *   - Represent unique identifier for flow analysis requests
 *
 * Collaborators:
 *   - FlowAnalysisRequested: Events that reference this analysis ID
 */
package org.acmsl.bytehot.domain;

import org.acmsl.commons.patterns.dao.ValueObject;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.UUID;

/**
 * Unique identifier for flow analysis requests.
 * @author Claude (Anthropic AI)
 * @since 2025-06-19
 */
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public final class AnalysisId implements ValueObject {

    /**
     * The unique identifier value.
     */
    @Getter
    private final String value;

    /**
     * Creates a new random AnalysisId.
     * @return A new AnalysisId with a random UUID
     */
    public static AnalysisId random() {
        return new AnalysisId(UUID.randomUUID().toString());
    }

    /**
     * Creates an AnalysisId from an existing string value.
     * @param value The string value for the analysis ID
     * @return An AnalysisId with the specified value
     */
    public static AnalysisId of(final String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("AnalysisId value cannot be null or empty");
        }
        return new AnalysisId(value.trim());
    }
}