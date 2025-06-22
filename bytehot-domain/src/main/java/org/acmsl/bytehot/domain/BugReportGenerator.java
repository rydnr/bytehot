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
 * Filename: BugReportGenerator.java
 *
 * Author: Claude Code
 *
 * Class name: BugReportGenerator
 *
 * Responsibilities:
 *   - Generate comprehensive bug reports from EventSnapshotExceptions
 *   - Serialize bug reports to various formats (JSON, Markdown, XML)
 *   - Create reproduction scripts and test cases
 *   - Provide developer-friendly bug analysis and recommendations
 *
 * Collaborators:
 *   - EventSnapshotException: Source of comprehensive error context
 *   - EventSnapshot: Event history and context
 *   - ErrorContext: Environmental context
 *   - CausalChain: Causal analysis information
 */
package org.acmsl.bytehot.domain;

import org.acmsl.bytehot.domain.EventSnapshot;
import org.acmsl.bytehot.domain.EventSnapshotException;
import org.acmsl.bytehot.domain.ErrorContext;
import org.acmsl.bytehot.domain.CausalChain;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Generates comprehensive bug reports with reproduction capabilities from EventSnapshotExceptions.
 * Provides multiple output formats and creates actionable debugging information.
 * @author Claude Code
 * @since 2025-06-19
 */
public class BugReportGenerator {

    /**
     * Comprehensive bug report with all analysis and reproduction information
     */
    @RequiredArgsConstructor
    @Builder(toBuilder = true)
    @EqualsAndHashCode
    @ToString
    @Getter
    public static class BugReport {
        /**
         * Unique identifier for this bug report
         */
        @NonNull
        private final String reportId;

        /**
         * When this report was generated
         */
        @NonNull
        private final Instant generatedAt;

        /**
         * Original exception that triggered the report
         */
        @NonNull
        private final EventSnapshotException sourceException;

        /**
         * Severity assessment of the bug
         */
        @NonNull
        private final BugSeverity severity;

        /**
         * Category classification of the bug
         */
        @NonNull
        private final BugCategory category;

        /**
         * Detailed analysis of the bug
         */
        @NonNull
        private final String analysis;

        /**
         * Recommendations for fixing the bug
         */
        @NonNull
        private final List<String> recommendations;

        /**
         * Steps to reproduce the bug
         */
        @NonNull
        private final List<String> reproductionSteps;

        /**
         * Generated test case for reproduction
         */
        @Nullable
        private final String reproductionTestCase;

        /**
         * Environmental requirements for reproduction
         */
        @NonNull
        private final Map<String, String> reproductionEnvironment;

        /**
         * Likelihood that this bug can be reproduced
         */
        private final double reproducibilityScore;

        /**
         * Related bugs or patterns (if any)
         */
        @NonNull
        private final List<String> relatedIssues;
    }

    /**
     * Bug severity levels
     */
    public enum BugSeverity {
        CRITICAL("Critical - System failure or data loss"),
        HIGH("High - Major functionality affected"),
        MEDIUM("Medium - Partial functionality affected"),
        LOW("Low - Minor issue or cosmetic"),
        INFO("Informational - Not a bug but notable behavior");

        @Getter
        private final String description;

        BugSeverity(String description) {
            this.description = description;
        }
    }

    /**
     * Bug category classifications
     */
    public enum BugCategory {
        MEMORY_LEAK("Memory Management - Leaks or excessive usage"),
        CONCURRENT_ACCESS("Concurrency - Race conditions or deadlocks"),
        VALIDATION_ERROR("Validation - Input or state validation failure"),
        CONFIGURATION_ERROR("Configuration - Setup or config issues"),
        DEPENDENCY_ERROR("Dependencies - Missing or incompatible dependencies"),
        PERFORMANCE_ISSUE("Performance - Slow execution or timeouts"),
        SECURITY_VULNERABILITY("Security - Potential security issues"),
        DATA_CORRUPTION("Data Integrity - Corruption or inconsistency"),
        NETWORK_ERROR("Network - Connectivity or communication issues"),
        UNKNOWN("Unknown - Unable to categorize automatically");

        @Getter
        private final String description;

        BugCategory(String description) {
            this.description = description;
        }
    }

