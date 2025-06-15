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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedReader;
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

    @Test
    public void bytehot_agent_attachment_produces_agent_attached_event(@TempDir Path tempDir) throws Exception {
        // given: create a simple test class
        final Path testClassFile = tempDir.resolve("TestApp.java");
        final String testClassContent = """
            public class TestApp {
                public static void main(String[] args) {
                    System.out.println("TestApp started");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // ignore
                    }
                    System.out.println("TestApp finished");
                }
            }
            """;
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
            "-Dhsconfig=" + configPath,
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
        // Return the shaded agent jar with all dependencies
        return "target/bytehot-latest-SNAPSHOT-agent.jar";
    }
}