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
 * Filename: TenantDataStructures.java
 *
 * Author: Claude Code
 *
 * Class name: TenantDataStructures
 *
 * Responsibilities:
 *   - Define data structures for multi-tenant operations and management
 *   - Provide immutable result objects for tenant operations
 *   - Support comprehensive tenant metadata and configuration management
 *   - Enable serialization for tenant persistence and audit trails
 *
 * Collaborators:
 *   - MultiTenantManager: Uses these structures for tenant operations
 *   - SecurityManager: Tenant security context and access control
 *   - ByteHotLogger: Tenant-aware logging and audit trails
 */
package org.acmsl.bytehot.infrastructure.tenant;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Objects;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collections;
import java.io.Serializable;

/**
 * Tenant configuration defining tenant settings and resource limits.
 */
class TenantConfiguration implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private final String name;
    private final String description;
    private final TenantTier tier;
    private final long maxMemoryMB;
    private final int maxCpuCores;
    private final int maxConcurrentOperations;
    private final boolean backupEnabled;
    private final boolean complianceEnabled;
    private final boolean auditEnabled;
    private final Set<String> enabledFeatures;
    private final Map<String, String> customProperties;
    private final Instant createdAt;
    private final String createdBy;
    
    public TenantConfiguration(final String name, final String description, final TenantTier tier,
                              final long maxMemoryMB, final int maxCpuCores, final int maxConcurrentOperations,
                              final boolean backupEnabled, final boolean complianceEnabled, final boolean auditEnabled,
                              final Set<String> enabledFeatures, final Map<String, String> customProperties,
                              final Instant createdAt, final String createdBy) {
        this.name = Objects.requireNonNull(name);
        this.description = Objects.requireNonNull(description);
        this.tier = Objects.requireNonNull(tier);
        this.maxMemoryMB = maxMemoryMB;
        this.maxCpuCores = maxCpuCores;
        this.maxConcurrentOperations = maxConcurrentOperations;
        this.backupEnabled = backupEnabled;
        this.complianceEnabled = complianceEnabled;
        this.auditEnabled = auditEnabled;
        this.enabledFeatures = new HashSet<>(Objects.requireNonNull(enabledFeatures));
        this.customProperties = new HashMap<>(Objects.requireNonNull(customProperties));
        this.createdAt = Objects.requireNonNull(createdAt);
        this.createdBy = Objects.requireNonNull(createdBy);
    }
    
    public static TenantConfiguration basicTenant(final String name, final String description) {
        return new TenantConfiguration(
            name, description, TenantTier.BASIC,
            512, 1, 10,
            false, false, true,
            Set.of("BASIC_HOTSWAP", "BASIC_LOGGING"),
            Map.of(),
            Instant.now(), "system"
        );
    }
    
    public static TenantConfiguration standardTenant(final String name, final String description) {
        return new TenantConfiguration(
            name, description, TenantTier.STANDARD,
            2048, 2, 50,
            true, false, true,
            Set.of("BASIC_HOTSWAP", "BASIC_LOGGING", "BACKUP_RECOVERY", "ENHANCED_MONITORING"),
            Map.of(),
            Instant.now(), "system"
        );
    }
    
    public static TenantConfiguration enterpriseTenant(final String name, final String description) {
        return new TenantConfiguration(
            name, description, TenantTier.ENTERPRISE,
            8192, 8, 200,
            true, true, true,
            Set.of("BASIC_HOTSWAP", "BASIC_LOGGING", "BACKUP_RECOVERY", "ENHANCED_MONITORING", 
                   "COMPLIANCE_REPORTING", "ENTERPRISE_SECURITY", "ADVANCED_ANALYTICS"),
            Map.of(),
            Instant.now(), "system"
        );
    }
    
    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public TenantTier getTier() { return tier; }
    public long getMaxMemoryMB() { return maxMemoryMB; }
    public int getMaxCpuCores() { return maxCpuCores; }
    public int getMaxConcurrentOperations() { return maxConcurrentOperations; }
    public boolean isBackupEnabled() { return backupEnabled; }
    public boolean isComplianceEnabled() { return complianceEnabled; }
    public boolean isAuditEnabled() { return auditEnabled; }
    public Set<String> getEnabledFeatures() { return Collections.unmodifiableSet(enabledFeatures); }
    public Map<String, String> getCustomProperties() { return Collections.unmodifiableMap(customProperties); }
    public Instant getCreatedAt() { return createdAt; }
    public String getCreatedBy() { return createdBy; }
    
    @Override
    public String toString() {
        return "TenantConfiguration{" +
               "name='" + name + '\'' +
               ", tier=" + tier +
               ", maxMemoryMB=" + maxMemoryMB +
               ", maxCpuCores=" + maxCpuCores +
               ", featuresCount=" + enabledFeatures.size() +
               '}';
    }
}

