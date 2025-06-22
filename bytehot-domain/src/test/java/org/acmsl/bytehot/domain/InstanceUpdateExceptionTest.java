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
 * Filename: InstanceUpdateExceptionTest.java
 *
 * Author: Claude Code
 *
 * Class name: InstanceUpdateExceptionTest
 *
 * Responsibilities:
 *   - Test InstanceUpdateException for proper exception handling
 *
 * Collaborators:
 *   - InstanceUpdateException: Exception for instance update failures
 */
package org.acmsl.bytehot.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Test InstanceUpdateException for proper exception handling
 * @author Claude Code
 * @since 2025-06-17
 */
public class InstanceUpdateExceptionTest {

    /**
     * Tests exception creation with message
     */
    @Test
    public void creates_exception_with_message() {
        // Given: An error message
        String message = "Failed to update instance";
        
        // When: Creating exception with message
        InstanceUpdateException exception = new InstanceUpdateException(message);
        
        // Then: Should have correct message
        assertEquals(message, exception.getMessage(), "Should have correct error message");
        assertNotNull(exception, "Exception should not be null");
    }

    /**
     * Tests exception creation with message and cause
     */
    @Test
    public void creates_exception_with_message_and_cause() {
        // Given: An error message and cause
        String message = "Reflection update failed";
        Throwable cause = new IllegalAccessException("Cannot access private field");
        
        // When: Creating exception with message and cause
        InstanceUpdateException exception = new InstanceUpdateException(message, cause);
        
        // Then: Should have correct message and cause
        assertEquals(message, exception.getMessage(), "Should have correct error message");
        assertSame(cause, exception.getCause(), "Should have correct cause");
    }

    /**
     * Tests exception creation with cause only
     */
    @Test
    public void creates_exception_with_cause_only() {
        // Given: A cause
        Throwable cause = new RuntimeException("Proxy refresh failed");
        
        // When: Creating exception with cause
        InstanceUpdateException exception = new InstanceUpdateException(cause);
        
        // Then: Should have correct cause
        assertSame(cause, exception.getCause(), "Should have correct cause");
        assertNotNull(exception.getMessage(), "Should have a message");
    }

    /**
     * Tests exception inheritance hierarchy
     */
    @Test
    public void extends_runtime_exception() {
        // Given: An instance update exception
        InstanceUpdateException exception = new InstanceUpdateException("Test");
        
        // When: Checking inheritance
        boolean isRuntimeException = exception instanceof RuntimeException;
        boolean isException = exception instanceof Exception;
        
        // Then: Should extend RuntimeException
        assertEquals(true, isRuntimeException, "Should be a RuntimeException");
        assertEquals(true, isException, "Should be an Exception");
    }

    /**
     * Tests exception with instance context information
     */
    @Test
    public void creates_exception_with_instance_context() {
        // Given: Instance context information
        String className = "com.example.Service";
        String instanceId = "instance-123";
        String operation = "reflection-update";
        String message = String.format("Failed to %s for instance %s of class %s", 
                                      operation, instanceId, className);
        
        // When: Creating exception with context
        InstanceUpdateException exception = new InstanceUpdateException(message);
        
        // Then: Should contain context information
        assertEquals(message, exception.getMessage(), "Should contain context information");
        assertEquals(true, exception.getMessage().contains(className), "Should contain class name");
        assertEquals(true, exception.getMessage().contains(instanceId), "Should contain instance ID");
        assertEquals(true, exception.getMessage().contains(operation), "Should contain operation");
    }
}