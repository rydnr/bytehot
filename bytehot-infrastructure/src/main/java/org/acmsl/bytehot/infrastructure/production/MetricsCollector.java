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
 * Filename: MetricsCollector.java
 *
 * Author: Claude Code
 *
 * Class name: MetricsCollector
 *
 * Responsibilities:
 *   - Collect real-time application and system metrics
 *   - Aggregate metrics data for analysis and reporting
 *   - Support custom metric registration and collection
 *   - Provide metrics export capabilities
 *
 * Collaborators:
 *   - PerformanceMonitor: Uses metrics for performance analysis
 *   - HealthCheckManager: Uses metrics for health assessment
 *   - Metric: Individual metric instances
 *   - MetricsRegistry: Registry of all metrics
 */
package org.acmsl.bytehot.infrastructure.production;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Collects and manages real-time application and system metrics.
 * @author Claude Code
 * @since 2025-07-04
 */
public class MetricsCollector {
    
    /**
     * Default metrics collection interval in seconds.
     */
    public static final int DEFAULT_COLLECTION_INTERVAL_SECONDS = 15;
    
    /**
     * Default metrics retention period in minutes.
     */
    public static final int DEFAULT_RETENTION_MINUTES = 60;
    
    /**
     * Metrics collector configuration.
     */
    private final MetricsCollectorConfiguration configuration;
    
    /**
     * Scheduled executor for periodic metric collection.
     */
    private final ScheduledExecutorService scheduler;
    
    /**
     * Registry of all metrics.
     */
    private final ConcurrentHashMap<String, Metric> metricsRegistry;
    
    /**
     * Historical metrics data.
     */
    private final ConcurrentHashMap<String, MetricHistory> metricsHistory;
    
    /**
     * Total number of metrics collected.
     */
    private final AtomicLong totalMetricsCollected;
    
    /**
     * Number of collection cycles performed.
     */
    private final AtomicLong collectionCycles;
    
    /**
     * Whether metrics collection is active.
     */
    private volatile boolean active;
    
    /**
     * Time when metrics collection started.
     */
    private volatile Instant startTime;
    
    /**
     * Creates a new MetricsCollector with default configuration.
     */
    public MetricsCollector() {
        this(MetricsCollectorConfiguration.defaultConfiguration());
    }
    
    /**
     * Creates a new MetricsCollector with the specified configuration.
     * @param configuration The metrics collector configuration
     */
    public MetricsCollector(final MetricsCollectorConfiguration configuration) {
        this.configuration = configuration;
        this.scheduler = Executors.newScheduledThreadPool(2);
        this.metricsRegistry = new ConcurrentHashMap<>();
        this.metricsHistory = new ConcurrentHashMap<>();
        this.totalMetricsCollected = new AtomicLong(0);
        this.collectionCycles = new AtomicLong(0);
        this.active = false;
        
        // Initialize default metrics
        initializeDefaultMetrics();
    }
    
    /**
     * Starts metrics collection.
     */
    public void startCollection() {
        if (active) {
            return;
        }
        
        active = true;
        startTime = Instant.now();
        
        // Schedule periodic metrics collection
        scheduler.scheduleAtFixedRate(
            this::collectMetrics,
            0,
            configuration.getCollectionInterval().getSeconds(),
            TimeUnit.SECONDS
        );
        
        // Schedule periodic cleanup of old metrics data
        scheduler.scheduleAtFixedRate(
            this::cleanupOldMetrics,
            configuration.getRetentionPeriod().getSeconds(),
            configuration.getRetentionPeriod().getSeconds(),
            TimeUnit.SECONDS
        );
    }
    
