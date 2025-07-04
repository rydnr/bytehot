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
 * Filename: PerformanceStatistics.java
 *
 * Author: Claude Code
 *
 * Class name: PerformanceStatistics
 *
 * Responsibilities:
 *   - Aggregate performance metrics over time
 *   - Calculate statistical measures for performance analysis
 *   - Support performance trend analysis
 *
 * Collaborators:
 *   - PerformanceMonitor: Creates statistics from collected snapshots
 *   - PerformanceSnapshot: Source data for statistical calculations
 */
package org.acmsl.bytehot.infrastructure.production;

import java.time.Duration;
import java.time.Instant;

/**
 * Aggregated performance statistics for analysis and reporting.
 * @author Claude Code
 * @since 2025-07-04
 */
public class PerformanceStatistics {
    
    /**
     * The time period covered by these statistics.
     */
    private final Duration timePeriod;
    
    /**
     * Start time of the statistics period.
     */
    private final Instant startTime;
    
    /**
     * End time of the statistics period.
     */
    private final Instant endTime;
    
    /**
     * Number of snapshots included in statistics.
     */
    private final int snapshotCount;
    
    /**
     * Average memory usage ratio.
     */
    private final double averageMemoryUsage;
    
    /**
     * Peak memory usage ratio.
     */
    private final double peakMemoryUsage;
    
    /**
     * Average CPU usage.
     */
    private final double averageCpuUsage;
    
    /**
     * Peak CPU usage.
     */
    private final double peakCpuUsage;
    
    /**
     * Total hot-swap operations.
     */
    private final long totalHotSwapOperations;
    
    /**
     * Average hot-swap operation time.
     */
    private final double averageHotSwapTime;
    
    /**
     * Creates a new PerformanceStatistics.
     * @param timePeriod The time period
     * @param startTime The start time
     * @param endTime The end time
     * @param snapshotCount The snapshot count
     * @param averageMemoryUsage The average memory usage
     * @param peakMemoryUsage The peak memory usage
     * @param averageCpuUsage The average CPU usage
     * @param peakCpuUsage The peak CPU usage
     * @param totalHotSwapOperations The total hot-swap operations
     * @param averageHotSwapTime The average hot-swap time
     */
    protected PerformanceStatistics(final Duration timePeriod,
                                  final Instant startTime,
                                  final Instant endTime,
                                  final int snapshotCount,
                                  final double averageMemoryUsage,
                                  final double peakMemoryUsage,
                                  final double averageCpuUsage,
                                  final double peakCpuUsage,
                                  final long totalHotSwapOperations,
                                  final double averageHotSwapTime) {
        this.timePeriod = timePeriod;
        this.startTime = startTime;
        this.endTime = endTime;
        this.snapshotCount = snapshotCount;
        this.averageMemoryUsage = averageMemoryUsage;
        this.peakMemoryUsage = peakMemoryUsage;
        this.averageCpuUsage = averageCpuUsage;
        this.peakCpuUsage = peakCpuUsage;
        this.totalHotSwapOperations = totalHotSwapOperations;
        this.averageHotSwapTime = averageHotSwapTime;
    }
    
    /**
     * Creates empty performance statistics.
     * @return Empty performance statistics
     */
    public static PerformanceStatistics empty() {
        Instant now = Instant.now();
        return new PerformanceStatistics(
            Duration.ZERO,
            now,
            now,
            0,
            0.0,
            0.0,
            0.0,
            0.0,
            0,
            0.0
        );
    }
    
    /**
     * Gets the time period.
     * @return The time period
     */
    public Duration getTimePeriod() {
        return timePeriod;
    }
    
    /**
     * Gets the start time.
     * @return The start time
     */
    public Instant getStartTime() {
        return startTime;
    }
    
    /**
     * Gets the end time.
     * @return The end time
     */
    public Instant getEndTime() {
        return endTime;
    }
    
    /**
     * Gets the snapshot count.
     * @return The snapshot count
     */
    public int getSnapshotCount() {
        return snapshotCount;
    }
    
    /**
     * Gets the average memory usage.
     * @return The average memory usage
     */
    public double getAverageMemoryUsage() {
        return averageMemoryUsage;
    }
    
    /**
     * Gets the peak memory usage.
     * @return The peak memory usage
     */
    public double getPeakMemoryUsage() {
        return peakMemoryUsage;
    }
    
    /**
     * Gets the average CPU usage.
     * @return The average CPU usage
     */
    public double getAverageCpuUsage() {
        return averageCpuUsage;
    }
    
    /**
     * Gets the peak CPU usage.
     * @return The peak CPU usage
     */
    public double getPeakCpuUsage() {
        return peakCpuUsage;
    }
    
    /**
     * Gets the total hot-swap operations.
     * @return The total hot-swap operations
     */
    public long getTotalHotSwapOperations() {
        return totalHotSwapOperations;
    }
    
    /**
     * Gets the average hot-swap time.
     * @return The average hot-swap time
     */
    public double getAverageHotSwapTime() {
        return averageHotSwapTime;
    }
    
    /**
     * Checks if statistics indicate healthy performance.
     * @return true if performance is healthy, false otherwise
     */
    public boolean isHealthy() {
        return averageMemoryUsage < 0.8 && 
               averageCpuUsage < 0.85 && 
               averageHotSwapTime < 5000.0;
    }
    
    /**
     * Gets a summary of the performance statistics.
     * @return A summary string
     */
    public String getSummary() {
        return String.format(
            "PerformanceStatistics[period=%s, snapshots=%d, avgMem=%.1f%%, peakMem=%.1f%%, avgCpu=%.1f%%, hotswap=%d ops, avgTime=%.1fms]",
            timePeriod,
            snapshotCount,
            averageMemoryUsage * 100,
            peakMemoryUsage * 100,
            averageCpuUsage * 100,
            totalHotSwapOperations,
            averageHotSwapTime
        );
    }
    
    @Override
    public String toString() {
        return getSummary();
    }
}