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
 * Filename: ConfigurationLoadingIntegrationTest.java
 *
 * Author: Claude Code
 *
 * Class name: ConfigurationLoadingIntegrationTest
 *
 * Responsibilities: Test configuration loading from YAML files and system properties
 *
 * Collaborators:
 *   - ConfigurationAdapter: Configuration loading implementation
 *   - WatchConfiguration: Domain configuration object
 */
package org.acmsl.bytehot.infrastructure.config;

import org.acmsl.bytehot.domain.WatchConfiguration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test to verify configuration loading from YAML files works correctly.
 * This test reproduces the issue where WatchConfiguration.folders is null even when
 * a valid YAML configuration file is provided.
 * @author Claude Code
 * @since 2025-06-21
 */
public class ConfigurationLoadingIntegrationTest {

    @TempDir
    protected Path tempDir;

    protected String originalBhconfig;

    @BeforeEach
    void setUp() {
        // Store original bhconfig to restore later
        originalBhconfig = System.getProperty("bhconfig");
    }

    @AfterEach
    void tearDown() {
        // Restore original bhconfig property
        if (originalBhconfig != null) {
            System.setProperty("bhconfig", originalBhconfig);
        } else {
            System.clearProperty("bhconfig");
        }
    }

    @Test
    void should_load_watch_paths_from_yaml_file() throws Exception {
        // Given: A valid YAML configuration file
        final Path configFile = tempDir.resolve("bytehot.yml");
        final String yamlContent = """
            bytehot:
              watch:
                - path: "target/classes"
                  patterns: ["*.class"]
                  recursive: true
                - path: "build/classes"
                  patterns: ["*.class", "*.jar"]
                  recursive: true
            """;
        Files.writeString(configFile, yamlContent);

        // And: bhconfig system property points to the file
        System.setProperty("bhconfig", configFile.toString());

        // When: Loading configuration using ConfigurationAdapter
        final ConfigurationAdapter adapter = new ConfigurationAdapter();
        final WatchConfiguration config = adapter.loadWatchConfiguration();

        // Then: Should load watch paths correctly with non-null folders
        assertThat(config).isNotNull();
        assertThat(config.getFolders()).isNotNull();
        assertThat(config.getFolders()).hasSize(2);
        
        // And: Should contain the configured paths
        assertThat(config.getFolders().get(0).getFolder().toString()).isEqualTo("target/classes");
        assertThat(config.getFolders().get(1).getFolder().toString()).isEqualTo("build/classes");
    }

    @Test
    void should_load_watch_paths_from_system_properties() throws Exception {
        // Given: System properties for watch configuration
        System.setProperty("bytehot.watch.paths", "target/classes,build/classes");
        System.setProperty("bytehot.watch.recursive", "true");

        // When: Loading configuration using ConfigurationAdapter
        final ConfigurationAdapter adapter = new ConfigurationAdapter();
        final WatchConfiguration config = adapter.loadWatchConfiguration();

        // Then: Should load watch paths correctly with non-null folders
        assertThat(config).isNotNull();
        assertThat(config.getFolders()).isNotNull();
        assertThat(config.getFolders()).hasSize(2);
        
        // And: Should contain the configured paths
        assertThat(config.getFolders().get(0).getFolder().toString()).isEqualTo("target/classes");
        assertThat(config.getFolders().get(1).getFolder().toString()).isEqualTo("build/classes");

        // Cleanup
        System.clearProperty("bytehot.watch.paths");
        System.clearProperty("bytehot.watch.recursive");
    }

    @Test
    void should_prioritize_system_properties_over_yaml_file() throws Exception {
        // Given: A YAML configuration file with one path
        final Path configFile = tempDir.resolve("bytehot.yml");
        final String yamlContent = """
            bytehot:
              watch:
                - path: "target/classes"
                  patterns: ["*.class"]
                  recursive: true
            """;
        Files.writeString(configFile, yamlContent);
        System.setProperty("bhconfig", configFile.toString());

        // And: System properties with different paths
        System.setProperty("bytehot.watch.paths", "different/path");

        // When: Loading configuration using ConfigurationAdapter
        final ConfigurationAdapter adapter = new ConfigurationAdapter();
        final WatchConfiguration config = adapter.loadWatchConfiguration();

        // Then: Should use system properties (higher priority)
        assertThat(config).isNotNull();
        assertThat(config.getFolders()).isNotNull();
        assertThat(config.getFolders()).hasSize(1);
        assertThat(config.getFolders().get(0).getFolder().toString()).isEqualTo("different/path");

        // Cleanup
        System.clearProperty("bytehot.watch.paths");
    }
}