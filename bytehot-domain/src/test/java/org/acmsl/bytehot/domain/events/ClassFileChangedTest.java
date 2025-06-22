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
 * Filename: ClassFileChangedTest.java
 *
 * Author: Claude Code
 *
 * Class name: ClassFileChangedTest
 *
 * Responsibilities:
 *   - Test ClassFileChanged event when .class file is modified on disk
 *
 * Collaborators:
 *   - ClassFileChanged: Domain event for .class file modifications
 *   - ClassFileWatcher: Watches for .class file changes
 */
package org.acmsl.bytehot.domain.events;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test ClassFileChanged event when .class file is modified on disk
 * @author Claude Code
 * @since 2025-06-16
 */
public class ClassFileChangedTest {

    /**
     * Tests that modifying a .class file triggers ClassFileChanged event
     */
    @Test
    public void modifying_class_file_triggers_changed_event(@TempDir Path tempDir) throws IOException, InterruptedException {
        // Given: A .class file in a watched directory
        Path classFile = tempDir.resolve("TestClass.class");
        byte[] originalBytecode = createSimpleClassBytecode("TestClass");
        Files.write(classFile, originalBytecode);

        Path watchDir = tempDir;
        CountDownLatch eventReceived = new CountDownLatch(1);
        AtomicReference<ClassFileChanged> capturedEvent = new AtomicReference<>();

        // When: A ClassFileWatcher monitors the directory
        org.acmsl.bytehot.domain.ClassFileWatcher watcher = 
            new org.acmsl.bytehot.domain.ClassFileWatcher(watchDir, 100);
        
        Thread watchThread = new Thread(() -> {
            try {
                watcher.watchClassFiles(event -> {
                    if (event instanceof ClassFileChanged) {
                        capturedEvent.set((ClassFileChanged) event);
                        eventReceived.countDown();
                    }
                });
            } catch (IOException e) {
                // Test failure
            }
        });
        watchThread.setDaemon(true);
        watchThread.start();

        // Give watcher time to initialize
        Thread.sleep(200);

        // Modify the .class file (simulate recompilation)
        byte[] modifiedBytecode = createSimpleClassBytecode("TestClass", "newMethod");
        Files.write(classFile, modifiedBytecode);

        // Then: ClassFileChanged event should be triggered
        assertTrue(eventReceived.await(3, TimeUnit.SECONDS), 
            "Should receive ClassFileChanged event within 3 seconds");
        
        ClassFileChanged event = capturedEvent.get();
        assertNotNull(event, "ClassFileChanged event should not be null");
        assertEquals(classFile, event.getClassFile(), "Event should contain the modified class file path");
        assertEquals("TestClass", event.getClassName(), "Event should contain the correct class name");
        assertTrue(event.getFileSize() > 0, "Event should contain file size information");
        assertNotNull(event.getTimestamp(), "Event should have a timestamp");

        watchThread.interrupt();
    }

    /**
     * Tests that non-.class files are ignored by ClassFileWatcher
     */
    @Test
    public void non_class_files_are_ignored(@TempDir Path tempDir) throws IOException, InterruptedException {
        // Given: A directory with various file types
        Path javaFile = tempDir.resolve("TestClass.java");
        Path txtFile = tempDir.resolve("readme.txt");
        Files.writeString(javaFile, "public class TestClass {}");
        Files.writeString(txtFile, "readme content");

        CountDownLatch eventReceived = new CountDownLatch(1);

        // When: A ClassFileWatcher monitors the directory
        org.acmsl.bytehot.domain.ClassFileWatcher watcher = 
            new org.acmsl.bytehot.domain.ClassFileWatcher(tempDir, 100);
        
        Thread watchThread = new Thread(() -> {
            try {
                watcher.watchClassFiles(event -> {
                    // Should not receive any events for non-.class files
                    eventReceived.countDown();
                });
            } catch (IOException e) {
                // Test failure
            }
        });
        watchThread.setDaemon(true);
        watchThread.start();

        // Give watcher time to initialize
        Thread.sleep(200);

        // Modify non-.class files
        Files.writeString(javaFile, "public class TestClass { void newMethod() {} }");
        Files.writeString(txtFile, "updated readme content");

        // Then: No events should be received
        boolean eventWasReceived = eventReceived.await(1, TimeUnit.SECONDS);
        assertTrue(!eventWasReceived, "Should not receive events for non-.class files");

        watchThread.interrupt();
    }

    /**
     * Creates simple bytecode for testing purposes
     */
    private byte[] createSimpleClassBytecode(String className) {
        return createSimpleClassBytecode(className, null);
    }

    /**
     * Creates simple bytecode with optional additional method for testing
     */
    private byte[] createSimpleClassBytecode(String className, String additionalMethod) {
        // This is a simplified mock bytecode - in real implementation we'd use ASM or similar
        String content = "MOCK_BYTECODE_FOR_" + className;
        if (additionalMethod != null) {
            content += "_WITH_" + additionalMethod;
        }
        return content.getBytes();
    }
}