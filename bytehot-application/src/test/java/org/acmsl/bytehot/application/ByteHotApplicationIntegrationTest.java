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
 * Filename: ByteHotApplicationIntegrationTest.java
 *
 * Author: Claude Code
 *
 * Class name: ByteHotApplicationIntegrationTest
 *
 * Responsibilities:
 *   - Test complete startup flow: agent attach → config load → capability check
 *
 * Collaborators:
 *   - ByteHotApplication: Main application layer orchestrator
 *   - WatchConfiguration: Configuration domain object
 *   - ByteHotAttachRequested: Incoming domain event
 *   - ByteHotAgentAttached: Response domain event
 */
package org.acmsl.bytehot.application;

import org.acmsl.bytehot.domain.WatchConfiguration;
import org.acmsl.bytehot.domain.events.ByteHotAgentAttached;
import org.acmsl.bytehot.domain.events.ByteHotAttachRequested;

import org.acmsl.commons.patterns.DomainResponseEvent;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration test for complete ByteHot startup flow
 * @author Claude Code
 * @since 2025-06-16
 */
public class ByteHotApplicationIntegrationTest {

    /**
     * Test implementation of Instrumentation interface for testing purposes
     */
    private static class TestInstrumentation implements Instrumentation {
        private final boolean canRedefineClasses;
        private final boolean canRetransformClasses;

        public TestInstrumentation(boolean canRedefineClasses, boolean canRetransformClasses) {
            this.canRedefineClasses = canRedefineClasses;
            this.canRetransformClasses = canRetransformClasses;
        }

        @Override
        public boolean isRedefineClassesSupported() {
            return canRedefineClasses;
        }

        @Override
        public boolean isRetransformClassesSupported() {
            return canRetransformClasses;
        }

        // Minimal implementation of other required methods
        @Override
        public void addTransformer(java.lang.instrument.ClassFileTransformer transformer, boolean canRetransform) {}

        @Override
        public void addTransformer(java.lang.instrument.ClassFileTransformer transformer) {}

        @Override
        public boolean removeTransformer(java.lang.instrument.ClassFileTransformer transformer) { return false; }

        @Override
        public void retransformClasses(Class<?>... classes) {}

        @Override
        public void redefineClasses(java.lang.instrument.ClassDefinition... definitions) {}

        @Override
        public Class<?>[] getAllLoadedClasses() { return new Class<?>[0]; }

        @Override
        public Class<?>[] getInitiatedClasses(ClassLoader loader) { return new Class<?>[0]; }

        @Override
        public long getObjectSize(Object objectToSize) { return 0L; }

        @Override
        public void appendToBootstrapClassLoaderSearch(java.util.jar.JarFile jarfile) {}

        @Override
        public void appendToSystemClassLoaderSearch(java.util.jar.JarFile jarfile) {}

        @Override
        public boolean isModifiableClass(Class<?> theClass) { return false; }

        @Override
        public boolean isNativeMethodPrefixSupported() { return false; }

        @Override
        public void setNativeMethodPrefix(java.lang.instrument.ClassFileTransformer transformer, String prefix) {}

        @Override
        public boolean isModifiableModule(Module module) { return false; }

        @Override
        public void redefineModule(Module module, java.util.Set<Module> extraReads, 
                                 java.util.Map<String, java.util.Set<Module>> extraExports,
                                 java.util.Map<String, java.util.Set<Module>> extraOpens,
                                 java.util.Set<Class<?>> extraUses,
                                 java.util.Map<Class<?>, java.util.List<Class<?>>> extraProvides) {}
    }

    /**
     * Set up clean state before each test
     */
    @BeforeEach
    public void setUp() {
        // Reset the ByteHotApplication state by clearing adapters
        resetByteHotApplicationState();
        
        // Reset the Ports singleton instance
        resetPortsInstance();
    }

    /**
     * Clean up after each test to ensure isolation
     */
    @AfterEach
    public void tearDown() {
        // Reset the ByteHotApplication state
        resetByteHotApplicationState();
        
        // Reset the Ports singleton instance
        resetPortsInstance();
        
        // Clear system properties that might affect tests
        System.clearProperty("bytehot.watch.paths");
    }

    /**
     * Resets ByteHotApplication state using reflection to access private fields
     */
    private void resetByteHotApplicationState() {
        try {
            // Reset the adaptersInitialized flag
            java.lang.reflect.Field adaptersInitializedField = 
                ByteHotApplication.class.getDeclaredField("adaptersInitialized");
            adaptersInitializedField.setAccessible(true);
            adaptersInitializedField.setBoolean(null, false);
        } catch (Exception e) {
            System.err.println("Warning: Could not reset ByteHotApplication state: " + e.getMessage());
        }
    }

    /**
     * Resets the Ports singleton instance using reflection
     */
    private void resetPortsInstance() {
        try {
            // Reset the Ports singleton instance
            java.lang.reflect.Field instanceField = 
                org.acmsl.bytehot.domain.Ports.class.getDeclaredField("instance");
            instanceField.setAccessible(true);
            instanceField.set(null, null);
        } catch (Exception e) {
            System.err.println("Warning: Could not reset Ports instance: " + e.getMessage());
        }
    }

