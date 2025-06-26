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
package org.acmsl.bytehot.domain;

import org.acmsl.bytehot.domain.BugReproductionTestGenerator.GeneratedTestCase;
import org.acmsl.bytehot.domain.BugReproductionTestGenerator.TestFramework;
import org.acmsl.bytehot.domain.BugReproductionTestGenerator.TestGenerationConfig;
import org.acmsl.bytehot.domain.exceptions.EventSnapshotException;
import org.acmsl.commons.patterns.DomainEvent;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for BugReproductionTestGenerator - automatic test case generation from bugs.
 * 
 * @author Claude Code
 * @since 2025-06-26
 */
class BugReproductionTestGeneratorTest {

    private BugReproductionTestGenerator generator;
    private EventSnapshotException sampleException;

    @BeforeEach
    void setUp() {
        generator = new BugReproductionTestGenerator();
        
        // Create a sample exception for testing
        final RuntimeException originalError = new IllegalStateException("Hot-swap failed: incompatible changes");
        sampleException = EventSnapshotException.captureAndThrow(
            originalError,
            "Test error for reproduction",
            List.<DomainEvent>of()
        );
    }

    @Test
    @DisplayName("🧪 BugReproductionTestGenerator generates valid test case from exception")
    void bugReproductionTestGeneratorGeneratesValidTestCase() {
        // When: We generate a test case from an exception
        final GeneratedTestCase testCase = generator.generateTestCase(sampleException);
        
        // Then: The test case is properly generated
        assertNotNull(testCase, "Test case should be generated");
        assertNotNull(testCase.getTestClassName(), "Test class name should be generated");
        assertNotNull(testCase.getTestMethodName(), "Test method name should be generated");
        assertNotNull(testCase.getSourceCode(), "Source code should be generated");
        assertNotNull(testCase.getDescription(), "Description should be generated");
        assertNotNull(testCase.getImports(), "Imports should be generated");
        
        // And: Test class name follows expected pattern
        assertTrue(testCase.getTestClassName().contains("Bug"), "Test class should contain 'Bug'");
        assertTrue(testCase.getTestClassName().contains("ReproductionTest"), "Test class should contain 'ReproductionTest'");
        assertTrue(testCase.getTestClassName().contains("IllegalStateException"), "Test class should contain exception type");
        
        // And: Test method name is appropriate
        assertTrue(testCase.getTestMethodName().startsWith("reproduceBug_"), "Test method should start with 'reproduceBug_'");
        
        // And: Source code contains essential elements
        final String sourceCode = testCase.getSourceCode();
        assertTrue(sourceCode.contains("package"), "Source should include package declaration");
        assertTrue(sourceCode.contains("class"), "Source should include class declaration");
        assertTrue(sourceCode.contains("@Test"), "Source should include test annotation");
        assertTrue(sourceCode.contains("void " + testCase.getTestMethodName()), "Source should include test method");
        assertTrue(sourceCode.contains("IllegalStateException"), "Source should reference the original exception");
        
        // And: Description contains relevant information
        final String description = testCase.getDescription();
        assertTrue(description.contains(sampleException.getErrorId()), "Description should include error ID");
        assertTrue(description.contains("IllegalStateException"), "Description should include exception type");
    }
    
    @Test
    @DisplayName("🎨 Test generator supports different frameworks")
    void testGeneratorSupportsDifferentFrameworks() {
        // Test JUnit 5 generation
        final TestGenerationConfig junitConfig = TestGenerationConfig.builder()
            .framework(TestFramework.JUNIT5)
            .build();
        final BugReproductionTestGenerator junitGenerator = new BugReproductionTestGenerator(junitConfig);
        
        final GeneratedTestCase junitTest = junitGenerator.generateTestCase(sampleException);
        
        assertTrue(junitTest.getSourceCode().contains("import org.junit.jupiter.api.Test"), 
            "JUnit 5 test should import JUnit annotations");
        assertTrue(junitTest.getSourceCode().contains("@DisplayName"), 
            "JUnit 5 test should use @DisplayName");
        assertTrue(junitTest.getSourceCode().contains("assertThrows"), 
            "JUnit 5 test should use assertThrows");
        
        // Test TestNG generation
        final TestGenerationConfig testngConfig = TestGenerationConfig.builder()
            .framework(TestFramework.TESTNG)
            .build();
        final BugReproductionTestGenerator testngGenerator = new BugReproductionTestGenerator(testngConfig);
        
        final GeneratedTestCase testngTest = testngGenerator.generateTestCase(sampleException);
        
        assertTrue(testngTest.getSourceCode().contains("import org.testng.annotations.Test"), 
            "TestNG test should import TestNG annotations");
        assertTrue(testngTest.getSourceCode().contains("@Test(description"), 
            "TestNG test should use description attribute");
        
        // Test ByteHot Event-Driven generation
        final TestGenerationConfig eventDrivenConfig = TestGenerationConfig.builder()
            .framework(TestFramework.BYTEHOT_EVENT_DRIVEN)
            .build();
        final BugReproductionTestGenerator eventDrivenGenerator = new BugReproductionTestGenerator(eventDrivenConfig);
        
        final GeneratedTestCase eventDrivenTest = eventDrivenGenerator.generateTestCase(sampleException);
        
        assertTrue(eventDrivenTest.getSourceCode().contains("EventDrivenTestSupport"), 
            "Event-driven test should extend EventDrivenTestSupport");
        assertTrue(eventDrivenTest.getSourceCode().contains("// Given:"), 
            "Event-driven test should use Given/When/Then structure");
        assertTrue(eventDrivenTest.getSourceCode().contains("// When:"), 
            "Event-driven test should include When section");
        assertTrue(eventDrivenTest.getSourceCode().contains("// Then:"), 
            "Event-driven test should include Then section");
    }
    
