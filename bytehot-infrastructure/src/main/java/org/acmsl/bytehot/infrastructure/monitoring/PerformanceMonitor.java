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
 *   - Monitor ByteHot performance metrics in real-time
 *   - Track hot-swap operation performance and success rates
 *   - Provide performance analysis and optimization recommendations
 *   - Generate performance reports and alerting
 *
 * Collaborators:
 *   - MetricsCollector: Gathers performance data points
 *   - AlertManager: Handles performance alerts and notifications
 *   - PerformanceAnalyzer: Analyzes trends and patterns
 *   - HotSwapCache: Monitors caching performance
 */
package org.acmsl.bytehot.infrastructure.monitoring;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ThreadMXBean;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Comprehensive performance monitoring system for ByteHot operations.
 * Tracks metrics, analyzes performance, and provides alerting capabilities.
 * @author Claude Code
 * @since 2025-07-06
 */
public class PerformanceMonitor {

    private static final PerformanceMonitor INSTANCE = new PerformanceMonitor();
    
    private final Map<String, PerformanceMetric> metrics = new ConcurrentHashMap<>();
    private final Map<String, PerformanceThreshold> thresholds = new ConcurrentHashMap<>();
    private final Map<String, AlertRule> alertRules = new ConcurrentHashMap<>();
    
    private final ScheduledExecutorService monitoringExecutor = 
        Executors.newScheduledThreadPool(2, r -> {
            Thread t = new Thread(r, "ByteHot-Performance-Monitor");
            t.setDaemon(true);
            return t;
        });

    private final AtomicLong totalHotSwapOperations = new AtomicLong(0);
    private final AtomicLong successfulHotSwapOperations = new AtomicLong(0);
    private final AtomicLong failedHotSwapOperations = new AtomicLong(0);
    private final LongAdder totalHotSwapDuration = new LongAdder();
    
    private volatile boolean monitoringEnabled = true;
    private volatile Instant startTime = Instant.now();
    
    private PerformanceMonitor() {
        initializeDefaultMetrics();
        initializeDefaultThresholds();
        initializeDefaultAlertRules();
        startPerformanceMonitoring();
    }

    /**
     * Gets the singleton instance of PerformanceMonitor.
     * @return The performance monitor instance
     */
    public static PerformanceMonitor getInstance() {
        return INSTANCE;
    }

    /**
     * Records a hot-swap operation performance metric.
     * This method can be hot-swapped to change operation recording behavior.
     * @param className Name of the class being hot-swapped
     * @param operation Type of operation (LOAD, REDEFINE, RETRANSFORM)
     * @param duration Duration of the operation
     * @param success Whether the operation succeeded
     */
    public void recordHotSwapOperation(final String className, 
                                     final HotSwapOperation operation, 
                                     final Duration duration, 
                                     final boolean success) {
        
        if (!monitoringEnabled) {
            return;
        }
        
        totalHotSwapOperations.incrementAndGet();
        
        if (success) {
            successfulHotSwapOperations.incrementAndGet();
        } else {
            failedHotSwapOperations.incrementAndGet();
        }
        
        totalHotSwapDuration.add(duration.toNanos());
        
        // Record operation-specific metrics
        final String operationKey = operation.name().toLowerCase() + "_operations";
        recordMetric(operationKey, duration.toMillis(), MetricType.DURATION);
        
        // Record class-specific metrics
        final String classKey = "class_" + className.replace('.', '_');
        recordMetric(classKey, duration.toMillis(), MetricType.DURATION);
        
        // Check performance thresholds
        checkPerformanceThresholds(className, operation, duration, success);
    }

    /**
     * Records a general performance metric.
     * This method can be hot-swapped to change metric recording behavior.
     * @param metricName Name of the metric
     * @param value Value of the metric
     * @param type Type of metric
     */
    public void recordMetric(final String metricName, final double value, final MetricType type) {
        if (!monitoringEnabled) {
            return;
        }
        
        final PerformanceMetric metric = metrics.computeIfAbsent(metricName, 
            k -> new PerformanceMetric(metricName, type));
        
        metric.recordValue(value);
    }

