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
 * ByteHot Application Layer - Use case orchestration and workflow coordination.
 * 
 * <p>This package contains the application layer of ByteHot, which orchestrates
 * use cases and coordinates between the domain layer and infrastructure adapters.
 * It follows the Hexagonal Architecture pattern and implements the Application
 * interface from java-commons.</p>
 * 
 * <h2>Key Components</h2>
 * 
 * <h3>Application Services</h3>
 * <ul>
 *   <li>{@code ByteHotApplication} - Main application orchestrator</li>
 *   <li>{@code HotSwapApplicationService} - Hot-swap use case coordination</li>
 *   <li>{@code DocumentationApplicationService} - Documentation generation workflows</li>
 * </ul>
 * 
 * <h3>Use Case Handlers</h3>
 * <ul>
 *   <li>{@code HandlesByteHotAttached} - Agent attachment workflows</li>
 *   <li>{@code HandlesClassFileChanged} - File change processing</li>
 *   <li>{@code HandlesDocumentationRequested} - Documentation generation</li>
 * </ul>
 * 
 * <h2>Architecture Role</h2>
 * <p>The application layer serves as the:</p>
 * <ul>
 *   <li><strong>Orchestrator</strong> - Coordinates domain operations</li>
 *   <li><strong>Transaction Boundary</strong> - Manages operation consistency</li>
 *   <li><strong>Event Dispatcher</strong> - Routes domain events to handlers</li>
 *   <li><strong>Adapter Coordinator</strong> - Manages infrastructure dependencies</li>
 * </ul>
 * 
 * <h2>Hot-Swap Workflow</h2>
 * <p>The application orchestrates the complete hot-swap pipeline:</p>
 * <ol>
 *   <li><strong>Event Reception</strong> - Receives {@code ClassFileChanged} events</li>
 *   <li><strong>Validation</strong> - Coordinates bytecode validation</li>
 *   <li><strong>Hot-Swap Execution</strong> - Manages hot-swap operations</li>
 *   <li><strong>Event Emission</strong> - Emits success/failure events</li>
 *   <li><strong>Error Handling</strong> - Manages exceptional scenarios</li>
 * </ol>
 * 
 * <h2>Event-Driven Coordination</h2>
 * <pre>{@code
 * @Override
 * public List<? extends DomainResponseEvent<?>> accept(DomainEvent event) {
 *     if (event instanceof ClassFileChanged classFileEvent) {
 *         return handleClassFileChanged(classFileEvent);
 *     } else if (event instanceof DocumentationRequested docEvent) {
 *         return handleDocumentationRequested(docEvent);
 *     }
 *     // ... handle other event types
 * }
 * 
 * private List<? extends DomainResponseEvent<?>> handleClassFileChanged(ClassFileChanged event) {
 *     // 1. Validate bytecode
 *     BytecodeValidated validation = validator.validate(event.getClassFile());
 *     
 *     // 2. Create hot-swap request
 *     HotSwapRequested request = hotSwapManager.requestHotSwap(event, validation);
 *     
 *     // 3. Perform redefinition
 *     ClassRedefinitionSucceeded result = hotSwapManager.performRedefinition(request);
 *     
 *     // 4. Return response events
 *     return List.of(validation, request, result);
 * }
 * }</pre>
 * 
 * <h2>Adapter Management</h2>
 * <p>The application discovers and manages infrastructure adapters:</p>
 * <ul>
 *   <li>File system watchers for change detection</li>
 *   <li>Event emitters for event publication</li>
 *   <li>Configuration providers for settings</li>
 *   <li>Documentation generators for code analysis</li>
 * </ul>
 * 
 * <h2>User Context Integration</h2>
 * <p>The application ensures user context flows through all operations,
 * enabling audit trails and user-specific behavior customization.</p>
 * 
 * @author rydnr
 * @since 2025-06-07
 * @version 1.0
 */
package org.acmsl.bytehot.application;