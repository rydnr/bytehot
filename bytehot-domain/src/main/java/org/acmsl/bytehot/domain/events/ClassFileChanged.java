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
 * Filename: ClassFileChanged.java
 *
 * Author: Claude Code
 *
 * Class name: ClassFileChanged
 *
 * Responsibilities:
 *   - Represent when a .class file is modified on disk
 *
 * Collaborators:
 *   - None
 */
package org.acmsl.bytehot.domain.events;

import org.acmsl.commons.patterns.eventsourcing.EventMetadata;
import org.acmsl.commons.patterns.eventsourcing.AbstractVersionedDomainEvent;

import java.nio.file.Path;
import java.time.Instant;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Represents when a .class file is modified on disk
 * @author Claude Code
 * @since 2025-06-16
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ClassFileChanged extends AbstractVersionedDomainEvent {

    /**
     * The path to the modified .class file
     */
    @Getter
    private final Path classFile;

    /**
     * The name of the class (extracted from filename)
     */
    @Getter
    private final String className;

    /**
     * The size of the file after modification
     */
    @Getter
    private final long fileSize;

    /**
     * The timestamp when the change was detected (domain-specific, different from event timestamp)
     */
    @Getter
    private final Instant detectionTimestamp;

    /**
     * Constructor with all parameters including EventSourcing metadata
     */
    public ClassFileChanged(
        String eventId,
        String aggregateType,
        String aggregateId,
        long aggregateVersion,
        Instant timestamp,
        String previousEventId,
        int schemaVersion,
        String userId,
        String correlationId,
        String causationId,
        Long streamPosition,
        Path classFile,
        String className,
        long fileSize,
        Instant detectionTimestamp
    ) {
        super(eventId, aggregateType, aggregateId, aggregateVersion, timestamp, 
              previousEventId, schemaVersion, userId, correlationId, causationId, streamPosition);
        this.classFile = classFile;
        this.className = className;
        this.fileSize = fileSize;
        this.detectionTimestamp = detectionTimestamp;
    }

    /**
     * Constructor using EventMetadata
     */
    public ClassFileChanged(
        EventMetadata metadata,
        Path classFile,
        String className,
        long fileSize,
        Instant detectionTimestamp
    ) {
        super(metadata);
        this.classFile = classFile;
        this.className = className;
        this.fileSize = fileSize;
        this.detectionTimestamp = detectionTimestamp;
    }

    /**
     * Factory method to create a ClassFileChanged event for a new file monitoring session
     */
    public static ClassFileChanged forNewSession(
        Path classFile,
        String className,
        long fileSize,
        Instant detectionTimestamp
    ) {
        EventMetadata metadata = createMetadataForNewAggregate(
            "filewatch", 
            classFile.toString()
        );
        
        return new ClassFileChanged(
            metadata,
            classFile,
            className,
            fileSize,
            detectionTimestamp
        );
    }

    /**
     * Factory method to create a ClassFileChanged event for an existing file monitoring session
     */
    public static ClassFileChanged forExistingSession(
        Path classFile,
        String className,
        long fileSize,
        Instant detectionTimestamp,
        String previousEventId,
        long currentVersion
    ) {
        EventMetadata metadata = EventMetadata.forExistingAggregate(
            "filewatch",
            classFile.toString(),
            previousEventId,
            currentVersion,
            detectionTimestamp
        );
        
        return new ClassFileChanged(
            metadata,
            classFile,
            className,
            fileSize,
            detectionTimestamp
        );
    }

    /**
     * Factory method to create a ClassFileChanged event with user context
     */
    public static ClassFileChanged withUser(
        Path classFile,
        String className,
        long fileSize,
        Instant detectionTimestamp,
        String userId,
        String previousEventId,
        long currentVersion
    ) {
        EventMetadata metadata = createMetadataWithUser(
            "filewatch",
            classFile.toString(),
            previousEventId,
            currentVersion,
            userId
        );
        
        return new ClassFileChanged(
            metadata,
            classFile,
            className,
            fileSize,
            detectionTimestamp
        );
    }
}