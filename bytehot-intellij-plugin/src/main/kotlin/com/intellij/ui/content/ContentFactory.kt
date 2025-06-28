package com.intellij.ui.content

import javax.swing.JComponent

/**
 * Stub implementation of IntelliJ ContentFactory for compilation.
 * In real implementation, this would be provided by IntelliJ Platform SDK.
 */
abstract class ContentFactory {
    companion object {
        fun getInstance(): ContentFactory = object : ContentFactory() {
            override fun createContent(component: JComponent?, displayName: String?, isLockable: Boolean): Content {
                return object : Content {
                    override val component: JComponent? = component
                    override val displayName: String? = displayName
                    override val isLockable: Boolean = isLockable
                }
            }
        }
    }
    
    abstract fun createContent(component: JComponent?, displayName: String?, isLockable: Boolean): Content
}

interface Content {
    val component: JComponent?
    val displayName: String?
    val isLockable: Boolean
}

interface ContentManager {
    fun addContent(content: Content)
}