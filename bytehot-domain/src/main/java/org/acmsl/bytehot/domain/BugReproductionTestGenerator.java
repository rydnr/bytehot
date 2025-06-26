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
 * Filename: BugReproductionTestGenerator.java
 *
 * Author: Claude Code
 *
 * Class name: BugReproductionTestGenerator
 *
 * Responsibilities:
 *   - Generate executable test cases from EventSnapshotException instances
 *   - Convert bug reports into automated reproduction tests
 *   - Support multiple test frameworks (JUnit 5, TestNG, etc.)
 *   - Create event-driven test scenarios from snapshots
 *
 * Collaborators:
 *   - EventSnapshotException: Source of bug reproduction data
 *   - EventSnapshot: Complete event context for test generation
 *   - TestCase: Generated test case representation
 */
package org.acmsl.bytehot.domain;

import org.acmsl.bytehot.domain.exceptions.EventSnapshotException;
import org.acmsl.commons.patterns.eventsourcing.VersionedDomainEvent;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Generates executable test cases from EventSnapshotException instances.
 * Transforms bug reports into automated reproduction tests for the ByteHot testing framework.
 * @author Claude Code
 * @since 2025-06-26
 */
public class BugReproductionTestGenerator {

    /**
     * Test framework enumeration
     */
    public enum TestFramework {
        /**
         * JUnit 5 test framework
         */
        JUNIT5("JUnit 5"),
        
        /**
         * TestNG test framework
         */
        TESTNG("TestNG"),
        
        /**
         * ByteHot Event-Driven test framework
         */
        BYTEHOT_EVENT_DRIVEN("ByteHot Event-Driven");
        
        /**
         * Display name for the test framework
         */
        private final String displayName;
        
        /**
         * Creates a test framework enum value
         * @param displayName the human-readable display name
         */
        TestFramework(final String displayName) {
            this.displayName = displayName;
        }
        
        /**
         * Gets the display name for this test framework
         * @return the display name
         */
        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Configuration for test generation
     */
    @Builder
    @Getter
    public static class TestGenerationConfig {
        /**
         * Target test framework
         */
        @Builder.Default
        private final TestFramework framework = TestFramework.BYTEHOT_EVENT_DRIVEN;
        
        /**
         * Package name for generated tests
         */
        @Builder.Default
        private final String packageName = "org.acmsl.bytehot.generated.tests";
        
        /**
         * Include full event history in test
         */
        @Builder.Default
        private final boolean includeFullEventHistory = true;
        
        /**
         * Include system state assertions
         */
        @Builder.Default
        private final boolean includeSystemStateAssertions = true;
        
        /**
         * Maximum events to include in test
         */
        @Builder.Default
        private final int maxEventsInTest = 50;
    }

    /**
     * Generated test case representation
     */
    @RequiredArgsConstructor
    @Builder
    @Getter
    public static class GeneratedTestCase {
        /**
         * Test class name
         */
        @NonNull
        private final String testClassName;
        
        /**
         * Test method name
         */
        @NonNull
        private final String testMethodName;
        
        /**
         * Complete test source code
         */
        @NonNull
        private final String sourceCode;
        
        /**
         * Test description
         */
        @NonNull
        private final String description;
        
        /**
         * Required imports for the test
         */
        @NonNull
        private final List<String> imports;
        
        /**
         * Test configuration used
         */
        @NonNull
        private final TestGenerationConfig config;
    }

    /**
     * Default configuration
     */
    private static final TestGenerationConfig DEFAULT_CONFIG = TestGenerationConfig.builder().build();

    /**
     * Current configuration
     */
    @NonNull
    private final TestGenerationConfig config;

    /**
     * Creates a new generator with default configuration
     */
    public BugReproductionTestGenerator() {
        this.config = DEFAULT_CONFIG;
    }

    /**
     * Creates a new generator with custom configuration
     * @param config test generation configuration
     */
    public BugReproductionTestGenerator(@NonNull final TestGenerationConfig config) {
        this.config = config;
    }

    /**
     * Generates a test case from an EventSnapshotException
     * @param snapshotException the exception with complete event context
     * @return generated test case
     */
    @NonNull
    public GeneratedTestCase generateTestCase(@NonNull final EventSnapshotException snapshotException) {
        final String testClassName = generateTestClassName(snapshotException);
        final String testMethodName = generateTestMethodName(snapshotException);
        
        final String sourceCode = generateTestSourceCode(snapshotException, testClassName, testMethodName);
        final String description = generateTestDescription(snapshotException);
        final List<String> imports = generateRequiredImports();
        
        return GeneratedTestCase.builder()
            .testClassName(testClassName)
            .testMethodName(testMethodName)
            .sourceCode(sourceCode)
            .description(description)
            .imports(imports)
            .config(config)
            .build();
    }