    /**
     * Generates a comprehensive bug report from an EventSnapshotException
     * @param exception the exception with complete context
     * @return detailed bug report with analysis and reproduction information
     */
    @NonNull
    public BugReport generateBugReport(@NonNull final EventSnapshotException exception) {
        String reportId = UUID.randomUUID().toString();
        Instant generatedAt = Instant.now();

        // Analyze the exception to determine severity and category
        BugSeverity severity = analyzeSeverity(exception);
        BugCategory category = analyzeCategory(exception);

        // Generate detailed analysis
        String analysis = generateAnalysis(exception);

        // Create recommendations
        List<String> recommendations = generateRecommendations(exception, category, severity);

        // Generate reproduction steps
        List<String> reproductionSteps = generateReproductionSteps(exception);

        // Create reproduction test case
        String reproductionTestCase = generateReproductionTestCase(exception);

        // Extract reproduction environment
        Map<String, String> reproductionEnvironment = extractReproductionEnvironment(exception);

        // Calculate reproducibility score
        double reproducibilityScore = calculateReproducibilityScore(exception);

        // Find related issues
        List<String> relatedIssues = findRelatedIssues(exception);

        return BugReport.builder()
            .reportId(reportId)
            .generatedAt(generatedAt)
            .sourceException(exception)
            .severity(severity)
            .category(category)
            .analysis(analysis)
            .recommendations(recommendations)
            .reproductionSteps(reproductionSteps)
            .reproductionTestCase(reproductionTestCase)
            .reproductionEnvironment(reproductionEnvironment)
            .reproducibilityScore(reproducibilityScore)
            .relatedIssues(relatedIssues)
            .build();
    }

    /**
     * Serializes a bug report to JSON format
     * @param report the bug report to serialize
     * @return JSON representation of the bug report
     */
    @NonNull
    public String toJson(@NonNull final BugReport report) {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"reportId\": \"").append(report.getReportId()).append("\",\n");
        json.append("  \"generatedAt\": \"").append(report.getGeneratedAt()).append("\",\n");
        json.append("  \"severity\": \"").append(report.getSeverity()).append("\",\n");
        json.append("  \"category\": \"").append(report.getCategory()).append("\",\n");
        json.append("  \"reproducibilityScore\": ").append(report.getReproducibilityScore()).append(",\n");
        json.append("  \"analysis\": \"").append(escapeJson(report.getAnalysis())).append("\",\n");
        
