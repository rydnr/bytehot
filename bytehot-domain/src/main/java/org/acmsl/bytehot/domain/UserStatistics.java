/*
                        ByteHot

    Copyright (C) 2025-today  rydnr@acm-sl.org

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 3 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public v3
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    Thanks to ACM S.L. for distributing this library under the GPLv3 license.
    Contact info: jose.sanleandro@acm-sl.com

 ******************************************************************************
 *
 * Filename: UserStatistics.java
 *
 * Author: Claude Code
 *
 * Class name: UserStatistics
 *
 * Responsibilities:
 *   - Track user usage statistics and analytics
 *   - Calculate derived metrics (success rates, averages)
 *   - Support immutable statistics updates
 *
 * Collaborators:
 *   - None (pure value object)
 */
package org.acmsl.bytehot.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * User statistics value object with analytics capabilities
 * @author Claude Code
 * @since 2025-06-18
 */
@RequiredArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
@Getter
public class UserStatistics {
    
    /**
     * Total number of hot-swap operations attempted
     */
    private final int totalHotSwaps;
    
    /**
     * Number of successful hot-swap operations
     */
    private final int successfulHotSwaps;
    
    /**
     * Number of failed hot-swap operations
     */
    private final int failedHotSwaps;
    
    /**
     * Total time saved through hot-swapping
     */
    private final Duration totalTimeSaved;
    
    /**
     * Number of user sessions started
     */
    private final int sessionsStarted;
    
    /**
     * Total active time across all sessions
     */
    private final Duration totalActiveTime;
    
    /**
     * Timestamp of first session
     */
    private final Instant firstSessionAt;
    
    /**
     * Timestamp of last session
     */
    private final Instant lastSessionAt;
    
    /**
     * Number of files being watched
     */
    private final int filesWatched;
    
    /**
     * Number of classes modified
     */
    private final int classesModified;
    
    /**
     * Map of class names to modification counts
     */
    private final Map<String, Integer> modificationsByClass;

    /**
     * Creates empty statistics for a new user
     * @return empty user statistics
     */
    public static UserStatistics empty() {
        return UserStatistics.builder()
            .totalHotSwaps(0)
            .successfulHotSwaps(0)
            .failedHotSwaps(0)
            .totalTimeSaved(Duration.ZERO)
            .sessionsStarted(0)
            .totalActiveTime(Duration.ZERO)
            .firstSessionAt(null)
            .lastSessionAt(null)
            .filesWatched(0)
            .classesModified(0)
            .modificationsByClass(new HashMap<>())
            .build();
    }

    /**
     * Records a hot-swap operation
     * @param successful whether the operation was successful
     * @param timeSaved time saved by the operation
     * @return updated statistics
     */
    public UserStatistics recordHotSwap(final boolean successful, final Duration timeSaved) {
        return this.toBuilder()
            .totalHotSwaps(totalHotSwaps + 1)
            .successfulHotSwaps(successful ? successfulHotSwaps + 1 : successfulHotSwaps)
            .failedHotSwaps(successful ? failedHotSwaps : failedHotSwaps + 1)
            .totalTimeSaved(totalTimeSaved.plus(timeSaved))
            .build();
    }

    /**
     * Records a class modification
     * @param className the name of the modified class
     * @return updated statistics
     */
    public UserStatistics recordClassModification(final String className) {
        final Map<String, Integer> newModifications = new HashMap<>(modificationsByClass);
        newModifications.merge(className, 1, Integer::sum);
        
        return this.toBuilder()
            .classesModified(classesModified + 1)
            .modificationsByClass(newModifications)
            .build();
    }

    /**
     * Records a user session
     * @param sessionStart start time of the session
     * @param sessionDuration duration of the session
     * @return updated statistics
     */
    public UserStatistics recordSession(final Instant sessionStart, final Duration sessionDuration) {
        return this.toBuilder()
            .sessionsStarted(sessionsStarted + 1)
            .totalActiveTime(totalActiveTime.plus(sessionDuration))
            .firstSessionAt(firstSessionAt != null ? firstSessionAt : sessionStart)
            .lastSessionAt(sessionStart)
            .build();
    }

    /**
     * Updates file watch count
     * @param fileCount number of files being watched
     * @return updated statistics
     */
    public UserStatistics updateFilesWatched(final int fileCount) {
        return this.toBuilder()
            .filesWatched(fileCount)
            .build();
    }

    /**
     * Calculates hot-swap success rate
     * @return success rate as percentage (0.0 to 1.0)
     */
    public double getSuccessRate() {
        if (totalHotSwaps == 0) {
            return 0.0;
        }
        return (double) successfulHotSwaps / totalHotSwaps;
    }

    /**
     * Calculates average time saved per successful hot-swap
     * @return average time saved
     */
    public Duration getAverageTimeSavedPerHotSwap() {
        if (successfulHotSwaps == 0) {
            return Duration.ZERO;
        }
        return totalTimeSaved.dividedBy(successfulHotSwaps);
    }

    /**
     * Calculates average session duration
     * @return average session duration
     */
    public Duration getAverageSessionDuration() {
        if (sessionsStarted == 0) {
            return Duration.ZERO;
        }
        return totalActiveTime.dividedBy(sessionsStarted);
    }

    /**
     * Gets the most modified class
     * @return class name with highest modification count, or null if none
     */
    public String getMostModifiedClass() {
        return modificationsByClass.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
    }

    /**
     * Gets modification count for a specific class
     * @param className the class name
     * @return modification count
     */
    public int getModificationCount(final String className) {
        return modificationsByClass.getOrDefault(className, 0);
    }
}