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
 * Filename: ErrorRecoveryManagerTest.java
 *
 * Author: Claude Code
 *
 * Class name: ErrorRecoveryManagerTest
 *
 * Responsibilities:
 *   - Test comprehensive error recovery mechanisms for ByteHot operations
 *   - Verify rollback functionality and state preservation during failures
 *
 * Collaborators:
 *   - ErrorRecoveryManager: Manages error recovery operations
 *   - ErrorResult: Contains error context and recovery strategy
 *   - InstanceTracker: Tracks instances for rollback operations
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test comprehensive error recovery mechanisms for ByteHot operations
 * @author Claude Code
 * @since 2025-06-17
 */
public class ErrorRecoveryManagerTest {

    private ErrorRecoveryManager recoveryManager;
    private InstanceTracker instanceTracker;

    @BeforeEach
    public void setUp() {
        instanceTracker = new InstanceTracker();
        recoveryManager = new ErrorRecoveryManager(instanceTracker);
    }

    /**
     * Tests rolling back failed class redefinition
     */
    @Test
    public void rolls_back_failed_class_redefinition() {
        // Given: A failed class redefinition
        ClassRedefinitionFailed redefinitionFailed = new ClassRedefinitionFailed(
            "com.example.UserService",
            Paths.get("/app/classes/UserService.class"),
            "Structural changes not supported",
            "JVM rejected class redefinition: unsupported changes",
            "Try using method body changes only",
            Instant.now()
        );
        
        // When: Performing rollback
        RecoveryResult result = recoveryManager.rollbackRedefinition(redefinitionFailed);
        
        // Then: Should successfully rollback
        assertTrue(result.isSuccessful(), "Rollback should be successful");
        assertEquals(RecoveryAction.ROLLBACK_CHANGES, result.getAction(), "Should perform rollback action");
        assertTrue(result.getMessage().contains("rollback"), "Should mention rollback in message");
        assertNotNull(result.getTimestamp(), "Should have timestamp");
    }

    /**
     * Tests preserving instance state during recovery
     */
    @Test
    public void preserves_instance_state_during_recovery() {
        // Given: Instances with current state
        TestService service1 = new TestService("data1");
        TestService service2 = new TestService("data2");
        
        instanceTracker.track(service1);
        instanceTracker.track(service2);
        
        // When: Preserving state during error recovery
        RecoveryResult result = recoveryManager.preserveInstanceStates("com.example.TestService");
        
        // Then: Should preserve all instance states
        assertTrue(result.isSuccessful(), "State preservation should be successful");
        assertEquals(RecoveryAction.PRESERVE_CURRENT_STATE, result.getAction(), "Should preserve current state");
        assertTrue(result.getMessage().contains("preserved"), "Should mention preservation in message");
    }

    /**
     * Tests rejecting invalid changes during recovery
     */
    @Test
    public void rejects_invalid_changes_during_recovery() {
        // Given: An invalid bytecode validation error
        BytecodeValidationException validationError = new BytecodeValidationException(
            "Invalid bytecode: method signature mismatch",
            null
        );
        
        // When: Rejecting invalid changes
        RecoveryResult result = recoveryManager.rejectChanges(validationError, "com.example.Service");
        
        // Then: Should reject changes
        assertTrue(result.isSuccessful(), "Change rejection should be successful");
        assertEquals(RecoveryAction.REJECT_CHANGE, result.getAction(), "Should reject changes");
        assertTrue(result.getMessage().contains("rejected"), "Should mention rejection in message");
    }

    /**
     * Tests retry mechanism for transient failures
     */
    @Test
    public void retries_operations_for_transient_failures() {
        // Given: A transient failure scenario
        String operation = "file-watch";
        String className = "com.example.FileWatchService";
        RuntimeException transientError = new RuntimeException("Temporary network failure");
        
        // When: Attempting retry recovery
        RecoveryResult result = recoveryManager.retryOperation(operation, className, transientError, 3);
        
        // Then: Should attempt retry
        assertNotNull(result, "Recovery result should not be null");
        assertEquals(RecoveryAction.RETRY_OPERATION, result.getAction(), "Should retry operation");
        assertTrue(result.getMessage().contains("retry"), "Should mention retry in message");
    }

    /**
     * Tests emergency shutdown for critical errors
     */
    @Test
    public void performs_emergency_shutdown_for_critical_errors() {
        // Given: A critical system error
        OutOfMemoryError criticalError = new OutOfMemoryError("Java heap space exhausted");
        
        // When: Performing emergency shutdown
        RecoveryResult result = recoveryManager.emergencyShutdown(criticalError, "com.example.Service");
        
        // Then: Should initiate emergency shutdown
        assertTrue(result.isSuccessful(), "Emergency shutdown should be initiated successfully");
        assertEquals(RecoveryAction.EMERGENCY_SHUTDOWN, result.getAction(), "Should perform emergency shutdown");
        assertTrue(result.getMessage().contains("emergency"), "Should mention emergency in message");
        assertTrue(result.requiresImmediateAction(), "Should require immediate action");
    }