    /**
     * Records startup performance metrics.
     * This method can be hot-swapped to change startup recording behavior.
     * @param phase Startup phase name
     * @param duration Duration of the phase
     */
    public void recordStartupMetric(final String phase, final Duration duration) {
        recordMetric("startup_" + phase, duration.toMillis(), MetricType.DURATION);
    }

    /**
     * Records cache performance metrics.
     * This method can be hot-swapped to change cache recording behavior.
     * @param operation Cache operation type
     * @param hit Whether it was a cache hit
     * @param duration Duration of the operation
     */
    public void recordCacheMetric(final String operation, final boolean hit, final Duration duration) {
        recordMetric("cache_" + operation, duration.toNanos(), MetricType.DURATION);
        recordMetric("cache_hit_rate", hit ? 1.0 : 0.0, MetricType.RATIO);
    }

    /**
     * Gets current performance statistics.
     * @return Current performance statistics
     */
    public PerformanceStatistics getPerformanceStatistics() {
        final long totalOps = totalHotSwapOperations.get();
        final long successfulOps = successfulHotSwapOperations.get();
        final long failedOps = failedHotSwapOperations.get();
        final long totalDuration = totalHotSwapDuration.sum();
        
        final double successRate = totalOps > 0 ? (double) successfulOps / totalOps : 0.0;
        final double averageDuration = totalOps > 0 ? totalDuration / (double) totalOps / 1_000_000.0 : 0.0;
        
        return new PerformanceStatistics(
            totalOps,
            successfulOps,
            failedOps,
            successRate,
            averageDuration,
            Duration.between(startTime, Instant.now()),
            getCurrentJvmMetrics(),
            getTopMetrics()
        );
    }

    /**
     * Sets a performance threshold for alerting.
     * This method can be hot-swapped to change threshold configuration.
     * @param metricName Name of the metric
     * @param threshold Threshold configuration
     */
    public void setPerformanceThreshold(final String metricName, final PerformanceThreshold threshold) {
        thresholds.put(metricName, threshold);
    }

    /**
     * Adds an alert rule for performance monitoring.
     * This method can be hot-swapped to change alert rule configuration.
     * @param ruleName Name of the alert rule
     * @param rule Alert rule configuration
     */
    public void addAlertRule(final String ruleName, final AlertRule rule) {
        alertRules.put(ruleName, rule);
    }

    /**
     * Generates a performance report.
     * This method can be hot-swapped to change report generation behavior.
     * @return Detailed performance report
     */
    public PerformanceReport generateReport() {
        final PerformanceStatistics stats = getPerformanceStatistics();
        final List<String> recommendations = generateOptimizationRecommendations(stats);
        final List<String> alerts = getActiveAlerts();
        
        return new PerformanceReport(
            Instant.now(),
            stats,
            recommendations,
            alerts,
            getMetricTrends(),
            getPerformanceProfile()
        );
    }

    /**
     * Enables or disables performance monitoring.
     * This method can be hot-swapped to change monitoring state.
     * @param enabled Whether monitoring should be enabled
     */
    public void setMonitoringEnabled(final boolean enabled) {
        this.monitoringEnabled = enabled;
        if (enabled) {
            System.out.println("Performance monitoring enabled");
        } else {
            System.out.println("Performance monitoring disabled");
        }
    }

    /**
     * Resets all performance metrics.
     */
    public void resetMetrics() {
        metrics.clear();
        totalHotSwapOperations.set(0);
        successfulHotSwapOperations.set(0);
        failedHotSwapOperations.set(0);
        totalHotSwapDuration.reset();
        startTime = Instant.now();
        System.out.println("Performance metrics reset");
    }

