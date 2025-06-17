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
 * Filename: InstanceTracker.java
 *
 * Author: Claude Code
 *
 * Class name: InstanceTracker
 *
 * Responsibilities:
 *   - Track existing instances of classes using weak references
 *   - Enable/disable tracking for specific classes
 *   - Provide instance discovery and counting capabilities
 *
 * Collaborators:
 *   - WeakReference: For tracking instances without preventing garbage collection
 *   - ConcurrentHashMap: For thread-safe tracking
 */
package org.acmsl.bytehot.domain;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks existing instances of classes using weak references
 * @author Claude Code
 * @since 2025-06-17
 */
public class InstanceTracker {

    /**
     * Registry of tracked instances per class using weak references
     */
    private final ConcurrentHashMap<Class<?>, List<WeakReference<Object>>> instanceRegistry;

    /**
     * Set of classes that have tracking enabled
     */
    private final Set<Class<?>> trackedClasses;

    /**
     * Creates a new instance tracker
     */
    public InstanceTracker() {
        this.instanceRegistry = new ConcurrentHashMap<>();
        this.trackedClasses = Collections.synchronizedSet(new HashSet<>());
    }

    /**
     * Checks if tracking is enabled globally (any class is being tracked)
     * @return true if any class has tracking enabled
     */
    public boolean isTrackingEnabled() {
        return !trackedClasses.isEmpty();
    }

    /**
     * Checks if tracking is enabled for a specific class
     * @param clazz the class to check
     * @return true if tracking is enabled for the class
     */
    public boolean isTrackingEnabled(final Class<?> clazz) {
        return trackedClasses.contains(clazz);
    }

    /**
     * Enables tracking for a specific class
     * @param clazz the class to enable tracking for
     */
    public void enableTracking(final Class<?> clazz) {
        trackedClasses.add(clazz);
        instanceRegistry.putIfAbsent(clazz, Collections.synchronizedList(new ArrayList<>()));
    }

    /**
     * Disables tracking for a specific class
     * @param clazz the class to disable tracking for
     */
    public void disableTracking(final Class<?> clazz) {
        trackedClasses.remove(clazz);
        instanceRegistry.remove(clazz);
    }

    /**
     * Tracks an instance of a class
     * @param instance the instance to track
     */
    public void track(final Object instance) {
        trackInstance(instance);
    }

    /**
     * Tracks an instance of a class
     * @param instance the instance to track
     */
    public void trackInstance(final Object instance) {
        if (instance == null) {
            return;
        }
        
        final Class<?> clazz = instance.getClass();
        if (isTrackingEnabled(clazz)) {
            final List<WeakReference<Object>> instances = instanceRegistry.get(clazz);
            if (instances != null) {
                instances.add(new WeakReference<>(instance));
            }
        }
    }

    /**
     * Finds all tracked instances of a specific class
     * @param clazz the class to find instances for
     * @return set of tracked instances (may be empty but never null)
     */
    public Set<Object> findInstances(final Class<?> clazz) {
        if (!isTrackingEnabled(clazz)) {
            return Collections.emptySet();
        }
        
        final List<WeakReference<Object>> weakRefs = instanceRegistry.get(clazz);
        if (weakRefs == null) {
            return Collections.emptySet();
        }
        
        final Set<Object> instances = new HashSet<>();
        cleanupWeakReferences(weakRefs, instances);
        
        return instances;
    }

    /**
     * Counts tracked instances of a specific class
     * @param clazz the class to count instances for
     * @return number of tracked instances
     */
    public int countInstances(final Class<?> clazz) {
        return findInstances(clazz).size();
    }

    /**
     * Gets the number of tracked instances for a specific class name
     * @param className the fully qualified class name
     * @return number of tracked instances
     */
    public int getInstanceCount(final String className) {
        try {
            final Class<?> clazz = Class.forName(className);
            return countInstances(clazz);
        } catch (ClassNotFoundException e) {
            return 0;
        }
    }

    /**
     * Cleans up weak references that have been garbage collected
     */
    public void cleanupWeakReferences() {
        for (final List<WeakReference<Object>> weakRefs : instanceRegistry.values()) {
            cleanupWeakReferences(weakRefs, null);
        }
    }

    /**
     * Cleans up weak references and optionally collects live instances
     * @param weakRefs the list of weak references to clean
     * @param liveInstances optional set to collect live instances into
     */
    protected void cleanupWeakReferences(final List<WeakReference<Object>> weakRefs, final Set<Object> liveInstances) {
        synchronized (weakRefs) {
            final List<WeakReference<Object>> toRemove = new ArrayList<>();
            
            for (final WeakReference<Object> weakRef : weakRefs) {
                final Object instance = weakRef.get();
                if (instance == null) {
                    // Instance has been garbage collected
                    toRemove.add(weakRef);
                } else if (liveInstances != null) {
                    // Collect live instance
                    liveInstances.add(instance);
                }
            }
            
            // Remove dead references
            weakRefs.removeAll(toRemove);
        }
    }
}