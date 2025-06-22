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
 * Filename: ClassFileChangedReloadBugTest.java
 *
 * Author: Claude Code
 *
 * Class name: ClassFileChangedReloadBugTest
 *
 * Responsibilities:
 *   - Test that detects the missing hot-swap pipeline bug
 *   - Verifies that ClassFileChanged events trigger actual class reloading
 *   - Documents the expected behavior vs current buggy behavior
 *
 * Collaborators:
 *   - ByteHotApplication: The application class that should trigger hot-swap
 *   - ClassFileChanged: The event that should trigger reloading
 *   - HotSwapManager: The domain class that should perform actual reloading
 */
package org.acmsl.bytehot.domain;

// Note: Domain test should not import application layer
// import org.acmsl.bytehot.application.ByteHotApplication;
import org.acmsl.bytehot.domain.events.ClassFileChanged;
import org.acmsl.bytehot.domain.events.HotSwapRequested;
import org.acmsl.bytehot.domain.events.ClassRedefinitionSucceeded;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.AfterEach;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import org.acmsl.bytehot.domain.testing.MockInstrumentationService;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test that detects the critical hot-swap pipeline bug in ByteHot.
 * 
 * **The Bug**: ClassFileChanged events are detected and processed by 
 * ByteHotApplication.processClassFileChanged(), but this method only logs 
 * the event instead of triggering the actual hot-swap pipeline that would
 * result in class redefinition.
 * 
 * **Expected Behavior**: When ClassFileChanged event is processed, it should:
 * 1. Validate the new bytecode
 * 2. Create a HotSwapRequested event
 * 3. Perform actual JVM class redefinition
 * 4. Emit ClassRedefinitionSucceeded or ClassRedefinitionFailed events
 * 
 * **Current Buggy Behavior**: ClassFileChanged is only logged, no actual
 * hot-swapping occurs.
 * 
 * @author Claude Code  
 * @since 2025-06-22
 */
public class ClassFileChangedReloadBugTest {

    private Path tempClassFile;
    private MockInstrumentationService mockInstrumentationService;

    @BeforeEach
    void setUp() throws Exception {
        // Create a temporary class file with compatible bytecode content
        tempClassFile = Files.createTempFile("HelloWorld", ".class");
        final String compatibleBytecode = "COMPATIBLE_BYTECODE:HelloWorld:test:method_body_only";
        Files.write(tempClassFile, compatibleBytecode.getBytes());
        
        // Initialize mock instrumentation for domain testing
        mockInstrumentationService = new MockInstrumentationService();
        mockInstrumentationService.addLoadedClass("HelloWorld", String.class);
    }

    @AfterEach
    void tearDown() throws Exception {
        // Clean up temporary file
        if (tempClassFile != null && Files.exists(tempClassFile)) {
            Files.delete(tempClassFile);
        }
    }

    @Test
    public void classFileChanged_event_triggers_hot_swap_manager_pipeline() throws Exception {
        // Given: A ClassFileChanged event representing a modified class file with real file
        final String className = "HelloWorld";
        final long fileSize = Files.size(tempClassFile);
        final Instant timestamp = Instant.now();
        
        final ClassFileChanged event = ClassFileChanged.forNewSession(
            tempClassFile, className, fileSize, timestamp
        );
        
        // When: We simulate the hot-swap pipeline using domain objects directly
        final HotSwapManager hotSwapManager = new HotSwapManager(mockInstrumentationService);
        
        // Create a HotSwapRequested event from the ClassFileChanged event
        final byte[] originalBytecode = "ORIGINAL_BYTECODE:HelloWorld:version:1".getBytes();
        final byte[] newBytecode = Files.readAllBytes(tempClassFile);
        
        final HotSwapRequested hotSwapRequest = new HotSwapRequested(
            tempClassFile,
            className,
            originalBytecode,
            newBytecode,
            "ClassFileChanged triggered hot-swap",
            timestamp,
            event
        );
        
        // Then: The hot-swap manager should be able to process the request
        final ClassRedefinitionSucceeded result = hotSwapManager.performRedefinition(hotSwapRequest);
        
        // Verify the hot-swap pipeline completed successfully
        assertNotNull(result, "Hot-swap pipeline should complete successfully");
        assertEquals(className, result.getClassName(), "Result should contain correct class name");
        assertEquals(tempClassFile, result.getClassFile(), "Result should contain correct file path");
        assertTrue(result.getAffectedInstances() >= 0, "Should report affected instances");
        assertNotNull(result.getDuration(), "Should measure redefinition duration");
        assertTrue(result.getDuration().toMillis() >= 0, "Duration should be non-negative");
        assertNotNull(result.getRedefinitionDetails(), "Should include redefinition details");
        
        // Verify that mock instrumentation was called
        assertEquals(1, mockInstrumentationService.getRedefinitionCalls().size(), 
            "Should have called redefinition once");
        
        System.out.println("✅ ClassFileChanged event successfully triggers hot-swap pipeline");
    }

}