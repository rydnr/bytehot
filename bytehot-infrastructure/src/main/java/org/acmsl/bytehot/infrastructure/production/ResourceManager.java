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
 * Filename: ResourceManager.java
 *
 * Author: Claude Code
 *
 * Class name: ResourceManager
 *
 * Responsibilities:
 *   - Manage system resources and optimization strategies
 *   - Monitor resource usage and trigger optimization
 *   - Coordinate memory, CPU, and I/O resource management
 *   - Provide resource allocation recommendations
 *
 * Collaborators:
 *   - PerformanceMonitor: Provides resource usage metrics
 *   - MemoryOptimizer: Handles memory-specific optimization
 *   - PerformanceOptimizer: Handles general performance optimization
 *   - ResourcePolicy: Defines resource management policies
 */
package org.acmsl.bytehot.infrastructure.production;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Manages system resources and optimization strategies for production environments.
 * @author Claude Code
 * @since 2025-07-04
 */
public class ResourceManager {
    
    /**
     * Default resource check interval in seconds.
     */
    public static final int DEFAULT_CHECK_INTERVAL_SECONDS = 60;
    
    /**
     * Default memory pressure threshold.
     */
    public static final double DEFAULT_MEMORY_PRESSURE_THRESHOLD = 0.8;
    
    /**
     * Default CPU pressure threshold.
     */
    public static final double DEFAULT_CPU_PRESSURE_THRESHOLD = 0.85;
    
    /**
     * JVM memory management bean.
     */
    private final MemoryMXBean memoryBean;
    
    /**
     * Resource management configuration.
     */
    private final ResourceManagerConfiguration configuration;
    
    /**
     * Scheduled executor for periodic resource checks.
     */
    private final ScheduledExecutorService scheduler;
    
    /**
     * Memory optimizer instance.
     */
    private final MemoryOptimizer memoryOptimizer;
    
    /**
     * Performance optimizer instance.
     */
    private final PerformanceOptimizer performanceOptimizer;
    
    /**
     * Resource usage history.
     */
    private final List<ResourceSnapshot> resourceHistory;
    
    /**
     * Active resource optimizations.
     */
    private final ConcurrentHashMap<String, ResourceOptimization> activeOptimizations;
    
    /**
     * Total number of optimizations performed.
     */
    private final AtomicLong totalOptimizations;
    
    /**
     * Whether resource management is active.
     */
    private volatile boolean active;
    
    /**
     * Time when resource management started.
     */
    private volatile Instant startTime;
    
    /**
     * Creates a new ResourceManager with default configuration.
     */
    public ResourceManager() {
        this(ResourceManagerConfiguration.defaultConfiguration());
    }
    
    /**
     * Creates a new ResourceManager with the specified configuration.
     * @param configuration The resource management configuration
     */
    public ResourceManager(final ResourceManagerConfiguration configuration) {
        this.configuration = configuration;
        this.memoryBean = ManagementFactory.getMemoryMXBean();
        this.scheduler = Executors.newScheduledThreadPool(2);
        this.memoryOptimizer = new MemoryOptimizer(configuration.getMemoryOptimizerConfiguration());
        this.performanceOptimizer = new PerformanceOptimizer(configuration.getPerformanceOptimizerConfiguration());
        this.resourceHistory = new ArrayList<>();
        this.activeOptimizations = new ConcurrentHashMap<>();
        this.totalOptimizations = new AtomicLong(0);
        this.active = false;
    }
    
    /**
     * Starts resource management.
     */
    public void startResourceManagement() {
        if (active) {
            return;
        }
        
        active = true;
        startTime = Instant.now();
        
        // Schedule periodic resource checks
        scheduler.scheduleAtFixedRate(
            this::checkResources,
            0,
            configuration.getCheckInterval().getSeconds(),
            TimeUnit.SECONDS
        );
        
        // Schedule periodic optimization reviews
        scheduler.scheduleAtFixedRate(
            this::reviewOptimizations,
            configuration.getOptimizationReviewInterval().getSeconds(),
            configuration.getOptimizationReviewInterval().getSeconds(),
            TimeUnit.SECONDS
        );
    }
    
