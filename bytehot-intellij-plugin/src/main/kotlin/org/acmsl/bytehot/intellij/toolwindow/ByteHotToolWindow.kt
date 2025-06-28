package org.acmsl.bytehot.intellij.toolwindow

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import org.acmsl.bytehot.intellij.services.ByteHotProcessManager
import org.acmsl.bytehot.intellij.services.ProcessLifecycleListener
import org.acmsl.bytehot.intellij.analysis.ProjectConfiguration
import javax.swing.*
import java.awt.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * ByteHot Tool Window Factory for IntelliJ IDEA.
 * 
 * Creates and manages the ByteHot tool window that provides:
 * - Live mode status monitoring
 * - Process control buttons
 * - Real-time logging output
 * - Agent status information
 */
class ByteHotToolWindowFactory : ToolWindowFactory {
    
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val myToolWindow = ByteHotToolWindow(project)
        val content = ContentFactory.getInstance().createContent(myToolWindow.contentPanel, "", false)
        toolWindow.contentManager.addContent(content)
    }
    
    override fun shouldBeAvailable(project: Project) = true
}

/**
 * Main tool window component for ByteHot integration.
 */
class ByteHotToolWindow(private val project: Project) : ProcessLifecycleListener {
    
    val contentPanel: JPanel = JPanel(BorderLayout())
    
    private val statusLabel = JLabel("Status: Ready")
    private val startButton = JButton("Start Live Mode")
    private val stopButton = JButton("Stop Live Mode")
    private val clearButton = JButton("Clear Log")
    private val logArea = JTextArea(15, 50)
    private val processInfoLabel = JLabel("Process: None")
    private val agentStatusLabel = JLabel("Agent: Not loaded")
    
    private val processManager = ByteHotProcessManager.getInstance(project)
    
    init {
        setupUI()
        setupActions()
        setupProcessManager()
        updateButtonStates()
        logMessage("ByteHot tool window initialized")
    }
    
    /**
     * Sets up the process manager integration.
     */
    private fun setupProcessManager() {
        processManager.addListener("toolwindow", this)
        
        // Update initial state
        if (processManager.isProcessActive()) {
            updateStatus("Active", Color.GREEN)
            updateProcessInfo("PID: ${processManager.getCurrentProcess()?.pid() ?: "Unknown"}")
            updateAgentStatus("Loaded and active")
        }
    }
    
    /**
     * Sets up the user interface components.
     */
    private fun setupUI() {
        // Create status panel
        val statusPanel = JPanel(FlowLayout(FlowLayout.LEFT))
        statusPanel.add(JLabel("Live Mode "))
        statusPanel.add(statusLabel)
        statusPanel.add(Box.createHorizontalStrut(20))
        statusPanel.add(processInfoLabel)
        statusPanel.add(Box.createHorizontalStrut(20))
        statusPanel.add(agentStatusLabel)
        
        // Create button panel
        val buttonPanel = JPanel(FlowLayout(FlowLayout.LEFT))
        buttonPanel.add(startButton)
        buttonPanel.add(stopButton)
        buttonPanel.add(Box.createHorizontalStrut(20))
        buttonPanel.add(clearButton)
        
        // Create control panel (status + buttons)
        val controlPanel = JPanel(BorderLayout())
        controlPanel.add(statusPanel, BorderLayout.NORTH)
        controlPanel.add(buttonPanel, BorderLayout.SOUTH)
        controlPanel.border = BorderFactory.createTitledBorder("Control")
        
        // Create log panel
        logArea.isEditable = false
        logArea.font = Font(Font.MONOSPACED, Font.PLAIN, 12)
        logArea.background = Color(248, 248, 248)
        val scrollPane = JScrollPane(logArea)
        scrollPane.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
        scrollPane.border = BorderFactory.createTitledBorder("Output")
        
        // Assemble main panel
        contentPanel.add(controlPanel, BorderLayout.NORTH)
        contentPanel.add(scrollPane, BorderLayout.CENTER)
        contentPanel.border = BorderFactory.createEmptyBorder(5, 5, 5, 5)
    }
    
