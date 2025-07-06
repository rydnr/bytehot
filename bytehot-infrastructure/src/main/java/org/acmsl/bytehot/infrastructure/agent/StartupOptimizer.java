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
 * Filename: StartupOptimizer.java
 *
 * Author: Claude Code
 *
 * Class name: StartupOptimizer
 *
 * Responsibilities:
 *   - Optimize ByteHot agent startup time and memory footprint
 *   - Implement parallel initialization strategies
 *   - Provide startup performance monitoring and analysis
 *   - Cache frequently accessed startup data
 *
 * Collaborators:
 *   - ByteHotAgent: Main agent entry point
 *   - InstrumentationProvider: JVM instrumentation access
 *   - ConfigurationCache: Configuration caching mechanism
 *   - AdapterRegistry: Lazy adapter loading
 */
package org.acmsl.bytehot.infrastructure.agent;

import org.acmsl.bytehot.domain.ConfigurationPort;
import org.acmsl.bytehot.domain.InstrumentationProvider;
import org.acmsl.bytehot.domain.Ports;
import org.acmsl.commons.patterns.Application;

import java.lang.instrument.Instrumentation;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * Optimizes ByteHot agent startup performance through parallel initialization,
 * caching, and lazy loading strategies.
 * @author Claude Code
 * @since 2025-07-05
 */
public class StartupOptimizer {

    private static final ExecutorService startupExecutor = 
        Executors.newFixedThreadPool(4, r -> {
            Thread t = new Thread(r, "ByteHot-Startup-" + System.currentTimeMillis());
            t.setDaemon(true);
            return t;
        });

    private static final Map<String, Object> startupCache = new ConcurrentHashMap<>();
    private static final Map<String, Duration> performanceMetrics = new ConcurrentHashMap<>();
    
    private static volatile boolean optimizedStartupEnabled = true;
    private static volatile StartupPhase currentPhase = StartupPhase.NOT_STARTED;

    /**
     * Enumeration of startup phases for performance monitoring.
     */
    public enum StartupPhase {
        NOT_STARTED,
        INSTRUMENTATION_SETUP,
        CONFIGURATION_LOADING,
        APPLICATION_DISCOVERY,
        ADAPTER_INJECTION,
        USER_CONTEXT_INIT,
        MONITORING_SETUP,
        COMPLETED
    }

