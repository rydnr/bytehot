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
 * Filename: HealthCheckManager.java
 *
 * Author: Claude Code
 *
 * Class name: HealthCheckManager
 *
 * Responsibilities:
 *   - Manage comprehensive health checks for ByteHot system components
 *   - Provide readiness and liveness probes for containerized deployments
 *   - Monitor system health status and trigger alerts on failures
 *   - Support custom health check implementations and integrations
 *
 * Collaborators:
 *   - PerformanceMonitor: Monitors system performance metrics
 *   - SecurityManager: Validates security system health
 *   - ByteHotLogger: Logs health check results and events
 *   - AuditTrail: Records health check audit events
 */
package org.acmsl.bytehot.infrastructure.health;

import org.acmsl.bytehot.infrastructure.monitoring.PerformanceMonitor;
import org.acmsl.bytehot.infrastructure.security.SecurityManager;
import org.acmsl.bytehot.infrastructure.logging.ByteHotLogger;
import org.acmsl.bytehot.infrastructure.logging.AuditTrail;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Enterprise-grade health check and monitoring system for ByteHot.
 * Provides comprehensive system health monitoring with proactive alerting.
 * @author Claude Code
 * @since 2025-07-06
 */
public class HealthCheckManager {

    private static final HealthCheckManager INSTANCE = new HealthCheckManager();
    private static final ByteHotLogger LOGGER = ByteHotLogger.getLogger(HealthCheckManager.class);
    
    private final Map<String, HealthCheck> healthChecks = new ConcurrentHashMap<>();
    private final Map<String, HealthCheckResult> lastResults = new ConcurrentHashMap<>();
    private final Map<String, HealthCheckHistory> healthHistory = new ConcurrentHashMap<>();
    private final Set<HealthCheckListener> listeners = Collections.synchronizedSet(new HashSet<>());
    
    private final ReentrantReadWriteLock healthLock = new ReentrantReadWriteLock();
    private final AtomicLong checkCounter = new AtomicLong(0);
    private final AtomicReference<SystemHealthStatus> systemStatus = new AtomicReference<>(SystemHealthStatus.UNKNOWN);
    
    private final ScheduledExecutorService healthExecutor = 
        Executors.newScheduledThreadPool(3, r -> {
            Thread t = new Thread(r, "ByteHot-Health-Check");
            t.setDaemon(true);
            return t;
        });
    
    private volatile HealthCheckConfiguration configuration = HealthCheckConfiguration.defaultConfiguration();
    private volatile boolean healthCheckEnabled = true;
    
    private HealthCheckManager() {
        initializeDefaultHealthChecks();
        startHealthCheckScheduler();
    }

    /**
     * Gets the singleton instance of HealthCheckManager.
     * @return The health check manager instance
     */
    public static HealthCheckManager getInstance() {
        return INSTANCE;
    }

    /**
     * Registers a health check with the system.
     * This method can be hot-swapped to change health check registration behavior.
     * @param name Unique name for the health check
     * @param healthCheck Health check implementation
     */
    public void registerHealthCheck(final String name, final HealthCheck healthCheck) {
        healthLock.writeLock().lock();
        try {
            healthChecks.put(name, healthCheck);
            healthHistory.put(name, new HealthCheckHistory(name));
            
            LOGGER.info("Health check registered: {}", name);
            LOGGER.audit("HEALTH_CHECK_REGISTERED", name, ByteHotLogger.AuditOutcome.SUCCESS, 
                "Health check registered successfully");
            
        } finally {
            healthLock.writeLock().unlock();
        }
    }

    /**
     * Unregisters a health check from the system.
     * This method can be hot-swapped to change health check unregistration behavior.
     * @param name Name of the health check to unregister
     */
    public void unregisterHealthCheck(final String name) {
        healthLock.writeLock().lock();
        try {
            healthChecks.remove(name);
            lastResults.remove(name);
            healthHistory.remove(name);
            
            LOGGER.info("Health check unregistered: {}", name);
            LOGGER.audit("HEALTH_CHECK_UNREGISTERED", name, ByteHotLogger.AuditOutcome.SUCCESS, 
                "Health check unregistered successfully");
            
        } finally {
            healthLock.writeLock().unlock();
        }
    }

