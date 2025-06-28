package com.intellij.openapi.wm

import com.intellij.openapi.project.Project

/**
 * Stub implementation of IntelliJ ToolWindowFactory for compilation.
 * In real implementation, this would be provided by IntelliJ Platform SDK.
 */
interface ToolWindowFactory {
    fun createToolWindowContent(project: Project, toolWindow: ToolWindow)
    fun shouldBeAvailable(project: Project): Boolean = true
}