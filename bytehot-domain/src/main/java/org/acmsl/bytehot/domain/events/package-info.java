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
 * ByteHot Domain Events - Events that occur within the ByteHot domain.
 * 
 * <p>This package contains all domain events that represent significant
 * business occurrences in the ByteHot system. These events follow the
 * Event Sourcing pattern and enable loose coupling between components.</p>
 * 
 * <h2>Event Categories</h2>
 * 
 * <h3>Lifecycle Events</h3>
 * <ul>
 *   <li>{@code ByteHotAttachRequested} - Agent attachment initiation</li>
 *   <li>{@code ByteHotAgentAttached} - Agent successfully attached</li>
 * </ul>
 * 
 * <h3>File System Events</h3>
 * <ul>
 *   <li>{@code ClassFileChanged} - Class file modification detected</li>
 *   <li>{@code WatchPathConfigured} - File system watching configured</li>
 * </ul>
 * 
 * <h3>Validation Events</h3>
 * <ul>
 *   <li>{@code BytecodeValidated} - Bytecode passes compatibility checks</li>
 *   <li>{@code BytecodeRejected} - Bytecode fails validation</li>
 * </ul>
 * 
 * <h3>Hot-Swap Events</h3>
 * <ul>
 *   <li>{@code HotSwapRequested} - Hot-swap operation requested</li>
 *   <li>{@code ClassRedefinitionSucceeded} - JVM redefinition successful</li>
 *   <li>{@code ClassRedefinitionFailed} - JVM redefinition failed</li>
 * </ul>
 * 
 * <h3>Documentation Events</h3>
 * <ul>
 *   <li>{@code DocumentationRequested} - Documentation generation requested</li>
 *   <li>{@code DocumentationLinkGenerated} - Cross-reference link created</li>
 *   <li>{@code FlowContextDetected} - Code flow context identified</li>
 * </ul>
 * 
 * <h2>Event Design</h2>
 * <p>All events in this package:</p>
 * <ul>
 *   <li>Extend {@code AbstractVersionedDomainEvent} for Event Sourcing support</li>
 *   <li>Are immutable value objects</li>
 *   <li>Include complete contextual information</li>
 *   <li>Support user context propagation</li>
 *   <li>Enable event replay and debugging</li>
 * </ul>
 * 
 * <h2>Usage Pattern</h2>
 * <pre>{@code
 * // Event creation with metadata
 * EventMetadata metadata = EventMetadata.forNewAggregate("hotswap", className);
 * HotSwapRequested event = new HotSwapRequested(
 *     metadata,
 *     classFile,
 *     className,
 *     originalBytecode,
 *     newBytecode,
 *     "User initiated hot-swap",
 *     timestamp,
 *     triggeringEvent
 * );
 * 
 * // Event emission
 * eventEmitter.emit(List.of(event));
 * }</pre>
 * 
 * @author rydnr
 * @since 2025-06-07
 * @version 1.0
 */
package org.acmsl.bytehot.domain.events;