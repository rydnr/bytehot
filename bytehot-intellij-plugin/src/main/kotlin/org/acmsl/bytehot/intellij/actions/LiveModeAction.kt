package org.acmsl.bytehot.intellij.actions

import org.acmsl.bytehot.intellij.ByteHotPlugin
import org.acmsl.bytehot.intellij.analysis.IntellijProjectAnalyzer
import java.awt.event.ActionEvent
import javax.swing.AbstractAction

/**
 * Live Mode Action for ByteHot IntelliJ IDEA plugin.
 * 
 * This action provides one-click activation of live coding mode,
 * automatically detecting project structure, main class, and
 * launching the application with ByteHot agent attached.
 * 
 * Features:
 * - Zero-configuration activation
 * - Automatic main class detection
 * - Classpath building from project structure
 * - Visual feedback through IntelliJ notifications
 */
open class LiveModeAction : AbstractAction("Start Live Mode") {
    
    private val plugin = ByteHotPlugin()
    private var isLiveModeActive = false
    private var currentProcess: Process? = null
    
    init {
        putValue(SHORT_DESCRIPTION, "Start ByteHot live mode for immediate code reflection")
        putValue(LONG_DESCRIPTION, "Launches your application with ByteHot agent for instant hot-swapping")
    }
    
    override fun actionPerformed(e: ActionEvent?) {
        if (isLiveModeActive) {
            stopLiveMode()
        } else {
            startLiveMode()
        }
    }
    
    /**
     * Starts live mode for the current project.
     * Performs project analysis, agent discovery, and process launching.
     */
    protected fun startLiveMode() {
        try {
            // Check if agent is available
            if (findAgentJar() == null) {
                showError("ByteHot agent not found. Please ensure bytehot-application-*-agent.jar is available.")
                return
            }
            
            // Analyze current project
            val analyzer = IntellijProjectAnalyzer()
            val projectConfig = analyzer.analyzeCurrentProject()
            
            if (projectConfig.mainClass == null) {
                showError("No main class found. Please ensure your project has a class with main method.")
                return
            }
            
            // Launch process with ByteHot agent
            val agentPath = findAgentJar()
            val command = buildLaunchCommand(projectConfig, agentPath!!)
            
            showInfo("Starting live mode for ${projectConfig.mainClass}")
            currentProcess = ProcessBuilder(command)
                .inheritIO()
                .start()
            
            isLiveModeActive = true
            updateActionState()
            
            // Monitor process in background
            Thread {
                val exitCode = currentProcess?.waitFor() ?: -1
                isLiveModeActive = false
                updateActionState()
                showInfo("Live mode stopped (exit code: $exitCode)")
            }.start()
            
        } catch (e: Exception) {
            showError("Failed to start live mode: ${e.message}")
        }
    }
    
    /**
     * Stops the current live mode session.
     */
    protected fun stopLiveMode() {
        currentProcess?.let { process ->
            if (process.isAlive) {
                process.destroyForcibly()
                showInfo("Live mode stopped")
            }
        }
        isLiveModeActive = false
        currentProcess = null
        updateActionState()
    }
    
    /**
     * Builds the command to launch the application with ByteHot agent.
     */
    open protected fun buildLaunchCommand(config: ProjectConfiguration, agentPath: String): List<String> {
        val command = mutableListOf<String>()
        
        command.add("java")
        command.add("-javaagent:$agentPath")
        
        // Add classpath
        config.classpath?.let { cp ->
            command.add("-cp")
            command.add(cp)
        }
        
        // Add JVM arguments if any
        config.jvmArgs?.forEach { arg ->
            command.add(arg)
        }
        
        // Add main class
        command.add(config.mainClass!!)
        
        // Add program arguments if any
        config.programArgs?.forEach { arg ->
            command.add(arg)
        }
        
        return command
    }
    
    /**
     * Updates the action state based on current live mode status.
     */
    open protected fun updateActionState() {
        if (isLiveModeActive) {
            putValue(NAME, "Stop Live Mode")
            putValue(SHORT_DESCRIPTION, "Stop the current ByteHot live mode session")
        } else {
            putValue(NAME, "Start Live Mode")
            putValue(SHORT_DESCRIPTION, "Start ByteHot live mode for immediate code reflection")
        }
    }
    
    /**
     * Shows an information message to the user.
     */
    protected fun showInfo(message: String) {
        // In real implementation, this would use IntelliJ's notification system
        println("ByteHot Info: $message")
    }
    
    /**
     * Shows an error message to the user.
     */
    protected fun showError(message: String) {
        // In real implementation, this would use IntelliJ's notification system
        System.err.println("ByteHot Error: $message")
    }
    
    /**
     * Discovers ByteHot agent JAR file.
     * First tries to extract bundled agent from plugin resources,
     * then falls back to common locations for development.
     * @return path to agent JAR or null if not found
     */
    protected fun findAgentJar(): String? {
        // First try to extract bundled agent from plugin resources
        try {
            val bundledAgentPath = extractBundledAgent()
            if (bundledAgentPath != null) {
                return bundledAgentPath
            }
        } catch (e: Exception) {
            // Log error but continue to fallback search
            println("Failed to extract bundled agent: ${e.message}")
        }
        
        // Fallback 1: Search in local Maven repository (for development)
        val userHome = System.getProperty("user.home")
        userHome?.let { home ->
            val localRepoPath = "$home/.m2/repository/org/acmsl/bytehot-application/latest-SNAPSHOT/bytehot-application-latest-SNAPSHOT-agent.jar"
            if (java.io.File(localRepoPath).exists()) {
                return localRepoPath
            }
        }
        
        // Fallback 2: Search in current project structure (for development)
        val projectTargetPath = "bytehot-application/target/bytehot-application-latest-SNAPSHOT-agent.jar"
        if (java.io.File(projectTargetPath).exists()) {
            return projectTargetPath
        }
        
        // Fallback 3: Search in current directory (for development)
        val currentDirPath = "bytehot-application-latest-SNAPSHOT-agent.jar"
        if (java.io.File(currentDirPath).exists()) {
            return currentDirPath
        }
        
        return null
    }
    
    /**
     * Extracts the bundled ByteHot agent JAR from plugin resources to a temporary file.
     * @return path to extracted agent JAR or null if not found
     */
    private fun extractBundledAgent(): String? {
        val agentResourcePath = "/agents/bytehot-application-agent.jar"
        
        // Try to load the bundled agent from plugin resources
        val agentStream = this::class.java.getResourceAsStream(agentResourcePath)
            ?: return null
        
        try {
            // Create temporary file
            val tempDir = System.getProperty("java.io.tmpdir")
            val tempFile = java.io.File(tempDir, "bytehot-agent-${System.currentTimeMillis()}.jar")
            
            // Copy resource to temporary file
            agentStream.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            
            // Mark for deletion on exit
            tempFile.deleteOnExit()
            
            return tempFile.absolutePath
        } catch (e: Exception) {
            throw RuntimeException("Failed to extract bundled ByteHot agent", e)
        }
    }
}

/**
 * Configuration holder for project analysis results.
 */
data class ProjectConfiguration(
    val mainClass: String?,
    val classpath: String?,
    val sourcePaths: List<String>?,
    val jvmArgs: List<String>?,
    val programArgs: List<String>?
)