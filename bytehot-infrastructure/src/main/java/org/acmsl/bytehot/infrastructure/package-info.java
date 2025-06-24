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
 * ByteHot Infrastructure Layer - External system integrations and adapters.
 * 
 * <p>This package contains the infrastructure layer of ByteHot, which provides
 * concrete implementations of domain ports and integrates with external systems.
 * It follows the Hexagonal Architecture pattern by implementing adapters for
 * various external dependencies.</p>
 * 
 * <h2>Infrastructure Components</h2>
 * 
 * <h3>Primary Adapters (Driving)</h3>
 * <p>These adapters receive external input and drive the application:</p>
 * <ul>
 *   <li><strong>Agent</strong> - JVM agent entry point and lifecycle management</li>
 *   <li><strong>CLI</strong> - Command-line interface for user interactions</li>
 *   <li><strong>File System</strong> - File change monitoring and detection</li>
 * </ul>
 * 
 * <h3>Secondary Adapters (Driven)</h3>
 * <p>These adapters are driven by the application to interact with external systems:</p>
 * <ul>
 *   <li><strong>Event Sourcing</strong> - Event persistence and retrieval</li>
 *   <li><strong>Configuration</strong> - Settings and configuration management</li>
 *   <li><strong>Documentation</strong> - Code analysis and documentation generation</li>
 * </ul>
 * 
 * <h2>Architecture Principles</h2>
 * <p>The infrastructure layer adheres to:</p>
 * <ul>
 *   <li><strong>Dependency Inversion</strong> - Depends on domain abstractions</li>
 *   <li><strong>Adapter Pattern</strong> - Implements domain ports</li>
 *   <li><strong>Technology Isolation</strong> - Encapsulates external dependencies</li>
 *   <li><strong>Plug-and-Play</strong> - Supports multiple implementations</li>
 * </ul>
 * 
 * <h2>Adapter Discovery</h2>
 * <p>The infrastructure supports automatic adapter discovery:</p>
 * <pre>{@code
 * // Adapters are discovered at runtime
 * Ports ports = Ports.getInstance();
 * 
 * // File watcher adapter
 * FileWatcherPort fileWatcher = ports.resolve(FileWatcherPort.class);
 * 
 * // Event emitter adapter  
 * EventEmitterPort eventEmitter = ports.resolve(EventEmitterPort.class);
 * 
 * // Configuration adapter
 * ConfigurationPort config = ports.resolve(ConfigurationPort.class);
 * }</pre>
 * 
 * <h2>Technology Integration</h2>
 * <p>Current infrastructure integrations include:</p>
 * <ul>
 *   <li><strong>Java NIO</strong> - File system monitoring with WatchService</li>
 *   <li><strong>JVM Instrumentation</strong> - Bytecode manipulation and class redefinition</li>
 *   <li><strong>Reflection</strong> - Dynamic adapter discovery and instantiation</li>
 *   <li><strong>Properties Files</strong> - Configuration management</li>
 * </ul>
 * 
 * <h2>Event Flow</h2>
 * <p>Infrastructure adapters participate in the event-driven architecture:</p>
 * <ol>
 *   <li><strong>Input</strong> - Primary adapters receive external events</li>
 *   <li><strong>Translation</strong> - Convert external events to domain events</li>
 *   <li><strong>Processing</strong> - Domain processes the events</li>
 *   <li><strong>Output</strong> - Secondary adapters handle domain responses</li>
 * </ol>
 * 
 * <h2>Testability</h2>
 * <p>Infrastructure adapters are designed for testability:</p>
 * <ul>
 *   <li>Mock implementations for unit testing</li>
 *   <li>Integration test support</li>
 *   <li>Test doubles for external dependencies</li>
 *   <li>Configurable behavior for testing scenarios</li>
 * </ul>
 * 
 * @author rydnr
 * @since 2025-06-07
 * @version 1.0
 */
package org.acmsl.bytehot.infrastructure;