    /**
     * Shuts down the performance monitor.
     */
    public void shutdown() {
        monitoringExecutor.shutdown();
        try {
            if (!monitoringExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                monitoringExecutor.shutdownNow();
            }
        } catch (final InterruptedException e) {
            monitoringExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Initializes default performance metrics.
     * This method can be hot-swapped to change default metrics.
     */
    protected void initializeDefaultMetrics() {
        metrics.put("hot_swap_operations", new PerformanceMetric("hot_swap_operations", MetricType.COUNTER));
        metrics.put("cache_operations", new PerformanceMetric("cache_operations", MetricType.COUNTER));
        metrics.put("startup_time", new PerformanceMetric("startup_time", MetricType.DURATION));
        metrics.put("memory_usage", new PerformanceMetric("memory_usage", MetricType.GAUGE));
    }

    /**
     * Initializes default performance thresholds.
     * This method can be hot-swapped to change default thresholds.
     */
    protected void initializeDefaultThresholds() {
        thresholds.put("hot_swap_duration", new PerformanceThreshold("hot_swap_duration", 5000.0, 10000.0));
        thresholds.put("success_rate", new PerformanceThreshold("success_rate", 0.95, 0.90));
        thresholds.put("memory_usage", new PerformanceThreshold("memory_usage", 0.80, 0.90));
        thresholds.put("cache_hit_rate", new PerformanceThreshold("cache_hit_rate", 0.80, 0.70));
    }

    /**
     * Initializes default alert rules.
     * This method can be hot-swapped to change default alert rules.
     */
    protected void initializeDefaultAlertRules() {
        alertRules.put("high_failure_rate", new AlertRule(
            "high_failure_rate",
            "Hot-swap failure rate exceeds threshold",
            AlertSeverity.WARNING,
            stats -> stats.getSuccessRate() < 0.90
        ));
        
        alertRules.put("slow_operations", new AlertRule(
            "slow_operations", 
            "Hot-swap operations are taking too long",
            AlertSeverity.WARNING,
            stats -> stats.getAverageOperationDuration() > 5000.0
        ));
        
        alertRules.put("memory_pressure", new AlertRule(
            "memory_pressure",
            "High memory usage detected",
            AlertSeverity.CRITICAL,
            stats -> stats.getJvmMetrics().getMemoryUsageRatio() > 0.90
        ));
    }

    /**
     * Starts performance monitoring tasks.
     * This method can be hot-swapped to change monitoring behavior.
     */
    protected void startPerformanceMonitoring() {
        // Schedule system metrics collection
        monitoringExecutor.scheduleAtFixedRate(
            this::collectSystemMetrics,
            0, 30, TimeUnit.SECONDS
        );
        
        // Schedule alert checking
        monitoringExecutor.scheduleAtFixedRate(
            this::checkAlerts,
            0, 60, TimeUnit.SECONDS
        );
        
        // Schedule performance analysis
        monitoringExecutor.scheduleAtFixedRate(
            this::analyzePerformance,
            0, 5, TimeUnit.MINUTES
        );
    }

    /**
     * Collects system-level metrics.
     * This method can be hot-swapped to change system metrics collection.
     */
    protected void collectSystemMetrics() {
        if (!monitoringEnabled) {
            return;
        }
        
        try {
            final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
            final ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
            
            // Memory metrics
            final long usedMemory = memoryBean.getHeapMemoryUsage().getUsed();
            final long maxMemory = memoryBean.getHeapMemoryUsage().getMax();
            final double memoryRatio = maxMemory > 0 ? (double) usedMemory / maxMemory : 0.0;
            
            recordMetric("jvm_memory_used", usedMemory, MetricType.GAUGE);
            recordMetric("jvm_memory_ratio", memoryRatio, MetricType.RATIO);
            
            // Thread metrics
            recordMetric("jvm_threads_active", threadBean.getThreadCount(), MetricType.GAUGE);
            recordMetric("jvm_threads_peak", threadBean.getPeakThreadCount(), MetricType.GAUGE);
            
            // GC metrics
            final List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
            long totalGcTime = 0;
            long totalGcCount = 0;
            
            for (final GarbageCollectorMXBean gcBean : gcBeans) {
                totalGcTime += gcBean.getCollectionTime();
                totalGcCount += gcBean.getCollectionCount();
            }
            
            recordMetric("jvm_gc_time", totalGcTime, MetricType.COUNTER);
            recordMetric("jvm_gc_count", totalGcCount, MetricType.COUNTER);
            
        } catch (final Exception e) {
            System.err.println("Error collecting system metrics: " + e.getMessage());
        }
    }

    /**
     * Checks performance thresholds against current metrics.
     * This method can be hot-swapped to change threshold checking behavior.
     * @param className Name of the class
     * @param operation Hot-swap operation type
     * @param duration Operation duration
     * @param success Operation success status
     */
    protected void checkPerformanceThresholds(final String className, 
                                            final HotSwapOperation operation, 
                                            final Duration duration, 
                                            final boolean success) {
        
        final PerformanceThreshold durationThreshold = thresholds.get("hot_swap_duration");
        if (durationThreshold != null && duration.toMillis() > durationThreshold.getWarningLevel()) {
            final String message = String.format(
                "Slow hot-swap operation detected: %s.%s took %dms", 
                className, operation, duration.toMillis());
            
            if (duration.toMillis() > durationThreshold.getCriticalLevel()) {
                triggerAlert("slow_operation", AlertSeverity.CRITICAL, message);
            } else {
                triggerAlert("slow_operation", AlertSeverity.WARNING, message);
            }
        }
        
        if (!success) {
            triggerAlert("operation_failure", AlertSeverity.WARNING, 
                "Hot-swap operation failed: " + className + "." + operation);
        }
    }

    /**
     * Checks all configured alert rules.
     * This method can be hot-swapped to change alert checking behavior.
     */
    protected void checkAlerts() {
        if (!monitoringEnabled) {
            return;
        }
        
        final PerformanceStatistics stats = getPerformanceStatistics();
        
        for (final AlertRule rule : alertRules.values()) {
            if (rule.evaluate(stats)) {
                triggerAlert(rule.getName(), rule.getSeverity(), rule.getMessage());
            }
        }
    }

    /**
     * Analyzes performance trends and patterns.
     * This method can be hot-swapped to change analysis behavior.
     */
    protected void analyzePerformance() {
        if (!monitoringEnabled) {
            return;
        }
        
        final PerformanceStatistics stats = getPerformanceStatistics();
        
        // Log performance summary
        System.out.println(String.format(
            "ByteHot Performance Summary: %d operations, %.1f%% success rate, %.1fms avg duration",
            stats.getTotalOperations(),
            stats.getSuccessRate() * 100,
            stats.getAverageOperationDuration()
        ));
        
        // Generate recommendations if performance is suboptimal
        final List<String> recommendations = generateOptimizationRecommendations(stats);
        if (!recommendations.isEmpty()) {
            System.out.println("Performance optimization recommendations:");
            recommendations.forEach(rec -> System.out.println("  - " + rec));
        }
    }

    /**
     * Generates optimization recommendations based on performance statistics.
     * This method can be hot-swapped to change recommendation logic.
     * @param stats Current performance statistics
     * @return List of optimization recommendations
     */
    protected List<String> generateOptimizationRecommendations(final PerformanceStatistics stats) {
        final List<String> recommendations = new java.util.ArrayList<>();
        
        if (stats.getSuccessRate() < 0.95) {
            recommendations.add("Consider reviewing failed hot-swap operations for common patterns");
        }
        
        if (stats.getAverageOperationDuration() > 3000.0) {
            recommendations.add("Hot-swap operations are slow - consider optimizing bytecode transformation");
        }
        
        if (stats.getJvmMetrics().getMemoryUsageRatio() > 0.80) {
            recommendations.add("High memory usage detected - consider tuning JVM heap size");
        }
        
        final Optional<PerformanceMetric> cacheHitRate = Optional.ofNullable(metrics.get("cache_hit_rate"));
        if (cacheHitRate.isPresent() && cacheHitRate.get().getAverage() < 0.70) {
            recommendations.add("Low cache hit rate - consider warming up caches or increasing cache size");
        }
        
        return recommendations;
    }

    /**
     * Triggers a performance alert.
     * This method can be hot-swapped to change alert triggering behavior.
     * @param alertName Name of the alert
     * @param severity Alert severity
     * @param message Alert message
     */
    protected void triggerAlert(final String alertName, final AlertSeverity severity, final String message) {
        final String severityPrefix = severity == AlertSeverity.CRITICAL ? "CRITICAL" : "WARNING";
        System.err.println(String.format("[%s] ByteHot Alert - %s: %s", 
            severityPrefix, alertName, message));
    }

    /**
     * Gets current JVM metrics.
     * @return Current JVM performance metrics
     */
    protected JvmMetrics getCurrentJvmMetrics() {
        final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        final ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        
        return new JvmMetrics(
            memoryBean.getHeapMemoryUsage().getUsed(),
            memoryBean.getHeapMemoryUsage().getMax(),
            threadBean.getThreadCount(),
            threadBean.getPeakThreadCount()
        );
    }

    /**
     * Gets top performance metrics.
     * @return Map of top performance metrics
     */
    protected Map<String, Double> getTopMetrics() {
        return metrics.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().getAverage(),
                (a, b) -> a,
                java.util.LinkedHashMap::new
            ));
    }

