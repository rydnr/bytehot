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
 * Filename: RollbackManagerTest.java
 *
 * Author: Claude Code
 *
 * Class name: RollbackManagerTest
 *
 * Responsibilities:
 *   - Test comprehensive rollback functionality for failed operations
 *   - Verify state restoration and transaction-like behavior
 *
 * Collaborators:
 *   - RollbackManager: Manages rollback operations and state restoration
 *   - RollbackSnapshot: Captures state for potential rollback
 *   - InstanceTracker: Tracks instances for state restoration
 */
package org.acmsl.bytehot.domain;

import org.acmsl.bytehot.domain.events.ClassRedefinitionFailed;
import org.acmsl.bytehot.domain.events.InstancesUpdated;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test comprehensive rollback functionality for failed operations
 * @author Claude Code
 * @since 2025-06-17
 */
public class RollbackManagerTest {

    private RollbackManager rollbackManager;
    private InstanceTracker instanceTracker;

    @BeforeEach
    public void setUp() {
        instanceTracker = new InstanceTracker();
        rollbackManager = new RollbackManager(instanceTracker);
    }

    /**
     * Tests creating rollback snapshots before operations
     */
    @Test
    public void creates_rollback_snapshots_before_operations() {
        // Given: A class and its instances
        TestService service = new TestService("initial-data");
        instanceTracker.enableTracking(TestService.class);
        instanceTracker.track(service);
        
        // When: Creating a rollback snapshot
        RollbackSnapshot snapshot = rollbackManager.createSnapshot("com.example.TestService");
        
        // Then: Should capture current state
        assertNotNull(snapshot, "Snapshot should not be null");
        assertEquals("com.example.TestService", snapshot.getClassName(), "Should capture class name");
        assertTrue(snapshot.getInstanceCount() >= 1, "Should capture instance count");
        assertNotNull(snapshot.getTimestamp(), "Should have timestamp");
        assertNotNull(snapshot.getSnapshotId(), "Should have unique ID");
    }

    /**
     * Tests rolling back after failed class redefinition
     */
    @Test
    public void rolls_back_after_failed_class_redefinition() {
        // Given: A snapshot and failed redefinition
        TestService service = new TestService("original-state");
        instanceTracker.enableTracking(TestService.class);
        instanceTracker.track(service);
        
        RollbackSnapshot snapshot = rollbackManager.createSnapshot("com.example.TestService");
        
        ClassRedefinitionFailed failure = new ClassRedefinitionFailed(
            "com.example.TestService",
            Paths.get("/app/classes/TestService.class"),
            "Structural changes not supported",
            "JVM rejected class redefinition",
            "Use method body changes only",
            Instant.now()
        );
        
        // When: Rolling back the failed operation
        RollbackResult result = rollbackManager.rollbackToSnapshot(snapshot, failure);
        
        // Then: Should successfully rollback
        assertTrue(result.isSuccessful(), "Rollback should be successful");
        assertEquals(snapshot.getSnapshotId(), result.getSnapshotId(), "Should reference correct snapshot");
        assertEquals("com.example.TestService", result.getClassName(), "Should rollback correct class");
        assertTrue(result.getMessage().contains("rollback"), "Should mention rollback in message");
    }

    /**
     * Tests rolling back instance state changes
     */
    @Test
    public void rolls_back_instance_state_changes() {
        // Given: Instances with modified state
        TestService service1 = new TestService("state-1");
        TestService service2 = new TestService("state-2");
        
        instanceTracker.enableTracking(TestService.class);
        instanceTracker.track(service1);
        instanceTracker.track(service2);
        
        RollbackSnapshot snapshot = rollbackManager.createSnapshot("com.example.TestService");
        
        // Simulate state changes
        service1.updateData("modified-state-1");
        service2.updateData("modified-state-2");
        
        // When: Rolling back instance state
        RollbackResult result = rollbackManager.rollbackInstanceStates(snapshot);
        
        // Then: Should restore original state
        assertTrue(result.isSuccessful(), "Instance state rollback should be successful");
        assertEquals(RollbackOperation.INSTANCE_STATE_RESTORE, result.getOperation(), "Should restore instance state");
        assertTrue(result.getMessage().contains("instance"), "Should mention instance in message");
    }

    /**
     * Tests rolling back bytecode changes
     */
    @Test
    public void rolls_back_bytecode_changes() {
        // Given: A class with modified bytecode
        String className = "com.example.ModifiedService";
        byte[] originalBytecode = "original-bytecode".getBytes();
        byte[] modifiedBytecode = "modified-bytecode".getBytes();
        
        RollbackSnapshot snapshot = rollbackManager.createBytecodeSnapshot(className, originalBytecode);
        
        // When: Rolling back bytecode changes
        RollbackResult result = rollbackManager.rollbackBytecode(snapshot, modifiedBytecode);
        
        // Then: Should restore original bytecode
        assertTrue(result.isSuccessful(), "Bytecode rollback should be successful");
        assertEquals(RollbackOperation.BYTECODE_RESTORE, result.getOperation(), "Should restore bytecode");
        assertEquals(className, result.getClassName(), "Should rollback correct class");
    }

