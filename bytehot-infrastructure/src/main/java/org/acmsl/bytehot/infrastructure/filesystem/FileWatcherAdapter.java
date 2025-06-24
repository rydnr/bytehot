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
 * Filename: FileWatcherAdapter.java
 *
 * Author: Claude Code
 *
 * Class name: FileWatcherAdapter
 *
 * Responsibilities:
 *   - Implement file system watching using Java NIO WatchService
 *   - Handle recursive directory watching with pattern matching
 *   - Provide infrastructure implementation of FileWatcherPort
 *   - Accept Application instance for processing events (Hexagonal Architecture)
 *
 * Collaborators:
 *   - FileWatcherPort: Interface this adapter implements
 *   - WatchService: Java NIO API for file system monitoring
 *   - Application: Interface from java-commons for processing events
 */
package org.acmsl.bytehot.infrastructure.filesystem;

import org.acmsl.bytehot.domain.FileWatcherPort;
import org.acmsl.bytehot.domain.events.ClassFileChanged;

import org.acmsl.commons.patterns.Adapter;
import org.acmsl.commons.patterns.Application;
import org.acmsl.commons.patterns.DomainResponseEvent;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

/**
 * Infrastructure adapter for file system watching operations
 * @author Claude Code
 * @since 2025-06-17
 */