    /**
     * Tests fallback mode activation
     */
    @Test
    public void activates_fallback_mode_for_configuration_errors() {
        // Given: A configuration error
        IllegalArgumentException configError = new IllegalArgumentException("Invalid configuration parameter");
        
        // When: Activating fallback mode
        RecoveryResult result = recoveryManager.activateFallbackMode(configError, "ByteHotConfiguration");
        
        // Then: Should activate fallback mode
        assertTrue(result.isSuccessful(), "Fallback mode activation should be successful");
        assertEquals(RecoveryAction.FALLBACK_MODE, result.getAction(), "Should activate fallback mode");
        assertTrue(result.getMessage().contains("fallback"), "Should mention fallback in message");
    }

    /**
     * Tests recovery strategy execution based on error result
     */
    @Test
    public void executes_recovery_strategy_based_on_error_result() {
        // Given: An error result with specific recovery strategy
        ErrorResult errorResult = ErrorResult.create(
            ErrorType.INSTANCE_UPDATE_ERROR,
            ErrorSeverity.ERROR,
            RecoveryStrategy.PRESERVE_CURRENT_STATE,
            true,
            "Instance update failed",
            "com.example.DataService",
            "instance-update",
            new IllegalAccessException("Cannot access private field")
        );
        
        // When: Executing recovery strategy
        RecoveryResult result = recoveryManager.executeRecoveryStrategy(errorResult);
        
        // Then: Should execute appropriate recovery
        assertTrue(result.isSuccessful(), "Recovery strategy execution should be successful");
        assertEquals(RecoveryAction.PRESERVE_CURRENT_STATE, result.getAction(), "Should preserve current state");
        assertEquals(errorResult.getClassName(), result.getClassName(), "Should preserve class name context");
    }

    /**
     * Tests recovery coordination for multiple simultaneous failures
     */
    @Test
    public void coordinates_recovery_for_multiple_failures() {
        // Given: Multiple error results
        ErrorResult error1 = ErrorResult.create(
            ErrorType.VALIDATION_ERROR, ErrorSeverity.WARNING, RecoveryStrategy.REJECT_CHANGE,
            true, "Validation error 1", "com.example.Service1", "validation", null
        );
        ErrorResult error2 = ErrorResult.create(
            ErrorType.REDEFINITION_FAILURE, ErrorSeverity.ERROR, RecoveryStrategy.ROLLBACK_CHANGES,
            true, "Redefinition error 2", "com.example.Service2", "redefinition", null
        );
        
        List<ErrorResult> errors = Arrays.asList(error1, error2);
        
        // When: Coordinating recovery for multiple failures
        List<RecoveryResult> results = recoveryManager.coordinateRecovery(errors);
        
        // Then: Should handle all failures
        assertEquals(2, results.size(), "Should handle all error results");
        assertTrue(results.stream().allMatch(RecoveryResult::isSuccessful), "All recoveries should be successful");
        
        // Should have different recovery actions for different error types
        assertTrue(results.stream().anyMatch(r -> r.getAction() == RecoveryAction.REJECT_CHANGE),
                  "Should include change rejection");
        assertTrue(results.stream().anyMatch(r -> r.getAction() == RecoveryAction.ROLLBACK_CHANGES),
                  "Should include rollback changes");
    }

    /**
     * Tests recovery state tracking and reporting
     */
    @Test
    public void tracks_recovery_state_and_provides_reporting() {
        // Given: Multiple recovery operations
        recoveryManager.retryOperation("op1", "Class1", new RuntimeException("Error 1"), 1);
        recoveryManager.rejectChanges(new BytecodeValidationException("Invalid", null), "Class2");
        recoveryManager.preserveInstanceStates("Class3");
        
        // When: Getting recovery statistics
        RecoveryStatistics stats = recoveryManager.getRecoveryStatistics();
        
        // Then: Should track all operations
        assertTrue(stats.getTotalRecoveryOperations() >= 3, "Should track multiple recovery operations");
        assertTrue(stats.getSuccessfulRecoveries() >= 0, "Should track successful recoveries");
        assertNotNull(stats.getLastRecoveryTime(), "Should track last recovery time");
        assertTrue(stats.getRecoverySuccessRate() >= 0.0 && stats.getRecoverySuccessRate() <= 1.0,
                  "Success rate should be between 0 and 1");
    }

    // Helper test class
    private static class TestService {
        private final String data;
        
        public TestService(final String data) {
            this.data = data;
        }
        
        public String getData() {
            return data;
        }
    }
}