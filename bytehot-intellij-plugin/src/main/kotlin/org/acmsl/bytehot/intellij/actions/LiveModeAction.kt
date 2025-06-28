package org.acmsl.bytehot.intellij.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import org.acmsl.bytehot.intellij.services.ByteHotProcessManager

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
class LiveModeAction : AnAction("Start Live Mode") {
    
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val processManager = ByteHotProcessManager.getInstance(project)
        
        if (processManager.isProcessActive()) {
            stopLiveMode(processManager)
        } else {
            startLiveMode(processManager, project)
        }
    }
    
    override fun update(e: AnActionEvent) {
        val project = e.project
        val presentation = e.presentation
        
        if (project == null) {
            presentation.isEnabled = false
            return
        }
        
        val processManager = ByteHotProcessManager.getInstance(project)
        val isActive = processManager.isProcessActive()
        
        presentation.text = if (isActive) "Stop Live Mode" else "Start Live Mode"
        presentation.description = if (isActive) 
            "Stop the current ByteHot live mode session" else 
            "Start ByteHot live mode for immediate code reflection"
        presentation.isEnabled = true
    }
    
    /**
     * Starts live mode using the process manager.
     */
    private fun startLiveMode(processManager: ByteHotProcessManager, project: Project) {
        processManager.startProcess().thenAccept { result ->
            when (result) {
                is org.acmsl.bytehot.intellij.services.ProcessResult.Success -> {
                    showInfo(project, "Live mode started for ${result.config.mainClass}")
                }
                is org.acmsl.bytehot.intellij.services.ProcessResult.Failure -> {
                    showError(project, result.error)
                }
            }
        }.exceptionally { throwable ->
            showError(project, "Failed to start live mode: ${throwable.message}")
            null
        }
    }
    
    /**
     * Stops the current live mode session using the process manager.
     */
    private fun stopLiveMode(processManager: ByteHotProcessManager) {
        processManager.stopProcess().thenAccept { success ->
            // Process manager will handle notifications through listeners
        }
    }
    
    /**
     * Shows an information message to the user using IntelliJ's notification system.
     */
    private fun showInfo(project: Project, message: String) {
        com.intellij.notification.NotificationGroupManager.getInstance()
            .getNotificationGroup("ByteHot")
            .createNotification("ByteHot", message, com.intellij.notification.NotificationType.INFORMATION)
            .notify(project)
    }
    
    /**
     * Shows an error message to the user using IntelliJ's notification system.
     */
    private fun showError(project: Project, message: String) {
        com.intellij.notification.NotificationGroupManager.getInstance()
            .getNotificationGroup("ByteHot")
            .createNotification("ByteHot Error", message, com.intellij.notification.NotificationType.ERROR)
            .notify(project)
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