    /**
     * Sets up button actions and event handlers.
     */
    private fun setupActions() {
        startButton.addActionListener { startLiveMode() }
        stopButton.addActionListener { stopLiveMode() }
        clearButton.addActionListener { clearLog() }
    }
    
    /**
     * Starts live mode using the process manager.
     */
    private fun startLiveMode() {
        logMessage("Starting live mode...")
        
        processManager.startProcess().thenAccept { result ->
            SwingUtilities.invokeLater {
                when (result) {
                    is org.acmsl.bytehot.intellij.services.ProcessResult.Success -> {
                        logMessage("Live mode started for ${result.config.mainClass}")
                    }
                    is org.acmsl.bytehot.intellij.services.ProcessResult.Failure -> {
                        logError(result.error)
                    }
                }
            }
        }.exceptionally { throwable ->
            SwingUtilities.invokeLater {
                logError("Failed to start live mode: ${throwable.message}")
            }
            null
        }
    }
    
    /**
     * Stops the current live mode session using the process manager.
     */
    private fun stopLiveMode() {
        logMessage("Stopping live mode...")
        processManager.stopProcess()
    }
    
    /**
     * Clears the log output area.
     */
    private fun clearLog() {
        logArea.text = ""
        logMessage("Log cleared")
    }
    
    /**
     * Updates the status label with specified text and color.
     */
    private fun updateStatus(status: String, color: Color) {
        SwingUtilities.invokeLater {
            statusLabel.text = "Status: $status"
            statusLabel.foreground = color
        }
    }
    
    /**
     * Updates the process information label.
     */
    private fun updateProcessInfo(info: String) {
        SwingUtilities.invokeLater {
            processInfoLabel.text = info
        }
    }
    
    /**
     * Updates the agent status label.
     */
    private fun updateAgentStatus(status: String) {
        SwingUtilities.invokeLater {
            agentStatusLabel.text = status
        }
    }
    
    /**
     * Updates button enabled states based on current live mode status.
     */
    private fun updateButtonStates() {
        SwingUtilities.invokeLater {
            val isActive = processManager.isProcessActive()
            startButton.isEnabled = !isActive
            stopButton.isEnabled = isActive
        }
    }
    
    /**
     * Logs a message with timestamp to the output area.
     */
    fun logMessage(message: String) {
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        SwingUtilities.invokeLater {
            logArea.append("[$timestamp] $message\n")
            logArea.caretPosition = logArea.document.length
        }
    }
    
    /**
     * Logs an error message with timestamp to the output area.
     */
    fun logError(message: String) {
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        SwingUtilities.invokeLater {
            logArea.append("[$timestamp] ERROR: $message\n")
            logArea.caretPosition = logArea.document.length
        }
    }
    
    
    // ProcessLifecycleListener implementation
    
    override fun onProcessStarted(process: Process, config: ProjectConfiguration) {
        SwingUtilities.invokeLater {
            updateStatus("Active", Color.GREEN)
            updateProcessInfo("PID: ${process.pid()}")
            updateAgentStatus("Loaded and active")
            updateButtonStates()
            logMessage("Process started with main class: ${config.mainClass}")
        }
    }
    
    override fun onProcessStopping(process: Process) {
        SwingUtilities.invokeLater {
            logMessage("Process stopping...")
        }
    }
    
    override fun onProcessStopped() {
        SwingUtilities.invokeLater {
            updateStatus("Ready", Color.BLACK)
            updateProcessInfo("Process: None")
            updateAgentStatus("Agent: Not loaded")
            updateButtonStates()
            logMessage("Process stopped")
        }
    }
    
    override fun onProcessExited(exitCode: Int) {
        SwingUtilities.invokeLater {
            updateStatus("Ready", Color.BLACK)
            updateProcessInfo("Process: None")
            updateAgentStatus("Agent: Not loaded")
            updateButtonStates()
            logMessage("Process exited with code: $exitCode")
        }
    }
}