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
 * Filename: BugReport.java
 *
 * Author: Claude Code
 *
 * Class name: BugReport
 *
 * Responsibilities:
 *   - Provide test-specific bug reporting capabilities
 *   - Generate reproducible test scenarios from event-driven test failures
 *   - Bridge between event-driven testing framework and domain bug reporting
 *
 * Collaborators:
 *   - EventDrivenTestSupport: Uses for test failure reporting
 *   - BugReportGenerator: Domain service for comprehensive bug analysis
 *   - EventTestContext: Source of test event data for reproduction
 */
package org.acmsl.bytehot.testing.support;

import org.acmsl.bytehot.domain.BugReportGenerator;
import org.acmsl.bytehot.domain.exceptions.EventSnapshotException;
import org.acmsl.bytehot.domain.EventSnapshot;
import org.acmsl.bytehot.domain.ErrorContext;

import org.acmsl.commons.patterns.DomainEvent;
import org.acmsl.commons.patterns.Test;
import org.acmsl.commons.patterns.eventsourcing.VersionedDomainEvent;

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
import java.util.stream.Collectors;

/**
 * Test-specific bug reporting for event-driven testing scenarios.
 * Provides integration between the event-driven testing framework and domain bug reporting.
 * @author Claude Code
 * @since 2025-06-24
 */
@RequiredArgsConstructor
// @Builder(toBuilder = true) // Temporarily disabled for compilation
@EqualsAndHashCode
@ToString
@Getter
public class BugReport implements Test {

    /**
     * Unique identifier for this test bug report
     */
    @NonNull
    private final String reportId;

    /**
     * When this test bug report was generated
     */
    @NonNull
    private final Instant generatedAt;

    /**
     * The test context when the failure occurred
     */
    @NonNull
    private final EventTestContext testContext;

    /**
     * The original test failure exception
     */
    @NonNull
    private final Throwable testFailure;

    /**
     * Events that were captured during the test
     */
    @NonNull
    private final List<DomainEvent> capturedEvents;

    /**
     * Test scenario description
     */
    @NonNull
    private final String scenarioDescription;

    /**
     * Severity of the test failure
     */
    @NonNull
    private final TestFailureSeverity severity;

    /**
     * Generated domain bug report for comprehensive analysis
     */
    // @Nullable annotation moved to field type
    private final BugReportGenerator.BugReport domainBugReport;

    /**
     * Test failure severity levels
     */
    public enum TestFailureSeverity {
        BLOCKING("Blocking - Test infrastructure failure"),
        CRITICAL("Critical - Core functionality broken"),
        MAJOR("Major - Important feature broken"),
        MINOR("Minor - Edge case or non-critical feature"),
        FLAKY("Flaky - Intermittent or timing-related failure");

        @Getter
        private final String description;

        TestFailureSeverity(String description) {
            this.description = description;
        }
    }

    /**
     * Creates a bug report from a test failure in event-driven testing
     * @param testContext the test context when failure occurred
     * @param testFailure the exception that caused the test to fail
     * @param scenarioDescription description of what the test was trying to accomplish
     * @return bug report for the test failure
     */
    @NonNull
    public static BugReport fromTestFailure(
        @NonNull final EventTestContext testContext,
        @NonNull final Throwable testFailure,
        @NonNull final String scenarioDescription
    ) {
        String reportId = "test-bug-" + UUID.randomUUID().toString();
        Instant generatedAt = Instant.now();
        
        // Extract captured events from test context
        List<DomainEvent> capturedEvents = testContext.getEmittedEvents().stream()
            .map(event -> (DomainEvent) event)
            .collect(Collectors.toList());

        // Analyze test failure severity
        TestFailureSeverity severity = analyzeTestFailureSeverity(testFailure, capturedEvents);

        // Generate comprehensive domain bug report if possible
        BugReportGenerator.BugReport domainBugReport = null;
        try {
            // Skip domain bug report generation for now due to type conflicts
            // This can be enhanced later when the domain model is more stable
            System.out.println("Domain bug report generation skipped for test failure: " + testFailure.getClass().getSimpleName());
        } catch (Exception e) {
            // If domain bug report generation fails, continue without it
            System.err.println("Failed to generate domain bug report: " + e.getMessage());
        }

        // Create using constructor
        BugReport report = new BugReport(
            reportId,
            generatedAt,
            testContext,
            testFailure,
            capturedEvents,
            scenarioDescription,
            severity,
            domainBugReport
        );
        return report;
    }

