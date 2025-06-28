package org.acmsl.bytehot.intellij.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import org.acmsl.bytehot.intellij.analysis.IntellijProjectAnalyzer
import org.acmsl.bytehot.intellij.analysis.ProjectConfiguration
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

/**
 * Service for managing ByteHot processes and their lifecycle.
 * 
 * This service handles:
 * - Process creation and management
 * - Agent discovery and extraction
 * - Project configuration analysis
 * - Process monitoring and lifecycle events
 * - Resource cleanup and disposal
 */
@Service(Service.Level.PROJECT)
class ByteHotProcessManager(private val project: Project) {
    
    companion object {
        fun getInstance(project: Project): ByteHotProcessManager {
            return project.getService(ByteHotProcessManager::class.java)
        }
    }
    
    private val currentProcess = AtomicReference<Process?>()
    private val isActive = AtomicBoolean(false)
    private val listeners = ConcurrentHashMap<String, ProcessLifecycleListener>()
    private val monitoringThread = AtomicReference<Thread?>()
    
    init {
        // Ensure cleanup when the project is disposed
        Disposer.register(project) {
            stopProcess()
            cleanup()
        }
    }
    
    /**
     * Starts a new ByteHot process for the current project.
     * 
     * @param config Optional project configuration override
     * @return CompletableFuture that completes when the process starts or fails
     */
    fun startProcess(config: ProjectConfiguration? = null): CompletableFuture<ProcessResult> {
        return CompletableFuture.supplyAsync {
            try {
                if (isActive.get()) {
                    return@supplyAsync ProcessResult.failure("Process is already running")
                }
                
                // Discover agent
                val agentPath = discoverAgent()
                    ?: return@supplyAsync ProcessResult.failure("ByteHot agent not found")
                
                // Analyze project if config not provided
                val projectConfig = config ?: analyzeProject()
                    ?: return@supplyAsync ProcessResult.failure("Failed to analyze project configuration")
                
                if (projectConfig.mainClass == null) {
                    return@supplyAsync ProcessResult.failure("No main class found in project")
                }
                
                // Build command
                val command = buildLaunchCommand(projectConfig, agentPath)
                
                // Start process
                val processBuilder = ProcessBuilder(command)
                    .directory(File(project.basePath ?: "."))
                    .redirectErrorStream(true)
                
                val process = processBuilder.start()
                currentProcess.set(process)
                isActive.set(true)
                
                // Notify listeners
                notifyListeners { it.onProcessStarted(process, projectConfig) }
                
                // Start monitoring
                startMonitoring(process)
                
                ProcessResult.success(process, projectConfig)
                
            } catch (e: Exception) {
                ProcessResult.failure("Failed to start process: ${e.message}")
            }
        }
    }
    
