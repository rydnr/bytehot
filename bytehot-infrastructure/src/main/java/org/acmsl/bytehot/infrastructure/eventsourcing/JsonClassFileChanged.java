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
 * Filename: JsonClassFileChanged.java
 *
 * Author: Claude Code
 *
 * Class name: JsonClassFileChanged
 *
 * Responsibilities:
 *   - JSON-serializable DTO for ClassFileChanged domain events
 *   - Bridge between domain events and JSON persistence
 *   - Handle Jackson serialization requirements in infrastructure layer
 *
 * Collaborators:
 *   - ClassFileChanged: Domain event being represented
 *   - EventSerializationSupport: Uses this DTO for JSON operations
 */
package org.acmsl.bytehot.infrastructure.eventsourcing;

import org.acmsl.bytehot.domain.events.ClassFileChanged;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

/**
 * JSON-serializable DTO for ClassFileChanged domain events
 * @author Claude Code
 * @since 2025-06-17
 */
public class JsonClassFileChanged {

    @JsonProperty("eventId")
    public final String eventId;
    
    @JsonProperty("aggregateType")
    public final String aggregateType;
    
    @JsonProperty("aggregateId")
    public final String aggregateId;
    
    @JsonProperty("aggregateVersion")
    public final long aggregateVersion;
    
    @JsonProperty("timestamp")
    public final Instant timestamp;
    
    @JsonProperty("previousEventId")
    public final String previousEventId;
    
    @JsonProperty("schemaVersion")
    public final int schemaVersion;
    
    @JsonProperty("userId")
    public final String userId;
    
    @JsonProperty("correlationId")
    public final String correlationId;
    
    @JsonProperty("classFile")
    public final String classFile; // Store as String for JSON compatibility
    
    @JsonProperty("className")
    public final String className;
    
    @JsonProperty("fileSize")
    public final long fileSize;
    
    @JsonProperty("detectionTimestamp")
    public final Instant detectionTimestamp;

    /**
     * JSON constructor for deserialization
     */
    @JsonCreator
    public JsonClassFileChanged(
        @JsonProperty("eventId") String eventId,
        @JsonProperty("aggregateType") String aggregateType,
        @JsonProperty("aggregateId") String aggregateId,
        @JsonProperty("aggregateVersion") long aggregateVersion,
        @JsonProperty("timestamp") Instant timestamp,
        @JsonProperty("previousEventId") String previousEventId,
        @JsonProperty("schemaVersion") int schemaVersion,
        @JsonProperty("userId") String userId,
        @JsonProperty("correlationId") String correlationId,
        @JsonProperty("classFile") String classFile,
        @JsonProperty("className") String className,
        @JsonProperty("fileSize") long fileSize,
        @JsonProperty("detectionTimestamp") Instant detectionTimestamp
    ) {
        this.eventId = eventId;
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.aggregateVersion = aggregateVersion;
        this.timestamp = timestamp;
        this.previousEventId = previousEventId;
        this.schemaVersion = schemaVersion;
        this.userId = userId;
        this.correlationId = correlationId;
        this.classFile = classFile;
        this.className = className;
        this.fileSize = fileSize;
        this.detectionTimestamp = detectionTimestamp;
    }

    /**
     * Converts a domain ClassFileChanged event to a JSON DTO
     */
    public static JsonClassFileChanged fromDomain(ClassFileChanged domainEvent) {
        return new JsonClassFileChanged(
            domainEvent.getEventId(),
            domainEvent.getAggregateType(),
            domainEvent.getAggregateId(),
            domainEvent.getAggregateVersion(),
            domainEvent.getTimestamp(),
            domainEvent.getPreviousEventId(),
            domainEvent.getSchemaVersion(),
            domainEvent.getUserId(),
            domainEvent.getCorrelationId(),
            domainEvent.getClassFile().toString(),
            domainEvent.getClassName(),
            domainEvent.getFileSize(),
            domainEvent.getDetectionTimestamp()
        );
    }

    /**
     * Converts this JSON DTO back to a domain ClassFileChanged event
     */
    public ClassFileChanged toDomain() {
        return new ClassFileChanged(
            eventId,
            aggregateType,
            aggregateId,
            aggregateVersion,
            timestamp,
            previousEventId,
            schemaVersion,
            userId,
            correlationId,
            Paths.get(classFile),
            className,
            fileSize,
            detectionTimestamp
        );
    }
}