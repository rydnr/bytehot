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

import java.nio.file.Path;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

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
     */
    @Getter
    private final Path folder;

    /**
     * The interval between checks, in milliseconds.
     */
    @Getter
    private final int interval;

    /**
     * Starts watching the folder using the FileWatcherPort.
     * @param patterns file patterns to watch
     * @param recursive whether to watch recursively
     * @return watch identifier for management
     * @throws Exception if watching fails
     */
    public String startWatching(final java.util.List<String> patterns, final boolean recursive) throws Exception {
        final FileWatcherPort watcherPort = Ports.resolve(FileWatcherPort.class);
        return watcherPort.startWatching(folder, patterns, recursive);
    }

    /**
     * Stops watching using the given watch identifier.
     * @param watchId the identifier returned by startWatching
     * @throws Exception if stopping fails
     */
    public void stopWatching(final String watchId) throws Exception {
        final FileWatcherPort watcherPort = Ports.resolve(FileWatcherPort.class);
        watcherPort.stopWatching(watchId);
    }

    /**
     * Checks if this folder is currently being watched.
     * @return true if folder is being watched
     */
    public boolean isWatching() {
        try {
            final FileWatcherPort watcherPort = Ports.resolve(FileWatcherPort.class);
            return watcherPort.isWatching(folder);
        } catch (final Exception e) {
            return false;
        }
    }
}