    /**
     * Generates a reproduction test case for this bug report
     * @return Java test code that can reproduce the failure
     */
    @NonNull
    public String generateReproductionTestCase() {
        StringBuilder testCase = new StringBuilder();
        
        testCase.append("@Test\n");
        testCase.append("@DisplayName(\"").append(scenarioDescription.replace("\"", "\\\"")).append("\")\n");
        testCase.append("void shouldReproduce_").append(reportId.replace("-", "_").substring(0, 16)).append("() {\n");
        testCase.append("    // Reproduction test for: ").append(scenarioDescription).append("\n");
        testCase.append("    // Original failure: ").append(testFailure.getClass().getSimpleName()).append("\n");
        testCase.append("    // Generated: ").append(generatedAt).append("\n");
        testCase.append("    \n");
        
        // Generate Given stage recreation
        testCase.append("    given()\n");
        testCase.append("        .testContext() // Recreate the test context\n");
        if (testContext.getArtifactCount() > 0) {
            testCase.append("        .artifacts(").append(generateArtifactsSetup()).append(")\n");
        }
        testCase.append("        .scenario(\"").append(scenarioDescription.replace("\"", "\\\"")).append("\")\n");
        testCase.append("    .when()\n");
        testCase.append("        .replayingEvents(") .append(capturedEvents.size()).append(" /* captured events */)\n");
        testCase.append("        .executingAction(() -> {\n");
        testCase.append("            // TODO: Add the specific action that caused the failure\n");
        testCase.append("            // Original exception: ").append(testFailure.getMessage()).append("\n");
        testCase.append("            fail(\"This reproduction test needs to be completed with the failing action\");\n");
        testCase.append("        })\n");
        testCase.append("    .then()\n");
        testCase.append("        .expectsException(").append(testFailure.getClass().getSimpleName()).append(".class)\n");
        testCase.append("        .withMessage(\"").append(testFailure.getMessage() != null ? 
                       testFailure.getMessage().replace("\"", "\\\"") : "").append("\");\n");
        testCase.append("}\n");
        
        return testCase.toString();
    }

    /**
     * Gets a summary of the test failure for quick understanding
     * @return human-readable summary
     */
    @NonNull
    public String getFailureSummary() {
        StringBuilder summary = new StringBuilder();
        
        summary.append("Test Failure Report: ").append(reportId.substring(0, 8)).append("\n");
        summary.append("Scenario: ").append(scenarioDescription).append("\n");
        summary.append("Severity: ").append(severity).append(" - ").append(severity.getDescription()).append("\n");
        summary.append("Exception: ").append(testFailure.getClass().getSimpleName());
        if (testFailure.getMessage() != null) {
            summary.append(" - ").append(testFailure.getMessage());
        }
        summary.append("\n");
        summary.append("Events Captured: ").append(capturedEvents.size()).append("\n");
        summary.append("Generated: ").append(generatedAt).append("\n");
        
        if (domainBugReport != null) {
            summary.append("Domain Analysis: ").append(domainBugReport.getCategory())
                   .append(" with ").append(String.format("%.1f%%", domainBugReport.getReproducibilityScore() * 100))
                   .append(" reproducibility\n");
        }
        
        return summary.toString();
    }

