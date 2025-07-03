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
 * Filename: HealthMonitor.java
 *
 * Author: Claude Code
 *
 * Class name: HealthMonitor
 *
 * Responsibilities:
 *   - Monitor plugin health and status
 *   - Track communication with ByteHot agent
 *   - Report health metrics and statistics
 *   - Detect and handle unhealthy states
 *
 * Collaborators:
 *   - PluginBase: Reports health status during operations
 *   - PluginCommunicationHandler: Monitors communication health
 *   - ScheduledExecutorService: Periodic health checks (implicit)
 *   - HealthStatus: Health state representation
 */
package org.acmsl.bytehot.plugin.core;

import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Monitors plugin health status and provides health reporting capabilities.
 * Tracks communication health, plugin status, and performance metrics.
 * 
 * @author Claude Code
 * @since 2025-07-03
 */
public class HealthMonitor {

    /**
     * Current health status of the plugin.
     */
    protected HealthStatus healthStatus = HealthStatus.INITIALIZING;

    /**
     * Last time health was checked.
     */
    protected Instant lastHealthCheck = Instant.now();

    /**
     * Last successful communication with agent.
     */
    protected Instant lastSuccessfulCommunication;

    /**
     * Executor service for periodic health checks.
     */
    protected ScheduledExecutorService executor;

    /**
     * Scheduled health check task.
     */
    protected ScheduledFuture<?> healthCheckTask;

    /**
     * Health check interval in seconds.
     */
    protected static final long HEALTH_CHECK_INTERVAL_SECONDS = 30;

    /**
     * Communication timeout threshold in seconds.
     */
    protected static final long COMMUNICATION_TIMEOUT_SECONDS = 120;

    /**
     * Starts health monitoring with periodic checks.
     */
    public void start() {
        if (executor == null || executor.isShutdown()) {
            executor = Executors.newSingleThreadScheduledExecutor(r -> {
                final Thread thread = new Thread(r, "ByteHot-HealthMonitor");
                thread.setDaemon(true);
                return thread;
            });
        }

        healthStatus = HealthStatus.HEALTHY;
        lastHealthCheck = Instant.now();

        // Schedule periodic health checks
        healthCheckTask = executor.scheduleAtFixedRate(
            this::performHealthCheck,
            HEALTH_CHECK_INTERVAL_SECONDS,
            HEALTH_CHECK_INTERVAL_SECONDS,
            TimeUnit.SECONDS
        );
    }

    /**
     * Stops health monitoring and cleans up resources.
     */
    public void stop() {
        healthStatus = HealthStatus.STOPPED;

        if (healthCheckTask != null && !healthCheckTask.isCancelled()) {
            healthCheckTask.cancel(false);
        }

        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Gets the current health status.
     * 
     * @return the current health status
     */
    public HealthStatus getHealthStatus() {
        return healthStatus;
    }

    /**
     * Records a successful communication with the ByteHot agent.
     */
    public void recordSuccessfulCommunication() {
        lastSuccessfulCommunication = Instant.now();
        if (healthStatus == HealthStatus.COMMUNICATION_ERROR) {
            healthStatus = HealthStatus.HEALTHY;
        }
    }

    /**
     * Records a communication error with the ByteHot agent.
     */
    public void recordCommunicationError() {
        healthStatus = HealthStatus.COMMUNICATION_ERROR;
    }

    /**
     * Records a plugin error.
     */
    public void recordPluginError() {
        healthStatus = HealthStatus.UNHEALTHY;
    }

    /**
     * Gets the last time health was checked.
     * 
     * @return the last health check timestamp
     */
    public Instant getLastHealthCheck() {
        return lastHealthCheck;
    }

    /**
     * Gets the last successful communication timestamp.
     * 
     * @return the last successful communication timestamp, or null if none
     */
    public Instant getLastSuccessfulCommunication() {
        return lastSuccessfulCommunication;
    }

    /**
     * Checks if the plugin is currently healthy.
     * 
     * @return true if the plugin is healthy, false otherwise
     */
    public boolean isHealthy() {
        return healthStatus == HealthStatus.HEALTHY;
    }

    /**
     * Performs a health check and updates status accordingly.
     */
    protected void performHealthCheck() {
        lastHealthCheck = Instant.now();

        // Check for communication timeout
        if (lastSuccessfulCommunication != null) {
            final long secondsSinceLastCommunication = 
                lastHealthCheck.getEpochSecond() - lastSuccessfulCommunication.getEpochSecond();
            
            if (secondsSinceLastCommunication > COMMUNICATION_TIMEOUT_SECONDS) {
                healthStatus = HealthStatus.COMMUNICATION_TIMEOUT;
            }
        }
    }
}

/**
 * Represents the health status of a plugin.
 */
enum HealthStatus {
    /**
     * Plugin is starting up.
     */
    INITIALIZING,

    /**
     * Plugin is healthy and functioning normally.
     */
    HEALTHY,

    /**
     * Plugin has encountered errors but may recover.
     */
    UNHEALTHY,

    /**
     * Plugin has communication errors with the agent.
     */
    COMMUNICATION_ERROR,

    /**
     * Plugin has not communicated with agent for extended period.
     */
    COMMUNICATION_TIMEOUT,

    /**
     * Plugin has been stopped.
     */
    STOPPED
}