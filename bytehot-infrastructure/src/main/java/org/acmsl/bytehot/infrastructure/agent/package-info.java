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
 * ByteHot JVM Agent Infrastructure - JVM agent lifecycle and instrumentation integration.
 * 
 * <p>This package provides the JVM agent infrastructure for ByteHot, implementing
 * the entry point for JVM agent attachment and managing the agent lifecycle.
 * It serves as the primary adapter that drives the ByteHot application from
 * JVM agent callbacks.</p>
 * 
 * <h2>Key Components</h2>
 * 
 * <h3>Agent Lifecycle</h3>
 * <ul>
 *   <li>{@code ByteHotAgent} - Main agent class with premain/agentmain methods</li>
 *   <li>{@code AgentLifecycleManager} - Manages agent startup and shutdown</li>
 *   <li>{@code InstrumentationBootstrap} - Initializes instrumentation services</li>
 * </ul>
 * 
 * <h3>JVM Integration</h3>
 * <ul>
 *   <li>{@code JvmInstrumentationService} - Wraps Java Instrumentation API</li>
 *   <li>{@code ClassRedefinitionService} - Handles class redefinition operations</li>
 *   <li>{@code AgentConfiguration} - Agent-specific configuration management</li>
 * </ul>
 * 
 * <h2>Agent Attachment</h2>
 * <p>ByteHot supports both static and dynamic agent attachment:</p>
 * 
 * <h3>Static Attachment (premain)</h3>
 * <pre>{@code
 * // JVM command line
 * java -javaagent:bytehot-agent.jar=config.properties MyApplication
 * 
 * // Agent initialization
 * public static void premain(String agentArgs, Instrumentation inst) {
 *     ByteHotAgent.initialize(agentArgs, inst);
 * }
 * }</pre>
 * 
 * <h3>Dynamic Attachment (agentmain)</h3>
 * <pre>{@code
 * // Runtime attachment
 * VirtualMachine vm = VirtualMachine.attach(pid);
 * vm.loadAgent("bytehot-agent.jar", "config.properties");
 * 
 * // Agent initialization
 * public static void agentmain(String agentArgs, Instrumentation inst) {
 *     ByteHotAgent.initialize(agentArgs, inst);
 * }
 * }</pre>
 * 
 * <h2>Instrumentation Capabilities</h2>
 * <p>The agent provides comprehensive instrumentation features:</p>
 * <ul>
 *   <li><strong>Class Redefinition</strong> - Hot-swap existing classes</li>
 *   <li><strong>Class Retransformation</strong> - Modify loaded classes</li>
 *   <li><strong>Class Loading Monitoring</strong> - Track new class loads</li>
 *   <li><strong>Instance Tracking</strong> - Monitor affected object instances</li>
 * </ul>
 * 
 * <h2>Agent Configuration</h2>
 * <p>Agent behavior is configurable through properties:</p>
 * <pre>{@code
 * # bytehot.properties
 * bytehot.watch.paths=/src/main/java,/src/test/java
 * bytehot.watch.recursive=true
 * bytehot.validation.strict=false
 * bytehot.events.emit=true
 * bytehot.documentation.enable=true
 * }</pre>
 * 
 * <h2>Event-Driven Integration</h2>
 * <p>The agent integrates with ByteHot's event-driven architecture:</p>
 * <ol>
 *   <li><strong>Attachment</strong> - Emits {@code ByteHotAttachRequested} event</li>
 *   <li><strong>Initialization</strong> - Configures file watching and services</li>
 *   <li><strong>Operation</strong> - Responds to file changes and hot-swap requests</li>
 *   <li><strong>Shutdown</strong> - Cleanly releases resources</li>
 * </ol>
 * 
 * <h2>Security Considerations</h2>
 * <p>The agent implements security best practices:</p>
 * <ul>
 *   <li>Validates agent arguments and configuration</li>
 *   <li>Restricts class redefinition to safe operations</li>
 *   <li>Implements proper error handling and logging</li>
 *   <li>Respects JVM security policies</li>
 * </ul>
 * 
 * <h2>Error Handling</h2>
 * <p>Agent operations include comprehensive error handling:</p>
 * <ul>
 *   <li>Graceful degradation on instrumentation failures</li>
 *   <li>Detailed error reporting with context</li>
 *   <li>Automatic recovery for transient failures</li>
 *   <li>Safe fallback modes</li>
 * </ul>
 * 
 * @author rydnr
 * @since 2025-06-07
 * @version 1.0
 */
package org.acmsl.bytehot.infrastructure.agent;