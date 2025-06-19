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
 * Filename: TimeWindow.java
 *
 * Author: Claude (Anthropic AI)
 *
 * Class name: TimeWindow
 *
 * Responsibilities:
 *   - Represent time window for flow analysis
 *
 * Collaborators:
 *   - FlowAnalysisRequested: Events that use time windows for analysis scope
 */
package org.acmsl.bytehot.domain;

import org.acmsl.commons.patterns.dao.ValueObject;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.Duration;
import java.time.Instant;

/**
 * Represents a time window for flow analysis.
 * @author Claude (Anthropic AI)
 * @since 2025-06-19
 */
@RequiredArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public final class TimeWindow implements ValueObject {

    /**
     * Start time of the window.
     */
    @Getter
    private final Instant startTime;

    /**
     * End time of the window.
     */
    @Getter
    private final Instant endTime;

    /**
     * Creates a time window from start time and duration.
     * @param startTime The start time
     * @param duration The duration of the window
     * @return A TimeWindow spanning the specified duration from start time
     */
    public static TimeWindow of(final Instant startTime, final Duration duration) {
        if (startTime == null) {
            throw new IllegalArgumentException("Start time cannot be null");
        }
        if (duration == null || duration.isNegative()) {
            throw new IllegalArgumentException("Duration must be positive");
        }
        
        return new TimeWindow(startTime, startTime.plus(duration));
    }

    /**
     * Creates a time window from start and end times.
     * @param startTime The start time
     * @param endTime The end time
     * @return A TimeWindow spanning from start to end time
     */
    public static TimeWindow between(final Instant startTime, final Instant endTime) {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Start and end times cannot be null");
        }
        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("End time must be after start time");
        }
        
        return new TimeWindow(startTime, endTime);
    }

    /**
     * Creates a time window representing the last specified duration from now.
     * @param duration The duration to look back from now
     * @return A TimeWindow ending at the current time
     */
    public static TimeWindow lastDuration(final Duration duration) {
        if (duration == null || duration.isNegative()) {
            throw new IllegalArgumentException("Duration must be positive");
        }
        
        Instant now = Instant.now();
        return new TimeWindow(now.minus(duration), now);
    }

    /**
     * Gets the duration of this time window.
     * @return The duration between start and end times
     */
    public Duration getDuration() {
        return Duration.between(startTime, endTime);
    }

    /**
     * Checks if the specified instant falls within this time window.
     * @param instant The instant to check
     * @return true if the instant is within the window (inclusive of boundaries)
     */
    public boolean contains(final Instant instant) {
        if (instant == null) {
            return false;
        }
        
        return !instant.isBefore(startTime) && !instant.isAfter(endTime);
    }

    /**
     * Checks if this time window overlaps with another time window.
     * @param other The other time window
     * @return true if the windows overlap
     */
    public boolean overlaps(final TimeWindow other) {
        if (other == null) {
            return false;
        }
        
        return !endTime.isBefore(other.startTime) && !startTime.isAfter(other.endTime);
    }
}