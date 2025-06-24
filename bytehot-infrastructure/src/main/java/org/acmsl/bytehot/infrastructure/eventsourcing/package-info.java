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
 * ByteHot Event Sourcing Infrastructure - Event persistence and replay capabilities.
 * 
 * <p>This package provides event sourcing infrastructure for ByteHot, implementing
 * event storage, retrieval, and replay mechanisms. It enables comprehensive
 * audit trails, debugging capabilities, and system state reconstruction.</p>
 * 
 * <h2>Key Components</h2>
 * 
 * <h3>Event Storage</h3>
 * <ul>
 *   <li>{@code EventStoreAdapter} - Main event persistence adapter</li>
 *   <li>{@code EventSerializer} - Event serialization and deserialization</li>
 *   <li>{@code EventStreamManager} - Event stream organization and access</li>
 * </ul>
 * 
 * <h3>Event Retrieval</h3>
 * <ul>
 *   <li>{@code EventQueryService} - Event querying and filtering</li>
 *   <li>{@code EventProjector} - Event stream projection and aggregation</li>
 *   <li>{@code SnapshotManager} - System state snapshots for performance</li>
 * </ul>
 * 
 * <h3>Replay and Recovery</h3>
 * <ul>
 *   <li>{@code EventReplayer} - Event stream replay for debugging</li>
 *   <li>{@code StateReconstructor} - System state reconstruction</li>
 *   <li>{@code TimelineNavigator} - Navigate through event history</li>
 * </ul>
 * 
 * <h2>Event Storage Strategy</h2>
 * 
 * <h3>Append-Only Storage</h3>
 * <p>Events are stored in an append-only manner for immutability:</p>
 * <pre>{@code
 * // Store domain event
 * EventStoreAdapter eventStore = ports.resolve(EventStoreAdapter.class);
 * 
 * VersionedDomainEvent event = new ClassFileChanged(
 *     metadata,
 *     classFile,
 *     className,
 *     fileSize,
 *     timestamp
 * );
 * 
 * // Append to event stream
 * eventStore.save(event);
 * }</pre>
 * 
 * <h3>Stream Organization</h3>
 * <p>Events are organized by aggregate streams:</p>
 * <ul>
 *   <li><strong>Aggregate Type</strong> - Groups events by domain aggregate</li>
 *   <li><strong>Aggregate ID</strong> - Individual aggregate instance streams</li>
 *   <li><strong>Version</strong> - Optimistic concurrency control</li>
 *   <li><strong>Timestamp</strong> - Temporal ordering and querying</li>
 * </ul>
 * 
 * <h2>Event Querying</h2>
 * 
 * <h3>Query Capabilities</h3>
 * <pre>{@code
 * // Query events by aggregate
 * List<VersionedDomainEvent> events = eventStore.getEventsForAggregate(
 *     "hotswap", "UserService-123"
 * );
 * 
 * // Query events by type
 * List<VersionedDomainEvent> hotSwapEvents = eventStore.getEventsByType(
 *     HotSwapRequested.class
 * );
 * 
 * // Query events by time range
 * List<VersionedDomainEvent> recentEvents = eventStore.getEventsSince(
 *     Instant.now().minus(Duration.ofHours(1))
 * );
 * 
 * // Query events by user
 * List<VersionedDomainEvent> userEvents = eventStore.getEventsByUser(
 *     "user-123"
 * );
 * }</pre>
 * 
 * <h3>Event Filtering</h3>
 * <p>Advanced filtering capabilities for targeted queries:</p>
 * <ul>
 *   <li><strong>Event Type</strong> - Filter by specific event classes</li>
 *   <li><strong>Aggregate Stream</strong> - Filter by aggregate type/ID</li>
 *   <li><strong>Time Range</strong> - Filter by timestamp ranges</li>
 *   <li><strong>User Context</strong> - Filter by user or correlation ID</li>
 * </ul>
 * 
 * <h2>Event Replay</h2>
 * 
 * <h3>Debugging Support</h3>
 * <p>Event replay enables powerful debugging capabilities:</p>
 * <pre>{@code
 * // Replay events for specific aggregate
 * EventReplayer replayer = new EventReplayer(eventStore);
 * 
 * replayer.replayAggregate("hotswap", "UserService-123")
 *     .withBreakpoint(ClassRedefinitionFailed.class)
 *     .withEventFilter(event -> event.getUserId().equals("user-123"))
 *     .onEvent(event -> {
 *         System.out.println("Replaying: " + event.getClass().getSimpleName());
 *     })
 *     .execute();
 * }</pre>
 * 
 * <h3>State Reconstruction</h3>
 * <p>Reconstruct system state at any point in time:</p>
 * <pre>{@code
 * // Reconstruct state at specific time
 * Instant targetTime = Instant.parse("2025-06-24T15:30:00Z");
 * 
 * StateReconstructor reconstructor = new StateReconstructor(eventStore);
 * HotSwapState state = reconstructor.reconstructStateAt(
 *     "hotswap", "UserService-123", targetTime
 * );
 * 
 * // Analyze state
 * System.out.println("Redefinition count: " + state.getRedefinitionCount());
 * System.out.println("Last successful swap: " + state.getLastSuccessfulSwap());
 * }</pre>
 * 
 * <h2>Snapshot Management</h2>
 * 
 * <h3>Performance Optimization</h3>
 * <p>Snapshots optimize replay performance for long event streams:</p>
 * <ul>
 *   <li><strong>Periodic Snapshots</strong> - Automatic snapshot creation</li>
 *   <li><strong>Snapshot Compression</strong> - Efficient storage</li>
 *   <li><strong>Incremental Replay</strong> - Replay from nearest snapshot</li>
 *   <li><strong>Snapshot Verification</strong> - Consistency checking</li>
 * </ul>
 * 
 * <h2>Event Serialization</h2>
 * 
 * <h3>Format Support</h3>
 * <p>Multiple serialization formats for different use cases:</p>
 * <ul>
 *   <li><strong>JSON</strong> - Human-readable, schema evolution friendly</li>
 *   <li><strong>Protocol Buffers</strong> - Compact binary format</li>
 *   <li><strong>Avro</strong> - Schema registry integration</li>
 *   <li><strong>Java Serialization</strong> - Legacy support</li>
 * </ul>
 * 
 * <h2>Monitoring and Analytics</h2>
 * 
 * <h3>Event Stream Analytics</h3>
 * <p>Built-in analytics for event stream monitoring:</p>
 * <ul>
 *   <li>Event throughput and latency metrics</li>
 *   <li>Aggregate activity patterns</li>
 *   <li>Error rate and failure analysis</li>
 *   <li>User behavior analytics</li>
 * </ul>
 * 
 * @author rydnr
 * @since 2025-06-07
 * @version 1.0
 */
package org.acmsl.bytehot.infrastructure.eventsourcing;