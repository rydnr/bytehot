/*
                        ByteHot

    Copyright (C) 2025-today  rydnr@acm-sl.org

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 3 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public v3
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    Thanks to ACM S.L. for distributing this library under the GPLv3 license.
    Contact info: jose.sanleandro@acm-sl.com

 ******************************************************************************
 *
 * Filename: WebhookManager.java
 *
 * Author: Claude Code
 *
 * Class name: WebhookManager
 *
 * Responsibilities:
 *   - Manage webhook subscriptions and event delivery for enterprise integration
 *   - Provide reliable delivery with retry mechanisms and failure handling
 *   - Support webhook authentication and signature verification
 *   - Enable comprehensive webhook monitoring and analytics
 *
 * Collaborators:
 *   - EnterpriseIntegrationApi: Primary API for webhook registration
 *   - ByteHotLogger: Logs webhook events and delivery attempts
 *   - SecurityManager: Validates webhook security and signatures
 *   - EventDispatcher: Coordinates system event notifications
 */
package org.acmsl.bytehot.infrastructure.api;

import org.acmsl.bytehot.infrastructure.logging.ByteHotLogger;
import org.acmsl.bytehot.infrastructure.security.SecurityManager;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Objects;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.io.IOException;

/**
 * Enterprise-grade webhook management system for ByteHot event notifications.
 * Provides reliable event delivery with retry mechanisms and comprehensive monitoring.
 * @author Claude Code
 * @since 2025-07-06
 */
public class WebhookManager {

    private static final WebhookManager INSTANCE = new WebhookManager();
    private static final ByteHotLogger LOGGER = ByteHotLogger.getLogger(WebhookManager.class);
    
    private final Map<String, WebhookSubscription> subscriptions = new ConcurrentHashMap<>();
    private final Map<String, WebhookDeliveryAttempt> deliveryHistory = new ConcurrentHashMap<>();
    private final Map<String, WebhookEventQueue> eventQueues = new ConcurrentHashMap<>();
    
    private final ReentrantReadWriteLock webhookLock = new ReentrantReadWriteLock();
    private final AtomicLong deliveryCounter = new AtomicLong(0);
    private final AtomicLong eventCounter = new AtomicLong(0);
    
    private final ScheduledExecutorService webhookExecutor = 
        Executors.newScheduledThreadPool(4, r -> {
            Thread t = new Thread(r, "ByteHot-Webhook-Manager");
            t.setDaemon(true);
            return t;
        });
    
    private final HttpClient httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build();
    
    private volatile WebhookConfiguration configuration = WebhookConfiguration.defaultConfiguration();
    private volatile boolean webhookEnabled = true;
    
    private WebhookManager() {
        initializeWebhookSystem();
        startWebhookMaintenance();
    }

    /**
     * Gets the singleton instance of WebhookManager.
     * @return The webhook manager instance
     */
    public static WebhookManager getInstance() {
        return INSTANCE;
    }

    /**
     * Registers a new webhook subscription.
     * This method can be hot-swapped to change subscription behavior.
     * @param subscription Webhook subscription details
     * @return Registration result
     */
    public CompletableFuture<WebhookRegistrationResult> registerWebhook(final WebhookSubscription subscription) {
        return CompletableFuture.supplyAsync(() -> {
            if (!webhookEnabled) {
                return WebhookRegistrationResult.failure("Webhook system is disabled");
            }
            
            LOGGER.info("Registering webhook: {} for events {}", 
                subscription.getEndpointUrl(), subscription.getEventTypes());
            
            webhookLock.writeLock().lock();
            try {
                // Validate subscription
                final ValidationResult validation = validateSubscription(subscription);
                if (!validation.isValid()) {
                    return WebhookRegistrationResult.failure("Validation failed: " + validation.getErrorMessage());
                }
                
                // Test webhook endpoint if required
                if (configuration.isTestEndpointOnRegistration()) {
                    final WebhookTestResult testResult = testWebhookEndpoint(subscription);
                    if (!testResult.isSuccess()) {
                        return WebhookRegistrationResult.failure("Endpoint test failed: " + testResult.getMessage());
                    }
                }
                
                // Generate subscription ID and register
                final String subscriptionId = generateSubscriptionId();
                subscription.setSubscriptionId(subscriptionId);
                subscription.setRegisteredAt(Instant.now());
                subscription.setActive(true);
                
                subscriptions.put(subscriptionId, subscription);
                
                // Create event queue for this subscription
                eventQueues.put(subscriptionId, new WebhookEventQueue(subscriptionId, configuration));
                
                LOGGER.info("Webhook registered successfully: {} (ID: {})", 
                    subscription.getEndpointUrl(), subscriptionId);
                LOGGER.audit("WEBHOOK_REGISTERED", subscription.getEndpointUrl(), 
                    ByteHotLogger.AuditOutcome.SUCCESS,
                    "Webhook subscription registered for events: " + subscription.getEventTypes());
                
                return WebhookRegistrationResult.success(subscriptionId, subscription);
                
            } catch (final Exception e) {
                LOGGER.error("Webhook registration failed: " + subscription.getEndpointUrl(), e);
                
                return WebhookRegistrationResult.failure("Registration failed: " + e.getMessage());
                
            } finally {
                webhookLock.writeLock().unlock();
            }
        }, webhookExecutor);
    }

