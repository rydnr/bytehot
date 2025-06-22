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
 * Filename: EventSnapshotException.java
 *
 * Author: Claude Code
 *
 * Class name: EventSnapshotException
 *
 * Responsibilities:
 *   - Enhanced exception that includes complete event context for reproduction
 *   - Capture comprehensive system state at time of error
 *   - Enable precise bug reproduction through event history
 *   - Provide developer-friendly error reporting with full context
 *
 * Collaborators:
 *   - EventSnapshot: Complete event history and context
 *   - ErrorContext: Environmental context at error time
 *   - CausalChain: Causal analysis of events leading to error
 *   - EventStorePort: Access to complete event history
 */
package org.acmsl.bytehot.domain;

import org.acmsl.bytehot.domain.EventSnapshot;
import org.acmsl.bytehot.domain.ErrorContext;
import org.acmsl.bytehot.domain.CausalChain;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Revolutionary exception that captures complete event context for precise bug reproduction.
 * Transforms traditional stack traces into comprehensive event-driven debugging information.
 * @author Claude Code
 * @since 2025-06-19
 */
public class EventSnapshotException extends Exception
    implements ErrorClassifiable {

    /**
     * Serial version UID for serialization
     */
    private static final long serialVersionUID = 1L;

    /**
     * Complete event snapshot at time of error
     */
    @NonNull
    private final EventSnapshot eventSnapshot;

    /**
     * Environmental context when error occurred
     */
    @NonNull
    private final ErrorContext errorContext;

    /**
     * Original exception that triggered the snapshot
     */
    @Nullable
    private final Throwable originalException;

    /**
     * Creates a new EventSnapshotException with complete context
     * @param message error message
     * @param eventSnapshot complete event history and context
     * @param errorContext environmental context
     * @param originalException the original exception (can be null)
     */
    public EventSnapshotException(
            @NonNull final String message,
            @NonNull final EventSnapshot eventSnapshot,
            @NonNull final ErrorContext errorContext,
            @Nullable final Throwable originalException) {
        super(message, originalException);
        this.eventSnapshot = eventSnapshot;
        this.errorContext = errorContext;
        this.originalException = originalException;
    }

    /**
     * Creates a new EventSnapshotException wrapping an existing exception
     * @param originalException the exception to wrap
     * @param eventSnapshot complete event history and context
     * @param errorContext environmental context
     */
    public EventSnapshotException(
            @NonNull final Throwable originalException,
            @NonNull final EventSnapshot eventSnapshot,
            @NonNull final ErrorContext errorContext) {
        this(
            "Event-driven error: " + originalException.getMessage(),
            eventSnapshot,
            errorContext,
            originalException
        );
    }

    /**
     * Gets the complete event snapshot
     * @return event snapshot with full context
     */
    @NonNull
    public EventSnapshot getEventSnapshot() {
        return eventSnapshot;
    }

    /**
     * Gets the error context
     * @return environmental context at error time
     */
    @NonNull
    public ErrorContext getErrorContext() {
        return errorContext;
    }

    /**
     * Gets the original exception if available
     * @return the wrapped exception, or null if none
     */
    @Nullable
    public Throwable getOriginalException() {
        return originalException;
    }

    /**
     * Gets the causal chain analysis if available
     * @return causal chain analysis, or null if not available
     */
    @Nullable
    public CausalChain getCausalChain() {
        return eventSnapshot.getCausalChain();
    }

    /**
     * Gets a comprehensive debugging report
     * @return detailed error report with event context
     */
    @NonNull
    public String getDebuggingReport() {
        StringBuilder report = new StringBuilder();
        
        // Header
        report.append("=== ByteHot Event-Driven Error Report ===\n\n");
        
        // Basic error information
        report.append("ERROR: ").append(getMessage()).append("\n");
        if (originalException != null) {
            report.append("ORIGINAL: ").append(originalException.getClass().getSimpleName())
                  .append(": ").append(originalException.getMessage()).append("\n");
        }
        report.append("TIME: ").append(errorContext.getCapturedAt()).append("\n\n");
        
        // Context summary
        report.append("CONTEXT SUMMARY:\n");
        report.append("- ").append(errorContext.getContextSummary()).append("\n");
        report.append("- ").append(eventSnapshot.getSummary()).append("\n\n");
        
        // Event history
        report.append("EVENT HISTORY (").append(eventSnapshot.getEventCount()).append(" events):\n");
        eventSnapshot.getEventHistory().forEach(event -> {
            report.append("  • ").append(event.getTimestamp())
                  .append(" - ").append(event.getEventType())
                  .append(" (v").append(event.getAggregateVersion()).append(")\n");
        });
        report.append("\n");
        
        // Causal analysis
        CausalChain causalChain = getCausalChain();
        if (causalChain != null) {
            report.append("CAUSAL ANALYSIS:\n");
            report.append("- ").append(causalChain.getDescription()).append("\n");
            if (!causalChain.getContributingFactors().isEmpty()) {
                report.append("- Contributing factors: ").append(causalChain.getContributingFactors()).append("\n");
            }
            causalChain.getDebuggingSuggestions().forEach(suggestion -> 
                report.append("- Suggestion: ").append(suggestion).append("\n")
            );
            report.append("\n");
        }
        
        // Memory and performance
        report.append("SYSTEM STATE:\n");
        report.append("- Memory usage: ").append(String.format("%.1f%%", errorContext.getMemoryUsagePercentage() * 100)).append("\n");
        report.append("- Thread: ").append(errorContext.getThreadName()).append(" (").append(errorContext.getThreadState()).append(")\n");
        if (errorContext.isHighMemoryUsage()) {
            report.append("- WARNING: High memory usage detected\n");
        }
        report.append("\n");
        
        // Reproduction information
        report.append("REPRODUCTION INFO:\n");
        report.append("- Snapshot ID: ").append(eventSnapshot.getSnapshotId()).append("\n");
        report.append("- Event span: ").append(eventSnapshot.getTimeSpan()).append("\n");
        report.append("- Capture location: ").append(errorContext.getCaptureLocation()).append("\n\n");
        
        // Original stack trace if available
        if (originalException != null) {
            report.append("ORIGINAL STACK TRACE:\n");
            StringWriter sw = new StringWriter();
            originalException.printStackTrace(new PrintWriter(sw));
            report.append(sw.toString()).append("\n");
        }
        
        report.append("=== End Report ===");
        return report.toString();
    }

    /**
     * Gets a concise error summary for logging
     * @return brief error description with key context
     */
    @NonNull
    public String getErrorSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("EventSnapshotException[")
            .append("events=").append(eventSnapshot.getEventCount())
            .append(", user=").append(errorContext.getUserId() != null ? 
                errorContext.getUserId().getDisplayName() : "anonymous")
            .append(", memory=").append(String.format("%.1f%%", errorContext.getMemoryUsagePercentage() * 100))
            .append(", snapshot=").append(eventSnapshot.getSnapshotId().substring(0, 8)).append("...")
            .append("]");
        
        if (originalException != null) {
            summary.append(" <- ").append(originalException.getClass().getSimpleName());
        }
        
        return summary.toString();
    }

    /**
     * Checks if this error is likely reproducible
     * @return true if event context suggests reproducibility
     */
    public boolean isLikelyReproducible() {
        CausalChain causalChain = getCausalChain();
        return eventSnapshot.getEventCount() > 0 && 
               (causalChain == null || causalChain.getConfidence() > 0.5);
    }

    /**
     * Gets suggested debugging steps based on the context
     * @return list of recommended debugging actions
     */
    public java.util.@NonNull List<String> getDebuggingSuggestions() {
        java.util.List<String> suggestions = new java.util.ArrayList<>();
        
        // Memory-related suggestions
        if (errorContext.isHighMemoryUsage()) {
            suggestions.add("Check for memory leaks - current usage is " + 
                String.format("%.1f%%", errorContext.getMemoryUsagePercentage() * 100));
        }
        
        // Event-related suggestions
        if (eventSnapshot.getEventCount() > 100) {
            suggestions.add("Large event history detected - consider event filtering or archiving");
        }
        
        // Causal analysis suggestions
        CausalChain causalChain = getCausalChain();
        if (causalChain != null) {
            suggestions.addAll(causalChain.getDebuggingSuggestions());
        }
        
        // Generic suggestions
        suggestions.add("Use snapshot ID " + eventSnapshot.getSnapshotId().substring(0, 8) + 
            "... to reproduce this exact scenario");
        
        if (eventSnapshot.getEventCount() > 0) {
            suggestions.add("Examine the last event: " + 
                eventSnapshot.getLastEvent().getEventType());
        }
        
        return suggestions;
    }

    /**
     * Enhanced toString that includes event context summary
     */
    @Override
    public String toString() {
        return getErrorSummary() + ": " + getMessage();
    }

    /**
     * Serializes the complete context to JSON for bug reports
     * @return JSON representation of the complete error context
     */
    @NonNull
    public String toJson() {
        // This would integrate with a JSON serialization library
        // For now, returning a simplified JSON-like format
        return "{\n" +
            "  \"type\": \"EventSnapshotException\",\n" +
            "  \"message\": \"" + getMessage().replace("\"", "\\\"") + "\",\n" +
            "  \"snapshotId\": \"" + eventSnapshot.getSnapshotId() + "\",\n" +
            "  \"eventCount\": " + eventSnapshot.getEventCount() + ",\n" +
            "  \"capturedAt\": \"" + errorContext.getCapturedAt() + "\",\n" +
            "  \"reproducible\": " + isLikelyReproducible() + ",\n" +
            "  \"summary\": \"" + getErrorSummary().replace("\"", "\\\"") + "\"\n" +
            "}";
    }

    /**
     * Accepts an error classifier and returns the appropriate error type.
     * Delegates to the original exception if available.
     * @param classifier the error classifier visitor
     * @return the error type for the original exception, or UNKNOWN_ERROR if no original
     */
    @Override
    public ErrorType acceptClassifier(final ErrorClassifier classifier) {
        return classifier.classifyEventSnapshotException(this);
    }

    /**
     * Accepts an error severity assessor and returns the appropriate severity.
     * Delegates to the original exception if available.
     * @param assessor the error severity assessor visitor
     * @return the error severity for the original exception, or ERROR if no original
     */
    @Override
    public ErrorSeverity acceptSeverityAssessor(final ErrorSeverityAssessor assessor) {
        return assessor.assessEventSnapshotException(this);
    }
}