    /**
     * Stops resource management.
     */
    public void stopResourceManagement() {
        if (!active) {
            return;
        }
        
        active = false;
        
        // Stop all active optimizations
        activeOptimizations.values().forEach(ResourceOptimization::stop);
        activeOptimizations.clear();
        
        // Shutdown scheduler
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
     * Triggers immediate resource optimization.
     * @param reason The reason for optimization
     * @return The optimization result
     */
    public ResourceOptimizationResult optimizeResources(final String reason) {
        if (!active) {
            return ResourceOptimizationResult.notActive("Resource management is not active");
        }
        
        ResourceSnapshot snapshot = captureResourceSnapshot();
        List<ResourceOptimization> optimizations = new ArrayList<>();
        
        // Check memory pressure
        if (isMemoryPressureHigh(snapshot)) {
            ResourceOptimization memoryOpt = memoryOptimizer.optimizeMemory(snapshot, reason);
            if (memoryOpt != null) {
                optimizations.add(memoryOpt);
                activeOptimizations.put(memoryOpt.getId(), memoryOpt);
            }
        }
        
        // Check CPU pressure
        if (isCpuPressureHigh(snapshot)) {
            ResourceOptimization cpuOpt = performanceOptimizer.optimizePerformance(snapshot, reason);
            if (cpuOpt != null) {
                optimizations.add(cpuOpt);
                activeOptimizations.put(cpuOpt.getId(), cpuOpt);
            }
        }
        
        // Check for general performance optimization opportunities
        if (shouldOptimizePerformance(snapshot)) {
            ResourceOptimization perfOpt = performanceOptimizer.optimizeGeneral(snapshot, reason);
            if (perfOpt != null) {
                optimizations.add(perfOpt);
                activeOptimizations.put(perfOpt.getId(), perfOpt);
            }
        }
        
        totalOptimizations.addAndGet(optimizations.size());
        
        return ResourceOptimizationResult.success(optimizations, snapshot);
    }
    
    /**
     * Gets current resource usage information.
     * @return Current resource usage
     */
    public ResourceUsage getCurrentResourceUsage() {
        ResourceSnapshot snapshot = captureResourceSnapshot();
        return ResourceUsage.fromSnapshot(snapshot);
    }
    
    /**
     * Gets resource management statistics.
     * @return Resource management statistics
     */
    public ResourceStatistics getResourceStatistics() {
        return ResourceStatistics.builder()
            .active(active)
            .startTime(startTime)
            .totalOptimizations(totalOptimizations.get())
            .activeOptimizations(activeOptimizations.size())
            .resourceHistory(resourceHistory.size())
            .averageMemoryUsage(calculateAverageMemoryUsage())
            .averageCpuUsage(calculateAverageCpuUsage())
            .build();
    }
    
    /**
     * Gets active optimizations.
     * @return List of active optimizations
     */
    public List<ResourceOptimization> getActiveOptimizations() {
        return new ArrayList<>(activeOptimizations.values());
    }
    
    /**
     * Gets resource usage history.
     * @return List of resource snapshots
     */
    public List<ResourceSnapshot> getResourceHistory() {
        return Collections.unmodifiableList(resourceHistory);
    }
    
    /**
     * Checks if resource management is active.
     * @return true if active, false otherwise
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * Gets the uptime of resource management.
     * @return The uptime duration
     */
    public Duration getUptime() {
        if (startTime == null) {
            return Duration.ZERO;
        }
        return Duration.between(startTime, Instant.now());
    }
    
    /**
     * Performs periodic resource checks.
     */
    protected void checkResources() {
        try {
            ResourceSnapshot snapshot = captureResourceSnapshot();
            resourceHistory.add(snapshot);
            
            // Limit history size
            if (resourceHistory.size() > configuration.getMaxHistorySize()) {
                resourceHistory.remove(0);
            }
            
            // Check for optimization triggers
            if (shouldTriggerOptimization(snapshot)) {
                optimizeResources("Periodic resource check");
            }
            
        } catch (Exception e) {
            // Log error but don't stop resource management
            System.err.println("Error during resource check: " + e.getMessage());
        }
    }
    
    /**
     * Reviews and manages active optimizations.
     */
    protected void reviewOptimizations() {
        try {
            List<String> completedOptimizations = new ArrayList<>();
            
            for (ResourceOptimization optimization : activeOptimizations.values()) {
                if (optimization.isCompleted() || optimization.hasExpired()) {
                    completedOptimizations.add(optimization.getId());
                }
            }
            
            // Remove completed optimizations
            completedOptimizations.forEach(activeOptimizations::remove);
            
        } catch (Exception e) {
            System.err.println("Error during optimization review: " + e.getMessage());
        }
    }
    
    /**
     * Captures a current resource snapshot.
     * @return A resource snapshot
     */
    protected ResourceSnapshot captureResourceSnapshot() {
        MemoryUsage heapMemory = memoryBean.getHeapMemoryUsage();
        MemoryUsage nonHeapMemory = memoryBean.getNonHeapMemoryUsage();
        
        return ResourceSnapshot.builder()
            .timestamp(Instant.now())
            .heapUsed(heapMemory.getUsed())
            .heapMax(heapMemory.getMax())
            .nonHeapUsed(nonHeapMemory.getUsed())
            .nonHeapMax(nonHeapMemory.getMax())
            .cpuUsage(getCurrentCpuUsage())
            .activeOptimizations(activeOptimizations.size())
            .build();
    }
    
    /**
     * Gets current CPU usage.
     * @return CPU usage as a percentage (0.0 to 1.0)
     */
    protected double getCurrentCpuUsage() {
        // This would be implemented using system-specific APIs
        // For now, return a placeholder value
        return 0.0;
    }
    
    /**
     * Checks if memory pressure is high.
     * @param snapshot The resource snapshot
     * @return true if memory pressure is high, false otherwise
     */
    protected boolean isMemoryPressureHigh(final ResourceSnapshot snapshot) {
        double memoryUsage = (double) snapshot.getHeapUsed() / snapshot.getHeapMax();
        return memoryUsage > configuration.getMemoryPressureThreshold();
    }
    
    /**
     * Checks if CPU pressure is high.
     * @param snapshot The resource snapshot
     * @return true if CPU pressure is high, false otherwise
     */
    protected boolean isCpuPressureHigh(final ResourceSnapshot snapshot) {
        return snapshot.getCpuUsage() > configuration.getCpuPressureThreshold();
    }
    
    /**
     * Determines if performance optimization should be triggered.
     * @param snapshot The resource snapshot
     * @return true if optimization should be triggered, false otherwise
     */
    protected boolean shouldOptimizePerformance(final ResourceSnapshot snapshot) {
        // Check if multiple resources are under pressure
        boolean memoryPressure = isMemoryPressureHigh(snapshot);
        boolean cpuPressure = isCpuPressureHigh(snapshot);
        
        return memoryPressure && cpuPressure;
    }
    
    /**
     * Determines if optimization should be triggered.
     * @param snapshot The resource snapshot
     * @return true if optimization should be triggered, false otherwise
     */
    protected boolean shouldTriggerOptimization(final ResourceSnapshot snapshot) {
        return isMemoryPressureHigh(snapshot) || 
               isCpuPressureHigh(snapshot) ||
               shouldOptimizePerformance(snapshot);
    }
    
    /**
     * Calculates average memory usage from history.
     * @return Average memory usage ratio
     */
    protected double calculateAverageMemoryUsage() {
        if (resourceHistory.isEmpty()) {
            return 0.0;
        }
        
        double total = resourceHistory.stream()
            .mapToDouble(snapshot -> (double) snapshot.getHeapUsed() / snapshot.getHeapMax())
            .sum();
        
        return total / resourceHistory.size();
    }
    
    /**
     * Calculates average CPU usage from history.
     * @return Average CPU usage
     */
    protected double calculateAverageCpuUsage() {
        if (resourceHistory.isEmpty()) {
            return 0.0;
        }
        
        double total = resourceHistory.stream()
            .mapToDouble(ResourceSnapshot::getCpuUsage)
            .sum();
        
        return total / resourceHistory.size();
    }
    
    /**
     * Shuts down the resource manager.
     */
    public void shutdown() {
        stopResourceManagement();
    }
}