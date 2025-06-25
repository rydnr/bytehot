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
 * Filename: ClassRedefinitionSucceeded.java
 *
 * Author: Claude Code
 *
 * Class name: ClassRedefinitionSucceeded
 *
 * Responsibilities:
 *   - Represent when JVM successfully redefines a class
 *
 * Collaborators:
 *   - None
 */
package org.acmsl.bytehot.domain.events;

import org.acmsl.commons.patterns.DomainEvent;

import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Represents when JVM successfully redefines a class
 * @author Claude Code
 * @since 2025-06-17
 */
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class ClassRedefinitionSucceeded implements DomainEvent {

    /**
     * The name of the redefined class
    
     */
    @Getter
    private final String className;

    /**
     * The source file that was hot-swapped
    
     */
    @Getter
    private final Path classFile;

    /**
     * The number of existing instances that were updated
    
     */
    @Getter
    private final int affectedInstances;

    /**
     * Technical details about the redefinition operation
    
     */
    @Getter
    private final String redefinitionDetails;

    /**
     * The time taken for the redefinition operation
    
     */
    @Getter
    private final Duration duration;

    /**
     * The timestamp when redefinition completed
    
     */
    @Getter
    private final Instant timestamp;
}