public class FileWatcherAdapter
    implements FileWatcherPort, Adapter<FileWatcherPort> {

    /**
     * The underlying NIO watch service
     */
    private WatchService watchService;

    /**
     * Thread pool for handling watch events
     */
    private ExecutorService executorService;

    /**
     * Map of watch IDs to their configurations
     */
    private final Map<String, WatchConfiguration> watchConfigurations = new ConcurrentHashMap<>();

    /**
     * Map of paths to their watch keys
     */
    private final Map<Path, WatchKey> pathToWatchKey = new ConcurrentHashMap<>();

    /**
     * Map of watch keys to their paths
     */
    private final Map<WatchKey, Path> watchKeyToPath = new ConcurrentHashMap<>();

    /**
     * Whether the watcher is currently running
     */
    private volatile boolean running;
    
    /**
     * Application instance for processing events (following hexagonal architecture)
     */
    private Application application;

    /**
     * Default constructor for adapter discovery
     */
    public FileWatcherAdapter() {
        // For adapter discovery only - minimal initialization
        this.application = null;
        this.watchService = null;
        this.executorService = null;
        this.running = false;
    }

    /**
     * Creates a new FileWatcherAdapter instance
     * @param application the application instance for processing events
     */
    public FileWatcherAdapter(final Application application) throws IOException {
        this.application = application;
        this.watchService = FileSystems.getDefault().newWatchService();
        this.executorService = Executors.newCachedThreadPool(r -> {
            final Thread thread = new Thread(r, "FileWatcher-" + Thread.currentThread().getId());
            thread.setDaemon(true);
            return thread;
        });
        // Maps are automatically initialized as field initializers
        this.running = true;
        
        // Start the watch service loop
        startWatchServiceLoop();
    }

    /**
     * Starts watching a directory for file changes
     */
    @Override
    public String startWatching(final Path path, final List<String> patterns, final boolean recursive) throws Exception {
        if (!Files.exists(path) || !Files.isDirectory(path)) {
            throw new IllegalArgumentException("Path must be an existing directory: " + path);
        }

        final String watchId = UUID.randomUUID().toString();
        final List<Pattern> compiledPatterns = new ArrayList<>();
        
        for (final String pattern : patterns) {
            compiledPatterns.add(Pattern.compile(pattern.replace("*", ".*")));
        }

        final WatchConfiguration config = new WatchConfiguration(path, compiledPatterns, recursive);
        watchConfigurations.put(watchId, config);

        if (recursive) {
            registerRecursive(path);
        } else {
            registerSingle(path);
        }

        return watchId;
    }

    /**
     * Stops watching a previously registered directory
     */
    @Override
    public void stopWatching(final String watchId) throws Exception {
        final WatchConfiguration config = watchConfigurations.remove(watchId);
        if (config != null) {
            final WatchKey watchKey = pathToWatchKey.remove(config.getPath());
            if (watchKey != null) {
                watchKey.cancel();
                watchKeyToPath.remove(watchKey);
            }
        }
    }

    /**
     * Checks if a directory is currently being watched
     */
    @Override
    public boolean isWatching(final Path path) {
        return pathToWatchKey.containsKey(path);
    }

    /**
     * Returns all currently watched paths
     */
    @Override
    public List<Path> getWatchedPaths() {
        return new ArrayList<>(pathToWatchKey.keySet());
    }

    /**
     * Checks if the file watcher is operational
     */
    @Override
    public boolean isWatcherAvailable() {
        return running && watchService != null;
    }
    
    /**
     * Properly initializes this adapter with full functionality
     * @param application the application instance for processing events
     */
    public void initialize(final Application application) throws IOException {
        if (this.watchService != null) {
            return; // Already initialized
        }
        
        this.application = application;
        this.watchService = FileSystems.getDefault().newWatchService();
        this.executorService = Executors.newCachedThreadPool(r -> {
            final Thread thread = new Thread(r, "FileWatcher-" + Thread.currentThread().getId());
            thread.setDaemon(true);
            return thread;
        });
        this.running = true;
        
        // Start the watch service loop
        startWatchServiceLoop();
    }

    /**
     * Returns the port interface this adapter implements
     */
    @Override
    public Class<FileWatcherPort> adapts() {
        return FileWatcherPort.class;
    }

    /**
     * Sets the application instance for processing events
     * @param application the application instance
     */
    public void setApplication(final Application application) {
        this.application = application;
    }

    /**
     * Registers a single directory for watching
     */
    protected void registerSingle(final Path path) throws IOException {
        final WatchKey watchKey = path.register(
            watchService,
            StandardWatchEventKinds.ENTRY_CREATE,
            StandardWatchEventKinds.ENTRY_DELETE,
            StandardWatchEventKinds.ENTRY_MODIFY
        );
        
        pathToWatchKey.put(path, watchKey);
        watchKeyToPath.put(watchKey, path);
    }

    /**
     * Registers a directory and all subdirectories for watching
     */
    protected void registerRecursive(final Path start) throws IOException {
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
                registerSingle(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Starts the watch service event processing loop
     */
    protected void startWatchServiceLoop() {
        executorService.submit(() -> {
            while (running) {
                try {
                    final WatchKey key = watchService.take();
                    final Path dir = watchKeyToPath.get(key);
                    
                    if (dir == null) {
                        continue;
                    }

                    for (final WatchEvent<?> event : key.pollEvents()) {
                        final WatchEvent.Kind<?> kind = event.kind();
                        
                        if (kind == StandardWatchEventKinds.OVERFLOW) {
                            continue;
                        }

                        @SuppressWarnings("unchecked")
                        final WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;
                        final Path filename = pathEvent.context();
                        final Path fullPath = dir.resolve(filename);

                        processFileEvent(kind, fullPath);
                    }

                    final boolean valid = key.reset();
                    if (!valid) {
                        pathToWatchKey.remove(dir);
                        watchKeyToPath.remove(key);
                    }
                    
                } catch (final InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (final Exception e) {
                    System.err.println("Error in file watcher: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Processes a file system event
     */
    protected void processFileEvent(final WatchEvent.Kind<?> kind, final Path path) {
        // Check if any watch configuration matches this file
        for (final WatchConfiguration config : watchConfigurations.values()) {
            if (matchesPatterns(path, config.getPatterns())) {
                if (kind == StandardWatchEventKinds.ENTRY_MODIFY && isClassFile(path)) {
                    emitClassFileChangedEvent(path);
                }
                System.out.println("File " + kind.name() + ": " + path);
            }
        }
    }

    /**
     * Checks if a path represents a .class file
     */
    protected boolean isClassFile(final Path path) {
        return path.toString().endsWith(".class");
    }

    /**
     * Emits a ClassFileChanged domain event
     */
    protected void emitClassFileChangedEvent(final Path classFile) {
        try {
            final String className = extractClassName(classFile);
            final long fileSize = Files.size(classFile);
            final Instant detectionTimestamp = Instant.now();
            
            final ClassFileChanged event = ClassFileChanged.forNewSession(
                classFile,
                className,
                fileSize,
                detectionTimestamp
            );
            
            // Process the event through the Application layer following hexagonal architecture
            if (application != null) {
                try {
                    // Use the generic Application interface directly - no reflection needed!
                    final List<? extends DomainResponseEvent<?>> responseEvents = application.accept(event);
                    System.out.println("ClassFileChanged event processed successfully. Generated " + 
                                     responseEvents.size() + " response events.");
                    
                    // Log response events for debugging
                    for (final DomainResponseEvent<?> responseEvent : responseEvents) {
                        System.out.println("Response event: " + responseEvent.getClass().getSimpleName());
                    }
                    
                } catch (final Exception processingException) {
                    System.err.println("Failed to process ClassFileChanged event: " + processingException.getMessage());
                    processingException.printStackTrace();
                }
            } else {
                System.err.println("No application instance available to process ClassFileChanged event");
            }
            
        } catch (final Exception e) {
            System.err.println("Failed to emit ClassFileChanged event for " + classFile + ": " + e.getMessage());
        }
    }

    /**
     * Extracts the class name from a .class file path
     */
    protected String extractClassName(final Path classFile) {
        final String fileName = classFile.getFileName().toString();
        if (fileName.endsWith(".class")) {
            return fileName.substring(0, fileName.length() - 6);
        }
        return fileName;
    }

    /**
     * Checks if a file matches any of the given patterns
     */
    protected boolean matchesPatterns(final Path path, final List<Pattern> patterns) {
        final String filename = path.getFileName().toString();
        
        for (final Pattern pattern : patterns) {
            if (pattern.matcher(filename).matches()) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Shuts down the file watcher
     */
    public void shutdown() {
        running = false;
        executorService.shutdown();
        
        try {
            watchService.close();
        } catch (final IOException e) {
            System.err.println("Error closing watch service: " + e.getMessage());
        }
    }

    /**
     * Internal class to hold watch configuration
     */
    protected static class WatchConfiguration {
        /**
         * The path being watched
         */
        private final Path path;

        /**
         * File patterns to match
         */
        private final List<Pattern> patterns;

        /**
         * Whether to watch recursively
         */
        private final boolean recursive;

        /**
         * Creates a new watch configuration
         */
        public WatchConfiguration(final Path path, final List<Pattern> patterns, final boolean recursive) {
            this.path = path;
            this.patterns = patterns;
            this.recursive = recursive;
        }

        /**
         * Returns the path being watched
         */
        public Path getPath() {
            return path;
        }

        /**
         * Returns the file patterns
         */
        public List<Pattern> getPatterns() {
            return patterns;
        }

        /**
         * Returns whether watching is recursive
         */
        public boolean isRecursive() {
            return recursive;
        }
    }
}