    /**
     * Tests the complete startup flow: agent attach → config load → capability check
     * @param tempDir the temporary folder
     * @throws IOException if files cannot be read or written
     * @throws Exception if the test fails for other reasons
     */
    @Test
    public void complete_startup_flow_produces_expected_events_and_output(@TempDir Path tempDir) throws IOException, Exception {
        // Given: A configuration with watch paths and instrumentation that supports hot-swap
        Path watchDir = tempDir.resolve("watch");
        Files.createDirectories(watchDir);
        
        Path configFile = tempDir.resolve("bytehot-config.yml");
        Files.writeString(configFile, 
            "watchPaths:\n" +
            "  - " + watchDir.toAbsolutePath() + "\n\n");

        // Register ConfigurationAdapter and load configuration via the port
        org.acmsl.bytehot.infrastructure.config.ConfigurationAdapter configAdapter = 
            new org.acmsl.bytehot.infrastructure.config.ConfigurationAdapter();
        org.acmsl.bytehot.domain.Ports.getInstance().inject(org.acmsl.bytehot.domain.ConfigurationPort.class, configAdapter);
        
        // Use system properties to configure the adapter, then load via ConfigurationPort
        System.setProperty("bytehot.watch.paths", watchDir.toAbsolutePath().toString());
        WatchConfiguration config = WatchConfiguration.load();
        
        // Test instrumentation that supports hot-swap capabilities
        Instrumentation instrumentation = new TestInstrumentation(true, true);
        
        ByteHotAttachRequested attachRequest = ByteHotAttachRequested.withUserContext(config, instrumentation);

        // Capture system output to verify all expected messages are printed
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            // When: Processing the attach request through the application layer
            ByteHotApplication.initialize(instrumentation);
            
            // FileWatcherAdapter discovery will be attempted by ByteHotApplication.initialize()
            
            ByteHotApplication application = ByteHotApplication.getInstance();
            List<? extends DomainResponseEvent<?>> responses = application.accept(attachRequest);

            // Then: Exactly one response event is produced
            assertEquals(1, responses.size(), "Should produce exactly one response event");
            
            // And: The response is a ByteHotAgentAttached event
            DomainResponseEvent<?> response = responses.get(0);
            assertInstanceOf(ByteHotAgentAttached.class, response, "Response should be ByteHotAgentAttached");
            
            ByteHotAgentAttached agentAttached = (ByteHotAgentAttached) response;
            assertEquals(attachRequest, agentAttached.getPreceding(), "Should reference the original request");
            assertEquals(config, agentAttached.getConfiguration(), "Should contain the configuration");

            // And: All expected startup messages are printed to system output
            String output = outputStream.toString();
            assertTrue(output.contains("ByteHotAgentAttached"), 
                "Should print ByteHotAgentAttached message");
            assertTrue(output.contains("WatchPathConfigured"), 
                "Should print WatchPathConfigured message");
            assertTrue(output.contains("HotSwapCapabilityEnabled"), 
                "Should print HotSwapCapabilityEnabled message when instrumentation supports it");

        } finally {
            System.setOut(originalOut);
        }
    }

    /**
     * Tests startup flow when JVM doesn't support hot-swap capabilities
     * @param tempDir a temporary folder
     * @throws IOException if files cannot be read or written
     * @throws Exception if the test fails for other reasons
     */
    @Test
    public void startup_flow_without_hotswap_support_skips_capability_message(@TempDir Path tempDir) throws IOException, Exception {
        // Given: A configuration with watch paths but instrumentation that doesn't support hot-swap
        Path watchDir = tempDir.resolve("watch");
        Files.createDirectories(watchDir);
        
        Path configFile = tempDir.resolve("bytehot-config.yml");
        Files.writeString(configFile, 
            "watchPaths:\n" +
            "  - " + watchDir.toAbsolutePath() + "\n\n");

        // Register ConfigurationAdapter and load configuration via the port
        org.acmsl.bytehot.infrastructure.config.ConfigurationAdapter configAdapter = 
            new org.acmsl.bytehot.infrastructure.config.ConfigurationAdapter();
        org.acmsl.bytehot.domain.Ports.getInstance().inject(org.acmsl.bytehot.domain.ConfigurationPort.class, configAdapter);
        
        // Use system properties to configure the adapter, then load via ConfigurationPort
        System.setProperty("bytehot.watch.paths", watchDir.toAbsolutePath().toString());
        WatchConfiguration config = WatchConfiguration.load();
        
        // Test instrumentation that doesn't support hot-swap capabilities
        Instrumentation instrumentation = new TestInstrumentation(false, false);
        
        ByteHotAttachRequested attachRequest = ByteHotAttachRequested.withUserContext(config, instrumentation);

        // Capture system output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            // When: Processing the attach request
            ByteHotApplication.initialize(instrumentation);
            
            // FileWatcherAdapter discovery will be attempted by ByteHotApplication.initialize()
            
            ByteHotApplication application = ByteHotApplication.getInstance();
            List<? extends DomainResponseEvent<?>> responses = application.accept(attachRequest);

            // Then: Agent still attaches successfully
            assertEquals(1, responses.size(), "Should produce exactly one response event");
            assertInstanceOf(ByteHotAgentAttached.class, responses.get(0), "Should still attach successfully");

            // And: Basic startup messages are printed but not hot-swap capability
            String output = outputStream.toString();
            assertTrue(output.contains("ByteHotAgentAttached"), 
                "Should print ByteHotAgentAttached message");
            assertTrue(output.contains("WatchPathConfigured"), 
                "Should print WatchPathConfigured message");
            assertTrue(!output.contains("HotSwapCapabilityEnabled"), 
                "Should NOT print HotSwapCapabilityEnabled when instrumentation doesn't support it");

        } finally {
            System.setOut(originalOut);
        }
    }
}