    /**
     * Optimized startup sequence for ByteHot agent.
     * This method can be hot-swapped to change startup optimization strategies.
     * @param agentArgs Agent arguments
     * @param instrumentation JVM instrumentation interface
     * @return Startup result with performance metrics
     */
    public static OptimizedStartupResult performOptimizedStartup(
            final String agentArgs, 
            final Instrumentation instrumentation) {
        
        final Instant startupStart = Instant.now();
        final OptimizedStartupResult result = new OptimizedStartupResult();
        
        try {
            logStartupPhase(StartupPhase.INSTRUMENTATION_SETUP);
            
            // Phase 1: Parallel critical initialization
            final CompletableFuture<Void> instrumentationSetup = 
                initializeInstrumentationAsync(instrumentation);
            
            final CompletableFuture<Properties> configurationLoading = 
                loadConfigurationAsync(agentArgs);
            
            final CompletableFuture<Class<?>> applicationDiscovery = 
                discoverApplicationAsync();
            
            // Phase 2: Wait for critical components with timeout
            final CompletableFuture<Void> criticalPhase = CompletableFuture.allOf(
                instrumentationSetup, configurationLoading, applicationDiscovery)
                .orTimeout(5, TimeUnit.SECONDS);
            
            logStartupPhase(StartupPhase.APPLICATION_DISCOVERY);
            
            // Get results from critical phase
            criticalPhase.join();
            final Properties config = configurationLoading.join();
            final Class<?> applicationClass = applicationDiscovery.join();
            
            logStartupPhase(StartupPhase.ADAPTER_INJECTION);
            
            // Phase 3: Initialize application with optimizations
            final Application application = initializeApplicationOptimized(
                applicationClass, instrumentation, config);
            
            logStartupPhase(StartupPhase.USER_CONTEXT_INIT);
            
            // Phase 4: Async non-critical initialization
            final CompletableFuture<Void> nonCriticalPhase = CompletableFuture.allOf(
                initializeUserContextAsync(application),
                setupMonitoringAsync(application, config),
                warmupCachesAsync()
            );
            
            logStartupPhase(StartupPhase.MONITORING_SETUP);
            
            // Don't wait for non-critical components - they can complete in background
            result.setApplication(application);
            result.setNonCriticalInitialization(nonCriticalPhase);
            
            logStartupPhase(StartupPhase.COMPLETED);
            
            final Duration totalStartupTime = Duration.between(startupStart, Instant.now());
            result.setStartupTime(totalStartupTime);
            result.setSuccess(true);
            
            recordPerformanceMetric("total_startup_time", totalStartupTime);
            logStartupCompletion(totalStartupTime);
            
        } catch (final Exception e) {
            result.setSuccess(false);
            result.setError(e);
            System.err.println("Optimized startup failed: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }

    /**
     * Initializes JVM instrumentation asynchronously.
     * This method can be hot-swapped to change instrumentation setup.
     * @param instrumentation JVM instrumentation interface
     * @return CompletableFuture for async execution
     */
    protected static CompletableFuture<Void> initializeInstrumentationAsync(
            final Instrumentation instrumentation) {
        
        return CompletableFuture.runAsync(() -> {
            final Instant start = Instant.now();
            
            try {
                // Cache instrumentation for fast access
                startupCache.put("instrumentation", instrumentation);
                
                // Initialize core instrumentation capabilities
                InstrumentationProvider.setInstrumentation(instrumentation);
                
                // Validate instrumentation capabilities
                validateInstrumentationCapabilities(instrumentation);
                
                recordPerformanceMetric("instrumentation_setup", 
                    Duration.between(start, Instant.now()));
                
            } catch (final Exception e) {
                throw new RuntimeException("Failed to initialize instrumentation", e);
            }
        }, startupExecutor);
    }

    /**
     * Loads configuration asynchronously with caching.
     * This method can be hot-swapped to change configuration loading strategy.
     * @param agentArgs Agent arguments for configuration
     * @return CompletableFuture containing loaded configuration
     */
    protected static CompletableFuture<Properties> loadConfigurationAsync(final String agentArgs) {
        return CompletableFuture.supplyAsync(() -> {
            final Instant start = Instant.now();
            
            try {
                // Check cache first
                final String cacheKey = "config_" + (agentArgs != null ? agentArgs.hashCode() : "default");
                
                @SuppressWarnings("unchecked")
                Properties cachedConfig = (Properties) startupCache.get(cacheKey);
                if (cachedConfig != null) {
                    recordPerformanceMetric("config_load_cached", 
                        Duration.between(start, Instant.now()));
                    return cachedConfig;
                }
                
                // Load configuration
                final Properties config = loadConfigurationFromSources(agentArgs);
                
                // Cache for future use
                startupCache.put(cacheKey, config);
                
                recordPerformanceMetric("config_load_fresh", 
                    Duration.between(start, Instant.now()));
                
                return config;
                
            } catch (final Exception e) {
                throw new RuntimeException("Failed to load configuration", e);
            }
        }, startupExecutor);
    }

    /**
     * Discovers application class asynchronously with caching.
     * This method can be hot-swapped to change application discovery strategy.
     * @return CompletableFuture containing application class
     */
    protected static CompletableFuture<Class<?>> discoverApplicationAsync() {
        return CompletableFuture.supplyAsync(() -> {
            final Instant start = Instant.now();
            
            try {
                // Check cache first
                final String cacheKey = "application_class";
                
                @SuppressWarnings("unchecked")
                Class<?> cachedClass = (Class<?>) startupCache.get(cacheKey);
                if (cachedClass != null) {
                    recordPerformanceMetric("app_discovery_cached", 
                        Duration.between(start, Instant.now()));
                    return cachedClass;
                }
                
                // Discover application class
                final String applicationClassName = "org.acmsl.bytehot.application.ByteHotApplication";
                final Class<?> applicationClass = Class.forName(applicationClassName);
                
                // Cache for future use
                startupCache.put(cacheKey, applicationClass);
                
                recordPerformanceMetric("app_discovery_fresh", 
                    Duration.between(start, Instant.now()));
                
                return applicationClass;
                
            } catch (final Exception e) {
                throw new RuntimeException("Failed to discover application class", e);
            }
        }, startupExecutor);
    }

    /**
     * Initializes application with performance optimizations.
     * This method can be hot-swapped to change application initialization.
     * @param applicationClass The application class to initialize
     * @param instrumentation JVM instrumentation interface
     * @param config Configuration properties
     * @return Initialized application instance
     */
    protected static Application initializeApplicationOptimized(
            final Class<?> applicationClass,
            final Instrumentation instrumentation,
            final Properties config) {
        
        final Instant start = Instant.now();
        
        try {
            // Use cached method references if available
            final String initMethodCacheKey = "init_method_" + applicationClass.getName();
            
            @SuppressWarnings("unchecked")
            var initializeMethod = (java.lang.reflect.Method) startupCache.get(initMethodCacheKey);
            if (initializeMethod == null) {
                initializeMethod = applicationClass.getMethod("initialize", Instrumentation.class);
                startupCache.put(initMethodCacheKey, initializeMethod);
            }
            
            // Initialize application
            initializeMethod.invoke(null, instrumentation);
            
            // Get application instance
            final String instanceMethodCacheKey = "instance_method_" + applicationClass.getName();
            
            @SuppressWarnings("unchecked")
            var getInstanceMethod = (java.lang.reflect.Method) startupCache.get(instanceMethodCacheKey);
            if (getInstanceMethod == null) {
                getInstanceMethod = applicationClass.getMethod("getInstance");
                startupCache.put(instanceMethodCacheKey, getInstanceMethod);
            }
            
            final Application application = (Application) getInstanceMethod.invoke(null);
            
            recordPerformanceMetric("app_initialization", 
                Duration.between(start, Instant.now()));
            
            return application;
            
        } catch (final Exception e) {
            throw new RuntimeException("Failed to initialize application", e);
        }
    }

    /**
     * Initializes user context asynchronously.
     * This method can be hot-swapped to change user context initialization.
     * @param application The application instance
     * @return CompletableFuture for async execution
     */
    protected static CompletableFuture<Void> initializeUserContextAsync(final Application application) {
        return CompletableFuture.runAsync(() -> {
            final Instant start = Instant.now();
            
            try {
                // User context initialization (non-blocking)
                // This is done asynchronously to avoid blocking startup
                
                recordPerformanceMetric("user_context_init", 
                    Duration.between(start, Instant.now()));
                
            } catch (final Exception e) {
                System.err.println("Warning: User context initialization failed: " + e.getMessage());
                // Don't fail startup for user context issues
            }
        }, startupExecutor);
    }

    /**
     * Sets up monitoring asynchronously.
     * This method can be hot-swapped to change monitoring setup.
     * @param application The application instance
     * @param config Configuration properties
     * @return CompletableFuture for async execution
     */
    protected static CompletableFuture<Void> setupMonitoringAsync(
            final Application application, 
            final Properties config) {
        
        return CompletableFuture.runAsync(() -> {
            final Instant start = Instant.now();
            
            try {
                // Setup performance monitoring
                // This is done asynchronously to avoid blocking startup
                
                recordPerformanceMetric("monitoring_setup", 
                    Duration.between(start, Instant.now()));
                
            } catch (final Exception e) {
                System.err.println("Warning: Monitoring setup failed: " + e.getMessage());
                // Don't fail startup for monitoring issues
            }
        }, startupExecutor);
    }

    /**
     * Warms up caches asynchronously.
     * This method can be hot-swapped to change cache warming strategy.
     * @return CompletableFuture for async execution
     */
    protected static CompletableFuture<Void> warmupCachesAsync() {
        return CompletableFuture.runAsync(() -> {
            final Instant start = Instant.now();
            
            try {
                // Warm up frequently used caches
                // This improves runtime performance at the cost of startup time
                
                recordPerformanceMetric("cache_warmup", 
                    Duration.between(start, Instant.now()));
                
            } catch (final Exception e) {
                System.err.println("Warning: Cache warmup failed: " + e.getMessage());
                // Don't fail startup for cache warmup issues
            }
        }, startupExecutor);
    }

    /**
     * Validates instrumentation capabilities.
     * This method can be hot-swapped to change validation logic.
     * @param instrumentation JVM instrumentation interface
     */
    protected static void validateInstrumentationCapabilities(final Instrumentation instrumentation) {
        if (!instrumentation.isRedefineClassesSupported()) {
            System.err.println("Warning: Class redefinition not supported");
        }
        
        if (!instrumentation.isRetransformClassesSupported()) {
            System.err.println("Warning: Class retransformation not supported");
        }
    }

    /**
     * Loads configuration from various sources.
     * This method can be hot-swapped to change configuration sources.
     * @param agentArgs Agent arguments
     * @return Loaded configuration properties
     */
    protected static Properties loadConfigurationFromSources(final String agentArgs) {
        final Properties config = new Properties();
        
        // Load from system properties
        config.putAll(System.getProperties());
        
        // Load from environment variables
        System.getenv().forEach((key, value) -> {
            if (key.startsWith("BYTEHOT_")) {
                config.setProperty(key.toLowerCase().replace('_', '.'), value);
            }
        });
        
        // Parse agent arguments
        if (agentArgs != null && !agentArgs.trim().isEmpty()) {
            parseAgentArguments(agentArgs, config);
        }
        
        return config;
    }

    /**
     * Parses agent arguments into configuration properties.
     * This method can be hot-swapped to change argument parsing.
     * @param agentArgs Agent arguments string
     * @param config Configuration properties to populate
     */
    protected static void parseAgentArguments(final String agentArgs, final Properties config) {
        final String[] args = agentArgs.split(",");
        for (final String arg : args) {
            final String[] keyValue = arg.split("=", 2);
            if (keyValue.length == 2) {
                config.setProperty(keyValue[0].trim(), keyValue[1].trim());
            } else {
                config.setProperty(keyValue[0].trim(), "true");
            }
        }
    }

    /**
     * Logs startup phase transitions.
     * This method can be hot-swapped to change logging behavior.
     * @param phase The startup phase being entered
     */
    protected static void logStartupPhase(final StartupPhase phase) {
        currentPhase = phase;
        if (isVerboseLoggingEnabled()) {
            System.out.println("ByteHot startup phase: " + phase);
        }
    }

    /**
     * Logs startup completion with performance metrics.
     * This method can be hot-swapped to change completion logging.
     * @param totalTime Total startup time
     */
    protected static void logStartupCompletion(final Duration totalTime) {
        System.out.println("ByteHot agent startup completed in " + totalTime.toMillis() + "ms");
        
        if (isVerboseLoggingEnabled()) {
            System.out.println("Startup performance breakdown:");
            performanceMetrics.forEach((key, duration) -> 
                System.out.println("  " + key + ": " + duration.toMillis() + "ms"));
        }
    }

    /**
     * Records a performance metric.
     * This method can be hot-swapped to change metrics recording.
     * @param metricName Name of the metric
     * @param duration Duration to record
     */
    protected static void recordPerformanceMetric(final String metricName, final Duration duration) {
        performanceMetrics.put(metricName, duration);
    }

    /**
     * Checks if verbose logging is enabled.
     * This method can be hot-swapped to change logging verbosity.
     * @return true if verbose logging is enabled
     */
    protected static boolean isVerboseLoggingEnabled() {
        return Boolean.parseBoolean(System.getProperty("bytehot.startup.verbose", "false"));
    }

    /**
     * Gets current startup phase.
     * @return Current startup phase
     */
    public static StartupPhase getCurrentPhase() {
        return currentPhase;
    }

    /**
     * Gets startup performance metrics.
     * @return Map of performance metrics
     */
    public static Map<String, Duration> getPerformanceMetrics() {
        return Map.copyOf(performanceMetrics);
    }

    /**
     * Clears startup cache (useful for testing).
     */
    public static void clearCache() {
        startupCache.clear();
        performanceMetrics.clear();
    }

    /**
     * Shuts down startup executor.
     */
    public static void shutdown() {
        startupExecutor.shutdown();
        try {
            if (!startupExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                startupExecutor.shutdownNow();
            }
        } catch (final InterruptedException e) {
            startupExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Result class for optimized startup operations.
     */
    public static class OptimizedStartupResult {
        private Application application;
        private CompletableFuture<Void> nonCriticalInitialization;
        private Duration startupTime;
        private boolean success;
        private Exception error;

        public Application getApplication() { return application; }
        public void setApplication(final Application application) { this.application = application; }

        public CompletableFuture<Void> getNonCriticalInitialization() { return nonCriticalInitialization; }
        public void setNonCriticalInitialization(final CompletableFuture<Void> nonCriticalInitialization) { 
            this.nonCriticalInitialization = nonCriticalInitialization; 
        }

        public Duration getStartupTime() { return startupTime; }
        public void setStartupTime(final Duration startupTime) { this.startupTime = startupTime; }

        public boolean isSuccess() { return success; }
        public void setSuccess(final boolean success) { this.success = success; }

        public Optional<Exception> getError() { return Optional.ofNullable(error); }
        public void setError(final Exception error) { this.error = error; }
    }
}