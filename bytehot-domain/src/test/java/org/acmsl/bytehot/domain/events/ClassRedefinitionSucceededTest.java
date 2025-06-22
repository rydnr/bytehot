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
 * Filename: ClassRedefinitionSucceededTest.java
 *
 * Author: Claude Code
 *
 * Class name: ClassRedefinitionSucceededTest
 *
 * Responsibilities:
 *   - Test ClassRedefinitionSucceeded event when JVM successfully redefines a class
 *
 * Collaborators:
 *   - ClassRedefinitionSucceeded: Domain event for successful redefinition
 *   - HotSwapManager: Performs JVM class redefinition
 */
package org.acmsl.bytehot.domain.events;

import org.acmsl.bytehot.domain.events.ClassFileChanged;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test ClassRedefinitionSucceeded event when JVM successfully redefines a class
 * @author Claude Code
 * @since 2025-06-17
 */
public class ClassRedefinitionSucceededTest {

    /**
     * Tests that successful JVM redefinition triggers ClassRedefinitionSucceeded event
     */
    @Test
    @Disabled("TODO: Fix architecture - test requires proper InstrumentationPort configuration")
    public void successful_jvm_redefinition_triggers_succeeded_event(@TempDir Path tempDir) 
            throws IOException, org.acmsl.bytehot.domain.HotSwapException {
        // Given: A hot-swap request ready for JVM redefinition
        Path classFile = tempDir.resolve("UserService.class");
        Files.write(classFile, createNewBytecode("UserService"));
        
        HotSwapRequested request = new HotSwapRequested(
            classFile,
            "UserService",
            createOriginalBytecode("UserService"),
            createNewBytecode("UserService"),
            "Validated bytecode ready for redefinition",
            java.time.Instant.now(),
            ClassFileChanged.forNewSession(classFile, "UserService", createNewBytecode("UserService").length, java.time.Instant.now())
        );
        
        // When: HotSwapManager performs successful redefinition
        org.acmsl.bytehot.domain.HotSwapManager manager = new org.acmsl.bytehot.domain.HotSwapManager();
        ClassRedefinitionSucceeded event = manager.performRedefinition(request);
        
        // Then: ClassRedefinitionSucceeded event should contain redefinition details
        assertNotNull(event, "ClassRedefinitionSucceeded event should not be null");
        assertEquals("UserService", event.getClassName(), "Event should contain the correct class name");
        assertEquals(classFile, event.getClassFile(), "Event should contain the class file path");
        assertTrue(event.getAffectedInstances() >= 0, "Event should contain affected instances count");
        assertNotNull(event.getRedefinitionDetails(), "Event should contain redefinition details");
        assertTrue(event.getRedefinitionDetails().contains("successfully"), 
            "Details should indicate success");
        assertNotNull(event.getDuration(), "Event should contain redefinition duration");
        assertTrue(event.getDuration().toMillis() >= 0, "Duration should be non-negative");
        assertNotNull(event.getTimestamp(), "Event should have a timestamp");
    }

    /**
     * Tests that redefinition details include performance metrics
     */
    @Test
    @Disabled("TODO: Fix architecture - test requires proper InstrumentationPort configuration")
    public void redefinition_success_includes_performance_metrics(@TempDir Path tempDir) 
            throws IOException, org.acmsl.bytehot.domain.HotSwapException {
        // Given: A complex class hot-swap request
        Path classFile = tempDir.resolve("ComplexService.class");
        Files.write(classFile, createComplexBytecode("ComplexService"));
        
        HotSwapRequested request = new HotSwapRequested(
            classFile,
            "ComplexService",
            createOriginalBytecode("ComplexService"),
            createComplexBytecode("ComplexService"),
            "Complex class validation passed",
            java.time.Instant.now(),
            ClassFileChanged.forNewSession(classFile, "ComplexService", createComplexBytecode("ComplexService").length, java.time.Instant.now())
        );
        
        // When: Performing redefinition with multiple instances
        org.acmsl.bytehot.domain.HotSwapManager manager = new org.acmsl.bytehot.domain.HotSwapManager();
        ClassRedefinitionSucceeded event = manager.performRedefinition(request);
        
        // Then: Event should include performance and instance metrics
        assertEquals("ComplexService", event.getClassName(), "Should preserve class name");
        assertTrue(event.getAffectedInstances() >= 0, "Should report affected instances");
        assertTrue(event.getDuration().compareTo(Duration.ZERO) >= 0, "Duration should be measured");
        
        String details = event.getRedefinitionDetails();
        assertTrue(details.contains("redefinition"), "Details should mention redefinition");
        assertTrue(details.length() > 10, "Details should be descriptive");
    }

    /**
     * Tests that redefinition success includes instance count information
     */
    @Test
    @Disabled("TODO: Fix architecture - test requires proper InstrumentationPort configuration")
    public void redefinition_success_reports_instance_information(@TempDir Path tempDir) 
            throws IOException, org.acmsl.bytehot.domain.HotSwapException {
        // Given: A service class with known instances
        Path classFile = tempDir.resolve("ServiceWithInstances.class");
        Files.write(classFile, createServiceBytecode("ServiceWithInstances"));
        
        HotSwapRequested request = new HotSwapRequested(
            classFile,
            "ServiceWithInstances",
            createOriginalBytecode("ServiceWithInstances"),
            createServiceBytecode("ServiceWithInstances"),
            "Service class redefinition requested",
            java.time.Instant.now(),
            ClassFileChanged.forNewSession(classFile, "ServiceWithInstances", createServiceBytecode("ServiceWithInstances").length, java.time.Instant.now())
        );
        
        // When: Redefinition affects multiple instances
        org.acmsl.bytehot.domain.HotSwapManager manager = new org.acmsl.bytehot.domain.HotSwapManager();
        ClassRedefinitionSucceeded event = manager.performRedefinition(request);
        
        // Then: Event should report instance information
        assertEquals("ServiceWithInstances", event.getClassName(), "Should have correct class name");
        assertTrue(event.getAffectedInstances() >= 0, "Affected instances should be non-negative");
        
        String details = event.getRedefinitionDetails();
        assertTrue(details.contains("redefinition") || details.contains("successful"), 
            "Details should indicate operation type and status");
    }

    /**
     * Creates original bytecode for testing purposes
     */
    private byte[] createOriginalBytecode(String className) {
        String content = String.format("MOCK_BYTECODE:%s:version:1:methods:getValue,processData", className);
        return content.getBytes();
    }

    /**
     * Creates new bytecode for testing purposes
     */
    private byte[] createNewBytecode(String className) {
        String content = String.format("MOCK_BYTECODE:%s:version:2:methods:getValue,processData:enhanced:true", className);
        return content.getBytes();
    }

    /**
     * Creates complex bytecode for testing purposes
     */
    private byte[] createComplexBytecode(String className) {
        String content = String.format("MOCK_BYTECODE:%s:version:2:methods:calculate,process,validate,transform:complexity:high", className);
        return content.getBytes();
    }

    /**
     * Creates service bytecode for testing purposes
     */
    private byte[] createServiceBytecode(String className) {
        String content = String.format("MOCK_BYTECODE:%s:version:2:methods:doWork,cleanup:instances:multiple", className);
        return content.getBytes();
    }
}