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

import org.acmsl.bytehot.testing.support.AgentJarBuilder;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
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
        // Try multiple possible locations for the agent JAR
        Path currentDir = Path.of(System.getProperty("user.dir"));
        Path agentJar = currentDir.resolve("target/bytehot-latest-SNAPSHOT-agent.jar");
        
        // If not found in current directory, try parent directory (for GitHub Actions)
        if (!Files.exists(agentJar)) {
            agentJar = currentDir.resolve("bytehot/target/bytehot-latest-SNAPSHOT-agent.jar");
        }
        
        if (!Files.exists(agentJar)) {
            System.out.println("Agent JAR not found. Attempting to build...");
            System.out.println("Current working directory: " + currentDir);
            System.out.println("Looking for agent JAR at: " + agentJar);
            
            // Determine the correct build directory
            Path buildDir = currentDir;
            if (Files.exists(currentDir.resolve("bytehot/pom.xml"))) {
                // We're in the parent directory
                buildDir = currentDir.resolve("bytehot");
            } else if (!Files.exists(currentDir.resolve("pom.xml"))) {
                // Neither current nor bytehot subdirectory has pom.xml
                throw new RuntimeException("No pom.xml found in " + currentDir + " or " + currentDir.resolve("bytehot"));
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
            
            // Re-check for the JAR in the expected location
            agentJar = buildDir.resolve("target/bytehot-latest-SNAPSHOT-agent.jar");
            if (!Files.exists(agentJar)) {
                throw new RuntimeException("Agent JAR was not created: " + agentJar);
            }
            
            System.out.println("Agent JAR built successfully: " + agentJar);
        } else {
            System.out.println("Agent JAR found: " + agentJar);
        }
    }

    @Test
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
        // Find the agent JAR in the most likely locations
        Path currentDir = Path.of(System.getProperty("user.dir"));
        Path agentJar = currentDir.resolve("target/bytehot-latest-SNAPSHOT-agent.jar");
        
        if (Files.exists(agentJar)) {
            return "target/bytehot-latest-SNAPSHOT-agent.jar";
        }
        
        // Try parent directory structure (for GitHub Actions)
        agentJar = currentDir.resolve("bytehot/target/bytehot-latest-SNAPSHOT-agent.jar");
        if (Files.exists(agentJar)) {
            return "bytehot/target/bytehot-latest-SNAPSHOT-agent.jar";
        }
        
        // Default to the standard location
        return "target/bytehot-latest-SNAPSHOT-agent.jar";
    }
}