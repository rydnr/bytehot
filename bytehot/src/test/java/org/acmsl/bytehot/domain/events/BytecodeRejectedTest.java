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
 * Filename: BytecodeRejectedTest.java
 *
 * Author: Claude Code
 *
 * Class name: BytecodeRejectedTest
 *
 * Responsibilities:
 *   - Test BytecodeRejected event when bytecode fails validation checks
 *
 * Collaborators:
 *   - BytecodeRejected: Domain event for rejected bytecode
 *   - BytecodeValidator: Validates bytecode for hot-swap compatibility
 */
package org.acmsl.bytehot.domain.events;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test BytecodeRejected event when bytecode fails validation checks
 * @author Claude Code
 * @since 2025-06-16
 */
public class BytecodeRejectedTest {

    /**
     * Tests that validating incompatible bytecode triggers BytecodeRejected event
     */
    @Test
    public void incompatible_bytecode_triggers_rejected_event(@TempDir Path tempDir) throws IOException {
        // Given: A .class file with incompatible bytecode changes
        Path classFile = tempDir.resolve("IncompatibleClass.class");
        byte[] incompatibleBytecode = createIncompatibleBytecode("IncompatibleClass");
        Files.write(classFile, incompatibleBytecode);
        
        // When: BytecodeValidator attempts to validate the bytecode
        org.acmsl.bytehot.domain.BytecodeValidator validator = new org.acmsl.bytehot.domain.BytecodeValidator();
        
        // Then: BytecodeRejected event should be triggered via exception
        org.acmsl.bytehot.domain.BytecodeValidationException exception = 
            assertThrows(org.acmsl.bytehot.domain.BytecodeValidationException.class, 
                () -> validator.validate(classFile));
        
        BytecodeRejected event = exception.getRejectionEvent();
        assertNotNull(event, "BytecodeRejected event should not be null");
        assertEquals(classFile, event.getClassFile(), "Event should contain the rejected class file path");
        assertEquals("IncompatibleClass", event.getClassName(), "Event should contain the correct class name");
        assertFalse(event.isValidForHotSwap(), "Event should indicate bytecode is invalid for hot-swap");
        assertNotNull(event.getRejectionReason(), "Event should contain rejection reason");
        assertTrue(event.getRejectionReason().contains("schema changes"), "Should mention schema changes as reason");
        assertNotNull(event.getTimestamp(), "Event should have a timestamp");
    }

    /**
     * Tests that schema changes are rejected
     */
    @Test
    public void schema_changes_are_rejected(@TempDir Path tempDir) throws IOException {
        // Given: A .class file with schema changes (incompatible with hot-swap)
        Path classFile = tempDir.resolve("SchemaChange.class");
        byte[] schemaChangeBytecode = createSchemaChangeBytecode("SchemaChange");
        Files.write(classFile, schemaChangeBytecode);
        
        // When: BytecodeValidator attempts to validate the bytecode
        org.acmsl.bytehot.domain.BytecodeValidator validator = new org.acmsl.bytehot.domain.BytecodeValidator();
        
        // Then: BytecodeRejected event should indicate schema changes are not allowed
        org.acmsl.bytehot.domain.BytecodeValidationException exception = 
            assertThrows(org.acmsl.bytehot.domain.BytecodeValidationException.class, 
                () -> validator.validate(classFile));
        
        BytecodeRejected event = exception.getRejectionEvent();
        assertEquals("SchemaChange", event.getClassName(), "Event should contain the correct class name");
        assertFalse(event.isValidForHotSwap(), "Schema changes should be invalid for hot-swap");
        assertTrue(event.getRejectionReason().contains("field addition"), "Should mention field addition");
    }

    /**
     * Creates incompatible bytecode for testing purposes
     */
    private byte[] createIncompatibleBytecode(String className) {
        // Mock bytecode format indicating incompatible changes
        String content = String.format("INCOMPATIBLE_BYTECODE:%s:changes:schema_changes:reason:field_removal", className);
        return content.getBytes();
    }

    /**
     * Creates bytecode with schema changes for testing purposes
     */
    private byte[] createSchemaChangeBytecode(String className) {
        // Mock bytecode format indicating schema changes
        String content = String.format("INCOMPATIBLE_BYTECODE:%s:changes:schema_changes:reason:field_addition:fields:newField", className);
        return content.getBytes();
    }
}