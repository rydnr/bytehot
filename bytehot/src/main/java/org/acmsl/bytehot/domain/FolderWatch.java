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
}
