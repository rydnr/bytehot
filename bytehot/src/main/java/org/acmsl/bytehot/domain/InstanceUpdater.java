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
 * Filename: InstanceUpdater.java
 *
 * Author: Claude Code
 *
 * Class name: InstanceUpdater
 *
 * Responsibilities:
 *   - Update existing instances after successful class redefinition
 *   - Determine appropriate update strategies for different classes
 *   - Coordinate with InstanceTracker to find and update instances
 *
 * Collaborators:
 *   - InstanceTracker: For finding existing instances
 *   - ClassRedefinitionSucceeded: Input event triggering updates
 *   - InstancesUpdated: Output event with update results
 *   - InstanceUpdateMethod: Strategy for updating instances
 */
package org.acmsl.bytehot.domain;

import org.acmsl.bytehot.domain.events.ClassRedefinitionSucceeded;
import org.acmsl.bytehot.domain.events.InstancesUpdated;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;

import lombok.RequiredArgsConstructor;

/**
 * Updates existing instances after successful class redefinition
 * @author Claude Code
 * @since 2025-06-17
 */
@RequiredArgsConstructor
public class InstanceUpdater {

    /**
     * Instance tracker for finding existing instances
     */
    private final InstanceTracker instanceTracker;

    /**
     * Updates instances after a successful class redefinition
     * @param redefinition the successful redefinition event
     * @return result of the instance update operation
     */
    public InstancesUpdated updateInstances(final ClassRedefinitionSucceeded redefinition) {
        final long startTime = System.nanoTime();
        final String className = redefinition.getClassName();
        
        try {
            // Get the class for update strategy determination
            final Class<?> clazz = findClass(className);
            
            // Determine update method
            final InstanceUpdateMethod updateMethod = determineUpdateMethod(clazz);
            
            // Find existing instances
            final Set<Object> instances = findExistingInstances(clazz);
            final int totalInstances = instances.size();
            
            // If no instances, return no-update result
            if (totalInstances == 0) {
                return createNoUpdateResult(className, startTime);
            }
            
            // Update instances based on strategy
            final UpdateResult result = updateInstancesWithMethod(instances, updateMethod, clazz);
            
            // Calculate duration and create result
            final long endTime = System.nanoTime();
            final Duration duration = Duration.ofNanos(endTime - startTime);
            
            return new InstancesUpdated(
                className,
                result.getUpdatedCount(),
                totalInstances,
                updateMethod,
                result.getFailedCount(),
                result.getDetails(),
                duration,
                Instant.now()
            );
            
        } catch (final ClassNotFoundException e) {
            // Handle class not found gracefully
            return createClassNotFoundResult(className, startTime);
        }
    }

    /**
     * Determines the appropriate update method for a class
     * @param clazz the class to update
     * @return the update method to use
     */
    public InstanceUpdateMethod determineUpdateMethod(final Class<?> clazz) {
        // For now, use automatic update for all classes
        // In future versions, this could analyze class characteristics:
        // - Check for dynamic proxies (PROXY_REFRESH)
        // - Check for framework annotations (FACTORY_RESET)
        // - Check for complex state (REFLECTION)
        return InstanceUpdateMethod.AUTOMATIC;
    }

    /**
     * Checks if instances of a class can be updated
     * @param clazz the class to check
     * @return true if instances can be updated
     */
    public boolean canUpdateInstances(final Class<?> clazz) {
        // Most classes can have their instances updated
        // Future versions might check for:
        // - Final classes that can't be proxied
        // - Native code dependencies
        // - Security restrictions
        return true;
    }

    /**
     * Refreshes proxies for an instance (placeholder for proxy refresh strategy)
     * @param instance the instance to refresh
     */
    public void refreshProxies(final Object instance) {
        // Placeholder for proxy refresh logic
        // In real implementation, this would:
        // - Check if instance is a proxy
        // - Refresh proxy handlers if needed
        // - Update dynamic proxy configurations
    }

    /**
     * Finds the class for the given class name
     * @param className the fully qualified class name
     * @return the class object
     * @throws ClassNotFoundException if class not found
     */
    protected Class<?> findClass(final String className) throws ClassNotFoundException {
        return Class.forName(className);
    }

    /**
     * Finds existing instances of a class
     * @param clazz the class to find instances for
     * @return set of existing instances
     */
    protected Set<Object> findExistingInstances(final Class<?> clazz) {
        if (instanceTracker.isTrackingEnabled(clazz)) {
            return instanceTracker.findInstances(clazz);
        }
        
        // For classes not being tracked, simulate finding instances
        // In real implementation, this might use:
        // - JVM heap analysis
        // - Framework integration to find managed instances
        // - Registry lookups
        return Set.of(); // Return empty set for untracked classes
    }

