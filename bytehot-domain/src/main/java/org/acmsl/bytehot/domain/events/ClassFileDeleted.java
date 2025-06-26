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
 * Filename: ClassFileDeleted.java
 *
 * Author: Claude Code
 *
 * Class name: ClassFileDeleted
 *
 * Responsibilities:
 *   - Represent when a .class file is removed from disk
 *
 * Collaborators:
 *   - None
 */
package org.acmsl.bytehot.domain.events;

import org.acmsl.commons.patterns.eventsourcing.AbstractVersionedDomainEvent;
import org.acmsl.commons.patterns.eventsourcing.EventMetadata;

import java.nio.file.Path;
import java.time.Instant;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Represents when a .class file is removed from disk
 * @author Claude Code
 * @since 2025-06-16
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ClassFileDeleted extends AbstractVersionedDomainEvent {

    /**
     * The path to the deleted .class file
    
     */
    @Getter
    private final Path classFile;

    /**
     * The name of the class (extracted from filename)
    
     */
    @Getter
    private final String className;

    /**
     * When the deletion was detected
    
     */
    @Getter
    private final Instant detectionTimestamp;

    /**
     * Constructor with all EventSourcing metadata
     * @param eventId unique identifier for this event
     * @param aggregateType type of the aggregate
     * @param aggregateId unique identifier of the aggregate
     * @param aggregateVersion version of the aggregate
     * @param timestamp when the event occurred
     * @param previousEventId ID of the previous event in the stream
     * @param schemaVersion version of the event schema
     * @param userId ID of the user associated with this event
     * @param correlationId correlation identifier
     * @param causationId causation identifier
     * @param streamPosition position in the event stream
     * @param classFile the path to the deleted .class file
     * @param className the name of the class (extracted from filename)
     * @param detectionTimestamp the timestamp when the deletion was detected
     */
    public ClassFileDeleted(
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
        Instant detectionTimestamp
    ) {
        super(eventId, aggregateType, aggregateId, aggregateVersion, timestamp, 
              previousEventId, schemaVersion, userId, correlationId, causationId, streamPosition);
        this.classFile = classFile;
        this.className = className;
        this.detectionTimestamp = detectionTimestamp;
    }

    /**
     * Constructor using EventMetadata
     * @param metadata event metadata containing versioning and context information
     * @param classFile the path to the deleted .class file
     * @param className the name of the class (extracted from filename)
     * @param detectionTimestamp the timestamp when the deletion was detected
     */
    public ClassFileDeleted(
        EventMetadata metadata,
        Path classFile,
        String className,
        Instant detectionTimestamp
    ) {
        super(metadata);
        this.classFile = classFile;
        this.className = className;
        this.detectionTimestamp = detectionTimestamp;
    }

    /**
     * Factory method to create a ClassFileDeleted event for a new file monitoring session
     * @param classFile the path to the deleted .class file
     * @param className the name of the class (extracted from filename)
     * @param detectionTimestamp the timestamp when the deletion was detected
     * @return new ClassFileDeleted event for new session
     */
    public static ClassFileDeleted forNewSession(
        Path classFile,
        String className,
        Instant detectionTimestamp
    ) {
        EventMetadata metadata = createMetadataForNewAggregate(
            "filewatch", 
            classFile.toString()
        );
        
        return new ClassFileDeleted(
            metadata,
            classFile,
            className,
            detectionTimestamp
        );
    }
}