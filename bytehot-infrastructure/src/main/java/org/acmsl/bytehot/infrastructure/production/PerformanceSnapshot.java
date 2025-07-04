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
 * Filename: PerformanceSnapshot.java
 *
 * Author: Claude Code
 *
 * Class name: PerformanceSnapshot
 *
 * Responsibilities:
 *   - Capture point-in-time performance metrics
 *   - Store JVM and application performance data
 *   - Support performance trend analysis
 *
 * Collaborators:
 *   - PerformanceMonitor: Creates performance snapshots
 *   - PerformanceStatistics: Uses snapshots for statistical analysis
 */
package org.acmsl.bytehot.infrastructure.production;

import java.time.Duration;
import java.time.Instant;

/**
 * Represents a point-in-time performance metrics snapshot.
 * @author Claude Code
 * @since 2025-07-04
 */
public class PerformanceSnapshot {
    
    /**
     * The timestamp when this snapshot was taken.
     */
    private final Instant timestamp;
    
    /**
     * Heap memory currently used in bytes.
     */
    private final long heapUsed;
    
    /**
     * Maximum heap memory available in bytes.
     */
    private final long heapMax;
    
    /**
     * Non-heap memory currently used in bytes.
     */
    private final long nonHeapUsed;
    
    /**
     * Maximum non-heap memory available in bytes.
     */
    private final long nonHeapMax;
    
    /**
     * Current CPU load (0.0 to 1.0).
     */
    private final double cpuLoad;
    
    /**
     * Current number of threads.
     */
    private final int threadCount;
    
    /**
     * Total number of garbage collections.
     */
    private final long gcCollections;
    
    /**
     * Total time spent in garbage collection in milliseconds.
     */
    private final long gcTime;
    
    /**
     * JVM uptime.
     */
    private final Duration uptime;
    
    /**
     * Total number of hot-swap operations.
     */
    private final long hotSwapOperations;
    
    /**
     * Average hot-swap operation time in milliseconds.
     */
    private final double averageHotSwapTime;
    
    /**
     * Creates a new PerformanceSnapshot.
     * @param timestamp The snapshot timestamp
     * @param heapUsed The heap memory used
     * @param heapMax The maximum heap memory
     * @param nonHeapUsed The non-heap memory used
     * @param nonHeapMax The maximum non-heap memory
     * @param cpuLoad The CPU load
     * @param threadCount The thread count
     * @param gcCollections The total GC collections
     * @param gcTime The total GC time
     * @param uptime The JVM uptime
     * @param hotSwapOperations The total hot-swap operations
     * @param averageHotSwapTime The average hot-swap time
     */
    protected PerformanceSnapshot(final Instant timestamp,
                                final long heapUsed,
                                final long heapMax,
                                final long nonHeapUsed,
                                final long nonHeapMax,
                                final double cpuLoad,
                                final int threadCount,
                                final long gcCollections,
                                final long gcTime,
                                final Duration uptime,
                                final long hotSwapOperations,
                                final double averageHotSwapTime) {
        this.timestamp = timestamp;
        this.heapUsed = heapUsed;
        this.heapMax = heapMax;
        this.nonHeapUsed = nonHeapUsed;
        this.nonHeapMax = nonHeapMax;
        this.cpuLoad = cpuLoad;
        this.threadCount = threadCount;
        this.gcCollections = gcCollections;
        this.gcTime = gcTime;
        this.uptime = uptime;
        this.hotSwapOperations = hotSwapOperations;
        this.averageHotSwapTime = averageHotSwapTime;
    }
    
    /**
     * Creates a builder for constructing performance snapshots.
     * @return A new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Gets the snapshot timestamp.
     * @return The snapshot timestamp
     */
    public Instant getTimestamp() {
        return timestamp;
    }
    
    /**
     * Gets the heap memory used.
     * @return The heap memory used in bytes
     */
    public long getHeapUsed() {
        return heapUsed;
    }
    
    /**
     * Gets the maximum heap memory.
     * @return The maximum heap memory in bytes
     */
    public long getHeapMax() {
        return heapMax;
    }
    
    /**
     * Gets the non-heap memory used.
     * @return The non-heap memory used in bytes
     */
    public long getNonHeapUsed() {
        return nonHeapUsed;
    }
    
    /**
     * Gets the maximum non-heap memory.
     * @return The maximum non-heap memory in bytes
     */
    public long getNonHeapMax() {
        return nonHeapMax;
    }
    
    /**
     * Gets the CPU load.
     * @return The CPU load (0.0 to 1.0)
     */
    public double getCpuLoad() {
        return cpuLoad;
    }
    
    /**
     * Gets the thread count.
     * @return The number of threads
     */
    public int getThreadCount() {
        return threadCount;
    }
    
    /**
     * Gets the total GC collections.
     * @return The total garbage collections
     */
    public long getGcCollections() {
        return gcCollections;
    }
    
    /**
     * Gets the total GC time.
     * @return The total GC time in milliseconds
     */
    public long getGcTime() {
        return gcTime;
    }
    
    /**
     * Gets the JVM uptime.
     * @return The JVM uptime
     */
    public Duration getUptime() {
        return uptime;
    }
    
    /**
     * Gets the total hot-swap operations.
     * @return The total hot-swap operations
     */
    public long getHotSwapOperations() {
        return hotSwapOperations;
    }
    
    /**
     * Gets the average hot-swap operation time.
     * @return The average hot-swap time in milliseconds
     */
    public double getAverageHotSwapTime() {
        return averageHotSwapTime;
    }
    
