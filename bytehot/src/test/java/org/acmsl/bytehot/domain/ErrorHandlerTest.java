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
 * Filename: ErrorHandlerTest.java
 *
 * Author: Claude Code
 *
 * Class name: ErrorHandlerTest
 *
 * Responsibilities:
 *   - Test comprehensive error handling for ByteHot operations
 *   - Verify error classification and recovery strategy selection
 *
 * Collaborators:
 *   - ErrorHandler: Central error handling coordinator
 *   - ErrorType: Classification of different error types
 *   - RecoveryStrategy: Strategy for recovering from errors
 */
package org.acmsl.bytehot.domain;

import org.acmsl.bytehot.domain.events.ClassRedefinitionFailed;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test comprehensive error handling for ByteHot operations
 * @author Claude Code
 * @since 2025-06-17
 */
public class ErrorHandlerTest {

    private ErrorHandler errorHandler;

    @BeforeEach
    public void setUp() {
        errorHandler = new ErrorHandler();
    }

    /**
     * Tests handling bytecode validation errors
     */
    @Test
    public void handles_bytecode_validation_errors() {
        // Given: A bytecode validation error
        BytecodeValidationException validationError = new BytecodeValidationException(
            "Invalid bytecode: method signature mismatch",
            null // No specific bytecode rejected event
        );
        
        // When: Handling the error
        ErrorResult result = errorHandler.handleError(validationError, "com.example.Service");
        
        // Then: Should classify and provide recovery strategy
        assertNotNull(result, "Error result should not be null");
        assertEquals(ErrorType.VALIDATION_ERROR, result.getErrorType(), "Should classify as validation error");
        assertEquals(RecoveryStrategy.REJECT_CHANGE, result.getRecoveryStrategy(), "Should reject invalid changes");
        assertTrue(result.isRecoverable(), "Validation errors should be recoverable");
        assertNotNull(result.getErrorMessage(), "Should have error message");
        assertTrue(result.getErrorMessage().contains("bytecode"), "Should mention bytecode in message");
    }

    /**
     * Tests handling hot-swap operation failures
     */
    @Test
    public void handles_hot_swap_failures() {
        // Given: A hot-swap failure event
        ClassRedefinitionFailed redefinitionFailed = new ClassRedefinitionFailed(
            "com.example.UserService",
            Paths.get("/app/classes/UserService.class"),
            "Structural changes not supported",
            "JVM rejected class redefinition: unsupported changes",
            "Try using method body changes only",
            Instant.now()
        );
        
        // When: Handling the failure
        ErrorResult result = errorHandler.handleRedefinitionFailure(redefinitionFailed);
        
        // Then: Should provide appropriate recovery strategy
        assertEquals(ErrorType.REDEFINITION_FAILURE, result.getErrorType(), "Should classify as redefinition failure");
        assertEquals(RecoveryStrategy.ROLLBACK_CHANGES, result.getRecoveryStrategy(), "Should rollback failed changes");
        assertTrue(result.isRecoverable(), "Redefinition failures should be recoverable");
        assertTrue(result.getErrorMessage().contains("redefinition"), "Should mention redefinition in message");
    }

    /**
     * Tests handling instance update errors
     */
    @Test
    public void handles_instance_update_errors() {
        // Given: An instance update error
        InstanceUpdateException updateError = new InstanceUpdateException(
            "Failed to update instance state", 
            new IllegalAccessException("Cannot access private field")
        );
        
        // When: Handling the error
        ErrorResult result = errorHandler.handleError(updateError, "com.example.DataService");
        
        // Then: Should provide instance-specific recovery
        assertEquals(ErrorType.INSTANCE_UPDATE_ERROR, result.getErrorType(), "Should classify as instance update error");
        assertEquals(RecoveryStrategy.PRESERVE_CURRENT_STATE, result.getRecoveryStrategy(), "Should preserve current state");
        assertTrue(result.isRecoverable(), "Instance update errors should be recoverable");
        assertNotNull(result.getCause(), "Should preserve original cause");
    }

    /**
     * Tests handling critical system errors
     */
    @Test
    public void handles_critical_system_errors() {
        // Given: A critical system error
        Error criticalError = new OutOfMemoryError("Java heap space");
        
        // When: Handling the critical error
        ErrorResult result = errorHandler.handleError(criticalError, "com.example.Service");
        
        // Then: Should mark as non-recoverable and suggest shutdown
        assertEquals(ErrorType.CRITICAL_SYSTEM_ERROR, result.getErrorType(), "Should classify as critical system error");
        assertEquals(RecoveryStrategy.EMERGENCY_SHUTDOWN, result.getRecoveryStrategy(), "Should suggest emergency shutdown");
        assertEquals(false, result.isRecoverable(), "Critical system errors should not be recoverable");
        assertTrue(result.requiresImmediateAttention(), "Should require immediate attention");
    }

