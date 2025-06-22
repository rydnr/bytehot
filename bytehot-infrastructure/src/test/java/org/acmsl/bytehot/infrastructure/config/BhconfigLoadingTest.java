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
 * Filename: BhconfigLoadingTest.java
 *
 * Author: Claude Code
 *
 * Class name: BhconfigLoadingTest
 *
 * Responsibilities: Test bhconfig parameter specifically
 *
 * Collaborators:
 *   - ConfigurationAdapter: Configuration loading implementation
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
 * Test specifically for the bhconfig parameter functionality.
 * @author Claude Code
 * @since 2025-06-21
 */
public class BhconfigLoadingTest {

    @TempDir
    protected Path tempDir;

    protected String originalBhconfig;

    @BeforeEach
    void setUp() {
        originalBhconfig = System.getProperty("bhconfig");
    }

    @AfterEach
    void tearDown() {
        if (originalBhconfig != null) {
            System.setProperty("bhconfig", originalBhconfig);
        } else {
            System.clearProperty("bhconfig");
        }
    }

    @Test
    void should_load_configuration_from_bhconfig_file_path() throws Exception {
        // Given: A YAML file in a specific location
        final Path configFile = tempDir.resolve("custom-bytehot.yml");
        final String yamlContent = """
            bytehot:
              watch:
                - path: "/tmp/bytehot-test/target/classes"
                  patterns: ["*.class"]
                  recursive: true
            """;
        Files.writeString(configFile, yamlContent);

        // And: bhconfig system property points to that file
        System.setProperty("bhconfig", configFile.toString());

        // When: Loading configuration via ConfigurationAdapter
        final ConfigurationAdapter adapter = new ConfigurationAdapter();
        final WatchConfiguration config = adapter.loadWatchConfiguration();

        // Then: Should successfully load the configuration
        assertThat(config).isNotNull();
        assertThat(config.getFolders()).isNotNull();
        assertThat(config.getFolders()).hasSize(1);
        assertThat(config.getFolders().get(0).getFolder().toString()).isEqualTo("/tmp/bytehot-test/target/classes");
    }
}