/**
 * Tenant context for thread-local tenant-aware operations.
 */
class TenantContext implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private final String contextId;
    private final String tenantId;
    private final String userId;
    private final String requestId;
    private final Instant createdAt;
    private final TenantConfiguration config;
    private final TenantResourceAllocation allocation;
    private final String threadName;
    
    public TenantContext(final String contextId, final String tenantId, final String userId,
                        final String requestId, final Instant createdAt,
                        final TenantConfiguration config, final TenantResourceAllocation allocation,
                        final String threadName) {
        this.contextId = Objects.requireNonNull(contextId);
        this.tenantId = Objects.requireNonNull(tenantId);
        this.userId = Objects.requireNonNull(userId);
        this.requestId = Objects.requireNonNull(requestId);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.config = Objects.requireNonNull(config);
        this.allocation = Objects.requireNonNull(allocation);
        this.threadName = Objects.requireNonNull(threadName);
    }
    
    // Getters
    public String getContextId() { return contextId; }
    public String getTenantId() { return tenantId; }
    public String getUserId() { return userId; }
    public String getRequestId() { return requestId; }
    public Instant getCreatedAt() { return createdAt; }
    public TenantConfiguration getConfig() { return config; }
    public TenantResourceAllocation getAllocation() { return allocation; }
    public String getThreadName() { return threadName; }
    
    @Override
    public String toString() {
        return "TenantContext{" +
               "contextId='" + contextId + '\'' +
               ", tenantId='" + tenantId + '\'' +
               ", userId='" + userId + '\'' +
               ", requestId='" + requestId + '\'' +
               ", threadName='" + threadName + '\'' +
               '}';
    }
}

/**
 * Tenant resource allocation and limits.
 */
class TenantResourceAllocation implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private final String tenantId;
    private final long allocatedMemoryMB;
    private final int allocatedCpuCores;
    private final TenantTier tier;
    private final TenantStatus status;
    private final Instant allocatedAt;
    
    public TenantResourceAllocation(final String tenantId, final long allocatedMemoryMB,
                                   final int allocatedCpuCores, final TenantTier tier,
                                   final TenantStatus status, final Instant allocatedAt) {
        this.tenantId = Objects.requireNonNull(tenantId);
        this.allocatedMemoryMB = allocatedMemoryMB;
        this.allocatedCpuCores = allocatedCpuCores;
        this.tier = Objects.requireNonNull(tier);
        this.status = Objects.requireNonNull(status);
        this.allocatedAt = Objects.requireNonNull(allocatedAt);
    }
    
    // Getters
    public String getTenantId() { return tenantId; }
    public long getAllocatedMemoryMB() { return allocatedMemoryMB; }
    public int getAllocatedCpuCores() { return allocatedCpuCores; }
    public TenantTier getTier() { return tier; }
    public TenantStatus getStatus() { return status; }
    public Instant getAllocatedAt() { return allocatedAt; }
    
    public String getSummary() {
        return String.format("Memory: %dMB, CPU: %d cores, Tier: %s", 
            allocatedMemoryMB, allocatedCpuCores, tier);
    }
    
    @Override
    public String toString() {
        return "TenantResourceAllocation{" +
               "tenantId='" + tenantId + '\'' +
               ", allocatedMemoryMB=" + allocatedMemoryMB +
               ", allocatedCpuCores=" + allocatedCpuCores +
               ", tier=" + tier +
               ", status=" + status +
               '}';
    }
}