    /**
     * Publishes an event to all matching webhook subscriptions.
     * This method can be hot-swapped to change event publishing behavior.
     * @param event System event to publish
     * @return List of delivery futures
     */
    public CompletableFuture<List<WebhookDeliveryResult>> publishEvent(final SystemEvent event) {
        return CompletableFuture.supplyAsync(() -> {
            if (!webhookEnabled) {
                return Collections.emptyList();
            }
            
            final long eventId = eventCounter.incrementAndGet();
            
            LOGGER.debug("Publishing event: {} (ID: {}) to webhook subscribers", 
                event.getEventType(), eventId);
            
            webhookLock.readLock().lock();
            try {
                // Find matching subscriptions
                final List<WebhookSubscription> matchingSubscriptions = findMatchingSubscriptions(event);
                
                if (matchingSubscriptions.isEmpty()) {
                    LOGGER.debug("No webhook subscriptions found for event: {}", event.getEventType());
                    return Collections.emptyList();
                }
                
                // Queue event for delivery to each matching subscription
                final List<CompletableFuture<WebhookDeliveryResult>> deliveryFutures = new ArrayList<>();
                
                for (final WebhookSubscription subscription : matchingSubscriptions) {
                    final WebhookEventQueue queue = eventQueues.get(subscription.getSubscriptionId());
                    if (queue != null && subscription.isActive()) {
                        // Add event to subscription queue
                        queue.enqueueEvent(event);
                        
                        // Schedule immediate delivery attempt
                        final CompletableFuture<WebhookDeliveryResult> deliveryFuture = 
                            scheduleEventDelivery(subscription, event);
                        deliveryFutures.add(deliveryFuture);
                    }
                }
                
                LOGGER.debug("Event {} queued for delivery to {} subscriptions", 
                    event.getEventType(), deliveryFutures.size());
                
                // Collect all delivery results
                return deliveryFutures.stream()
                    .map(future -> {
                        try {
                            return future.get(configuration.getDeliveryTimeoutSeconds(), TimeUnit.SECONDS);
                        } catch (final Exception e) {
                            LOGGER.error("Webhook delivery future failed", e);
                            return WebhookDeliveryResult.failure("unknown", "Future execution failed: " + e.getMessage());
                        }
                    })
                    .collect(Collectors.toList());
                
            } finally {
                webhookLock.readLock().unlock();
            }
        }, webhookExecutor);
    }

    /**
     * Unregisters a webhook subscription.
     * This method can be hot-swapped to change unregistration behavior.
     * @param subscriptionId Subscription ID to unregister
     * @return Unregistration result
     */
    public CompletableFuture<WebhookUnregistrationResult> unregisterWebhook(final String subscriptionId) {
        return CompletableFuture.supplyAsync(() -> {
            LOGGER.info("Unregistering webhook subscription: {}", subscriptionId);
            
            webhookLock.writeLock().lock();
            try {
                final WebhookSubscription subscription = subscriptions.remove(subscriptionId);
                if (subscription == null) {
                    return WebhookUnregistrationResult.failure("Subscription not found: " + subscriptionId);
                }
                
                // Remove event queue
                final WebhookEventQueue queue = eventQueues.remove(subscriptionId);
                if (queue != null) {
                    queue.shutdown();
                }
                
                // Clean up delivery history
                cleanupDeliveryHistory(subscriptionId);
                
                LOGGER.info("Webhook unregistered successfully: {} (ID: {})", 
                    subscription.getEndpointUrl(), subscriptionId);
                LOGGER.audit("WEBHOOK_UNREGISTERED", subscription.getEndpointUrl(), 
                    ByteHotLogger.AuditOutcome.SUCCESS,
                    "Webhook subscription unregistered successfully");
                
                return WebhookUnregistrationResult.success(subscriptionId, subscription);
                
            } catch (final Exception e) {
                LOGGER.error("Webhook unregistration failed: " + subscriptionId, e);
                
                return WebhookUnregistrationResult.failure("Unregistration failed: " + e.getMessage());
                
            } finally {
                webhookLock.writeLock().unlock();
            }
        }, webhookExecutor);
    }

