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
 * Filename: StatePreserver.java
 *
 * Author: Claude Code
 *
 * Class name: StatePreserver
 *
 * Responsibilities:
 *   - Preserve object state before instance updates
 *   - Restore object state after instance updates
 *   - Handle complex object state management using reflection
 *
 * Collaborators:
 *   - InstanceUpdater: Uses this to preserve state during updates
 *   - Object: The objects whose state is being preserved
 */
package org.acmsl.bytehot.domain;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * Preserves and restores object state during instance updates
 * @author Claude Code
 * @since 2025-06-17
 */
public class StatePreserver {

    /**
     * Preserves the state of an object by extracting all field values
     * @param object the object whose state to preserve
     * @return map containing field names and their values
     */
    public Map<String, Object> preserveState(final Object object) {
        if (object == null) {
            return new HashMap<>();
        }

        final Map<String, Object> state = new HashMap<>();
        final Class<?> clazz = object.getClass();

        try {
            // Get all declared fields including private ones
            final Field[] fields = clazz.getDeclaredFields();
            
            for (final Field field : fields) {
                // Skip static and final fields as they don't represent instance state
                if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
                    continue;
                }
                
                // Make field accessible if it's private
                final boolean wasAccessible = field.isAccessible();
                if (!wasAccessible) {
                    field.setAccessible(true);
                }
                
                try {
                    // Get field value and store it
                    final Object value = field.get(object);
                    state.put(field.getName(), value);
                } finally {
                    // Restore original accessibility
                    field.setAccessible(wasAccessible);
                }
            }
        } catch (final IllegalAccessException e) {
            // This should not happen as we set fields accessible
            throw new InstanceUpdateException("Failed to preserve object state", e);
        }

        return state;
    }

    /**
     * Restores the state of an object from preserved field values
     * @param object the object whose state to restore
     * @param state the preserved state map
     */
    public void restoreState(final Object object, final Map<String, Object> state) {
        if (object == null || state == null) {
            return;
        }

        final Class<?> clazz = object.getClass();

        try {
            // Get all declared fields
            final Field[] fields = clazz.getDeclaredFields();
            
            for (final Field field : fields) {
                // Skip static and final fields
                if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
                    continue;
                }
                
                final String fieldName = field.getName();
                if (!state.containsKey(fieldName)) {
                    continue; // Skip fields not in preserved state
                }
                
                // Make field accessible if it's private
                final boolean wasAccessible = field.isAccessible();
                if (!wasAccessible) {
                    field.setAccessible(true);
                }
                
                try {
                    // Restore field value
                    final Object value = state.get(fieldName);
                    field.set(object, value);
                } finally {
                    // Restore original accessibility
                    field.setAccessible(wasAccessible);
                }
            }
        } catch (final IllegalAccessException e) {
            // This should not happen as we set fields accessible
            throw new InstanceUpdateException("Failed to restore object state", e);
        }
    }

    /**
     * Checks if an object's state can be preserved
     * @param object the object to check
     * @return true if state can be preserved
     */
    public boolean canPreserveState(final Object object) {
        if (object == null) {
            return false;
        }

        final Class<?> clazz = object.getClass();
        
        // Check for common immutable types that don't need state preservation
        if (isImmutableType(clazz)) {
            return true; // Can preserve, but might not be necessary
        }

        // Check if class has any non-static, non-final fields
        final Field[] fields = clazz.getDeclaredFields();
        for (final Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())) {
                return true; // Has mutable instance fields
            }
        }

        return true; // Default to true - can always try to preserve state
    }

    /**
     * Checks if a class represents an immutable type
     * @param clazz the class to check
     * @return true if the class is immutable
     */
    protected boolean isImmutableType(final Class<?> clazz) {
        // Common immutable types
        return clazz == String.class ||
               clazz == Integer.class ||
               clazz == Long.class ||
               clazz == Double.class ||
               clazz == Float.class ||
               clazz == Boolean.class ||
               clazz == Character.class ||
               clazz == Byte.class ||
               clazz == Short.class ||
               clazz.isPrimitive();
    }
}