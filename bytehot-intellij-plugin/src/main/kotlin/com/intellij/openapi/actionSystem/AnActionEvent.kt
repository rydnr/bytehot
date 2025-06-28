package com.intellij.openapi.actionSystem

import com.intellij.openapi.project.Project

/**
 * Stub implementation of IntelliJ AnActionEvent for compilation.
 * In real implementation, this would be provided by IntelliJ Platform SDK.
 */
class AnActionEvent {
    val project: Project? = null
    val presentation: Presentation = Presentation()
}

class Presentation {
    var text: String = ""
    var description: String = ""
    var isEnabled: Boolean = true
}