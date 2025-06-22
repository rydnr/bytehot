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
 * Filename: InstancesUpdatedTest.java
 *
 * Author: Claude Code
 *
 * Class name: InstancesUpdatedTest
 *
 * Responsibilities:
 *   - Test InstancesUpdated domain event for instance update operations
 *
 * Collaborators:
 *   - InstancesUpdated: Domain event for instance update completion
 *   - InstanceUpdateMethod: Enum for update strategies
 */
package org.acmsl.bytehot.domain.events;

import org.acmsl.bytehot.domain.InstanceUpdateMethod;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test InstancesUpdated domain event for instance update operations
 * @author Claude Code
 * @since 2025-06-17
 */
public class InstancesUpdatedTest {

    /**
     * Tests successful automatic instance update
     */
    @Test
    public void successful_automatic_update_creates_event() {
        // Given: Parameters for successful automatic update
        String className = "com.example.UserService";
        int updatedInstances = 5;
        int totalInstances = 5;
        InstanceUpdateMethod updateMethod = InstanceUpdateMethod.AUTOMATIC;
        int failedUpdates = 0;
        String updateDetails = "JVM automatically updated all instances with new method implementations";
        Duration duration = Duration.ofMillis(3);
        Instant timestamp = Instant.now();
        
        // When: Creating InstancesUpdated event
        InstancesUpdated event = new InstancesUpdated(
            className,
            updatedInstances,
            totalInstances,
            updateMethod,
            failedUpdates,
            updateDetails,
            duration,
            timestamp
        );
        
        // Then: Event should contain all details
        assertNotNull(event, "Event should not be null");
        assertEquals(className, event.getClassName(), "Should have correct class name");
        assertEquals(updatedInstances, event.getUpdatedInstances(), "Should have correct updated instances count");
        assertEquals(totalInstances, event.getTotalInstances(), "Should have correct total instances count");
        assertEquals(updateMethod, event.getUpdateMethod(), "Should have correct update method");
        assertEquals(failedUpdates, event.getFailedUpdates(), "Should have correct failed updates count");
        assertEquals(updateDetails, event.getUpdateDetails(), "Should have correct update details");
        assertEquals(duration, event.getDuration(), "Should have correct duration");
        assertEquals(timestamp, event.getTimestamp(), "Should have correct timestamp");
    }

    /**
     * Tests partial instance update with failures
     */
    @Test
    public void partial_update_with_failures_creates_event() {
        // Given: Parameters for partial update with failures
        String className = "com.example.ComplexService";
        int updatedInstances = 8;
        int totalInstances = 10;
        InstanceUpdateMethod updateMethod = InstanceUpdateMethod.REFLECTION;
        int failedUpdates = 2;
        String updateDetails = "2 instances failed to update due to state inconsistencies";
        Duration duration = Duration.ofMillis(45);
        Instant timestamp = Instant.now();
        
        // When: Creating InstancesUpdated event
        InstancesUpdated event = new InstancesUpdated(
            className,
            updatedInstances,
            totalInstances,
            updateMethod,
            failedUpdates,
            updateDetails,
            duration,
            timestamp
        );
        
        // Then: Event should reflect partial success
        assertEquals(className, event.getClassName(), "Should have correct class name");
        assertEquals(updatedInstances, event.getUpdatedInstances(), "Should have correct updated instances count");
        assertEquals(totalInstances, event.getTotalInstances(), "Should have correct total instances count");
        assertEquals(updateMethod, event.getUpdateMethod(), "Should have correct update method");
        assertEquals(failedUpdates, event.getFailedUpdates(), "Should have correct failed updates count");
        assertTrue(event.getFailedUpdates() > 0, "Should have some failed updates");
        assertTrue(event.getUpdatedInstances() < event.getTotalInstances(), "Not all instances should be updated");
        assertEquals(updateDetails, event.getUpdateDetails(), "Should have correct update details");
        assertEquals(duration, event.getDuration(), "Should have correct duration");
        assertEquals(timestamp, event.getTimestamp(), "Should have correct timestamp");
    }

