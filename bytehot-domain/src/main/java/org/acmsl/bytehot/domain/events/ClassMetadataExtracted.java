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
 * Filename: ClassMetadataExtracted.java
 *
 * Author: Claude Code
 *
 * Class name: ClassMetadataExtracted
 *
 * Responsibilities:
 *   - Represent when class information is successfully parsed from bytecode
 *
 * Collaborators:
 *   - None
 */
package org.acmsl.bytehot.domain.events;

import org.acmsl.commons.patterns.DomainEvent;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Represents when class information is successfully parsed from bytecode
 * @author Claude Code
 * @since 2025-06-16
 */
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class ClassMetadataExtracted implements DomainEvent {

    /**
     * The path to the analyzed .class file
    
     */
    @Getter
    private final Path classFile;

    /**
     * The fully qualified name of the class
    
     */
    @Getter
    private final String className;

    /**
     * The fully qualified name of the superclass
    
     */
    @Getter
    private final String superClassName;

    /**
     * The list of implemented interfaces
    
     */
    @Getter
    private final List<String> interfaces;

    /**
     * The list of declared fields
    
     */
    @Getter
    private final List<String> fields;

    /**
     * The list of declared methods
    
     */
    @Getter
    private final List<String> methods;

    /**
     * The timestamp when the metadata was extracted
    
     */
    @Getter
    private final Instant timestamp;
}