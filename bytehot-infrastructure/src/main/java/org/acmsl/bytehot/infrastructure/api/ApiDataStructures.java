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
 * Filename: ApiDataStructures.java
 *
 * Author: Claude Code
 *
 * Class name: ApiDataStructures
 *
 * Responsibilities:
 *   - Define data structures for API requests and responses
 *   - Provide webhook subscription and delivery models
 *   - Support client registration and authentication models
 *   - Enable comprehensive API system status and metrics reporting
 *
 * Collaborators:
 *   - EnterpriseIntegrationApi: Uses these structures for API operations
 *   - SecurityManager: Authentication and authorization context
 *   - WebhookManager: Webhook subscription and delivery tracking
 */
package org.acmsl.bytehot.infrastructure.api;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Objects;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * API request representing incoming HTTP requests to ByteHot enterprise integration endpoints.
 */
class ApiRequest {
    
    private final String requestId;
    private final String method;
    private final String path;
    private final Map<String, String> headers;
    private final Map<String, String> parameters;
    private final String body;
    private final String clientId;
    private final String apiKey;
    private final String signature;
    private final String remoteAddress;
    private final Instant timestamp;
    
    public ApiRequest(final String requestId, final String method, final String path,
                     final Map<String, String> headers, final Map<String, String> parameters,
                     final String body, final String clientId, final String apiKey,
                     final String signature, final String remoteAddress, final Instant timestamp) {
        this.requestId = Objects.requireNonNull(requestId);
        this.method = Objects.requireNonNull(method);
        this.path = Objects.requireNonNull(path);
        this.headers = new HashMap<>(Objects.requireNonNullElse(headers, Collections.emptyMap()));
        this.parameters = new HashMap<>(Objects.requireNonNullElse(parameters, Collections.emptyMap()));
        this.body = body;
        this.clientId = clientId;
        this.apiKey = apiKey;
        this.signature = signature;
        this.remoteAddress = remoteAddress;
        this.timestamp = Objects.requireNonNull(timestamp);
    }
    
    public String getRequestId() { return requestId; }
    public String getMethod() { return method; }
    public String getPath() { return path; }
    public Map<String, String> getHeaders() { return Collections.unmodifiableMap(headers); }
    public Map<String, String> getParameters() { return Collections.unmodifiableMap(parameters); }
    public String getBody() { return body; }
    public String getClientId() { return clientId; }
    public String getApiKey() { return apiKey; }
    public String getSignature() { return signature; }
    public String getRemoteAddress() { return remoteAddress; }
    public Instant getTimestamp() { return timestamp; }
    
    public String getParameter(final String name, final String defaultValue) {
        return parameters.getOrDefault(name, defaultValue);
    }
    
    public String getHeader(final String name) {
        return headers.get(name);
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, Object> getBodyAsMap() {
        // In a real implementation, this would parse JSON body
        // Simplified for demonstration
        if (body == null) {
            return Collections.emptyMap();
        }
        
        // Simulate JSON parsing
        final Map<String, Object> result = new HashMap<>();
        if (body.contains("endpointUrl")) {
            result.put("endpointUrl", "http://example.com/webhook");
            result.put("eventTypes", List.of("hotswap.*", "health.*"));
        }
        return result;
    }
}

/**
 * API response for HTTP responses from ByteHot enterprise integration endpoints.
 */
class ApiResponse {
    
    private final int statusCode;
    private final String statusMessage;
    private final Map<String, String> headers;
    private final Object data;
    private final String errorMessage;
    private final Instant timestamp;
    
    private ApiResponse(final int statusCode, final String statusMessage,
                       final Map<String, String> headers, final Object data,
                       final String errorMessage, final Instant timestamp) {
        this.statusCode = statusCode;
        this.statusMessage = Objects.requireNonNull(statusMessage);
        this.headers = new HashMap<>(Objects.requireNonNullElse(headers, Collections.emptyMap()));
        this.data = data;
        this.errorMessage = errorMessage;
        this.timestamp = Objects.requireNonNull(timestamp);
    }
    
    public static ApiResponse ok(final Object data) {
        return new ApiResponse(200, "OK", null, data, null, Instant.now());
    }
    
    public static ApiResponse created(final Object data) {
        return new ApiResponse(201, "Created", null, data, null, Instant.now());
    }
    
    public static ApiResponse badRequest(final String errorMessage) {
        return new ApiResponse(400, "Bad Request", null, null, errorMessage, Instant.now());
    }
    
