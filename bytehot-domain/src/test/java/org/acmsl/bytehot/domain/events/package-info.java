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
 * ByteHot Domain Events Tests - Comprehensive domain event testing.
 * 
 * <p>This package contains thorough unit tests for all ByteHot domain events,
 * focusing on event construction, validation, serialization, and behavior
 * verification in isolation.</p>
 * 
 * <h2>Test Categories</h2>
 * 
 * <h3>Event Construction Tests</h3>
 * <ul>
 *   <li><strong>Factory Method Tests</strong> - Event creation via factory methods</li>
 *   <li><strong>Builder Pattern Tests</strong> - Event construction with builders</li>
 *   <li><strong>Validation Tests</strong> - Event parameter validation</li>
 * </ul>
 * 
 * <h3>Event Behavior Tests</h3>
 * <ul>
 *   <li><strong>Immutability Tests</strong> - Verify events are immutable</li>
 *   <li><strong>Equality Tests</strong> - Test equals/hashCode contracts</li>
 *   <li><strong>Serialization Tests</strong> - Event serialization/deserialization</li>
 * </ul>
 * 
 * <h3>Event Sourcing Tests</h3>
 * <ul>
 *   <li><strong>Metadata Tests</strong> - EventMetadata integration</li>
 *   <li><strong>Versioning Tests</strong> - Event version compatibility</li>
 *   <li><strong>Causality Tests</strong> - Event causality chain validation</li>
 * </ul>
 * 
 * <h2>Testing Patterns</h2>
 * 
 * <h3>Event Creation Testing</h3>
 * <p>Systematic testing of event creation patterns:</p>
 * <pre>{@code
 * @Test
 * void classFileChanged_should_create_with_factory_method() {
 *     // Given: Valid event parameters
 *     Path classFile = createTempClassFile("TestClass.java");
 *     String className = "TestClass";
 *     long fileSize = 1024L;
 *     Instant timestamp = Instant.now();
 *     
 *     // When: Creating event via factory method
 *     ClassFileChanged event = ClassFileChanged.forNewSession(
 *         classFile, className, fileSize, timestamp
 *     );
 *     
 *     // Then: Event should be properly constructed
 *     assertThat(event)
 *         .extracting(ClassFileChanged::getClassFile,
 *                    ClassFileChanged::getClassName,
 *                    ClassFileChanged::getFileSize,
 *                    ClassFileChanged::getTimestamp)
 *         .containsExactly(classFile, className, fileSize, timestamp);
 *     
 *     // And: Should have valid event metadata
 *     assertThat(event.getEventId()).isNotBlank();
 *     assertThat(event.getAggregateType()).isEqualTo("file-change");
 *     assertThat(event.getAggregateVersion()).isEqualTo(1L);
 * }
 * }</pre>
 * 
 * <h3>Parameter Validation Testing</h3>
 * <p>Test event parameter validation and error handling:</p>
 * <pre>{@code
 * @Test
 * void hotSwapRequested_should_validate_parameters() {
 *     // Test null class file
 *     assertThatThrownBy(() -> new HotSwapRequested(
 *         metadata, null, "TestClass", bytecode, bytecode, "reason", timestamp, trigger
 *     )).isInstanceOf(IllegalArgumentException.class)
 *       .hasMessageContaining("classFile cannot be null");
 *     
 *     // Test empty class name
 *     assertThatThrownBy(() -> new HotSwapRequested(
 *         metadata, classFile, "", bytecode, bytecode, "reason", timestamp, trigger
 *     )).isInstanceOf(IllegalArgumentException.class)
 *       .hasMessageContaining("className cannot be empty");
 *     
 *     // Test null bytecode
 *     assertThatThrownBy(() -> new HotSwapRequested(
 *         metadata, classFile, "TestClass", null, bytecode, "reason", timestamp, trigger
 *     )).isInstanceOf(IllegalArgumentException.class)
 *       .hasMessageContaining("originalBytecode cannot be null");
 * }
 * }</pre>
 * 
 * <h2>Event Lifecycle Testing</h2>
 * 
 * <h3>Event Flow Testing</h3>
 * <p>Test events in realistic domain scenarios:</p>
 * <pre>{@code
 * @Test
 * void classRedefinitionSucceeded_should_contain_complete_context() {
 *     // Given: Successful redefinition scenario
 *     HotSwapRequested request = createHotSwapRequest();
 *     Duration redefinitionDuration = Duration.ofMillis(150);
 *     int affectedInstances = 5;
 *     String details = "Successfully redefined TestClass with 5 affected instances";
 *     
 *     // When: Creating success event
 *     ClassRedefinitionSucceeded event = new ClassRedefinitionSucceeded(
 *         request.getEventMetadata().forNext(),
 *         request.getClassFile(),
 *         request.getClassName(),
 *         affectedInstances,
 *         details,
 *         redefinitionDuration,
 *         Instant.now(),
 *         request
 *     );
 *     
 *     // Then: Event should contain complete context
 *     assertThat(event.getClassName()).isEqualTo("TestClass");
 *     assertThat(event.getAffectedInstances()).isEqualTo(5);
 *     assertThat(event.getDuration()).isEqualTo(redefinitionDuration);
 *     assertThat(event.getRedefinitionDetails()).contains("Successfully");
 *     assertThat(event.getPreviousEventId()).isEqualTo(request.getEventId());
 * }
 * }</pre>
 * 
 * <h2>Event Metadata Testing</h2>
 * 
 * <h3>Metadata Integration</h3>
 * <p>Test proper EventMetadata integration in all events:</p>
 * <pre>{@code
 * @Test
 * void all_events_should_have_valid_metadata() {
 *     // Test each event type has proper metadata
 *     List<VersionedDomainEvent> events = List.of(
 *         createClassFileChangedEvent(),
 *         createHotSwapRequestedEvent(),
 *         createClassRedefinitionSucceededEvent(),
 *         createDocumentationRequestedEvent()
 *     );
 *     
 *     events.forEach(event -> {
 *         // Verify metadata completeness
 *         assertThat(event.getEventId()).isNotBlank();
 *         assertThat(event.getAggregateType()).isNotBlank();
 *         assertThat(event.getAggregateId()).isNotBlank();
 *         assertThat(event.getAggregateVersion()).isPositive();
 *         assertThat(event.getTimestamp()).isNotNull();
 *         assertThat(event.getSchemaVersion()).isPositive();
 *         
 *         // Verify metadata consistency
 *         EventMetadata metadata = event.getMetadata();
 *         assertThat(metadata.getEventId()).isEqualTo(event.getEventId());
 *         assertThat(metadata.getAggregateType()).isEqualTo(event.getAggregateType());
 *     });
 * }
 * }</pre>
 * 
 * <h3>Causality Chain Testing</h3>
 * <p>Test event causality and correlation:</p>
 * <pre>{@code
 * @Test
 * void events_should_maintain_causality_chain() {
 *     // Given: Initial event
 *     ClassFileChanged initial = createClassFileChangedEvent();
 *     
 *     // When: Creating subsequent events
 *     HotSwapRequested request = new HotSwapRequested(
 *         initial.getMetadata().forNext(),
 *         // ... other parameters
 *         initial // Previous event
 *     );
 *     
 *     ClassRedefinitionSucceeded success = new ClassRedefinitionSucceeded(
 *         request.getMetadata().forNext(),
 *         // ... other parameters
 *         request // Previous event
 *     );
 *     
 *     // Then: Causality chain should be maintained
 *     assertThat(request.getPreviousEventId()).isEqualTo(initial.getEventId());
 *     assertThat(success.getPreviousEventId()).isEqualTo(request.getEventId());
 *     
 *     // And: Correlation ID should be preserved
 *     assertThat(request.getCorrelationId()).isEqualTo(initial.getCorrelationId());
 *     assertThat(success.getCorrelationId()).isEqualTo(initial.getCorrelationId());
 * }
 * }</pre>
 * 
 * <h2>Serialization Testing</h2>
 * 
 * <h3>Event Serialization</h3>
 * <p>Test event serialization for event sourcing:</p>
 * <pre>{@code
 * @Test
 * void events_should_serialize_and_deserialize_correctly() {
 *     // Given: Original event
 *     ClassRedefinitionSucceeded original = createClassRedefinitionSucceededEvent();
 *     
 *     // When: Serializing and deserializing
 *     String json = eventSerializer.serialize(original);
 *     ClassRedefinitionSucceeded deserialized = eventSerializer.deserialize(
 *         json, ClassRedefinitionSucceeded.class
 *     );
 *     
 *     // Then: Events should be equal
 *     assertThat(deserialized).isEqualTo(original);
 *     
 *     // And: All properties should be preserved
 *     assertThat(deserialized.getClassName()).isEqualTo(original.getClassName());
 *     assertThat(deserialized.getAffectedInstances()).isEqualTo(original.getAffectedInstances());
 *     assertThat(deserialized.getDuration()).isEqualTo(original.getDuration());
 *     assertThat(deserialized.getEventMetadata()).isEqualTo(original.getEventMetadata());
 * }
 * }</pre>
 * 
 * <h2>Error Event Testing</h2>
 * 
 * <h3>Failure Scenario Events</h3>
 * <p>Test events generated during error conditions:</p>
 * <pre>{@code
 * @Test
 * void classRedefinitionFailed_should_capture_failure_context() {
 *     // Given: Failure scenario
 *     String failureReason = "JVM rejected bytecode changes as incompatible";
 *     String jvmError = "java.lang.UnsupportedOperationException: incompatible changes";
 *     String recoveryAction = "Review changes for compatibility or restart application";
 *     
 *     // When: Creating failure event
 *     ClassRedefinitionFailed event = new ClassRedefinitionFailed(
 *         "TestClass",
 *         classFile,
 *         failureReason,
 *         jvmError,
 *         recoveryAction,
 *         Instant.now()
 *     );
 *     
 *     // Then: Event should capture complete failure context
 *     assertThat(event.getFailureReason()).isEqualTo(failureReason);
 *     assertThat(event.getJvmError()).isEqualTo(jvmError);
 *     assertThat(event.getRecoveryAction()).isEqualTo(recoveryAction);
 *     assertThat(event.getClassName()).isEqualTo("TestClass");
 * }
 * }</pre>
 * 
 * <h2>Performance Testing</h2>
 * 
 * <h3>Event Creation Performance</h3>
 * <p>Test event creation performance characteristics:</p>
 * <pre>{@code
 * @Test
 * void event_creation_should_be_fast() {
 *     // Given: Event creation parameters
 *     int iterations = 10000;
 *     
 *     // When: Creating many events
 *     long startTime = System.nanoTime();
 *     
 *     for (int i = 0; i < iterations; i++) {
 *         ClassFileChanged event = ClassFileChanged.forNewSession(
 *             classFile, "TestClass" + i, 1024L, Instant.now()
 *         );
 *     }
 *     
 *     long duration = System.nanoTime() - startTime;
 *     
 *     // Then: Should complete quickly
 *     double avgTimePerEvent = duration / (double) iterations;
 *     assertThat(avgTimePerEvent).isLessThan(100_000); // < 100 microseconds per event
 * }
 * }</pre>
 * 
 * @author rydnr
 * @since 2025-06-07
 * @version 1.0
 */
package org.acmsl.bytehot.domain.events;