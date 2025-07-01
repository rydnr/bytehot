package org.acmsl.bytehot.eclipse.commands;

import org.acmsl.bytehot.eclipse.ByteHotPlugin;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Test suite for StartLiveModeHandler.
 */
public class StartLiveModeHandlerTest {
    
    private StartLiveModeHandler handler;
    private Path tempDir;
    private String projectName;
    private TestableByteHotPlugin plugin;
    
    /**
     * Test-specific plugin that can disable agent discovery to test failure scenarios.
     */
    private static class TestableByteHotPlugin extends ByteHotPlugin {
        private boolean disableAgentDiscovery = false;
        
        public void setDisableAgentDiscovery(boolean disable) {
            this.disableAgentDiscovery = disable;
        }
        
        @Override
        public java.util.Optional<String> findAgentJar() {
            if (disableAgentDiscovery) {
                return java.util.Optional.empty();
            }
            return super.findAgentJar();
        }
    }
    
    @Before
    public void setUp() throws IOException {
        // Create and initialize plugin instance
        plugin = new TestableByteHotPlugin();
        plugin.initializePlugin();
        
        handler = new StartLiveModeHandler();
        tempDir = Files.createTempDirectory("handler-test");
        projectName = "test-project";
    }
    
    @Test
    public void testCanExecuteWithValidProject() throws IOException {
        // Given: Valid project structure
        createValidProject();
        createAgentJar();
        
        // When: Check if can execute
        boolean canExecute = handler.canExecute(tempDir.toString(), projectName);
        
        // Then: Should be able to execute
        assertTrue("Should be able to execute with valid project", canExecute);
    }
    
    @Test
    public void testCanExecuteWithoutAgent() throws IOException {
        // Given: Valid project but no agent
        createValidProject();
        plugin.setDisableAgentDiscovery(true);
        
        // When: Check if can execute
        boolean canExecute = handler.canExecute(tempDir.toString(), projectName);
        
        // Then: Should not be able to execute without agent
        assertFalse("Should not be able to execute without agent", canExecute);
    }
    
    @Test
    public void testCanExecuteWithInvalidProject() {
        // Given: Invalid project path
        String invalidPath = "/nonexistent/path";
        
        // When: Check if can execute
        boolean canExecute = handler.canExecute(invalidPath, projectName);
        
        // Then: Should not be able to execute
        assertFalse("Should not be able to execute with invalid project", canExecute);
    }
    
    @Test
    public void testExecuteDryRun() throws IOException {
        // Given: Valid project with agent
        createValidProject();
        createAgentJar();
        
        // When: Execute dry run
        boolean result = handler.executeDryRun(tempDir.toString(), projectName);
        
        // Then: Should succeed
        assertTrue("Dry run should succeed", result);
    }
    
    @Test
    public void testExecuteDryRunWithInvalidProject() {
        // Given: Invalid project
        String invalidPath = "/nonexistent/path";
        
        // When: Execute dry run
        boolean result = handler.executeDryRun(invalidPath, projectName);
        
        // Then: Should fail
        assertFalse("Dry run should fail with invalid project", result);
    }
    
    @Test
    public void testExecuteWithValidProject() throws IOException {
        // Given: Valid project with agent
        createValidProject();
        createAgentJar();
        
        // When: Execute start live mode
        // Note: This test would need to be adapted for actual process launching
        // For now, we test the preparation phase
        boolean canExecute = handler.canExecute(tempDir.toString(), projectName);
        
        // Then: Should be prepared to execute
        assertTrue("Should be prepared to execute", canExecute);
    }
    
    @Test
    public void testExecuteWithInvalidConfiguration() {
        // Given: Project without main class
        // When: Try to execute
        boolean result = handler.execute("/nonexistent", "invalid-project");
        
        // Then: Should fail
        assertFalse("Should fail with invalid configuration", result);
    }
    
    @Test
    public void testHandlerInitialization() {
        // Given: New handler instance
        StartLiveModeHandler newHandler = new StartLiveModeHandler();
        
        // Then: Should be properly initialized
        assertNotNull("Handler should not be null", newHandler);
    }
    
    // Helper methods
    
    private void createValidProject() throws IOException {
        // Create standard Maven project structure
        Files.createDirectories(tempDir.resolve("src/main/java/com/example"));
        Files.createDirectories(tempDir.resolve("target/classes"));
        
        // Create a main class
        String mainClassContent = """
            package com.example;
            
            public class Application {
                public static void main(String[] args) {
                    System.out.println("Hello World");
                }
            }
            """;
        
        Path mainClassFile = tempDir.resolve("src/main/java/com/example/Application.java");
        Files.writeString(mainClassFile, mainClassContent);
    }
    
    private void createAgentJar() throws IOException {
        // Mock agent jar in local repository
        String userHome = tempDir.toString();
        System.setProperty("user.home", userHome);
        
        Path m2Dir = tempDir.resolve(".m2/repository/org/acmsl/bytehot-application/latest-SNAPSHOT");
        Files.createDirectories(m2Dir);
        Files.createFile(m2Dir.resolve("bytehot-application-latest-SNAPSHOT-agent.jar"));
    }
}