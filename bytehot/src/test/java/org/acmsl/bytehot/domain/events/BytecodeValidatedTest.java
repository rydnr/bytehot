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
 * Filename: BytecodeValidatedTest.java
 *
 * Author: Claude Code
 *
 * Class name: BytecodeValidatedTest
 *
 * Responsibilities:
 *   - Test BytecodeValidated event when bytecode passes validation checks
 *
 * Collaborators:
 *   - BytecodeValidated: Domain event for validated bytecode
 *   - BytecodeValidator: Validates bytecode for hot-swap compatibility
 */
package org.acmsl.bytehot.domain.events;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test BytecodeValidated event when bytecode passes validation checks
 * @author Claude Code
 * @since 2025-06-16
 */
public class BytecodeValidatedTest {

    /**
     * Tests that validating compatible bytecode triggers BytecodeValidated event
     */
    @Test
    public void compatible_bytecode_triggers_validated_event(@TempDir Path tempDir) throws IOException, org.acmsl.bytehot.domain.BytecodeValidationException {
        // Given: A .class file with compatible bytecode changes
        Path classFile = tempDir.resolve("CompatibleClass.class");
        byte[] compatibleBytecode = createCompatibleBytecode("CompatibleClass");
        Files.write(classFile, compatibleBytecode);
        
        // When: BytecodeValidator validates the bytecode
        org.acmsl.bytehot.domain.BytecodeValidator validator = new org.acmsl.bytehot.domain.BytecodeValidator();
        BytecodeValidated event = validator.validate(classFile);
        
        // Then: BytecodeValidated event should be triggered
        assertNotNull(event, "BytecodeValidated event should not be null");
        assertEquals(classFile, event.getClassFile(), "Event should contain the validated class file path");
        assertEquals("CompatibleClass", event.getClassName(), "Event should contain the correct class name");
        assertTrue(event.isValidForHotSwap(), "Event should indicate bytecode is valid for hot-swap");
        assertNotNull(event.getValidationDetails(), "Event should contain validation details");
        assertTrue(event.getValidationDetails().contains("method body changes"), "Validation details should mention method body changes");
        assertNotNull(event.getTimestamp(), "Event should have a timestamp");
    }

    /**
     * Tests that validating method body changes passes validation
     */
    @Test
    public void method_body_change_passes_validation(@TempDir Path tempDir) throws IOException, org.acmsl.bytehot.domain.BytecodeValidationException {
        // Given: A .class file with only method body changes (safe for hot-swap)
        Path classFile = tempDir.resolve("MethodBodyChange.class");
        byte[] methodBodyChangeBytecode = createMethodBodyChangeBytecode("MethodBodyChange");
        Files.write(classFile, methodBodyChangeBytecode);
        
        // When: BytecodeValidator validates the bytecode
        org.acmsl.bytehot.domain.BytecodeValidator validator = new org.acmsl.bytehot.domain.BytecodeValidator();
        BytecodeValidated event = validator.validate(classFile);
        
        // Then: BytecodeValidated event should indicate method body changes are valid
        assertNotNull(event, "BytecodeValidated event should not be null");
        assertEquals("MethodBodyChange", event.getClassName(), "Event should contain the correct class name");
        assertTrue(event.isValidForHotSwap(), "Method body changes should be valid for hot-swap");
        assertTrue(event.getValidationDetails().contains("method body changes"), "Should mention method body changes");
    }

    /**
     * Creates compatible bytecode for testing purposes
     */
    private byte[] createCompatibleBytecode(String className) {
        // Mock bytecode format indicating compatible changes
        String content = String.format("COMPATIBLE_BYTECODE:%s:changes:method_body_only", className);
        return content.getBytes();
    }

    /**
     * Creates bytecode with method body changes for testing purposes
     */
    private byte[] createMethodBodyChangeBytecode(String className) {
        // Mock bytecode format indicating method body changes
        String content = String.format("COMPATIBLE_BYTECODE:%s:changes:method_body_changes:methods:someMethod", className);
        return content.getBytes();
    }
}