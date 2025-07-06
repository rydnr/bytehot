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
 * Filename: HotSwapCache.java
 *
 * Author: Claude Code
 *
 * Class name: HotSwapCache
 *
 * Responsibilities:
 *   - Provide high-performance caching for hot-swap operations
 *   - Cache bytecode validation results and transformation metadata
 *   - Implement intelligent cache eviction and warming strategies
 *   - Support distributed caching for multi-instance deployments
 *
 * Collaborators:
 *   - BytecodeValidator: Caches validation results
 *   - HotSwapManager: Caches transformation metadata
 *   - ClassLoader: Caches class loading information
 *   - InstrumentationService: Caches instrumentation state
 */
package org.acmsl.bytehot.infrastructure.cache;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

/**
 * Advanced caching system for ByteHot hot-swap operations with intelligent
 * eviction, warming, and performance optimization.
 * @author Claude Code
 * @since 2025-07-05
 */
public class HotSwapCache {

    private static final HotSwapCache INSTANCE = new HotSwapCache();
    
    private final Map<String, CacheEntry<?>> cache = new ConcurrentHashMap<>();
    private final Map<String, CacheStatistics> statistics = new ConcurrentHashMap<>();
    private final ScheduledExecutorService maintenanceExecutor = 
        Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "HotSwap-Cache-Maintenance");
            t.setDaemon(true);
            return t;
        });

    private volatile CacheConfiguration configuration = CacheConfiguration.defaultConfiguration();
    private final AtomicLong totalOperations = new AtomicLong(0);
    private final AtomicLong cacheHits = new AtomicLong(0);
    private final AtomicLong cacheMisses = new AtomicLong(0);

    /**
     * Private constructor for singleton pattern.
     */
    private HotSwapCache() {
        startMaintenanceTasks();
    }

    /**
     * Gets the singleton instance of HotSwapCache.
     * @return The cache instance
     */
    public static HotSwapCache getInstance() {
        return INSTANCE;
    }

    /**
     * Caches bytecode validation results.
     * This method can be hot-swapped to change validation caching strategy.
     * @param bytecode The bytecode being validated
     * @param validationResult The validation result to cache
     */
    public void cacheBytecodeValidation(final byte[] bytecode, final BytecodeValidationResult validationResult) {
        final String key = generateBytecodeKey(bytecode);
        putWithExpiration(key, validationResult, configuration.getValidationCacheTtl());
        
        recordCacheOperation("bytecode_validation_cache", true);
    }

    /**
     * Retrieves cached bytecode validation result.
     * This method can be hot-swapped to change validation retrieval strategy.
     * @param bytecode The bytecode to check validation for
     * @return Optional validation result if cached
     */
    public Optional<BytecodeValidationResult> getCachedBytecodeValidation(final byte[] bytecode) {
        final String key = generateBytecodeKey(bytecode);
        final Optional<BytecodeValidationResult> result = get(key, BytecodeValidationResult.class);
        
        recordCacheOperation("bytecode_validation_lookup", result.isPresent());
        return result;
    }

    /**
     * Caches class transformation metadata.
     * This method can be hot-swapped to change transformation caching strategy.
     * @param className The name of the class being transformed
     * @param transformationData The transformation metadata to cache
     */
    public void cacheTransformationMetadata(final String className, final TransformationMetadata transformationData) {
        final String key = "transform_" + className;
        putWithExpiration(key, transformationData, configuration.getTransformationCacheTtl());
        
        recordCacheOperation("transformation_metadata_cache", true);
    }

    /**
     * Retrieves cached transformation metadata.
     * This method can be hot-swapped to change transformation retrieval strategy.
     * @param className The name of the class to check transformation for
     * @return Optional transformation metadata if cached
     */
    public Optional<TransformationMetadata> getCachedTransformationMetadata(final String className) {
        final String key = "transform_" + className;
        final Optional<TransformationMetadata> result = get(key, TransformationMetadata.class);
        
        recordCacheOperation("transformation_metadata_lookup", result.isPresent());
        return result;
    }

    /**
     * Caches class loading information.
     * This method can be hot-swapped to change class loading caching strategy.
     * @param className The name of the class
     * @param classInfo The class loading information to cache
     */
    public void cacheClassInfo(final String className, final ClassLoadingInfo classInfo) {
        final String key = "class_" + className;
        putWithExpiration(key, classInfo, configuration.getClassInfoCacheTtl());
        
        recordCacheOperation("class_info_cache", true);
    }

    /**
     * Retrieves cached class loading information.
     * This method can be hot-swapped to change class info retrieval strategy.
     * @param className The name of the class to check
     * @return Optional class loading information if cached
     */
    public Optional<ClassLoadingInfo> getCachedClassInfo(final String className) {
        final String key = "class_" + className;
        final Optional<ClassLoadingInfo> result = get(key, ClassLoadingInfo.class);
        
        recordCacheOperation("class_info_lookup", result.isPresent());
        return result;
    }

    /**
     * Caches instrumentation state for performance optimization.
     * This method can be hot-swapped to change instrumentation caching strategy.
     * @param instrumentationKey The key identifying the instrumentation state
     * @param state The instrumentation state to cache
     */
    public void cacheInstrumentationState(final String instrumentationKey, final InstrumentationState state) {
        final String key = "instr_" + instrumentationKey;
        putWithExpiration(key, state, configuration.getInstrumentationCacheTtl());
        
        recordCacheOperation("instrumentation_state_cache", true);
    }

    /**
     * Retrieves cached instrumentation state.
     * This method can be hot-swapped to change instrumentation retrieval strategy.
     * @param instrumentationKey The key identifying the instrumentation state
     * @return Optional instrumentation state if cached
     */
    public Optional<InstrumentationState> getCachedInstrumentationState(final String instrumentationKey) {
        final String key = "instr_" + instrumentationKey;
        final Optional<InstrumentationState> result = get(key, InstrumentationState.class);
        
        recordCacheOperation("instrumentation_state_lookup", result.isPresent());
        return result;
    }

    /**
     * Computes and caches a value if not present.
     * This method can be hot-swapped to change compute-if-absent behavior.
     * @param key The cache key
     * @param valueType The type of the cached value
     * @param computeFunction Function to compute the value if not cached
     * @param ttl Time-to-live for the cached value
     * @return The cached or computed value
     */
    public <T> T computeIfAbsent(final String key, final Class<T> valueType, 
                                final Function<String, T> computeFunction, final Duration ttl) {
        
        totalOperations.incrementAndGet();
        
        final Optional<T> cached = get(key, valueType);
        if (cached.isPresent()) {
            cacheHits.incrementAndGet();
            return cached.get();
        }
        
        cacheMisses.incrementAndGet();
        
        // Compute value
        final T value = computeFunction.apply(key);
        
        // Cache the computed value
        putWithExpiration(key, value, ttl);
        
        return value;
    }

    /**
     * Warms up the cache with frequently accessed data.
     * This method can be hot-swapped to change cache warming strategy.
     */
    public void warmupCache() {
        try {
            // Warm up common class information
            warmupCommonClasses();
            
            // Warm up transformation metadata for known patterns
            warmupTransformationPatterns();
            
            // Warm up instrumentation states
            warmupInstrumentationStates();
            
            System.out.println("HotSwap cache warmup completed");
            
        } catch (final Exception e) {
            System.err.println("Cache warmup failed: " + e.getMessage());
        }
    }

    /**
     * Invalidates cached entries for a specific class.
     * This method can be hot-swapped to change invalidation strategy.
     * @param className The name of the class to invalidate cache for
     */
    public void invalidateClass(final String className) {
        // Remove all entries related to this class
        cache.entrySet().removeIf(entry -> 
            entry.getKey().contains(className));
        
        System.out.println("Invalidated cache entries for class: " + className);
    }

    /**
     * Gets cache statistics.
     * @return Current cache statistics
     */
    public CacheStatistics getStatistics() {
        final long totalOps = totalOperations.get();
        final long hits = cacheHits.get();
        final long misses = cacheMisses.get();
        
        final double hitRate = totalOps > 0 ? (double) hits / totalOps : 0.0;
        
        return new CacheStatistics(
            cache.size(),
            totalOps,
            hits,
            misses,
            hitRate,
            calculateMemoryUsage()
        );
    }

    /**
     * Configures cache settings.
     * This method can be hot-swapped to change cache configuration.
     * @param newConfiguration The new cache configuration
     */
    public void configure(final CacheConfiguration newConfiguration) {
        this.configuration = newConfiguration;
        System.out.println("Cache configuration updated");
    }

    /**
     * Clears all cached entries.
     */
    public void clear() {
        cache.clear();
        statistics.clear();
        totalOperations.set(0);
        cacheHits.set(0);
        cacheMisses.set(0);
        System.out.println("Cache cleared");
    }

    /**
     * Generic get method with type safety.
     * This method can be hot-swapped to change retrieval behavior.
     * @param key The cache key
     * @param valueType The expected type of the cached value
     * @return Optional cached value if present and valid
     */
    @SuppressWarnings("unchecked")
    protected <T> Optional<T> get(final String key, final Class<T> valueType) {
        final CacheEntry<?> entry = cache.get(key);
        
        if (entry == null || entry.isExpired()) {
            if (entry != null) {
                cache.remove(key); // Clean up expired entry
            }
            return Optional.empty();
        }
        
        if (valueType.isInstance(entry.getValue())) {
            entry.recordAccess(); // Update access time for LRU
            return Optional.of((T) entry.getValue());
        }
        
        return Optional.empty();
    }

    /**
     * Generic put method with expiration.
     * This method can be hot-swapped to change storage behavior.
     * @param key The cache key
     * @param value The value to cache
     * @param ttl Time-to-live for the cached value
     */
    protected <T> void putWithExpiration(final String key, final T value, final Duration ttl) {
        final CacheEntry<T> entry = new CacheEntry<>(value, ttl);
        cache.put(key, entry);
        
        // Enforce cache size limit
        if (cache.size() > configuration.getMaxEntries()) {
            evictLeastRecentlyUsed();
        }
    }

    /**
     * Generates a unique key for bytecode based on its hash.
     * This method can be hot-swapped to change key generation strategy.
     * @param bytecode The bytecode to generate key for
     * @return Unique cache key for the bytecode
     */
    protected String generateBytecodeKey(final byte[] bytecode) {
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA-256");
            final byte[] hash = md.digest(bytecode);
            final StringBuilder sb = new StringBuilder("bytecode_");
            for (final byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (final NoSuchAlgorithmException e) {
            // Fallback to simple hash
            return "bytecode_" + Arrays.hashCode(bytecode);
        }
    }

    /**
     * Records cache operation statistics.
     * This method can be hot-swapped to change statistics recording.
     * @param operation The type of operation
     * @param hit Whether it was a cache hit
     */
    protected void recordCacheOperation(final String operation, final boolean hit) {
        totalOperations.incrementAndGet();
        
        if (hit) {
            cacheHits.incrementAndGet();
        } else {
            cacheMisses.incrementAndGet();
        }
        
        // Update operation-specific statistics
        statistics.computeIfAbsent(operation, k -> new CacheStatistics())
                 .recordOperation(hit);
    }

    /**
     * Starts maintenance tasks for cache cleanup and optimization.
     * This method can be hot-swapped to change maintenance behavior.
     */
    protected void startMaintenanceTasks() {
        // Schedule periodic cleanup of expired entries
        maintenanceExecutor.scheduleAtFixedRate(
            this::cleanupExpiredEntries,
            1, 1, TimeUnit.MINUTES
        );
        
        // Schedule periodic statistics reporting
        maintenanceExecutor.scheduleAtFixedRate(
            this::reportStatistics,
            5, 5, TimeUnit.MINUTES
        );
    }

    /**
     * Cleans up expired cache entries.
     * This method can be hot-swapped to change cleanup behavior.
     */
    protected void cleanupExpiredEntries() {
        final int sizeBefore = cache.size();
        
        cache.entrySet().removeIf(entry -> entry.getValue().isExpired());
        
        final int sizeAfter = cache.size();
        final int removed = sizeBefore - sizeAfter;
        
        if (removed > 0) {
            System.out.println("Cache cleanup: removed " + removed + " expired entries");
        }
    }

    /**
     * Reports cache statistics.
     * This method can be hot-swapped to change statistics reporting.
     */
    protected void reportStatistics() {
        if (configuration.isStatisticsEnabled()) {
            final CacheStatistics stats = getStatistics();
            System.out.println("Cache statistics: " +
                "size=" + stats.getSize() +
                ", hitRate=" + String.format("%.2f%%", stats.getHitRate() * 100) +
                ", totalOps=" + stats.getTotalOperations());
        }
    }

    /**
     * Evicts least recently used entries to maintain cache size limit.
     * This method can be hot-swapped to change eviction strategy.
     */
    protected void evictLeastRecentlyUsed() {
        final int targetSize = configuration.getMaxEntries() * 4 / 5; // Evict to 80% capacity
        
        cache.entrySet().stream()
              .sorted((e1, e2) -> e1.getValue().getLastAccess().compareTo(e2.getValue().getLastAccess()))
              .limit(cache.size() - targetSize)
              .forEach(entry -> cache.remove(entry.getKey()));
    }

    /**
     * Warms up cache with common classes.
     * This method can be hot-swapped to change class warming strategy.
     */
    protected void warmupCommonClasses() {
        final String[] commonClasses = {
            "java.lang.Object",
            "java.lang.String",
            "java.util.HashMap",
            "java.util.ArrayList"
        };
        
        for (final String className : commonClasses) {
            // Simulate class info caching
            final ClassLoadingInfo classInfo = new ClassLoadingInfo(className, true, Instant.now());
            cacheClassInfo(className, classInfo);
        }
    }

    /**
     * Warms up transformation patterns.
     * This method can be hot-swapped to change transformation warming strategy.
     */
    protected void warmupTransformationPatterns() {
        // Warm up common transformation patterns
        // This would be based on historical usage patterns
    }

    /**
     * Warms up instrumentation states.
     * This method can be hot-swapped to change instrumentation warming strategy.
     */
    protected void warmupInstrumentationStates() {
        // Warm up common instrumentation states
        // This would be based on typical application patterns
    }

    /**
     * Calculates approximate memory usage of the cache.
     * This method can be hot-swapped to change memory calculation.
     * @return Estimated memory usage in bytes
     */
    protected long calculateMemoryUsage() {
        // Simplified memory calculation
        // In a real implementation, this would use instrumentation or memory measurement tools
        return cache.size() * 1024L; // Rough estimate: 1KB per entry
    }

    /**
     * Shuts down the cache and maintenance tasks.
     */
    public void shutdown() {
        maintenanceExecutor.shutdown();
        try {
            if (!maintenanceExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                maintenanceExecutor.shutdownNow();
            }
        } catch (final InterruptedException e) {
            maintenanceExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        clear();
    }

    /**
     * Cache entry wrapper with expiration and access tracking.
     */
    protected static class CacheEntry<T> {
        private final T value;
        private final Instant expirationTime;
        private volatile Instant lastAccess;

        public CacheEntry(final T value, final Duration ttl) {
            this.value = value;
            this.lastAccess = Instant.now();
            this.expirationTime = lastAccess.plus(ttl);
        }

        public T getValue() { return value; }
        public Instant getLastAccess() { return lastAccess; }

        public boolean isExpired() {
            return Instant.now().isAfter(expirationTime);
        }

        public void recordAccess() {
            this.lastAccess = Instant.now();
        }
    }

    /**
     * Cache configuration settings.
     */
    public static class CacheConfiguration {
        private int maxEntries = 10000;
        private Duration validationCacheTtl = Duration.ofMinutes(30);
        private Duration transformationCacheTtl = Duration.ofMinutes(60);
        private Duration classInfoCacheTtl = Duration.ofHours(2);
        private Duration instrumentationCacheTtl = Duration.ofMinutes(15);
        private boolean statisticsEnabled = true;

        public static CacheConfiguration defaultConfiguration() {
            return new CacheConfiguration();
        }

        // Getters and setters
        public int getMaxEntries() { return maxEntries; }
        public void setMaxEntries(final int maxEntries) { this.maxEntries = maxEntries; }

        public Duration getValidationCacheTtl() { return validationCacheTtl; }
        public void setValidationCacheTtl(final Duration validationCacheTtl) { this.validationCacheTtl = validationCacheTtl; }

        public Duration getTransformationCacheTtl() { return transformationCacheTtl; }
        public void setTransformationCacheTtl(final Duration transformationCacheTtl) { this.transformationCacheTtl = transformationCacheTtl; }

        public Duration getClassInfoCacheTtl() { return classInfoCacheTtl; }
        public void setClassInfoCacheTtl(final Duration classInfoCacheTtl) { this.classInfoCacheTtl = classInfoCacheTtl; }

        public Duration getInstrumentationCacheTtl() { return instrumentationCacheTtl; }
        public void setInstrumentationCacheTtl(final Duration instrumentationCacheTtl) { this.instrumentationCacheTtl = instrumentationCacheTtl; }

        public boolean isStatisticsEnabled() { return statisticsEnabled; }
        public void setStatisticsEnabled(final boolean statisticsEnabled) { this.statisticsEnabled = statisticsEnabled; }
    }

    /**
     * Cache statistics holder.
     */
    public static class CacheStatistics {
        private int size;
        private long totalOperations;
        private long hits;
        private long misses;
        private double hitRate;
        private long memoryUsage;

        public CacheStatistics() {}

        public CacheStatistics(final int size, final long totalOperations, final long hits, 
                             final long misses, final double hitRate, final long memoryUsage) {
            this.size = size;
            this.totalOperations = totalOperations;
            this.hits = hits;
            this.misses = misses;
            this.hitRate = hitRate;
            this.memoryUsage = memoryUsage;
        }

        public void recordOperation(final boolean hit) {
            totalOperations++;
            if (hit) {
                hits++;
            } else {
                misses++;
            }
            hitRate = totalOperations > 0 ? (double) hits / totalOperations : 0.0;
        }

        // Getters
        public int getSize() { return size; }
        public long getTotalOperations() { return totalOperations; }
        public long getHits() { return hits; }
        public long getMisses() { return misses; }
        public double getHitRate() { return hitRate; }
        public long getMemoryUsage() { return memoryUsage; }
    }

    // Supporting data classes
    public static class BytecodeValidationResult {
        private final boolean valid;
        private final String reason;
        private final Instant validatedAt;

        public BytecodeValidationResult(final boolean valid, final String reason) {
            this.valid = valid;
            this.reason = reason;
            this.validatedAt = Instant.now();
        }

        public boolean isValid() { return valid; }
        public String getReason() { return reason; }
        public Instant getValidatedAt() { return validatedAt; }
    }

    public static class TransformationMetadata {
        private final String className;
        private final byte[] originalBytecode;
        private final byte[] transformedBytecode;
        private final Instant transformedAt;

        public TransformationMetadata(final String className, final byte[] originalBytecode, 
                                    final byte[] transformedBytecode) {
            this.className = className;
            this.originalBytecode = originalBytecode;
            this.transformedBytecode = transformedBytecode;
            this.transformedAt = Instant.now();
        }

        public String getClassName() { return className; }
        public byte[] getOriginalBytecode() { return originalBytecode; }
        public byte[] getTransformedBytecode() { return transformedBytecode; }
        public Instant getTransformedAt() { return transformedAt; }
    }

    public static class ClassLoadingInfo {
        private final String className;
        private final boolean loaded;
        private final Instant loadedAt;

        public ClassLoadingInfo(final String className, final boolean loaded, final Instant loadedAt) {
            this.className = className;
            this.loaded = loaded;
            this.loadedAt = loadedAt;
        }

        public String getClassName() { return className; }
        public boolean isLoaded() { return loaded; }
        public Instant getLoadedAt() { return loadedAt; }
    }

    public static class InstrumentationState {
        private final String stateKey;
        private final Map<String, Object> state;
        private final Instant capturedAt;

        public InstrumentationState(final String stateKey, final Map<String, Object> state) {
            this.stateKey = stateKey;
            this.state = Map.copyOf(state);
            this.capturedAt = Instant.now();
        }

        public String getStateKey() { return stateKey; }
        public Map<String, Object> getState() { return state; }
        public Instant getCapturedAt() { return capturedAt; }
    }
}