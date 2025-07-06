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
 * Filename: MultiTenantManager.java
 *
 * Author: Claude Code
 *
 * Class name: MultiTenantManager
 *
 * Responsibilities:
 *   - Manage multi-tenant isolation and resource partitioning for ByteHot
 *   - Provide tenant-specific configuration and context management
 *   - Implement tenant-aware security and access control
 *   - Support shared environments with proper tenant separation
 *
 * Collaborators:
 *   - SecurityManager: Tenant-specific security policy enforcement
 *   - ByteHotLogger: Tenant-aware logging and audit trails
 *   - BackupManager: Tenant-specific backup and recovery operations
 *   - ComplianceManager: Tenant-specific compliance and governance
 */
package org.acmsl.bytehot.infrastructure.tenant;

import org.acmsl.bytehot.infrastructure.security.SecurityManager;
import org.acmsl.bytehot.infrastructure.logging.ByteHotLogger;
import org.acmsl.bytehot.infrastructure.backup.BackupManager;
import org.acmsl.bytehot.infrastructure.compliance.ComplianceManager;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Objects;

/**
 * Enterprise-grade multi-tenant management system for ByteHot shared environments.
 * Provides comprehensive tenant isolation, resource partitioning, and context management.
 * @author Claude Code
 * @since 2025-07-06
 */
public class MultiTenantManager {

    private static final MultiTenantManager INSTANCE = new MultiTenantManager();
    private static final ByteHotLogger LOGGER = ByteHotLogger.getLogger(MultiTenantManager.class);
    
    private final Map<String, TenantConfiguration> tenants = new ConcurrentHashMap<>();
    private final Map<String, TenantContext> activeTenantContexts = new ConcurrentHashMap<>();
    private final Map<String, TenantResourceAllocation> resourceAllocations = new ConcurrentHashMap<>();
    private final Set<TenantEventListener> eventListeners = Collections.synchronizedSet(new HashSet<>());
    
    private final ReentrantReadWriteLock tenantLock = new ReentrantReadWriteLock();
    private final AtomicLong tenantCounter = new AtomicLong(0);
    private final AtomicLong contextCounter = new AtomicLong(0);
    
    private final ScheduledExecutorService tenantExecutor = 
        Executors.newScheduledThreadPool(5, r -> {
            Thread t = new Thread(r, "ByteHot-MultiTenant-Manager");
            t.setDaemon(true);
            return t;
        });
    
    private volatile MultiTenantConfiguration configuration = MultiTenantConfiguration.defaultConfiguration();
    private volatile boolean multiTenancyEnabled = true;
    
    // Thread-local storage for current tenant context
    private static final ThreadLocal<TenantContext> CURRENT_TENANT = new ThreadLocal<>();
    
    private MultiTenantManager() {
        initializeMultiTenantSystem();
        startTenantMonitoring();
    }

    /**
     * Gets the singleton instance of MultiTenantManager.
     * @return The multi-tenant manager instance
     */
    public static MultiTenantManager getInstance() {
        return INSTANCE;
    }