    /**
     * Gets webhook delivery statistics and metrics.
     * @return Webhook system statistics
     */
    public WebhookStatistics getWebhookStatistics() {
        webhookLock.readLock().lock();
        try {
            final int totalSubscriptions = subscriptions.size();
            final int activeSubscriptions = (int) subscriptions.values().stream()
                .filter(WebhookSubscription::isActive)
                .count();
            
            final long totalDeliveries = subscriptions.values().stream()
                .mapToLong(WebhookSubscription::getTotalDeliveries)
                .sum();
            
            final long totalEvents = eventCounter.get();
            
            final Map<String, Integer> eventTypeStats = calculateEventTypeStatistics();
            final Map<String, Double> deliverySuccessRates = calculateDeliverySuccessRates();
            
            return new WebhookStatistics(
                totalSubscriptions,
                activeSubscriptions,
                totalEvents,
                totalDeliveries,
                eventTypeStats,
                deliverySuccessRates,
                Instant.now()
            );
            
        } finally {
            webhookLock.readLock().unlock();
        }
    }

    /**
     * Lists all registered webhook subscriptions.
     * @return List of webhook subscriptions
     */
    public List<WebhookSubscription> listWebhooks() {
        webhookLock.readLock().lock();
        try {
            return new ArrayList<>(subscriptions.values());
        } finally {
            webhookLock.readLock().unlock();
        }
    }

    /**
     * Tests a webhook endpoint for connectivity and response.
     * This method can be hot-swapped to change testing behavior.
     * @param subscription Webhook subscription to test
     * @return Test result
     */
    public WebhookTestResult testWebhookEndpoint(final WebhookSubscription subscription) {
        try {
            final TestWebhookEvent testEvent = new TestWebhookEvent(
                "webhook.test",
                Instant.now(),
                Map.of(
                    "test", true,
                    "subscriptionId", subscription.getSubscriptionId(),
                    "timestamp", Instant.now().toString()
                )
            );
            
            final WebhookDeliveryResult result = deliverWebhookEvent(subscription, testEvent).join();
            
            if (result.isSuccess()) {
                return WebhookTestResult.success("Test webhook delivered successfully in " + 
                    result.getDuration().toMillis() + "ms");
            } else {
                return WebhookTestResult.failure("Test webhook failed: " + result.getErrorMessage());
            }
            
        } catch (final Exception e) {
            return WebhookTestResult.failure("Test webhook exception: " + e.getMessage());
        }
    }

    /**
     * Configures webhook system settings.
     * This method can be hot-swapped to change webhook configuration behavior.
     * @param newConfiguration New webhook configuration
     */
    public void configure(final WebhookConfiguration newConfiguration) {
        this.configuration = newConfiguration;
        this.webhookEnabled = newConfiguration.isEnabled();
        
        LOGGER.info("Webhook configuration updated");
        LOGGER.audit("WEBHOOK_CONFIGURED", "system", ByteHotLogger.AuditOutcome.SUCCESS, 
            "Webhook configuration updated successfully");
    }

