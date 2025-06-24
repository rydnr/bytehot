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
 * ByteHot Testing Support - Advanced testing utilities and framework support.
 * 
 * <p>This package provides advanced testing support utilities for ByteHot,
 * including event-driven testing frameworks, bug reproduction systems,
 * and sophisticated test automation capabilities.</p>
 * 
 * <h2>Key Components</h2>
 * 
 * <h3>Event-Driven Testing Framework</h3>
 * <ul>
 *   <li>{@code EventDrivenTestSupport} - Core event-driven testing framework</li>
 *   <li>{@code EventTestContext} - Test execution context and state management</li>
 *   <li>{@code EventCapturingEmitter} - Event capture for verification</li>
 * </ul>
 * 
 * <h3>Bug Reproduction System</h3>
 * <ul>
 *   <li>{@code BugReport} - Comprehensive bug reporting with reproduction steps</li>
 *   <li>{@code EventSnapshotCapture} - Event context capture for debugging</li>
 *   <li>{@code ReproductionTestGenerator} - Automatic test case generation</li>
 * </ul>
 * 
 * <h3>Test Data Management</h3>
 * <ul>
 *   <li>{@code InMemoryEventStoreAdapter} - In-memory event storage for testing</li>
 *   <li>{@code TestDataBuilder} - Fluent test data construction</li>
 *   <li>{@code MockAdapterRegistry} - Mock infrastructure adapters</li>
 * </ul>
 * 
 * <h2>Event-Driven Testing Framework</h2>
 * 
 * <h3>Given-When-Then Pattern</h3>
 * <p>Structured event-driven testing with clear test phases:</p>
 * <pre>{@code
 * public class EventDrivenTestExample extends EventDrivenTestSupport {
 *     
 *     @Test
 *     void hotSwap_should_succeed_for_compatible_changes() {
 *         given()
 *             .classFile("UserService.java")
 *             .withValidBytecode()
 *             .instrumentationAvailable()
 *         .when()
 *             .fileIsModified()
 *             .hotSwapIsRequested()
 *         .then()
 *             .expectEvent(ClassRedefinitionSucceeded.class)
 *             .withClassName("UserService")
 *             .withPositiveInstanceCount();
 *     }
 * }
 * }</pre>
 * 
 * <h3>Event Context Management</h3>
 * <p>Comprehensive test context for event state tracking:</p>
 * <pre>{@code
 * // Access test context
 * EventTestContext context = getTestContext();
 * 
 * // Store test artifacts
 * context.storeArtifact("originalBytecode", bytecode);
 * context.storeArtifact("classFile", classFile);
 * 
 * // Capture events during test execution
 * context.addEmittedEvent(event);
 * 
 * // Verify captured events
 * List<DomainEvent> emittedEvents = context.getEmittedEvents();
 * assertThat(emittedEvents).hasSize(3);
 * }</pre>
 * 
 * <h2>Bug Reproduction System</h2>
 * 
 * <h3>Automatic Bug Reports</h3>
 * <p>Generate comprehensive bug reports from test failures:</p>
 * <pre>{@code
 * @Test
 * void testWithAutomaticBugReporting() {
 *     try {
 *         // Test logic that might fail
 *         performComplexOperation();
 *     } catch (Exception e) {
 *         // Generate bug report from failure
 *         BugReport report = BugReport.fromTestFailure(
 *             getTestContext(),
 *             e,
 *             "Complex operation should not fail with valid input"
 *         );
 *         
 *         // Bug report includes:
 *         // - Complete event history
 *         // - Test context and artifacts
 *         // - Reproduction test case
 *         // - System state snapshot
 *         
 *         System.out.println(report.generateBugReport());
 *         throw new AssertionError("Test failed - bug report generated: " + report.getReportId());
 *     }
 * }
 * }</pre>
 * 
 * <h3>Reproduction Test Generation</h3>
 * <p>Automatically generate test cases to reproduce bugs:</p>
 * <pre>{@code
 * String reproductionTest = bugReport.getReproductionTestCase();
 * 
 * TestMethod generatedTest = TestMethodBuilder.create()
 *     .withName("reproduceBug_" + bugReport.getId())
 *     .withScenario(bugReport.getOriginalScenario())
 *     .withEventReplay(bugReport.getCapturedEvents())
 *     .withExpectedException(bugReport.getOriginalException())
 *     .build();
 * }</pre>
 * 
 * <h2>In-Memory Event Store</h2>
 * 
 * <h3>Test Event Storage</h3>
 * <p>Efficient in-memory event storage for testing scenarios:</p>
 * <pre>{@code
 * // Configure in-memory event store
 * InMemoryEventStoreAdapter eventStore = new InMemoryEventStoreAdapter()
 *     .withCapacity(1000)
 *     .withRetentionPolicy(RetentionPolicy.TEST_DURATION)
 *     .withConcurrentAccess(true);
 * 
 * // Use in test configuration
 * TestApplicationContext.builder()
 *     .withEventStore(eventStore)
 *     .build();
 * 
 * // Verify stored events
 * List<VersionedDomainEvent> events = eventStore.getAllEvents();
 * assertThat(events).extracting(VersionedDomainEvent::getClass)
 *     .contains(ClassFileChanged.class, HotSwapRequested.class);
 * }</pre>
 * 
 * <h3>Event Store Operations</h3>
 * <p>Comprehensive event store testing capabilities:</p>
 * <pre>{@code
 * eventStore.save(event);
 * 
 * List<VersionedDomainEvent> hotSwapEvents = eventStore.getEventsByType(
 *     HotSwapRequested.class
 * );
 * 
 * eventStore.clearAllEvents();
 * 
 * String diagnostics = eventStore.getDiagnosticInfo();
 * System.out.println("Event store state: " + diagnostics);
 * }</pre>
 * 
 * <h2>Test Stages Framework</h2>
 * 
 * <h3>Fluent Test API</h3>
 * <p>Fluent API for readable and maintainable tests:</p>
 * <pre>{@code
 * GivenStage given = given()
 *     .applicationInitialized()
 *     .fileWatchingEnabled()
 *     .eventCapturingEnabled();
 * 
 * WhenStage when = given.when()
 *     .classFileIsCreated("NewService.java")
 *     .classFileIsModified("ExistingService.java")
 *     .waitForEventProcessing();
 * 
 * ThenStage then = when.then()
 *     .expectEvents(ClassFileChanged.class, HotSwapRequested.class)
 *     .expectNoErrorEvents()
 *     .expectEventCount(2);
 * }</pre>
 * 
 * <h3>Custom Stage Extensions</h3>
 * <p>Extend stages with domain-specific operations:</p>
 * <pre>{@code
 * public class HotSwapTestStages extends EventDrivenTestSupport {
 *     
 *     public HotSwapGivenStage givenHotSwapScenario() {
 *         return new HotSwapGivenStage(getTestContext());
 *     }
 *     
 *     public static class HotSwapGivenStage extends GivenStage {
 *         public HotSwapGivenStage compatibleClassChange() {
 *             return this;
 *         }
 *         
 *         public HotSwapGivenStage incompatibleClassChange() {
 *             return this;
 *         }
 *     }
 * }
 * }</pre>
 * 
 * <h2>Assertion Extensions</h2>
 * 
 * <h3>Event Assertions</h3>
 * <p>Specialized assertions for event verification:</p>
 * <pre>{@code
 * assertThat(capturedEvents)
 *     .hasEventOfType(ClassRedefinitionSucceeded.class)
 *     .hasEventCount(3)
 *     .hasEventsInOrder(
 *         ClassFileChanged.class,
 *         HotSwapRequested.class,
 *         ClassRedefinitionSucceeded.class
 *     );
 * 
 * assertThat(hotSwapEvent)
 *     .hasClassName("UserService")
 *     .hasNonNegativeInstanceCount()
 *     .hasReasonContaining("file changed");
 * }</pre>
 * 
 * <h2>Test Performance</h2>
 * 
 * <h3>Performance Monitoring</h3>
 * <p>Built-in performance monitoring for test execution:</p>
 * <ul>
 *   <li><strong>Event Processing Time</strong> - Measure event handling latency</li>
 *   <li><strong>Memory Usage</strong> - Track memory consumption during tests</li>
 *   <li><strong>Thread Usage</strong> - Monitor concurrent execution</li>
 *   <li><strong>Resource Cleanup</strong> - Verify proper resource management</li>
 * </ul>
 * 
 * @author rydnr
 * @since 2025-06-24
 * @version 1.0
 */
package org.acmsl.bytehot.testing.support;