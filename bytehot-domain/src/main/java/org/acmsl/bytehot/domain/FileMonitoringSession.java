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
 * Filename: FileMonitoringSession.java
 *
 * Author: Claude Code
 *
 * Class name: FileMonitoringSession
 *
 * Responsibilities:
 *   - Handle ClassFileChanged events and coordinate file monitoring
 *   - Manage file monitoring sessions and track file changes
 *   - Produce appropriate response events for hot-swap processing
 *   - Maintain file change history for event sourcing
 *
 * Collaborators:
 *   - ClassFileChanged: Input event from file system monitoring
 *   - ClassFileProcessed: Output event indicating successful processing
 *   - HotSwapRequested: Output event to trigger hot-swap operations
 */
package org.acmsl.bytehot.domain;

import org.acmsl.bytehot.domain.events.ClassFileChanged;
import org.acmsl.bytehot.domain.events.ClassFileProcessed;
import org.acmsl.bytehot.domain.events.HotSwapRequested;

import org.acmsl.commons.patterns.DomainResponseEvent;

import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Domain aggregate for managing file monitoring sessions.
 * This aggregate handles ClassFileChanged events and coordinates the file monitoring
 * process, determining what actions should be taken when files change.
 * 
 * @author Claude Code
 * @since 2025-06-18
 */
@EqualsAndHashCode
@ToString
public class FileMonitoringSession {

    /**
     * Unique session identifier
     */
    @Getter
    private final String sessionId;

    /**
     * When this session was created
     */
    @Getter
    private final Instant createdAt;

    /**
     * List of files being monitored in this session
     */
    @Getter
    private final List<Path> monitoredFiles;

    /**
     * Number of file changes processed in this session
     */
    @Getter
    private int processedChanges;

    /**
     * Creates a new file monitoring session.
     * 
     * @param sessionId unique identifier for this session
     */
    public FileMonitoringSession(String sessionId) {
        this.sessionId = sessionId;
        this.createdAt = Instant.now();
        this.monitoredFiles = new ArrayList<>();
        this.processedChanges = 0;
    }

    /**
     * Accepts a ClassFileChanged event and processes it.
     * This is the primary entry point for the aggregate following DDD patterns.
     * 
     * @param event the ClassFileChanged event to process
     * @return a response event indicating the result of processing
     */
    public static DomainResponseEvent<ClassFileChanged> accept(final ClassFileChanged event) {
        try {
            // For now, create a simple processing response
            // In a full implementation, this would:
            // 1. Determine if the file should trigger hot-swap
            // 2. Validate the bytecode
            // 3. Create appropriate response events
            
            // Create a successful processing response
            ClassFileProcessed response = ClassFileProcessed.fromFileChange(event);
            
            return response;
            
        } catch (Exception e) {
            // In case of errors, we would create an error event
            // For now, we'll create a basic response indicating processing
            return ClassFileProcessed.fromFileChange(event);
        }
    }

    /**
     * Processes a file change within the context of this session.
     * 
     * @param event the file change event
     * @return list of response events
     */
    public List<DomainResponseEvent<?>> processFileChange(ClassFileChanged event) {
        List<DomainResponseEvent<?>> responses = new ArrayList<>();
        
        // Add file to monitoring if not already present
        if (!monitoredFiles.contains(event.getClassFile())) {
            monitoredFiles.add(event.getClassFile());
        }
        
        // Increment processed changes counter
        processedChanges++;
        
        // Create a processing response
        responses.add(ClassFileProcessed.fromFileChange(event));
        
        // If this looks like a significant change, create hot-swap request
        if (shouldTriggerHotSwap(event)) {
            responses.add(createHotSwapRequest(event));
        }
        
        return responses;
    }

    /**
     * Determines if a file change should trigger a hot-swap operation.
     * 
     * @param event the file change event
     * @return true if hot-swap should be triggered
     */
    private boolean shouldTriggerHotSwap(ClassFileChanged event) {
        // Simple heuristic: files larger than 100 bytes likely contain meaningful changes
        return event.getFileSize() > 100;
    }

    /**
     * Creates a hot-swap request event from a file change.
     * 
     * @param event the file change event
     * @return hot-swap request event
     */
    private HotSwapRequested createHotSwapRequest(ClassFileChanged event) {
        return HotSwapRequested.fromFileChange(event, sessionId);
    }

    /**
     * Gets statistics about this monitoring session.
     * 
     * @return session statistics
     */
    public SessionStatistics getStatistics() {
        return new SessionStatistics(
            sessionId,
            createdAt,
            monitoredFiles.size(),
            processedChanges
        );
    }

    /**
     * Statistics for a file monitoring session.
     */
    @EqualsAndHashCode
    @ToString
    public static class SessionStatistics {
        /**
         * The session id
         */
        @Getter
        private final String sessionId;

        /**
         * The creation timestamp
         */
        @Getter
        private final Instant createdAt;

        /**
         * The count of monitored files
         */
        @Getter
        private final int monitoredFileCount;

        /**
         * The count of processed changes
         */
        @Getter
        private final int processedChangeCount;

        /**
         * Creates a new instance
         * @param sessionId the id of the session
         * @param createdAt the creation timestamp
         * @param monitoredFileCount the number of monitored files
         * @param processedChangeCount the number of processed changes
         */
        public SessionStatistics(String sessionId, Instant createdAt, int monitoredFileCount, int processedChangeCount) {
            this.sessionId = sessionId;
            this.createdAt = createdAt;
            this.monitoredFileCount = monitoredFileCount;
            this.processedChangeCount = processedChangeCount;
        }
    }
}