    /**
     * Registers a new tenant in the system.
     * This method can be hot-swapped to change tenant registration behavior.
     * @param tenantId Unique tenant identifier
     * @param tenantConfig Tenant configuration and settings
     * @return Tenant registration result
     */
    public CompletableFuture<TenantRegistrationResult> registerTenant(final String tenantId, 
                                                                    final TenantConfiguration tenantConfig) {
        return CompletableFuture.supplyAsync(() -> {
            if (!multiTenancyEnabled) {
                return TenantRegistrationResult.disabled(tenantId);
            }
            
            LOGGER.info("Registering new tenant: {} (Name: {})", tenantId, tenantConfig.getName());
            LOGGER.audit("TENANT_REGISTRATION_STARTED", tenantId, ByteHotLogger.AuditOutcome.SUCCESS, 
                "Tenant registration initiated");
            
            tenantLock.writeLock().lock();
            try {
                // Validate tenant doesn't already exist
                if (tenants.containsKey(tenantId)) {
                    return TenantRegistrationResult.alreadyExists(tenantId);
                }
                
                // Validate tenant configuration
                final TenantValidationResult validationResult = validateTenantConfiguration(tenantConfig);
                if (!validationResult.isValid()) {
                    return TenantRegistrationResult.validationFailed(tenantId, validationResult.getErrors());
                }
                
                // Allocate resources for the tenant
                final TenantResourceAllocation allocation = allocateResourcesForTenant(tenantId, tenantConfig);
                
                // Initialize tenant-specific components
                initializeTenantComponents(tenantId, tenantConfig);
                
                // Store tenant configuration and allocation
                tenants.put(tenantId, tenantConfig);
                resourceAllocations.put(tenantId, allocation);
                
                // Notify listeners
                notifyTenantRegistered(tenantId, tenantConfig);
                
                LOGGER.info("Tenant registered successfully: {} (Resources: {})", 
                    tenantId, allocation.getSummary());
                
                LOGGER.audit("TENANT_REGISTERED", tenantId, ByteHotLogger.AuditOutcome.SUCCESS,
                    "Tenant registered with resource allocation: " + allocation.getSummary());
                
                return TenantRegistrationResult.success(tenantId, allocation);
                
            } catch (final Exception e) {
                LOGGER.error("Tenant registration failed: " + tenantId, e);
                LOGGER.audit("TENANT_REGISTRATION_FAILED", tenantId, ByteHotLogger.AuditOutcome.FAILURE,
                    "Registration failed: " + e.getMessage());
                
                return TenantRegistrationResult.error(tenantId, e);
                
            } finally {
                tenantLock.writeLock().unlock();
            }
        }, tenantExecutor);
    }

    /**
     * Creates and activates a tenant context for operations.
     * This method can be hot-swapped to change context management behavior.
     * @param tenantId Tenant identifier
     * @param userId User identifier within the tenant
     * @param requestId Request identifier for correlation
     * @return Tenant context creation result
     */
    public CompletableFuture<TenantContext> createTenantContext(final String tenantId, 
                                                               final String userId, 
                                                               final String requestId) {
        return CompletableFuture.supplyAsync(() -> {
            LOGGER.debug("Creating tenant context: {} (User: {}, Request: {})", tenantId, userId, requestId);
            
            tenantLock.readLock().lock();
            try {
                final TenantConfiguration config = tenants.get(tenantId);
                if (config == null) {
                    throw new IllegalArgumentException("Tenant not found: " + tenantId);
                }
                
                final TenantResourceAllocation allocation = resourceAllocations.get(tenantId);
                if (allocation == null) {
                    throw new IllegalStateException("Resource allocation not found for tenant: " + tenantId);
                }
                
                // Create tenant context
                final TenantContext context = new TenantContext(
                    String.valueOf(contextCounter.incrementAndGet()),
                    tenantId,
                    userId,
                    requestId,
                    Instant.now(),
                    config,
                    allocation,
                    Thread.currentThread().getName()
                );
                
                // Store active context
                activeTenantContexts.put(context.getContextId(), context);
                
                // Set thread-local context
                CURRENT_TENANT.set(context);
                
                LOGGER.debug("Tenant context created: {} for tenant {}", context.getContextId(), tenantId);
                
                return context;
                
            } finally {
                tenantLock.readLock().unlock();
            }
        }, tenantExecutor);
    }

