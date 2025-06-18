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
 * Filename: HotSwapCapabilityNixTest.java
 *
 * Author: Claude Code
 *
 * Class name: HotSwapCapabilityNixTest
 *
 * Responsibilities:
 *   - Test HotSwapCapabilityEnabled event with different JVM versions using Nix
 *
 * Collaborators:
 *   - Nix flake: Provides different JVM versions
 *   - ByteHot: The main application class
 */
package org.acmsl.bytehot.domain.events;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Test HotSwapCapabilityEnabled event with different JVM versions using Nix
 * @author Claude Code
 * @since 2025-06-15
 */
public class HotSwapCapabilityNixTest {

    private boolean nixAvailable = false;

    @BeforeEach
    public void checkNixAvailability() {
        try {
            ProcessBuilder nixCheck = new ProcessBuilder("nix", "--version");
            Process process = nixCheck.start();
            nixAvailable = process.waitFor(5, TimeUnit.SECONDS) && process.exitValue() == 0;
        } catch (IOException | InterruptedException e) {
            nixAvailable = false;
        }
    }

    /**
     * Tests that newer JVM versions (21+) support hot-swapping capabilities.
     */
    @Test
    public void modern_jvm_supports_hotswap_capabilities(@TempDir Path tempDir) throws IOException, InterruptedException {
        assumeTrue(nixAvailable, "Nix is not available - skipping test");
        
        testJvmHotSwapCapability(tempDir, "21", true);
    }

    /**
     * Tests that older JVM versions (8) may have limited hot-swapping capabilities.
     */
    @Test
    public void legacy_jvm_may_have_limited_hotswap_capabilities(@TempDir Path tempDir) throws IOException, InterruptedException {
        assumeTrue(nixAvailable, "Nix is not available - skipping test");
        
        // Java 8 should still support basic hot-swap, but let's test it
        testJvmHotSwapCapability(tempDir, "8", true);
    }

    /**
     * Tests HotSwap capabilities with a specific JVM version using Nix.
     */
    private void testJvmHotSwapCapability(@TempDir Path tempDir, String jvmVersion, boolean expectHotSwapSupport) 
            throws IOException, InterruptedException {
        
        // First: Build the agent with the target JVM version to avoid UnsupportedClassVersionError
        ProcessBuilder packageBuilder = new ProcessBuilder(
            "nix", "develop", "./.nix#rydnr-bytehot-" + jvmVersion, "-c",
            "mvn", "clean", "package", "-DskipTests", 
            "-Dmaven.compiler.source=" + (jvmVersion.equals("8") ? "1.8" : jvmVersion),
            "-Dmaven.compiler.target=" + (jvmVersion.equals("8") ? "1.8" : jvmVersion),
            "-Dmaven.compiler.release=" + (jvmVersion.equals("8") ? "8" : jvmVersion)
        );
        packageBuilder.directory(Path.of(".").toFile());
        packageBuilder.redirectErrorStream(true);
        Process packageProcess = packageBuilder.start();
        
        if (!packageProcess.waitFor(120, TimeUnit.SECONDS) || packageProcess.exitValue() != 0) {
            String error = new String(packageProcess.getInputStream().readAllBytes());
            throw new RuntimeException("Failed to build agent with JVM " + jvmVersion + ": " + error);
        }
        
        // Given: A test Java class and valid configuration
        Path testJavaFile = tempDir.resolve("TestApp.java");
        Files.writeString(testJavaFile, """
            public class TestApp {
                public static void main(String[] args) {
                    System.out.println("Test application started with JVM""" + jvmVersion + """
");
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

        // Compile the test class using Nix shell
        ProcessBuilder compileBuilder = new ProcessBuilder(
            "nix", "develop", "./.nix#rydnr-bytehot-" + jvmVersion, "-c",
            "javac", testJavaFile.toString()
        );
        compileBuilder.directory(Path.of(".").toFile());
        Process compileProcess = compileBuilder.start();
        
        if (!compileProcess.waitFor(30, TimeUnit.SECONDS) || compileProcess.exitValue() != 0) {
            String error = new String(compileProcess.getErrorStream().readAllBytes());
            throw new RuntimeException("Failed to compile with JVM " + jvmVersion + ": " + error);
        }

        // When: Run the test app with ByteHot agent using Nix shell
        Path agentJar = Path.of(System.getProperty("user.dir") + "/target/bytehot-latest-SNAPSHOT-agent.jar");
        ProcessBuilder runBuilder = new ProcessBuilder(
            "nix", "develop", "./.nix#rydnr-bytehot-" + jvmVersion, "-c",
            "sh", "-c", 
            "echo 'JAVA_HOME='$JAVA_HOME && echo 'Java version:' && java -version && " +
            "java -javaagent:" + agentJar.toAbsolutePath() + 
            " -Dbhconfig=" + configFile.toAbsolutePath() + 
            " -cp " + tempDir.toString() + " TestApp"
        );
        runBuilder.directory(Path.of(".").toFile());
        runBuilder.redirectErrorStream(true);
        Process runProcess = runBuilder.start();
        
        String output = new String(runProcess.getInputStream().readAllBytes());
        if (!runProcess.waitFor(30, TimeUnit.SECONDS)) {
            runProcess.destroyForcibly();
            throw new RuntimeException("Process timed out for JVM " + jvmVersion);
        }

        // Then: Check the output based on expected hot-swap support
        assertTrue(output.contains("ByteHotAgentAttached"), 
            "ByteHotAgentAttached should always be present. Output: " + output);
        
        assertTrue(output.contains("WatchPathConfigured"), 
            "WatchPathConfigured should always be present. Output: " + output);
        
        assertTrue(output.contains("Test application started with JVM" + jvmVersion), 
            "Test application should start successfully. Output: " + output);

        if (expectHotSwapSupport) {
            assertTrue(output.contains("HotSwapCapabilityEnabled"), 
                "JVM " + jvmVersion + " should support HotSwapCapabilityEnabled. Output: " + output);
        } else {
            assertFalse(output.contains("HotSwapCapabilityEnabled"), 
                "JVM " + jvmVersion + " should NOT support HotSwapCapabilityEnabled. Output: " + output);
        }
    }
}