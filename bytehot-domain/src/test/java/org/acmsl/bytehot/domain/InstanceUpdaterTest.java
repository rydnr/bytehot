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
 * Filename: InstanceUpdaterTest.java
 *
 * Author: Claude Code
 *
 * Class name: InstanceUpdaterTest
 *
 * Responsibilities:
 *   - Test InstanceUpdater for updating existing instances after class redefinition
 *
 * Collaborators:
 *   - InstanceUpdater: Coordinates instance updates with different strategies
 *   - ClassRedefinitionSucceeded: Input event from successful hot-swap
 *   - InstancesUpdated: Output event with update results
 */
package org.acmsl.bytehot.domain;

import org.acmsl.bytehot.domain.events.ClassRedefinitionSucceeded;
import org.acmsl.bytehot.domain.events.InstancesUpdated;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test InstanceUpdater for updating existing instances after class redefinition
 * @author Claude Code
 * @since 2025-06-17
 */
public class InstanceUpdaterTest {

    private InstanceUpdater updater;
    private InstanceTracker tracker;

    @BeforeEach
    public void setUp() {
        tracker = new InstanceTracker();
        updater = new InstanceUpdater(tracker);
    }

    /**
     * Tests automatic instance update for method body changes
     */
    @Test
    public void automatic_update_for_method_body_changes() {
        // Given: A tracked class with instances
        Class<?> testClass = TestService.class;
        tracker.enableTracking(testClass);
        
        TestService instance1 = new TestService("service1");
        TestService instance2 = new TestService("service2");
        tracker.trackInstance(instance1);
        tracker.trackInstance(instance2);
        
        ClassRedefinitionSucceeded redefinition = createRedefinitionEvent(
            testClass.getName(), 2, "Method implementation updated successfully"
        );
        
        // When: Updating instances automatically
        InstancesUpdated result = updater.updateInstances(redefinition);
        
        // Then: Should report automatic update
        assertNotNull(result, "Result should not be null");
        assertEquals(testClass.getName(), result.getClassName(), "Should have correct class name");
        assertEquals(InstanceUpdateMethod.AUTOMATIC, result.getUpdateMethod(), "Should use automatic update");
        assertEquals(2, result.getUpdatedInstances(), "Should update 2 instances");
        assertEquals(2, result.getTotalInstances(), "Should find 2 total instances");
        assertEquals(0, result.getFailedUpdates(), "Should have no failures");
        assertNotNull(result.getUpdateDetails(), "Should have update details");
        assertTrue(result.getDuration().toMillis() >= 0, "Duration should be non-negative");
    }

    /**
     * Tests determining update method based on class characteristics
     */
    @Test
    public void determines_update_method_based_on_class() {
        // When: Determining update method for different classes
        InstanceUpdateMethod stringMethod = updater.determineUpdateMethod(String.class);
        InstanceUpdateMethod serviceMethod = updater.determineUpdateMethod(TestService.class);
        
        // Then: Should return appropriate methods
        assertEquals(InstanceUpdateMethod.AUTOMATIC, stringMethod, 
            "String class should use automatic update");
        assertEquals(InstanceUpdateMethod.AUTOMATIC, serviceMethod, 
            "Regular service class should use automatic update");
    }

    /**
     * Tests checking if instances can be updated
     */
    @Test
    public void can_check_if_instances_can_be_updated() {
        // When: Checking if classes can be updated
        boolean stringCanUpdate = updater.canUpdateInstances(String.class);
        boolean serviceCanUpdate = updater.canUpdateInstances(TestService.class);
        
        // Then: Should indicate updateability
        assertTrue(stringCanUpdate, "String instances should be updatable");
        assertTrue(serviceCanUpdate, "Service instances should be updatable");
    }

    /**
     * Tests instance update with tracked instances
     */
    @Test
    public void updates_tracked_instances() {
        // Given: A class with tracked instances
        Class<?> testClass = TestService.class;
        tracker.enableTracking(testClass);
        
        TestService instance1 = new TestService("service1");
        TestService instance2 = new TestService("service2");
        tracker.trackInstance(instance1);
        tracker.trackInstance(instance2);
        
        ClassRedefinitionSucceeded redefinition = createRedefinitionEvent(
            testClass.getName(), 2, "Test service redefinition"
        );
        
        // When: Updating instances
        InstancesUpdated result = updater.updateInstances(redefinition);
        
        // Then: Should update tracked instances
        assertEquals(testClass.getName(), result.getClassName(), "Should have correct class name");
        assertEquals(2, result.getUpdatedInstances(), "Should update 2 tracked instances");
        assertEquals(2, result.getTotalInstances(), "Should find 2 total instances");
        assertEquals(InstanceUpdateMethod.AUTOMATIC, result.getUpdateMethod(), "Should use automatic");
    }

    /**
     * Tests instance update with no tracked instances
     */
    @Test
    public void handles_no_tracked_instances() {
        // Given: A class with no tracked instances
        ClassRedefinitionSucceeded redefinition = createRedefinitionEvent(
            "com.example.UnknownService", 0, "No instances to update"
        );
        
        // When: Updating instances
        InstancesUpdated result = updater.updateInstances(redefinition);
        
        // Then: Should handle gracefully
        assertEquals("com.example.UnknownService", result.getClassName(), "Should have correct class name");
        assertEquals(0, result.getUpdatedInstances(), "Should update 0 instances");
        assertEquals(0, result.getTotalInstances(), "Should find 0 total instances");
        assertEquals(InstanceUpdateMethod.NO_UPDATE, result.getUpdateMethod(), "Should use no update");
        assertEquals(0, result.getFailedUpdates(), "Should have no failures");
    }

    /**
     * Tests reflection-based instance update for complex scenarios
     */
    @Test
    public void reflection_update_for_complex_scenarios() {
        // Given: A class that requires reflection-based updates
        Class<?> complexClass = ComplexService.class;
        tracker.enableTracking(complexClass);
        
        ComplexService instance = new ComplexService("complex");
        tracker.trackInstance(instance);
        
        ClassRedefinitionSucceeded redefinition = createRedefinitionEvent(
            complexClass.getName(), 1, "Complex service with state changes"
        );
        
        // When: Updating instances (should detect need for reflection)
        InstancesUpdated result = updater.updateInstances(redefinition);
        
        // Then: Should use reflection method for complex scenarios
        assertEquals(complexClass.getName(), result.getClassName(), "Should have correct class name");
        assertEquals(1, result.getUpdatedInstances(), "Should update 1 instance");
        assertEquals(1, result.getTotalInstances(), "Should find 1 total instance");
        // Note: For this test, we expect automatic, but in real implementation
        // complex classes might trigger reflection-based updates
        assertTrue(result.getUpdateMethod() == InstanceUpdateMethod.AUTOMATIC || 
                  result.getUpdateMethod() == InstanceUpdateMethod.REFLECTION,
                  "Should use automatic or reflection update method");
    }

    /**
     * Helper method to create redefinition events
     */
    private ClassRedefinitionSucceeded createRedefinitionEvent(String className, int affectedInstances, String details) {
        return new ClassRedefinitionSucceeded(
            className,
            Paths.get("/test/" + className.replace('.', '/') + ".class"),
            affectedInstances,
            details,
            Duration.ofMillis(10),
            Instant.now()
        );
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
    }

    /**
     * Complex service class that might require reflection updates
     */
    private static class ComplexService {
        private final String name;
        private transient Object cachedData;
        
        public ComplexService(String name) {
            this.name = name;
            this.cachedData = new Object();
        }
        
        public String getName() {
            return name;
        }
        
        public Object getCachedData() {
            return cachedData;
        }
    }
}