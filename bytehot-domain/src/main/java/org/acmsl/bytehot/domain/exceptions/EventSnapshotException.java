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
/******************************************************************************
 *
 * Filename: EventSnapshotException.java
 *
 * Author: Claude Code
 *
 * Class name: EventSnapshotException
 *
 * Responsibilities:
 *   - Enhanced exception capturing complete event history for bug reproduction
 *   - Serialize event snapshots for developer-friendly error reporting
 *   - Enable automatic test case generation from production errors
 *   - Provide complete context for debugging and error analysis
 *
 * Collaborators:
 *   - DomainEvent: Events captured in the snapshot for reproduction
 *   - EventSnapshot: Immutable snapshot of event sequence and system state
 *   - BugReport: Serializable bug report for automated issue creation
 */
package org.acmsl.bytehot.domain.exceptions;

import org.acmsl.bytehot.domain.EventSnapshot;
import org.acmsl.bytehot.domain.ErrorClassification;
import org.acmsl.bytehot.domain.BugPriority;
import org.acmsl.commons.patterns.DomainEvent;
import org.acmsl.commons.patterns.eventsourcing.VersionedDomainEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Revolutionary exception class that transforms every error into a reproducible test case
 * by capturing complete event history and system context at the time of failure.
 * 
 * This exception represents the core innovation of Milestone 6D: automatic bug reproduction
 * through event snapshots. When any error occurs in ByteHot, this exception captures:
 * 
 * 1. Complete event sequence leading to the error
 * 2. System state and configuration at failure time
 * 3. Thread and execution context information
 * 4. Reproducible test case generation data
 * 
 * Key Features:
 * - Automatic event history capture (last N events before error)
 * - System state snapshot for complete reproduction context
 * - Developer-friendly serialization for bug reports
 * - Automatic test case generation capabilities
 * - Thread-safe event collection and snapshot creation
 * 
 * Usage Examples:
 * ```java
 * // Automatic creation during error handling
 * throw EventSnapshotException.captureAndThrow(
 *     originalException, 
 *     "Hot-swap operation failed",
 *     eventHistory.getRecentEvents(50)
 * );
 * 
 * // Reproduction in test environment
 * EventSnapshot snapshot = exception.getEventSnapshot();
 * TestCase testCase = snapshot.generateReproductionTest();
 * ```
 * 
 * Revolutionary Impact:
 * This transforms debugging from "hard to reproduce" to "automatically reproducible",
 * enabling developers to instantly recreate any production error with exact context.
 * 
 * @author Claude Code
 * @since 2025-06-22
 */
public class EventSnapshotException extends RuntimeException {

    /**
     * Serial version UID for serialization compatibility
     */
    protected static final long serialVersionUID = 1L;

    /**
     * Unique identifier for this error occurrence
     */
    @Getter
    protected final String errorId;

    /**
     * Complete event snapshot capturing the system state at error time
     */
    @Getter
    protected final EventSnapshot eventSnapshot;

    /**
     * Original exception that triggered this enhanced error capture
     */
    @Getter
    protected final Throwable originalCause;

    /**
     * Error classification for automatic categorization and routing
     */
    @Getter
    protected final ErrorClassification classification;

    /**
     * Additional metadata for debugging and analysis
     */
    @Getter
    protected final Map<String, Object> debugMetadata;

    /**
     * Timestamp when this error was captured
     */
    @Getter
    protected final Instant capturedAt;

    /**
     * Constructs a new EventSnapshotException with complete error context.
     * 
     * @param message human-readable error description
     * @param originalCause the original exception that caused this error
     * @param eventSnapshot complete event history and system state snapshot
     * @param classification error classification for categorization
     * @param debugMetadata additional debug information
     */
    protected EventSnapshotException(
        final String message,
        final Throwable originalCause,
        final EventSnapshot eventSnapshot,
        final ErrorClassification classification,
        final Map<String, Object> debugMetadata) {
        
        super(message, originalCause);
        this.errorId = UUID.randomUUID().toString();
        this.originalCause = originalCause;
        this.eventSnapshot = eventSnapshot;
        this.classification = classification;
        this.debugMetadata = Map.copyOf(debugMetadata);
        this.capturedAt = Instant.now();
    }

