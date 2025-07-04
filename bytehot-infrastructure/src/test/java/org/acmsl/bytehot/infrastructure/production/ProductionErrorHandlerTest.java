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
 * Filename: ProductionErrorHandlerTest.java
 *
 * Author: Claude Code
 *
 * Class name: ProductionErrorHandlerTest
 *
 * Responsibilities:
 *   - Test ProductionErrorHandler functionality
 *   - Verify error classification and recovery mechanisms
 *   - Test circuit breaker integration
 *   - Validate incident reporting behavior
 *
 * Collaborators:
 *   - ProductionErrorHandler: The class under test
 *   - ErrorClassifier: Mock for error classification
 *   - RecoveryManager: Mock for recovery operations
 *   - IncidentReporter: Mock for incident reporting
 *   - CircuitBreaker: Mock for circuit breaker functionality
 */
package org.acmsl.bytehot.infrastructure.production;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test for ProductionErrorHandler - verifying comprehensive error handling in production environments.
 * @author Claude Code
 * @since 2025-07-04
 */
@ExtendWith(MockitoExtension.class)
class ProductionErrorHandlerTest {

    @Mock
    private ErrorClassifier errorClassifier;

    @Mock
    private RecoveryManager recoveryManager;

    @Mock
    private IncidentReporter incidentReporter;

    @Mock
    private CircuitBreaker circuitBreaker;

    private ProductionErrorHandler errorHandler;

    @BeforeEach
    void setUp() {
        errorHandler = new ProductionErrorHandler(
            errorClassifier,
            recoveryManager,
            incidentReporter,
            circuitBreaker
        );
    }

    @Test
    @DisplayName("🧪 Should classify error and attempt recovery on error handling")
    void shouldClassifyErrorAndAttemptRecovery() {
        // Given: A runtime exception and operation context
        RuntimeException error = new RuntimeException("Test error");
        OperationContext context = createTestContext();
        
        ErrorClassification classification = createTestClassification(error, false);
        RecoveryResult recovery = RecoveryResult.successful("Recovery successful");
        
        when(errorClassifier.classify(error, context)).thenReturn(classification);
        when(recoveryManager.attemptRecovery(classification)).thenReturn(recovery);

        // When: Handling the error
        ErrorHandlingResult result = errorHandler.handleError(error, context);

        // Then: Error should be classified and recovery attempted
        assertNotNull(result);
        assertTrue(result.isSuccessful());
        assertEquals(classification, result.getClassification());
        assertEquals(recovery, result.getRecovery());
        
        verify(errorClassifier).classify(error, context);
        verify(recoveryManager).attemptRecovery(classification);
        verify(circuitBreaker).recordResult(true);
    }

    @Test
    @DisplayName("🧪 Should report incident when classification requires it")
    void shouldReportIncidentWhenRequired() {
        // Given: An error that requires incident reporting
        RuntimeException error = new RuntimeException("Critical error");
        OperationContext context = createTestContext();
        
        ErrorClassification classification = createTestClassification(error, true);
        RecoveryResult recovery = RecoveryResult.failed("Recovery failed");
        
        when(errorClassifier.classify(error, context)).thenReturn(classification);
        when(recoveryManager.attemptRecovery(classification)).thenReturn(recovery);

        // When: Handling the error
        ErrorHandlingResult result = errorHandler.handleError(error, context);

        // Then: Incident should be reported
        assertNotNull(result);
        assertFalse(result.isSuccessful());
        
        verify(incidentReporter).reportIncident(error, context, recovery);
        verify(circuitBreaker).recordResult(false);
    }

    @Test
    @DisplayName("🧪 Should not report incident when classification doesn't require it")
    void shouldNotReportIncidentWhenNotRequired() {
        // Given: An error that doesn't require incident reporting
        RuntimeException error = new RuntimeException("Minor error");
        OperationContext context = createTestContext();
        
        ErrorClassification classification = createTestClassification(error, false);
        RecoveryResult recovery = RecoveryResult.successful("Recovery successful");
        
        when(errorClassifier.classify(error, context)).thenReturn(classification);
        when(recoveryManager.attemptRecovery(classification)).thenReturn(recovery);

        // When: Handling the error
        errorHandler.handleError(error, context);

        // Then: Incident should not be reported
        verify(incidentReporter, never()).reportIncident(any(), any(), any());
    }

    @Test
    @DisplayName("🧪 Should handle error handling failure gracefully")
    void shouldHandleErrorHandlingFailureGracefully() {
        // Given: Error classifier throws exception
        RuntimeException originalError = new RuntimeException("Original error");
        RuntimeException classifierError = new RuntimeException("Classifier failed");
        OperationContext context = createTestContext();
        
        when(errorClassifier.classify(originalError, context)).thenThrow(classifierError);

        // When: Handling the error
        ErrorHandlingResult result = errorHandler.handleError(originalError, context);

        // Then: Should return failure result with handling error
        assertNotNull(result);
        assertFalse(result.isSuccessful());
        assertEquals(classifierError, result.getHandlingError());
        
        // Should not attempt recovery or incident reporting
        verify(recoveryManager, never()).attemptRecovery(any());
        verify(incidentReporter, never()).reportIncident(any(), any(), any());
    }

    @Test
    @DisplayName("🧪 Should provide error statistics")
    void shouldProvideErrorStatistics() {
        // Given: Error handler with some state
        when(circuitBreaker.getState()).thenReturn(CircuitBreakerState.CLOSED);

        // When: Getting error statistics
        ErrorStatistics statistics = errorHandler.getErrorStatistics();

        // Then: Should return valid statistics
        assertNotNull(statistics);
        assertEquals(CircuitBreakerState.CLOSED, statistics.getCircuitBreakerState());
    }

    @Test
    @DisplayName("🧪 Should detect degraded system state")
    void shouldDetectDegradedSystemState() {
        // Given: Circuit breaker in open state
        when(circuitBreaker.getState()).thenReturn(CircuitBreakerState.OPEN);

        // When: Checking if system is degraded
        boolean isDegraded = errorHandler.isSystemDegraded();

        // Then: Should detect degraded state
        assertTrue(isDegraded);
    }

    @Test
    @DisplayName("🧪 Should handle async error processing")
    void shouldHandleAsyncErrorProcessing() throws Exception {
        // Given: An error and context for async processing
        RuntimeException error = new RuntimeException("Async test error");
        OperationContext context = createTestContext();
        
        ErrorClassification classification = createTestClassification(error, false);
        RecoveryResult recovery = RecoveryResult.successful("Async recovery");
        
        when(errorClassifier.classify(error, context)).thenReturn(classification);
        when(recoveryManager.attemptRecovery(classification)).thenReturn(recovery);

        // When: Handling error asynchronously
        ErrorHandlingResult result = errorHandler.handleErrorAsync(error, context).get();

        // Then: Should process error correctly
        assertNotNull(result);
        assertTrue(result.isSuccessful());
        assertEquals(classification, result.getClassification());
        assertEquals(recovery, result.getRecovery());
    }

    private OperationContext createTestContext() {
        return OperationContext.builder()
            .operationType("test-operation")
            .isCritical(false)
            .retryCount(0)
            .build();
    }

    private ErrorClassification createTestClassification(Throwable error, boolean requiresIncident) {
        return ErrorClassification.builder()
            .errorType(ErrorType.RUNTIME)
            .severity(ErrorSeverity.MEDIUM)
            .recoverability(Recoverability.TRANSIENT)
            .requiresIncidentReport(requiresIncident)
            .error(error)
            .build();
    }
}