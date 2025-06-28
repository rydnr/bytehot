package org.acmsl.bytehot.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import java.io.File

/**
 * ByteHot Live Mode Task.
 * 
 * Provides zero-configuration live mode activation for Gradle projects.
 * Automatically detects main class, builds classpath, and discovers ByteHot agent.
 * 
 * Features:
 * - Zero-configuration activation
 * - Automatic main class detection
 * - Classpath building from project dependencies
 * - Agent auto-discovery
 * - Process launching and lifecycle management
 */
open class ByteHotTask : DefaultTask() {
    
    /**
     * Reference to the ByteHot extension for configuration.
     */
    @Input
    @Optional
    var extension: ByteHotExtension? = null
    
    init {
        group = "bytehot"
        description = "Starts ByteHot live mode for immediate code reflection"
    }
    
    @TaskAction
    fun executeTask() {
        val ext = extension ?: project.extensions.getByType(ByteHotExtension::class.java)
        
        // Check system properties for overrides
        val enabled = project.findProperty("bytehot.enabled")?.toString()?.toBoolean() ?: ext.enabled
        val dryRun = project.findProperty("bytehot.dryRun")?.toString()?.toBoolean() ?: ext.dryRun
        
        if (!enabled) {
            logger.info("ByteHot plugin is disabled")
            return
        }
        
        if (dryRun) {
            logger.lifecycle("=== ByteHot Dry Run Mode ===")
            performDryRun(ext)
            return
        }
        
        logger.lifecycle("=== ByteHot Live Mode ===")
        performLiveMode(ext)
    }
    
    /**
     * Performs dry run - shows configuration without execution.
     */
    protected fun performDryRun(extension: ByteHotExtension) {
        try {
            val analyzer = GradleProjectAnalyzer(project)
            val config = analyzer.analyzeProject()
            val agentPath = findAgentJar()
            
            logger.lifecycle("Configuration Analysis:")
            logger.lifecycle("  Main Class: ${config.mainClass ?: "Not detected"}")
            logger.lifecycle("  Classpath: ${config.classpath ?: "Not built"}")
            logger.lifecycle("  Source Paths: ${config.sourcePaths}")
            logger.lifecycle("  Agent Path: ${agentPath ?: "Not found"}")
            logger.lifecycle("  JVM Args: ${extension.jvmArgs}")
            logger.lifecycle("  Program Args: ${extension.programArgs}")
            
            if (agentPath != null && config.mainClass != null) {
                val command = buildLaunchCommand(config, agentPath)
                logger.lifecycle("Launch Command:")
                logger.lifecycle("  ${command.joinToString(" ")}")
            } else {
                if (agentPath == null) {
                    logger.warn("ByteHot agent not found. Please ensure bytehot-application-*-agent.jar is available.")
                }
                if (config.mainClass == null) {
                    logger.warn("No main class detected. Please specify mainClass in bytehot configuration.")
                }
            }
        } catch (e: Exception) {
            logger.error("Dry run failed: ${e.message}")
            throw e
        }
    }
    
    /**
     * Performs live mode execution.
     */
    protected fun performLiveMode(extension: ByteHotExtension) {
        try {
            // Check if agent is available
            val agentPath = findAgentJar()
            if (agentPath == null) {
                throw RuntimeException("ByteHot agent not found. Please ensure bytehot-application-*-agent.jar is available.")
            }
            
            // Analyze project
            val analyzer = GradleProjectAnalyzer(project)
            val config = analyzer.analyzeProject()
            
            if (config.mainClass == null) {
                throw RuntimeException("No main class found. Please specify mainClass in bytehot configuration.")
            }
            
            // Build and execute launch command
            val command = buildLaunchCommand(config, agentPath)
            
            logger.lifecycle("Starting live mode for ${config.mainClass}")
            if (extension.verbose) {
                logger.lifecycle("Command: ${command.joinToString(" ")}")
            }
            
            val processBuilder = ProcessBuilder(command)
                .inheritIO()
                .directory(project.projectDir)
            
            val process = processBuilder.start()
            
            // Wait for process to complete
            val exitCode = process.waitFor()
            logger.lifecycle("Live mode stopped (exit code: $exitCode)")
            
        } catch (e: Exception) {
            logger.error("Live mode failed: ${e.message}")
            throw e
        }
    }
    
    /**
     * Builds the command to launch the application with ByteHot agent.
     */
    protected open fun buildLaunchCommand(config: ProjectConfiguration, agentPath: String): List<String> {
        val command = mutableListOf<String>()
        
        command.add("java")
        command.add("-javaagent:$agentPath")
        
        // Add classpath
        config.classpath?.let { cp ->
            command.add("-cp")
            command.add(cp)
        }
        
        // Add JVM arguments from extension
        val ext = extension ?: project.extensions.getByType(ByteHotExtension::class.java)
        ext.jvmArgs.forEach { arg ->
            command.add(arg)
        }
        
        // Add JVM arguments from configuration
        config.jvmArgs?.forEach { arg ->
            command.add(arg)
        }
        
        // Add main class
        command.add(config.mainClass!!)
        
        // Add program arguments from extension
        ext.programArgs.forEach { arg ->
            command.add(arg)
        }
        
        // Add program arguments from configuration
        config.programArgs?.forEach { arg ->
            command.add(arg)
        }
        
        return command
    }
    
    /**
     * Discovers ByteHot agent JAR file.
     * Implements search strategy: local repository → project target → current directory
     */
    protected open fun findAgentJar(): String? {
        val userHome = System.getProperty("user.home")
        
        // Search in local Maven repository
        userHome?.let { home ->
            val localRepoPath = "$home/.m2/repository/org/acmsl/bytehot-application/latest-SNAPSHOT/bytehot-application-latest-SNAPSHOT-agent.jar"
            if (File(localRepoPath).exists()) {
                return localRepoPath
            }
        }
        
        // Search in current project structure
        val projectTargetPath = File(project.rootDir, "bytehot-application/target/bytehot-application-latest-SNAPSHOT-agent.jar")
        if (projectTargetPath.exists()) {
            return projectTargetPath.absolutePath
        }
        
        // Search in current directory
        val currentDirPath = File(project.projectDir, "bytehot-application-latest-SNAPSHOT-agent.jar")
        if (currentDirPath.exists()) {
            return currentDirPath.absolutePath
        }
        
        return null
    }
}