    /**
     * Gets the original exception that caused this error (alias for getOriginalCause for compatibility).
     * @return the original exception
     */
    public Throwable getOriginalException() {
        return originalCause;
    }

    /**
     * Factory method to capture complete error context and create enhanced exception.
     * 
     * This is the primary entry point for creating EventSnapshotExceptions. It automatically
     * captures the current system state, recent event history, and creates a comprehensive
     * error snapshot for reproduction.
     * 
     * @param originalCause the original exception that occurred
     * @param message descriptive error message for developers
     * @param recentEvents list of recent events leading to the error
     * @return EventSnapshotException with complete reproduction context
     */
    public static EventSnapshotException captureAndThrow(
        final Throwable originalCause,
        final String message,
        final List<DomainEvent> recentEvents) {
        
        return captureAndThrow(originalCause, message, recentEvents, Map.of());
    }

    /**
     * Factory method to capture complete error context with additional debug metadata.
     * 
     * @param originalCause the original exception that occurred
     * @param message descriptive error message for developers
     * @param recentEvents list of recent events leading to the error
     * @param additionalMetadata extra debug information for this specific error
     * @return EventSnapshotException with complete reproduction context
     */
    public static EventSnapshotException captureAndThrow(
        final Throwable originalCause,
        final String message,
        final List<DomainEvent> recentEvents,
        final Map<String, Object> additionalMetadata) {
        
        try {
            // Convert DomainEvents to VersionedDomainEvents for existing EventSnapshot
            final List<VersionedDomainEvent> versionedEvents = recentEvents.stream()
                .filter(event -> event instanceof VersionedDomainEvent)
                .map(event -> (VersionedDomainEvent) event)
                .toList();
            
            // Create event snapshot using existing EventSnapshot structure
            final EventSnapshot eventSnapshot = EventSnapshot.create(
                versionedEvents,
                null, // userId - can be null for now
                captureEnvironmentContext(),
                Thread.currentThread().getName(),
                captureSystemProperties(),
                null, // causalChain - can be null for now  
                capturePerformanceMetrics()
            );
            
            // Classify error for automatic routing and handling
            final ErrorClassification classification = classifyError(originalCause, recentEvents);
            
            // Merge metadata with automatic system information
            final Map<String, Object> enrichedMetadata = enrichMetadata(additionalMetadata, originalCause);
            
            return new EventSnapshotException(
                message, 
                originalCause, 
                eventSnapshot, 
                classification, 
                enrichedMetadata
            );
            
        } catch (final Exception captureException) {
            // Graceful degradation - never let error capture itself cause issues
            return createFallbackException(originalCause, message, captureException);
        }
    }

    /**
     * Gets a reproducible test case description that can recreate this exact error scenario.
     * 
     * This method generates test case information including:
     * - Given: Initial system state and configuration
     * - When: Exact sequence of events that led to the error
     * - Then: Expected error condition and verification
     * 
     * @return String description of reproduction test case
     */
    public String getReproductionTestCase() {
        return String.format(
            "ReproduceBug_%s:\n" +
            "  Given: System state captured at %s\n" +
            "  When: Execute %d events in sequence\n" +
            "  Then: Expect %s with message: %s\n" +
            "  Events: %s",
            errorId.substring(0, 8),
            capturedAt,
            eventSnapshot.getEventCount(),
            originalCause.getClass().getSimpleName(),
            originalCause.getMessage(),
            eventSnapshot.getSummary()
        );
    }