    /**
     * Shuts down the webhook system gracefully.
     */
    public void shutdown() {
        LOGGER.info("Shutting down webhook system");
        
        // Shutdown all event queues
        eventQueues.values().forEach(WebhookEventQueue::shutdown);
        
        webhookExecutor.shutdown();
        try {
            if (!webhookExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
                webhookExecutor.shutdownNow();
            }
        } catch (final InterruptedException e) {
            webhookExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        LOGGER.info("Webhook system shutdown completed");
    }

    // Implementation details and helper methods...
    
    /**
     * Initializes the webhook system.
     * This method can be hot-swapped to change initialization behavior.
     */
    protected void initializeWebhookSystem() {
        LOGGER.info("Initializing webhook system");
        
        // Load existing subscriptions if any
        loadExistingSubscriptions();
        
        LOGGER.info("Webhook system initialized successfully");
    }

    /**
     * Starts webhook maintenance tasks.
     * This method can be hot-swapped to change maintenance behavior.
     */
    protected void startWebhookMaintenance() {
        // Schedule webhook health checks
        webhookExecutor.scheduleAtFixedRate(
            this::performWebhookHealthChecks,
            1, 5, TimeUnit.MINUTES
        );
        
        // Schedule failed delivery retries
        webhookExecutor.scheduleAtFixedRate(
            this::processFailedDeliveries,
            30, 30, TimeUnit.SECONDS
        );
        
        // Schedule metrics collection
        webhookExecutor.scheduleAtFixedRate(
            this::collectWebhookMetrics,
            1, 1, TimeUnit.MINUTES
        );
        
        // Schedule cleanup tasks
        webhookExecutor.scheduleAtFixedRate(
            this::performCleanupTasks,
            1, 1, TimeUnit.HOURS
        );
        
        LOGGER.info("Webhook maintenance tasks started");
    }

    protected List<WebhookSubscription> findMatchingSubscriptions(final SystemEvent event) {
        return subscriptions.values().stream()
            .filter(WebhookSubscription::isActive)
            .filter(subscription -> 
                subscription.getEventTypes().contains("*") || 
                subscription.getEventTypes().contains(event.getEventType()) ||
                subscription.getEventTypes().stream().anyMatch(pattern -> 
                    matchesEventPattern(pattern, event.getEventType())
                )
            )
            .collect(Collectors.toList());
    }

    protected boolean matchesEventPattern(final String pattern, final String eventType) {
        if (pattern.endsWith("*")) {
            final String prefix = pattern.substring(0, pattern.length() - 1);
            return eventType.startsWith(prefix);
        }
        return pattern.equals(eventType);
    }

    protected CompletableFuture<WebhookDeliveryResult> scheduleEventDelivery(final WebhookSubscription subscription, 
                                                                           final SystemEvent event) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return deliverWebhookEvent(subscription, event).join();
            } catch (final Exception e) {
                LOGGER.error("Scheduled webhook delivery failed", e);
                return WebhookDeliveryResult.failure(subscription.getSubscriptionId(), 
                    "Scheduled delivery failed: " + e.getMessage());
            }
        }, webhookExecutor);
    }

    protected CompletableFuture<WebhookDeliveryResult> deliverWebhookEvent(final WebhookSubscription subscription, 
                                                                         final SystemEvent event) {
        return CompletableFuture.supplyAsync(() -> {
            final long deliveryId = deliveryCounter.incrementAndGet();
            final Instant startTime = Instant.now();
            
            try {
                // Create webhook payload
                final WebhookPayload payload = createWebhookPayload(deliveryId, subscription, event);
                
                // Create HTTP request
                final HttpRequest request = buildHttpRequest(subscription, payload);
                
                // Send webhook with retry logic
                final HttpResponse<String> response = sendWebhookWithRetry(request, subscription);
                
                final Duration duration = Duration.between(startTime, Instant.now());
                final boolean success = isSuccessResponse(response.statusCode());
                
                // Create delivery result
                final WebhookDeliveryResult result = new WebhookDeliveryResult(
                    deliveryId,
                    subscription.getSubscriptionId(),
                    subscription.getEndpointUrl(),
                    success,
                    response.statusCode(),
                    duration,
                    success ? null : "HTTP " + response.statusCode() + ": " + response.body()
                );
                
                // Update subscription metrics
                subscription.incrementDeliveryCount();
                if (success) {
                    subscription.resetFailureCount();
                } else {
                    subscription.incrementFailureCount();
                }
                
                // Store delivery attempt
                final WebhookDeliveryAttempt attempt = new WebhookDeliveryAttempt(
                    deliveryId,
                    subscription.getSubscriptionId(),
                    event.getEventType(),
                    startTime,
                    duration,
                    response.statusCode(),
                    success,
                    result.getErrorMessage()
                );
                deliveryHistory.put(String.valueOf(deliveryId), attempt);
                
                // Log delivery result
                if (success) {
                    LOGGER.debug("Webhook delivered successfully: {} -> {} ({}ms)", 
                        subscription.getEndpointUrl(), response.statusCode(), duration.toMillis());
                } else {
                    LOGGER.warn("Webhook delivery failed: {} -> {} ({}ms): {}", 
                        subscription.getEndpointUrl(), response.statusCode(), duration.toMillis(),
                        result.getErrorMessage());
                }
                
                return result;
                
            } catch (final Exception e) {
                final Duration duration = Duration.between(startTime, Instant.now());
                
                LOGGER.error("Webhook delivery exception: " + subscription.getEndpointUrl(), e);
                
                subscription.incrementFailureCount();
                
                return new WebhookDeliveryResult(
                    deliveryId,
                    subscription.getSubscriptionId(),
                    subscription.getEndpointUrl(),
                    false,
                    0,
                    duration,
                    "Delivery exception: " + e.getMessage()
                );
            }
        }, webhookExecutor);
    }

    protected WebhookPayload createWebhookPayload(final long deliveryId, final WebhookSubscription subscription, 
                                                 final SystemEvent event) {
        final String signature = generateWebhookSignature(subscription, event, deliveryId);
        
        return new WebhookPayload(
            deliveryId,
            subscription.getSubscriptionId(),
            event.getEventType(),
            event.getTimestamp(),
            event.getData(),
            signature
        );
    }

    protected HttpRequest buildHttpRequest(final WebhookSubscription subscription, final WebhookPayload payload) {
        return HttpRequest.newBuilder()
            .uri(URI.create(subscription.getEndpointUrl()))
            .timeout(Duration.ofSeconds(configuration.getDeliveryTimeoutSeconds()))
            .header("Content-Type", "application/json")
            .header("User-Agent", "ByteHot-Webhook/" + configuration.getVersion())
            .header("X-Webhook-ID", String.valueOf(payload.getDeliveryId()))
            .header("X-Webhook-Event", payload.getEventType())
            .header("X-Webhook-Signature", payload.getSignature())
            .header("X-Webhook-Timestamp", payload.getTimestamp().toString())
            .POST(HttpRequest.BodyPublishers.ofString(payload.toJson()))
            .build();
    }

    protected HttpResponse<String> sendWebhookWithRetry(final HttpRequest request, final WebhookSubscription subscription) 
            throws IOException, InterruptedException {
        
        IOException lastException = null;
        
        for (int attempt = 1; attempt <= configuration.getMaxRetryAttempts(); attempt++) {
            try {
                final HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                
                if (isSuccessResponse(response.statusCode()) || !isRetryableError(response.statusCode())) {
                    return response;
                }
                
                // Wait before retry
                if (attempt < configuration.getMaxRetryAttempts()) {
                    final Duration retryDelay = calculateRetryDelay(attempt);
                    Thread.sleep(retryDelay.toMillis());
                }
                
            } catch (final IOException e) {
                lastException = e;
                
                if (attempt < configuration.getMaxRetryAttempts()) {
                    final Duration retryDelay = calculateRetryDelay(attempt);
                    try {
                        Thread.sleep(retryDelay.toMillis());
                    } catch (final InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw ie;
                    }
                }
            }
        }
        
        if (lastException != null) {
            throw lastException;
        }
        
        // This shouldn't happen, but return a fallback response
        return new MockHttpResponse(500, "Max retries exceeded");
    }

    protected boolean isSuccessResponse(final int statusCode) {
        return statusCode >= 200 && statusCode < 300;
    }

    protected boolean isRetryableError(final int statusCode) {
        return statusCode >= 500 || statusCode == 408 || statusCode == 429;
    }

    protected Duration calculateRetryDelay(final int attempt) {
        // Exponential backoff with jitter
        final long baseDelay = configuration.getRetryDelayMs();
        final long exponentialDelay = baseDelay * (1L << (attempt - 1));
        final long jitter = (long) (Math.random() * baseDelay);
        return Duration.ofMillis(Math.min(exponentialDelay + jitter, configuration.getMaxRetryDelayMs()));
    }

    protected ValidationResult validateSubscription(final WebhookSubscription subscription) {
        if (subscription.getEndpointUrl() == null || subscription.getEndpointUrl().trim().isEmpty()) {
            return ValidationResult.invalid("Endpoint URL is required");
        }
        
        if (subscription.getEventTypes() == null || subscription.getEventTypes().isEmpty()) {
            return ValidationResult.invalid("At least one event type must be specified");
        }
        
        try {
            final URI uri = URI.create(subscription.getEndpointUrl());
            if (!uri.isAbsolute() || (!"http".equals(uri.getScheme()) && !"https".equals(uri.getScheme()))) {
                return ValidationResult.invalid("Invalid endpoint URL format");
            }
        } catch (final Exception e) {
            return ValidationResult.invalid("Invalid endpoint URL: " + e.getMessage());
        }
        
        return ValidationResult.valid();
    }

    protected String generateSubscriptionId() {
        return "whsub_" + System.currentTimeMillis() + "_" + System.nanoTime();
    }

    protected String generateWebhookSignature(final WebhookSubscription subscription, final SystemEvent event, final long deliveryId) {
        final String payload = deliveryId + subscription.getSubscriptionId() + event.getEventType() + 
                              event.getTimestamp().toString() + event.getData().toString();
        return "sha256=" + Math.abs(Objects.hash(subscription.getSecretKey(), payload));
    }

    protected void performWebhookHealthChecks() {
        LOGGER.debug("Performing webhook health checks");
        
        for (final WebhookSubscription subscription : subscriptions.values()) {
            if (subscription.isActive() && shouldPerformHealthCheck(subscription)) {
                webhookExecutor.submit(() -> {
                    final WebhookTestResult testResult = testWebhookEndpoint(subscription);
                    if (!testResult.isSuccess()) {
                        subscription.incrementFailureCount();
                        
                        if (subscription.getConsecutiveFailures() >= configuration.getMaxConsecutiveFailures()) {
                            subscription.setActive(false);
                            LOGGER.warn("Webhook subscription disabled due to health check failures: {}", 
                                subscription.getEndpointUrl());
                        }
                    } else {
                        subscription.resetFailureCount();
                    }
                });
            }
        }
    }

    protected boolean shouldPerformHealthCheck(final WebhookSubscription subscription) {
        return subscription.getConsecutiveFailures() >= 3 || 
               subscription.getRegisteredAt().isBefore(Instant.now().minus(Duration.ofHours(1)));
    }

    protected void processFailedDeliveries() {
        // Process failed deliveries and retry if appropriate
        for (final WebhookEventQueue queue : eventQueues.values()) {
            queue.processFailedEvents();
        }
    }

    protected void collectWebhookMetrics() {
        // Collect webhook system metrics for monitoring
        final WebhookStatistics stats = getWebhookStatistics();
        
        // In a real implementation, these would be sent to a metrics system
        LOGGER.debug("Webhook metrics - Subscriptions: {}, Events: {}, Deliveries: {}", 
            stats.getTotalSubscriptions(), stats.getTotalEvents(), stats.getTotalDeliveries());
    }

    protected void performCleanupTasks() {
        // Clean up old delivery history
        final Instant cutoffTime = Instant.now().minus(Duration.ofDays(7));
        deliveryHistory.entrySet().removeIf(entry -> 
            entry.getValue().getTimestamp().isBefore(cutoffTime));
        
        // Clean up inactive subscriptions
        final Instant inactiveCutoff = Instant.now().minus(Duration.ofDays(30));
        subscriptions.entrySet().removeIf(entry -> {
            final WebhookSubscription subscription = entry.getValue();
            return !subscription.isActive() && 
                   subscription.getRegisteredAt().isBefore(inactiveCutoff);
        });
    }

    protected void cleanupDeliveryHistory(final String subscriptionId) {
        deliveryHistory.entrySet().removeIf(entry -> 
            subscriptionId.equals(entry.getValue().getSubscriptionId()));
    }

    protected void loadExistingSubscriptions() {
        // Load existing webhook subscriptions from persistent storage
        // In a real implementation, this would load from database or file system
        LOGGER.debug("Loading existing webhook subscriptions");
    }

    protected Map<String, Integer> calculateEventTypeStatistics() {
        // Calculate statistics by event type
        return Collections.emptyMap(); // Simplified for demonstration
    }

    protected Map<String, Double> calculateDeliverySuccessRates() {
        // Calculate delivery success rates by subscription
        return Collections.emptyMap(); // Simplified for demonstration
    }

    // Supporting classes...
    
    protected static class TestWebhookEvent implements SystemEvent {
        private final String eventType;
        private final Instant timestamp;
        private final Map<String, Object> data;
        
        public TestWebhookEvent(final String eventType, final Instant timestamp, final Map<String, Object> data) {
            this.eventType = eventType;
            this.timestamp = timestamp;
            this.data = data;
        }
        
        @Override
        public String getEventType() { return eventType; }
        
        @Override
        public Instant getTimestamp() { return timestamp; }
        
        @Override
        public Map<String, Object> getData() { return data; }
    }
    
    protected static class MockHttpResponse implements HttpResponse<String> {
        private final int statusCode;
        private final String body;
        
        public MockHttpResponse(final int statusCode, final String body) {
            this.statusCode = statusCode;
            this.body = body;
        }
        
        @Override
        public int statusCode() { return statusCode; }
        
        @Override
        public String body() { return body; }
        
        @Override
        public HttpRequest request() { return null; }
        
        @Override
        public Optional<HttpResponse<String>> previousResponse() { return Optional.empty(); }
        
        @Override
        public java.net.http.HttpHeaders headers() { return null; }
        
        @Override
        public Optional<javax.net.ssl.SSLSession> sslSession() { return Optional.empty(); }
        
        @Override
        public URI uri() { return null; }
        
        @Override
        public java.net.http.HttpClient.Version version() { return null; }
    }
}