    public static ApiResponse unauthorized(final String errorMessage) {
        return new ApiResponse(401, "Unauthorized", null, null, errorMessage, Instant.now());
    }
    
    public static ApiResponse notFound(final String errorMessage) {
        return new ApiResponse(404, "Not Found", null, null, errorMessage, Instant.now());
    }
    
    public static ApiResponse internalServerError(final String errorMessage) {
        return new ApiResponse(500, "Internal Server Error", null, null, errorMessage, Instant.now());
    }
    
    public static ApiResponse serviceUnavailable(final String errorMessage) {
        return new ApiResponse(503, "Service Unavailable", null, null, errorMessage, Instant.now());
    }
    
    public int getStatusCode() { return statusCode; }
    public String getStatusMessage() { return statusMessage; }
    public Map<String, String> getHeaders() { return Collections.unmodifiableMap(headers); }
    public Object getData() { return data; }
    public String getErrorMessage() { return errorMessage; }
    public Instant getTimestamp() { return timestamp; }
    
    public boolean isSuccess() {
        return statusCode >= 200 && statusCode < 300;
    }
    
    public boolean isError() {
        return statusCode >= 400;
    }
}

/**
 * Webhook subscription configuration for receiving system event notifications.
 */
class WebhookSubscription {
    
    private String subscriptionId;
    private final String endpointUrl;
    private final Set<String> eventTypes;
    private final String secretKey;
    private final String clientId;
    private Instant registeredAt;
    private volatile boolean active;
    private final AtomicInteger consecutiveFailures = new AtomicInteger(0);
    private final AtomicLong totalDeliveries = new AtomicLong(0);
    
    public WebhookSubscription(final String endpointUrl, final Set<String> eventTypes,
                              final String secretKey, final String clientId) {
        this.endpointUrl = Objects.requireNonNull(endpointUrl);
        this.eventTypes = Objects.requireNonNull(eventTypes);
        this.secretKey = Objects.requireNonNull(secretKey);
        this.clientId = Objects.requireNonNull(clientId);
        this.active = true;
    }
    
    public String getSubscriptionId() { return subscriptionId; }
    public void setSubscriptionId(final String subscriptionId) { this.subscriptionId = subscriptionId; }
    
    public String getEndpointUrl() { return endpointUrl; }
    public Set<String> getEventTypes() { return Collections.unmodifiableSet(eventTypes); }
    public String getSecretKey() { return secretKey; }
    public String getClientId() { return clientId; }
    public Instant getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(final Instant registeredAt) { this.registeredAt = registeredAt; }
    
    public boolean isActive() { return active; }
    public void setActive(final boolean active) { this.active = active; }
    
    public int getConsecutiveFailures() { return consecutiveFailures.get(); }
    public void incrementFailureCount() { consecutiveFailures.incrementAndGet(); }
    public void resetFailureCount() { consecutiveFailures.set(0); }
    
    public long getTotalDeliveries() { return totalDeliveries.get(); }
    public void incrementDeliveryCount() { totalDeliveries.incrementAndGet(); }
    
    public boolean shouldHealthCheck() {
        // Health check every hour or after 5 consecutive failures
        return consecutiveFailures.get() >= 5 || 
               (registeredAt != null && registeredAt.isBefore(Instant.now().minus(Duration.ofHours(1))));
    }
}

/**
 * Result of webhook registration operations.
 */
class WebhookRegistrationResult {
    
    private final boolean success;
    private final String subscriptionId;
    private final WebhookSubscription subscription;
    private final String errorMessage;
    private final Instant timestamp;
    
    private WebhookRegistrationResult(final boolean success, final String subscriptionId,
                                    final WebhookSubscription subscription, final String errorMessage,
                                    final Instant timestamp) {
        this.success = success;
        this.subscriptionId = subscriptionId;
        this.subscription = subscription;
        this.errorMessage = errorMessage;
        this.timestamp = Objects.requireNonNull(timestamp);
    }
    
    public static WebhookRegistrationResult success(final String subscriptionId, final WebhookSubscription subscription) {
        return new WebhookRegistrationResult(true, subscriptionId, subscription, null, Instant.now());
    }
    
    public static WebhookRegistrationResult failure(final String errorMessage) {
        return new WebhookRegistrationResult(false, null, null, errorMessage, Instant.now());
    }
    
