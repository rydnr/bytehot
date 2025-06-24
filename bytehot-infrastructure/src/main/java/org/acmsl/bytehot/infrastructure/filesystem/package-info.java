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
/**
 * ByteHot File System Infrastructure - File monitoring and change detection.
 * 
 * <p>This package provides file system monitoring infrastructure for ByteHot,
 * implementing efficient file change detection using Java NIO WatchService.
 * It serves as a primary adapter that drives hot-swap operations based on
 * file system events.</p>
 * 
 * <h2>Key Components</h2>
 * 
 * <h3>File Watching</h3>
 * <ul>
 *   <li>{@code FileWatcherAdapter} - Main file system monitoring adapter</li>
 *   <li>{@code WatchServiceManager} - Java NIO WatchService wrapper</li>
 *   <li>{@code FileChangeDetector} - Change event processing and filtering</li>
 * </ul>
 * 
 * <h3>Path Management</h3>
 * <ul>
 *   <li>{@code WatchPathRegistry} - Manages watched directory paths</li>
 *   <li>{@code FileFilter} - Filters relevant file changes</li>
 *   <li>{@code PathResolver} - Resolves relative and symbolic paths</li>
 * </ul>
 * 
 * <h3>Event Processing</h3>
 * <ul>
 *   <li>{@code FileEventProcessor} - Converts filesystem events to domain events</li>
 *   <li>{@code DebounceManager} - Prevents duplicate change notifications</li>
 *   <li>{@code BatchProcessor} - Groups related file changes</li>
 * </ul>
 * 
 * <h2>File Monitoring Strategy</h2>
 * 
 * <h3>Watch Service Integration</h3>
 * <pre>{@code
 * // Configure file watching
 * FileWatcherAdapter watcher = new FileWatcherAdapter();
 * watcher.initialize(application);
 * 
 * // Register watch paths
 * WatchConfiguration config = WatchConfiguration.builder()
 *     .addPath("/src/main/java")
 *     .addPath("/src/test/java") 
 *     .recursive(true)
 *     .filePattern("*.java")
 *     .debounceMs(500)
 *     .build();
 * 
 * watcher.configure(config);
 * watcher.startWatching();
 * }</pre>
 * 
 * <h3>Event Types</h3>
 * <p>The file watcher responds to these filesystem events:</p>
 * <ul>
 *   <li><strong>ENTRY_CREATE</strong> - New files created</li>
 *   <li><strong>ENTRY_MODIFY</strong> - Existing files modified</li>
 *   <li><strong>ENTRY_DELETE</strong> - Files deleted (cleanup)</li>
 *   <li><strong>OVERFLOW</strong> - Too many events (batch processing)</li>
 * </ul>
 * 
 * <h2>Change Detection</h2>
 * 
 * <h3>Filtering Strategy</h3>
 * <p>File changes are filtered to focus on relevant modifications:</p>
 * <ul>
 *   <li><strong>File Extension</strong> - Only .java and .class files</li>
 *   <li><strong>Path Patterns</strong> - Configurable include/exclude patterns</li>
 *   <li><strong>Debouncing</strong> - Prevents rapid-fire duplicate events</li>
 *   <li><strong>Size Validation</strong> - Ignores empty or corrupted files</li>
 * </ul>
 * 
 * <h3>Event Translation</h3>
 * <pre>{@code
 * // Filesystem event to domain event translation
 * WatchEvent<Path> watchEvent = // ... from WatchService
 * 
 * // Convert to domain event
 * ClassFileChanged domainEvent = ClassFileChanged.forNewSession(
 *     watchEvent.context(),
 *     extractClassName(watchEvent.context()),
 *     Files.size(watchEvent.context()),
 *     Instant.now()
 * );
 * 
 * // Emit to application
 * application.accept(domainEvent);
 * }</pre>
 * 
 * <h2>Performance Optimization</h2>
 * 
 * <h3>Efficient Watching</h3>
 * <ul>
 *   <li><strong>Recursive Registration</strong> - Monitors subdirectories automatically</li>
 *   <li><strong>Event Batching</strong> - Groups related changes together</li>
 *   <li><strong>Thread Pool</strong> - Processes events asynchronously</li>
 *   <li><strong>Memory Management</strong> - Limits event queue size</li>
 * </ul>
 * 
 * <h3>Debouncing Strategy</h3>
 * <pre>{@code
 * // Debounce rapid file changes
 * DebounceManager debouncer = new DebounceManager(500, TimeUnit.MILLISECONDS);
 * 
 * // Only process if no changes for 500ms
 * debouncer.debounce(filePath, () -> {
 *     processFileChange(filePath);
 * });
 * }</pre>
 * 
 * <h2>Error Handling</h2>
 * <p>File system monitoring includes robust error handling:</p>
 * <ul>
 *   <li><strong>Watch Registration Failures</strong> - Graceful fallback to polling</li>
 *   <li><strong>Permission Issues</strong> - Clear error messages and alternatives</li>
 *   <li><strong>Path Resolution Errors</strong> - Invalid path handling</li>
 *   <li><strong>Resource Cleanup</strong> - Proper cleanup on shutdown</li>
 * </ul>
 * 
 * <h2>Configuration</h2>
 * <p>File watching behavior is highly configurable:</p>
 * <pre>{@code
 * # bytehot.properties
 * bytehot.watch.paths=/src/main/java,/src/test/java
 * bytehot.watch.recursive=true
 * bytehot.watch.debounce.ms=500
 * bytehot.watch.file.patterns=*.java,*.class
 * bytehot.watch.exclude.patterns=target/,target/classes/**,.git/objects/**
 * bytehot.watch.poll.fallback=true
 * }</pre>
 * 
 * @author rydnr
 * @since 2025-06-07
 * @version 1.0
 */
package org.acmsl.bytehot.infrastructure.filesystem;