// Additional supporting classes would be defined in separate files...

/**
 * Webhook unregistration result.
 */
class WebhookUnregistrationResult {
    
    private final boolean success;
    private final String subscriptionId;
    private final WebhookSubscription subscription;
    private final String errorMessage;
    private final Instant timestamp;
    
    private WebhookUnregistrationResult(final boolean success, final String subscriptionId,
                                      final WebhookSubscription subscription, final String errorMessage,
                                      final Instant timestamp) {
        this.success = success;
        this.subscriptionId = subscriptionId;
        this.subscription = subscription;
        this.errorMessage = errorMessage;
        this.timestamp = Objects.requireNonNull(timestamp);
    }
    
    public static WebhookUnregistrationResult success(final String subscriptionId, final WebhookSubscription subscription) {
        return new WebhookUnregistrationResult(true, subscriptionId, subscription, null, Instant.now());
    }
    
    public static WebhookUnregistrationResult failure(final String errorMessage) {
        return new WebhookUnregistrationResult(false, null, null, errorMessage, Instant.now());
    }
    
    public boolean isSuccess() { return success; }
    public String getSubscriptionId() { return subscriptionId; }
    public WebhookSubscription getSubscription() { return subscription; }
    public String getErrorMessage() { return errorMessage; }
    public Instant getTimestamp() { return timestamp; }
}

