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
 * Filename: MemoryOptimizer.java
 *
 * Author: Claude Code
 *
 * Class name: MemoryOptimizer
 *
 * Responsibilities:
 *   - Optimize memory usage and allocation patterns
 *   - Trigger garbage collection when appropriate
 *   - Manage memory pressure and allocation strategies
 *   - Provide memory usage recommendations
 *
 * Collaborators:
 *   - ResourceSnapshot: Provides memory usage information
 *   - ResourceOptimization: Represents memory optimization operations
 *   - MemoryOptimizerConfiguration: Configuration for memory optimization
 */
package org.acmsl.bytehot.infrastructure.production;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Optimizes memory usage and manages memory-related performance issues.
 * @author Claude Code
 * @since 2025-07-04
 */
public class MemoryOptimizer {
    
    /**
     * Default memory pressure threshold for optimization.
     */
    public static final double DEFAULT_OPTIMIZATION_THRESHOLD = 0.85;
    
    /**
     * Default aggressive cleanup threshold.
     */
    public static final double DEFAULT_AGGRESSIVE_THRESHOLD = 0.95;
    
    /**
     * JVM memory management bean.
     */
    private final MemoryMXBean memoryBean;
    
    /**
     * JVM garbage collector beans.
     */
    private final List<GarbageCollectorMXBean> gcBeans;
    
    /**
     * JVM memory pool beans.
     */
    private final List<MemoryPoolMXBean> poolBeans;
    
    /**
     * Memory optimizer configuration.
     */
    private final MemoryOptimizerConfiguration configuration;
    
    /**
     * Number of memory optimizations performed.
     */
    private final AtomicLong optimizationCount;
    
    /**
     * Number of garbage collections triggered.
     */
    private final AtomicLong gcTriggeredCount;
    
    /**
     * Time of last optimization.
     */
    private volatile Instant lastOptimization;
    
    /**
     * Creates a new MemoryOptimizer with default configuration.
     */
    public MemoryOptimizer() {
        this(MemoryOptimizerConfiguration.defaultConfiguration());
    }
    
    /**
     * Creates a new MemoryOptimizer with the specified configuration.
     * @param configuration The memory optimizer configuration
     */
    public MemoryOptimizer(final MemoryOptimizerConfiguration configuration) {
        this.configuration = configuration;
        this.memoryBean = ManagementFactory.getMemoryMXBean();
        this.gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        this.poolBeans = ManagementFactory.getMemoryPoolMXBeans();
        this.optimizationCount = new AtomicLong(0);
        this.gcTriggeredCount = new AtomicLong(0);
    }
    
    /**
     * Optimizes memory based on the current resource snapshot.
     * @param snapshot The current resource snapshot
     * @param reason The reason for optimization
     * @return The memory optimization operation, or null if no optimization needed
     */
    public ResourceOptimization optimizeMemory(final ResourceSnapshot snapshot, final String reason) {
        double memoryUsage = (double) snapshot.getHeapUsed() / snapshot.getHeapMax();
        
        if (memoryUsage < configuration.getOptimizationThreshold()) {
            return null; // No optimization needed
        }
        
        // Check cooldown period
        if (lastOptimization != null) {
            Duration timeSinceLastOptimization = Duration.between(lastOptimization, Instant.now());
            if (timeSinceLastOptimization.compareTo(configuration.getOptimizationCooldown()) < 0) {
                return null; // Still in cooldown period
            }
        }
        
        MemoryOptimizationStrategy strategy = selectOptimizationStrategy(snapshot);
        ResourceOptimization optimization = createMemoryOptimization(strategy, snapshot, reason);
        
        // Execute the optimization
        executeOptimization(optimization);
        
        optimizationCount.incrementAndGet();
        lastOptimization = Instant.now();
        
        return optimization;
    }
    
    /**
     * Triggers garbage collection if conditions are met.
     * @param force Whether to force garbage collection regardless of conditions
     * @return true if GC was triggered, false otherwise
     */
    public boolean triggerGarbageCollection(final boolean force) {
        if (!force && !shouldTriggerGC()) {
            return false;
        }
        
        if (configuration.isGcTriggeringEnabled()) {
            System.gc();
            gcTriggeredCount.incrementAndGet();
            return true;
        }
        
        return false;
    }
    
