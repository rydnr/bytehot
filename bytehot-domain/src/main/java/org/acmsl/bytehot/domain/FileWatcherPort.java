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
 * Filename: FileWatcherPort.java
 *
 * Author: Claude Code
 *
 * Class name: FileWatcherPort
 *
 * Responsibilities:
 *   - Define interface for file system watching operations
 *   - Abstract file system implementation from domain logic
 *   - Enable different watching strategies and platforms
 *
 * Collaborators:
 *   - FolderWatch: Uses this port to monitor directories
 *   - FileWatcherAdapter: Infrastructure implementation
 */
package org.acmsl.bytehot.domain;

import org.acmsl.commons.patterns.Port;

import java.nio.file.Path;
import java.util.List;

/**
 * Port interface for file system watching operations
 * @author Claude Code
 * @since 2025-06-17
 */
public interface FileWatcherPort
    extends Port {

    /**
     * Starts watching a directory for file changes
     * @param path the directory path to watch
     * @param patterns file patterns to include
     * @param recursive whether to watch subdirectories
     * @return watch identifier for later management
     * @throws Exception if watching cannot be started
     */
    String startWatching(final Path path, final List<String> patterns, final boolean recursive) throws Exception;

    /**
     * Stops watching a previously registered directory
     * @param watchId the watch identifier returned by startWatching
     * @throws Exception if stopping fails
     */
    void stopWatching(final String watchId) throws Exception;

    /**
     * Checks if a directory is currently being watched
     * @param path the directory path to check
     * @return true if path is being watched
     */
    boolean isWatching(final Path path);

    /**
     * Returns all currently watched paths
     * @return list of paths being watched
     */
    List<Path> getWatchedPaths();

    /**
     * Checks if the file watcher is operational
     * @return true if file watching is available
     */
    boolean isWatcherAvailable();
}