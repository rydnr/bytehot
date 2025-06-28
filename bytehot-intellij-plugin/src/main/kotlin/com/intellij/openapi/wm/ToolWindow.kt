package com.intellij.openapi.wm

import com.intellij.ui.content.ContentManager

/**
 * Stub implementation of IntelliJ ToolWindow for compilation.
 * In real implementation, this would be provided by IntelliJ Platform SDK.
 */
interface ToolWindow {
    val contentManager: ContentManager
        get() = object : ContentManager {
            override fun addContent(content: com.intellij.ui.content.Content) {
                // Stub implementation
            }
        }
}