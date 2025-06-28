package org.acmsl.bytehot.gradle

/**
 * ByteHot Gradle Plugin Extension.
 * 
 * Provides configuration options for the ByteHot Gradle plugin.
 * All options have sensible defaults for zero-configuration usage.
 * 
 * Example configuration:
 * ```gradle
 * bytehot {
 *     enabled = true
 *     mainClass = 'com.example.Application'
 *     agentPath = '/path/to/bytehot-agent.jar'
 *     verbose = true
 *     dryRun = false
 *     jvmArgs = ['-Xmx512m', '-Dspring.profiles.active=dev']
 *     watchPaths = ['src/main/java', 'src/main/kotlin']
 * }
 * ```
 */
open class ByteHotExtension {
    
    /**
     * Enable or disable the ByteHot plugin.
     * Default: true
     */
    var enabled: Boolean = true
    
    /**
     * Main class to execute.
     * If null, the plugin will auto-detect using multiple strategies.
     * Default: null (auto-detected)
     */
    var mainClass: String? = null
    
    /**
     * Path to the ByteHot agent JAR file.
     * If null, the plugin will auto-discover the agent.
     * Default: null (auto-discovered)
     */
    var agentPath: String? = null
    
    /**
     * Enable verbose output for debugging.
     * Default: false
     */
    var verbose: Boolean = false
    
    /**
     * Dry run mode - show configuration without executing.
     * Default: false
     */
    var dryRun: Boolean = false
    
    /**
     * Additional JVM arguments to pass to the application.
     * Default: empty list
     */
    var jvmArgs: List<String> = emptyList()
    
    /**
     * Directories to monitor for changes.
     * If empty, the plugin will auto-detect source directories.
     * Default: empty list (auto-detected)
     */
    var watchPaths: List<String> = emptyList()
    
    /**
     * Program arguments to pass to the main class.
     * Default: empty list
     */
    var programArgs: List<String> = emptyList()
}