    /**
     * Calculates the heap memory usage ratio.
     * @return The heap usage ratio (0.0 to 1.0)
     */
    public double getHeapUsageRatio() {
        if (heapMax <= 0) {
            return 0.0;
        }
        return (double) heapUsed / heapMax;
    }
    
    /**
     * Calculates the non-heap memory usage ratio.
     * @return The non-heap usage ratio (0.0 to 1.0)
     */
    public double getNonHeapUsageRatio() {
        if (nonHeapMax <= 0) {
            return 0.0;
        }
        return (double) nonHeapUsed / nonHeapMax;
    }
    
    /**
     * Checks if this snapshot indicates high memory usage.
     * @param threshold The memory usage threshold (0.0 to 1.0)
     * @return true if memory usage is above threshold, false otherwise
     */
    public boolean isHighMemoryUsage(final double threshold) {
        return getHeapUsageRatio() > threshold;
    }
    
    /**
     * Checks if this snapshot indicates high CPU usage.
     * @param threshold The CPU usage threshold (0.0 to 1.0)
     * @return true if CPU usage is above threshold, false otherwise
     */
    public boolean isHighCpuUsage(final double threshold) {
        return cpuLoad > threshold;
    }
    
    /**
     * Gets a human-readable summary of this snapshot.
     * @return A summary string
     */
    public String getSummary() {
        return String.format(
            "PerformanceSnapshot[timestamp=%s, heap=%.1f%%, cpu=%.1f%%, threads=%d, hotswap=%d ops, avgTime=%.1fms]",
            timestamp,
            getHeapUsageRatio() * 100,
            cpuLoad * 100,
            threadCount,
            hotSwapOperations,
            averageHotSwapTime
        );
    }
    
    @Override
    public String toString() {
        return getSummary();
    }
    
    /**
     * Builder for constructing PerformanceSnapshot instances.
     */
    public static class Builder {
        
        private Instant timestamp;
        private long heapUsed;
        private long heapMax;
        private long nonHeapUsed;
        private long nonHeapMax;
        private double cpuLoad;
        private int threadCount;
        private long gcCollections;
        private long gcTime;
        private Duration uptime;
        private long hotSwapOperations;
        private double averageHotSwapTime;
        
        /**
         * Sets the snapshot timestamp.
         * @param timestamp The snapshot timestamp
         * @return This builder instance
         */
        public Builder timestamp(final Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }
        
        /**
         * Sets the heap memory used.
         * @param heapUsed The heap memory used
         * @return This builder instance
         */
        public Builder heapUsed(final long heapUsed) {
            this.heapUsed = heapUsed;
            return this;
        }
        
        /**
         * Sets the maximum heap memory.
         * @param heapMax The maximum heap memory
         * @return This builder instance
         */
        public Builder heapMax(final long heapMax) {
            this.heapMax = heapMax;
            return this;
        }
        
        /**
         * Sets the non-heap memory used.
         * @param nonHeapUsed The non-heap memory used
         * @return This builder instance
         */
        public Builder nonHeapUsed(final long nonHeapUsed) {
            this.nonHeapUsed = nonHeapUsed;
            return this;
        }
        
        /**
         * Sets the maximum non-heap memory.
         * @param nonHeapMax The maximum non-heap memory
         * @return This builder instance
         */
        public Builder nonHeapMax(final long nonHeapMax) {
            this.nonHeapMax = nonHeapMax;
            return this;
        }
        
        /**
         * Sets the CPU load.
         * @param cpuLoad The CPU load
         * @return This builder instance
         */
        public Builder cpuLoad(final double cpuLoad) {
            this.cpuLoad = cpuLoad;
            return this;
        }
        
        /**
         * Sets the thread count.
         * @param threadCount The thread count
         * @return This builder instance
         */
        public Builder threadCount(final int threadCount) {
            this.threadCount = threadCount;
            return this;
        }
        
        /**
         * Sets the total GC collections.
         * @param gcCollections The total GC collections
         * @return This builder instance
         */
        public Builder gcCollections(final long gcCollections) {
            this.gcCollections = gcCollections;
            return this;
        }
        
        /**
         * Sets the total GC time.
         * @param gcTime The total GC time
         * @return This builder instance
         */
        public Builder gcTime(final long gcTime) {
            this.gcTime = gcTime;
            return this;
        }
        
        /**
         * Sets the JVM uptime.
         * @param uptime The JVM uptime
         * @return This builder instance
         */
        public Builder uptime(final Duration uptime) {
            this.uptime = uptime;
            return this;
        }
        
        /**
         * Sets the total hot-swap operations.
         * @param hotSwapOperations The total hot-swap operations
         * @return This builder instance
         */
        public Builder hotSwapOperations(final long hotSwapOperations) {
            this.hotSwapOperations = hotSwapOperations;
            return this;
        }
        
        /**
         * Sets the average hot-swap time.
         * @param averageHotSwapTime The average hot-swap time
         * @return This builder instance
         */
        public Builder averageHotSwapTime(final double averageHotSwapTime) {
            this.averageHotSwapTime = averageHotSwapTime;
            return this;
        }
        
        /**
         * Builds the PerformanceSnapshot instance.
         * @return A new PerformanceSnapshot instance
         */
        public PerformanceSnapshot build() {
            return new PerformanceSnapshot(
                timestamp,
                heapUsed,
                heapMax,
                nonHeapUsed,
                nonHeapMax,
                cpuLoad,
                threadCount,
                gcCollections,
                gcTime,
                uptime,
                hotSwapOperations,
                averageHotSwapTime
            );
        }
    }
}