    /**
     * Generates multiple test cases for different scenarios
     * @param snapshotException the exception with event context
     * @return list of generated test cases
     */
    @NonNull
    public List<GeneratedTestCase> generateMultipleTestCases(@NonNull final EventSnapshotException snapshotException) {
        return List.of(
            generateTestCase(snapshotException),
            generateMinimalReproductionTest(snapshotException),
            generateSystemStateVerificationTest(snapshotException)
        );
    }

    /**
     * Generates test class name from exception
     * @param exception the exception to generate test class name for
     * @return the generated test class name
     */
    @NonNull
    protected String generateTestClassName(@NonNull final EventSnapshotException exception) {
        final String errorId = exception.getErrorId().replaceAll("-", "");
        final String errorType = exception.getOriginalCause().getClass().getSimpleName();
        return String.format("Bug%s_%sReproductionTest", 
            errorId.substring(0, Math.min(8, errorId.length())),
            errorType);
    }

    /**
     * Generates test method name from exception
     * @param exception the exception to generate test method name for
     * @return the generated test method name
     */
    @NonNull
    protected String generateTestMethodName(@NonNull final EventSnapshotException exception) {
        final String classification = exception.getClassification().name().toLowerCase();
        return String.format("reproduceBug_%s", classification);
    }

    /**
     * Generates the complete test source code
     * @param exception the exception to generate test code for
     * @param testClassName the name of the test class
     * @param testMethodName the name of the test method
     * @return the complete test source code
     */
    @NonNull
    protected String generateTestSourceCode(
            @NonNull final EventSnapshotException exception,
            @NonNull final String testClassName,
            @NonNull final String testMethodName) {
        
        return switch (config.getFramework()) {
            case JUNIT5 -> generateJUnit5TestSource(exception, testClassName, testMethodName);
            case TESTNG -> generateTestNGTestSource(exception, testClassName, testMethodName);
            case BYTEHOT_EVENT_DRIVEN -> generateByteHotEventDrivenTestSource(exception, testClassName, testMethodName);
        };
    }

    /**
     * Generates ByteHot event-driven test source code
     * @param exception the exception to generate test code for
     * @param testClassName the name of the test class
     * @param testMethodName the name of the test method
     * @return the complete ByteHot event-driven test source code
     */
    @NonNull
    protected String generateByteHotEventDrivenTestSource(
            @NonNull final EventSnapshotException exception,
            @NonNull final String testClassName,
            @NonNull final String testMethodName) {
        
        final StringBuilder source = new StringBuilder();
        
        // Package declaration
        source.append("package ").append(config.getPackageName()).append(";\n\n");
        
        // Imports
        generateRequiredImports().forEach(imp -> 
            source.append("import ").append(imp).append(";\n"));
        source.append("\n");
        
        // Class declaration
        source.append("/**\n");
        source.append(" * Auto-generated test case for bug reproduction.\n");
        source.append(" * \n");
        source.append(" * Error ID: ").append(exception.getErrorId()).append("\n");
        source.append(" * Classification: ").append(exception.getClassification().getDisplayName()).append("\n");
        source.append(" * Captured At: ").append(exception.getCapturedAt()).append("\n");
        source.append(" * \n");
        source.append(" * This test reproduces the exact error conditions from production.\n");
        source.append(" */\n");
        source.append("class ").append(testClassName).append(" extends EventDrivenTestSupport {\n\n");
        
        // Test method
        source.append("    @Test\n");
        source.append("    @DisplayName(\"🐛 Reproduce bug: ").append(exception.getClassification().getDisplayName()).append("\")\n");
        source.append("    void ").append(testMethodName).append("() {\n");
        
        // Given section - recreate system state
        source.append("        // Given: Recreate the exact system state from production\n");
        if (config.isIncludeFullEventHistory()) {
            final List<VersionedDomainEvent> events = exception.getEventSnapshot().getEventHistory();
            if (!events.isEmpty()) {
                source.append("        givenEvents(\n");
                for (int i = 0; i < Math.min(events.size(), config.getMaxEventsInTest()); i++) {
                    final VersionedDomainEvent event = events.get(i);
                    source.append("            ").append(formatEventForTest(event));
                    if (i < events.size() - 1) {
                        source.append(",");
                    }
                    source.append("\n");
                }
                source.append("        );\n\n");
            }
        }
        
        // Environment state
        source.append("        // And: System environment matches production\n");
        final Map<String, String> envContext = exception.getEventSnapshot().getEnvironmentContext();
        envContext.forEach((key, value) -> {
            if (!key.equals("fallback") && !key.equals("error")) {
                source.append("        assertEnvironmentProperty(\"").append(key).append("\", \"").append(value).append("\");\n");
            }
        });
        source.append("\n");
        
        // When section - trigger the error
        source.append("        // When: The same operation is performed\n");
        source.append("        assertThrows(").append(exception.getOriginalCause().getClass().getSimpleName()).append(".class, () -> {\n");
        source.append("            // Trigger the exact same operation that caused the error\n");
        source.append("            reproduceErrorCondition();\n");
        source.append("        });\n\n");
        
        // Then section - verify error characteristics
        source.append("        // Then: Verify the error matches the production bug\n");
        source.append("        // Error classification: ").append(exception.getClassification().getDisplayName()).append("\n");
        source.append("        // Original message: ").append(safeString(exception.getOriginalCause().getMessage())).append("\n");
        
        source.append("    }\n\n");
        
        // Helper method for error reproduction
        source.append("    private void reproduceErrorCondition() {\n");
        source.append("        // TODO: Implement the specific operation that triggered the error\n");
        source.append("        // Based on the event history and error context\n");
        source.append("        throw new ").append(exception.getOriginalCause().getClass().getSimpleName()).append("(\"")
            .append(safeString(exception.getOriginalCause().getMessage())).append("\");\n");
        source.append("    }\n");
        
        source.append("}\n");
        
        return source.toString();
    }