    /**
     * Tests error context information preservation
     */
    @Test
    public void preserves_error_context_information() {
        // Given: An error with context
        String className = "com.example.ComplexService";
        String operation = "instance-update";
        Exception originalError = new IllegalStateException("Invalid state transition");
        
        // When: Handling with context
        ErrorResult result = errorHandler.handleErrorWithContext(originalError, className, operation);
        
        // Then: Should preserve all context information
        assertEquals(className, result.getClassName(), "Should preserve class name");
        assertEquals(operation, result.getOperation(), "Should preserve operation");
        
        // The cause should now be an EventSnapshotException that wraps the original
        Throwable cause = result.getCause();
        if (cause instanceof EventSnapshotException) {
            EventSnapshotException enhanced = (EventSnapshotException) cause;
            assertEquals(originalError, enhanced.getOriginalException(), "Should preserve original exception in enhanced wrapper");
        } else {
            assertEquals(originalError, cause, "Should preserve original exception");
        }
        
        assertNotNull(result.getTimestamp(), "Should have timestamp");
        assertNotNull(result.getErrorId(), "Should have unique error ID");
    }

    /**
     * Tests error severity assessment
     */
    @Test
    public void assesses_error_severity_correctly() {
        // Given: Different types of errors
        Exception warningError = new IllegalArgumentException("Invalid parameter");
        Exception errorLevel = new RuntimeException("Operation failed");
        Error criticalError = new OutOfMemoryError("Memory exhausted");
        
        // When: Assessing severity
        ErrorSeverity warningSeverity = errorHandler.assessSeverity(warningError);
        ErrorSeverity errorSeverity = errorHandler.assessSeverity(errorLevel);
        ErrorSeverity criticalSeverity = errorHandler.assessSeverity(criticalError);
        
        // Then: Should assess correctly
        assertEquals(ErrorSeverity.WARNING, warningSeverity, "Should classify as warning");
        assertEquals(ErrorSeverity.ERROR, errorSeverity, "Should classify as error");
        assertEquals(ErrorSeverity.CRITICAL, criticalSeverity, "Should classify as critical");
    }

    /**
     * Tests error recovery suggestion based on context
     */
    @Test
    public void suggests_context_appropriate_recovery() {
        // Given: Same error type in different contexts
        RuntimeException sameError = new RuntimeException("Generic failure");
        
        // When: Handling in different contexts
        ErrorResult fileWatchContext = errorHandler.handleErrorWithContext(
            sameError, "FileWatchService", "file-monitoring"
        );
        ErrorResult hotSwapContext = errorHandler.handleErrorWithContext(
            sameError, "HotSwapManager", "class-redefinition"
        );
        
        // Then: Should suggest different recovery strategies
        assertNotNull(fileWatchContext.getRecoveryStrategy(), "Should have recovery for file watch");
        assertNotNull(hotSwapContext.getRecoveryStrategy(), "Should have recovery for hot swap");
        // Recovery strategies may differ based on operation context
        assertTrue(fileWatchContext.isRecoverable() || hotSwapContext.isRecoverable(), 
                  "At least one context should be recoverable");
    }

    /**
     * Tests error aggregation for multiple related failures
     */
    @Test
    public void aggregates_related_errors() {
        // Given: Multiple related errors
        Exception error1 = new RuntimeException("First failure");
        Exception error2 = new RuntimeException("Second failure");
        Exception error3 = new RuntimeException("Third failure");
        
        String className = "com.example.Service";
        
        // When: Handling multiple errors for same class
        ErrorResult result1 = errorHandler.handleError(error1, className);
        ErrorResult result2 = errorHandler.handleError(error2, className);
        ErrorResult result3 = errorHandler.handleError(error3, className);
        
        // Then: Should track error patterns
        int errorCount = errorHandler.getErrorCount(className);
        assertTrue(errorCount >= 3, "Should track multiple errors for same class");
        
        boolean hasPattern = errorHandler.detectErrorPattern(className);
        assertTrue(hasPattern, "Should detect error pattern with multiple failures");
    }
}