    public boolean isSuccess() { return success; }
    public String getSubscriptionId() { return subscriptionId; }
    public WebhookSubscription getSubscription() { return subscription; }
    public String getErrorMessage() { return errorMessage; }
    public Instant getTimestamp() { return timestamp; }
}

/**
 * Result of webhook delivery attempts.
 */
class WebhookDeliveryResult {
    
    private final long deliveryId;
    private final String subscriptionId;
    private final String endpointUrl;
    private final boolean success;
    private final int statusCode;
    private final Duration duration;
    private final String errorMessage;
    private final Instant timestamp;
    
    public WebhookDeliveryResult(final long deliveryId, final String subscriptionId,
                               final String endpointUrl, final boolean success,
                               final int statusCode, final Duration duration,
                               final String errorMessage) {
        this.deliveryId = deliveryId;
        this.subscriptionId = Objects.requireNonNull(subscriptionId);
        this.endpointUrl = Objects.requireNonNull(endpointUrl);
        this.success = success;
        this.statusCode = statusCode;
        this.duration = Objects.requireNonNull(duration);
        this.errorMessage = errorMessage;
        this.timestamp = Instant.now();
    }
    
    public static WebhookDeliveryResult failure(final String subscriptionId, final String errorMessage) {
        return new WebhookDeliveryResult(0, subscriptionId, "unknown", false, 0, Duration.ZERO, errorMessage);
    }
    
    public long getDeliveryId() { return deliveryId; }
    public String getSubscriptionId() { return subscriptionId; }
    public String getEndpointUrl() { return endpointUrl; }
    public boolean isSuccess() { return success; }
    public int getStatusCode() { return statusCode; }
    public Duration getDuration() { return duration; }
    public String getErrorMessage() { return errorMessage; }
    public Instant getTimestamp() { return timestamp; }
}

/**
 * System event interface for webhook notifications.
 */
interface SystemEvent {
    String getEventType();
    Instant getTimestamp();
    Map<String, Object> getData();
}

/**
 * Webhook payload for event notifications.
 */
class WebhookPayload {
    
    private final long deliveryId;
    private final String subscriptionId;
    private final String eventType;
    private final Instant timestamp;
    private final Map<String, Object> data;
    private final String signature;
    
    public WebhookPayload(final long deliveryId, final String subscriptionId,
                         final String eventType, final Instant timestamp,
                         final Map<String, Object> data, final String signature) {
        this.deliveryId = deliveryId;
        this.subscriptionId = Objects.requireNonNull(subscriptionId);
        this.eventType = Objects.requireNonNull(eventType);
        this.timestamp = Objects.requireNonNull(timestamp);
        this.data = new HashMap<>(Objects.requireNonNullElse(data, Collections.emptyMap()));
        this.signature = Objects.requireNonNull(signature);
    }
    
    public long getDeliveryId() { return deliveryId; }
    public String getSubscriptionId() { return subscriptionId; }
    public String getEventType() { return eventType; }
    public Instant getTimestamp() { return timestamp; }
    public Map<String, Object> getData() { return Collections.unmodifiableMap(data); }
    public String getSignature() { return signature; }
    
    public String toJson() {
        // Simplified JSON serialization
        return String.format(
            "{\"deliveryId\":%d,\"subscriptionId\":\"%s\",\"eventType\":\"%s\",\"timestamp\":\"%s\",\"data\":%s,\"signature\":\"%s\"}",
            deliveryId, subscriptionId, eventType, timestamp.toString(), 
            data.toString().replace("=", ":"), signature
        );
    }
}

/**
 * Client registration request for API access.
 */
class ClientRegistration {
    
    private final String clientName;
    private final String organization;
    private final String contactEmail;
    private final Set<String> allowedEndpoints;
    private final Map<String, String> metadata;
    
    public ClientRegistration(final String clientName, final String organization,
                            final String contactEmail, final Set<String> allowedEndpoints,
                            final Map<String, String> metadata) {
        this.clientName = Objects.requireNonNull(clientName);
        this.organization = Objects.requireNonNull(organization);
        this.contactEmail = contactEmail;
        this.allowedEndpoints = Objects.requireNonNull(allowedEndpoints);
        this.metadata = new HashMap<>(Objects.requireNonNullElse(metadata, Collections.emptyMap()));
    }
    
    public String getClientName() { return clientName; }
    public String getOrganization() { return organization; }
    public String getContactEmail() { return contactEmail; }
    public Set<String> getAllowedEndpoints() { return Collections.unmodifiableSet(allowedEndpoints); }
    public Map<String, String> getMetadata() { return Collections.unmodifiableMap(metadata); }
}

/**
 * Registered API client information.
 */
class ApiClient {
    