    /**
     * Analyzes memory allocation patterns and provides recommendations.
     * @param snapshot The resource snapshot
     * @return Memory analysis with recommendations
     */
    public MemoryAnalysis analyzeMemoryUsage(final ResourceSnapshot snapshot) {
        MemoryAnalysis.Builder analysisBuilder = MemoryAnalysis.builder();
        
        // Analyze heap usage
        double heapUsage = (double) snapshot.getHeapUsed() / snapshot.getHeapMax();
        analysisBuilder.heapUsage(heapUsage);
        
        if (heapUsage > 0.9) {
            analysisBuilder.addRecommendation("Critical heap usage - consider increasing heap size");
        } else if (heapUsage > 0.8) {
            analysisBuilder.addRecommendation("High heap usage - monitor for memory leaks");
        }
        
        // Analyze non-heap usage
        double nonHeapUsage = (double) snapshot.getNonHeapUsed() / snapshot.getNonHeapMax();
        analysisBuilder.nonHeapUsage(nonHeapUsage);
        
        if (nonHeapUsage > 0.85) {
            analysisBuilder.addRecommendation("High non-heap usage - check metaspace and code cache");
        }
        
        // Analyze garbage collection activity
        MemoryPoolAnalysis poolAnalysis = analyzeMemoryPools();
        analysisBuilder.poolAnalysis(poolAnalysis);
        
        // Analyze GC performance
        GCAnalysis gcAnalysis = analyzeGarbageCollection();
        analysisBuilder.gcAnalysis(gcAnalysis);
        
        return analysisBuilder.build();
    }
    
    /**
     * Gets memory optimization statistics.
     * @return Memory optimization statistics
     */
    public MemoryOptimizationStatistics getStatistics() {
        return MemoryOptimizationStatistics.builder()
            .optimizationCount(optimizationCount.get())
            .gcTriggeredCount(gcTriggeredCount.get())
            .lastOptimization(lastOptimization)
            .currentHeapUsage(getCurrentHeapUsage())
            .currentNonHeapUsage(getCurrentNonHeapUsage())
            .totalGcCollections(getTotalGcCollections())
            .totalGcTime(getTotalGcTime())
            .build();
    }
    
    /**
     * Selects the appropriate optimization strategy based on the resource snapshot.
     * @param snapshot The resource snapshot
     * @return The selected optimization strategy
     */
    protected MemoryOptimizationStrategy selectOptimizationStrategy(final ResourceSnapshot snapshot) {
        double memoryUsage = (double) snapshot.getHeapUsed() / snapshot.getHeapMax();
        
        if (memoryUsage > configuration.getAggressiveThreshold()) {
            return MemoryOptimizationStrategy.AGGRESSIVE_CLEANUP;
        } else if (memoryUsage > configuration.getOptimizationThreshold()) {
            return MemoryOptimizationStrategy.CONSERVATIVE_CLEANUP;
        } else {
            return MemoryOptimizationStrategy.PREVENTIVE_OPTIMIZATION;
        }
    }
    
    /**
     * Creates a memory optimization operation.
     * @param strategy The optimization strategy
     * @param snapshot The resource snapshot
     * @param reason The reason for optimization
     * @return The memory optimization operation
     */
    protected ResourceOptimization createMemoryOptimization(final MemoryOptimizationStrategy strategy,
                                                           final ResourceSnapshot snapshot,
                                                           final String reason) {
        return ResourceOptimization.builder()
            .id(generateOptimizationId())
            .type(ResourceOptimizationType.MEMORY)
            .strategy(strategy.name())
            .reason(reason)
            .startTime(Instant.now())
            .resourceSnapshot(snapshot)
            .estimatedDuration(strategy.getEstimatedDuration())
            .build();
    }
    
    /**
     * Executes the memory optimization.
     * @param optimization The optimization to execute
     */
    protected void executeOptimization(final ResourceOptimization optimization) {
        MemoryOptimizationStrategy strategy = MemoryOptimizationStrategy.valueOf(optimization.getStrategy());
        
        switch (strategy) {
            case AGGRESSIVE_CLEANUP:
                performAggressiveCleanup();
                break;
            case CONSERVATIVE_CLEANUP:
                performConservativeCleanup();
                break;
            case PREVENTIVE_OPTIMIZATION:
                performPreventiveOptimization();
                break;
            default:
                // Do nothing for unknown strategies
                break;
        }
        
        optimization.markCompleted();
    }
    