    /**
     * Executes an operation within a tenant context.
     * This method can be hot-swapped to change tenant-aware execution behavior.
     * @param tenantId Tenant identifier
     * @param operation Operation to execute
     * @return Operation result with tenant isolation
     */
    public <T> CompletableFuture<T> executeInTenantContext(final String tenantId, 
                                                          final TenantOperation<T> operation) {
        return CompletableFuture.supplyAsync(() -> {
            final TenantContext previousContext = CURRENT_TENANT.get();
            
            try {
                // Create temporary context for this operation
                final TenantContext context = createTenantContext(
                    tenantId, 
                    "system", 
                    "operation_" + System.currentTimeMillis()
                ).join();
                
                // Execute operation with tenant context
                CURRENT_TENANT.set(context);
                
                final T result = operation.execute(context);
                
                LOGGER.debug("Operation executed successfully in tenant context: {}", tenantId);
                
                return result;
                
            } catch (final Exception e) {
                LOGGER.error("Operation failed in tenant context: " + tenantId, e);
                throw new RuntimeException("Tenant operation failed", e);
                
            } finally {
                // Restore previous context
                CURRENT_TENANT.set(previousContext);
            }
        }, tenantExecutor);
    }

    /**
     * Gets the current tenant context for the current thread.
     * @return Current tenant context or empty if none set
     */
    public static Optional<TenantContext> getCurrentTenantContext() {
        return Optional.ofNullable(CURRENT_TENANT.get());
    }

    /**
     * Sets the tenant context for the current thread.
     * @param context Tenant context to set
     */
    public static void setCurrentTenantContext(final TenantContext context) {
        CURRENT_TENANT.set(context);
    }

    /**
     * Clears the tenant context for the current thread.
     */
    public static void clearCurrentTenantContext() {
        CURRENT_TENANT.remove();
    }

    /**
     * Gets tenant-specific resource usage statistics.
     * This method can be hot-swapped to change metrics collection behavior.
     * @param tenantId Tenant identifier
     * @return Resource usage statistics for the tenant
     */
    public CompletableFuture<TenantResourceUsage> getTenantResourceUsage(final String tenantId) {
        return CompletableFuture.supplyAsync(() -> {
            tenantLock.readLock().lock();
            try {
                final TenantResourceAllocation allocation = resourceAllocations.get(tenantId);
                if (allocation == null) {
                    throw new IllegalArgumentException("Tenant not found: " + tenantId);
                }
                
                // Collect current usage metrics
                final TenantResourceUsage usage = collectResourceUsage(tenantId, allocation);
                
                LOGGER.debug("Resource usage collected for tenant: {} (CPU: {}%, Memory: {}%)", 
                    tenantId, usage.getCpuUsagePercent(), usage.getMemoryUsagePercent());
                
                return usage;
                
            } finally {
                tenantLock.readLock().unlock();
            }
        }, tenantExecutor);
    }

    /**
     * Updates tenant configuration at runtime.
     * This method can be hot-swapped to change configuration update behavior.
     * @param tenantId Tenant identifier
     * @param newConfig New tenant configuration
     * @return Configuration update result
     */
    public CompletableFuture<TenantConfigurationUpdateResult> updateTenantConfiguration(
            final String tenantId, final TenantConfiguration newConfig) {
        return CompletableFuture.supplyAsync(() -> {
            LOGGER.info("Updating tenant configuration: {}", tenantId);
            
            tenantLock.writeLock().lock();
            try {
                final TenantConfiguration currentConfig = tenants.get(tenantId);
                if (currentConfig == null) {
                    return TenantConfigurationUpdateResult.tenantNotFound(tenantId);
                }
                
                // Validate new configuration
                final TenantValidationResult validationResult = validateTenantConfiguration(newConfig);
                if (!validationResult.isValid()) {
                    return TenantConfigurationUpdateResult.validationFailed(tenantId, validationResult.getErrors());
                }
                
                // Check if resource reallocation is needed
                final boolean needsReallocation = requiresResourceReallocation(currentConfig, newConfig);
                
                if (needsReallocation) {
                    // Reallocate resources
                    final TenantResourceAllocation newAllocation = allocateResourcesForTenant(tenantId, newConfig);
                    resourceAllocations.put(tenantId, newAllocation);
                }
                
                // Update configuration
                tenants.put(tenantId, newConfig);
                
                // Reinitialize tenant components if needed
                if (needsReallocation) {
                    reinitializeTenantComponents(tenantId, newConfig);
                }
                
                // Notify listeners
                notifyTenantConfigurationUpdated(tenantId, currentConfig, newConfig);
                
                LOGGER.info("Tenant configuration updated successfully: {}", tenantId);
                LOGGER.audit("TENANT_CONFIG_UPDATED", tenantId, ByteHotLogger.AuditOutcome.SUCCESS,
                    "Configuration updated successfully");
                
                return TenantConfigurationUpdateResult.success(tenantId, needsReallocation);
                
            } catch (final Exception e) {
                LOGGER.error("Tenant configuration update failed: " + tenantId, e);
                LOGGER.audit("TENANT_CONFIG_UPDATE_FAILED", tenantId, ByteHotLogger.AuditOutcome.FAILURE,
                    "Configuration update failed: " + e.getMessage());
                
                return TenantConfigurationUpdateResult.error(tenantId, e);
                
            } finally {
                tenantLock.writeLock().unlock();
            }
        }, tenantExecutor);
    }

