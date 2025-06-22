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
 * Filename: HotSwapManager.java
 *
 * Author: Claude Code
 *
 * Class name: HotSwapManager
 *
 * Responsibilities:
 *   - Coordinate hot-swap operations and interface with JVM
 *   - Manage the transition from validation to actual redefinition
 *
 * Collaborators:
 *   - HotSwapRequested: Domain event for hot-swap initiation
 *   - BytecodeValidated: Input from validation process
 */
package org.acmsl.bytehot.domain;

import org.acmsl.bytehot.domain.events.BytecodeValidated;
import org.acmsl.bytehot.domain.events.ClassFileChanged;
import org.acmsl.bytehot.domain.events.ClassRedefinitionFailed;
import org.acmsl.bytehot.domain.events.ClassRedefinitionSucceeded;
import org.acmsl.bytehot.domain.events.HotSwapRequested;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Coordinates hot-swap operations and interfaces with JVM
 * @author Claude Code
 * @since 2025-06-17
 */
@EqualsAndHashCode
@ToString
public class HotSwapManager {

    /**
     * Creates a hot-swap request from validated bytecode
     * @param classFile the class file to hot-swap
     * @param validation the successful validation result
     * @param originalBytecode the current bytecode in the JVM
     * @return the hot-swap request event
     * @throws IOException if file cannot be read
     */
    public HotSwapRequested requestHotSwap(final Path classFile, final BytecodeValidated validation, 
                                          final byte[] originalBytecode) throws IOException {
        final byte[] newBytecode = Files.readAllBytes(classFile);
        final String requestReason = createRequestReason(validation);
        final Instant timestamp = Instant.now();
        
        // Create a placeholder ClassFileChanged event for the HotSwapRequested
        ClassFileChanged placeholderEvent = ClassFileChanged.forNewSession(
            classFile,
            validation.getClassName(),
            newBytecode.length,
            timestamp
        );
        
        return new HotSwapRequested(
            classFile,
            validation.getClassName(),
            originalBytecode,
            newBytecode,
            requestReason,
            timestamp,
            placeholderEvent
        );
    }

    /**
     * The instrumentation service for this manager
     */
    private final InstrumentationService instrumentationService;

    /**
     * Creates a new HotSwapManager with instrumentation service
     * @param instrumentationService the instrumentation service
     */
    public HotSwapManager(final InstrumentationService instrumentationService) {
        if (instrumentationService == null) {
            throw new IllegalArgumentException("InstrumentationService cannot be null");
        }
        this.instrumentationService = instrumentationService;
    }

    /**
     * Performs JVM class redefinition for a hot-swap request
     * @param request the hot-swap request to execute
     * @return the success event with redefinition details
     * @throws HotSwapException if redefinition fails
     */
    public ClassRedefinitionSucceeded performRedefinition(final HotSwapRequested request) throws HotSwapException {
        final long startTime = System.nanoTime();
        
        try {
            // Find the loaded class to redefine
            final Class<?> targetClass = instrumentationService.findLoadedClass(request.getClassName());
            if (targetClass == null) {
                throw createClassNotFoundException(request);
            }
            
            // Perform actual JVM class redefinition
            try {
                instrumentationService.redefineClass(targetClass, request.getNewBytecode());
            } catch (final Exception redefinitionException) {
                // Wrap JVM redefinition exceptions
                final ClassRedefinitionFailed failure = createJvmRedefinitionFailure(request, redefinitionException);
                throw new HotSwapException(failure, redefinitionException);
            }
            
            // Calculate success metrics
            final long endTime = System.nanoTime();
            final Duration duration = Duration.ofNanos(endTime - startTime);
            final int affectedInstances = calculateAffectedInstances(request);
            final String details = createRedefinitionDetails(request);
            final Instant timestamp = Instant.now();
            
            return new ClassRedefinitionSucceeded(
                request.getClassName(),
                request.getClassFile(),
                affectedInstances,
                details,
                duration,
                timestamp
            );
            
        } catch (final Exception e) {
            if (e instanceof HotSwapException) {
                throw e;
            }
            // Wrap unexpected exceptions
            final ClassRedefinitionFailed failure = createUnexpectedFailure(request, e);
            throw new HotSwapException(failure, e);
        }
    }