    /**
     * Exports the bug report to a comprehensive format combining test and domain analysis
     * @return detailed bug report suitable for filing issues
     */
    @NonNull
    public String toDetailedReport() {
        StringBuilder report = new StringBuilder();
        
        report.append("# Event-Driven Test Bug Report\n\n");
        report.append("**Report ID:** ").append(reportId).append("\n");
        report.append("**Generated:** ").append(generatedAt).append("\n");
        report.append("**Severity:** ").append(severity).append(" - ").append(severity.getDescription()).append("\n\n");
        
        report.append("## Test Scenario\n\n");
        report.append(scenarioDescription).append("\n\n");
        
        report.append("## Failure Details\n\n");
        report.append("- **Exception Type:** ").append(testFailure.getClass().getSimpleName()).append("\n");
        report.append("- **Exception Message:** ").append(testFailure.getMessage() != null ? testFailure.getMessage() : "N/A").append("\n");
        report.append("- **Events Captured:** ").append(capturedEvents.size()).append("\n\n");
        
        if (!capturedEvents.isEmpty()) {
            report.append("## Event Sequence\n\n");
            for (int i = 0; i < capturedEvents.size(); i++) {
                DomainEvent event = capturedEvents.get(i);
                report.append(i + 1).append(". ").append(event.getClass().getSimpleName());
                if (event instanceof VersionedDomainEvent) {
                    VersionedDomainEvent versionedEvent = (VersionedDomainEvent) event;
                    report.append(" (v").append(versionedEvent.getAggregateVersion()).append(")");
                }
                report.append("\n");
            }
            report.append("\n");
        }
        
        report.append("## Reproduction Test Case\n\n");
        report.append("```java\n");
        report.append(generateReproductionTestCase());
        report.append("```\n\n");
        
        // Include domain bug report if available
        if (domainBugReport != null) {
            report.append("## Domain Analysis\n\n");
            report.append("The domain layer analysis provides additional insights:\n\n");
            report.append("- **Category:** ").append(domainBugReport.getCategory()).append("\n");
            report.append("- **Reproducibility:** ").append(String.format("%.1f%%", domainBugReport.getReproducibilityScore() * 100)).append("\n");
            report.append("- **Analysis:** ").append(domainBugReport.getAnalysis()).append("\n\n");
            
            if (!domainBugReport.getRecommendations().isEmpty()) {
                report.append("### Recommendations\n\n");
                for (String recommendation : domainBugReport.getRecommendations()) {
                    report.append("- ").append(recommendation).append("\n");
                }
                report.append("\n");
            }
        }
        
        report.append("## Test Context\n\n");
        report.append("- **Artifacts:** ").append(testContext.getArtifactCount()).append(" items\n");
        report.append("- **Test Thread:** ").append(Thread.currentThread().getName()).append("\n");
        
        return report.toString();
    }

    /**
     * Analyzes test failure severity based on exception and context
     */
    protected static TestFailureSeverity analyzeTestFailureSeverity(
        @NonNull final Throwable testFailure,
        @NonNull final List<DomainEvent> capturedEvents
    ) {
        // Analyze based on exception type
        if (testFailure instanceof AssertionError) {
            // Check if it's a simple assertion failure or something more serious
            String message = testFailure.getMessage();
            if (message != null && (message.contains("timeout") || message.contains("timing"))) {
                return TestFailureSeverity.FLAKY;
            }
            return TestFailureSeverity.MAJOR;
        }
        
        if (testFailure instanceof OutOfMemoryError || testFailure instanceof StackOverflowError) {
            return TestFailureSeverity.BLOCKING;
        }
        
        if (testFailure instanceof SecurityException) {
            return TestFailureSeverity.CRITICAL;
        }
        
        if (testFailure instanceof IllegalArgumentException || testFailure instanceof IllegalStateException) {
            return TestFailureSeverity.MAJOR;
        }
        
        if (testFailure instanceof InterruptedException) {
            return TestFailureSeverity.FLAKY;
        }
        
        // Check event context for additional severity indicators
        if (capturedEvents.isEmpty()) {
            return TestFailureSeverity.BLOCKING; // No events captured suggests infrastructure issue
        }
        
        return TestFailureSeverity.MAJOR; // Default for unknown exceptions
    }