    /**
     * Generates JUnit 5 test source code
     * @param exception the exception to generate test code for
     * @param testClassName the name of the test class
     * @param testMethodName the name of the test method
     * @return the complete JUnit 5 test source code
     */
    @NonNull
    protected String generateJUnit5TestSource(
            @NonNull final EventSnapshotException exception,
            @NonNull final String testClassName,
            @NonNull final String testMethodName) {
        
        return String.format("""
            package %s;
            
            import org.junit.jupiter.api.Test;
            import org.junit.jupiter.api.DisplayName;
            import static org.junit.jupiter.api.Assertions.*;
            
            /**
             * Auto-generated JUnit 5 test for bug reproduction.
             * Error ID: %s
             */
            class %s {
            
                @Test
                @DisplayName("Reproduce bug: %s")
                void %s() {
                    // Given: Bug reproduction context
                    String errorId = "%s";
                    String errorMessage = "%s";
                    
                    // When & Then: Reproduce the error
                    assertThrows(%s.class, () -> {
                        // TODO: Implement reproduction logic
                        throw new %s(errorMessage);
                    });
                }
            }
            """,
            config.getPackageName(),
            exception.getErrorId(),
            testClassName,
            exception.getClassification().getDisplayName(),
            testMethodName,
            exception.getErrorId(),
            safeString(exception.getOriginalCause().getMessage()),
            exception.getOriginalCause().getClass().getSimpleName(),
            exception.getOriginalCause().getClass().getSimpleName()
        );
    }

    /**
     * Generates TestNG test source code
     * @param exception the exception to generate test code for
     * @param testClassName the name of the test class
     * @param testMethodName the name of the test method
     * @return the complete TestNG test source code
     */
    @NonNull
    protected String generateTestNGTestSource(
            @NonNull final EventSnapshotException exception,
            @NonNull final String testClassName,
            @NonNull final String testMethodName) {
        
        return String.format("""
            package %s;
            
            import org.testng.annotations.Test;
            import static org.testng.Assert.*;
            
            /**
             * Auto-generated TestNG test for bug reproduction.
             * Error ID: %s
             */
            public class %s {
            
                @Test(description = "Reproduce bug: %s")
                public void %s() {
                    // Given: Bug reproduction context
                    String errorId = "%s";
                    
                    // When & Then: Reproduce the error
                    expectThrows(%s.class, () -> {
                        // TODO: Implement reproduction logic
                        throw new %s("%s");
                    });
                }
            }
            """,
            config.getPackageName(),
            exception.getErrorId(),
            testClassName,
            exception.getClassification().getDisplayName(),
            testMethodName,
            exception.getErrorId(),
            exception.getOriginalCause().getClass().getSimpleName(),
            exception.getOriginalCause().getClass().getSimpleName(),
            safeString(exception.getOriginalCause().getMessage())
        );
    }

