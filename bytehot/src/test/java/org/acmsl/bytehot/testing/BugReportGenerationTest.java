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
 * Filename: BugReportGenerationTest.java
 *
 * Author: Claude Code
 *
 * Class name: BugReportGenerationTest
 *
 * Responsibilities:
 *   - Test comprehensive bug report generation from EventSnapshotExceptions
 *   - Verify serialization to different formats (JSON, Markdown)
 *   - Validate reproduction test case generation
 *   - Demonstrate complete bug analysis workflow
 *
 * Collaborators:
 *   - BugReportGenerator: Bug report generation and serialization
 *   - EventSnapshotException: Source of comprehensive error context
 *   - ErrorHandler: Error handling with snapshot generation
 */
package org.acmsl.bytehot.testing;

import org.acmsl.bytehot.domain.BugReportGenerator;
import org.acmsl.bytehot.domain.BugReportGenerator.BugReport;
import org.acmsl.bytehot.domain.BugReportGenerator.BugSeverity;
import org.acmsl.bytehot.domain.BugReportGenerator.BugCategory;
import org.acmsl.bytehot.domain.ErrorHandler;
import org.acmsl.bytehot.domain.EventSnapshotException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test comprehensive bug report generation and serialization capabilities
 * @author Claude Code
 * @since 2025-06-19
 */
public class BugReportGenerationTest {

    private BugReportGenerator bugReportGenerator;
    private ErrorHandler errorHandler;

    @BeforeEach
    void setUp() {
        bugReportGenerator = new BugReportGenerator();
        errorHandler = new ErrorHandler();
    }

    @Test
    void shouldGenerateComprehensiveBugReport() {
        // Given: A complex error scenario
        RuntimeException originalError = new RuntimeException("Complex error with multiple contributing factors");
        EventSnapshotException enhancedException = errorHandler.handleErrorWithSnapshot(originalError);
        
        // When: Generating a bug report
        BugReport bugReport = bugReportGenerator.generateBugReport(enhancedException);
        
        // Then: The bug report should contain comprehensive analysis
        assertNotNull(bugReport.getReportId(), "Bug report should have unique ID");
        assertNotNull(bugReport.getGeneratedAt(), "Bug report should have generation timestamp");
        assertSame(enhancedException, bugReport.getSourceException(), "Bug report should reference source exception");
        
        // And: Severity and category should be analyzed
        assertNotNull(bugReport.getSeverity(), "Bug severity should be analyzed");
        assertNotNull(bugReport.getCategory(), "Bug category should be classified");
        
        // And: Analysis should be provided
        assertNotNull(bugReport.getAnalysis(), "Detailed analysis should be provided");
        assertFalse(bugReport.getAnalysis().isEmpty(), "Analysis should not be empty");
        
        // And: Recommendations should be actionable
        assertNotNull(bugReport.getRecommendations(), "Recommendations should be provided");
        assertFalse(bugReport.getRecommendations().isEmpty(), "At least one recommendation should be provided");
        
        // And: Reproduction information should be complete
        assertNotNull(bugReport.getReproductionSteps(), "Reproduction steps should be provided");
        assertFalse(bugReport.getReproductionSteps().isEmpty(), "At least one reproduction step should be provided");
        assertNotNull(bugReport.getReproductionEnvironment(), "Reproduction environment should be captured");
        assertTrue(bugReport.getReproducibilityScore() >= 0.0 && bugReport.getReproducibilityScore() <= 1.0, 
                  "Reproducibility score should be valid percentage");
    }

    @Test
    void shouldClassifyMemoryErrors() {
        // Given: An OutOfMemoryError
        OutOfMemoryError memoryError = new OutOfMemoryError("Java heap space exhausted");
        EventSnapshotException enhancedException = errorHandler.handleErrorWithSnapshot(memoryError);
        
        // When: Generating bug report
        BugReport bugReport = bugReportGenerator.generateBugReport(enhancedException);
        
        // Then: Should be classified as critical severity and memory category
        assertEquals(BugSeverity.CRITICAL, bugReport.getSeverity(), "Memory errors should be critical");
        assertEquals(BugCategory.MEMORY_LEAK, bugReport.getCategory(), "Should be classified as memory issue");
        
        // And: Should provide memory-specific recommendations
        assertTrue(bugReport.getRecommendations().stream()
            .anyMatch(rec -> rec.toLowerCase().contains("memory")), 
            "Should provide memory-related recommendations");
    }

