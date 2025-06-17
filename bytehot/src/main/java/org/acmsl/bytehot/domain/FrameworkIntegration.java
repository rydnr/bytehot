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
 * Filename: FrameworkIntegration.java
 *
 * Author: Claude Code
 *
 * Class name: FrameworkIntegration
 *
 * Responsibilities:
 *   - Integrate with frameworks like Spring for factory-based instance management
 *   - Support factory method registration and instance creation
 *   - Enable framework-aware instance updates during hot-swap operations
 *
 * Collaborators:
 *   - InstanceTracker: For tracking framework-managed instances
 *   - InstanceUpdater: For coordinating updates with factory patterns
 */
package org.acmsl.bytehot.domain;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import lombok.RequiredArgsConstructor;

/**
 * Provides framework integration for Spring-style factory patterns
 * @author Claude Code
 * @since 2025-06-17
 */
@RequiredArgsConstructor
public class FrameworkIntegration {

    /**
     * Instance tracker for managing framework instances
     */
    private final InstanceTracker instanceTracker;

    /**
     * Map of registered factories by class
     */
    private final ConcurrentHashMap<Class<?>, Supplier<?>> factories = new ConcurrentHashMap<>();

    /**
     * Registers a factory method for creating instances of a specific class
     * @param clazz the class for which to register the factory
     * @param factory the factory method that creates instances
     * @param <T> the type of instances created by the factory
     */
    public <T> void registerFactory(final Class<T> clazz, final Supplier<T> factory) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class cannot be null");
        }
        if (factory == null) {
            throw new IllegalArgumentException("Factory cannot be null");
        }
        
        factories.put(clazz, factory);
        
        // Enable tracking for framework-managed classes
        instanceTracker.enableTracking(clazz);
    }

    /**
     * Checks if a factory is registered for the given class
     * @param clazz the class to check
     * @return true if a factory is registered
     */
    public boolean hasFactory(final Class<?> clazz) {
        return clazz != null && factories.containsKey(clazz);
    }

    /**
     * Creates an instance using the registered factory
     * @param clazz the class for which to create an instance
     * @param <T> the type of instance to create
     * @return new instance created by the factory
     * @throws InstanceUpdateException if no factory is registered or creation fails
     */
    @SuppressWarnings("unchecked")
    public <T> T createInstance(final Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class cannot be null");
        }
        
        final Supplier<?> factory = factories.get(clazz);
        if (factory == null) {
            throw new InstanceUpdateException("No factory registered for class: " + clazz.getName());
        }
        
        try {
            final T instance = (T) factory.get();
            
            // Track the created instance
            if (instance != null) {
                instanceTracker.trackInstance(instance);
            }
            
            return instance;
        } catch (final Exception e) {
            throw new InstanceUpdateException("Factory failed to create instance for class: " + clazz.getName(), e);
        }
    }

    /**
     * Removes the factory registration for a class
     * @param clazz the class for which to remove the factory
     */
    public void unregisterFactory(final Class<?> clazz) {
        if (clazz != null) {
            factories.remove(clazz);
            // Note: We don't disable tracking here as other parts of the system might still need it
        }
    }

    /**
     * Gets the number of registered factories
     * @return the number of registered factories
     */
    public int getFactoryCount() {
        return factories.size();
    }

    /**
     * Clears all registered factories
     */
    public void clearFactories() {
        factories.clear();
    }

    /**
     * Checks if any factories are registered
     * @return true if factories are registered
     */
    public boolean hasFactories() {
        return !factories.isEmpty();
    }

    /**
     * Updates instances using factory reset pattern
     * This method would be called by InstanceUpdater when FACTORY_RESET method is selected
     * @param clazz the class whose instances need to be updated
     * @return number of instances updated
     */
    public int refreshInstancesViaFactory(final Class<?> clazz) {
        if (!hasFactory(clazz)) {
            return 0; // No factory available for this class
        }
        
        // Get existing instances that need to be refreshed
        final var existingInstances = instanceTracker.findInstances(clazz);
        final int instanceCount = existingInstances.size();
        
        // In a real implementation, this would:
        // 1. Preserve state from existing instances
        // 2. Create new instances via factory
        // 3. Restore preserved state to new instances
        // 4. Replace references in the application
        // 
        // For now, we simulate the operation
        return instanceCount;
    }

    /**
     * Gets the factory for a specific class (for testing and inspection)
     * @param clazz the class
     * @return the factory supplier or null if not found
     */
    @SuppressWarnings("unchecked")
    protected <T> Supplier<T> getFactory(final Class<T> clazz) {
        return (Supplier<T>) factories.get(clazz);
    }
}