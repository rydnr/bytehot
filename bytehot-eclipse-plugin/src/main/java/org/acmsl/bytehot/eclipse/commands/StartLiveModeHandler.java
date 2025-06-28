package org.acmsl.bytehot.eclipse.commands;

import org.acmsl.bytehot.eclipse.ByteHotPlugin;
import org.acmsl.bytehot.eclipse.analysis.EclipseProjectAnalyzer;
import org.acmsl.bytehot.eclipse.analysis.ProjectConfiguration;
import org.acmsl.bytehot.eclipse.launch.EclipseProcessLauncher;

/**
 * Start Live Mode Command Handler for Eclipse.
 * 
 * Handles the "Start Live Mode" command from Eclipse UI, providing zero-configuration
 * live mode activation for Eclipse projects with comprehensive error handling.
 * 
 * Features:
 * - Zero-configuration project analysis
 * - Automatic main class detection
 * - Process launching with ByteHot agent
 * - Eclipse-native error handling and user feedback
 */
public class StartLiveModeHandler {
    
    private final ByteHotPlugin plugin;
    
    public StartLiveModeHandler() {
        this.plugin = ByteHotPlugin.getDefault();
    }
    
    /**
     * Executes the start live mode command for the given project.
     */
    public boolean execute(String projectPath, String projectName) {
        try {
            plugin.logInfo("Starting live mode for project: " + projectName);
            
            // Initialize plugin if needed
            if (!plugin.initializePlugin()) {
                showError("Failed to initialize ByteHot plugin");
                return false;
            }
            
            // Analyze project
            EclipseProjectAnalyzer analyzer = new EclipseProjectAnalyzer(projectPath, projectName);
            ProjectConfiguration config = analyzer.analyzeProject();
            
            plugin.logInfo("Project analysis completed:");
            plugin.logInfo("  Main class: " + config.getMainClass());
            plugin.logInfo("  Source paths: " + config.getSourcePaths());
            plugin.logInfo("  Classpath: " + config.getClasspath());
            
            // Validate configuration
            if (!config.isValid()) {
                showError("Invalid project configuration. Please ensure the project has a main class and valid classpath.");
                return false;
            }
            
            // Launch application with ByteHot agent
            EclipseProcessLauncher launcher = new EclipseProcessLauncher();
            Process process = launcher.launchWithAgent(config);
            
            if (process != null && process.isAlive()) {
                plugin.logInfo("Live mode started successfully for " + projectName);
                showInfo("Live mode started successfully for project: " + projectName);
                return true;
            } else {
                showError("Failed to start application process");
                return false;
            }
            
        } catch (Exception e) {
            plugin.logError("Failed to start live mode", e);
            showError("Failed to start live mode: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Executes the start live mode command with dry run (shows configuration without execution).
     */
    public boolean executeDryRun(String projectPath, String projectName) {
        try {
            plugin.logInfo("Performing dry run for project: " + projectName);
            
            // Initialize plugin if needed
            if (!plugin.initializePlugin()) {
                showError("Failed to initialize ByteHot plugin");
                return false;
            }
            
            // Analyze project
            EclipseProjectAnalyzer analyzer = new EclipseProjectAnalyzer(projectPath, projectName);
            ProjectConfiguration config = analyzer.analyzeProject();
            
            // Build launch command
            EclipseProcessLauncher launcher = new EclipseProcessLauncher();
            String[] command = launcher.buildLaunchCommand(config);
            
            // Show configuration details
            StringBuilder info = new StringBuilder();
            info.append("ByteHot Live Mode Configuration:\n\n");
            info.append("Project: ").append(projectName).append("\n");
            info.append("Main Class: ").append(config.getMainClass()).append("\n");
            info.append("Source Paths: ").append(String.join(", ", config.getSourcePaths())).append("\n");
            info.append("Classpath: ").append(config.getClasspath()).append("\n");
            info.append("JVM Args: ").append(String.join(" ", config.getJvmArgs())).append("\n\n");
            info.append("Launch Command:\n");
            info.append(String.join(" ", command));
            
            showInfo(info.toString());
            return true;
            
        } catch (Exception e) {
            plugin.logError("Dry run failed", e);
            showError("Dry run failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Checks if live mode can be started for the given project.
     */
    public boolean canExecute(String projectPath, String projectName) {
        try {
            // Check if plugin can be initialized
            if (!plugin.initializePlugin()) {
                return false;
            }
            
            // Check if agent is available
            if (!plugin.findAgentJar().isPresent()) {
                return false;
            }
            
            // Try to analyze project
            EclipseProjectAnalyzer analyzer = new EclipseProjectAnalyzer(projectPath, projectName);
            ProjectConfiguration config = analyzer.analyzeProject();
            
            return config.isValid();
            
        } catch (Exception e) {
            plugin.logError("Cannot execute live mode", e);
            return false;
        }
    }
    
    /**
     * Shows an informational message to the user.
     * In a real Eclipse plugin, this would use Eclipse's message dialogs.
     */
    protected void showInfo(String message) {
        plugin.logInfo("INFO: " + message);
        // In real implementation: MessageDialog.openInformation(shell, "ByteHot", message)
    }
    
    /**
     * Shows an error message to the user.
     * In a real Eclipse plugin, this would use Eclipse's error dialogs.
     */
    protected void showError(String message) {
        plugin.logError("ERROR: " + message);
        // In real implementation: MessageDialog.openError(shell, "ByteHot Error", message)
    }
}