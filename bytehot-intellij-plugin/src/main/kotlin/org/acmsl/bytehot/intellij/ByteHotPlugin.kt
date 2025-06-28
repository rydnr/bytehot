package org.acmsl.bytehot.intellij

/**
 * Main ByteHot IntelliJ IDEA plugin class.
 * Provides native IDE integration for live coding with ByteHot agent.
 * 
 * This plugin implements zero-configuration live mode activation,
 * visual feedback for hot-swap operations, and seamless integration
 * with IntelliJ's run/debug infrastructure.
 */
open class ByteHotPlugin {
    
    companion object {
        const val PLUGIN_ID = "org.acmsl.bytehot"
        const val PLUGIN_NAME = "ByteHot"
        const val LIVE_MODE_ACTION_ID = "ByteHot.LiveMode"
        const val STOP_LIVE_MODE_ACTION_ID = "ByteHot.StopLiveMode"
        const val TOOL_WINDOW_ID = "ByteHot"
    }
    
    /**
     * Plugin initialization.
     * Sets up the foundation for ByteHot integration.
     */
    fun initialize() {
        // Plugin initialization logic will be handled by IntelliJ framework
        // through plugin.xml configuration and action registrations
    }
    
    /**
     * Checks if ByteHot agent is available for the current project.
     * @return true if agent can be discovered, false otherwise
     */
    fun isAgentAvailable(): Boolean {
        // Basic agent discovery - will be enhanced with proper implementation
        return findAgentJar() != null
    }
    
    /**
     * Discovers ByteHot agent JAR file.
     * Implements search strategy: local repository → project target → current directory
     * @return path to agent JAR or null if not found
     */
    open protected fun findAgentJar(): String? {
        val userHome = System.getProperty("user.home")
        
        // Search in local Maven repository
        userHome?.let { home ->
            val localRepoPath = "$home/.m2/repository/org/acmsl/bytehot-application/latest-SNAPSHOT/bytehot-application-latest-SNAPSHOT-agent.jar"
            if (java.io.File(localRepoPath).exists()) {
                return localRepoPath
            }
        }
        
        // Search in current project structure
        val projectTargetPath = "bytehot-application/target/bytehot-application-latest-SNAPSHOT-agent.jar"
        if (java.io.File(projectTargetPath).exists()) {
            return projectTargetPath
        }
        
        // Search in current directory
        val currentDirPath = "bytehot-application-latest-SNAPSHOT-agent.jar"
        if (java.io.File(currentDirPath).exists()) {
            return currentDirPath
        }
        
        return null
    }
}