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
 *   - Manage system health checks and monitoring
 *   - Execute periodic health assessments
 *   - Coordinate health check across different system components
 *   - Provide comprehensive system health status
 *
 * Collaborators:
 *   - HealthCheck: Individual health check implementations
 *   - HealthStatus: Overall system health status
 *   - HealthCheckResult: Results from individual health checks
 *   - PerformanceMonitor: Provides performance metrics for health assessment
 */
package org.acmsl.bytehot.infrastructure.production;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Manages comprehensive system health checks and monitoring.
 * @author Claude Code
 * @since 2025-07-04
 */
public class HealthCheckManager {
    
    /**
     * Default health check interval in seconds.
     */
    public static final int DEFAULT_HEALTH_CHECK_INTERVAL_SECONDS = 60;
    
    /**
     * Default health check timeout in seconds.
     */
    public static final int DEFAULT_HEALTH_CHECK_TIMEOUT_SECONDS = 30;
    
    /**
     * Health check manager configuration.
     */
    private final HealthCheckConfiguration configuration;
    
    /**
     * Scheduled executor for periodic health checks.
     */
    private final ScheduledExecutorService scheduler;
    
    /**
     * Registered health checks.
     */
    private final ConcurrentHashMap<String, HealthCheck> healthChecks;
    
    /**
     * Health check results history.
     */
    private final List<SystemHealthStatus> healthHistory;
    
    /**
     * Total number of health checks performed.
     */
    private final AtomicLong totalHealthChecks;
    
    /**
     * Number of failed health checks.
     */
    private final AtomicLong failedHealthChecks;
    
    /**
     * Current system health status.
     */
    private volatile SystemHealthStatus currentHealthStatus;
    
    /**
     * Whether health checking is active.
     */
    private volatile boolean active;
    
    /**
     * Time when health checking started.
     */
    private volatile Instant startTime;
    
    /**
     * Creates a new HealthCheckManager with default configuration.
     */
    public HealthCheckManager() {
        this(HealthCheckConfiguration.defaultConfiguration());
    }
    
    /**
     * Creates a new HealthCheckManager with the specified configuration.
     * @param configuration The health check configuration
     */
    public HealthCheckManager(final HealthCheckConfiguration configuration) {
        this.configuration = configuration;
        this.scheduler = Executors.newScheduledThreadPool(configuration.getMaxConcurrentChecks());
        this.healthChecks = new ConcurrentHashMap<>();
        this.healthHistory = new ArrayList<>();
        this.totalHealthChecks = new AtomicLong(0);
        this.failedHealthChecks = new AtomicLong(0);
        this.active = false;
        
        // Initialize with default health checks
        initializeDefaultHealthChecks();
    }
    
    /**
     * Starts health checking.
     */
    public void startHealthChecking() {
        if (active) {
            return;
        }
        
        active = true;
        startTime = Instant.now();
        
        // Schedule periodic health checks
        scheduler.scheduleAtFixedRate(
            this::performHealthChecks,
            0,
            configuration.getHealthCheckInterval().getSeconds(),
            TimeUnit.SECONDS
        );
    }
    
