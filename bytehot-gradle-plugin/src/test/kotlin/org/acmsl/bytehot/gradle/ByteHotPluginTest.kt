package org.acmsl.bytehot.gradle

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test
import kotlin.test.*

/**
 * Unit tests for ByteHotPlugin using TDD approach.
 */
class ByteHotPluginTest {
    
    @Test
    fun testPluginAppliesSuccessfully() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("org.acmsl.bytehot")
        
        assertNotNull(project.plugins.findPlugin("org.acmsl.bytehot"), "Plugin should be applied")
    }
    
    @Test
    fun testPluginAddsLiveTask() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("org.acmsl.bytehot")
        
        val liveTask = project.tasks.findByName("live")
        assertNotNull(liveTask, "Plugin should add 'live' task")
        assertTrue(liveTask is ByteHotTask, "Live task should be ByteHotTask instance")
    }
    
    @Test
    fun testPluginAddsExtension() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("org.acmsl.bytehot")
        
        val extension = project.extensions.findByType(ByteHotExtension::class.java)
        assertNotNull(extension, "Plugin should add ByteHot extension")
    }
    
    @Test
    fun testExtensionDefaultValues() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("org.acmsl.bytehot")
        
        val extension = project.extensions.getByType(ByteHotExtension::class.java)
        assertTrue(extension.enabled, "Extension should be enabled by default")
        assertFalse(extension.dryRun, "Dry run should be false by default")
        assertFalse(extension.verbose, "Verbose should be false by default")
        assertNull(extension.mainClass, "Main class should be null by default (auto-detected)")
        assertNull(extension.agentPath, "Agent path should be null by default (auto-discovered)")
    }
    
    @Test
    fun testTaskConfiguration() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("org.acmsl.bytehot")
        
        val liveTask = project.tasks.getByName("live") as ByteHotTask
        assertEquals("Starts ByteHot live mode for immediate code reflection", liveTask.description)
        assertEquals("bytehot", liveTask.group)
    }
    
    @Test
    fun testTaskDependsOnClasses() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("java") // Apply Java plugin first
        project.pluginManager.apply("org.acmsl.bytehot")
        
        val liveTask = project.tasks.getByName("live") as ByteHotTask
        val classesTasks = project.tasks.matching { it.name == "classes" }
        
        // Live task should depend on classes compilation
        assertTrue(liveTask.dependsOn.containsAll(classesTasks), "Live task should depend on classes task")
    }
}