    private final String clientId;
    private final String clientName;
    private final String organization;
    private final String apiKey;
    private final String secretKey;
    private final Set<String> allowedEndpoints;
    private final Instant registeredAt;
    private volatile boolean active;
    private final AtomicLong requestCount = new AtomicLong(0);
    private volatile Instant lastRequestAt;
    
    public ApiClient(final String clientId, final String clientName, final String organization,
                    final String apiKey, final String secretKey, final Set<String> allowedEndpoints,
                    final Instant registeredAt, final boolean active) {
        this.clientId = Objects.requireNonNull(clientId);
        this.clientName = Objects.requireNonNull(clientName);
        this.organization = Objects.requireNonNull(organization);
        this.apiKey = Objects.requireNonNull(apiKey);
        this.secretKey = Objects.requireNonNull(secretKey);
        this.allowedEndpoints = Objects.requireNonNull(allowedEndpoints);
        this.registeredAt = Objects.requireNonNull(registeredAt);
        this.active = active;
    }
    
    public String getClientId() { return clientId; }
    public String getClientName() { return clientName; }
    public String getOrganization() { return organization; }
    public String getApiKey() { return apiKey; }
    public String getSecretKey() { return secretKey; }
    public Set<String> getAllowedEndpoints() { return Collections.unmodifiableSet(allowedEndpoints); }
    public Instant getRegisteredAt() { return registeredAt; }
    
    public boolean isActive() { return active; }
    public void setActive(final boolean active) { this.active = active; }
    
    public long getRequestCount() { return requestCount.get(); }
    public void incrementRequestCount() { 
        requestCount.incrementAndGet();
        lastRequestAt = Instant.now();
    }
    
    public Instant getLastRequestAt() { return lastRequestAt; }
}

/**
 * Result of client registration operations.
 */
class ClientRegistrationResult {
    
    private final boolean success;
    private final String clientId;
    private final String apiKey;
    private final ApiClient client;
    private final String errorMessage;
    private final Instant timestamp;
    
    private ClientRegistrationResult(final boolean success, final String clientId,
                                   final String apiKey, final ApiClient client,
                                   final String errorMessage, final Instant timestamp) {
        this.success = success;
        this.clientId = clientId;
        this.apiKey = apiKey;
        this.client = client;
        this.errorMessage = errorMessage;
        this.timestamp = Objects.requireNonNull(timestamp);
    }
    
    public static ClientRegistrationResult success(final String clientId, final String apiKey, final ApiClient client) {
        return new ClientRegistrationResult(true, clientId, apiKey, client, null, Instant.now());
    }
    
    public static ClientRegistrationResult failure(final String errorMessage) {
        return new ClientRegistrationResult(false, null, null, null, errorMessage, Instant.now());
    }
    
    public boolean isSuccess() { return success; }
    public String getClientId() { return clientId; }
    public String getApiKey() { return apiKey; }
    public ApiClient getClient() { return client; }
    public String getErrorMessage() { return errorMessage; }
    public Instant getTimestamp() { return timestamp; }
}

/**
 * Authentication result for API requests.
 */
class AuthenticationResult {
    
    private final boolean success;
    private final ClientContext clientContext;
    private final String errorMessage;
    private final Instant timestamp;
    
    private AuthenticationResult(final boolean success, final ClientContext clientContext,
                               final String errorMessage, final Instant timestamp) {
        this.success = success;
        this.clientContext = clientContext;
        this.errorMessage = errorMessage;
        this.timestamp = Objects.requireNonNull(timestamp);
    }
    
    public static AuthenticationResult success(final ClientContext clientContext) {
        return new AuthenticationResult(true, clientContext, null, Instant.now());
    }
    
    public static AuthenticationResult failure(final String errorMessage) {
        return new AuthenticationResult(false, null, errorMessage, Instant.now());
    }
    
    public boolean isSuccess() { return success; }
    public ClientContext getClientContext() { return clientContext; }
    public String getErrorMessage() { return errorMessage; }
    public Instant getTimestamp() { return timestamp; }
}

/**
 * Client context for authenticated API requests.
 */
class ClientContext {
    
    private final String clientId;
    private final String clientName;
    private final String organization;
    private final Set<String> allowedEndpoints;
    private final String remoteAddress;
    private final Instant authenticatedAt;
    
