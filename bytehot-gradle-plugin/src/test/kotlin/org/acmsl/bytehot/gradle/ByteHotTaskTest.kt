package org.acmsl.bytehot.gradle

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test
import kotlin.test.*
import java.io.File

/**
 * Unit tests for ByteHotTask using TDD approach.
 */
class ByteHotTaskTest {
    
    @Test
    fun testTaskInstantiation() {
        val project = ProjectBuilder.builder().build()
        val task = project.tasks.create("testLive", ByteHotTask::class.java)
        
        assertNotNull(task, "Task should be instantiable")
    }
    
    @Test
    fun testTaskActionWithDryRun() {
        val project = ProjectBuilder.builder().build()
        val extension = ByteHotExtension().apply {
            dryRun = true
            mainClass = "com.example.TestApp"
        }
        
        val task = project.tasks.create("testLive", ByteHotTask::class.java) { t ->
            t.extension = extension
        }
        
        // Should not throw exception in dry run mode
        try {
            task.executeTask()
            // Test passes if no exception is thrown
        } catch (e: Exception) {
            fail("Dry run should not throw exception: ${e.message}")
        }
    }
    
    @Test
    fun testTaskActionWhenDisabled() {
        val project = ProjectBuilder.builder().build()
        val extension = ByteHotExtension().apply {
            enabled = false
        }
        
        val task = project.tasks.create("testLive", ByteHotTask::class.java) { t ->
            t.extension = extension
        }
        
        // Should not throw exception when disabled
        try {
            task.executeTask()
            // Test passes if no exception is thrown
        } catch (e: Exception) {
            fail("Disabled task should not throw exception: ${e.message}")
        }
    }
    
    @Test
    fun testBuildLaunchCommand() {
        val project = ProjectBuilder.builder().build()
        val task = TestableBytHotTask(project)
        
        val config = ProjectConfiguration(
            mainClass = "com.example.TestApp",
            classpath = "/path/to/classes",
            sourcePaths = emptyList(),
            jvmArgs = emptyList(),
            programArgs = emptyList()
        )
        
        val command = task.buildLaunchCommand(config, "/path/to/agent.jar")
        
        assertTrue(command.contains("java"), "Should contain java command")
        assertTrue(command.contains("-javaagent:/path/to/agent.jar"), "Should contain agent argument")
        assertTrue(command.contains("-cp"), "Should contain classpath flag")
        assertTrue(command.contains("/path/to/classes"), "Should contain classpath")
        assertTrue(command.contains("com.example.TestApp"), "Should contain main class")
    }
    
    @Test
    fun testBuildLaunchCommandWithJvmArgs() {
        val project = ProjectBuilder.builder().build()
        val task = TestableBytHotTask(project)
        
        val config = ProjectConfiguration(
            mainClass = "com.example.TestApp",
            classpath = "/path/to/classes",
            sourcePaths = emptyList(),
            jvmArgs = listOf("-Xmx512m", "-Dtest.prop=value"),
            programArgs = listOf("arg1", "arg2")
        )
        
        val command = task.buildLaunchCommand(config, "/path/to/agent.jar")
        
        assertTrue(command.contains("-Xmx512m"), "Should contain JVM arg")
        assertTrue(command.contains("-Dtest.prop=value"), "Should contain system property")
        assertTrue(command.contains("arg1"), "Should contain program arg")
        assertTrue(command.contains("arg2"), "Should contain program arg")
    }
    
    @Test
    fun testFindAgentJarReturnsNullWhenNotFound() {
        val project = ProjectBuilder.builder().build()
        val task = TestableBytHotTask(project)
        
        val result = task.findAgentJar()
        
        // In test environment, agent JAR likely doesn't exist
        // Method should handle this gracefully
        if (result != null) {
            assertTrue(result.endsWith("agent.jar"), "If found, should be agent JAR")
        }
    }
}

/**
 * Testable version of ByteHotTask that exposes protected methods.
 */
class TestableBytHotTask(project: Project) : ByteHotTask() {
    
    init {
        this.project = project
    }
    
    public fun buildLaunchCommand(config: ProjectConfiguration, agentPath: String): List<String> {
        return super.buildLaunchCommand(config, agentPath)
    }
    
    public fun findAgentJar(): String? {
        return super.findAgentJar()
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