package org.acmsl.bytehot.eclipse.launch;

import org.acmsl.bytehot.eclipse.analysis.ProjectConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Eclipse Process Launcher for ByteHot.
 * 
 * Handles launching Java applications with ByteHot agent from Eclipse workspace,
 * providing proper process management and environment setup.
 * 
 * Features:
 * - Process launching with ByteHot agent attachment
 * - Environment setup and working directory management
 * - Command line building and validation
 * - Process lifecycle management
 */
public class EclipseProcessLauncher {
    
    /**
     * Launches the application with ByteHot agent based on the provided configuration.
     */
    public Process launchWithAgent(ProjectConfiguration config) throws IOException {
        if (!config.isValid()) {
            throw new IllegalArgumentException("Invalid project configuration");
        }
        
        // Build launch command
        String[] command = buildLaunchCommand(config);
        
        // Create process builder
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        
        // Set working directory to project root if possible
        setWorkingDirectory(processBuilder, config);
        
        // Inherit IO so output appears in Eclipse console
        processBuilder.inheritIO();
        
        // Start the process
        return processBuilder.start();
    }
    
    /**
     * Builds the command line arguments for launching the application.
     */
    public String[] buildLaunchCommand(ProjectConfiguration config) {
        List<String> command = new ArrayList<>();
        
        // Java executable
        command.add(getJavaExecutable());
        
        // JVM arguments
        command.addAll(config.getJvmArgs());
        
        // Classpath
        if (config.getClasspath() != null && !config.getClasspath().trim().isEmpty()) {
            command.add("-cp");
            command.add(config.getClasspath());
        }
        
        // Main class
        command.add(config.getMainClass());
        
        // Program arguments
        command.addAll(config.getProgramArgs());
        
        return command.toArray(new String[0]);
    }
    
    /**
     * Gets the Java executable path.
     */
    protected String getJavaExecutable() {
        String javaHome = System.getProperty("java.home");
        if (javaHome != null) {
            String javaExe = javaHome + File.separator + "bin" + File.separator + "java";
            if (isWindows()) {
                javaExe += ".exe";
            }
            if (new File(javaExe).exists()) {
                return javaExe;
            }
        }
        
        // Fallback to java on PATH
        return "java";
    }
    
    /**
     * Sets the working directory for the process.
     */
    protected void setWorkingDirectory(ProcessBuilder processBuilder, ProjectConfiguration config) {
        // Try to infer project root from source paths
        if (!config.getSourcePaths().isEmpty()) {
            String sourcePath = config.getSourcePaths().get(0);
            
            // If it's src/main/java, go up to project root
            if (sourcePath.endsWith("src/main/java")) {
                String projectRoot = sourcePath.replace("/src/main/java", "");
                File projectDir = new File(projectRoot);
                if (projectDir.exists() && projectDir.isDirectory()) {
                    processBuilder.directory(projectDir);
                    return;
                }
            }
            
            // If it's src, go up one level
            if (sourcePath.endsWith("src")) {
                String projectRoot = sourcePath.replace("/src", "");
                File projectDir = new File(projectRoot);
                if (projectDir.exists() && projectDir.isDirectory()) {
                    processBuilder.directory(projectDir);
                    return;
                }
            }
            
            // Use the source path directory itself
            File sourceDir = new File(sourcePath);
            if (sourceDir.exists() && sourceDir.isDirectory()) {
                processBuilder.directory(sourceDir.getParentFile());
            }
        }
    }
    
    /**
     * Checks if running on Windows.
     */
    protected boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }
    
    /**
     * Launches the application with a dry run (returns command without execution).
     */
    public String[] getDryRunCommand(ProjectConfiguration config) {
        return buildLaunchCommand(config);
    }
}