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
 * Filename: PerformanceMonitorConfiguration.java
 *
 * Author: Claude Code
 *
 * Class name: PerformanceMonitorConfiguration
 *
 * Responsibilities:
 *   - Configure performance monitoring parameters
 *   - Define alert thresholds and monitoring intervals
 *   - Support customizable monitoring behavior
 *
 * Collaborators:
 *   - PerformanceMonitor: Uses this configuration for monitoring
 */
package org.acmsl.bytehot.infrastructure.production;

import java.time.Duration;

/**
 * Configuration for performance monitoring behavior.
 * @author Claude Code
 * @since 2025-07-04
 */
public class PerformanceMonitorConfiguration {
    
    /**
     * Default monitoring interval.
     */
    public static final Duration DEFAULT_MONITORING_INTERVAL = Duration.ofSeconds(30);
    
    /**
     * Default memory usage threshold.
     */
    public static final double DEFAULT_MEMORY_THRESHOLD = 0.8;
    
    /**
     * Default CPU usage threshold.
     */
    public static final double DEFAULT_CPU_THRESHOLD = 0.85;
    
    /**
     * Default hot-swap time threshold in milliseconds.
     */
    public static final double DEFAULT_HOTSWAP_TIME_THRESHOLD = 5000.0;
    
    /**
     * Default maximum snapshot history.
     */
    public static final int DEFAULT_MAX_SNAPSHOT_HISTORY = 100;
    
    /**
     * Monitoring interval between metric collections.
     */
    private final Duration monitoringInterval;
    
    /**
     * Memory usage threshold for alerts (0.0 to 1.0).
     */
    private final double memoryThreshold;
    
    /**
     * CPU usage threshold for alerts (0.0 to 1.0).
     */
    private final double cpuThreshold;
    
    /**
     * Hot-swap operation time threshold for alerts (milliseconds).
     */
    private final double hotSwapTimeThreshold;
    
    /**
     * Maximum number of snapshots to keep in memory.
     */
    private final int maxSnapshotHistory;
    
    /**
     * Whether to enable detailed GC monitoring.
     */
    private final boolean enableGcMonitoring;
    
    /**
     * Whether to enable thread monitoring.
     */
    private final boolean enableThreadMonitoring;
    
    /**
     * Whether to enable hot-swap operation tracking.
     */
    private final boolean enableHotSwapTracking;
    
    /**
     * Creates a new PerformanceMonitorConfiguration.
     * @param monitoringInterval The monitoring interval
     * @param memoryThreshold The memory threshold
     * @param cpuThreshold The CPU threshold
     * @param hotSwapTimeThreshold The hot-swap time threshold
     * @param maxSnapshotHistory The maximum snapshot history
     * @param enableGcMonitoring Whether to enable GC monitoring
     * @param enableThreadMonitoring Whether to enable thread monitoring
     * @param enableHotSwapTracking Whether to enable hot-swap tracking
     */
    protected PerformanceMonitorConfiguration(final Duration monitoringInterval,
                                            final double memoryThreshold,
                                            final double cpuThreshold,
                                            final double hotSwapTimeThreshold,
                                            final int maxSnapshotHistory,
                                            final boolean enableGcMonitoring,
                                            final boolean enableThreadMonitoring,
                                            final boolean enableHotSwapTracking) {
        this.monitoringInterval = monitoringInterval;
        this.memoryThreshold = memoryThreshold;
        this.cpuThreshold = cpuThreshold;
        this.hotSwapTimeThreshold = hotSwapTimeThreshold;
        this.maxSnapshotHistory = maxSnapshotHistory;
        this.enableGcMonitoring = enableGcMonitoring;
        this.enableThreadMonitoring = enableThreadMonitoring;
        this.enableHotSwapTracking = enableHotSwapTracking;
    }
    
    /**
     * Creates a default configuration.
     * @return A default performance monitor configuration
     */
    public static PerformanceMonitorConfiguration defaultConfiguration() {
        return new Builder()
            .monitoringInterval(DEFAULT_MONITORING_INTERVAL)
            .memoryThreshold(DEFAULT_MEMORY_THRESHOLD)
            .cpuThreshold(DEFAULT_CPU_THRESHOLD)
            .hotSwapTimeThreshold(DEFAULT_HOTSWAP_TIME_THRESHOLD)
            .maxSnapshotHistory(DEFAULT_MAX_SNAPSHOT_HISTORY)
            .enableGcMonitoring(true)
            .enableThreadMonitoring(true)
            .enableHotSwapTracking(true)
            .build();
    }
    
    /**
     * Creates a production-optimized configuration.
     * @return A production-optimized configuration
     */
    public static PerformanceMonitorConfiguration productionConfiguration() {
        return new Builder()
            .monitoringInterval(Duration.ofMinutes(1))
            .memoryThreshold(0.85)
            .cpuThreshold(0.9)
            .hotSwapTimeThreshold(10000.0)
            .maxSnapshotHistory(200)
            .enableGcMonitoring(true)
            .enableThreadMonitoring(false)
            .enableHotSwapTracking(true)
            .build();
    }
    
    /**
     * Creates a development-friendly configuration.
     * @return A development-optimized configuration
     */
    public static PerformanceMonitorConfiguration developmentConfiguration() {
        return new Builder()
            .monitoringInterval(Duration.ofSeconds(10))
            .memoryThreshold(0.7)
            .cpuThreshold(0.8)
            .hotSwapTimeThreshold(2000.0)
            .maxSnapshotHistory(50)
            .enableGcMonitoring(true)
            .enableThreadMonitoring(true)
            .enableHotSwapTracking(true)
            .build();
    }
    