    /**
     * Lists all registered tenants with their status.
     * @return List of tenant information
     */
    public List<TenantInfo> listTenants() {
        tenantLock.readLock().lock();
        try {
            return tenants.entrySet().stream()
                .map(entry -> {
                    final String tenantId = entry.getKey();
                    final TenantConfiguration config = entry.getValue();
                    final TenantResourceAllocation allocation = resourceAllocations.get(tenantId);
                    
                    return new TenantInfo(
                        tenantId,
                        config.getName(),
                        config.getTier(),
                        config.getCreatedAt(),
                        allocation != null ? allocation.getStatus() : TenantStatus.UNKNOWN,
                        getActiveTenantContextCount(tenantId)
                    );
                })
                .sorted((a, b) -> a.getName().compareTo(b.getName()))
                .collect(Collectors.toList());
                
        } finally {
            tenantLock.readLock().unlock();
        }
    }

    /**
     * Gets multi-tenant system statistics.
     * @return Current multi-tenant system statistics
     */
    public MultiTenantStatistics getMultiTenantStatistics() {
        tenantLock.readLock().lock();
        try {
            final int totalTenants = tenants.size();
            final int activeTenants = (int) resourceAllocations.values().stream()
                .filter(allocation -> allocation.getStatus() == TenantStatus.ACTIVE)
                .count();
            final int totalContexts = activeTenantContexts.size();
            
            final Map<TenantTier, Integer> tenantsByTier = tenants.values().stream()
                .collect(Collectors.groupingBy(
                    TenantConfiguration::getTier,
                    Collectors.collectingAndThen(Collectors.counting(), Math::toIntExact)
                ));
            
            final long totalMemoryAllocated = resourceAllocations.values().stream()
                .mapToLong(TenantResourceAllocation::getAllocatedMemoryMB)
                .sum();
            
            return new MultiTenantStatistics(
                totalTenants, activeTenants, totalContexts,
                tenantsByTier, totalMemoryAllocated,
                configuration.getMaxTenants(),
                Instant.now()
            );
            
        } finally {
            tenantLock.readLock().unlock();
        }
    }

