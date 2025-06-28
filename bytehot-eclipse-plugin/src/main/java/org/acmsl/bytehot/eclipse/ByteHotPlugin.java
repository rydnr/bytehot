package org.acmsl.bytehot.eclipse;

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
     * Uses multiple search strategies similar to other plugins.
     */
    public Optional<String> findAgentJar() {
        String userHome = System.getProperty("user.home");
        
        // Strategy 1: Local Maven repository
        if (userHome != null) {
            String localRepoPath = userHome + "/.m2/repository/org/acmsl/bytehot-application/latest-SNAPSHOT/bytehot-application-latest-SNAPSHOT-agent.jar";
            if (fileExists(localRepoPath)) {
                return Optional.of(localRepoPath);
            }
        }
        
        // Strategy 2: Current workspace relative path
        String workspacePath = System.getProperty("user.dir") + "/bytehot-application/target/bytehot-application-latest-SNAPSHOT-agent.jar";
        if (fileExists(workspacePath)) {
            return Optional.of(workspacePath);
        }
        
        // Strategy 3: Current directory
        String currentDirPath = System.getProperty("user.dir") + "/bytehot-application-latest-SNAPSHOT-agent.jar";
        if (fileExists(currentDirPath)) {
            return Optional.of(currentDirPath);
        }
        
        return Optional.empty();
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