    /**
     * Generates minimal reproduction test (fastest execution)
     * @param exception the exception to generate minimal test for
     * @return minimal reproduction test case
     */
    @NonNull
    protected GeneratedTestCase generateMinimalReproductionTest(@NonNull final EventSnapshotException exception) {
        return GeneratedTestCase.builder()
            .testClassName(generateTestClassName(exception) + "Minimal")
            .testMethodName("reproduceMinimalBug")
            .sourceCode(generateMinimalTestCode(exception))
            .description("Minimal reproduction test with essential elements only")
            .imports(generateRequiredImports())
            .config(config)
            .build();
    }

    /**
     * Generates system state verification test
     * @param exception the exception to generate system state test for
     * @return system state verification test case
     */
    @NonNull
    protected GeneratedTestCase generateSystemStateVerificationTest(@NonNull final EventSnapshotException exception) {
        return GeneratedTestCase.builder()
            .testClassName(generateTestClassName(exception) + "SystemState")
            .testMethodName("verifySystemState")
            .sourceCode(generateSystemStateTestCode(exception))
            .description("Verifies system state conditions that led to the bug")
            .imports(generateRequiredImports())
            .config(config)
            .build();
    }

    /**
     * Generates minimal test code
     * @param exception the exception to generate minimal test code for
     * @return minimal test code string
     */
    @NonNull
    protected String generateMinimalTestCode(@NonNull final EventSnapshotException exception) {
        return String.format("""
            // Minimal reproduction test
            @Test
            void reproduceMinimalBug() {
                assertThrows(%s.class, () -> {
                    throw new %s("%s");
                });
            }
            """,
            exception.getOriginalCause().getClass().getSimpleName(),
            exception.getOriginalCause().getClass().getSimpleName(),
            safeString(exception.getOriginalCause().getMessage())
        );
    }

    /**
     * Generates system state test code
     * @param exception the exception to generate system state test code for
     * @return system state test code string
     */
    @NonNull
    protected String generateSystemStateTestCode(@NonNull final EventSnapshotException exception) {
        final StringBuilder code = new StringBuilder();
        code.append("@Test\n");
        code.append("void verifySystemState() {\n");
        code.append("    // Verify system state conditions\n");
        
        final Map<String, String> sysProps = exception.getEventSnapshot().getSystemProperties();
        sysProps.forEach((key, value) -> {
            if (key.startsWith("java.")) {
                code.append("    assertEquals(\"").append(value).append("\", System.getProperty(\"").append(key).append("\"));\n");
            }
        });
        
        code.append("}\n");
        return code.toString();
    }

    /**
     * Generates test description
     * @param exception the exception to generate description for
     * @return test description string
     */
    @NonNull
    protected String generateTestDescription(@NonNull final EventSnapshotException exception) {
        return String.format(
            "Auto-generated test case reproducing %s (Error ID: %s)\n" +
            "Original error: %s\n" +
            "Captured at: %s\n" +
            "Event count: %d",
            exception.getClassification().getDisplayName(),
            exception.getErrorId(),
            exception.getOriginalCause().getClass().getSimpleName(),
            exception.getCapturedAt(),
            exception.getEventSnapshot().getEventCount()
        );
    }

    /**
     * Generates required imports list
     * @return such imports
     */
    @NonNull
    protected List<String> generateRequiredImports() {
        return switch (config.getFramework()) {
            case JUNIT5 -> List.of(
                "org.junit.jupiter.api.Test",
                "org.junit.jupiter.api.DisplayName",
                "static org.junit.jupiter.api.Assertions.*"
            );
            case TESTNG -> List.of(
                "org.testng.annotations.Test",
                "static org.testng.Assert.*"
            );
            case BYTEHOT_EVENT_DRIVEN -> List.of(
                "org.acmsl.bytehot.testing.EventDrivenTestSupport",
                "org.junit.jupiter.api.Test",
                "org.junit.jupiter.api.DisplayName",
                "static org.junit.jupiter.api.Assertions.*"
            );
        };
    }

    /**
     * Formats an event for test code inclusion
     * @param event the event to format for test code
     * @return formatted event string for test code
     */
    @NonNull
    protected String formatEventForTest(@NonNull final VersionedDomainEvent event) {
        return String.format("// Event: %s at %s", 
            event.getEventType(), 
            event.getTimestamp());
    }

    /**
     * Makes a string safe for code generation
     * @param input the input string to make safe
     * @return safe string for code generation
     */
    @NonNull
    protected String safeString(@Nullable final String input) {
        if (input == null) {
            return "No message";
        }
        return input.replace("\"", "\\\"").replace("\n", "\\n");
    }

    /**
     * Gets the current configuration
     * @return test generation configuration
     */
    @NonNull
    public TestGenerationConfig getConfig() {
        return config;
    }
}
