package com.intellij.openapi.actionSystem

/**
 * Stub implementation of IntelliJ AnAction for compilation.
 * In real implementation, this would be provided by IntelliJ Platform SDK.
 */
abstract class AnAction(private val text: String? = null) {
    abstract fun actionPerformed(e: AnActionEvent)
    open fun update(e: AnActionEvent) {}
}