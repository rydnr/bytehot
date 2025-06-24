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
 * ByteHot Event Infrastructure - Event emission, routing, and handling.
 * 
 * <p>This package provides event infrastructure for ByteHot, implementing
 * event emission, routing, filtering, and external event integration.
 * It serves as both primary and secondary adapters for event-driven
 * communication with external systems.</p>
 * 
 * <h2>Key Components</h2>
 * 
 * <h3>Event Emission</h3>
 * <ul>
 *   <li>{@code EventEmitterAdapter} - Main event emission adapter</li>
 *   <li>{@code EventPublisher} - Event publication to external systems</li>
 *   <li>{@code EventBatcher} - Batches events for efficient transmission</li>
 * </ul>
 * 
 * <h3>Event Routing</h3>
 * <ul>
 *   <li>{@code EventRouter} - Routes events to appropriate handlers</li>
 *   <li>{@code EventFilter} - Filters events based on criteria</li>
 *   <li>{@code EventTransformer} - Transforms events between formats</li>
 * </ul>
 * 
 * <h3>External Integration</h3>
 * <ul>
 *   <li>{@code WebhookEventEmitter} - HTTP webhook event delivery</li>
 *   <li>{@code MessageQueueEmitter} - Message queue integration</li>
 *   <li>{@code LogEventEmitter} - Structured logging event output</li>
 * </ul>
 * 
 * <h2>Event Emission Strategies</h2>
 * 
 * <h3>Synchronous Emission</h3>
 * <p>Immediate event emission for real-time processing:</p>
 * <pre>{@code
 * // Configure synchronous emission
 * EventEmitterAdapter emitter = new EventEmitterAdapter()
 *     .withStrategy(EmissionStrategy.SYNCHRONOUS)
 *     .withTimeout(Duration.ofSeconds(5));
 * 
 * // Emit events immediately
 * List<DomainEvent> events = List.of(
 *     new ClassFileChanged(metadata, classFile, className, size, timestamp)
 * );
 * 
 * emitter.emit(events);
 * }</pre>
 * 
 * <h3>Asynchronous Emission</h3>
 * <p>Non-blocking event emission for high throughput:</p>
 * <pre>{@code
 * // Configure asynchronous emission
 * EventEmitterAdapter emitter = new EventEmitterAdapter()
 *     .withStrategy(EmissionStrategy.ASYNCHRONOUS)
 *     .withThreadPool(Executors.newFixedThreadPool(4))
 *     .withQueueSize(1000);
 * 
 * // Emit events asynchronously
 * CompletableFuture<Void> future = emitter.emitAsync(events);
 * }</pre>
 * 
 * <h3>Batched Emission</h3>
 * <p>Efficient batching for high-volume event streams:</p>
 * <pre>{@code
 * // Configure batched emission
 * EventBatcher batcher = new EventBatcher()
 *     .withBatchSize(100)
 *     .withFlushInterval(Duration.ofSeconds(1))
 *     .withCompressionEnabled(true);
 * 
 * // Events are automatically batched
 * batcher.addEvent(event);
 * // ... more events added
 * // Batch automatically flushed when size or time limit reached
 * }</pre>
 * 
 * <h2>Event Routing</h2>
 * 
 * <h3>Route Configuration</h3>
 * <p>Flexible event routing based on event properties:</p>
 * <pre>{@code
 * // Configure event routing
 * EventRouter router = new EventRouter()
 *     .addRoute(ClassFileChanged.class, fileChangeHandler)
 *     .addRoute(HotSwapRequested.class, hotSwapHandler)
 *     .addRoute(event -> event.getUserId().startsWith("admin"), adminHandler)
 *     .addFallbackRoute(defaultHandler);
 * 
 * // Route events to appropriate handlers
 * router.route(event);
 * }</pre>
 * 
 * <h3>Event Filtering</h3>
 * <p>Filter events before emission or routing:</p>
 * <pre>{@code
 * // Configure event filters
 * EventFilter filter = new EventFilter()
 *     .excludeEventType(DebugEvent.class)
 *     .includeUserPattern("user-*")
 *     .excludeTestEvents()
 *     .addCustomFilter(event -> event.getTimestamp().isAfter(cutoffTime));
 * 
 * // Apply filters
 * List<DomainEvent> filteredEvents = filter.filter(events);
 * }</pre>
 * 
 * <h2>External System Integration</h2>
 * 
 * <h3>Webhook Integration</h3>
 * <p>HTTP webhook delivery for external system integration:</p>
 * <pre>{@code
 * // Configure webhook emitter
 * WebhookEventEmitter webhook = new WebhookEventEmitter()
 *     .withEndpoint("https://api.example.com/bytehot/events")
 *     .withAuthentication(bearerToken)
 *     .withRetryPolicy(RetryPolicy.exponentialBackoff())
 *     .withEventFormat(EventFormat.JSON);
 * 
 * // Register webhook as event target
 * eventEmitter.addTarget(webhook);
 * }</pre>
 * 
 * <h3>Message Queue Integration</h3>
 * <p>Integration with message queue systems:</p>
 * <pre>{@code
 * // Configure message queue emitter
 * MessageQueueEmitter mqEmitter = new MessageQueueEmitter()
 *     .withConnection("amqp://localhost:5672")
 *     .withExchange("bytehot.events")
 *     .withRoutingKeyStrategy(event -> event.getClass().getSimpleName())
 *     .withPersistentDelivery(true);
 * 
 * // Register message queue as event target
 * eventEmitter.addTarget(mqEmitter);
 * }</pre>
 * 
 * <h2>Event Transformation</h2>
 * 
 * <h3>Format Transformation</h3>
 * <p>Transform events between different formats:</p>
 * <pre>{@code
 * // Configure event transformer
 * EventTransformer transformer = new EventTransformer()
 *     .addTransformation(ClassFileChanged.class, CloudEventFormat.class)
 *     .addTransformation(HotSwapRequested.class, WebhookEventFormat.class)
 *     .withDefaultTransformation(JsonEventFormat.class);
 * 
 * // Transform events for external consumption
 * List<ExternalEvent> externalEvents = transformer.transform(domainEvents);
 * }</pre>
 * 
 * <h2>Event Monitoring</h2>
 * 
 * <h3>Emission Metrics</h3>
 * <p>Built-in monitoring and metrics for event emission:</p>
 * <ul>
 *   <li><strong>Throughput</strong> - Events per second emission rate</li>
 *   <li><strong>Latency</strong> - Time from event creation to emission</li>
 *   <li><strong>Success Rate</strong> - Percentage of successful emissions</li>
 *   <li><strong>Error Rate</strong> - Failed emission tracking</li>
 * </ul>
 * 
 * <h3>Health Monitoring</h3>
 * <pre>{@code
 * // Monitor event emission health
 * EventEmissionMonitor monitor = new EventEmissionMonitor(emitter)
 *     .withMetricsInterval(Duration.ofMinutes(1))
 *     .withHealthCheckInterval(Duration.ofSeconds(30))
 *     .withAlertThreshold(0.95); // 95% success rate threshold
 * 
 * // Register health check
 * monitor.onHealthChange(status -> {
 *     if (status == HealthStatus.UNHEALTHY) {
 *         alertingService.sendAlert("Event emission unhealthy");
 *     }
 * });
 * }</pre>
 * 
 * <h2>Configuration</h2>
 * 
 * <p>Event infrastructure is configurable through properties:</p>
 * <pre>{@code
 * # Event emission settings
 * bytehot.events.emission.strategy=asynchronous
 * bytehot.events.emission.batch.size=100
 * bytehot.events.emission.batch.timeout.ms=1000
 * bytehot.events.emission.thread.pool.size=4
 * 
 * # External integration
 * bytehot.events.webhook.enabled=true
 * bytehot.events.webhook.url=https://api.example.com/events
 * bytehot.events.mq.enabled=false
 * bytehot.events.mq.connection=amqp://localhost:5672
 * 
 * # Monitoring
 * bytehot.events.monitoring.enabled=true
 * bytehot.events.metrics.interval.seconds=60
 * }</pre>
 * 
 * @author rydnr
 * @since 2025-06-07
 * @version 1.0
 */
package org.acmsl.bytehot.infrastructure.events;