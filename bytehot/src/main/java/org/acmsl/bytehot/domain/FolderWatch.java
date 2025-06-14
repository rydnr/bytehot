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
 * Filename: FolderWatch.java
 *
 * Author: rydnr
 *
 * Class name: FolderWatch
 *
 * Responsibilities: Watch folders for changes, every certain milliseconds.
 *
 * Collaborators:
 *   - None
 */
package org.acmsl.bytehot.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.function.Consumer;

/**
 * Watches folders for changes, every certain milliseconds.
 * @author <a href="mailto:rydnr@acm-sl.org">rydnr</a>
 * @since 2025-06-07
 */
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class FolderWatch {

    /**
     * The folder to watch.
     * @return such folder.
     */
    @Getter
    private final Path folder;

    /**
     * The interval between checks.
     * @return such value, in milliseconds.
     */
    @Getter
    private final int interval;

    /**
     * Watches the folder for any file changes. The call blocks until the
     * thread is interrupted.
     *
     * @param onChange callback invoked when a file changes
     * @throws IOException          in case of IO errors
     * @throws InterruptedException if the watch thread is interrupted
     */
    public void watch(final Consumer<Path> onChange)
        throws IOException, InterruptedException {
        final WatchService watchService =
            folder.getFileSystem().newWatchService();

        folder.register(watchService,
            StandardWatchEventKinds.ENTRY_CREATE,
            StandardWatchEventKinds.ENTRY_DELETE,
            StandardWatchEventKinds.ENTRY_MODIFY);

        while (!Thread.currentThread().isInterrupted()) {
            final WatchKey key = watchService.take();

            for (final WatchEvent<?> event : key.pollEvents()) {
                final Path changed =
                    folder.resolve((Path) event.context());
                onChange.accept(changed);
            }

            if (!key.reset()) {
                break;
            }
        }
    }
}