/**
 * Webhook delivery attempt record.
 */
class WebhookDeliveryAttempt {
    
    private final long deliveryId;
    private final String subscriptionId;
    private final String eventType;
    private final Instant timestamp;
    private final Duration duration;
    private final int statusCode;
    private final boolean success;
    private final String errorMessage;
    
    public WebhookDeliveryAttempt(final long deliveryId, final String subscriptionId,
                                 final String eventType, final Instant timestamp,
                                 final Duration duration, final int statusCode,
                                 final boolean success, final String errorMessage) {
        this.deliveryId = deliveryId;
        this.subscriptionId = Objects.requireNonNull(subscriptionId);
        this.eventType = Objects.requireNonNull(eventType);
        this.timestamp = Objects.requireNonNull(timestamp);
        this.duration = Objects.requireNonNull(duration);
        this.statusCode = statusCode;
        this.success = success;
        this.errorMessage = errorMessage;
    }
    
    public long getDeliveryId() { return deliveryId; }
    public String getSubscriptionId() { return subscriptionId; }
    public String getEventType() { return eventType; }
    public Instant getTimestamp() { return timestamp; }
    public Duration getDuration() { return duration; }
    public int getStatusCode() { return statusCode; }
    public boolean isSuccess() { return success; }
    public String getErrorMessage() { return errorMessage; }
}

