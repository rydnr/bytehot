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
 * Filename: WatchPathConfigured.java
 *
 * Author: Claude Code
 *
 * Class name: WatchPathConfigured
 *
 * Responsibilities:
 *   - Represent the event when watch paths are successfully configured
 *
 * Collaborators:
 *   - WatchConfiguration: The configuration containing watch paths
 *   - ByteHotAttachRequested: The preceding event that requested configuration
 */
package org.acmsl.bytehot.domain.events;

import org.acmsl.bytehot.domain.WatchConfiguration;
import org.acmsl.commons.patterns.DomainResponseEvent;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.List;

/**
 * Event representing successful configuration of watch paths.
 * @author Claude Code
 * @since 2025-06-15
 */
@RequiredArgsConstructor
@EqualsAndHashCode
public class WatchPathConfigured implements DomainResponseEvent<ByteHotAttachRequested> {

    /**
     * The configuration that was loaded and applied.
     */
    @Getter
    private final WatchConfiguration configuration;

    /**
     * The preceding event that requested this configuration.
     */
    @Getter
    private final ByteHotAttachRequested precedingEvent;

    /**
     * Returns the list of configured watch paths.
     * @return The list of watch paths from the configuration.
     */
    public List<Path> getWatchPaths() {
        return configuration.getFolders().stream()
            .map(folder -> folder.getFolder())
            .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public ByteHotAttachRequested getPreceding() {
        return precedingEvent;
    }
}