    /**
     * Gets active alerts.
     * @return List of active alert messages
     */
    protected List<String> getActiveAlerts() {
        // This would maintain a list of active alerts in a real implementation
        return List.of();
    }

    /**
     * Gets metric trends.
     * @return Map of metric trends
     */
    protected Map<String, String> getMetricTrends() {
        // This would analyze trends over time in a real implementation
        return Map.of();
    }

    /**
     * Gets performance profile.
     * @return Performance profile summary
     */
    protected String getPerformanceProfile() {
        final PerformanceStatistics stats = getPerformanceStatistics();
        
        if (stats.getSuccessRate() > 0.98 && stats.getAverageOperationDuration() < 1000.0) {
            return "EXCELLENT";
        } else if (stats.getSuccessRate() > 0.95 && stats.getAverageOperationDuration() < 3000.0) {
            return "GOOD";
        } else if (stats.getSuccessRate() > 0.90 && stats.getAverageOperationDuration() < 5000.0) {
            return "FAIR";
        } else {
            return "POOR";
        }
    }

    // Enums and supporting classes
    
    public enum HotSwapOperation {
        LOAD, REDEFINE, RETRANSFORM, VALIDATE, TRANSFORM
    }

    public enum MetricType {
        COUNTER, GAUGE, DURATION, RATIO
    }

