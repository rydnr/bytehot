package org.acmsl.bytehot.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * ByteHot Gradle Plugin.
 * 
 * Provides seamless live mode activation for Gradle projects with zero-configuration setup.
 * Automatically detects main class, builds classpath, and discovers ByteHot agent.
 * 
 * Usage:
 * ```gradle
 * plugins {
 *     id 'org.acmsl.bytehot'
 * }
 * 
 * // Run live mode
 * ./gradlew live
 * ```
 */
class ByteHotPlugin : Plugin<Project> {
    
    companion object {
        const val PLUGIN_ID = "org.acmsl.bytehot"
        const val EXTENSION_NAME = "bytehot"
        const val LIVE_TASK_NAME = "live"
        const val TASK_GROUP = "bytehot"
    }
    
    override fun apply(project: Project) {
        // Create the extension for configuration
        val extension = project.extensions.create(EXTENSION_NAME, ByteHotExtension::class.java)
        
        // Register the live task  
        project.tasks.register(LIVE_TASK_NAME, ByteHotTask::class.java) {
            group = TASK_GROUP
            description = "Starts ByteHot live mode for immediate code reflection"
            // Extension will be resolved at execution time
        }
        
        // Configure task dependencies after evaluation
        project.afterEvaluate {
            val liveTask = project.tasks.named(LIVE_TASK_NAME)
            
            // Make task depend on compilation if Java plugin is applied
            project.plugins.withId("java") {
                liveTask.configure {
                    dependsOn("classes")
                }
            }
            
            // Make task depend on compilation if Kotlin plugin is applied  
            project.plugins.withId("org.jetbrains.kotlin.jvm") {
                liveTask.configure {
                    dependsOn("classes")
                }
            }
        }
    }
}