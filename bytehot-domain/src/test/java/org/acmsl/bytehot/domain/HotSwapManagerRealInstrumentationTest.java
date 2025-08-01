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
 * Filename: HotSwapManagerRealInstrumentationTest.java
 *
 * Author: Claude Code
 *
 * Class name: HotSwapManagerRealInstrumentationTest
 *
 * Responsibilities:
 *   - Test that HotSwapManager uses real InstrumentationService instead of mock logic
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
 * Test that HotSwapManager uses real InstrumentationService instead of mock logic
 * @author Claude Code
 * @since 2025-06-22
 */
public class HotSwapManagerRealInstrumentationTest {

    @TempDir
    Path tempDir;

    @Test
    public void shouldUseRealInstrumentationServiceInsteadOfMockLogic() throws IOException, HotSwapException {
        // Given: HotSwapManager source code analysis to verify it uses InstrumentationService
        Path hotSwapManagerFile = findHotSwapManagerFile();
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
            .as("HotSwapManager should use service for class lookup")
            .contains("instrumentationService.findLoadedClass(");
            
        // And: Should NOT contain mock logic anymore
        assertThat(sourceCode)
            .as("HotSwapManager should not contain mock JVM redefinition logic")
            .doesNotContain("Mock JVM redefinition logic for testing");
    }
    
    /**
     * Helper method to find the HotSwapManager.java file in different environments.
     * Tries multiple possible paths to be compatible with local development and CI/CD.
     */
    private Path findHotSwapManagerFile() {
        // Possible paths where the file might be located
        String[] possiblePaths = {
            "src/main/java/org/acmsl/bytehot/domain/HotSwapManager.java",
            "../src/main/java/org/acmsl/bytehot/domain/HotSwapManager.java",
            "./bytehot-domain/src/main/java/org/acmsl/bytehot/domain/HotSwapManager.java",
            "bytehot-domain/src/main/java/org/acmsl/bytehot/domain/HotSwapManager.java"
        };
        
        for (String pathStr : possiblePaths) {
            Path path = Path.of(pathStr);
            if (Files.exists(path)) {
                return path;
            }
        }
        
        // If none found, provide helpful error message
        String currentDir = System.getProperty("user.dir");
        throw new RuntimeException(
            "HotSwapManager.java not found in any expected location. Current directory: " + currentDir +
            ". Tried paths: " + String.join(", ", possiblePaths)
        );
    }
}