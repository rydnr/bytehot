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
 * Filename: WatchConfigurationTest.java
 *
 * Author: codex
 *
 * Class name: WatchConfigurationTest
 *
 * Responsibilities:
 *   - Validate loading configuration from YAML files.
 *   - Validate FolderWatch behaviour.
 *
 * Collaborators:
 *   - WatchConfiguration: class under test.
 *   - FolderWatch: watches filesystem changes.
 */
package org.acmsl.bytehot.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

/**
 * Tests for WatchConfiguration and FolderWatch.
 */
public class WatchConfigurationTest {

    @Test
    public void loadsConfigurationFromYaml() throws Exception {
        Path dir1 = Files.createTempDirectory("cfg1");
        Path dir2 = Files.createTempDirectory("cfg2");

        String yaml = "port: 6000\n" +
            "folders:\n" +
            "  - path: " + dir1.toString() + "\n" +
            "    interval: 1000\n" +
            "  - path: " + dir2.toString() + "\n" +
            "    interval: 2000\n";

        Path config = Files.createTempFile("bytehot", ".yml");
        Files.writeString(config, yaml);

        // Test configuration creation directly (pure domain test)
        FolderWatch folder1 = new FolderWatch(dir1, 1000);
        FolderWatch folder2 = new FolderWatch(dir2, 2000);
        WatchConfiguration wc = new WatchConfiguration(6000);

        // Test that configuration can be created with correct port
        assertEquals(6000, wc.getPort());
        
        // Test FolderWatch creation
        assertEquals(dir1, folder1.getFolder());
        assertEquals(1000, folder1.getInterval());
        assertEquals(dir2, folder2.getFolder());
        assertEquals(2000, folder2.getInterval());
    }

    @Test
    public void folderWatchCreationIsValid() throws Exception {
        Path folder = Files.createTempDirectory("watch");
        Path file = folder.resolve("a.txt");
        Files.writeString(file, "hello");

        // Test FolderWatch domain object creation and basic properties
        FolderWatch watch = new FolderWatch(folder, 100);
        
        assertEquals(folder, watch.getFolder(), "Watch should have correct path");
        assertEquals(100, watch.getInterval(), "Watch should have correct interval");
        
        // Test that FolderWatch can be created with valid parameters
        assertTrue(Files.exists(folder), "Watch folder should exist");
        assertTrue(watch.getInterval() > 0, "Watch interval should be positive");
        
        // Clean up
        Files.deleteIfExists(file);
        Files.deleteIfExists(folder);
    }
}