/**
 * Tenant resource usage statistics.
 */
class TenantResourceUsage implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private final String tenantId;
    private final double cpuUsagePercent;
    private final long memoryUsageMB;
    private final long memoryLimitMB;
    private final int cpuLimitCores;
    private final long activeContexts;
    private final Instant collectedAt;
    
    public TenantResourceUsage(final String tenantId, final double cpuUsagePercent,
                              final long memoryUsageMB, final long memoryLimitMB,
                              final int cpuLimitCores, final long activeContexts,
                              final Instant collectedAt) {
        this.tenantId = Objects.requireNonNull(tenantId);
        this.cpuUsagePercent = cpuUsagePercent;
        this.memoryUsageMB = memoryUsageMB;
        this.memoryLimitMB = memoryLimitMB;
        this.cpuLimitCores = cpuLimitCores;
        this.activeContexts = activeContexts;
        this.collectedAt = Objects.requireNonNull(collectedAt);
    }
    
    // Getters
    public String getTenantId() { return tenantId; }
    public double getCpuUsagePercent() { return cpuUsagePercent; }
    public long getMemoryUsageMB() { return memoryUsageMB; }
    public long getMemoryLimitMB() { return memoryLimitMB; }
    public int getCpuLimitCores() { return cpuLimitCores; }
    public long getActiveContexts() { return activeContexts; }
    public Instant getCollectedAt() { return collectedAt; }
    
    public double getMemoryUsagePercent() {
        return memoryLimitMB > 0 ? (double) memoryUsageMB / memoryLimitMB * 100.0 : 0.0;
    }
    
    @Override
    public String toString() {
        return "TenantResourceUsage{" +
               "tenantId='" + tenantId + '\'' +
               ", cpuUsagePercent=" + String.format("%.1f", cpuUsagePercent) +
               ", memoryUsagePercent=" + String.format("%.1f", getMemoryUsagePercent()) +
               ", activeContexts=" + activeContexts +
               '}';
    }
}

/**
 * Tenant information summary.
 */
class TenantInfo implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private final String tenantId;
    private final String name;
    private final TenantTier tier;
    private final Instant createdAt;
    private final TenantStatus status;
    private final long activeContexts;
    
    public TenantInfo(final String tenantId, final String name, final TenantTier tier,
                     final Instant createdAt, final TenantStatus status, final long activeContexts) {
        this.tenantId = Objects.requireNonNull(tenantId);
        this.name = Objects.requireNonNull(name);
        this.tier = Objects.requireNonNull(tier);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.status = Objects.requireNonNull(status);
        this.activeContexts = activeContexts;
    }
    
    // Getters
    public String getTenantId() { return tenantId; }
    public String getName() { return name; }
    public TenantTier getTier() { return tier; }
    public Instant getCreatedAt() { return createdAt; }
    public TenantStatus getStatus() { return status; }
    public long getActiveContexts() { return activeContexts; }
    
    @Override
    public String toString() {
        return "TenantInfo{" +
               "tenantId='" + tenantId + '\'' +
               ", name='" + name + '\'' +
               ", tier=" + tier +
               ", status=" + status +
               ", activeContexts=" + activeContexts +
               '}';
    }
}

/**
 * Multi-tenant system configuration.
 */
