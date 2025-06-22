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
 * Filename: ClassFileDeletedTest.java
 *
 * Author: Claude Code
 *
 * Class name: ClassFileDeletedTest
 *
 * Responsibilities:
 *   - Test ClassFileDeleted event when .class file is removed
 *
 * Collaborators:
 *   - ClassFileDeleted: Domain event for removed .class files
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
 * Test ClassFileDeleted event when .class file is removed
 * @author Claude Code
 * @since 2025-06-16
 */
public class ClassFileDeletedTest {

    /**
     * Tests that deleting a .class file triggers ClassFileDeleted event
     */
    @Test
    public void deleting_class_file_triggers_deleted_event(@TempDir Path tempDir) throws IOException, InterruptedException {
        // Given: A watched directory with an existing .class file
        Path classFile = tempDir.resolve("DeleteMe.class");
        byte[] bytecode = createSimpleClassBytecode("DeleteMe");
        Files.write(classFile, bytecode);
        
        CountDownLatch eventReceived = new CountDownLatch(1);
        AtomicReference<ClassFileDeleted> capturedEvent = new AtomicReference<>();

        // When: A ClassFileWatcher monitors the directory
        org.acmsl.bytehot.domain.ClassFileWatcher watcher = 
            new org.acmsl.bytehot.domain.ClassFileWatcher(tempDir, 100);
        
        Thread watchThread = new Thread(() -> {
            try {
                watcher.watchClassFiles(event -> {
                    if (event instanceof ClassFileDeleted) {
                        capturedEvent.set((ClassFileDeleted) event);
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

        // Delete the .class file
        Files.delete(classFile);

        // Then: ClassFileDeleted event should be triggered
        assertTrue(eventReceived.await(3, TimeUnit.SECONDS), 
            "Should receive ClassFileDeleted event within 3 seconds");
        
        ClassFileDeleted event = capturedEvent.get();
        assertNotNull(event, "ClassFileDeleted event should not be null");
        assertEquals(classFile, event.getClassFile(), "Event should contain the deleted class file path");
        assertEquals("DeleteMe", event.getClassName(), "Event should contain the correct class name");
        assertNotNull(event.getTimestamp(), "Event should have a timestamp");

        watchThread.interrupt();
    }

    /**
     * Tests that non-.class file deletions are ignored
     */
    @Test
    public void non_class_file_deletion_ignored(@TempDir Path tempDir) throws IOException, InterruptedException {
        // Given: A watched directory with a non-.class file
        Path txtFile = tempDir.resolve("not-a-class.txt");
        Files.write(txtFile, "This is not a class file".getBytes());
        
        CountDownLatch eventReceived = new CountDownLatch(1);
        
        // When: A ClassFileWatcher monitors the directory
        org.acmsl.bytehot.domain.ClassFileWatcher watcher = 
            new org.acmsl.bytehot.domain.ClassFileWatcher(tempDir, 100);
        
        Thread watchThread = new Thread(() -> {
            try {
                watcher.watchClassFiles(event -> {
                    if (event instanceof ClassFileDeleted) {
                        eventReceived.countDown(); // Should not happen
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

        // Delete the non-.class file
        Files.delete(txtFile);

        // Then: No ClassFileDeleted event should be triggered
        assertTrue(eventReceived.getCount() == 1, 
            "Should NOT receive ClassFileDeleted event for non-.class files");

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