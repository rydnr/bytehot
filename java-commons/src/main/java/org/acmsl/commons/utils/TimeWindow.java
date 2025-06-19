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
 * Filename: TimeWindow.java
 *
 * Author: Claude (Anthropic AI)
 *
 * Class name: TimeWindow
 *
 * Responsibilities:
 *   - Represent time window for temporal analysis and operations
 *   - Provide time-based containment and overlap calculations
 *   - Support common time window operations and validations
 *
 * Collaborators:
 *   - ValueObject: Marker interface for value objects
 */
package org.acmsl.commons.utils;

import org.acmsl.commons.patterns.dao.ValueObject;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.Duration;
import java.time.Instant;

/**
 * Represents a time window for temporal analysis and operations.
 * Provides functionality for time-based containment, overlap calculations, and common time window operations.
 * @author Claude (Anthropic AI)
 * @since 2025-06-19
 */
@EqualsAndHashCode
@ToString
public final class TimeWindow implements ValueObject {

    /**
     * Start time of the window.
     */
    @Getter
    @NonNull
    private final Instant startTime;

    /**
     * End time of the window.
     */
    @Getter
    @NonNull
    private final Instant endTime;

    /**
     * Constructor for TimeWindow.
     * @param startTime the start time of the window
     * @param endTime the end time of the window
     */
    public TimeWindow(@NonNull final Instant startTime, @NonNull final Instant endTime) {
        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("End time must be after start time");
        }
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Creates a time window from start time and duration.
     * @param startTime The start time
     * @param duration The duration of the window
     * @return A TimeWindow spanning the specified duration from start time
     * @throws IllegalArgumentException if startTime is null or duration is null/negative
     */
    @NonNull
    public static TimeWindow of(@NonNull final Instant startTime, @NonNull final Duration duration) {
        if (duration.isNegative()) {
            throw new IllegalArgumentException("Duration must be positive");
        }
        
        return new TimeWindow(startTime, startTime.plus(duration));
    }

    /**
     * Creates a time window from start and end times.
     * @param startTime The start time
     * @param endTime The end time
     * @return A TimeWindow spanning from start to end time
     * @throws IllegalArgumentException if endTime is before startTime
     */
    @NonNull
    public static TimeWindow between(@NonNull final Instant startTime, @NonNull final Instant endTime) {
        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("End time must be after start time");
        }
        
        return new TimeWindow(startTime, endTime);
    }

    /**
     * Creates a time window representing the last specified duration from now.
     * @param duration The duration to look back from now
     * @return A TimeWindow ending at the current time
     * @throws IllegalArgumentException if duration is null or negative
     */
    @NonNull
    public static TimeWindow lastDuration(@NonNull final Duration duration) {
        if (duration.isNegative()) {
            throw new IllegalArgumentException("Duration must be positive");
        }
        
        Instant now = Instant.now();
        return new TimeWindow(now.minus(duration), now);
    }

    /**
     * Gets the duration of this time window.
     * @return The duration between start and end times
     */
    @NonNull
    public Duration getDuration() {
        return Duration.between(startTime, endTime);
    }

    /**
     * Checks if the specified instant falls within this time window.
     * @param instant The instant to check
     * @return true if the instant is within the window (inclusive of boundaries)
     */
    public boolean contains(@Nullable final Instant instant) {
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
    public boolean overlaps(@Nullable final TimeWindow other) {
        if (other == null) {
            return false;
        }
        
        return !endTime.isBefore(other.startTime) && !startTime.isAfter(other.endTime);
    }

    /**
     * Creates a new time window shifted by the specified duration.
     * @param duration The duration to shift (positive for future, negative for past)
     * @return A new TimeWindow shifted by the specified duration
     */
    @NonNull
    public TimeWindow shift(@NonNull final Duration duration) {
        return new TimeWindow(startTime.plus(duration), endTime.plus(duration));
    }

    /**
     * Creates a new time window extended by the specified duration at the end.
     * @param duration The duration to extend
     * @return A new TimeWindow extended by the specified duration
     * @throws IllegalArgumentException if duration is negative
     */
    @NonNull
    public TimeWindow extend(@NonNull final Duration duration) {
        if (duration.isNegative()) {
            throw new IllegalArgumentException("Extension duration must be positive");
        }
        
        return new TimeWindow(startTime, endTime.plus(duration));
    }

    /**
     * Checks if this time window is entirely before another time window.
     * @param other The other time window
     * @return true if this window is entirely before the other
     */
    public boolean isBefore(@Nullable final TimeWindow other) {
        return other != null && endTime.isBefore(other.startTime);
    }

    /**
     * Checks if this time window is entirely after another time window.
     * @param other The other time window
     * @return true if this window is entirely after the other
     */
    public boolean isAfter(@Nullable final TimeWindow other) {
        return other != null && startTime.isAfter(other.endTime);
    }
}