class MultiTenantConfiguration implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private final boolean enabled;
    private final int maxTenants;
    private final long maxMemoryPerTenantMB;
    private final int maxCpuCoresPerTenant;
    private final boolean monitoringEnabled;
    private final Duration monitoringInterval;
    private final Duration contextMaxAge;
    private final boolean forceRemovalAllowed;
    private final Map<String, TenantConfiguration> defaultTenants;
    
    public MultiTenantConfiguration(final boolean enabled, final int maxTenants,
                                   final long maxMemoryPerTenantMB, final int maxCpuCoresPerTenant,
                                   final boolean monitoringEnabled, final Duration monitoringInterval,
                                   final Duration contextMaxAge, final boolean forceRemovalAllowed,
                                   final Map<String, TenantConfiguration> defaultTenants) {
        this.enabled = enabled;
        this.maxTenants = maxTenants;
        this.maxMemoryPerTenantMB = maxMemoryPerTenantMB;
        this.maxCpuCoresPerTenant = maxCpuCoresPerTenant;
        this.monitoringEnabled = monitoringEnabled;
        this.monitoringInterval = Objects.requireNonNull(monitoringInterval);
        this.contextMaxAge = Objects.requireNonNull(contextMaxAge);
        this.forceRemovalAllowed = forceRemovalAllowed;
        this.defaultTenants = defaultTenants != null ? new HashMap<>(defaultTenants) : null;
    }
    
    public static MultiTenantConfiguration defaultConfiguration() {
        return new MultiTenantConfiguration(
            true, 100, 4096, 4,
            true, Duration.ofMinutes(5), Duration.ofHours(24),
            false, null
        );
    }
    
    public static MultiTenantConfiguration enterpriseConfiguration() {
        return new MultiTenantConfiguration(
            true, 1000, 16384, 16,
            true, Duration.ofMinutes(1), Duration.ofHours(12),
            true, Map.of(
                "default", TenantConfiguration.basicTenant("Default Tenant", "Default system tenant"),
                "admin", TenantConfiguration.enterpriseTenant("Admin Tenant", "Administrative tenant")
            )
        );
    }
    
    // Getters
    public boolean isEnabled() { return enabled; }
    public int getMaxTenants() { return maxTenants; }
    public long getMaxMemoryPerTenantMB() { return maxMemoryPerTenantMB; }
    public int getMaxCpuCoresPerTenant() { return maxCpuCoresPerTenant; }
    public boolean isMonitoringEnabled() { return monitoringEnabled; }
    public Duration getMonitoringInterval() { return monitoringInterval; }
    public Duration getContextMaxAge() { return contextMaxAge; }
    public boolean isForceRemovalAllowed() { return forceRemovalAllowed; }
    public Map<String, TenantConfiguration> getDefaultTenants() { 
        return defaultTenants != null ? Collections.unmodifiableMap(defaultTenants) : null; 
    }
    
    @Override
    public String toString() {
        return "MultiTenantConfiguration{" +
               "enabled=" + enabled +
               ", maxTenants=" + maxTenants +
               ", maxMemoryPerTenantMB=" + maxMemoryPerTenantMB +
               ", maxCpuCoresPerTenant=" + maxCpuCoresPerTenant +
               ", monitoringEnabled=" + monitoringEnabled +
               '}';
    }
}

/**
 * Multi-tenant system statistics.
 */
