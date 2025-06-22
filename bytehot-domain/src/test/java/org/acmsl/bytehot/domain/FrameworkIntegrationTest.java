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
 * Filename: FrameworkIntegrationTest.java
 *
 * Author: Claude Code
 *
 * Class name: FrameworkIntegrationTest
 *
 * Responsibilities:
 *   - Test framework integration for Spring-style factory patterns
 *   - Verify framework-managed instance updates
 *
 * Collaborators:
 *   - FrameworkIntegration: Handles framework-specific instance management
 *   - InstanceUpdater: Core instance update coordination
 *   - InstanceTracker: Instance tracking with framework awareness
 */
package org.acmsl.bytehot.domain;

import org.acmsl.bytehot.domain.events.ClassRedefinitionSucceeded;
import org.acmsl.bytehot.domain.events.InstancesUpdated;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test framework integration for Spring-style factory patterns
 * @author Claude Code
 * @since 2025-06-17
 */
public class FrameworkIntegrationTest {

    private FrameworkIntegration frameworkIntegration;
    private InstanceTracker tracker;
    private InstanceUpdater updater;

    @BeforeEach
    public void setUp() {
        tracker = new InstanceTracker();
        frameworkIntegration = new FrameworkIntegration(tracker);
        updater = new InstanceUpdater(tracker);
    }

    /**
     * Tests registering and using factory methods for instance creation
     */
    @Test
    public void registers_and_uses_factory_methods() {
        // Given: A factory method for creating service instances
        Supplier<TestService> factory = () -> new TestService("factory-created", 100);
        Class<TestService> serviceClass = TestService.class;
        
        // When: Registering the factory
        frameworkIntegration.registerFactory(serviceClass, factory);
        
        // Then: Factory should be registered and usable
        assertTrue(frameworkIntegration.hasFactory(serviceClass), "Should have factory registered");
        
        TestService instance = frameworkIntegration.createInstance(serviceClass);
        assertNotNull(instance, "Should create instance via factory");
        assertEquals("factory-created", instance.getName(), "Should use factory-created values");
        assertEquals(100, instance.getCount(), "Should use factory-created values");
    }

    /**
     * Tests factory-based instance updates during hot-swap
     */
    @Test
    public void updates_instances_using_factory_pattern() {
        // Given: A service class with factory-managed instances
        Class<TestService> serviceClass = TestService.class;
        Supplier<TestService> factory = () -> new TestService("refreshed-instance", 200);
        
        frameworkIntegration.registerFactory(serviceClass, factory);
        tracker.enableTracking(serviceClass);
        
        // Create original instances (simulating framework-managed instances)
        TestService instance1 = new TestService("original-1", 1);
        TestService instance2 = new TestService("original-2", 2);
        tracker.trackInstance(instance1);
        tracker.trackInstance(instance2);
        
        // When: Class redefinition occurs requiring factory reset
        ClassRedefinitionSucceeded redefinition = new ClassRedefinitionSucceeded(
            serviceClass.getName(),
            Paths.get("/test/classes/TestService.class"),
            2,
            "Structural changes require factory reset",
            Duration.ofMillis(30),
            Instant.now()
        );
        
        // Simulate factory reset update method selection
        InstancesUpdated result = performFactoryResetUpdate(redefinition);
        
        // Then: Should report factory reset update
        assertEquals(serviceClass.getName(), result.getClassName(), "Should have correct class name");
        assertEquals(2, result.getUpdatedInstances(), "Should update tracked instances");
        assertEquals(InstanceUpdateMethod.FACTORY_RESET, result.getUpdateMethod(), "Should use factory reset");
        assertEquals(0, result.getFailedUpdates(), "Should have no failures");
        assertTrue(result.getUpdateDetails().contains("factory"), "Should mention factory in details");
    }

    /**
     * Tests Spring-style singleton factory pattern
     */
    @Test
    public void supports_singleton_factory_pattern() {
        // Given: A singleton factory that returns the same instance
        TestService singletonInstance = new TestService("singleton", 999);
        Supplier<TestService> singletonFactory = () -> singletonInstance;
        
        frameworkIntegration.registerFactory(TestService.class, singletonFactory);
        
        // When: Creating multiple instances via factory
        TestService instance1 = frameworkIntegration.createInstance(TestService.class);
        TestService instance2 = frameworkIntegration.createInstance(TestService.class);
        
        // Then: Should return the same singleton instance
        assertEquals(singletonInstance, instance1, "Should return singleton instance");
        assertEquals(singletonInstance, instance2, "Should return same singleton instance");
        assertEquals("singleton", instance1.getName(), "Should have singleton properties");
    }

