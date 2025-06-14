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
 * Author: rydnr
 *
 * Class name: WatchConfigurationTest
 *
 * Responsibilities: Verify folder watch detects changes.
 *
 * Collaborators:
 *   - FolderWatch: watch implementation
 */
package org.acmsl.bytehot.domain;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Verify folder watch detects changes.
 * @author <a href="mailto:rydnr@acm-sl.org">rydnr</a>
 * @since 2025-06-07
 */
@RunWith(JUnit4.class)
public class WatchConfigurationTest {

    /**
     * Checks FolderWatch reports file modifications.
     * @throws Exception when something goes wrong
     */
    @Test
    public void folderWatchDetectsChanges() throws Exception {
        Path dir = Files.createTempDirectory("bytehot-test");
        Path file = Files.createFile(dir.resolve("sample.txt"));
        FolderWatch watch = new FolderWatch(dir, 100);
        CountDownLatch latch = new CountDownLatch(1);
        Thread watcher = new Thread(() -> {
            try {
                watch.watch(p -> latch.countDown());
            } catch (Exception ignored) {
                // ignore
            }
        });
        watcher.start();
        Thread.sleep(200);
        Files.writeString(file, "data");
        boolean modified = latch.await(5, TimeUnit.SECONDS);
        watcher.interrupt();
        Assert.assertTrue(modified);
    }
}