    /**
     * Stops the current ByteHot process.
     * 
     * @param force Whether to force termination
     * @return CompletableFuture that completes when the process stops
     */
    fun stopProcess(force: Boolean = false): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync {
            try {
                val process = currentProcess.get()
                if (process == null || !process.isAlive) {
                    isActive.set(false)
                    return@supplyAsync true
                }
                
                // Notify listeners before stopping
                notifyListeners { it.onProcessStopping(process) }
                
                if (force) {
                    process.destroyForcibly()
                } else {
                    process.destroy()
                    
                    // Wait for graceful shutdown, then force if needed
                    val exited = process.waitFor(5, java.util.concurrent.TimeUnit.SECONDS)
                    if (!exited) {
                        process.destroyForcibly()
                    }
                }
                
                currentProcess.set(null)
                isActive.set(false)
                
                // Notify listeners after stopping
                notifyListeners { it.onProcessStopped() }
                
                true
            } catch (e: Exception) {
                false
            }
        }
    }
    
    /**
     * Checks if a ByteHot process is currently active.
     */
    fun isProcessActive(): Boolean = isActive.get()
    
    /**
     * Gets the current process, if any.
     */
    fun getCurrentProcess(): Process? = currentProcess.get()
    
    /**
     * Registers a process lifecycle listener.
     */
    fun addListener(id: String, listener: ProcessLifecycleListener) {
        listeners[id] = listener
    }
    
    /**
     * Unregisters a process lifecycle listener.
     */
    fun removeListener(id: String) {
        listeners.remove(id)
    }
    
    /**
     * Discovers the ByteHot agent JAR file.
     */
    private fun discoverAgent(): String? {
        // First try extracting bundled agent
        try {
            val bundledAgent = extractBundledAgent()
            if (bundledAgent != null) {
                return bundledAgent
            }
        } catch (e: Exception) {
            // Log but continue to fallback
        }
        
        // Search in common locations
        val searchPaths = listOf(
            // Local Maven repository
            "${System.getProperty("user.home")}/.m2/repository/org/acmsl/bytehot-application/latest-SNAPSHOT/bytehot-application-latest-SNAPSHOT-agent.jar",
            // Project target directory
            "${project.basePath}/bytehot-application/target/bytehot-application-latest-SNAPSHOT-agent.jar",
            // Current directory
            "bytehot-application-latest-SNAPSHOT-agent.jar"
        )
        
        return searchPaths.firstOrNull { File(it).exists() }
    }
    
    /**
     * Extracts bundled agent from plugin resources.
     */
    private fun extractBundledAgent(): String? {
        val resourcePath = "/agents/bytehot-application-agent.jar"
        val agentStream = javaClass.getResourceAsStream(resourcePath) ?: return null
        
        val tempDir = System.getProperty("java.io.tmpdir")
        val tempFile = File(tempDir, "bytehot-agent-${System.currentTimeMillis()}.jar")
        
        agentStream.use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        
        tempFile.deleteOnExit()
        return tempFile.absolutePath
    }
    
    /**
     * Analyzes the current project configuration.
     */
    private fun analyzeProject(): ProjectConfiguration? {
        return try {
            val analyzer = IntellijProjectAnalyzer()
            analyzer.analyzeCurrentProject()
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Builds the command to launch the application with ByteHot agent.
     */
    private fun buildLaunchCommand(config: ProjectConfiguration, agentPath: String): List<String> {
        val command = mutableListOf<String>()
        
        command.add("java")
        command.add("-javaagent:$agentPath")
        
        // Add classpath
        config.classpath?.let { cp ->
            command.add("-cp")
            command.add(cp)
        }
        
        // Add JVM arguments
        config.jvmArgs?.forEach { arg ->
            command.add(arg)
        }
        
        // Add main class
        command.add(config.mainClass!!)
        
        // Add program arguments
        config.programArgs?.forEach { arg ->
            command.add(arg)
        }
        
        return command
    }
    
    /**
     * Starts monitoring the process in a background thread.
     */
    private fun startMonitoring(process: Process) {
        val thread = Thread {
            try {
                val exitCode = process.waitFor()
                
                // Process has exited
                currentProcess.set(null)
                isActive.set(false)
                
                // Notify listeners
                notifyListeners { it.onProcessExited(exitCode) }
                
            } catch (e: InterruptedException) {
                // Thread was interrupted, cleanup
                currentProcess.set(null)
                isActive.set(false)
            }
        }
        
        thread.name = "ByteHot-Process-Monitor"
        thread.isDaemon = true
        thread.start()
        
        monitoringThread.set(thread)
    }
    
    /**
     * Notifies all registered listeners.
     */
    private fun notifyListeners(action: (ProcessLifecycleListener) -> Unit) {
        listeners.values.forEach { listener ->
            try {
                action(listener)
            } catch (e: Exception) {
                // Log but don't let one listener break others
            }
        }
    }
    
    /**
     * Cleans up resources.
     */
    private fun cleanup() {
        // Stop monitoring thread
        monitoringThread.get()?.interrupt()
        monitoringThread.set(null)
        
        // Clear listeners
        listeners.clear()
        
        // Ensure process is stopped
        currentProcess.get()?.destroyForcibly()
        currentProcess.set(null)
        isActive.set(false)
    }
}

/**
 * Interface for process lifecycle listeners.
 */
interface ProcessLifecycleListener {
    
    /**
     * Called when a process is started.
     */
    fun onProcessStarted(process: Process, config: ProjectConfiguration) {}
    
    /**
     * Called when a process is about to be stopped.
     */
    fun onProcessStopping(process: Process) {}
    
    /**
     * Called when a process has been stopped.
     */
    fun onProcessStopped() {}
    
    /**
     * Called when a process exits (either normally or due to error).
     */
    fun onProcessExited(exitCode: Int) {}
}

/**
 * Result of a process operation.
 */
sealed class ProcessResult {
    data class Success(val process: Process, val config: ProjectConfiguration) : ProcessResult()
    data class Failure(val error: String) : ProcessResult()
    
    companion object {
        fun success(process: Process, config: ProjectConfiguration) = Success(process, config)
        fun failure(error: String) = Failure(error)
    }
    
    fun isSuccess(): Boolean = this is Success
    fun isFailure(): Boolean = this is Failure
}