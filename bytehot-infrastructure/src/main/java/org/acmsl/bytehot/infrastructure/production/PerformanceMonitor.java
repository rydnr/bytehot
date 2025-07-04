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
 * Filename: PerformanceMonitor.java
 *
 * Author: Claude Code
 *
 * Class name: PerformanceMonitor
 *
 * Responsibilities:
 *   - Monitor JVM and application performance metrics
 *   - Track memory usage, CPU utilization, and GC activity
 *   - Collect hot-swap operation performance data
 *   - Provide real-time performance insights
 *
 * Collaborators:
 *   - MetricsCollector: Collects and aggregates metrics data
 *   - PerformanceSnapshot: Represents point-in-time performance data
 *   - PerformanceAlert: Triggers when thresholds are exceeded
 */
package org.acmsl.bytehot.infrastructure.production;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Monitors JVM and application performance metrics for production environments.
 * @author Claude Code
 * @since 2025-07-04
 */
public class PerformanceMonitor {
    
    /**
     * Default monitoring interval in seconds.
     */
    public static final int DEFAULT_MONITORING_INTERVAL_SECONDS = 30;
    
    /**
     * Default memory usage alert threshold (80%).
     */
    public static final double DEFAULT_MEMORY_THRESHOLD = 0.8;
    
    /**
     * Default CPU usage alert threshold (85%).
     */
    public static final double DEFAULT_CPU_THRESHOLD = 0.85;
    
    /**
     * JVM memory management bean.
     */
    private final MemoryMXBean memoryBean;
    
    /**
     * JVM operating system bean.
     */
    private final OperatingSystemMXBean osBean;
    
    /**
     * JVM runtime bean.
     */
    private final RuntimeMXBean runtimeBean;
    
    /**
     * JVM thread management bean.
     */
    private final ThreadMXBean threadBean;
    
    /**
     * JVM garbage collector beans.
     */
    private final List<GarbageCollectorMXBean> gcBeans;
    
    /**
     * Scheduled executor for periodic monitoring.
     */
    private final ScheduledExecutorService scheduler;
    
    /**
     * Configuration for performance monitoring.
     */
    private final PerformanceMonitorConfiguration configuration;
    
    /**
     * Collection of performance snapshots.
     */
    private final List<PerformanceSnapshot> snapshots;
    
    /**
     * Hot-swap operation metrics.
     */
    private final ConcurrentHashMap<String, OperationMetrics> operationMetrics;
    
    /**
     * Total number of hot-swap operations.
     */
    private final AtomicLong totalHotSwapOperations;
    
    /**
     * Total hot-swap operation time.
     */
    private final AtomicLong totalHotSwapTime;
    
    /**
     * Whether monitoring is currently active.
     */
    private volatile boolean monitoring;
    
    /**
     * Time when monitoring started.
     */
    private volatile Instant monitoringStartTime;
    
    /**
     * Creates a new PerformanceMonitor with default configuration.
     */
    public PerformanceMonitor() {
        this(PerformanceMonitorConfiguration.defaultConfiguration());
    }
    
    /**
     * Creates a new PerformanceMonitor with the specified configuration.
     * @param configuration The monitoring configuration
     */
    public PerformanceMonitor(final PerformanceMonitorConfiguration configuration) {
        this.configuration = configuration;
        this.memoryBean = ManagementFactory.getMemoryMXBean();
        this.osBean = ManagementFactory.getOperatingSystemMXBean();
        this.runtimeBean = ManagementFactory.getRuntimeMXBean();
        this.threadBean = ManagementFactory.getThreadMXBean();
        this.gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.snapshots = new ArrayList<>();
        this.operationMetrics = new ConcurrentHashMap<>();
        this.totalHotSwapOperations = new AtomicLong(0);
        this.totalHotSwapTime = new AtomicLong(0);
        this.monitoring = false;
    }
    
    /**
     * Starts performance monitoring.
     */
    public void startMonitoring() {
        if (monitoring) {
            return;
        }
        
        monitoring = true;
        monitoringStartTime = Instant.now();
        
        scheduler.scheduleAtFixedRate(
            this::collectMetrics,
            0,
            configuration.getMonitoringInterval().getSeconds(),
            TimeUnit.SECONDS
        );
    }
    