class MultiTenantStatistics implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private final int totalTenants;
    private final int activeTenants;
    private final int totalContexts;
    private final Map<TenantTier, Integer> tenantsByTier;
    private final long totalMemoryAllocatedMB;
    private final int maxTenants;
    private final Instant collectedAt;
    
    public MultiTenantStatistics(final int totalTenants, final int activeTenants, final int totalContexts,
                                final Map<TenantTier, Integer> tenantsByTier, final long totalMemoryAllocatedMB,
                                final int maxTenants, final Instant collectedAt) {
        this.totalTenants = totalTenants;
        this.activeTenants = activeTenants;
        this.totalContexts = totalContexts;
        this.tenantsByTier = new HashMap<>(Objects.requireNonNull(tenantsByTier));
        this.totalMemoryAllocatedMB = totalMemoryAllocatedMB;
        this.maxTenants = maxTenants;
        this.collectedAt = Objects.requireNonNull(collectedAt);
    }
    
    // Getters
    public int getTotalTenants() { return totalTenants; }
    public int getActiveTenants() { return activeTenants; }
    public int getTotalContexts() { return totalContexts; }
    public Map<TenantTier, Integer> getTenantsByTier() { return Collections.unmodifiableMap(tenantsByTier); }
    public long getTotalMemoryAllocatedMB() { return totalMemoryAllocatedMB; }
    public int getMaxTenants() { return maxTenants; }
    public Instant getCollectedAt() { return collectedAt; }
    
    public double getTenantUtilizationPercent() {
        return maxTenants > 0 ? (double) totalTenants / maxTenants * 100.0 : 0.0;
    }
    
    @Override
    public String toString() {
        return "MultiTenantStatistics{" +
               "totalTenants=" + totalTenants +
               ", activeTenants=" + activeTenants +
               ", totalContexts=" + totalContexts +
               ", utilizationPercent=" + String.format("%.1f", getTenantUtilizationPercent()) +
               ", totalMemoryAllocatedMB=" + totalMemoryAllocatedMB +
               '}';
    }
}

// Result classes for tenant operations

/**
 * Tenant registration result.
 */
class TenantRegistrationResult {
    
    private final boolean success;
    private final String tenantId;
    private final TenantResourceAllocation allocation;
    private final List<String> errors;
    private final Throwable error;
    private final String message;
    
    private TenantRegistrationResult(final boolean success, final String tenantId,
                                    final TenantResourceAllocation allocation, final List<String> errors,
                                    final Throwable error, final String message) {
        this.success = success;
        this.tenantId = tenantId;
        this.allocation = allocation;
        this.errors = errors != null ? new ArrayList<>(errors) : null;
        this.error = error;
        this.message = message;
    }
    
    public static TenantRegistrationResult success(final String tenantId, final TenantResourceAllocation allocation) {
        return new TenantRegistrationResult(true, tenantId, allocation, null, null, "Tenant registered successfully");
    }
    
    public static TenantRegistrationResult disabled(final String tenantId) {
        return new TenantRegistrationResult(false, tenantId, null, null, null, "Multi-tenancy is disabled");
    }
    
    public static TenantRegistrationResult alreadyExists(final String tenantId) {
        return new TenantRegistrationResult(false, tenantId, null, null, null, "Tenant already exists");
    }
    
    public static TenantRegistrationResult validationFailed(final String tenantId, final List<String> errors) {
        return new TenantRegistrationResult(false, tenantId, null, errors, null, "Validation failed");
    }
    
    public static TenantRegistrationResult error(final String tenantId, final Throwable error) {
        return new TenantRegistrationResult(false, tenantId, null, null, error, "Registration failed: " + error.getMessage());
    }
    
    // Getters
    public boolean isSuccess() { return success; }
    public String getTenantId() { return tenantId; }
    public TenantResourceAllocation getAllocation() { return allocation; }
    public List<String> getErrors() { return errors != null ? Collections.unmodifiableList(errors) : null; }
    public Throwable getError() { return error; }
    public String getMessage() { return message; }
}

/**
 * Tenant validation result.
 */
class TenantValidationResult {
    
    private final boolean valid;
    private final List<String> errors;
    
    public TenantValidationResult(final boolean valid, final List<String> errors) {
        this.valid = valid;
        this.errors = new ArrayList<>(Objects.requireNonNull(errors));
    }
    
    public boolean isValid() { return valid; }
    public List<String> getErrors() { return Collections.unmodifiableList(errors); }
}

/**
 * Tenant configuration update result.
 */
class TenantConfigurationUpdateResult {
    
    private final boolean success;
    private final String tenantId;
    private final boolean resourcesReallocated;
    private final List<String> errors;
    private final Throwable error;
    private final String message;
    