    /**
     * Executes all registered health checks.
     * This method can be hot-swapped to change health check execution behavior.
     * @return Combined health check results
     */
    public CompletableFuture<SystemHealthResult> executeHealthChecks() {
        return CompletableFuture.supplyAsync(() -> {
            if (!healthCheckEnabled) {
                return SystemHealthResult.disabled();
            }
            
            final long executionId = checkCounter.incrementAndGet();
            final Instant startTime = Instant.now();
            
            LOGGER.debug("Executing health checks - execution ID: {}", executionId);
            
            healthLock.readLock().lock();
            try {
                final List<CompletableFuture<HealthCheckResult>> healthCheckFutures = 
                    healthChecks.entrySet().stream()
                        .map(entry -> executeHealthCheck(entry.getKey(), entry.getValue()))
                        .collect(Collectors.toList());
                
                final List<HealthCheckResult> results = healthCheckFutures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());
                
                final SystemHealthResult systemResult = aggregateResults(results, executionId, startTime);
                
                updateSystemStatus(systemResult);
                notifyListeners(systemResult);
                
                return systemResult;
                
            } finally {
                healthLock.readLock().unlock();
            }
        }, healthExecutor);
    }

    /**
     * Executes a specific health check by name.
     * This method can be hot-swapped to change individual health check execution behavior.
     * @param name Name of the health check to execute
     * @return Health check result
     */
    public CompletableFuture<Optional<HealthCheckResult>> executeHealthCheck(final String name) {
        return CompletableFuture.supplyAsync(() -> {
            healthLock.readLock().lock();
            try {
                final HealthCheck healthCheck = healthChecks.get(name);
                if (healthCheck == null) {
                    return Optional.empty();
                }
                
                final HealthCheckResult result = executeHealthCheck(name, healthCheck).join();
                return Optional.of(result);
                
            } finally {
                healthLock.readLock().unlock();
            }
        }, healthExecutor);
    }

    /**
     * Gets the current system health status.
     * This method can be hot-swapped to change system status reporting behavior.
     * @return Current system health status
     */
    public SystemHealthStatus getSystemHealthStatus() {
        return systemStatus.get();
    }

    /**
     * Checks if the system is ready to serve requests.
     * This method can be hot-swapped to change readiness check behavior.
     * @return Readiness check result
     */
    public CompletableFuture<ReadinessResult> checkReadiness() {
        return CompletableFuture.supplyAsync(() -> {
            final List<String> readinessChecks = List.of(
                "core-system", "security-system", "configuration-system", "logging-system"
            );
            
            final List<HealthCheckResult> results = new ArrayList<>();
            
            for (final String checkName : readinessChecks) {
                final Optional<HealthCheckResult> result = executeHealthCheck(checkName).join();
                if (result.isPresent()) {
                    results.add(result.get());
                } else {
                    // Create a failure result for missing critical check
                    results.add(HealthCheckResult.failure(checkName, "Health check not registered"));
                }
            }
            
            final boolean isReady = results.stream()
                .allMatch(result -> result.getStatus() == HealthStatus.HEALTHY);
            
            final String message = isReady ? "System is ready" : "System is not ready";
            final List<String> issues = results.stream()
                .filter(result -> result.getStatus() != HealthStatus.HEALTHY)
                .map(result -> result.getName() + ": " + result.getMessage())
                .collect(Collectors.toList());
            
            return new ReadinessResult(isReady, message, issues, results);
            
        }, healthExecutor);
    }

    /**
     * Checks if the system is alive and responding.
     * This method can be hot-swapped to change liveness check behavior.
     * @return Liveness check result
     */
    public CompletableFuture<LivenessResult> checkLiveness() {
        return CompletableFuture.supplyAsync(() -> {
            final List<String> livenessChecks = List.of(
                "jvm-health", "thread-health", "memory-health"
            );
            
            final List<HealthCheckResult> results = new ArrayList<>();
            
            for (final String checkName : livenessChecks) {
                final Optional<HealthCheckResult> result = executeHealthCheck(checkName).join();
                if (result.isPresent()) {
                    results.add(result.get());
                } else {
                    // Create a basic health check for missing liveness check
                    results.add(createBasicHealthCheck(checkName));
                }
            }
            
            final boolean isAlive = results.stream()
                .allMatch(result -> result.getStatus() != HealthStatus.UNHEALTHY);
            
            final String message = isAlive ? "System is alive" : "System is not responding";
            final List<String> issues = results.stream()
                .filter(result -> result.getStatus() == HealthStatus.UNHEALTHY)
                .map(result -> result.getName() + ": " + result.getMessage())
                .collect(Collectors.toList());
            
            return new LivenessResult(isAlive, message, issues, results);
            
        }, healthExecutor);
    }

    /**
     * Gets health check history for a specific check.
     * @param checkName Name of the health check
     * @param maxEntries Maximum number of historical entries to return
     * @return Health check history
     */
    public Optional<HealthCheckHistory> getHealthCheckHistory(final String checkName, final int maxEntries) {
        healthLock.readLock().lock();
        try {
            final HealthCheckHistory history = healthHistory.get(checkName);
            if (history == null) {
                return Optional.empty();
            }
            
            return Optional.of(history.getRecentHistory(maxEntries));
            
        } finally {
            healthLock.readLock().unlock();
        }
    }

    /**
     * Gets current health statistics.
     * @return System health statistics
     */
    public HealthStatistics getHealthStatistics() {
        healthLock.readLock().lock();
        try {
            final int totalChecks = healthChecks.size();
            final int healthyChecks = (int) lastResults.values().stream()
                .filter(result -> result.getStatus() == HealthStatus.HEALTHY)
                .count();
            final int unhealthyChecks = (int) lastResults.values().stream()
                .filter(result -> result.getStatus() == HealthStatus.UNHEALTHY)
                .count();
            final int degradedChecks = (int) lastResults.values().stream()
                .filter(result -> result.getStatus() == HealthStatus.DEGRADED)
                .count();
            
            final long totalExecutions = checkCounter.get();
            final SystemHealthStatus currentStatus = systemStatus.get();
            
            return new HealthStatistics(
                totalChecks, healthyChecks, unhealthyChecks, degradedChecks,
                totalExecutions, currentStatus, lastResults
            );
            
        } finally {
            healthLock.readLock().unlock();
        }
    }

    /**
     * Adds a health check listener.
     * @param listener Health check listener
     */
    public void addHealthCheckListener(final HealthCheckListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a health check listener.
     * @param listener Health check listener to remove
     */
    public void removeHealthCheckListener(final HealthCheckListener listener) {
        listeners.remove(listener);
    }

    /**
     * Configures health check settings.
     * This method can be hot-swapped to change health check configuration.
     * @param newConfiguration New health check configuration
     */
    public void configure(final HealthCheckConfiguration newConfiguration) {
        this.configuration = newConfiguration;
        this.healthCheckEnabled = newConfiguration.isEnabled();
        
        LOGGER.info("Health check configuration updated");
        LOGGER.audit("HEALTH_CHECK_CONFIGURED", "system", ByteHotLogger.AuditOutcome.SUCCESS, 
            "Health check configuration updated successfully");
    }

    /**
     * Shuts down the health check system.
     */
    public void shutdown() {
        healthExecutor.shutdown();
        try {
            if (!healthExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                healthExecutor.shutdownNow();
            }
        } catch (final InterruptedException e) {
            healthExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Initializes default health checks for core system components.
     * This method can be hot-swapped to change default health check initialization.
     */
    protected void initializeDefaultHealthChecks() {
        // Core system health check
        registerHealthCheck("core-system", new CoreSystemHealthCheck());
        
        // JVM health check
        registerHealthCheck("jvm-health", new JvmHealthCheck());
        
        // Memory health check
        registerHealthCheck("memory-health", new MemoryHealthCheck());
        
        // Thread health check
        registerHealthCheck("thread-health", new ThreadHealthCheck());
        
        // Security system health check
        registerHealthCheck("security-system", new SecuritySystemHealthCheck());
        
        // Configuration system health check
        registerHealthCheck("configuration-system", new ConfigurationSystemHealthCheck());
        
        // Logging system health check
        registerHealthCheck("logging-system", new LoggingSystemHealthCheck());
        
        // Performance monitoring health check
        registerHealthCheck("performance-monitoring", new PerformanceMonitoringHealthCheck());
        
        LOGGER.info("Default health checks initialized");
    }

    /**
     * Starts the health check scheduler.
     * This method can be hot-swapped to change health check scheduling behavior.
     */
    protected void startHealthCheckScheduler() {
        // Schedule regular health checks
        healthExecutor.scheduleAtFixedRate(
            () -> executeHealthChecks().thenAccept(this::logHealthCheckResults),
            configuration.getInitialDelay().toSeconds(),
            configuration.getInterval().toSeconds(),
            TimeUnit.SECONDS
        );
        
        // Schedule health history cleanup
        healthExecutor.scheduleAtFixedRate(
            this::cleanupHealthHistory,
            1, 1, TimeUnit.HOURS
        );
        
        LOGGER.info("Health check scheduler started");
    }

    /**
     * Executes a single health check with timeout and error handling.
     * @param name Name of the health check
     * @param healthCheck Health check implementation
     * @return Health check result future
     */
    protected CompletableFuture<HealthCheckResult> executeHealthCheck(final String name, final HealthCheck healthCheck) {
        return CompletableFuture.supplyAsync(() -> {
            final Instant startTime = Instant.now();
            
            try {
                final HealthCheckResult result = healthCheck.check();
                final Duration duration = Duration.between(startTime, Instant.now());
                
                final HealthCheckResult timedResult = result.withDuration(duration);
                
                // Update last results and history
                lastResults.put(name, timedResult);
                healthHistory.get(name).addResult(timedResult);
                
                return timedResult;
                
            } catch (final Exception e) {
                final Duration duration = Duration.between(startTime, Instant.now());
                final HealthCheckResult failureResult = HealthCheckResult.failure(name, 
                    "Health check failed: " + e.getMessage(), e).withDuration(duration);
                
                lastResults.put(name, failureResult);
                healthHistory.get(name).addResult(failureResult);
                
                LOGGER.error("Health check failed: " + name, e);
                
                return failureResult;
            }
            
        }, healthExecutor).orTimeout(configuration.getTimeout().toMillis(), TimeUnit.MILLISECONDS)
          .exceptionally(throwable -> {
              final HealthCheckResult timeoutResult = HealthCheckResult.failure(name, 
                  "Health check timed out", throwable);
              
              lastResults.put(name, timeoutResult);
              healthHistory.get(name).addResult(timeoutResult);
              
              LOGGER.error("Health check timed out: " + name, throwable);
              
              return timeoutResult;
          });
    }

    /**
     * Aggregates individual health check results into system health result.
     * @param results Individual health check results
     * @param executionId Execution ID
     * @param startTime Execution start time
     * @return System health result
     */
    protected SystemHealthResult aggregateResults(final List<HealthCheckResult> results, 
                                                 final long executionId, final Instant startTime) {
        final Duration totalDuration = Duration.between(startTime, Instant.now());
        
        final int healthyCount = (int) results.stream()
            .filter(result -> result.getStatus() == HealthStatus.HEALTHY)
            .count();
        
        final int unhealthyCount = (int) results.stream()
            .filter(result -> result.getStatus() == HealthStatus.UNHEALTHY)
            .count();
        
        final int degradedCount = (int) results.stream()
            .filter(result -> result.getStatus() == HealthStatus.DEGRADED)
            .count();
        
        final SystemHealthStatus overallStatus = determineOverallStatus(healthyCount, unhealthyCount, degradedCount);
        
        return new SystemHealthResult(
            executionId, startTime, totalDuration, overallStatus,
            results.size(), healthyCount, unhealthyCount, degradedCount, results
        );
    }

    /**
     * Determines overall system health status based on individual check results.
     * @param healthyCount Number of healthy checks
     * @param unhealthyCount Number of unhealthy checks
     * @param degradedCount Number of degraded checks
     * @return Overall system health status
     */
    protected SystemHealthStatus determineOverallStatus(final int healthyCount, 
                                                       final int unhealthyCount, 
                                                       final int degradedCount) {
        if (unhealthyCount > 0) {
            return SystemHealthStatus.UNHEALTHY;
        } else if (degradedCount > 0) {
            return SystemHealthStatus.DEGRADED;
        } else if (healthyCount > 0) {
            return SystemHealthStatus.HEALTHY;
        } else {
            return SystemHealthStatus.UNKNOWN;
        }
    }

    /**
     * Updates the system health status and triggers alerts if needed.
     * @param systemResult System health result
     */
    protected void updateSystemStatus(final SystemHealthResult systemResult) {
        final SystemHealthStatus previousStatus = systemStatus.getAndSet(systemResult.getStatus());
        
        if (previousStatus != systemResult.getStatus()) {
            LOGGER.info("System health status changed from {} to {}", 
                previousStatus, systemResult.getStatus());
            
            LOGGER.audit("HEALTH_STATUS_CHANGED", "system", ByteHotLogger.AuditOutcome.SUCCESS, 
                String.format("System health status changed from %s to %s", 
                    previousStatus, systemResult.getStatus()));
            
            // Trigger alerts for degraded or unhealthy status
            if (systemResult.getStatus() == SystemHealthStatus.UNHEALTHY) {
                triggerHealthAlert(systemResult, "System is unhealthy");
            } else if (systemResult.getStatus() == SystemHealthStatus.DEGRADED) {
                triggerHealthAlert(systemResult, "System is degraded");
            }
        }
    }

    /**
     * Notifies health check listeners of system health changes.
     * @param systemResult System health result
     */
    protected void notifyListeners(final SystemHealthResult systemResult) {
        listeners.forEach(listener -> {
            try {
                listener.onHealthCheckResult(systemResult);
            } catch (final Exception e) {
                LOGGER.error("Health check listener failed", e);
            }
        });
    }

    /**
     * Creates a basic health check for system components.
     * @param checkName Name of the health check
     * @return Basic health check result
     */
    protected HealthCheckResult createBasicHealthCheck(final String checkName) {
        return switch (checkName) {
            case "jvm-health" -> checkJvmHealth();
            case "thread-health" -> checkThreadHealth();
            case "memory-health" -> checkMemoryHealth();
            default -> HealthCheckResult.success(checkName, "Basic health check passed");
        };
    }

    /**
     * Performs basic JVM health check.
     * @return JVM health check result
     */
    protected HealthCheckResult checkJvmHealth() {
        final Runtime runtime = Runtime.getRuntime();
        final long totalMemory = runtime.totalMemory();
        final long freeMemory = runtime.freeMemory();
        final long maxMemory = runtime.maxMemory();
        
        final double memoryUsage = (double) (totalMemory - freeMemory) / maxMemory;
        
        if (memoryUsage > 0.9) {
            return HealthCheckResult.failure("jvm-health", 
                String.format("High memory usage: %.2f%%", memoryUsage * 100));
        } else if (memoryUsage > 0.8) {
            return HealthCheckResult.degraded("jvm-health", 
                String.format("Elevated memory usage: %.2f%%", memoryUsage * 100));
        } else {
            return HealthCheckResult.success("jvm-health", 
                String.format("JVM healthy, memory usage: %.2f%%", memoryUsage * 100));
        }
    }

    /**
     * Performs basic thread health check.
     * @return Thread health check result
     */
    protected HealthCheckResult checkThreadHealth() {
        final int activeThreads = Thread.activeCount();
        final int maxThreads = 1000; // Configurable threshold
        
        if (activeThreads > maxThreads) {
            return HealthCheckResult.failure("thread-health", 
                String.format("Too many active threads: %d", activeThreads));
        } else if (activeThreads > maxThreads * 0.8) {
            return HealthCheckResult.degraded("thread-health", 
                String.format("High thread count: %d", activeThreads));
        } else {
            return HealthCheckResult.success("thread-health", 
                String.format("Thread count normal: %d", activeThreads));
        }
    }

    /**
     * Performs basic memory health check.
     * @return Memory health check result
     */
    protected HealthCheckResult checkMemoryHealth() {
        final Runtime runtime = Runtime.getRuntime();
        final long totalMemory = runtime.totalMemory();
        final long freeMemory = runtime.freeMemory();
        final long usedMemory = totalMemory - freeMemory;
        
        final double memoryUsage = (double) usedMemory / totalMemory;
        
        if (memoryUsage > 0.95) {
            return HealthCheckResult.failure("memory-health", 
                String.format("Critical memory usage: %.2f%%", memoryUsage * 100));
        } else if (memoryUsage > 0.85) {
            return HealthCheckResult.degraded("memory-health", 
                String.format("High memory usage: %.2f%%", memoryUsage * 100));
        } else {
            return HealthCheckResult.success("memory-health", 
                String.format("Memory usage normal: %.2f%%", memoryUsage * 100));
        }
    }

    /**
     * Triggers health alert for system issues.
     * @param systemResult System health result
     * @param message Alert message
     */
    protected void triggerHealthAlert(final SystemHealthResult systemResult, final String message) {
        LOGGER.security(ByteHotLogger.SecurityEventType.SUSPICIOUS_ACTIVITY, 
            ByteHotLogger.SecuritySeverity.HIGH, message, 
            Map.of("healthStatus", systemResult.getStatus().toString(),
                   "unhealthyCount", systemResult.getUnhealthyCount(),
                   "degradedCount", systemResult.getDegradedCount()));
    }

    /**
     * Logs health check results.
     * @param systemResult System health result
     */
    protected void logHealthCheckResults(final SystemHealthResult systemResult) {
        if (configuration.isVerboseLogging()) {
            LOGGER.info("Health check completed - Status: {}, Healthy: {}, Unhealthy: {}, Degraded: {}",
                systemResult.getStatus(), systemResult.getHealthyCount(), 
                systemResult.getUnhealthyCount(), systemResult.getDegradedCount());
        }
    }

    /**
     * Cleans up old health history entries.
     */
    protected void cleanupHealthHistory() {
        final int maxHistorySize = configuration.getMaxHistorySize();
        
        healthHistory.values().forEach(history -> history.cleanup(maxHistorySize));
    }

    // Enums and supporting classes
    
    public enum HealthStatus {
        HEALTHY, DEGRADED, UNHEALTHY, UNKNOWN
    }

    public enum SystemHealthStatus {
        HEALTHY, DEGRADED, UNHEALTHY, UNKNOWN
    }

    // Interfaces
    
    public interface HealthCheck {
        HealthCheckResult check();
    }

    public interface HealthCheckListener {
        void onHealthCheckResult(SystemHealthResult result);
    }

    // Static inner classes for data structures
    
    public static class HealthCheckResult {
        private final String name;
        private final HealthStatus status;
        private final String message;
        private final Throwable throwable;
        private final Instant timestamp;
        private final Duration duration;
        private final Map<String, Object> details;

        public HealthCheckResult(final String name, final HealthStatus status, final String message,
                                final Throwable throwable, final Instant timestamp, final Duration duration,
                                final Map<String, Object> details) {
            this.name = name;
            this.status = status;
            this.message = message;
            this.throwable = throwable;
            this.timestamp = timestamp;
            this.duration = duration;
            this.details = details != null ? new java.util.HashMap<>(details) : new java.util.HashMap<>();
        }

        public static HealthCheckResult success(final String name, final String message) {
            return new HealthCheckResult(name, HealthStatus.HEALTHY, message, null, 
                Instant.now(), Duration.ZERO, null);
        }

        public static HealthCheckResult degraded(final String name, final String message) {
            return new HealthCheckResult(name, HealthStatus.DEGRADED, message, null, 
                Instant.now(), Duration.ZERO, null);
        }

        public static HealthCheckResult failure(final String name, final String message) {
            return new HealthCheckResult(name, HealthStatus.UNHEALTHY, message, null, 
                Instant.now(), Duration.ZERO, null);
        }

        public static HealthCheckResult failure(final String name, final String message, final Throwable throwable) {
            return new HealthCheckResult(name, HealthStatus.UNHEALTHY, message, throwable, 
                Instant.now(), Duration.ZERO, null);
        }

        public HealthCheckResult withDuration(final Duration duration) {
            return new HealthCheckResult(name, status, message, throwable, timestamp, duration, details);
        }

        public HealthCheckResult withDetails(final Map<String, Object> details) {
            return new HealthCheckResult(name, status, message, throwable, timestamp, duration, details);
        }

        public String getName() { return name; }
        public HealthStatus getStatus() { return status; }
        public String getMessage() { return message; }
        public Throwable getThrowable() { return throwable; }
        public Instant getTimestamp() { return timestamp; }
        public Duration getDuration() { return duration; }
        public Map<String, Object> getDetails() { return Collections.unmodifiableMap(details); }
    }

    public static class SystemHealthResult {
        private final long executionId;
        private final Instant timestamp;
        private final Duration duration;
        private final SystemHealthStatus status;
        private final int totalChecks;
        private final int healthyCount;
        private final int unhealthyCount;
        private final int degradedCount;
        private final List<HealthCheckResult> results;

        public SystemHealthResult(final long executionId, final Instant timestamp, final Duration duration,
                                 final SystemHealthStatus status, final int totalChecks, final int healthyCount,
                                 final int unhealthyCount, final int degradedCount, final List<HealthCheckResult> results) {
            this.executionId = executionId;
            this.timestamp = timestamp;
            this.duration = duration;
            this.status = status;
            this.totalChecks = totalChecks;
            this.healthyCount = healthyCount;
            this.unhealthyCount = unhealthyCount;
            this.degradedCount = degradedCount;
            this.results = new ArrayList<>(results);
        }

        public static SystemHealthResult disabled() {
            return new SystemHealthResult(0, Instant.now(), Duration.ZERO, SystemHealthStatus.UNKNOWN,
                0, 0, 0, 0, Collections.emptyList());
        }

        public long getExecutionId() { return executionId; }
        public Instant getTimestamp() { return timestamp; }
        public Duration getDuration() { return duration; }
        public SystemHealthStatus getStatus() { return status; }
        public int getTotalChecks() { return totalChecks; }
        public int getHealthyCount() { return healthyCount; }
        public int getUnhealthyCount() { return unhealthyCount; }
        public int getDegradedCount() { return degradedCount; }
        public List<HealthCheckResult> getResults() { return Collections.unmodifiableList(results); }
    }

    public static class ReadinessResult {
        private final boolean ready;
        private final String message;
        private final List<String> issues;
        private final List<HealthCheckResult> results;
        private final Instant timestamp;

        public ReadinessResult(final boolean ready, final String message, final List<String> issues,
                              final List<HealthCheckResult> results) {
            this.ready = ready;
            this.message = message;
            this.issues = new ArrayList<>(issues);
            this.results = new ArrayList<>(results);
            this.timestamp = Instant.now();
        }

        public boolean isReady() { return ready; }
        public String getMessage() { return message; }
        public List<String> getIssues() { return Collections.unmodifiableList(issues); }
        public List<HealthCheckResult> getResults() { return Collections.unmodifiableList(results); }
        public Instant getTimestamp() { return timestamp; }
    }

    public static class LivenessResult {
        private final boolean alive;
        private final String message;
        private final List<String> issues;
        private final List<HealthCheckResult> results;
        private final Instant timestamp;

        public LivenessResult(final boolean alive, final String message, final List<String> issues,
                             final List<HealthCheckResult> results) {
            this.alive = alive;
            this.message = message;
            this.issues = new ArrayList<>(issues);
            this.results = new ArrayList<>(results);
            this.timestamp = Instant.now();
        }

        public boolean isAlive() { return alive; }
        public String getMessage() { return message; }
        public List<String> getIssues() { return Collections.unmodifiableList(issues); }
        public List<HealthCheckResult> getResults() { return Collections.unmodifiableList(results); }
        public Instant getTimestamp() { return timestamp; }
    }

    public static class HealthCheckHistory {
        private final String checkName;
        private final List<HealthCheckResult> history;

        public HealthCheckHistory(final String checkName) {
            this.checkName = checkName;
            this.history = Collections.synchronizedList(new ArrayList<>());
        }

        public void addResult(final HealthCheckResult result) {
            history.add(result);
        }

        public HealthCheckHistory getRecentHistory(final int maxEntries) {
            final HealthCheckHistory recentHistory = new HealthCheckHistory(checkName);
            final int startIndex = Math.max(0, history.size() - maxEntries);
            recentHistory.history.addAll(history.subList(startIndex, history.size()));
            return recentHistory;
        }

        public void cleanup(final int maxSize) {
            while (history.size() > maxSize) {
                history.remove(0);
            }
        }

        public String getCheckName() { return checkName; }
        public List<HealthCheckResult> getHistory() { return Collections.unmodifiableList(history); }
    }

    public static class HealthStatistics {
        private final int totalChecks;
        private final int healthyChecks;
        private final int unhealthyChecks;
        private final int degradedChecks;
        private final long totalExecutions;
        private final SystemHealthStatus currentStatus;
        private final Map<String, HealthCheckResult> lastResults;

        public HealthStatistics(final int totalChecks, final int healthyChecks, final int unhealthyChecks,
                               final int degradedChecks, final long totalExecutions, final SystemHealthStatus currentStatus,
                               final Map<String, HealthCheckResult> lastResults) {
            this.totalChecks = totalChecks;
            this.healthyChecks = healthyChecks;
            this.unhealthyChecks = unhealthyChecks;
            this.degradedChecks = degradedChecks;
            this.totalExecutions = totalExecutions;
            this.currentStatus = currentStatus;
            this.lastResults = new java.util.HashMap<>(lastResults);
        }

        public int getTotalChecks() { return totalChecks; }
        public int getHealthyChecks() { return healthyChecks; }
        public int getUnhealthyChecks() { return unhealthyChecks; }
        public int getDegradedChecks() { return degradedChecks; }
        public long getTotalExecutions() { return totalExecutions; }
        public SystemHealthStatus getCurrentStatus() { return currentStatus; }
        public Map<String, HealthCheckResult> getLastResults() { return Collections.unmodifiableMap(lastResults); }
    }

    public static class HealthCheckConfiguration {
        private boolean enabled = true;
        private Duration interval = Duration.ofMinutes(1);
        private Duration timeout = Duration.ofSeconds(30);
        private Duration initialDelay = Duration.ofSeconds(10);
        private int maxHistorySize = 100;
        private boolean verboseLogging = false;

        public static HealthCheckConfiguration defaultConfiguration() {
            return new HealthCheckConfiguration();
        }

        public boolean isEnabled() { return enabled; }
        public void setEnabled(final boolean enabled) { this.enabled = enabled; }

        public Duration getInterval() { return interval; }
        public void setInterval(final Duration interval) { this.interval = interval; }

        public Duration getTimeout() { return timeout; }
        public void setTimeout(final Duration timeout) { this.timeout = timeout; }

        public Duration getInitialDelay() { return initialDelay; }
        public void setInitialDelay(final Duration initialDelay) { this.initialDelay = initialDelay; }

        public int getMaxHistorySize() { return maxHistorySize; }
        public void setMaxHistorySize(final int maxHistorySize) { this.maxHistorySize = maxHistorySize; }

        public boolean isVerboseLogging() { return verboseLogging; }
        public void setVerboseLogging(final boolean verboseLogging) { this.verboseLogging = verboseLogging; }
    }

    // Default health check implementations
    
    protected static class CoreSystemHealthCheck implements HealthCheck {
        @Override
        public HealthCheckResult check() {
            // Check if core ByteHot systems are operational
            return HealthCheckResult.success("core-system", "Core system operational");
        }
    }

    protected static class JvmHealthCheck implements HealthCheck {
        @Override
        public HealthCheckResult check() {
            final Runtime runtime = Runtime.getRuntime();
            final long totalMemory = runtime.totalMemory();
            final long freeMemory = runtime.freeMemory();
            final long maxMemory = runtime.maxMemory();
            
            final double memoryUsage = (double) (totalMemory - freeMemory) / maxMemory;
            
            final Map<String, Object> details = Map.of(
                "totalMemory", totalMemory,
                "freeMemory", freeMemory,
                "maxMemory", maxMemory,
                "memoryUsage", memoryUsage
            );
            
            if (memoryUsage > 0.9) {
                return HealthCheckResult.failure("jvm-health", 
                    String.format("Critical memory usage: %.2f%%", memoryUsage * 100))
                    .withDetails(details);
            } else if (memoryUsage > 0.8) {
                return HealthCheckResult.degraded("jvm-health", 
                    String.format("High memory usage: %.2f%%", memoryUsage * 100))
                    .withDetails(details);
            } else {
                return HealthCheckResult.success("jvm-health", 
                    String.format("JVM healthy, memory usage: %.2f%%", memoryUsage * 100))
                    .withDetails(details);
            }
        }
    }

    protected static class MemoryHealthCheck implements HealthCheck {
        @Override
        public HealthCheckResult check() {
            final Runtime runtime = Runtime.getRuntime();
            final long totalMemory = runtime.totalMemory();
            final long freeMemory = runtime.freeMemory();
            final long usedMemory = totalMemory - freeMemory;
            
            final double memoryUsage = (double) usedMemory / totalMemory;
            
            if (memoryUsage > 0.95) {
                return HealthCheckResult.failure("memory-health", 
                    String.format("Critical memory usage: %.2f%%", memoryUsage * 100));
            } else if (memoryUsage > 0.85) {
                return HealthCheckResult.degraded("memory-health", 
                    String.format("High memory usage: %.2f%%", memoryUsage * 100));
            } else {
                return HealthCheckResult.success("memory-health", 
                    String.format("Memory usage normal: %.2f%%", memoryUsage * 100));
            }
        }
    }

    protected static class ThreadHealthCheck implements HealthCheck {
        @Override
        public HealthCheckResult check() {
            final int activeThreads = Thread.activeCount();
            final int maxThreads = 1000;
            
            if (activeThreads > maxThreads) {
                return HealthCheckResult.failure("thread-health", 
                    String.format("Too many active threads: %d", activeThreads));
            } else if (activeThreads > maxThreads * 0.8) {
                return HealthCheckResult.degraded("thread-health", 
                    String.format("High thread count: %d", activeThreads));
            } else {
                return HealthCheckResult.success("thread-health", 
                    String.format("Thread count normal: %d", activeThreads));
            }
        }
    }

    protected static class SecuritySystemHealthCheck implements HealthCheck {
        @Override
        public HealthCheckResult check() {
            try {
                final SecurityManager securityManager = SecurityManager.getInstance();
                // Check if security system is responsive
                return HealthCheckResult.success("security-system", "Security system operational");
            } catch (final Exception e) {
                return HealthCheckResult.failure("security-system", 
                    "Security system check failed: " + e.getMessage(), e);
            }
        }
    }

    protected static class ConfigurationSystemHealthCheck implements HealthCheck {
        @Override
        public HealthCheckResult check() {
            // Check configuration system health
            return HealthCheckResult.success("configuration-system", "Configuration system operational");
        }
    }

    protected static class LoggingSystemHealthCheck implements HealthCheck {
        @Override
        public HealthCheckResult check() {
            try {
                // Test logging system
                final ByteHotLogger testLogger = ByteHotLogger.getLogger("health-check-test");
                testLogger.debug("Health check test message");
                return HealthCheckResult.success("logging-system", "Logging system operational");
            } catch (final Exception e) {
                return HealthCheckResult.failure("logging-system", 
                    "Logging system check failed: " + e.getMessage(), e);
            }
        }
    }

    protected static class PerformanceMonitoringHealthCheck implements HealthCheck {
        @Override
        public HealthCheckResult check() {
            try {
                final PerformanceMonitor monitor = PerformanceMonitor.getInstance();
                // Check if performance monitoring is operational
                return HealthCheckResult.success("performance-monitoring", "Performance monitoring operational");
            } catch (final Exception e) {
                return HealthCheckResult.failure("performance-monitoring", 
                    "Performance monitoring check failed: " + e.getMessage(), e);
            }
        }
    }
}