        // Recommendations array
        json.append("  \"recommendations\": [\n");
        for (int i = 0; i < report.getRecommendations().size(); i++) {
            json.append("    \"").append(escapeJson(report.getRecommendations().get(i))).append("\"");
            if (i < report.getRecommendations().size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        json.append("  ],\n");

        // Reproduction steps array
        json.append("  \"reproductionSteps\": [\n");
        for (int i = 0; i < report.getReproductionSteps().size(); i++) {
            json.append("    \"").append(escapeJson(report.getReproductionSteps().get(i))).append("\"");
            if (i < report.getReproductionSteps().size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        json.append("  ],\n");

        // Environment object
        json.append("  \"reproductionEnvironment\": {\n");
        String[] envKeys = report.getReproductionEnvironment().keySet().toArray(new String[0]);
        for (int i = 0; i < envKeys.length; i++) {
            String key = envKeys[i];
            String value = report.getReproductionEnvironment().get(key);
            json.append("    \"").append(escapeJson(key)).append("\": \"").append(escapeJson(value)).append("\"");
            if (i < envKeys.length - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        json.append("  },\n");

        // Test case
        if (report.getReproductionTestCase() != null) {
            json.append("  \"reproductionTestCase\": \"").append(escapeJson(report.getReproductionTestCase())).append("\",\n");
        }

        // Exception details
        json.append("  \"exceptionDetails\": {\n");
        json.append("    \"type\": \"").append(report.getSourceException().getClass().getSimpleName()).append("\",\n");
        json.append("    \"message\": \"").append(escapeJson(report.getSourceException().getMessage())).append("\",\n");
        json.append("    \"snapshotId\": \"").append(report.getSourceException().getEventSnapshot().getSnapshotId()).append("\",\n");
        json.append("    \"eventCount\": ").append(report.getSourceException().getEventSnapshot().getEventCount()).append("\n");
        json.append("  }\n");

        json.append("}");
        return json.toString();
    }

    /**
     * Generates a Markdown bug report for documentation or GitHub issues
     * @param report the bug report to format
     * @return Markdown formatted bug report
     */
    @NonNull
    public String toMarkdown(@NonNull final BugReport report) {
        StringBuilder md = new StringBuilder();
        
        md.append("# Bug Report: ").append(report.getReportId().substring(0, 8)).append("\n\n");
        md.append("**Generated:** ").append(report.getGeneratedAt()).append("  \n");
        md.append("**Severity:** ").append(report.getSeverity()).append(" - ").append(report.getSeverity().getDescription()).append("  \n");
        md.append("**Category:** ").append(report.getCategory()).append(" - ").append(report.getCategory().getDescription()).append("  \n");
        md.append("**Reproducibility:** ").append(String.format("%.1f%%", report.getReproducibilityScore() * 100)).append("  \n\n");

        md.append("## Analysis\n\n");
        md.append(report.getAnalysis()).append("\n\n");

        md.append("## Exception Details\n\n");
        md.append("- **Type:** ").append(report.getSourceException().getClass().getSimpleName()).append("\n");
        md.append("- **Message:** ").append(report.getSourceException().getMessage()).append("\n");
        md.append("- **Snapshot ID:** `").append(report.getSourceException().getEventSnapshot().getSnapshotId()).append("`\n");
        md.append("- **Event Count:** ").append(report.getSourceException().getEventSnapshot().getEventCount()).append("\n\n");

        md.append("## Reproduction Steps\n\n");
        for (int i = 0; i < report.getReproductionSteps().size(); i++) {
            md.append(i + 1).append(". ").append(report.getReproductionSteps().get(i)).append("\n");
        }
        md.append("\n");

        md.append("## Environment Requirements\n\n");
        md.append("```\n");
        for (Map.Entry<String, String> entry : report.getReproductionEnvironment().entrySet()) {
            md.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
        }
        md.append("```\n\n");

        md.append("## Recommendations\n\n");
        for (String recommendation : report.getRecommendations()) {
            md.append("- ").append(recommendation).append("\n");
        }
        md.append("\n");

        if (report.getReproductionTestCase() != null) {
            md.append("## Reproduction Test Case\n\n");
            md.append("```java\n");
            md.append(report.getReproductionTestCase());
            md.append("\n```\n\n");
        }

        md.append("## Debugging Information\n\n");
        md.append("For complete debugging context, see the attached EventSnapshot with ID `");
        md.append(report.getSourceException().getEventSnapshot().getSnapshotId()).append("`.\n\n");
        md.append("Use the following debugging report for detailed analysis:\n\n");
        md.append("```\n");
        md.append(report.getSourceException().getDebuggingReport());
        md.append("\n```\n");

        return md.toString();
    }

    /**
     * Analyzes the severity of a bug based on the exception context
     */
    protected BugSeverity analyzeSeverity(@NonNull final EventSnapshotException exception) {
        Throwable originalException = exception.getOriginalException();
        
        if (originalException instanceof OutOfMemoryError || originalException instanceof StackOverflowError) {
            return BugSeverity.CRITICAL;
        }
        
        if (exception.getErrorContext().isHighMemoryUsage()) {
            return BugSeverity.HIGH;
        }
        
        if (originalException instanceof SecurityException) {
            return BugSeverity.HIGH;
        }
        
        if (originalException instanceof IllegalArgumentException || originalException instanceof IllegalStateException) {
            return BugSeverity.MEDIUM;
        }
        
        return BugSeverity.MEDIUM; // Default
    }

    /**
     * Analyzes the category of a bug based on the exception context
     */
    protected BugCategory analyzeCategory(@NonNull final EventSnapshotException exception) {
        Throwable originalException = exception.getOriginalException();
        String exceptionMessage = originalException.getMessage();
        
        if (originalException instanceof OutOfMemoryError || 
            (exceptionMessage != null && exceptionMessage.toLowerCase().contains("memory"))) {
            return BugCategory.MEMORY_LEAK;
        }
        
        if (originalException instanceof SecurityException) {
            return BugCategory.SECURITY_VULNERABILITY;
        }
        
        if (originalException instanceof IllegalArgumentException || originalException instanceof IllegalStateException) {
            return BugCategory.VALIDATION_ERROR;
        }
        
        if (exceptionMessage != null) {
            String lowerMessage = exceptionMessage.toLowerCase();
            if (lowerMessage.contains("concurrent") || lowerMessage.contains("thread") || lowerMessage.contains("lock")) {
                return BugCategory.CONCURRENT_ACCESS;
            }
            if (lowerMessage.contains("config") || lowerMessage.contains("property")) {
                return BugCategory.CONFIGURATION_ERROR;
            }
            if (lowerMessage.contains("network") || lowerMessage.contains("connection")) {
                return BugCategory.NETWORK_ERROR;
            }
            if (lowerMessage.contains("performance") || lowerMessage.contains("timeout")) {
                return BugCategory.PERFORMANCE_ISSUE;
            }
        }
        
        return BugCategory.UNKNOWN;
    }

    /**
     * Generates detailed analysis of the bug
     */
    protected String generateAnalysis(@NonNull final EventSnapshotException exception) {
        StringBuilder analysis = new StringBuilder();
        
        analysis.append("This error occurred in the context of ")
                .append(exception.getErrorContext().getThreadName())
                .append(" thread with ")
                .append(exception.getEventSnapshot().getEventCount())
                .append(" related events. ");
        
        if (exception.getCausalChain() != null) {
            analysis.append("Causal analysis indicates: ")
                    .append(exception.getCausalChain().getDescription())
                    .append(" ");
        }
        
        analysis.append("Memory usage at error time: ")
                .append(String.format("%.1f%%", exception.getErrorContext().getMemoryUsagePercentage() * 100))
                .append(". ");
        
        if (exception.isLikelyReproducible()) {
            analysis.append("This error appears to be reproducible based on the event context captured.");
        } else {
            analysis.append("This error may be difficult to reproduce due to limited event context or timing-dependent conditions.");
        }
        
        return analysis.toString();
    }

    /**
     * Generates actionable recommendations for fixing the bug
     */
    protected List<String> generateRecommendations(@NonNull final EventSnapshotException exception, 
                                                 @NonNull final BugCategory category, 
                                                 @NonNull final BugSeverity severity) {
        List<String> recommendations = new java.util.ArrayList<>();
        
        // Add specific recommendations based on category
        switch (category) {
            case MEMORY_LEAK:
                recommendations.add("Review memory allocation patterns and ensure proper cleanup");
                recommendations.add("Use memory profiling tools to identify leak sources");
                recommendations.add("Consider implementing memory monitoring and alerts");
                break;
            case CONCURRENT_ACCESS:
                recommendations.add("Review thread synchronization and locking mechanisms");
                recommendations.add("Consider using concurrent data structures");
                recommendations.add("Add thread-safety tests to prevent regression");
                break;
            case VALIDATION_ERROR:
                recommendations.add("Improve input validation and error handling");
                recommendations.add("Add comprehensive unit tests for edge cases");
                recommendations.add("Consider using defensive programming techniques");
                break;
            case CONFIGURATION_ERROR:
                recommendations.add("Validate configuration values at startup");
                recommendations.add("Provide clear error messages for configuration issues");
                recommendations.add("Document required configuration parameters");
                break;
            default:
                recommendations.add("Review the error context and event history for patterns");
                recommendations.add("Add comprehensive logging around the failure point");
                break;
        }
        
        // Add severity-based recommendations
        if (severity == BugSeverity.CRITICAL) {
            recommendations.add("URGENT: This is a critical issue that requires immediate attention");
            recommendations.add("Consider implementing circuit breaker patterns to prevent cascading failures");
        }
        
        // Add general recommendations
        recommendations.addAll(exception.getDebuggingSuggestions());
        
        return recommendations;
    }

    /**
     * Generates reproduction steps based on the event snapshot
     */
    protected List<String> generateReproductionSteps(@NonNull final EventSnapshotException exception) {
        List<String> steps = new java.util.ArrayList<>();
        
        steps.add("Set up environment matching the reproduction requirements (see Environment section)");
        steps.add("Load the EventSnapshot with ID: " + exception.getEventSnapshot().getSnapshotId());
        steps.add("Replay the " + exception.getEventSnapshot().getEventCount() + " events leading to the error");
        
        if (exception.getCausalChain() != null) {
            steps.add("Pay special attention to: " + exception.getCausalChain().getDescription());
        }
        
        steps.add("Monitor memory usage - error occurred at " + 
                 String.format("%.1f%%", exception.getErrorContext().getMemoryUsagePercentage() * 100) + " memory usage");
        steps.add("Execute the failing operation in thread: " + exception.getErrorContext().getThreadName());
        steps.add("Verify that the same exception type is thrown: " + 
                 exception.getOriginalException().getClass().getSimpleName());
        
        return steps;
    }

    /**
     * Generates a reproduction test case
     */
    protected String generateReproductionTestCase(@NonNull final EventSnapshotException exception) {
        StringBuilder testCase = new StringBuilder();
        
        testCase.append("@Test\n");
        testCase.append("void shouldReproduceBug_").append(exception.getEventSnapshot().getSnapshotId().substring(0, 8)).append("() {\n");
        testCase.append("    // Reproduction test for bug report: ").append(exception.getEventSnapshot().getSnapshotId()).append("\n");
        testCase.append("    // Original error: ").append(exception.getOriginalException().getClass().getSimpleName()).append("\n");
        testCase.append("    \n");
        testCase.append("    // Load event snapshot\n");
        testCase.append("    EventSnapshot snapshot = loadEventSnapshot(\"").append(exception.getEventSnapshot().getSnapshotId()).append("\");\n");
        testCase.append("    \n");
        testCase.append("    // Replay events leading to error\n");
        testCase.append("    for (VersionedDomainEvent event : snapshot.getEventHistory()) {\n");
        testCase.append("        // Replay event: event.getEventType()\n");
        testCase.append("        replayEvent(event);\n");
        testCase.append("    }\n");
        testCase.append("    \n");
        testCase.append("    // Execute the operation that caused the error\n");
        testCase.append("    assertThrows(").append(exception.getOriginalException().getClass().getSimpleName()).append(".class, () -> {\n");
        testCase.append("        // Add the specific operation that triggered the error\n");
        testCase.append("        executeFailingOperation();\n");
        testCase.append("    });\n");
        testCase.append("}\n");
        
        return testCase.toString();
    }

    /**
     * Extracts reproduction environment from error context
     */
    protected Map<String, String> extractReproductionEnvironment(@NonNull final EventSnapshotException exception) {
        Map<String, String> environment = new java.util.HashMap<>();
        
        ErrorContext errorContext = exception.getErrorContext();
        
        // Add essential environment information
        environment.putAll(errorContext.getSystemProperties());
        environment.putAll(errorContext.getEnvironmentVariables());
        
        // Add ByteHot-specific context
        environment.putAll(errorContext.getByteHotContext().entrySet().stream()
            .collect(java.util.stream.Collectors.toMap(
                Map.Entry::getKey,
                entry -> String.valueOf(entry.getValue())
            )));
        
        // Add memory requirements
        environment.put("memory.required", String.format("%.1f%%", errorContext.getMemoryUsagePercentage() * 100));
        environment.put("thread.name", errorContext.getThreadName());
        environment.put("thread.state", errorContext.getThreadState().toString());
        
        return environment;
    }

    /**
     * Calculates reproducibility score based on available context
     */
    protected double calculateReproducibilityScore(@NonNull final EventSnapshotException exception) {
        double score = 0.0;
        
        // Event history availability (40% weight)
        if (exception.getEventSnapshot().getEventCount() > 0) {
            score += 0.4;
        }
        
        // Causal chain analysis (20% weight)
        if (exception.getCausalChain() != null) {
            score += 0.2 * exception.getCausalChain().getConfidence();
        }
        
        // Environmental context (20% weight)
        if (!exception.getErrorContext().getSystemProperties().isEmpty()) {
            score += 0.2;
        }
        
        // Error determinism (20% weight)
        if (exception.getOriginalException() instanceof IllegalArgumentException ||
            exception.getOriginalException() instanceof IllegalStateException) {
            score += 0.2; // Deterministic errors are more reproducible
        } else if (exception.getOriginalException() instanceof OutOfMemoryError) {
            score += 0.1; // Memory errors are somewhat reproducible
        }
        
        return Math.min(1.0, score);
    }

    /**
     * Finds related issues or patterns
     */
    protected List<String> findRelatedIssues(@NonNull final EventSnapshotException exception) {
        List<String> relatedIssues = new java.util.ArrayList<>();
        
        // This could be enhanced to search for similar patterns in a bug database
        // For now, provide generic related issue hints
        
        String exceptionType = exception.getOriginalException().getClass().getSimpleName();
        relatedIssues.add("Search for similar " + exceptionType + " exceptions in bug tracking system");
        
        if (exception.getCausalChain() != null) {
            relatedIssues.add("Look for issues related to: " + exception.getCausalChain().getDescription());
        }
        
        relatedIssues.add("Check for similar patterns in event snapshot ID: " + 
                         exception.getEventSnapshot().getSnapshotId().substring(0, 8) + "*");
        
        return relatedIssues;
    }

    /**
     * Escapes JSON special characters
     */
    private String escapeJson(final String text) {
        if (text == null) {
            return "";
        }
        return text.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}