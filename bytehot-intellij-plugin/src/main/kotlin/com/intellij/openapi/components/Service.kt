package com.intellij.openapi.components

/**
 * Stub implementation of IntelliJ Service annotation for compilation.
 * In real implementation, this would be provided by IntelliJ Platform SDK.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Service(val value: Level = Level.APP) {
    enum class Level {
        APP, PROJECT
    }
}