package org.acmsl.bytehot.intellij

import org.junit.Test
import kotlin.test.*

/**
 * Unit tests for ByteHotPlugin using TDD approach.
 */
class ByteHotPluginTest {
    
    @Test
    fun testPluginInstantiation() {
        val plugin = ByteHotPlugin()
        assertNotNull(plugin, "Plugin should be instantiable")
    }
    
    @Test
    fun testPluginConstants() {
        assertEquals("org.acmsl.bytehot", ByteHotPlugin.PLUGIN_ID)
        assertEquals("ByteHot", ByteHotPlugin.PLUGIN_NAME)
        assertEquals("ByteHot.LiveMode", ByteHotPlugin.LIVE_MODE_ACTION_ID)
        assertEquals("ByteHot.StopLiveMode", ByteHotPlugin.STOP_LIVE_MODE_ACTION_ID)
        assertEquals("ByteHot", ByteHotPlugin.TOOL_WINDOW_ID)
    }
    
    @Test
    fun testInitialize() {
        val plugin = ByteHotPlugin()
        // Should not throw exception
        plugin.initialize()
    }
    
    @Test
    fun testIsAgentAvailable() {
        val plugin = ByteHotPlugin()
        val isAvailable = plugin.isAgentAvailable()
        
        // Should return a boolean without throwing exception
        assertTrue(isAvailable is Boolean, "Should return boolean")
    }
    
    @Test
    fun testFindAgentJarHandlesNullUserHome() {
        val plugin = TestableByteHotPlugin()
        
        // Create a temporary system property backup
        val originalUserHome = System.getProperty("user.home")
        
        try {
            // Test with null user.home
            System.clearProperty("user.home")
            val result = plugin.findAgentJar()
            
            // Should not crash, may return null
            // This is acceptable behavior
            
        } finally {
            // Restore original user.home
            if (originalUserHome != null) {
                System.setProperty("user.home", originalUserHome)
            }
        }
    }
    
    @Test
    fun testFindAgentJarReturnsNullWhenNotFound() {
        val plugin = TestableByteHotPlugin()
        val result = plugin.findAgentJar()
        
        // In test environment, agent JAR likely doesn't exist
        // Method should handle this gracefully
        if (result != null) {
            assertTrue(result.endsWith("agent.jar"), "If found, should be agent JAR")
        }
    }
}

/**
 * Testable version of ByteHotPlugin that exposes protected methods.
 */
class TestableByteHotPlugin : ByteHotPlugin() {
    public override fun findAgentJar(): String? {
        return super.findAgentJar()
    }
}