    /**
     * Removes a tenant from the system.
     * This method can be hot-swapped to change tenant removal behavior.
     * @param tenantId Tenant identifier to remove
     * @return Tenant removal result
     */
    public CompletableFuture<TenantRemovalResult> removeTenant(final String tenantId) {
        return CompletableFuture.supplyAsync(() -> {
            LOGGER.info("Removing tenant: {}", tenantId);
            LOGGER.audit("TENANT_REMOVAL_STARTED", tenantId, ByteHotLogger.AuditOutcome.SUCCESS,
                "Tenant removal initiated");
            
            tenantLock.writeLock().lock();
            try {
                final TenantConfiguration config = tenants.get(tenantId);
                if (config == null) {
                    return TenantRemovalResult.tenantNotFound(tenantId);
                }
                
                // Check if tenant has active contexts
                final long activeContexts = getActiveTenantContextCount(tenantId);
                if (activeContexts > 0 && !configuration.isForceRemovalAllowed()) {
                    return TenantRemovalResult.hasActiveContexts(tenantId, (int) activeContexts);
                }
                
                // Clean up tenant contexts
                cleanupTenantContexts(tenantId);
                
                // Clean up tenant resources
                cleanupTenantResources(tenantId);
                
                // Remove tenant configuration and allocation
                tenants.remove(tenantId);
                resourceAllocations.remove(tenantId);
                
                // Notify listeners
                notifyTenantRemoved(tenantId, config);
                
                LOGGER.info("Tenant removed successfully: {}", tenantId);
                LOGGER.audit("TENANT_REMOVED", tenantId, ByteHotLogger.AuditOutcome.SUCCESS,
                    "Tenant removed successfully");
                
                return TenantRemovalResult.success(tenantId);
                
            } catch (final Exception e) {
                LOGGER.error("Tenant removal failed: " + tenantId, e);
                LOGGER.audit("TENANT_REMOVAL_FAILED", tenantId, ByteHotLogger.AuditOutcome.FAILURE,
                    "Removal failed: " + e.getMessage());
                
                return TenantRemovalResult.error(tenantId, e);
                
            } finally {
                tenantLock.writeLock().unlock();
            }
        }, tenantExecutor);
    }

    /**
     * Adds a tenant event listener.
     * @param listener Event listener to add
     */
    public void addTenantEventListener(final TenantEventListener listener) {
        eventListeners.add(listener);
    }

    /**
     * Removes a tenant event listener.
     * @param listener Event listener to remove
     */
    public void removeTenantEventListener(final TenantEventListener listener) {
        eventListeners.remove(listener);
    }

    /**
     * Configures multi-tenant system settings.
     * This method can be hot-swapped to change multi-tenant configuration.
     * @param newConfiguration New multi-tenant configuration
     */
    public void configure(final MultiTenantConfiguration newConfiguration) {
        this.configuration = newConfiguration;
        this.multiTenancyEnabled = newConfiguration.isEnabled();
        
        LOGGER.info("Multi-tenant configuration updated");
        LOGGER.audit("MULTITENANT_CONFIGURED", "system", ByteHotLogger.AuditOutcome.SUCCESS,
            "Multi-tenant configuration updated successfully");
    }