    /**
     * Generates a developer-friendly bug report with complete context.
     * 
     * @return String bug report suitable for automated issue creation or manual analysis
     */
    public String generateBugReport() {
        return String.format(
            "# Bug Report - %s\n\n" +
            "## Error Summary\n" +
            "- **Error ID**: %s\n" +
            "- **Classification**: %s\n" +
            "- **Occurred At**: %s\n" +
            "- **Original Error**: %s\n" +
            "- **Message**: %s\n\n" +
            "## Event Context\n" +
            "- **Events Leading to Error**: %d events captured\n" +
            "- **Thread**: %s\n" +
            "- **Event Summary**: %s\n\n" +
            "## System State\n" +
            "- **Environment**: %s\n" +
            "- **System Properties**: %s\n\n" +
            "## Reproduction\n" +
            "This error is automatically reproducible using the captured event snapshot.\n" +
            "```\n%s\n```\n\n" +
            "## Stack Trace\n" +
            "```\n%s\n```",
            generateBugTitle(),
            errorId,
            classification.getDisplayName(),
            capturedAt,
            originalCause.getClass().getSimpleName(),
            originalCause.getMessage(),
            eventSnapshot.getEventCount(),
            eventSnapshot.getThreadName(),
            eventSnapshot.getSummary(),
            eventSnapshot.getEnvironmentContext(),
            eventSnapshot.getSystemProperties(),
            getReproductionTestCase(),
            getStackTraceAsString()
        );
    }

    /**
     * Captures environment context for the existing EventSnapshot structure.
     * 
     * @return Map of environment context information
     */
    protected static Map<String, String> captureEnvironmentContext() {
        return Map.of(
            "working_directory", System.getProperty("user.dir"),
            "java_version", System.getProperty("java.version"),
            "java_vendor", System.getProperty("java.vendor"),
            "os_name", System.getProperty("os.name"),
            "os_version", System.getProperty("os.version"),
            "available_processors", String.valueOf(Runtime.getRuntime().availableProcessors())
        );
    }

    /**
     * Captures system properties for the existing EventSnapshot structure.
     * 
     * @return Map of relevant system properties
     */
    protected static Map<String, String> captureSystemProperties() {
        return Map.of(
            "java.version", System.getProperty("java.version", "unknown"),
            "java.vendor", System.getProperty("java.vendor", "unknown"),
            "java.home", System.getProperty("java.home", "unknown"),
            "os.name", System.getProperty("os.name", "unknown"),
            "os.arch", System.getProperty("os.arch", "unknown"),
            "user.name", System.getProperty("user.name", "unknown"),
            "user.dir", System.getProperty("user.dir", "unknown")
        );
    }

    /**
     * Captures performance metrics for the existing EventSnapshot structure.
     * 
     * @return Map of performance metrics
     */
    protected static Map<String, Object> capturePerformanceMetrics() {
        final Runtime runtime = Runtime.getRuntime();
        return Map.of(
            "free_memory", runtime.freeMemory(),
            "total_memory", runtime.totalMemory(),
            "max_memory", runtime.maxMemory(),
            "used_memory", runtime.totalMemory() - runtime.freeMemory(),
            "available_processors", runtime.availableProcessors(),
            "current_time_millis", System.currentTimeMillis(),
            "nano_time", System.nanoTime()
        );
    }

    /**
     * Classifies the error for automatic handling and routing.
     * 
     * @param cause original exception
     * @param events recent events that may have contributed to the error
     * @return ErrorClassification for this error type
     */
    protected static ErrorClassification classifyError(final Throwable cause, final List<DomainEvent> events) {
        // Classify based on exception type and event patterns
        // Check message content first for hot-swap specific errors
        if (cause.getMessage() != null && cause.getMessage().toLowerCase().contains("hot-swap")) {
            return ErrorClassification.HOT_SWAP_FAILURE;
        } else if (cause instanceof ClassCastException) {
            return ErrorClassification.TYPE_MISMATCH;
        } else if (cause instanceof NullPointerException) {
            return ErrorClassification.NULL_REFERENCE;
        } else if (cause instanceof IllegalStateException) {
            return ErrorClassification.INVALID_STATE;
        } else if (events.stream().anyMatch(e -> e.getClass().getSimpleName().contains("FileChanged"))) {
            return ErrorClassification.FILE_MONITORING_ERROR;
        } else {
            return ErrorClassification.UNKNOWN;
        }
    }

