package org.acmsl.bytehot.intellij

import org.acmsl.bytehot.intellij.actions.LiveModeAction
import org.acmsl.bytehot.intellij.analysis.ProjectConfiguration
import org.junit.Test
import kotlin.test.*

/**
 * Unit tests for LiveModeAction using TDD approach.
 * 
 * These tests focus on verifying the action can be instantiated
 * and basic functionality works. Full integration testing would
 * require the actual IntelliJ Platform SDK.
 */
class LiveModeActionTest {
    
    @Test
    fun testActionInstantiation() {
        val action = LiveModeAction()
        assertNotNull(action, "Action should be instantiable")
    }
    
    @Test
    fun testProjectConfigurationCreation() {
        val config = ProjectConfiguration(
            mainClass = "com.example.TestApp",
            classpath = "/path/to/classes",
            sourcePaths = listOf("/src/main/java"),
            jvmArgs = listOf("-Xmx512m"),
            programArgs = listOf("arg1", "arg2")
        )
        
        assertEquals("com.example.TestApp", config.mainClass)
        assertEquals("/path/to/classes", config.classpath)
        assertEquals(1, config.sourcePaths?.size)
        assertEquals(1, config.jvmArgs?.size)
        assertEquals(2, config.programArgs?.size)
    }
    
    @Test
    fun testProjectConfigurationWithNulls() {
        val config = ProjectConfiguration(
            mainClass = null,
            classpath = null,
            sourcePaths = null,
            jvmArgs = null,
            programArgs = null
        )
        
        assertNull(config.mainClass)
        assertNull(config.classpath)
        assertNull(config.sourcePaths)
        assertNull(config.jvmArgs)
        assertNull(config.programArgs)
    }
    
    @Test
    fun testConfigurationEmptyLists() {
        val config = ProjectConfiguration(
            mainClass = "com.example.App",
            classpath = "/classes",
            sourcePaths = emptyList(),
            jvmArgs = emptyList(),
            programArgs = emptyList()
        )
        
        assertEquals("com.example.App", config.mainClass)
        assertEquals(0, config.sourcePaths?.size)
        assertEquals(0, config.jvmArgs?.size)
        assertEquals(0, config.programArgs?.size)
    }
    
    @Test
    fun testConfigurationDataClass() {
        val config1 = ProjectConfiguration(
            mainClass = "test.App",
            classpath = "/test",
            sourcePaths = emptyList(),
            jvmArgs = emptyList(),
            programArgs = emptyList()
        )
        
        val config2 = ProjectConfiguration(
            mainClass = "test.App", 
            classpath = "/test",
            sourcePaths = emptyList(),
            jvmArgs = emptyList(),
            programArgs = emptyList()
        )
        
        assertEquals(config1, config2, "Configurations with same values should be equal")
        assertEquals(config1.hashCode(), config2.hashCode(), "Hash codes should match")
        assertNotSame(config1, config2, "Should be different instances")
    }
}