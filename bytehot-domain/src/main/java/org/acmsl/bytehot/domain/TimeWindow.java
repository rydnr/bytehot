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
 * Time window domain object for analysis periods and temporal queries.
 * Encapsulates time-based business logic and temporal operations.
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
    protected final Instant startTime;

    /**
     * End time of the window.
     */
    @Getter
    protected final Instant endTime;

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

    /**
     * Creates a new time window extending this one by the specified duration.
     * @param additionalDuration The duration to extend by
     * @return A new TimeWindow extended by the duration
     */
    public TimeWindow extendBy(final Duration additionalDuration) {
        if (additionalDuration == null || additionalDuration.isNegative()) {
            throw new IllegalArgumentException("Extension duration must be positive");
        }
        return new TimeWindow(startTime, endTime.plus(additionalDuration));
    }

    /**
     * Creates a new time window that starts earlier by the specified duration.
     * @param prependDuration The duration to prepend
     * @return A new TimeWindow starting earlier by the duration
     */
    public TimeWindow prependBy(final Duration prependDuration) {
        if (prependDuration == null || prependDuration.isNegative()) {
            throw new IllegalArgumentException("Prepend duration must be positive");
        }
        return new TimeWindow(startTime.minus(prependDuration), endTime);
    }

    /**
     * Calculates the intersection of this time window with another.
     * @param other The other time window
     * @return The intersection time window, or null if no overlap
     */
    public TimeWindow intersectionWith(final TimeWindow other) {
        if (other == null || !overlaps(other)) {
            return null;
        }
        
        Instant intersectionStart = startTime.isAfter(other.startTime) ? startTime : other.startTime;
        Instant intersectionEnd = endTime.isBefore(other.endTime) ? endTime : other.endTime;
        
        return new TimeWindow(intersectionStart, intersectionEnd);
    }

    /**
     * Creates the union of this time window with another (spanning both).
     * @param other The other time window
     * @return A new TimeWindow spanning both windows
     */
    public TimeWindow unionWith(final TimeWindow other) {
        if (other == null) {
            return this;
        }
        
        Instant unionStart = startTime.isBefore(other.startTime) ? startTime : other.startTime;
        Instant unionEnd = endTime.isAfter(other.endTime) ? endTime : other.endTime;
        
        return new TimeWindow(unionStart, unionEnd);
    }

    /**
     * Splits this time window into smaller windows of the specified duration.
     * @param segmentDuration The duration of each segment
     * @return List of TimeWindow segments
     */
    public java.util.List<TimeWindow> splitInto(final Duration segmentDuration) {
        if (segmentDuration == null || segmentDuration.isNegative() || segmentDuration.isZero()) {
            throw new IllegalArgumentException("Segment duration must be positive");
        }
        
        java.util.List<TimeWindow> segments = new java.util.ArrayList<>();
        Instant current = startTime;
        
        while (current.isBefore(endTime)) {
            Instant segmentEnd = current.plus(segmentDuration);
            if (segmentEnd.isAfter(endTime)) {
                segmentEnd = endTime;
            }
            segments.add(new TimeWindow(current, segmentEnd));
            current = segmentEnd;
        }
        
        return segments;
    }

    /**
     * Checks if this time window is in the past relative to now.
     * @return true if the entire window is in the past
     */
    public boolean isInPast() {
        return endTime.isBefore(Instant.now());
    }

    /**
     * Checks if this time window is in the future relative to now.
     * @return true if the entire window is in the future
     */
    public boolean isInFuture() {
        return startTime.isAfter(Instant.now());
    }

    /**
     * Checks if this time window includes the current moment.
     * @return true if now is within this window
     */
    public boolean includesNow() {
        return contains(Instant.now());
    }

    /**
     * Gets the percentage of this window that has elapsed relative to now.
     * @return percentage (0.0 to 1.0) of window elapsed, or 1.0 if window is past
     */
    public double getElapsedPercentage() {
        Instant now = Instant.now();
        if (now.isBefore(startTime)) {
            return 0.0;
        }
        if (now.isAfter(endTime)) {
            return 1.0;
        }
        
        Duration totalDuration = Duration.between(startTime, endTime);
        Duration elapsedDuration = Duration.between(startTime, now);
        
        return (double) elapsedDuration.toMillis() / totalDuration.toMillis();
    }

    /**
     * Gets a human-readable description of this time window.
     * @return description suitable for logging or user display
     */
    public String getDescription() {
        Duration duration = getDuration();
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        
        String durationStr;
        if (hours > 0) {
            durationStr = String.format("%d hours %d minutes", hours, minutes);
        } else if (minutes > 0) {
            durationStr = String.format("%d minutes", minutes);
        } else {
            durationStr = String.format("%d seconds", duration.getSeconds());
        }
        
        if (isInPast()) {
            return String.format("Past window of %s", durationStr);
        } else if (isInFuture()) {
            return String.format("Future window of %s", durationStr);
        } else if (includesNow()) {
            return String.format("Current window of %s (%.1f%% elapsed)", 
                durationStr, getElapsedPercentage() * 100);
        } else {
            return String.format("Window of %s", durationStr);
        }
    }

    /**
     * Determines if this time window is suitable for real-time analysis.
     * @return true if window is recent enough for real-time processing
     */
    public boolean isSuitableForRealTimeAnalysis() {
        // Consider windows that end within the last 5 minutes as real-time
        Duration timeSinceEnd = Duration.between(endTime, Instant.now());
        return timeSinceEnd.compareTo(Duration.ofMinutes(5)) <= 0;
    }
}