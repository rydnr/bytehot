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

import org.acmsl.commons.patterns.eventsourcing.AbstractVersionedDomainEvent;
import org.acmsl.commons.patterns.eventsourcing.EventMetadata;

import java.nio.file.Path;
import java.time.Instant;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Represents when bytecode fails validation checks for hot-swap compatibility
 * @author Claude Code
 * @since 2025-06-16
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BytecodeRejected extends AbstractVersionedDomainEvent {

    /**
     * The path to the rejected .class file
     */
    @Getter
    private final Path classFile;

    /**
     * The name of the rejected class
     */
    @Getter
    private final String className;

    /**
     * Whether the bytecode is valid for hot-swap operations (always false for rejected bytecode)
     */
    @Getter
    private final boolean validForHotSwap;

    /**
     * The reason why the bytecode was rejected
     */
    @Getter
    private final String rejectionReason;

    /**
     * The timestamp when the rejection occurred
     */
    @Getter
    private final Instant detectionTimestamp;

    /**
     * Constructor with all EventSourcing metadata
     * @param eventId the unique event identifier
     * @param aggregateType the type of aggregate this event belongs to
     * @param aggregateId the unique identifier of the aggregate
     * @param aggregateVersion the version of the aggregate after this event
     * @param timestamp the timestamp when this event occurred
     * @param previousEventId the ID of the previous event in the aggregate stream
     * @param schemaVersion the version of the event schema
     * @param userId the identifier of the user who triggered this event
     * @param correlationId the correlation identifier for tracking related events
     * @param causationId the causation identifier for tracking command origin
     * @param streamPosition the stream position in the event store
     * @param classFile the path to the rejected .class file
     * @param className the name of the rejected class
     * @param validForHotSwap whether the bytecode is valid for hot-swap (always false)
     * @param rejectionReason the reason why the bytecode was rejected
     * @param detectionTimestamp the timestamp when the rejection was detected
     */
    public BytecodeRejected(
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
        boolean validForHotSwap,
        String rejectionReason,
        Instant detectionTimestamp
    ) {
        super(eventId, aggregateType, aggregateId, aggregateVersion, timestamp, 
              previousEventId, schemaVersion, userId, correlationId, causationId, streamPosition);
        this.classFile = classFile;
        this.className = className;
        this.validForHotSwap = validForHotSwap;
        this.rejectionReason = rejectionReason;
        this.detectionTimestamp = detectionTimestamp;
    }

    /**
     * Constructor using EventMetadata
     * @param metadata the event metadata containing EventSourcing information
     * @param classFile the path to the rejected .class file
     * @param className the name of the rejected class
     * @param validForHotSwap whether the bytecode is valid for hot-swap (always false)
     * @param rejectionReason the reason why the bytecode was rejected
     * @param detectionTimestamp the timestamp when the rejection was detected
     */
    public BytecodeRejected(
        EventMetadata metadata,
        Path classFile,
        String className,
        boolean validForHotSwap,
        String rejectionReason,
        Instant detectionTimestamp
    ) {
        super(metadata);
        this.classFile = classFile;
        this.className = className;
        this.validForHotSwap = validForHotSwap;
        this.rejectionReason = rejectionReason;
        this.detectionTimestamp = detectionTimestamp;
    }

    /**
     * Factory method to create a BytecodeRejected event for a new validation session
     * @param classFile the path to the rejected .class file
     * @param className the name of the rejected class
     * @param rejectionReason the reason why the bytecode was rejected
     * @param detectionTimestamp the timestamp when the rejection was detected
     * @return a new BytecodeRejected event instance
     */
    public static BytecodeRejected forNewSession(
        Path classFile,
        String className,
        String rejectionReason,
        Instant detectionTimestamp
    ) {
        EventMetadata metadata = createMetadataForNewAggregate(
            "validation", 
            classFile.toString()
        );
        
        return new BytecodeRejected(
            metadata,
            classFile,
            className,
            false, // Always false for rejected bytecode
            rejectionReason,
            detectionTimestamp
        );
    }
}