    private TenantConfigurationUpdateResult(final boolean success, final String tenantId,
                                           final boolean resourcesReallocated, final List<String> errors,
                                           final Throwable error, final String message) {
        this.success = success;
        this.tenantId = tenantId;
        this.resourcesReallocated = resourcesReallocated;
        this.errors = errors != null ? new ArrayList<>(errors) : null;
        this.error = error;
        this.message = message;
    }
    
    public static TenantConfigurationUpdateResult success(final String tenantId, final boolean resourcesReallocated) {
        return new TenantConfigurationUpdateResult(true, tenantId, resourcesReallocated, null, null, 
            "Configuration updated successfully");
    }
    
    public static TenantConfigurationUpdateResult tenantNotFound(final String tenantId) {
        return new TenantConfigurationUpdateResult(false, tenantId, false, null, null, "Tenant not found");
    }
    
    public static TenantConfigurationUpdateResult validationFailed(final String tenantId, final List<String> errors) {
        return new TenantConfigurationUpdateResult(false, tenantId, false, errors, null, "Validation failed");
    }
    
    public static TenantConfigurationUpdateResult error(final String tenantId, final Throwable error) {
        return new TenantConfigurationUpdateResult(false, tenantId, false, null, error, 
            "Update failed: " + error.getMessage());
    }
    
    // Getters
    public boolean isSuccess() { return success; }
    public String getTenantId() { return tenantId; }
    public boolean areResourcesReallocated() { return resourcesReallocated; }
    public List<String> getErrors() { return errors != null ? Collections.unmodifiableList(errors) : null; }
    public Throwable getError() { return error; }
    public String getMessage() { return message; }
}

/**
 * Tenant removal result.
 */
class TenantRemovalResult {
    
    private final boolean success;
    private final String tenantId;
    private final int activeContexts;
    private final Throwable error;
    private final String message;
    
    private TenantRemovalResult(final boolean success, final String tenantId, final int activeContexts,
                               final Throwable error, final String message) {
        this.success = success;
        this.tenantId = tenantId;
        this.activeContexts = activeContexts;
        this.error = error;
        this.message = message;
    }
    
    public static TenantRemovalResult success(final String tenantId) {
        return new TenantRemovalResult(true, tenantId, 0, null, "Tenant removed successfully");
    }
    
    public static TenantRemovalResult tenantNotFound(final String tenantId) {
        return new TenantRemovalResult(false, tenantId, 0, null, "Tenant not found");
    }
    
    public static TenantRemovalResult hasActiveContexts(final String tenantId, final int activeContexts) {
        return new TenantRemovalResult(false, tenantId, activeContexts, null, 
            "Cannot remove tenant with active contexts");
    }
    
    public static TenantRemovalResult error(final String tenantId, final Throwable error) {
        return new TenantRemovalResult(false, tenantId, 0, error, "Removal failed: " + error.getMessage());
    }
    
    // Getters
    public boolean isSuccess() { return success; }
    public String getTenantId() { return tenantId; }
    public int getActiveContexts() { return activeContexts; }
    public Throwable getError() { return error; }
    public String getMessage() { return message; }
}

// Enums for tenant operations

/**
 * Tenant tier levels.
 */
enum TenantTier {
    BASIC("Basic tier with essential features"),
    STANDARD("Standard tier with enhanced features"),
    PREMIUM("Premium tier with advanced features"),
    ENTERPRISE("Enterprise tier with all features");
    
    private final String description;
    
    TenantTier(final String description) {
        this.description = description;
    }
    
    public String getDescription() { return description; }
}

/**
 * Tenant status levels.
 */
enum TenantStatus {
    PENDING("Tenant registration pending"),
    ACTIVE("Tenant is active and operational"),
    SUSPENDED("Tenant is temporarily suspended"),
    INACTIVE("Tenant is inactive"),
    UNKNOWN("Tenant status unknown");
    
    private final String description;
    
    TenantStatus(final String description) {
        this.description = description;
    }
    
    public String getDescription() { return description; }
}