    /**
     * Stops metrics collection.
     */
    public void stopCollection() {
        if (!active) {
            return;
        }
        
        active = false;
        
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
     * Registers a custom metric.
     * @param name The metric name
     * @param metric The metric implementation
     */
    public void registerMetric(final String name, final Metric metric) {
        metricsRegistry.put(name, metric);
        metricsHistory.put(name, new MetricHistory(name, configuration.getMaxHistoryPoints()));
    }
    
    /**
     * Unregisters a metric.
     * @param name The metric name
     */
    public void unregisterMetric(final String name) {
        metricsRegistry.remove(name);
        metricsHistory.remove(name);
    }
    
    /**
     * Gets current value of a specific metric.
     * @param name The metric name
     * @return The current metric value, or null if not found
     */
    public MetricValue getCurrentMetricValue(final String name) {
        Metric metric = metricsRegistry.get(name);
        if (metric == null) {
            return null;
        }
        
        try {
            return metric.getValue();
        } catch (Exception e) {
            return MetricValue.error(name, "Error collecting metric: " + e.getMessage());
        }
    }
    
    /**
     * Gets all current metric values.
     * @return Map of metric names to current values
     */
    public Map<String, MetricValue> getAllCurrentMetricValues() {
        Map<String, MetricValue> values = new ConcurrentHashMap<>();
        
        for (Map.Entry<String, Metric> entry : metricsRegistry.entrySet()) {
            String name = entry.getKey();
            MetricValue value = getCurrentMetricValue(name);
            if (value != null) {
                values.put(name, value);
            }
        }
        
        return values;
    }
    
    /**
     * Gets historical data for a specific metric.
     * @param name The metric name
     * @return The metric history, or null if not found
     */
    public MetricHistory getMetricHistory(final String name) {
        return metricsHistory.get(name);
    }
    
    /**
     * Gets all registered metrics.
     * @return Collection of metric names
     */
    public Collection<String> getRegisteredMetrics() {
        return Collections.unmodifiableSet(metricsRegistry.keySet());
    }
    
    /**
     * Gets metrics collection statistics.
     * @return Metrics collection statistics
     */
    public MetricsCollectionStatistics getStatistics() {
        return MetricsCollectionStatistics.builder()
            .active(active)
            .startTime(startTime)
            .totalMetricsCollected(totalMetricsCollected.get())
            .collectionCycles(collectionCycles.get())
            .registeredMetrics(metricsRegistry.size())
            .averageMetricsPerCycle(calculateAverageMetricsPerCycle())
            .uptime(getUptime())
            .build();
    }
    
    /**
     * Exports all metrics in a specific format.
     * @param format The export format
     * @return The exported metrics data
     */
    public String exportMetrics(final MetricsExportFormat format) {
        Map<String, MetricValue> currentValues = getAllCurrentMetricValues();
        
        switch (format) {
            case JSON:
                return exportAsJson(currentValues);
            case PROMETHEUS:
                return exportAsPrometheus(currentValues);
            case CSV:
                return exportAsCsv(currentValues);
            default:
                return exportAsJson(currentValues);
        }
    }
    
    /**
     * Checks if metrics collection is active.
     * @return true if active, false otherwise
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * Gets the uptime of metrics collection.
     * @return The uptime duration
     */
    public Duration getUptime() {
        if (startTime == null) {
            return Duration.ZERO;
        }
        return Duration.between(startTime, Instant.now());
    }
    
    /**
     * Performs metrics collection for all registered metrics.
     */
    protected void collectMetrics() {
        try {
            Instant collectionTime = Instant.now();
            int metricsCollected = 0;
            
            for (Map.Entry<String, Metric> entry : metricsRegistry.entrySet()) {
                String name = entry.getKey();
                Metric metric = entry.getValue();
                
                try {
                    MetricValue value = metric.getValue();
                    value.setTimestamp(collectionTime);
                    
                    // Add to history
                    MetricHistory history = metricsHistory.get(name);
                    if (history != null) {
                        history.addValue(value);
                    }
                    
                    metricsCollected++;
                } catch (Exception e) {
                    // Log error but continue with other metrics
                    System.err.println("Error collecting metric '" + name + "': " + e.getMessage());
                }
            }
            
            totalMetricsCollected.addAndGet(metricsCollected);
            collectionCycles.incrementAndGet();
            
        } catch (Exception e) {
            System.err.println("Error during metrics collection cycle: " + e.getMessage());
        }
    }
    
    /**
     * Cleans up old metrics data based on retention policy.
     */
    protected void cleanupOldMetrics() {
        try {
            Instant cutoffTime = Instant.now().minus(configuration.getRetentionPeriod());
            
            for (MetricHistory history : metricsHistory.values()) {
                history.removeOldValues(cutoffTime);
            }
            
        } catch (Exception e) {
            System.err.println("Error during metrics cleanup: " + e.getMessage());
        }
    }
    
    /**
     * Calculates the average number of metrics collected per cycle.
     * @return The average metrics per cycle
     */
    protected double calculateAverageMetricsPerCycle() {
        long cycles = collectionCycles.get();
        if (cycles == 0) {
            return 0.0;
        }
        
        return (double) totalMetricsCollected.get() / cycles;
    }
    
    /**
     * Initializes default system metrics.
     */
    protected void initializeDefaultMetrics() {
        // JVM metrics
        registerMetric("jvm.memory.heap.used", new HeapMemoryUsedMetric());
        registerMetric("jvm.memory.heap.max", new HeapMemoryMaxMetric());
        registerMetric("jvm.memory.nonheap.used", new NonHeapMemoryUsedMetric());
        registerMetric("jvm.memory.nonheap.max", new NonHeapMemoryMaxMetric());
        registerMetric("jvm.threads.count", new ThreadCountMetric());
        registerMetric("jvm.threads.daemon", new DaemonThreadCountMetric());
        registerMetric("jvm.gc.collections", new GcCollectionsMetric());
        registerMetric("jvm.gc.time", new GcTimeMetric());
        registerMetric("jvm.uptime", new UptimeMetric());
        
        // System metrics
        registerMetric("system.cpu.usage", new CpuUsageMetric());
        registerMetric("system.load.average", new LoadAverageMetric());
        
        // ByteHot specific metrics
        registerMetric("bytehot.hotswap.operations", new HotSwapOperationsMetric());
        registerMetric("bytehot.hotswap.average_time", new HotSwapAverageTimeMetric());
        registerMetric("bytehot.errors.total", new ErrorCountMetric());
        registerMetric("bytehot.optimizations.total", new OptimizationCountMetric());
    }
    
    /**
     * Exports metrics as JSON format.
     * @param values The metric values
     * @return JSON representation
     */
    protected String exportAsJson(final Map<String, MetricValue> values) {
        // TODO: Implement JSON export
        return "{}";
    }
    
    /**
     * Exports metrics as Prometheus format.
     * @param values The metric values
     * @return Prometheus representation
     */
    protected String exportAsPrometheus(final Map<String, MetricValue> values) {
        // TODO: Implement Prometheus export
        return "";
    }
    
    /**
     * Exports metrics as CSV format.
     * @param values The metric values
     * @return CSV representation
     */
    protected String exportAsCsv(final Map<String, MetricValue> values) {
        // TODO: Implement CSV export
        return "";
    }
    
    /**
     * Shuts down the metrics collector.
     */
    public void shutdown() {
        stopCollection();
    }
}