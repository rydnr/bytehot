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
 * Test HotSwapCapabilityEnabled event with different JVM versions using Nix.
 * 
 * NOTE: These tests are disabled by default to prevent dependency version conflicts
 * when building from the parent directory. To enable these tests, run:
 * 
 * mvn test -Dnix.tests.enabled=true
 * 
 * These tests require:
 * - Nix package manager installed
 * - All dependencies compiled with the same Java version
 * 
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
     * Checks if we're running with Java 8 profile which has dependency compatibility issues
     */
    private boolean isJava8Profile() {
        return System.getProperty("java8") != null;
    }

    /**
     * Checks if Nix tests are explicitly enabled via system property
     */
    private boolean areNixTestsEnabled() {
        return "true".equals(System.getProperty("nix.tests.enabled"));
    }

    /**
     * Tests that minimum supported JVM version (17) works correctly.
     * ByteHot is currently configured to require Java 17+ in the Maven configuration.
     */
    @Test
    public void minimum_supported_jvm_17_works(@TempDir Path tempDir) throws IOException, InterruptedException {
        assumeTrue(areNixTestsEnabled(), "Nix tests are disabled by default. Enable with -Dnix.tests.enabled=true");
        assumeTrue(nixAvailable, "Nix is not available - skipping test");
        assumeTrue(!isJava8Profile(), "Skipping Nix test when using Java 8 profile due to dependency compatibility issues");
        
        testJvmHotSwapCapability(tempDir, "17", true);
    }

    /**
     * Tests that newer JVM versions (21+) support hot-swapping capabilities.
     */
    @Test
    public void modern_jvm_supports_hotswap_capabilities(@TempDir Path tempDir) throws IOException, InterruptedException {
        assumeTrue(areNixTestsEnabled(), "Nix tests are disabled by default. Enable with -Dnix.tests.enabled=true");
        assumeTrue(nixAvailable, "Nix is not available - skipping test");
        assumeTrue(!isJava8Profile(), "Skipping Nix test when using Java 8 profile due to dependency compatibility issues");
        
        testJvmHotSwapCapability(tempDir, "21", true);
    }

    /**
     * Tests that Java 8 is supported with the java8 profile.
     * ByteHot supports Java 8 when using the java8 Maven profile.
     */
    @Test
    public void java_8_supported_with_java8_profile(@TempDir Path tempDir) throws IOException, InterruptedException {
        assumeTrue(areNixTestsEnabled(), "Nix tests are disabled by default. Enable with -Dnix.tests.enabled=true");
        assumeTrue(nixAvailable, "Nix is not available - skipping test");
        assumeTrue(!isJava8Profile(), "Skipping Nix test when using Java 8 profile due to dependency compatibility issues");
        
        testJvmHotSwapCapability(tempDir, "8", true);
    }

    /**
     * Tests that Java 11 is not supported due to Maven configuration.
     */
    @Test
    public void java_11_not_supported_due_to_maven_configuration(@TempDir Path tempDir) throws IOException, InterruptedException {
        assumeTrue(areNixTestsEnabled(), "Nix tests are disabled by default. Enable with -Dnix.tests.enabled=true");
        assumeTrue(nixAvailable, "Nix is not available - skipping test");
        assumeTrue(!isJava8Profile(), "Skipping Nix test when using Java 8 profile due to dependency compatibility issues");
        
        testJvmBuildFailure(tempDir, "11", "ByteHot requires Java 17+ per Maven configuration");
    }

    /**
     * Tests HotSwap capabilities with a specific JVM version using Nix.
     */
    private void testJvmHotSwapCapability(@TempDir Path tempDir, String jvmVersion, boolean expectHotSwapSupport) 
            throws IOException, InterruptedException {
        
        // First: Build the agent with the target JVM version to avoid UnsupportedClassVersionError
        ProcessBuilder packageBuilder;
        if (jvmVersion.equals("8")) {
            // Java 8 uses the java8 profile for compatibility
            packageBuilder = new ProcessBuilder(
                "nix", "develop", "./.nix#rydnr-bytehot-" + jvmVersion, "-c",
                "mvn", "clean", "package", "-DskipTests", "-Djava8"
            );
        } else {
            packageBuilder = new ProcessBuilder(
                "nix", "develop", "./.nix#rydnr-bytehot-" + jvmVersion, "-c",
                "mvn", "clean", "package", "-DskipTests", 
                "-Dmaven.compiler.source=" + jvmVersion,
                "-Dmaven.compiler.target=" + jvmVersion,
                "-Djavac.source=" + jvmVersion,
                "-Djavac.target=" + jvmVersion
            );
        }
        packageBuilder.directory(Path.of(".").toFile());
        packageBuilder.redirectErrorStream(true);
        Process packageProcess = packageBuilder.start();
        
        if (!packageProcess.waitFor(120, TimeUnit.SECONDS) || packageProcess.exitValue() != 0) {
            String error = new String(packageProcess.getInputStream().readAllBytes());
            throw new RuntimeException("Failed to build agent with JVM " + jvmVersion + ": " + error);
        }
        
        // Given: A test Java class and valid configuration
        Path testJavaFile = tempDir.resolve("TestApp.java");
        Files.writeString(testJavaFile, 
            "public class TestApp {\n" +
            "    public static void main(String[] args) {\n" +
            "        System.out.println(\"Test application started with JVM" + jvmVersion + "\");\n" +
            "    }\n" +
            "}\n");

        Path watchDir = tempDir.resolve("watch");
        Files.createDirectories(watchDir);
        
        Path configFile = tempDir.resolve("bytehot-config.yml");
        Files.writeString(configFile, 
            "watchPaths:\n" +
            "  - " + watchDir.toAbsolutePath() + "\n\n");

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

    /**
     * Tests that a specific JVM version fails to build ByteHot due to language compatibility issues.
     */
    private void testJvmBuildFailure(@TempDir Path tempDir, String jvmVersion, String expectedReason) 
            throws IOException, InterruptedException {
        
        // Attempt to build with incompatible JVM version without the proper profile - expect failure
        ProcessBuilder packageBuilder = new ProcessBuilder(
            "nix", "develop", "./.nix#rydnr-bytehot-" + jvmVersion, "-c",
            "mvn", "clean", "compile", "-q"
        );
        packageBuilder.directory(Path.of(".").toFile());
        packageBuilder.redirectErrorStream(true);
        Process packageProcess = packageBuilder.start();
        
        String output = new String(packageProcess.getInputStream().readAllBytes());
        boolean buildFailed = !packageProcess.waitFor(60, TimeUnit.SECONDS) || packageProcess.exitValue() != 0;
        
        // Then: Verify that the build failed as expected
        assertTrue(buildFailed, 
            "Build should fail for JVM " + jvmVersion + " due to: " + expectedReason + ". Output: " + output);
        
        // Verify it's failing for the right reason (compilation error, not environment issue)
        assertTrue(output.contains("BUILD FAILURE") || 
                   output.contains("COMPILATION ERROR") ||
                   output.contains("Failed to execute goal") ||
                   output.contains("invalid target release"), 
            "Should fail due to compilation issues. Output: " + output);
    }
}