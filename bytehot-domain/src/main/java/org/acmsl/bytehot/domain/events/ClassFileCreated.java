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
 * Filename: ClassFileCreated.java
 *
 * Author: Claude Code
 *
 * Class name: ClassFileCreated
 *
 * Responsibilities:
 *   - Represent when a new .class file appears on disk
 *
 * Collaborators:
 *   - None
 */
package org.acmsl.bytehot.domain.events;

import org.acmsl.commons.patterns.DomainEvent;

import java.nio.file.Path;
import java.time.Instant;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Represents when a new .class file appears on disk
 * @author Claude Code
 * @since 2025-06-16
 */
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class ClassFileCreated implements DomainEvent {

    /**
     * The path to the created .class file
    
     */
    @Getter
    private final Path classFile;

    /**
     * The name of the class (extracted from filename)
    
     */
    @Getter
    private final String className;

    /**
     * The size of the newly created file
    
     */
    @Getter
    private final long fileSize;

    /**
     * The timestamp when the creation was detected
    
     */
    @Getter
    private final Instant timestamp;
}