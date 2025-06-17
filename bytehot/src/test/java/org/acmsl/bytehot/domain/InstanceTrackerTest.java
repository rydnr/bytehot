/*
                        ByteHot

    Copyright (C) 2025-today  rydnr@acm-sl.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
/*
 ******************************************************************************
 *
 * Filename: InstanceTrackerTest.java
 *
 * Author: Claude Code
 *
 * Class name: InstanceTrackerTest
 *
 * Responsibilities:
 *   - Test InstanceTracker for tracking existing instances of classes
 *
 * Collaborators:
 *   - InstanceTracker: Tracks instances using weak references
 */
package org.acmsl.bytehot.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test InstanceTracker for tracking existing instances of classes
 * @author Claude Code
 * @since 2025-06-17
 */
public class InstanceTrackerTest {

    private InstanceTracker tracker;

    @BeforeEach
    public void setUp() {
        tracker = new InstanceTracker();
    }

    /**
     * Tests tracking is disabled by default
     */
    @Test
    public void tracking_disabled_by_default() {
        // When: Creating new tracker
        // Then: Tracking should be disabled by default
        assertFalse(tracker.isTrackingEnabled(), "Tracking should be disabled by default");
    }

    /**
     * Tests enabling tracking for a specific class
     */
    @Test
    public void can_enable_tracking_for_class() {
        // Given: A test class
        Class<?> testClass = String.class;
        
        // When: Enabling tracking for the class
        tracker.enableTracking(testClass);
        
        // Then: Tracking should be enabled for that class
        assertTrue(tracker.isTrackingEnabled(testClass), "Tracking should be enabled for the class");
        assertTrue(tracker.isTrackingEnabled(), "General tracking should be enabled");
    }

    /**
     * Tests disabling tracking for a specific class
     */
    @Test
    public void can_disable_tracking_for_class() {
        // Given: A test class with tracking enabled
        Class<?> testClass = String.class;
        tracker.enableTracking(testClass);
        
        // When: Disabling tracking for the class
        tracker.disableTracking(testClass);
        
        // Then: Tracking should be disabled for that class
        assertFalse(tracker.isTrackingEnabled(testClass), "Tracking should be disabled for the class");
    }

    /**
     * Tests tracking instances of a class
     */
    @Test
    public void can_track_instances_of_class() {
        // Given: A test class with tracking enabled
        Class<?> testClass = TestService.class;
        tracker.enableTracking(testClass);
        
        TestService instance1 = new TestService("service1");
        TestService instance2 = new TestService("service2");
        
        // When: Tracking instances
        tracker.trackInstance(instance1);
        tracker.trackInstance(instance2);
        
        // Then: Should be able to find tracked instances
        Set<Object> instances = tracker.findInstances(testClass);
        assertNotNull(instances, "Should return non-null set");
        assertEquals(2, instances.size(), "Should find 2 tracked instances");
        assertTrue(instances.contains(instance1), "Should contain first instance");
        assertTrue(instances.contains(instance2), "Should contain second instance");
    }

    /**
     * Tests counting instances of a class
     */
    @Test
    public void can_count_instances_of_class() {
        // Given: A test class with tracking enabled
        Class<?> testClass = TestService.class;
        tracker.enableTracking(testClass);
        
        TestService instance1 = new TestService("service1");
        TestService instance2 = new TestService("service2");
        TestService instance3 = new TestService("service3");
        
        // When: Tracking instances
        tracker.trackInstance(instance1);
        tracker.trackInstance(instance2);
        tracker.trackInstance(instance3);
        
        // Then: Should count correct number of instances
        assertEquals(3, tracker.countInstances(testClass), "Should count 3 instances");
    }

    /**
     * Tests that tracking returns empty set for untracked classes
     */
    @Test
    public void returns_empty_for_untracked_classes() {
        // Given: A class without tracking enabled
        Class<?> testClass = TestService.class;
        
        // When: Finding instances
        Set<Object> instances = tracker.findInstances(testClass);
        
        // Then: Should return empty set
        assertNotNull(instances, "Should return non-null set");
        assertEquals(0, instances.size(), "Should return empty set for untracked class");
        assertEquals(0, tracker.countInstances(testClass), "Should count 0 for untracked class");
    }

    /**
     * Tests that instances are automatically cleaned up when garbage collected
     */
    @Test
    public void instances_cleaned_up_when_garbage_collected() {
        // Given: A test class with tracking enabled
        Class<?> testClass = TestService.class;
        tracker.enableTracking(testClass);
        
        // Create instances and track them
        TestService instance1 = new TestService("service1");
        tracker.trackInstance(instance1);
        
        // Verify instance is tracked
        assertEquals(1, tracker.countInstances(testClass), "Should initially count 1 instance");
        
        // When: Instance goes out of scope and GC runs
        instance1 = null;
        System.gc(); // Suggest garbage collection
        
        // Force weak reference cleanup
        tracker.cleanupWeakReferences();
        
        // Then: Instance should be cleaned up
        assertEquals(0, tracker.countInstances(testClass), "Should count 0 after cleanup");
    }

    /**
     * Tests multiple classes can be tracked simultaneously
     */
    @Test
    public void can_track_multiple_classes_simultaneously() {
        // Given: Multiple classes
        Class<?> stringClass = String.class;
        Class<?> serviceClass = TestService.class;
        
        // When: Enabling tracking and adding instances
        tracker.enableTracking(stringClass);
        tracker.enableTracking(serviceClass);
        
        String stringInstance = "test";
        TestService serviceInstance = new TestService("service");
        
        tracker.trackInstance(stringInstance);
        tracker.trackInstance(serviceInstance);
        
        // Then: Should track instances for each class separately
        assertEquals(1, tracker.countInstances(stringClass), "Should count 1 String instance");
        assertEquals(1, tracker.countInstances(serviceClass), "Should count 1 TestService instance");
        
        Set<Object> stringInstances = tracker.findInstances(stringClass);
        Set<Object> serviceInstances = tracker.findInstances(serviceClass);
        
        assertTrue(stringInstances.contains(stringInstance), "Should contain string instance");
        assertTrue(serviceInstances.contains(serviceInstance), "Should contain service instance");
        assertFalse(stringInstances.contains(serviceInstance), "String set should not contain service");
        assertFalse(serviceInstances.contains(stringInstance), "Service set should not contain string");
    }

    /**
     * Test service class for instance tracking
     */
    private static class TestService {
        private final String name;
        
        public TestService(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
        
        @Override
        public String toString() {
            return "TestService{name='" + name + "'}";
        }
    }
}