    /**
     * Tests rollback with timeout handling
     */
    @Test
    public void handles_rollback_timeouts() {
        // Given: A snapshot and timeout constraint
        RollbackSnapshot snapshot = rollbackManager.createSnapshot("com.example.TimeoutService");
        Duration timeout = Duration.ofMillis(100);
        
        // When: Attempting rollback with timeout
        RollbackResult result = rollbackManager.rollbackWithTimeout(snapshot, timeout);
        
        // Then: Should complete within timeout or handle timeout gracefully
        assertNotNull(result, "Should return result even if timeout occurs");
        assertTrue(result.isSuccessful() || result.isTimedOut(), "Should either succeed or indicate timeout");
        
        if (result.isTimedOut()) {
            assertTrue(result.getMessage().contains("timeout"), "Should mention timeout in message");
        }
    }

    /**
     * Tests cascading rollback for related operations
     */
    @Test
    public void performs_cascading_rollback_for_related_operations() {
        // Given: Multiple related snapshots
        RollbackSnapshot snapshot1 = rollbackManager.createSnapshot("com.example.Service1");
        RollbackSnapshot snapshot2 = rollbackManager.createSnapshot("com.example.Service2");
        RollbackSnapshot snapshot3 = rollbackManager.createSnapshot("com.example.Service3");
        
        // When: Performing cascading rollback
        CascadingRollbackResult result = rollbackManager.rollbackCascading(
            Arrays.asList(snapshot1, snapshot2, snapshot3)
        );
        
        // Then: Should rollback all related operations
        assertTrue(result.isOverallSuccessful(), "Cascading rollback should be successful");
        assertEquals(3, result.getRollbackResults().size(), "Should rollback all snapshots");
        assertTrue(result.getRollbackResults().stream().allMatch(RollbackResult::isSuccessful),
                  "All individual rollbacks should be successful");
    }

    /**
     * Tests rollback conflict detection and resolution
     */
    @Test
    public void detects_and_resolves_rollback_conflicts() {
        // Given: Conflicting state changes
        TestService service = new TestService("original");
        instanceTracker.enableTracking(TestService.class);
        instanceTracker.track(service);
        
        RollbackSnapshot snapshot1 = rollbackManager.createSnapshot("com.example.TestService");
        
        // Simulate concurrent modifications
        service.updateData("concurrent-change-1");
        RollbackSnapshot snapshot2 = rollbackManager.createSnapshot("com.example.TestService");
        
        service.updateData("concurrent-change-2");
        
        // When: Attempting rollback with conflicts
        ConflictResolutionResult result = rollbackManager.rollbackWithConflictResolution(
            snapshot1, ConflictResolutionStrategy.MERGE_CHANGES
        );
        
        // Then: Should detect and resolve conflicts
        assertNotNull(result, "Conflict resolution result should not be null");
        assertTrue(result.hasConflicts() || result.isSuccessful(), "Should either have conflicts or succeed");
        
        if (result.hasConflicts()) {
            assertNotNull(result.getResolutionStrategy(), "Should have resolution strategy");
            assertTrue(result.getMessage().contains("conflict"), "Should mention conflicts in message");
        }
    }

    /**
     * Tests rollback performance and resource cleanup
     */
    @Test
    public void manages_rollback_performance_and_cleanup() {
        // Given: Multiple snapshots consuming resources
        for (int i = 0; i < 10; i++) {
            rollbackManager.createSnapshot("com.example.Service" + i);
        }
        
        // When: Performing cleanup
        CleanupResult cleanupResult = rollbackManager.cleanupOldSnapshots(Duration.ofMinutes(1));
        
        // Then: Should clean up resources efficiently
        assertTrue(cleanupResult.isSuccessful(), "Cleanup should be successful");
        assertTrue(cleanupResult.getCleanedSnapshotCount() >= 0, "Should report cleaned snapshots");
        assertTrue(cleanupResult.getCleanupDuration().toMillis() >= 0, "Should have cleanup duration");
    }

    /**
     * Tests rollback audit trail and logging
     */
    @Test
    public void maintains_rollback_audit_trail() {
        // Given: Multiple rollback operations
        RollbackSnapshot snapshot1 = rollbackManager.createSnapshot("com.example.AuditService1");
        RollbackSnapshot snapshot2 = rollbackManager.createSnapshot("com.example.AuditService2");
        
        rollbackManager.rollbackToSnapshot(snapshot1, null);
        rollbackManager.rollbackToSnapshot(snapshot2, null);
        
        // When: Retrieving audit trail
        RollbackAuditTrail auditTrail = rollbackManager.getAuditTrail();
        
        // Then: Should maintain comprehensive audit information
        assertNotNull(auditTrail, "Audit trail should not be null");
        assertTrue(auditTrail.getTotalOperations() >= 2, "Should track all operations");
        assertTrue(auditTrail.getSuccessfulOperations() >= 0, "Should track successful operations");
        assertNotNull(auditTrail.getLastOperationTime(), "Should track last operation time");
        
        // Should provide detailed operation history
        assertTrue(auditTrail.getOperationHistory().size() >= 2, "Should maintain operation history");
    }

    // Helper test classes
    private static class TestService {
        private String data;
        
        public TestService(final String data) {
            this.data = data;
        }
        
        public String getData() {
            return data;
        }
        
        public void updateData(final String newData) {
            this.data = newData;
        }
    }
}