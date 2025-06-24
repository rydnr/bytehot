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
 * ByteHot Domain Exceptions - Domain-specific exceptional conditions.
 * 
 * <p>This package contains exceptions that represent domain-specific
 * error conditions in the ByteHot system. These exceptions carry
 * rich contextual information and support event-driven error handling.</p>
 * 
 * <h2>Exception Categories</h2>
 * 
 * <h3>Validation Exceptions</h3>
 * <ul>
 *   <li>{@code BytecodeValidationException} - Bytecode compatibility issues</li>
 *   <li>{@code ClassValidationException} - Class structure validation failures</li>
 * </ul>
 * 
 * <h3>Hot-Swap Exceptions</h3>
 * <ul>
 *   <li>{@code HotSwapException} - General hot-swap operation failures</li>
 *   <li>{@code ClassRedefinitionException} - JVM redefinition failures</li>
 * </ul>
 * 
 * <h3>Event Sourcing Exceptions</h3>
 * <ul>
 *   <li>{@code EventSnapshotException} - Event capture and reproduction failures</li>
 *   <li>{@code EventStoreException} - Event persistence issues</li>
 * </ul>
 * 
 * <h3>Configuration Exceptions</h3>
 * <ul>
 *   <li>{@code ConfigurationException} - Invalid configuration settings</li>
 *   <li>{@code FileWatchingException} - File system monitoring failures</li>
 * </ul>
 * 
 * <h2>Exception Design</h2>
 * <p>Domain exceptions in this package:</p>
 * <ul>
 *   <li>Carry domain events for context preservation</li>
 *   <li>Include detailed error metadata</li>
 *   <li>Support automatic bug report generation</li>
 *   <li>Enable event replay for debugging</li>
 *   <li>Maintain causality chains</li>
 * </ul>
 * 
 * <h2>Error Handling Pattern</h2>
 * <pre>{@code
 * try {
 *     hotSwapManager.performRedefinition(request);
 * } catch (HotSwapException e) {
 *     // Exception contains the failure event
 *     ClassRedefinitionFailed failureEvent = e.getFailureEvent();
 *     
 *     // Emit failure event for event sourcing
 *     eventEmitter.emit(List.of(failureEvent));
 *     
 *     // Log with context
 *     logger.error("Hot-swap failed for {}: {}", 
 *         failureEvent.getClassName(), 
 *         failureEvent.getFailureReason());
 * }
 * }</pre>
 * 
 * <h2>Event Snapshot Integration</h2>
 * <p>The {@code EventSnapshotException} provides sophisticated error
 * reproduction capabilities by capturing the complete event context
 * leading to the failure, enabling developers to reproduce bugs
 * deterministically.</p>
 * 
 * @author rydnr
 * @since 2025-06-07
 * @version 1.0
 */
package org.acmsl.bytehot.domain.exceptions;