    public enum AlertSeverity {
        WARNING, CRITICAL
    }

    // Static inner classes for data structures
    
    public static class PerformanceMetric {
        private final String name;
        private final MetricType type;
        private final LongAdder count = new LongAdder();
        private final LongAdder sum = new LongAdder();
        private volatile double min = Double.MAX_VALUE;
        private volatile double max = Double.MIN_VALUE;
        private volatile double last = 0.0;

        public PerformanceMetric(final String name, final MetricType type) {
            this.name = name;
            this.type = type;
        }

        public synchronized void recordValue(final double value) {
            count.increment();
            sum.add((long) value);
            last = value;
            min = Math.min(min, value);
            max = Math.max(max, value);
        }

        public String getName() { return name; }
        public MetricType getType() { return type; }
        public long getCount() { return count.sum(); }
        public double getAverage() { return count.sum() > 0 ? sum.sum() / (double) count.sum() : 0.0; }
        public double getMin() { return min == Double.MAX_VALUE ? 0.0 : min; }
        public double getMax() { return max == Double.MIN_VALUE ? 0.0 : max; }
        public double getLast() { return last; }
    }

    public static class PerformanceThreshold {
        private final String metricName;
        private final double warningLevel;
        private final double criticalLevel;

        public PerformanceThreshold(final String metricName, final double warningLevel, final double criticalLevel) {
            this.metricName = metricName;
            this.warningLevel = warningLevel;
            this.criticalLevel = criticalLevel;
        }

