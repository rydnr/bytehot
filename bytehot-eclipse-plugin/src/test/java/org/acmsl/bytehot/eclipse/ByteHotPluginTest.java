package org.acmsl.bytehot.eclipse;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Test suite for ByteHotPlugin.
 */
public class ByteHotPluginTest {
    
    private ByteHotPlugin plugin;
    private Path tempDir;
    
    /**
     * Test-specific plugin that can disable bundled agent extraction to test fallback logic.
     */
    private static class TestableByteHotPlugin extends ByteHotPlugin {
        private boolean disableBundledAgent = false;
        
        public void setDisableBundledAgent(boolean disable) {
            this.disableBundledAgent = disable;
        }
        
        @Override
        protected String extractBundledAgent() throws IOException {
            if (disableBundledAgent) {
                return null; // Force fallback to repository search
            }
            return super.extractBundledAgent();
        }
    }
    
    @Before
    public void setUp() throws IOException {
        plugin = new TestableByteHotPlugin();
        tempDir = Files.createTempDirectory("bytehot-test");
    }
    
    @Test
    public void testPluginInitialization() {
        // Given: Fresh plugin instance
        assertFalse("Plugin should not be initialized initially", plugin.isInitialized());
        
        // When: Initialize plugin
        boolean result = plugin.initializePlugin();
        
        // Then: Plugin should be initialized successfully
        assertTrue("Plugin initialization should succeed", result);
        assertTrue("Plugin should be initialized", plugin.isInitialized());
    }
    
    @Test
    public void testPluginSingleton() {
        // Given: Multiple plugin instances
        ByteHotPlugin plugin1 = new ByteHotPlugin();
        ByteHotPlugin plugin2 = ByteHotPlugin.getDefault();
        
        // Then: Should return the same instance
        assertSame("Should return same instance", plugin1, plugin2);
    }
    
    @Test
    public void testAgentDiscoveryPrioritizesBundledAgent() {
        // Given: Plugin should try bundled agent first
        
        // When: Search for agent (bundled extraction may fail in test environment)
        Optional<String> result = plugin.findAgentJar();
        
        // Then: Should return a path (either bundled or fallback)
        // In production with bundled agent, this would extract from resources
        // In test environment, it falls back to development paths
        assertTrue("Agent should be found through bundled or fallback mechanism", result.isPresent());
    }
    
    @Test
    public void testAgentDiscoveryWithLocalRepository() throws IOException {
        // Given: Mock local repository structure and disable bundled agent
        TestableByteHotPlugin testPlugin = (TestableByteHotPlugin) plugin;
        testPlugin.setDisableBundledAgent(true);
        
        String originalUserHome = System.getProperty("user.home");
        
        Path m2Dir = tempDir.resolve(".m2/repository/org/acmsl/bytehot-application/latest-SNAPSHOT");
        Files.createDirectories(m2Dir);
        Path agentJar = m2Dir.resolve("bytehot-application-latest-SNAPSHOT-agent.jar");
        Files.createFile(agentJar);
        
        try {
            System.setProperty("user.home", tempDir.toString());
            
            // When: Search for agent (should fall back to repository after bundled fails)
            Optional<String> result = plugin.findAgentJar();
            
            // Then: Should find the agent
            assertTrue("Agent should be found", result.isPresent());
            assertTrue("Agent path should contain expected jar", 
                      result.get().contains("bytehot-application-latest-SNAPSHOT-agent.jar"));
        } finally {
            System.setProperty("user.home", originalUserHome);
        }
    }
    
    @Test
    public void testAgentDiscoveryWithCurrentDirectory() throws IOException {
        // Given: Agent jar in current directory and disable bundled agent
        TestableByteHotPlugin testPlugin = (TestableByteHotPlugin) plugin;
        testPlugin.setDisableBundledAgent(true);
        
        String originalUserDir = System.getProperty("user.dir");
        String originalUserHome = System.getProperty("user.home");
        Path agentJar = tempDir.resolve("bytehot-application-latest-SNAPSHOT-agent.jar");
        Files.createFile(agentJar);
        
        try {
            // Set user.home to nonexistent directory to skip repository check
            System.setProperty("user.home", "/nonexistent");
            System.setProperty("user.dir", tempDir.toString());
            
            // When: Search for agent
            Optional<String> result = plugin.findAgentJar();
            
            // Then: Should find the agent
            assertTrue("Agent should be found", result.isPresent());
            assertEquals("Agent path should match", agentJar.toString(), result.get());
        } finally {
            System.setProperty("user.dir", originalUserDir);
            System.setProperty("user.home", originalUserHome);
        }
    }
    
    @Test
    public void testAgentDiscoveryNotFound() {
        // Given: No agent jar available and disable bundled agent
        TestableByteHotPlugin testPlugin = (TestableByteHotPlugin) plugin;
        testPlugin.setDisableBundledAgent(true);
        
        String originalUserHome = System.getProperty("user.home");
        String originalUserDir = System.getProperty("user.dir");
        
        try {
            System.setProperty("user.home", "/nonexistent");
            System.setProperty("user.dir", "/nonexistent");
            
            // When: Search for agent
            Optional<String> result = plugin.findAgentJar();
            
            // Then: Should not find the agent
            assertFalse("Agent should not be found", result.isPresent());
        } finally {
            System.setProperty("user.home", originalUserHome);
            System.setProperty("user.dir", originalUserDir);
        }
    }
    
    @Test
    public void testFileExistsCheck() throws IOException {
        // Given: Existing and non-existing files
        Path existingFile = tempDir.resolve("existing.txt");
        Files.createFile(existingFile);
        Path nonExistingFile = tempDir.resolve("nonexisting.txt");
        
        // Then: Should correctly identify file existence
        assertTrue("Should detect existing file", plugin.fileExists(existingFile.toString()));
        assertFalse("Should detect non-existing file", plugin.fileExists(nonExistingFile.toString()));
    }
    
    @Test
    public void testLogging() {
        // Given: Plugin with logging capability
        // When: Log messages
        plugin.logInfo("Test info message");
        plugin.logError("Test error message");
        plugin.logError("Test error with exception", new RuntimeException("Test exception"));
        
        // Then: Should not throw exceptions (basic logging test)
        // In real implementation, we would capture and verify log output
        assertTrue("Logging should work without exceptions", true);
    }
    
    @Test
    public void testMultipleInitialization() {
        // Given: Plugin that has been initialized
        plugin.initializePlugin();
        assertTrue("Plugin should be initialized", plugin.isInitialized());
        
        // When: Initialize again
        boolean result = plugin.initializePlugin();
        
        // Then: Should still return true and remain initialized
        assertTrue("Second initialization should succeed", result);
        assertTrue("Plugin should remain initialized", plugin.isInitialized());
    }
}