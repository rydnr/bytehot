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
 * Filename: BytecodeRejected.java
 *
 * Author: Claude Code
 *
 * Class name: BytecodeRejected
 *
 * Responsibilities:
 *   - Represent when bytecode fails validation checks for hot-swap compatibility
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
 * Represents when bytecode fails validation checks for hot-swap compatibility
 * @author Claude Code
 * @since 2025-06-16
 */
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class BytecodeRejected implements DomainEvent {

    /**
     * The path to the rejected .class file
     * @return the class file path
     */
    @Getter
    private final Path classFile;

    /**
     * The name of the rejected class
     * @return the class name
     */
    @Getter
    private final String className;

    /**
     * Whether the bytecode is valid for hot-swap operations (always false for rejected bytecode)
     * @return false for rejected bytecode
     */
    @Getter
    private final boolean validForHotSwap;

    /**
     * The reason why the bytecode was rejected
     * @return rejection reason
     */
    @Getter
    private final String rejectionReason;

    /**
     * The timestamp when the rejection occurred
     * @return the timestamp
     */
    @Getter
    private final Instant timestamp;
}