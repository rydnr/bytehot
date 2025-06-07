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
 * Filename: WatchConfiguration.java
 *
 * Author: rydnr
 *
 * Class name: WatchConfiguration
 *
 * Responsibilities: Manage the configuration for ByteHot.
 *
 * Collaborators:
 *   - None
 */
package org.acmsl.bytehot.domain;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Manages the configuration for ByteHot.
 * @author <a href="mailto:rydnr@acm-sl.org">rydnr</a>
 * @since 2025-06-07
 */
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class WatchConfiguration {

    /**
     * The port on which ByteHot will listen for requests.
     * @return the port number.
     */
    @Getter
    private final int port;

    /**
     * The list of folders to watch for changes.
     * @return the list of FolderWatch objects.
     */
    @Getter
    private List<FolderWatch> folders;

    /**
     * Loads the configuration from a YAML file.
     * @param configFile the path to the configuration file.
     * @return a WatchConfiguration object containing the loaded configuration.
     * @throws IOException if an error occurs while reading the file.
     */
    public static WatchConfiguration load(final Path configFile)
        throws IOException {
        // TODO: load a yaml with this format
        /*
        port: 6000
        folders:
          - path: /tmp/foo
            interval: 1000
          - path: /tmp/bar
            interval: 2000
         */
        return null;
    }
}