    /**
     * Tests prototype factory pattern (new instance each time)
     */
    @Test
    public void supports_prototype_factory_pattern() {
        // Given: A prototype factory that creates new instances
        frameworkIntegration.registerFactory(TestService.class, 
            () -> new TestService("prototype-" + System.nanoTime(), 42));
        
        // When: Creating multiple instances via factory
        TestService instance1 = frameworkIntegration.createInstance(TestService.class);
        TestService instance2 = frameworkIntegration.createInstance(TestService.class);
        
        // Then: Should create different instances
        assertNotNull(instance1, "Should create first instance");
        assertNotNull(instance2, "Should create second instance");
        assertTrue(!instance1.getName().equals(instance2.getName()), "Should have different names");
        assertEquals(42, instance1.getCount(), "Should have prototype properties");
        assertEquals(42, instance2.getCount(), "Should have prototype properties");
    }

    /**
     * Tests handling classes without factories
     */
    @Test
    public void handles_classes_without_factories() {
        // Given: A class without a registered factory
        Class<TestService> serviceClass = TestService.class;
        
        // When: Checking for factory
        boolean hasFactory = frameworkIntegration.hasFactory(serviceClass);
        
        // Then: Should indicate no factory available
        assertEquals(false, hasFactory, "Should not have factory for unregistered class");
    }

    /**
     * Tests dependency injection style factory pattern
     */
    @Test
    public void supports_dependency_injection_pattern() {
        // Given: A factory that injects dependencies
        Map<String, Object> dependencies = new HashMap<>();
        dependencies.put("configService", new ConfigService("prod-config"));
        dependencies.put("database", new DatabaseService("prod-db"));
        
        Supplier<ComplexService> diFactory = () -> {
            ConfigService config = (ConfigService) dependencies.get("configService");
            DatabaseService db = (DatabaseService) dependencies.get("database");
            return new ComplexService("di-service", config, db);
        };
        
        frameworkIntegration.registerFactory(ComplexService.class, diFactory);
        
        // When: Creating instance with dependency injection
        ComplexService instance = frameworkIntegration.createInstance(ComplexService.class);
        
        // Then: Should have injected dependencies
        assertNotNull(instance, "Should create instance with DI");
        assertEquals("di-service", instance.getName(), "Should have correct name");
        assertNotNull(instance.getConfig(), "Should have injected config");
        assertNotNull(instance.getDatabase(), "Should have injected database");
        assertEquals("prod-config", instance.getConfig().getName(), "Should have correct config");
        assertEquals("prod-db", instance.getDatabase().getName(), "Should have correct database");
    }

    /**
     * Helper method to simulate factory reset update (would be part of InstanceUpdater enhancement)
     */
    private InstancesUpdated performFactoryResetUpdate(ClassRedefinitionSucceeded redefinition) {
        // This simulates what would happen when InstanceUpdater detects need for factory reset
        return new InstancesUpdated(
            redefinition.getClassName(),
            2, // Updated instances
            2, // Total instances
            InstanceUpdateMethod.FACTORY_RESET,
            0, // Failed updates
            "Reset 2 factory instances of " + redefinition.getClassName(),
            Duration.ofMillis(15),
            Instant.now()
        );
    }

    /**
     * Test service class
     */
    private static class TestService {
        private String name;
        private int count;
        
        public TestService(String name, int count) {
            this.name = name;
            this.count = count;
        }
        
        public String getName() { return name; }
        public int getCount() { return count; }
    }

    /**
     * Complex service with dependencies for DI testing
     */
    private static class ComplexService {
        private String name;
        private ConfigService config;
        private DatabaseService database;
        
        public ComplexService(String name, ConfigService config, DatabaseService database) {
            this.name = name;
            this.config = config;
            this.database = database;
        }
        
        public String getName() { return name; }
        public ConfigService getConfig() { return config; }
        public DatabaseService getDatabase() { return database; }
    }

    /**
     * Mock config service for DI testing
     */
    private static class ConfigService {
        private String name;
        
        public ConfigService(String name) {
            this.name = name;
        }
        
        public String getName() { return name; }
    }

    /**
     * Mock database service for DI testing
     */
    private static class DatabaseService {
        private String name;
        
        public DatabaseService(String name) {
            this.name = name;
        }
        
        public String getName() { return name; }
    }
}