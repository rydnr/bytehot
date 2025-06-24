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
 * ByteHot Command-Line Interface - User interaction and command processing.
 * 
 * <p>This package provides the command-line interface infrastructure for ByteHot,
 * enabling users to interact with the agent, configure behavior, and monitor
 * hot-swap operations through command-line tools.</p>
 * 
 * <h2>Key Components</h2>
 * 
 * <h3>CLI Framework</h3>
 * <ul>
 *   <li>{@code ByteHotCLI} - Main CLI application entry point</li>
 *   <li>{@code CommandProcessor} - Command parsing and execution</li>
 *   <li>{@code CLIConfiguration} - CLI-specific configuration</li>
 * </ul>
 * 
 * <h3>Command Categories</h3>
 * <ul>
 *   <li>{@code AgentCommands} - Agent attachment and lifecycle</li>
 *   <li>{@code ConfigCommands} - Configuration management</li>
 *   <li>{@code MonitoringCommands} - Status and monitoring</li>
 *   <li>{@code DocumentationCommands} - Documentation generation</li>
 * </ul>
 * 
 * <h2>Available Commands</h2>
 * 
 * <h3>Agent Management</h3>
 * <pre>{@code
 * # Attach agent to running JVM
 * bytehot attach <pid> [options]
 * 
 * # Detach agent from JVM
 * bytehot detach <pid>
 * 
 * # Check agent status
 * bytehot status <pid>
 * }</pre>
 * 
 * <h3>Configuration</h3>
 * <pre>{@code
 * # Configure watch paths
 * bytehot config watch-paths /src/main/java,/src/test/java
 * 
 * # Set validation mode
 * bytehot config validation strict
 * 
 * # Enable documentation generation
 * bytehot config documentation enable
 * }</pre>
 * 
 * <h3>Monitoring</h3>
 * <pre>{@code
 * # Monitor hot-swap events
 * bytehot monitor events
 * 
 * # View statistics
 * bytehot monitor stats
 * 
 * # Check file watching status
 * bytehot monitor watchers
 * }</pre>
 * 
 * <h3>Documentation</h3>
 * <pre>{@code
 * # Generate documentation for classes
 * bytehot docs generate --classes com.example.MyClass
 * 
 * # Analyze code flows
 * bytehot docs analyze --flow user-registration
 * 
 * # Export documentation
 * bytehot docs export --format html --output /docs
 * }</pre>
 * 
 * <h2>Interactive Mode</h2>
 * <p>The CLI supports an interactive shell for extended sessions:</p>
 * <pre>{@code
 * $ bytehot shell
 * ByteHot Interactive Shell v1.0
 * 
 * bytehot> attach 12345
 * Agent attached successfully to PID 12345
 * 
 * bytehot> config watch-paths /app/src
 * Watch paths configured: /app/src
 * 
 * bytehot> monitor events
 * [2025-06-24 15:30:22] ClassFileChanged: UserService.java
 * [2025-06-24 15:30:22] HotSwapRequested: UserService
 * [2025-06-24 15:30:23] ClassRedefinitionSucceeded: UserService (2 instances)
 * 
 * bytehot> exit
 * }</pre>
 * 
 * <h2>Output Formats</h2>
 * <p>CLI commands support multiple output formats:</p>
 * <ul>
 *   <li><strong>Human</strong> - Formatted for console reading</li>
 *   <li><strong>JSON</strong> - Machine-readable structured data</li>
 *   <li><strong>CSV</strong> - Tabular data export</li>
 *   <li><strong>XML</strong> - Structured markup format</li>
 * </ul>
 * 
 * <h2>Configuration Integration</h2>
 * <p>CLI commands integrate with ByteHot configuration:</p>
 * <ul>
 *   <li>Read from configuration files</li>
 *   <li>Override with command-line options</li>
 *   <li>Persist changes to configuration</li>
 *   <li>Validate configuration values</li>
 * </ul>
 * 
 * <h2>Event Streaming</h2>
 * <p>The CLI can stream real-time events:</p>
 * <pre>{@code
 * # Stream all events
 * bytehot stream
 * 
 * # Filter by event type
 * bytehot stream --filter ClassFileChanged,HotSwapRequested
 * 
 * # Output to file
 * bytehot stream --output events.log
 * }</pre>
 * 
 * <h2>Error Handling</h2>
 * <p>CLI commands provide comprehensive error handling:</p>
 * <ul>
 *   <li>Clear error messages with suggested solutions</li>
 *   <li>Exit codes following POSIX conventions</li>
 *   <li>Verbose mode for debugging</li>
 *   <li>Help and usage information</li>
 * </ul>
 * 
 * @author rydnr
 * @since 2025-06-07
 * @version 1.0
 */
package org.acmsl.bytehot.infrastructure.cli;