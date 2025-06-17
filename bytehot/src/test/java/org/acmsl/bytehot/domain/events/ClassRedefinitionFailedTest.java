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
 * Filename: ClassRedefinitionFailedTest.java
 *
 * Author: Claude Code
 *
 * Class name: ClassRedefinitionFailedTest
 *
 * Responsibilities:
 *   - Test ClassRedefinitionFailed event when JVM rejects class redefinition
 *
 * Collaborators:
 *   - ClassRedefinitionFailed: Domain event for redefinition failures
 *   - HotSwapException: Exception containing failure details
 */
package org.acmsl.bytehot.domain.events;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test ClassRedefinitionFailed event when JVM rejects class redefinition
 * @author Claude Code
 * @since 2025-06-17
 */
public class ClassRedefinitionFailedTest {

    /**
     * Tests that JVM redefinition failure triggers ClassRedefinitionFailed event
     */
    @Test
    public void jvm_redefinition_failure_triggers_failed_event(@TempDir Path tempDir) throws IOException {
        // Given: A hot-swap request that will fail at JVM level
        Path classFile = tempDir.resolve("IncompatibleClass.class");
        Files.write(classFile, createIncompatibleBytecode("IncompatibleClass"));
        
        HotSwapRequested request = new HotSwapRequested(
            classFile,
            "IncompatibleClass",
            createOriginalBytecode("IncompatibleClass"),
            createIncompatibleBytecode("IncompatibleClass"),
            "Bypassed validation - testing JVM rejection",
            java.time.Instant.now()
        );
        
        // When: HotSwapManager attempts redefinition and JVM rejects it
        org.acmsl.bytehot.domain.HotSwapManager manager = new org.acmsl.bytehot.domain.HotSwapManager();
        
        // Then: HotSwapException should be thrown with ClassRedefinitionFailed event
        org.acmsl.bytehot.domain.HotSwapException exception = 
            assertThrows(org.acmsl.bytehot.domain.HotSwapException.class, 
                () -> manager.performRedefinition(request));
        
        ClassRedefinitionFailed failureEvent = exception.getFailureEvent();
        assertNotNull(failureEvent, "Exception should contain ClassRedefinitionFailed event");
        assertEquals("IncompatibleClass", failureEvent.getClassName(), "Event should contain correct class name");
        assertEquals(classFile, failureEvent.getClassFile(), "Event should contain class file path");
        assertNotNull(failureEvent.getFailureReason(), "Event should contain failure reason");
        assertTrue(failureEvent.getFailureReason().contains("incompatible"), 
            "Failure reason should mention incompatibility");
        assertNotNull(failureEvent.getJvmError(), "Event should contain JVM error message");
        assertNotNull(failureEvent.getRecoveryAction(), "Event should suggest recovery action");
        assertNotNull(failureEvent.getTimestamp(), "Event should have a timestamp");
    }

    /**
     * Tests that schema change rejection provides detailed error information
     */
    @Test
    public void schema_change_rejection_provides_detailed_error(@TempDir Path tempDir) throws IOException {
        // Given: A request with schema changes that JVM will reject
        Path classFile = tempDir.resolve("SchemaChangeClass.class");
        Files.write(classFile, createSchemaChangeBytecode("SchemaChangeClass"));
        
        HotSwapRequested request = new HotSwapRequested(
            classFile,
            "SchemaChangeClass",
            createOriginalBytecode("SchemaChangeClass"),
            createSchemaChangeBytecode("SchemaChangeClass"),
            "Schema changes detected but attempting redefinition",
            java.time.Instant.now()
        );
        
        // When: JVM rejects due to schema changes
        org.acmsl.bytehot.domain.HotSwapManager manager = new org.acmsl.bytehot.domain.HotSwapManager();
        
        // Then: Detailed failure information should be provided
        org.acmsl.bytehot.domain.HotSwapException exception = 
            assertThrows(org.acmsl.bytehot.domain.HotSwapException.class, 
                () -> manager.performRedefinition(request));
        
        ClassRedefinitionFailed failureEvent = exception.getFailureEvent();
        assertEquals("SchemaChangeClass", failureEvent.getClassName(), "Should have correct class name");
        
        String failureReason = failureEvent.getFailureReason();
        assertTrue(failureReason.contains("schema") || failureReason.contains("incompatible"), 
            "Should mention schema issues");
        
        String jvmError = failureEvent.getJvmError();
        assertNotNull(jvmError, "Should have JVM error message");
        assertTrue(jvmError.length() > 0, "JVM error should not be empty");
        
        String recoveryAction = failureEvent.getRecoveryAction();
        assertTrue(recoveryAction.toLowerCase().contains("restart") || recoveryAction.toLowerCase().contains("reload"), 
            "Should suggest recovery action");
    }

    /**
     * Tests that class not found error provides appropriate guidance
     */
    @Test
    public void class_not_found_error_provides_guidance(@TempDir Path tempDir) throws IOException {
        // Given: A request for a class that's not loaded in JVM
        Path classFile = tempDir.resolve("NotLoadedClass.class");
        Files.write(classFile, createNewBytecode("NotLoadedClass"));
        
        HotSwapRequested request = new HotSwapRequested(
            classFile,
            "NotLoadedClass",
            createOriginalBytecode("NotLoadedClass"),
            createNewBytecode("NotLoadedClass"),
            "Attempting to redefine unloaded class",
            java.time.Instant.now()
        );
        
        // When: JVM cannot find the class to redefine
        org.acmsl.bytehot.domain.HotSwapManager manager = new org.acmsl.bytehot.domain.HotSwapManager();
        
        // Then: Appropriate guidance should be provided
        org.acmsl.bytehot.domain.HotSwapException exception = 
            assertThrows(org.acmsl.bytehot.domain.HotSwapException.class, 
                () -> manager.performRedefinition(request));
        
        ClassRedefinitionFailed failureEvent = exception.getFailureEvent();
        assertEquals("NotLoadedClass", failureEvent.getClassName(), "Should have correct class name");
        
        String failureReason = failureEvent.getFailureReason();
        assertTrue(failureReason.contains("not found") || failureReason.contains("not loaded"), 
            "Should mention class loading issue");
        
        String recoveryAction = failureEvent.getRecoveryAction();
        assertTrue(recoveryAction.contains("load") || recoveryAction.contains("instantiate"), 
            "Should suggest loading the class first");
    }

    /**
     * Creates original bytecode for testing purposes
     */
    private byte[] createOriginalBytecode(String className) {
        String content = String.format("MOCK_BYTECODE:%s:version:1:fields:name,id", className);
        return content.getBytes();
    }

    /**
     * Creates incompatible bytecode for testing purposes
     */
    private byte[] createIncompatibleBytecode(String className) {
        String content = String.format("INCOMPATIBLE_BYTECODE:%s:version:2:fields:name,id,newField:schema:changed", className);
        return content.getBytes();
    }

    /**
     * Creates schema change bytecode for testing purposes
     */
    private byte[] createSchemaChangeBytecode(String className) {
        String content = String.format("SCHEMA_CHANGE_BYTECODE:%s:version:2:fields:name,id,addedField:change:field_addition", className);
        return content.getBytes();
    }

    /**
     * Creates new bytecode for testing purposes
     */
    private byte[] createNewBytecode(String className) {
        String content = String.format("NEW_BYTECODE:%s:version:2:fields:name,id:enhancement:true", className);
        return content.getBytes();
    }
}