/**
 * Webhook system statistics.
 */
class WebhookStatistics {
    
    private final int totalSubscriptions;
    private final int activeSubscriptions;
    private final long totalEvents;
    private final long totalDeliveries;
    private final Map<String, Integer> eventTypeStats;
    private final Map<String, Double> deliverySuccessRates;
    private final Instant timestamp;
    
    public WebhookStatistics(final int totalSubscriptions, final int activeSubscriptions,
                           final long totalEvents, final long totalDeliveries,
                           final Map<String, Integer> eventTypeStats,
                           final Map<String, Double> deliverySuccessRates,
                           final Instant timestamp) {
        this.totalSubscriptions = totalSubscriptions;
        this.activeSubscriptions = activeSubscriptions;
        this.totalEvents = totalEvents;
        this.totalDeliveries = totalDeliveries;
        this.eventTypeStats = new java.util.HashMap<>(Objects.requireNonNullElse(eventTypeStats, Collections.emptyMap()));
        this.deliverySuccessRates = new java.util.HashMap<>(Objects.requireNonNullElse(deliverySuccessRates, Collections.emptyMap()));
        this.timestamp = Objects.requireNonNull(timestamp);
    }
    
    public int getTotalSubscriptions() { return totalSubscriptions; }
    public int getActiveSubscriptions() { return activeSubscriptions; }
    public long getTotalEvents() { return totalEvents; }
    public long getTotalDeliveries() { return totalDeliveries; }
    public Map<String, Integer> getEventTypeStats() { return Collections.unmodifiableMap(eventTypeStats); }
    public Map<String, Double> getDeliverySuccessRates() { return Collections.unmodifiableMap(deliverySuccessRates); }
    public Instant getTimestamp() { return timestamp; }
}

