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
 * Filename: ByteHotSpringPluginTest.java
 *
 * Author: Claude Code
 *
 * Class name: ByteHotSpringPluginTest
 *
 * Responsibilities:
 *   - Test ByteHotSpringPlugin implementation and lifecycle
 *   - Verify Spring framework integration capabilities
 *   - Test plugin foundation integration with Spring-specific features
 *   - Ensure proper initialization and configuration
 *
 * Collaborators:
 *   - ByteHotSpringPlugin: The main plugin class under test
 *   - PluginBase: Parent class functionality testing
 *   - Spring Framework: Integration testing components
 *   - JUnit: Testing framework
 */
package org.acmsl.bytehot.spring;

import org.acmsl.bytehot.plugin.core.PluginBase;
import org.acmsl.bytehot.plugin.core.PluginConfiguration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for ByteHotSpringPlugin - verifying Spring framework integration.
 * @author Claude Code
 * @since 2025-07-03
 */
class ByteHotSpringPluginTest {

    private ByteHotSpringPlugin springPlugin;

    @BeforeEach
    void setUp() {
        springPlugin = new ByteHotSpringPlugin();
    }

    @Test
    @DisplayName("🧪 ByteHotSpringPlugin extends PluginBase foundation")
    void byteHotSpringPluginExtendsPluginBase() {
        // Then: Spring plugin should extend PluginBase
        assertInstanceOf(PluginBase.class, springPlugin,
            "ByteHotSpringPlugin should extend PluginBase from plugin-commons");
    }

    @Test
    @DisplayName("🧪 ByteHotSpringPlugin provides correct plugin metadata")
    void byteHotSpringPluginProvidesCorrectMetadata() {
        // Then: Plugin should have Spring-specific metadata
        assertEquals("ByteHot Spring Plugin", springPlugin.getPluginName(),
            "Plugin should have correct name");
        assertEquals("1.0.0-SNAPSHOT", springPlugin.getPluginVersion(),
            "Plugin should have correct version");
        
        final PluginConfiguration config = springPlugin.getDefaultConfiguration();
        assertNotNull(config, "Plugin should have default configuration");
        assertTrue(config.isValid(), "Default configuration should be valid");
    }

    @Test
    @DisplayName("🧪 ByteHotSpringPlugin initializes Spring-specific components")
    void byteHotSpringPluginInitializesSpringComponents() {
        // When: Plugin is created
        // Then: Spring-specific components should be initialized
        assertNotNull(springPlugin.getSpringContextManager(),
            "Spring context manager should be initialized");
        assertNotNull(springPlugin.getSpringBeanHotSwapHandler(),
            "Spring bean hot-swap handler should be initialized");
        assertNotNull(springPlugin.getSpringConfigurationDetector(),
            "Spring configuration detector should be initialized");
        assertNotNull(springPlugin.getSpringAnnotationProcessor(),
            "Spring annotation processor should be initialized");
    }

    @Test
    @DisplayName("🧪 ByteHotSpringPlugin initializes successfully")
    void byteHotSpringPluginInitializesSuccessfully() {
        // When: Plugin is initialized
        // Note: This will test the full initialization chain including Spring discovery
        // For now, we expect it to succeed even without a running Spring context
        boolean result = springPlugin.initialize();

        // Then: Initialization should succeed
        assertTrue(result, "Spring plugin initialization should succeed");
    }

    @Test
    @DisplayName("🧪 ByteHotSpringPlugin provides Spring-specific configuration")
    void byteHotSpringPluginProvidesSpringConfiguration() {
        // Given: A Spring plugin with configuration
        final PluginConfiguration config = springPlugin.getDefaultConfiguration();

        // Then: Configuration should be Spring-specific
        assertNotNull(config.getConfigurationName(), 
            "Configuration should have a name");
        assertTrue(config.getConfigurationName().contains("spring") || 
                  config.getConfigurationName().contains("Spring"),
            "Configuration name should indicate Spring-specific config");
    }
}