    public ClientContext(final String clientId, final String clientName, final String organization,
                        final Set<String> allowedEndpoints, final String remoteAddress) {
        this.clientId = Objects.requireNonNull(clientId);
        this.clientName = Objects.requireNonNull(clientName);
        this.organization = Objects.requireNonNull(organization);
        this.allowedEndpoints = Objects.requireNonNull(allowedEndpoints);
        this.remoteAddress = remoteAddress;
        this.authenticatedAt = Instant.now();
    }
    
    public String getClientId() { return clientId; }
    public String getClientName() { return clientName; }
    public String getOrganization() { return organization; }
    public Set<String> getAllowedEndpoints() { return Collections.unmodifiableSet(allowedEndpoints); }
    public String getRemoteAddress() { return remoteAddress; }
    public Instant getAuthenticatedAt() { return authenticatedAt; }
    
    public boolean hasEndpointAccess(final String endpoint) {
        return allowedEndpoints.contains("*") || allowedEndpoints.contains(endpoint);
    }
}

/**
 * API system status and metrics.
 */
class ApiSystemStatus {
    
    private final boolean enabled;
    private final String version;
    private final Instant timestamp;
    private final long totalRequests;
    private final long totalWebhooks;
    private final int activeWebhooks;
    private final int registeredClients;
    private final Set<String> availableEndpoints;
    private final SystemHealthStatus health;
    
    public ApiSystemStatus(final boolean enabled, final String version, final Instant timestamp,
                          final long totalRequests, final long totalWebhooks, final int activeWebhooks,
                          final int registeredClients, final Set<String> availableEndpoints,
                          final SystemHealthStatus health) {
        this.enabled = enabled;
        this.version = Objects.requireNonNull(version);
        this.timestamp = Objects.requireNonNull(timestamp);
        this.totalRequests = totalRequests;
        this.totalWebhooks = totalWebhooks;
        this.activeWebhooks = activeWebhooks;
        this.registeredClients = registeredClients;
        this.availableEndpoints = Objects.requireNonNull(availableEndpoints);
        this.health = Objects.requireNonNull(health);
    }
    
    public boolean isEnabled() { return enabled; }
    public String getVersion() { return version; }
    public Instant getTimestamp() { return timestamp; }
    public long getTotalRequests() { return totalRequests; }
    public long getTotalWebhooks() { return totalWebhooks; }
    public int getActiveWebhooks() { return activeWebhooks; }
    public int getRegisteredClients() { return registeredClients; }
    public Set<String> getAvailableEndpoints() { return Collections.unmodifiableSet(availableEndpoints); }
    public SystemHealthStatus getHealth() { return health; }
    
    public Map<String, Object> toMap() {
        final Map<String, Object> result = new HashMap<>();
        result.put("enabled", enabled);
        result.put("version", version);
        result.put("timestamp", timestamp.toString());
        result.put("totalRequests", totalRequests);
        result.put("totalWebhooks", totalWebhooks);
        result.put("activeWebhooks", activeWebhooks);
        result.put("registeredClients", registeredClients);
        result.put("availableEndpoints", new ArrayList<>(availableEndpoints));
        result.put("health", health.toMap());
        return result;
    }
}

/**
 * System health status for API monitoring.
 */
class SystemHealthStatus {
    
    private final boolean healthy;
    private final String status;
    private final Map<String, Object> details;
    
    public SystemHealthStatus(final boolean healthy, final String status, final Map<String, Object> details) {
        this.healthy = healthy;
        this.status = Objects.requireNonNull(status);
        this.details = new HashMap<>(Objects.requireNonNullElse(details, Collections.emptyMap()));
    }
    
    public boolean isHealthy() { return healthy; }
    public String getStatus() { return status; }
    public Map<String, Object> getDetails() { return Collections.unmodifiableMap(details); }
    
    public Map<String, Object> toMap() {
        final Map<String, Object> result = new HashMap<>();
        result.put("healthy", healthy);
        result.put("status", status);
        result.put("details", details);
        return result;
    }
}

/**
 * Validation result for various operations.
 */
class ValidationResult {
    
    private final boolean valid;
    private final String errorMessage;
    private final List<String> validationErrors;
    
    private ValidationResult(final boolean valid, final String errorMessage, final List<String> validationErrors) {
        this.valid = valid;
        this.errorMessage = errorMessage;
        this.validationErrors = new ArrayList<>(Objects.requireNonNullElse(validationErrors, Collections.emptyList()));
    }
    