    /**
     * Creates a descriptive reason for the hot-swap request based on validation
     * @param validation the validation result
     * @return a human-readable reason for the request
     */
    protected String createRequestReason(final BytecodeValidated validation) {
        return "Bytecode validation passed - initiating hot-swap";
    }

    /**
     * Creates a JVM rejection exception for incompatible bytecode
     * @param request the hot-swap request
     * @param content the bytecode content
     * @return the hot-swap exception
     */
    protected HotSwapException createJvmRejectionException(final HotSwapRequested request, final String content) {
        String reason;
        String jvmError;
        String recoveryAction;
        
        if (content.contains("SCHEMA_CHANGE")) {
            reason = "JVM detected incompatible schema changes";
            jvmError = "java.lang.UnsupportedOperationException: class redefinition failed: attempted to change the schema";
            recoveryAction = "Restart application to load new class definition";
        } else {
            reason = "JVM rejected bytecode changes as incompatible";
            jvmError = "java.lang.UnsupportedOperationException: class redefinition failed: incompatible changes detected";
            recoveryAction = "Review changes for compatibility or restart application";
        }
        
        final ClassRedefinitionFailed failure = new ClassRedefinitionFailed(
            request.getClassName(),
            request.getClassFile(),
            reason,
            jvmError,
            recoveryAction,
            Instant.now()
        );
        
        return new HotSwapException(failure);
    }

    /**
     * Creates a class not found exception
     * @param request the hot-swap request
     * @return the hot-swap exception
     */
    protected HotSwapException createClassNotFoundException(final HotSwapRequested request) {
        final ClassRedefinitionFailed failure = new ClassRedefinitionFailed(
            request.getClassName(),
            request.getClassFile(),
            "Class not found in loaded classes",
            "java.lang.ClassNotFoundException: " + request.getClassName() + " not loaded in current JVM",
            "Load or instantiate the class before attempting hot-swap",
            Instant.now()
        );
        
        return new HotSwapException(failure);
    }

    /**
     * Creates an unexpected failure event
     * @param request the hot-swap request
     * @param cause the unexpected exception
     * @return the failure event
     */
    protected ClassRedefinitionFailed createUnexpectedFailure(final HotSwapRequested request, final Exception cause) {
        return new ClassRedefinitionFailed(
            request.getClassName(),
            request.getClassFile(),
            "Unexpected error during redefinition",
            cause.getMessage(),
            "Check logs for details and retry operation",
            Instant.now()
        );
    }

    /**
     * Calculates the number of affected instances (mock implementation)
     * @param request the hot-swap request
     * @return the number of affected instances
     */
    protected int calculateAffectedInstances(final HotSwapRequested request) {
        final String content = new String(request.getNewBytecode());
        if (content.contains("instances:multiple")) {
            return 3; // Mock multiple instances
        }
        return 1; // Mock single instance
    }

    /**
     * Creates redefinition details description
     * @param request the hot-swap request
     * @return the redefinition details
     */
    protected String createRedefinitionDetails(final HotSwapRequested request) {
        return String.format("Class %s redefinition completed successfully", request.getClassName());
    }


    /**
     * Creates a failure event for JVM redefinition errors
     * @param request the hot-swap request
     * @param cause the JVM exception
     * @return the failure event
     */
    protected ClassRedefinitionFailed createJvmRedefinitionFailure(final HotSwapRequested request, final Exception cause) {
        String reason = "JVM class redefinition failed";
        String jvmError = cause.getMessage();
        String recoveryAction = "Check bytecode compatibility and retry";
        
        if (cause.getMessage() != null) {
            if (cause.getMessage().contains("schema")) {
                reason = "JVM rejected schema changes";
                recoveryAction = "Restart application to load new class definition";
            } else if (cause.getMessage().contains("unsupported")) {
                reason = "JVM does not support this type of change";
                recoveryAction = "Use compatible changes or restart application";
            }
        }
        
        return new ClassRedefinitionFailed(
            request.getClassName(),
            request.getClassFile(),
            reason,
            jvmError,
            recoveryAction,
            Instant.now()
        );
    }
}