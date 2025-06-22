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
 * Filename: ClassMetadataExtractedTest.java
 *
 * Author: Claude Code
 *
 * Class name: ClassMetadataExtractedTest
 *
 * Responsibilities:
 *   - Test ClassMetadataExtracted event when class information is parsed
 *
 * Collaborators:
 *   - ClassMetadataExtracted: Domain event for extracted class metadata
 *   - BytecodeAnalyzer: Analyzes bytecode and extracts metadata
 */
package org.acmsl.bytehot.domain.events;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test ClassMetadataExtracted event when class information is parsed
 * @author Claude Code
 * @since 2025-06-16
 */
public class ClassMetadataExtractedTest {

    /**
     * Tests that analyzing a valid .class file triggers ClassMetadataExtracted event
     */
    @Test
    public void valid_class_file_triggers_metadata_extracted_event(@TempDir Path tempDir) throws IOException {
        // Given: A valid .class file
        Path classFile = tempDir.resolve("TestClass.class");
        byte[] validBytecode = createValidClassBytecode("TestClass", "java.lang.Object");
        Files.write(classFile, validBytecode);
        
        // When: BytecodeAnalyzer analyzes the class file
        org.acmsl.bytehot.domain.BytecodeAnalyzer analyzer = new org.acmsl.bytehot.domain.BytecodeAnalyzer();
        ClassMetadataExtracted event = analyzer.extractMetadata(classFile);
        
        // Then: ClassMetadataExtracted event should contain class information
        assertNotNull(event, "ClassMetadataExtracted event should not be null");
        assertEquals(classFile, event.getClassFile(), "Event should contain the analyzed class file path");
        assertEquals("TestClass", event.getClassName(), "Event should contain the correct class name");
        assertEquals("java.lang.Object", event.getSuperClassName(), "Event should contain the superclass name");
        assertTrue(event.getInterfaces().isEmpty(), "Event should contain empty interfaces list for simple class");
        assertTrue(event.getMethods().size() >= 1, "Event should contain at least constructor method");
        assertTrue(event.getFields().isEmpty(), "Event should contain empty fields list for simple class");
        assertNotNull(event.getTimestamp(), "Event should have a timestamp");
    }

    /**
     * Tests that analyzing a class with interfaces and fields extracts complete metadata
     */
    @Test
    public void complex_class_file_extracts_complete_metadata(@TempDir Path tempDir) throws IOException {
        // Given: A complex .class file with interfaces and fields
        Path classFile = tempDir.resolve("ComplexClass.class");
        byte[] complexBytecode = createComplexClassBytecode("ComplexClass", 
            "java.lang.Object", 
            List.of("java.io.Serializable", "java.lang.Runnable"),
            List.of("privateField", "publicField"),
            List.of("<init>", "run", "toString"));
        Files.write(classFile, complexBytecode);
        
        // When: BytecodeAnalyzer analyzes the complex class file
        org.acmsl.bytehot.domain.BytecodeAnalyzer analyzer = new org.acmsl.bytehot.domain.BytecodeAnalyzer();
        ClassMetadataExtracted event = analyzer.extractMetadata(classFile);
        
        // Then: ClassMetadataExtracted event should contain complete class information
        assertNotNull(event, "ClassMetadataExtracted event should not be null");
        assertEquals("ComplexClass", event.getClassName(), "Event should contain the correct class name");
        assertEquals("java.lang.Object", event.getSuperClassName(), "Event should contain the superclass name");
        assertEquals(2, event.getInterfaces().size(), "Event should contain interfaces");
        assertTrue(event.getInterfaces().contains("java.io.Serializable"), "Event should contain Serializable interface");
        assertTrue(event.getInterfaces().contains("java.lang.Runnable"), "Event should contain Runnable interface");
        assertEquals(2, event.getFields().size(), "Event should contain fields");
        assertTrue(event.getFields().contains("privateField"), "Event should contain privateField");
        assertTrue(event.getFields().contains("publicField"), "Event should contain publicField");
        assertEquals(3, event.getMethods().size(), "Event should contain methods");
        assertTrue(event.getMethods().contains("<init>"), "Event should contain constructor");
        assertTrue(event.getMethods().contains("run"), "Event should contain run method");
        assertTrue(event.getMethods().contains("toString"), "Event should contain toString method");
    }

    /**
     * Creates simple valid bytecode for testing purposes
     */
    private byte[] createValidClassBytecode(String className, String superClassName) {
        // This is a simplified mock bytecode - in real implementation we'd use ASM or similar
        String content = String.format("VALID_BYTECODE:%s:extends:%s:interfaces::fields::methods:<init>", 
            className, superClassName);
        return content.getBytes();
    }

    /**
     * Creates complex bytecode for testing purposes
     */
    private byte[] createComplexClassBytecode(String className, String superClassName, 
                                            List<String> interfaces, List<String> fields, List<String> methods) {
        String content = String.format("VALID_BYTECODE:%s:extends:%s:interfaces:%s:fields:%s:methods:%s", 
            className, 
            superClassName,
            String.join(",", interfaces),
            String.join(",", fields),
            String.join(",", methods));
        return content.getBytes();
    }
}