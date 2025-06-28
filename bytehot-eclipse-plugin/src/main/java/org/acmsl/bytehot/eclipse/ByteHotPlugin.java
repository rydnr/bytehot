package org.acmsl.bytehot.eclipse;

import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;

/**
 * ByteHot Eclipse Plugin Main Class.
 * 
 * Provides the foundation for Eclipse IDE integration with ByteHot live coding capabilities.
 * This plugin enables zero-configuration live mode activation directly from Eclipse workspace.
 * 
 * Features:
 * - Zero-configuration Eclipse integration
 * - Automatic project analysis and main class detection
 * - Native workspace integration
 * - Eclipse-native UI components and visual feedback
 */
public class ByteHotPlugin {
    
    public static final String PLUGIN_ID = "org.acmsl.bytehot.eclipse";
    public static final String PLUGIN_NAME = "ByteHot Eclipse Plugin";
    
    private static ByteHotPlugin instance;
    
    protected boolean initialized = false;
    
    public ByteHotPlugin() {
        instance = this;
    }
    
    /**
     * Returns the shared instance of the plugin.
     */
    public static ByteHotPlugin getDefault() {
        return instance;
    }
    
    /**
     * Initializes the plugin foundation.
     * Sets up the core components needed for Eclipse integration.
     */
    public boolean initializePlugin() {
        if (initialized) {
            return true;
        }
        
        try {
            // Initialize core plugin components
            setupAgentDiscovery();
            setupProjectAnalysis();
            
            initialized = true;
            return true;
        } catch (Exception e) {
            logError("Failed to initialize ByteHot plugin", e);
            return false;
        }
    }
    
    /**
     * Sets up agent discovery system.
     */
    protected void setupAgentDiscovery() {
        // Agent discovery will be implemented via EclipseAgentDiscovery
    }
    
    /**
     * Sets up project analysis capabilities.
     */
    protected void setupProjectAnalysis() {
        // Project analysis will be implemented via EclipseProjectAnalyzer
    }
    
    /**
     * Discovers the ByteHot agent JAR file.
     * First tries to extract bundled agent from plugin resources,
     * then falls back to common locations for development.
     */
    public Optional<String> findAgentJar() {
        // First try to extract bundled agent from plugin resources
        try {
            String bundledAgentPath = extractBundledAgent();
            if (bundledAgentPath != null) {
                return Optional.of(bundledAgentPath);
            }
        } catch (Exception e) {
            // Log error but continue to fallback search
            logError("Failed to extract bundled agent: " + e.getMessage());
        }
        
        // Fallback 1: Local Maven repository (for development)
        String userHome = System.getProperty("user.home");
        if (userHome != null) {
            String localRepoPath = userHome + "/.m2/repository/org/acmsl/bytehot-application/latest-SNAPSHOT/bytehot-application-latest-SNAPSHOT-agent.jar";
            if (fileExists(localRepoPath)) {
                return Optional.of(localRepoPath);
            }
        }
        
        // Fallback 2: Current workspace relative path (for development)
        String workspacePath = System.getProperty("user.dir") + "/bytehot-application/target/bytehot-application-latest-SNAPSHOT-agent.jar";
        if (fileExists(workspacePath)) {
            return Optional.of(workspacePath);
        }
        
        // Fallback 3: Current directory (for development)
        String currentDirPath = System.getProperty("user.dir") + "/bytehot-application-latest-SNAPSHOT-agent.jar";
        if (fileExists(currentDirPath)) {
            return Optional.of(currentDirPath);
        }
        
        return Optional.empty();
    }
    
    /**
     * Extracts the bundled ByteHot agent JAR from plugin resources to a temporary file.
     * @return path to extracted agent JAR or null if not found
     */
    protected String extractBundledAgent() throws IOException {
        String agentResourcePath = "/agents/bytehot-application-agent.jar";
        
        // Try to load the bundled agent from plugin resources
        InputStream agentStream = this.getClass().getResourceAsStream(agentResourcePath);
        if (agentStream == null) {
            return null;
        }
        
        try {
            // Create temporary file
            String tempDir = System.getProperty("java.io.tmpdir");
            File tempFile = new File(tempDir, "bytehot-agent-" + System.currentTimeMillis() + ".jar");
            
            // Copy resource to temporary file
            try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = agentStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            
            // Mark for deletion on exit
            tempFile.deleteOnExit();
            
            return tempFile.getAbsolutePath();
        } finally {
            agentStream.close();
        }
    }
    
    /**
     * Checks if a file exists at the given path.
     */
    protected boolean fileExists(String path) {
        try {
            return new java.io.File(path).exists();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Logs an informational message.
     */
    public void logInfo(String message) {
        System.out.println("[ByteHot INFO] " + message);
    }
    
    /**
     * Logs an error message with optional throwable.
     */
    public void logError(String message, Throwable throwable) {
        System.err.println("[ByteHot ERROR] " + message);
        if (throwable != null) {
            throwable.printStackTrace();
        }
    }
    
    /**
     * Logs an error message.
     */
    public void logError(String message) {
        logError(message, null);
    }
    
    /**
     * Returns whether the plugin is initialized.
     */
    public boolean isInitialized() {
        return initialized;
    }
}