    public static ValidationResult valid() {
        return new ValidationResult(true, null, null);
    }
    
    public static ValidationResult invalid(final String errorMessage) {
        return new ValidationResult(false, errorMessage, null);
    }
    
    public static ValidationResult invalid(final List<String> validationErrors) {
        return new ValidationResult(false, null, validationErrors);
    }
    
    public boolean isValid() { return valid; }
    public String getErrorMessage() { return errorMessage; }
    public List<String> getValidationErrors() { return Collections.unmodifiableList(validationErrors); }
}

/**
 * Webhook test result for endpoint validation.
 */
class WebhookTestResult {
    
    private final boolean success;
    private final String message;
    private final int statusCode;
    private final Duration responseTime;
    private final Instant timestamp;
    
    private WebhookTestResult(final boolean success, final String message,
                            final int statusCode, final Duration responseTime,
                            final Instant timestamp) {
        this.success = success;
        this.message = Objects.requireNonNull(message);
        this.statusCode = statusCode;
        this.responseTime = responseTime;
        this.timestamp = Objects.requireNonNull(timestamp);
    }
    
    public static WebhookTestResult success(final String message) {
        return new WebhookTestResult(true, message, 200, Duration.ofMillis(100), Instant.now());
    }
    
    public static WebhookTestResult failure(final String message) {
        return new WebhookTestResult(false, message, 0, Duration.ZERO, Instant.now());
    }
    
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public int getStatusCode() { return statusCode; }
    public Duration getResponseTime() { return responseTime; }
    public Instant getTimestamp() { return timestamp; }
}

/**
 * API configuration settings.
 */
class ApiConfiguration {
    
    private boolean enabled = true;
    private String version = "1.0.0";
    private int port = 8080;
    private boolean requireSignature = true;
    private boolean requireWebhookValidation = true;
    private int webhookTimeoutSeconds = 30;
    private int maxWebhookFailures = 5;
    private Duration requestTimeout = Duration.ofSeconds(30);
    private int maxConcurrentRequests = 100;
    private boolean rateLimitingEnabled = true;
    private int rateLimitRequestsPerMinute = 1000;
    
    public static ApiConfiguration defaultConfiguration() {
        return new ApiConfiguration();
    }
    
    public boolean isEnabled() { return enabled; }
    public void setEnabled(final boolean enabled) { this.enabled = enabled; }
    
    public String getVersion() { return version; }
    public void setVersion(final String version) { this.version = version; }
    
    public int getPort() { return port; }
    public void setPort(final int port) { this.port = port; }
    
    public boolean isRequireSignature() { return requireSignature; }
    public void setRequireSignature(final boolean requireSignature) { this.requireSignature = requireSignature; }
    
    public boolean isRequireWebhookValidation() { return requireWebhookValidation; }
    public void setRequireWebhookValidation(final boolean requireWebhookValidation) { 
        this.requireWebhookValidation = requireWebhookValidation; 
    }
    
    public int getWebhookTimeoutSeconds() { return webhookTimeoutSeconds; }
    public void setWebhookTimeoutSeconds(final int webhookTimeoutSeconds) { 
        this.webhookTimeoutSeconds = webhookTimeoutSeconds; 
    }
    
    public int getMaxWebhookFailures() { return maxWebhookFailures; }
    public void setMaxWebhookFailures(final int maxWebhookFailures) { 
        this.maxWebhookFailures = maxWebhookFailures; 
    }
    
    public Duration getRequestTimeout() { return requestTimeout; }
    public void setRequestTimeout(final Duration requestTimeout) { this.requestTimeout = requestTimeout; }
    
    public int getMaxConcurrentRequests() { return maxConcurrentRequests; }
    public void setMaxConcurrentRequests(final int maxConcurrentRequests) { 
        this.maxConcurrentRequests = maxConcurrentRequests; 
    }
    
    public boolean isRateLimitingEnabled() { return rateLimitingEnabled; }
    public void setRateLimitingEnabled(final boolean rateLimitingEnabled) { 
        this.rateLimitingEnabled = rateLimitingEnabled; 
    }
    
    public int getRateLimitRequestsPerMinute() { return rateLimitRequestsPerMinute; }
    public void setRateLimitRequestsPerMinute(final int rateLimitRequestsPerMinute) { 
        this.rateLimitRequestsPerMinute = rateLimitRequestsPerMinute; 
    }
}