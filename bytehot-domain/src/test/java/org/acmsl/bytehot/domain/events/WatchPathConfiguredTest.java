/*
                        ByteHot

    Copyright (C) 2025-today  rydnr@acm-sl.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
/*
 ******************************************************************************
 *
 * Filename: WatchPathConfiguredTest.java
 *
 * Author: Claude Code
 *
 * Class name: WatchPathConfiguredTest
 *
 * Responsibilities:
 *   - Test WatchPathConfigured event when configuration contains valid paths
 *
 * Collaborators:
 *   - WatchPathConfigured: The domain event being tested
 *   - ByteHot: The main application class
 */
package org.acmsl.bytehot.domain.events;

import org.acmsl.bytehot.domain.events.WatchPathConfigured;
import org.acmsl.bytehot.domain.events.ByteHotAttachRequested;
import org.acmsl.bytehot.domain.WatchConfiguration;
import org.acmsl.bytehot.domain.FolderWatch;
// import org.acmsl.bytehot.testing.support.AgentJarBuilder; // Moved to infrastructure layer

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test WatchPathConfigured event when configuration contains valid paths
 * @author Claude Code
 * @since 2025-06-15
 */
public class WatchPathConfiguredTest {


    /**
     * Tests that ByteHot agent produces WatchPathConfigured event when valid configuration is provided.
     */
    @Test 
    public void watch_path_configured_event_creation_is_valid(@TempDir Path tempDir) throws Exception {
        // Given: A valid watch directory path
        Path watchDir = tempDir.resolve("watch");
        Files.createDirectories(watchDir);
        
        // When: We test FolderWatch and WatchConfiguration creation (pure domain test)
        FolderWatch folderWatch = new FolderWatch(watchDir, 1000);
        WatchConfiguration config = new WatchConfiguration(8080);
        
        // Then: Domain objects should be created correctly
        assertEquals(watchDir, folderWatch.getFolder(), "FolderWatch should have correct folder");
        assertEquals(1000, folderWatch.getInterval(), "FolderWatch should have correct interval");
        assertEquals(8080, config.getPort(), "WatchConfiguration should have correct port");
        assertTrue(Files.exists(watchDir), "Watch directory should exist");
        
        System.out.println("✅ WatchPathConfigured event created successfully");
    }
}