    /**
     * Stops health checking.
     */
    public void stopHealthChecking() {
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
     * Registers a health check.
     * @param name The health check name
     * @param healthCheck The health check implementation
     */
    public void registerHealthCheck(final String name, final HealthCheck healthCheck) {
        healthChecks.put(name, healthCheck);
    }
    
    /**
     * Unregisters a health check.
     * @param name The health check name
     */
    public void unregisterHealthCheck(final String name) {
        healthChecks.remove(name);
    }
    
    /**
     * Performs an immediate health check of all registered checks.
     * @return The current system health status
     */
    public SystemHealthStatus performImmediateHealthCheck() {
        return performHealthChecks();
    }
    
    /**
     * Gets the current system health status.
     * @return The current health status
     */
    public SystemHealthStatus getCurrentHealthStatus() {
        return currentHealthStatus;
    }
    
    /**
     * Gets health check statistics.
     * @return Health check statistics
     */
    public HealthCheckStatistics getStatistics() {
        return HealthCheckStatistics.builder()
            .active(active)
            .startTime(startTime)
            .totalHealthChecks(totalHealthChecks.get())
            .failedHealthChecks(failedHealthChecks.get())
            .registeredHealthChecks(healthChecks.size())
            .healthHistorySize(healthHistory.size())
            .successRate(calculateSuccessRate())
            .build();
    }
    
    /**
     * Gets the health check history.
     * @return List of system health status snapshots
     */
    public List<SystemHealthStatus> getHealthHistory() {
        return Collections.unmodifiableList(healthHistory);
    }
    
    /**
     * Gets all registered health checks.
     * @return Map of health check names to implementations
     */
    public ConcurrentHashMap<String, HealthCheck> getRegisteredHealthChecks() {
        return new ConcurrentHashMap<>(healthChecks);
    }
    
    /**
     * Checks if health checking is active.
     * @return true if active, false otherwise
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * Gets the uptime of health checking.
     * @return The uptime duration
     */
    public Duration getUptime() {
        if (startTime == null) {
            return Duration.ZERO;
        }
        return Duration.between(startTime, Instant.now());
    }
    
    /**
     * Performs health checks on all registered checks.
     * @return The system health status
     */
    protected SystemHealthStatus performHealthChecks() {
        Instant checkTime = Instant.now();
        List<CompletableFuture<HealthCheckResult>> futures = new ArrayList<>();
        
        // Execute all health checks concurrently
        for (HealthCheck healthCheck : healthChecks.values()) {
            CompletableFuture<HealthCheckResult> future = CompletableFuture.supplyAsync(
                () -> executeHealthCheck(healthCheck),
                scheduler
            );
            futures.add(future);
        }
        
        // Collect all results
        List<HealthCheckResult> results = new ArrayList<>();
        for (CompletableFuture<HealthCheckResult> future : futures) {
            try {
                HealthCheckResult result = future.get(
                    configuration.getHealthCheckTimeout().getSeconds(),
                    TimeUnit.SECONDS
                );
                results.add(result);
            } catch (Exception e) {
                // Create a failed result for the timeout/error
                HealthCheckResult failedResult = HealthCheckResult.failed(
                    "timeout-or-error",
                    "Health check failed or timed out: " + e.getMessage()
                );
                results.add(failedResult);
                failedHealthChecks.incrementAndGet();
            }
        }
        
        totalHealthChecks.incrementAndGet();
        
        // Create system health status
        SystemHealthStatus healthStatus = createSystemHealthStatus(checkTime, results);
        currentHealthStatus = healthStatus;
        
        // Add to history
        healthHistory.add(healthStatus);
        if (healthHistory.size() > configuration.getMaxHistorySize()) {
            healthHistory.remove(0);
        }
        
        // Check for health alerts
        checkHealthAlerts(healthStatus);
        
        return healthStatus;
    }
    
    /**
     * Executes a single health check safely.
     * @param healthCheck The health check to execute
     * @return The health check result
     */
    protected HealthCheckResult executeHealthCheck(final HealthCheck healthCheck) {
        try {
            return healthCheck.check();
        } catch (Exception e) {
            return HealthCheckResult.failed(
                healthCheck.getName(),
                "Health check threw exception: " + e.getMessage()
            );
        }
    }
    
    /**
     * Creates a system health status from individual health check results.
     * @param checkTime The time of the health check
     * @param results The individual health check results
     * @return The system health status
     */
    protected SystemHealthStatus createSystemHealthStatus(final Instant checkTime,
                                                         final List<HealthCheckResult> results) {
        OverallHealth overallHealth = determineOverallHealth(results);
        
        return SystemHealthStatus.builder()
            .checkTime(checkTime)
            .overallHealth(overallHealth)
            .healthCheckResults(results)
            .totalChecks(results.size())
            .passedChecks(countPassedChecks(results))
            .failedChecks(countFailedChecks(results))
            .build();
    }
    
    /**
     * Determines the overall health from individual results.
     * @param results The health check results
     * @return The overall health status
     */
    protected OverallHealth determineOverallHealth(final List<HealthCheckResult> results) {
        if (results.isEmpty()) {
            return OverallHealth.UNKNOWN;
        }
        
        long failedCount = countFailedChecks(results);
        long criticalFailures = results.stream()
            .mapToLong(result -> result.isCritical() && !result.isHealthy() ? 1 : 0)
            .sum();
        
        if (criticalFailures > 0) {
            return OverallHealth.CRITICAL;
        }
        
        double failureRate = (double) failedCount / results.size();
        
        if (failureRate == 0.0) {
            return OverallHealth.HEALTHY;
        } else if (failureRate < 0.2) {
            return OverallHealth.WARNING;
        } else {
            return OverallHealth.UNHEALTHY;
        }
    }
    
    /**
     * Counts passed health checks.
     * @param results The health check results
     * @return The number of passed checks
     */
    protected long countPassedChecks(final List<HealthCheckResult> results) {
        return results.stream()
            .mapToLong(result -> result.isHealthy() ? 1 : 0)
            .sum();
    }
    
    /**
     * Counts failed health checks.
     * @param results The health check results
     * @return The number of failed checks
     */
    protected long countFailedChecks(final List<HealthCheckResult> results) {
        return results.stream()
            .mapToLong(result -> result.isHealthy() ? 0 : 1)
            .sum();
    }
    
    /**
     * Checks for health alerts and triggers notifications if needed.
     * @param healthStatus The current health status
     */
    protected void checkHealthAlerts(final SystemHealthStatus healthStatus) {
        if (healthStatus.getOverallHealth() == OverallHealth.CRITICAL ||
            healthStatus.getOverallHealth() == OverallHealth.UNHEALTHY) {
            
            handleHealthAlert(healthStatus);
        }
    }
    
    /**
     * Handles a health alert.
     * @param healthStatus The health status that triggered the alert
     */
    protected void handleHealthAlert(final SystemHealthStatus healthStatus) {
        // TODO: Implement health alert handling
        System.err.println("HEALTH ALERT: System health is " + healthStatus.getOverallHealth());
    }
    
    /**
     * Calculates the success rate of health checks.
     * @return The success rate (0.0 to 1.0)
     */
    protected double calculateSuccessRate() {
        long total = totalHealthChecks.get();
        if (total == 0) {
            return 1.0;
        }
        
        long failed = failedHealthChecks.get();
        return (double) (total - failed) / total;
    }
    
    /**
     * Initializes default health checks.
     */
    protected void initializeDefaultHealthChecks() {
        // Memory health check
        registerHealthCheck("memory", new MemoryHealthCheck());
        
        // CPU health check
        registerHealthCheck("cpu", new CpuHealthCheck());
        
        // Disk space health check
        registerHealthCheck("disk", new DiskSpaceHealthCheck());
        
        // Hot-swap health check
        registerHealthCheck("hotswap", new HotSwapHealthCheck());
        
        // Threading health check
        registerHealthCheck("threading", new ThreadingHealthCheck());
    }
    
    /**
     * Shuts down the health check manager.
     */
    public void shutdown() {
        stopHealthChecking();
    }
}