    /**
     * Stops performance monitoring.
     */
    public void stopMonitoring() {
        if (!monitoring) {
            return;
        }
        
        monitoring = false;
        scheduler.shutdown();
        
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Records a hot-swap operation performance.
     * @param operationType The type of operation
     * @param duration The operation duration
     * @param successful Whether the operation was successful
     */
    public void recordHotSwapOperation(final String operationType, 
                                     final Duration duration, 
                                     final boolean successful) {
        totalHotSwapOperations.incrementAndGet();
        totalHotSwapTime.addAndGet(duration.toMillis());
        
        operationMetrics.compute(operationType, (key, existing) -> {
            if (existing == null) {
                return new OperationMetrics(operationType, 1, duration.toMillis(), 
                                          successful ? 1 : 0);
            }
            
            return existing.withOperation(duration.toMillis(), successful);
        });
    }
    
    /**
     * Gets the current performance snapshot.
     * @return The current performance snapshot
     */
    public PerformanceSnapshot getCurrentSnapshot() {
        return createSnapshot();
    }
    
    /**
     * Gets all collected performance snapshots.
     * @return List of performance snapshots
     */
    public List<PerformanceSnapshot> getAllSnapshots() {
        return new ArrayList<>(snapshots);
    }
    
    /**
     * Gets performance statistics for the monitoring period.
     * @return Performance statistics
     */
    public PerformanceStatistics getStatistics() {
        if (snapshots.isEmpty()) {
            return PerformanceStatistics.empty();
        }
        
        return calculateStatistics();
    }
    
    /**
     * Gets hot-swap operation metrics.
     * @return Map of operation metrics by type
     */
    public ConcurrentHashMap<String, OperationMetrics> getOperationMetrics() {
        return new ConcurrentHashMap<>(operationMetrics);
    }
    
    /**
     * Checks if monitoring is currently active.
     * @return true if monitoring is active, false otherwise
     */
    public boolean isMonitoring() {
        return monitoring;
    }
    
    /**
     * Gets the monitoring uptime.
     * @return The monitoring uptime duration
     */
    public Duration getMonitoringUptime() {
        if (monitoringStartTime == null) {
            return Duration.ZERO;
        }
        
        return Duration.between(monitoringStartTime, Instant.now());
    }
    
    /**
     * Collects current performance metrics.
     */
    protected void collectMetrics() {
        try {
            PerformanceSnapshot snapshot = createSnapshot();
            snapshots.add(snapshot);
            
            // Limit snapshot history to prevent memory issues
            if (snapshots.size() > configuration.getMaxSnapshotHistory()) {
                snapshots.remove(0);
            }
            
            // Check for performance alerts
            checkPerformanceThresholds(snapshot);
            
        } catch (Exception e) {
            // Log error but don't stop monitoring
            System.err.println("Error collecting performance metrics: " + e.getMessage());
        }
    }
    
    /**
     * Creates a performance snapshot from current JVM state.
     * @return A new performance snapshot
     */
    protected PerformanceSnapshot createSnapshot() {
        MemoryUsage heapMemory = memoryBean.getHeapMemoryUsage();
        MemoryUsage nonHeapMemory = memoryBean.getNonHeapMemoryUsage();
        
        return PerformanceSnapshot.builder()
            .timestamp(Instant.now())
            .heapUsed(heapMemory.getUsed())
            .heapMax(heapMemory.getMax())
            .nonHeapUsed(nonHeapMemory.getUsed())
            .nonHeapMax(nonHeapMemory.getMax())
            .cpuLoad(getCpuLoad())
            .threadCount(threadBean.getThreadCount())
            .gcCollections(getTotalGcCollections())
            .gcTime(getTotalGcTime())
            .uptime(Duration.ofMillis(runtimeBean.getUptime()))
            .hotSwapOperations(totalHotSwapOperations.get())
            .averageHotSwapTime(calculateAverageHotSwapTime())
            .build();
    }
    
    /**
     * Gets the current CPU load.
     * @return CPU load as a percentage (0.0 to 1.0)
     */
    protected double getCpuLoad() {
        if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
            return ((com.sun.management.OperatingSystemMXBean) osBean).getCpuLoad();
        }
        
        return -1.0; // Not available
    }
    
    /**
     * Gets the total number of garbage collections.
     * @return Total GC collections
     */
    protected long getTotalGcCollections() {
        return gcBeans.stream()
            .mapToLong(GarbageCollectorMXBean::getCollectionCount)
            .sum();
    }
    
    /**
     * Gets the total time spent in garbage collection.
     * @return Total GC time in milliseconds
     */
    protected long getTotalGcTime() {
        return gcBeans.stream()
            .mapToLong(GarbageCollectorMXBean::getCollectionTime)
            .sum();
    }
    
    /**
     * Calculates the average hot-swap operation time.
     * @return Average operation time in milliseconds
     */
    protected double calculateAverageHotSwapTime() {
        long operations = totalHotSwapOperations.get();
        if (operations == 0) {
            return 0.0;
        }
        
        return (double) totalHotSwapTime.get() / operations;
    }
    
    /**
     * Checks performance thresholds and triggers alerts if exceeded.
     * @param snapshot The current performance snapshot
     */
    protected void checkPerformanceThresholds(final PerformanceSnapshot snapshot) {
        // Check memory usage
        double heapUsageRatio = (double) snapshot.getHeapUsed() / snapshot.getHeapMax();
        if (heapUsageRatio > configuration.getMemoryThreshold()) {
            handlePerformanceAlert(PerformanceAlert.memoryAlert(heapUsageRatio, snapshot));
        }
        
        // Check CPU usage
        if (snapshot.getCpuLoad() > configuration.getCpuThreshold()) {
            handlePerformanceAlert(PerformanceAlert.cpuAlert(snapshot.getCpuLoad(), snapshot));
        }
        
        // Check hot-swap performance
        if (snapshot.getAverageHotSwapTime() > configuration.getHotSwapTimeThreshold()) {
            handlePerformanceAlert(PerformanceAlert.hotSwapAlert(snapshot.getAverageHotSwapTime(), snapshot));
        }
    }
    
    /**
     * Handles a performance alert.
     * @param alert The performance alert
     */
    protected void handlePerformanceAlert(final PerformanceAlert alert) {
        // TODO: Implement alert handling (logging, notifications, etc.)
        System.err.println("PERFORMANCE ALERT: " + alert.getMessage());
    }
    
    /**
     * Calculates performance statistics from collected snapshots.
     * @return Performance statistics
     */
    protected PerformanceStatistics calculateStatistics() {
        // TODO: Implement comprehensive statistics calculation
        return PerformanceStatistics.empty();
    }
    
    /**
     * Shuts down the performance monitor.
     */
    public void shutdown() {
        stopMonitoring();
    }
}