    @Test
    @DisplayName("🔄 Generator creates multiple test variants")
    void generatorCreatesMultipleTestVariants() {
        // When: We generate multiple test cases
        final List<GeneratedTestCase> testCases = generator.generateMultipleTestCases(sampleException);
        
        // Then: Multiple test variants are generated
        assertNotNull(testCases, "Test cases list should not be null");
        assertEquals(3, testCases.size(), "Should generate 3 test variants");
        
        // And: Each test case has a different purpose
        final List<String> classNames = testCases.stream()
            .map(GeneratedTestCase::getTestClassName)
            .toList();
        
        assertTrue(classNames.stream().anyMatch(name -> name.contains("ReproductionTest")), 
            "Should include main reproduction test");
        assertTrue(classNames.stream().anyMatch(name -> name.contains("Minimal")), 
            "Should include minimal reproduction test");
        assertTrue(classNames.stream().anyMatch(name -> name.contains("SystemState")), 
            "Should include system state verification test");
        
        // And: Each test case has valid source code
        testCases.forEach(testCase -> {
            assertNotNull(testCase.getSourceCode(), "Each test case should have source code");
            assertTrue(testCase.getSourceCode().length() > 0, "Source code should not be empty");
        });
    }
    
    @Test
    @DisplayName("⚙️ Generator respects configuration options")
    void generatorRespectsConfigurationOptions() {
        // Given: Custom configuration
        final TestGenerationConfig customConfig = TestGenerationConfig.builder()
            .packageName("com.example.bugs")
            .framework(TestFramework.JUNIT5)
            .includeFullEventHistory(false)
            .includeSystemStateAssertions(false)
            .maxEventsInTest(10)
            .build();
        
        final BugReproductionTestGenerator customGenerator = new BugReproductionTestGenerator(customConfig);
        
        // When: We generate a test case
        final GeneratedTestCase testCase = customGenerator.generateTestCase(sampleException);
        
        // Then: Configuration is respected
        assertTrue(testCase.getSourceCode().contains("package com.example.bugs"), 
            "Should use custom package name");
        assertEquals(TestFramework.JUNIT5, testCase.getConfig().getFramework(), 
            "Should use specified framework");
        assertEquals("com.example.bugs", testCase.getConfig().getPackageName(), 
            "Should preserve package configuration");
        assertFalse(testCase.getConfig().isIncludeFullEventHistory(), 
            "Should preserve event history configuration");
        assertEquals(10, testCase.getConfig().getMaxEventsInTest(), 
            "Should preserve max events configuration");
    }
    
    @Test
    @DisplayName("🛡️ Generator handles edge cases gracefully")
    void generatorHandlesEdgeCasesGracefully() {
        // Test with null message exception
        final RuntimeException nullMessageError = new RuntimeException();
        final EventSnapshotException nullMessageException = EventSnapshotException.captureAndThrow(
            nullMessageError,
            "Error with null message",
            List.<DomainEvent>of()
        );
        
        final GeneratedTestCase nullMessageTest = generator.generateTestCase(nullMessageException);
        
        assertNotNull(nullMessageTest, "Should handle null message gracefully");
        assertNotNull(nullMessageTest.getSourceCode(), "Should generate valid source code");
        assertTrue(nullMessageTest.getSourceCode().contains("RuntimeException"), 
            "Should include correct exception type");
        
        // Test with very long error message
        final String longMessage = "A".repeat(10000);
        final RuntimeException longMessageError = new RuntimeException(longMessage);
        final EventSnapshotException longMessageException = EventSnapshotException.captureAndThrow(
            longMessageError,
            "Error with very long message",
            List.<DomainEvent>of()
        );
        
        final GeneratedTestCase longMessageTest = generator.generateTestCase(longMessageException);
        
        assertNotNull(longMessageTest, "Should handle long message gracefully");
        assertNotNull(longMessageTest.getSourceCode(), "Should generate valid source code");
        assertTrue(longMessageTest.getSourceCode().length() > 0, "Source code should not be empty");
    }
    
    @Test
    @DisplayName("📝 Generated tests are syntactically valid")
    void generatedTestsAreSyntacticallyValid() {
        // When: We generate test cases for different frameworks
        final List<TestFramework> frameworks = List.of(
            TestFramework.JUNIT5,
            TestFramework.TESTNG,
            TestFramework.BYTEHOT_EVENT_DRIVEN
        );
        
        frameworks.forEach(framework -> {
            final TestGenerationConfig config = TestGenerationConfig.builder()
                .framework(framework)
                .build();
            final BugReproductionTestGenerator frameworkGenerator = new BugReproductionTestGenerator(config);
            
            final GeneratedTestCase testCase = frameworkGenerator.generateTestCase(sampleException);
            
            // Then: Generated code has basic syntactic validity
            final String sourceCode = testCase.getSourceCode();
            
            // Check for balanced braces
            final long openBraces = sourceCode.chars().filter(ch -> ch == '{').count();
            final long closeBraces = sourceCode.chars().filter(ch -> ch == '}').count();
            assertEquals(openBraces, closeBraces, 
                "Generated code should have balanced braces for " + framework);
            
            // Check for proper Java class structure
            assertTrue(sourceCode.contains("class " + testCase.getTestClassName()), 
                "Should contain class declaration for " + framework);
            assertTrue(sourceCode.contains("void " + testCase.getTestMethodName()), 
                "Should contain test method for " + framework);
            
            // Check for proper imports
            assertFalse(testCase.getImports().isEmpty(), 
                "Should have required imports for " + framework);
        });
    }
}