    /**
     * Creates an EventSnapshot from test data for domain analysis
     */
    protected static EventSnapshot createEventSnapshotFromTest(
        @NonNull final List<DomainEvent> capturedEvents,
        @NonNull final EventTestContext testContext
    ) {
        // This would be implemented to create a proper EventSnapshot
        // For now, return a minimal implementation
        String snapshotId = "test-snapshot-" + UUID.randomUUID().toString();
        List<VersionedDomainEvent> versionedEvents = capturedEvents.stream()
            .filter(event -> event instanceof VersionedDomainEvent)
            .map(event -> (VersionedDomainEvent) event)
            .collect(Collectors.toList());
        
        return new EventSnapshot(
            snapshotId,
            Instant.now(),
            versionedEvents,
            null, // userId - nullable
            Map.of(), // environmentContext
            Thread.currentThread().getName(), // threadName
            Map.of(), // systemProperties
            null, // causalChain - nullable
            Map.of() // performanceMetrics
        );
    }

    /**
     * Creates an ErrorContext from test data for domain analysis
     */
    protected static ErrorContext createErrorContextFromTest(
        @NonNull final EventTestContext testContext,
        @NonNull final Throwable testFailure
    ) {
        // This would be implemented to create a proper ErrorContext
        // For now, return a minimal implementation with test-specific data
        Map<String, String> systemProperties = Map.of(
            "java.version", System.getProperty("java.version"),
            "os.name", System.getProperty("os.name"),
            "test.framework", "event-driven-testing"
        );
        
        Map<String, String> environmentVariables = Map.of(
            "TEST_EXECUTION", "true"
        );
        
        Map<String, Object> byteHotContext = Map.of(
            "test.scenario", "test-scenario-placeholder",
            "test.artifacts.count", testContext.getArtifactCount(),
            "test.events.captured", testContext.getEmittedEvents().size()
        );
        
        return new ErrorContext(
            Instant.now(), // capturedAt
            null, // userId - nullable
            Thread.currentThread().getName(), // threadName
            Thread.currentThread().getId(), // threadId
            Thread.currentThread().getState(), // threadState
            systemProperties, // systemProperties
            environmentVariables, // environmentVariables
            new ErrorContext.MemoryInfo(
                Runtime.getRuntime().totalMemory(), // totalHeapMemory
                Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory(), // usedHeapMemory
                Runtime.getRuntime().maxMemory(), // maxHeapMemory
                Runtime.getRuntime().freeMemory(), // freeHeapMemory
                0L, // gcCount - placeholder
                0L  // gcTime - placeholder
            ), // memoryInfo
            "test-classloader", // classLoaderInfo
            Thread.currentThread().getStackTrace(), // stackTrace
            byteHotContext, // byteHotContext
            Map.of() // customContext
        );
    }

    /**
     * Generates artifacts setup code for reproduction
     */
    protected String generateArtifactsSetup() {
        if (testContext.getArtifactCount() == 0) {
            return "Map.of()";
        }
        
        StringBuilder setup = new StringBuilder("Map.of(\n");
        String[] keys = testContext.getArtifactKeys().toArray(new String[0]);
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            Object value = testContext.getArtifact(key);
            setup.append("            \"").append(key).append("\", ");
            if (value instanceof String) {
                setup.append("\"").append(value.toString().replace("\"", "\\\"")).append("\"");
            } else {
                setup.append("/* ").append(value.getClass().getSimpleName()).append(" */");
            }
            if (i < keys.length - 1) {
                setup.append(",");
            }
            setup.append("\n");
        }
        setup.append("        )");
        
        return setup.toString();
    }
}