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
 * Filename: ClassFileProcessed.java
 *
 * Author: Claude Code
 *
 * Class name: ClassFileProcessed
 *
 * Responsibilities:
 *   - Represent successful processing of a class file change
 *   - Carry information about the processing result
 *   - Enable tracking of file processing history
 *
 * Collaborators:
 *   - ClassFileChanged: The original event that triggered processing
 *   - FileMonitoringSession: Aggregate that produces this event
 */
package org.acmsl.bytehot.domain.events;

import org.acmsl.bytehot.domain.ProcessingResult;
import org.acmsl.commons.patterns.DomainResponseEvent;

import java.nio.file.Path;
import java.time.Instant;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Domain event indicating that a class file change has been successfully processed.
 * This event is produced by the FileMonitoringSession aggregate when it successfully
 * handles a ClassFileChanged event.
 * 
 * @author Claude Code
 * @since 2025-06-18
 */
@EqualsAndHashCode
@ToString
public class ClassFileProcessed implements DomainResponseEvent<ClassFileChanged> {

    /**
     * The path to the processed class file
     */
    @Getter
    private final Path classFile;

    /**
     * The name of the processed class
     */
    @Getter
    private final String className;

    /**
     * When the processing completed
     */
    @Getter
    private final Instant processedAt;

    /**
     * Result of the processing
     */
    @Getter
    private final ProcessingResult result;

    /**
     * Optional message about the processing
     */
    @Getter
    private final String message;

    /**
     * The original event that triggered this processing
     */
    @Getter
    private final ClassFileChanged preceding;

    /**
     * Creates a new ClassFileProcessed event.
     * 
     * @param originalEvent the original ClassFileChanged event
     * @param classFile the path to the processed file
     * @param className the name of the processed class
     * @param result the processing result
     * @param message optional processing message
     */
    public ClassFileProcessed(
            ClassFileChanged originalEvent,
            Path classFile,
            String className,
            ProcessingResult result,
            String message) {
        this.preceding = originalEvent;
        this.classFile = classFile;
        this.className = className;
        this.processedAt = Instant.now();
        this.result = result;
        this.message = message;
    }

    /**
     * Factory method to create a successful processing event from a file change.
     * 
     * @param fileChangeEvent the original file change event
     * @return a new ClassFileProcessed event indicating success
     */
    public static ClassFileProcessed fromFileChange(ClassFileChanged fileChangeEvent) {
        return new ClassFileProcessed(
            fileChangeEvent,
            fileChangeEvent.getClassFile(),
            fileChangeEvent.getClassName(),
            ProcessingResult.SUCCESS,
            "File change processed successfully"
        );
    }

    /**
     * Factory method to create a processing event with custom result.
     * 
     * @param fileChangeEvent the original file change event
     * @param result the processing result
     * @param message the processing message
     * @return a new ClassFileProcessed event
     */
    public static ClassFileProcessed withResult(
            ClassFileChanged fileChangeEvent,
            ProcessingResult result,
            String message) {
        return new ClassFileProcessed(
            fileChangeEvent,
            fileChangeEvent.getClassFile(),
            fileChangeEvent.getClassName(),
            result,
            message
        );
    }

}