    /**
     * Performs aggressive memory cleanup.
     */
    protected void performAggressiveCleanup() {
        // Trigger garbage collection
        if (configuration.isGcTriggeringEnabled()) {
            System.gc();
            gcTriggeredCount.incrementAndGet();
        }
        
        // Clear weak references
        clearWeakReferences();
        
        // Suggest heap dump if configured
        if (configuration.isHeapDumpOnAggressiveCleanup()) {
            suggestHeapDump();
        }
    }
    
    /**
     * Performs conservative memory cleanup.
     */
    protected void performConservativeCleanup() {
        // Clear soft references
        clearSoftReferences();
        
        // Optionally trigger GC
        if (configuration.isGcTriggeringEnabled() && shouldTriggerGC()) {
            System.gc();
            gcTriggeredCount.incrementAndGet();
        }
    }
    
    /**
     * Performs preventive memory optimization.
     */
    protected void performPreventiveOptimization() {
        // Clear unused caches
        clearUnusedCaches();
        
        // Optimize object allocation patterns
        optimizeAllocationPatterns();
    }
    
    /**
     * Determines if garbage collection should be triggered.
     * @return true if GC should be triggered, false otherwise
     */
    protected boolean shouldTriggerGC() {
        // Check if enough time has passed since last GC
        long currentGcTime = getTotalGcTime();
        long gcCollections = getTotalGcCollections();
        
        if (gcCollections == 0) {
            return true; // No GCs yet, safe to trigger
        }
        
        // Don't trigger if recent GC activity is high
        double avgGcTime = (double) currentGcTime / gcCollections;
        return avgGcTime < configuration.getMaxAverageGcTime();
    }
    
    /**
     * Gets current heap usage ratio.
     * @return Heap usage ratio (0.0 to 1.0)
     */
    protected double getCurrentHeapUsage() {
        MemoryUsage heapMemory = memoryBean.getHeapMemoryUsage();
        return (double) heapMemory.getUsed() / heapMemory.getMax();
    }
    
    /**
     * Gets current non-heap usage ratio.
     * @return Non-heap usage ratio (0.0 to 1.0)
     */
    protected double getCurrentNonHeapUsage() {
        MemoryUsage nonHeapMemory = memoryBean.getNonHeapMemoryUsage();
        return (double) nonHeapMemory.getUsed() / nonHeapMemory.getMax();
    }
    
    /**
     * Gets total garbage collections across all collectors.
     * @return Total GC collections
     */
    protected long getTotalGcCollections() {
        return gcBeans.stream()
            .mapToLong(GarbageCollectorMXBean::getCollectionCount)
            .sum();
    }
    
    /**
     * Gets total garbage collection time across all collectors.
     * @return Total GC time in milliseconds
     */
    protected long getTotalGcTime() {
        return gcBeans.stream()
            .mapToLong(GarbageCollectorMXBean::getCollectionTime)
            .sum();
    }
    
    /**
     * Analyzes memory pools.
     * @return Memory pool analysis
     */
    protected MemoryPoolAnalysis analyzeMemoryPools() {
        // TODO: Implement detailed memory pool analysis
        return MemoryPoolAnalysis.empty();
    }
    
    /**
     * Analyzes garbage collection performance.
     * @return GC analysis
     */
    protected GCAnalysis analyzeGarbageCollection() {
        // TODO: Implement detailed GC analysis
        return GCAnalysis.empty();
    }
    
    /**
     * Generates a unique optimization ID.
     * @return A unique optimization ID
     */
    protected String generateOptimizationId() {
        return "MEM-OPT-" + System.currentTimeMillis() + "-" + optimizationCount.get();
    }
    
    /**
     * Clears weak references.
     */
    protected void clearWeakReferences() {
        // Implementation would clear application-specific weak references
    }
    
    /**
     * Clears soft references.
     */
    protected void clearSoftReferences() {
        // Implementation would clear application-specific soft references
    }
    
    /**
     * Clears unused caches.
     */
    protected void clearUnusedCaches() {
        // Implementation would clear application-specific caches
    }
    
    /**
     * Optimizes object allocation patterns.
     */
    protected void optimizeAllocationPatterns() {
        // Implementation would optimize allocation patterns
    }
    
    /**
     * Suggests a heap dump for analysis.
     */
    protected void suggestHeapDump() {
        System.err.println("MEMORY OPTIMIZER: Consider taking a heap dump for analysis");
    }
}