        public String getMetricName() { return metricName; }
        public double getWarningLevel() { return warningLevel; }
        public double getCriticalLevel() { return criticalLevel; }
    }

    public static class AlertRule {
        private final String name;
        private final String message;
        private final AlertSeverity severity;
        private final java.util.function.Predicate<PerformanceStatistics> condition;

        public AlertRule(final String name, final String message, final AlertSeverity severity, 
                        final java.util.function.Predicate<PerformanceStatistics> condition) {
            this.name = name;
            this.message = message;
            this.severity = severity;
            this.condition = condition;
        }

        public String getName() { return name; }
        public String getMessage() { return message; }
        public AlertSeverity getSeverity() { return severity; }
        public boolean evaluate(final PerformanceStatistics stats) { return condition.test(stats); }
    }

    public static class PerformanceStatistics {
        private final long totalOperations;
        private final long successfulOperations;
        private final long failedOperations;
        private final double successRate;
        private final double averageOperationDuration;
        private final Duration uptime;
        private final JvmMetrics jvmMetrics;
        private final Map<String, Double> topMetrics;

        public PerformanceStatistics(final long totalOperations, final long successfulOperations,
                                   final long failedOperations, final double successRate,
                                   final double averageOperationDuration, final Duration uptime,
                                   final JvmMetrics jvmMetrics, final Map<String, Double> topMetrics) {
            this.totalOperations = totalOperations;
            this.successfulOperations = successfulOperations;
            this.failedOperations = failedOperations;
            this.successRate = successRate;
            this.averageOperationDuration = averageOperationDuration;
            this.uptime = uptime;
            this.jvmMetrics = jvmMetrics;
            this.topMetrics = topMetrics;
        }

        public long getTotalOperations() { return totalOperations; }
        public long getSuccessfulOperations() { return successfulOperations; }
        public long getFailedOperations() { return failedOperations; }
        public double getSuccessRate() { return successRate; }
        public double getAverageOperationDuration() { return averageOperationDuration; }
        public Duration getUptime() { return uptime; }
        public JvmMetrics getJvmMetrics() { return jvmMetrics; }
        public Map<String, Double> getTopMetrics() { return topMetrics; }
    }

    public static class JvmMetrics {
        private final long memoryUsed;
        private final long memoryMax;
        private final int threadCount;
        private final int peakThreadCount;

        public JvmMetrics(final long memoryUsed, final long memoryMax, 
                         final int threadCount, final int peakThreadCount) {
            this.memoryUsed = memoryUsed;
            this.memoryMax = memoryMax;
            this.threadCount = threadCount;
            this.peakThreadCount = peakThreadCount;
        }

        public long getMemoryUsed() { return memoryUsed; }
        public long getMemoryMax() { return memoryMax; }
        public double getMemoryUsageRatio() { return memoryMax > 0 ? (double) memoryUsed / memoryMax : 0.0; }
        public int getThreadCount() { return threadCount; }
        public int getPeakThreadCount() { return peakThreadCount; }
    }

    public static class PerformanceReport {
        private final Instant generatedAt;
        private final PerformanceStatistics statistics;
        private final List<String> recommendations;
        private final List<String> alerts;
        private final Map<String, String> metricTrends;
        private final String performanceProfile;

        public PerformanceReport(final Instant generatedAt, final PerformanceStatistics statistics,
                               final List<String> recommendations, final List<String> alerts,
                               final Map<String, String> metricTrends, final String performanceProfile) {
            this.generatedAt = generatedAt;
            this.statistics = statistics;
            this.recommendations = recommendations;
            this.alerts = alerts;
            this.metricTrends = metricTrends;
            this.performanceProfile = performanceProfile;
        }

        public Instant getGeneratedAt() { return generatedAt; }
        public PerformanceStatistics getStatistics() { return statistics; }
        public List<String> getRecommendations() { return recommendations; }
        public List<String> getAlerts() { return alerts; }
        public Map<String, String> getMetricTrends() { return metricTrends; }
        public String getPerformanceProfile() { return performanceProfile; }
    }
}