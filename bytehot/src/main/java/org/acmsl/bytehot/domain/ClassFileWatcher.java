/*
                        ByteHot

    Copyright (C) 2025-today  rydnr@acm-sl.org

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 3 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public v3
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    Thanks to ACM S.L. for distributing this library under the GPLv3 license.
    Contact info: jose.sanleandro@acm-sl.com

 ******************************************************************************
 *
 * Filename: ClassFileWatcher.java
 *
 * Author: Claude Code
 *
 * Class name: ClassFileWatcher
 *
 * Responsibilities:
 *   - Watch directories for .class file changes
 *   - Filter and emit domain events for class file modifications
 *
 * Collaborators:
 *   - FolderWatch: Base file watching functionality
 *   - ClassFileChanged: Domain event for class file changes
 */
package org.acmsl.bytehot.domain;

import org.acmsl.bytehot.domain.events.ClassFileChanged;
import org.acmsl.bytehot.domain.events.ClassFileCreated;
import org.acmsl.bytehot.domain.events.ClassFileDeleted;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Watches directories for .class file changes and emits domain events
 * @author Claude Code
 * @since 2025-06-16
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ClassFileWatcher extends FolderWatch {

    /**
     * File extension for Java class files
     */
    private static final String CLASS_FILE_EXTENSION = ".class";
    
    /**
     * Maximum number of retry attempts when reading file size
     */
    private static final int MAX_FILE_SIZE_RETRY_ATTEMPTS = 5;
    
    /**
     * Delay in milliseconds between file size retry attempts
     */
    private static final int FILE_SIZE_RETRY_DELAY_MS = 10;

    /**
     * Creates a new ClassFileWatcher
     * @param folder the folder to watch
     * @param interval the polling interval in milliseconds
     */
    public ClassFileWatcher(final Path folder, final int interval) {
        super(folder, interval);
    }

    /**
     * Watches the folder for .class file changes and emits domain events
     * @param onClassFileEvent callback for class file domain events
     * @throws IOException if watching fails
     */
    public void watchClassFiles(final Consumer<Object> onClassFileEvent) throws IOException {
        final WatchService watchService = java.nio.file.FileSystems.getDefault().newWatchService();
        getFolder().register(watchService,
            StandardWatchEventKinds.ENTRY_CREATE,
            StandardWatchEventKinds.ENTRY_MODIFY,
            StandardWatchEventKinds.ENTRY_DELETE);

        try {
            while (!Thread.currentThread().isInterrupted()) {
                final WatchKey key = watchService.poll(getInterval(), TimeUnit.MILLISECONDS);
                if (key != null) {
                    for (final WatchEvent<?> event : key.pollEvents()) {
                        final Path changedPath = getFolder().resolve((Path) event.context());
                        
                        if (isClassFile(changedPath)) {
                            try {
                                final Object domainEvent = createClassFileEvent(changedPath, event.kind());
                                if (domainEvent != null) {
                                    onClassFileEvent.accept(domainEvent);
                                }
                            } catch (final IOException e) {
                                handleFileProcessingError(changedPath, e);
                            }
                        }
                    }
                    key.reset();
                }
            }
        } catch (final InterruptedException ignored) {
            Thread.currentThread().interrupt();
        } finally {
            watchService.close();
        }
    }

    /**
     * Checks if the given path represents a .class file
     * @param path the file path to check
     * @return true if it's a .class file
     */
    private boolean isClassFile(final Path path) {
        return path.toString().endsWith(CLASS_FILE_EXTENSION);
    }

    /**
     * Creates the appropriate domain event based on the file system event type
     * @param classFile the path to the .class file
     * @param eventKind the type of file system event
     * @return the domain event, or null if event should be ignored
     * @throws IOException if file information cannot be read
     */
    private Object createClassFileEvent(final Path classFile, final WatchEvent.Kind<?> eventKind) throws IOException {
        if (eventKind == StandardWatchEventKinds.ENTRY_CREATE) {
            return createClassFileCreatedEvent(classFile);
        } else if (eventKind == StandardWatchEventKinds.ENTRY_MODIFY) {
            return createClassFileChangedEvent(classFile);
        } else if (eventKind == StandardWatchEventKinds.ENTRY_DELETE) {
            return createClassFileDeletedEvent(classFile);
        }
        return null;
    }

    /**
     * Creates a ClassFileCreated event from a new .class file
     * @param classFile the path to the new .class file
     * @return the domain event
     * @throws IOException if file information cannot be read
     */
    private ClassFileCreated createClassFileCreatedEvent(final Path classFile) throws IOException {
        final String className = extractClassName(classFile);
        final long fileSize = waitForFileToBeWritten(classFile);
        final Instant timestamp = Instant.now();
        
        return new ClassFileCreated(classFile, className, fileSize, timestamp);
    }

    /**
     * Creates a ClassFileChanged event from a modified .class file
     * @param classFile the path to the modified .class file
     * @return the domain event
     * @throws IOException if file information cannot be read
     */
    private ClassFileChanged createClassFileChangedEvent(final Path classFile) throws IOException {
        final String className = extractClassName(classFile);
        final long fileSize = Files.size(classFile);
        final Instant timestamp = Instant.now();
        
        return new ClassFileChanged(classFile, className, fileSize, timestamp);
    }

    /**
     * Creates a ClassFileDeleted event from a deleted .class file
     * @param classFile the path to the deleted .class file
     * @return the domain event
     */
    private ClassFileDeleted createClassFileDeletedEvent(final Path classFile) {
        final String className = extractClassName(classFile);
        final Instant timestamp = Instant.now();
        
        return new ClassFileDeleted(classFile, className, timestamp);
    }

    /**
     * Waits briefly for a newly created file to be fully written
     * @param classFile the file to check
     * @return the file size once stable
     * @throws IOException if file cannot be read
     */
    private long waitForFileToBeWritten(final Path classFile) throws IOException {
        long fileSize = 0;
        for (int attempt = 0; attempt < MAX_FILE_SIZE_RETRY_ATTEMPTS; attempt++) {
            try {
                fileSize = Files.size(classFile);
                if (fileSize > 0) {
                    break;
                }
                Thread.sleep(FILE_SIZE_RETRY_DELAY_MS);
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        return fileSize;
    }

    /**
     * Handles errors during file processing
     * @param classFile the file that caused the error
     * @param error the exception that occurred
     */
    private void handleFileProcessingError(final Path classFile, final IOException error) {
        // TODO: Replace with proper logging framework
        System.err.println("Error processing class file event for " + classFile + ": " + error.getMessage());
    }

    /**
     * Extracts the class name from a .class file path
     * @param classFile the .class file path
     * @return the class name (without .class extension)
     */
    private String extractClassName(final Path classFile) {
        final String fileName = classFile.getFileName().toString();
        if (fileName.endsWith(CLASS_FILE_EXTENSION)) {
            return fileName.substring(0, fileName.length() - CLASS_FILE_EXTENSION.length());
        }
        return fileName;
    }
}