    /**
     * Tests proxy refresh update scenario
     */
    @Test
    public void proxy_refresh_update_creates_event() {
        // Given: Parameters for proxy refresh update
        String className = "com.example.ProxyService";
        int updatedInstances = 3;
        int totalInstances = 3;
        InstanceUpdateMethod updateMethod = InstanceUpdateMethod.PROXY_REFRESH;
        int failedUpdates = 0;
        String updateDetails = "All dynamic proxies refreshed successfully";
        Duration duration = Duration.ofMillis(12);
        Instant timestamp = Instant.now();
        
        // When: Creating InstancesUpdated event
        InstancesUpdated event = new InstancesUpdated(
            className,
            updatedInstances,
            totalInstances,
            updateMethod,
            failedUpdates,
            updateDetails,
            duration,
            timestamp
        );
        
        // Then: Event should indicate proxy refresh strategy
        assertEquals(InstanceUpdateMethod.PROXY_REFRESH, event.getUpdateMethod(), 
            "Should use proxy refresh update method");
        assertEquals(0, event.getFailedUpdates(), "Should have no failures for proxy refresh");
        assertTrue(event.getUpdateDetails().toLowerCase().contains("prox"), 
            "Update details should mention proxy refresh");
        assertEquals(updatedInstances, event.getTotalInstances(), 
            "All instances should be updated in proxy refresh");
    }

    /**
     * Tests factory reset update scenario
     */
    @Test
    public void factory_reset_update_creates_event() {
        // Given: Parameters for factory reset update
        String className = "com.example.BeanService";
        int updatedInstances = 1;
        int totalInstances = 1;
        InstanceUpdateMethod updateMethod = InstanceUpdateMethod.FACTORY_RESET;
        int failedUpdates = 0;
        String updateDetails = "Singleton bean recreated through factory";
        Duration duration = Duration.ofMillis(25);
        Instant timestamp = Instant.now();
        
        // When: Creating InstancesUpdated event
        InstancesUpdated event = new InstancesUpdated(
            className,
            updatedInstances,
            totalInstances,
            updateMethod,
            failedUpdates,
            updateDetails,
            duration,
            timestamp
        );
        
        // Then: Event should indicate factory reset strategy
        assertEquals(InstanceUpdateMethod.FACTORY_RESET, event.getUpdateMethod(), 
            "Should use factory reset update method");
        assertEquals(1, event.getUpdatedInstances(), "Should have updated singleton instance");
        assertEquals(1, event.getTotalInstances(), "Should have one total instance (singleton)");
        assertTrue(event.getUpdateDetails().contains("factory"), 
            "Update details should mention factory reset");
    }

    /**
     * Tests no update scenario
     */
    @Test
    public void no_update_scenario_creates_event() {
        // Given: Parameters for no update scenario
        String className = "com.example.StaticUtility";
        int updatedInstances = 0;
        int totalInstances = 0;
        InstanceUpdateMethod updateMethod = InstanceUpdateMethod.NO_UPDATE;
        int failedUpdates = 0;
        String updateDetails = "No instances found - static utility class";
        Duration duration = Duration.ofMillis(1);
        Instant timestamp = Instant.now();
        
        // When: Creating InstancesUpdated event
        InstancesUpdated event = new InstancesUpdated(
            className,
            updatedInstances,
            totalInstances,
            updateMethod,
            failedUpdates,
            updateDetails,
            duration,
            timestamp
        );
        
        // Then: Event should indicate no update needed
        assertEquals(InstanceUpdateMethod.NO_UPDATE, event.getUpdateMethod(), 
            "Should use no update method");
        assertEquals(0, event.getUpdatedInstances(), "Should have no updated instances");
        assertEquals(0, event.getTotalInstances(), "Should have no total instances");
        assertEquals(0, event.getFailedUpdates(), "Should have no failed updates");
        assertTrue(event.getDuration().toMillis() <= 5, 
            "Duration should be very short for no update");
    }
}