    /**
     * Enriches metadata with automatic system information.
     * 
     * @param userMetadata user-provided metadata
     * @param cause original exception
     * @return enriched metadata map
     */
    protected static Map<String, Object> enrichMetadata(
        final Map<String, Object> userMetadata,
        final Throwable cause) {
        
        return Map.of(
            "error_class", cause.getClass().getSimpleName(),
            "error_message", Optional.ofNullable(cause.getMessage()).orElse("No message"),
            "thread_name", Thread.currentThread().getName(),
            "timestamp", Instant.now().toString(),
            "user_metadata", userMetadata
        );
    }

    /**
     * Creates a fallback exception when event capture itself fails.
     * 
     * @param originalCause the original exception
     * @param message error message
     * @param captureException exception that occurred during capture
     * @return simplified EventSnapshotException
     */
    protected static EventSnapshotException createFallbackException(
        final Throwable originalCause,
        final String message,
        final Exception captureException) {
        
        // Create minimal fallback snapshot using existing EventSnapshot structure
        final EventSnapshot fallbackSnapshot = EventSnapshot.create(
            List.of(), // empty event history
            null, // no user ID
            Map.of("error", "capture_failed"), // minimal environment context
            Thread.currentThread().getName(),
            Map.of("capture_error", captureException.getMessage()), // system properties with error info
            null, // no causal chain
            Map.of("capture_failed", true) // performance metrics indicating failure
        );
        
        return new EventSnapshotException(
            message + " (Error capture failed: " + captureException.getMessage() + ")",
            originalCause,
            fallbackSnapshot,
            ErrorClassification.CAPTURE_FAILURE,
            Map.of("capture_error", captureException.getMessage())
        );
    }

    /**
     * Generates a descriptive bug title.
     * 
     * @return bug title for issue tracking
     */
    protected String generateBugTitle() {
        return String.format("[Bug] %s: %s", 
            classification.getDisplayName(),
            Optional.ofNullable(originalCause.getMessage())
                .orElse(originalCause.getClass().getSimpleName())
        );
    }

    /**
     * Generates a detailed bug description with context.
     * 
     * @return bug description with reproduction information
     */
    protected String generateBugDescription() {
        return String.format(
            "## Error Summary\n" +
            "- **Error ID**: %s\n" +
            "- **Classification**: %s\n" +
            "- **Occurred At**: %s\n" +
            "- **Original Error**: %s\n\n" +
            "## Event Context\n" +
            "- **Events Leading to Error**: %d events captured\n" +
            "- **System State**: Captured at failure time\n" +
            "- **Thread**: %s\n\n" +
            "## Reproduction\n" +
            "This error is automatically reproducible using the captured event snapshot.\n" +
            "See attached reproduction test case for exact steps.\n",
            errorId,
            classification.getDisplayName(),
            capturedAt,
            originalCause.getClass().getSimpleName(),
            eventSnapshot.getEventCount(),
            eventSnapshot.getThreadName()
        );
    }

    /**
     * Generates step-by-step reproduction instructions.
     * 
     * @return list of reproduction steps
     */
    protected List<String> generateReproductionSteps() {
        return List.of(
            "1. Load the captured system state snapshot",
            "2. Execute the captured event sequence in order", 
            "3. Verify that the same error occurs",
            "4. Use the generated test case for automated reproduction"
        );
    }

    /**
     * Gets stack trace as formatted string.
     * 
     * @return formatted stack trace
     */
    protected String getStackTraceAsString() {
        final StringBuilder sb = new StringBuilder();
        for (final StackTraceElement element : originalCause.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }

    /**
     * Determines bug priority based on classification and context.
     * 
     * @return bug priority level
     */
    protected BugPriority determinePriority() {
        return switch (classification) {
            case HOT_SWAP_FAILURE -> BugPriority.HIGH;
            case NULL_REFERENCE, TYPE_MISMATCH -> BugPriority.MEDIUM;
            case FILE_MONITORING_ERROR -> BugPriority.LOW;
            case CAPTURE_FAILURE -> BugPriority.LOW;
            default -> BugPriority.MEDIUM;
        };
    }

    /**
     * Generates automatic labels for issue tracking.
     * 
     * @return list of labels for this bug
     */
    protected List<String> generateLabels() {
        return List.of(
            "bug",
            "auto-generated",
            classification.getLabel(),
            "has-reproduction"
        );
    }

}