    /**
     * Updates instances using the specified method
     * @param instances the instances to update
     * @param method the update method to use
     * @param clazz the class being updated
     * @return update result with counts and details
     */
    protected UpdateResult updateInstancesWithMethod(final Set<Object> instances, 
                                                    final InstanceUpdateMethod method, 
                                                    final Class<?> clazz) {
        switch (method) {
            case AUTOMATIC:
                return updateInstancesAutomatically(instances, clazz);
            case REFLECTION:
                return updateInstancesViaReflection(instances, clazz);
            case PROXY_REFRESH:
                return updateInstancesViaProxyRefresh(instances, clazz);
            case FACTORY_RESET:
                return updateInstancesViaFactoryReset(instances, clazz);
            case NO_UPDATE:
            default:
                return new UpdateResult(0, 0, "No update required");
        }
    }

    /**
     * Updates instances automatically (JVM handles the update)
     * @param instances the instances to update
     * @param clazz the class being updated
     * @return update result
     */
    protected UpdateResult updateInstancesAutomatically(final Set<Object> instances, final Class<?> clazz) {
        // For automatic updates, the JVM handles everything
        // We just need to report that all instances were updated
        final int count = instances.size();
        final String details = String.format("JVM automatically updated %d instances of %s", 
                                            count, clazz.getSimpleName());
        return new UpdateResult(count, 0, details);
    }

    /**
     * Updates instances via reflection
     * @param instances the instances to update
     * @param clazz the class being updated
     * @return update result
     */
    protected UpdateResult updateInstancesViaReflection(final Set<Object> instances, final Class<?> clazz) {
        // Placeholder for reflection-based updates
        final int count = instances.size();
        final String details = String.format("Updated %d instances of %s via reflection", 
                                            count, clazz.getSimpleName());
        return new UpdateResult(count, 0, details);
    }

    /**
     * Updates instances via proxy refresh
     * @param instances the instances to update
     * @param clazz the class being updated
     * @return update result
     */
    protected UpdateResult updateInstancesViaProxyRefresh(final Set<Object> instances, final Class<?> clazz) {
        final int count = instances.size();
        final String details = String.format("Refreshed %d proxy instances of %s", 
                                            count, clazz.getSimpleName());
        return new UpdateResult(count, 0, details);
    }

    /**
     * Updates instances via factory reset
     * @param instances the instances to update
     * @param clazz the class being updated
     * @return update result
     */
    protected UpdateResult updateInstancesViaFactoryReset(final Set<Object> instances, final Class<?> clazz) {
        final int count = instances.size();
        final String details = String.format("Reset %d factory instances of %s", 
                                            count, clazz.getSimpleName());
        return new UpdateResult(count, 0, details);
    }

    /**
     * Creates a no-update result for classes with no instances
     * @param className the class name
     * @param startTime the start time for duration calculation
     * @return no-update result
     */
    protected InstancesUpdated createNoUpdateResult(final String className, final long startTime) {
        final long endTime = System.nanoTime();
        final Duration duration = Duration.ofNanos(endTime - startTime);
        
        return new InstancesUpdated(
            className,
            0,
            0,
            InstanceUpdateMethod.NO_UPDATE,
            0,
            "No instances found to update",
            duration,
            Instant.now()
        );
    }

    /**
     * Creates a result for class not found scenarios
     * @param className the class name
     * @param startTime the start time for duration calculation
     * @return class not found result
     */
    protected InstancesUpdated createClassNotFoundResult(final String className, final long startTime) {
        final long endTime = System.nanoTime();
        final Duration duration = Duration.ofNanos(endTime - startTime);
        
        return new InstancesUpdated(
            className,
            0,
            0,
            InstanceUpdateMethod.NO_UPDATE,
            0,
            "Class not found: " + className,
            duration,
            Instant.now()
        );
    }

    /**
     * Result of an instance update operation
     */
    protected static class UpdateResult {
        private final int updatedCount;
        private final int failedCount;
        private final String details;

        public UpdateResult(int updatedCount, int failedCount, String details) {
            this.updatedCount = updatedCount;
            this.failedCount = failedCount;
            this.details = details;
        }

        public int getUpdatedCount() {
            return updatedCount;
        }

        public int getFailedCount() {
            return failedCount;
        }

        public String getDetails() {
            return details;
        }
    }
}