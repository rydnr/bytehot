package com.intellij.openapi.project

/**
 * Stub implementation of IntelliJ Project interface for compilation.
 * In real implementation, this would be provided by IntelliJ Platform SDK.
 */
interface Project {
    val basePath: String?
    
    @Suppress("UNCHECKED_CAST")
    fun <T> getService(serviceClass: Class<T>): T {
        // Stub implementation - return a mock instance
        return serviceClass.getDeclaredConstructor(Project::class.java).newInstance(this) as T
    }
}