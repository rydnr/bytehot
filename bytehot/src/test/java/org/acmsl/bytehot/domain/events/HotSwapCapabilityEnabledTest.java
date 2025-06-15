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
/*
 ******************************************************************************
 *
 * Filename: HotSwapCapabilityEnabledTest.java
 *
 * Author: Claude Code
 *
 * Class name: HotSwapCapabilityEnabledTest
 *
 * Responsibilities:
 *   - Test HotSwapCapabilityEnabled event when all checks pass
 *
 * Collaborators:
 *   - HotSwapCapabilityEnabled: The domain event being tested
 *   - ByteHot: The main application class
 */
package org.acmsl.bytehot.domain.events;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test HotSwapCapabilityEnabled event when all checks pass
 * @author Claude Code
 * @since 2025-06-15
 */
public class HotSwapCapabilityEnabledTest {

    /**
     * Tests that ByteHot agent produces HotSwapCapabilityEnabled event when JVM supports hot-swapping.
     */
    @Test
    public void bytehot_agent_with_hotswap_support_produces_capability_enabled_event(@TempDir Path tempDir) throws IOException, InterruptedException {
        // Given: A test Java class and valid configuration
        Path testJavaFile = tempDir.resolve("TestApp.java");
        Files.writeString(testJavaFile, """
            public class TestApp {
                public static void main(String[] args) {
                    System.out.println("Test application started");
                }
            }
            """);

        Path watchDir = tempDir.resolve("watch");
        Files.createDirectories(watchDir);
        
        Path configFile = tempDir.resolve("bytehot-config.yml");
        Files.writeString(configFile, """
            watchPaths:
              - """ + watchDir.toAbsolutePath() + """
            
            """);

        // Compile the test class
        ProcessBuilder compileBuilder = new ProcessBuilder("javac", testJavaFile.toString());
        Process compileProcess = compileBuilder.start();
        compileProcess.waitFor(10, TimeUnit.SECONDS);

        // When: Run the test app with ByteHot agent and configuration
        Path agentJar = Path.of(System.getProperty("user.dir") + "/target/bytehot-latest-SNAPSHOT-agent.jar");
        ProcessBuilder runBuilder = new ProcessBuilder(
            "java",
            "-javaagent:" + agentJar.toAbsolutePath(),
            "-Dbhconfig=" + configFile.toAbsolutePath(),
            "-cp", tempDir.toString(),
            "TestApp"
        );
        runBuilder.redirectErrorStream(true);
        Process runProcess = runBuilder.start();
        
        String output = new String(runProcess.getInputStream().readAllBytes());
        runProcess.waitFor(10, TimeUnit.SECONDS);

        // Then: Output should contain HotSwapCapabilityEnabled event
        assertTrue(output.contains("HotSwapCapabilityEnabled"), 
            "Expected HotSwapCapabilityEnabled event in output, but got: " + output);
    }
}