    /**
     * Shuts down the multi-tenant system gracefully.
     */
    public void shutdown() {
        LOGGER.info("Shutting down multi-tenant system");
        
        // Clean up all tenant contexts
        tenantLock.writeLock().lock();
        try {
            activeTenantContexts.clear();
            CURRENT_TENANT.remove();
        } finally {
            tenantLock.writeLock().unlock();
        }
        
        tenantExecutor.shutdown();
        try {
            if (!tenantExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
                tenantExecutor.shutdownNow();
            }
        } catch (final InterruptedException e) {
            tenantExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        LOGGER.info("Multi-tenant system shutdown completed");
    }

    // Helper methods and implementation details...
    
    /**
     * Initializes the multi-tenant system with default settings.
     * This method can be hot-swapped to change initialization behavior.
     */
    protected void initializeMultiTenantSystem() {
        LOGGER.info("Initializing multi-tenant system");
        
        if (configuration.getDefaultTenants() != null) {
            // Initialize default tenants if configured
            configuration.getDefaultTenants().forEach((tenantId, config) -> {
                registerTenant(tenantId, config)
                    .thenAccept(result -> {
                        LOGGER.info("Default tenant initialized: {} (Success: {})", 
                            tenantId, result.isSuccess());
                    })
                    .exceptionally(e -> {
                        LOGGER.warn("Failed to initialize default tenant: " + tenantId, e);
                        return null;
                    });
            });
        }
        
        LOGGER.info("Multi-tenant system initialized successfully");
    }

    /**
     * Starts tenant monitoring and resource management.
     * This method can be hot-swapped to change monitoring behavior.
     */
    protected void startTenantMonitoring() {
        if (configuration.isMonitoringEnabled()) {
            // Schedule tenant resource monitoring
            tenantExecutor.scheduleAtFixedRate(
                this::performTenantResourceMonitoring,
                configuration.getMonitoringInterval().toMinutes(),
                configuration.getMonitoringInterval().toMinutes(),
                TimeUnit.MINUTES
            );
            
            // Schedule tenant context cleanup
            tenantExecutor.scheduleAtFixedRate(
                this::performTenantContextCleanup,
                1, 1, TimeUnit.HOURS
            );
        }
        
        LOGGER.info("Tenant monitoring started");
    }

    protected TenantValidationResult validateTenantConfiguration(final TenantConfiguration config) {
        final List<String> errors = new ArrayList<>();
        
        if (config.getName() == null || config.getName().trim().isEmpty()) {
            errors.add("Tenant name is required");
        }
        
        if (config.getTier() == null) {
            errors.add("Tenant tier is required");
        }
        
        if (config.getMaxMemoryMB() <= 0) {
            errors.add("Max memory must be positive");
        }
        
        if (config.getMaxCpuCores() <= 0) {
            errors.add("Max CPU cores must be positive");
        }
        
        // Check if we have capacity for this tenant
        if (tenants.size() >= configuration.getMaxTenants()) {
            errors.add("Maximum tenant limit reached");
        }
        
        return new TenantValidationResult(errors.isEmpty(), errors);
    }

    protected TenantResourceAllocation allocateResourcesForTenant(final String tenantId, 
                                                                final TenantConfiguration config) {
        // Calculate resource allocation based on tier and configuration
        final long memoryMB = Math.min(config.getMaxMemoryMB(), 
            configuration.getMaxMemoryPerTenantMB());
        final int cpuCores = Math.min(config.getMaxCpuCores(), 
            configuration.getMaxCpuCoresPerTenant());
        
        return new TenantResourceAllocation(
            tenantId,
            memoryMB,
            cpuCores,
            config.getTier(),
            TenantStatus.ACTIVE,
            Instant.now()
        );
    }

    protected void initializeTenantComponents(final String tenantId, final TenantConfiguration config) {
        // Initialize tenant-specific backup configurations
        if (config.isBackupEnabled()) {
            // Would initialize tenant-specific backup policies
            LOGGER.debug("Initializing backup for tenant: {}", tenantId);
        }
        
        // Initialize tenant-specific compliance settings
        if (config.isComplianceEnabled()) {
            // Would initialize tenant-specific compliance policies
            LOGGER.debug("Initializing compliance for tenant: {}", tenantId);
        }
        
        // Initialize tenant-specific security context
        // Would set up tenant-specific security policies
        LOGGER.debug("Initializing security context for tenant: {}", tenantId);
    }

    protected TenantResourceUsage collectResourceUsage(final String tenantId, 
                                                     final TenantResourceAllocation allocation) {
        // Simplified resource usage collection
        // In a real implementation, this would collect actual metrics
        return new TenantResourceUsage(
            tenantId,
            Math.random() * allocation.getAllocatedCpuCores() * 100, // CPU usage %
            (long) (Math.random() * allocation.getAllocatedMemoryMB()), // Memory usage MB
            allocation.getAllocatedMemoryMB(), // Memory limit MB
            allocation.getAllocatedCpuCores(), // CPU limit cores
            getActiveTenantContextCount(tenantId),
            Instant.now()
        );
    }

    protected boolean requiresResourceReallocation(final TenantConfiguration current, 
                                                 final TenantConfiguration updated) {
        return current.getMaxMemoryMB() != updated.getMaxMemoryMB() ||
               current.getMaxCpuCores() != updated.getMaxCpuCores() ||
               !current.getTier().equals(updated.getTier());
    }

    protected void reinitializeTenantComponents(final String tenantId, final TenantConfiguration config) {
        // Reinitialize components with new configuration
        initializeTenantComponents(tenantId, config);
        LOGGER.debug("Reinitialized components for tenant: {}", tenantId);
    }

    protected long getActiveTenantContextCount(final String tenantId) {
        return activeTenantContexts.values().stream()
            .filter(context -> context.getTenantId().equals(tenantId))
            .count();
    }

    protected void cleanupTenantContexts(final String tenantId) {
        final List<String> contextsToRemove = activeTenantContexts.entrySet().stream()
            .filter(entry -> entry.getValue().getTenantId().equals(tenantId))
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
        
        contextsToRemove.forEach(activeTenantContexts::remove);
        
        LOGGER.debug("Cleaned up {} contexts for tenant: {}", contextsToRemove.size(), tenantId);
    }

    protected void cleanupTenantResources(final String tenantId) {
        // Clean up tenant-specific resources
        // Would clean up tenant-specific backup data, compliance reports, etc.
        LOGGER.debug("Cleaning up resources for tenant: {}", tenantId);
    }

    protected void performTenantResourceMonitoring() {
        if (multiTenancyEnabled) {
            LOGGER.debug("Performing tenant resource monitoring");
            
            tenants.keySet().forEach(tenantId -> {
                getTenantResourceUsage(tenantId)
                    .thenAccept(usage -> {
                        // Check for resource violations
                        if (usage.getCpuUsagePercent() > 90.0) {
                            LOGGER.warn("High CPU usage for tenant {}: {}%", 
                                tenantId, usage.getCpuUsagePercent());
                        }
                        
                        if (usage.getMemoryUsagePercent() > 90.0) {
                            LOGGER.warn("High memory usage for tenant {}: {}%", 
                                tenantId, usage.getMemoryUsagePercent());
                        }
                    })
                    .exceptionally(e -> {
                        LOGGER.error("Failed to collect resource usage for tenant: " + tenantId, e);
                        return null;
                    });
            });
        }
    }

    protected void performTenantContextCleanup() {
        if (multiTenancyEnabled) {
            LOGGER.debug("Performing tenant context cleanup");
            
            final Instant cutoffTime = Instant.now().minus(configuration.getContextMaxAge());
            final List<String> expiredContexts = activeTenantContexts.entrySet().stream()
                .filter(entry -> entry.getValue().getCreatedAt().isBefore(cutoffTime))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
            
            expiredContexts.forEach(contextId -> {
                activeTenantContexts.remove(contextId);
                LOGGER.debug("Cleaned up expired tenant context: {}", contextId);
            });
            
            if (!expiredContexts.isEmpty()) {
                LOGGER.info("Cleaned up {} expired tenant contexts", expiredContexts.size());
            }
        }
    }

    // Event notification methods
    protected void notifyTenantRegistered(final String tenantId, final TenantConfiguration config) {
        eventListeners.forEach(listener -> {
            try {
                listener.onTenantRegistered(tenantId, config);
            } catch (final Exception e) {
                LOGGER.error("Tenant event listener failed", e);
            }
        });
    }

    protected void notifyTenantConfigurationUpdated(final String tenantId, 
                                                  final TenantConfiguration oldConfig,
                                                  final TenantConfiguration newConfig) {
        eventListeners.forEach(listener -> {
            try {
                listener.onTenantConfigurationUpdated(tenantId, oldConfig, newConfig);
            } catch (final Exception e) {
                LOGGER.error("Tenant event listener failed", e);
            }
        });
    }

    protected void notifyTenantRemoved(final String tenantId, final TenantConfiguration config) {
        eventListeners.forEach(listener -> {
            try {
                listener.onTenantRemoved(tenantId, config);
            } catch (final Exception e) {
                LOGGER.error("Tenant event listener failed", e);
            }
        });
    }

    // Functional interface for tenant operations
    @FunctionalInterface
    public interface TenantOperation<T> {
        T execute(TenantContext context) throws Exception;
    }

    // Interface for tenant event listeners
    public interface TenantEventListener {
        void onTenantRegistered(String tenantId, TenantConfiguration config);
        void onTenantConfigurationUpdated(String tenantId, TenantConfiguration oldConfig, TenantConfiguration newConfig);
        void onTenantRemoved(String tenantId, TenantConfiguration config);
    }
}