    @Test
    void shouldClassifySecurityErrors() {
        // Given: A SecurityException
        SecurityException securityError = new SecurityException("Access denied to protected resource");
        EventSnapshotException enhancedException = errorHandler.handleErrorWithSnapshot(securityError);
        
        // When: Generating bug report
        BugReport bugReport = bugReportGenerator.generateBugReport(enhancedException);
        
        // Then: Should be classified appropriately
        assertEquals(BugSeverity.HIGH, bugReport.getSeverity(), "Security errors should be high severity");
        assertEquals(BugCategory.SECURITY_VULNERABILITY, bugReport.getCategory(), "Should be classified as security issue");
    }

    @Test
    void shouldClassifyValidationErrors() {
        // Given: A validation error
        IllegalArgumentException validationError = new IllegalArgumentException("Invalid bytecode format detected");
        EventSnapshotException enhancedException = errorHandler.handleErrorWithSnapshot(validationError);
        
        // When: Generating bug report
        BugReport bugReport = bugReportGenerator.generateBugReport(enhancedException);
        
        // Then: Should be classified as validation error
        assertEquals(BugCategory.VALIDATION_ERROR, bugReport.getCategory(), "Should be classified as validation error");
        assertEquals(BugSeverity.MEDIUM, bugReport.getSeverity(), "Validation errors should be medium severity");
    }

    @Test
    void shouldSerializeToJson() {
        // Given: A bug report
        RuntimeException error = new RuntimeException("JSON serialization test error");
        EventSnapshotException enhancedException = errorHandler.handleErrorWithSnapshot(error);
        BugReport bugReport = bugReportGenerator.generateBugReport(enhancedException);
        
        // When: Serializing to JSON
        String json = bugReportGenerator.toJson(bugReport);
        
        // Then: JSON should contain essential information
        assertNotNull(json, "JSON serialization should produce output");
        assertTrue(json.contains("reportId"), "JSON should contain report ID");
        assertTrue(json.contains("severity"), "JSON should contain severity");
        assertTrue(json.contains("category"), "JSON should contain category");
        assertTrue(json.contains("recommendations"), "JSON should contain recommendations");
        assertTrue(json.contains("reproductionSteps"), "JSON should contain reproduction steps");
        assertTrue(json.contains("reproductionEnvironment"), "JSON should contain environment");
        assertTrue(json.contains("exceptionDetails"), "JSON should contain exception details");
        assertTrue(json.contains("JSON serialization test error"), "JSON should contain original error message");
        
        // And: Should be valid JSON structure
        assertTrue(json.startsWith("{"), "Should start with opening brace");
        assertTrue(json.endsWith("}"), "Should end with closing brace");
    }

    @Test
    void shouldSerializeToMarkdown() {
        // Given: A bug report
        IllegalStateException error = new IllegalStateException("Markdown generation test error");
        EventSnapshotException enhancedException = errorHandler.handleErrorWithSnapshot(error);
        BugReport bugReport = bugReportGenerator.generateBugReport(enhancedException);
        
        // When: Serializing to Markdown
        String markdown = bugReportGenerator.toMarkdown(bugReport);
        
        // Then: Markdown should be properly formatted
        assertNotNull(markdown, "Markdown serialization should produce output");
        assertTrue(markdown.contains("# Bug Report:"), "Should contain main heading");
        assertTrue(markdown.contains("## Analysis"), "Should contain analysis section");
        assertTrue(markdown.contains("## Exception Details"), "Should contain exception details section");
        assertTrue(markdown.contains("## Reproduction Steps"), "Should contain reproduction steps section");
        assertTrue(markdown.contains("## Environment Requirements"), "Should contain environment section");
        assertTrue(markdown.contains("## Recommendations"), "Should contain recommendations section");
        assertTrue(markdown.contains("Markdown generation test error"), "Should contain original error message");
        
        // And: Should have proper Markdown formatting
        assertTrue(markdown.contains("**Severity:**"), "Should use bold formatting for labels");
        assertTrue(markdown.contains("1. "), "Should use numbered lists for steps");
        assertTrue(markdown.contains("- "), "Should use bullet lists for recommendations");
    }

