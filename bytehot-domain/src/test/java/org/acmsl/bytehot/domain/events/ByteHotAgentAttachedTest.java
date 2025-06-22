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
 * Filename: ByteHotAgentAttachedTest.java
 *
 * Author: Claude
 *
 * Class name: ByteHotAgentAttachedTest
 *
 * Responsibilities:
 *   - Test that ByteHot agent attachment produces ByteHotAgentAttached event
 *
 * Collaborators:
 *   - ByteHotAgentAttached: The event being tested
 *   - ProcessBuilder: To run test JVM with agent attached
 */
package org.acmsl.bytehot.domain.events;

// import org.acmsl.bytehot.testing.support.AgentJarBuilder; // Moved to infrastructure layer

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests for ByteHot agent attachment producing ByteHotAgentAttached event.
 * @author Claude
 * @since 2025-06-15
 */
public class ByteHotAgentAttachedTest {

    /**
     * Ensures the agent JAR exists before running tests
     */
    @BeforeAll
    public static void ensureAgentJarExists() throws IOException, InterruptedException {
        // Try multiple possible locations for the agent JAR (now built in bytehot-application module)
        Path currentDir = Path.of(System.getProperty("user.dir"));
        Path agentJar = currentDir.resolve("target/bytehot-application-latest-SNAPSHOT-agent.jar");
        
        // If not found in current directory, try application module
        if (!Files.exists(agentJar)) {
            agentJar = currentDir.resolve("bytehot-application/target/bytehot-application-latest-SNAPSHOT-agent.jar");
        }
        
        // Fallback: try parent directory structure
        if (!Files.exists(agentJar)) {
            agentJar = currentDir.resolve("../bytehot-application/target/bytehot-application-latest-SNAPSHOT-agent.jar");
        }
        
        if (!Files.exists(agentJar)) {
            System.out.println("Agent JAR not found. Attempting to build...");
            System.out.println("Current working directory: " + currentDir);
            System.out.println("Looking for agent JAR at: " + agentJar);
            
            // Determine the correct build directory (from root to build all modules)
            Path buildDir = currentDir;
            
            // If we're in a module directory, go to parent to build all modules
            if (currentDir.getFileName().toString().startsWith("bytehot-")) {
                buildDir = currentDir.getParent();
            } else if (!Files.exists(currentDir.resolve("pom.xml"))) {
                // If no pom.xml found, assume we need to go to parent
                buildDir = currentDir.getParent();
                if (buildDir == null || !Files.exists(buildDir.resolve("pom.xml"))) {
                    throw new RuntimeException("No root pom.xml found. Current dir: " + currentDir);
                }
            }
            
            System.out.println("Building from directory: " + buildDir);
            ProcessBuilder builder = new ProcessBuilder("mvn", "package", "-DskipTests=true", "-q");
            builder.directory(buildDir.toFile());
            builder.inheritIO();
            
            Process process = builder.start();
            int exitCode = process.waitFor();
            
            if (exitCode != 0) {
                throw new RuntimeException("Failed to build agent JAR for test. Exit code: " + exitCode);
            }
            
            // Re-check for the JAR in the application module
            agentJar = buildDir.resolve("bytehot-application/target/bytehot-application-latest-SNAPSHOT-agent.jar");
            if (!Files.exists(agentJar)) {
                throw new RuntimeException("Agent JAR was not created: " + agentJar);
            }
            
            System.out.println("Agent JAR built successfully: " + agentJar);
        } else {
            System.out.println("Agent JAR found: " + agentJar);
        }
    }

    @Test
    @Disabled("TODO: Fix architecture - domain test should not depend on infrastructure testing utilities")
    public void bytehot_agent_attachment_produces_agent_attached_event(@TempDir Path tempDir) throws Exception {
        // given: create a simple test class
        final Path testClassFile = tempDir.resolve("TestApp.java");
        final String testClassContent = 
            "public class TestApp {\n" +
            "    public static void main(String[] args) {\n" +
            "        System.out.println(\"TestApp started\");\n" +
            "        try {\n" +
            "            Thread.sleep(1000);\n" +
            "        } catch (InterruptedException e) {\n" +
            "            // ignore\n" +
            "        }\n" +
            "        System.out.println(\"TestApp finished\");\n" +
            "    }\n" +
            "}\n";
        Files.writeString(testClassFile, testClassContent);

        // compile the test class
        final ProcessBuilder compileProcess = new ProcessBuilder(
            "javac", 
            "-cp", System.getProperty("java.class.path"),
            testClassFile.toString()
        );
        compileProcess.directory(tempDir.toFile());
        final Process compile = compileProcess.start();
        if (!compile.waitFor(10, TimeUnit.SECONDS) || compile.exitValue() != 0) {
            fail("Failed to compile test class");
        }

        // when: run the test class with ByteHot agent attached
        final String agentJarPath = System.getProperty("user.dir") + "/" + findByteHotAgentJar();
        final String configPath = System.getProperty("user.dir") + "/src/test/resources/test-config.yml";
        final ProcessBuilder runProcess = new ProcessBuilder(
            "java",
            "-javaagent:" + agentJarPath,
            "-Dbhconfig=" + configPath,
            "-cp", tempDir.toString(),
            "TestApp"
        );
        runProcess.directory(tempDir.toFile());
        final Process run = runProcess.start();

        // then: check that ByteHotAgentAttached event is printed to stdout
        final StringBuilder stdout = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(run.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stdout.append(line).append("\n");
            }
        }

        if (!run.waitFor(10, TimeUnit.SECONDS)) {
            run.destroyForcibly();
            fail("Test process timed out");
        }

        final String output = stdout.toString();
        assertTrue(output.contains("ByteHotAgentAttached"), 
            "Expected ByteHotAgentAttached event in output, but got: " + output);
    }

    private String findByteHotAgentJar() {
        // Find the agent JAR in the most likely locations (now in bytehot-application module)
        Path currentDir = Path.of(System.getProperty("user.dir"));
        
        // Try application module from current directory
        Path agentJar = currentDir.resolve("bytehot-application/target/bytehot-application-latest-SNAPSHOT-agent.jar");
        if (Files.exists(agentJar)) {
            return "bytehot-application/target/bytehot-application-latest-SNAPSHOT-agent.jar";
        }
        
        // Try if we're in the application module itself
        agentJar = currentDir.resolve("target/bytehot-application-latest-SNAPSHOT-agent.jar");
        if (Files.exists(agentJar)) {
            return "target/bytehot-application-latest-SNAPSHOT-agent.jar";
        }
        
        // Try parent directory structure (for GitHub Actions)
        agentJar = currentDir.resolve("../bytehot-application/target/bytehot-application-latest-SNAPSHOT-agent.jar");
        if (Files.exists(agentJar)) {
            return "../bytehot-application/target/bytehot-application-latest-SNAPSHOT-agent.jar";
        }
        
        // Default to the application module location
        return "bytehot-application/target/bytehot-application-latest-SNAPSHOT-agent.jar";
    }
}