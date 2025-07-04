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
 * Filename: PerformanceAlert.java
 *
 * Author: Claude Code
 *
 * Class name: PerformanceAlert
 *
 * Responsibilities:
 *   - Represent performance threshold violations
 *   - Provide context for performance issues
 *   - Support performance alerting and monitoring
 *
 * Collaborators:
 *   - PerformanceMonitor: Creates alerts when thresholds are exceeded
 *   - PerformanceSnapshot: Provides context for the alert
 */
package org.acmsl.bytehot.infrastructure.production;

import java.time.Instant;

/**
 * Represents a performance alert triggered when thresholds are exceeded.
 * @author Claude Code
 * @since 2025-07-04
 */
public class PerformanceAlert {
    
    /**
     * The type of performance alert.
     */
    private final PerformanceAlertType alertType;
    
    /**
     * The alert severity level.
     */
    private final AlertSeverity severity;
    
    /**
     * The alert message.
     */
    private final String message;
    
    /**
     * The metric value that triggered the alert.
     */
    private final double metricValue;
    
    /**
     * The threshold that was exceeded.
     */
    private final double threshold;
    
    /**
     * The performance snapshot associated with this alert.
     */
    private final PerformanceSnapshot snapshot;
    
    /**
     * The time when the alert was created.
     */
    private final Instant alertTime;
    
    /**
     * Creates a new PerformanceAlert.
     * @param alertType The alert type
     * @param severity The alert severity
     * @param message The alert message
     * @param metricValue The metric value
     * @param threshold The threshold
     * @param snapshot The performance snapshot
     * @param alertTime The alert time
     */
    protected PerformanceAlert(final PerformanceAlertType alertType,
                             final AlertSeverity severity,
                             final String message,
                             final double metricValue,
                             final double threshold,
                             final PerformanceSnapshot snapshot,
                             final Instant alertTime) {
        this.alertType = alertType;
        this.severity = severity;
        this.message = message;
        this.metricValue = metricValue;
        this.threshold = threshold;
        this.snapshot = snapshot;
        this.alertTime = alertTime;
    }
    
    /**
     * Creates a memory usage alert.
     * @param memoryUsage The memory usage ratio
     * @param snapshot The performance snapshot
     * @return A memory usage alert
     */
    public static PerformanceAlert memoryAlert(final double memoryUsage, final PerformanceSnapshot snapshot) {
        return new PerformanceAlert(
            PerformanceAlertType.MEMORY_USAGE,
            determineSeverity(memoryUsage, 0.85, 0.95),
            String.format("High memory usage detected: %.1f%%", memoryUsage * 100),
            memoryUsage,
            0.8, // Default threshold
            snapshot,
            Instant.now()
        );
    }
    
    /**
     * Creates a CPU usage alert.
     * @param cpuUsage The CPU usage ratio
     * @param snapshot The performance snapshot
     * @return A CPU usage alert
     */
    public static PerformanceAlert cpuAlert(final double cpuUsage, final PerformanceSnapshot snapshot) {
        return new PerformanceAlert(
            PerformanceAlertType.CPU_USAGE,
            determineSeverity(cpuUsage, 0.8, 0.95),
            String.format("High CPU usage detected: %.1f%%", cpuUsage * 100),
            cpuUsage,
            0.85, // Default threshold
            snapshot,
            Instant.now()
        );
    }
    
    /**
     * Creates a hot-swap performance alert.
     * @param averageTime The average hot-swap time
     * @param snapshot The performance snapshot
     * @return A hot-swap performance alert
     */
    public static PerformanceAlert hotSwapAlert(final double averageTime, final PerformanceSnapshot snapshot) {
        return new PerformanceAlert(
            PerformanceAlertType.HOTSWAP_PERFORMANCE,
            determineSeverity(averageTime, 5000.0, 10000.0),
            String.format("Slow hot-swap operations detected: %.1fms average", averageTime),
            averageTime,
            5000.0, // Default threshold
            snapshot,
            Instant.now()
        );
    }
    
    /**
     * Creates a garbage collection alert.
     * @param gcTime The GC time percentage
     * @param snapshot The performance snapshot
     * @return A garbage collection alert
     */
    public static PerformanceAlert gcAlert(final double gcTime, final PerformanceSnapshot snapshot) {
        return new PerformanceAlert(
            PerformanceAlertType.GARBAGE_COLLECTION,
            determineSeverity(gcTime, 10.0, 20.0),
            String.format("High garbage collection activity: %.1f%% of time", gcTime),
            gcTime,
            5.0, // Default threshold
            snapshot,
            Instant.now()
        );
    }
    
    /**
     * Gets the alert type.
     * @return The alert type
     */
    public PerformanceAlertType getAlertType() {
        return alertType;
    }
    
    /**
     * Gets the alert severity.
     * @return The alert severity
     */
    public AlertSeverity getSeverity() {
        return severity;
    }
    
    /**
     * Gets the alert message.
     * @return The alert message
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * Gets the metric value that triggered the alert.
     * @return The metric value
     */
    public double getMetricValue() {
        return metricValue;
    }
    
    /**
     * Gets the threshold that was exceeded.
     * @return The threshold
     */
    public double getThreshold() {
        return threshold;
    }
    
    /**
     * Gets the performance snapshot.
     * @return The performance snapshot
     */
    public PerformanceSnapshot getSnapshot() {
        return snapshot;
    }
    
    /**
     * Gets the alert time.
     * @return The alert time
     */
    public Instant getAlertTime() {
        return alertTime;
    }
    
    /**
     * Calculates how much the metric value exceeds the threshold.
     * @return The excess percentage
     */
    public double getThresholdExcess() {
        if (threshold == 0) {
            return 0.0;
        }
        return ((metricValue - threshold) / threshold) * 100;
    }
    
    /**
     * Checks if this is a critical alert.
     * @return true if critical, false otherwise
     */
    public boolean isCritical() {
        return severity == AlertSeverity.CRITICAL;
    }
    
    /**
     * Gets a detailed alert description.
     * @return A detailed description
     */
    public String getDetailedDescription() {
        return String.format(
            "%s Alert [%s]: %s (Value: %.2f, Threshold: %.2f, Excess: +%.1f%%) at %s",
            severity,
            alertType,
            message,
            metricValue,
            threshold,
            getThresholdExcess(),
            alertTime
        );
    }
    
    /**
     * Determines alert severity based on metric value and thresholds.
     * @param value The metric value
     * @param warningThreshold The warning threshold
     * @param criticalThreshold The critical threshold
     * @return The alert severity
     */
    protected static AlertSeverity determineSeverity(final double value, 
                                                   final double warningThreshold, 
                                                   final double criticalThreshold) {
        if (value >= criticalThreshold) {
            return AlertSeverity.CRITICAL;
        } else if (value >= warningThreshold) {
            return AlertSeverity.WARNING;
        } else {
            return AlertSeverity.INFO;
        }
    }
    
    @Override
    public String toString() {
        return getDetailedDescription();
    }
}