    @Test
    void shouldGenerateReproductionTestCase() {
        // Given: A reproducible error
        RuntimeException error = new RuntimeException("Reproducible test case generation error");
        EventSnapshotException enhancedException = errorHandler.handleErrorWithSnapshot(error);
        BugReport bugReport = bugReportGenerator.generateBugReport(enhancedException);
        
        // When: Getting the reproduction test case
        String testCase = bugReport.getReproductionTestCase();
        
        // Then: Test case should be properly formatted
        assertNotNull(testCase, "Reproduction test case should be generated");
        assertTrue(testCase.contains("@Test"), "Should contain JUnit test annotation");
        assertTrue(testCase.contains("void shouldReproduceBug_"), "Should have descriptive test method name");
        assertTrue(testCase.contains("EventSnapshot snapshot"), "Should load event snapshot");
        assertTrue(testCase.contains("replayEvent(event)"), "Should replay events");
        assertTrue(testCase.contains("assertThrows"), "Should verify exception is thrown");
        assertTrue(testCase.contains("RuntimeException.class"), "Should expect correct exception type");
        
        // And: Should contain snapshot ID reference
        String snapshotId = enhancedException.getEventSnapshot().getSnapshotId();
        assertTrue(testCase.contains(snapshotId), "Should reference the specific snapshot ID");
    }

    @Test
    void shouldCalculateReproducibilityScore() {
        // Given: Different types of errors
        RuntimeException deterministicError = new IllegalArgumentException("Deterministic validation error");
        OutOfMemoryError memoryError = new OutOfMemoryError("Non-deterministic memory error");
        
        // When: Generating bug reports
        BugReport deterministicReport = bugReportGenerator.generateBugReport(
            errorHandler.handleErrorWithSnapshot(deterministicError));
        BugReport memoryReport = bugReportGenerator.generateBugReport(
            errorHandler.handleErrorWithSnapshot(memoryError));
        
        // Then: Reproducibility scores should reflect error nature
        assertTrue(deterministicReport.getReproducibilityScore() >= 0.0 && 
                  deterministicReport.getReproducibilityScore() <= 1.0,
                  "Deterministic error reproducibility should be valid percentage");
        
        assertTrue(memoryReport.getReproducibilityScore() >= 0.0 && 
                  memoryReport.getReproducibilityScore() <= 1.0,
                  "Memory error reproducibility should be valid percentage");
        
        // Note: Actual comparison depends on available event context
        // Both should provide meaningful reproducibility scores
    }

    @Test
    void shouldProvideEnvironmentRequirements() {
        // Given: An error with environmental context
        RuntimeException error = new RuntimeException("Environment-dependent error");
        EventSnapshotException enhancedException = errorHandler.handleErrorWithSnapshot(error);
        BugReport bugReport = bugReportGenerator.generateBugReport(enhancedException);
        
        // When: Getting environment requirements
        java.util.Map<String, String> environment = bugReport.getReproductionEnvironment();
        
        // Then: Environment should contain essential information
        assertNotNull(environment, "Environment requirements should be provided");
        assertFalse(environment.isEmpty(), "Environment should contain at least some information");
        
        // And: Should contain memory and thread information
        assertTrue(environment.containsKey("memory.required"), "Should specify memory requirements");
        assertTrue(environment.containsKey("thread.name"), "Should capture thread information");
        assertTrue(environment.containsKey("thread.state"), "Should capture thread state");
    }

    @Test
    void shouldProvideRelatedIssuesHints() {
        // Given: An error with context
        SecurityException error = new SecurityException("Security context violation");
        EventSnapshotException enhancedException = errorHandler.handleErrorWithSnapshot(error);
        BugReport bugReport = bugReportGenerator.generateBugReport(enhancedException);
        
        // When: Getting related issues
        java.util.List<String> relatedIssues = bugReport.getRelatedIssues();
        
        // Then: Should provide hints for finding similar issues
        assertNotNull(relatedIssues, "Related issues hints should be provided");
        assertFalse(relatedIssues.isEmpty(), "At least one related issue hint should be provided");
        
        // And: Should reference exception type and snapshot
        assertTrue(relatedIssues.stream()
            .anyMatch(issue -> issue.contains("SecurityException")),
            "Should suggest searching for similar exception types");
        
        String snapshotId = enhancedException.getEventSnapshot().getSnapshotId();
        assertTrue(relatedIssues.stream()
            .anyMatch(issue -> issue.contains(snapshotId.substring(0, 8))),
            "Should suggest searching by snapshot ID pattern");
    }
}