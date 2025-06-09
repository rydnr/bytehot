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

        WatchConfiguration wc = WatchConfiguration.load(config);

        assertEquals(6000, wc.getPort());
        assertEquals(
            List.of(new FolderWatch(dir1, 1000), new FolderWatch(dir2, 2000)),
            wc.getFolders());
    }

    @Test
    public void folderWatchDetectsChanges() throws Exception {
        Path folder = Files.createTempDirectory("watch");
        Path file = folder.resolve("a.txt");
        Files.writeString(file, "hello");

        FolderWatch watch = new FolderWatch(folder, 100);
        CountDownLatch latch = new CountDownLatch(1);

        Thread t = new Thread(() -> {
            try {
                watch.watch(changed -> {
                    if (changed.equals(file)) {
                        latch.countDown();
                    }
                });
            } catch (IOException e) {
                // ignore for test
            }
        });
        t.setDaemon(true);
        t.start();

        Files.writeString(file, "world", StandardOpenOption.APPEND);
        assertTrue(latch.await(5, TimeUnit.SECONDS));
        t.interrupt();
    }
}
