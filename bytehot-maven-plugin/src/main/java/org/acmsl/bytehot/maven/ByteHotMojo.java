package org.acmsl.bytehot.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.execution.MavenSession;

import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;

/**
 * LiveMojo for ByteHot agent integration providing seamless live mode activation.
 * This goal starts any Java application with ByteHot agent attached using automatic 
 * main class detection and zero configuration.
 */
@Mojo(name = "live", 
      requiresProject = true,
      requiresDirectInvocation = true,
      requiresDependencyResolution = ResolutionScope.RUNTIME)
public class ByteHotMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    protected MavenProject project;

    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    protected MavenSession session;

    @Parameter(property = "bytehot.enabled", defaultValue = "true")
    protected boolean enabled;

    @Parameter(property = "bytehot.mainClass")
    protected String mainClass;

    @Parameter(property = "bytehot.watchPaths")
    protected List<String> watchPaths;

    @Parameter(property = "bytehot.jvmArgs")
    protected List<String> jvmArgs;

    @Parameter(property = "bytehot.agentPath")
    protected String agentPath;

    @Parameter(property = "bytehot.verbose", defaultValue = "false")
    protected boolean verbose;

    @Parameter(property = "bytehot.dryRun", defaultValue = "false")
    protected boolean dryRun;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (!enabled) {
            getLog().info("ByteHot agent activation is disabled");
            return;
        }

        try {
            getLog().info("Starting application in ByteHot live mode...");
            
            // Initialize and validate configuration
            validateConfiguration();
            
            // Analyze project to detect configuration
            ProjectConfiguration config = analyzeProject();
            
            if (verbose) {
                logConfiguration(config);
            }
            
            // Discover and configure agent
            configureAgent(config);
            
            // Start application with agent or dry run
            if (dryRun) {
                getLog().info("DRY RUN: Would start application: " + config.getMainClass());
                getLog().info("DRY RUN: JVM arguments: " + String.join(" ", config.getJvmArgs()));
            } else {
                startApplicationWithAgent(config);
            }
            
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to start application in live mode", e);
        }
    }

    protected void validateConfiguration() throws MojoFailureException {
        if (agentPath != null && agentPath.trim().isEmpty()) {
            throw new MojoFailureException("ByteHot agent path cannot be empty when specified");
        }
        
        if (project == null) {
            throw new MojoFailureException("Maven project is required for ByteHot plugin");
        }
    }

    protected ProjectConfiguration analyzeProject() throws MojoExecutionException {
        MavenProjectAnalyzer analyzer = new MavenProjectAnalyzer(project, session);
        
        ProjectConfiguration config = new ProjectConfiguration();
        
        // Auto-detect main class if not specified
        if (mainClass == null) {
            try {
                config.setMainClass(analyzer.detectMainClass());
                getLog().info("Auto-detected main class: " + config.getMainClass());
            } catch (Exception e) {
                throw new MojoExecutionException("Could not detect main class. Please specify using -Dbytehot.mainClass=...", e);
            }
        } else {
            config.setMainClass(mainClass);
        }
        
        // Auto-detect watch paths if not specified  
        if (watchPaths == null || watchPaths.isEmpty()) {
            config.setWatchPaths(analyzer.detectSourceDirectories());
            if (verbose) {
                getLog().info("Auto-detected watch paths: " + config.getWatchPaths());
            }
        } else {
            config.setWatchPaths(watchPaths);
        }
        
        // Build classpath from Maven dependencies
        try {
            config.setClasspath(analyzer.buildClasspath());
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to build classpath", e);
        }
        
        return config;
    }

    /**
     * Logs the current project configuration for debugging purposes.
     * @param config the project configuration to log
     */
    protected void logConfiguration(ProjectConfiguration config) {
        getLog().info("ByteHot Configuration:");
        getLog().info("  - Main Class: " + config.getMainClass());
        getLog().info("  - Watch Paths: " + config.getWatchPaths());
        getLog().info("  - Classpath Elements: " + config.getClasspath().size());
        getLog().info("  - Agent Path: " + (agentPath != null ? agentPath : "auto-discovery"));
        getLog().info("  - JVM Args: " + (jvmArgs != null ? jvmArgs : "none"));
        getLog().info("  - Dry Run: " + dryRun);
    }

    /**
     * Configures the ByteHot agent for the project.
     * @param config the project configuration to use for agent setup
     * @throws MojoExecutionException if agent configuration fails
     */
    protected void configureAgent(ProjectConfiguration config) throws MojoExecutionException {
        // Resolve agent path
        String resolvedAgentPath;
        if (agentPath != null && !agentPath.trim().isEmpty()) {
            resolvedAgentPath = agentPath;
        } else {
            AgentDiscovery discovery = new AgentDiscovery();
            resolvedAgentPath = discovery.getAgentPath()
                .orElseThrow(() -> new MojoExecutionException(
                    "Could not find ByteHot agent JAR. Please specify using -Dbytehot.agentPath=..."));
        }

        // Build JVM arguments including agent
        List<String> allJvmArgs = new ArrayList<>();
        if (jvmArgs != null) {
            allJvmArgs.addAll(jvmArgs);
        }
        
        // Add the agent argument
        allJvmArgs.add("-javaagent:" + resolvedAgentPath);
        
        config.setJvmArgs(allJvmArgs);
        
        if (verbose) {
            getLog().info("Resolved ByteHot agent: " + resolvedAgentPath);
        }
    }

    /**
     * Starts the application with the ByteHot agent attached.
     * @param config the project configuration containing main class and JVM settings
     * @throws MojoExecutionException if the application fails to start
     */
    protected void startApplicationWithAgent(ProjectConfiguration config) throws MojoExecutionException {
        try {
            getLog().info("Starting " + config.getMainClass() + " with ByteHot agent...");
            
            ProcessBuilder processBuilder = new ProcessBuilder();
            List<String> command = new ArrayList<>();
            
            // Java executable
            String javaHome = System.getProperty("java.home");
            command.add(javaHome + File.separator + "bin" + File.separator + "java");
            
            // JVM arguments (including agent)
            command.addAll(config.getJvmArgs());
            
            // Classpath
            command.add("-cp");
            command.add(String.join(File.pathSeparator, config.getClasspath()));
            
            // Main class
            command.add(config.getMainClass());
            
            processBuilder.command(command);
            processBuilder.inheritIO();
            
            if (verbose) {
                getLog().info("Executing: " + String.join(" ", command));
            }
            
            Process process = processBuilder.start();
            
            // Register shutdown hook to cleanup process
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (process.isAlive()) {
                    getLog().info("Stopping ByteHot live mode...");
                    process.destroyForcibly();
                }
            }));
            
            // Wait for process to complete
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new MojoExecutionException("Application exited with code: " + exitCode);
            }
            
        } catch (IOException | InterruptedException e) {
            throw new MojoExecutionException("Failed to start application process", e);
        }
    }
}