    /**
     * Creates a builder for constructing configurations.
     * @return A new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Gets the monitoring interval.
     * @return The monitoring interval
     */
    public Duration getMonitoringInterval() {
        return monitoringInterval;
    }
    
    /**
     * Gets the memory threshold.
     * @return The memory threshold
     */
    public double getMemoryThreshold() {
        return memoryThreshold;
    }
    
    /**
     * Gets the CPU threshold.
     * @return The CPU threshold
     */
    public double getCpuThreshold() {
        return cpuThreshold;
    }
    
    /**
     * Gets the hot-swap time threshold.
     * @return The hot-swap time threshold
     */
    public double getHotSwapTimeThreshold() {
        return hotSwapTimeThreshold;
    }
    
    /**
     * Gets the maximum snapshot history.
     * @return The maximum snapshot history
     */
    public int getMaxSnapshotHistory() {
        return maxSnapshotHistory;
    }
    
    /**
     * Checks if GC monitoring is enabled.
     * @return true if GC monitoring is enabled, false otherwise
     */
    public boolean isGcMonitoringEnabled() {
        return enableGcMonitoring;
    }
    
    /**
     * Checks if thread monitoring is enabled.
     * @return true if thread monitoring is enabled, false otherwise
     */
    public boolean isThreadMonitoringEnabled() {
        return enableThreadMonitoring;
    }
    
    /**
     * Checks if hot-swap tracking is enabled.
     * @return true if hot-swap tracking is enabled, false otherwise
     */
    public boolean isHotSwapTrackingEnabled() {
        return enableHotSwapTracking;
    }
    
    /**
     * Builder for constructing PerformanceMonitorConfiguration instances.
     */
    public static class Builder {
        
        private Duration monitoringInterval = DEFAULT_MONITORING_INTERVAL;
        private double memoryThreshold = DEFAULT_MEMORY_THRESHOLD;
        private double cpuThreshold = DEFAULT_CPU_THRESHOLD;
        private double hotSwapTimeThreshold = DEFAULT_HOTSWAP_TIME_THRESHOLD;
        private int maxSnapshotHistory = DEFAULT_MAX_SNAPSHOT_HISTORY;
        private boolean enableGcMonitoring = true;
        private boolean enableThreadMonitoring = true;
        private boolean enableHotSwapTracking = true;
        
        /**
         * Sets the monitoring interval.
         * @param monitoringInterval The monitoring interval
         * @return This builder instance
         */
        public Builder monitoringInterval(final Duration monitoringInterval) {
            this.monitoringInterval = monitoringInterval;
            return this;
        }
        
        /**
         * Sets the memory threshold.
         * @param memoryThreshold The memory threshold
         * @return This builder instance
         */
        public Builder memoryThreshold(final double memoryThreshold) {
            this.memoryThreshold = memoryThreshold;
            return this;
        }
        
        /**
         * Sets the CPU threshold.
         * @param cpuThreshold The CPU threshold
         * @return This builder instance
         */
        public Builder cpuThreshold(final double cpuThreshold) {
            this.cpuThreshold = cpuThreshold;
            return this;
        }
        
        /**
         * Sets the hot-swap time threshold.
         * @param hotSwapTimeThreshold The hot-swap time threshold
         * @return This builder instance
         */
        public Builder hotSwapTimeThreshold(final double hotSwapTimeThreshold) {
            this.hotSwapTimeThreshold = hotSwapTimeThreshold;
            return this;
        }
        
        /**
         * Sets the maximum snapshot history.
         * @param maxSnapshotHistory The maximum snapshot history
         * @return This builder instance
         */
        public Builder maxSnapshotHistory(final int maxSnapshotHistory) {
            this.maxSnapshotHistory = maxSnapshotHistory;
            return this;
        }
        
        /**
         * Sets whether to enable GC monitoring.
         * @param enableGcMonitoring Whether to enable GC monitoring
         * @return This builder instance
         */
        public Builder enableGcMonitoring(final boolean enableGcMonitoring) {
            this.enableGcMonitoring = enableGcMonitoring;
            return this;
        }
        
        /**
         * Sets whether to enable thread monitoring.
         * @param enableThreadMonitoring Whether to enable thread monitoring
         * @return This builder instance
         */
        public Builder enableThreadMonitoring(final boolean enableThreadMonitoring) {
            this.enableThreadMonitoring = enableThreadMonitoring;
            return this;
        }
        
        /**
         * Sets whether to enable hot-swap tracking.
         * @param enableHotSwapTracking Whether to enable hot-swap tracking
         * @return This builder instance
         */
        public Builder enableHotSwapTracking(final boolean enableHotSwapTracking) {
            this.enableHotSwapTracking = enableHotSwapTracking;
            return this;
        }
        
        /**
         * Builds the PerformanceMonitorConfiguration instance.
         * @return A new PerformanceMonitorConfiguration instance
         */
        public PerformanceMonitorConfiguration build() {
            return new PerformanceMonitorConfiguration(
                monitoringInterval,
                memoryThreshold,
                cpuThreshold,
                hotSwapTimeThreshold,
                maxSnapshotHistory,
                enableGcMonitoring,
                enableThreadMonitoring,
                enableHotSwapTracking
            );
        }
    }
}