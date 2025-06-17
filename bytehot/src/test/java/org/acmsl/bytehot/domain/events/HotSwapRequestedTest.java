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
 * Filename: HotSwapRequestedTest.java
 *
 * Author: Claude Code
 *
 * Class name: HotSwapRequestedTest
 *
 * Responsibilities:
 *   - Test HotSwapRequested event when hot-swap operation is initiated
 *
 * Collaborators:
 *   - HotSwapRequested: Domain event for hot-swap initiation
 *   - HotSwapManager: Coordinates hot-swap operations
 */
package org.acmsl.bytehot.domain.events;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test HotSwapRequested event when hot-swap operation is initiated
 * @author Claude Code
 * @since 2025-06-17
 */
public class HotSwapRequestedTest {

    /**
     * Tests that requesting hot-swap after successful validation triggers HotSwapRequested event
     */
    @Test
    public void validated_bytecode_triggers_hotswap_request(@TempDir Path tempDir) throws IOException {
        // Given: A successfully validated bytecode change
        Path classFile = tempDir.resolve("UserService.class");
        byte[] originalBytecode = createOriginalBytecode("UserService");
        byte[] newBytecode = createNewBytecode("UserService");
        Files.write(classFile, newBytecode);
        
        BytecodeValidated validation = new BytecodeValidated(
            classFile,
            "UserService",
            true,
            "Method body changes validated successfully",
            java.time.Instant.now()
        );
        
        // When: HotSwapManager requests hot-swap
        org.acmsl.bytehot.domain.HotSwapManager manager = new org.acmsl.bytehot.domain.HotSwapManager();
        HotSwapRequested event = manager.requestHotSwap(classFile, validation, originalBytecode);
        
        // Then: HotSwapRequested event should contain all necessary information
        assertNotNull(event, "HotSwapRequested event should not be null");
        assertEquals(classFile, event.getClassFile(), "Event should contain the class file path");
        assertEquals("UserService", event.getClassName(), "Event should contain the correct class name");
        assertArrayEquals(originalBytecode, event.getOriginalBytecode(), "Event should contain original bytecode");
        assertArrayEquals(newBytecode, event.getNewBytecode(), "Event should contain new bytecode");
        assertEquals("Bytecode validation passed - initiating hot-swap", event.getRequestReason(), 
            "Event should contain request reason");
        assertNotNull(event.getTimestamp(), "Event should have a timestamp");
    }

    /**
     * Tests that hot-swap request includes validation context
     */
    @Test
    public void hotswap_request_includes_validation_context(@TempDir Path tempDir) throws IOException {
        // Given: A validation event with specific details
        Path classFile = tempDir.resolve("ComplexService.class");
        byte[] originalBytecode = createComplexOriginalBytecode("ComplexService");
        byte[] newBytecode = createComplexNewBytecode("ComplexService");
        Files.write(classFile, newBytecode);
        
        BytecodeValidated validation = new BytecodeValidated(
            classFile,
            "ComplexService",
            true,
            "Method body changes in calculateResult() method validated",
            java.time.Instant.now()
        );
        
        // When: HotSwapManager creates request with validation context
        org.acmsl.bytehot.domain.HotSwapManager manager = new org.acmsl.bytehot.domain.HotSwapManager();
        HotSwapRequested event = manager.requestHotSwap(classFile, validation, originalBytecode);
        
        // Then: Request should include validation details
        assertEquals("ComplexService", event.getClassName(), "Should preserve class name from validation");
        assertTrue(event.getRequestReason().contains("validation passed"), 
            "Request reason should reference successful validation");
        assertTrue(event.getNewBytecode().length > event.getOriginalBytecode().length,
            "New bytecode should be different from original");
        
        // Verify bytecode integrity
        String newContent = new String(event.getNewBytecode());
        assertTrue(newContent.contains("ComplexService"), "New bytecode should contain class name");
        assertTrue(newContent.contains("enhanced"), "New bytecode should contain modifications");
    }

    /**
     * Tests that request reason reflects validation details
     */
    @Test
    public void request_reason_reflects_validation_details(@TempDir Path tempDir) throws IOException {
        // Given: Validation with specific method body changes
        Path classFile = tempDir.resolve("TestService.class");
        Files.write(classFile, createMethodBodyChangeBytecode("TestService"));
        
        BytecodeValidated validation = new BytecodeValidated(
            classFile,
            "TestService", 
            true,
            "Method body changes detected in processData() method",
            java.time.Instant.now()
        );
        
        // When: Creating hot-swap request
        org.acmsl.bytehot.domain.HotSwapManager manager = new org.acmsl.bytehot.domain.HotSwapManager();
        HotSwapRequested event = manager.requestHotSwap(classFile, validation, createOriginalBytecode("TestService"));
        
        // Then: Request reason should be descriptive
        String reason = event.getRequestReason();
        assertTrue(reason.contains("validation passed"), "Should mention validation success");
        assertEquals("TestService", event.getClassName(), "Should preserve class name");
        assertTrue(event.getNewBytecode().length > 0, "Should have new bytecode");
        assertTrue(event.getOriginalBytecode().length > 0, "Should have original bytecode");
    }

    /**
     * Creates original bytecode for testing purposes
     */
    private byte[] createOriginalBytecode(String className) {
        String content = String.format("ORIGINAL_BYTECODE:%s:methods:getValue,processData", className);
        return content.getBytes();
    }

    /**
     * Creates new bytecode for testing purposes
     */
    private byte[] createNewBytecode(String className) {
        String content = String.format("NEW_BYTECODE:%s:methods:getValue,processData:enhanced:true", className);
        return content.getBytes();
    }

    /**
     * Creates complex original bytecode for testing purposes
     */
    private byte[] createComplexOriginalBytecode(String className) {
        String content = String.format("ORIGINAL_BYTECODE:%s:methods:calculateResult,processInput,formatOutput:complexity:high", className);
        return content.getBytes();
    }

    /**
     * Creates complex new bytecode for testing purposes
     */
    private byte[] createComplexNewBytecode(String className) {
        String content = String.format("NEW_BYTECODE:%s:methods:calculateResult,processInput,formatOutput:complexity:high:enhanced:true:optimized:true", className);
        return content.getBytes();
    }

    /**
     * Creates bytecode with method body changes for testing purposes
     */
    private byte[] createMethodBodyChangeBytecode(String className) {
        String content = String.format("NEW_BYTECODE:%s:methods:processData:changes:method_body_only", className);
        return content.getBytes();
    }
}