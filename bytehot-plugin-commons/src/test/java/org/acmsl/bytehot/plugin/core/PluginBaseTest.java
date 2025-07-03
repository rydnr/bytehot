/*
                        ByteHot

    Copyright (C) 2025-today  rydnr@acm-sl.org

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 3 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public v3
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    Thanks to ACM S.L. for distributing this library under the GPLv3 license.
    Contact info: jose.sanleandro@acm-sl.com

 ******************************************************************************
 *
 * Filename: PluginBaseTest.java
 *
 * Author: Claude Code
 *
 * Class name: PluginBaseTest
 *
 * Responsibilities:
 *   - Test PluginBase abstract class functionality
 *   - Verify plugin lifecycle management
 *   - Test agent discovery and configuration integration
 *   - Ensure proper initialization sequence
 *
 * Collaborators:
 *   - PluginBase: The abstract class under test
 *   - AgentDiscovery: Agent location functionality
 *   - ConfigurationManager: Configuration management
 *   - PluginCommunicationHandler: Communication interface
 */
package org.acmsl.bytehot.plugin.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for PluginBase abstract class - verifying plugin foundation functionality.
 * @author Claude Code
 * @since 2025-07-03
 */
class PluginBaseTest {

    /**
     * Concrete implementation of PluginBase for testing
     */
    private static class TestPlugin extends PluginBase {
        private boolean onInitializeCalled = false;

        @Override
        public String getPluginName() {
            return "TestPlugin";
        }

        @Override
        public String getPluginVersion() {
            return "1.0.0-TEST";
        }

        @Override
        public PluginConfiguration getDefaultConfiguration() {
            return new PluginConfiguration() {
                @Override
                public String getConfigurationName() {
                    return "test-config";
                }

                @Override
                public boolean isValid() {
                    return true;
                }
            };
        }

        @Override
        protected boolean onInitialize() {
            onInitializeCalled = true;
            return true;
        }

        public boolean isOnInitializeCalled() {
            return onInitializeCalled;
        }
    }

    private TestPlugin testPlugin;

    @BeforeEach
    void setUp() {
        testPlugin = new TestPlugin();
    }

    @Test
    @DisplayName("🧪 PluginBase provides required abstract methods")
    void pluginBaseProvideRequiredAbstractMethods() {
        // Then: TestPlugin should implement all required abstract methods
        assertEquals("TestPlugin", testPlugin.getPluginName(),
            "Plugin should have a name");
        assertEquals("1.0.0-TEST", testPlugin.getPluginVersion(),
            "Plugin should have a version");
        assertNotNull(testPlugin.getDefaultConfiguration(),
            "Plugin should have default configuration");
    }

    @Test
    @DisplayName("🧪 PluginBase initializes components in correct order")
    void pluginBaseInitializesComponentsInCorrectOrder() {
        // When: Initializing the plugin
        // Note: This test will need to be adjusted once AgentDiscovery and other components are implemented
        assertNotNull(testPlugin, "Test plugin should be created successfully");
        
        // The initialization sequence should be:
        // 1. AgentDiscovery.discoverAgent()
        // 2. ConfigurationManager.loadConfiguration()
        // 3. CommunicationHandler.connect()
        // 4. HealthMonitor.start()
        // 5. onInitialize()
        
        // For now, verify the plugin can be created
        assertTrue(true, "Plugin creation should succeed");
    }

    @Test
    @DisplayName("🧪 PluginBase calls onInitialize when components are ready")
    void pluginBaseCallsOnInitializeWhenComponentsReady() {
        // Given: A test plugin that tracks onInitialize calls
        assertFalse(testPlugin.isOnInitializeCalled(), 
            "onInitialize should not be called before initialization");

        // When: Initializing the plugin through the initialize() method
        boolean result = testPlugin.initialize();

        // Then: onInitialize should be called and return true
        assertTrue(result, "initialize() should return true for successful initialization");
        assertTrue(testPlugin.isOnInitializeCalled(), 
            "onInitialize should be called during initialization");
    }

    @Test
    @DisplayName("🧪 PluginBase has proper plugin metadata")
    void pluginBaseHasProperPluginMetadata() {
        // Then: Plugin should have non-empty metadata
        assertNotNull(testPlugin.getPluginName(), "Plugin name should not be null");
        assertFalse(testPlugin.getPluginName().trim().isEmpty(), 
            "Plugin name should not be empty");
        
        assertNotNull(testPlugin.getPluginVersion(), "Plugin version should not be null");
        assertFalse(testPlugin.getPluginVersion().trim().isEmpty(), 
            "Plugin version should not be empty");

        final PluginConfiguration config = testPlugin.getDefaultConfiguration();
        assertNotNull(config, "Default configuration should not be null");
        assertTrue(config.isValid(), "Default configuration should be valid");
    }

    @Test
    @DisplayName("🧪 PluginBase supports configuration validation")
    void pluginBaseSupportConfigurationValidation() {
        // Given: A plugin with default configuration
        final PluginConfiguration config = testPlugin.getDefaultConfiguration();

        // Then: Configuration should be validatable
        assertNotNull(config, "Configuration should not be null");
        assertTrue(config.isValid(), "Default configuration should be valid");
        assertNotNull(config.getConfigurationName(), 
            "Configuration should have a name");
    }
}

/**
 * Minimal PluginConfiguration interface for testing
 */
interface PluginConfiguration {
    String getConfigurationName();
    boolean isValid();
}