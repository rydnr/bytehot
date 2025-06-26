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
 * Filename: HotSwapRequested.java
 *
 * Author: Claude Code
 *
 * Class name: HotSwapRequested
 *
 * Responsibilities:
 *   - Represent when a hot-swap operation is initiated
 *
 * Collaborators:
 *   - None
 */
package org.acmsl.bytehot.domain.events;

import org.acmsl.commons.patterns.eventsourcing.AbstractVersionedDomainEvent;
import org.acmsl.commons.patterns.eventsourcing.EventMetadata;
import org.acmsl.commons.patterns.DomainEvent;
import org.acmsl.commons.patterns.DomainResponseEvent;

import java.nio.file.Path;
import java.time.Instant;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Represents when a hot-swap operation is initiated for validated bytecode
 * @author Claude Code
 * @since 2025-06-17
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class HotSwapRequested extends AbstractVersionedDomainEvent implements DomainResponseEvent<ClassFileChanged> {

    /**
     * The path to the .class file being hot-swapped
    
     */
    @Getter
    private final Path classFile;

    /**
     * The fully qualified name of the class being hot-swapped
    
     */
    @Getter
    private final String className;

    /**
     * The current bytecode in the JVM
    
     */
    @Getter
    private final byte[] originalBytecode;

    /**
     * The new bytecode to install
    
     */
    @Getter
    private final byte[] newBytecode;

    /**
     * The reason why hot-swap was requested
    
     */
    @Getter
    private final String requestReason;

    /**
     * The timestamp when hot-swap was requested
    
     */
    @Getter
    private final Instant timestamp;

    /**
     * The original event that triggered this hot-swap request
    
     */
    private final ClassFileChanged preceding;

    /**
     * Creates a new HotSwapRequested event.
     * 
     * @param metadata the event metadata including user context
     * @param classFile the path to the class file
     * @param className the class name
     * @param originalBytecode the original bytecode
     * @param newBytecode the new bytecode
     * @param requestReason the reason for the request
     * @param timestamp when the request was made
     * @param preceding the original event that triggered this
     */
    public HotSwapRequested(
            EventMetadata metadata,
            Path classFile,
            String className,
            byte[] originalBytecode,
            byte[] newBytecode,
            String requestReason,
            Instant timestamp,
            ClassFileChanged preceding) {
        super(metadata);
        this.classFile = classFile;
        this.className = className;
        this.originalBytecode = originalBytecode;
        this.newBytecode = newBytecode;
        this.requestReason = requestReason;
        this.timestamp = timestamp;
        this.preceding = preceding;
    }

    /**
     * Returns the preceding event that triggered this hot-swap request
    
     */
    @Override
    public ClassFileChanged getPreceding() {
        return preceding;
    }

    /**
     * Factory method to create a hot-swap request from a file change.
     * This creates a placeholder bytecode for the hot-swap request.
     * 
     * @param fileChangeEvent the original file change event
     * @param sessionId the monitoring session ID (used as request reason)
     * @return new HotSwapRequested event based on file change
     */
    public static HotSwapRequested fromFileChange(ClassFileChanged fileChangeEvent, String sessionId) {
        // For the demonstration, create placeholder bytecode
        // In a real implementation, this would read the actual bytecode
        byte[] placeholderBytecode = new byte[]{0x01, 0x02, 0x03, 0x04};
        byte[] newBytecode = new byte[]{0x05, 0x06, 0x07, 0x08};
        
        // Create metadata with correlation to the original event and preserve user context
        final EventMetadata metadata = createMetadataWithFullContext(
            "hotswap",
            fileChangeEvent.getClassName() + "-request",
            null,
            0L,
            fileChangeEvent.getMetadata() != null ? fileChangeEvent.getMetadata().getUserId() : "system",
            fileChangeEvent.getEventId(),
            null
        );
        
        return new HotSwapRequested(
            metadata,
            fileChangeEvent.getClassFile(),
            fileChangeEvent.getClassName(),
            placeholderBytecode,
            newBytecode,
            "File change detected in session: " + sessionId,
            Instant.now(),
            fileChangeEvent
        );
    }
}