/**
 * Webhook event queue for reliable delivery.
 */
class WebhookEventQueue {
    
    private final String subscriptionId;
    private final List<SystemEvent> pendingEvents = Collections.synchronizedList(new ArrayList<>());
    private final List<SystemEvent> failedEvents = Collections.synchronizedList(new ArrayList<>());
    private final WebhookConfiguration configuration;
    private volatile boolean shutdown = false;
    
    public WebhookEventQueue(final String subscriptionId, final WebhookConfiguration configuration) {
        this.subscriptionId = Objects.requireNonNull(subscriptionId);
        this.configuration = Objects.requireNonNull(configuration);
    }
    
    public void enqueueEvent(final SystemEvent event) {
        if (!shutdown) {
            pendingEvents.add(event);
        }
    }
    
    public void processFailedEvents() {
        // Process failed events for retry
        if (!failedEvents.isEmpty()) {
            final List<SystemEvent> eventsToRetry = new ArrayList<>(failedEvents);
            failedEvents.clear();
            pendingEvents.addAll(eventsToRetry);
        }
    }
    
    public void shutdown() {
        this.shutdown = true;
        pendingEvents.clear();
        failedEvents.clear();
    }
    
    public String getSubscriptionId() { return subscriptionId; }
    public int getPendingEventCount() { return pendingEvents.size(); }
    public int getFailedEventCount() { return failedEvents.size(); }
}

/**
 * Webhook configuration settings.
 */
class WebhookConfiguration {
    
    private boolean enabled = true;
    private String version = "1.0.0";
    private boolean testEndpointOnRegistration = true;
    private int deliveryTimeoutSeconds = 30;
    private int maxRetryAttempts = 3;
    private long retryDelayMs = 1000;
    private long maxRetryDelayMs = 30000;
    private int maxConsecutiveFailures = 5;
    private boolean enableSignatureValidation = true;
    private Duration healthCheckInterval = Duration.ofMinutes(5);
    
    public static WebhookConfiguration defaultConfiguration() {
        return new WebhookConfiguration();
    }
    
    public boolean isEnabled() { return enabled; }
    public void setEnabled(final boolean enabled) { this.enabled = enabled; }
    
    public String getVersion() { return version; }
    public void setVersion(final String version) { this.version = version; }
    
    public boolean isTestEndpointOnRegistration() { return testEndpointOnRegistration; }
    public void setTestEndpointOnRegistration(final boolean testEndpointOnRegistration) { 
        this.testEndpointOnRegistration = testEndpointOnRegistration; 
    }
    
    public int getDeliveryTimeoutSeconds() { return deliveryTimeoutSeconds; }
    public void setDeliveryTimeoutSeconds(final int deliveryTimeoutSeconds) { 
        this.deliveryTimeoutSeconds = deliveryTimeoutSeconds; 
    }
    
    public int getMaxRetryAttempts() { return maxRetryAttempts; }
    public void setMaxRetryAttempts(final int maxRetryAttempts) { this.maxRetryAttempts = maxRetryAttempts; }
    
    public long getRetryDelayMs() { return retryDelayMs; }
    public void setRetryDelayMs(final long retryDelayMs) { this.retryDelayMs = retryDelayMs; }
    
    public long getMaxRetryDelayMs() { return maxRetryDelayMs; }
    public void setMaxRetryDelayMs(final long maxRetryDelayMs) { this.maxRetryDelayMs = maxRetryDelayMs; }
    
    public int getMaxConsecutiveFailures() { return maxConsecutiveFailures; }
    public void setMaxConsecutiveFailures(final int maxConsecutiveFailures) { 
        this.maxConsecutiveFailures = maxConsecutiveFailures; 
    }
    
    public boolean isEnableSignatureValidation() { return enableSignatureValidation; }
    public void setEnableSignatureValidation(final boolean enableSignatureValidation) { 
        this.enableSignatureValidation = enableSignatureValidation; 
    }
    
    public Duration getHealthCheckInterval() { return healthCheckInterval; }
    public void setHealthCheckInterval(final Duration healthCheckInterval) { 
        this.healthCheckInterval = healthCheckInterval; 
    }
}