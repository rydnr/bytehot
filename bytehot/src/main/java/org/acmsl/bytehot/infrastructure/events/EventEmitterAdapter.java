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
 * Filename: EventEmitterAdapter.java
 *
 * Author: Claude Code
 *
 * Class name: EventEmitterAdapter
 *
 * Responsibilities:
 *   - Implement event emission to various targets (console, files, message queues)
 *   - Handle event serialization and routing
 *   - Provide infrastructure implementation of EventEmitterPort
 *
 * Collaborators:
 *   - EventEmitterPort: Interface this adapter implements
 *   - DomainResponseEvent: Events being emitted
 */
package org.acmsl.bytehot.infrastructure.events;

import org.acmsl.bytehot.domain.EventEmitterPort;

import org.acmsl.commons.patterns.Adapter;
import org.acmsl.commons.patterns.DomainResponseEvent;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Infrastructure adapter for emitting domain events to external systems
 * @author Claude Code
 * @since 2025-06-17
 */
public class EventEmitterAdapter
    implements EventEmitterPort, Adapter<EventEmitterPort> {

    /**
     * Default log file path for event emission
     */
    private static final String DEFAULT_LOG_FILE = "bytehot-events.log";

    /**
     * Date time formatter for event timestamps
     */
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * Counter for emitted events
     */
    private final AtomicLong eventCounter;

    /**
     * Target for event emission (console, file, etc.)
     */
    private final EmissionTarget target;

    /**
     * Path to log file if using file emission
     */
    private final Path logFile;

    /**
     * Whether to include stack traces in error events
     */
    private final boolean includeStackTraces;

    /**
     * Creates a new EventEmitterAdapter with console output
     */
    public EventEmitterAdapter() {
        this(EmissionTarget.CONSOLE, null, false);
    }

    /**
     * Creates a new EventEmitterAdapter with specified configuration
     */
    public EventEmitterAdapter(final EmissionTarget target, final String logFilePath, final boolean includeStackTraces) {
        this.eventCounter = new AtomicLong(0);
        this.target = target != null ? target : EmissionTarget.CONSOLE;
        this.logFile = logFilePath != null ? Paths.get(logFilePath) : Paths.get(DEFAULT_LOG_FILE);
        this.includeStackTraces = includeStackTraces;
    }

    /**
     * Emits a single domain event
     */
    @Override
    public void emit(final DomainResponseEvent<?> event) throws Exception {
        if (event == null) {
            return;
        }

        final String eventMessage = formatEvent(event);
        
        switch (target) {
            case CONSOLE:
                System.out.println(eventMessage);
                break;
            case FILE:
                writeToFile(eventMessage);
                break;
            case BOTH:
                System.out.println(eventMessage);
                writeToFile(eventMessage);
                break;
        }

        eventCounter.incrementAndGet();
    }

    /**
     * Emits multiple domain events
     */
    @Override
    public void emit(final List<DomainResponseEvent<?>> events) throws Exception {
        if (events == null || events.isEmpty()) {
            return;
        }

        for (final DomainResponseEvent<?> event : events) {
            emit(event);
        }
    }

    /**
     * Checks if event emission is available
     */
    @Override
    public boolean isEmissionAvailable() {
        switch (target) {
            case CONSOLE:
            case BOTH:
                return true;
            case FILE:
                return isFileWritable();
            default:
                return false;
        }
    }

    /**
     * Returns the emission target description
     */
    @Override
    public String getEmissionTarget() {
        switch (target) {
            case CONSOLE:
                return "Console Output";
            case FILE:
                return "File: " + logFile.toString();
            case BOTH:
                return "Console and File: " + logFile.toString();
            default:
                return "Unknown Target";
        }
    }

    /**
     * Returns the number of events emitted
     */
    @Override
    public long getEmittedEventCount() {
        return eventCounter.get();
    }

    /**
     * Returns the port interface this adapter implements
     */
    @Override
    public Class<EventEmitterPort> adapts() {
        return EventEmitterPort.class;
    }

    /**
     * Formats an event for emission
     */
    protected String formatEvent(final DomainResponseEvent<?> event) {
        final StringBuilder sb = new StringBuilder();
        
        // Timestamp
        sb.append("[").append(LocalDateTime.now().format(TIMESTAMP_FORMAT)).append("] ");
        
        // Event type
        sb.append("[").append(event.getClass().getSimpleName()).append("] ");
        
        // Event details
        sb.append("Event: ").append(event.toString());
        
        // Previous event reference if available
        if (event.getPreceding() != null) {
            sb.append(" | Previous: ").append(event.getPreceding().getClass().getSimpleName());
        }

        // Stack trace for error events if enabled
        if (includeStackTraces && isErrorEvent(event)) {
            final String stackTrace = getStackTrace();
            if (!stackTrace.isEmpty()) {
                sb.append("\nStack Trace:\n").append(stackTrace);
            }
        }

        return sb.toString();
    }

    /**
     * Writes a message to the log file
     */
    protected void writeToFile(final String message) throws Exception {
        try {
            // Create parent directories if they don't exist
            if (logFile.getParent() != null) {
                Files.createDirectories(logFile.getParent());
            }

            // Append to file
            Files.write(logFile, (message + System.lineSeparator()).getBytes(), 
                       StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                       
        } catch (final IOException e) {
            throw new Exception("Failed to write to log file " + logFile + ": " + e.getMessage(), e);
        }
    }

    /**
     * Checks if the log file is writable
     */
    protected boolean isFileWritable() {
        try {
            if (Files.exists(logFile)) {
                return Files.isWritable(logFile);
            } else {
                // Check if parent directory is writable
                final Path parent = logFile.getParent();
                return parent == null || Files.isWritable(parent);
            }
        } catch (final Exception e) {
            return false;
        }
    }

    /**
     * Determines if an event represents an error condition
     */
    protected boolean isErrorEvent(final DomainResponseEvent<?> event) {
        final String eventName = event.getClass().getSimpleName().toLowerCase();
        return eventName.contains("error") || eventName.contains("failed") || eventName.contains("exception");
    }

    /**
     * Gets the current stack trace as a string
     */
    protected String getStackTrace() {
        try (final StringWriter sw = new StringWriter();
             final PrintWriter pw = new PrintWriter(sw)) {
            
            new Exception("Stack trace").printStackTrace(pw);
            return sw.toString();
            
        } catch (final Exception e) {
            return "Unable to generate stack trace: " + e.getMessage();
        }
    }

    /**
     * Enumeration of emission targets
     */
    public enum EmissionTarget {
        /**
         * Emit to console only
         */
        CONSOLE,
        
        /**
         * Emit to file only
         */
        FILE,
        
        /**
         * Emit to both console and file
         */
        BOTH
    }

    /**
     * Returns the current emission target
     */
    public EmissionTarget getTarget() {
        return target;
    }

    /**
     * Returns the log file path
     */
    public Path getLogFile() {
        return logFile;
    }

    /**
     * Returns whether stack traces are included
     */
    public boolean isIncludeStackTraces() {
        return includeStackTraces;
    }
}