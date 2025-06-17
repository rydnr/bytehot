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
 * Filename: ClassFileCreatedTest.java
 *
 * Author: Claude Code
 *
 * Class name: ClassFileCreatedTest
 *
 * Responsibilities:
 *   - Test ClassFileCreated event when new .class file appears
 *
 * Collaborators:
 *   - ClassFileCreated: Domain event for new .class files
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
 * Test ClassFileCreated event when new .class file appears
 * @author Claude Code
 * @since 2025-06-16
 */
public class ClassFileCreatedTest {

    /**
     * Tests that creating a new .class file triggers ClassFileCreated event
     */
    @Test
    public void creating_new_class_file_triggers_created_event(@TempDir Path tempDir) throws IOException, InterruptedException {
        // Given: An empty watched directory
        Path watchDir = tempDir;
        CountDownLatch eventReceived = new CountDownLatch(1);
        AtomicReference<ClassFileCreated> capturedEvent = new AtomicReference<>();

        // When: A ClassFileWatcher monitors the directory
        org.acmsl.bytehot.domain.ClassFileWatcher watcher = 
            new org.acmsl.bytehot.domain.ClassFileWatcher(watchDir, 100);
        
        Thread watchThread = new Thread(() -> {
            try {
                watcher.watchClassFiles(event -> {
                    if (event instanceof ClassFileCreated) {
                        capturedEvent.set((ClassFileCreated) event);
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

        // Create a new .class file (simulate compilation)
        Path newClassFile = tempDir.resolve("NewClass.class");
        byte[] bytecode = createSimpleClassBytecode("NewClass");
        Files.write(newClassFile, bytecode);

        // Then: ClassFileCreated event should be triggered
        assertTrue(eventReceived.await(3, TimeUnit.SECONDS), 
            "Should receive ClassFileCreated event within 3 seconds");
        
        ClassFileCreated event = capturedEvent.get();
        assertNotNull(event, "ClassFileCreated event should not be null");
        assertEquals(newClassFile, event.getClassFile(), "Event should contain the new class file path");
        assertEquals("NewClass", event.getClassName(), "Event should contain the correct class name");
        assertTrue(event.getFileSize() > 0, "Event should contain file size information");
        assertNotNull(event.getTimestamp(), "Event should have a timestamp");

        watchThread.interrupt();
    }

    /**
     * Tests that multiple .class files created simultaneously are all detected
     */
    @Test
    public void multiple_class_files_creation_detected(@TempDir Path tempDir) throws IOException, InterruptedException {
        // Given: An empty watched directory
        CountDownLatch eventsReceived = new CountDownLatch(3); // Expect 3 events
        AtomicReference<String> capturedClassNames = new AtomicReference<>("");

        // When: A ClassFileWatcher monitors the directory
        org.acmsl.bytehot.domain.ClassFileWatcher watcher = 
            new org.acmsl.bytehot.domain.ClassFileWatcher(tempDir, 100);
        
        Thread watchThread = new Thread(() -> {
            try {
                watcher.watchClassFiles(event -> {
                    if (event instanceof ClassFileCreated) {
                        ClassFileCreated created = (ClassFileCreated) event;
                        capturedClassNames.updateAndGet(names -> names + created.getClassName() + ",");
                        eventsReceived.countDown();
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

        // Create multiple .class files
        Files.write(tempDir.resolve("ClassA.class"), createSimpleClassBytecode("ClassA"));
        Files.write(tempDir.resolve("ClassB.class"), createSimpleClassBytecode("ClassB"));
        Files.write(tempDir.resolve("ClassC.class"), createSimpleClassBytecode("ClassC"));

        // Then: All ClassFileCreated events should be received
        assertTrue(eventsReceived.await(3, TimeUnit.SECONDS), 
            "Should receive all 3 ClassFileCreated events within 3 seconds");
        
        String allClassNames = capturedClassNames.get();
        assertTrue(allClassNames.contains("ClassA"), "Should detect ClassA creation");
        assertTrue(allClassNames.contains("ClassB"), "Should detect ClassB creation");
        assertTrue(allClassNames.contains("ClassC"), "Should detect ClassC creation");

        watchThread.interrupt();
    }

    /**
     * Creates simple bytecode for testing purposes
     */
    private byte[] createSimpleClassBytecode(String className) {
        // This is a simplified mock bytecode - in real implementation we'd use ASM or similar
        String content = "MOCK_BYTECODE_FOR_" + className;
        return content.getBytes();
    }
}