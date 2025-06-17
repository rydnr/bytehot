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
 * Filename: ConfigurationAdapterTest.java
 *
 * Author: Claude Code
 *
 * Class name: ConfigurationAdapterTest
 *
 * Responsibilities:
 *   - Test the ConfigurationAdapter infrastructure implementation
 *   - Verify configuration loading from various sources
 *   - Ensure proper port interface implementation
 *
 * Collaborators:
 *   - ConfigurationAdapter: The adapter being tested
 *   - ConfigurationPort: The port interface
 */
package org.acmsl.bytehot.infrastructure.config;

import org.acmsl.bytehot.domain.ConfigurationPort;
import org.acmsl.bytehot.domain.WatchConfiguration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for ConfigurationAdapter.
 * @author Claude Code
 * @since 2025-06-17
 */
public class ConfigurationAdapterTest {

    /**
     * The adapter under test
     */
    private ConfigurationAdapter adapter;

    /**
     * Sets up test environment
     */
    @BeforeEach
    public void setUp() {
        adapter = new ConfigurationAdapter();
    }

    /**
     * Tests that the adapter correctly implements the port interface
     */
    @Test
    public void adapts_returns_configuration_port() {
        assertEquals(ConfigurationPort.class, adapter.adapts());
    }

    /**
     * Tests loading default configuration when no external config is available
     */
    @Test
    public void loadWatchConfiguration_returns_default_when_no_external_config() throws Exception {
        final WatchConfiguration config = adapter.loadWatchConfiguration();
        
        assertNotNull(config);
        assertNotNull(config.getFolders());
    }

    /**
     * Tests configuration source description
     */
    @Test
    public void getConfigurationSource_returns_description() {
        final String source = adapter.getConfigurationSource();
        
        assertNotNull(source);
        assertTrue(source.length() > 0);
    }

    /**
     * Tests configuration availability check
     */
    @Test
    public void isConfigurationAvailable_works() {
        // This will depend on the test environment
        // Just verify the method executes without error
        adapter.isConfigurationAvailable();
    }
}