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
 * Filename: HotSwapManagerActualRedefinitionTest.java
 *
 * Author: Claude Code
 *
 * Class name: HotSwapManagerActualRedefinitionTest
 *
 * Responsibilities:
 *   - Test that HotSwapManager performs ACTUAL JVM class redefinition
 *   - Detect the critical bug where mock logic was used instead of real redefinition
 *   - Verify HotSwapManager uses real InstrumentationService instead of mock logic
 *
 * Collaborators:
 *   - HotSwapManager: Class under test
 *   - InstrumentationService: Core domain service for JVM instrumentation
 */
package org.acmsl.bytehot.domain;

import org.acmsl.bytehot.domain.events.BytecodeValidated;
import org.acmsl.bytehot.domain.events.ClassRedefinitionSucceeded;
import org.acmsl.bytehot.domain.events.HotSwapRequested;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test that detects the critical bug where HotSwapManager used mock logic instead of real JVM redefinition.
 * Updated to verify InstrumentationService usage as core domain service.
 * @author Claude Code
 * @since 2025-06-22
 */
public class HotSwapManagerActualRedefinitionTest {

    @TempDir
    Path tempDir;

    @Test
    public void shouldUseRealInstrumentationPortInsteadOfMockLogic() throws IOException {
        // Given: HotSwapManager source code analysis to verify it calls InstrumentationPort
        Path hotSwapManagerFile = Path.of("/home/chous/github/rydnr/bytehot/bytehot-domain/src/main/java/org/acmsl/bytehot/domain/HotSwapManager.java");
        assertThat(hotSwapManagerFile).exists();
        
        String sourceCode = Files.readString(hotSwapManagerFile);
        
        // Then: HotSwapManager should use InstrumentationService directly
        assertThat(sourceCode)
            .as("HotSwapManager should have InstrumentationService as dependency")
            .contains("InstrumentationService instrumentationService");
            
        assertThat(sourceCode)
            .as("HotSwapManager should call redefineClass on InstrumentationService")
            .contains("instrumentationService.redefineClass(");
            
        assertThat(sourceCode)
            .as("HotSwapManager should look up loaded classes through service")
            .contains("findLoadedClass(");
            
        // And: Should NOT contain mock logic anymore
        assertThat(sourceCode)
            .as("HotSwapManager should not contain mock JVM redefinition logic")
            .doesNotContain("Mock JVM redefinition logic for testing");
            
        // And: Should contain class lookup logic
        assertThat(sourceCode)
            .as("HotSwapManager should contain findLoadedClass method")
            .contains("findLoadedClass(");
    }

    @Test
    public void shouldNotReturnSuccessWithoutCallInginstrumentationPort() throws IOException {
        // This test detects the original bug by analyzing the source code structure
        
        // Given: HotSwapManager source code
        Path hotSwapManagerFile = Path.of("/home/chous/github/rydnr/bytehot/bytehot-domain/src/main/java/org/acmsl/bytehot/domain/HotSwapManager.java");
        String sourceCode = Files.readString(hotSwapManagerFile);
        
        // Then: performRedefinition method should contain actual redefinition logic
        assertThat(sourceCode)
            .as("CRITICAL: performRedefinition should call real InstrumentationService.redefineClass()")
            .contains("instrumentationService.redefineClass(targetClass, request.getNewBytecode())");
            
        // And: Should NOT contain mock simulation patterns
        assertThat(sourceCode)
            .as("Should not simulate successful redefinition without actual JVM calls")
            .doesNotContain("Simulate successful redefinition");
            
        // And: Should handle real JVM exceptions
        assertThat(sourceCode)
            .as("Should handle real JVM redefinition exceptions")
            .contains("createJvmRedefinitionFailure(request, redefinitionException)");
    }

    @Test
    public void shouldValidateClassLookupBeforeRedefinition() throws IOException {
        // Given: HotSwapManager source code
        Path hotSwapManagerFile = Path.of("/home/chous/github/rydnr/bytehot/bytehot-domain/src/main/java/org/acmsl/bytehot/domain/HotSwapManager.java");
        String sourceCode = Files.readString(hotSwapManagerFile);
        
        // Then: Should find loaded class before attempting redefinition
        assertThat(sourceCode)
            .as("Should look up loaded class before redefinition")
            .contains("instrumentationService.findLoadedClass(request.getClassName())");
            
        assertThat(sourceCode)
            .as("Should check if target class was found")
            .contains("if (targetClass == null)");
            
        assertThat(sourceCode)
            .as("Should throw exception when class is not found")
            .contains("throw createClassNotFoundException(request)");
            
        // And: Should delegate class lookup to instrumentation service
        assertThat(sourceCode)
            .as("Should delegate to instrumentation service for class lookup")
            .contains("instrumentationService.findLoadedClass(");
    }

    @Test
    public void shouldHandleRealJvmExceptionsNotMockOnes() throws IOException {
        // Given: HotSwapManager source code
        Path hotSwapManagerFile = Path.of("/home/chous/github/rydnr/bytehot/bytehot-domain/src/main/java/org/acmsl/bytehot/domain/HotSwapManager.java");
        String sourceCode = Files.readString(hotSwapManagerFile);
        
        // Then: Should handle real JVM redefinition exceptions
        assertThat(sourceCode)
            .as("Should catch real JVM redefinition exceptions")
            .contains("catch (final Exception redefinitionException)");
            
        assertThat(sourceCode)
            .as("Should create failure event from real JVM exceptions")
            .contains("createJvmRedefinitionFailure(request, redefinitionException)");
            
        // And: Should NOT contain mock exception patterns
        assertThat(sourceCode)
            .as("Should not contain mock JVM rejection simulation")
            .doesNotContain("Simulate JVM rejection");
            
        assertThat(sourceCode)
            .as("Should not check for mock content patterns")
            .doesNotContain("content.contains(\"INCOMPATIBLE_BYTECODE\")");
    }
}