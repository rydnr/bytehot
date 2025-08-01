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
     */
    @Getter
    private final int port;

    /**
     * The list of folders to watch for changes.
     */
    @Getter
    private List<FolderWatch> folders;

    /**
     * Loads the configuration using the ConfigurationPort.
     * @return a WatchConfiguration object containing the loaded configuration.
     * @throws Exception if an error occurs while loading the configuration.
     */
    public static WatchConfiguration load() throws Exception {
        final ConfigurationPort configPort = Ports.resolve(ConfigurationPort.class);
        return configPort.loadWatchConfiguration();
    }
}
