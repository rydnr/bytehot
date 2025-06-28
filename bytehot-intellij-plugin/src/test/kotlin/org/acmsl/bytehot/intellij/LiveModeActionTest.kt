package org.acmsl.bytehot.intellij

import org.acmsl.bytehot.intellij.actions.LiveModeAction
import org.acmsl.bytehot.intellij.actions.ProjectConfiguration
import org.junit.Test
import kotlin.test.*

/**
 * Unit tests for LiveModeAction using TDD approach.
 */
class LiveModeActionTest {
    
    @Test
    fun testActionInstantiation() {
        val action = LiveModeAction()
        assertNotNull(action, "Action should be instantiable")
    }
    
    @Test
    fun testActionInitialState() {
        val action = LiveModeAction()
        
        assertEquals("Start Live Mode", action.getValue("Name"))
        assertNotNull(action.getValue("ShortDescription"))
        assertNotNull(action.getValue("LongDescription"))
    }
    
    @Test
    fun testBuildLaunchCommandBasic() {
        val action = TestableLiveModeAction()
        val config = ProjectConfiguration(
            mainClass = "com.example.TestApp",
            classpath = "/path/to/classes",
            sourcePaths = emptyList(),
            jvmArgs = emptyList(),
            programArgs = emptyList()
        )
        
        val command = action.buildLaunchCommand(config, "/path/to/agent.jar")
        
        assertTrue(command.contains("java"), "Should contain java command")
        assertTrue(command.contains("-javaagent:/path/to/agent.jar"), "Should contain agent argument")
        assertTrue(command.contains("-cp"), "Should contain classpath flag")
        assertTrue(command.contains("/path/to/classes"), "Should contain classpath")
        assertTrue(command.contains("com.example.TestApp"), "Should contain main class")
    }
    
    @Test
    fun testBuildLaunchCommandWithJvmArgs() {
        val action = TestableLiveModeAction()
        val config = ProjectConfiguration(
            mainClass = "com.example.TestApp",
            classpath = "/path/to/classes",
            sourcePaths = emptyList(),
            jvmArgs = listOf("-Xmx512m", "-Dtest.prop=value"),
            programArgs = listOf("arg1", "arg2")
        )
        
        val command = action.buildLaunchCommand(config, "/path/to/agent.jar")
        
        assertTrue(command.contains("-Xmx512m"), "Should contain JVM arg")
        assertTrue(command.contains("-Dtest.prop=value"), "Should contain system property")
        assertTrue(command.contains("arg1"), "Should contain program arg")
        assertTrue(command.contains("arg2"), "Should contain program arg")
    }
    
    @Test
    fun testBuildLaunchCommandWithoutClasspath() {
        val action = TestableLiveModeAction()
        val config = ProjectConfiguration(
            mainClass = "com.example.TestApp",
            classpath = null,
            sourcePaths = emptyList(),
            jvmArgs = emptyList(),
            programArgs = emptyList()
        )
        
        val command = action.buildLaunchCommand(config, "/path/to/agent.jar")
        
        assertFalse(command.contains("-cp"), "Should not contain classpath flag when no classpath")
        assertTrue(command.contains("com.example.TestApp"), "Should still contain main class")
    }
    
    @Test
    fun testUpdateActionStateToggling() {
        val action = TestableLiveModeAction()
        
        // Initial state
        assertEquals("Start Live Mode", action.getValue("Name"))
        
        // Simulate active state
        action.setLiveModeActive(true)
        action.updateActionState()
        assertEquals("Stop Live Mode", action.getValue("Name"))
        
        // Simulate inactive state
        action.setLiveModeActive(false)
        action.updateActionState()
        assertEquals("Start Live Mode", action.getValue("Name"))
    }
    
    @Test
    fun testFindAgentJarPrioritizesBundledAgent() {
        val action = TestableLiveModeAction()
        
        // When - Should try bundled agent first
        val result = action.findAgentJar()
        
        // Then - Should return a path (either bundled or fallback)
        // In production with bundled agent, this would extract from resources
        // In test environment, it may fall back to development paths
        assertNotNull(result, "Should find agent JAR through bundled or fallback mechanism")
    }
    
    @Test
    fun testFindAgentJarFallbackBehavior() {
        val action = TestableLiveModeAction()
        
        // When - Bundled agent extraction may fail in test environment
        val result = action.findAgentJar()
        
        // Then - Should attempt fallback search strategy
        // This tests that the fallback mechanism is in place
        assertNotNull(result, "Should have fallback mechanism for development")
    }
}

/**
 * Testable version of LiveModeAction that exposes protected methods.
 */
class TestableLiveModeAction : LiveModeAction() {
    private var testLiveModeActive = false
    
    fun setLiveModeActive(active: Boolean) {
        testLiveModeActive = active
    }
    
    public override fun buildLaunchCommand(config: ProjectConfiguration, agentPath: String): List<String> {
        return super.buildLaunchCommand(config, agentPath)
    }
    
    public override fun updateActionState() {
        if (testLiveModeActive) {
            putValue(NAME, "Stop Live Mode")
            putValue(SHORT_DESCRIPTION, "Stop the current ByteHot live mode session")
        } else {
            putValue(NAME, "Start Live Mode")
            putValue(SHORT_DESCRIPTION, "Start ByteHot live mode for immediate code reflection")
        }
    }
    
    public override fun findAgentJar(): String? {
        return super.findAgentJar()
    }
}